package com.code.services.workflow.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransactionData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitment;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitmentData;
import com.code.enums.CategoriesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QualificationLevelRewardsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RecruitmentClassesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.UnitsAncestorsLevelsEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

/**
 * WorkFlow Service to control the flow of recruitment Decisions process
 */
public class RecruitmentsWorkFlow extends BaseWorkFlow {

    /**
     * Private constructor to prevent instantiation
     */
    private RecruitmentsWorkFlow() {
    }

    /*
     * ************************************** Work Flow Steps ***************************************
     */
    /**
     * <p>
     * Initiate movements workflow process
     * </p>
     * <ul>
     * <li>validate movements requests</li>
     * <li>create movements workflow instance</li>
     * <li>send task to appropriate employee</li>
     * </ul>
     * 
     * @param requester
     *            employee that instantiates the workflow process
     * @param recRequests
     *            recruitment requests to be handled
     * @param processId
     *            type of the requests process
     * @param attachments
     *            attached documents, photos etc... with requests
     * @param taskUrl
     *            url of the workflow task
     * @param internalCopiesEmployees
     *            employees which receive notification upon termination of the workflow instance
     * @param externalCopies
     *            employees that will receive hard copies from the transaction upon termination of the workflow instance
     * @throws BusinessException
     *             if any error occurs
     */
    public static void initRecruitment(EmployeeData requester, List<WFRecruitmentData> recRequests, long processId, String attachments, String taskUrl, List<EmployeeData> internalCopiesEmployees, String externalCopies) throws BusinessException {

	if (recRequests != null && recRequests.size() > 0) {
	    validateRecruitmentRequests(recRequests, null, processId, attachments);

	    CustomSession session = DataAccess.getSession();
	    try {
		session.beginTransaction();

		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();

		WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, getRecruitmentsInstanceBeneficiariesIds(recRequests), session);

		if (processId == WFProcessesEnum.OFFICERS_RECRUITMENT.getCode()) {
		    Set<Long> regionIds = new HashSet<Long>();
		    for (WFRecruitmentData wfRecruitment : recRequests) {
			if (wfRecruitment.getRecruitmentRegionId() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
			    regionIds.add(wfRecruitment.getRecruitmentRegionId());
			}
		    }

		    if (regionIds.size() > 0) {
			int subLevel = regionIds.size() == 1 ? 0 : 1;
			Iterator<Long> iterator = regionIds.iterator();
			while (iterator.hasNext()) {
			    EmployeeData militaryAffairsUnitManager = getMilitaryAffairsUnitManager(iterator.next());
			    addWFTask(instance.getInstanceId(), getDelegate(militaryAffairsUnitManager.getEmpId(), processId, requester.getEmpId()), militaryAffairsUnitManager.getEmpId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), "1" + (subLevel == 0 ? "" : ("." + (subLevel++))), session);
			}
		    } else {
			addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    }
		} else {
		    addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		}

		String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);
		saveWFRecruitments(recRequests, instance.getInstanceId(), internalCopies, externalCopies, session);

		session.commitTransaction();
	    } catch (Exception e) {
		session.rollbackTransaction();

		if (e instanceof BusinessException)
		    throw (BusinessException) e;

		e.printStackTrace();
		throw new BusinessException("error_general");
	    } finally {
		session.close();
	    }
	}
    }

    public static void doRecruitmentSRE(EmployeeData requester, WFInstance instance, long processId, List<WFRecruitmentData> recRequests, WFTask sreTask, List<EmployeeData> internalCopiesEmployees, String externalCopies, String attachments) throws BusinessException {
	if (recRequests != null && recRequests.size() > 0) {
	    validateRecruitmentRequests(recRequests, instance.getInstanceId(), processId, attachments);

	    CustomSession session = DataAccess.getSession();
	    try {
		session.beginTransaction();

		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();

		boolean allSREActionsDone = true;
		List<WFTask> secondaryReviewerEmpsTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode());
		for (WFTask wfTask : secondaryReviewerEmpsTasks) {
		    if (wfTask.getAction() == null && !wfTask.getAssigneeId().equals(sreTask.getAssigneeId())) {
			allSREActionsDone = false;
			break;
		    }
		}

		if (allSREActionsDone) {
		    completeWFTask(sreTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getEmpId(), instance.getProcessId(), requester.getEmpId()), requester.getEmpId(), sreTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), sreTask.getLevel(), session);
		} else {
		    setWFTaskAction(sreTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, session);
		}

		String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);
		saveWFRecruitments(recRequests, instance.getInstanceId(), internalCopies, externalCopies, session);

		session.commitTransaction();
	    } catch (Exception e) {
		session.rollbackTransaction();

		if (e instanceof BusinessException)
		    throw (BusinessException) e;

		e.printStackTrace();
		throw new BusinessException("error_general");
	    } finally {
		session.close();
	    }
	}
    }

    /**
     * Handles the actions of sign manager
     * 
     * @param requester
     *            employee that instantiates the workflow process
     * @param instance
     *            workflow instance
     * @param recRequests
     *            recruitment requests to be signed
     * @param smTask
     *            sign manager task
     * @param approvalFlag
     *            1 for approve, 0 for reject and 2 for return to reviewer
     * @throws BusinessException
     *             if any error occurs
     * @see WFActionFlagsEnum
     * 
     */
    public static void doRecruitmentSM(EmployeeData requester, WFInstance instance, List<WFRecruitmentData> recRequests, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		EmployeeData curSM = EmployeesService.getEmployeeData(smTask.getOriginalId());

		// close and notify if sign manager is PRESIDENCY, For persons whose ranks are tenth or less
		if (recRequests.get(0).getCategoryId().equals(CategoriesEnum.PERSONS.getCode()) && recRequests.get(0).getRankId().longValue() >= RanksEnum.TENTH.getCode()) {
		    if (EmployeesService.getEmployeeDirectManager(smTask.getOriginalId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
			closeRecruitmentWorkFlow(requester, instance, recRequests, smTask, session);
		    else
			completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		}
		// close and notify if Approved by PRESIDENCY
		else if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
		    closeRecruitmentWorkFlow(requester, instance, recRequests, smTask, session);
		else { // Send to next manager
		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		}
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode())
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getEmpId(), instance.getProcessId(), requester.getEmpId()), requester.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), smTask.getLevel(), session);
	    else {
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

    /**
     * handles the actions of reviewer employee
     * 
     * @param requester
     *            employee that instantiates the workflow process
     * @param recRequests
     *            recruitment requests to be reviewed
     * @param instance
     *            recruitment workflow instance
     * @param reTask
     *            reviewe employee task
     * @param internalCopiesEmployees
     *            employees which receive notification upon termination of the workflow instance
     * @param externalCopies
     *            employees that will receive hard copies from the transaction upon termination of the workflow instance
     * @throws BusinessException
     *             if any error occurs
     */
    public static void doRecruitmentRE(EmployeeData requester, List<WFRecruitmentData> recRequests, WFInstance instance, WFTask reTask, String attachments, List<EmployeeData> internalCopiesEmployees, String externalCopies, int approvalFlag) throws BusinessException {
	if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
	    if (recRequests != null && recRequests.size() > 0) {
		validateRecruitmentRequests(recRequests, instance.getInstanceId(), instance.getProcessId(), attachments);
	    }
	}
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		updateWFInstanceBeneficiaries(instance.getInstanceId(), getRecruitmentsInstanceBeneficiariesIds(recRequests), session);

		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();

		String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);

		saveWFRecruitments(recRequests, instance.getInstanceId(), internalCopies, externalCopies, session);

		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getManagerId(), instance.getProcessId(), requester.getEmpId()), requester.getManagerId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), reTask.getLevel(), session);

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    updateWFInstanceAttachments(instance, attachments, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}

    }

    /**
     * <p>
     * <ul>
     * <li>closes the recruitment workflow instance</li>
     * <li>do the appropriate integeration</li>
     * <li>send notification to the internal employees</li>
     * </ul>
     * </p>
     * 
     * @param requester
     *            employee that instantiates the workflow process
     * @param instance
     *            recruitment workflow instance
     * @param recRequests
     *            recruitment requests to be closed
     * @param smTask
     *            sign manager tasks
     * @param session
     *            optional database session
     * @throws BusinessException
     *             if any error occurs
     */
    private static void closeRecruitmentWorkFlow(EmployeeData requester, WFInstance instance, List<WFRecruitmentData> recRequests, WFTask smTask, CustomSession session) throws BusinessException {
	try {

	    List<Long> beneficairyEmployeesIds = new ArrayList<Long>();

	    for (WFRecruitmentData recRequest : recRequests) {
		recRequest.setDecisionApprovedId(smTask.getOriginalId());
		DataAccess.updateEntity(recRequest.getWfRecruitment(), session);

		beneficairyEmployeesIds.add(recRequest.getEmployeeId());
	    }

	    doRecruitmentIntegration(recRequests, instance, smTask, session);

	    // Get all recruitments needed copies (Internal copies + Dynamic copies + Computed copies)
	    List<Long> internalCopiesList = new ArrayList<Long>();

	    // Get internal copies
	    if (recRequests.get(0).getInternalCopies() != null) {
		String[] internalCopies = recRequests.get(0).getInternalCopies().split(",");
		for (int i = 0; i < internalCopies.length; i++) {
		    internalCopiesList.add(Long.parseLong(internalCopies[i]));
		}
	    }

	    // Compute all copies
	    Long[] allCopies = computeInstanceNotifications(new ArrayList<Long>(Arrays.asList(recRequests.get(0).getCategoryId())), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), instance.getProcessId(), beneficairyEmployeesIds, internalCopiesList);
	    // Get all recruitments needed copies (Internal copies + Dynamic copies + Computed copies)

	    closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), allCopies, session);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * integrate recruitment workflow process with recruitment transaction service
     * 
     * @param recRequests
     *            recruitment requests to be integerated
     * @param instance
     *            workflow instance
     * @param smTask
     *            sign manager task
     * @param session
     *            optional database session
     * @throws BusinessException
     *             if any error occurs
     */
    private static void doRecruitmentIntegration(List<WFRecruitmentData> recRequests, WFInstance instance, WFTask smTask, CustomSession session) throws BusinessException {
	List<RecruitmentTransactionData> recTransactions = constructRecTransactions(recRequests, instance.getProcessId(), instance.getAttachments());
	RecruitmentsService.handleRecRequests(recTransactions, getWFProcess(instance.getProcessId()).getProcessName(), session);
    }

    /**
     * construct recuitment transacations from recruitment workflow requests
     * 
     * @param recRequests
     *            recruitment requests
     * @param processId
     *            workflow process id
     * @return constructed recruitment transactions filled with the appropriate details
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<RecruitmentTransactionData> constructRecTransactions(List<WFRecruitmentData> recRequests, long processId, String attachments) throws BusinessException {
	List<RecruitmentTransactionData> recTransactions = new ArrayList<RecruitmentTransactionData>();
	if (recRequests != null && recRequests.size() > 0) {

	    String directedToJobName = null;
	    if (processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode()) {
		if (processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() && recRequests.get(0).getRecruitmentRegionId().longValue() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() && recRequests.size() > 1) {
		    directedToJobName = getMessage("label_directorateRecruitmentDirectedToJobName");
		} else {
		    JobData jobData = JobsService.getJobById(recRequests.get(0).getJobId());
		    if (jobData == null)
			throw new BusinessException("error_transactionDataError");

		    UnitData directedToUnit = UnitsService.getAncestorUnderPresidencyByLevel(jobData.getUnitId(), UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode());
		    if (directedToUnit == null || directedToUnit.getOfficialManagerId() == null)
			throw new BusinessException("error_canNotDetermineDirectedToEmployee");

		    directedToJobName = EmployeesService.getEmployeeData(directedToUnit.getOfficialManagerId()).getJobDesc();
		}
	    }

	    Long recruitmentClass = null;
	    if (processId == WFProcessesEnum.OFFICERS_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.OFFICERS_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.INSPECTORS_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.INSPECTORS_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.PERSONS_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.PERSONS_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.USERS_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.USERS_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.WAGES_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.WAGES_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.CONTRACTORS_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.CONTRACTORS_RECRUITMENT.getCode();
	    } else if (processId == WFProcessesEnum.MEDICAL_STAFF_RECRUITMENT.getCode()) {
		recruitmentClass = RecruitmentClassesEnum.MEDICAL_STAFF_RECRUITMENT.getCode();
	    } else {
		throw new BusinessException("error_transactionDataError");
	    }

	    for (WFRecruitmentData recRequest : recRequests) {
		RecruitmentTransactionData recTransaction = new RecruitmentTransactionData();
		recTransaction.setRecruitmentClass(recruitmentClass);
		JobData job = JobsService.getJobById(recRequest.getJobId());
		recTransaction.setBasedOnOrderNumber(recRequest.getBasedOnOrderNumber());
		recTransaction.setBasedOnOrderDate(recRequest.getBasedOnOrderDate());
		recTransaction.setBasedOn(recRequest.getBasedOn());
		recTransaction.setAttachments(attachments);
		recTransaction.setCategoryId(recRequest.getCategoryId());
		recTransaction.setEmployeeId(recRequest.getEmployeeId());
		recTransaction.setJobId(recRequest.getJobId());
		recTransaction.setTransJobCode(job.getCode());
		recTransaction.setTransJobName(job.getName());
		recTransaction.setTransUnitFullName(job.getUnitFullName());
		recTransaction.setRankId(recRequest.getRankId());
		recTransaction.setRankTitleId(recRequest.getRankTitleId());
		recTransaction.setDegreeId(recRequest.getDegreeId());
		recTransaction.setFirstRecruitmentDate(recRequest.getFirstRecruitmentDate());
		recTransaction.setRecruitmentDate(recRequest.getRecruitmentDate());
		recTransaction.setSeniorityDays(recRequest.getSeniorityDays());
		recTransaction.setSeniorityMonths(recRequest.getSeniorityMonths());
		recTransaction.setTransMinorSpecializationDesc(job.getMinorSpecializationDescription());
		recTransaction.setDecisionApprovedId(recRequest.getDecisionApprovedId());
		recTransaction.setDirectedToJobName(directedToJobName);

		EmployeeData directManagerOfDecisionApprovedEmployee = EmployeesService.getEmployeeDirectManager(recRequest.getDecisionApprovedId());
		if (directManagerOfDecisionApprovedEmployee == null)
		    throw new BusinessException("error_employeeDataError");
		recTransaction.setOriginalDecisionApprovedId(directManagerOfDecisionApprovedEmployee.getEmpId());

		recTransaction.setEtrFlag(FlagsEnum.ON.getCode());
		recTransaction.setMigrationFlag(FlagsEnum.OFF.getCode());
		recTransaction.setRecruitmentRegionId(recRequest.getRecruitmentRegionId());
		recTransaction.setGraduationGroupNumber(recRequest.getGraduationGroupNumber());
		recTransaction.setGraduationGroupPlaceId(recRequest.getGraduationGroupPlace());
		recTransaction.setInternalCopies(recRequest.getInternalCopies());
		recTransaction.setExternalCopies(recRequest.getExternalCopies());

		recTransaction.setReferring(recRequest.getReferring());

		if (processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode())
		    recTransaction.setRecruitmentType(FlagsEnum.OFF.getCode());
		else
		    recTransaction.setRecruitmentType(FlagsEnum.ON.getCode());

		if (processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode())
		    recTransaction.setJoiningDate(recRequest.getRecruitmentDate());
		// add qualification level reward for soldiers recruitments
		if (processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode()
			|| processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode()
			|| processId == WFProcessesEnum.INSPECTORS_RECRUITMENT.getCode() || processId == WFProcessesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode()) {
		    if (recRequest.getQualificationLevelReward().intValue() == QualificationLevelRewardsEnum.PRIMARY_SCHOOL.getCode()) {
			recTransaction.setQualificationLevelReward(getMessage("label_primarySchoolQualificationClause"));
		    } else if (recRequest.getQualificationLevelReward().intValue() == QualificationLevelRewardsEnum.INTERMEDIATE_EDUCATION.getCode()) {
			recTransaction.setQualificationLevelReward(getMessage("label_intermediateEducationQualificationClause"));
		    } else if (recRequest.getQualificationLevelReward().intValue() == QualificationLevelRewardsEnum.SECONDARY_SCHOOL.getCode()) {
			recTransaction.setQualificationLevelReward(getMessage("label_secondarySchoolQualificationClause"));
		    }
		}

		// adding last promotion date to recruitment transaction
		recTransaction.setLastPromotionDate(recRequest.getLastPromotionDate());

		if (processId != WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() && processId != WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode() && recRequest.getFirstRecruitmentDate() == null)
		    recTransaction.setFirstRecruitmentDate(recRequest.getRecruitmentDate());

		recTransactions.add(recTransaction);
	    }
	}
	return recTransactions;
    }

    /*
     * ***************************************** Operations *****************************************
     */
    /**
     * construct recruitment request for officer ; wrapper for {@link #constructWFRecruitment(EmployeeData, long, String, Date, String, Long, Long, Long, Date, JobData)}
     * 
     * @param empId
     *            employee id for which recruitment request to constructed
     * @param basedOnOrderNumber
     *            royal order number of recruitment
     * @param basedOnOrderDate
     *            royal order date of recruitment
     * @return constructed recruitment request filled with the appropriate details
     * @throws BusinessException
     *             if any error occurs
     */
    public static WFRecruitmentData constructOfficersWFRecruitment(long processId, Long empId, String basedOnOrderNumber, Date basedOnOrderDate) throws BusinessException {
	EmployeeData employee = EmployeesService.getEmployeeData(empId);
	if (employee == null)
	    throw new BusinessException("error_employeeDataError");
	return constructWFRecruitment(processId, employee, basedOnOrderNumber, basedOnOrderDate, null, null, null, null, employee.getInsertionDate(), null, null);
    }

    /**
     * construct recruitment request for soldier ; wrapper for {@link #constructWFRecruitment(EmployeeData, long, String, Date, String, Long, Long, Long, Date, JobData)}
     * 
     * @param empId
     *            employee id for which recruitment request to constructed
     * @param basedOnOrderNumber
     *            royal order number of recruitment
     * @param basedOnOrderDate
     *            royal order date of recruitment
     * @param regionId
     *            recruitment region of the soldier
     * @param rankId
     *            recruitment rank of the soldier
     * @param degreeId
     *            recruitment degree of the soldier
     * @param recruitmentDate
     *            recruitment date of the soldier
     * @param jobData
     *            the job which the soldier will occupy
     * @return constructed recruitment request filled with the appropriate details
     * @throws BusinessException
     *             if any error occurs
     */
    public static WFRecruitmentData constructSoldiersWFRecruitment(long processId, Long empId, String basedOnOrderNumber, Date basedOnOrderDate, Long regionId, Long rankId, Long degreeId, Date recruitmentDate, Integer QualificationLevelReward, JobData jobData) throws BusinessException {
	EmployeeData employee = EmployeesService.getEmployeeData(empId);
	if (employee == null)
	    throw new BusinessException("error_employeeDataError");
	return constructWFRecruitment(processId, employee, basedOnOrderNumber, basedOnOrderDate, null, regionId, rankId, degreeId, recruitmentDate, QualificationLevelReward, jobData);
    }

    public static WFRecruitmentData constructExceptionalSoldiersWFRecruitment(long processId, EmployeeData employee, String basedOnOrderNumber, Date basedOnOrderDate, Date recruitmentDate) throws BusinessException {
	return constructWFRecruitment(processId, employee, basedOnOrderNumber, basedOnOrderDate, null, null, employee.getRecruitmentRankId(), null, recruitmentDate, null, null);
    }

    /**
     * construct recruitment request for a civilian employee ; wrapper for {@link #constructWFRecruitment(EmployeeData, long, String, Date, String, Long, Long, Long, Date, JobData)}
     * 
     * @param empId
     *            employee id for which recruitment request to constructed
     * @param categoryId
     *            category of the civilian employee
     * @param basedOnOrderNumber
     *            royal order number of recruitment
     * @param basedOnOrderDate
     *            royal order date of recruitment
     * @param basedOn
     * @return constructed recruitment request filled with the appropriate details
     * @throws BusinessException
     *             if any error occurs
     */
    public static WFRecruitmentData constructCiviliansWFRecruitment(long processId, Long empId, String basedOnOrderNumber, Date basedOnOrderDate, String basedOn) throws BusinessException {
	EmployeeData employee = EmployeesService.getEmployeeData(empId);
	if (employee == null)
	    throw new BusinessException("error_employeeDataError");
	return constructWFRecruitment(processId, employee, basedOnOrderNumber, basedOnOrderDate, basedOn, null, null, null, null, null, null);
    }

    /**
     * delete recruitment workflow request from the database
     * 
     * @param recRequest
     *            request to be deleted
     * @param useSession
     *            optional database session
     * @throws BusinessException
     *             if any error occurs
     */
    public static void deleteWFRecruitment(WFRecruitmentData recRequest, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(recRequest.getWfRecruitment(), session);
	    deleteWFInstanceBeneficiaries(recRequest.getInstanceId(), Arrays.asList(recRequest.getEmployeeId()), session);

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

    /**
     * construct recruitment request based on employee recruitment details
     * 
     * @param empData
     *            employee will be recruited
     * @param categoryId
     *            category of employee
     * @param basedOnOrderNumber
     *            royal order number of recruitment
     * @param basedOnOrderDate
     *            royal order date of recruitment
     * @param basedOn
     * @param regionId
     *            recruitment region of employee
     * @param rankId
     *            recruitment rank of employee
     * @param degreeId
     *            recruitment degree of employee
     * @param recruitmentDate
     *            recuritment date of employee
     * @param jobData
     *            job that employee will occupy
     * @return constructed recruitment request filled with the appropriate details
     */
    private static WFRecruitmentData constructWFRecruitment(long processId, EmployeeData empData, String basedOnOrderNumber, Date basedOnOrderDate, String basedOn, Long regionId, Long rankId, Long degreeId, Date recruitmentDate, Integer QualificationLevelReward, JobData jobData) {
	WFRecruitmentData recRequest = new WFRecruitmentData();
	recRequest.setBasedOnOrderNumber(basedOnOrderNumber);
	recRequest.setBasedOnOrderDate(basedOnOrderDate);
	recRequest.setBasedOn(basedOn);
	recRequest.setCategoryId(empData.getCategoryId());

	if (empData.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
	    recRequest.setRecruitmentRegionId(empData.getRecruitmentRegionId());
	    recRequest.setRecruitmentRegionDesc(empData.getRecruitmentRegionDesc());
	} else {
	    recRequest.setRecruitmentRegionId(regionId);
	}

	recRequest.setEmployeeId(empData.getEmpId());
	recRequest.setEmployeeName(empData.getName());
	recRequest.setGraduationGroupNumber(empData.getGraduationGroupNumber());
	recRequest.setGraduationGroupPlace(empData.getGraduationGroupPlace());
	recRequest.setEmpRecruitmentRankId(empData.getRecruitmentRankId());
	recRequest.setEmpRecruitmentMinorSpecId(empData.getRecruitmentMinorSpecId());
	recRequest.setRankId(rankId);
	recRequest.setMilitaryNumber(empData.getMilitaryNumber());
	recRequest.setQualificationLevelReward(QualificationLevelReward);

	if (empData.getCategoryId() != CategoriesEnum.OFFICERS.getCode()) {
	    if (empData.getStatusId().equals(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode()))
		recRequest.setDegreeId(empData.getDegreeId());
	    else
		recRequest.setDegreeId(degreeId);
	}

	if (processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode())
	    recRequest.setRecruitmentDate(empData.getRecTrainingJoiningDate());
	else if (processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode()) {
	    recRequest.setRecruitmentDate(basedOnOrderDate);
	} else {
	    recRequest.setRecruitmentDate(recruitmentDate);
	}

	if (processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode() || processId == WFProcessesEnum.INSPECTORS_RECRUITMENT.getCode()
		|| processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode())
	    recRequest.setRecruitmentDocsFlagBoolean(true);

	if (jobData != null) {
	    recRequest.setJobId(jobData.getId());
	    recRequest.setJobCode(jobData.getCode());
	    recRequest.setJobName(jobData.getName());
	    recRequest.setUnitFullName(jobData.getUnitFullName());
	    recRequest.setMinorSpecId(jobData.getMinorSpecializationId());
	    recRequest.setMinorSpecDescription(jobData.getMinorSpecializationDescription());
	}
	return recRequest;
    }

    /**
     * inserts or update recruitment requests into the database
     * 
     * @param recRequests
     *            recuruitment requests to be inserted / updated
     * @param instanceId
     *            workflow instance
     * @param internalCopies
     *            Internal employees which receive notification upon termination of workflow instance
     * @param externalCopies
     *            External employees which receive hard copy of decision
     * @param useSession
     *            optional database session
     * @throws BusinessException
     *             if any error occurs
     */
    private static void saveWFRecruitments(List<WFRecruitmentData> recRequests, long instanceId, String internalCopies, String externalCopies, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (WFRecruitmentData recRequest : recRequests) {
		recRequest.setInternalCopies(internalCopies);
		recRequest.setExternalCopies(externalCopies);
		if (recRequest.getSeniorityDays() == null)
		    recRequest.setSeniorityDays(0);
		if (recRequest.getSeniorityMonths() == null)
		    recRequest.setSeniorityMonths(0);

		if (recRequest.getInstanceId() == null) {
		    recRequest.setInstanceId(instanceId);
		    DataAccess.addEntity(recRequest.getWfRecruitment(), session);
		} else
		    DataAccess.updateEntity(recRequest.getWfRecruitment(), session);
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

    /**
     * adds predefined external copies to the external copies inserted in the UI
     * 
     */
    public static String getSoldiersRecruitmentExternalCopies() {
	String externalCopies = getMessage("label_soldiersRecruitmentCopies");
	return externalCopies;
    }

    /**
     * gets the manager of man power unit given the region id
     * 
     * @param regionId
     *            the region id of the man power unit which we want to get its manager
     * @throws BusinessException
     */
    public static EmployeeData getMilitaryAffairsUnitManager(long regionId) throws BusinessException {
	WFPosition position = getWFPosition(WFPositionsEnum.REGION_OFFICERS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);
	if (position == null) {
	    throw new BusinessException("error_positionNotFound");
	}

	EmployeeData emp = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
	if (emp == null) {
	    throw new BusinessException("error_employeeDataError");
	}

	EmployeeData militaryAffairsUnitManager = EmployeesService.getEmployeeDirectManager(emp.getEmpId());
	if (militaryAffairsUnitManager == null) {
	    throw new BusinessException("error_employeeDataError");
	}

	return militaryAffairsUnitManager;
    }

    private static List<Long> getRecruitmentsInstanceBeneficiariesIds(List<WFRecruitmentData> recRequests) throws BusinessException {
	List<Long> instanceBeneficiariesIds = new ArrayList<Long>();
	for (WFRecruitmentData recRequest : recRequests) {
	    instanceBeneficiariesIds.add(recRequest.getEmployeeId());
	}
	return instanceBeneficiariesIds;
    }

    /*
     * ******************************************* Queries ******************************************
     */
    /**
     * wrapper for {@link #searchWFRecruitmentsByInstanceId(Long[])}
     * 
     * @param instanceId
     *            recuritment workflow instance
     * @return recruitment requests in the recruitment workflow instance
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<WFRecruitmentData> getWFRecruitmentsByInstanceId(Long instanceId) throws BusinessException {
	return searchWFRecruitmentsByInstanceId(new Long[] { instanceId }, (long) FlagsEnum.ALL.getCode());
    }

    /**
     * wrapper for {@link #searchWFRecruitmentsByInstanceId(Long[])}
     * 
     * @param instanceId
     *            recuritment workflow instance
     * @param recruitmentRegionId
     *            recruitment region id
     * @return recruitment requests in the recruitment workflow instance
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<WFRecruitmentData> getWFRecruitmentsByInstanceIdAndRecRegionId(Long instanceId, Long recruitmentRegionId) throws BusinessException {
	return searchWFRecruitmentsByInstanceId(new Long[] { instanceId }, recruitmentRegionId);
    }

    /**
     * wrapper for {@link #searchWFRecruitmentsByInstanceId(Long[])}
     * 
     * @param instanceIds
     *            array of recruitment workflow instances
     * @return recruitment requests in the recruitment workflow instances
     * @throws BusinessException
     */
    public static Map<Long, List<WFRecruitmentData>> getWFRecruitmentsByInstanceIds(Long[] instanceIds) throws BusinessException {
	List<WFRecruitmentData> wfRecruitmentsData = searchWFRecruitmentsByInstanceId(instanceIds, (long) FlagsEnum.ALL.getCode());
	Map<Long, List<WFRecruitmentData>> wfRecruitments = new HashMap<Long, List<WFRecruitmentData>>();

	for (WFRecruitmentData wfRecruitmentData : wfRecruitmentsData) {
	    List<WFRecruitmentData> wfRecruitmentDataList = wfRecruitments.get(wfRecruitmentData.getInstanceId());
	    if (wfRecruitmentDataList == null) {
		wfRecruitmentDataList = new ArrayList<WFRecruitmentData>();
		wfRecruitmentDataList.add(wfRecruitmentData);
		wfRecruitments.put(wfRecruitmentData.getInstanceId(), wfRecruitmentDataList);
	    } else {
		wfRecruitmentDataList.add(wfRecruitmentData);
	    }
	}
	return wfRecruitments;
    }

    /**
     * search for recruitment requests by their instances
     * 
     * @param instanceIds
     *            recruitment workflow instances
     * 
     * @return recruitment requests in the recruitment workflow instances
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<WFRecruitmentData> searchWFRecruitmentsByInstanceId(Long[] instanceIds, Long recRegionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceIds);
	    qParams.put("P_REC_REGION_ID", recRegionId == null ? (long) FlagsEnum.ALL.getCode() : recRegionId);
	    return DataAccess.executeNamedQuery(WFRecruitmentData.class, QueryNamesEnum.WF_GET_WFRECRUITMENT_BY_INSTANCE_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * searches for recruitment workflow tasks by assignee employee and his role
     * 
     * @param assigneeId
     *            employee assigneed to the task
     * @param assigneeWfRole
     *            role of the assignee employee in the workflow
     * @return list of objects ; each object contains the task and it related objects ( instance , process , assignee and delegate employees )
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<Object> getWFRecruitmentsTasks(Long assigneeId, String assigneeWfRole) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLE", assigneeWfRole);
	    qParams.put("P_PROCESS_GROUP_ID", WFProcessesGroupsEnum.RECRUITMENTS.getCode());
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_WFRECRUITMENT_TASKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*
     * ****************************************** Validations ***************************************
     */
    /**
     * main validation method for recruitment requests
     * 
     * @param recRequests
     *            to be validate recruitment requests
     * @param instanceId
     *            workflow instance
     * @param processId
     *            workflow process
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateRecruitmentRequests(List<WFRecruitmentData> recRequests, Long instanceId, long processId, String attachments) throws BusinessException {
	validateMandatoryFields(recRequests, processId, instanceId == null ? true : false, attachments);

	validateHijriDates(recRequests);

	int graduationGroupNumber = 0;
	int graduationGroupPlace = 0;

	if (recRequests.size() > 0 && recRequests.get(0).getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
	    graduationGroupNumber = recRequests.get(0).getGraduationGroupNumber();
	    graduationGroupPlace = recRequests.get(0).getGraduationGroupPlace();
	}

	HashSet<Long> employeesIDs = new HashSet<Long>();
	HashSet<Long> jobsIDs = new HashSet<Long>();

	for (int i = 0; i < recRequests.size(); i++) {
	    if (!employeesIDs.add(recRequests.get(i).getEmployeeId()))
		throw new BusinessException("error_recruitmentEmployeeRepeated");

	    if (!(processId == WFProcessesEnum.OFFICERS_RECRUITMENT.getCode() && instanceId == null && RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() != recRequests.get(0).getRecruitmentRegionId()) && !jobsIDs.add(recRequests.get(i).getJobId()))
		throw new BusinessException("error_recruitmentJobRepeated");

	    if (recRequests.get(i).getCategoryId() == CategoriesEnum.OFFICERS.getCode() && (graduationGroupNumber != recRequests.get(i).getGraduationGroupNumber() || graduationGroupPlace != recRequests.get(i).getGraduationGroupPlace())) {
		throw new BusinessException("error_officersRecruitmentDataNotSuitable");
	    }

	    if (recRequests.get(i).getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode() && recRequests.size() > 1 && recRequests.get(i).getRankId().longValue() < RanksEnum.TENTH.getCode()) {
		throw new BusinessException("error_employeesRecruitmentAboveTenthRankCannotBeCollective");
	    }

	    EmployeeData emp = EmployeesService.getEmployeeData(recRequests.get(i).getEmployeeId());
	    if (emp == null)
		throw new BusinessException("error_employeeDataError");

	    if (emp.getInsertionDate() != null && emp.getInsertionDate().after(recRequests.get(i).getRecruitmentDate() == null ? HijriDateService.getHijriSysDate() : recRequests.get(i).getRecruitmentDate()))
		throw new BusinessException("error_registerationDateAfterRecruitmentDate");

	    long empStatusId = emp.getStatusId().longValue();
	    if (emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		if ((processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode()) && empStatusId != EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode())
		    throw new BusinessException("error_recruitmentSoldiersOrFirstSoldiersStatusesIncorrect");
		if ((processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode()) && empStatusId != EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode())
		    throw new BusinessException("error_recruitmentCorporalOrHigherStatusesIncorrect");
		if ((processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode()) && empStatusId != EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode())
		    throw new BusinessException("error_recruitmentGraduationLetterStatusesIncorrect");
		if ((processId == WFProcessesEnum.INSPECTORS_RECRUITMENT.getCode() || processId == WFProcessesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode()) && (empStatusId != EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode() && empStatusId != EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode()))
		    throw new BusinessException("error_recruitmentInspectorsStatusesIncorrect");

		JobData job = JobsService.getJobById(recRequests.get(i).getJobId());
		if (job == null)
		    throw new BusinessException("error_transactionDataError");

		if (recRequests.get(i).getEmpRecruitmentRankId().longValue() != job.getRankId().longValue())
		    throw new BusinessException("error_jobsAreNotSuitableForSoldiers");
		if ((processId != WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() && processId != WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode()) && recRequests.get(i).getEmpRecruitmentMinorSpecId().longValue() != job.getMinorSpecializationId().longValue())
		    throw new BusinessException("error_jobsAreNotSuitableForSoldiers");
	    } else if (empStatusId != EmployeeStatusEnum.UNDER_RECRUITMENT.getCode())
		throw new BusinessException("error_recruitmentEmployeesStatusesIncorrect");

	    RecruitmentsService.validateBusinessRules(recRequests.get(i).getRecruitmentDate(), recRequests.get(i).getFirstRecruitmentDate(), recRequests.get(i).getLastPromotionDate(), recRequests.get(i).getBasedOnOrderDate(), recRequests.get(i).getJobId());
	}

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(employeesIDs.toArray(new Long[employeesIDs.size()]), jobsIDs.toArray(new Long[jobsIDs.size()]), TransactionClassesEnum.RECRUITMENT.getCode(), false, instanceId);

    }

    /**
     * validate mandatory fields in the recruitment requests
     * 
     * @param recRequests
     *            recruitment requests to be validated
     * @param processId
     *            workflow process
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateMandatoryFields(List<WFRecruitmentData> recRequests, long processId, boolean isRequester, String attachments) throws BusinessException {
	for (WFRecruitmentData recRequest : recRequests) {
	    if (recRequest.getCategoryId() == null)
		throw new BusinessException("error_categoryMandatory");
	    long categoryId = recRequest.getCategoryId();

	    if (recRequest.getEmployeeId() == null)
		throw new BusinessException("error_empSelectionManadatory");

	    if (recRequest.getJobId() == null && !(processId == WFProcessesEnum.OFFICERS_RECRUITMENT.getCode() && isRequester == true && RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() != recRequest.getRecruitmentRegionId()))
		throw new BusinessException("error_recruitmentJobIsMandatory");

	    if (recRequest.getRecruitmentDate() == null && (recRequest.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || (recRequest.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && (processId != WFProcessesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode() && processId != WFProcessesEnum.INSPECTORS_RECRUITMENT.getCode()))))
		throw new BusinessException("error_recDateMandatory");

	    if (recRequest.getLastPromotionDate() != null && recRequest.getFirstRecruitmentDate() == null)
		throw new BusinessException("error_firstRecDateMandatory");

	    if (recRequest.getRecruitmentDocsFlag() == null || recRequest.getRecruitmentDocsFlag().intValue() == FlagsEnum.OFF.getCode())
		throw new BusinessException("error_recDocsFlagMandatory");

	    if (categoryId != CategoriesEnum.OFFICERS.getCode() && categoryId != CategoriesEnum.SOLDIERS.getCode() && (recRequest.getBasedOn() == null || recRequest.getBasedOn().length() == 0))
		throw new BusinessException("error_basedOnMandatory");

	    if (recRequest.getBasedOnOrderNumber() == null || recRequest.getBasedOnOrderNumber().length() == 0)
		throw new BusinessException("error_basedOnOrderNumberMandatory");

	    if (recRequest.getBasedOnOrderDate() == null)
		throw new BusinessException("error_basedOnOrderDateMandatory");

	    if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
		if (recRequest.getQualificationLevelReward() == null && processId != WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() && processId != WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode()) {
		    throw new BusinessException("error_qualificationLevelRewardMandatory");
		}
	    }

	    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
		if (recRequest.getRankId() == null)
		    throw new BusinessException("error_rankIsMandatory");

		if (recRequest.getGraduationGroupNumber() == null)
		    throw new BusinessException("error_graduationGroupNumberMandatory");

		if (recRequest.getGraduationGroupPlace() == null)
		    throw new BusinessException("error_graduationGroupPlaceMandatory");
	    } else if (recRequest.getDegreeId() == null) {
		throw new BusinessException("error_degreeMandatory");
	    }

	    if ((categoryId == CategoriesEnum.OFFICERS.getCode() || (categoryId == CategoriesEnum.SOLDIERS.getCode() && (processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode() || processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() || processId == WFProcessesEnum.INSPECTORS_RECRUITMENT.getCode()))) && recRequest.getRecruitmentRegionId() == null)
		throw new BusinessException("error_recruitmentRegionMandatory");

	    if (attachments == null && (processId == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() ||
		    processId == RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode() ||
		    processId == RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() ||
		    processId == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode() ||
		    processId == RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode() ||
		    processId == RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode() ||
		    processId == RecruitmentClassesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode() ||
		    processId == RecruitmentClassesEnum.INSPECTORS_RECRUITMENT.getCode()))
		throw new BusinessException("error_attachmentsMissing");
	}
    }

    /**
     * count either the running recrutiment instance that contains certain employees or the running recrutiment instance that contains certain job
     * 
     * @param employeesIds
     *            employees in running recruitment requests
     * @param jobsIds
     *            jobs in running recruitment requests
     * @param excludedInstanceId
     *            workflow instance to be excluded in counting
     * @return count of the running request based on the method paramters
     * @throws BusinessException
     *             if any error occurs
     */
    public static long countRunningRequests(Long[] employeesIds, Long[] jobsIds, Long excludedInstanceId) throws BusinessException {
	return getRunningRequests(employeesIds, jobsIds, excludedInstanceId).size();
    }

    /**
     * list either the running recrutiment instance that contains certain employees or the running recrutiment instance that contains certain job
     * 
     * @param employeesIds
     *            employees in running recruitment requests
     * @param jobsIds
     *            jobs in running recruitment requests
     * @param excludedInstanceId
     *            workflow instance to be excluded in counting
     * @return count of the running request based on the method paramters
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<WFRecruitment> getRunningRequests(Long[] employeesIds, Long[] jobsIds, Long excludedInstanceId) throws BusinessException {
	try {

	    if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
		return new ArrayList<WFRecruitment>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMPS_IDS", (employeesIds != null && employeesIds.length > 0) ? employeesIds : new Long[] { (long) FlagsEnum.ALL.getCode() });
	    qParams.put("P_JOBS_IDS", (jobsIds != null && jobsIds.length > 0) ? jobsIds : new Long[] { (long) FlagsEnum.ALL.getCode() });
	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId == null ? (long) FlagsEnum.ALL.getCode() : excludedInstanceId);

	    return DataAccess.executeNamedQuery(WFRecruitment.class, QueryNamesEnum.WF_CHECK_EXISTING_RECRUITMENT_PROCESSES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * @return the list of employees ids who are in running recruitment process
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<Long> getEmployeesIdsInRunningRecruitmentProcesses() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_GET_EMPLOYEES_IDS_IN_RUNNING_RECRUITMENT_PRCOESSES.getCode(), new HashMap<String, Object>());
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * @return the list of jobs ids who are in running recruitment process
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<Long> getJobsIdsInRunningRecruitmentProcesses() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_GET_JOBS_IDS_IN_RUNNING_RECRUITMENT_PRCOESSES.getCode(), new HashMap<String, Object>());
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * checks if any of the dates in the recruitment requests is not validate hijri dates
     * 
     * @param recRequests
     *            recuritment requests to be validated
     * @throws BusinessException
     *             if any invlaid hijri dates founded
     */
    private static void validateHijriDates(List<WFRecruitmentData> recRequests) throws BusinessException {
	for (WFRecruitmentData recRequest : recRequests) {
	    if (!(recRequest.getRecruitmentDate() == null || HijriDateService.isValidHijriDate(recRequest.getRecruitmentDate()) && (recRequest.getFirstRecruitmentDate() == null || HijriDateService.isValidHijriDate(recRequest.getFirstRecruitmentDate())) && HijriDateService.isValidHijriDate(recRequest.getBasedOnOrderDate()) && (recRequest.getLastPromotionDate() == null || HijriDateService.isValidHijriDate(recRequest.getLastPromotionDate()))))
		throw new BusinessException("error_invalidHijriDate");
	}
    }

    /*
     * ************************************** Distributions *****************************************
     */
    /**
     * construct recuruitment requests for ON_DUTY_UNDER_RECRUITMENT and NEW_STUDENT_RANKED_SOLDIER soldiers who have the passed distribution details
     * 
     * @param processId
     *            workflow process id
     * @param regionId
     *            distribution region of the soldiers
     * @param trainingUnitId
     *            training unit of the soldiers
     * @param trainingUnitHKey
     *            hierarchy key of training unit of the soldiers
     * @param degreeId
     *            degree of soldiers
     * @param rankId
     *            recruitment rank of soldiers
     * @param recruitmentDate
     *            recruitment date of soldiers
     * @param jobCodeStart
     *            start code for jobs which the soldiers will be recruited in it.
     * @param basedOnOrderNumber
     *            royal order number of recruitment
     * @param basedOnOrderDate
     *            royal order date of recruitment
     * @return constructed recruitment requests filled with the appropriate details
     * @throws BusinessException
     *             if any error occurs
     * @see EmployeeStatusEnum
     */
    public static List<WFRecruitmentData> getDistributedSoldiers(long processId, Long regionId, Long trainingUnitId, String trainingUnitHKey, Long degreeId, Long rankId, Date recruitmentDate, Integer qualificationLevelReward, String basedOnOrderNumber, Date basedOnOrderDate) throws BusinessException {
	List<WFRecruitmentData> resultRecRequests = new ArrayList<WFRecruitmentData>();

	Long statusId;
	if (processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode())
	    statusId = EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode();
	else if (processId == WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode())
	    statusId = EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode();
	else if (processId == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode())
	    statusId = EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode();
	else
	    throw new BusinessException("error_transactionDataError");

	List<EmployeeData> matchingSoldiers = EmployeesService.getSoldiersByRecruitmentInfo(new Long[] { statusId }, rankId, regionId == null ? (long) FlagsEnum.ALL.getCode() : regionId, trainingUnitId == null ? (long) FlagsEnum.ALL.getCode() : trainingUnitId, "M", FlagsEnum.OFF.getCode());

	if (matchingSoldiers.size() > 0) {
	    List<Long> employeesIdsInRunningRequests = getEmployeesIdsInRunningRecruitmentProcesses();
	    if (employeesIdsInRunningRequests.size() > 0) {
		Map<Long, EmployeeData> matchingSoldiersMap = new HashMap<Long, EmployeeData>();
		for (EmployeeData emp : matchingSoldiers)
		    matchingSoldiersMap.put(emp.getEmpId(), emp);

		for (Long id : employeesIdsInRunningRequests)
		    if (matchingSoldiersMap.containsKey(id))
			matchingSoldiers.remove(matchingSoldiersMap.get(id));
	    }

	    Set<Long> minorSpecsIds = null;
	    if (processId != WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode()) {
		minorSpecsIds = new HashSet<Long>();
		for (EmployeeData emp : matchingSoldiers)
		    minorSpecsIds.add(emp.getRecruitmentMinorSpecId());
	    }

	    List<JobData> matchingJobs = JobsService.getJobsForRecruitment(CategoriesEnum.SOLDIERS.getCode(), rankId, minorSpecsIds == null ? null : minorSpecsIds.toArray(new Long[minorSpecsIds.size()]), (trainingUnitHKey == null || trainingUnitHKey.trim().isEmpty()) ? null : trainingUnitHKey, regionId == null ? FlagsEnum.ALL.getCode() : regionId);

	    List<Long> JobsIdsInRunningRequests = getJobsIdsInRunningRecruitmentProcesses();
	    if (JobsIdsInRunningRequests.size() > 0) {
		Map<Long, JobData> matchingJobsMap = new HashMap<Long, JobData>();
		for (JobData job : matchingJobs)
		    matchingJobsMap.put(job.getId(), job);

		for (Long id : JobsIdsInRunningRequests)
		    if (matchingJobsMap.containsKey(id))
			matchingJobs.remove(matchingJobsMap.get(id));
	    }

	    Map<Long, List<JobData>> jobsMap;
	    if (processId != WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode()) {
		jobsMap = new HashMap<Long, List<JobData>>();
		for (JobData job : matchingJobs) {
		    if (jobsMap.containsKey(job.getMinorSpecializationId()))
			jobsMap.get(job.getMinorSpecializationId()).add(job);
		    else {
			List<JobData> jobsList = new ArrayList<JobData>();
			jobsList.add(job);
			jobsMap.put(job.getMinorSpecializationId(), jobsList);
		    }
		}

		for (int i = 0; i < matchingSoldiers.size(); i++) {
		    EmployeeData emp = matchingSoldiers.get(i);
		    List<JobData> jobsList = jobsMap.get(emp.getRecruitmentMinorSpecId());
		    if (jobsList == null || jobsList.size() == 0) {
			resultRecRequests.add(constructSoldiersWFRecruitment(processId, emp.getEmpId(), basedOnOrderNumber, basedOnOrderDate, regionId, rankId, degreeId, recruitmentDate, qualificationLevelReward, null));
		    } else {
			JobData jobData = jobsList.remove(0);
			resultRecRequests.add(constructSoldiersWFRecruitment(processId, emp.getEmpId(), basedOnOrderNumber, basedOnOrderDate, regionId, rankId, degreeId, recruitmentDate, qualificationLevelReward, jobData));
		    }
		}
	    } else {
		for (int i = 0; i < matchingSoldiers.size(); i++) {
		    EmployeeData emp = matchingSoldiers.get(i);
		    if (i >= matchingJobs.size()) {
			resultRecRequests.add(constructSoldiersWFRecruitment(processId, emp.getEmpId(), basedOnOrderNumber, basedOnOrderDate, regionId, rankId, degreeId, recruitmentDate, qualificationLevelReward, null));
		    } else {
			resultRecRequests.add(constructSoldiersWFRecruitment(processId, emp.getEmpId(), basedOnOrderNumber, basedOnOrderDate, regionId, rankId, degreeId, recruitmentDate, qualificationLevelReward, matchingJobs.get(i)));
		    }
		}
	    }
	}

	return resultRecRequests;
    }

    /**
     * construct recruitment workflow requests for employees based on their graduation details and recruitment region
     * 
     * @param recruitmentRegionId
     *            the region officers will be recruited in it
     * @param graduationGroupNumber
     *            graduation group number of officers
     * @param graduationGroupPlace
     *            graduation group place of officers
     * @param basedOnOrderNumber
     *            royal order number of recuriutment
     * @param basedOnOrderDate
     *            royal order date of recruitment
     * @return constructed recruiment requests filled with appropriate details
     * @throws BusinessException
     *             if any error occurss
     * @see RegionsEnum , GraduationGroupPlaceEnum
     */
    public static List<WFRecruitmentData> getDistributedOfficers(long processId, long recruitmentRegionId, int graduationGroupNumber, int graduationGroupPlace, String basedOnOrderNumber, Date basedOnOrderDate) throws BusinessException {
	List<WFRecruitmentData> resultRecRequests = new ArrayList<WFRecruitmentData>();
	List<EmployeeData> employees = EmployeesService.getOfficersByRecruitmentInfo(recruitmentRegionId, graduationGroupNumber, graduationGroupPlace);

	if (employees.size() > 0) {
	    List<Long> employeesIdsInRunningRequests = getEmployeesIdsInRunningRecruitmentProcesses();
	    if (employeesIdsInRunningRequests.size() > 0) {
		Map<Long, EmployeeData> matchingOfficersMap = new HashMap<Long, EmployeeData>();
		for (EmployeeData emp : employees)
		    matchingOfficersMap.put(emp.getEmpId(), emp);

		for (Long id : employeesIdsInRunningRequests)
		    if (matchingOfficersMap.containsKey(id))
			employees.remove(matchingOfficersMap.get(id));
	    }

	    for (EmployeeData empData : employees) {
		resultRecRequests.add(constructOfficersWFRecruitment(processId, empData.getEmpId(), basedOnOrderNumber, basedOnOrderDate));
	    }
	}

	return resultRecRequests;
    }

}