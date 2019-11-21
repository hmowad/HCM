package com.code.integration.webservicesclients.eservices;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.code.enums.eservices.HTTPStatusCodeEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.eservices.workflow.WFProcess;
import com.code.integration.parameters.eservices.workflow.WFProcessStepData;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.Log4jService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WFProcessStepsClient extends BaseClient {

    private static Client client;
    private static String serverUrl;

    public static void init() {
	client = ClientBuilder.newBuilder().build();
	serverUrl = ETRConfigurationService.getEServicesURL() + "/wfProcessSteps";
    }

    public static List<WFProcess> getAllWfProcess() throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: wfProcessSteps/getAllWfProcess");

	Response response = client.target(serverUrl).path("/getAllWfProcess").request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: wfProcessSteps/getAllWfProcess");

	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    return getEntityList(WFProcess.class, response.readEntity(String.class));
	}
    }

    public static List<WFProcess> getWfProcess(String name) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: wfProcessSteps/getWfProcess");

	Response response = client.target(serverUrl).path("/getWfProcess").queryParam("name", name).request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: wfProcessSteps/getWfProcess");

	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    return getEntityList(WFProcess.class, response.readEntity(String.class));
	}
    }

    public static List<WFProcessStepData> getWfProcessDetails(Long wfTransactionId) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: wfProcessSteps/getWfProcessDetails");

	Response response = client.target(serverUrl).path("/getWfProcessDetails").queryParam("wfTransactionId", wfTransactionId).request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: wfProcessSteps/getWfProcessDetails");

	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.readEntity(String.class));
	} else {
	    return getEntityList(WFProcessStepData.class, response.readEntity(String.class));
	}
    }

    public static WFProcess saveWfProcess(WFProcess wfProcess) throws BusinessException {
	init();
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
	String wfProcessString = gson.toJson(wfProcess);

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: wfProcessSteps/saveWfProcess");

	Response response = client.target(serverUrl).path("/saveWfProcess").request()
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
		.post(Entity.entity(wfProcessString, MediaType.APPLICATION_JSON + "; charset=utf-8"));

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: wfProcessSteps/saveWfProcess");
	
	if (response.getStatus() == HTTPStatusCodeEnum.CONFLICT.getCode()) {
	    throw new BusinessException(response.getEntity().toString());
	}
	return (WFProcess) response.getEntity();
    }

    public static void enableWfProcess(WFProcess wfProcess) throws BusinessException {
	init();
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
	String wfProcessString = gson.toJson(wfProcess);

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: wfProcessSteps/enableWfProcess");

	Response response = client.target(serverUrl).path("/enableWfProcess").request()
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
		.post(Entity.entity(wfProcessString, MediaType.APPLICATION_JSON + "; charset=utf-8"));

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: wfProcessSteps/enableWfProcess");
	
	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.getEntity().toString());
	}
    }

    public static void saveOrUpdateWfProcessStep(List<WFProcessStepData> wfProcessStepDataList)
	    throws BusinessException {

	init();
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String wfProcessStepDataListString = gson.toJson(wfProcessStepDataList);

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: wfProcessSteps/saveOrUpdate");

	Response response = client.target(serverUrl).path("/saveOrUpdate").request()
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
		.post(Entity.entity(wfProcessStepDataListString, MediaType.APPLICATION_JSON + "; charset=utf-8"));

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: wfProcessSteps/saveOrUpdate");
	
	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(response.getEntity().toString());
	}
    }
}
