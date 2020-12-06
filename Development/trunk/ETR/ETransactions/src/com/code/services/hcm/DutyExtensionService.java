package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.attachments.ExternalAttachment;
import com.code.dal.orm.hcm.dutyextension.DutyExtensionTransaction;
import com.code.dal.orm.hcm.dutyextension.DutyExtensionTransactionData;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.requests.eservices.ExtentionRequest;
import com.code.integration.responses.hcm.WSTerminatedEmployeesResponse;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.HijriDateService;

public class DutyExtensionService extends BaseService {

    public static List<WSTerminatedEmployeesResponse> getTerminatedEmployeesByParentUnitId(Long unitId) throws DatabaseException, BusinessException {
	try {
	    UnitData unitData = UnitsService.getUnitById(unitId);
	    if (unitData == null)
		throw new Exception();
	    List<Object> terminationTransactionsDataAndEmployeeDataList = TerminationsService.getTerminationTransactionsDataByUnitHKey(UnitsService.getHKeyPrefix(unitData.gethKey()) + "%");
	    List<WSTerminatedEmployeesResponse> wsTerminatedEmployeesResponseList = new ArrayList<WSTerminatedEmployeesResponse>();
	    for (Object terminationTransactionsDataAndEmployeeData : terminationTransactionsDataAndEmployeeDataList) {
		TerminationTransactionData terminationTransactionData = (TerminationTransactionData) ((Object[]) terminationTransactionsDataAndEmployeeData)[0];
		EmployeeData employee = (EmployeeData) ((Object[]) terminationTransactionsDataAndEmployeeData)[1];
		wsTerminatedEmployeesResponseList.add(new WSTerminatedEmployeesResponse(terminationTransactionData, employee));
	    }
	    return wsTerminatedEmployeesResponseList;
	} catch (BusinessException e) {
	    throw new DatabaseException(e.getMessage());
	} catch (Exception e) {
	    throw new BusinessException("error_unitDoesntExist");
	}
    }

    public static DutyExtensionTransactionData getDutyExtensionTransactionDataByEmpId(Long empId) throws DatabaseException {
	List<DutyExtensionTransactionData> result = searchDutyExtensionTransactionData(null, empId, null, null);
	return (result == null || result.size() == 0) ? null : result.get(0);
    }

    public static DutyExtensionTransactionData getDutyExtensionTransactionDataById(Long id) throws DatabaseException {
	List<DutyExtensionTransactionData> result = searchDutyExtensionTransactionData(id, null, null, null);
	return (result == null || result.size() == 0) ? null : result.get(0);
    }

    public static void addExtensionTransaction(ExtentionRequest extentionRequest) throws DatabaseException, BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    validateExtensionTransaction(extentionRequest.getDutyExtensionTransaction());
	    DataAccess.addEntity(extentionRequest.getDutyExtensionTransaction(), session);
	    for (ExternalAttachment attachment : extentionRequest.getAttachmentList()) {
		attachment.setTransactionId(extentionRequest.getDutyExtensionTransaction().getId());
		attachment.setTransactionTableName(DataAccess.getTableName(DutyExtensionTransaction.class));
		DataAccess.addEntity(attachment, session);
	    }
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(extentionRequest.getDutyExtensionTransaction(), extentionRequest.getAttachmentList(), FlagsEnum.OFF.getCode(), session);
	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    throw new DatabaseException(e.getMessage());
	} finally {
	    session.close();
	}

    }

    public static void payrollIntegrationFailureHandle(Long transactionId, CustomSession session) throws DatabaseException, BusinessException {
	DutyExtensionTransactionData dutyExtensionTransactionData = getDutyExtensionTransactionDataById(transactionId);
	List<ExternalAttachment> externalAttachments = ExternalAttachmentsService.getExternalAttachmentByTransactionIdAndTableName(transactionId, DataAccess.getTableName(DutyExtensionTransaction.class));
	if (dutyExtensionTransactionData == null)
	    throw new BusinessException("error_transactionDataRetrievingError");
	doPayrollIntegration(dutyExtensionTransactionData.getDutyExtensionTransaction(), externalAttachments, FlagsEnum.ON.getCode(), session);
    }

    private static void doPayrollIntegration(DutyExtensionTransaction dutyExtensionTransaction, List<ExternalAttachment> externalAttachments, int resendFlag, CustomSession session) throws BusinessException {
	Long adminDecisionId = null;
	if (dutyExtensionTransaction.getTransactionType().equals(TransactionTypesEnum.DUTY_EXTENSION_TRANSACTION.getCode()))
	    adminDecisionId = AdminDecisionsEnum.DUTY_EXTENSION.getCode();
	else if (dutyExtensionTransaction.getTransactionType().equals(TransactionTypesEnum.DUTY_REEXTENSION_TRANSACTION.getCode()))
	    adminDecisionId = AdminDecisionsEnum.DUTY_REEXTENSION.getCode();

	if (adminDecisionId != null) {
	    EmployeeData employee = EmployeesService.getEmployeeData(dutyExtensionTransaction.getEmpId());
	    if (employee == null)
		throw new BusinessException("error_employeeDataError");
	    if (employee.getServiceTerminationDate() == null)
		throw new BusinessException("error_empShouldBeTerminated");
	    String gregTransactionDateString = HijriDateService.hijriToGregDateString(HijriDateService.getHijriDateString(dutyExtensionTransaction.getTransactionDate()));
	    String gregServiceTerminationDateString = HijriDateService.hijriToGregDateString(employee.getServiceTerminationDateString());
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>(Arrays.asList(new AdminDecisionEmployeeData(employee.getEmpId(), employee.getName(), dutyExtensionTransaction.getId(), dutyExtensionTransaction.getBasedOnExtensionId(), gregServiceTerminationDateString, null, System.currentTimeMillis() + "", null)));
	    session.flushTransaction();
	    PayrollEngineService.doPayrollIntegrationWithAttachmentList(adminDecisionId, employee.getCategoryId(), gregTransactionDateString, adminDecisionEmployeeDataList, externalAttachments, UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId(), gregTransactionDateString, DataAccess.getTableName(DutyExtensionTransaction.class), resendFlag, FlagsEnum.OFF.getCode(), session);
	}
    }

    private static void validateExtensionTransaction(DutyExtensionTransaction dutyExtensionTransaction) throws BusinessException {
	if (dutyExtensionTransaction.getEmpId() == null)
	    throw new BusinessException("error_employeeIsMandatory");
	if (dutyExtensionTransaction.getServiceTerminationReasonId() == null)
	    throw new BusinessException("error_terminationReasonIsMandatory");
	if (dutyExtensionTransaction.getTransactionDate() == null)
	    throw new BusinessException("error_transactionDateIsMandatory");
	if (dutyExtensionTransaction.getTransactionType().equals(TransactionTypesEnum.DUTY_EXTENSION_TRANSACTION.getCode())) {
	    if (dutyExtensionTransaction.getExtensionPeriodMonths() == null)
		throw new BusinessException("error_monthsMandatorySoldier");
	} else if (dutyExtensionTransaction.getTransactionType().equals(TransactionTypesEnum.DUTY_REEXTENSION_TRANSACTION.getCode())) {
	    if (dutyExtensionTransaction.getExtensionPeriodMonths() == null)
		throw new BusinessException("error_monthsMandatoryCivilains");
	    if (dutyExtensionTransaction.getBasedOnExtensionId() == null)
		throw new BusinessException("error_originalExtensionTransactionIsMandatory");
	}

    }

    private static List<DutyExtensionTransactionData> searchDutyExtensionTransactionData(Long id, Long empId, Integer serviceTerminationReason, Integer transactionType) throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_ID", id == null ? FlagsEnum.ALL.getCode() : id);
	qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	qParams.put("P_SERVICE_TERMINATION_REASON", serviceTerminationReason == null ? FlagsEnum.ALL.getCode() : serviceTerminationReason);
	qParams.put("P_TRANSACTION_TYPE", transactionType == null ? FlagsEnum.ALL.getCode() : transactionType);

	return DataAccess.executeNamedQuery(DutyExtensionTransactionData.class, QueryNamesEnum.HCM_SEARCH_DUTY_EXTENSION_TRANSACTION_DATA.getCode(), qParams);
    }

}
