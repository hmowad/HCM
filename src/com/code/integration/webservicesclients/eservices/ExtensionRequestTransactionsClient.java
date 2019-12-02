package com.code.integration.webservicesclients.eservices;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.code.enums.eservices.HTTPStatusCodeEnum;
import com.code.enums.eservices.ServiceURLEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.eservices.transactions.ExtensionRequestTransaction;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.Log4jService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExtensionRequestTransactionsClient extends BaseClient {

    private static Client client;
    private static String serverUrl;

    public static void init() {
	client = ClientBuilder.newBuilder().build();
	serverUrl = ETRConfigurationService.getEServicesURL() + "/extension-request";
    }

    public static String getNextSequenceValue(String entityName) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: /common/getNextSequenceValue");

	Response response = client.target(ETRConfigurationService.getEServicesURL())
		.path(ServiceURLEnum.NEXT_SEQUENCE_VALUE.getCode())
		.queryParam("entityName", entityName).request().get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: /common/getNextSequenceValue");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return response.readEntity(String.class);
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    public static ExtensionRequestTransaction getExtensionRequestTransactionByInstanceId(Long instanceId) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: extension-request/extension");

	Response response = client.target(serverUrl).path("/extension")
		.queryParam("instanceId", instanceId)
		.request()
		.accept(MediaType.APPLICATION_JSON + ";charset=utf-8").get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: extension-request/extension");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return response.readEntity(ExtensionRequestTransaction.class);
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    public static ExtensionRequestTransaction initTransaction(Long processId, String taskUrl, ExtensionRequestTransaction extensionRequestTransaction) throws BusinessException {
	init();
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
	String extensionRequestTransactionString = gson.toJson(extensionRequestTransaction);

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: extension-request/initTransaction");

	Response response = client.target(serverUrl).path("/initTransaction")
		.queryParam("processId", processId.toString())
		.queryParam("taskUrl", taskUrl)
		.request()
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
		.post(Entity.entity(extensionRequestTransactionString, MediaType.APPLICATION_JSON + ";charset=utf-8"));

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: extension-request/initTransaction");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return response.readEntity(ExtensionRequestTransaction.class);
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }

    public static ExtensionRequestTransaction validateReExtensionTransaction(Long empId) throws BusinessException {
	init();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: extension-request/validateReExtension");

	Response response = client.target(serverUrl).path("/validateReExtension").queryParam("empId", empId).request().accept(MediaType.APPLICATION_JSON + ";charset=utf-8").get();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: extension-request/validateReExtension");

	if (response.getStatus() == HTTPStatusCodeEnum.OK.getCode()) {
	    return response.readEntity(ExtensionRequestTransaction.class);
	} else {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}
    }
}
