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
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.hcm.trainings.GraduationPlaceData;
import com.code.dal.orm.hcm.trainings.GraduationPlaceDetailData;
import com.code.dal.orm.hcm.trainings.QualificationMajorSpec;
import com.code.dal.orm.hcm.trainings.QualificationMinorSpecData;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingExternalPartyData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionDetailData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.CategoriesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.FundSourceEnum;
import com.code.enums.GradesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTransactionCategoryEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.UnitsAncestorsLevelsEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.hcm.TerminationsService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

/**
 * WorkFlow Service to control the flow of employee training processes.
 * 
 */
public class TrainingEmployeesWorkFlow extends BaseWorkFlow {

    private static final int MIN_EVENING_COURSE_NOMINATION_BETWEEN_REQUEST_AND_START_DAYS = 30;

    /**
     * Private constructor to prevent instantiation
     */
    private TrainingEmployeesWorkFlow() {
    }

    /***************************** WFTraining *****************************/
    /*---------------------------CollectiveApproval----------------------*/
    public static void doTrainingsCollectiveAction(Object trainingTaskObject, List<Object> tasksAndTrainingsObjects, int actionTypeFlag) throws BusinessException {
	// object[0] - WFTrainingData
	// object[1] - WFTask
	// object[2] - WFInstance
	// object[3] - processName
	// object[4] - requester
	// object[5] - delegatingName
	WFTrainingData trainingRequest = (WFTrainingData) (((Object[]) trainingTaskObject)[0]);
	WFTask task = (WFTask) (((Object[]) trainingTaskObject)[1]);
	WFInstance instance = (WFInstance) (((Object[]) trainingTaskObject)[2]);
	EmployeeData requester = (EmployeeData) (((Object[]) trainingTaskObject)[4]);
	List<WFTrainingData> trainingList = new ArrayList<WFTrainingData>();
	for (int i = 0; i < tasksAndTrainingsObjects.size(); i++) {
	    WFTrainingData currentWFTrainingData = (WFTrainingData) (((Object[]) tasksAndTrainingsObjects.get(i))[0]);
	    if (task.getInstanceId().longValue() == currentWFTrainingData.getInstanceId().longValue()) {
		trainingList.add(currentWFTrainingData);
	    }
	}

	if (actionTypeFlag == 1) {
	    if (isClaimRequest(instance)) // Claims
		TrainingEmployeesWorkFlow.doWFTrainingDM(requester, instance, task, true);
	    else // Nominations
		TrainingEmployeesWorkFlow.doNominationDM(requester, trainingList, trainingRequest.getCourseEventId() == null ? null : TrainingCoursesEventsService.getCourseEventById(trainingRequest.getCourseEventId()), instance, task, true);
	} else if (actionTypeFlag == 2) {
	    if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.SIGN_MANAGER.getCode())) {
		if (isClaimRequest(instance)) // Claims
		    TrainingEmployeesWorkFlow.doWFTrainingSM(requester, instance, trainingList, task, WFActionFlagsEnum.APPROVE.getCode());
		else // Nominations
		    TrainingEmployeesWorkFlow.doNominationSM(requester, instance, trainingList, trainingRequest.getCourseEventId() == null ? null : TrainingCoursesEventsService.getCourseEventById(trainingRequest.getCourseEventId()), task, WFActionFlagsEnum.APPROVE.getCode());

	    } else if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode())) {
		TrainingEmployeesWorkFlow.doNominationSSM(requester, instance, trainingList, task, WFActionFlagsEnum.APPROVE.getCode());

	    } else if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.EXTRA_SECONDARY_SIGN_MANAGER.getCode())) {
		TrainingEmployeesWorkFlow.doNominationESSM(requester, instance, trainingList, trainingRequest.getCourseEventId() == null ? null : TrainingCoursesEventsService.getCourseEventById(trainingRequest.getCourseEventId()), task, WFActionFlagsEnum.APPROVE.getCode());
	    }
	}
    }

    /*---------------------------Work Flow Steps Claims----------------------*/
    public static void initWFTraining(EmployeeData requester, List<WFTrainingData> wfTrainingList, long processId, String taskUrl) throws BusinessException {
	validateWFTrainings(wfTrainingList, null, processId);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), null, getTrainingEmployeesInstanceBeneficiariesIds(wfTrainingList), session);

	    if (isRequestProcess(processId)) {
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
	    } else {
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
	    }

	    for (WFTrainingData wfTrainingData : wfTrainingList) {
		wfTrainingData.setInstanceId(instance.getInstanceId());
		DataAccess.addEntity(wfTrainingData.getWfTraining(), session);
	    }

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    for (WFTrainingData wfTrainingData : wfTrainingList)
		wfTrainingData.setInstanceId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doWFTrainingDM(EmployeeData requester, WFInstance instance, WFTask dmTask, boolean isApproved) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    String role;
	    long assigneeId = requester.getEmpId();
	    long originalId = requester.getEmpId();

	    if (isApproved) {

		EmployeeData nextDM = EmployeesService.getEmployeeDirectManager(dmTask.getOriginalId());

		if (nextDM.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode() || nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {

		    EmployeeData managerRedirectEmployee = getTrainingManager(instance.getProcessId(), requester.getPhysicalRegionId());
		    originalId = managerRedirectEmployee.getEmpId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
		} else {
		    originalId = nextDM.getEmpId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    role = WFTaskRolesEnum.DIRECT_MANAGER.getCode();
		}

		completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, originalId, dmTask.getTaskUrl(), role, dmTask.getLevel(), session);

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

    public static void doWFTrainingMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), mrTask.getLevel());
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void doWFTrainingRE(EmployeeData requester, WFInstance instance, List<WFTrainingData> wfTrainingList, WFTask reTask, boolean isApproved) throws BusinessException {
	if (isApproved && !isRequestProcess(instance.getProcessId()) && requester.getEmpId().equals(reTask.getOriginalId()))
	    validateWFTrainings(wfTrainingList, null, instance.getProcessId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {

		if (!isRequestProcess(instance.getProcessId()) && requester.getEmpId().equals(reTask.getOriginalId())) {
		    List<Long> beneficiariesIds = new ArrayList<Long>();
		    for (WFTrainingData wfTrainingData : wfTrainingList) {
			DataAccess.updateEntity(wfTrainingData.getWfTraining(), session);
			beneficiariesIds.add(wfTrainingData.getEmployeeId());
		    }

		    updateWFInstanceBeneficiaries(instance.getInstanceId(), beneficiariesIds, session);
		}

		EmployeeData trainingManager = EmployeesService.getEmployeeDirectManager(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(trainingManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), trainingManager.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), reTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    public static void doWFTrainingSM(EmployeeData requester, WFInstance instance, List<WFTrainingData> wfTrainingList, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(smTask.getOriginalId());

		if (nextManager.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode() || nextManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
		    closeTrainingEmployeeWorkFlow(requester, instance, wfTrainingList, null, smTask, session);
		} else { // Send to next manager

		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		List<WFTask> reviewerTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode());
		long originalId = reviewerTasks.isEmpty() ? requester.getEmpId() : reviewerTasks.get(0).getOriginalId();
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), requester.getEmpId()), originalId, smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), smTask.getLevel(), session);

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

    /*---------------------------Work Flow Steps Nomination----------------------*/
    public static void initNomination(EmployeeData requester, List<WFTrainingData> wfTrainingList, TrainingCourseEventData trainingCourseEvent, long processId, String taskUrl, List<EmployeeData> internalCopiesEmployees) throws BusinessException {
	validateWFTrainings(wfTrainingList, trainingCourseEvent, processId);
	initialValidationWFTrainings(wfTrainingList, processId);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), null, getTrainingEmployeesInstanceBeneficiariesIds(wfTrainingList), session);
	    if (processId == WFProcessesEnum.SCHOLARSHIP.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CANCEL.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_TERMINATION.getCode())
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
	    else
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);

	    for (WFTrainingData wfTrainingData : wfTrainingList) {
		wfTrainingData.setInstanceId(instance.getInstanceId());
		String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);
		wfTrainingData.setInternalCopies(internalCopies);
		DataAccess.addEntity(wfTrainingData.getWfTraining(), session);
	    }

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    for (WFTrainingData wfTrainingData : wfTrainingList)
		wfTrainingData.setInstanceId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doNominationDM(EmployeeData requester, List<WFTrainingData> wfTrainingList, TrainingCourseEventData courseEventData, WFInstance instance, WFTask dmTask, boolean isApproved) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    String role;
	    long assigneeId, originalId;

	    if (isApproved) {

		EmployeeData nextDM = EmployeesService.getEmployeeDirectManager(dmTask.getOriginalId());

		if (nextDM.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode() || nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {

		    long requestingRegionId = requester.getPhysicalRegionId().longValue();
		    EmployeeData trainingManager = null;
		    WFPosition position = null;

		    if (wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {

			if (requestingRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {

			    position = getWFPosition(WFPositionsEnum.INTERNAL_TRAINING_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

			    if (courseEventData.getTrainingUnitRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() || courseEventData.getTrainingUnitRegionId() == RegionsEnum.ACADEMY.getCode())
				role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			    else
				role = WFTaskRolesEnum.MANAGER_REDIRECT_TO_SPECIALIST.getCode();
			} else {

			    position = getWFPosition(WFPositionsEnum.REGION_TRAINING_UNIT_MANAGER.getCode(), requestingRegionId);
			    trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

			    if (requestingRegionId == courseEventData.getTrainingUnitRegionId())
				role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			    else
				role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
			}

		    } else if (wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() || wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.MORNING_COURSE.getCode() || wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {

			if (requestingRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {

			    position = getWFPosition(WFPositionsEnum.EXTERNAL_TRAINING_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
			    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			} else {
			    position = getWFPosition(WFPositionsEnum.REGION_TRAINING_UNIT_MANAGER.getCode(), requestingRegionId);
			    trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
			    role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
			}
		    } else {

			if (requestingRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
			    position = getWFPosition(WFPositionsEnum.TRAINING_PLANNING_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
			    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			} else {
			    position = getWFPosition(WFPositionsEnum.REGION_TRAINING_UNIT_MANAGER.getCode(), requestingRegionId);
			    trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
			    role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
			}
		    }

		    originalId = trainingManager.getEmpId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		} else {
		    originalId = nextDM.getEmpId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    role = WFTaskRolesEnum.DIRECT_MANAGER.getCode();
		}

		completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), new Date(), HijriDateService.getHijriSysDate(), instance.getInstanceId(), assigneeId, originalId, dmTask.getTaskUrl(), role, dmTask.getLevel(), session);

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

    public static void doNominationMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
	try {
	    completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), new Date(), HijriDateService.getHijriSysDate(), instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), mrTask.getLevel());
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void doNominationSMR(EmployeeData requester, WFInstance instance, WFTask smrTask, long reviewerEmpId) throws BusinessException {
	try {
	    completeWFTask(smrTask, WFTaskActionsEnum.REDIRECT.getCode(), new Date(), HijriDateService.getHijriSysDate(), instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, smrTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), smrTask.getLevel());
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void doNominationESMR(EmployeeData requester, WFInstance instance, WFTask esmrTask, long reviewerEmpId) throws BusinessException {
	try {
	    completeWFTask(esmrTask, WFTaskActionsEnum.REDIRECT.getCode(), new Date(), HijriDateService.getHijriSysDate(), instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, esmrTask.getTaskUrl(), WFTaskRolesEnum.EXTRA_SECONDARY_REVIEWER_EMP.getCode(), esmrTask.getLevel());
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void doNominationMRTS(EmployeeData requester, long trainingUniRegionId, WFInstance instance, WFTask mrtsTask) throws BusinessException {
	try {
	    WFPosition position = getWFPosition(WFPositionsEnum.REGION_TRAINING_UNIT_MANAGER.getCode(), trainingUniRegionId);
	    EmployeeData trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

	    completeWFTask(mrtsTask, WFTaskActionsEnum.REDIRECT.getCode(), new Date(), HijriDateService.getHijriSysDate(), instance.getInstanceId(), getDelegate(trainingManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), trainingManager.getEmpId(), mrtsTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode(), mrtsTask.getLevel());
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void doNominationRE(EmployeeData requester, WFInstance instance, List<WFTrainingData> wfTrainingList, TrainingCourseEventData trainingCourseEvent, WFTask reTask, boolean isApproved, List<EmployeeData> internalCopiesEmployees) throws BusinessException {
	if (isApproved)
	    validateWFTrainings(wfTrainingList, trainingCourseEvent, instance.getProcessId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    if (isApproved) {
		updateWFInstanceBeneficiaries(instance.getInstanceId(), getTrainingEmployeesInstanceBeneficiariesIds(wfTrainingList), session);

		List<WFTrainingData> trainingRequestsToAdd = new ArrayList<WFTrainingData>();
		List<WFTrainingData> trainingRequestsToUpdate = new ArrayList<WFTrainingData>();

		for (WFTrainingData wfTrainingData : wfTrainingList) {
		    String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);
		    wfTrainingData.setInternalCopies(internalCopies);
		    if (wfTrainingData.getInstanceId() == null) {
			trainingRequestsToAdd.add(wfTrainingData);
		    } else
			trainingRequestsToUpdate.add(wfTrainingData);
		}

		if (trainingRequestsToAdd.size() > 0)
		    addWFTrainings(trainingRequestsToAdd, instance.getInstanceId(), session);
		if (trainingRequestsToUpdate.size() > 0)
		    updateWFTrainings(trainingRequestsToUpdate, session);

		EmployeeData trainingManager = EmployeesService.getEmployeeDirectManager(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), new Date(), HijriDateService.getHijriSysDate(), instance.getInstanceId(), getDelegate(trainingManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), trainingManager.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), reTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    public static void doNominationSRE(EmployeeData requester, WFInstance instance, List<WFTrainingData> wfTrainingList, TrainingCourseEventData trainingCourseEvent, WFTask sreTask, boolean isApproved, List<EmployeeData> internalCopiesEmployees) throws BusinessException {
	if (isApproved)
	    validateWFTrainings(wfTrainingList, trainingCourseEvent, instance.getProcessId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    if (isApproved) {
		updateWFInstanceBeneficiaries(instance.getInstanceId(), getTrainingEmployeesInstanceBeneficiariesIds(wfTrainingList), session);

		List<WFTrainingData> trainingRequestsToAdd = new ArrayList<WFTrainingData>();
		List<WFTrainingData> trainingRequestsToUpdate = new ArrayList<WFTrainingData>();

		for (WFTrainingData wfTrainingData : wfTrainingList) {
		    String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);
		    wfTrainingData.setInternalCopies(internalCopies);
		    if (wfTrainingData.getInstanceId() == null)
			trainingRequestsToAdd.add(wfTrainingData);
		    else
			trainingRequestsToUpdate.add(wfTrainingData);
		}

		if (trainingRequestsToAdd.size() > 0)
		    addWFTrainings(trainingRequestsToAdd, instance.getInstanceId(), session);
		if (trainingRequestsToUpdate.size() > 0)
		    updateWFTrainings(trainingRequestsToUpdate, session);

		EmployeeData trainingManager = EmployeesService.getEmployeeDirectManager(sreTask.getOriginalId());
		completeWFTask(sreTask, WFTaskActionsEnum.REVIEW.getCode(), new Date(), HijriDateService.getHijriSysDate(), instance.getInstanceId(), getDelegate(trainingManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), trainingManager.getEmpId(), sreTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), sreTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, sreTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    public static void doNominationESRE(EmployeeData requester, WFInstance instance, WFTask esreTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    if (isApproved) {
		EmployeeData trainingManager = EmployeesService.getEmployeeDirectManager(esreTask.getOriginalId());
		completeWFTask(esreTask, WFTaskActionsEnum.REVIEW.getCode(), new Date(), HijriDateService.getHijriSysDate(), instance.getInstanceId(), getDelegate(trainingManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), trainingManager.getEmpId(), esreTask.getTaskUrl(), WFTaskRolesEnum.EXTRA_SECONDARY_SIGN_MANAGER.getCode(), esreTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, esreTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    public static void doNominationSSM(EmployeeData requester, WFInstance instance, List<WFTrainingData> wfTrainingList, WFTask ssmTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		String role;
		long assigneeId;
		long originalId;

		EmployeeData curManager = EmployeesService.getEmployeeData(ssmTask.getOriginalId());

		if (curManager.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()) {
		    WFPosition position;
		    if (wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode())
			position = getWFPosition(WFPositionsEnum.INTERNAL_TRAINING_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		    else if (wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() || wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.MORNING_COURSE.getCode() || wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.SCHOLARSHIP.getCode())
			position = getWFPosition(WFPositionsEnum.EXTERNAL_TRAINING_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		    else {
			position = getWFPosition(WFPositionsEnum.TRAINING_PLANNING_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		    }

		    EmployeeData trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

		    originalId = trainingManager.getEmpId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
		} else { // Send to next manager
		    originalId = curManager.getManagerId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    role = WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode();
		}

		completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, originalId, ssmTask.getTaskUrl(), role, ssmTask.getLevel(), session);

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		List<WFTask> reviewerTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode());
		long originalId = reviewerTasks.isEmpty() ? requester.getEmpId() : reviewerTasks.get(0).getOriginalId();
		completeWFTask(ssmTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), requester.getEmpId()), originalId, ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), ssmTask.getLevel(), session);

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

    public static void doNominationSM(EmployeeData requester, WFInstance instance, List<WFTrainingData> wfTrainingList, TrainingCourseEventData courseEventData, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(smTask.getOriginalId());

		boolean cycleCompleted = false;

		long requestingRegionId = requester.getPhysicalRegionId().longValue();

		if (wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
		    if (courseEventData.getTrainingUnitRegionId() == requestingRegionId && courseEventData.getTrainingUnitRegionId() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() && courseEventData.getTrainingUnitRegionId() != RegionsEnum.ACADEMY.getCode()) {
			if (nextManager.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    cycleCompleted = true;
			}
		    } else if (nextManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
			cycleCompleted = true;
		    }
		} else if (wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {
		    EmployeeData curManager = EmployeesService.getEmployeeData(smTask.getOriginalId());

		    if (instance.getProcessId().longValue() == WFProcessesEnum.SCHOLARSHIP_REQUEST.getCode()) {
			if (curManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
			    cycleCompleted = true;
			}
		    } else {
			if (EmployeesService.getEmployeeDirectManager(wfTrainingList.get(0).getEmployeeId()) != null && EmployeesService.getEmployeeDirectManager(wfTrainingList.get(0).getEmployeeId()).getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
			    if (curManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
				cycleCompleted = true;
			    }
			} else if (nextManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
			    cycleCompleted = true;
			}
		    }
		} else if (wfTrainingList.get(0).getTrainingTypeId().longValue() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode()) {
		    EmployeeData curManager = EmployeesService.getEmployeeData(smTask.getOriginalId());
		    if (curManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
			cycleCompleted = true;
		    }
		} else if (nextManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
		    cycleCompleted = true;
		}

		if (cycleCompleted) {

		    if (wfTrainingList.get(0).getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {

			if (courseEventData.getTrainingUnitRegionId() != requestingRegionId && requestingRegionId != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()
				&& (courseEventData.getTrainingUnitRegionId() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() && courseEventData.getTrainingUnitRegionId() != RegionsEnum.ACADEMY.getCode())) {

			    WFPosition position = getWFPosition(WFPositionsEnum.REGION_TRAINING_UNIT_MANAGER.getCode(), courseEventData.getTrainingUnitRegionId());
			    EmployeeData trainingManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(trainingManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), trainingManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.EXTRA_SECONDARY_MANAGER_REDIRECT.getCode(), smTask.getLevel(), session);
			} else {
			    closeTrainingEmployeeWorkFlow(requester, instance, wfTrainingList, courseEventData, smTask, session);
			}
		    } else {
			closeTrainingEmployeeWorkFlow(requester, instance, wfTrainingList, courseEventData, smTask, session);
		    }
		} else { // Send to next manager

		    if ((instance.getProcessId().longValue() == WFProcessesEnum.STUDY_ENROLLMENT_REQUEST.getCode() && wfTrainingList.get(0).getFundSource().intValue() == FundSourceEnum.COST_ON_BORDER_GUARD.getCode()) || (instance.getProcessId().longValue() == WFProcessesEnum.SCHOLARSHIP_REQUEST.getCode())) {

			WFPosition planningUnitPosition = getWFPosition(WFPositionsEnum.TRAINING_PLANNING_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			EmployeeData planningUnitManager = EmployeesService.getEmployeeByPosition(planningUnitPosition.getUnitId(), planningUnitPosition.getEmpId());

			WFPosition postGraduatePosition = getWFPosition(WFPositionsEnum.POST_GRADUATE_SCHOLARSHIP_REPRESENTATIVE.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			EmployeeData representative = EmployeesService.getEmployeeByPosition(postGraduatePosition.getUnitId(), postGraduatePosition.getEmpId());

			if (planningUnitManager.getManagerId().longValue() == smTask.getOriginalId()) {
			    if (planningUnitManager.getManagerId().longValue() == representative.getEmpId().longValue())
				completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
			    else
				completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(representative.getEmpId(), instance.getProcessId(), requester.getEmpId()), representative.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
			} else if (representative.getEmpId().longValue() == smTask.getOriginalId()) {
			    List<WFTaskData> prevsTasks = BaseWorkFlow.getWFInstanceCompletedTasksData(smTask.getInstanceId(), smTask.getTaskId(), smTask.getLevel());
			    WFTaskData task = prevsTasks.get(prevsTasks.size() - 1);
			    if (task.getOriginalId().longValue() == planningUnitManager.getManagerId().longValue()) {
				EmployeeData nextSignManager = EmployeesService.getEmployeeDirectManager(task.getOriginalId());
				completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextSignManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextSignManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
			    } else
				completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
			} else {
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
			}
		    } else {
			completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		    }
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		List<WFTask> reviewerTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode());
		long originalId = reviewerTasks.isEmpty() ? requester.getEmpId() : reviewerTasks.get(0).getOriginalId();
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), requester.getEmpId()), originalId, smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), smTask.getLevel(), session);

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

    public static void doNominationESSM(EmployeeData requester, WFInstance instance, List<WFTrainingData> wfTrainingList, TrainingCourseEventData courseEventData, WFTask essmTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(essmTask.getOriginalId());

		if (nextManager.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()) {

		    closeTrainingEmployeeWorkFlow(requester, instance, wfTrainingList, courseEventData, essmTask, session);
		} else { // Send to next manager
		    completeWFTask(essmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), essmTask.getTaskUrl(), WFTaskRolesEnum.EXTRA_SECONDARY_SIGN_MANAGER.getCode(), essmTask.getLevel(), session);
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		List<WFTask> reviewerTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.EXTRA_SECONDARY_REVIEWER_EMP.getCode());
		long originalId = reviewerTasks.isEmpty() ? requester.getEmpId() : reviewerTasks.get(0).getOriginalId();
		completeWFTask(essmTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), requester.getEmpId()), originalId, essmTask.getTaskUrl(), WFTaskRolesEnum.EXTRA_SECONDARY_REVIEWER_EMP.getCode(), essmTask.getLevel(), session);

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, essmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /*---------------------------Work Flow Steps EmployeeMilitaryIntCourseEventCancel ----------------------*/

    public static void initEmployeeCourseEventCancel(EmployeeData requester, WFTrainingData wfTrainingData, TrainingCourseEventData courseEventData, String taskUrl) throws BusinessException {
	validateWFTrainings(Arrays.asList(wfTrainingData), courseEventData, WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode(), requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), null, Arrays.asList(wfTrainingData.getEmployeeId()), session);

	    addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), instance.getProcessId(), requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

	    wfTrainingData.setInstanceId(instance.getInstanceId());
	    DataAccess.addEntity(wfTrainingData.getWfTraining(), session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    wfTrainingData.setInstanceId(null);
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doEmployeeCourseEventCancelSM(EmployeeData requester, WFInstance instance, WFTrainingData wfTrainingData, TrainingCourseEventData courseEventData, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		long trainingUnitManagerId = UnitsService.getUnitById(courseEventData.getTrainingUnitId()).getPhysicalManagerId();
		if (smTask.getOriginalId().equals(trainingUnitManagerId)) {
		    closeTrainingEmployeeWorkFlow(requester, instance, Arrays.asList(wfTrainingData), courseEventData, smTask, session);
		} else { // Send to next manager
		    EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(smTask.getOriginalId());
		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		List<WFTask> reviewerTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode());
		long originalId = reviewerTasks.isEmpty() ? requester.getEmpId() : reviewerTasks.get(0).getOriginalId();
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), requester.getEmpId()), originalId, smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), smTask.getLevel(), session);

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

    public static void doEmployeeCourseEventCancelRE(EmployeeData requester, WFInstance instance, WFTrainingData wfTrainingData, TrainingCourseEventData courseEventData, WFTask reTask, boolean isApproved) throws BusinessException {
	if (isApproved)
	    validateWFTrainings(Arrays.asList(wfTrainingData), courseEventData, instance.getProcessId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {

		DataAccess.updateEntity(wfTrainingData.getWfTraining(), session);
		updateWFInstanceBeneficiaries(instance.getInstanceId(), Arrays.asList(wfTrainingData.getEmployeeId()), session);

		EmployeeData manager = EmployeesService.getEmployeeDirectManager(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(manager.getEmpId(), instance.getProcessId(), requester.getEmpId()), manager.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), reTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /*---------------------------Operations---------------------------*/
    private static void addWFTrainings(List<WFTrainingData> traningRequests, long instanceId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (WFTrainingData trainingRequest : traningRequests) {
		trainingRequest.setInstanceId(instanceId);
		DataAccess.addEntity(trainingRequest.getWfTraining(), session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (WFTrainingData trainingRequest : traningRequests)
		trainingRequest.setInstanceId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void updateWFTrainings(List<WFTrainingData> traningRequests, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (WFTrainingData trainingRequest : traningRequests) {
		DataAccess.updateEntity(trainingRequest.getWfTraining(), session);
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

    public static void deleteWFTraining(WFTrainingData wfTrainingData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(wfTrainingData.getWfTraining(), session);
	    deleteWFInstanceBeneficiaries(wfTrainingData.getInstanceId(), Arrays.asList(wfTrainingData.getEmployeeId()), session);

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

    private static void closeTrainingEmployeeWorkFlow(EmployeeData requester, WFInstance instance, List<WFTrainingData> wfTrainingList, TrainingCourseEventData courseEventData, WFTask smTask, CustomSession session) throws BusinessException {
	try {
	    doTrainingIntegration(wfTrainingList, courseEventData, instance, smTask, session);

	    List<Long> beneficairyEmployeesIds = new ArrayList<Long>();
	    Set<Long> categoriesIdsHashSet = new HashSet<Long>();
	    List<Long> internalCopiesList = new ArrayList<Long>();

	    for (WFTrainingData wfTrainingData : wfTrainingList) {
		beneficairyEmployeesIds.add(wfTrainingData.getEmployeeId());
		categoriesIdsHashSet.add(wfTrainingData.getCategoryId());
	    }
	    List<Long> categoriesIdsList = Arrays.asList(categoriesIdsHashSet.toArray(new Long[categoriesIdsHashSet.size()]));

	    if (wfTrainingList.get(0).getInternalCopies() != null) {
		String[] internalCopies = wfTrainingList.get(0).getInternalCopies().split(",");
		for (int i = 0; i < internalCopies.length; i++) {
		    internalCopiesList.add(Long.valueOf(internalCopies[i]));
		}
	    }

	    List<Long> additionalIds = new ArrayList<Long>();
	    additionalIds.addAll(beneficairyEmployeesIds);
	    additionalIds.addAll(internalCopiesList);

	    // Compute all copies
	    Long[] allCopies = computeInstanceNotifications(categoriesIdsList, requester.getPhysicalRegionId(), instance.getProcessId(), beneficairyEmployeesIds, additionalIds);

	    closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), allCopies, session);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void doTrainingIntegration(List<WFTrainingData> wfTrainingList, TrainingCourseEventData courseEventData, WFInstance instance, WFTask smTask, CustomSession session) throws BusinessException {
	// For all claim request except studyEnrollment and scholarship
	if (courseEventData == null && isProcessHasCourseEvent(instance.getProcessId())) {
	    List<TrainingCourseEventData> previousCourseEventsList = new ArrayList<TrainingCourseEventData>();
	    previousCourseEventsList = TrainingCoursesEventsService.getPreviousCourseEvents(wfTrainingList.get(0).getCourseId(), wfTrainingList.get(0).getTrainingTypeId(), wfTrainingList.get(0).getSerial(), wfTrainingList.get(0).getTrainingUnitId(), wfTrainingList.get(0).getExternalPartyId(), wfTrainingList.get(0).getStartDateString(), wfTrainingList.get(0).getEndDateString(), FlagsEnum.OFF.getCode());

	    if (previousCourseEventsList.isEmpty() || previousCourseEventsList.get(0).getTrainingYearId() != null) // Skip course events added after training plan because their eflag is 0
		courseEventData = TrainingCoursesEventsWorkFlow.constructCourseEventByWFTraining(wfTrainingList.get(0));
	    else
		courseEventData = previousCourseEventsList.get(0);
	}

	int trainingTransactionCategory = specifyTrainingTransactionCategory(instance.getProcessId());

	List<TrainingTransactionData> transactionsList = new ArrayList<>();
	for (WFTrainingData wfTraining : wfTrainingList) {

	    if (wfTraining.getTrainingTypeId().longValue() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode() || wfTraining.getTrainingTypeId().longValue() == TrainingTypesEnum.SCHOLARSHIP.getCode())
		transactionsList.add(constructTrainingTransactionForScholarshipAndStudyEnrollment(wfTraining, instance.getProcessId()));
	    else
		transactionsList.add(constructTrainingTransaction(wfTraining, instance.getProcessId(), courseEventData == null ? null : courseEventData.getId()));

	    if (instance.getProcessId().longValue() == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode() || instance.getProcessId().longValue() == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode()) {
		TrainingEmployeesService.handleTrainingRequests(Arrays.asList(constructTrainingTransaction(wfTraining, (instance.getProcessId() == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode()) ? WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REQUEST.getCode() : WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REQUEST.getCode(), courseEventData == null ? null : courseEventData.getId())), null,
			getWFProcess(instance.getProcessId()).getProcessName(), courseEventData, TrainingTransactionCategoryEnum.NOMINATION.getCode(), session);
	    }
	}
	// the following code handles training transaction details (decisions ONLY) , in case of replacement(msh m3ana hna) , the nomination transaction is not included
	Long transactionTypeId = null;

	if (instance.getProcessId().longValue() == WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TRAINEE_COURSE_EVENT_CANCEL.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	} else if (instance.getProcessId().longValue() == WFProcessesEnum.SCHOLARSHIP.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	} else if (instance.getProcessId().longValue() == WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_EXTENSION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	} else if (instance.getProcessId().longValue() == WFProcessesEnum.SCHOLARSHIP_TERMINATION.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TERMINATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	} else if (instance.getProcessId().longValue() == WFProcessesEnum.SCHOLARSHIP_CANCEL.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_CANCELLATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	}

	List<TrainingTransactionDetailData> trainingTransactionDetails = null;
	if (transactionTypeId != null) {
	    trainingTransactionDetails = new ArrayList<TrainingTransactionDetailData>();
	    for (int i = 0; i < transactionsList.size(); i++) {
		TrainingTransactionDetailData transactionDetailData = constructTrainingTransactionDetail(transactionsList.get(i).getId(), wfTrainingList.get(i), transactionsList.get(i).getEmployeeId(), EmployeesService.getEmployeeData(smTask.getAssigneeId()).getPhysicalRegionId(), smTask.getAssigneeId(), smTask.getOriginalId(), transactionTypeId, wfTrainingList.get(i).getInternalCopies(), wfTrainingList.get(i).getExternalCopies());
		trainingTransactionDetails.add(transactionDetailData);
	    }
	}

	TrainingEmployeesService.handleTrainingRequests(transactionsList, trainingTransactionDetails, getWFProcess(instance.getProcessId()).getProcessName(), courseEventData, trainingTransactionCategory, session);
    }

    public static WFTrainingData constructWFTraining(long employeeId, long categoryId, long trainingTypeId, long processId) throws BusinessException {
	WFTrainingData wfTraining = new WFTrainingData();
	wfTraining.setEmployeeId(employeeId);
	wfTraining.setCategoryId(categoryId);
	wfTraining.setTrainingTypeId(trainingTypeId);

	if (processId == WFProcessesEnum.MORNING_TRAINING_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REQUEST.getCode()) {
	    EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	    wfTraining.setEmployeeName(employee.getName());
	    wfTraining.setEmployeeNumber(employee.getCategoryId() == CategoriesEnum.OFFICERS.getCode() ? (employee.getMilitaryNumber() != null ? employee.getMilitaryNumber().toString() : "") : employee.getJobCode());
	    wfTraining.setEmployeeJobName(employee.getJobDesc());
	    wfTraining.setEmployeePhysicalUnitFullname(employee.getPhysicalUnitFullName());
	    wfTraining.setEmployeeRankDesc(employee.getRankDesc());
	}

	if (processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode()
		|| processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode()) {
	    wfTraining.setSuccessFlag(FlagsEnum.ON.getCode());
	    wfTraining.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
	    wfTraining.setAttendanceGrade(GradesEnum.NOT_AVAILABLE.getCode());
	    wfTraining.setBehaviorGrade(GradesEnum.NOT_AVAILABLE.getCode());

	} else if (processId == WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode()
		|| processId == WFProcessesEnum.EVENING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.EVENING_TRAINING_CLAIM_REQUEST.getCode()
		|| processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode() || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode()
		|| processId == WFProcessesEnum.SCHOLARSHIP_CLAIM.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM_REQUEST.getCode()) {

	    wfTraining.setSuccessFlag(FlagsEnum.ON.getCode());
	    wfTraining.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());

	    if (processId == WFProcessesEnum.EVENING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.EVENING_TRAINING_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode() || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode())
		wfTraining.setFundSource(FundSourceEnum.COST_ON_EMPLOYEE.getCode());
	    else
		wfTraining.setFundSource(FundSourceEnum.COST_ON_BORDER_GUARD.getCode());
	}
	return wfTraining;
    }

    public static WFTrainingData constructWFTrainingForScholarshipDecision(Long basedOnTrainingTransactionId, long employeeId, long categoryId, long processId) throws BusinessException {

	WFTrainingData wfTraining = new WFTrainingData();

	wfTraining.setEmployeeId(employeeId);
	wfTraining.setCategoryId(categoryId);
	wfTraining.setTrainingTypeId(TrainingTypesEnum.SCHOLARSHIP.getCode());

	if (basedOnTrainingTransactionId != null) {
	    TrainingTransactionData trainingTransaction = TrainingEmployeesService.getTrainingTransactionsDataByIds(new Long[] { basedOnTrainingTransactionId }).get(0);

	    wfTraining.setBasedOnTrainingTransactionId(basedOnTrainingTransactionId);
	    wfTraining.setGraduationPlaceDetailDesc(trainingTransaction.getGraduationPlaceDetailDesc());
	    wfTraining.setGraduationPlaceDetailId(trainingTransaction.getGraduationPlaceDetailId());
	    wfTraining.setGraduationPlaceDetailAddress(trainingTransaction.getGraduationPlaceDetailAddress());
	    wfTraining.setGraduationPlaceDesc(trainingTransaction.getGraduationPlaceDesc());
	    wfTraining.setGraduationPlaceCountryName(trainingTransaction.getGraduationPlaceCountryName());
	    wfTraining.setQualificationLevelId(trainingTransaction.getQualificationLevelId());
	    wfTraining.setStudySubject(trainingTransaction.getStudySubject());

	    if (processId != WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode())
		wfTraining.setStartDate(trainingTransaction.getStudyStartDate());
	    else
		wfTraining.setStartDate(HijriDateService.addSubHijriDays(trainingTransaction.getStudyEndDate(), 1));

	    if (processId == WFProcessesEnum.SCHOLARSHIP.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CANCEL.getCode()) {
		wfTraining.setMonthsCount(trainingTransaction.getStudyMonthsCount());
		wfTraining.setDaysCount(trainingTransaction.getStudyDaysCount());
		wfTraining.setEndDate(trainingTransaction.getStudyEndDate());
	    }
	}

	return wfTraining;
    }

    private static TrainingTransactionData constructTrainingTransactionForScholarshipAndStudyEnrollment(WFTrainingData wfTraining, long processId) throws BusinessException {
	TrainingTransactionData trainingTransaction;

	if (wfTraining.getBasedOnTrainingTransactionId() != null) {

	    List<TrainingTransactionData> transactions = TrainingEmployeesService.getTrainingTransactionsDataByIds(new Long[] { wfTraining.getBasedOnTrainingTransactionId() });

	    if (transactions.isEmpty())
		throw new BusinessException("error_transactionDataError");

	    trainingTransaction = transactions.get(0);

	    if (processId == WFProcessesEnum.SCHOLARSHIP.getCode()) {
		trainingTransaction.setStatus(TraineeStatusEnum.SCHOLARSHIP.getCode());
		trainingTransaction.setGraduationPlaceDetailId(wfTraining.getGraduationPlaceDetailId());
		trainingTransaction.setQualificationLevelId(wfTraining.getQualificationLevelId());
		trainingTransaction.setStudySubject(wfTraining.getStudySubject());

	    } else if (processId == WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.SCHOLARSHIP_EXTENSION.getCode());

	    else if (processId == WFProcessesEnum.SCHOLARSHIP_TERMINATION.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.SCHOLARSHIP_TERMINATION.getCode());

	    else if (processId == WFProcessesEnum.SCHOLARSHIP_CANCEL.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.SCHOLARSHIP_CANCELLATION.getCode());
	} else {
	    trainingTransaction = new TrainingTransactionData();
	    trainingTransaction.setTrainingTypeId(wfTraining.getTrainingTypeId());
	    trainingTransaction.setEmployeeId(wfTraining.getEmployeeId());
	    trainingTransaction.setSuccessFlag(wfTraining.getSuccessFlag());
	    trainingTransaction.setQualificationGrade(wfTraining.getQualificationGrade());
	    trainingTransaction.setFundSource(wfTraining.getFundSource());
	    trainingTransaction.setGraduationPlaceDetailId(wfTraining.getGraduationPlaceDetailId());
	    trainingTransaction.setQualificationLevelId(wfTraining.getQualificationLevelId());
	    trainingTransaction.setQualificationMinorSpecId(wfTraining.getQualificationMinorSpecId());
	    trainingTransaction.setStudySubject(wfTraining.getStudySubject());
	    trainingTransaction.setStudyGraduationDate(wfTraining.getStudyGraduationDate());
	    trainingTransaction.setAttachments(wfTraining.getAttachments());
	    trainingTransaction.setEflag(FlagsEnum.ON.getCode());
	    trainingTransaction.setMigFlag(FlagsEnum.OFF.getCode());

	    if (processId == WFProcessesEnum.STUDY_ENROLLMENT_REQUEST.getCode() || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode() || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.SCHOLARSHIP_REQUEST.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM_REQUEST.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode());

	    else if (processId == WFProcessesEnum.SCHOLARSHIP.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.SCHOLARSHIP.getCode());

	    if (processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode() || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode() ||
		    processId == WFProcessesEnum.SCHOLARSHIP_CLAIM.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM_REQUEST.getCode())
		trainingTransaction.setEflag(FlagsEnum.OFF.getCode());
	}

	if (wfTraining.getTrainingTypeId().longValue() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode()) {
	    trainingTransaction.setStudyStartDate(wfTraining.getStartDate());
	    trainingTransaction.setStudyEndDate(wfTraining.getEndDate());
	    trainingTransaction.setStudyDaysCount(HijriDateService.hijriDateDiff(wfTraining.getStartDate(), wfTraining.getEndDate()));
	} else {
	    if (processId == WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode()) {
		trainingTransaction.setStudyMonthsCount(trainingTransaction.getStudyMonthsCount() + (wfTraining.getMonthsCount() == null ? 0 : wfTraining.getMonthsCount()));
		trainingTransaction.setStudyDaysCount((trainingTransaction.getStudyDaysCount() == null ? 0 : trainingTransaction.getStudyDaysCount()) + (wfTraining.getDaysCount() == null ? 0 : wfTraining.getDaysCount()));
		trainingTransaction.setStudyEndDate(HijriDateService.addSubHijriMonthsDays(trainingTransaction.getStudyEndDate(), wfTraining.getMonthsCount() == null ? 0 : wfTraining.getMonthsCount(), wfTraining.getDaysCount() == null ? 0 : wfTraining.getDaysCount()));

	    } else if (processId == WFProcessesEnum.SCHOLARSHIP_TERMINATION.getCode()) {
		if (!wfTraining.getEndDate().after(wfTraining.getStartDate()))
		    throw new BusinessException("error_trainingStudyEndDateBeforeStudyStartDate");
		trainingTransaction.setStudyEndDate(wfTraining.getEndDate());
		Integer[] diff = HijriDateService.hijriDateDiffInMonthsAndDays(HijriDateService.getHijriDateString(wfTraining.getStartDate()), HijriDateService.getHijriDateString(wfTraining.getEndDate()));
		trainingTransaction.setStudyDaysCount(diff[0]);
		trainingTransaction.setStudyMonthsCount(diff[1]);
	    } else {
		trainingTransaction.setStudyStartDate(wfTraining.getStartDate());
		trainingTransaction.setStudyEndDate(wfTraining.getEndDate());
		trainingTransaction.setStudyMonthsCount(wfTraining.getMonthsCount());
		trainingTransaction.setStudyDaysCount(wfTraining.getDaysCount());
	    }
	}

	return trainingTransaction;
    }

    private static TrainingTransactionData constructTrainingTransaction(WFTrainingData wfTraining, long processId, Long courseEventId) throws BusinessException {
	TrainingTransactionData trainingTransaction;

	if (processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_APOLOGY_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_APOLOGY_REQUEST.getCode()
		|| processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode()
		|| processId == WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode()) {

	    Long[] trainingTypeId = new Long[] { wfTraining.getTrainingTypeId() };

	    Integer[] statusIds;
	    if (wfTraining.getTrainingTypeId().equals(TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode())) {
		statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() };
	    } else {
		statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(), TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() };
	    }

	    List<TrainingTransactionData> transactions = TrainingEmployeesService.getTrainingTransactionsData(trainingTypeId, statusIds, FlagsEnum.ALL.getCode(), wfTraining.getEmployeeId(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId);

	    if (transactions.isEmpty())
		throw new BusinessException("error_transactionDataError");

	    trainingTransaction = transactions.get(0);
	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode()) {
		trainingTransaction.setReplacementEmployeeId(wfTraining.getReplacementEmployeeId());
	    }
	    trainingTransaction.setReasons(wfTraining.getReasons());
	    trainingTransaction.setReasonType(wfTraining.getReasonType());
	    if (wfTraining.getAttachments() != null)
		trainingTransaction.setAttachments(wfTraining.getAttachments());

	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_APOLOGY_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_APOLOGY_REQUEST.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.APOLOGIZED.getCode());
	    else if (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.REPLACED.getCode());
	    else if (processId == WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.CANCELLED.getCode());

	} else {
	    trainingTransaction = new TrainingTransactionData();
	    trainingTransaction.setTrainingTypeId(wfTraining.getTrainingTypeId());
	    trainingTransaction.setEmployeeId(wfTraining.getReplacementEmployeeId() == null ? wfTraining.getEmployeeId() : wfTraining.getReplacementEmployeeId());
	    trainingTransaction.setCourseEventId(courseEventId);
	    trainingTransaction.setReplacementEmployeeId(null);
	    trainingTransaction.setTrainingPurpose(wfTraining.getTrainingPurpose());
	    trainingTransaction.setOtherPurpose(wfTraining.getOtherPurpose());
	    trainingTransaction.setSuccessFlag(wfTraining.getSuccessFlag());
	    trainingTransaction.setSuccessRanking(wfTraining.getSuccessRanking());
	    trainingTransaction.setSuccessRankingDesc(wfTraining.getSuccessRankingDesc());
	    trainingTransaction.setQualificationGradePercentage(wfTraining.getQualificationGradePercentage());
	    trainingTransaction.setQualificationGrade(wfTraining.getQualificationGrade());
	    trainingTransaction.setAttendanceGradePercentage(wfTraining.getAttendanceGradePercentage());
	    trainingTransaction.setAttendanceGrade(wfTraining.getAttendanceGrade());
	    trainingTransaction.setBehaviorGrade(wfTraining.getBehaviorGrade());
	    trainingTransaction.setBehaviorGradePercentage(wfTraining.getBehaviorGradePercentage());
	    trainingTransaction.setFundSource(wfTraining.getFundSource());
	    trainingTransaction.setAttachments(wfTraining.getAttachments());
	    trainingTransaction.setEflag(FlagsEnum.ON.getCode());
	    trainingTransaction.setMigFlag(FlagsEnum.OFF.getCode());

	    if (processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REQUEST.getCode()
		    || processId == WFProcessesEnum.MORNING_TRAINING_REQUEST.getCode() || processId == WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.EVENING_TRAINING_REQUEST.getCode() || processId == WFProcessesEnum.EVENING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.EVENING_TRAINING_CLAIM_REQUEST.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode());

	    else if (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REQUEST.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.NOMINATION_ACCEPTED.getCode());

	    else if (processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode()
		    || processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.FINISHED.getCode());

	    else if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_POSTPONE_REQUEST.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.TRAINING_COURSE_EVENT_POSTPONED.getCode());

	    else if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_CANCEL_REQUEST.getCode())
		trainingTransaction.setStatus(TraineeStatusEnum.TRAINING_COURSE_EVENT_CANCELLED.getCode());

	    if (processId == WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode() ||
		    processId == WFProcessesEnum.EVENING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.EVENING_TRAINING_CLAIM_REQUEST.getCode() ||
		    processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode() ||
		    processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode())
		trainingTransaction.setEflag(FlagsEnum.OFF.getCode());

	}

	return trainingTransaction;
    }

    public static TrainingTransactionDetailData constructTrainingTransactionDetail(Long trainingTransactionId, WFTrainingData wfTraining, long employeeId, Long decisionRegionId, Long decisionApprovedId, Long originalDecisionApprovedId, long transactionTypeId, String internalCopies, String externalCopies) throws BusinessException {

	TrainingTransactionDetailData trainingTransactionDetailData = new TrainingTransactionDetailData();

	trainingTransactionDetailData.setTrainingTransactionId(trainingTransactionId);
	trainingTransactionDetailData.setDecisionRegionId(decisionRegionId);
	trainingTransactionDetailData.setDecisionApprovedId(originalDecisionApprovedId);
	trainingTransactionDetailData.setOriginalDecisionApprovedId(originalDecisionApprovedId);
	trainingTransactionDetailData.setTransactionTypeId(transactionTypeId);

	EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	trainingTransactionDetailData.setTransEmpCategoryId(employee.getCategoryId());
	if (employee.getStatusId() == EmployeeStatusEnum.SERVICE_TERMINATED.getCode()) {
	    TerminationTransactionData terminationTransaction = TerminationsService.getEffectiveTerminationTransaction(employeeId);
	    trainingTransactionDetailData.setTransEmpJobCode(terminationTransaction.getJobCode());
	    trainingTransactionDetailData.setTransEmpJobName(terminationTransaction.getJobName());
	    trainingTransactionDetailData.setTransEmpRankDesc(terminationTransaction.getTransEmpRankDesc());
	    trainingTransactionDetailData.setTransEmpUnitFullName(terminationTransaction.getTransEmpUnitFullName());
	} else {
	    trainingTransactionDetailData.setTransEmpJobCode(employee.getJobCode());
	    trainingTransactionDetailData.setTransEmpJobName(employee.getJobDesc());
	    trainingTransactionDetailData.setTransEmpRankDesc(employee.getRankDesc());
	    trainingTransactionDetailData.setTransEmpUnitFullName(employee.getPhysicalUnitFullName());
	}

	if (wfTraining != null) {
	    trainingTransactionDetailData.setMinistryDecisionNumber(wfTraining.getMinistryDecisionNumber());
	    trainingTransactionDetailData.setMinistryDecisionDate(wfTraining.getMinistryDecisionDate());
	    trainingTransactionDetailData.setMinistryReportNumber(wfTraining.getMinistryReportNumber());
	    trainingTransactionDetailData.setMinistryReportDate(wfTraining.getMinistryReportDate());
	    trainingTransactionDetailData.setStudyStartDate(wfTraining.getStartDate());
	    trainingTransactionDetailData.setStudyEndDate(wfTraining.getEndDate());
	    trainingTransactionDetailData.setStudyMonthsCount(wfTraining.getMonthsCount());
	    trainingTransactionDetailData.setStudyDaysCount(wfTraining.getDaysCount());
	    trainingTransactionDetailData.setAttachments(wfTraining.getAttachments());
	    trainingTransactionDetailData.setReasons(wfTraining.getReasons());

	    if (wfTraining.getTrainingTypeId().longValue() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {
		JobData jobData = JobsService.getJobById(employee.getJobId());
		if (jobData == null)
		    throw new BusinessException("error_transactionDataError");

		UnitData directedToUnit = UnitsService.getAncestorUnderPresidencyByLevel(jobData.getUnitId(), UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode());
		if (directedToUnit != null && directedToUnit.getOfficialManagerId() != null)
		    trainingTransactionDetailData.setDirectedToJobName(EmployeesService.getEmployeeData(directedToUnit.getOfficialManagerId()).getJobDesc());
	    }
	}

	trainingTransactionDetailData.setInternalCopies(internalCopies);
	trainingTransactionDetailData.setExternalCopies(externalCopies);

	return trainingTransactionDetailData;
    }

    private static boolean isRequestProcess(long processId) {

	if (processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode()
		|| processId == WFProcessesEnum.EVENING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode()
		|| processId == WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM.getCode()
		|| processId == WFProcessesEnum.SCHOLARSHIP.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode()
		|| processId == WFProcessesEnum.SCHOLARSHIP_TERMINATION.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CANCEL.getCode()
		|| processId == WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode()) {
	    return false;
	} else {
	    return true;
	}
    }

    private static int specifyTrainingTransactionCategory(long processId) {
	if (processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode()
		|| processId == WFProcessesEnum.EVENING_TRAINING_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode()
		|| processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode()
		|| processId == WFProcessesEnum.EVENING_TRAINING_CLAIM.getCode() || processId == WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode()
		|| processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode()
		|| processId == WFProcessesEnum.SCHOLARSHIP_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM.getCode()) {

	    return TrainingTransactionCategoryEnum.CLAIM.getCode();
	} else if (processId == WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode()) {
	    return TrainingTransactionCategoryEnum.EMPLOYEE_COURSE_EVENT_CANCEL.getCode();
	} else if (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_APOLOGY_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_APOLOGY_REQUEST.getCode()) {
	    return TrainingTransactionCategoryEnum.NOMINATION_APOLOGY.getCode();
	} else if (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode()) {
	    return TrainingTransactionCategoryEnum.NOMINATION_REPLACEMENT.getCode();
	} else {
	    return TrainingTransactionCategoryEnum.NOMINATION.getCode();
	}
    }

    private static EmployeeData getTrainingManager(long processId, long physicalRegionId) throws BusinessException {
	WFPosition position = null;
	if (physicalRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {

	    if (processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode()) {
		position = getWFPosition(WFPositionsEnum.INTERNAL_TRAINING_UNIT_MANAGER.getCode(), physicalRegionId);

	    } else if (processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode()
		    || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM.getCode()
		    || processId == WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode()) {
		position = getWFPosition(WFPositionsEnum.EXTERNAL_TRAINING_UNIT_MANAGER.getCode(), physicalRegionId);

	    } else if (processId == WFProcessesEnum.EVENING_TRAINING_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.EVENING_TRAINING_CLAIM.getCode()
		    || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode()
		    || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode()) {
		position = getWFPosition(WFPositionsEnum.TRAINING_PLANNING_UNIT_MANAGER.getCode(), physicalRegionId);
	    }
	} else {
	    position = getWFPosition(WFPositionsEnum.REGION_TRAINING_UNIT_MANAGER.getCode(), physicalRegionId);
	}

	if (position == null) {
	    throw new BusinessException("error_positionNotFound");
	}

	EmployeeData emp = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
	if (emp == null) {
	    throw new BusinessException("error_employeeDataError");
	}

	return emp;
    }

    private static boolean isProcessHasCourseEvent(long processId) {
	if (processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode() || processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CLAIM_REQUEST.getCode()
		|| processId == WFProcessesEnum.STUDY_ENROLLMENT_REQUEST.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_REQUEST.getCode() || processId == WFProcessesEnum.SCHOLARSHIP.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode()
		|| processId == WFProcessesEnum.SCHOLARSHIP_TERMINATION.getCode() || processId == WFProcessesEnum.SCHOLARSHIP_CANCEL.getCode())
	    return false;
	else
	    return true;
    }

    private static List<Long> getTrainingEmployeesInstanceBeneficiariesIds(List<WFTrainingData> wfTrainingList) throws BusinessException {

	List<Long> instanceBeneficiariesIds = new ArrayList<Long>();
	for (WFTrainingData wfTraining : wfTrainingList) {
	    instanceBeneficiariesIds.add(wfTraining.getEmployeeId());
	    if (wfTraining.getReplacementEmployeeId() != null)
		instanceBeneficiariesIds.add(wfTraining.getReplacementEmployeeId());
	}

	return instanceBeneficiariesIds;
    }

    private static boolean isClaimRequest(WFInstance instance) {
	if (instance.getProcessId().equals(WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.EVENING_TRAINING_CLAIM_REQUEST.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.SCHOLARSHIP_CLAIM_REQUEST.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.EVENING_TRAINING_CLAIM.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode())
		|| instance.getProcessId().equals(WFProcessesEnum.SCHOLARSHIP_CLAIM.getCode()))
	    return true;

	return false;
    }

    /*---------------------------Validations--------------------------*/
    private static void initialValidationWFTrainings(List<WFTrainingData> wfTrainingList, long processId) throws BusinessException {
	for (WFTrainingData wfTraining : wfTrainingList) {
	    if (wfTraining.getTrainingTypeId() == TrainingTypesEnum.EVENING_COURSE.getCode() && specifyTrainingTransactionCategory(processId) == TrainingTransactionCategoryEnum.NOMINATION.getCode()) {
		if (HijriDateService.hijriDateDiff(HijriDateService.getHijriSysDate(), wfTraining.getStartDate()) < MIN_EVENING_COURSE_NOMINATION_BETWEEN_REQUEST_AND_START_DAYS) {
		    throw new BusinessException("error_eveningCourseStartdateAndRequestDateMinPeriod");
		}
	    }
	}
    }

    private static void validateWFTrainings(List<WFTrainingData> wfTrainingList, TrainingCourseEventData courseEventData, long processId) throws BusinessException {
	if (wfTrainingList == null || wfTrainingList.isEmpty()) {
	    throw new BusinessException("error_transactionDataError");
	}

	int trainingTransactionCategory = specifyTrainingTransactionCategory(processId);

	validateRunningWFTrainings(wfTrainingList);

	if (courseEventData != null)
	    TrainingCoursesEventsWorkFlow.validateRunningWFTrainingCourseEvents(courseEventData.getId(), null);

	// for all claims processes except scholarships and study enrollment
	if (courseEventData == null && isProcessHasCourseEvent(processId)) {
	    if (wfTrainingList.get(0).getCourseId() == null)
		throw new BusinessException("error_transactionDataError");

	    List<TrainingCourseEventData> previousCourseEventsList = TrainingCoursesEventsService.getPreviousCourseEvents(wfTrainingList.get(0).getCourseId(), wfTrainingList.get(0).getTrainingTypeId() == null ? FlagsEnum.ALL.getCode() : wfTrainingList.get(0).getTrainingTypeId(), wfTrainingList.get(0).getSerial(), wfTrainingList.get(0).getTrainingUnitId(), wfTrainingList.get(0).getExternalPartyId(),
		    wfTrainingList.get(0).getStartDateString(), wfTrainingList.get(0).getEndDateString(), FlagsEnum.OFF.getCode());
	    if (previousCourseEventsList.isEmpty() || previousCourseEventsList.get(0).getTrainingYearId() != null) // Skip course events added after training plan because their eflag is 0
		courseEventData = TrainingCoursesEventsWorkFlow.constructCourseEventByWFTraining(wfTrainingList.get(0));
	    else
		courseEventData = previousCourseEventsList.get(0);
	}

	if (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode())
	    if (wfTrainingList.get(0).getEmployeeId().equals(wfTrainingList.get(0).getReplacementEmployeeId()))
		throw new BusinessException("error_selfExchangeIsInvalid");

	List<TrainingTransactionData> transactionsList = new ArrayList<>();
	for (WFTrainingData wfTraining : wfTrainingList) {
	    if ((wfTraining.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() || wfTraining.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) && (trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_REPLACEMENT.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_APOLOGY.getCode()))
		if (wfTraining.getCourseEventId() == null)
		    throw new BusinessException("error_trainingCourseIsMandatory");

	    if (wfTraining.getTrainingTypeId().longValue() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode() || wfTraining.getTrainingTypeId().longValue() == TrainingTypesEnum.SCHOLARSHIP.getCode())
		transactionsList.add(constructTrainingTransactionForScholarshipAndStudyEnrollment(wfTraining, processId));
	    else
		transactionsList.add(constructTrainingTransaction(wfTraining, processId, courseEventData == null ? null : courseEventData.getId()));

	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode()) {
		TrainingEmployeesService.validateTrainingTransactions(Arrays.asList(constructTrainingTransaction(wfTraining, (processId == WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode()) ? WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REQUEST.getCode() : WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REQUEST.getCode(), courseEventData == null ? null : courseEventData.getId())), courseEventData,
			TrainingTransactionCategoryEnum.NOMINATION.getCode());
	    }
	}

	TrainingEmployeesService.validateTrainingTransactions(transactionsList, courseEventData, trainingTransactionCategory);

	Long transactionTypeId = null;

	if (processId == WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TRAINEE_COURSE_EVENT_CANCEL.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	} else if (processId == WFProcessesEnum.SCHOLARSHIP.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	} else if (processId == WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_EXTENSION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	} else if (processId == WFProcessesEnum.SCHOLARSHIP_TERMINATION.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TERMINATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	} else if (processId == WFProcessesEnum.SCHOLARSHIP_CANCEL.getCode()) {
	    transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_CANCELLATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	}

	if (transactionTypeId != null) {
	    for (int i = 0; i < transactionsList.size(); i++) {
		TrainingTransactionDetailData transactionDetailData = constructTrainingTransactionDetail(transactionsList.get(i).getId(), wfTrainingList.get(i), transactionsList.get(i).getEmployeeId(), null, null, null, transactionTypeId, wfTrainingList.get(i).getInternalCopies(), wfTrainingList.get(i).getExternalCopies());
		TrainingEmployeesService.validateTrainingTransactionDetail(transactionDetailData);
	    }
	}

	if (courseEventData != null && courseEventData.getId() == null) {
	    TrainingCoursesEventsService.validateCourseEvent(courseEventData, trainingTransactionCategory);
	}
    }

    private static void validateRunningWFTrainings(List<WFTrainingData> wfTrainingList) throws BusinessException {
	List<Long> empsIds = new ArrayList<Long>();
	for (int i = 0; i < wfTrainingList.size(); i++) {
	    empsIds.add(wfTrainingList.get(i).getEmployeeId());

	    if (wfTrainingList.get(i).getReplacementEmployeeId() != null)
		empsIds.add(wfTrainingList.get(i).getReplacementEmployeeId());
	}

	List<WFTrainingData> runningRequests = getRunningWFTrainingDataByEmployeesIds(empsIds.toArray(new Long[empsIds.size()]), wfTrainingList.get(0).getInstanceId() == null ? FlagsEnum.ALL.getCode() : wfTrainingList.get(0).getInstanceId());

	if (!runningRequests.isEmpty()) {
	    StringBuilder employeesNames = new StringBuilder();
	    for (int i = 0; i < runningRequests.size(); i++) {
		employeesNames.append(EmployeesService.getEmployeesByEmpsIds(new Long[] { runningRequests.get(i).getEmployeeId() }).get(0).getName()).append(',');
		if (runningRequests.get(i).getReplacementEmployeeId() != null)
		    employeesNames.append(EmployeesService.getEmployeesByEmpsIds(new Long[] { runningRequests.get(i).getReplacementEmployeeId() }).get(0).getName()).append(',');
	    }
	    employeesNames.deleteCharAt(employeesNames.length() - 1);
	    throw new BusinessException("error_employeeHasPendingTrainingWF", new String[] { employeesNames.toString() });
	}
    }

    public static void validateTrainingCourseWFBusinessRules(TrainingCourseData trainingCourseData, String originalCourseName, boolean validateDelete) throws BusinessException {
	if (trainingCourseData == null)
	    throw new BusinessException("error_transactionDataError");

	if (validateDelete || (trainingCourseData.getId() != null && !trainingCourseData.getName().equals(originalCourseName))) {
	    List<WFTrainingData> wfTrainingList = getWFTrainingDataByCourseId(trainingCourseData.getId());
	    if (wfTrainingList.size() > 0)
		throw new BusinessException(validateDelete ? "error_cannotEditOrDeleteTrainingCourse" : "error_cannotEditCourseName");
	}
    }

    public static void validateExternalPartyWFBusinessRules(TrainingExternalPartyData trainingExternalPartyData, String originalExternalPartyDesc, boolean validateDelete) throws BusinessException {
	if (trainingExternalPartyData == null)
	    throw new BusinessException("error_transactionDataError");

	if (validateDelete || (trainingExternalPartyData.getId() != null && !trainingExternalPartyData.getDescription().equals(originalExternalPartyDesc))) {
	    List<WFTrainingData> wfTrainingData = getWFTrainingDataByExternalPartyId(trainingExternalPartyData.getId());
	    if (wfTrainingData.size() != 0)
		throw new BusinessException(validateDelete ? "error_cannotDeleteExternalParty" : "error_cannotEditExternalPartyName");
	}
    }

    public static void validateGraduatioPlaceWFBusinessRules(GraduationPlaceData graduationPlace, String originalGraduationPlaceDesc) throws BusinessException {
	if (graduationPlace.getId() != null && !graduationPlace.getDescription().equals(originalGraduationPlaceDesc)) {
	    if (TrainingEmployeesWorkFlow.getWFTrainingDataByGraduationPlaceId(graduationPlace.getId()).size() > 0)
		throw new BusinessException("error_graduationPlaceIsUsedInTheSystem");
	}
    }

    public static void validateGraduatioPlaceDetailWFBusinessRules(GraduationPlaceDetailData graduationPlaceDetail, String originalGraduationPlaceDetailDesc, boolean validateDelete) throws BusinessException {
	if (validateDelete || (graduationPlaceDetail.getId() != null && !graduationPlaceDetail.getDescription().equals(originalGraduationPlaceDetailDesc))) {
	    if (getWFTrainingDataByGraduationPlaceDetailId(graduationPlaceDetail.getId()).size() > 0)
		throw new BusinessException("error_graduationPlaceIsUsedInTheSystem");
	}
    }

    public static void validateQualificationMajorSpecWFBusinessRules(QualificationMajorSpec qualMajorSpec) throws BusinessException {
	if (qualMajorSpec == null)
	    throw new BusinessException("error_transactionDataError");

	if (getWFTrainingDataByQualMajorSpecId(qualMajorSpec.getId()).size() > 0)
	    throw new BusinessException("error_courseQualIsUsedInTheSystem");
    }

    public static void validateQualificationMinorSpecWFBusinessRules(QualificationMinorSpecData qualMinorSpec) throws BusinessException {
	if (qualMinorSpec == null)
	    throw new BusinessException("error_transactionDataError");

	if (getWFTrainingDataByQualMinorSpecId(qualMinorSpec.getId()).size() > 0)
	    throw new BusinessException("error_courseQualIsUsedInTheSystem");
    }

    /*---------------------------Queries------------------------------*/

    public static List<Object> getWFTrainingTasks(Long assigneeId, String[] assigneeWfRoles) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLES", assigneeWfRoles);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_WFTRAINING_TASKS.getCode(), qParams);
	} catch (Exception e) {
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFTrainingData> getWFTrainingDataByCourseEventId(long courseEventId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), courseEventId);
    }

    public static List<WFTrainingData> getRunningWFTrainingDataForTrainingCourseEvent(long courseEventId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), WFInstanceStatusEnum.RUNNING.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), courseEventId);
    }

    public static List<WFTrainingData> getWFTrainingDataByInstanceId(long instanceId) throws BusinessException {
	return searchWFTrainingData(instanceId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    private static List<WFTrainingData> getWFTrainingDataByExternalPartyId(long externalPartyId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, externalPartyId, FlagsEnum.ALL.getCode());
    }

    private static List<WFTrainingData> getWFTrainingDataByCourseId(long courseId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseId, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<WFTrainingData> getRunningWFTrainingDataByEmployeesIds(Long[] employeesIds, long excludedInstanceId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), excludedInstanceId, WFInstanceStatusEnum.RUNNING.getCode(), FlagsEnum.ALL.getCode(), employeesIds, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    private static List<WFTrainingData> getWFTrainingDataByGraduationPlaceId(long graduationPlaceId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), graduationPlaceId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    private static List<WFTrainingData> getWFTrainingDataByGraduationPlaceDetailId(long graduationPlaceDetailId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), graduationPlaceDetailId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    private static List<WFTrainingData> getWFTrainingDataByQualMajorSpecId(long qualMajorSpecId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), qualMajorSpecId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    private static List<WFTrainingData> getWFTrainingDataByQualMinorSpecId(long qualMinorSpecId) throws BusinessException {
	return searchWFTrainingData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), qualMinorSpecId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    private static List<WFTrainingData> searchWFTrainingData(long instanceId, long qualMajorSpecId, long qualMinorSpecId, long graduationPlaceId, long graduationPlaceDetailId, long excludedInstanceId, int instanceStatus, long courseId, Long[] employeesIds, long externalPartyId, long courseEventId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_INSTANCE_ID", instanceId);
	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId);
	    qParams.put("P_EXTERNAL_PARTY_ID", externalPartyId);
	    qParams.put("P_COURSE_ID", courseId);
	    qParams.put("P_COURSE_EVENT_ID", courseEventId);
	    qParams.put("P_STATUS", instanceStatus);
	    qParams.put("P_GRADUATION_PLACE_ID", graduationPlaceId);
	    qParams.put("P_GRADUATION_PLACE_DETAIL_ID", graduationPlaceDetailId);
	    qParams.put("P_QUAL_MAJOR_SPEC_ID", qualMajorSpecId);
	    qParams.put("P_QUAL_MINOR_SPEC_ID", qualMinorSpecId);

	    if (employeesIds == null || employeesIds.length == 0) {
		qParams.put("P_EMPS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EMPS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_EMPS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EMPS_IDS", employeesIds);
	    }

	    List<WFTrainingData> result = DataAccess.executeNamedQuery(WFTrainingData.class, QueryNamesEnum.WF_GET_WFTRAINING.getCode(), qParams);
	    return result;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
