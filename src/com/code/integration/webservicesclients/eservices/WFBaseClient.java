package com.code.integration.webservicesclients.eservices;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.code.integration.parameters.eservices.workflow.WFTask;
import com.code.services.config.ETRConfigurationService;

public class WFBaseClient {
	private static Client client;
	private static String serverUrl;

	public static void init() {
		client = ClientBuilder.newBuilder().build();
		serverUrl = ETRConfigurationService.getEServicesURL() + "/wfBase";
	}

	public static WFTask getWFTaskById(Long taskId) {
		init();
		return client.target(serverUrl)
				.path("wfTask/" + taskId.toString())
				.request()
				.accept(MediaType.APPLICATION_JSON + ";charset=utf-8")
				.get(WFTask.class);
	}
}
