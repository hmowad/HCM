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
		return client.target(ETRConfigurationService.getEServicesURL()).path(ServiceURLEnum.NEXT_SEQUENCE_VALUE.getCode()).queryParam("entityName", entityName).request().get(String.class);
	}

	public static ExtensionRequestTransaction getExtensionRequestTransactionByInstanceId(Long instanceId) throws BusinessException {
		init();
		return client.target(serverUrl).path("/extension")
				.queryParam("instanceId", instanceId)
				.request()
				.accept(MediaType.APPLICATION_JSON + ";charset=utf-8")
				.get(ExtensionRequestTransaction.class);
	}

	public static ExtensionRequestTransaction initTransaction(Long processId, String taskUrl, ExtensionRequestTransaction extensionRequestTransaction) throws BusinessException {
		init();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
		String extensionRequestTransactionString = gson.toJson(extensionRequestTransaction);

		Response response = client.target(serverUrl).path("/initTransaction")
				.queryParam("processId", processId.toString())
				.queryParam("taskUrl", taskUrl) 
				.request()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(Entity.entity(extensionRequestTransactionString, MediaType.APPLICATION_JSON + ";charset=utf-8"));

		if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
			throw new BusinessException(response.readEntity(String.class));
		}
		return response.readEntity(ExtensionRequestTransaction.class);
	}

	public static ExtensionRequestTransaction validateReExtensionTransaction(Long empId) throws BusinessException {
		init();
		Response response = client.target(serverUrl).path("/validateReExtension").queryParam("empId", empId).request().accept(MediaType.APPLICATION_JSON + ";charset=utf-8").get();
		if(response.getStatus() != HTTPStatusCodeEnum.OK.getCode()){
			throw new BusinessException(response.readEntity(String.class));
		}else{
			return response.readEntity(ExtensionRequestTransaction.class);
		}	
	}
}
