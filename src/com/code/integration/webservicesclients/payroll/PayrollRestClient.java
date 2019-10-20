package com.code.integration.webservicesclients.payroll;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;

public class PayrollRestClient {

    private static Client client;
    private static String payrollRestServicesUrl;

    public static void init() throws BusinessException {
	try {
	    client = ClientBuilder.newBuilder().build();
	    payrollRestServicesUrl = ETRConfigurationService.getPayrollRestServiceURL();

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_payrollRestServiceURLError");
	}
    }

    public static String getAdminDecisionVariables(Long adminDecisionId, Long categoryId, String executionDateString) throws BusinessException {
	try {
	    return client.target(payrollRestServicesUrl).path("DecisionTypeWebService/getAdminDecisionVariables").queryParam("adminDecisionId", adminDecisionId)
		    .queryParam("categoryId", categoryId).queryParam("executionDate", executionDateString).request().get(String.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_adminDecisionParametersError");
	}
    }

    public static void applyAdminDecision(JsonObject body) throws BusinessException {
	try {
	    Response response = client.target(payrollRestServicesUrl).path("DecisionWebService/ApplyAdminDecision")
		    .request()
		    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
		    .post(Entity.entity(body, MediaType.APPLICATION_JSON + "; charset=utf-8"));
	    if (response.getStatus() != Status.OK.getStatusCode())
		throw new BusinessException("error_payrollServiceExecutionError");
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_payrollServiceExecutionError");
	}
    }

    public static void destroy() throws BusinessException {
	try {
	    client.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_message");
	}
    }

}
