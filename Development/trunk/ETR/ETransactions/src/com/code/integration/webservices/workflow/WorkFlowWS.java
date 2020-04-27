package com.code.integration.webservices.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFInstanceData;
import com.code.dal.orm.workflow.WFProcess;
import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.enums.FlagsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.integration.responses.workflow.WSProcessesGroupsResponse;
import com.code.integration.responses.workflow.WSProcessesResponse;
import com.code.integration.responses.workflow.WSWFCountsResponse;
import com.code.integration.responses.workflow.WSWFDashboardItem;
import com.code.integration.responses.workflow.WSWFDashboardResponse;
import com.code.integration.responses.workflow.WSWFDelegationsResponse;
import com.code.integration.responses.workflow.WSWFInstanceTaskInfo;
import com.code.integration.responses.workflow.WSWFInstanceTasksResponse;
import com.code.integration.responses.workflow.WSWFInstancesResponse;
import com.code.integration.responses.workflow.WSWFTasksResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.DashBoardWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/security",
	portName = "WorkFlowWSHttpPort")
public class WorkFlowWS {

    private static int MAX_LIST_SIZE = 100;

    @WebMethod(operationName = "getWorkFlowCounts")
    @WebResult(name = "workflowCountsResponse")
    public WSWFCountsResponse getWorkFlowCounts(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSWFCountsResponse response = new WSWFCountsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setInboxCount(BaseWorkFlow.countWFTasks(employeeId, FlagsEnum.OFF.getCode()));
	    response.setNotificationsCount(BaseWorkFlow.countWFTasks(employeeId, FlagsEnum.ON.getCode()));
	    response.setOutboxCount(BaseWorkFlow.getWFInstancesUnderProcessingCount(employeeId));

	    ArrayList<Object> returnedCounts = (ArrayList<Object>) BaseWorkFlow.countWFDelegations(employeeId);
	    Object[] delegationsCounts = (Object[]) (returnedCounts.get(0));

	    response.setTotalDelegationsFromCount((Long) delegationsCounts[0]);
	    response.setTotalDelegationsToCount((Long) delegationsCounts[1]);
	    response.setPartialDelegationsFromCount((Long) delegationsCounts[2]);
	    response.setPartialDelegationsToCount((Long) delegationsCounts[3]);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    /****************************************** Instance Methods ****************************************************/

    @WebMethod(operationName = "getWFInstances")
    @WebResult(name = "wfInstancesResponse")
    public WSWFInstancesResponse getWFInstances(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId,
	    @WebParam(name = "processGroupId") long processGroupId, @WebParam(name = "processId") long processId) {

	WSWFInstancesResponse response = new WSWFInstancesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, requesterId, response))
	    return response;

	try {
	    List<WFInstanceData> wfInstances = BaseWorkFlow.searchWFInstancesData(requesterId, null, processGroupId, processId, true, false);
	    response.setWFInstances(wfInstances);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "closeProcess")
    @WebResult(name = "closeProcessResponse")
    public WSResponseBase closeProcess(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    WFTask task = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != task.getAssigneeId() || task.getAction() != null || !task.getAssigneeWfRole().equals(WFTaskRolesEnum.NOTIFICATION.getCode()))
		// TODO:To Be Discussed
		throw new BusinessException("error_integrationError");

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(task.getInstanceId());

	    BaseWorkFlow.closeWFInstanceByNotification(instance, task);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    /********************************************** Task Methods ****************************************************/

    @WebMethod(operationName = "getWFTasks")
    @WebResult(name = "wfTasksResponse")
    public WSWFTasksResponse getWFTasks(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "assigneeId") long assigneeId,
	    @WebParam(name = "processGroupId") long processGroupId, @WebParam(name = "processId") long processId) {

	return searchWFTasks(sessionId, assigneeId, processGroupId, processId, 1);
    }

    @WebMethod(operationName = "getWFNotifications")
    @WebResult(name = "wfNotificationsResponse")
    public WSWFTasksResponse getWFNotifications(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "assigneeId") long assigneeId,
	    @WebParam(name = "processGroupId") long processGroupId, @WebParam(name = "processId") long processId) {

	return searchWFTasks(sessionId, assigneeId, processGroupId, processId, 2);
    }

    private WSWFTasksResponse searchWFTasks(String sessionId, long assigneeId, long processGroupId, long processId, int taskRole) {

	WSWFTasksResponse response = new WSWFTasksResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, assigneeId, response))
	    return response;

	try {
	    boolean isNotifications = (taskRole == 2);
	    List<WFTaskData> wfTasks = BaseWorkFlow.searchWFTasksData(assigneeId, null, "", processGroupId, processId, true, taskRole, isNotifications ? true : false);

	    if (isNotifications)
		wfTasks = wfTasks.subList(0, Math.min(MAX_LIST_SIZE, wfTasks.size()));

	    response.setWFTasks(wfTasks);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getWFInstanceTasks")
    @WebResult(name = "wfInstanceTasksResponse")
    public WSWFInstanceTasksResponse getWFInstanceTasks(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "instanceId") long instanceId) {

	WSWFInstanceTasksResponse response = new WSWFInstanceTasksResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    List<WSWFInstanceTaskInfo> wFInstanceTasksInfo = new ArrayList<WSWFInstanceTaskInfo>();
	    WSWFInstanceTaskInfo wFInstanceTaskInfo = new WSWFInstanceTaskInfo();

	    WFInstanceData wfInstance = BaseWorkFlow.getWFInstanceDataById(instanceId);
	    wFInstanceTaskInfo.setEmployeeNameAndRank(wfInstance.getRequesterRankDesc() + "/ " + wfInstance.getRequesterName());
	    wFInstanceTaskInfo.setRequestHijriDateString(wfInstance.getHijriRequestDateString());

	    EmployeeData requester = EmployeesService.getEmployeeData(wfInstance.getRequesterId());
	    if (requester.getJobId() != null)
		wFInstanceTaskInfo.setEmployeeJobDescription(requester.getJobDesc());
	    else
		wFInstanceTaskInfo.setEmployeeStatus(requester.getStatusDesc());

	    wFInstanceTaskInfo.setTaskLevel("1");

	    wFInstanceTasksInfo.add(wFInstanceTaskInfo);

	    List<WFTaskData> wfTasks = BaseWorkFlow.getWFInstanceTasksData(instanceId);
	    for (WFTaskData wfTask : wfTasks) {
		wFInstanceTaskInfo = new WSWFInstanceTaskInfo();

		// Adjusting delegation information.
		if (wfTask.getDelegatingName() != null) {
		    EmployeeData assigneeEmp = EmployeesService.getEmployeeData(wfTask.getAssigneeId());
		    wfTask.setOriginalName(wfTask.getOriginalName() + " (" + BaseService.getMessage("label_delegateeName") + ": " + assigneeEmp.getEmpId() + " - " + assigneeEmp.getRankDesc() + "/ " + assigneeEmp.getName() + " ) ");
		}
		wFInstanceTaskInfo.setEmployeeNameAndRank(wfTask.getOriginalRankDesc() + "/ " + wfTask.getOriginalName());

		EmployeeData originalEmployee = EmployeesService.getEmployeeData(wfTask.getOriginalId());
		if (originalEmployee.getJobId() != null)
		    wFInstanceTaskInfo.setEmployeeJobDescription(originalEmployee.getJobDesc());
		else
		    wFInstanceTaskInfo.setEmployeeStatus(originalEmployee.getStatusDesc());

		wFInstanceTaskInfo.setTaskHijriAssignDateString(wfTask.getHijriAssignDateString());

		if (wfTask.getAction() != null) {
		    wFInstanceTaskInfo.setIsDone(true);
		    wFInstanceTaskInfo.setTaskAction(wfTask.getAction());
		    wFInstanceTaskInfo.setTaskHijriActionDateString(wfTask.getHijriActionDateString());
		}

		wFInstanceTaskInfo.setTaskLevel(wfTask.getLevel());

		if ((wfTask.getAssigneeWfRole().equals(WFTaskRolesEnum.NOTIFICATION.getCode()) && wfTask.getAction() != null)
			|| (wfTasks.get(wfTasks.size() - 1).getTaskId().equals(wfTask.getTaskId()) && wfTask.getAction() != null)) {
		    wFInstanceTaskInfo.setIsEnd(true);
		}

		wFInstanceTasksInfo.add(wFInstanceTaskInfo);
	    }

	    response.setWFInstanceTasksInfo(wFInstanceTasksInfo);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "delegateWFTask")
    @WebResult(name = "delegateWFTaskResponse")
    public WSResponseBase delegateWFTask(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "wfTaskId") long wfTaskId, @WebParam(name = "delegateId") long delegateId) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    List<Long> tasksIds = new ArrayList<Long>();
	    tasksIds.add(wfTaskId);
	    BaseWorkFlow.delegateWFTasks(tasksIds, employeeId, delegateId, employeeId);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    /********************************************** Groups - Processes Methods **************************************/

    @WebMethod(operationName = "getProcessesGroups")
    @WebResult(name = "processesGroupsResponse")
    public WSProcessesGroupsResponse getProcessesGroups(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSProcessesGroupsResponse response = new WSProcessesGroupsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    List<WFProcessGroup> processesGroups = BaseWorkFlow.getWFProcessesGroups();
	    if (processesGroups != null && processesGroups.size() > 0) {
		WFProcessGroup allGroups = new WFProcessGroup();
		allGroups.setProcessGroupId((long) FlagsEnum.OFF.getCode());
		allGroups.setProcessGroupName(BaseService.getMessage("label_searchByModule"));
		processesGroups.add(0, allGroups);
	    }
	    response.setProcessesGroups(processesGroups);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getProcesses")
    @WebResult(name = "processesResponse")
    public WSProcessesResponse getProcesses(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "processGroupId") long processGroupId, @WebParam(name = "processName") String processName) {

	WSProcessesResponse response = new WSProcessesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setProcesses(BaseWorkFlow.getWFGroupProcesses(processGroupId, processName, null));

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getAcceptanceDashBoardData")
    @WebResult(name = "acceptanceDashBoardDataResponse")
    public WSWFDashboardResponse getAcceptanceDashBoardData(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {
	return getDashBoardData(sessionId, employeeId, 1);
    }

    @WebMethod(operationName = "getApprovalDashBoardData")
    @WebResult(name = "approvalDashBoardDataResponse")
    public WSWFDashboardResponse getApprovalDashBoardData(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {
	return getDashBoardData(sessionId, employeeId, 2);
    }

    private WSWFDashboardResponse getDashBoardData(String sessionId, long employeeId, int actionTypeFlag) {

	WSWFDashboardResponse response = new WSWFDashboardResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    List<Object> dashBoardData = DashBoardWorkFlow.getDashBoardData(actionTypeFlag, employeeId);
	    List<WSWFDashboardItem> dashboardItems = new ArrayList<WSWFDashboardItem>();

	    for (Object dashBoardDataObject : dashBoardData) {
		Object[] dashBoardDataObjectInfo = (Object[]) dashBoardDataObject;
		WSWFDashboardItem dashboardItem = new WSWFDashboardItem();

		dashboardItem.setProcessGroupId((long) dashBoardDataObjectInfo[0]);
		dashboardItem.setProcessGroupName((String) dashBoardDataObjectInfo[1]);
		dashboardItem.setTasksCount((long) dashBoardDataObjectInfo[2]);

		dashboardItems.add(dashboardItem);
	    }
	    response.setDashBoardData(dashboardItems);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    /********************************************** Delegation Methods **********************************************/

    @WebMethod(operationName = "getWorkFlowDelegationsTo")
    @WebResult(name = "workflowDelegationsToResponse")
    public WSWFDelegationsResponse getWorkFlowDelegationsTo(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSWFDelegationsResponse response = new WSWFDelegationsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setTotalDelegations(BaseWorkFlow.getWFTotalDelegationTo(employeeId));
	    response.setPartialDelegations(BaseWorkFlow.getWFPartialDelegationTo(employeeId));

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getWorkFlowDelegationsFrom")
    @WebResult(name = "workflowDelegationsFromResponse")
    public WSWFDelegationsResponse getWorkFlowDelegationsFrom(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSWFDelegationsResponse response = new WSWFDelegationsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setTotalDelegations(BaseWorkFlow.getWFTotalDelegationsFrom(employeeId));
	    response.setPartialDelegations(BaseWorkFlow.getWFPartialDelegationsFrom(employeeId));

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "saveWorkFlowDelegation")
    @WebResult(name = "saveWorkFlowDelegationResponse")
    public WSResponseBase saveWorkFlowDelegation(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "delegateId") long delegateId, @WebParam(name = "unitId") Long unitId, @WebParam(name = "selectedProcessesIds") String selectedProcessesIds) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    String unitHkey = null;
	    if (unitId != null)
		unitHkey = UnitsService.getUnitById(unitId).gethKey();

	    // total delegation
	    if (selectedProcessesIds == null || selectedProcessesIds.isEmpty()) {
		BaseWorkFlow.saveWFDelegation(employeeId, delegateId, null, unitId, unitHkey, BaseWorkFlow.getWFTotalDelegationsFrom(employeeId), BaseWorkFlow.getWFPartialDelegationsFrom(employeeId));
		response.setMessage(BaseService.getMessage("notify_successOperation"));
	    } else {
		// partial delegation(s)

		// get all processes
		List<WFProcess> processesList = BaseWorkFlow.getWFGroupProcesses(FlagsEnum.OFF.getCode(), null, null);
		Map<Long, String> processesMap = new HashMap<Long, String>();
		for (WFProcess wfProcess : processesList) {
		    processesMap.put(wfProcess.getProcessId(), wfProcess.getProcessName());
		}

		String[] processesIDs = selectedProcessesIds.split(",");
		if (processesIDs != null && processesIDs.length > 0) {
		    int unsuccessfulProcessesCount = 0;
		    String unsuccessfulProcessesIfAny = "";
		    String comma = "";
		    for (int i = 0; i < processesIDs.length; i++) {
			try {
			    BaseWorkFlow.saveWFDelegation(employeeId, delegateId, Long.parseLong(processesIDs[i]), unitId, unitHkey, BaseWorkFlow.getWFTotalDelegationsFrom(employeeId), BaseWorkFlow.getWFPartialDelegationsFrom(employeeId));
			} catch (BusinessException e) {
			    unsuccessfulProcessesIfAny += comma + BaseService.getMessage(e.getMessage()) + " - " + processesMap.get(Long.parseLong(processesIDs[i]));
			    unsuccessfulProcessesCount++;
			    comma = ", \n ";
			}
		    }
		    if (unsuccessfulProcessesCount > 0) {
			response.setStatus(WSResponseStatusEnum.FAILED.getCode());
			response.setMessage(unsuccessfulProcessesIfAny);
		    } else {
			response.setMessage(BaseService.getMessage("notify_successOperation"));
		    }
		}
	    }
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "deleteWorkFlowDelegation")
    @WebResult(name = "deleteWorkFlowDelegationResponse")
    public WSResponseBase deleteWorkFlowDelegation(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "delegationId") long delegationId) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    BaseWorkFlow.deleteWFDelegation(delegationId, employeeId);

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