package com.code.services.hcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.dutyextension.DutyExtensionTransaction;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.hcm.WSTerminatedEmployeesResponse;
import com.code.services.BaseService;

public class DutyExtensionService extends BaseService {

    public static List<WSTerminatedEmployeesResponse> getTerminatedEmployeesByUnitId(Long unitId) throws DatabaseException {

	List<TerminationTransactionData> terminationTransactionsDataList = TerminationsService.getTerminationTransactionsDataByUnitId(unitId);
	List<WSTerminatedEmployeesResponse> wsTerminatedEmployeesResponseList = new ArrayList<WSTerminatedEmployeesResponse>();
	for (TerminationTransactionData terminationTransactionData : terminationTransactionsDataList) {
	    wsTerminatedEmployeesResponseList.add(new WSTerminatedEmployeesResponse(terminationTransactionData.getEmpId(), terminationTransactionData.getEmpName(), terminationTransactionData.getReasonId(), terminationTransactionData.getReasonDesc()));
	}
	return wsTerminatedEmployeesResponseList;
    }

    public static DutyExtensionTransaction getDutyExtensionTransactionByEmpId(Long empId) throws DatabaseException {
	List<DutyExtensionTransaction> result = searchDutyExtensionTransactions(null, empId, null, TransactionTypesEnum.DUTY_EXTENSION_TRANSACTION.getCode());
	return (result == null || result.size() == 0) ? null : result.get(0);
    }

    public static void addExtensionTransaction(DutyExtensionTransaction dutyExtensionTransaction) throws DatabaseException, BusinessException {
	dutyExtensionTransaction.setTransactionType(TransactionTypesEnum.DUTY_EXTENSION_TRANSACTION.getCode());
	validateExtensionTransaction(dutyExtensionTransaction);
	DataAccess.addEntity(dutyExtensionTransaction);
    }

    public static void addReExtensionTransaction(DutyExtensionTransaction dutyExtensionTransaction) throws DatabaseException, BusinessException {
	dutyExtensionTransaction.setTransactionType(TransactionTypesEnum.DUTY_REEXTENSION_TRANSACTION.getCode());
	validateExtensionTransaction(dutyExtensionTransaction);
	DataAccess.addEntity(dutyExtensionTransaction);
    }

    private static void validateExtensionTransaction(DutyExtensionTransaction dutyExtensionTransaction) throws BusinessException {
	if (dutyExtensionTransaction.getEmpId() == null)
	    throw new BusinessException("error_employeeIsMandatory");
	if (dutyExtensionTransaction.getServiceTerminationReasonId() == null)
	    throw new BusinessException("error_terminationReasonIsMandatory");
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

    private static List<DutyExtensionTransaction> searchDutyExtensionTransactions(Long id, Long empId, Integer serviceTerminationReason, Integer transactionType) throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_ID", id == null ? FlagsEnum.ALL.getCode() : id);
	qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	qParams.put("P_SERVICE_TERMINATION_REASON", serviceTerminationReason == null ? FlagsEnum.ALL.getCode() : serviceTerminationReason);
	qParams.put("P_TRANSACTION_TYPE", transactionType == null ? FlagsEnum.ALL.getCode() : transactionType);

	return DataAccess.executeNamedQuery(DutyExtensionTransaction.class, QueryNamesEnum.HCM_SEARCH_DUTY_EXTENSION_TRANSACTION.getCode(), qParams);
    }

}
