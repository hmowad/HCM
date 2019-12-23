package com.code.integration.webservicesclients.eservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.code.enums.FlagsEnum;
import com.code.enums.eservices.HTTPStatusCodeEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.eservices.workflow.WFInstance;
import com.code.integration.parameters.eservices.workflow.WFProcessCycle;
import com.code.integration.parameters.eservices.workflow.WFTask;
import com.code.integration.parameters.eservices.workflow.WFTaskData;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.Log4jService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EServicesWorkFlowClient extends BaseClient {

    private static Client client;
    private static String serverUrl;

    public static void init() {
	client = ClientBuilder.newBuilder().build();
	serverUrl = ETRConfigurationService.getEServicesURL() + "/wfBase";
    }

    /**************************************************
     * Instances Methods
     * 
     * @throws BusinessException
     ******************************************************/
    public static WFInstance getWFInstanceById(Long instanceId) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: wfBase/wfInstance");

	Response response = client.target(serverUrl).path("wfInstance/" + instanceId.toString()).request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: wfBase/wfInstance");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return response.readEntity(WFInstance.class);
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    public static void closeWFInstancesByNotification(List<WFTask> tasks) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/wfInstance/closeByNotification");

	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	for (WFTask task : tasks) {
	    Response response = client.target(serverUrl).path("wfInstance/closeByNotification").request()
		    .post(Entity.entity(gson.toJson(task), MediaType.APPLICATION_JSON + ";"));
	    if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
		throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	    }
	    Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	}

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/wfInstance/closeByNotification");
    }

    /***************************************************
     * Tasks Methods
     **********************************************************/
    public static WFTask getWFTaskById(Long taskId) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/wfTask/{taskId}");

	Response response = client.target(serverUrl).path("wfTaskData/" + taskId.toString()).request()
		.accept(MediaType.APPLICATION_JSON + ";charset=utf-8").get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/wfTask/{taskId}");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return response.readEntity(WFTask.class);
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    public static WFTaskData getWFTaskDataById(Long taskId) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/wfTaskData");

	Response response = client.target(serverUrl).path("wfTaskData/" + taskId.toString()).request()
		.accept(MediaType.APPLICATION_JSON + ";charset=utf-8").get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/wfTaskData");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return response.readEntity(WFTaskData.class);
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    public static List<WFTaskData> getWFInstanceCompletedTasksData(Long instanceId, Long taskId) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/wfTasks/completedTasks/{instanceId}");

	Response response = client.target(serverUrl).path("wfTasks/completedTasks/" + instanceId.toString())
		.queryParam("taskId", taskId).request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/wfTasks/completedTasks/{instanceId}");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return getEntityList(WFTaskData.class, response.readEntity(String.class));
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    public static List<WFTaskData> searchWFTasksData(Long assigneeId, Integer taskRole, String taskOwnerName, String subject, Integer processGroupId, Long processId, Boolean isRunning, Boolean desc) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/wfTask");

	Response response = client.target(serverUrl).path("wfTask/").queryParam("assigneeId", assigneeId.toString())
		.queryParam("onlyNotifications", taskRole == 0 ? false : true)
		.queryParam("taskOwnerName", taskOwnerName).queryParam("subject", subject)
		.queryParam("processGroupId", (processGroupId == null || processGroupId == 0) ? FlagsEnum.ALL.getCode() + "" : processGroupId.toString())
		.queryParam("processId", (processId == null || processId == 0) ? FlagsEnum.ALL.getCode() + "" : processId.toString())
		.queryParam("isRunning", isRunning == null ? null : Boolean.toString(isRunning))
		.queryParam("isDESC", desc == null ? null : Boolean.toString(desc))
		.request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/wfTask");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return getEntityList(WFTaskData.class, response.readEntity(String.class));
	} else {
	    String errorMsg = getExceptionMessage(response.getStatus(), response.readEntity(String.class));
	    if(errorMsg.equals("error_noData"))
		return new ArrayList<WFTaskData>();
	    throw new BusinessException(errorMsg);
	}
    }

    public static Long countWFTasks(Long assigneeId, Integer notificationFlag) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/countTasks");

	Response response = client.target(serverUrl).path("countTasks/")
		.queryParam("assigneeId", assigneeId.toString())
		.queryParam("notificationFlag", notificationFlag.toString())
		.request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/countTasks");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    String countString = response.readEntity(String.class);
	    return Long.parseLong(countString);
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    public static void doNotifyTasks(List<WFTask> notificationTasks) throws BusinessException {
	init();
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String notificationTasksString = gson.toJson(notificationTasks);

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/wfInbox/doNotifyTasks");

	Response response = client.target(serverUrl).path("wfInbox/doNotifyTasks").request()
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
		.post(Entity.entity(notificationTasksString, MediaType.APPLICATION_JSON + ";"));

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/wfInbox/doNotifyTasks");

	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    /***************************************************
     * Process Cycles
     **********************************************************/
    public static List<WFProcessCycle> getAllWFProcessesCycle(Long processId, String processName, Long jobCategory,
	    Long employeeRegion, Long rankId, Long approvalStoppingPt, Boolean enabled) throws BusinessException {
	init();
	
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/wfProcessCycles");

	Response response = client.target(serverUrl).path("wfProcessCycles/")
		.queryParam("processId", processId == null ? null : processId.toString())
		.queryParam("processName", processName)
		.queryParam("jobCategory", jobCategory == null ? null : jobCategory.toString())
		.queryParam("employeeRegion", employeeRegion == null ? null : employeeRegion.toString())
		.queryParam("rankId", rankId == null ? null : rankId.toString())
		.queryParam("approvalStoppingPt", approvalStoppingPt == null ? null : approvalStoppingPt.toString())
		.queryParam("enabled", enabled == null ? null : enabled.toString())
		.request().accept(MediaType.APPLICATION_JSON + ";charset=utf-8").get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/wfProcessCycles");
	
	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
	return getEntityList(WFProcessCycle.class, response.readEntity(String.class));
    }

    public static Long saveOrUpdateWFProcessCycles(WFProcessCycle wfProcessCycle) throws BusinessException {
	init();
	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling: service wfBase/updateWFProcessCycles");

	Response response = client.target(serverUrl).path("updateWFProcessCycles/")
		.request()
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
		.post(Entity.entity(gson.toJson(wfProcessCycle), MediaType.APPLICATION_JSON + ";charset=utf-8"));

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling: service wfBase/updateWFProcessCycles");
	
	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
	return Long.parseLong(response.readEntity(String.class));
    }

}
