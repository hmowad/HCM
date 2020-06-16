package com.code.services.workflow.hcm;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseDecision;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourse;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
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
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

/**
 * WorkFlow Service to control the flow of training course processes.
 */
public class TrainingCoursesWorkFlow extends BaseWorkFlow {

    /**
     * Private constructor to prevent instantiation
     */
    private TrainingCoursesWorkFlow() {
    }

    /*********************************************************** WFTrainingCourseName ***********************************************************/
    /*--------------------------- work flow steps ------------------------------*/
    public static void initWFTrainingCoursesNames(EmployeeData requester, WFTrainingCourseData wfTrainingCourseNameData, String attachments, long processId, String taskUrl) throws BusinessException {
	if (wfTrainingCourseNameData.getCourseName() == null || wfTrainingCourseNameData.getCourseName().length() == 0)
	    throw new BusinessException("error_courseNameMandatory");

	List<TrainingCourseData> result = TrainingCoursesService.getTrainingCoursesByExactNameAndExcludedId(wfTrainingCourseNameData.getCourseType(), wfTrainingCourseNameData.getCourseName(), FlagsEnum.ALL.getCode());
	if (result.size() > 0)
	    throw new BusinessException("error_duplicateCourseName");

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, Arrays.asList(requester.getEmpId()), session);

	    long originalId;
	    WFPosition position = null;
	    if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
		position = getWFPosition(WFPositionsEnum.PROGRAMS_CURRICULUM_UNIT_MANAGER.getCode(), requester.getPhysicalRegionId());
		originalId = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();
		addWFTask(instance.getInstanceId(), getDelegate(originalId, processId, requester.getEmpId()), originalId, curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
	    } else {
		position = getWFPosition(WFPositionsEnum.REGION_TRAINING_UNIT_MANAGER.getCode(), requester.getPhysicalRegionId());
		originalId = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();
		addWFTask(instance.getInstanceId(), getDelegate(originalId, processId, requester.getEmpId()), originalId, curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
	    }

	    wfTrainingCourseNameData.setInstanceId(instance.getInstanceId());
	    DataAccess.addEntity(wfTrainingCourseNameData.getWfTrainingCourse(), session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    wfTrainingCourseNameData.setInstanceId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doWFTrainingCoursesNamesSM(EmployeeData requester, WFInstance instance, WFTrainingCourseData wfTrainingCourseNameData, WFTask smTask, int approvalFlag) throws BusinessException {
	if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode() && TrainingCoursesEventsWorkFlow.isEmployeeAuthorizedForPosition(WFPositionsEnum.PROGRAMS_CURRICULUM_UNIT_MANAGER.getCode(), EmployeesService.getEmployeeData(smTask.getOriginalId()).getPhysicalUnitId(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
	    TrainingCoursesService.validateTrainingCourse(constructTrainingCourse(wfTrainingCourseNameData), wfTrainingCourseNameData.getCourseName());
	}

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(smTask.getOriginalId());

		if (nextManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
		    TrainingCoursesService.validateTrainingCourse(constructTrainingCourse(wfTrainingCourseNameData), wfTrainingCourseNameData.getCourseName());
		    closeTrainingCoursesNamesWorkFlow(requester, instance, wfTrainingCourseNameData, smTask, session);
		} else {
		    if (TrainingCoursesEventsWorkFlow.isEmployeeAuthorizedForPosition(WFPositionsEnum.PROGRAMS_CURRICULUM_UNIT_MANAGER.getCode(), EmployeesService.getEmployeeData(smTask.getOriginalId()).getPhysicalUnitId(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
			DataAccess.updateEntity(wfTrainingCourseNameData.getWfTrainingCourse(), session);
		    }

		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
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

    public static void doWFTrainingCoursesNamesSSM(EmployeeData requester, WFInstance instance, WFTrainingCourseData wfTrainingCourseNameData, WFTask smTask, int approvalFlag) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(smTask.getOriginalId());
		EmployeeData currentManager = EmployeesService.getEmployeeData(smTask.getOriginalId());
		long originalId;
		WFPosition position = null;
		if (currentManager.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()) {
		    position = getWFPosition(WFPositionsEnum.PROGRAMS_CURRICULUM_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		    originalId = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();
		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), requester.getEmpId()), originalId, smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		} else {

		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), smTask.getLevel(), session);
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

    /*--------------------------- Operations ------------------------------*/
    private static void closeTrainingCoursesNamesWorkFlow(EmployeeData requester, WFInstance instance, WFTrainingCourseData wfTrainingCourseNameData, WFTask smTask, CustomSession session) throws BusinessException {
	try {
	    closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), null, session);

	    TrainingCoursesService.addTrainingCourse(constructTrainingCourse(wfTrainingCourseNameData), session);

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static TrainingCourseData constructTrainingCourse(WFTrainingCourseData wfTrainingCourseNameData) {
	TrainingCourseData trainingCourseData = new TrainingCourseData();
	trainingCourseData.setName(wfTrainingCourseNameData.getCourseName());
	trainingCourseData.setQualificationMinorSpecId(wfTrainingCourseNameData.getQualificationMinorSpecId());
	trainingCourseData.setType(wfTrainingCourseNameData.getCourseType());
	return trainingCourseData;
    }

    /*--------------------------- Queries ------------------------------*/
    public static WFTrainingCourseData getWFTrainingCourseDataByInstanceId(long instanceId) throws BusinessException {
	return searchWFTrainingCoursesData(instanceId).get(0);
    }

    private static List<WFTrainingCourseData> searchWFTrainingCoursesData(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_INSTANCE_ID", instanceId);

	    return DataAccess.executeNamedQuery(WFTrainingCourseData.class, QueryNamesEnum.WF_SEARCH_WF_TRAINING_COURSE_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*********************************************************** WFTrainingCourseSyllabusAttachments ***********************************************************/
    /*--------------------------- work flow steps ------------------------------*/
    public static void initWFTrainingCourseSyllabusAttachments(EmployeeData requester, WFTrainingCourseData wfTrainingCourseSyllabusAttachment, Long preparatorEmployeeId, String attachments, long processId, String taskUrl) throws BusinessException {
	if (processId == WFProcessesEnum.MILITARY_TRAINING_COURSE_SYLLABUS_APPROVE.getCode())
	    TrainingCoursesService.validateCourseSyllabusAttachment(wfTrainingCourseSyllabusAttachment.getCourseId(), wfTrainingCourseSyllabusAttachment.getContentType(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_APPROVE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId());
	else if (processId == WFProcessesEnum.MILITARY_TRAINING_COURSE_SYLLABUS_CANCEL.getCode())
	    TrainingCoursesService.validateCourseSyllabusAttachment(wfTrainingCourseSyllabusAttachment.getCourseId(), wfTrainingCourseSyllabusAttachment.getContentType(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId());

	if (processId == WFProcessesEnum.MILITARY_TRAINING_COURSE_SYLLABUS_APPROVE.getCode() && preparatorEmployeeId == null)
	    throw new BusinessException("error_preparatorChoiceMandatory");

	validateWFTrainingCourseSyllabusAttachmentsRunning(wfTrainingCourseSyllabusAttachment.getCourseId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, Arrays.asList(requester.getEmpId()), session);

	    if (instance.getProcessId() == WFProcessesEnum.MILITARY_TRAINING_COURSE_SYLLABUS_APPROVE.getCode()) {

		addWFTask(instance.getInstanceId(), getDelegate(preparatorEmployeeId, processId, requester.getEmpId()), preparatorEmployeeId, curDate, curHijriDate, taskUrl, WFTaskRolesEnum.PREPARATOR.getCode(), "1", session);

	    } else if (instance.getProcessId() == WFProcessesEnum.MILITARY_TRAINING_COURSE_SYLLABUS_CANCEL.getCode()) {

		WFPosition position = getWFPosition(WFPositionsEnum.PROGRAMS_CURRICULUM_COMMITTEE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		Long programCurriculumCommitteePresidentId = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();
		addWFTask(instance.getInstanceId(), getDelegate(programCurriculumCommitteePresidentId, processId, requester.getEmpId()), programCurriculumCommitteePresidentId, curDate, curHijriDate, taskUrl, WFTaskRolesEnum.COMMITTEE_PRESIDENT.getCode(), "1", session);
	    }
	    wfTrainingCourseSyllabusAttachment.setInstanceId(instance.getInstanceId());
	    DataAccess.addEntity(wfTrainingCourseSyllabusAttachment.getWfTrainingCourse(), session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    wfTrainingCourseSyllabusAttachment.setInstanceId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doWFCourseSyllabusAttachmentsPreparation(EmployeeData requester, WFInstance instance, WFTrainingCourseData wfTrainingCourseSyllabusAttachment, String attachments, WFTask prpTask) throws BusinessException {
	if (wfTrainingCourseSyllabusAttachment.getSyllabusAttachments() == null)
	    throw new BusinessException("error_courseSyllabusAttachmentMandatory");

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    Long committeeSecretaryId = instance.getRequesterId();
	    completeWFTask(prpTask, WFTaskActionsEnum.PREPARE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(committeeSecretaryId, instance.getProcessId(), requester.getEmpId()), committeeSecretaryId, prpTask.getTaskUrl(), WFTaskRolesEnum.COMMITTEE_SECRETARY.getCode(), prpTask.getLevel(), session);
	    DataAccess.updateEntity(wfTrainingCourseSyllabusAttachment.getWfTrainingCourse(), session);
	    updateWFInstanceAttachments(instance, attachments, session);
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

    public static void doWFCourseSyllabusAttachmentsCS(EmployeeData requester, WFInstance instance, WFTrainingCourseData wfTrainingCourseSyllabusAttachment, String attachments, WFTask csTask, int approvalFlag) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		WFPosition position = getWFPosition(WFPositionsEnum.PROGRAMS_CURRICULUM_COMMITTEE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		EmployeeData programCurriculumUnitManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
		completeWFTask(csTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(programCurriculumUnitManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), programCurriculumUnitManager.getEmpId(), csTask.getTaskUrl(), WFTaskRolesEnum.COMMITTEE_PRESIDENT.getCode(), csTask.getLevel(), session);
		DataAccess.updateEntity(wfTrainingCourseSyllabusAttachment.getWfTrainingCourse(), session);

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_TO_PREPARATOR.getCode()) {

		Long preparatorId = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.PREPARATOR.getCode()).get(0).getOriginalId();
		completeWFTask(csTask, WFTaskActionsEnum.RETURN_TO_PREPARATOR.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(preparatorId, instance.getProcessId(), requester.getEmpId()), preparatorId, csTask.getTaskUrl(), WFTaskRolesEnum.PREPARATOR.getCode(), csTask.getLevel(), session);

	    } else {

		closeWFInstanceByAction(requester.getEmpId(), instance, csTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }
	    updateWFInstanceAttachments(instance, attachments, session);
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

    public static void doWFCourseSyllabusAttachmentsCP(EmployeeData requester, WFInstance instance, WFTrainingCourseData wfTrainingCourseSyllabusAttachment, String attachments, WFTask cpTask, int approvalFlag, List<Long> commeitteeMembersIds) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		Long managerId = EmployeesService.getEmployeeDirectManager(cpTask.getOriginalId()).getEmpId();

		completeWFTask(cpTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(managerId, instance.getProcessId(), requester.getEmpId()), managerId, cpTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), cpTask.getLevel(), session);

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_TO_COMMITTEE_SECRETARY.getCode()) {
		Long secretaryId = instance.getRequesterId();
		completeWFTask(cpTask, WFTaskActionsEnum.RETURN_TO_COMMITTEE_SECRETARY.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(secretaryId, instance.getProcessId(), requester.getEmpId()), secretaryId, cpTask.getTaskUrl(), WFTaskRolesEnum.COMMITTEE_SECRETARY.getCode(), cpTask.getLevel(), session);

	    } else if (approvalFlag == WFActionFlagsEnum.REJECT.getCode()) {

		closeWFInstanceByAction(requester.getEmpId(), instance, cpTask, WFTaskActionsEnum.REJECT.getCode(), null, session);

	    } else {

		if (commeitteeMembersIds == null || commeitteeMembersIds.isEmpty())
		    throw new BusinessException("error_commeitteeMembersIds");

		List<EmployeeData> commeitteeMembers = EmployeesService.getEmployeesByEmpsIds(commeitteeMembersIds.toArray(new Long[commeitteeMembersIds.size()]));
		if (commeitteeMembers == null || commeitteeMembers.isEmpty())
		    throw new BusinessException("error_transactionDataError");

		int subLevel = commeitteeMembers.size() == 1 ? 0 : 1;
		for (EmployeeData commeitteeMember : commeitteeMembers)// LEVEL
		    completeWFTask(cpTask, WFTaskActionsEnum.SEND_FOR_ADVISE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(commeitteeMember.getEmpId(), instance.getProcessId(), requester.getEmpId()), commeitteeMember.getEmpId(), cpTask.getTaskUrl(), WFTaskRolesEnum.COMMITTEE_MEMBER.getCode(), cpTask.getLevel() + (subLevel == 0 ? "" : ("." + (subLevel++))), session);

	    }
	    updateWFInstanceAttachments(instance, attachments, session);
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

    public static void doWFCourseSyllabusAttachmentsCM(EmployeeData requester, WFInstance instance, WFTrainingCourseData wfTrainingCourseSyllabusAttachment, String attachments, WFTask cmTask, int approvalFlag) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    Long committePresidentId = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.COMMITTEE_PRESIDENT.getCode()).get(0).getOriginalId();
	    boolean allCMActionsDone = true;
	    List<WFTask> committeMemberEmpsTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.COMMITTEE_MEMBER.getCode());
	    for (WFTask wfTask : committeMemberEmpsTasks) {
		if (wfTask.getAction() == null && !wfTask.getTaskId().equals(cmTask.getTaskId())) {
		    allCMActionsDone = false;
		    break;
		}
	    }

	    if (allCMActionsDone) {
		if (approvalFlag == WFActionFlagsEnum.RECOMMEND_APPROVE.getCode()) {
		    completeWFTask(cmTask, WFTaskActionsEnum.RECOMMEND_APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(committePresidentId, instance.getProcessId(), requester.getEmpId()), committePresidentId, cmTask.getTaskUrl(), WFTaskRolesEnum.COMMITTEE_PRESIDENT.getCode(), cmTask.getLevel(), session);
		} else {
		    completeWFTask(cmTask, WFTaskActionsEnum.RECOMMEND_REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(committePresidentId, instance.getProcessId(), requester.getEmpId()), committePresidentId, cmTask.getTaskUrl(), WFTaskRolesEnum.COMMITTEE_PRESIDENT.getCode(), cmTask.getLevel(), session);
		}

	    } else {
		if (approvalFlag == WFActionFlagsEnum.RECOMMEND_APPROVE.getCode()) {
		    setWFTaskAction(cmTask, WFTaskActionsEnum.RECOMMEND_APPROVE.getCode(), curDate, curHijriDate, session);
		} else {
		    setWFTaskAction(cmTask, WFTaskActionsEnum.RECOMMEND_REVIEW.getCode(), curDate, curHijriDate, session);
		}
	    }
	    updateWFInstanceAttachments(instance, attachments, session);
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

    public static void doWFCourseSyllabusAttachmentsSM(EmployeeData requester, WFInstance instance, WFTrainingCourseData wfTrainingCourseSyllabusAttachment, String attachments, WFTask smTask, int approvalFlag) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData currentManager = EmployeesService.getEmployeeData(smTask.getOriginalId());
		EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(smTask.getOriginalId());

		if (currentManager.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
		    closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), null, session);
		    TrainingCoursesService.handleTrainingCourseDecision(constructTrainingCourseDecision(wfTrainingCourseSyllabusAttachment, instance.getProcessId(), smTask.getOriginalId(), smTask.getOriginalId()), getWFProcess(instance.getProcessId()).getProcessName(), session);
		} else {
		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		}
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_TO_COMMITTEE_PRESIDENT.getCode()) {

		Long committePresidentId = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.COMMITTEE_PRESIDENT.getCode()).get(0).getOriginalId();
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_TO_COMMITTEE_PRESIDENT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(committePresidentId, instance.getProcessId(), requester.getEmpId()), committePresidentId, smTask.getTaskUrl(), WFTaskRolesEnum.COMMITTEE_PRESIDENT.getCode(), smTask.getLevel(), session);

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_TO_COMMITTEE_SECRETARY.getCode()) {

		Long secretaryId = instance.getRequesterId();
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_TO_COMMITTEE_SECRETARY.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(secretaryId, instance.getProcessId(), requester.getEmpId()), secretaryId, smTask.getTaskUrl(), WFTaskRolesEnum.COMMITTEE_SECRETARY.getCode(), smTask.getLevel(), session);

	    } else if (approvalFlag == WFActionFlagsEnum.REJECT.getCode()) {

		closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.REJECT.getCode(), null, session);

	    }
	    updateWFInstanceAttachments(instance, attachments, session);
	    session.commitTransaction();
	} catch (

	BusinessException e) {
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

    /*--------------------------- Operations ------------------------------*/
    public static void validateWFTrainingCourseSyllabusAttachmentsRunning(Long courseId) throws BusinessException {
	List<WFTrainingCourse> WFTrainingCourse = getRunningWTrainingCourses(courseId);
	if (!WFTrainingCourse.isEmpty())
	    throw new BusinessException("error_runningWTrainingCourses");

    }

    private static TrainingCourseDecision constructTrainingCourseDecision(WFTrainingCourseData wfTrainingCourseData, long processId, long decisionApprovedId, long originalDecisionApprovedId) throws BusinessException {
	try {
	    TrainingCourseDecision trainingCourseDecision = new TrainingCourseDecision();
	    trainingCourseDecision.setCourseId(wfTrainingCourseData.getCourseId());
	    trainingCourseDecision.setTransactionCourseName(TrainingCoursesService.getTrainingCoursesById(wfTrainingCourseData.getCourseId()).getName());
	    trainingCourseDecision.setContentType(wfTrainingCourseData.getContentType());
	    trainingCourseDecision.setSyllabusAttachments(wfTrainingCourseData.getSyllabusAttachments());
	    trainingCourseDecision.setDecisionApprovedId(decisionApprovedId);
	    trainingCourseDecision.setOriginalDecisionApprovedId(originalDecisionApprovedId);
	    long transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(processId == WFProcessesEnum.MILITARY_TRAINING_COURSE_SYLLABUS_APPROVE.getCode() ? TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_APPROVE_DECISION.getCode() : TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	    trainingCourseDecision.setTransactionTypeId(transactionTypeId);

	    return trainingCourseDecision;
	} catch (BusinessException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*--------------------------- Queries ------------------------------*/
    private static List<WFTrainingCourse> getRunningWTrainingCourses(long courseId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_COURSE_ID", courseId);
	    return DataAccess.executeNamedQuery(WFTrainingCourse.class, QueryNamesEnum.WF_GET_RUNNING_WF_TRAINING_COURSES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
