package com.code.services.hcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.TransactionTimeline;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.enums.MovementTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

/*
 * Service to handle the employee's future transactions.
 */

public class TransactionsTimelineService extends BaseService {

    public static List<TransactionTimeline> getAllFutureTransactions(long employeeId) throws BusinessException {
	List<TransactionTimeline> movementTransactions = getMovementsTransactions(employeeId);
	List<TransactionTimeline> allTransactionsExceptMovement = getAllFutureTransactionsExceptMovements(employeeId);
	return sortLists(movementTransactions, allTransactionsExceptMovement);
    }

    private static List<TransactionTimeline> getMovementsTransactions(long employeeId) throws BusinessException {

	List<MovementTransactionData> movementsTransactions = MovementsService.getMovementTransactionsHistory(employeeId);
	List<TransactionTimeline> returnList = new ArrayList<>();
	if (movementsTransactions == null || movementsTransactions.isEmpty()) {
	    return returnList;
	}
	if (movementsTransactions.get(0).getTransactionTypeCode() == TransactionTypesEnum.MVT_NEW_DECISION.getCode() && HijriDateService.getHijriSysDate().before(movementsTransactions.get(0).getExecutionDate())) {
	    return constructFutureMovementTransactionsTimeline(movementsTransactions.get(0), 1);
	} else if (movementsTransactions.get(0).getEndDate() != null && HijriDateService.getHijriSysDate().before(movementsTransactions.get(0).getEndDate())) {
	    TransactionTimeline transaction = consturctActiveTransactionsTimeline(movementsTransactions);
	    if (transaction != null)
		returnList.add(consturctActiveTransactionsTimeline(movementsTransactions));
	    return returnList;
	}
	return returnList;
    }

    private static String getMovementTransactionDescription(MovementTransactionData movementsTransaction) {
	String description;
	if (movementsTransaction.getReplacementTransId() != null) {
	    description = getMessage("label_moveByReplacement");
	} else if (movementsTransaction.getFreezeJobId() != null) {
	    description = getMessage("label_moveByJobFreeze");
	} else if (movementsTransaction.getMovementTypeId() == MovementTypesEnum.MANDATE.getCode() || movementsTransaction.getMovementTypeId() == MovementTypesEnum.SECONDMENT.getCode()) {
	    description = movementsTransaction.getMovementTypeDesc();
	} else if (movementsTransaction.getLocationFlag() == 0) {
	    description = movementsTransaction.getMovementTypeDesc() + " " + getMessage("label_internal");
	} else {
	    description = movementsTransaction.getMovementTypeDesc() + " " + getMessage("label_external");
	}
	return description;
    }

    private static List<TransactionTimeline> constructFutureMovementTransactionsTimeline(MovementTransactionData movementsTransaction, int typeFlag) {
	List<TransactionTimeline> transactions = new ArrayList<>();
	TransactionTimeline startTransaction = new TransactionTimeline();
	TransactionTimeline endTransaction = new TransactionTimeline();

	String description = getMovementTransactionDescription(movementsTransaction);
	String daysPeriod;
	if (movementsTransaction.getPeriodDays() == null && movementsTransaction.getPeriodMonths() == null)
	    daysPeriod = "-";
	else if (movementsTransaction.getPeriodDays() == null)
	    daysPeriod = movementsTransaction.getPeriodMonths() * 30 + "";
	else if (movementsTransaction.getPeriodMonths() == null)
	    daysPeriod = movementsTransaction.getPeriodDays() + "";
	else
	    daysPeriod = movementsTransaction.getPeriodMonths() * 30 + movementsTransaction.getPeriodDays() + "";

	startTransaction.setDueDateString(movementsTransaction.getExecutionDateString());
	startTransaction.setDaysPeriod(daysPeriod);
	startTransaction.setTransactionTypeDescription(getMessage("label_start") + " " + description);
	transactions.add(startTransaction);
	if (movementsTransaction.getEndDateString() != null) {
	    endTransaction.setDueDateString(movementsTransaction.getEndDateString());
	    endTransaction.setDaysPeriod(daysPeriod == null ? "-" : daysPeriod);
	    endTransaction.setTransactionTypeDescription(getMessage("label_end") + " " + description);
	    transactions.add(endTransaction);
	}

	return transactions;
    }

    private static TransactionTimeline consturctActiveTransactionsTimeline(List<MovementTransactionData> movementsTransactions) {
	TransactionTimeline movementTransactionTimeline = new TransactionTimeline();
	long daysCount = 0;
	String lastEndDate = null;
	for (MovementTransactionData transaction : movementsTransactions) {
	    if (transaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_NEW_DECISION.getCode()) {
		if (transaction.getSuccessorDecisionEffectFlag() != null)
		    return null;
		daysCount += transaction.getPeriodMonths() == null ? transaction.getPeriodDays() : transaction.getPeriodMonths() * 30 + (transaction.getPeriodDays() == null ? 0 : transaction.getPeriodDays());
		movementTransactionTimeline.setDaysPeriod(daysCount + "");
		movementTransactionTimeline.setDueDateString(lastEndDate == null ? transaction.getEndDateString() : lastEndDate);
		movementTransactionTimeline.setTransactionTypeDescription(getMessage("label_end") + getMovementTransactionDescription(transaction));
		return movementTransactionTimeline;
	    }
	    if (transaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode()) {
		movementTransactionTimeline.setDueDateString(transaction.getEndDateString());
		movementTransactionTimeline.setDaysPeriod(transaction.getPeriodMonths() == null ? transaction.getPeriodDays() + "" : transaction.getPeriodMonths() * 30 + (transaction.getPeriodDays() == null ? 0 : transaction.getPeriodDays()) + "");
		movementTransactionTimeline.setTransactionTypeDescription(getMessage("label_end") + getMovementTransactionDescription(transaction));
		return movementTransactionTimeline;
	    }
	    if (transaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode() && transaction.getSuccessorDecisionEffectFlag() == null) {
		daysCount += transaction.getPeriodMonths() == null ? transaction.getPeriodDays() : transaction.getPeriodMonths() * 30 + (transaction.getPeriodDays() == null ? 0 : transaction.getPeriodDays());
		if (lastEndDate == null)
		    lastEndDate = transaction.getEndDateString();
	    }
	}
	return null;
    }

    private static List<TransactionTimeline> sortLists(List<TransactionTimeline> movementTransactions, List<TransactionTimeline> allTransactionsExceptMovement) {
	List<TransactionTimeline> allTransactions = new ArrayList<>();
	for (TransactionTimeline transaction : allTransactionsExceptMovement) {
	    for (Iterator<TransactionTimeline> i = movementTransactions.iterator(); i.hasNext();) {
		TransactionTimeline sortMovementTransaction = i.next();
		if (sortMovementTransaction.getDueDate().before(transaction.getDueDate())) {
		    allTransactions.add(sortMovementTransaction);
		    i.remove();
		}
	    }
	    allTransactions.add(transaction);
	}
	if (!movementTransactions.isEmpty())
	    allTransactions.addAll(movementTransactions);

	return allTransactions;

    }

    private static List<TransactionTimeline> getAllFutureTransactionsExceptMovements(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMP_ID", employeeId);
	    qParams.put("P_SYSTEM_DATE", HijriDateService.getHijriSysDateString());

	    return DataAccess.executeNamedQuery(TransactionTimeline.class, QueryNamesEnum.HCM_GET_ALL_FUTURE_TRANSACTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
