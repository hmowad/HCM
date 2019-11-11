package com.code.integration.webservicesclients.eservices;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.code.enums.HTTPStatusCodeEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.extensionrequest.WFTask;
import com.code.services.config.ETRConfigurationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WFGeneralProcessClient {
	private static Client client;
	private static String serverUrl;

	public static void init() {
		client = ClientBuilder.newBuilder().build();
		serverUrl = ETRConfigurationService.getEServicesURL() + "/wfGeneralProcess";
	}
	
	public static void doApproval(Long requesterId, WFTask curTask, Long mainEmpId, Long secondEmpId, WFTaskActionsEnum action, Long transactionId) throws BusinessException {
		init();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
		Response response = client.target(serverUrl).path("/wf-doApproval")
				.queryParam("requesterId", requesterId)
				.queryParam("mainEmpId", mainEmpId != null ? mainEmpId.toString() : "")
				.queryParam("secondEmpId", secondEmpId != null ? secondEmpId.toString() : "")
				.queryParam("transactionId", transactionId != null ? transactionId.toString() : "")
				.queryParam("action", action != null ? action.getCode() : "")
				.request()
				.post(Entity.entity(gson.toJson(curTask), MediaType.APPLICATION_JSON + ";"));
		
		if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
			throw new BusinessException(response.readEntity(String.class));
		}
	}

}
