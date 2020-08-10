package com.code.services.workflow.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.retirements.DisclaimerTransactionData;
import com.code.dal.orm.hcm.retirements.DisclaimerTransactionDetail;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.retirements.WFDisclaimerData;
import com.code.dal.orm.workflow.hcm.retirements.WFDisclaimerDetail;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.RetirementsService;
import com.code.services.hcm.TerminationsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

/**
 * WorkFlow Service to control the flow of retirement process.
 */
public class RetirementsWorkFlow extends BaseWorkFlow {
    /**
     * Private constructor to prevent instantiation
     */
    private RetirementsWorkFlow() {
    }

    /*********************************************************** Retirements WorkFlow ***********************************************************/

    /*------------------------------------------------ Work Flow Steps --------------------------------------------------*/
    public static void initDisclaimer(EmployeeData requester,
	    long processId, String attachments, String taskUrl, WFDisclaimerData wfDisclaimerData) throws BusinessException {
	validateDisclaimerRequest(wfDisclaimerData);
	List<UnitData> managersUnits = getManagersUnits(wfDisclaimerData.getEmpOfficialUnitId(), wfDisclaimerData.getEmpCategoryId(), wfDisclaimerData.getEmpPhysicalRegionId(), wfDisclaimerData.getTerminationMigFlag());
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    String wfTaskRole;
	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, Arrays.asList(wfDisclaimerData.getEmpId()), session);
	    if (isRegionOfficerDisclaimerRequest(wfDisclaimerData))
		wfTaskRole = WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode();
	    else
		wfTaskRole = WFTaskRolesEnum.SIGN_MANAGER.getCode();
	    for (int i = 1; i < managersUnits.size() + 1; i++) {
		validateUnitManager(managersUnits.get(i - 1));
		Long unitManagerId = managersUnits.get(i - 1).getPhysicalManagerId();
		addWFTask(instance.getInstanceId(), getDelegate(unitManagerId, processId, requester.getEmpId()), unitManagerId, curDate, curHijriDate, taskUrl, wfTaskRole, "1." + i, session);
		// Add WfDisclaimerDetail
		EmployeeData unitManagerData = EmployeesService.getEmployeeData(unitManagerId);
		WFDisclaimerDetail wfDisclaimerDetail = RetirementsWorkFlow.constructWFDisclaimerDetail(instance.getInstanceId(), unitManagerData);
		addModifyWFDisclaimerDetail(wfDisclaimerDetail);
	    }
	    addModifyWFDisclaimerData(wfDisclaimerData, instance.getInstanceId(), session);
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

    public static void doSSM(EmployeeData requester, WFInstance instance, WFDisclaimerData wfDisclaimerData, WFDisclaimerDetail wfDisclaimerDetail,
	    WFTask ssmTask, int approvalFlag, Long reviewerEmpId) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    WFPosition position = getRegionPayrollUnitManager(wfDisclaimerData.getEmpPhysicalRegionId());
	    if (position == null) {
		throw new BusinessException("error_positionNotFound");
	    }
	    UnitData regionUnit = UnitsService.getUnitById(position.getUnitId());
	    if (approvalFlag == WFActionFlagsEnum.REDIRECT.getCode()) {
		if (reviewerEmpId == null)
		    throw new BusinessException("error_redirectToEmployeeMandatory");
		if (reviewerEmpId.equals(ssmTask.getOriginalId()))
		    throw new BusinessException("error_cannotRedirectToYourself");
		if (ssmTask.getAssigneeId().equals(regionUnit.getPhysicalManagerId())) {
		    updateDisclaimerAmounts(wfDisclaimerData);
		    addModifyWFDisclaimerData(wfDisclaimerData, null, session);
		}
		WFTask task = completeWFTask(ssmTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), ssmTask.getLevel(), session);
		Long originalPhysicalUnitId = EmployeesService.getEmployeeData(ssmTask.getOriginalId()).getPhysicalUnitId();
		task.setFlexField3(originalPhysicalUnitId + "");
		DataAccess.updateEntity(task, session);
	    } else if (approvalFlag == WFActionFlagsEnum.DISCLAIM.getCode() || approvalFlag == WFActionFlagsEnum.CLAIM.getCode()) {
		if (ssmTask.getAssigneeId().equals(regionUnit.getPhysicalManagerId())) {
		    validateDisclaimerAmounts(wfDisclaimerData, approvalFlag);
		    updateDisclaimerAmounts(wfDisclaimerData);
		    addModifyWFDisclaimerData(wfDisclaimerData, null, session);
		}
		String wfTaskAction = (approvalFlag == WFActionFlagsEnum.DISCLAIM.getCode()) ? WFTaskActionsEnum.DISCLAIM.getCode() : WFTaskActionsEnum.CLAIM.getCode();
		if (countUncompletedWFInstanceTasks(instance.getInstanceId()) == FlagsEnum.ON.getCode()) { // if function returned 1, it means that current task is the only remaining task so a task for ESM will be added
		    if (wfDisclaimerData.getEmpCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
			List<UnitData> managersUnits = new ArrayList<>();
			if (wfDisclaimerData.getSentBackUnitsString() != null && !wfDisclaimerData.getSentBackUnitsString().equals("")) {
			    List<UnitData> sentBackUnits = UnitsService.getUnitsByIdsString(wfDisclaimerData.getSentBackUnitsString());

			    Set<Long> managersUnitsIds = new HashSet<Long>();
			    for (UnitData sentBackUnit : sentBackUnits) {
				if (sentBackUnit.getRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) { // add payrollUnitManager for emp region
				    managersUnits.add(sentBackUnit);
				    managersUnitsIds.add(sentBackUnit.getId());
				}
			    }
			    UnitData payrollGeneralDirectorateUnitData = getPayrollUnitData(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    managersUnitsIds.add(payrollGeneralDirectorateUnitData.getId());
			    if (managersUnitsIds.size() > managersUnits.size()) {
				managersUnits.add(payrollGeneralDirectorateUnitData);
			    }
			} else
			    managersUnits = getManagersUnits(null, CategoriesEnum.OFFICERS.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), FlagsEnum.ON.getCode());
			setWFTaskAction(ssmTask, wfTaskAction, curDate, curHijriDate, session);
			for (int i = 1; i < managersUnits.size() + 1; i++) {
			    validateUnitManager(managersUnits.get(i - 1));
			    Long unitManagerId = managersUnits.get(i - 1).getPhysicalManagerId();
			    addWFTask(instance.getInstanceId(), getDelegate(unitManagerId, instance.getProcessId(), requester.getEmpId()), unitManagerId, curDate, curHijriDate, ssmTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), ssmTask.getLevel() + "." + i, session);
			    // Add or modify WfDisclaimerDetail
			    EmployeeData unitManagerData = EmployeesService.getEmployeeData(unitManagerId);
			    WFDisclaimerDetail managerWFDisclaimerDetail = new WFDisclaimerDetail();
			    if (wfDisclaimerData.getSentBackUnitsString() == null || wfDisclaimerData.getSentBackUnitsString().equals(""))
				managerWFDisclaimerDetail = RetirementsWorkFlow.constructWFDisclaimerDetail(instance.getInstanceId(), unitManagerData);
			    else {
				List<WFDisclaimerDetail> managerWFDisclaimerDetailList = getWFDisclaimerDetailsByManagerUnitIdAndInstanceId(EmployeesService.getEmployeeData(unitManagerId).getPhysicalUnitId(), instance.getInstanceId());
				managerWFDisclaimerDetail = (managerWFDisclaimerDetailList != null && managerWFDisclaimerDetailList.size() != 0) ? managerWFDisclaimerDetailList.get(0) : null;
				if (managerWFDisclaimerDetail != null)
				    managerWFDisclaimerDetail.setClaimedFlag(FlagsEnum.OFF.getCode());
			    }
			    addModifyWFDisclaimerDetail(managerWFDisclaimerDetail);
			}
		    } else {
			throw new BusinessException("error_transactionDataError");
		    }
		} else {
		    setWFTaskAction(ssmTask, wfTaskAction, curDate, curHijriDate, session);
		}
		addModifyWFDisclaimerDetail(wfDisclaimerDetail, session);
	    } else {
		throw new BusinessException("error_transactionDataError");
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

    public static void doSRE(EmployeeData requester, WFDisclaimerData wfDisclaimerData, long processId, WFTask sreTask, int reviewFlag, Long reviewerEmpId) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    if (reviewFlag == WFActionFlagsEnum.REDIRECT.getCode()) {
		if (reviewerEmpId == null)
		    throw new BusinessException("error_redirectToEmployeeMandatory");
		if (reviewerEmpId.equals(sreTask.getOriginalId()))
		    throw new BusinessException("error_cannotRedirectToYourself");
		if (reviewerEmpId.equals(EmployeesService.getEmployeeData(sreTask.getOriginalId()).getManagerId()))
		    throw new BusinessException("error_cannotRedirectTaskToYourUnitManager");
		WFTask task = completeWFTask(sreTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, wfDisclaimerData.getInstanceId(), getDelegate(reviewerEmpId, processId, requester.getEmpId()), reviewerEmpId, sreTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), sreTask.getLevel(), session);
		task.setFlexField1(sreTask.getOriginalId() + "");
		task.setFlexField2(EmployeesService.getEmployeeData(sreTask.getOriginalId()).getPhysicalUnitId() + "");
		task.setFlexField3(sreTask.getFlexField3());
		DataAccess.updateEntity(task, session);
	    } else if (reviewFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		WFTask previousTask = getPredecessorWFTask(sreTask);
		if (previousTask == null)
		    throw new BusinessException("error_transactionDataError");
		WFTask task = completeWFTask(sreTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, wfDisclaimerData.getInstanceId(), getDelegate(previousTask.getOriginalId(), processId, requester.getEmpId()), previousTask.getOriginalId(), previousTask.getTaskUrl(), previousTask.getAssigneeWfRole(), sreTask.getLevel(), session);
		task.setFlexField1(previousTask.getFlexField1());
		task.setFlexField2(previousTask.getFlexField2());
		task.setFlexField3(previousTask.getFlexField3());
		DataAccess.updateEntity(task, session);
		if (previousTask.getTaskId() != null) { // this condition is added to mark the task not to use it again
		    previousTask.setFlexField3(null); // if the task.flex3 is null, it means that it had been used before
		    DataAccess.updateEntity(previousTask, session);
		}
	    } else {
		throw new BusinessException("error_transactionDataError");
	    }
	    WFPosition position = getRegionPayrollUnitManager(wfDisclaimerData.getEmpPhysicalRegionId());
	    if (position == null) {
		throw new BusinessException("error_positionNotFound");
	    }
	    if (sreTask.getFlexField3().equals(UnitsService.getUnitById(position.getUnitId()).getId() + "")) {
		updateDisclaimerAmounts(wfDisclaimerData);
		addModifyWFDisclaimerData(wfDisclaimerData, null, session);
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

    public static void doSM(EmployeeData requester, WFInstance instance, WFDisclaimerData wfDisclaimerData, WFDisclaimerDetail wfDisclaimerDetail,
	    WFTask smTask, int approvalFlag, Long reviewerEmpId) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    WFPosition position = new WFPosition();
	    if (isRegionOfficerDisclaimerRequest(wfDisclaimerData)) {
		position = getRegionPayrollUnitManager(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    } else {
		position = getRegionPayrollUnitManager(wfDisclaimerData.getEmpPhysicalRegionId());
	    }
	    if (position == null) {
		throw new BusinessException("error_positionNotFound");
	    }
	    UnitData regionUnit = UnitsService.getUnitById(position.getUnitId());
	    if (approvalFlag == WFActionFlagsEnum.REDIRECT.getCode()) {
		if (reviewerEmpId == null)
		    throw new BusinessException("error_redirectToEmployeeMandatory");
		if (reviewerEmpId.equals(smTask.getOriginalId()))
		    throw new BusinessException("error_cannotRedirectToYourself");
		if (smTask.getAssigneeId().equals(regionUnit.getPhysicalManagerId())) {
		    updateDisclaimerAmounts(wfDisclaimerData);
		    addModifyWFDisclaimerData(wfDisclaimerData, null, session);
		}
		WFTask task = completeWFTask(smTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), smTask.getLevel(), session);
		Long originalPhysicalUnitId = EmployeesService.getEmployeeData(smTask.getOriginalId()).getPhysicalUnitId();
		task.setFlexField3(originalPhysicalUnitId + "");
		DataAccess.updateEntity(task, session);
	    } else if (approvalFlag == WFActionFlagsEnum.CLAIM.getCode() || approvalFlag == WFActionFlagsEnum.DISCLAIM.getCode()) {
		if (smTask.getAssigneeId().equals(regionUnit.getPhysicalManagerId())) {
		    validateDisclaimerAmounts(wfDisclaimerData, approvalFlag);
		    updateDisclaimerAmounts(wfDisclaimerData);
		    addModifyWFDisclaimerData(wfDisclaimerData, null, session);
		}
		String wfTaskAction = (approvalFlag == WFActionFlagsEnum.CLAIM.getCode()) ? WFTaskActionsEnum.CLAIM.getCode() : WFTaskActionsEnum.DISCLAIM.getCode();
		if (countUncompletedWFInstanceTasks(instance.getInstanceId()) == FlagsEnum.ON.getCode()) { // if function returned 1, it means that current task is the only remaining task so a task for ESM will be added
		    WFPosition retirementAffairsUnitManagerPosition = getWFPosition(WFPositionsEnum.RETIREMENT_AFFAIRS_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		    if (retirementAffairsUnitManagerPosition == null)
			throw new BusinessException("error_positionNotFound");
		    Long retirementAffairsUnitManagerId = UnitsService.getUnitPhysicalManagerId(retirementAffairsUnitManagerPosition.getUnitId());
		    if (retirementAffairsUnitManagerId == null)
			throw new BusinessException("error_positionNotFound");
		    completeWFTask(smTask, wfTaskAction, curDate, curHijriDate, instance.getInstanceId(), getDelegate(retirementAffairsUnitManagerId, instance.getProcessId(), requester.getEmpId()), retirementAffairsUnitManagerId, smTask.getTaskUrl(), WFTaskRolesEnum.EXTRA_SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		    wfDisclaimerData.setSentBackUnitsString(null);
		    addModifyWFDisclaimerData(wfDisclaimerData, instance.getInstanceId(), session);
		} else {
		    setWFTaskAction(smTask, wfTaskAction, curDate, curHijriDate, session);
		}
		addModifyWFDisclaimerDetail(wfDisclaimerDetail, session);
	    } else {
		throw new BusinessException("error_transactionDataError");
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

    public static void doRE(EmployeeData requester, WFDisclaimerData wfDisclaimerData, long processId, WFTask reTask, int reviewFlag, Long reviewerEmpId) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    if (reviewFlag == WFActionFlagsEnum.REDIRECT.getCode()) {
		if (reviewerEmpId == null)
		    throw new BusinessException("error_redirectToEmployeeMandatory");
		if (reviewerEmpId.equals(reTask.getOriginalId()))
		    throw new BusinessException("error_cannotRedirectToYourself");
		if (reviewerEmpId.equals(EmployeesService.getEmployeeData(reTask.getOriginalId()).getManagerId()))
		    throw new BusinessException("error_cannotRedirectTaskToYourUnitManager");
		WFTask task = completeWFTask(reTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, wfDisclaimerData.getInstanceId(), getDelegate(reviewerEmpId, processId, requester.getEmpId()), reviewerEmpId, reTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), reTask.getLevel(), session);
		task.setFlexField1(reTask.getOriginalId() + "");
		task.setFlexField2(EmployeesService.getEmployeeData(reTask.getOriginalId()).getPhysicalUnitId() + "");
		task.setFlexField3(reTask.getFlexField3());
		DataAccess.updateEntity(task, session);
	    } else if (reviewFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		WFTask previousTask = getPredecessorWFTask(reTask);
		if (previousTask == null)
		    throw new BusinessException("error_transactionDataError");
		WFTask task = completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, wfDisclaimerData.getInstanceId(), getDelegate(previousTask.getOriginalId(), processId, requester.getEmpId()), previousTask.getOriginalId(), previousTask.getTaskUrl(), previousTask.getAssigneeWfRole(), reTask.getLevel(), session);
		task.setFlexField1(previousTask.getFlexField1());
		task.setFlexField2(previousTask.getFlexField2());
		task.setFlexField3(previousTask.getFlexField3());
		DataAccess.updateEntity(task, session);
		if (previousTask.getTaskId() != null) { // this condition is added to mark the task not to use it again
		    previousTask.setFlexField3(null); // if the task.flex3 is null, it means that it had been used before
		    DataAccess.updateEntity(previousTask, session);
		}
	    } else {
		throw new BusinessException("error_transactionDataError");
	    }
	    WFPosition position = getRegionPayrollUnitManager(wfDisclaimerData.getEmpPhysicalRegionId());
	    if (position == null) {
		throw new BusinessException("error_positionNotFound");
	    }
	    if (reTask.getFlexField3().equals(UnitsService.getUnitById(position.getUnitId()).getId() + "")) {
		updateDisclaimerAmounts(wfDisclaimerData);
		addModifyWFDisclaimerData(wfDisclaimerData, null, session);
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

    private static UnitData getPayrollUnitData(Long physicalRegionId) throws BusinessException {
	WFPosition regionPosition = getRegionPayrollUnitManager(physicalRegionId);
	if (regionPosition == null) {
	    throw new BusinessException("error_positionNotFound");
	}
	return UnitsService.getUnitById(regionPosition.getUnitId());
    }

    public static Boolean isRegionOfficerDisclaimerRequest(WFDisclaimerData wfDisclaimerData) {
	if (wfDisclaimerData.getEmpPhysicalRegionId() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()
		&& wfDisclaimerData.getEmpCategoryId() == CategoriesEnum.OFFICERS.getCode())
	    return true;
	return false;
    }

    public static void doESM(EmployeeData requester, WFInstance instance, WFDisclaimerData wfDisclaimerData, WFTask esmTask, int actionFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    if (actionFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		if (!(wfDisclaimerData.getSentBackUnitsString() == null || wfDisclaimerData.getSentBackUnitsString().equals("")))
		    throw new BusinessException("error_cantApproveSentBackUnitsSelected");
		closeDisclaimerWorkFlow(requester, instance, wfDisclaimerData, esmTask, session);
	    } else if (actionFlag == WFActionFlagsEnum.REJECT.getCode()) {
		if (!(wfDisclaimerData.getSentBackUnitsString() == null || wfDisclaimerData.getSentBackUnitsString().equals("")))
		    throw new BusinessException("error_cantRejectSentBackUnitsSelected");
		closeWFInstanceByAction(requester.getEmpId(), instance, esmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    } else if (actionFlag == WFActionFlagsEnum.SEND_BACK_TO_UNITS.getCode()) {
		if (wfDisclaimerData.getSentBackUnitsString() == null || wfDisclaimerData.getSentBackUnitsString().equals(""))
		    throw new BusinessException("error_sentBackUnitsMandatory");
		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();
		List<UnitData> sentBackUnits = UnitsService.getUnitsByIdsString(wfDisclaimerData.getSentBackUnitsString());

		UnitData payrollRegionUnitData = getPayrollUnitData(wfDisclaimerData.getEmpPhysicalRegionId());
		UnitData payrollGeneralDirectorateUnitData = getPayrollUnitData(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		int i = 0; // for tasks levels

		Set<Long> regionsIds = new HashSet<Long>();
		if (isRegionOfficerDisclaimerRequest(wfDisclaimerData)) {
		    Set<Long> generalDirectorateUnitsIds = new HashSet<Long>();
		    for (UnitData sentBackUnit : sentBackUnits) {
			if (sentBackUnit.getRegionId() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
			    regionsIds.add(sentBackUnit.getId());
			} else {
			    generalDirectorateUnitsIds.add(sentBackUnit.getId());
			}
		    }

		    if (regionsIds.size() > 0) {// add tasks for all ssm and PayrollRegionManager
			for (Long unitId : regionsIds) {
			    i++;
			    UnitData regionUnitData = UnitsService.getUnitById(unitId);
			    addWFTask(instance.getInstanceId(), getDelegate(regionUnitData.getPhysicalManagerId(), instance.getProcessId(), requester.getEmpId()), regionUnitData.getPhysicalManagerId(), curDate, curHijriDate, esmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), esmTask.getLevel() + "." + i, session);
			}
			regionsIds.add(payrollRegionUnitData.getId());
			if (regionsIds.size() > i) {
			    i++;
			    addWFTask(instance.getInstanceId(), getDelegate(payrollRegionUnitData.getPhysicalManagerId(), instance.getProcessId(), requester.getEmpId()), payrollRegionUnitData.getPhysicalManagerId(), curDate, curHijriDate, esmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), esmTask.getLevel() + "." + i, session);
			}
		    } else if (generalDirectorateUnitsIds.size() > 0) {// add tasks for all sm and PayrollGeneralDirectorateManager
			for (Long unitId : generalDirectorateUnitsIds) {
			    i++;
			    UnitData generalDirectorateUnitData = UnitsService.getUnitById(unitId);
			    addWFTask(instance.getInstanceId(), getDelegate(generalDirectorateUnitData.getPhysicalManagerId(), instance.getProcessId(), requester.getEmpId()), generalDirectorateUnitData.getPhysicalManagerId(), curDate, curHijriDate, esmTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), esmTask.getLevel() + "." + i, session);
			}
			generalDirectorateUnitsIds.add(payrollGeneralDirectorateUnitData.getId());
			if (generalDirectorateUnitsIds.size() > i) {
			    i++;
			    addWFTask(instance.getInstanceId(), getDelegate(payrollGeneralDirectorateUnitData.getPhysicalManagerId(), instance.getProcessId(), requester.getEmpId()), payrollGeneralDirectorateUnitData.getPhysicalManagerId(), curDate, curHijriDate, esmTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), esmTask.getLevel() + "." + i, session);
			}
		    }
		} else { // if not regions officer then all tasks are sm
		    for (UnitData sentBackUnit : sentBackUnits) {
			i++;
			regionsIds.add(sentBackUnit.getId());
			addWFTask(instance.getInstanceId(), getDelegate(sentBackUnit.getPhysicalManagerId(), instance.getProcessId(), requester.getEmpId()), sentBackUnit.getPhysicalManagerId(), curDate, curHijriDate, esmTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), esmTask.getLevel() + "." + i, session);
		    }

		    regionsIds.add(payrollRegionUnitData.getId());
		    if (regionsIds.size() > i) {
			i++;
			addWFTask(instance.getInstanceId(), getDelegate(payrollRegionUnitData.getPhysicalManagerId(), instance.getProcessId(), requester.getEmpId()), payrollRegionUnitData.getPhysicalManagerId(), curDate, curHijriDate, esmTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), esmTask.getLevel() + "." + i, session);
		    }
		}
		setWFTaskAction(esmTask, WFTaskActionsEnum.SEND_BACK_TO_UNITS.getCode(), curDate, curHijriDate, session);
		addModifyWFDisclaimerData(wfDisclaimerData, instance.getInstanceId(), session);
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

    public static String getManagersUnitsIdsString(Long instanceId, Long empUnitRegionId) throws BusinessException {
	String unitsIdsString = "";
	List<WFDisclaimerDetail> wfDisclaiamerDetails = getWFDisclaimerDetailsByManagerUnitIdAndInstanceId(null, instanceId);

	for (WFDisclaimerDetail wfDisclaimerDetail : wfDisclaiamerDetails) {
	    UnitData unitData = UnitsService.getUnitByExactFullName(wfDisclaimerDetail.getManagerUnitFullName());
	    unitsIdsString += unitData.getId() + ",";
	}

	return unitsIdsString.substring(0, unitsIdsString.length() - 1);
    }

    public static String getSentBackUnitsNames(String sentBackUnitsIdsString) throws BusinessException {
	String sentBackUnitsNamesString = "";
	List<UnitData> sentBackUnits = UnitsService.getUnitsByIdsString(sentBackUnitsIdsString);
	for (UnitData sentBackUnit : sentBackUnits) {
	    sentBackUnitsNamesString += sentBackUnit.getFullName() + ",";
	}
	return sentBackUnitsNamesString.substring(0, sentBackUnitsNamesString.length() - 1);
    }

    /*------------------------------------------------ Integration Operations --------------------------------------------------*/
    private static void closeDisclaimerWorkFlow(EmployeeData requester, WFInstance instance, WFDisclaimerData wfDisclaimerData,
	    WFTask esmTask, CustomSession session) throws BusinessException {
	try {
	    wfDisclaimerData.setDecisionApprovedId(esmTask.getOriginalId());
	    wfDisclaimerData.setOriginalDecisionApprovedId(esmTask.getOriginalId());
	    DataAccess.updateEntity(wfDisclaimerData.getWFDisclaimer(), session);
	    doDisclaimerIntegration(wfDisclaimerData, searchWFDisclaimerDetails(null, null, instance.getInstanceId()), getWFProcess(instance.getProcessId()).getProcessName(), requester.getEmpId(), session);

	    Long[] allCopies = computeInstanceNotifications(new ArrayList<Long>(Arrays.asList(wfDisclaimerData.getEmpCategoryId())), wfDisclaimerData.getEmpPhysicalRegionId(), instance.getProcessId(), new ArrayList<Long>(Arrays.asList(wfDisclaimerData.getEmpId())), new ArrayList<Long>(Arrays.asList(wfDisclaimerData.getEmpId())));
	    closeWFInstanceByAction(requester.getEmpId(), instance, esmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), allCopies, session);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void doDisclaimerIntegration(WFDisclaimerData wfDisclaimerData,
	    List<WFDisclaimerDetail> wfDisclaimerDetails, String processName, Long requester, CustomSession session) throws BusinessException {
	RetirementsService.saveDisclaimerTransactionAndDetails(constructDisclaimerTransaction(wfDisclaimerData), constructDisclaimerTransactionDetails(wfDisclaimerDetails), processName, session);
    }

    private static DisclaimerTransactionData constructDisclaimerTransaction(WFDisclaimerData wfDisclaimerData) {
	DisclaimerTransactionData disclaimerTransactionData = new DisclaimerTransactionData();
	disclaimerTransactionData.setEmpId(wfDisclaimerData.getEmpId());
	disclaimerTransactionData.setTerminationTransactionId(wfDisclaimerData.getTerminationTransactionId());
	disclaimerTransactionData.setTransEmpRankDesc(wfDisclaimerData.getEmpRankDesc());
	disclaimerTransactionData.setTransEmpJobDesc(wfDisclaimerData.getEmpJobDesc());
	disclaimerTransactionData.setTransEmpUnitFullName(wfDisclaimerData.getEmpOfficialUnitFullName());
	disclaimerTransactionData.setTransTerminationReason(wfDisclaimerData.getTerminationReason());
	disclaimerTransactionData.setTransServiceTerminationDate(wfDisclaimerData.getServiceTerminationDate());
	disclaimerTransactionData.setAllowanceAmount(wfDisclaimerData.getAllowanceAmount());
	disclaimerTransactionData.setBasicAmount(wfDisclaimerData.getBasicAmount());
	disclaimerTransactionData.setCreditBankAmount(wfDisclaimerData.getCreditBankAmount());
	disclaimerTransactionData.setDecisionApprovedId(wfDisclaimerData.getDecisionApprovedId());
	disclaimerTransactionData.setDeductionReason(wfDisclaimerData.getDeductionReason());
	disclaimerTransactionData.seteFlag(FlagsEnum.ON.getCode());
	disclaimerTransactionData.setMigFlag(FlagsEnum.OFF.getCode());
	disclaimerTransactionData.setOriginalDecisionApprovedId(wfDisclaimerData.getOriginalDecisionApprovedId());
	disclaimerTransactionData.setOtherAmount(wfDisclaimerData.getOtherAmount());
	disclaimerTransactionData.setRealEstateFundAmount(wfDisclaimerData.getRealEstateFundAmount());
	updateDisclaimerAmounts(wfDisclaimerData);
	disclaimerTransactionData.setTotalDueAmount(wfDisclaimerData.getBasicAmount().doubleValue() + wfDisclaimerData.getAllowanceAmount().doubleValue());
	disclaimerTransactionData.setTotalGovernmentalDueAmount(wfDisclaimerData.getCreditBankAmount().doubleValue() + wfDisclaimerData.getRealEstateFundAmount().doubleValue()
		+ wfDisclaimerData.getOtherAmount().doubleValue());
	disclaimerTransactionData.setTransEmpCategoryId(wfDisclaimerData.getEmpCategoryId());

	return disclaimerTransactionData;
    }

    private static List<DisclaimerTransactionDetail> constructDisclaimerTransactionDetails(List<WFDisclaimerDetail> wfDisclaimerDetails) throws BusinessException {
	List<DisclaimerTransactionDetail> disclaimerTransactionDetails = new ArrayList<DisclaimerTransactionDetail>();

	for (WFDisclaimerDetail wfDisclaimerDetail : wfDisclaimerDetails) {
	    DisclaimerTransactionDetail disclaimerTransactionDetail = new DisclaimerTransactionDetail();

	    disclaimerTransactionDetail.setManagerId(UnitsService.getUnitById(wfDisclaimerDetail.getManagerUnitId()).getPhysicalManagerId());
	    disclaimerTransactionDetail.setTransManagerUnitId(wfDisclaimerDetail.getManagerUnitId());
	    disclaimerTransactionDetail.setTransManagerJobDesc(wfDisclaimerDetail.getManagerJobDesc());
	    disclaimerTransactionDetail.setTransManagerName(wfDisclaimerDetail.getManagerName());
	    disclaimerTransactionDetail.setTransManagerUnitFullName(wfDisclaimerDetail.getManagerUnitFullName());
	    disclaimerTransactionDetail.setTransManagerRankDesc(wfDisclaimerDetail.getManagerRankDesc());
	    if (wfDisclaimerDetail.getClaimedFlag() == null)
		wfDisclaimerDetail.setClaimedFlag(FlagsEnum.OFF.getCode());
	    disclaimerTransactionDetail.setClaimedFlag(wfDisclaimerDetail.getClaimedFlag());

	    disclaimerTransactionDetails.add(disclaimerTransactionDetail);
	}
	return disclaimerTransactionDetails;
    }

    /*********************************************** WFDisclaimerData ***********************************************************/
    public static WFDisclaimerData constructWFDisclaimerData(EmployeeData empData) throws BusinessException {

	TerminationTransactionData terminationTransaction = TerminationsService.getEffectiveTerminationTransaction(empData.getEmpId());
	WFDisclaimerData wfDisclaimerData = new WFDisclaimerData();
	wfDisclaimerData.setEmpId(empData.getEmpId());
	wfDisclaimerData.setEmpCategoryId(empData.getCategoryId());
	if (terminationTransaction != null) {
	    wfDisclaimerData.setTerminationTransactionId(terminationTransaction.getId());
	    wfDisclaimerData.setServiceTerminationDate(terminationTransaction.getServiceTerminationDate());
	    wfDisclaimerData.setEmpJobDesc(terminationTransaction.getJobName());
	    wfDisclaimerData.setEmpRankDesc(terminationTransaction.getTransEmpRankDesc());
	    wfDisclaimerData.setEmpOfficialUnitFullName(terminationTransaction.getTransEmpUnitFullName());
	    wfDisclaimerData.setEmpOfficialUnitId(terminationTransaction.getTransEmpUnitId());
	    wfDisclaimerData.setTerminationMigFlag(terminationTransaction.getMigFlag());
	    wfDisclaimerData.setTerminationReason(terminationTransaction.getReasonDesc());
	    UnitData empUnitData = UnitsService.getUnitById(terminationTransaction.getTransEmpUnitId());
	    if (empUnitData == null)
		throw new BusinessException("error_steUnitIdMandatory");
	    wfDisclaimerData.setEmpPhysicalRegionId(empUnitData.getRegionId());
	}
	return wfDisclaimerData;
    }

    private static void addModifyWFDisclaimerData(WFDisclaimerData wfDisclaimerData, Long instanceId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    if (wfDisclaimerData.getInstanceId() == null) {
		wfDisclaimerData.setInstanceId(instanceId);
		DataAccess.addEntity(wfDisclaimerData.getWFDisclaimer(), session);
	    } else
		DataAccess.updateEntity(wfDisclaimerData.getWFDisclaimer(), session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (instanceId != null)
		wfDisclaimerData.setInstanceId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void updateDisclaimerAmounts(WFDisclaimerData wfDisclaimerData) {
	if (wfDisclaimerData.getBasicAmount() == null)
	    wfDisclaimerData.setBasicAmount(0D);
	if (wfDisclaimerData.getAllowanceAmount() == null)
	    wfDisclaimerData.setAllowanceAmount(0D);
	if (wfDisclaimerData.getRealEstateFundAmount() == null)
	    wfDisclaimerData.setRealEstateFundAmount(0D);
	if (wfDisclaimerData.getOtherAmount() == null)
	    wfDisclaimerData.setOtherAmount(0D);
	if (wfDisclaimerData.getCreditBankAmount() == null)
	    wfDisclaimerData.setCreditBankAmount(0D);
    }

    /*********************************************** WFDisclaimerDetailData ***********************************************************/
    public static WFDisclaimerDetail constructWFDisclaimerDetail(long instanceId, EmployeeData employeeData) {
	WFDisclaimerDetail wfDisclaimerDetail = new WFDisclaimerDetail();
	wfDisclaimerDetail.setManagerId(employeeData.getEmpId());
	wfDisclaimerDetail.setManagerUnitId(employeeData.getPhysicalUnitId());
	wfDisclaimerDetail.setManagerJobDesc(employeeData.getJobDesc());
	wfDisclaimerDetail.setManagerName(employeeData.getName());
	wfDisclaimerDetail.setManagerRankDesc(employeeData.getRankDesc());
	wfDisclaimerDetail.setManagerUnitFullName(employeeData.getPhysicalUnitFullName());
	wfDisclaimerDetail.setInstanceId(instanceId);
	wfDisclaimerDetail.setClaimedFlag(FlagsEnum.OFF.getCode());
	return wfDisclaimerDetail;
    }

    public static void addModifyWFDisclaimerDetail(WFDisclaimerDetail wfDisclaimerDetail, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    if (wfDisclaimerDetail.getId() == null) {
		DataAccess.addEntity(wfDisclaimerDetail, session);
	    } else {
		DataAccess.updateEntity(wfDisclaimerDetail, session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
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

    /*------------------------------------------------ Validations --------------------------------------------------*/
    private static void validateDisclaimerRequest(WFDisclaimerData wfDisclaimerData) throws BusinessException {

	if (wfDisclaimerData == null)
	    throw new BusinessException("error_transactionDataError");
	if (wfDisclaimerData.getEmpId() == null)
	    throw new BusinessException("error_employeeMandatory");
	if (wfDisclaimerData.getEmpOfficialUnitFullName() == null)
	    throw new BusinessException("error_steOfficialUnitFullNameMandatory");
	if (wfDisclaimerData.getTerminationTransactionId() == null)
	    throw new BusinessException("error_steEmpShouldBeTerminated");
	if (RetirementsService.getDisclaimerTransByTerminationTransId(wfDisclaimerData.getTerminationTransactionId()) != null)
	    throw new BusinessException("error_previousDisclaimerRequestExists");
	if (!wfDisclaimerData.getTerminationTransactionId().equals(TerminationsService.getEffectiveTerminationTransaction(wfDisclaimerData.getEmpId()).getId()))
	    throw new BusinessException("error_steEmpShouldBeTerminated");

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { wfDisclaimerData.getEmpId() }, null, TransactionClassesEnum.RETIREMENTS.getCode(), false, wfDisclaimerData.getInstanceId());
    }

    private static void validateUnitManager(UnitData managerUnit) throws BusinessException {
	if (managerUnit.getPhysicalManagerId() == null) {
	    throw new BusinessException("error_disclaimerUnitManagerRequired", new Object[] { managerUnit.getFullName() });
	}
    }

    private static void validateDisclaimerAmounts(WFDisclaimerData wfDisclaimerData, int action) throws BusinessException {
	if (action == WFActionFlagsEnum.CLAIM.getCode() && (wfDisclaimerData.getTotalDueAmount() == null || wfDisclaimerData.getTotalDueAmount().doubleValue() == 0D) && (wfDisclaimerData.getTotalGovernmentalDueAmount() == null || wfDisclaimerData.getTotalGovernmentalDueAmount().doubleValue() == 0D))
	    throw new BusinessException("error_disclaimerAmountsMadantory");
	else if (action == WFActionFlagsEnum.CLAIM.getCode() && ((wfDisclaimerData.getAllowanceAmount() != null && wfDisclaimerData.getAllowanceAmount().doubleValue() < 0D) || (wfDisclaimerData.getBasicAmount() != null && wfDisclaimerData.getBasicAmount().doubleValue() < 0D) || (wfDisclaimerData.getCreditBankAmount() != null && wfDisclaimerData.getCreditBankAmount().doubleValue() < 0D)
		|| (wfDisclaimerData.getRealEstateFundAmount() != null && wfDisclaimerData.getRealEstateFundAmount().doubleValue() < 0D) || (wfDisclaimerData.getOtherAmount() != null && wfDisclaimerData.getOtherAmount().doubleValue() < 0D)))
	    throw new BusinessException("error_disclaimerAmountsMustBePositive");
	else if (action == WFActionFlagsEnum.DISCLAIM.getCode() && (wfDisclaimerData.getTotalDueAmount().doubleValue() != 0D || wfDisclaimerData.getTotalGovernmentalDueAmount().doubleValue() != 0D))
	    throw new BusinessException("error_claimedAmountsMustBeZero");
    }

    /*------------------------------------------------ Utilities --------------------------------------------------*/
    private static List<UnitData> getManagersUnits(Long unitId, Long empCategoryId, Long empPhysicalRegionId, Integer isMigratedFlag) throws BusinessException {
	List<UnitData> managersUnits = new ArrayList<>();
	if (isMigratedFlag == FlagsEnum.OFF.getCode()) {
	    UnitData associateManagerUnit = getAssociateManagerUnit(unitId);
	    if (associateManagerUnit != null) {
		if (associateManagerUnit.getPhysicalManagerId() != null)
		    managersUnits.add(associateManagerUnit);
	    }
	}
	String configuredManagersUnits = ETRConfigurationService.getDisclaimerUnitsIds(empPhysicalRegionId, empCategoryId);
	managersUnits.addAll(UnitsService.getUnitsByIdsString(configuredManagersUnits));
	WFPosition position = getRegionPayrollUnitManager(empPhysicalRegionId);
	if (position == null) {
	    throw new BusinessException("error_positionNotFound");
	}
	managersUnits.add(UnitsService.getUnitById(position.getUnitId()));
	return managersUnits;
    }

    private static UnitData getAssociateManagerUnit(Long unitId) throws BusinessException {
	try {
	    UnitData empUnit = unitId == null ? null : UnitsService.getUnitById(unitId);
	    if (empUnit == null)
		throw new BusinessException("error_transactionDataError");
	    return UnitsService.getAncestorUnderPresidencyByLevel(empUnit.getId(), empUnit.getRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? 2 : 3);
	} catch (BusinessException e) {
	    e.printStackTrace();
	    throw new BusinessException(e.getMessage());
	}
    }

    private static WFTask getPredecessorWFTask(WFTask currentTask) throws BusinessException {
	WFTask previousWFTask = null;
	if (currentTask.getFlexField1() == null) {
	    previousWFTask = new WFTask();
	    UnitData managerUnit = UnitsService.getUnitById(Long.parseLong(currentTask.getFlexField3()));
	    validateUnitManager(managerUnit);
	    previousWFTask.setOriginalId(managerUnit.getPhysicalManagerId());
	    previousWFTask.setTaskUrl(currentTask.getTaskUrl());
	    if (currentTask.getAssigneeWfRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()))
		previousWFTask.setAssigneeWfRole(WFTaskRolesEnum.SIGN_MANAGER.getCode());
	    else if (currentTask.getAssigneeWfRole().equals(WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode()))
		previousWFTask.setAssigneeWfRole(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode());
	    return previousWFTask;
	}
	List<WFTask> wfTasks = getWFInstanceCompletedTasksByLevelAndOriginalId(currentTask.getInstanceId(), currentTask.getLevel(), Long.parseLong(currentTask.getFlexField1()));
	if (wfTasks.size() != FlagsEnum.OFF.getCode()) {
	    for (WFTask wfTask : wfTasks) {
		if (WFTaskActionsEnum.REDIRECT.getCode().equals(wfTask.getAction()) && wfTask.getFlexField3() != null) {
		    previousWFTask = wfTask;
		    break;
		}
	    }
	}
	EmployeeData previousTaskOriginalEmployee = EmployeesService.getEmployeeData(Long.parseLong(currentTask.getFlexField1()));
	if (previousTaskOriginalEmployee.getPhysicalUnitId() != null && previousTaskOriginalEmployee.getPhysicalUnitId().equals(Long.parseLong(currentTask.getFlexField2())))
	    return previousWFTask;
	else
	    return getPredecessorWFTask(previousWFTask);
    }

    public static WFPosition getRegionPayrollUnitManager(Long physicalRegionId) throws BusinessException {
	try {
	    return getWFPosition(WFPositionsEnum.REGION_PAYROLL_UNIT_MANAGER.getCode(), physicalRegionId);
	} catch (BusinessException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*------------------------------------------------ Queries --------------------------------------------------*/

    public static List<WFDisclaimerData> getWFDisclaimerDataByInstanceIds(Long[] instanceIds) throws BusinessException {
	return searchWFDisclaimerData(instanceIds, null, FlagsEnum.ON.getCode());
    }

    public static List<WFDisclaimerData> getWFDisclaimerDataByTerminationTransId(Long terminationTransId) throws BusinessException {
	return searchWFDisclaimerData(null, terminationTransId, FlagsEnum.ALL.getCode());
    }

    private static List<WFDisclaimerData> searchWFDisclaimerData(Long[] instanceIds, Long disclaimerTerminationTRansactionId, int instanceIdFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", (instanceIds == null || instanceIds.length == 0) ? new Long[] { (long) FlagsEnum.ALL.getCode() } : instanceIds);
	    qParams.put("P_TERMINATION_TRANS_ID", disclaimerTerminationTRansactionId == null ? (long) FlagsEnum.ALL.getCode() : disclaimerTerminationTRansactionId);
	    qParams.put("P_INSTANCE_ID_FLAG", instanceIdFlag);
	    return DataAccess.executeNamedQuery(WFDisclaimerData.class, QueryNamesEnum.WF_GET_DISCLAIMER_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFDisclaimerDetail> getWFDisclaimerDetailsByManagerUnitIdAndInstanceId(Long managerUnitId, Long instanceId) throws BusinessException {
	return searchWFDisclaimerDetails(null, managerUnitId, instanceId);
    }

    private static List<WFDisclaimerDetail> searchWFDisclaimerDetails(Long managerId, Long managerUnitId, Long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MANAGER_ID", managerId == null ? FlagsEnum.ALL.getCode() : managerId);
	    qParams.put("P_MANAGER_UNIT_ID", managerUnitId == null ? FlagsEnum.ALL.getCode() : managerUnitId);
	    qParams.put("P_INSTANCE_ID", instanceId == null ? FlagsEnum.ALL.getCode() : instanceId);
	    return DataAccess.executeNamedQuery(WFDisclaimerDetail.class, QueryNamesEnum.WF_GET_DISCLAIMER_DETAILS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFDisclaimerData> getRunningDisclaimerRequests(Long[] empsIds, Long excludedInstanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMPS_IDS", (empsIds == null || empsIds.length == 0) ? new Long[] { (long) FlagsEnum.ALL.getCode() } : empsIds);
	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId == null ? FlagsEnum.ALL.getCode() : excludedInstanceId);
	    return DataAccess.executeNamedQuery(WFDisclaimerData.class, QueryNamesEnum.WF_GET_RUNNING_DISCLAIMER_REQUESTS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<Object> getWFRetirementsTasks(Long assigneeId, String assigneeWfRole) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLE", assigneeWfRole);
	    qParams.put("P_PROCESS_GROUP_ID", WFProcessesGroupsEnum.RETIREMENTS.getCode());
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_RETIREMENT_TASKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}