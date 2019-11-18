package com.code.services.integration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.code.dal.DataAccess;
import com.code.dal.orm.integration.payroll.AdminDecisionVariablesMapping;
import com.code.dal.orm.setup.AdminDecision;
import com.code.enums.FlagsEnum;
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
import com.code.services.util.Log4jService;

public class PayrollEngineService extends BaseService {

    private static Map<String, String> employeeIdMapping;
    private static Map<String, String> transactionIdMapping;

    public static void init() {
	employeeIdMapping = new HashMap<String, String>();
	transactionIdMapping = new HashMap<String, String>();
	for (EmployeeIdMappingEnum employeeId : EmployeeIdMappingEnum.values()) {
	    employeeIdMapping.put(employeeId.getTableName(), employeeId.getEmployeeIdColumnName());
	}
	for (TransactionIdMappingEnum transactionId : TransactionIdMappingEnum.values()) {
	    transactionIdMapping.put(transactionId.getTableName(), transactionId.getTransactionIdColumnName());
	}
    }

    public static void doPayrollIntegration(Long adminDecisionId, Long categoryId, String executionDateString, List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList, Long unitId, String decisionDateString) throws BusinessException {
	Log4jService.traceInfo(PayrollEngineService.class, "Start of doPayrollIntegration() method");
	PayrollRestClient.init();
	if (adminDecisionId == null)
	    throw new BusinessException("error_adminDecisionRecordDosntExist");
	String adminDecisionVariables = PayrollRestClient.getAdminDecisionVariables(adminDecisionId, categoryId, executionDateString);
	Log4jService.traceInfo(PayrollEngineService.class, "adminDecisionVariables: " + adminDecisionVariables);
	List<AdminDecisionResponse> adminDecisionList = getAdminDecisionVariablesMap(adminDecisionVariables);
	Log4jService.traceInfo(PayrollEngineService.class, "adminDecisionList successfully created");
	JsonObject applyAdminDecisionBody = getApplyAdminDecisionBody(adminDecisionList, adminDecisionEmployeeDataList, unitId, decisionDateString, adminDecisionId, categoryId);
	Log4jService.traceInfo(PayrollEngineService.class, "applyAdminDecisionBody: " + applyAdminDecisionBody.toString());
	PayrollRestClient.applyAdminDecision(applyAdminDecisionBody);
	PayrollRestClient.destroy();
	Log4jService.traceInfo(PayrollEngineService.class, "End of doPayrollIntegration() method");
    }

    public static Integer getIntegrationWithAllowanceAndDeductionFlag() {
	return Integer.parseInt(getConfig("integrationWithAllowanceAndDeductionFlag"));
    }

    private static List<AdminDecisionResponse> getAdminDecisionVariablesMap(String adminDecisionVariables) {
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
    }

    private static JsonObject getApplyAdminDecisionBody(List<AdminDecisionResponse> adminDecisionList, List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList, Long unitId, String decisionDateString, Long adminDecisionId, Long categoryId) throws BusinessException {
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
			String tableName = adminDecisionVariable.getVariableMapping().substring(0, adminDecisionVariable.getVariableMapping().indexOf("."));
			String columnName = adminDecisionVariable.getVariableMapping().substring(adminDecisionVariable.getVariableMapping().indexOf(".") + 1, adminDecisionVariable.getVariableMapping().length());
			Log4jService.traceInfo(PayrollEngineService.class, "tableName: " + tableName);
			Log4jService.traceInfo(PayrollEngineService.class, "columnName: " + columnName);
			if (employeeIdMapping.get(tableName) == null)
			    throw new BusinessException("error_tableIsNotDefined");
			StringBuffer mappingQuery = new StringBuffer("select to_char(" + columnName
				+ ") from " + tableName + " where "
				+ employeeIdMapping.get(tableName) + " = " + employeeData.getEmpId());
			mappingQuery.append(employeeData.getTransactionId() != null && transactionIdMapping.get(tableName) != null ? " and "
				+ transactionIdMapping.get(tableName) + " = " + employeeData.getTransactionId() : "");
			Log4jService.traceInfo(PayrollEngineService.class, "mappingQuery: " + mappingQuery.toString());
			List<String> result = DataAccess.executeNativeQuery(String.class, mappingQuery, new HashMap<String, Object>());
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

		    String tableName = adminDecisionVariable.getVariableMapping().substring(0, adminDecisionVariable.getVariableMapping().indexOf("."));
		    String columnName = adminDecisionVariable.getVariableMapping().substring(adminDecisionVariable.getVariableMapping().indexOf(".") + 1, adminDecisionVariable.getVariableMapping().length());
		    Log4jService.traceInfo(PayrollEngineService.class, "tableName: " + tableName);
		    Log4jService.traceInfo(PayrollEngineService.class, "columnName: " + columnName);
		    StringBuffer mappingQuery = new StringBuffer("select to_char(" + columnName
			    + ") from " + tableName + " where " + employeeIdMapping.get(tableName) + " = " + adminDecisionEmployeeDataList.get(0).getEmpId());
		    mappingQuery.append(adminDecisionEmployeeDataList.get(0).getTransactionId() != null && transactionIdMapping.get(tableName) != null ? " and " + transactionIdMapping.get(tableName) + " = " + adminDecisionEmployeeDataList.get(0).getTransactionId() : "");
		    Log4jService.traceInfo(PayrollEngineService.class, "mappingQuery: " + mappingQuery.toString());
		    List<String> result = DataAccess.executeNativeQuery(String.class, mappingQuery, new HashMap<String, Object>());
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
			.add("adminDecisionNumber", UUID.randomUUID().toString())
			.add("employeesList", employeeArray.build())
			.add("variablesList", variableArray.build())
			.build();
		decisionTypesList.add(decisionTypeObject);
	    }
	    return Json.createObjectBuilder().add("DecisionTypesList", decisionTypesList.build()).build();
	} catch (BusinessException e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollEngineService.class, "Exception: " + e.getMessage());
	    throw e;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollEngineService.class, "Exception: " + e.getMessage());
	    throw new BusinessException("error_general");
	}

    }

    public static AdminDecision getAdminDecisionByName(String adminDecisionName) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ADMIN_DECISION_NAME", adminDecisionName == null ? FlagsEnum.ALL.getCode() + "" : adminDecisionName);
	    List<AdminDecision> result = DataAccess.executeNamedQuery(AdminDecision.class, QueryNamesEnum.HCM_GET_ADMIN_DECISION_BY_NAME.getCode(), qParams);
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

}
