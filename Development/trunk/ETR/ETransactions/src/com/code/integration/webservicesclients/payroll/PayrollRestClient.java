package com.code.integration.webservicesclients.payroll;

import java.io.StringReader;
import java.util.Base64;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.code.dal.CustomSession;
import com.code.dal.orm.integration.payroll.PayrollIntegrationFailureLog;
import com.code.enums.FlagsEnum;
import com.code.enums.IntegrationTypeFlagEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.Log4jService;

public class PayrollRestClient {

    private static Client client;
    private static String payrollRestServicesGetAdminDecisionUrl;
    private static String allowanceRestServicesGetAdminDecisionUrl;
    private static String esbPayrollRestServicesGetAdminDecisionUrl;
    private static String esbAllowanceRestServicesGetAdminDecisionUrl;
    private static String payrollRestServicesApplyAdminDecisionUrl;
    private static String allowanceRestServicesApplyAdminDecisionUrl;
    private static String esbPayrollRestServicesApplyAdminDecisionUrl;
    private static String esbAllowanceRestServicesApplyAdminDecisionUrl;
    private static Integer esbEnabledFlag;
    private static String ESBUsername;
    private static String ESBPassword;
    private static String ESBAuthorizationHeaderValue;

    public static void init() {
	try {
	    Log4jService.traceInfo(PayrollRestClient.class, "Start of init() method");
	    payrollRestServicesGetAdminDecisionUrl = ETRConfigurationService.getPayrollRestServiceGetAdminDecisionURL();
	    allowanceRestServicesGetAdminDecisionUrl = ETRConfigurationService.getAllowanceRestServiceGetAdminDecisionURL();
	    esbPayrollRestServicesGetAdminDecisionUrl = ETRConfigurationService.getESBPayrollRestServiceGetAdminDecisionURL();
	    esbAllowanceRestServicesGetAdminDecisionUrl = ETRConfigurationService.getESBAllowanceRestServiceGetAdminDecisionURL();

	    payrollRestServicesApplyAdminDecisionUrl = ETRConfigurationService.getPayrollRestServiceApplyAdminDecisionURL();
	    allowanceRestServicesApplyAdminDecisionUrl = ETRConfigurationService.getAllowanceRestServiceApplyAdminDecisionURL();
	    esbPayrollRestServicesApplyAdminDecisionUrl = ETRConfigurationService.getESBPayrollRestServiceApplyAdminDecisionURL();
	    esbAllowanceRestServicesApplyAdminDecisionUrl = ETRConfigurationService.getESBAllowanceRestServiceApplyAdminDecisionURL();

	    ESBUsername = ETRConfigurationService.getESBUsername();
	    ESBPassword = ETRConfigurationService.getESBPassword();
	    esbEnabledFlag = ETRConfigurationService.getESBEnabledFlag();
	    ESBAuthorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString((ESBUsername + ":" + ESBPassword).getBytes());
	    client = ClientBuilder.newBuilder().build();
	    Log4jService.traceInfo(PayrollRestClient.class, "Client successfully created");
	} catch (Exception e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollRestClient.class, "Payroll rest client initialization failed!");
	    System.out.println("Payroll rest client initialization failed!");
	}
    }

    public static String getAdminDecisionVariables(Integer integrationTypeFlag, Long adminDecisionId, Long categoryId, String executionDateString, PayrollIntegrationFailureLog payrollIntegrationFailureLog, Integer resendFlag, CustomSession... useSession) throws BusinessException {
	Response response = null;
	WebTarget webTarget = null;
	String responseString = "";
	try {
	    Log4jService.traceInfo(PayrollRestClient.class, "Start of getAdminDecisionVariables() method");
	    if (esbEnabledFlag != null && esbEnabledFlag.equals(FlagsEnum.ON.getCode())) {
		webTarget = client.target(integrationTypeFlag == IntegrationTypeFlagEnum.INTEGRATE_ALLOWANCES.getCode() ? esbAllowanceRestServicesGetAdminDecisionUrl : esbPayrollRestServicesGetAdminDecisionUrl)
			.queryParam("adminDecisionId", adminDecisionId)
			.queryParam("categoryId", categoryId)
			.queryParam("executionDate", executionDateString);
		response = webTarget.request()
			.header(HttpHeaders.AUTHORIZATION, ESBAuthorizationHeaderValue)
			.get();
	    } else {
		webTarget = client.target(integrationTypeFlag == IntegrationTypeFlagEnum.INTEGRATE_ALLOWANCES.getCode() ? allowanceRestServicesGetAdminDecisionUrl : payrollRestServicesGetAdminDecisionUrl)
			.queryParam("adminDecisionId", adminDecisionId)
			.queryParam("categoryId", categoryId)
			.queryParam("executionDate", executionDateString);
		response = webTarget.request().get();
	    }
	    responseString = response == null ? null : response.readEntity(String.class);
	    if (response.getStatus() != Status.OK.getStatusCode())
		handlePayrollException(responseString);
	    return responseString;
	} catch (Exception e) {
	    handleBusinessException(e, resendFlag, integrationTypeFlag);
	    payrollIntegrationFailureLog.setRequestURL(webTarget.getUri().toString());
	    payrollIntegrationFailureLog.setResponse(responseString);
	    PayrollEngineService.addPayrollIntegrationFailureReport(payrollIntegrationFailureLog, useSession);
	    return null;
	}
    }

    public static void applyAdminDecision(Integer integrationTypeFlag, String body, PayrollIntegrationFailureLog payrollIntegrationFailureLog, Integer resendFlag, CustomSession... useSession) throws BusinessException {
	Response response = null;
	WebTarget webTarget = null;
	String responseString = "";
	try {
	    if (esbEnabledFlag != null && esbEnabledFlag.equals(FlagsEnum.ON.getCode())) {
		webTarget = client.target(integrationTypeFlag == IntegrationTypeFlagEnum.INTEGRATE_ALLOWANCES.getCode() ? esbAllowanceRestServicesApplyAdminDecisionUrl : esbPayrollRestServicesApplyAdminDecisionUrl);
		response = webTarget.request()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			.header(HttpHeaders.AUTHORIZATION, ESBAuthorizationHeaderValue)
			.post(Entity.entity(body, MediaType.APPLICATION_JSON + "; charset=utf-8"));
	    } else {
		webTarget = client.target(integrationTypeFlag == IntegrationTypeFlagEnum.INTEGRATE_ALLOWANCES.getCode() ? allowanceRestServicesApplyAdminDecisionUrl : payrollRestServicesApplyAdminDecisionUrl);
		response = webTarget.request()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			.post(Entity.entity(body, MediaType.APPLICATION_JSON + "; charset=utf-8"));
	    }
	    responseString = response == null ? null : response.readEntity(String.class);
	    if (response.getStatus() != Status.OK.getStatusCode())
		handlePayrollException(responseString);
	} catch (Exception e) {
	    handleBusinessException(e, resendFlag, integrationTypeFlag);
	    payrollIntegrationFailureLog.setRequestBody(body.toString());
	    payrollIntegrationFailureLog.setRequestURL(webTarget.getUri().toString());
	    payrollIntegrationFailureLog.setResponse(responseString);
	    PayrollEngineService.addPayrollIntegrationFailureReport(payrollIntegrationFailureLog, useSession);
	}
    }

    private static void handlePayrollException(String responseString) throws Exception {
	Log4jService.traceInfo(PayrollRestClient.class, "Error: " + responseString);
	JsonReader jsonReader = Json.createReader(new StringReader(responseString));
	JsonObject errorJson = jsonReader.readObject();
	if (errorJson.containsKey("Response")) {
	    String errorString = errorJson.get("Response").toString();
	    throw new BusinessException(responseString == null ? null : errorString.substring(1, errorString.length() - 1), new Object[] { FlagsEnum.OFF.getCode() });
	} else
	    throw new Exception();
    }

    private static void handleBusinessException(Exception e, Integer resendFlag, Integer integrationTypeFlag) throws BusinessException {
	if (e instanceof BusinessException) {
	    if (resendFlag.equals(FlagsEnum.ON.getCode()))
		throw (BusinessException) e;
	} else {
	    if (resendFlag.equals(FlagsEnum.ON.getCode())) {
		if (integrationTypeFlag.equals(IntegrationTypeFlagEnum.INTEGRATE_ALLOWANCES.getCode()))
		    throw new BusinessException("error_cantAccessAllowanceSystem");
		else
		    throw new BusinessException("error_cantAccessPayrollSystem");
	    }
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
