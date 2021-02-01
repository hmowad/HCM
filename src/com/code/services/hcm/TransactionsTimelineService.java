package com.code.services.hcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.TransactionTimeline;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
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
	return getMovementsTransactions(employeeId).size() + getAllFutureTransactionsExceptMovements(employeeId).size();
    }

    private static List<TransactionTimeline> getMovementsTransactions(long employeeId) throws BusinessException {

	List<MovementTransactionData> movementsTransactions = MovementsService.getMovementTransactionsHistory(employeeId);
	List<TransactionTimeline> returnList = new ArrayList<>();
	MovementTransactionData lastValidTrnForTerminationJoining = EmployeesService.getEmployeeData(employeeId).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) ? MovementsService.getLastValidMovementTransactionForReturnJoiningDate(employeeId, MovementTypesEnum.SUBJOIN.getCode(), ETRConfigurationService.getMovementTerminationJoiningApplyDate()) : null;
	if (lastValidTrnForTerminationJoining != null) {
	    returnList.add(constructSubjoinTerminationJoiningDateTransactionTimeline(lastValidTrnForTerminationJoining));
	}
	if (movementsTransactions.isEmpty()) {
	    return returnList;
	}

	for (int i = 0; i < movementsTransactions.size(); i++) {
	    if (movementsTransactions.get(i).getTransactionTypeCode() == TransactionTypesEnum.MVT_NEW_DECISION.getCode() && movementsTransactions.get(i).getSuccessorDecisionEffectFlag() == null && HijriDateService.getHijriSysDate().before(movementsTransactions.get(i).getExecutionDate())) {
		returnList.addAll(constructFutureMovementTransactionTimeline(movementsTransactions.get(i)));
		return returnList;
	    } else if (movementsTransactions.get(i).getEndDate() != null && HijriDateService.getHijriSysDate().before(movementsTransactions.get(i).getEndDate())) {
		TransactionTimeline transaction = consturctActiveTransactionTimeline(movementsTransactions.subList(i, movementsTransactions.size()));
		if (transaction != null) {
		    returnList.add(transaction);
		    return returnList;
		}
	    }
	}
	return returnList;

    }

    private static String getMovementTransactionDescription(MovementTransactionData movementsTransaction) {
	String description = "";
	if (movementsTransaction.getReplacementTransId() != null) {
	    description = getMessage("label_moveByReplacement");
	} else if (movementsTransaction.getFreezeJobId() != null) {
	    description = getMessage("label_moveByJobFreeze");
	} else if (movementsTransaction.getMovementTypeId() == MovementTypesEnum.MANDATE.getCode() || movementsTransaction.getMovementTypeId() == MovementTypesEnum.SECONDMENT.getCode()) {
	    description = movementsTransaction.getMovementTypeDesc();
	} else {
	    description = ((movementsTransaction.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && movementsTransaction.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()) && movementsTransaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode()) ? getMessage("label_assignment") : movementsTransaction.getMovementTypeDesc();
	    description += " " + (movementsTransaction.getLocationFlag() == LocationFlagsEnum.INTERNAL.getCode() ? getMessage("label_internal") : getMessage("label_external"));
	}
	return description;
    }

    private static String calculatePeriod(String days, String weeks, String months) {
	String period = "";
	if (!months.equals("-") && !months.equals("0"))
	    period += months + " " + getMessage("label_month");
	if (!weeks.equals("-") && !weeks.equals("0"))
	    period += weeks + " " + getMessage("label_week");
	if (!days.equals("-") && !days.equals("0"))
	    period = (period.isEmpty() ? "" : period + " " + getMessage("label_and")) + " " + days + " " + getMessage("label_day");
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

    private static TransactionTimeline constructSubjoinTerminationJoiningDateTransactionTimeline(MovementTransactionData movementsTransaction) {
	TransactionTimeline transaction = new TransactionTimeline();
	String period = calculatePeriod(movementsTransaction.getPeriodDays() == null ? "-" : movementsTransaction.getPeriodDays() + "", "-", movementsTransaction.getPeriodMonths() == null ? "-" : movementsTransaction.getPeriodMonths() + "");
	transaction.setPeriod(period);
	transaction.setTransactionTypeDescription(getMessage("label_subjoinTerminationJoiningDate"));
	transaction.setDueDateString(movementsTransaction.getEndDateString());
	return transaction;
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

    private static List<TransactionTimeline> sortLists(List<TransactionTimeline> movementTransactions, List<TransactionTimeline> allTransactionsExceptMovement) throws BusinessException {
	List<TransactionTimeline> allTransactions = new ArrayList<>();
	for (TransactionTimeline transaction : allTransactionsExceptMovement) {
	    if (transaction.getDays() == null && transaction.getWeeks() == null && transaction.getMonths() == null) {
		Integer[] diff = HijriDateService.hijriDateDiffInMonthsAndDays(transaction.getStartDateString(), transaction.getEndDateString());
		transaction.setPeriod(calculatePeriod(diff[0].toString() + "", "-", diff[1].toString() + ""));
	    } else
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
