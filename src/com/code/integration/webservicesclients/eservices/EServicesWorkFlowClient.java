package com.code.integration.webservicesclients.eservices;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.code.enums.FlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.eservices.HTTPStatusCodeEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.eservices.workflow.WFInstance;
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
	Response response = client.target(serverUrl).path("wfInstance/" + instanceId.toString()).request().get();
	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    return response.readEntity(WFInstance.class);
	}
    }

    public static void closeWFInstancesByNotification(List<WFTask> tasks) throws BusinessException {
	init();
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	for (WFTask task : tasks) {
	    Response response = client.target(serverUrl).path("wfBase/wfInstance/closeByNotification").request()
		    .post(Entity.entity(gson.toJson(task), MediaType.APPLICATION_JSON + ";"));
	    if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
		throw new BusinessException(response.readEntity(String.class));
	    }
	}
    }

    /***************************************************
     * Tasks Methods
     **********************************************************/
    public static WFTask getWFTaskById(Long taskId) throws BusinessException {
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start get wftask by id");
	init();
	Response response = client.target(serverUrl).path("wfTask/" + taskId.toString()).request()
		.accept(MediaType.APPLICATION_JSON + ";charset=utf-8").get();
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end get wftask by id");
	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    return response.readEntity(WFTask.class);
	}
    }

    public static WFTaskData getWFTaskDataById(Long taskId) throws BusinessException {
	init();
	Response response = client.target(serverUrl).path("wfTaskData/" + taskId.toString()).request().get();
	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    return response.readEntity(WFTaskData.class);
	}
    }

    public static List<WFTaskData> getWFInstanceCompletedTasksData(Long instanceId, Long taskId)
	    throws BusinessException {
	init();
	Response response = client.target(serverUrl).path("wfTasks/completedTasks/" + instanceId.toString())
		.queryParam("taskId", taskId).request().get();
	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    return getEntityList(WFTaskData.class, response.readEntity(String.class));
	}
    }

    public static List<WFTaskData> searchWFTasksData(Long assigneeId, Integer taskRole, String taskOwnerName, String subject, Integer processGroupId, Long processId, Boolean isRunning, Boolean desc) throws BusinessException {
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start searchWFTasksData");
	init();
	if (taskRole == 2) {
	    processGroupId = null;
	    processId = WFProcessesEnum.ESERVICES_NOTIFICATIONS.getCode();
	}
	Response response = client.target(serverUrl).path("wfTask/").queryParam("assigneeId", assigneeId.toString())
		.queryParam("onlyNotifications", taskRole == 0 ? false : true)
		.queryParam("taskOwnerName", taskOwnerName).queryParam("subject", subject)
		.queryParam("processGroupId", (processGroupId == null || processGroupId == 0) ? FlagsEnum.ALL.getCode() + "" : processGroupId.toString())
		.queryParam("processId", (processId == null || processId == 0) ? FlagsEnum.ALL.getCode() + "" : processId.toString())
		.queryParam("isRunning", isRunning == null ? null : Boolean.toString(isRunning))
		.queryParam("isDESC", desc == null ? null : Boolean.toString(desc)).request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end searchWFTasksData");

	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    return getEntityList(WFTaskData.class, response.readEntity(String.class));
	}
    }

    public static Long countWFTasks(Long assigneeId, Integer notificationFlag) throws BusinessException {
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start countWFTasks");
	init();
	Response response = client.target(serverUrl).path("countTasks/")
				.queryParam("assigneeId", assigneeId.toString())
				.queryParam("notificationFlag", notificationFlag.toString())
				.request().get();
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end countWFTasks");

	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    String countString = response.readEntity(String.class);
	    return Long.parseLong(countString);
	}
    }

}
