package com.code.integration.webservices.workflow.hcm;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.missions.WFMissionData;
import com.code.dal.orm.workflow.hcm.missions.WFMissionDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.integration.responses.workflow.hcm.WSWFMissionResponse;
import com.code.integration.responses.workflow.hcm.WSWFMissionTaskInfo;
import com.code.integration.responses.workflow.hcm.WSWFMissionsTasksResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.hcm.MissionsWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/missions",
	portName = "MissionsWorkFlowWSHttpPort")
public class MissionsWorkFlowWS {

    @WebMethod(operationName = "getWfMissionAndWfMissionDetails")
    @WebResult(name = "wfMissionAndWFMissionDetailsResponse")
    public WSWFMissionResponse getWFMissionAndWFMissionDetails(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId) {

	WSWFMissionResponse response = new WSWFMissionResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {

	    WFTask wfTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != wfTask.getAssigneeId())
		throw new BusinessException("error_general");

	    WFMissionData wfMission = MissionsWorkFlow.getWFMissionByInstanceId(wfTask.getInstanceId());
	    List<WFMissionDetailData> wfMissionDetailDataList = MissionsWorkFlow.getWFMissionDetailsByInstanceId(wfTask.getInstanceId());

	    if (wfMission == null)
		throw new BusinessException("error_general");

	    response.setWfMission(wfMission);
	    response.setWfMissionDetails(wfMissionDetailDataList);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}

	return response;
    }

    @WebMethod(operationName = "doMissionDM")
    @WebResult(name = "wfMissionDMResponse")
    public WSResponseBase doMissionDM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId, @WebParam(name = "isApproved") boolean isApproved, @WebParam(name = "refuseReasons") String refuseReasons) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    BaseWorkFlow.validateWFTaskRefuseReasonsAndNotes(isApproved ? WFActionFlagsEnum.APPROVE.getCode() : WFActionFlagsEnum.REJECT.getCode(), refuseReasons, null);

	    WFTask dmTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != dmTask.getAssigneeId() || dmTask.getAction() != null || !dmTask.getAssigneeWfRole().equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()))
		throw new BusinessException("error_general");

	    if (!isApproved)
		dmTask.setRefuseReasons(refuseReasons);

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(dmTask.getInstanceId());
	    EmployeeData requester = EmployeesService.getEmployeeData(instance.getRequesterId());

	    WFMissionData wfMission = MissionsWorkFlow.getWFMissionByInstanceId(instance.getInstanceId());
	    List<WFMissionDetailData> wfMissionDetailDataList = MissionsWorkFlow.getWFMissionDetailsByInstanceId(instance.getInstanceId());

	    MissionsWorkFlow.doMissionDM(requester, instance, wfMission, wfMissionDetailDataList, dmTask, isApproved);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());

	    if (e instanceof BusinessException)
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    else
		response.setMessage(BaseService.getMessage("error_general"));

	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}

	return response;
    }

    @WebMethod(operationName = "doMissionSM")
    @WebResult(name = "wfMissionSMResponse")
    public WSResponseBase doMissionSM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId, @WebParam(name = "approvalFlag") int approvalFlag,
	    @WebParam(name = "notes") String notes, @WebParam(name = "refuseReasons") String refuseReasons) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    BaseWorkFlow.validateWFTaskRefuseReasonsAndNotes(approvalFlag, refuseReasons, notes);

	    WFTask smTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != smTask.getAssigneeId() || smTask.getAction() != null || !smTask.getAssigneeWfRole().equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()))
		throw new BusinessException("error_general");

	    if (notes != null && !notes.trim().isEmpty())
		smTask.setNotes(notes);

	    if (approvalFlag == WFActionFlagsEnum.REJECT.getCode())
		smTask.setRefuseReasons(refuseReasons);

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(smTask.getInstanceId());
	    EmployeeData requester = EmployeesService.getEmployeeData(instance.getRequesterId());

	    WFMissionData wfMission = MissionsWorkFlow.getWFMissionByInstanceId(instance.getInstanceId());
	    List<WFMissionDetailData> wfMissionDetailDataList = MissionsWorkFlow.getWFMissionDetailsByInstanceId(instance.getInstanceId());

	    MissionsWorkFlow.doMissionSM(requester, instance, wfMission, wfMissionDetailDataList, smTask, approvalFlag);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}

	return response;
    }
    /*------------------------------------------------ Actions ------------------------------------------------------*/

    @WebMethod(operationName = "doMissionsCollectiveDM")
    @WebResult(name = "missionsCollectiveDMResponse")
    public WSResponseBase doMissionsCollectiveDM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "tasksIds") String tasksIds) {

	return doMissionsCollectiveAction(sessionId, employeeId, tasksIds, 1);
    }

    @WebMethod(operationName = "doMissionsCollectiveSM")
    @WebResult(name = "missionsCollectiveSMResponse")
    public WSResponseBase doMissionsCollectiveSM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "tasksIds") String tasksIds) {

	return doMissionsCollectiveAction(sessionId, employeeId, tasksIds, 2);
    }

    private WSResponseBase doMissionsCollectiveAction(String sessionId, long employeeId, String tasksIds, int actionTypeFlag) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;
	try {
	    String unsuccessfulTaskIdsIfAny = "";
	    String comma = "";
	    int unsuccessfulTasksCount = 0;

	    String[] taskId = tasksIds.split(",");
	    for (int i = 0; i < taskId.length; i++) {
		WFTask task = null;
		try {
		    task = BaseWorkFlow.getWFTaskById(Long.parseLong(taskId[i]));
		    if (task.getAssigneeId() != employeeId || task.getAction() != null || !task.getAssigneeWfRole().equals((actionTypeFlag == 2) ? WFTaskRolesEnum.SIGN_MANAGER.getCode() : WFTaskRolesEnum.DIRECT_MANAGER.getCode()))
			continue;
		    WFInstance instance = BaseWorkFlow.getWFInstanceById(task.getInstanceId());
		    EmployeeData requester = EmployeesService.getEmployeeData(employeeId);
		    List<WFMissionDetailData> wfMissionDetailDataList = MissionsWorkFlow.getWFMissionDetailsByInstanceId(instance.getInstanceId());
		    WFMissionData missionRequest = MissionsWorkFlow.getWFMissionByInstanceId(instance.getInstanceId());
		    if (wfMissionDetailDataList == null)
			wfMissionDetailDataList = new ArrayList<WFMissionDetailData>();
		    if (actionTypeFlag == 1) {
			MissionsWorkFlow.doMissionDM(requester, instance, missionRequest, wfMissionDetailDataList, task, true);
		    } else if (actionTypeFlag == 2) {
			MissionsWorkFlow.doMissionSM(requester, instance, missionRequest, wfMissionDetailDataList, task, FlagsEnum.ON.getCode());
		    }

		} catch (BusinessException e) {
		    unsuccessfulTaskIdsIfAny += comma + task.getTaskId();
		    unsuccessfulTasksCount++;
		    comma = ", ";
		}
	    }
	    if (unsuccessfulTasksCount > 0) {
		response.setStatus(WSResponseStatusEnum.FAILED.getCode());
		response.setMessage(BaseService.getParameterizedMessage("error_thereAreErrorsForNOfTasks", new Object[] { unsuccessfulTasksCount, unsuccessfulTaskIdsIfAny }));
	    } else {
		response.setStatus(WSResponseStatusEnum.SUCCESS.getCode());
		response.setMessage(BaseService.getMessage("notify_successOperation"));
	    }
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    /*------------------------------------------------ Queries ------------------------------------------------------*/

    @WebMethod(operationName = "getWFMissionsDMTasks")
    @WebResult(name = "wfMissionsDMTasksResponse")
    public WSWFMissionsTasksResponse getWFMissionsDMTasks(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	return getWFMissionsTasks(sessionId, employeeId, 1);
    }

    @WebMethod(operationName = "getWFMissionsSMTasks")
    @WebResult(name = "wfMissionsSMTasksResponse")
    public WSWFMissionsTasksResponse getWFMissionsSMTasks(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	return getWFMissionsTasks(sessionId, employeeId, 2);
    }

    private WSWFMissionsTasksResponse getWFMissionsTasks(String sessionId, long employeeId, int actionTypeFlag) {

	WSWFMissionsTasksResponse response = new WSWFMissionsTasksResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    // object[0] - WFMissionData
	    // object[1] - WFTask
	    // object[2] - WFInstance
	    // object[3] - processName
	    // object[4] - requester
	    // object[5] - delegatingName
	    ArrayList<Object> tasksAndMissionsObjects = (ArrayList<Object>) MissionsWorkFlow.getWFMissionsTasks(employeeId, actionTypeFlag == 1 ? WFTaskRolesEnum.DIRECT_MANAGER.getCode() : WFTaskRolesEnum.SIGN_MANAGER.getCode());
	    // handle collective process

	    List<WSWFMissionTaskInfo> wfMissionsTasksList = new ArrayList<WSWFMissionTaskInfo>();

	    for (Object MissionTaskObject : tasksAndMissionsObjects) {
		Object[] MissionTaskObjectArray = (Object[]) MissionTaskObject;
		WSWFMissionTaskInfo missionTaskInfo = new WSWFMissionTaskInfo();

		WFMissionData wfMission = ((WFMissionData) MissionTaskObjectArray[0]);
		missionTaskInfo.setLocationFlag(wfMission.getLocationFlag());
		missionTaskInfo.setLocation(wfMission.getLocation());
		missionTaskInfo.setDestination(wfMission.getDestination());
		missionTaskInfo.setStartDateString(wfMission.getStartDateString());
		missionTaskInfo.setPeriod(wfMission.getPeriod());

		missionTaskInfo.setTaskId(((WFTask) MissionTaskObjectArray[1]).getTaskId());
		missionTaskInfo.setProcessId(((WFInstance) MissionTaskObjectArray[2]).getProcessId());
		missionTaskInfo.setProcessName((String) MissionTaskObjectArray[3]);
		missionTaskInfo.setRequesterName(((EmployeeData) MissionTaskObjectArray[4]).getName());
		missionTaskInfo.setDelegatingName((String) MissionTaskObjectArray[5]);

		wfMissionsTasksList.add(missionTaskInfo);
	    }

	    response.setMissions(wfMissionsTasksList);
	    response.setStatus(WSResponseStatusEnum.SUCCESS.getCode());
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

}