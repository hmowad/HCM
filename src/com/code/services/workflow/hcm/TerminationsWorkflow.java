package com.code.services.workflow.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.terminations.TerminationRecordData;
import com.code.dal.orm.hcm.terminations.TerminationRecordDetailData;
import com.code.dal.orm.hcm.terminations.TerminationTransaction;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.log.EmployeeLog;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationCancellationMovement;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationCancellationMovementData;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationData;
import com.code.enums.CategoriesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.TerminationRecordStatusEnum;
import com.code.enums.TerminationTypeFlagEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.TerminationsService;
import com.code.services.integration.PayrollEngineService;
import com.code.services.log.LogService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

public class TerminationsWorkflow extends BaseWorkFlow {

    /**
     * Private constructor to prevent instantiation
     */
    private TerminationsWorkflow() {
    }

    /********************************** Work flow **********************************/

    public static void initTermination(EmployeeData requester, WFTerminationData wfTermination, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, long processId, String attachments, String taskUrl) throws BusinessException {

	validateTerminationWF(requester, wfTermination, terminationRecordData, terminationRecordDetailDataList, processId, null);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    List<Long> beneficiariesIds = terminationRecordData.getTypeFlag().equals(TerminationTypeFlagEnum.REQUEST.getCode()) ? Arrays.asList(requester.getEmpId()) : getTerminationsReportsInstanceBeneficiariesIds(terminationRecordDetailDataList);

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, beneficiariesIds, session);

	    if (terminationRecordData.getTypeFlag().equals(TerminationTypeFlagEnum.REQUEST.getCode()))
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
	    else {
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		terminationRecordData.setStatus(TerminationRecordStatusEnum.UNDER_APPROVAL.getCode());
		TerminationsService.saveTerminationRecord(terminationRecordData, requester.getEmpId(), session);

		wfTermination = new WFTerminationData();
		wfTermination.setRecordId(terminationRecordData.getId());
	    }
	    saveTerminationWorkflow(wfTermination, instance.getInstanceId(), session);

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // this method is only used by (request)
    public static void doTerminationDM(EmployeeData requester, WFInstance instance, WFTerminationData wfTermination, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, WFTask dmTask, boolean isApproved) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		EmployeeData curDM = EmployeesService.getEmployeeData(dmTask.getOriginalId());

		if (instance.getProcessId().equals(WFProcessesEnum.OFFICERS_TERMINATION_REQUEST.getCode())) { // Officers.
		    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == requester.getPhysicalRegionId()) {
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    // go to SM (termination manager)
			    long officersServiceTerminationUnitManagerId = getTerminationManagerId(CategoriesEnum.OFFICERS.getCode(), requester.getPhysicalRegionId());
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(officersServiceTerminationUnitManagerId, instance.getProcessId(), requester.getEmpId()), officersServiceTerminationUnitManagerId, dmTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			} else { // send to next DM
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else { // Regions
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    // send to SMR
			    long officersRegionUnitManagerId = getTerminationManagerId(CategoriesEnum.OFFICERS.getCode(), requester.getPhysicalRegionId());
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(officersRegionUnitManagerId, instance.getProcessId(), requester.getEmpId()), officersRegionUnitManagerId, dmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode(), "1", session);
			} else { // send to next DM
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    }
		} else if (instance.getProcessId().equals(WFProcessesEnum.SOLDIERS_TERMINATION_REQUEST.getCode())) { // For Soldiers
		    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == requester.getPhysicalRegionId()) {
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    long soldiersServiceTerminationUnitManagerId = getTerminationManagerId(requester.getCategoryId(), requester.getPhysicalRegionId());
			    // construct master and detail
			    constructTerminationRequestRecordAndDetail(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, dmTask, session);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(soldiersServiceTerminationUnitManagerId, instance.getProcessId(), requester.getEmpId()), soldiersServiceTerminationUnitManagerId, dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else {
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    constructTerminationRequestRecordAndDetail(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, dmTask, session);
			    long soldiersRegionUnitManagerId = getTerminationManagerId(CategoriesEnum.SOLDIERS.getCode(), requester.getPhysicalRegionId());
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(soldiersRegionUnitManagerId, instance.getProcessId(), requester.getEmpId()), soldiersRegionUnitManagerId, dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    }
		} else { // Civilians
		    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			long civiliansServiceTerminationUnitManagerId = getTerminationManagerId(requester.getCategoryId(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			// construct master and detail
			constructTerminationRequestRecordAndDetail(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, dmTask, session);
			completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(civiliansServiceTerminationUnitManagerId, instance.getProcessId(), requester.getEmpId()), civiliansServiceTerminationUnitManagerId, dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
		    } else {
			completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
		    }
		}
		saveTerminationWorkflow(wfTermination, instance.getInstanceId(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, dmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doTerminationSMR(EmployeeData requester, WFInstance instance, WFTask smrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    completeWFTask(smrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, smrTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), "1");
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void doTerminationSRE(EmployeeData requester, WFInstance instance, WFTask sreTask, boolean isApproved) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    if (isApproved) {
		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();

		EmployeeData curDM = EmployeesService.getEmployeeDirectManager(sreTask.getOriginalId());
		completeWFTask(sreTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getEmpId(), instance.getProcessId(), requester.getEmpId()), curDM.getEmpId(), sreTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, sreTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doTerminationSSM(EmployeeData requester, WFInstance instance, WFTerminationData wfTermination, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, WFTask ssmTask, int approvalFlag) throws BusinessException {

	// OFFICERS AND SOLDIERS ONLY !!!
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    EmployeeData curSSM = EmployeesService.getEmployeeData(ssmTask.getOriginalId());

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		if (instance.getProcessId() == WFProcessesEnum.OFFICERS_TERMINATION_REQUEST.getCode()) {
		    if (curSSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			// go to SM (termination manager)
			long officersServiceTerminationUnitManagerId = getTerminationManagerId(CategoriesEnum.OFFICERS.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(officersServiceTerminationUnitManagerId, instance.getProcessId(), requester.getEmpId()), officersServiceTerminationUnitManagerId, ssmTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    } else { // send to next SSM
			completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSSM.getManagerId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
		    }
		} else { // SOLDIERS
		    if (curSSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			// go to MR (termination manager)
			long soldiersServiceTerminationUnitManagerId = getTerminationManagerId(CategoriesEnum.SOLDIERS.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			// construct master and detail
			constructTerminationRequestRecordAndDetail(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, ssmTask, session);
			saveTerminationWorkflow(wfTermination, instance.getInstanceId(), session);
			completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(soldiersServiceTerminationUnitManagerId, instance.getProcessId(), requester.getEmpId()), soldiersServiceTerminationUnitManagerId, ssmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
		    } else { // send to next SSM
			completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSSM.getManagerId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
		    }
		}
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		WFTask secondaryReviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode()).get(0);
		completeWFTask(ssmTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(secondaryReviewerTask.getOriginalId(), instance.getProcessId(), requester.getEmpId()), secondaryReviewerTask.getOriginalId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), "1", session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, ssmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doTerminationMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1");
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void doTerminationRE(EmployeeData requester, WFInstance instance, WFTerminationData wfTermination, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, WFTask reTask, String attachments, boolean isApproved) throws BusinessException {

	// TODO: move most of validations to Service
	if (isApproved)
	    validateTerminationWF(requester, wfTermination, terminationRecordData, terminationRecordDetailDataList, instance.getProcessId(), instance);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    if (isApproved) {
		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();

		EmployeeData currentDirectManager = EmployeesService.getEmployeeDirectManager(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

		if (terminationRecordData.getTypeFlag().equals(TerminationTypeFlagEnum.REQUEST.getCode()))
		    TerminationsService.addModifyTerminationRecordDetail(terminationRecordData, terminationRecordDetailDataList, reTask.getOriginalId(), session);

		TerminationsService.saveTerminationRecord(terminationRecordData, requester.getEmpId(), session);
		saveTerminationWorkflow(wfTermination, instance.getInstanceId(), session);
		updateWFInstanceAttachments(instance, attachments, session);
	    } else {
		terminationRecordData.setStatus(TerminationRecordStatusEnum.CLOSED.getCode());
		TerminationsService.saveTerminationRecord(terminationRecordData, requester.getEmpId(), session);
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doTerminationSM(EmployeeData requester, WFInstance instance, WFTerminationData wfTermination, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, WFTask smTask, long transactionTypeId, int approvalFlag) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    EmployeeData currentDirectManager = EmployeesService.getEmployeeData(smTask.getOriginalId());

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		long unitTypeCode = currentDirectManager.getUnitTypeCode();
		long requesterRegionId = requester.getPhysicalRegionId();

		// 1- case of request
		if (instance.getProcessId() == WFProcessesEnum.OFFICERS_TERMINATION_REQUEST.getCode()) {
		    constructTerminationRequestRecordAndDetail(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, session);
		    saveTerminationWorkflow(wfTermination, instance.getInstanceId(), session);
		    closeOfficersTerminationRequestWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, session);
		} else if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_TERMINATION_REQUEST.getCode()) {
		    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == requesterRegionId) {
			// cases that stop at president assistant
			if (EmployeesService.getEmployeeDirectManager(currentDirectManager.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
			    closeTerminationWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, transactionTypeId, session);
			else
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    } else {
			if (unitTypeCode >= UnitTypesEnum.REGION_COMMANDER.getCode())
			    closeTerminationWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, transactionTypeId, session);
			else
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    }
		} else if (instance.getProcessId() == WFProcessesEnum.CONTRACTORS_TERMINATION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.CIVILIANS_TERMINATION_REQUEST.getCode()) {
		    // For all cases, it should reach presidency to complete this workFlow
		    if (unitTypeCode >= UnitTypesEnum.PRESIDENCY.getCode())
			closeTerminationWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, transactionTypeId, session);
		    else
			completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		}
		// 2- case of record
		else {
		    long categoryId = terminationRecordData.getCategoryId();
		    if (CategoriesEnum.SOLDIERS.getCode() == categoryId) { // Soldiers
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == requesterRegionId) { // General directorate of border guards
			    // cases that require approval of presidency
			    if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_TERMINATION_REQUEST.getCode() || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_DISPENSING_SERVICES.getCode() || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_DISQUALIFICATION_MILITARY_SERVICE.getCode()) {
				if (unitTypeCode >= UnitTypesEnum.PRESIDENCY.getCode())
				    closeTerminationWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, transactionTypeId, session);
				else
				    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			    }
			    // cases that require approval of vice president
			    else if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_DISMISS.getCode() || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_LOSS.getCode()) {
				if (unitTypeCode >= UnitTypesEnum.PRESIDENCY.getCode())
				    closeTerminationWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, transactionTypeId, session);
				else {
				    if (EmployeesService.getEmployeeDirectManager(currentDirectManager.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
					currentDirectManager.setManagerId(getVicePresidencyId());
				    }
				    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
				}
			    }
			    // cases that require approval of assistant
			    else if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode() || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_DEATH.getCode() || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_LACK_MEDICAL_FITNESS.getCode() || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_FOREIGNER_MARRIAGE.getCode()
				    || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_ABSENCE.getCode()) {
				if (unitTypeCode >= UnitTypesEnum.ASSISTANT.getCode())
				    closeTerminationWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, transactionTypeId, session);
				else {
				    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
				}
			    }
			} else { // Regions
			    if (unitTypeCode >= UnitTypesEnum.REGION_COMMANDER.getCode())
				closeTerminationWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, transactionTypeId, session);
			    else
				completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			}
		    } else { // Civilians and Contractors
			if (unitTypeCode >= UnitTypesEnum.PRESIDENCY.getCode())
			    closeTerminationWorkFlow(requester, instance, wfTermination, terminationRecordData, terminationRecordDetailDataList, smTask, transactionTypeId, session);
			else
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    }
		}
		// check if reached last SM then closeTerminationWorkFlow() and completeWFTask() depending on region and type
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		// NO return reviewer in case of officers
		if (instance.getProcessId() == WFProcessesEnum.OFFICERS_TERMINATION_REQUEST.getCode())
		    throw new BusinessException("error_general");
		else if (instance.getProcessId() == WFProcessesEnum.SOLDIERS_TERMINATION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.CIVILIANS_TERMINATION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.CONTRACTORS_TERMINATION_REQUEST.getCode()) {
		    WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode()).get(0);
		    completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerTask.getOriginalId(), instance.getProcessId(), requester.getEmpId()), reviewerTask.getOriginalId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
		} else // RECORD
		    completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getEmpId(), instance.getProcessId(), requester.getEmpId()), requester.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
		// update status of master
		if (instance.getProcessId() != WFProcessesEnum.OFFICERS_TERMINATION_REQUEST.getCode()) {
		    terminationRecordData.setStatus(TerminationRecordStatusEnum.CLOSED.getCode());
		    TerminationsService.saveTerminationRecord(terminationRecordData, smTask.getOriginalId(), session);
		}
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /*********************************************** Validation ***********************************************************/

    // A wrapper for validation in all cases
    private static void validateTerminationWF(EmployeeData requester, WFTerminationData wfTermination, TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetails, long processId, WFInstance instance) throws BusinessException {

	if (terminationRecordData.getTypeFlag().equals(TerminationTypeFlagEnum.REQUEST.getCode())) {
	    validateTerminationRequest(requester, wfTermination, terminationRecordDetails, processId, instance);
	} else { // record process
	    if (terminationRecordDetails == null || terminationRecordDetails.size() == 0)
		throw new BusinessException("error_NoOneToTerminate");

	    // init case only
	    if (instance == null) {
		Long[] empsIds = new Long[terminationRecordDetails.size()];
		for (int i = 0; i < terminationRecordDetails.size(); i++)
		    empsIds[i] = terminationRecordDetails.get(i).getEmpId();

		EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(empsIds, null, TransactionClassesEnum.TERMINATIONS.getCode(), false, terminationRecordData.getId() * -1, terminationRecordData.getCategoryId());
	    }

	    TerminationsService.validateTerminationRecordDetails(terminationRecordData, terminationRecordDetails);
	}
    }

    private static void validateTerminationRequest(EmployeeData requester, WFTerminationData wfTermination, List<TerminationRecordDetailData> terminationRecordDetails, long processId, WFInstance instance) throws BusinessException {

	if (instance == null) { // case of init()
	    Long[] empsIds = new Long[] { requester.getEmpId() };
	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(empsIds, null, TransactionClassesEnum.TERMINATIONS.getCode(), false, wfTermination.getInstanceId(), requester.getCategoryId());
	    // 1. validate required fields
	    if (wfTermination == null || wfTermination.getReasons() == null || wfTermination.getReasons().trim().isEmpty())
		throw new BusinessException("error_terminationRequestReasonsIsMandatory");

	    float periodUntilDesiredTerminationDateByDays;

	    if (requester.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		if (wfTermination.getDesiredTerminationDate() != null) {

		    if (!HijriDateService.isValidHijriDate(wfTermination.getDesiredTerminationDate()))
			throw new BusinessException("error_invalidDesiredTerminationDate");

		    if (wfTermination.getDesiredTerminationDate().before(HijriDateService.getHijriSysDate()))
			throw new BusinessException("error_desiredTerminationDateBeforeToday");

		    periodUntilDesiredTerminationDateByDays = HijriDateService.hijriDateDiff(HijriDateService.getHijriSysDate(), wfTermination.getDesiredTerminationDate());

		    if (periodUntilDesiredTerminationDateByDays > ETRConfigurationService.getTerminationsOfficersPeriodUntilDesiredTerminationDateDaysMax())
			throw new BusinessException("error_desiredTerminationDatePeriodForOfficers", new Object[] { ETRConfigurationService.getTerminationsOfficersPeriodUntilDesiredTerminationDateDaysMax() });
		}
	    } else {
		// desiredTerminationDate is required for all categories except officers
		if (wfTermination.getDesiredTerminationDate() == null)
		    throw new BusinessException("error_desiredTerminationDateIsMandatory");

		if (!HijriDateService.isValidHijriDate(wfTermination.getDesiredTerminationDate()))
		    throw new BusinessException("error_invalidDesiredTerminationDate");

		float servicePeriodYears = HijriDateService.hijriDateDiffByHijriYear(requester.getRecruitmentDate(), wfTermination.getDesiredTerminationDate());
		if (CategoriesEnum.SOLDIERS.getCode() == requester.getCategoryId().longValue()) {
		    if (wfTermination.getDesiredTerminationDate().before(HijriDateService.getHijriSysDate()))
			throw new BusinessException("error_desiredTerminationDateBeforeToday");
		    periodUntilDesiredTerminationDateByDays = HijriDateService.hijriDateDiff(HijriDateService.getHijriSysDate(), wfTermination.getDesiredTerminationDate());

		    if (servicePeriodYears < ETRConfigurationService.getTerminationsSoldierServicePeriodBeforeTerminationYearsMin())
			throw new BusinessException("error_soldierServicePeriodBeforeTerminationYearsMin", new Object[] { ETRConfigurationService.getTerminationsSoldierServicePeriodBeforeTerminationYearsMin() });

		    if (periodUntilDesiredTerminationDateByDays < ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMin() || periodUntilDesiredTerminationDateByDays > ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMax()) {
			if (ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMin() == 0)
			    throw new BusinessException("error_desiredTerminationDatePeriodForSoldiersStartingFromRequestDate", new Object[] { ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMax() });

			else
			    throw new BusinessException("error_desiredTerminationDatePeriodForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMin(), ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMax() });
		    }
		} else { // Persons and contractors
		    periodUntilDesiredTerminationDateByDays = HijriDateService.hijriDateDiff(HijriDateService.getHijriSysDate(), wfTermination.getDesiredTerminationDate());
		    if (periodUntilDesiredTerminationDateByDays < ETRConfigurationService.getTerminationsCiviliansPeriodUntilDesiredTerminationDateDaysMin() || periodUntilDesiredTerminationDateByDays > ETRConfigurationService.getTerminationsCiviliansPeriodUntilDesiredTerminationDateDaysMax())
			throw new BusinessException("error_desiredTerminationDatePeriodForPersons", new Object[] { ETRConfigurationService.getTerminationsCiviliansPeriodUntilDesiredTerminationDateDaysMin(), ETRConfigurationService.getTerminationsCiviliansPeriodUntilDesiredTerminationDateDaysMax() });
		}
	    }
	} else { // in case of RE ... check required fields too
	    TerminationRecordDetailData terminationRecordDetailData = terminationRecordDetails.get(0);

	    if (CategoriesEnum.SOLDIERS.getCode() == requester.getCategoryId().longValue()) {
		// check if desiredTermination
		if (terminationRecordDetailData.getJudgmentFlag() == null || terminationRecordDetailData.getJudgmentFlag().intValue() == FlagsEnum.OFF.getCode())
		    throw new BusinessException("error_noJudjmentIsMandatoryRequestSoldiers");

		if (terminationRecordDetailData.getSpecializationPeriodFlag() == null || terminationRecordDetailData.getSpecializationPeriodFlag().intValue() == FlagsEnum.OFF.getCode())
		    throw new BusinessException("error_specializationPeriodFlagBooleanIsMandatory");
	    } else if (CategoriesEnum.CONTRACTORS.getCode() != requester.getCategoryId().longValue() && CategoriesEnum.OFFICERS.getCode() != requester.getCategoryId().longValue()) { // Persons
		float servicePeriodYears = HijriDateService.hijriDateDiffByHijriYear(requester.getRecruitmentDate(), wfTermination.getDesiredTerminationDate());
		if (servicePeriodYears <= ETRConfigurationService.getTerminationsCiviliansTerminationRequestReqDesiredDiffYearsMin() && (terminationRecordDetailData.getJudgmentFlag() == null || terminationRecordDetailData.getJudgmentFlag().intValue() == FlagsEnum.OFF.getCode()))
		    throw new BusinessException("error_noJudjmentIsMandatory", new Object[] { ETRConfigurationService.getTerminationsCiviliansTerminationRequestReqDesiredDiffYearsMin() });
	    }
	}
    }

    public static List<WFInstance> getRunningRequests(Long[] empsIds, Long excludedInstanceId) throws BusinessException {
	try {
	    if (empsIds == null || empsIds.length == 0)
		return new ArrayList<WFInstance>();

	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMPS_IDS", empsIds);
	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId == null ? (long) FlagsEnum.ALL.getCode() : excludedInstanceId);

	    Long[] TerminationRequestProcessesIds = new Long[] { WFProcessesEnum.OFFICERS_TERMINATION_REQUEST.getCode(), WFProcessesEnum.SOLDIERS_TERMINATION_REQUEST.getCode(),
		    WFProcessesEnum.CIVILIANS_TERMINATION_REQUEST.getCode(), WFProcessesEnum.CONTRACTORS_TERMINATION_REQUEST.getCode() };
	    qParams.put("P_PROCESSES_IDS", TerminationRequestProcessesIds);

	    return DataAccess.executeNamedQuery(WFInstance.class, QueryNamesEnum.WF_GET_RUNNING_TERMINATION_REQUESTS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * validate termination Civilian movement fields, request date , Disclaimer Date and joining movement date
     * 
     * @param wfTerminationCancellationMovementData
     * @param requestDate
     * @throws BusinessException
     */
    private static void validateTerminationCivilianMovement(WFTerminationCancellationMovementData wfTerminationCancellationMovementData, Date movementTransactionExecutionDate, Date requestDate) throws BusinessException {

	TerminationTransaction lastTerminationMovementTransaction = TerminationsService.getLastTerminationMovementTransaction(wfTerminationCancellationMovementData.getEmpId());

	if (lastTerminationMovementTransaction != null) {
	    MovementTransactionData lastExternalMoveTransaction = MovementsService.getExternalMoveTransactionByEmployeeId(wfTerminationCancellationMovementData.getEmpId());

	    if (lastExternalMoveTransaction != null && !lastExternalMoveTransaction.getExecutionDate().after(lastTerminationMovementTransaction.getDecisionDate()))
		throw new BusinessException("error_employeeHasMovementTerminationTransaction");
	}

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { wfTerminationCancellationMovementData.getEmpId() }, null, TransactionClassesEnum.TERMINATIONS.getCode(), false, wfTerminationCancellationMovementData.getInstanceId() == null ? FlagsEnum.ALL.getCode() : wfTerminationCancellationMovementData.getInstanceId());

	if (wfTerminationCancellationMovementData.getReferring() == null || wfTerminationCancellationMovementData.getReferring().trim().isEmpty())
	    throw new BusinessException("error_refferingToIsMandatory");

	if (wfTerminationCancellationMovementData.getMovedTo() == null || wfTerminationCancellationMovementData.getMovedTo().trim().isEmpty())
	    throw new BusinessException("error_TerminationMovementDestinationIsMandatory");

	if (wfTerminationCancellationMovementData.getMovementJoiningDesc() == null || wfTerminationCancellationMovementData.getMovementJoiningDesc().trim().isEmpty())
	    throw new BusinessException("error_joiningBasedonIsMandatory");

	if (wfTerminationCancellationMovementData.getMovementJoiningDate() == null)
	    throw new BusinessException("error_terminationMovementJoiningDateIsMandatory");

	if (!HijriDateService.isValidHijriDate(wfTerminationCancellationMovementData.getMovementJoiningDate()))
	    throw new BusinessException("error_invalidMovementJoiningDate");

	if (wfTerminationCancellationMovementData.getDisclaimerDate() == null)
	    throw new BusinessException("error_disclaimerDateIsMandatory");

	if (!HijriDateService.isValidHijriDate(wfTerminationCancellationMovementData.getDisclaimerDate()))
	    throw new BusinessException("error_invalidDisclaimerDate");

	if (wfTerminationCancellationMovementData.getServiceTerminationDate() == null)
	    throw new BusinessException("error_terminationMovementDateIsMandatory");

	if (!HijriDateService.isValidHijriDate(wfTerminationCancellationMovementData.getServiceTerminationDate()))
	    throw new BusinessException("error_invalidMovementServiceTerminationDate");

	if (!wfTerminationCancellationMovementData.getDisclaimerDate().before(requestDate) || HijriDateService.hijriDateDiff(wfTerminationCancellationMovementData.getDisclaimerDate(), movementTransactionExecutionDate) > ETRConfigurationService.getTerminationsCiviliansDisclaimerDateDaysMax())
	    throw new BusinessException("error_disclaimerDateIssue", new Object[] { ETRConfigurationService.getTerminationsCiviliansDisclaimerDateDaysMax() });

	if (!wfTerminationCancellationMovementData.getMovementJoiningDate().before(requestDate) || !wfTerminationCancellationMovementData.getMovementJoiningDate().after(wfTerminationCancellationMovementData.getDisclaimerDate()))
	    throw new BusinessException("error_terminationMovementJoiningDateIssue");

	if (wfTerminationCancellationMovementData.getServiceTerminationDate().before(wfTerminationCancellationMovementData.getDisclaimerDate()) || wfTerminationCancellationMovementData.getServiceTerminationDate().after(requestDate))
	    throw new BusinessException("error_serviceTerminationDateIssue");
    }

    private static void validateTerminationCancellation(WFTerminationCancellationMovementData wfTerminationCancellationMovement) throws BusinessException {

	if (wfTerminationCancellationMovement.getEmpId() == null)
	    throw new BusinessException("error_employeeMandatory");

	if (wfTerminationCancellationMovement.getJobId() == null)
	    throw new BusinessException("error_steCancellationReturnedToJobIsMandatory");

	if (wfTerminationCancellationMovement.getReferring() == null || wfTerminationCancellationMovement.getReferring().trim().isEmpty())
	    throw new BusinessException("error_refferingToIsMandatory");

	if (wfTerminationCancellationMovement.getReasons() == null || wfTerminationCancellationMovement.getReasons().trim().isEmpty())
	    throw new BusinessException("error_reasonsMandatory");

	// Validate that job is available using ConflictValidator to check all future transactions on this job just in case it's not my job
	Long[] jobsToBeValidated = null;
	if (!wfTerminationCancellationMovement.getEmpId().equals(JobsService.getJobById(wfTerminationCancellationMovement.getJobId()).getEmployeeId()))
	    jobsToBeValidated = new Long[] { wfTerminationCancellationMovement.getJobId() };

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { wfTerminationCancellationMovement.getEmpId() }, jobsToBeValidated, TransactionClassesEnum.TERMINATIONS.getCode(), true, wfTerminationCancellationMovement.getInstanceId());
    }

    /***********************************************************************************************************************/

    private static void saveTerminationWorkflow(WFTerminationData wfTermination, long instanceId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (wfTermination.getInstanceId() != null) { // Update
		DataAccess.updateEntity(wfTermination.getWfTermination(), session);
	    } else {
		wfTermination.setInstanceId(instanceId);
		DataAccess.addEntity(wfTermination.getWfTermination(), session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    wfTermination.setInstanceId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void saveTerminationCivilianMovementWorkflow(WFTerminationCancellationMovementData wfTerminationCancellationMovementData, long instanceId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (wfTerminationCancellationMovementData.getInstanceId() != null) { // Update
		DataAccess.updateEntity(wfTerminationCancellationMovementData.getWfServicesCancellationMovement(), session);
	    } else {
		wfTerminationCancellationMovementData.setInstanceId(instanceId);
		DataAccess.addEntity(wfTerminationCancellationMovementData.getWfServicesCancellationMovement(), session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    wfTerminationCancellationMovementData.setInstanceId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void constructTerminationRequestRecordAndDetail(EmployeeData requester, WFInstance instance, WFTerminationData wfTermination, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, WFTask dmTask, CustomSession session) throws BusinessException {

	terminationRecordData.setCategoryId(requester.getCategoryId());
	terminationRecordData.setRegionId(requester.getPhysicalRegionId());
	terminationRecordData.setTypeFlag(TerminationTypeFlagEnum.REQUEST.getCode());

	if (CategoriesEnum.OFFICERS.getCode() == requester.getCategoryId().longValue()) {
	    terminationRecordData.setReasonId(TerminationReasonsEnum.OFFICERS_TERMINATION_REQUEST.getCode());
	    terminationRecordData.setStatus(TerminationRecordStatusEnum.CURRENT.getCode());
	} else if (CategoriesEnum.SOLDIERS.getCode() == requester.getCategoryId().longValue()) {
	    terminationRecordData.setReasonId(TerminationReasonsEnum.SOLDIERS_TERMINATION_REQUEST.getCode());
	    terminationRecordData.setStatus(TerminationRecordStatusEnum.UNDER_APPROVAL.getCode());
	} else if (CategoriesEnum.CONTRACTORS.getCode() == requester.getCategoryId().longValue()) {
	    terminationRecordData.setReasonId(TerminationReasonsEnum.CONTRACTORS_END_CONTRACT.getCode());
	    terminationRecordData.setStatus(TerminationRecordStatusEnum.UNDER_APPROVAL.getCode());
	} else {
	    terminationRecordData.setReasonId(TerminationReasonsEnum.PERSONS_TERMINATION_REQUEST.getCode());
	    terminationRecordData.setStatus(TerminationRecordStatusEnum.UNDER_APPROVAL.getCode());
	}

	terminationRecordData.setAttachments(instance.getAttachments());
	terminationRecordData.setApprovedId(dmTask.getOriginalId());

	TerminationsService.saveTerminationRecord(terminationRecordData, dmTask.getOriginalId(), session);

	// Construct and save Details list
	TerminationsService.addSingularReasonsTerminationRecordDetail(terminationRecordData, terminationRecordDetailDataList, requester.getEmpId(), dmTask.getOriginalId());

	terminationRecordDetailDataList.get(0).setTerminationRequestReason(wfTermination.getReasons());
	terminationRecordDetailDataList.get(0).setDesiredTerminationDate(wfTermination.getDesiredTerminationDate());

	if (terminationRecordDetailDataList.get(0).getDesiredTerminationDate() != null)
	    terminationRecordDetailDataList.get(0).setServiceTerminationDate(terminationRecordDetailDataList.get(0).getDesiredTerminationDate());

	TerminationsService.addModifyTerminationRecordDetail(terminationRecordData, terminationRecordDetailDataList, dmTask.getOriginalId(), session);

	// update wfTermination record id basedOn terminationRecordData id
	wfTermination.setRecordId(terminationRecordData.getId());
    }

    /************************************ Close WorkFlow and integration *************************************************************/

    private static void closeTerminationWorkFlow(EmployeeData requester, WFInstance instance, WFTerminationData wfTermination, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, WFTask smTask, long tansactionTypeId, CustomSession session) throws BusinessException, DatabaseException {

	// update status and set decisionApprovedId and originalDecisionApprocedId of terminationRecord and save to db
	terminationRecordData.setStatus(TerminationRecordStatusEnum.CLOSED.getCode());
	terminationRecordData.setDecisionApprovedId(smTask.getOriginalId());
	terminationRecordData.setOriginalDecisionApprovedId(smTask.getOriginalId());

	EmployeeData signManager = EmployeesService.getEmployeeData(smTask.getOriginalId());

	doTerminationIntegration(requester, wfTermination, instance, terminationRecordData, terminationRecordDetailDataList, smTask.getOriginalId(), signManager, tansactionTypeId, session);
	// Handle notifications.
	List<Long> categoriesIds = new ArrayList<Long>();
	List<Long> beneficairyEmployeesIds = new ArrayList<Long>();
	List<Long> additionalIds = new ArrayList<Long>();

	categoriesIds.add(terminationRecordData.getCategoryId());

	for (TerminationRecordDetailData terminationRecordDetailData : terminationRecordDetailDataList) {
	    beneficairyEmployeesIds.add(terminationRecordDetailData.getEmpId());
	}

	if (terminationRecordData.getInternalCopies() != null)
	    additionalIds.addAll(Arrays.asList(getTerminationCopiesNotifications(terminationRecordData.getInternalCopies())));

	Long[] notificationsEmpsIds = computeInstanceNotifications(categoriesIds, signManager.getPhysicalRegionId(), instance.getProcessId(), beneficairyEmployeesIds, additionalIds);

	closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), notificationsEmpsIds, session);

    }

    private static void closeOfficersTerminationRequestWorkFlow(EmployeeData requester, WFInstance instance, WFTerminationData wfTermination, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, WFTask smTask, CustomSession session) throws BusinessException, DatabaseException {

	EmployeeData signManager = EmployeesService.getEmployeeData(smTask.getOriginalId());

	// Handle notifications.
	List<Long> categoriesIds = new ArrayList<Long>();
	List<Long> beneficairyEmployeesIds = new ArrayList<Long>();
	List<Long> additionalIds = new ArrayList<Long>();

	categoriesIds.add(terminationRecordData.getCategoryId());

	// add requester and manager of department that will review this request
	beneficairyEmployeesIds.add(requester.getEmpId());

	Long[] notificationsEmpsIds = computeInstanceNotifications(categoriesIds, signManager.getPhysicalRegionId(), instance.getProcessId(), beneficairyEmployeesIds, additionalIds);

	closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), notificationsEmpsIds, session);

    }

    private static void doTerminationIntegration(EmployeeData requester, WFTerminationData wfTermination, WFInstance instance, TerminationRecordData terminationRecordData,
	    List<TerminationRecordDetailData> terminationRecordDetailDataList, Long loginEmpId, EmployeeData signManager, long tansactionTypeId, CustomSession session) throws BusinessException {
	try {
	    Long[] empsIds = new Long[terminationRecordDetailDataList.size()];
	    HashMap<Long, TerminationRecordDetailData> employeesTerminationRecordDetailDataMap = new HashMap<Long, TerminationRecordDetailData>();
	    for (int i = 0; i < terminationRecordDetailDataList.size(); i++) {
		empsIds[i] = terminationRecordDetailDataList.get(i).getEmpId();
		employeesTerminationRecordDetailDataMap.put(empsIds[i], terminationRecordDetailDataList.get(i));
	    }

	    List<EmployeeData> tempEmployees = EmployeesService.getEmployeesByEmpsIds(empsIds);
	    List<Long> empsRanksIds = new ArrayList<Long>();

	    for (int i = 0; i < tempEmployees.size(); i++) {
		empsRanksIds.add(employeesTerminationRecordDetailDataMap.get(tempEmployees.get(i).getEmpId()).getEmpRankId());
	    }

	    TerminationsService.validateTerminationsPromotionsIntegration(tempEmployees, empsRanksIds);

	    TerminationsService.saveTerminationRecord(terminationRecordData, loginEmpId, session);

	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    // set serviceTerminationDate for detail and transaction and update Employee status and job and unit
	    if (terminationRecordData.getTypeFlag().equals(TerminationTypeFlagEnum.REQUEST.getCode())) {
		if (terminationRecordDetailDataList.get(0).getDesiredTerminationDate() == null)
		    terminationRecordDetailDataList.get(0).setServiceTerminationDate(curHijriDate);
		else
		    terminationRecordDetailDataList.get(0).setServiceTerminationDate(terminationRecordDetailDataList.get(0).getDesiredTerminationDate());
	    } else { // Record cases
		if (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_DISPENSING_SERVICES.getCode()) || terminationRecordData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_DISQUALIFICATION_MILITARY_SERVICE.getCode())) {
		    terminationRecordDetailDataList.get(0).setServiceTerminationDate(curHijriDate);
		} else if (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_TERMINATION_REQUEST.getCode()) || terminationRecordData.getReasonId().equals(TerminationReasonsEnum.PERSONS_TERMINATION_REQUEST.getCode())) {
		    terminationRecordDetailDataList.get(0).setServiceTerminationDate(terminationRecordDetailDataList.get(0).getDesiredTerminationDate());
		} else if (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode()) || terminationRecordData.getReasonId().equals(TerminationReasonsEnum.PERSONS_REACHING_RETIREMENT_AGE.getCode())) {
		    for (TerminationRecordDetailData terminationRecordDetailDataItr : terminationRecordDetailDataList) {
			if (terminationRecordDetailDataItr.getEmpLastExtensionEndDate() != null)
			    terminationRecordDetailDataItr.setServiceTerminationDate(terminationRecordDetailDataItr.getEmpLastExtensionEndDate());
			else
			    terminationRecordDetailDataItr.setServiceTerminationDate(terminationRecordDetailDataItr.getEmpTerminationDueDate());
		    }
		}
	    }

	    TerminationsService.addModifyTerminationRecordDetail(terminationRecordData, terminationRecordDetailDataList, loginEmpId, session);

	    // construct transaction
	    List<TerminationTransactionData> terminationTransactionList = TerminationsService.constructTerminationTransactions(terminationRecordData, terminationRecordDetailDataList, tansactionTypeId);
	    List<TerminationTransactionData> nonFutureTerminationTransaction = new ArrayList<>();
	    for (TerminationTransactionData terminationTransactionData : terminationTransactionList) {
		if (!terminationTransactionData.getServiceTerminationDate().after(HijriDateService.getHijriSysDate()))
		    nonFutureTerminationTransaction.add(terminationTransactionData);
	    }
	    // set Decision Region Id
	    for (TerminationTransactionData terminationTransaction : terminationTransactionList) {
		terminationTransaction.setDecisionRegionId(signManager.getPhysicalRegionId());
	    }

	    // save transaction
	    TerminationsService.addTerminationTransaction(terminationTransactionList, loginEmpId, getWFProcess(instance.getProcessId()).getProcessName(), session);

	    // Termination and Employee Integration
	    // case of request
	    if (terminationRecordData.getTypeFlag().equals(TerminationTypeFlagEnum.REQUEST.getCode())) {

		requester.setServiceTerminationDate(terminationRecordDetailDataList.get(0).getServiceTerminationDate());
		tempEmployees = new ArrayList<EmployeeData>(1);
		tempEmployees.add(requester);
		TerminationsService.terminateEmployeeService(tempEmployees, session);
	    } else {

		for (EmployeeData emp : tempEmployees)
		    emp.setServiceTerminationDate(employeesTerminationRecordDetailDataMap.get(emp.getEmpId()).getServiceTerminationDate());

		TerminationsService.terminateEmployeeService(tempEmployees, session);
		if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		    TerminationsService.doPayrollIntegration(terminationTransactionList, FlagsEnum.OFF.getCode(), session);
	    }
	    TerminationsService.logTerminatedEmployeeData(nonFutureTerminationTransaction, session);

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void handleTerminationsInvalidation(Long[] instancesIds, CustomSession session) throws BusinessException {
	try {
	    List<Long> terminationRecordsIds = getTerminationsRecordsIdsByInstancesIds(instancesIds);
	    Long[] recordsIds = new Long[terminationRecordsIds.size()];
	    recordsIds = terminationRecordsIds.isEmpty() ? null : terminationRecordsIds.toArray(recordsIds);

	    TerminationsService.closeTerminationRecords(recordsIds, session);

	} catch (BusinessException e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    /***********************************************************************************************************************/

    private static long getTerminationManagerId(long categoryId, long regionId) throws BusinessException {
	WFPosition position = new WFPosition();

	if (CategoriesEnum.OFFICERS.getCode() == categoryId)
	    if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
		position = getWFPosition(WFPositionsEnum.OFFICERS_SERVICE_TERMINATION_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    else
		position = getWFPosition(WFPositionsEnum.REGION_OFFICERS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);
	else if (CategoriesEnum.SOLDIERS.getCode() == categoryId)
	    if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
		position = getWFPosition(WFPositionsEnum.SOLDIERS_SERVICE_TERMINATION_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    else
		position = getWFPosition(WFPositionsEnum.REGION_SOLDIERS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);
	else if (CategoriesEnum.USERS.getCode() == categoryId || CategoriesEnum.WAGES.getCode() == categoryId)
	    position = getWFPosition(WFPositionsEnum.USERS_WAGES_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	else if (CategoriesEnum.CONTRACTORS.getCode() == categoryId)
	    position = getWFPosition(WFPositionsEnum.CONTRACTORS_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	else
	    position = getWFPosition(WFPositionsEnum.PERSONS_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());

	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();

    }

    private static long getVicePresidencyId() throws BusinessException {
	WFPosition position = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();
    }

    private static Long[] getTerminationCopiesNotifications(String copies) {
	Long[] empsGroup = null;
	if (copies == null || copies.equals(""))
	    return null;

	String[] list = copies.split(",");
	if (list.length > 0) {
	    empsGroup = new Long[list.length];
	    for (int i = 0; i < list.length; i++) {
		empsGroup[i] = Long.parseLong(list[i].trim());
	    }
	}

	return empsGroup;
    }

    /***********************************************************************************************************************/

    public static WFTerminationData getWFTerminationByInstanceId(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    return DataAccess.executeNamedQuery(WFTerminationData.class, QueryNamesEnum.WF_GET_TERMINATION_BY_INSTANCE_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * search for terminations workflow tasks by assignee employee
     * 
     * @param assigneeId
     *            id of the employee assigned to handle the task
     * @param assigneeWfRole
     *            the role of the employee assigned to handle the task
     * @return requested workflow tasks
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<Object> getWFTerminationsTasks(Long assigneeId, String[] assigneeWfRoles) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLES", assigneeWfRoles);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_WFTERMINATIONS_TASKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static WFTerminationCancellationMovementData getWFTerminationCancellationMovementByInstanceId(Long instanceId) throws BusinessException {
	try {
	    if (instanceId == null)
		return new WFTerminationCancellationMovementData();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);

	    return DataAccess.executeNamedQuery(WFTerminationCancellationMovementData.class, QueryNamesEnum.WF_GET_TERMINATION_CANCELATION_MOVEMENT_BY_INSTANCE_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    private static List<Long> getTerminationsRecordsIdsByInstancesIds(Long[] instancesIds) throws BusinessException {
	if (instancesIds == null || instancesIds.length == 0)
	    return new ArrayList<Long>();

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCES_IDS", instancesIds);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_GET_TERMINATIONS_RECORDS_IDS_BY_INSTANCES_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /***********************************************************************************************************************/

    public static List<WFTerminationCancellationMovement> getRunningTerminationCancellationMovements(Long[] empsIds, Long[] jobsIds, Long excludedInstanceId) throws BusinessException {
	try {
	    if ((empsIds == null || empsIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
		return new ArrayList<WFTerminationCancellationMovement>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMPS_IDS", (empsIds != null && empsIds.length > 0) ? empsIds : new Long[] { (long) FlagsEnum.ALL.getCode() });
	    qParams.put("P_JOBS_IDS", (jobsIds != null && jobsIds.length > 0) ? jobsIds : new Long[] { (long) FlagsEnum.ALL.getCode() });

	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId == null ? (long) FlagsEnum.ALL.getCode() : excludedInstanceId);

	    return DataAccess.executeNamedQuery(WFTerminationCancellationMovement.class, QueryNamesEnum.WF_GET_RUNNING_TERMINATION_CANCELLATION_MOVEMENTS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /************************************** TerminationCivilianMovement ************************************/

    public static TerminationTransactionData addTerminationDecisionCancellationEmployee(EmployeeData requester, EmployeeData selectedEmpData, WFTerminationCancellationMovementData wfCancellationMovementData, long mode) throws BusinessException {
	// 1- check that employee is service terminated!
	TerminationTransactionData cancelledTerminationTransactionData = TerminationsService.getEffectiveTerminationTransaction(selectedEmpData.getEmpId());
	if (cancelledTerminationTransactionData == null)
	    throw new BusinessException("error_steEmpShouldBeTerminated");

	if (cancelledTerminationTransactionData.getMigFlag() == FlagsEnum.ON.getCode())
	    throw new BusinessException("error_steCancelEtransactionNotValid");

	if (mode == CategoriesEnum.PERSONS.getCode() && cancelledTerminationTransactionData.getTransactionTypeCode().equals(TransactionTypesEnum.STE_TERMINATION_MOVEMENT.getCode()))
	    throw new BusinessException("error_steCanNotCancelTerminationMovement");

	if (!cancelledTerminationTransactionData.getDecisionRegionId().equals(requester.getPhysicalRegionId()))
	    throw new BusinessException("error_steEmpRegionNotSuitable");

	wfCancellationMovementData.setCancelTransactionId(cancelledTerminationTransactionData.getId());
	wfCancellationMovementData.setEmpId(selectedEmpData.getEmpId());
	wfCancellationMovementData.setEmpName(selectedEmpData.getName());

	// 1- check if employee still has a job
	if (selectedEmpData.getJobId() != null) {
	    wfCancellationMovementData.setJobId(selectedEmpData.getJobId());
	    wfCancellationMovementData.setJobName(selectedEmpData.getJobDesc());
	    wfCancellationMovementData.setJobCode(selectedEmpData.getJobCode());
	} else { // employee has NO job
		 // check if his last job is still vacant
	    JobData selectedJobData = JobsService.getJobById(cancelledTerminationTransactionData.getJobId());
	    if (selectedJobData.getStatus().equals(JobStatusEnum.VACANT.getCode()) && selectedJobData.getRankId().equals(selectedEmpData.getRankId())) {
		wfCancellationMovementData.setJobId(selectedJobData.getId());
		wfCancellationMovementData.setJobName(selectedJobData.getName());
		wfCancellationMovementData.setJobCode(selectedJobData.getCode());
	    } else {
		wfCancellationMovementData.setJobId(null);
		wfCancellationMovementData.setJobName(null);
		wfCancellationMovementData.setJobCode(null);
	    }
	}

	return cancelledTerminationTransactionData;
    }

    public static void initTerminationCancellationMovement(WFTerminationCancellationMovementData wfTerminationCancellationMovementData, Date movementTransactionExecutionDate, EmployeeData requester, long processId, String attachments, String taskUrl) throws BusinessException {

	Date curDate = new Date();
	Date curHijriDate = HijriDateService.getHijriSysDate();

	if (processId == WFProcessesEnum.CIVILIANS_TERMINATION_MOVEMENT.getCode())
	    validateTerminationCivilianMovement(wfTerminationCancellationMovementData, movementTransactionExecutionDate, curHijriDate);
	else if (processId == WFProcessesEnum.SOLDIERS_TERMINATION_CANCELLATION.getCode() || processId == WFProcessesEnum.CIVILIANS_TERMINATION_CANCELLATION.getCode())
	    validateTerminationCancellation(wfTerminationCancellationMovementData);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, Arrays.asList(wfTerminationCancellationMovementData.getEmpId()), session);
	    saveTerminationCivilianMovementWorkflow(wfTerminationCancellationMovementData, instance.getInstanceId(), session);
	    addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doTerminationCancellationMovementRE(EmployeeData requester, WFInstance instance, WFTerminationCancellationMovementData wfCancellationMovementData, Date movementTransactionExecutionDate, WFTask reTask, String attachments, boolean isApproved) throws BusinessException {

	if (isApproved) {
	    if (instance.getProcessId().equals(WFProcessesEnum.CIVILIANS_TERMINATION_MOVEMENT.getCode()))
		validateTerminationCivilianMovement(wfCancellationMovementData, movementTransactionExecutionDate, instance.getHijriRequestDate());
	    else if (instance.getProcessId().equals(WFProcessesEnum.SOLDIERS_TERMINATION_CANCELLATION.getCode()) || instance.getProcessId().equals(WFProcessesEnum.CIVILIANS_TERMINATION_CANCELLATION.getCode()))
		validateTerminationCancellation(wfCancellationMovementData);
	}

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    if (isApproved) {
		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();

		EmployeeData curDM = EmployeesService.getEmployeeDirectManager(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getEmpId(), instance.getProcessId(), requester.getEmpId()), curDM.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

		saveTerminationCivilianMovementWorkflow(wfCancellationMovementData, instance.getInstanceId(), session);
		updateWFInstanceBeneficiaries(instance.getInstanceId(), Arrays.asList(wfCancellationMovementData.getEmpId()), session);
		updateWFInstanceAttachments(instance, attachments, session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}

    }

    public static void doTerminationCancellationMovementSM(EmployeeData requester, WFInstance instance, WFTerminationCancellationMovementData wfCancellationMovementData, Long cancelledTransactionReasonId, WFTask smTask, long loginEmpId, long tansactionTypeId, Long categoryId, int approvalFlag) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    EmployeeData currentDirectManager = EmployeesService.getEmployeeData(smTask.getOriginalId());

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		long unitTypeCode = currentDirectManager.getUnitTypeCode();
		long requesterRegionId = requester.getPhysicalRegionId();
		if (instance.getProcessId().equals(WFProcessesEnum.CIVILIANS_TERMINATION_MOVEMENT.getCode())) {
		    if (unitTypeCode >= UnitTypesEnum.PRESIDENCY.getCode())
			closeTerminationCancellationMovementWorkFlow(requester, instance, wfCancellationMovementData, smTask, loginEmpId, tansactionTypeId, categoryId, session);
		    else {
			completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    }
		}

		else if (instance.getProcessId().equals(WFProcessesEnum.CIVILIANS_TERMINATION_CANCELLATION.getCode())) {
		    if (unitTypeCode >= UnitTypesEnum.PRESIDENCY.getCode())
			closeTerminationCancellationMovementWorkFlow(requester, instance, wfCancellationMovementData, smTask, loginEmpId, tansactionTypeId, categoryId, session);
		    else {
			completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    }
		}
		// case of Soldiers Cancellation
		else {

		    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == requesterRegionId) { // General directorate of border guards
			// cases that require approval of vice president
			if (cancelledTransactionReasonId.longValue() == TerminationReasonsEnum.SOLDIERS_DISMISS.getCode() || cancelledTransactionReasonId.longValue() == TerminationReasonsEnum.SOLDIERS_LOSS.getCode() || cancelledTransactionReasonId.longValue() == TerminationReasonsEnum.SOLDIERS_ABSENCE.getCode()) {
			    if (unitTypeCode >= UnitTypesEnum.PRESIDENCY.getCode())
				closeTerminationCancellationMovementWorkFlow(requester, instance, wfCancellationMovementData, smTask, loginEmpId, tansactionTypeId, categoryId, session);
			    else {
				if (EmployeesService.getEmployeeDirectManager(currentDirectManager.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
				    currentDirectManager.setManagerId(getVicePresidencyId());

				completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			    }
			}
			// cases that require approval of region commander
			else if (cancelledTransactionReasonId.longValue() == TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode() || cancelledTransactionReasonId.longValue() == TerminationReasonsEnum.SOLDIERS_DEATH.getCode() || cancelledTransactionReasonId.longValue() == TerminationReasonsEnum.SOLDIERS_LACK_MEDICAL_FITNESS.getCode() || cancelledTransactionReasonId.longValue() == TerminationReasonsEnum.SOLDIERS_FOREIGNER_MARRIAGE.getCode()) {
			    if (unitTypeCode >= UnitTypesEnum.ASSISTANT.getCode())
				closeTerminationCancellationMovementWorkFlow(requester, instance, wfCancellationMovementData, smTask, loginEmpId, tansactionTypeId, categoryId, session);
			    else {
				completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			    }
			}
			// cases that require approval of presidency
			else {
			    if (unitTypeCode >= UnitTypesEnum.PRESIDENCY.getCode())
				closeTerminationCancellationMovementWorkFlow(requester, instance, wfCancellationMovementData, smTask, loginEmpId, tansactionTypeId, categoryId, session);
			    else {
				completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			    }
			}
		    } else { // Regions
			if (unitTypeCode >= UnitTypesEnum.REGION_COMMANDER.getCode())
			    closeTerminationCancellationMovementWorkFlow(requester, instance, wfCancellationMovementData, smTask, loginEmpId, tansactionTypeId, categoryId, session);
			else {
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(currentDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), currentDirectManager.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			}
		    }
		}
		// check if reached last SM then closeTerminationWorkFlow() and completeWFTask() depending on region and type
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getEmpId(), instance.getProcessId(), requester.getEmpId()), requester.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {

	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}

    }

    private static void closeTerminationCancellationMovementWorkFlow(EmployeeData requester, WFInstance instance, WFTerminationCancellationMovementData wfTerminationCancellationData, WFTask smTask, long loginEmpId, long tansactionTypeId, Long categoryId, CustomSession session) throws BusinessException, DatabaseException {

	EmployeeData signManager = EmployeesService.getEmployeeData(smTask.getOriginalId());

	// Handle notifications.
	List<Long> categoriesIds = new ArrayList<Long>();
	List<Long> beneficairyEmployeesIds = new ArrayList<Long>();
	List<Long> additionalIds = new ArrayList<Long>();

	doTerminationCancellationMovementIntegration(instance.getProcessId(), wfTerminationCancellationData, loginEmpId, signManager, tansactionTypeId, categoryId, session);

	categoriesIds.add(categoryId);

	// add requester and manager of department that will review this request
	beneficairyEmployeesIds.add(requester.getEmpId());
	if (wfTerminationCancellationData.getInternalCopies() != null)
	    additionalIds.addAll(Arrays.asList(getTerminationCopiesNotifications(wfTerminationCancellationData.getInternalCopies())));

	Long[] notificationsEmpsIds = computeInstanceNotifications(categoriesIds, signManager.getPhysicalRegionId(), instance.getProcessId(), beneficairyEmployeesIds, additionalIds);

	closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), notificationsEmpsIds, session);
    }

    private static void doTerminationCancellationMovementIntegration(Long processId, WFTerminationCancellationMovementData wfTerminationCancellationMovementData, Long loginEmpId, EmployeeData signManager, long tansactionTypeId, Long categoryId, CustomSession session) throws BusinessException {
	try {

	    EmployeeData employee = EmployeesService.getEmployeeData(wfTerminationCancellationMovementData.getEmpId());
	    JobData empJob = null;

	    if (processId.equals(WFProcessesEnum.SOLDIERS_TERMINATION_CANCELLATION.getCode()) || processId.equals(WFProcessesEnum.CIVILIANS_TERMINATION_CANCELLATION.getCode()))
		empJob = JobsService.getJobById(wfTerminationCancellationMovementData.getJobId());

	    // construct transaction
	    TerminationTransactionData terminationTransaction = TerminationsService.constructTerminationCancellationMovementTransaction(employee, empJob, processId, wfTerminationCancellationMovementData, tansactionTypeId, categoryId);

	    // set Decision Region Id
	    terminationTransaction.setDecisionRegionId(signManager.getPhysicalRegionId());
	    terminationTransaction.setDecisionApprovedId(signManager.getEmpId());
	    terminationTransaction.setOriginalDecisionApprovedId(signManager.getEmpId());

	    List<TerminationTransactionData> tmpList = new ArrayList<TerminationTransactionData>();
	    tmpList.add(terminationTransaction);
	    // save transaction
	    TerminationsService.addTerminationTransaction(tmpList, loginEmpId, getWFProcess(processId).getProcessName(), session);

	    if (processId.equals(WFProcessesEnum.SOLDIERS_TERMINATION_CANCELLATION.getCode()) || processId.equals(WFProcessesEnum.CIVILIANS_TERMINATION_CANCELLATION.getCode())) {
		// in case of cancellation, employee status, job id, and unit should be changed again !

		Date empServiceTerminationDate = employee.getServiceTerminationDate();
		employee.setStatusId(EmployeeStatusEnum.ON_DUTY.getCode());
		JobsService.changeJobStatus(empJob, JobStatusEnum.OCCUPIED.getCode(), session);
		employee.setJobId(empJob.getId());
		employee.setOfficialUnitId(empJob.getUnitId());
		employee.setPhysicalUnitId(empJob.getUnitId());
		employee.setServiceTerminationDate(null);
		EmployeesService.updateEmployee(employee, session);
		if (!empServiceTerminationDate.after(HijriDateService.getHijriSysDate())) {
		    EmployeeLog log = new EmployeeLog.Builder().setJobId(empJob.getId()).setOfficialUnitId(empJob.getUnitId()).setPhysicalUnitId(empJob.getUnitId()).setBasicJobNameId(empJob.getBasicJobNameId()).constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), terminationTransaction.getDecisionNumber(), terminationTransaction.getDecisionDate(), empServiceTerminationDate, DataAccess.getTableName(TerminationTransaction.class)).build();
		    LogService.logEmployeeData(log, session);
		}
		if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		    TerminationsService.doPayrollIntegration(tmpList, FlagsEnum.OFF.getCode(), session);
	    }

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static WFPosition getWFPositionByCodeAndRegionId(int positionCode, long regionId) throws BusinessException {
	return getWFPosition(positionCode, regionId);
    }

    /********************************** Beneficiaries Operations **********************************/
    public static TerminationRecordDetailData addCollectiveReasonsTerminationRecordDetail(WFInstance instance, TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList, Long selectedEmployeeId, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<TerminationRecordDetailData> terminationReportDetailDataListBeneficiaries = new ArrayList<TerminationRecordDetailData>();
	    terminationReportDetailDataListBeneficiaries.add(TerminationsService.addCollectiveReasonsTerminationRecordDetail(terminationRecordData, terminationRecordDetailDataList, selectedEmployeeId, loginEmpId, session));

	    if (instance != null)
		addWFInstanceBeneficiaries(instance.getInstanceId(), getTerminationsReportsInstanceBeneficiariesIds(terminationReportDetailDataListBeneficiaries), session);

	    if (!isOpenedSession)
		session.commitTransaction();

	    return terminationReportDetailDataListBeneficiaries.get(0);
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deleteTerminationRecordDetail(WFInstance instance, List<TerminationRecordDetailData> terminationRecordDetailDataList, TerminationRecordDetailData terminationRecordDetailData, long loginEmpId, CustomSession... useSession) throws BusinessException {
	if (terminationRecordDetailData.getId() == null) {
	    terminationRecordDetailDataList.remove(terminationRecordDetailData);
	    return;
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (instance != null) {
		List<TerminationRecordDetailData> terminationRecordDetailDataListBeneficiaries = new ArrayList<TerminationRecordDetailData>();
		terminationRecordDetailDataListBeneficiaries.add(terminationRecordDetailData);
		deleteWFInstanceBeneficiaries(instance.getInstanceId(), getTerminationsReportsInstanceBeneficiariesIds(terminationRecordDetailDataListBeneficiaries), session);
	    }
	    TerminationsService.deleteTerminationRecordDetail(terminationRecordDetailDataList, terminationRecordDetailData, loginEmpId, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    private static List<Long> getTerminationsReportsInstanceBeneficiariesIds(List<TerminationRecordDetailData> terminationRecordDetails) throws BusinessException {

	List<Long> instanceBeneficiariesIds = new ArrayList<Long>();
	for (TerminationRecordDetailData terminationRecordDetailDataItr : terminationRecordDetails) {
	    instanceBeneficiariesIds.add(terminationRecordDetailDataItr.getEmpId());
	}

	return instanceBeneficiariesIds;
    }

}
