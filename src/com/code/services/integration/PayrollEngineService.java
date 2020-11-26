package com.code.services.integration;

import java.io.StringReader;
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
import com.code.dal.orm.hcm.dutyextension.DutyExtensionTransaction;
import com.code.dal.orm.hcm.employees.Employee;
import com.code.dal.orm.hcm.incentives.IncentiveTransaction;
import com.code.dal.orm.hcm.movements.MovementTransaction;
import com.code.dal.orm.hcm.promotions.PromotionTransaction;
import com.code.dal.orm.hcm.raises.RaiseTransaction;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransaction;
import com.code.dal.orm.hcm.retirements.DisclaimerTransaction;
import com.code.dal.orm.hcm.terminations.TerminationTransaction;
import com.code.dal.orm.hcm.trainings.TrainingTransaction;
import com.code.dal.orm.hcm.vacations.TransientVacationTransaction;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.integration.payroll.AdminDecisionVariablesMapping;
import com.code.dal.orm.integration.payroll.PayrollIntegrationFailureLog;
import com.code.dal.orm.integration.payroll.PayrollIntegrationFailureLogData;
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
import com.code.services.hcm.DutyExtensionService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.hcm.IncentivesService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.PromotionsService;
import com.code.services.hcm.RaisesService;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.hcm.RetirementsService;
import com.code.services.hcm.TerminationsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.VacationsService;
import com.code.services.util.HijriDateService;
import com.code.services.util.Log4jService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static void doPayrollIntegration(Long adminDecisionId, Long categoryId, String executionDateString, List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList, Long unitId, String decisionDateString, String tableName, Integer resendFlag, Integer originalDecisionFlag, CustomSession... useSession) throws BusinessException {
	try {
	    Log4jService.traceInfo(PayrollEngineService.class, "Start of doPayrollIntegration() method");
	    PayrollIntegrationFailureLog payrollIntegrationFailureLog = new PayrollIntegrationFailureLog();
	    payrollIntegrationFailureLog.setDecisionNumber(originalDecisionFlag.equals(FlagsEnum.ON.getCode()) ? adminDecisionEmployeeDataList.get(0).getOriginalAdminDecisionNumber() : adminDecisionEmployeeDataList.get(0).getDecisionNumber());
	    payrollIntegrationFailureLog.setDecisionDate(HijriDateService.getHijriDate(HijriDateService.gregToHijriDateString(decisionDateString)));
	    payrollIntegrationFailureLog.setTableName(tableName);
	    payrollIntegrationFailureLog.setAdminDecisionId(adminDecisionId);
	    payrollIntegrationFailureLog.setCategoryId(categoryId);
	    payrollIntegrationFailureLog.setExecutedFlag(FlagsEnum.OFF.getCode());
	    payrollIntegrationFailureLog.setRowId(DataAccess.getTableName(Employee.class).equals(tableName) ? adminDecisionEmployeeDataList.get(0).getEmpId() : adminDecisionEmployeeDataList.get(0).getTransactionId());
	    if (adminDecisionId == null)
		throw new BusinessException("error_adminDecisionRecordDosntExist");

	    AdminDecision adminDecision = getAdminDecisionById(adminDecisionId);
	    if (adminDecision == null)
		throw new BusinessException("error_adminDecisionRecordDosntExist");

	    if (adminDecision.getIntegrationTypeFlag() == IntegrationTypeFlagEnum.NO_INTEGRATON.getCode())
		return;
	    String adminDecisionVariables = PayrollRestClient.getAdminDecisionVariables(adminDecision.getIntegrationTypeFlag(), adminDecision.getId(), categoryId, executionDateString, payrollIntegrationFailureLog, resendFlag);
	    Log4jService.traceInfo(PayrollEngineService.class, "adminDecisionVariables: " + adminDecisionVariables);
	    if (adminDecisionVariables == null)
		return;
	    AdminDecisionResponse[] adminDecisionList = getAdminDecisionVariablesMap(adminDecisionVariables, payrollIntegrationFailureLog, resendFlag, useSession);
	    if (adminDecisionList == null)
		return;
	    Log4jService.traceInfo(PayrollEngineService.class, "adminDecisionList successfully created");
	    JsonObject applyAdminDecisionBody = getApplyAdminDecisionBody(adminDecisionList, adminDecisionEmployeeDataList, unitId, decisionDateString, adminDecision.getId(), categoryId, payrollIntegrationFailureLog, resendFlag, useSession);
	    if (applyAdminDecisionBody == null)
		return;
	    Log4jService.traceInfo(PayrollEngineService.class, "applyAdminDecisionBody: " + applyAdminDecisionBody.toString());
	    PayrollRestClient.applyAdminDecision(adminDecision.getIntegrationTypeFlag(), applyAdminDecisionBody, payrollIntegrationFailureLog, resendFlag);
	    Log4jService.traceInfo(PayrollEngineService.class, "End of doPayrollIntegration() method");
	} catch (BusinessException e) {
	    throw e;
	}
    }

    public static Integer getIntegrationWithAllowanceAndDeductionFlag() {
	return integrationFlag;
    }

    private static AdminDecisionResponse[] getAdminDecisionVariablesMap(String adminDecisionVariables, PayrollIntegrationFailureLog payrollIntegrationFailureLog, Integer resendFlag, CustomSession... useSession) throws BusinessException {
	try {
	    JsonReader jsonReader = Json.createReader(new StringReader(adminDecisionVariables));
	    JsonObject adminDecisionVariablesJson = jsonReader.readObject();
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    return gson.fromJson(adminDecisionVariablesJson.getJsonObject("Response").getJsonArray("DecisionTypesList").toString(), AdminDecisionResponse[].class);

	} catch (Exception e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollEngineService.class, "Exception: " + e.getMessage());
	    if (resendFlag.equals(FlagsEnum.ON.getCode()))
		throw new BusinessException("error_cantAccessPayrollSystem");
	    addPayrollIntegrationFailureReport(payrollIntegrationFailureLog, useSession);
	    return null;
	}
    }

    private static JsonObject getApplyAdminDecisionBody(AdminDecisionResponse[] adminDecisionList, List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList, Long unitId, String decisionDateString, Long adminDecisionId, Long categoryId, PayrollIntegrationFailureLog payrollIntegrationFailureLog, Integer resendFlag, CustomSession... useSession) throws BusinessException {
	try {
	    Log4jService.traceInfo(PayrollEngineService.class, "start of getApplyAdminDecisionBody() method");
	    if (adminDecisionEmployeeDataList == null || adminDecisionEmployeeDataList.size() == 0) {
		Log4jService.traceInfo(PayrollEngineService.class, "Employees list is empty!!");
		throw new BusinessException("error_integrationError");
	    }
	    JsonArrayBuilder decisionTypesList = Json.createArrayBuilder();
	    for (int i = 0; i < adminDecisionList.length; i++) {
		JsonArrayBuilder employeeArray = Json.createArrayBuilder();
		JsonArray variableList = null;
		AdminDecisionVariable[] adminDecisionVariableArray = adminDecisionList[i].getVariableArray();
		for (AdminDecisionEmployeeData employeeData : adminDecisionEmployeeDataList) {
		    JsonArrayBuilder employeeListVariablesArray = Json.createArrayBuilder();
		    for (AdminDecisionVariable adminDecisionVariable : adminDecisionVariableArray) {
			String tableName, columnName, queryTransactionId;
			String[] variablesMappingData = adminDecisionVariable.getVariableMapping().split(",");
			if (variablesMappingData.length > 1) {
			    if (employeeData.getOriginalTransactionId() == null) {
				Log4jService.traceInfo(PayrollEngineService.class, "Exception: originalTransactionId is null!!");
				throw new BusinessException("error_integrationError");
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
			if (adminDecisionVariable.getIsLOV() != null && adminDecisionVariable.getIsLOV().equals(FlagsEnum.ON.getCode())) {
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
			    employeeListVariablesArray.add(variable);
		    }

		    variableList = employeeListVariablesArray.build();
		    JsonObject employeeObject = Json.createObjectBuilder()
			    .add("id", employeeData.getEmpId() + "")
			    .add("name", employeeData.getEmpName())
			    .add("startDate", employeeData.getGregStartDateString())
			    .add("endDate", employeeData.getGregEndDateString() == null ? "" : employeeData.getGregEndDateString())
			    .add("variablesList", variableList)
			    .build();
		    employeeArray.add(employeeObject);
		}

		JsonObject decisionTypeObject = Json.createObjectBuilder()
			.add("decisionTypeId", adminDecisionList[i].getId() + "")
			.add("name", adminDecisionList[i].getName())
			.add("departmentId", unitId + "")
			.add("decisionDate", decisionDateString)
			.add("decisionStartDate", adminDecisionEmployeeDataList.get(0).getGregStartDateString())
			.add("decisionEndDate", adminDecisionEmployeeDataList.get(0).getGregEndDateString() == null ? "" : adminDecisionEmployeeDataList.get(0).getGregEndDateString())
			.add("categoryId", categoryId + "")
			.add("adminDecisionId", adminDecisionId + "")
			.add("adminDecisionNumber", adminDecisionEmployeeDataList.get(0).getDecisionNumber())
			.add("originalAdminDecisionNumber", adminDecisionEmployeeDataList.get(0).getOriginalAdminDecisionNumber() == null ? "" : adminDecisionEmployeeDataList.get(0).getOriginalAdminDecisionNumber())
			.add("employeesList", employeeArray.build())
			.add("variablesList", variableList)
			.build();
		decisionTypesList.add(decisionTypeObject);
	    }
	    return Json.createObjectBuilder().add("DecisionTypesList", decisionTypesList.build()).build();
	} catch (Exception e) {
	    e.printStackTrace();
	    Log4jService.traceInfo(PayrollEngineService.class, "Exception: " + e.getMessage());
	    if (resendFlag.equals(FlagsEnum.ON.getCode()))
		throw new BusinessException("error_cantAccessPayrollSystem");
	    addPayrollIntegrationFailureReport(payrollIntegrationFailureLog, useSession);
	    return null;
	}

    }

    public static void resendFailedTransaction(PayrollIntegrationFailureLogData payrollIntegrationFailureLogData) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {

	    session.beginTransaction();
	    if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(DisclaimerTransaction.class))) {
		RetirementsService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getDecisionNumber(), payrollIntegrationFailureLogData.getDecisionDate(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(TerminationTransaction.class))) {
		TerminationsService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getDecisionNumber(), payrollIntegrationFailureLogData.getDecisionDate(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(RecruitmentTransaction.class))) {
		RecruitmentsService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getDecisionNumber(), payrollIntegrationFailureLogData.getDecisionDate(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(MovementTransaction.class))) {
		MovementsService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getDecisionNumber(), payrollIntegrationFailureLogData.getDecisionDate(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(PromotionTransaction.class))) {
		PromotionsService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getDecisionNumber(), payrollIntegrationFailureLogData.getDecisionDate(), payrollIntegrationFailureLogData.getCategoryId(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(Employee.class))) {
		EmployeesService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getRowId());
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(RaiseTransaction.class))) {
		RaisesService.PayrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getDecisionNumber(), payrollIntegrationFailureLogData.getDecisionDate(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(Vacation.class))) {
		VacationsService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getDecisionDate(), payrollIntegrationFailureLogData.getDecisionNumber(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(TransientVacationTransaction.class))) {
		FutureVacationsService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getDecisionDate(), payrollIntegrationFailureLogData.getDecisionNumber(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(DutyExtensionTransaction.class))) {
		DutyExtensionService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getRowId(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(IncentiveTransaction.class))) {
		IncentivesService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getRowId(), session);
	    } else if (payrollIntegrationFailureLogData.getTableName().equals(DataAccess.getTableName(TrainingTransaction.class))) {
		TrainingEmployeesService.payrollIntegrationFailureHandle(payrollIntegrationFailureLogData.getRowId(), payrollIntegrationFailureLogData.getAdminDecisionId(), session);
	    }

	    payrollIntegrationFailureLogData.setExecutedFlag(FlagsEnum.ON.getCode());
	    DataAccess.updateEntity(payrollIntegrationFailureLogData.getPayrollIntegrationFailureLog(), session);
	    session.commitTransaction();

	} catch (DatabaseException e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} catch (BusinessException e1) {
	    session.rollbackTransaction();
	    throw e1;
	} finally {
	    session.close();
	}
    }

    public static AdminDecision getAdminDecisionById(long adminDecisionId) throws BusinessException {
	List<AdminDecision> result = searchAdminDecision(adminDecisionId, null);
	return (result != null && result.size() > 0) ? result.get(0) : null;
    }

    public static List<AdminDecision> getAllAdminDecisions() throws BusinessException {
	return searchAdminDecision(null, null);
    }

    public static void addPayrollIntegrationFailureReport(PayrollIntegrationFailureLog payrollIntegrationFailureLog, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    payrollIntegrationFailureLog.setRequestDate(new Date());
	    DataAccess.addEntity(payrollIntegrationFailureLog, session);
	    Log4jService.traceInfo(PayrollEngineService.class, "ERROR: payroll integration failure occured!! " + "id: " + payrollIntegrationFailureLog.getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (DatabaseException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static List<AdminDecision> searchAdminDecision(Long adminDecisionId, String adminDecisionName) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ADMIN_DECISION_ID", adminDecisionId == null ? FlagsEnum.ALL.getCode() : adminDecisionId);
	    qParams.put("P_ADMIN_DECISION_NAME", adminDecisionName == null || adminDecisionName.equals("") ? FlagsEnum.ALL.getCode() + "" : adminDecisionName);
	    return DataAccess.executeNamedQuery(AdminDecision.class, QueryNamesEnum.HCM_SEARCH_ADMIN_DECISION.getCode(), qParams);
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

    public static List<PayrollIntegrationFailureLogData> getPayrollIntegrationFailureLog(Long categoryId, Long adminDecisionId, String decisionNumber, String decisionDateString) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CATEGORY_ID", categoryId == null ? FlagsEnum.ALL.getCode() : categoryId);
	    qParams.put("P_ADMIN_DECISION_ID", adminDecisionId == null ? FlagsEnum.ALL.getCode() : adminDecisionId);
	    qParams.put("P_DECISION_NUMBER", decisionNumber == null || decisionNumber.equals("") ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (decisionDateString == null || decisionDateString.equals("")) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", decisionDateString);
	    }

	    return DataAccess.executeNamedQuery(PayrollIntegrationFailureLogData.class, QueryNamesEnum.HCM_GET_NON_EXECUTED_PAYROLL_INTEGRATION_FAILURE_LOG.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
