package com.code.integration.webservicesclients.eservices;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.code.enums.HTTPStatusCodeEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.extensionrequest.WFProcess;
import com.code.integration.parameters.extensionrequest.WFProcessStepData;
import com.code.services.config.ETRConfigurationService;
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
		return (List<WFProcess>) getEntityList(WFProcess.class, client.target(serverUrl).path("/getAllWfProcess").request().get(String.class));
	}

	public static List<WFProcess> getWfProcess(String name) throws BusinessException {
		init();
		return getEntityList(WFProcess.class,
				client.target(serverUrl).path("/getWfProcess").queryParam("name", name).request().get(String.class));
	}

	public static List<WFProcessStepData> getWfProcessDetails(Long wfTransactionId) throws BusinessException {
		init();
		return getEntityList(WFProcessStepData.class, client.target(serverUrl).path("/getWfProcessDetails")
				.queryParam("wfTransactionId", wfTransactionId).request().get(String.class));
	}

	public static WFProcess saveWfProcess(WFProcess wfProcess) throws BusinessException {
		init();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
		String wfProcessString = gson.toJson(wfProcess);

		Response response = client.target(serverUrl).path("/saveWfProcess").request()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(Entity.entity(wfProcessString, MediaType.APPLICATION_JSON + "; charset=utf-8"));

		if (response.getStatus() == HTTPStatusCodeEnum.CONFLICT.getCode()) {
			throw new BusinessException(response.getEntity().toString());
		}
		return (WFProcess) response.getEntity();
	}

	public static void enableWfProcess(WFProcess wfProcess) throws BusinessException {
		init();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
		String wfProcessString = gson.toJson(wfProcess);

		Response response = client.target(serverUrl).path("/enableWfProcess").request()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(Entity.entity(wfProcessString, MediaType.APPLICATION_JSON + "; charset=utf-8"));

		if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
			throw new BusinessException(response.getEntity().toString());
		}
	}

	public static void saveOrUpdateWfProcessStep(List<WFProcessStepData> wfProcessStepDataList)
			throws BusinessException {

		init();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String wfProcessStepDataListString = gson.toJson(wfProcessStepDataList);

		Response response = client.target(serverUrl).path("/saveOrUpdate").request()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(Entity.entity(wfProcessStepDataListString, MediaType.APPLICATION_JSON + "; charset=utf-8"));

		if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
			throw new BusinessException(response.getEntity().toString());
		}
	}
}
