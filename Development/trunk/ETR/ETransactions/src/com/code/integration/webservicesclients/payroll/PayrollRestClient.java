package com.code.integration.webservicesclients.payroll;

import java.util.Base64;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.Log4jService;

public class PayrollRestClient {

    private static Client client;
    private static String payrollRestServicesUrl;
    private static Integer esbEnabledFlag;
    private static String esbRestServicesUrl;
    private static String ESBUsername;
    private static String ESBPassword;
    private static String ESBAuthorizationHeaderValue;

    public static void init() {
	try {
	    Log4jService.traceInfo(PayrollRestClient.class, "Start of init() method");
	    client = ClientBuilder.newBuilder().build();
	    payrollRestServicesUrl = ETRConfigurationService.getPayrollRestServiceURL();
	    esbRestServicesUrl = ETRConfigurationService.getESBRestServiceURL();
	    ESBUsername = ETRConfigurationService.getESBUsername();
	    ESBPassword = ETRConfigurationService.getESBPassword();
	    esbEnabledFlag = ETRConfigurationService.getESBEnabledFlag();
	    ESBAuthorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString((ESBUsername + ":" + ESBPassword).getBytes());
	    Log4jService.traceInfo(PayrollRestClient.class, "Client successfully created");
	} catch (Exception e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollRestClient.class, "Payroll rest client initialization failed!");
	    System.out.println("Payroll rest client initialization failed!");
	}
    }

    public static String getAdminDecisionVariables(Long adminDecisionId, Long categoryId, String executionDateString) throws BusinessException {
	try {
	    Log4jService.traceInfo(PayrollRestClient.class, "Start of getAdminDecisionVariables() method");

	    if (esbEnabledFlag != null && esbEnabledFlag.equals(FlagsEnum.ON.getCode()))
		return client.target(esbRestServicesUrl).path("payroll-decision/admin-decision-variables")
			.queryParam("adminDecisionId", adminDecisionId)
			.queryParam("categoryId", categoryId)
			.queryParam("executionDate", executionDateString).request()
			.header(HttpHeaders.AUTHORIZATION, ESBAuthorizationHeaderValue)
			.get(String.class);
	    else
		return client.target(payrollRestServicesUrl).path("DecisionTypeWebService/getAdminDecisionVariables")
			.queryParam("adminDecisionId", adminDecisionId)
			.queryParam("categoryId", categoryId)
			.queryParam("executionDate", executionDateString).request().get(String.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollRestClient.class, "Error in retrieving AdminDecisionVariables");
	    throw new BusinessException("error_adminDecisionParametersError");
	}
    }

    public static void applyAdminDecision(JsonObject body) throws BusinessException {
	try {
	    Response response;
	    if (esbEnabledFlag != null && esbEnabledFlag.equals(FlagsEnum.ON.getCode()))
		response = client.target(esbRestServicesUrl).path("payroll-decision/admin-decision-variables")
			.request()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			.header(HttpHeaders.AUTHORIZATION, ESBAuthorizationHeaderValue)
			.post(Entity.entity(body, MediaType.APPLICATION_JSON + "; charset=utf-8"));
	    else
		response = client.target(payrollRestServicesUrl).path("DecisionWebService/ApplyAdminDecision")
			.request()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			.post(Entity.entity(body, MediaType.APPLICATION_JSON + "; charset=utf-8"));
	    if (response.getStatus() != Status.OK.getStatusCode()) {
		Log4jService.traceInfo(PayrollRestClient.class, "Error: " + response.readEntity(String.class));
		throw new BusinessException("error_payrollServiceExecutionError");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollRestClient.class, "Error in applyAdminDecision");
	    throw new BusinessException("error_payrollServiceExecutionError");
	}
    }

    public static void destroy() {
	try {
	    client.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
