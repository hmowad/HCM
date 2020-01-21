package com.code.integration.webservicesclients.eservices;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.code.enums.WFTaskActionsEnum;
import com.code.enums.eservices.HTTPStatusCodeEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.eservices.workflow.WFTask;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.Log4jService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WFGeneralProcessClient extends BaseClient {
    private static Client client;
    private static String serverUrl;

    public static void init() {
	client = ClientBuilder.newBuilder().build();
	serverUrl = ETRConfigurationService.getEServicesURL() + "/wfGeneralProcess";
    }

    public static void doApproval(WFTask curTask, Long mainEmpId, Long secondEmpId, WFTaskActionsEnum action) throws BusinessException {
	init();
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "start of calling service: wfGeneralProcess/wf-doApproval");

	Response response = client.target(serverUrl).path("/wf-doApproval")
		.queryParam("mainEmpId", mainEmpId != null ? mainEmpId.toString() : "")
		.queryParam("secondEmpId", secondEmpId != null ? secondEmpId.toString() : "")
		.queryParam("action", action != null ? action.getCode() : "")
		.request()
		.post(Entity.entity(gson.toJson(curTask), MediaType.APPLICATION_JSON + ";"));

	Log4jService.traceInfo(EServicesWorkFlowClient.class, "Response:   " + response);
	Log4jService.traceInfo(EServicesWorkFlowClient.class, "end of calling service: wfGeneralProcess/wf-doApproval");

	if (response.getStatus() != HTTPStatusCodeEnum.OK.getCode()) {
	    throw new BusinessException(getExceptionMessage(response.getStatus(), response.readEntity(String.class)));
	}

    }

}
