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
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.setup.AdminDecision;
import com.code.enums.QueryNamesEnum;
import com.code.enums.databasecolumnamemappings.EmployeeIdMappingEnum;
import com.code.enums.databasecolumnamemappings.TransactionIdMappingEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionResponse;
import com.code.integration.responses.payroll.AdminDecisionVariable;
import com.code.integration.webservicesclients.payroll.PayrollRestClient;

public class PayrollEngineService {

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

    public static void doPayrollIntegration(Long adminDecisionId, Long categoryId, String executionDateString, List<EmployeeData> employeeDataList, Long unitId, String decisionDateString, String decisionStartDateString, String decisionEndDateString, String transactionId) throws BusinessException {
	PayrollRestClient.init();
	String adminDecisionVariables = PayrollRestClient.getAdminDecisionVariables(adminDecisionId, categoryId, executionDateString);
	List<AdminDecisionResponse> adminDecisionList = getAdminDecisionVariablesMap(adminDecisionVariables);
	JsonObject applyAdminDecisionBody = getApplyAdminDecisionBody(adminDecisionList, employeeDataList, unitId, decisionDateString, decisionStartDateString, decisionEndDateString, adminDecisionId, categoryId, transactionId);
	PayrollRestClient.applyAdminDecision(applyAdminDecisionBody);
	PayrollRestClient.destroy();
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
		adminDecisionVariableList.add(adminDecisionVariable);
	    }
	    adminDecision.setVariableArray(adminDecisionVariableList);
	    adminDecisionsList.add(adminDecision);
	}
	jsonReader.close();
	return adminDecisionsList;
    }

    private static JsonObject getApplyAdminDecisionBody(List<AdminDecisionResponse> adminDecisionList, List<EmployeeData> employeeDataList, Long unitId, String decisionDateString, String decisionStartDateString, String decisionEndDateString, Long adminDecisionId, Long categoryId, String transactionId) throws BusinessException {
	try {
	    JsonArrayBuilder decisionTypesList = Json.createArrayBuilder();
	    for (int i = 0; i < adminDecisionList.size(); i++) {
		JsonArrayBuilder employeeArray = Json.createArrayBuilder();
		JsonArrayBuilder variableArray = Json.createArrayBuilder();
		List<AdminDecisionVariable> adminDecisionVariableArray = adminDecisionList.get(i).getVariableArray();
		for (EmployeeData employeeData : employeeDataList) {
		    JsonArrayBuilder employeeLisVariablesArray = Json.createArrayBuilder();
		    for (AdminDecisionVariable adminDecisionVariable : adminDecisionVariableArray) {
			String tableName = adminDecisionVariable.getVariableMapping().substring(0, adminDecisionVariable.getVariableMapping().indexOf("."));
			String columnName = adminDecisionVariable.getVariableMapping().substring(adminDecisionVariable.getVariableMapping().indexOf(".") + 1, adminDecisionVariable.getVariableMapping().length());
			StringBuffer mappingQuery = new StringBuffer("select to_char(" + columnName
				+ ") from " + tableName + " where "
				+ employeeIdMapping.get(tableName) + " = " + employeeData.getEmpId());
			mappingQuery.append(transactionId != null && transactionIdMapping.get(tableName) != null ? " and "
				+ transactionIdMapping.get(tableName) + " = " + transactionId : "");
			String mappingValue = DataAccess.executeNativeQuery(String.class, mappingQuery, new HashMap<String, Object>()).get(0).toString();
			JsonObject variable = Json.createObjectBuilder()
				.add("id", adminDecisionVariable.getVariableId() + "")
				.add("value", mappingValue)
				.add("variableMapping", adminDecisionVariable.getVariableMapping())
				.build();
			employeeLisVariablesArray.add(variable);
		    }

		    JsonObject employeeObject = Json.createObjectBuilder()
			    .add("id", employeeData.getEmpId() + "")
			    .add("name", employeeData.getName())
			    .add("startDate", decisionStartDateString)
			    .add("endDate", decisionStartDateString)
			    .add("variablesList", employeeLisVariablesArray.build())
			    .build();
		    employeeArray.add(employeeObject);
		}
		if (employeeDataList.size() > 0) {
		    for (AdminDecisionVariable adminDecisionVariable : adminDecisionVariableArray) {

			String tableName = adminDecisionVariable.getVariableMapping().substring(0, adminDecisionVariable.getVariableMapping().indexOf("."));
			String columnName = adminDecisionVariable.getVariableMapping().substring(adminDecisionVariable.getVariableMapping().indexOf(".") + 1, adminDecisionVariable.getVariableMapping().length());
			StringBuffer mappingQuery = new StringBuffer("select to_char(" + columnName
				+ ") from " + tableName + " where " + employeeIdMapping.get(tableName) + " = " + employeeDataList.get(0).getEmpId());
			mappingQuery.append(transactionId != null && transactionIdMapping.get(tableName) != null ? " and " + transactionIdMapping.get(tableName) + " = " + transactionId : "");
			String mappingValue = DataAccess.executeNativeQuery(String.class, mappingQuery, new HashMap<String, Object>()).get(0).toString();
			JsonObject variable = Json.createObjectBuilder()
				.add("id", adminDecisionVariable.getVariableId() + "")
				.add("value", mappingValue)
				.add("variableMapping", adminDecisionVariable.getVariableMapping())
				.build();
			variableArray.add(variable);
		    }
		}
		JsonObject decisionTypeObject = Json.createObjectBuilder()
			.add("decisionTypeId", adminDecisionList.get(i).getId() + "")
			.add("name", adminDecisionList.get(i).getName())
			.add("departmentId", unitId + "")
			.add("decisionNumber", UUID.randomUUID().toString())
			.add("decisionDate", decisionDateString)
			.add("decisionStartDate", decisionStartDateString)
			.add("decisionEndDate", decisionEndDateString)
			.add("categoryId", categoryId + "")
			.add("adminDecisionId", adminDecisionId + "")
			.add("employeesList", employeeArray.build())
			.add("variablesList", variableArray.build())
			.build();
		decisionTypesList.add(decisionTypeObject);
	    }
	    return Json.createObjectBuilder().add("DecisionTypesList", decisionTypesList.build()).build();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    public static AdminDecision getAdminDecisionByName(String adminDecisionName) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ADMIN_DECISION_NAME", adminDecisionName);
	    List<AdminDecision> result = DataAccess.executeNamedQuery(AdminDecision.class, QueryNamesEnum.GET_ADMIN_DECISION_BY_NAME.getCode(), qParams);
	    return (result != null && result.size() > 0) ? result.get(0) : null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
