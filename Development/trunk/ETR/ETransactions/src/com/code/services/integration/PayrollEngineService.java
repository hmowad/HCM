package com.code.services.integration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.integration.payroll.AdminDecisionVariablesMapping;
import com.code.dal.orm.integration.payroll.PayrollIntegrationFailureLog;
import com.code.dal.orm.setup.AdminDecision;
import com.code.enums.FlagsEnum;
import com.code.enums.IntegrationTypeFlagEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.databasecolumnamemappings.EmployeeIdMappingEnum;
import com.code.enums.databasecolumnamemappings.TransactionIdMappingEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.integration.responses.payroll.AdminDecisionResponse;
import com.code.integration.responses.payroll.AdminDecisionVariable;
import com.code.integration.webservicesclients.payroll.PayrollRestClient;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.HijriDateService;
import com.code.services.util.Log4jService;

public class PayrollEngineService extends BaseService {

    private static Map<String, String> employeeIdMapping;
    private static Map<String, String> transactionIdMapping;
    private static Integer integrationFlag;

    public static void init() {
	employeeIdMapping = new HashMap<String, String>();
	transactionIdMapping = new HashMap<String, String>();
	for (EmployeeIdMappingEnum employeeId : EmployeeIdMappingEnum.values()) {
	    employeeIdMapping.put(employeeId.getTableName(), employeeId.getEmployeeIdColumnName());
	}
	for (TransactionIdMappingEnum transactionId : TransactionIdMappingEnum.values()) {
	    transactionIdMapping.put(transactionId.getTableName(), transactionId.getTransactionIdColumnName());
	}
	integrationFlag = ETRConfigurationService.getIntegrationWithAllowanceAndDeductionFlag();
    }

    public static void doPayrollIntegration(Long adminDecisionId, Long categoryId, String executionDateString, List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList, Long unitId, String decisionDateString, String tableName, CustomSession... useSession) throws BusinessException {
	try {
	    Log4jService.traceInfo(PayrollEngineService.class, "Start of doPayrollIntegration() method");
	    PayrollIntegrationFailureLog payrollIntegrationFailureLog = new PayrollIntegrationFailureLog();
	    payrollIntegrationFailureLog.setDecisionNumber(adminDecisionEmployeeDataList.get(0).getDecisionNumber());
	    payrollIntegrationFailureLog.setDecisionDate(HijriDateService.getHijriDate(HijriDateService.gregToHijriDateString(decisionDateString)));
	    payrollIntegrationFailureLog.setTableName(tableName);
	    if (adminDecisionId == null)
		throw new BusinessException("error_adminDecisionRecordDosntExist");

	    AdminDecision adminDecision = getAdminDecisionById(adminDecisionId);
	    if (adminDecision == null)
		throw new BusinessException("error_adminDecisionRecordDosntExist");

	    if (adminDecision.getIntegrationTypeFlag() == IntegrationTypeFlagEnum.NO_INTEGRATON.getCode())
		return;
	    String adminDecisionVariables = PayrollRestClient.getAdminDecisionVariables(adminDecision.getIntegrationTypeFlag(), adminDecision.getId(), categoryId, executionDateString, payrollIntegrationFailureLog);
	    Log4jService.traceInfo(PayrollEngineService.class, "adminDecisionVariables: " + adminDecisionVariables);
	    if (adminDecisionVariables == null)
		return;
	    List<AdminDecisionResponse> adminDecisionList = getAdminDecisionVariablesMap(adminDecisionVariables, payrollIntegrationFailureLog, useSession);
	    if (adminDecisionList == null)
		return;
	    Log4jService.traceInfo(PayrollEngineService.class, "adminDecisionList successfully created");
	    JsonObject applyAdminDecisionBody = getApplyAdminDecisionBody(adminDecisionList, adminDecisionEmployeeDataList, unitId, decisionDateString, adminDecision.getId(), categoryId, payrollIntegrationFailureLog, useSession);
	    if (applyAdminDecisionBody == null)
		return;
	    Log4jService.traceInfo(PayrollEngineService.class, "applyAdminDecisionBody: " + applyAdminDecisionBody.toString());
	    PayrollRestClient.applyAdminDecision(adminDecision.getIntegrationTypeFlag(), applyAdminDecisionBody, payrollIntegrationFailureLog);
	    Log4jService.traceInfo(PayrollEngineService.class, "End of doPayrollIntegration() method");
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	} catch (DatabaseException e1) {
	    throw new BusinessException("error_general");
	}
    }

    public static Integer getIntegrationWithAllowanceAndDeductionFlag() {
	return integrationFlag;
    }

    private static List<AdminDecisionResponse> getAdminDecisionVariablesMap(String adminDecisionVariables, PayrollIntegrationFailureLog payrollIntegrationFailureLog, CustomSession... useSession) throws DatabaseException {
	try {
	    List<AdminDecisionResponse> adminDecisionsList = new ArrayList<AdminDecisionResponse>();
	    JsonReader jsonReader = Json.createReader(new StringReader(adminDecisionVariables));
	    JsonObject adminDecisionVariablesJson = jsonReader.readObject();
	    adminDecisionVariablesJson = adminDecisionVariablesJson.getJsonObject("Response");
	    JsonArray decisionTypesArray = adminDecisionVariablesJson.getJsonArray("DecisionTypesList");
	    for (int i = 0; i < decisionTypesArray.size(); i++) {
		AdminDecisionResponse adminDecision = new AdminDecisionResponse();
		adminDecision.setId(decisionTypesArray.getJsonObject(i).getInt("id"));
		adminDecision.setName(decisionTypesArray.getJsonObject(i).getString("name"));
		JsonArray variableArray = decisionTypesArray.getJsonObject(i).getJsonArray("variableArray");
		List<AdminDecisionVariable> adminDecisionVariableList = new ArrayList<AdminDecisionVariable>();
		for (int j = 0; j < variableArray.size(); j++) {
		    AdminDecisionVariable adminDecisionVariable = new AdminDecisionVariable();
		    adminDecisionVariable.setVariableId(variableArray.getJsonObject(j).getInt("variableId"));
		    adminDecisionVariable.setVariableName(variableArray.getJsonObject(j).getString("variableName"));
		    adminDecisionVariable.setVariableMapping(variableArray.getJsonObject(j).getString("variableMapping"));
		    adminDecisionVariable.setIsLov(variableArray.getJsonObject(j).containsKey("isLov") ? variableArray.getJsonObject(j).getInt("isLov") : null);
		    adminDecisionVariableList.add(adminDecisionVariable);
		}
		adminDecision.setVariableArray(adminDecisionVariableList);
		adminDecisionsList.add(adminDecision);
	    }
	    jsonReader.close();
	    return adminDecisionsList;
	} catch (Exception e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollEngineService.class, "Exception: " + e.getMessage());
	    addPayrollIntegrationFailureReport(payrollIntegrationFailureLog, useSession);
	    return null;
	}
    }

    private static JsonObject getApplyAdminDecisionBody(List<AdminDecisionResponse> adminDecisionList, List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList, Long unitId, String decisionDateString, Long adminDecisionId, Long categoryId, PayrollIntegrationFailureLog payrollIntegrationFailureLog, CustomSession... useSession) throws DatabaseException {
	try {
	    Log4jService.traceInfo(PayrollEngineService.class, "start of getApplyAdminDecisionBody() method");
	    if (adminDecisionEmployeeDataList == null || adminDecisionEmployeeDataList.size() == 0) {
		Log4jService.traceInfo(PayrollEngineService.class, "Employees list is empty!!");
		throw new BusinessException("error_general");
	    }
	    JsonArrayBuilder decisionTypesList = Json.createArrayBuilder();
	    for (int i = 0; i < adminDecisionList.size(); i++) {
		JsonArrayBuilder employeeArray = Json.createArrayBuilder();
		JsonArrayBuilder variableArray = Json.createArrayBuilder();
		List<AdminDecisionVariable> adminDecisionVariableArray = adminDecisionList.get(i).getVariableArray();
		for (AdminDecisionEmployeeData employeeData : adminDecisionEmployeeDataList) {
		    JsonArrayBuilder employeeLisVariablesArray = Json.createArrayBuilder();
		    for (AdminDecisionVariable adminDecisionVariable : adminDecisionVariableArray) {
			String tableName, columnName, queryTransactionId;
			String[] variablesMappingData = adminDecisionVariable.getVariableMapping().split(",");
			if (variablesMappingData.length > 1) {
			    if (employeeData.getOriginalTransactionId() == null) {
				Log4jService.traceInfo(PayrollEngineService.class, "Exception: originalTransactionId is null!!");
				throw new BusinessException("error_general");
			    }
			    queryTransactionId = employeeData.getOriginalTransactionId() + "";
			    tableName = variablesMappingData[1].substring(0, variablesMappingData[1].indexOf("."));
			    columnName = variablesMappingData[1].substring(variablesMappingData[1].indexOf(".") + 1, variablesMappingData[1].length());
			} else {
			    queryTransactionId = employeeData.getTransactionId() + "";
			    tableName = variablesMappingData[0].substring(0, variablesMappingData[0].indexOf("."));
			    columnName = variablesMappingData[0].substring(variablesMappingData[0].indexOf(".") + 1, variablesMappingData[0].length());
			}
			Log4jService.traceInfo(PayrollEngineService.class, "tableName: " + tableName);
			Log4jService.traceInfo(PayrollEngineService.class, "columnName: " + columnName);
			if (employeeIdMapping.get(tableName) == null) {
			    throw new BusinessException("error_tableIsNotDefined");
			}
			StringBuffer mappingQuery = new StringBuffer("select to_char(" + columnName
				+ ") from " + tableName + " where "
				+ employeeIdMapping.get(tableName) + " = " + employeeData.getEmpId());
			mappingQuery.append(employeeData.getTransactionId() != null && transactionIdMapping.get(tableName) != null ? " and "
				+ transactionIdMapping.get(tableName) + " = " + queryTransactionId : "");
			Log4jService.traceInfo(PayrollEngineService.class, "mappingQuery: " + mappingQuery.toString());
			List<String> result = DataAccess.executeNativeQuery(String.class, mappingQuery, new HashMap<String, Object>(), useSession);
			String mappingValue = result == null || result.size() == 0 ? null : result.get(0);
			Log4jService.traceInfo(PayrollEngineService.class, "HCM value: " + mappingValue);
			if (adminDecisionVariable.getIsLov() != null && adminDecisionVariable.getIsLov().equals(FlagsEnum.ON.getCode())) {
			    AdminDecisionVariablesMapping adminDecisionVariablesMapping = getAdminDecisionVariablesMappingByVariableNameAndHCMValue(adminDecisionVariable.getVariableMapping(), mappingValue);
			    if (adminDecisionVariablesMapping == null) {
				Log4jService.traceInfo(PayrollEngineService.class, "Error: adminDecisionVariablesMapping is null");
				throw new BusinessException("error_adminDecisionVariablesMappingNotFound");
			    }
			    mappingValue = adminDecisionVariablesMapping.getPrlValue();
			    Log4jService.traceInfo(PayrollEngineService.class, "PRL Value: " + mappingValue);
			}
			JsonObject variable = Json.createObjectBuilder()
				.add("id", adminDecisionVariable.getVariableId() + "")
				.add("value", mappingValue == null ? "" : mappingValue)
				.add("variableMapping", adminDecisionVariable.getVariableMapping())
				.build();
			if (mappingValue != null)
			    employeeLisVariablesArray.add(variable);
		    }

		    JsonObject employeeObject = Json.createObjectBuilder()
			    .add("id", employeeData.getEmpId() + "")
			    .add("name", employeeData.getEmpName())
			    .add("startDate", employeeData.getGregStartDateString())
			    .add("endDate", employeeData.getGregEndDateString() == null ? "" : employeeData.getGregEndDateString())
			    .add("variablesList", employeeLisVariablesArray.build())
			    .build();
		    employeeArray.add(employeeObject);
		}

		for (AdminDecisionVariable adminDecisionVariable : adminDecisionVariableArray) {
		    String tableName, columnName, queryTransactionId;
		    String[] variablesMappingData = adminDecisionVariable.getVariableMapping().split(",");
		    if (variablesMappingData.length > 1) {
			if (adminDecisionEmployeeDataList.get(0).getOriginalTransactionId() == null) {
			    Log4jService.traceInfo(PayrollEngineService.class, "Exception: originalTransactionId is null!!");
			    throw new BusinessException("error_general");
			}
			queryTransactionId = adminDecisionEmployeeDataList.get(0).getOriginalTransactionId() + "";
			tableName = variablesMappingData[1].substring(0, variablesMappingData[1].indexOf("."));
			columnName = variablesMappingData[1].substring(variablesMappingData[1].indexOf(".") + 1, variablesMappingData[1].length());
		    } else {
			queryTransactionId = adminDecisionEmployeeDataList.get(0).getTransactionId() + "";
			tableName = variablesMappingData[0].substring(0, variablesMappingData[0].indexOf("."));
			columnName = variablesMappingData[0].substring(variablesMappingData[0].indexOf(".") + 1, variablesMappingData[0].length());
		    }
		    Log4jService.traceInfo(PayrollEngineService.class, "tableName: " + tableName);
		    Log4jService.traceInfo(PayrollEngineService.class, "columnName: " + columnName);
		    StringBuffer mappingQuery = new StringBuffer("select to_char(" + columnName
			    + ") from " + tableName + " where " + employeeIdMapping.get(tableName) + " = " + adminDecisionEmployeeDataList.get(0).getEmpId());
		    mappingQuery.append(adminDecisionEmployeeDataList.get(0).getTransactionId() != null && transactionIdMapping.get(tableName) != null ? " and " + transactionIdMapping.get(tableName) + " = " + queryTransactionId : "");
		    Log4jService.traceInfo(PayrollEngineService.class, "mappingQuery: " + mappingQuery.toString());
		    List<String> result = DataAccess.executeNativeQuery(String.class, mappingQuery, new HashMap<String, Object>(), useSession);
		    String mappingValue = result == null || result.size() == 0 ? null : result.get(0);
		    Log4jService.traceInfo(PayrollEngineService.class, "HCM value: " + mappingValue);
		    if (adminDecisionVariable.getIsLov() != null && adminDecisionVariable.getIsLov().equals(FlagsEnum.ON.getCode())) {
			AdminDecisionVariablesMapping adminDecisionVariablesMapping = getAdminDecisionVariablesMappingByVariableNameAndHCMValue(adminDecisionVariable.getVariableMapping(), mappingValue);
			if (adminDecisionVariablesMapping == null) {
			    Log4jService.traceInfo(PayrollEngineService.class, "Error: adminDecisionVariablesMapping is null");
			    throw new BusinessException("error_adminDecisionVariablesMappingNotFound");
			}
			mappingValue = adminDecisionVariablesMapping.getPrlValue();
			Log4jService.traceInfo(PayrollEngineService.class, "PRL value: " + mappingValue);
		    }
		    JsonObject variable = Json.createObjectBuilder()
			    .add("id", adminDecisionVariable.getVariableId() + "")
			    .add("value", mappingValue == null ? "" : mappingValue)
			    .add("variableMapping", adminDecisionVariable.getVariableMapping())
			    .build();
		    if (mappingValue != null)
			variableArray.add(variable);
		}

		JsonObject decisionTypeObject = Json.createObjectBuilder()
			.add("decisionTypeId", adminDecisionList.get(i).getId() + "")
			.add("name", adminDecisionList.get(i).getName())
			.add("departmentId", unitId + "")
			.add("decisionDate", decisionDateString)
			.add("decisionStartDate", adminDecisionEmployeeDataList.get(0).getGregStartDateString())
			.add("decisionEndDate", adminDecisionEmployeeDataList.get(0).getGregEndDateString() == null ? "" : adminDecisionEmployeeDataList.get(0).getGregEndDateString())
			.add("categoryId", categoryId + "")
			.add("adminDecisionId", adminDecisionId + "")
			.add("adminDecisionNumber", adminDecisionEmployeeDataList.get(0).getDecisionNumber())
			.add("originalAdminDecisionNumber", adminDecisionEmployeeDataList.get(0).getOriginalAdminDecisionNumber() == null ? "" : adminDecisionEmployeeDataList.get(0).getOriginalAdminDecisionNumber())
			.add("employeesList", employeeArray.build())
			.add("variablesList", variableArray.build())
			.build();
		decisionTypesList.add(decisionTypeObject);
	    }
	    return Json.createObjectBuilder().add("DecisionTypesList", decisionTypesList.build()).build();
	} catch (Exception e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollEngineService.class, "Exception: " + e.getMessage());
	    addPayrollIntegrationFailureReport(payrollIntegrationFailureLog, useSession);
	    return null;
	}

    }

    public static AdminDecision getAdminDecisionById(long adminDecisionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ADMIN_DECISION_ID", adminDecisionId);
	    List<AdminDecision> result = DataAccess.executeNamedQuery(AdminDecision.class, QueryNamesEnum.HCM_GET_ADMIN_DECISION_BY_ID.getCode(), qParams);
	    return (result != null && result.size() > 0) ? result.get(0) : null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static AdminDecisionVariablesMapping getAdminDecisionVariablesMappingByVariableNameAndHCMValue(String variableName, String hcmValue) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_VARIABLE_NAME", variableName == null ? FlagsEnum.ALL.getCode() + "" : variableName);
	    qParams.put("P_HCM_VALUE", hcmValue == null ? FlagsEnum.ALL.getCode() + "" : hcmValue);
	    List<AdminDecisionVariablesMapping> result = DataAccess.executeNamedQuery(AdminDecisionVariablesMapping.class, QueryNamesEnum.HCM_GET_ADMIN_DECISION_VARIABLES_MAPPING_BY_VARIABLE_NAME_AND_HCM_VALUE.getCode(), qParams);
	    return (result != null && result.size() > 0) ? result.get(0) : null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void addPayrollIntegrationFailureReport(PayrollIntegrationFailureLog payrollIntegrationFailureLog, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    payrollIntegrationFailureLog.setRequestDate(new Date());
	    DataAccess.addEntity(payrollIntegrationFailureLog, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

}
