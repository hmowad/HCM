package com.code.integration.webservices.workflow.hcm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.FundSourceEnum;
import com.code.enums.GradesEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.integration.responses.util.WSAttachmentsResponse;
import com.code.integration.responses.workflow.hcm.WSWFTrainingResponse;
import com.code.integration.responses.workflow.hcm.WSWFTrainingTaskInfo;
import com.code.integration.responses.workflow.hcm.WSWFTrainingsTasksResponse;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.util.AttachmentsService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/trainings",
	portName = "TrainingsWorkFlowWSHttpPort")
public class TrainingsWorkFlowWS {

    @WebMethod(operationName = "initMorningTrainingClaimRequest")
    @WebResult(name = "initMorningTrainingClaimRequestResponse")
    public WSResponseBase initMorningTrainingClaimRequest(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId, @WebParam(name = "courseId") long courseId, @WebParam(name = "externalPartyId") long externalPartyId, @WebParam(name = "startDateString") String startDateString,
	    @WebParam(name = "endDateString") String endDateString, @WebParam(name = "qualificationGrade") int qualificationGrade, @WebParam(name = "attachments") String attachments) {
	return initClaimRequests(WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode(), sessionId, requesterId, courseId, externalPartyId, startDateString, endDateString, qualificationGrade, null, attachments);
    }

    @WebMethod(operationName = "initEveningTrainingClaimRequest")
    @WebResult(name = "initEveningTrainingClaimRequestResponse")
    public WSResponseBase initEveningTrainingClaimRequest(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId, @WebParam(name = "courseId") long courseId, @WebParam(name = "externalPartyId") long externalPartyId, @WebParam(name = "startDateString") String startDateString,
	    @WebParam(name = "endDateString") String endDateString, @WebParam(name = "qualificationGrade") int qualificationGrade, @WebParam(name = "fundSource") int fundSource, @WebParam(name = "attachments") String attachments) {

	return initClaimRequests(WFProcessesEnum.EVENING_TRAINING_CLAIM_REQUEST.getCode(), sessionId, requesterId, courseId, externalPartyId, startDateString, endDateString, qualificationGrade, fundSource, attachments);
    }

    private WSResponseBase initClaimRequests(long processId, String sessionId, long requesterId, long courseId, long externalPartyId, String startDateString, String endDateString, int qualificationGrade, Integer fundSource, String attachments) {
	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, requesterId, response))
	    return response;

	try {
	    long trainingTypeId = processId == WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode() ? TrainingTypesEnum.MORNING_COURSE.getCode() : TrainingTypesEnum.EVENING_COURSE.getCode();
	    String screenName = processId == WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode() ? "MorningTrainingClaimRequest" : "EveningTrainingClaimRequest";

	    if (trainingTypeId == TrainingTypesEnum.EVENING_COURSE.getCode() && fundSource.longValue() != FundSourceEnum.COST_ON_BORDER_GUARD.getCode() && fundSource.longValue() != FundSourceEnum.COST_ON_EMPLOYEE.getCode())
		throw new BusinessException("error_transactionDataError");

	    if (qualificationGrade != GradesEnum.EXCELLENT.getCode() && qualificationGrade != GradesEnum.VERY_GOOD.getCode() && qualificationGrade != GradesEnum.GOOD.getCode() && qualificationGrade != GradesEnum.ACCEPTED.getCode() && qualificationGrade != GradesEnum.NOT_AVAILABLE.getCode()) {
		throw new BusinessException("error_transactionDataError");
	    }

	    EmployeeData requester = EmployeesService.getEmployeeData(requesterId);
	    WFTrainingData wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(requesterId, requester.getCategoryId(), trainingTypeId, processId);
	    wfTraining.setCourseId(courseId);
	    wfTraining.setExternalPartyId(externalPartyId);
	    wfTraining.setStartDateString(startDateString);
	    wfTraining.setEndDateString(endDateString);
	    wfTraining.setQualificationGrade(qualificationGrade);
	    wfTraining.setFundSource(fundSource);
	    wfTraining.setAttachments(attachments);

	    List<WFTrainingData> wfTrainingList = new ArrayList<WFTrainingData>();
	    wfTrainingList.add(wfTraining);

	    String taskUrl = "/Trainings/" + screenName + ".jsf?mode=" + requester.getCategoryId() + "&rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();
	    TrainingEmployeesWorkFlow.initWFTraining(requester, wfTrainingList, processId, taskUrl);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    if (e instanceof BusinessException)
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    else {
		response.setMessage(BaseService.getMessage("error_general"));
		e.printStackTrace();
	    }
	}
	return response;
    }

    @WebMethod(operationName = "doTrainingClaimsDM")
    @WebResult(name = "wfTrainingClaimsDMResponse")
    public WSResponseBase doTrainingClaimsDM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId, @WebParam(name = "isApproved") boolean isApproved, @WebParam(name = "refuseReasons") String refuseReasons) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    BaseWorkFlow.validateWFTaskRefuseReasonsAndNotes(isApproved ? WFActionFlagsEnum.APPROVE.getCode() : WFActionFlagsEnum.REJECT.getCode(), refuseReasons, null);

	    WFTask dmTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != dmTask.getAssigneeId() || dmTask.getAction() != null || !dmTask.getAssigneeWfRole().equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()))
		throw new BusinessException("error_integrationError");

	    if (!isApproved)
		dmTask.setRefuseReasons(refuseReasons);

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(dmTask.getInstanceId());
	    EmployeeData requester = EmployeesService.getEmployeeData(instance.getRequesterId());
	    TrainingEmployeesWorkFlow.doWFTrainingDM(requester, instance, dmTask, isApproved);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}

	return response;
    }

    @WebMethod(operationName = "getWFTrainings")
    @WebResult(name = "wfTrainingsResponse")
    public WSWFTrainingResponse getWFTrainings(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId) {

	WSWFTrainingResponse response = new WSWFTrainingResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    WFTask wfTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != wfTask.getAssigneeId())
		throw new BusinessException("error_integrationError");

	    List<WFTrainingData> wfTrainingDataList = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(wfTask.getInstanceId());

	    if (wfTrainingDataList.size() == 0)
		throw new BusinessException("error_integrationError");

	    response.setWfTrainingsData(wfTrainingDataList);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getAttachmentsURL")
    @WebResult(name = "attachmentsURLResponse")
    public WSAttachmentsResponse getAttachmentsURL(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "attachmentId") String attachmentId, @WebParam(name = "addAttachmentFlag") boolean addAttachmentFlag) {

	WSAttachmentsResponse response = new WSAttachmentsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    String url = "";
	    if (addAttachmentFlag) { // Add

		if (attachmentId == null || attachmentId.isEmpty())
		    attachmentId = AttachmentsService.getNextAttachmentsId();

		url = AttachmentsService.getAttachmentsAddURL(attachmentId, employeeId);
	    } else { // View
		url = AttachmentsService.getAttachmentsViewURL(attachmentId, employeeId);
	    }

	    response.setAttachmentId(attachmentId);
	    response.setUrl(url);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "doNominationDM")
    @WebResult(name = "wfNominationDMResponse")
    public WSResponseBase doNominationDM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId, @WebParam(name = "isApproved") boolean isApproved, @WebParam(name = "refuseReasons") String refuseReasons) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    BaseWorkFlow.validateWFTaskRefuseReasonsAndNotes(isApproved ? WFActionFlagsEnum.APPROVE.getCode() : WFActionFlagsEnum.REJECT.getCode(), refuseReasons, null);

	    WFTask dmTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != dmTask.getAssigneeId() || dmTask.getAction() != null || !dmTask.getAssigneeWfRole().equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()))
		throw new BusinessException("error_integrationError");

	    if (!isApproved)
		dmTask.setRefuseReasons(refuseReasons);

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(dmTask.getInstanceId());
	    EmployeeData requester = EmployeesService.getEmployeeData(instance.getRequesterId());

	    List<WFTrainingData> wfTrainingList = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(instance.getInstanceId());
	    Long selectedCourseEventId = wfTrainingList.get(0).getCourseEventId();
	    TrainingEmployeesWorkFlow.doNominationDM(requester, wfTrainingList, selectedCourseEventId == null ? null : TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId), instance, dmTask, isApproved);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}

	return response;
    }

    /************************************************* Collective Actions ******************************************************/

    /*------------------------------------------------ Actions ------------------------------------------------------*/

    @WebMethod(operationName = "doTrainingsCollectiveDM")
    @WebResult(name = "trainingsCollectiveDMResponse")
    public WSResponseBase doTrainingsCollectiveDM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "tasksIds") String tasksIds) {

	return doTrainingsCollectiveAction(sessionId, employeeId, tasksIds, 1);
    }

    @WebMethod(operationName = "doTrainingsCollectiveSM")
    @WebResult(name = "trainingsCollectiveSMResponse")
    public WSResponseBase doTrainingsCollectiveSM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "tasksIds") String tasksIds) {

	return doTrainingsCollectiveAction(sessionId, employeeId, tasksIds, 2);
    }

    private WSResponseBase doTrainingsCollectiveAction(String sessionId, long employeeId, String tasksIds, int actionTypeFlag) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    tasksIds = "," + tasksIds + ",";
	    ArrayList<Object> tasksAndTrainingsObjects = (ArrayList<Object>) TrainingEmployeesWorkFlow.getWFTrainingTasks(employeeId, actionTypeFlag == 1 ? new String[] { WFTaskRolesEnum.DIRECT_MANAGER.getCode() } : new String[] { WFTaskRolesEnum.SIGN_MANAGER.getCode(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), WFTaskRolesEnum.EXTRA_SECONDARY_SIGN_MANAGER.getCode() });
	    Set<Long> taskIdsSet = new HashSet<Long>();
	    String unsuccessfulTaskIdsIfAny = "";
	    String comma = "";
	    int unsuccessfulTasksCount = 0;
	    for (Object trainingTaskObject : tasksAndTrainingsObjects) {
		try {
		    if (!taskIdsSet.contains(((WFTask) ((Object[]) trainingTaskObject)[1]).getTaskId()) && tasksIds.contains("," + ((WFTask) ((Object[]) trainingTaskObject)[1]).getTaskId() + ",")) {
			taskIdsSet.add(((WFTask) ((Object[]) trainingTaskObject)[1]).getTaskId());
			TrainingEmployeesWorkFlow.doTrainingsCollectiveAction(trainingTaskObject, tasksAndTrainingsObjects, actionTypeFlag);
		    }
		} catch (BusinessException e) {
		    unsuccessfulTaskIdsIfAny += comma + ((WFTask) ((Object[]) trainingTaskObject)[1]).getTaskId();
		    unsuccessfulTasksCount++;
		    comma = ", ";
		}
	    }

	    if (taskIdsSet.isEmpty())
		throw new BusinessException("error_integrationError");
	    if (unsuccessfulTasksCount > 0)
		throw new BusinessException("error_thereAreErrorsForNOfTasks", new Object[] { unsuccessfulTasksCount, unsuccessfulTaskIdsIfAny });

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    if (e instanceof BusinessException) {
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    } else {
		response.setMessage(BaseService.getMessage("error_integrationError"));
		e.printStackTrace();
	    }
	}
	return response;
    }

    /*------------------------------------------------ Queries ------------------------------------------------------*/

    @WebMethod(operationName = "getWFTrainingsDMTasks")
    @WebResult(name = "wfTrainingsDMTasksResponse")
    public WSWFTrainingsTasksResponse getWFTrainingsDMTasks(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	return getWFTrainingsTasks(sessionId, employeeId, 1);
    }

    @WebMethod(operationName = "getWFTrainingsSMTasks")
    @WebResult(name = "wfTrainingsSMTasksResponse")
    public WSWFTrainingsTasksResponse getWFTrainingsSMTasks(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	return getWFTrainingsTasks(sessionId, employeeId, 2);
    }

    private WSWFTrainingsTasksResponse getWFTrainingsTasks(String sessionId, long employeeId, int actionTypeFlag) {

	WSWFTrainingsTasksResponse response = new WSWFTrainingsTasksResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    // object[0] - WFTrainingData
	    // object[1] - WFTask
	    // object[2] - WFInstance
	    // object[3] - processName
	    // object[4] - requester
	    // object[5] - delegatingName
	    ArrayList<Object> tasksAndTrainingsObjects = (ArrayList<Object>) TrainingEmployeesWorkFlow.getWFTrainingTasks(employeeId, actionTypeFlag == 1 ? new String[] { WFTaskRolesEnum.DIRECT_MANAGER.getCode() } : new String[] { WFTaskRolesEnum.SIGN_MANAGER.getCode(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), WFTaskRolesEnum.EXTRA_SECONDARY_SIGN_MANAGER.getCode() });
	    // handle collective process
	    ArrayList<Object> trainingsTasks = new ArrayList<>();
	    for (int i = 0; i < tasksAndTrainingsObjects.size(); i++) {
		WFTask currentTask = (WFTask) (((Object[]) tasksAndTrainingsObjects.get(i))[1]);
		WFTask previousTask = i == 0 ? null : (WFTask) (((Object[]) tasksAndTrainingsObjects.get(i - 1))[1]);
		if (previousTask == null || currentTask.getTaskId().longValue() != previousTask.getTaskId().longValue()) {
		    trainingsTasks.add(tasksAndTrainingsObjects.get(i));
		}
	    }

	    List<WSWFTrainingTaskInfo> wfTrainingsTasksList = new ArrayList<WSWFTrainingTaskInfo>();

	    for (Object TrainingTaskObject : trainingsTasks) {
		Object[] TrainingTaskObjectArray = (Object[]) TrainingTaskObject;
		WSWFTrainingTaskInfo trainingTaskInfo = new WSWFTrainingTaskInfo();

		WFTrainingData wfTraining = ((WFTrainingData) TrainingTaskObjectArray[0]);
		trainingTaskInfo.setTrainingTypeId(wfTraining.getTrainingTypeId());
		trainingTaskInfo.setTrainingUnitName(wfTraining.getTrainingUnitName());
		trainingTaskInfo.setExternalPartyDesc(wfTraining.getExternalPartyDesc());
		trainingTaskInfo.setGraduationPlaceDetailDesc(wfTraining.getGraduationPlaceDetailDesc());
		trainingTaskInfo.setCourseName(wfTraining.getCourseEventId() == null ? wfTraining.getCourseName() : wfTraining.getEventCourseName());
		trainingTaskInfo.setStudySubject(wfTraining.getStudySubject());

		trainingTaskInfo.setTaskId(((WFTask) TrainingTaskObjectArray[1]).getTaskId());
		trainingTaskInfo.setProcessId(((WFInstance) TrainingTaskObjectArray[2]).getProcessId());
		trainingTaskInfo.setProcessName((String) TrainingTaskObjectArray[3]);
		trainingTaskInfo.setRequesterName(((EmployeeData) TrainingTaskObjectArray[4]).getName());
		trainingTaskInfo.setDelegatingName((String) TrainingTaskObjectArray[5]);

		wfTrainingsTasksList.add(trainingTaskInfo);
	    }

	    response.setWfTrainingsTasksList(wfTrainingsTasksList);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

}
