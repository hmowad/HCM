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

    public static int getFutureTransactionsCount(long employeeId) throws BusinessException {
	return MovementsService.getMovementTransactionsHistory(employeeId).size() + getAllFutureTransactionsExceptMovements(employeeId).size();
    }

    private static List<TransactionTimeline> getMovementsTransactions(long employeeId) throws BusinessException {

	List<MovementTransactionData> movementsTransactions = MovementsService.getMovementTransactionsHistory(employeeId);
	if (movementsTransactions.isEmpty()) {
	    return new ArrayList<>();
	}

	List<TransactionTimeline> returnList = new ArrayList<>();
	if (movementsTransactions.get(0).getTransactionTypeCode() == TransactionTypesEnum.MVT_NEW_DECISION.getCode() && HijriDateService.getHijriSysDate().before(movementsTransactions.get(0).getExecutionDate())) {
	    return constructFutureMovementTransactionTimeline(movementsTransactions.get(0));
	} else if (movementsTransactions.get(0).getEndDate() != null && HijriDateService.getHijriSysDate().before(movementsTransactions.get(0).getEndDate())) {
	    TransactionTimeline transaction = consturctActiveTransactionTimeline(movementsTransactions);
	    if (transaction != null)
		returnList.add(transaction);
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

    private static String calculatePeriod(String days, String weeks, String months) {
	String period = "";
	if (!months.equals("-"))
	    period += months + " " + getMessage("label_months");
	if (!weeks.equals("-"))
	    period += weeks + " " + getMessage("label_weeks");
	if (!days.equals("-"))
	    period = (period.isEmpty() ? "" : period + " " + getMessage("label_and")) + " " + days + " " + getMessage("label_days");
	if (period.isEmpty())
	    period = "-";
	return period;
    }

    private static List<TransactionTimeline> constructFutureMovementTransactionTimeline(MovementTransactionData movementsTransaction) {
	List<TransactionTimeline> transactions = new ArrayList<>();
	TransactionTimeline startTransaction = new TransactionTimeline();
	TransactionTimeline endTransaction = new TransactionTimeline();

	String description = getMovementTransactionDescription(movementsTransaction);
	String period = calculatePeriod(movementsTransaction.getPeriodDays() == null ? "-" : movementsTransaction.getPeriodDays() + "", "-", movementsTransaction.getPeriodMonths() == null ? "-" : movementsTransaction.getPeriodMonths() + "");
	startTransaction.setDueDateString(movementsTransaction.getExecutionDateString());
	startTransaction.setPeriod(period);
	startTransaction.setTransactionTypeDescription(getMessage("label_start") + " " + description);
	transactions.add(startTransaction);
	if (movementsTransaction.getEndDateString() != null) {
	    endTransaction.setDueDateString(movementsTransaction.getEndDateString());
	    endTransaction.setPeriod(period);
	    endTransaction.setTransactionTypeDescription(getMessage("label_end") + " " + description);
	    transactions.add(endTransaction);
	}

	return transactions;
    }

    private static TransactionTimeline consturctActiveTransactionTimeline(List<MovementTransactionData> movementsTransactions) {
	TransactionTimeline movementTransactionTimeline = new TransactionTimeline();
	long daysCount = 0;
	long monthsCount = 0;
	String lastEndDate = null;
	for (MovementTransactionData transaction : movementsTransactions) {
	    if (transaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_NEW_DECISION.getCode()) {
		if (transaction.getSuccessorDecisionEffectFlag() != null)
		    return null;
		daysCount += transaction.getPeriodDays() == null ? 0 : transaction.getPeriodDays();
		monthsCount += transaction.getPeriodMonths() == null ? 0 : transaction.getPeriodMonths() + (daysCount / 30);
		daysCount = daysCount % 30;

		movementTransactionTimeline.setPeriod(calculatePeriod(daysCount + "", "-", monthsCount + ""));
		movementTransactionTimeline.setDueDateString(lastEndDate == null ? transaction.getEndDateString() : lastEndDate);
		movementTransactionTimeline.setTransactionTypeDescription(getMessage("label_end") + " " + getMovementTransactionDescription(transaction));
		return movementTransactionTimeline;
	    }
	    if (transaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode()) {
		movementTransactionTimeline.setDueDateString(transaction.getEndDateString());
		movementTransactionTimeline.setPeriod(calculatePeriod(transaction.getPeriodDays() == null ? "-" : transaction.getPeriodDays() + "", "-", transaction.getPeriodMonths() == null ? "-" : transaction.getPeriodMonths() + ""));
		movementTransactionTimeline.setTransactionTypeDescription(getMessage("label_end") + " " + getMovementTransactionDescription(transaction));
		return movementTransactionTimeline;
	    }
	    if (transaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode() && transaction.getSuccessorDecisionEffectFlag() == null) {
		daysCount += transaction.getPeriodDays() == null ? 0 : transaction.getPeriodDays();
		monthsCount += transaction.getPeriodMonths() == null ? 0 : transaction.getPeriodMonths();
		if (lastEndDate == null)
		    lastEndDate = transaction.getEndDateString();
	    }
	}

	return null;
    }

    private static List<TransactionTimeline> sortLists(List<TransactionTimeline> movementTransactions, List<TransactionTimeline> allTransactionsExceptMovement) {
	List<TransactionTimeline> allTransactions = new ArrayList<>();
	for (TransactionTimeline transaction : allTransactionsExceptMovement) {
	    transaction.setPeriod(calculatePeriod(transaction.getDays(), transaction.getWeeks(), transaction.getMonths()));
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
