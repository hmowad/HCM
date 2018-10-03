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
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.jobs.JobTransaction;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.organization.jobs.WFJob;
import com.code.dal.orm.workflow.hcm.organization.jobs.WFJobData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

public class JobsWorkFlow extends BaseWorkFlow {

    private JobsWorkFlow() {

    }

    /*-------------------------------------------- Steps -------------------------------------------------------------------------*/

    public static void initJob(EmployeeData requester, long categoryId, String referring, String reasons, String remarks, Map<Long, List<WFJobData>> jobsRequests,
	    String internalCopies, String externalCopies, Integer reservationStatus, long processId, String attachments, String taskUrl) throws BusinessException {

	validateWFJobs(categoryId, reasons, remarks, jobsRequests, reservationStatus, null);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, Arrays.asList(requester.getEmpId()), session);

	    if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()))
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
	    else
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);

	    saveWFJobs(jobsRequests, referring, reasons, remarks, internalCopies, externalCopies, reservationStatus, requester.getEmpId(), instance.getInstanceId(), session);

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

    // Secondary Sign Manager
    public static void doJobSSM(EmployeeData requester, WFInstance instance, WFTask ssmTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		EmployeeData curSSM = EmployeesService.getEmployeeData(ssmTask.getOriginalId());

		if (curSSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
		    // send the request to the manager redirect if the current secondary sign manager is the region commander
		    WFPosition position = getWFPosition(WFPositionsEnum.ORGANIZATION_ADMINISTRATIVE_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		    EmployeeData organizationAdminUnitManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
		    completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(organizationAdminUnitManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), organizationAdminUnitManager.getEmpId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1");
		} else {
		    // send the request to the next secondary sign manager
		    completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSSM.getManagerId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1");
		}
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		// return to the requester who is the secondary reviewer employee
		completeWFTask(ssmTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(instance.getRequesterId(), instance.getProcessId(), requester.getEmpId()), instance.getRequesterId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), "1", session);
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

    // Secondary Reviewer Employee
    public static void doJobSRE(EmployeeData requester, WFInstance instance, long categoryId, String referring, String reasons, String remarks,
	    Map<Long, List<WFJobData>> jobsRequests, String internalCopies, String externalCopies, Integer reservationStatus, String attachments,
	    WFTask sreTask, boolean isApproved) throws BusinessException {

	if (isApproved)
	    validateWFJobs(categoryId, reasons, remarks, jobsRequests, reservationStatus, instance.getInstanceId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (attachments != null && !attachments.isEmpty())
		updateWFInstanceAttachments(instance, attachments, session);

	    if (isApproved) {
		saveWFJobs(jobsRequests, referring, reasons, remarks, internalCopies, externalCopies, reservationStatus, sreTask.getAssigneeId(), instance.getInstanceId(), session);
		// send to secondary sign manager
		EmployeeData reviewerEmployee = EmployeesService.getEmployeeData(sreTask.getOriginalId());
		completeWFTask(sreTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmployee.getManagerId(), instance.getProcessId(), requester.getEmpId()), reviewerEmployee.getManagerId(), sreTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
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

    // Manager Redirect
    public static void doJobMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    // send to reviewer employee
	    completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1");
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // Reviewer Employee
    public static void doJobRE(EmployeeData requester, WFInstance instance, long categoryId, String referring, String reasons, String remarks,
	    Map<Long, List<WFJobData>> jobsRequests, String internalCopies, String externalCopies, Integer reservationStatus, String attachments,
	    WFTask reTask, boolean isApproved) throws BusinessException {

	if (isApproved)
	    validateWFJobs(categoryId, reasons, remarks, jobsRequests, reservationStatus, instance.getInstanceId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (attachments != null && !attachments.isEmpty())
		updateWFInstanceAttachments(instance, attachments, session);

	    if (isApproved) {
		saveWFJobs(jobsRequests, referring, reasons, remarks, internalCopies, externalCopies, reservationStatus, reTask.getAssigneeId(), instance.getInstanceId(), session);
		// send to sign manager
		EmployeeData reviewerEmployee = EmployeesService.getEmployeeData(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmployee.getManagerId(), instance.getProcessId(), requester.getEmpId()), reviewerEmployee.getManagerId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
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

    // Sign Manager
    public static void doJobSM(EmployeeData requester, WFInstance instance, long categoryId, String referring, String reasons, String remarks,
	    Map<Long, List<WFJobData>> jobsRequests, String internalCopies, String externalCopies, Integer reservationStatus, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		EmployeeData curSM = EmployeesService.getEmployeeData(smTask.getOriginalId());

		if (jobsRequests.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_RESERVE.getCode()))
			|| jobsRequests.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_UNRESERVE.getCode()))) {

		    if (EmployeesService.getEmployeeDirectManager(curSM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
			closeJobsWorkFlow(requester, instance, categoryId, referring, reasons, remarks, jobsRequests, internalCopies, externalCopies, reservationStatus, smTask, curSM.getPhysicalRegionId(), session);
		    else
			completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		} else {

		    if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
			closeJobsWorkFlow(requester, instance, categoryId, referring, reasons, remarks, jobsRequests, internalCopies, externalCopies, reservationStatus, smTask, curSM.getPhysicalRegionId(), session);
		    else
			completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		}
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {

		if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
		    // return to the requester who is the reviewer employee
		    completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(instance.getRequesterId(), instance.getProcessId(), requester.getEmpId()), instance.getRequesterId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
		} else {
		    // return to the reviewer employee
		    WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode()).get(0);
		    completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerTask.getOriginalId(), instance.getProcessId(), requester.getEmpId()), reviewerTask.getOriginalId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
		}
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

    private static void closeJobsWorkFlow(EmployeeData requester, WFInstance instance, long categoryId, String referring, String reasons, String remarks,
	    Map<Long, List<WFJobData>> jobsRequests, String internalCopies, String externalCopies, Integer reservationStatus,
	    WFTask finalTask, long decisionRegionId, CustomSession session) throws Exception {

	String[] etrCorInfo = ETRCorrespondence.doETRCorOut(getWFProcess(instance.getProcessId()).getProcessName(), session);
	String decisionNumber = etrCorInfo[0];
	String decisionDateString = etrCorInfo[1];

	Map<Long, List<JobData>> jobs = constructJobsMap(jobsRequests, categoryId, false);

	if (jobs.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_RESERVE.getCode()))) {

	    List<JobData> jobsToReserve = jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_RESERVE.getCode()));

	    List<Long> jobsIds = new ArrayList<Long>();
	    for (JobData job : jobsToReserve) {
		jobsIds.add(job.getId());
	    }

	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobsIds.toArray(new Long[jobsIds.size()]), TransactionClassesEnum.JOBS.getCode(), false, instance.getInstanceId());

	    JobTransaction decisionData = new JobTransaction();
	    decisionData.setDecisionNumber(decisionNumber);
	    decisionData.setDecisionDate(HijriDateService.getHijriDate(decisionDateString));
	    decisionData.setTransactionUserId(finalTask.getAssigneeId());
	    decisionData.setDecisionRegionId(decisionRegionId);
	    decisionData.setDecisionApprovedId(finalTask.getOriginalId());
	    decisionData.setOriginalDecisionApprovedId(finalTask.getOriginalId());
	    decisionData.setReferring(referring);
	    decisionData.setAttachments(instance.getAttachments());
	    decisionData.setInternalCopies(internalCopies);
	    decisionData.setExternalCopies(externalCopies);
	    decisionData.seteFlag(FlagsEnum.ON.getCode());

	    JobsService.reserveJobs(jobsToReserve, reservationStatus, remarks, decisionData, false, session);

	} else if (jobs.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_UNRESERVE.getCode()))) {

	    List<JobData> jobsToUnReserve = jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_UNRESERVE.getCode()));

	    List<Long> jobsIds = new ArrayList<Long>();
	    for (JobData job : jobsToUnReserve) {
		jobsIds.add(job.getId());
	    }

	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobsIds.toArray(new Long[jobsIds.size()]), TransactionClassesEnum.JOBS.getCode(), false, instance.getInstanceId());

	    JobTransaction decisionData = new JobTransaction();
	    decisionData.setDecisionNumber(decisionNumber);
	    decisionData.setDecisionDate(HijriDateService.getHijriDate(decisionDateString));
	    decisionData.setTransactionUserId(finalTask.getAssigneeId());
	    decisionData.setDecisionRegionId(decisionRegionId);
	    decisionData.setDecisionApprovedId(finalTask.getOriginalId());
	    decisionData.setOriginalDecisionApprovedId(finalTask.getOriginalId());
	    decisionData.setReferring(referring);
	    decisionData.setAttachments(instance.getAttachments());
	    decisionData.setInternalCopies(internalCopies);
	    decisionData.setExternalCopies(externalCopies);
	    decisionData.seteFlag(FlagsEnum.ON.getCode());

	    JobsService.unreserveJobs(jobsToUnReserve, decisionData, false, session);
	} else {

	    List<JobData> jobsToScale = new ArrayList<JobData>();
	    if (jobs.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_UP.getCode())))
		jobsToScale.addAll(jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_UP.getCode())));
	    if (jobs.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_DOWN.getCode())))
		jobsToScale.addAll(jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_DOWN.getCode())));

	    Long[] jobsIds = JobsService.getJobsIds(jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_RENAME.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_CANCELLATION.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_FREEZE.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_UNFREEZE.getCode())),
		    jobsToScale,
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_MOVE.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode())));

	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobsIds, TransactionClassesEnum.JOBS.getCode(), false, instance.getInstanceId());

	    JobTransaction decisionData = new JobTransaction();
	    decisionData.setDecisionNumber(decisionNumber);
	    decisionData.setDecisionDate(HijriDateService.getHijriDate(decisionDateString));
	    decisionData.setExecutionDate(HijriDateService.getHijriDate(decisionDateString));
	    decisionData.setTransactionUserId(finalTask.getAssigneeId());
	    decisionData.setDecisionRegionId(decisionRegionId);
	    decisionData.setDecisionApprovedId(finalTask.getOriginalId());
	    decisionData.setOriginalDecisionApprovedId(finalTask.getOriginalId());
	    decisionData.setReferring(referring);
	    decisionData.setAttachments(instance.getAttachments());
	    decisionData.setInternalCopies(internalCopies);
	    decisionData.setExternalCopies(externalCopies);
	    decisionData.seteFlag(FlagsEnum.ON.getCode());

	    JobsService.handleJobsTransactions(
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_RENAME.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_CANCELLATION.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_FREEZE.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_UNFREEZE.getCode())),
		    jobsToScale,
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_MOVE.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode())),
		    decisionData, session);
	}

	for (Map.Entry<Long, List<WFJobData>> entry : jobsRequests.entrySet()) {
	    for (WFJobData wfJobData : entry.getValue()) {
		wfJobData.setDecisionReference(decisionNumber + "," + decisionDateString);
		DataAccess.updateEntity(wfJobData.getWfJob(), session);
	    }
	}

	List<Long> categoriesIdsList = Arrays.asList(categoryId);
	List<Long> additionalIds = new ArrayList<Long>();

	if (internalCopies != null && !internalCopies.isEmpty()) {
	    List<Long> internalCopiesList = new ArrayList<Long>();
	    String[] internalCopiesEmployeesIds = internalCopies.split(",");
	    for (String internalCopyEmployeeId : internalCopiesEmployeesIds) {
		internalCopiesList.add(Long.valueOf(internalCopyEmployeeId));
	    }
	    additionalIds.addAll(internalCopiesList);
	}

	// Compute all copies
	Long[] allCopies = computeInstanceNotifications(categoriesIdsList, decisionRegionId, instance.getProcessId(), null, additionalIds);

	closeWFInstanceByAction(requester.getEmpId(), instance, finalTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), allCopies, session);
    }

    /*-------------------------------------------- Validations -------------------------------------------------------------------*/

    private static void validateWFJobs(long categoryId, String reasons, String remarks, Map<Long, List<WFJobData>> jobsRequests, Integer reservationStatus, Long instanceId) throws BusinessException {

	if (jobsRequests.isEmpty())
	    throw new BusinessException("error_atLeastOneJobShouldExist");

	Map<Long, List<JobData>> jobs = constructJobsMap(jobsRequests, categoryId, false);

	if (jobs.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_RESERVE.getCode()))) {

	    if (reservationStatus == null)
		throw new BusinessException("error_reservationStatusMandatroy");

	    List<JobData> JobsToReserve = jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_RESERVE.getCode()));

	    List<Long> jobsIds = new ArrayList<Long>();
	    for (JobData job : JobsToReserve) {
		jobsIds.add(job.getId());
	    }

	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobsIds.toArray(new Long[jobsIds.size()]), TransactionClassesEnum.JOBS.getCode(), false, instanceId);

	    JobsService.validateReserveJobs(JobsToReserve, remarks, false);

	} else if (jobs.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_UNRESERVE.getCode()))) {

	    List<JobData> JobsToUnReserve = jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_UNRESERVE.getCode()));

	    List<Long> jobsIds = new ArrayList<Long>();
	    for (JobData job : JobsToUnReserve) {
		jobsIds.add(job.getId());
	    }

	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobsIds.toArray(new Long[jobsIds.size()]), TransactionClassesEnum.JOBS.getCode(), false, instanceId);

	    JobsService.validateUnreserveJobs(JobsToUnReserve, false);

	} else {
	    if (jobsRequests.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode()))) {
		for (WFJobData wfJob : jobsRequests.get(getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode()))) {
		    if (wfJob.getJobsCount() == null || wfJob.getJobsCount().intValue() <= 0 || wfJob.getJobsCount().intValue() > 999)
			throw new BusinessException("error_jobsCountMandatory");
		}
	    }

	    if (categoryId == CategoriesEnum.SOLDIERS.getCode() && (reasons == null || reasons.trim().isEmpty()))
		throw new BusinessException("error_purposeIsMandatory");

	    List<JobData> jobsToScale = new ArrayList<>();
	    if (jobs.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_UP.getCode())))
		jobsToScale.addAll(jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_UP.getCode())));
	    if (jobs.containsKey(getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_DOWN.getCode())))
		jobsToScale.addAll(jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_DOWN.getCode())));

	    Long[] jobsIds = JobsService.getJobsIds(jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_RENAME.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_CANCELLATION.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_FREEZE.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_UNFREEZE.getCode())),
		    jobsToScale,
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_MOVE.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode())));

	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobsIds, TransactionClassesEnum.JOBS.getCode(), false, instanceId);

	    JobsService.validateJobs(
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_RENAME.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_CANCELLATION.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_FREEZE.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_UNFREEZE.getCode())),
		    jobsToScale,
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_MOVE.getCode())),
		    jobs.get(getTransactionTypeId(TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode())));
	}
    }

    /*-------------------------------------------- Queries -----------------------------------------------------------------------*/

    public static List<WFJob> getRunningRequests(Long[] jobsIds, Long excludedInstanceId) throws BusinessException {
	try {
	    if (jobsIds == null || jobsIds.length == 0)
		return new ArrayList<WFJob>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_JOBS_IDS", jobsIds);
	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId == null ? (long) FlagsEnum.ALL.getCode() : excludedInstanceId);

	    return DataAccess.executeNamedQuery(WFJob.class, QueryNamesEnum.WF_GET_RUNNING_JOBS_REQUESTS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFJobData> getWFJobsByInstanceId(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);

	    return DataAccess.executeNamedQuery(WFJobData.class, QueryNamesEnum.WF_GET_WFJOBS_BY_INSTANCE_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long countJobsRequestsByBasicJobNameId(Long basicJobNameId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_BASIC_JOB_NAME_ID", basicJobNameId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_COUNT_JOBS_REQUESTS_BY_BASIC_JOB_NAME_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void deleteWFJob(WFJobData wfJobData, long userId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    wfJobData.getWfJob().setSystemUser(userId + ""); // For Audit.
	    DataAccess.deleteEntity(wfJobData.getWfJob(), session);

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

    /*-------------------------------------------- Utilities ---------------------------------------------------------------------*/

    public static WFJobData constructWFJob(JobData jobData, int transactionTypeCode) {
	WFJobData wfJobData = new WFJobData();
	wfJobData.setJobId(jobData.getId());
	wfJobData.setCode(jobData.getCode());
	wfJobData.setBasicJobNameId(jobData.getBasicJobNameId());
	wfJobData.setName(jobData.getName());
	wfJobData.setRankId(jobData.getRankId());
	wfJobData.setRankDesc(jobData.getRankDescription());
	wfJobData.setMinorSpecializationId(jobData.getMinorSpecializationId());
	wfJobData.setMinorSpecializationDesc(jobData.getMinorSpecializationDescription());
	wfJobData.setUnitId(jobData.getUnitId());
	wfJobData.setUnitFullName(jobData.getUnitFullName());
	wfJobData.setApprovedFlag(jobData.getApprovedFlag());
	wfJobData.setTransactionTypeId(getTransactionTypeId(transactionTypeCode));
	return wfJobData;
    }

    private static Map<Long, List<JobData>> constructJobsMap(Map<Long, List<WFJobData>> jobsRequests, long categoryId, boolean skipJobsCount) throws BusinessException {
	try {
	    if (jobsRequests == null || jobsRequests.isEmpty())
		return new HashMap<Long, List<JobData>>();

	    // get ALL the jobs that are involved in this workflow.
	    // construct the comma separated jobs ids string.
	    StringBuilder jobsInWorkflowIds = new StringBuilder();
	    for (Map.Entry<Long, List<WFJobData>> entry : jobsRequests.entrySet()) {
		if (!entry.getKey().equals(getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode()))) {
		    for (WFJobData wfJobData : entry.getValue()) {
			jobsInWorkflowIds.append(wfJobData.getJobId()).append(",");
		    }
		}
	    }
	    // construct a map contains the job id and the job data.
	    Map<Long, JobData> jobsInWorkflowIdsMap = new HashMap<Long, JobData>();
	    if (jobsInWorkflowIds.length() > 0) {
		List<JobData> jobsInWorkflow = JobsService.getJobsByIdsString(jobsInWorkflowIds.substring(0, jobsInWorkflowIds.length() - 1));
		for (JobData jobData : jobsInWorkflow) {
		    jobsInWorkflowIdsMap.put(jobData.getId(), jobData);
		}
	    }

	    // construct a map contains the transaction type id and the jobs data.
	    Map<Long, List<JobData>> jobs = new HashMap<Long, List<JobData>>();
	    for (Map.Entry<Long, List<WFJobData>> entry : jobsRequests.entrySet()) {

		jobs.put(entry.getKey(), new ArrayList<JobData>());

		if (entry.getKey().equals(getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode()))) {
		    for (WFJobData wfJob : entry.getValue()) {
			int jobsCount = 0;
			do {
			    JobData jobData = new JobData();
			    jobData.setStatus(JobStatusEnum.VACANT.getCode());
			    jobData.setCategoryId(categoryId);
			    jobData.setBasicJobNameId(wfJob.getNewBasicJobNameId());
			    jobData.setName(wfJob.getNewName());
			    jobData.setRankId(wfJob.getNewRankId());
			    jobData.setRegionId(wfJob.getNewRegionId());
			    jobData.setUnitId(wfJob.getNewUnitId());
			    jobData.setUnitFullName(wfJob.getNewUnitFullName());
			    jobData.setMinorSpecializationId(wfJob.getNewMinorSpecializationId());
			    jobData.setMinorSpecializationDescription(wfJob.getNewMinorSpecializationDesc());
			    jobData.setGeneralSpecialization(wfJob.getNewGeneralSpecialization());
			    jobData.setGeneralType(wfJob.getNewGeneralType());
			    jobData.setReasons(wfJob.getReasons());
			    jobData.setRemarks(wfJob.getRemarks());
			    jobData.setApprovedFlag(categoryId == CategoriesEnum.SOLDIERS.getCode() ? wfJob.getNewApprovedFlag() : FlagsEnum.ON.getCode());

			    jobs.get(entry.getKey()).add(jobData);
			    jobsCount++;
			} while (wfJob.getJobsCount() != null && jobsCount < wfJob.getJobsCount() && !skipJobsCount);
		    }
		} else {
		    for (WFJobData wfJob : entry.getValue()) {
			JobData jobData = jobsInWorkflowIdsMap.get(wfJob.getJobId());

			jobData.setNewBasicJobNameId(wfJob.getNewBasicJobNameId());
			jobData.setNewName(wfJob.getNewName());
			jobData.setNewRankId(wfJob.getNewRankId());
			jobData.setNewMinorSpecializationId(wfJob.getNewMinorSpecializationId());
			jobData.setNewRegionId(wfJob.getNewRegionId());
			jobData.setNewUnitId(wfJob.getNewUnitId());
			jobData.setNewUnitFullName(wfJob.getNewUnitFullName());
			jobData.setReasons(wfJob.getReasons());
			if (wfJob.getTransactionTypeId().equals(getTransactionTypeId(TransactionTypesEnum.JOB_RESERVE.getCode())))
			    jobData.setReservationRemarks(wfJob.getRemarks());
			else
			    jobData.setRemarks(wfJob.getRemarks());

			jobs.get(entry.getKey()).add(jobData);
		    }
		}
	    }
	    return jobs;
	} catch (BusinessException e) {
	    throw e;
	}
    }

    private static void saveWFJobs(Map<Long, List<WFJobData>> jobsRequests, String referring, String reasons, String remarks, String internalCopies, String externalCopies, Integer reservationStatus, Long userId, Long instanceId, CustomSession session) throws DatabaseException {
	for (Map.Entry<Long, List<WFJobData>> wfJobs : jobsRequests.entrySet()) {
	    for (WFJobData wfJobData : wfJobs.getValue()) {
		wfJobData.setReferring(referring);
		wfJobData.setReasons(reasons);
		wfJobData.setRemarks(remarks);
		wfJobData.setInternalCopies(internalCopies);
		wfJobData.setExternalCopies(externalCopies);
		wfJobData.setNewReservationStatus(reservationStatus);
		wfJobData.getWfJob().setSystemUser(userId + ""); // For Auditing.
		if (wfJobData.getId() == null) {
		    wfJobData.setInstanceId(instanceId);
		    DataAccess.addEntity(wfJobData.getWfJob(), session);
		} else {
		    DataAccess.updateEntity(wfJobData.getWfJob(), session);
		}
	    }
	}
    }

    public static Long getTransactionTypeId(int transactionTypeCode) {
	return CommonService.getTransactionTypeByCodeAndClass(transactionTypeCode, TransactionClassesEnum.JOBS.getCode()).getId();
    }
}