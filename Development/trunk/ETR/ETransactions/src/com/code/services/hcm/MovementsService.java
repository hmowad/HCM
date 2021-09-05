package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.additionalspecializations.EmployeeAdditionalSpecializationData;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransaction;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.movements.MovementType;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.log.EmployeeLog;
import com.code.dal.orm.log.EmployeeLogData;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTransactionViewsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.buswfcoop.BusinessWorkflowCooperation;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.integration.PayrollEngineService;
import com.code.services.log.LogService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

/**
 * This class consists of static methods to operate on and handle movements services for officers, soldiers, and civilians.
 */
public class MovementsService extends BaseService {
    private static final int SOLDIERS_SUBJOIN_MAX_PERIOD = ETRConfigurationService.getMovementsSoldiersSubjoinPeriodDaysMax(); // 2 years
    private static final int PERSONS_SUBJOIN_MAX_PERIOD = ETRConfigurationService.getMovementsPersonsSubjoinPeriodDaysMax(); // 1 year
    private static final int PERSONS_MIN_PERIOD_TO_MOVE = ETRConfigurationService.getMovementsPersonsMinPeriodDaysToMove(); // 1 year
    private static final int PERSONS_MIN_PERIOD_TO_SUBJOIN = ETRConfigurationService.getMovementsPersonsMinPeriodDaysToSubjoin(); // 1 year

    /**
     * private constructor to prevent instantiation
     * 
     */
    private MovementsService() {

    }

    /*
     * ************************************* Movement Type **********************************************
     */
    /**
     * Wrapper for {@link #searchMovementTypes(long, String)}
     * 
     * @return list of movement types
     * @throws BusinessException
     *             if any error occurs
     * @see #searchMovementTypes(long, String)
     */
    public static List<MovementType> getAllMovementTypes() throws BusinessException {
	return searchMovementTypes(FlagsEnum.ALL.getCode(), null);
    }

    /**
     * Wrapper for {@link #searchMovementTypes(long, String)}
     * 
     * @param id
     *            id of the movement type
     * @return list of movement types
     * @throws BusinessException
     *             if any error occurs
     * @see #searchMovementTypes(long, String)
     */
    public static MovementType getMovementTypeById(long id) throws BusinessException {
	List<MovementType> movementTypes = searchMovementTypes(id, null);
	if (movementTypes.size() > 0)
	    return movementTypes.get(0);
	return null;
    }

    /**
     * Searches for a movement type using its id and description
     * 
     * @param id
     *            id of the movement type
     * @param description
     *            description of the movement type
     * 
     * @return list of the movement types
     * @throws BusinessException
     *             if any errors happen
     */
    private static List<MovementType> searchMovementTypes(long id, String description) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);
	    qParams.put("P_DESC", description == null || description.length() == 0 ? FlagsEnum.ALL.getCode() + "" : description);

	    return DataAccess.executeNamedQuery(MovementType.class, QueryNamesEnum.HCM_SEARCH_MOVEMENT_TYPES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*
     * ************************************ Movement Transactions operations ****************************************
     */
    /**
     * Used to construct transactions of the extend/end/cancel of registrations screens based on on transactions which we search for using the following parameters
     * 
     * @param decisionNumber
     *            decision number of the movement transactions we search for
     * @param decisionDate
     *            decision date of the movement transactions we search for
     * @param categoriesIds
     *            categories id of the movement transactions we search for
     * @param movementTypeId
     *            movement type id of the movement transactions we search for
     * @param transactionTypeCode
     *            transaction type code of the constructed movement transactions
     * @param etrFlag
     *            etrFlag of the movement transactions we search for
     * @return list of the constructed movement transaction
     * @throws BusinessException
     *             if any error occurs
     * @see getValidDecisionMembers(String, Date, Long[], long, long, int), #constructMovementTransaction(long, Date, Date, Long, String, Integer, Integer, Integer, String, String, Date, Integer, long, int)
     */
    public static List<MovementTransactionData> constructMovementTransactionsByDecisionInfo(String decisionNumber, Date decisionDate, Long[] categoriesIds, long movementTypeId, int transactionTypeCode, int etrFlag) throws BusinessException {
	List<MovementTransactionData> movementTransactions = getValidDecisionMembers(decisionNumber, decisionDate, categoriesIds, movementTypeId, FlagsEnum.ALL.getCode(), etrFlag);
	List<MovementTransactionData> resultTransactions = new ArrayList<MovementTransactionData>();
	if (movementTransactions != null && movementTransactions.size() > 0) {
	    for (MovementTransactionData movementTransaction : movementTransactions) {
		resultTransactions.add(constructMovementTransaction(movementTransaction.getEmployeeId(), movementTransaction.getExecutionDate(), movementTransaction.getEndDate(), movementTransaction.getUnitId(), movementTransaction.getUnitFullName(), movementTransaction.getPeriodDays(), movementTransaction.getPeriodMonths(), movementTransaction.getLocationFlag(), movementTransaction.getLocation(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(),
			movementTransaction.getReasonType(), movementTransaction.getMovementTypeId(), transactionTypeCode));
	    }
	}
	return resultTransactions;
    }

    /**
     * Used to construct transaction for the registrations screens
     * 
     * @param employeeId
     *            employee id of the constructed transaction
     * @param executionDate
     *            execution date of the constructed transaction
     * @param endDate
     *            end date of the constructed transaction
     * @param unitId
     *            unit id of the constructed transaction
     * @param unitFullName
     *            unit full name of the constructed transaction
     * @param periodDays
     *            period days of the constructed transaction
     * @param periodMonths
     *            period months of the constructed transaction
     * @param locationFlag
     *            location flag of the constructed transaction
     * @param location
     *            location of the constructed transaction
     * @param decisionNumber
     *            decision number of the constructed transaction
     * @param decisionDate
     *            decision date of the constructed transaction
     * @param reasonType
     *            movement reason type of the constructed transaction
     * @param movementTypeId
     *            movement type id of the constructed transaction
     * @param transactionTypeCode
     *            transaction type code of the constructed transaction
     * @return list of the constructed movement transactions
     * @throws BusinessException
     *             if any error occurs
     */
    public static MovementTransactionData constructMovementTransaction(long employeeId, Date executionDate, Date endDate, Long unitId, String unitFullName, Integer periodDays, Integer periodMonths, Integer locationFlag, String location, String decisionNumber, Date decisionDate, Integer reasonType, long movementTypeId, int transactionTypeCode) throws BusinessException {

	MovementTransactionData movementTransaction = new MovementTransactionData();

	EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	if (employee == null)
	    throw new BusinessException("error_employeeDataError");

	JobData job = null;
	if (employee.getJobId() != null)
	    job = JobsService.getJobById(employee.getJobId());
	movementTransaction.setEmployeeId(employeeId);
	movementTransaction.setEmployeeName(employee.getName());

	movementTransaction.setMovementTypeId(movementTypeId);
	movementTransaction.setMovementTypeDesc(MovementsService.getMovementTypeById(movementTypeId).getDescription());

	movementTransaction.setCategoryId(employee.getCategoryId());
	movementTransaction.setCategoryDesc(employee.getCategoryDesc());

	movementTransaction.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(transactionTypeCode, TransactionClassesEnum.MOVEMENTS.getCode()).getId());

	movementTransaction.setExecutionDateFlag(FlagsEnum.OFF.getCode());
	movementTransaction.setVacationGrantFlag(FlagsEnum.OFF.getCode());
	movementTransaction.setManagerFlag(FlagsEnum.OFF.getCode());
	movementTransaction.setEtrFlag(FlagsEnum.OFF.getCode());
	movementTransaction.setMigrationFlag(FlagsEnum.OFF.getCode());
	movementTransaction.setSecurityUnitFlag(FlagsEnum.OFF.getCode());

	movementTransaction.setTransEmpJobCode(employee.getJobCode());
	movementTransaction.setTransEmpJobName(employee.getJobDesc());
	movementTransaction.setTransEmpJobRankDesc(job != null ? job.getRankDescription() : null);
	movementTransaction.setTransEmpJobMinorSpecDesc(employee.getMinorSpecDesc());
	movementTransaction.setTransEmpRankDesc(employee.getRankDesc() + (employee.getRankTitleDesc() != null ? " " + employee.getRankTitleDesc() : ""));
	movementTransaction.setTransEmpUnitFullName(employee.getOfficialUnitFullName());
	movementTransaction.setTransEmpJobClassJobCode(employee.getJobClassificationCode());
	movementTransaction.setTransEmpJobClassJobDesc(employee.getJobClassificationDesc());

	movementTransaction.setReasonType(reasonType);
	movementTransaction.setLocationFlag(locationFlag);

	movementTransaction.setVerbalOrderFlag(FlagsEnum.OFF.getCode());
	movementTransaction.setRequestTransactionFlag(FlagsEnum.OFF.getCode());
	movementTransaction.setSequentialMvtFlag(FlagsEnum.OFF.getCode());
	movementTransaction.setTransferAllowanceFlag(FlagsEnum.OFF.getCode());

	if (transactionTypeCode != TransactionTypesEnum.MVT_NEW_DECISION.getCode()) {
	    movementTransaction.setUnitId(unitId);
	    movementTransaction.setUnitFullName(unitFullName);
	    movementTransaction.setLocation(location);
	    movementTransaction.setBasedOnDecisionNumber(decisionNumber);
	    movementTransaction.setBasedOnDecisionDate(decisionDate);

	    if (transactionTypeCode == TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode()) {
		movementTransaction.setExecutionDate(HijriDateService.addSubHijriDays(endDate, 1));
	    } else if (transactionTypeCode == TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode()) {
		movementTransaction.setExecutionDate(executionDate);
	    } else if (transactionTypeCode == TransactionTypesEnum.MVT_CANCEL_DECISION.getCode()) {
		movementTransaction.setExecutionDate(executionDate);
		movementTransaction.setEndDate(endDate);
		movementTransaction.setPeriodDays(periodDays);
		movementTransaction.setPeriodMonths(periodMonths);
	    }
	}

	return movementTransaction;
    }

    /**
     * The main method in movements transaction, used for handling all movement transactions (validating, inserting and doing effects for transactions)
     * 
     * @param processName
     *            process name of the movement transactions
     * @param movementTransactions
     *            movement transactions to be handled
     * @param isReplacementMovement
     *            Determines if the movement transactions are relacement movement type or not
     * @param requestId
     *            instance id of the workflow that created the transactions
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    public static void handleMovementsTransactions(String processName, List<MovementTransactionData> movementTransactions, boolean isReplacementMovement, Long requestId, int transactionSourceView, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (movementTransactions != null && movementTransactions.size() > 0) {
		if (!isOpenedSession)
		    session.beginTransaction();
		long movementTypeId = movementTransactions.get(0).getMovementTypeId().longValue();
		if (movementTypeId == MovementTypesEnum.MOVE.getCode())
		    // handle Move, Replacement Move, Move by freeze, and Move
		    // registration
		    handleMoveTransactions(movementTransactions, isReplacementMovement, processName, requestId, transactionSourceView, session);
		if (movementTypeId == MovementTypesEnum.SUBJOIN.getCode())
		    // handle Subjoin, and Subjoin registration
		    handleSubjoinTransactions(movementTransactions, processName, requestId, transactionSourceView, session);
		if (movementTypeId == MovementTypesEnum.ASSIGNMENT.getCode())
		    // handle Assignment, and Assignment registration
		    handleAssignmentTransactions(movementTransactions, processName, requestId, transactionSourceView, session);
		if (movementTypeId == MovementTypesEnum.MANDATE.getCode())
		    // handle MANDATE
		    handleMandateTransactions(movementTransactions, processName, requestId, session);
		if (movementTypeId == MovementTypesEnum.SECONDMENT.getCode())
		    // handle SECONDMENT
		    handleSecondmentTransactions(movementTransactions, processName, requestId, session);
		if (movementTypeId == MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode())
		    // handle Internal assignments
		    handleInternalAssignmentTransactions(movementTransactions, session);

		if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode())) {
		    doPayrollIntegration(movementTransactions, FlagsEnum.OFF.getCode(), session);
		}
		if (!isOpenedSession)
		    session.commitTransaction();
	    }
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void doPayrollIntegration(List<MovementTransactionData> movementTransactions, Integer resendFlag, CustomSession session) throws BusinessException {
	List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>();
	Long adminDecision = null;
	Boolean endDateFlag = false;
	if (movementTransactions != null && movementTransactions.size() > 0) {
	    if (movementTransactions.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		if (movementTransactions.get(0).getMovementTypeId().longValue() == MovementTypesEnum.MOVE.getCode()) {
		    if (movementTransactions.get(0).getJoiningDate() != null)
			adminDecision = AdminDecisionsEnum.OFFICERS_MOVE_JOINING_DATE_REQUEST.getCode();
		    else {
			if (movementTransactions.get(0).getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()) {
			    if (movementTransactions.get(0).getRequestTransactionFlag().equals(FlagsEnum.ON.getCode()))
				adminDecision = AdminDecisionsEnum.OFFICERS_MOVE_REQUEST.getCode();
			    else
				adminDecision = AdminDecisionsEnum.OFFICERS_MOVE_DECISION_REQUEST.getCode();
			} else {
			    adminDecision = AdminDecisionsEnum.OFFICERS_MOVE_REGISTERATION.getCode();
			}
		    }
		} else if (movementTransactions.get(0).getMovementTypeId().longValue() == MovementTypesEnum.SUBJOIN.getCode()) {
		    if (movementTransactions.get(0).getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId()) {
			if (movementTransactions.get(0).getReturnJoiningDate() != null)
			    adminDecision = AdminDecisionsEnum.OFFICERS_SUBJOIN_RETURN_JOINING_DATE_REQUEST.getCode();
			else if (movementTransactions.get(0).getJoiningDate() != null)
			    adminDecision = AdminDecisionsEnum.OFFICERS_SUBJOIN_JOINING_DATE_REQUEST.getCode();
			else {
			    endDateFlag = true;
			    if (movementTransactions.get(0).getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()) {
				if (movementTransactions.get(0).getRequestTransactionFlag().equals(FlagsEnum.ON.getCode()))
				    adminDecision = AdminDecisionsEnum.OFFICERS_SUBJOIN_REQUEST.getCode();
				else
				    adminDecision = AdminDecisionsEnum.OFFICERS_SUBJOIN_DECISION_REQUEST.getCode();
			    } else {
				adminDecision = AdminDecisionsEnum.OFFICERS_SUBJOIN_REGISTERATION.getCode();
			    }
			}
		    } else if (movementTransactions.get(0).getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId()) {
			endDateFlag = true;
			if (movementTransactions.get(0).getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()) {
			    if (movementTransactions.get(0).getRequestTransactionFlag().equals(FlagsEnum.ON.getCode()))
				adminDecision = AdminDecisionsEnum.OFFICERS_EXTEND_SUBJOIN_REQUEST.getCode();
			    else
				adminDecision = AdminDecisionsEnum.OFFICERS_EXTEND_SUBJOIN_DECISION_REQUEST.getCode();
			} else {
			    adminDecision = AdminDecisionsEnum.OFFICERS_EXTEND_SUBJOIN_REGISTERATION.getCode();
			}
		    } else if (movementTransactions.get(0).getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId()) {
			if (movementTransactions.get(0).getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()) {
			    if (movementTransactions.get(0).getRequestTransactionFlag().equals(FlagsEnum.ON.getCode()))
				adminDecision = AdminDecisionsEnum.OFFICERS_CANCEL_SUBJOIN_REQUEST.getCode();
			    else
				adminDecision = AdminDecisionsEnum.OFFICERS_CANCEL_SUBJOIN_DECISION_REQUEST.getCode();
			} else {
			    adminDecision = AdminDecisionsEnum.OFFICERS_CANCEL_SUBJOIN_REGISTERATION.getCode();
			}
		    } else if (movementTransactions.get(0).getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId()) {
			if (movementTransactions.get(0).getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()) {
			    if (movementTransactions.get(0).getRequestTransactionFlag().equals(FlagsEnum.ON.getCode()))
				adminDecision = AdminDecisionsEnum.OFFICERS_TERMINATE_SUBJOIN_REQUEST.getCode();
			    else
				adminDecision = AdminDecisionsEnum.OFFICERS_TERMINATE_SUBJOIN_DECISION_REQUEST.getCode();
			} else {
			    adminDecision = AdminDecisionsEnum.OFFICERS_TERMINATE_SUBJOIN_REGISTERATION.getCode();
			}
		    }
		} else if (movementTransactions.get(0).getMovementTypeId().longValue() == MovementTypesEnum.ASSIGNMENT.getCode()) {
		    endDateFlag = true;
		    if (movementTransactions.get(0).getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()) {
			adminDecision = AdminDecisionsEnum.OFFICERS_INTERNAL_ASSIGNMENT_DECISION_REQUEST.getCode();
		    } else {
			adminDecision = AdminDecisionsEnum.OFFICERS_EXTERNAL_ASSIGNMENT_REGISTRATION.getCode();
		    }
		}
	    }
	    if (adminDecision != null) {
		for (MovementTransactionData movementTransactionData : movementTransactions) {
		    String gregStartDateString = movementTransactionData.getReturnJoiningDate() != null ? HijriDateService.hijriToGregDateString(movementTransactionData.getReturnJoiningDateString()) : movementTransactionData.getJoiningDate() != null ? HijriDateService.hijriToGregDateString(movementTransactionData.getJoiningDateString()) : HijriDateService.hijriToGregDateString(movementTransactionData.getExecutionDateString());
		    String gregEndDateString = endDateFlag ? HijriDateService.hijriToGregDateString(movementTransactionData.getEndDateString()) : null;
		    EmployeeData employee = EmployeesService.getEmployeeData(movementTransactionData.getEmployeeId());
		    String decisionNumber = (movementTransactionData.getJoiningDate() != null || movementTransactionData.getReturnJoiningDate() != null) ? System.currentTimeMillis() + "" : movementTransactionData.getDecisionNumber();
		    String originalDecisionNumber = (movementTransactionData.getJoiningDate() != null || movementTransactionData.getReturnJoiningDate() != null) ? movementTransactionData.getDecisionNumber() : movementTransactionData.getBasedOnDecisionNumber();
		    adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(movementTransactionData.getEmployeeId(), employee.getName(), movementTransactionData.getId(), null, gregStartDateString, gregEndDateString, decisionNumber, originalDecisionNumber));
		}
		String gregExecutionDateString = HijriDateService.hijriToGregDateString(movementTransactions.get(0).getExecutionDateString());
		String gregDecisionDateString = HijriDateService.hijriToGregDateString(movementTransactions.get(0).getDecisionDateString());
		Long unitId = (movementTransactions.get(0).getUnitId() == null ? UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId() : movementTransactions.get(0).getUnitId());
		Integer originalDecisionFlag = (movementTransactions.get(0).getJoiningDate() != null || movementTransactions.get(0).getReturnJoiningDate() != null) ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode();
		if (session != null)
		    session.flushTransaction();
		PayrollEngineService.doPayrollIntegration(adminDecision, movementTransactions.get(0).getCategoryId(), gregExecutionDateString, adminDecisionEmployeeDataList, movementTransactions.get(0).getAttachments(), unitId, gregDecisionDateString, DataAccess.getTableName(MovementTransaction.class), resendFlag, originalDecisionFlag, session);
	    }
	}
    }

    public static void payrollIntegrationFailureHandle(String decisionNumber, Date decisionDate, CustomSession session) throws BusinessException {
	List<MovementTransactionData> movementTransactionDataList = getMovementTransactionsByDecisionNumberAndDecisionDate(decisionNumber, decisionDate, decisionDate);
	if (movementTransactionDataList != null && movementTransactionDataList.size() != 0) {
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(movementTransactionDataList, FlagsEnum.ON.getCode(), session);
	} else {
	    throw new BusinessException("error_transactionDataRetrievingError");
	}
    }

    /**
     * Handles the business of Move, Replacement Move, Move by freeze, and Move registration
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param isReplacementMovement
     *            Determines if the movement transactions are replacement movement type or not
     * @param processName
     *            process name of the movement transactions
     * @param requestId
     *            instance id of the workflow that created the transactions
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void handleMoveTransactions(List<MovementTransactionData> movementTransactions, boolean isReplacementMovement, String processName, Long requestId, int transactionSourceView, CustomSession session) throws BusinessException {
	// validate
	int replacementIndex = 1;
	for (MovementTransactionData movementTransaction : movementTransactions) {
	    validateMoveRules(movementTransaction, isReplacementMovement ? movementTransactions.get(replacementIndex).getEmployeeId() : null, requestId, transactionSourceView);
	    if (isReplacementMovement)
		replacementIndex = replacementIndex - 1;
	}

	// add transactions
	addMovementTransactions(movementTransactions, processName, session);
	if (isReplacementMovement) {
	    MovementTransactionData firstTransaction = movementTransactions.get(0);
	    MovementTransactionData secondTransaction = movementTransactions.get(1);
	    firstTransaction.setReplacementTransId(secondTransaction.getId());
	    secondTransaction.setReplacementTransId(firstTransaction.getId());
	    updateMovementTransactions(movementTransactions, session);
	}

	// do effects on employees, jobs, and units
	doMoveEffects(movementTransactions, isReplacementMovement, session);
    }

    /**
     * Handles the business of subjoin and subjoin registration
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param processName
     *            process name of the movement transactions
     * @param requestId
     *            instance id of the workflow that created the transactions
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void handleSubjoinTransactions(List<MovementTransactionData> movementTransactions, String processName, Long requestId, int transactionSourceView, CustomSession session) throws BusinessException {
	// Validate
	for (MovementTransactionData movementTransaction : movementTransactions) {
	    validateSubjoinRules(movementTransaction, requestId, transactionSourceView);
	}

	// add transactions
	addMovementTransactions(movementTransactions, processName, session);

	// do effects on employees, jobs, and units
	doSubjoinEffects(movementTransactions, session);
    }

    /**
     * Handles the business of officers assignment, and assignment registration
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param processName
     *            process name of the movement transactions
     * @param requestId
     *            instance id of the workflow that created the transactions
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void handleAssignmentTransactions(List<MovementTransactionData> movementTransactions, String processName, Long requestId, int transactionSourceView, CustomSession session) throws BusinessException {
	// validate
	for (MovementTransactionData movementTransaction : movementTransactions) {
	    validateAssignmentRules(movementTransaction, requestId, transactionSourceView);
	}

	// add transactions
	addMovementTransactions(movementTransactions, processName, session);

	// do effects on employees, jobs, and units
	doAssignmentEffect(movementTransactions, session);
    }

    /**
     * Handles the business of soldiers mandate
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param processName
     *            process name of the movement transactions
     * @param requestId
     *            instance id of the workflow that created the transactions
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void handleMandateTransactions(List<MovementTransactionData> movementTransactions, String processName, Long requestId, CustomSession session) throws BusinessException {
	// Validate
	for (MovementTransactionData movementTransaction : movementTransactions) {
	    validateMandateRules(movementTransaction, requestId);
	}

	// add transactions
	addMovementTransactions(movementTransactions, processName, session);

	// do effects on employees, jobs, and units
	doMandateEffects(movementTransactions, session);
    }

    /**
     * Handles the business of soldiers secondment
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param processName
     *            process name of the movement transactions
     * @param requestId
     *            instance id of the workflow that created the transactions
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void handleSecondmentTransactions(List<MovementTransactionData> movementTransactions, String processName, Long requestId, CustomSession session) throws BusinessException {
	// validate
	for (MovementTransactionData movementTransaction : movementTransactions) {
	    validateSecondmentRules(movementTransaction, requestId);
	}

	// add transactions
	addMovementTransactions(movementTransactions, processName, session);

	// do effects on employees, jobs, and units
	doSecondmentEffects(movementTransactions, session);
    }

    /**
     * Handles the business of internal assignment registration
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void handleInternalAssignmentTransactions(List<MovementTransactionData> movementTransactions, CustomSession session) throws BusinessException {
	// validate
	for (MovementTransactionData movementTransaction : movementTransactions) {
	    validateInternalAssignmentRules(movementTransaction);
	}

	// add transactions
	addMovementTransactions(movementTransactions, null, session);

	// do effects on employees, jobs, and units
	doInternalAssignmentEffects(movementTransactions, session);
    }

    /**
     * Apply the effect of move transaction on employees, jobs, and units
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param isReplacementMovement
     *            Determines if the movement transactions are replacement movement type or not
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    public static void doMoveEffects(List<MovementTransactionData> movementTransactions, boolean isReplacementMovement, CustomSession session) throws BusinessException {
	try {
	    long categoryId = movementTransactions.get(0).getCategoryId();
	    boolean isSoldiersMove = categoryId == CategoriesEnum.SOLDIERS.getCode();

	    EmployeeData replacementEmp = null;
	    if (isReplacementMovement) {
		replacementEmp = EmployeesService.getEmployeeData(movementTransactions.get(1).getEmployeeId());
	    }

	    List<Long> affectedEmployeesIds = new ArrayList<Long>();

	    Map<Long, UnitData> unitsMap = new HashMap<Long, UnitData>();
	    List<EmployeeData> employees = new ArrayList<EmployeeData>();
	    Map<Long, JobData> jobsMap = new HashMap<Long, JobData>();

	    boolean sequentialMovement = false;

	    List<JobData> jobsToFreeze = new ArrayList<JobData>();
	    List<JobData> jobsToUnfreeze = new ArrayList<JobData>();

	    for (MovementTransactionData movementTransaction : movementTransactions) {
		if (movementTransaction.getEffectFlag().intValue() == FlagsEnum.OFF.getCode())
		    continue;

		if (movementTransaction.getSequentialMvtFlagBoolean()) {
		    sequentialMovement = true;
		    break;
		}
	    }

	    for (MovementTransactionData movementTransaction : movementTransactions) {

		if (movementTransaction.getEffectFlag().intValue() == FlagsEnum.OFF.getCode())
		    continue;

		EmployeeData emp = (replacementEmp != null && movementTransaction.getEmployeeId().longValue() == replacementEmp.getEmpId().longValue()) ? replacementEmp : EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());

		// add empId to the affected list to do changes after effect like invalidating the inbox
		affectedEmployeesIds.add(emp.getEmpId());

		if (movementTransaction.getReasonType().intValue() == MovementsReasonTypesEnum.FOR_PUBLIC_INTEREST.getCode())
		    emp.setMovementBlacklistFlag(FlagsEnum.ON.getCode());

		if (!isReplacementMovement && movementTransaction.getFreezeJobId() == null) {
		    if (!sequentialMovement) {
			JobsService.changeJobStatus(JobsService.getJobById(emp.getJobId()), JobStatusEnum.VACANT.getCode(), session);
		    } else if (jobsMap.get(emp.getJobId()) == null) {
			jobsMap.put(emp.getJobId(), JobsService.getJobById(emp.getJobId()));
		    }

		}

		if (!isReplacementMovement) {
		    adjustUnitsManagers(unitsMap, emp, movementTransaction.getUnitId(), movementTransaction.getManagerFlagBoolean(), true);
		} else if (!emp.getEmpId().equals(replacementEmp.getEmpId())) {
		    UnitData empOfficialUnit = UnitsService.getUnitById(emp.getOfficialUnitId());
		    UnitData replacementEmpOfficialUnit = UnitsService.getUnitById(replacementEmp.getOfficialUnitId());

		    adjustUnitsManagers(unitsMap, emp, replacementEmp.getOfficialUnitId(), replacementEmp.getEmpId().equals(replacementEmpOfficialUnit.getOfficialManagerId()), true);
		    adjustUnitsManagers(unitsMap, replacementEmp, emp.getOfficialUnitId(), emp.getEmpId().equals(empOfficialUnit.getOfficialManagerId()), true);
		}

		if (movementTransaction.getLocationFlag().intValue() == FlagsEnum.ON.getCode()) {
		    emp.setServiceTerminationRegionId(emp.getPhysicalRegionId());
		    emp.setServiceTerminationUnitId(emp.getPhysicalUnitId());
		    emp.setStatusId(EmployeeStatusEnum.MOVED_EXTERNALLY.getCode());
		    emp.setJobId(null);
		    emp.setPhysicalUnitId(null);
		    emp.setServiceTerminationDate(movementTransaction.getExecutionDate());
		} else if (movementTransaction.getFreezeJobId() == null) {
		    if (!isReplacementMovement) {
			if (!sequentialMovement)
			    JobsService.changeJobStatus(JobsService.getJobById(movementTransaction.getJobId()), JobStatusEnum.OCCUPIED.getCode(), session);
			else if (jobsMap.get(movementTransaction.getJobId()) == null) {
			    jobsMap.put(movementTransaction.getJobId(), JobsService.getJobById(movementTransaction.getJobId()));
			}
		    } else if (replacementEmp.getEmpId().longValue() != movementTransaction.getEmployeeId().longValue()) {
			replacementEmp.setJobId(null);
			emp.setJobId(null);
			EmployeesService.updateEmployee(replacementEmp, session);
			EmployeesService.updateEmployee(emp, session);
			session.flushTransaction();
		    }

		    emp.setStatusId(EmployeeStatusEnum.ON_DUTY.getCode());
		    emp.setPhysicalUnitId(movementTransaction.getUnitId());
		    emp.setJobId(movementTransaction.getJobId());
		}

		if (isSoldiersMove && movementTransaction.getFreezeJobId() != null) {
		    JobData empJob = JobsService.getJobById(emp.getJobId());

		    JobData jobToFreeze;
		    if (movementTransaction.getFreezeJobId().longValue() == emp.getJobId().longValue()) {
			jobToFreeze = empJob;
		    } else {
			jobToFreeze = JobsService.getJobById(movementTransaction.getFreezeJobId());
		    }

		    JobsService.changeJobStatus(empJob, JobStatusEnum.VACANT.getCode(), session);

		    JobData jobToUnfreeze = JobsService.getJobById(movementTransaction.getJobId());

		    jobsToFreeze.add(jobToFreeze);
		    jobsToUnfreeze.add(jobToUnfreeze);

		    emp.setPhysicalUnitId(movementTransaction.getUnitId());
		    emp.setJobId(movementTransaction.getJobId());
		}

		if (!sequentialMovement) {
		    EmployeesService.updateEmployee(emp, session);

		    EmployeeLog employeeLog;
		    if (movementTransaction.getLocationFlag().intValue() == FlagsEnum.ON.getCode()) {
			employeeLog = new EmployeeLog.Builder().constructCommonFields(emp.getEmpId(), FlagsEnum.OFF.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    } else {
			employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(movementTransaction.getUnitId()).setJobId(movementTransaction.getJobId()).setBasicJobNameId(JobsService.getJobById(movementTransaction.getJobId()).getBasicJobNameId()).setOfficialUnitId(movementTransaction.getUnitId())
				.constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    }
		    LogService.logEmployeeData(employeeLog, session);

		} else {
		    employees.add(emp);
		}
	    }

	    if (isSoldiersMove && jobsToFreeze.size() > 0) {
		boolean validateJobsBalances = !movementTransactions.get(0).getExecutionDate().after(movementTransactions.get(0).getDecisionDate());
		JobsService.handleJobsTransactions(null, null, null, jobsToFreeze, jobsToUnfreeze, null, null, null, movementTransactions.get(0).getDecisionNumber(), movementTransactions.get(0).getDecisionDate(), movementTransactions.get(0).getExecutionDate(), movementTransactions.get(0).getDecisionApprovedId(), false, false, false, validateJobsBalances, session);
		for (JobData jobToUnfreeze : jobsToUnfreeze)
		    JobsService.changeJobStatus(jobToUnfreeze, JobStatusEnum.OCCUPIED.getCode(), session);
	    }

	    // Update Employees and jobs in case of sequential move
	    if (sequentialMovement) {

		Map<Long, Long> empsJobs = new HashMap<Long, Long>();
		Map<Long, MovementTransactionData> empsMovTransactionsMap = new HashMap<Long, MovementTransactionData>();
		for (MovementTransactionData movementTransaction : movementTransactions) {
		    empsMovTransactionsMap.put(movementTransaction.getEmployeeId(), movementTransaction);
		}
		for (EmployeeData emp : employees) {
		    JobData jobData = jobsMap.get(emp.getJobId());

		    MovementTransactionData empMovementTransactionData = empsMovTransactionsMap.get(emp.getEmpId());
		    EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(empMovementTransactionData.getUnitId()).setJobId(empMovementTransactionData.getJobId()).setBasicJobNameId(jobData.getBasicJobNameId()).setOfficialUnitId(empMovementTransactionData.getUnitId())
			    .constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), empMovementTransactionData.getDecisionNumber(), empMovementTransactionData.getDecisionDate(), empMovementTransactionData.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(employeeLog, session);

		    if (jobData.getStatus() != JobStatusEnum.OCCUPIED.getCode()) {
			JobsService.changeJobStatus(jobData, JobStatusEnum.OCCUPIED.getCode(), session);
		    }
		    jobsMap.remove(jobData.getId());

		    empsJobs.put(emp.getEmpId(), emp.getJobId());

		    emp.setJobId(null);
		    EmployeesService.updateEmployee(emp, session);
		}

		for (JobData job : jobsMap.values()) {
		    JobsService.changeJobStatus(job, JobStatusEnum.VACANT.getCode(), session);
		}

		session.flushTransaction();

		for (EmployeeData emp : employees) {
		    emp.setJobId(empsJobs.get(emp.getEmpId()));
		    EmployeesService.updateEmployee(emp, session);
		}
	    }

	    // Apply the changes made in he adjust Units Managers method
	    UnitsService.modifyUnitsManagers(unitsMap.values(), true, session);

	    // Apply further changes on the affected employees like invlidating thier inbox
	    doChangesOnAffectedEmployees(affectedEmployeesIds.toArray(new Long[affectedEmployeesIds.size()]), session);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Apply the effect of subjoin transaction on employees, and units
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void doSubjoinEffects(List<MovementTransactionData> movementTransactions, CustomSession session) throws BusinessException {
	long categoryId = movementTransactions.get(0).getCategoryId();
	long transactionTypeId = movementTransactions.get(0).getTransactionTypeId();
	boolean isNewDecision = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
	boolean isCancellation = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
	boolean isTermination = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();

	// do effects on employees, jobs, and units
	if (isCancellation || isTermination) {
	    doCancellationAndTerminationEffect(movementTransactions, isTermination, session);
	} else {

	    List<Long> affectedEmployeesIds = new ArrayList<Long>();

	    Date sysDate = HijriDateService.getHijriSysDate();

	    Map<Long, UnitData> unitsMap = new HashMap<Long, UnitData>();

	    for (MovementTransactionData movementTransaction : movementTransactions) {
		if (movementTransaction.getEffectFlag().intValue() == FlagsEnum.OFF.getCode())
		    continue;

		EmployeeData emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());

		if (emp == null)
		    throw new BusinessException("error_employeeDataError");

		// add empId to the affected list to do changes after effect like invalidating the inbox
		affectedEmployeesIds.add(emp.getEmpId());

		if (sysDate.after(movementTransaction.getEndDate())) {
		    EmployeeLogData lastEmployeeLog = LogService.getLastEmployeeLogBeforeGivenDate(emp.getEmpId(), movementTransaction.getExecutionDate());
		    EmployeeLog transactionEmployeeLog = new EmployeeLog.Builder().setPhysicalUnitId(movementTransaction.getLocationFlag().intValue() == FlagsEnum.ON.getCode() ? lastEmployeeLog.getOfficialUnitId() : movementTransaction.getUnitId())
			    .constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(transactionEmployeeLog, session);
		    EmployeeLog revertEmployeeLog = new EmployeeLog.Builder().setPhysicalUnitId(lastEmployeeLog.getOfficialUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), HijriDateService.addSubHijriDays(movementTransaction.getEndDate(), 1), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(revertEmployeeLog, session);
		    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
			MovementTransactionData lastTransaction = getLastMovementTransactionByEmployeeId(movementTransaction.getEmployeeId());
			if (lastTransaction != null && lastTransaction.getMovementTypeId().equals(MovementTypesEnum.ASSIGNMENT.getCode()) && lastTransaction.getEndDate() == null && !lastTransaction.getExecutionDate().after(movementTransaction.getExecutionDate())) {
			    // If employee was assigned with no period and
			    // we make a subjoin that ends in the past and
			    // starts after assignment, we should revert
			    // assignment effect
			    if (lastTransaction.getManagerFlagBoolean()) {
				adjustUnitsManagers(unitsMap, emp, null, false, false);
			    }
			    emp.setStatusId(EmployeeStatusEnum.ON_DUTY.getCode());
			    emp.setPhysicalUnitId(emp.getOfficialUnitId());
			    EmployeesService.updateEmployee(emp, session);

			}
		    }

		    continue;
		}

		if (movementTransaction.getReasonType().intValue() == MovementsReasonTypesEnum.FOR_PUBLIC_INTEREST.getCode())
		    emp.setMovementBlacklistFlag(FlagsEnum.ON.getCode());

		adjustUnitsManagers(unitsMap, emp, movementTransaction.getUnitId(), movementTransaction.getManagerFlagBoolean(), false);

		if (categoryId != CategoriesEnum.OFFICERS.getCode() && categoryId != CategoriesEnum.SOLDIERS.getCode()) {
		    if (movementTransaction.getLocationFlag().intValue() == FlagsEnum.ON.getCode()) {
			emp.setPhysicalUnitId(emp.getOfficialUnitId());
			emp.setStatusId(EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode());
		    } else {
			emp.setStatusId(EmployeeStatusEnum.PERSONS_SUBJOINED.getCode());
			emp.setPhysicalUnitId(movementTransaction.getUnitId());
		    }
		} else {
		    if (movementTransaction.getLocationFlag().intValue() == FlagsEnum.ON.getCode()) {
			emp.setPhysicalUnitId(emp.getOfficialUnitId());
			emp.setStatusId(EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode());
		    } else {
			emp.setPhysicalUnitId(movementTransaction.getUnitId());
			emp.setStatusId(EmployeeStatusEnum.SUBJOINED.getCode());
		    }
		}

		EmployeesService.updateEmployee(emp, session);
		if (isNewDecision || (movementTransaction.getExecutionDate().before(sysDate))) { // for extension
		    EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(emp.getPhysicalUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(employeeLog, session);
		}

	    }

	    // Apply the changes made in he adjust Units Managers method
	    UnitsService.modifyUnitsManagers(unitsMap.values(), false, session);

	    // Apply further changes on the affected employees like invlidating thier inbox
	    doChangesOnAffectedEmployees(affectedEmployeesIds.toArray(new Long[affectedEmployeesIds.size()]), session);
	}

    }

    /**
     * Apply the effect of assignment (officers only) transaction on officers, and units
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void doAssignmentEffect(List<MovementTransactionData> movementTransactions, CustomSession session) throws BusinessException {
	// do effects on employees, jobs, and units
	Date sysDate = HijriDateService.getHijriSysDate();
	Map<Long, UnitData> unitsMap = new HashMap<Long, UnitData>();

	List<Long> affectedEmployeesIds = new ArrayList<Long>();

	for (MovementTransactionData movementTransaction : movementTransactions) {
	    if (movementTransaction.getEffectFlag().intValue() == FlagsEnum.OFF.getCode() || (movementTransaction.getEndDate() == null && countEmployeeTransactionsAfterGivenExecutionDate(movementTransaction.getEmployeeId(), movementTransaction.getExecutionDate()) > 0))
		continue;

	    EmployeeData emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());

	    if (emp == null)
		throw new BusinessException("error_employeeDataError");

	    // add empId to the affected list to do changes after effect like invalidating the inbox
	    affectedEmployeesIds.add(emp.getEmpId());

	    int locationFlag = movementTransaction.getLocationFlag().intValue();
	    if (sysDate.after(locationFlag == LocationFlagsEnum.EXTERNAL.getCode() ? movementTransaction.getEndDate() : movementTransaction.getExecutionDate())) {

		EmployeeLogData lastEmployeeLog = LogService.getLastEmployeeLogBeforeGivenDate(emp.getEmpId(), movementTransaction.getExecutionDate());
		EmployeeLog transactionEmployeeLog = new EmployeeLog.Builder().setPhysicalUnitId(locationFlag == LocationFlagsEnum.EXTERNAL.getCode() ? lastEmployeeLog.getOfficialUnitId() : movementTransaction.getUnitId())
			.constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		LogService.logEmployeeData(transactionEmployeeLog, session);

		if (locationFlag == LocationFlagsEnum.EXTERNAL.getCode()) {
		    EmployeeLog revertEmployeeLog = new EmployeeLog.Builder().setPhysicalUnitId(lastEmployeeLog.getOfficialUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), HijriDateService.addSubHijriDays(movementTransaction.getEndDate(), 1), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(revertEmployeeLog, session);

		    MovementTransactionData lastTransaction = getLastMovementTransactionByEmployeeId(movementTransaction.getEmployeeId());
		    if (lastTransaction != null && lastTransaction.getMovementTypeId().equals(MovementTypesEnum.ASSIGNMENT.getCode()) && lastTransaction.getEndDate() == null && !lastTransaction.getExecutionDate().after(movementTransaction.getExecutionDate())) {
			// If employee was assigned with no period and we
			// make an assignment with period that ends in the
			// past and starts after assignment, we should
			// revert assignment effect
			if (lastTransaction.getManagerFlagBoolean()) {
			    adjustUnitsManagers(unitsMap, emp, null, false, false);
			}
			emp.setStatusId(EmployeeStatusEnum.ON_DUTY.getCode());
			emp.setPhysicalUnitId(emp.getOfficialUnitId());
			EmployeesService.updateEmployee(emp, session);

		    }
		}
		continue;
	    }

	    adjustUnitsManagers(unitsMap, emp, movementTransaction.getUnitId(), movementTransaction.getManagerFlagBoolean(), false);

	    if (locationFlag == LocationFlagsEnum.EXTERNAL.getCode()) {
		emp.setPhysicalUnitId(emp.getOfficialUnitId());
		emp.setStatusId(EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode());
	    } else {
		emp.setStatusId(EmployeeStatusEnum.ASSIGNED.getCode());
		emp.setPhysicalUnitId(movementTransaction.getUnitId());
	    }
	    EmployeesService.updateEmployee(emp, session);
	    EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(emp.getPhysicalUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
	    LogService.logEmployeeData(employeeLog, session);
	}

	// Apply the changes made in he adjust Units Managers method
	UnitsService.modifyUnitsManagers(unitsMap.values(), false, session);

	// Apply further changes on the affected employees like invlidating thier inbox
	doChangesOnAffectedEmployees(affectedEmployeesIds.toArray(new Long[affectedEmployeesIds.size()]), session);
    }

    /**
     * Apply the effect of mandate (soldiers only) transaction on soldiers, and units
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    public static void doMandateEffects(List<MovementTransactionData> movementTransactions, CustomSession session) throws BusinessException {
	long transactionTypeId = movementTransactions.get(0).getTransactionTypeId();
	boolean isNewDecision = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
	boolean isCancellation = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
	boolean isTermination = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();

	// do effects on employees, jobs, and units
	if (isCancellation || isTermination) {
	    doCancellationAndTerminationEffect(movementTransactions, isTermination, session);
	} else {
	    List<Long> affectedEmployeesIds = new ArrayList<Long>();

	    Date sysDate = HijriDateService.getHijriSysDate();
	    for (MovementTransactionData movementTransaction : movementTransactions) {
		if (movementTransaction.getEffectFlag().intValue() == FlagsEnum.OFF.getCode())
		    continue;

		EmployeeData emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());

		if (emp == null)
		    throw new BusinessException("error_employeeDataError");

		if (sysDate.after(movementTransaction.getEndDate())) {
		    EmployeeLogData lastEmployeeLog = LogService.getLastEmployeeLogBeforeGivenDate(emp.getEmpId(), movementTransaction.getExecutionDate());
		    EmployeeLog transactionEmployeeLog = new EmployeeLog.Builder().setPhysicalUnitId(lastEmployeeLog.getOfficialUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(transactionEmployeeLog, session);
		    EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(lastEmployeeLog.getOfficialUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), HijriDateService.addSubHijriDays(movementTransaction.getEndDate(), 1), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(employeeLog, session);
		    continue;
		}

		// add empId to the affected list to do changes after effect like invalidating the inbox
		affectedEmployeesIds.add(emp.getEmpId());

		emp.setStatusId(EmployeeStatusEnum.MANDATED.getCode());
		emp.setPhysicalUnitId(emp.getOfficialUnitId());
		EmployeesService.updateEmployee(emp, session);
		if (isNewDecision || (movementTransaction.getExecutionDate().before(sysDate))) { // for extension
		    EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(emp.getPhysicalUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(employeeLog, session);
		}
	    }

	    // Apply further changes on the affected employees like invlidating thier inbox
	    doChangesOnAffectedEmployees(affectedEmployeesIds.toArray(new Long[affectedEmployeesIds.size()]), session);
	}
    }

    /**
     * Apply the effect of secondment (soldiers only) transaction on soldiers, and units
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    public static void doSecondmentEffects(List<MovementTransactionData> movementTransactions, CustomSession session) throws BusinessException {
	long transactionTypeId = movementTransactions.get(0).getTransactionTypeId();
	boolean isNewDecision = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
	boolean isCancellation = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
	boolean isTermination = transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();

	// do effects on employees, jobs, and units
	if (isCancellation || isTermination) {
	    doCancellationAndTerminationEffect(movementTransactions, isTermination, session);
	} else {
	    List<Long> affectedEmployeesIds = new ArrayList<Long>();

	    Date sysDate = HijriDateService.getHijriSysDate();
	    for (MovementTransactionData movementTransaction : movementTransactions) {
		if (movementTransaction.getEffectFlag().intValue() == FlagsEnum.OFF.getCode())
		    continue;

		EmployeeData emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());

		if (emp == null)
		    throw new BusinessException("error_employeeDataError");

		if (sysDate.after(movementTransaction.getEndDate())) {
		    EmployeeLogData lastEmployeeLog = LogService.getLastEmployeeLogBeforeGivenDate(emp.getEmpId(), movementTransaction.getExecutionDate());
		    EmployeeLog transactionEmployeeLog = new EmployeeLog.Builder().setPhysicalUnitId(lastEmployeeLog.getOfficialUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(transactionEmployeeLog, session);
		    EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(lastEmployeeLog.getOfficialUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), HijriDateService.addSubHijriDays(movementTransaction.getEndDate(), 1), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(employeeLog, session);
		    continue;
		}

		// add empId to the affected list to do changes after effect like invalidating the inbox
		affectedEmployeesIds.add(emp.getEmpId());

		emp.setStatusId(EmployeeStatusEnum.SECONDMENTED.getCode());
		emp.setPhysicalUnitId(emp.getOfficialUnitId());
		EmployeesService.updateEmployee(emp, session);
		if (isNewDecision || (movementTransaction.getExecutionDate().before(sysDate))) { // for extension
		    EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(emp.getPhysicalUnitId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		    LogService.logEmployeeData(employeeLog, session);
		}
	    }

	    // Apply further changes on the affected employees like invlidating thier inbox
	    doChangesOnAffectedEmployees(affectedEmployeesIds.toArray(new Long[affectedEmployeesIds.size()]), session);
	}
    }

    /**
     * Apply the effect of internal assignment transaction on employees, and units
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    public static void doInternalAssignmentEffects(List<MovementTransactionData> movementTransactions, CustomSession session) throws BusinessException {
	Map<Long, UnitData> unitsMap = new HashMap<Long, UnitData>();

	List<Long> affectedEmployeesIds = new ArrayList<Long>();

	// do effects on employees, and units
	for (MovementTransactionData movementTransaction : movementTransactions) {
	    EmployeeData empData = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());

	    if (empData == null)
		throw new BusinessException("error_employeeDataError");

	    // add empId to the affected list to do changes after effect like invalidating the inbox
	    affectedEmployeesIds.add(empData.getEmpId());

	    adjustUnitsManagers(unitsMap, empData, movementTransaction.getUnitId(), movementTransaction.getManagerFlagBoolean(), false);

	    empData.setPhysicalUnitId(movementTransaction.getUnitId());
	    EmployeesService.updateEmployee(empData, session);
	    EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(movementTransaction.getUnitId()).constructCommonFields(empData.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
	    LogService.logEmployeeData(employeeLog, session);
	}

	// Apply the changes made in he adjust Units Managers method
	UnitsService.modifyUnitsManagers(unitsMap.values(), false, session);

	// Apply further changes on the affected employees like invlidating thier inbox
	doChangesOnAffectedEmployees(affectedEmployeesIds.toArray(new Long[affectedEmployeesIds.size()]), session);
    }

    /**
     * Apply the cancel or terminate effect of the subjoin, mandate, or secondment transactions
     * 
     * @param movementTransactions
     *            list of movement transactions to be handled
     * @param isTermination
     *            determines if the transaction sent is termination or cancellation
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void doCancellationAndTerminationEffect(List<MovementTransactionData> movementTransactions, boolean isTermination, CustomSession session) throws BusinessException {
	int size = movementTransactions.size();
	Long[] employeesIds = new Long[size];

	List<Long> affectedEmployeesIds = new ArrayList<Long>();

	for (int i = 0; i < size; i++) {
	    Long empId = movementTransactions.get(i).getEmployeeId();
	    employeesIds[i] = empId;
	}

	List<MovementTransactionData> predecessors = new ArrayList<MovementTransactionData>();
	List<Object> empTransactionObjectsList = (ArrayList<Object>) getCancelledAndTerminatedMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(movementTransactions.get(0).getBasedOnDecisionNumber(), movementTransactions.get(0).getBasedOnDecisionDate(), employeesIds);
	Date sysDate = HijriDateService.getHijriSysDate();

	Map<Long, UnitData> unitsMap = new HashMap<Long, UnitData>();
	Map<Long, MovementTransactionData> empsMovTransactionsMap = new HashMap<Long, MovementTransactionData>();
	for (MovementTransactionData movementTransaction : movementTransactions) {
	    empsMovTransactionsMap.put(movementTransaction.getEmployeeId(), movementTransaction);
	}

	for (Object empTransactionObject : empTransactionObjectsList) {
	    MovementTransactionData movementTransaction = null;
	    MovementTransactionData predecessorTrans = (MovementTransactionData) ((Object[]) empTransactionObject)[0];
	    EmployeeData emp = (EmployeeData) ((Object[]) empTransactionObject)[1];
	    predecessors.add(predecessorTrans);

	    movementTransaction = empsMovTransactionsMap.get(emp.getEmpId());

	    predecessorTrans.setSuccessorDecisionEffectFlag(isTermination ? TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode() : TransactionTypesEnum.MVT_CANCEL_DECISION.getCode());
	    if (predecessorTrans.getEffectFlag().intValue() == FlagsEnum.ON.getCode() && ((!isTermination && sysDate.after(predecessorTrans.getEndDate())) || (isTermination && movementTransaction.getEndDate().before(sysDate) && predecessorTrans.getEndDate().before(sysDate)))) {
		EmployeeLogData lastEmployeeLog = LogService.getLastEmployeeLogBeforeGivenDate(emp.getEmpId(), movementTransaction.getExecutionDate());
		EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(lastEmployeeLog.getOfficialUnitId())
			.constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), isTermination ? HijriDateService.addSubHijriDays(movementTransaction.getEndDate(), 1) : movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		LogService.logEmployeeData(employeeLog, session);
	    }
	    if (predecessorTrans.getEffectFlag().intValue() == FlagsEnum.ON.getCode() && ((!isTermination && (!(sysDate.before(predecessorTrans.getExecutionDate()) || sysDate.after(predecessorTrans.getEndDate())))) || (isTermination && (movementTransaction.getEndDate().before(sysDate) && !predecessorTrans.getEndDate().before(sysDate))))) {

		adjustUnitsManagers(unitsMap, emp, null, false, false);

		emp.setPhysicalUnitId(emp.getOfficialUnitId());
		emp.setStatusId(EmployeeStatusEnum.ON_DUTY.getCode());
		EmployeesService.updateEmployee(emp, session);
		EmployeeLogData lastEmployeeLog = LogService.getLastEmployeeLogBeforeGivenDate(emp.getEmpId(), movementTransaction.getExecutionDate());
		EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(lastEmployeeLog.getOfficialUnitId())
			.constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), isTermination ? HijriDateService.addSubHijriDays(movementTransaction.getEndDate(), 1) : movementTransaction.getExecutionDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		LogService.logEmployeeData(employeeLog, session);

		// add empId to the affected list to do changes after effect like invalidating the inbox
		affectedEmployeesIds.add(emp.getEmpId());
	    }
	}

	// Apply the changes made in he adjust Units Managers method
	UnitsService.modifyUnitsManagers(unitsMap.values(), false, session);

	// Apply further changes on the affected employees like invlidating thier inbox
	doChangesOnAffectedEmployees(affectedEmployeesIds.toArray(new Long[affectedEmployeesIds.size()]), session);

	updateMovementTransactions(predecessors, session);
    }

    /**
     * Apply other changes on the affected employees after doing the doEffect on them
     * 
     * @param empsIds
     *            Affected Employees
     * @param session
     * @throws BusinessException
     */
    private static void doChangesOnAffectedEmployees(Long[] empsIds, CustomSession session) throws BusinessException {
	BusinessWorkflowCooperation.invalidateEmployeesInboxAndDelegations(empsIds, TransactionClassesEnum.MOVEMENTS.getCode(), session);
    }

    /**
     * Adjusting unit managers(unit from/to) for a movement process given a map of units to avoid concurrent modification in the same unit
     * 
     * @param unitsMap
     *            map of units that already modified
     * @param employee
     *            employee in the movement process
     * @param isMVTManager
     *            Is the movement process as manager
     * @param isMoveTransaction
     *            type of movement transaction is move
     * @throws BusinessException
     *             if any error occurs
     */
    public static void adjustUnitsManagers(Map<Long, UnitData> unitsMap, EmployeeData employee, Long mvtUnitId, boolean isMVTManager, boolean isMoveTransaction) throws BusinessException {

	/**************************************** Handling units that the employee is a physical manager on them ***************************************/
	if (employee.getIsManager().intValue() == FlagsEnum.ON.getCode()) {
	    List<UnitData> unitsHavetheSamePhysicalManager = UnitsService.getUnitsByPhysicalManager(employee.getEmpId());
	    for (UnitData samePhysicalManagerUnit : unitsHavetheSamePhysicalManager) {
		UnitData empManagerPhysicalUnit = unitsMap.containsKey(samePhysicalManagerUnit.getId()) ? unitsMap.get(samePhysicalManagerUnit.getId()) : samePhysicalManagerUnit;

		if (employee.getEmpId().equals(empManagerPhysicalUnit.getPhysicalManagerId())) {
		    empManagerPhysicalUnit.setPhysicalManagerId(null);
		    unitsMap.put(empManagerPhysicalUnit.getId(), empManagerPhysicalUnit);
		}
	    }
	}

	/****************************************************** Handling employee official unit ******************************************************/
	if (isMoveTransaction) {
	    UnitData empOfficialUnit = unitsMap.containsKey(employee.getOfficialUnitId()) ? unitsMap.get(employee.getOfficialUnitId()) : UnitsService.getUnitById(employee.getOfficialUnitId());
	    if (employee.getEmpId().equals(empOfficialUnit.getOfficialManagerId())) {
		empOfficialUnit.setOfficialManagerId(null);
		unitsMap.put(empOfficialUnit.getId(), empOfficialUnit);
	    }
	}

	/****************************************************** Handling movement transaction unit ******************************************************/
	if (isMVTManager && mvtUnitId != null) {
	    UnitData mvtUnit = unitsMap.containsKey(mvtUnitId) ? unitsMap.get(mvtUnitId) : UnitsService.getUnitById(mvtUnitId);
	    mvtUnit.setPhysicalManagerId(employee.getEmpId());

	    if (isMoveTransaction)
		mvtUnit.setOfficialManagerId(employee.getEmpId());

	    unitsMap.put(mvtUnit.getId(), mvtUnit);
	}
    }

    /**
     * Adding a list of movement transactions in the database
     * 
     * @param movementTransactions
     *            list of movement transactions to be added
     * @param processName
     *            process name of the movement transactions
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void addMovementTransactions(List<MovementTransactionData> movementTransactions, String processName, CustomSession... useSession) throws BusinessException {
	if (movementTransactions != null) {

	    Date sysDate = HijriDateService.getHijriSysDate();

	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();

		// ETR Cor Info
		String[] etrCorInfo = null;
		if (processName != null)
		    etrCorInfo = ETRCorrespondence.doETRCorOut(processName, session);

		for (MovementTransactionData movementTransaction : movementTransactions) {
		    if (movementTransaction.getExecutionDateFlag().intValue() == FlagsEnum.ON.getCode())
			movementTransaction.setExecutionDate(sysDate);

		    if (etrCorInfo != null) {
			movementTransaction.setDecisionNumber(etrCorInfo[0]);
			movementTransaction.setDecisionDateString(etrCorInfo[1]);
		    }

		    if (movementTransaction.getTransactionTypeId().intValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId()) {
			movementTransaction.setEffectFlag(FlagsEnum.ON.getCode());
			movementTransaction.setPeriodDays(HijriDateService.hijriDateDiff(movementTransaction.getExecutionDate(), movementTransaction.getEndDate()) + 1);
		    } else if (movementTransaction.getTransactionTypeId().intValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId())
			movementTransaction.setEffectFlag(FlagsEnum.ON.getCode());
		    else
			movementTransaction.setEffectFlag(movementTransaction.getExecutionDate().after(sysDate) ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode());
		    DataAccess.addEntity(movementTransaction.getMovementTransaction(), session);
		}

		if (!isOpenedSession)
		    session.commitTransaction();

		for (MovementTransactionData movementTransactionData : movementTransactions)
		    movementTransactionData.setId(movementTransactionData.getMovementTransaction().getId());

	    } catch (Exception e) {
		if (!isOpenedSession)
		    session.rollbackTransaction();

		for (MovementTransactionData movementTransactionData : movementTransactions)
		    movementTransactionData.setId(null);

		if (e instanceof BusinessException)
		    throw (BusinessException) e;

		e.printStackTrace();
		throw new BusinessException("error_general");
	    } finally {
		if (!isOpenedSession)
		    session.close();
	    }
	}
    }

    /**
     * Updating a list of movement transactions in the database
     * 
     * @param movementTransactions
     *            list of movement transactions to be updated
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    private static void updateMovementTransactions(List<MovementTransactionData> movementTransactions, CustomSession... useSession) throws BusinessException {
	if (movementTransactions != null) {
	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();

		for (MovementTransactionData movementTransactionData : movementTransactions)
		    DataAccess.updateEntity(movementTransactionData.getMovementTransaction(), session);

		if (!isOpenedSession)
		    session.commitTransaction();

	    } catch (Exception e) {
		if (!isOpenedSession)
		    session.rollbackTransaction();
		e.printStackTrace();
		throw new BusinessException("error_general");
	    } finally {
		if (!isOpenedSession)
		    session.close();
	    }
	}
    }

    public static void setEmployeeAsPhysicalManagerOnUnit(UnitData unit, List<UnitData> employeeUnitsAsPhysicalManager, Long employeePhysicalUnitId, Long employeeId) throws BusinessException {
	validateAddingPhysicalManagerOnUnit(unit, employeeUnitsAsPhysicalManager, employeePhysicalUnitId);
	validateReplacedUnitManager(unit, employeeId);
	try {
	    unit.setPhysicalManagerId(employeeId);
	    DataAccess.updateEntity(unit.getUnit());
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void removePhysicalManagerFromUnit(UnitData unit, List<UnitData> employeeUnitsAsPhysicalManager, Long employeePhysicalUnitId) throws BusinessException {
	validateRemovingPhysicalManagerFromUnit(unit, employeeUnitsAsPhysicalManager, employeePhysicalUnitId);
	try {
	    unit.setPhysicalManagerId(null);
	    DataAccess.updateEntity(unit.getUnit());
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * To validate a movement transaction, used by the MovementWorkFlow validate only
     * 
     * @param movementTransaction
     *            transaction to be validated
     * @param replacementEmployeeId
     *            the id of the replacement employee if it's a replacement movement transaction
     * @param requestId
     *            instance id of workflow of the movement transaction
     * @throws BusinessException
     *             if any error occurs
     */
    public static void validateBusinessRules(MovementTransactionData movementTransaction, Long replacementEmployeeId, Long requestId, int transactionSourceView) throws BusinessException {
	if (movementTransaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode())
	    validateMoveRules(movementTransaction, replacementEmployeeId, requestId, transactionSourceView);
	if (movementTransaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode())
	    validateSubjoinRules(movementTransaction, requestId, transactionSourceView);
	if (movementTransaction.getMovementTypeId() == MovementTypesEnum.ASSIGNMENT.getCode())
	    validateAssignmentRules(movementTransaction, requestId, transactionSourceView);
	if (movementTransaction.getMovementTypeId() == MovementTypesEnum.MANDATE.getCode())
	    validateMandateRules(movementTransaction, requestId);
	if (movementTransaction.getMovementTypeId() == MovementTypesEnum.SECONDMENT.getCode())
	    validateSecondmentRules(movementTransaction, requestId);
    }

    /*
     * ************************************ Movement Transactions validations ****************************************
     */

    // Either to make one big validate for all and inside it make the
    // checks for every type or make validate for every type
    /**
     * To validate the common rules between movement transactions types for a transaction
     * 
     * @param movementTransaction
     *            movement transaction to be validated
     * @param employee
     *            employee enrolled in the movement transaction
     * @param replacementEmployee
     *            replacement employee if it's a replacement movement transaction
     * @param isRegistration
     *            the type of movement transaction, if it's a workflow or a registration screen
     * @throws BusinessException
     *             if any error occurs
     */
    private static void commonValidate(MovementTransactionData movementTransaction, EmployeeData employee, EmployeeData replacementEmployee, int transactionSourceView) throws BusinessException {

	// Validate mandatory fields
	validateMandatoryFields(movementTransaction, transactionSourceView);

	// Validate if non electronic transaction and it's decision number and
	// decision date are repeated
	// In another non electronic transaction
	if (movementTransaction.getEtrFlag().equals(FlagsEnum.OFF.getCode()) && !movementTransaction.getMovementTypeId().equals(MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode()) && getMovementTransactions(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), movementTransaction.getDecisionNumber(), movementTransaction.getDecisionDate(), movementTransaction.getDecisionDate(), FlagsEnum.ALL.getCode(), FlagsEnum.OFF.getCode()).size() > 0)
	    throw new BusinessException("error_repeatedDecisionNumber");

	// Validate employees status
	validateEmployeeStatus(employee, movementTransaction);
	if (replacementEmployee != null) {
	    validateEmployeeStatus(replacementEmployee, movementTransaction);
	}

	if (movementTransaction.getManagerFlagBoolean()) {
	    // validate replaced Manager status
	    UnitData unit = UnitsService.getUnitById(movementTransaction.getUnitId());
	    validateReplacedUnitManager(unit, employee.getEmpId());
	}

	// validate if employee is banned from move due to his additional specializations
	List<EmployeeAdditionalSpecializationData> EmployeeAdditionalSpecializationDataList = AdditionalSpecializationsService.getEmployeeAdditionalSpecializationsDataByEmpId(movementTransaction.getEmployeeId());
	StringBuilder bannedAdditionalSpecs = new StringBuilder("");
	for (int i = 0; i < EmployeeAdditionalSpecializationDataList.size(); i++) {
	    if (EmployeeAdditionalSpecializationDataList.get(i).getMovementBlacklistFlagBoolean() == true) {
		bannedAdditionalSpecs.append(EmployeeAdditionalSpecializationDataList.get(i).getAdditionalSpecializationDesc());
		bannedAdditionalSpecs.append(" , ");
	    }
	}

	if (bannedAdditionalSpecs.length() > 0) {
	    bannedAdditionalSpecs.delete(bannedAdditionalSpecs.length() - 3, bannedAdditionalSpecs.length() - 1);
	    throw new BusinessException("error_bannedFromMovementDueToAdditionalSpec", new String[] { bannedAdditionalSpecs.toString() });
	}

	// Validate hijri dates
	validateHijriDates(movementTransaction);

	boolean isTermination = movementTransaction.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();
	if (isTermination) {
	    if (movementTransaction.getEndDate().before(movementTransaction.getExecutionDate()))
		throw new BusinessException("error_endDateBeforeStartDate");

	    Date originalEndDate = getMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(movementTransaction.getBasedOnDecisionNumber(), movementTransaction.getBasedOnDecisionDate(), movementTransaction.getBasedOnDecisionDate(), null, null, new Long[] { movementTransaction.getEmployeeId() }, movementTransaction.getMovementTypeId(), FlagsEnum.ALL.getCode()).get(0).getEndDate();
	    if (!originalEndDate.after(movementTransaction.getEndDate()))
		throw new BusinessException("error_terminationDateBeforeEndDate");
	}

	if (movementTransaction.getPeriodDays() != null && (movementTransaction.getPeriodDays() > 29 || movementTransaction.getPeriodDays() < 1))
	    throw new BusinessException("error_invalidPeriodDays", new String[] { employee.getName() });
	if (movementTransaction.getPeriodMonths() != null && movementTransaction.getPeriodMonths() < 1)
	    throw new BusinessException("error_invalidPeriodMonths", new String[] { employee.getName() });
	if (employee.getRecruitmentDate() == null)
	    throw new BusinessException("error_insertRecruitmentDateFirst", new String[] { employee.getName() });
	if (movementTransaction.getExecutionDate() != null && (employee.getRecruitmentDate().after(movementTransaction.getExecutionDate()) || employee.getRecruitmentDateString().equals(movementTransaction.getExecutionDateString())))
	    throw new BusinessException("error_execDateMustBeAfterRecDate", new String[] { employee.getName() });
    }

    /**
     * To validate hijri dates in a movement transaction
     * 
     * @param movement
     *            the movement transaction
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateHijriDates(MovementTransactionData movement) throws BusinessException {
	if (!(movement.getExecutionDate() == null || HijriDateService.isValidHijriDate(movement.getExecutionDate())))
	    throw new BusinessException("error_invalidExecutionDate");
	if (!(movement.getEndDate() == null || HijriDateService.isValidHijriDate(movement.getEndDate())))
	    throw new BusinessException("error_invalidEndDate");
	if (!(movement.getBasedOnOrderDate() == null || HijriDateService.isValidHijriDate(movement.getBasedOnOrderDate())))
	    throw new BusinessException("error_invalidBasedOnOrderDate");
	if (!(movement.getBasedOnDecisionDate() == null || HijriDateService.isValidHijriDate(movement.getBasedOnDecisionDate())))
	    throw new BusinessException("error_invalidBasedOnDecisionDate");
	if (!(movement.getMinistryApprovalDate() == null || HijriDateService.isValidHijriDate(movement.getMinistryApprovalDate())))
	    throw new BusinessException("error_invalidMinistryApprovalDate");
    }

    /**
     * To validate mandatory fields in a movement transaction
     * 
     * @param movementTransaction
     *            the movement transaction
     * @param isRegistration
     *            the type of movement transaction, if it's a workflow or a registration screen
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateMandatoryFields(MovementTransactionData movementTransaction, int transactionSourceView) throws BusinessException {
	// TODO based on fields should be checked in all Decision Requests

	// Common mandatory fields
	if (movementTransaction.getCategoryId() == null) {
	    throw new BusinessException("error_transactionDataError");
	} else if (movementTransaction.getMovementTypeId() == null) {
	    throw new BusinessException("error_transactionDataError");
	} else if (movementTransaction.getTransactionTypeId() == null) {
	    throw new BusinessException("error_transactionDataError");
	} else if (movementTransaction.getEmployeeId() == null) {
	    throw new BusinessException("error_empSelectionManadatory");
	} else if (movementTransaction.getLocationFlag() == null) {
	    throw new BusinessException("error_locationFlagMandatory");
	} else if (movementTransaction.getLocationFlag().longValue() == FlagsEnum.OFF.getCode() && movementTransaction.getUnitId() == null) {
	    throw new BusinessException("error_unitIsMandatory");
	} else if (movementTransaction.getLocationFlag().longValue() == FlagsEnum.ON.getCode() && (movementTransaction.getLocation() == null || movementTransaction.getLocation().trim().isEmpty())) {
	    throw new BusinessException("error_locationIsMandatory");
	} else if (movementTransaction.getVerbalOrderFlag() == null)
	    throw new BusinessException("error_transactionDataError");

	if (transactionSourceView != MovementTransactionViewsEnum.REGISTRATION.getCode()) {
	    if (movementTransaction.getRequestTransactionFlag().equals(FlagsEnum.OFF.getCode()) && movementTransaction.getVerbalOrderFlag().equals(FlagsEnum.OFF.getCode()) &&
		    movementTransaction.getMovementTypeId().longValue() != MovementTypesEnum.MANDATE.getCode() && movementTransaction.getMovementTypeId().longValue() != MovementTypesEnum.SECONDMENT.getCode()) {
		if (movementTransaction.getBasedOn() == null) {
		    throw new BusinessException("error_basedOnMandatory");
		} else if (movementTransaction.getBasedOnOrderNumber() == null) {
		    throw new BusinessException("error_basedOnOrderNumberMandatory");
		} else if (movementTransaction.getBasedOnOrderDate() == null) {
		    throw new BusinessException("error_basedOnOrderDateMandatory");
		}
	    }

	    // Move
	    if (movementTransaction.getMovementTypeId().longValue() == MovementTypesEnum.MOVE.getCode()) {
		if (movementTransaction.getReasonType() == null) {
		    throw new BusinessException("error_moveReasonTypeIsMandatory");
		} else if (movementTransaction.getLocationFlag().equals(FlagsEnum.OFF.getCode()) && movementTransaction.getJobId() == null) {
		    throw new BusinessException("error_jobIsMandatory");
		}
	    }
	    // Subjoin
	    if (movementTransaction.getMovementTypeId().longValue() == MovementTypesEnum.SUBJOIN.getCode()) {
		if (movementTransaction.getReasonType() == null) {
		    throw new BusinessException("error_subjoinReasonTypeIsMandatory");
		} else if (movementTransaction.getEndDate() == null) {
		    throw new BusinessException("error_periodIsMandatory");
		}
	    }
	    // Assignment
	    if (movementTransaction.getMovementTypeId().longValue() == MovementTypesEnum.ASSIGNMENT.getCode()) {
		if (movementTransaction.getLocationFlag().longValue() == LocationFlagsEnum.INTERNAL.getCode() && movementTransaction.getJobId() == null) {
		    throw new BusinessException("error_jobIsMandatory");
		} else if (movementTransaction.getLocationFlag().longValue() == LocationFlagsEnum.EXTERNAL.getCode() && movementTransaction.getEndDate() == null) {
		    throw new BusinessException("error_endDateIsMandaory");
		}
	    }
	    // Mandate or secondment
	    if (movementTransaction.getMovementTypeId().longValue() == MovementTypesEnum.MANDATE.getCode() || movementTransaction.getMovementTypeId().longValue() == MovementTypesEnum.SECONDMENT.getCode()) {
		if (movementTransaction.getEndDate() == null) {
		    throw new BusinessException("error_endDateIsMandaory");
		} else if (movementTransaction.getMinistryApprovalNumber() == null) {
		    throw new BusinessException("error_ministryApprovalNumberMandatory");
		} else if (movementTransaction.getMinistryApprovalDate() == null) {
		    throw new BusinessException("error_ministryApprovalDateMandatory");
		}
	    }
	} else { // Registration
		 // Internal assignment
	    if (movementTransaction.getMovementTypeId().longValue() == MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode()) {
		if (movementTransaction.getManagerFlag() == null) {
		    throw new BusinessException("error_managerFlagIsMandaory");
		}
	    } else {
		// Common
		if (movementTransaction.getDecisionNumber() == null) {
		    throw new BusinessException("error_decNumberMandatory");
		} else if (movementTransaction.getDecisionDate() == null) {
		    throw new BusinessException("error_decDateMandatory");
		} else if (movementTransaction.getLocationFlag().longValue() == LocationFlagsEnum.EXTERNAL.getCode() && (movementTransaction.getLocation() == null || movementTransaction.getLocation().trim().isEmpty())) {
		    throw new BusinessException("error_locationIsMandatory");
		}
		// Move
		if (movementTransaction.getMovementTypeId().longValue() == MovementTypesEnum.MOVE.getCode()) {
		    if (movementTransaction.getReasonType() == null) {
			throw new BusinessException("error_moveReasonTypeIsMandatory");
		    }
		}
		// Assignment
		if (movementTransaction.getMovementTypeId().longValue() == MovementTypesEnum.ASSIGNMENT.getCode()) {
		    if (movementTransaction.getReasonType() == null) {
			throw new BusinessException("error_assignmentReasonTypeIsMandatory");
		    } else if (movementTransaction.getLocationFlag().longValue() == LocationFlagsEnum.EXTERNAL.getCode() && movementTransaction.getExecutionDateFlag() == FlagsEnum.OFF.getCode() && movementTransaction.getExecutionDate() == null) {
			throw new BusinessException("error_executionDateIsMandatory");
		    }
		}
	    }
	}
    }

    /**
     * Validates the status of an employee to know if we can do the movement transaction for him
     * 
     * @param employee
     *            employee enrolled in the movement transaction
     * @param movementTransaction
     *            movement transaction of the employee
     * @throws BusinessException
     *             if any error occurs
     */
    public static void validateEmployeeStatus(EmployeeData employee, MovementTransactionData movementTransaction) throws BusinessException {
	long categoryId = employee.getCategoryId();
	long movementTypeId = movementTransaction.getMovementTypeId();
	long transactionTypeId = movementTransaction.getTransactionTypeId();
	long empStatusId = employee.getStatusId();
	Date sysDate = HijriDateService.getHijriSysDate();
	Date executionDate = movementTransaction.getExecutionDate();
	Date endDate = movementTransaction.getEndDate();
	Date lastPromotionDate = employee.getLastPromotionDate() == null ? employee.getRecruitmentDate() : employee.getLastPromotionDate();

	long newDecisionId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();
	long extensionDecisionId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();
	long terminationDecisionId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();
	long cancelDecisionId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();

	if (movementTypeId == MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode()) {
	    if (empStatusId < EmployeeStatusEnum.ON_DUTY.getCode() || empStatusId > EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()) {
		throw new BusinessException("error_employeeStatusInNotSuitable");
	    }
	}
	if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
	    if (movementTypeId == MovementTypesEnum.MOVE.getCode()) {
		if (executionDate != null && (countEmployeeTransactionsAfterGivenExecutionDate(employee.getEmpId(), executionDate) != 0 || !executionDate.after(lastPromotionDate))) {
		    throw new BusinessException("error_moveTransactionShouldbeLastOne");
		} else if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.ASSIGNED.getCode()) {
		    throw new BusinessException("error_employeeStatusInNotSuitable");
		}

	    } else if (movementTypeId == MovementTypesEnum.SUBJOIN.getCode()) {
		if (!endDate.before(sysDate)) {
		    if (transactionTypeId == newDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.ASSIGNED.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == extensionDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == terminationDecisionId) {
			if (empStatusId != EmployeeStatusEnum.SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == cancelDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.ASSIGNED.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    }
		}

	    } else if (movementTypeId == MovementTypesEnum.ASSIGNMENT.getCode()) {
		Date checkDate = endDate == null ? executionDate : endDate;
		if (!checkDate.before(sysDate)) {
		    if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.ASSIGNED.getCode()) {
			throw new BusinessException("error_employeeStatusInNotSuitable");
		    }
		}

	    }

	} else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {

	    if (employee.getMovementBlacklistFlag().equals(FlagsEnum.ON.getCode())) {
		throw new BusinessException("error_mvtBlackList");
	    }

	    if (movementTypeId == MovementTypesEnum.MOVE.getCode()) {
		if (executionDate != null && (countEmployeeTransactionsAfterGivenExecutionDate(employee.getEmpId(), executionDate) != 0 || !executionDate.after(lastPromotionDate))) {
		    throw new BusinessException("error_moveTransactionShouldbeLastOne");
		} else if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode()) {
		    throw new BusinessException("error_employeeStatusInNotSuitable");
		}

	    } else if (movementTypeId == MovementTypesEnum.SUBJOIN.getCode()) {
		if (!endDate.before(sysDate)) {
		    if (transactionTypeId == newDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == extensionDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == terminationDecisionId) {
			if (empStatusId != EmployeeStatusEnum.SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == cancelDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    }
		}

	    } else if (movementTypeId == MovementTypesEnum.MANDATE.getCode()) {
		if (!endDate.before(sysDate)) {
		    if (transactionTypeId == newDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == extensionDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.MANDATED.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == terminationDecisionId) {
			if (empStatusId != EmployeeStatusEnum.MANDATED.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == cancelDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.MANDATED.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    }
		}

	    } else if (movementTypeId == MovementTypesEnum.SECONDMENT.getCode()) {
		if (!endDate.before(sysDate)) {
		    if (transactionTypeId == newDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == extensionDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.SECONDMENTED.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == terminationDecisionId) {
			if (empStatusId != EmployeeStatusEnum.SECONDMENTED.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == cancelDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.SECONDMENTED.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    }
		}

	    }

	} else {

	    if (movementTypeId == MovementTypesEnum.MOVE.getCode()) {
		if (executionDate != null && (countEmployeeTransactionsAfterGivenExecutionDate(employee.getEmpId(), executionDate) != 0 || !executionDate.after(lastPromotionDate))) {
		    throw new BusinessException("error_moveTransactionShouldbeLastOne");
		} else if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode()) {
		    throw new BusinessException("error_employeeStatusInNotSuitable");
		}

	    } else if (movementTypeId == MovementTypesEnum.SUBJOIN.getCode()) {
		if (!endDate.before(sysDate)) {
		    if (transactionTypeId == newDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == extensionDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.PERSONS_SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == terminationDecisionId) {
			if (empStatusId != EmployeeStatusEnum.PERSONS_SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    } else if (transactionTypeId == cancelDecisionId) {
			if (empStatusId != EmployeeStatusEnum.ON_DUTY.getCode() && empStatusId != EmployeeStatusEnum.PERSONS_SUBJOINED.getCode() && empStatusId != EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode()) {
			    throw new BusinessException("error_employeeStatusInNotSuitable");
			}
		    }
		}
	    }
	}
    }

    /**
     * validate soldiers fourteen month rule
     * 
     * @param movementTransaction
     *            move transaction to be validated
     * @param emp
     *            employee to be validated
     * @throws BusinessException
     *             if any error occurs
     */

    public static boolean checkMonthsRule(Date executionDate, Date empServiceTerminationDueDate, Date empLastExtentionEndDate, Long categoryId) throws BusinessException {
	Date empTerminationDueDate = (empLastExtentionEndDate == null || (empServiceTerminationDueDate != null && empServiceTerminationDueDate.after(empLastExtentionEndDate))) ? empServiceTerminationDueDate : empLastExtentionEndDate;

	if (executionDate.after(empTerminationDueDate)) {
	    return false;
	}

	Integer[] dateDiff = HijriDateService.hijriDateDiffInMonthsAndDays(HijriDateService.getHijriDateString(executionDate), HijriDateService.getHijriDateString(empTerminationDueDate));
	if (categoryId != null && categoryId.equals(CategoriesEnum.OFFICERS.getCode())) {
	    if (dateDiff[1] < ETRConfigurationService.getMovementOfficersPeriodBetweenMovementAndServiceTerminationDueDate() || (dateDiff[1] == ETRConfigurationService.getMovementOfficersPeriodBetweenMovementAndServiceTerminationDueDate() && dateDiff[0] == 0)) {
		return false;
	    }
	} else if (categoryId != null && categoryId.equals(CategoriesEnum.SOLDIERS.getCode())) {
	    if (dateDiff[1] < ETRConfigurationService.getMovementSoldiersPeriodBetweenMovementAndServiceTerminationDueDate() || (dateDiff[1] == ETRConfigurationService.getMovementSoldiersPeriodBetweenMovementAndServiceTerminationDueDate() && dateDiff[0] == 0)) {
		return false;
	    }
	}
	return true;
    }

    /**
     * validate rules for a move transaction
     * 
     * @param movementTransaction
     *            move transaction to be validated
     * @param replacementEmployeeId
     *            replacement employee id if it's a replacement move transaction
     * @param isRegistration
     *            the type of move transaction, if it's a workflow or a registration screen
     * @param requestId
     *            instance id of the workflow that created the transaction
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateMoveRules(MovementTransactionData movementTransaction, Long replacementEmployeeId, Long requestId, int transactionSourceView) throws BusinessException {
	// Load necessary data
	EmployeeData replacementEmp = null;
	if (replacementEmployeeId != null) {
	    if (replacementEmployeeId.longValue() == movementTransaction.getEmployeeId().longValue())
		throw new BusinessException("error_selfExchangeIsInvalid");

	    replacementEmp = EmployeesService.getEmployeeData(replacementEmployeeId);
	    if (replacementEmp == null)
		throw new BusinessException("error_employeeDataError");
	}

	EmployeeData emp;
	emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());
	if (emp == null)
	    throw new BusinessException("error_employeeDataError");

	JobData job = null;
	if (movementTransaction.getJobId() != null && !movementTransaction.getJobId().equals(-1L))
	    job = JobsService.getJobById(movementTransaction.getJobId());

	// Validations
	commonValidate(movementTransaction, emp, replacementEmp, transactionSourceView);

	if (transactionSourceView == MovementTransactionViewsEnum.REQUEST.getCode()) {
	    if (emp.getOfficialRegionId() != emp.getPhysicalRegionId()) {
		throw new BusinessException("error_cannotDoRequestAsOficialRegionNotEqualPhysicalRegion");
	    }

	    if (replacementEmp != null && replacementEmp.getOfficialRegionId() != replacementEmp.getPhysicalRegionId()) {
		throw new BusinessException("error_cannotDoRequestAsReplacementEmpOficialRegionNotEqualPhysicalRegion");
	    }

	}

	if (!skipMonthsRuleValidation(movementTransaction.getExtraParams())) {
	    if (emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		if (!checkMonthsRule(movementTransaction.getExecutionDate() != null ? movementTransaction.getExecutionDate() : HijriDateService.getHijriSysDate(), emp.getServiceTerminationDueDate(), emp.getLastExtensionEndDate(), emp.getCategoryId()))
		    throw new BusinessException("error_cannotDoMoveRequestAsEmployeeTerminationDueDateLessThanMinMonths", new String[] { emp.getName(), emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() ? ETRConfigurationService.getMovementOfficersPeriodBetweenMovementAndServiceTerminationDueDate() + "" : ETRConfigurationService.getMovementSoldiersPeriodBetweenMovementAndServiceTerminationDueDate() + "" });
		if (replacementEmp != null && !checkMonthsRule(movementTransaction.getExecutionDate() != null ? movementTransaction.getExecutionDate() : HijriDateService.getHijriSysDate(), replacementEmp.getServiceTerminationDueDate(), replacementEmp.getLastExtensionEndDate(), emp.getCategoryId())) {
		    throw new BusinessException("error_cannotDoMoveRequestAsEmployeeTerminationDueDateLessThanMinMonths", new String[] { replacementEmp.getName(), emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() ? ETRConfigurationService.getMovementOfficersPeriodBetweenMovementAndServiceTerminationDueDate() + "" : ETRConfigurationService.getMovementSoldiersPeriodBetweenMovementAndServiceTerminationDueDate() + "" });
		}
	    }
	}

	Long[] validateJobsIds = null;
	if (movementTransaction.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode())) {
	    if (movementTransaction.getFreezeJobId() != null) {
		validateJobsIds = new Long[] { movementTransaction.getJobId(), movementTransaction.getFreezeJobId() };
	    } else {
		validateJobsIds = new Long[] { movementTransaction.getJobId() };
	    }
	}

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { movementTransaction.getEmployeeId() }, validateJobsIds, TransactionClassesEnum.MOVEMENTS.getCode(), false, requestId);
	countConflictDatesTransactions(movementTransaction.getEmployeeId(), movementTransaction.getExecutionDate() == null ? HijriDateService.getHijriSysDate() : movementTransaction.getExecutionDate(), movementTransaction.getEndDate());

	// Job must be vacant except the cases of exchange move and move by
	// freeze
	if (job != null && movementTransaction.getFreezeJobId() == null && replacementEmployeeId == null && job.getStatus().intValue() != JobStatusEnum.VACANT.getCode() && movementTransaction.getSequentialMvtFlagBoolean() == false)
	    throw new BusinessException("error_mvtIsAllowedOnVacantJobsOnly");

	if (movementTransaction.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
	    if (replacementEmployeeId != null && checkIfEmployeeExistsInCertainPositions(emp))
		throw new BusinessException("error_cannotDoReplacementMovement");
	    if (replacementEmployeeId != null && checkIfEmployeeExistsInCertainPositions(replacementEmp))
		throw new BusinessException("error_cannotDoReplacementMovementWithReplacementEmployee");

	} else if (movementTransaction.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
	    if (replacementEmployeeId == null && job != null && emp.getRankId() != job.getRankId().longValue())
		throw new BusinessException("error_empRankNotMatchingJobRank");

	    if (replacementEmployeeId != null && emp.getRankId() != replacementEmp.getRankId().longValue())
		throw new BusinessException("error_empsRanksNotMatching");

	    if (movementTransaction.getReasonType() != null && movementTransaction.getReasonType().intValue() == MovementsReasonTypesEnum.FOR_PUBLIC_INTEREST.getCode() && movementTransaction.getLocationFlag().intValue() == FlagsEnum.ON.getCode())
		throw new BusinessException("error_exterrnalMoveForPublicInterest");
	} else {
	    if (replacementEmployeeId == null && job != null && emp.getCategoryId().longValue() != job.getCategoryId().longValue())
		throw new BusinessException("error_jobCategoryNotMatchingEmployeeCategory");

	    if (replacementEmployeeId == null && job != null && emp.getRankId() != job.getRankId().longValue())
		throw new BusinessException("error_civilianRankNotMatchingJobRank");

	    if (replacementEmployeeId != null && !emp.getCategoryId().equals(replacementEmp.getCategoryId()))
		throw new BusinessException("error_empCategoryNotMatchingReplacementEmpCategory");
	    if (replacementEmployeeId != null && emp.getRankId() != replacementEmp.getRankId().longValue())
		throw new BusinessException("error_civiliansRanksNotMatching");

	    if (movementTransaction.getExecutionDateFlag().intValue() == FlagsEnum.ON.getCode()) {
		if (emp.getLastPromotionDate() != null && HijriDateService.hijriDateDiff(emp.getLastPromotionDate(), HijriDateService.getHijriSysDate()) < PERSONS_MIN_PERIOD_TO_MOVE)
		    throw new BusinessException("error_minPeriodToMove");
	    } else if (emp.getLastPromotionDate() != null && movementTransaction.getExecutionDate() != null && HijriDateService.hijriDateDiff(emp.getLastPromotionDate(), movementTransaction.getExecutionDate()) < PERSONS_MIN_PERIOD_TO_MOVE)
		throw new BusinessException("error_minPeriodToMove");
	}
    }

    /**
     * validate rules for a subjoin transaction
     * 
     * @param movementTransaction
     *            move transaction to be validated
     * @param isRegistration
     *            the type of subjoin transaction, if it's a workflow or a registration screen
     * @param requestId
     *            instance id of the workflow that created the transaction
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateSubjoinRules(MovementTransactionData movementTransaction, Long requestId, int transactionSourceView) throws BusinessException {
	// Load necessary data
	EmployeeData emp = null;
	emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());
	if (emp == null)
	    throw new BusinessException("error_employeeDataError");

	// Validations
	commonValidate(movementTransaction, emp, null, transactionSourceView);

	if (transactionSourceView == MovementTransactionViewsEnum.REQUEST.getCode()) {

	    if (emp.getStatusId().longValue() == EmployeeStatusEnum.SUBJOINED.getCode() || emp.getStatusId().longValue() == EmployeeStatusEnum.PERSONS_SUBJOINED.getCode()) {

		MovementTransactionData lastValidTran = MovementsService.getLastValidMovementTransaction(emp.getEmpId(), MovementTypesEnum.SUBJOIN.getCode(), FlagsEnum.ON.getCode());
		if (emp.getPhysicalRegionId() != UnitsService.getUnitById(lastValidTran.getUnitId()).getRegionId()) {
		    throw new BusinessException("error_cannotDoRequestAsSubjoinRegionNotEqualPhysicalRegion");
		}

	    } else {

		if (emp.getOfficialRegionId() != emp.getPhysicalRegionId())
		    throw new BusinessException("error_cannotDoRequestAsOficialRegionNotEqualPhysicalRegion");
	    }

	}

	if (!skipMonthsRuleValidation(movementTransaction.getExtraParams())) {
	    if ((movementTransaction.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || movementTransaction.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) &&
		    (CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().equals(movementTransaction.getTransactionTypeId()) || CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().equals(movementTransaction.getTransactionTypeId()))
		    && !checkMonthsRule(movementTransaction.getExecutionDate() != null ? movementTransaction.getExecutionDate() : HijriDateService.getHijriSysDate(), emp.getServiceTerminationDueDate(), emp.getLastExtensionEndDate(), emp.getCategoryId())) {
		throw new BusinessException("error_cannotDoSubjoinRequestAsEmployeeTerminationDueDateLessThanMinMonths", new String[] { emp.getName(), emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() ? ETRConfigurationService.getMovementOfficersPeriodBetweenMovementAndServiceTerminationDueDate() + "" : ETRConfigurationService.getMovementSoldiersPeriodBetweenMovementAndServiceTerminationDueDate() + "" });
	    }
	}

	Long empRegionId = UnitsService.getUnitById(emp.getOfficialUnitId()).getRegionId();
	Long subjoinRegionId = movementTransaction.getUnitId() == null ? null : UnitsService.getUnitById(movementTransaction.getUnitId()).getRegionId();

	boolean isCancellation = movementTransaction.getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());
	boolean isTermination = movementTransaction.getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { movementTransaction.getEmployeeId() }, null, TransactionClassesEnum.MOVEMENTS.getCode(), isCancellation, requestId);
	if (!isCancellation && !isTermination)
	    countConflictDatesTransactions(movementTransaction.getEmployeeId(), movementTransaction.getExecutionDate(), movementTransaction.getEndDate());

	if (movementTransaction.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && movementTransaction.getUnitId().equals(emp.getPhysicalUnitId()) && movementTransaction.getUnitId().equals(emp.getOfficialUnitId()))
	    throw new BusinessException("error_cannotDoMovementTransactionOnSameUnit");

	if (movementTransaction.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && movementTransaction.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()) {

	    if (HijriDateService.hijriDateDiff(movementTransaction.getExecutionDate(), movementTransaction.getEndDate()) > PERSONS_SUBJOIN_MAX_PERIOD)
		throw new BusinessException("error_maxPeriodOfPersonsSubjoin");
	    if (movementTransaction.getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId()) && emp.getLastPromotionDate() != null && !movementTransaction.getExecutionDate().before(emp.getLastPromotionDate()) && HijriDateService.hijriDateDiff(emp.getLastPromotionDate(), movementTransaction.getExecutionDate()) < PERSONS_MIN_PERIOD_TO_SUBJOIN
		    && (movementTransaction.getLocationFlag() == FlagsEnum.ON.getCode() || (subjoinRegionId != null && empRegionId != subjoinRegionId)))
		throw new BusinessException("error_minPeriodToSubjoinAfterPromotion");
	    if (emp.getRecruitmentDate() != null && HijriDateService.hijriDateDiff(emp.getRecruitmentDate(), movementTransaction.getExecutionDate()) < PERSONS_MIN_PERIOD_TO_SUBJOIN && (movementTransaction.getLocationFlag() == FlagsEnum.ON.getCode() || (subjoinRegionId != null && empRegionId != subjoinRegionId)))
		throw new BusinessException("error_minPeriodToSubjoinAfterRecruitment");
	} else {
	    if (movementTransaction.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && movementTransaction.getExecutionDate() != null && HijriDateService.hijriDateDiff(movementTransaction.getExecutionDate(), movementTransaction.getEndDate()) > SOLDIERS_SUBJOIN_MAX_PERIOD)
		throw new BusinessException("error_maxPeriodOfSubjoin");
	    if (movementTransaction.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && movementTransaction.getReasonType() != null && movementTransaction.getReasonType().intValue() == MovementsReasonTypesEnum.FOR_PUBLIC_INTEREST.getCode() && movementTransaction.getLocationFlag() == FlagsEnum.ON.getCode())
		throw new BusinessException("error_externalSubJoinForPublicInterest");
	}
    }

    /**
     * validate rules for an assignment transaction, officers only
     * 
     * @param movementTransaction
     *            assignment transaction to be validated
     * @param isRegistration
     *            the type of assignment transaction, if it's a workflow or a registration screen
     * @param requestId
     *            instance id of the workflow that created the transaction
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateAssignmentRules(MovementTransactionData movementTransaction, Long requestId, int transactionSourceView) throws BusinessException {
	// Load necessary data
	EmployeeData emp = null;
	emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());
	if (emp == null)
	    throw new BusinessException("error_employeeDataError");

	boolean isCancellation = movementTransaction.getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());

	// Validations
	commonValidate(movementTransaction, emp, null, transactionSourceView);
	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { movementTransaction.getEmployeeId() }, null, TransactionClassesEnum.MOVEMENTS.getCode(), isCancellation, requestId);
	countConflictDatesTransactions(movementTransaction.getEmployeeId(), movementTransaction.getExecutionDate(), movementTransaction.getEndDate());

	if (movementTransaction.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && movementTransaction.getJobId().equals(emp.getJobId()))
	    throw new BusinessException("error_cannotAssignOfficerToHisSameJob");
    }

    /**
     * validate rules for an internal assignment transaction
     * 
     * @param movementTransaction
     *            internal assignment transaction to be validated
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateInternalAssignmentRules(MovementTransactionData movementTransaction) throws BusinessException {
	// Load necessary data
	EmployeeData emp = null;
	emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());
	if (emp == null)
	    throw new BusinessException("error_employeeDataError");

	// Validations
	commonValidate(movementTransaction, emp, null, MovementTransactionViewsEnum.REGISTRATION.getCode());

	// Manager cannot assign himself
	if (movementTransaction.getEmployeeId().equals(movementTransaction.getOriginalDecisionApprovedId())) {
	    throw new BusinessException("error_notAuthorizedToAssignThisEmployee");
	}

	EmployeeData originalDecisionApprovedEmployee = EmployeesService.getEmployeeData(movementTransaction.getOriginalDecisionApprovedId());
	if (originalDecisionApprovedEmployee == null)
	    throw new BusinessException("error_employeeDataError");

	// Manager cannot assign other employee in his own place
	if (movementTransaction.getManagerFlagBoolean() && originalDecisionApprovedEmployee.getPhysicalUnitId().equals(movementTransaction.getUnitId())) {
	    throw new BusinessException("error_notAuthorizedToAssignThisEmployee");
	}
    }

    /**
     * validate rules for a mandate transaction, officers only
     * 
     * @param movementTransaction
     *            mandate transaction to be validated
     * @param isRegistration
     *            the type of mandate transaction, if it's a workflow or a registration screen
     * @param requestId
     *            instance id of the workflow that created the transaction
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateMandateRules(MovementTransactionData movementTransaction, Long requestId) throws BusinessException {
	// Load necessary data
	EmployeeData emp = null;
	emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());
	if (emp == null)
	    throw new BusinessException("error_employeeDataError");

	boolean isCancellation = movementTransaction.getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());
	boolean isTermination = movementTransaction.getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());

	// Validations
	commonValidate(movementTransaction, emp, null, MovementTransactionViewsEnum.DECISION.getCode());
	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { movementTransaction.getEmployeeId() }, null, TransactionClassesEnum.MOVEMENTS.getCode(), isCancellation, requestId);
	if (!isCancellation && !isTermination)
	    countConflictDatesTransactions(movementTransaction.getEmployeeId(), movementTransaction.getExecutionDate(), movementTransaction.getEndDate());
    }

    /**
     * validate rules for a secondment transaction, officers only
     * 
     * @param movementTransaction
     *            mandate transaction to be validated
     * @param isRegistration
     *            the type of secondment transaction, if it's a workflow or a registration screen
     * @param requestId
     *            instance id of the workflow that created the transaction
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateSecondmentRules(MovementTransactionData movementTransaction, Long requestId) throws BusinessException {
	// Load necessary data
	EmployeeData emp = null;
	emp = EmployeesService.getEmployeeData(movementTransaction.getEmployeeId());
	if (emp == null)
	    throw new BusinessException("error_employeeDataError");

	boolean isCancellation = movementTransaction.getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());
	boolean isTermination = movementTransaction.getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());

	// Validations
	commonValidate(movementTransaction, emp, null, MovementTransactionViewsEnum.DECISION.getCode());
	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { movementTransaction.getEmployeeId() }, null, TransactionClassesEnum.MOVEMENTS.getCode(), isCancellation, requestId);
	if (!isCancellation && !isTermination)
	    countConflictDatesTransactions(movementTransaction.getEmployeeId(), movementTransaction.getExecutionDate(), movementTransaction.getEndDate());
    }

    /**
     * It's a wrapper for {@link #checkForCertainPositions(UnitData, int)}, used to check if the employee is in a region commander or a manager in a unit related to a region commander
     * 
     * @param employee
     *            employee that we check
     * @return true if employee exists in the mentioned positions
     * @throws BusinessException
     *             if any error occurs
     * @see #checkForCertainPositions(UnitData, int)
     */
    public static boolean checkIfEmployeeExistsInCertainPositions(EmployeeData employee) throws BusinessException {
	UnitData officialUnit = UnitsService.getUnitById(employee.getOfficialUnitId());
	int isManager = employee.getEmpId().equals(officialUnit.getOfficialManagerId()) ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode();
	return checkForCertainPositions(officialUnit, isManager);
    }

    /**
     * It's a wrapper for {@link #checkForCertainPositions(UnitData, int)}, used to check if a movement operation is on a region commander or a manager of unit related to a region commander
     * 
     * @param unitId
     *            unit id that we perform the movement operation on
     * @param asManager
     *            movement operation as manager or not
     * @return true if movement operation is on the mentioned positions
     * @throws BusinessException
     *             if any error occurs
     * @see #checkForCertainPositions(UnitData, int)
     */
    public static boolean checkIfMovementOperationOnCertainPositions(long unitId, int asManager) throws BusinessException {
	UnitData unit = UnitsService.getUnitById(unitId);
	return checkForCertainPositions(unit, asManager);
    }

    private static boolean skipMonthsRuleValidation(String extraParams) {
	if (extraParams != null && extraParams.length() > 0) {
	    String[] seperatedParams = extraParams.split(",");
	    if (seperatedParams != null && seperatedParams.length > 0) {
		for (String params : seperatedParams) {
		    String[] tokens = params.split("=");
		    if (tokens != null && tokens.length > 1 && tokens[0].equals("skipMonthsRuleValidation") && tokens[1].equals(FlagsEnum.ON.getCode() + "")) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    /**
     * Checks if given unit is a region commander or it's manager is related to a region commander
     * 
     * @param unit
     *            unit we check for it
     * @param managerFlag
     *            To check for it's manager or not
     * @return true if mentioned positions apply
     * @throws BusinessException
     *             if any error occurs
     */
    private static boolean checkForCertainPositions(UnitData unit, int managerFlag) throws BusinessException {
	UnitData parentUnit = UnitsService.getUnitById(unit.getParentUnitId());
	if (unit.getUnitTypeCode().equals(UnitTypesEnum.REGION_COMMANDER.getCode())
		|| (managerFlag == FlagsEnum.ON.getCode() && parentUnit.getUnitTypeCode().equals(UnitTypesEnum.REGION_COMMANDER.getCode())))
	    return true;

	return false;
    }

    /**
     * Counts the transactions for a specific employee that conflict with given execution date and end date
     * 
     * @param employeeId
     * @param executionDate
     *            start of the transactions
     * @param endDate
     *            end of the transactions
     * @throws BusinessException
     *             if any error occurs
     */
    private static void countConflictDatesTransactions(long employeeId, Date executionDate, Date endDate) throws BusinessException {
	try {
	    if (executionDate == null)
		throw new BusinessException("error_transactionDataError");

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", employeeId);
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriDateString(executionDate));

	    if (endDate != null) {
		qParams.put("P_END_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_END_DATE", HijriDateService.getHijriDateString(endDate));
	    } else {
		qParams.put("P_END_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_END_DATE", HijriDateService.getHijriSysDateString());
	    }

	    List<Long> result = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_CONFLICT_DATES_TRANSACTIONS.getCode(), qParams);
	    if (!result.isEmpty() && result.get(0).longValue() > 0)
		throw new BusinessException("error_mvtDatesConflict");

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * gets the future transactions that are not taken effect yet for specific employee id's or jobs id's
     * 
     * @param employeesIds
     *            array of employee id's
     * @param jobsIds
     *            array of jobs id's
     * @return count of the transactions
     * @throws BusinessException
     *             if any error occurs
     */

    public static List<MovementTransaction> getFutureEffectMoveTransactions(Long[] employeesIds, Long jobsIds[], boolean moveFlag) throws BusinessException {
	try {
	    if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
		return new ArrayList<MovementTransaction>();

	    HashMap<String, Object> qParams = new HashMap<String, Object>();

	    if (employeesIds != null && employeesIds.length > 0) {
		qParams.put("P_EMPS_IDS", employeesIds);
	    } else {
		qParams.put("P_EMPS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    if (jobsIds != null && jobsIds.length > 0) {
		qParams.put("P_JOBS_IDS", jobsIds);
	    } else {
		qParams.put("P_JOBS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_MOVEMENT_TYPE_ID", moveFlag ? MovementTypesEnum.MOVE.getCode() : (long) FlagsEnum.ALL.getCode());

	    return DataAccess.executeNamedQuery(MovementTransaction.class, QueryNamesEnum.HCM_GET_FUTUTRE_EFFECT_MOVE_TRANSACTIONS.getCode(), qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void validateRemovingPhysicalManagerFromUnit(UnitData unit, List<UnitData> employeeUnitsAsPhysicalManager, Long employeePhysicalUnitId) throws BusinessException {
	// delete employee's physical unit , he/she won't be a manager anymore
	if (unit.getId().equals(employeePhysicalUnitId))
	    throw new BusinessException("error_employeeMustBePhyManagerOnHisPhyUnit");
    }

    private static void validateAddingPhysicalManagerOnUnit(UnitData unit, List<UnitData> employeeUnitsAsPhysicalManager, Long employeePhysicalUnitId) throws BusinessException {
	Boolean sameParentFlag = new Boolean(true);
	Boolean sameHierarchicalTree = new Boolean(true);

	// case 1 -> all units have the same parent
	for (UnitData curUnit : employeeUnitsAsPhysicalManager) {
	    if (!(curUnit.getParentUnitId()).equals(unit.getParentUnitId())) {
		sameParentFlag = false; // doesn't satisfy the condition
		break;
	    }
	}
	// case 2 -> all units are in a hierarchical view
	// check on his/her top Hierarchical unit
	// check if the selected unit in the bottom of the tree
	if (!sameParentFlag) {
	    if (!employeeUnitsAsPhysicalManager.get(employeeUnitsAsPhysicalManager.size() - 1).getId().equals(unit.getParentUnitId()))
		sameHierarchicalTree = false;
	}

	// only one case must happen
	if (!sameParentFlag && !sameHierarchicalTree)
	    throw new BusinessException("error_unitsMustBeSameParentUnitsOrHierarchicalView");
    }

    private static void validateReplacedUnitManager(UnitData unit, long employeeId) throws BusinessException {
	// check if the unit has a physical Manager
	if (unit.getPhysicalManagerId() != null) {
	    // if he/she is a physical manager on exactly one unit -> it's ok to remove it
	    // if he/she is a physical manager on more than one unit -> check it's not his/her physical unit
	    // get all units of this manager
	    List<UnitData> replacedManagerUnitsAsPhysicalManager = UnitsService.getUnitsByPhysicalManager(unit.getPhysicalManagerId());
	    EmployeeData emp = EmployeesService.getEmployeeData(employeeId);
	    if (replacedManagerUnitsAsPhysicalManager.size() > 1) {
		if (unit.getId().equals(EmployeesService.getEmployeeData(unit.getPhysicalManagerId()).getPhysicalUnitId()))
		    throw new BusinessException("error_replacedManagerPhysicalUnit", new Object[] { emp.getName(), unit.getFullName() });

		else { // check if replaced manager has Hierarchical view of units -> it's allowed only to remove his/her leaf child
		    if (replacedManagerUnitsAsPhysicalManager.get(0).getId().equals(replacedManagerUnitsAsPhysicalManager.get(1).getParentUnitId())) {
			if (!(replacedManagerUnitsAsPhysicalManager.get(replacedManagerUnitsAsPhysicalManager.size() - 1).getId().equals(unit.getId())))
			    throw new BusinessException("error_replacedManagerHierarchicalPhysicalUnit");
		    }

		}
	    }
	}
    }

    /*
     * ************************************ Movement Transactions Queries ****************************************
     */

    /**
     * Gets all not executed movement transactions
     * 
     * @return list of the movement transactions
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<MovementTransactionData> getNotExecutedMovementsTransactions() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriSysDateString());
	    return DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_NOT_EXECUTED_MOVEMENTS_TRANSACTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets employees that have transactions should be rolled back
     * 
     * @return list of the employees
     * @throws BusinessException
     *             if any error occurs
     * 
     */
    private static List<EmployeeData> getEmployeesAndMovementsTransactionsForRollback() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_HIJRI_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_GET_EMPLOYESS_AND_MOVEMENTS_TRANSACTIONS_FOR_ROLLBACK.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets movement transactions using search parameters. Wrapper for {@link #searchMovementTransactions(long, Long[], String, Date, Date, long, long, int)}. Returns only one record for each (decision number and decision date), returns only electronic transactions
     * 
     * @param employeeId
     * @param categoryId
     * @param decisionNumber
     * @param decisionDateFrom
     * @param decisionDateTo
     * @param movementTypeId
     * @param etrFlag
     * @return list of the movement transactions
     * @throws BusinessException
     *             if any error occurs
     * @see #searchMovementTransactions(long, Long[], String, Date, Date, long, long, int)
     */
    public static List<MovementTransactionData> getMovementTransactions(long employeeId, long categoryId, String decisionNumber, Date decisionDateFrom, Date decisionDateTo, long movementTypeId, int etrFlag) throws BusinessException {
	Long[] categoriesIds = categoryId == FlagsEnum.ALL.getCode() ? null : new Long[] { categoryId };
	return searchMovementTransactions(employeeId, categoriesIds, decisionNumber, decisionDateFrom, decisionDateTo, movementTypeId, FlagsEnum.ALL.getCode(), etrFlag);
    }

    /**
     * Gets movement transactions using search parameters. Wrapper for {@link #searchMovementTransactions(long, Long[], String, Date, Date, long, long, int)}. Returns only one record for each (decision number and decision date) used in employee Archive returns all electronic and non electronic transactions
     * 
     * @param employeeId
     * @return list of the movement transactions
     * @throws BusinessException
     *             if any error occurs
     * @see #searchMovementTransactions(long, Long[], String, Date, Date, long, long, int)
     */
    public static List<MovementTransactionData> getMovementTransactionsHistory(long employeeId) throws BusinessException {
	return searchMovementTransactions(employeeId, null, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    /**
     * Gets movement transactions using search parameters. Wrapper for {@link #searchMovementTransactions(long, Long[], String, Date, Date, long, long, int)}. Used in Movements Inquiry
     * 
     * @param employeeId
     * @param categoriesIds
     * @param decisionNumber
     * @param decisionDateFrom
     * @param decisionDateTo
     * @param movementTypeId
     * @return list of the movement transactions
     * @throws BusinessException
     *             if any error occurs
     * @see #searchMovementTransactions(long, Long[], String, Date, Date, long, long, int)
     */
    public static List<MovementTransactionData> getMovementTransactionsForInquiry(long employeeId, Long[] categoriesIds, String decisionNumber, Date decisionDateFrom, Date decisionDateTo, long movementTypeId, long regionId) throws BusinessException {
	return searchMovementTransactions(employeeId, categoriesIds, decisionNumber, decisionDateFrom, decisionDateTo, movementTypeId, regionId, FlagsEnum.ON.getCode());
    }

    /**
     * Wrapper for {@link #getMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(String, Date, Date, Date, Date, Long[], long, long)}. For each (decision number and decision date), returns one record for single movement transactions and multiple records for multiple movement transactions
     * 
     * @param decisionNumber
     * @param decisionDateFrom
     * @param decisionDateTo
     * @return list of the movement transactions
     * @throws BusinessException
     *             if any error occurs
     * @see #getMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(String, Date, Date, Date, Date, Long[], long, long)
     */
    public static List<MovementTransactionData> getMovementTransactionsByDecisionNumberAndDecisionDate(String decisionNumber, Date decisionDateFrom, Date decisionDateTo) throws BusinessException {
	return getMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(decisionNumber, decisionDateFrom, decisionDateTo, null, null, null, (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Wrapper for {@link #getMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(String, Date, Date, Date, Date, Long[], long, long)}. For each (decision number and decision date), returns one record for single movement transactions and multiple records for multiple movement transactions
     * 
     * @param employeeId
     * @param executionDate
     * @param movementTypeId
     * @param transactionTypeId
     * @return list of the movement transactions
     * @throws BusinessException
     *             if any error occurs
     * @see #getMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(String, Date, Date, Date, Date, Long[], long, long)
     */
    public static List<MovementTransactionData> getMovementTransactionsByMovementInfo(long employeeId, Date executionDate, long movementTypeId, long transactionTypeId) throws BusinessException {
	Long[] employeesIds = null;
	if (employeeId != (long) FlagsEnum.ALL.getCode())
	    employeesIds = new Long[] { employeeId };
	return getMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(null, null, null, executionDate, executionDate, employeesIds, movementTypeId, transactionTypeId);
    }

    /**
     * Query to search for movement transactions using search parameters
     * 
     * @param decisionNumber
     * @param decisionDateFrom
     * @param decisionDateTo
     * @param executionDateFrom
     * @param executionDateTo
     * @param employeesIds
     * @param movementTypeId
     * @param transactionTypeId
     * @return list of the movement transactions
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<MovementTransactionData> getMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(String decisionNumber, Date decisionDateFrom, Date decisionDateTo, Date executionDateFrom, Date executionDateTo, Long[] employeesIds, long movementTypeId, long transactionTypeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (decisionDateFrom != null) {
		qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriDateString(decisionDateFrom));
	    } else {
		qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (decisionDateTo != null) {
		qParams.put("P_DECISION_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriDateString(decisionDateTo));
	    } else {
		qParams.put("P_DECISION_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    if (employeesIds != null && employeesIds.length > 0) {
		qParams.put("P_EMPLOYEES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EMPLOYEES_IDS", employeesIds);
	    } else {
		qParams.put("P_EMPLOYEES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EMPLOYEES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_MOVEMENT_TYPE_ID", movementTypeId);
	    qParams.put("P_TRANS_TYPE_ID", transactionTypeId);

	    if (executionDateFrom != null) {
		qParams.put("P_EXECUTION_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EXECUTION_DATE_FROM", HijriDateService.getHijriDateString(executionDateFrom));
	    } else {
		qParams.put("P_EXECUTION_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EXECUTION_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (executionDateTo != null) {
		qParams.put("P_EXECUTION_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EXECUTION_DATE_TO", HijriDateService.getHijriDateString(executionDateTo));
	    } else {
		qParams.put("P_EXECUTION_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EXECUTION_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    return DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_MOVEMENT_TRANSACTIONS_BY_DECISION_NUMBER_AND_DECISION_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query that returns list of arrays using search parameters, each array contains pairs of objects (EmployeeData and MovementTransactionData).
     * 
     * @param decisionNumber
     * @param decisionDate
     * @param employeesIds
     * @return
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<Object> getCancelledAndTerminatedMovementTransactionsByDecisionNumberAndDecisionDateAndEmployeesIds(String decisionNumber, Date decisionDate, Long[] employeesIds) throws BusinessException {
	try {
	    if (decisionNumber == null || decisionDate == null || employeesIds == null || employeesIds.length == 0) {
		return new ArrayList<Object>();
	    }

	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_DECISION_NUMBER", decisionNumber);
	    qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    qParams.put("P_EMPLOYEES_IDS", employeesIds);

	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.HCM_GET_CANCELLED_AND_TERMINATED_MOVEMENT_TRANSACTIONS_BY_DECISION_NUMBER_AND_DECISION_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query to get movement transaction by id
     * 
     * @param id
     *            movement transaction id
     * @return movement transaction
     * @throws BusinessException
     *             if any error occurs
     */
    public static MovementTransactionData getMovementTransactionById(long id) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRANS_ID", id);
	    List<MovementTransactionData> result = DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_MOVEMENT_TRANSACTIONS_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    public static MovementTransactionData getLastValidMovementTransactionForJoiningDate(long employeeId, long movementTypeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMP_ID", employeeId);
	    qParams.put("P_MOVEMENT_TYPE_ID", movementTypeId);

	    List<MovementTransactionData> movementTransactions = DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_LAST_MOVE_TRANSACTION_FOR_JOINING_DATE.getCode(), qParams);
	    if (movementTransactions != null && movementTransactions.size() > 0) {
		return movementTransactions.get(0);
	    }
	    return null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static MovementTransactionData getLastValidMovementTransactionForReturnJoiningDate(long employeeId, long movementTypeId, String applyDateString) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMP_ID", employeeId);
	    qParams.put("P_MOVEMENT_TYPE_ID", movementTypeId);
	    qParams.put("P_HIJRI_APPLY_DATE", applyDateString == null ? FlagsEnum.ALL.getCode() + "" : applyDateString);
	    qParams.put("P_HIJRI_SYS_DATE", HijriDateService.getHijriSysDateString());

	    List<MovementTransactionData> movementTransactions = DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_LAST_MOVE_TRANSACTION_FOR_RETURN_JOINING_DATE.getCode(), qParams);
	    if (movementTransactions != null && movementTransactions.size() > 0) {
		return movementTransactions.get(0);
	    }
	    return null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query to get the last valid movement transaction that can be followed by another transaction
     * 
     * @param employeeId
     *            employee id of the transaction
     * @param movementTypeId
     *            movement type id of the transaction
     * @param etrFlag
     *            electronic flag
     * @return movement transaction data
     * @throws BusinessException
     *             if any error occurs
     */
    public static MovementTransactionData getLastValidMovementTransaction(long employeeId, long movementTypeId, int etrFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMP_ID", employeeId);
	    qParams.put("P_MOVEMENT_TYPE_ID", movementTypeId);

	    Long[] transactionTypesIds = new Long[3];
	    transactionTypesIds[0] = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();
	    transactionTypesIds[1] = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();
	    // To avoid selecting a transaction that has been extended then
	    // terminated
	    transactionTypesIds[2] = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();

	    qParams.put("P_TRANS_TYPES_IDS", transactionTypesIds);
	    qParams.put("P_ETR_FLAG", etrFlag);

	    List<MovementTransactionData> movementTransactions = DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_LAST_VALID_MOVEMENT_TRANSACTION.getCode(), qParams);
	    if (movementTransactions != null && movementTransactions.size() > 0) {
		MovementTransactionData movementTransaction = movementTransactions.get(0);
		if (movementTransaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode())
		    return null;
		return movementTransaction;
	    }
	    return null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query to get the last valid movement transaction that can be followed by another transaction for a specific employee
     * 
     * @param employeeId
     * @return movement transaction data
     * @throws BusinessException
     *             if any error occurs
     */
    public static MovementTransactionData getLastMovementTransactionByEmployeeId(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    Long[] transactionTypesIds = new Long[3];
	    transactionTypesIds[0] = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();
	    transactionTypesIds[1] = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();
	    // To avoid selecting a transaction that has been extended then
	    // terminated
	    transactionTypesIds[2] = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();

	    qParams.put("P_EMP_ID", employeeId);
	    qParams.put("P_MOVEMENT_TYPE_ID", FlagsEnum.ALL.getCode());
	    qParams.put("P_TRANS_TYPES_IDS", transactionTypesIds);
	    qParams.put("P_ETR_FLAG", FlagsEnum.ALL.getCode());

	    List<MovementTransactionData> movementTransactions = DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_LAST_VALID_MOVEMENT_TRANSACTION.getCode(), qParams);
	    if (movementTransactions != null && movementTransactions.size() > 0) {
		MovementTransactionData movementTransaction = movementTransactions.get(0);
		if (movementTransaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode())
		    return null;
		return movementTransaction;
	    }
	    return null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query to get the external move (MovementTypeId = 1) transaction for a specific employee
     * 
     * @param employeeId
     * @return movement transaction data
     * @throws BusinessException
     *             if any error occurs
     */
    public static MovementTransactionData getExternalMoveTransactionByEmployeeId(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    Long[] transactionTypesIds = new Long[1];
	    transactionTypesIds[0] = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId().longValue();

	    qParams.put("P_EMP_ID", employeeId);
	    qParams.put("P_MOVEMENT_TYPE_ID", MovementTypesEnum.MOVE.getCode());
	    qParams.put("P_TRANS_TYPES_IDS", transactionTypesIds);
	    qParams.put("P_ETR_FLAG", FlagsEnum.ALL.getCode());

	    List<MovementTransactionData> movementTransactions = DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_LAST_VALID_MOVEMENT_TRANSACTION.getCode(), qParams);
	    if (movementTransactions != null && movementTransactions.size() > 0) {
		MovementTransactionData movementTransaction = movementTransactions.get(0);
		if (movementTransaction.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()))
		    return movementTransaction;
	    }
	    return null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query that returns list of movement transactions data that can be followed by another transaction, using search parameters
     * 
     * @param decisionNumber
     * @param decisionDate
     * @param categoriesIds
     * @param movementTypeId
     * @param regionId
     * @param etrFlag
     * @return list of movement transactions data
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<MovementTransactionData> getValidDecisionMembers(String decisionNumber, Date decisionDate, Long[] categoriesIds, long movementTypeId, long regionId, int etrFlag) throws BusinessException {
	try {
	    if (decisionNumber == null || decisionNumber.length() == 0 || decisionDate == null)
		throw new BusinessException("error_transactionSearchFieldsMandatory");

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DECISION_NUMBER", decisionNumber);
	    qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));

	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_MOVEMENT_TYPE_ID", movementTypeId);
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_ETR_FLAG", etrFlag);

	    List<MovementTransactionData> transactions = DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_MOVEMENT_TRANSACTIONS_VALID_DECISION_MEMBERS.getCode(), qParams);
	    if (!transactions.isEmpty()) {
		boolean isCancellation = transactions.get(0).getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
		boolean isTermination = transactions.get(0).getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
		if (isCancellation || isTermination)
		    throw new BusinessException("error_decisionCannotBeFellowByAnotherOne");
	    }
	    return transactions;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query that returns the count of transactions for a specific employee that starts after given execution date
     * 
     * @param employeeId
     * @param executionDate
     * @return count of the transactions
     * @throws BusinessException
     *             if any error occurs
     */
    private static long countEmployeeTransactionsAfterGivenExecutionDate(long employeeId, Date executionDate) throws BusinessException {
	try {
	    if (executionDate == null) {
		throw new BusinessException("error_transactionDataError");
	    }

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", employeeId);
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriDateString(executionDate));

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_EMPLOYEE_TRANSACTIONS_AFTER_GIVEN_EXECUTION_DATE.getCode(), qParams).get(0).longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query to search for movement transactions using search parameters
     * 
     * @param employeeId
     * @param categoriesIds
     * @param decisionNumber
     * @param decisionDateFrom
     * @param decisionDateTo
     * @param movementTypeId
     * @param regionId
     * @param etrFlag
     *            electronic flag
     * @return list of movement transaction data
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<MovementTransactionData> searchMovementTransactions(long employeeId, Long[] categoriesIds, String decisionNumber, Date decisionDateFrom, Date decisionDateTo, long movementTypeId, long regionId, int etrFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMPLOYEE_ID", employeeId);

	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_ETR_FLAG", etrFlag);
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (decisionDateFrom != null) {
		qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriDateString(decisionDateFrom));
	    } else {
		qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (decisionDateTo != null) {
		qParams.put("P_DECISION_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriDateString(decisionDateTo));
	    } else {
		qParams.put("P_DECISION_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    qParams.put("P_MOVEMENT_TYPE_ID", movementTypeId);
	    qParams.put("P_REGION_ID", regionId);

	    return DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_SEARCH_MOVEMENT_TRANSACTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<MovementTransaction> getLastMoveTransactionForWishes(long employeeId, String officialRegionShortDesc) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_OFFICIAL_REGION_SHORT_DESC", officialRegionShortDesc + '%');
	    return DataAccess.executeNamedQuery(MovementTransaction.class, QueryNamesEnum.HCM_GET_LAST_MOVE_TRANSACTION_FOR_WISHES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<MovementTransaction> getExternalMovementTransactions(long employeeId, Date executionDateFrom, Date executionDateTo) throws BusinessException {
	try {
	    if (executionDateFrom == null || executionDateTo == null)
		throw new BusinessException("error_general");

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_EXECUTION_DATE_FROM", HijriDateService.getHijriDateString(executionDateFrom));
	    qParams.put("P_EXECUTION_DATE_TO", HijriDateService.getHijriDateString(executionDateTo));
	    return DataAccess.executeNamedQuery(MovementTransaction.class, QueryNamesEnum.HCM_GET_EXTERNAL_MOVEMENT_TRANSACTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static MovementTransactionData getMovementTransactionForJoiningReport(long employeeId, String decisionNumber, Date decisionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_DECISION_NUMBER", decisionNumber);
	    qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    List<MovementTransactionData> result = DataAccess.executeNamedQuery(MovementTransactionData.class, QueryNamesEnum.HCM_GET_MOVEMENT_TRANSACTION_FOR_JOINING_REPORT.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Query to get a report bytes of a transaction to be printed
     * 
     * @param transaction
     * @return array of report bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getMovementDecisionBytes(MovementTransactionData transaction) throws BusinessException {
	try {
	    String reportName = "";
	    boolean isSingleReport = true;

	    Map<String, Object> parameters = new HashMap<String, Object>();
	    List<MovementTransactionData> transactions = getMovementTransactionsByDecisionNumberAndDecisionDate(transaction.getDecisionNumber(), transaction.getDecisionDate(), transaction.getDecisionDate());
	    if (transactions.size() == 1) {
		isSingleReport = true;
	    } else {
		isSingleReport = false;
	    }

	    parameters.put("P_TRANSACTION_ID", transaction.getId());
	    // SOLDIERS REPORTS
	    if (transaction.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		if (transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode() && transaction.getLocationFlag() != LocationFlagsEnum.EXTERNAL.getCode() && isSingleReport && transaction.getReplacementTransId() == null && transaction.getFreezeJobId() == null) {
		    // single internal movement without replacement.
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_MOVE_INTERNAL.getCode();
		} else if (((transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode()) && (transaction.getLocationFlag() == LocationFlagsEnum.EXTERNAL.getCode()) && isSingleReport && transaction.getReplacementTransId() == null && transaction.getFreezeJobId() == null) || (transaction.getMovementTypeId() == MovementTypesEnum.MANDATE.getCode()) || (transaction.getMovementTypeId() == MovementTypesEnum.SECONDMENT.getCode())) {
		    // single external movement without replacement, any mandate
		    // operation or any secondment operation
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_MOVE_EXTERNAL.getCode();
		} else if ((transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode()) && (transaction.getReplacementTransId() != null) && transaction.getFreezeJobId() == null) {
		    // exchange movement
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_MOVE_EXCHANGE.getCode();
		} else if ((transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode() && transaction.getFreezeJobId() == null) && (transaction.getLocationFlag() != LocationFlagsEnum.EXTERNAL.getCode()) && !isSingleReport) {
		    // collective internal movement
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_COLLECTIVE_MOVE_INTERNAL.getCode();
		} else if ((transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode()) && (transaction.getLocationFlag() == LocationFlagsEnum.EXTERNAL.getCode()) && !isSingleReport && transaction.getReplacementTransId() == null && transaction.getFreezeJobId() == null) {
		    // collective external movement
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_COLLECTIVE_MOVE_EXTERNAL.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode() && isSingleReport && transaction.getFreezeJobId() != null) {
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_MOVE_FREEZE.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode() && !isSingleReport && transaction.getFreezeJobId() != null) {
		    parameters.put("P_SOLDIERS_COUNT", String.valueOf(transactions.size()));
		    parameters.put("P_SOLDIER_START", transactions.get(0).getEmployeeName());
		    parameters.put("P_SOLDIER_END", transactions.get(transactions.size() - 1).getEmployeeName());

		    String reportDecisionName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_COLLECTIVE_MOVE_FREEZE_DECISION.getCode();
		    String reportAppendixName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_COLLECTIVE_MOVE_FREEZE_APPENDIX.getCode();
		    List<ArrayList<Object>> reportParts = new ArrayList<ArrayList<Object>>();
		    ArrayList<Object> reportDecision = new ArrayList<Object>();
		    reportDecision.add(reportDecisionName);
		    reportDecision.add(parameters);
		    reportParts.add(reportDecision);
		    ArrayList<Object> reportAppendix = new ArrayList<Object>();
		    reportAppendix.add(reportAppendixName);
		    reportAppendix.add(parameters);
		    reportParts.add(reportAppendix);
		    return getReportData(reportParts);

		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && isSingleReport && !(transaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_CANCEL_DECISION.getCode()) && !(transaction.getTransactionTypeCode() == TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode())) {
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_SUBJION.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && !isSingleReport && !(TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode() == transaction.getTransactionTypeCode() || TransactionTypesEnum.MVT_CANCEL_DECISION.getCode() == transaction.getTransactionTypeCode())) {

		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_COLLECTIVE_SUBJOIN.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && !isSingleReport && (TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode() == transaction.getTransactionTypeCode() || TransactionTypesEnum.MVT_CANCEL_DECISION.getCode() == transaction.getTransactionTypeCode())) {
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_COLLECTIVE_SUBJOIN_CANCEL_TERMINATE.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && isSingleReport && (TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode() == transaction.getTransactionTypeCode() || TransactionTypesEnum.MVT_CANCEL_DECISION.getCode() == transaction.getTransactionTypeCode())) {
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_SOLDIERS_SUBJOIN_CANCEL_TERMINATE.getCode();
		}
		// OFFICERS REPORTS
	    } else if (transaction.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
		if (transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode() && transaction.getLocationFlag() != 1 && isSingleReport && transaction.getReplacementTransId() == null) {
		    // internal officer single movement
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_MOVE_INTERNAL.getCode();
		} else if ((transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode()) && (transaction.getReplacementTransId() != null)) {
		    // officer exchange move
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_MOVE_EXCHANGE.getCode();
		} else if ((transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode()) && (transaction.getLocationFlag() != 1) && !isSingleReport) {
		    // internal officer collective movement
		    parameters.put("P_OFFICERS_COUNT", String.valueOf(transactions.size()));
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_COLLECTIVE_MOVE_INTERNAL.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && isSingleReport) {
		    // internal officer single subjoin
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_SUBJOIN.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && !isSingleReport) {
		    // internal officer collective subjoin
		    parameters.put("P_OFFICERS_COUNT", String.valueOf(transactions.size()));
		    if (TransactionTypesEnum.MVT_NEW_DECISION.getCode() == transaction.getTransactionTypeCode())
			reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_COLLECTIVE_SUBJOIN_INTERNAL.getCode();
		    else if (TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode() == transaction.getTransactionTypeCode())
			reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_COLLECTIVE_SUBJOIN_INTERNAL_EXTENSION.getCode();
		    else if (TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode() == transaction.getTransactionTypeCode())
			reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_COLLECTIVE_SUBJOIN_INTERNAL_TERMINATION.getCode();
		    else if (TransactionTypesEnum.MVT_CANCEL_DECISION.getCode() == transaction.getTransactionTypeCode())
			reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_COLLECTIVE_SUBJOIN_INTERNAL_CANCEL.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.ASSIGNMENT.getCode() && isSingleReport) {
		    // internal officer single assignment
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_ASSIGNMENT_INTERNAL.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.ASSIGNMENT.getCode() && !isSingleReport) {
		    // internal officer collective assignment
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_OFFICERS_COLLECTIVE_ASSIGNMENT_INTERNAL.getCode();
		}
	    }
	    // Employees Reports
	    else {
		parameters.put("P_EMPLOYEES_COUNT", String.valueOf(transactions.size()));
		if (transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode() && transaction.getLocationFlag() != 1 && isSingleReport && transaction.getReplacementTransId() == null) {
		    // internal employee single movement
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_EMPLOYEES_MOVE.getCode();
		} else if ((transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode()) && (transaction.getReplacementTransId() != null)) {
		    // employee exchange move
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_EMPLOYEES_MOVE_EXCHANGE.getCode();
		} else if ((transaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode()) && (transaction.getLocationFlag() != 1) && !isSingleReport) {
		    // internal employee collective movement
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_EMPLOYEES_COLLECTIVE_MOVE.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && isSingleReport) {
		    // internal employee single Assignment
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_EMPLOYEES_ASSIGNMENT.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && !isSingleReport && (TransactionTypesEnum.MVT_NEW_DECISION.getCode() == transaction.getTransactionTypeCode() || TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode() == transaction.getTransactionTypeCode())) {
		    // internal employee collective ASSIGNMENT
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_EMPLOYEES_COLLECTIVE_ASSIGNMENT.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && !isSingleReport && (TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode() == transaction.getTransactionTypeCode())) {
		    // internal employee collective assignment
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_EMPLOYEES_COLLECTIVE_ASSIGNMENT_TERMINATION.getCode();
		} else if (transaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode() && !isSingleReport && (TransactionTypesEnum.MVT_CANCEL_DECISION.getCode() == transaction.getTransactionTypeCode())) {
		    // internal employee collective assignment
		    reportName = ReportNamesEnum.MOVEMENTS_DECISION_EMPLOYEES_COLLECTIVE_ASSIGNMENT_CANCELATION.getCode();
		}
	    }

	    return getReportData(reportName, parameters);

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Gets report bytes for movement transaction statistics report by sending report parameters
     * 
     * @param regionId
     *            region id of the transactions
     * @param regionDesc
     *            region description of the transactions
     * @param categoryId
     *            category id of the transactions
     * @param fromDate
     *            start date period of the transactions
     * @param toDate
     *            end date period of the transactions
     * @param reportTitle
     *            title of the report to be printed
     * @return array of report bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getMovementsReportsBytes(long regionId, String regionDesc, long categoryId, Date fromDate, Date toDate, String reportTitle) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = ReportNamesEnum.MOVEMENTS_TRANSACTIONS_STATISTICS.getCode();
	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_REGION_DESC", regionDesc);
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));

	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_REPORT_TITLE", reportTitle);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Gets report bytes for subjoin transaction and statistical reports by sending report parameters
     * 
     * @param regionId
     *            physical region id of the employees in the transactions
     * @param categoryId
     *            category id of the transactions
     * @param employeeUnitFullName
     *            employee official unit full name during the movement transaction
     * @param employeeRegionDesc
     *            employee official region description during the movement transaction
     * @param fromDate
     *            start date period of the transactions
     * @param toDate
     *            end date period of the transactions
     * @param newDecision
     *            flag to indicate whether to print new decisions or not
     * @param extensionDecision
     *            flag to indicate whether to print extension decisions or not
     * @param terminationDecision
     *            flag to indicate whether to print extension decisions or not
     * @param cancellationDecision
     *            flag to indicate whether to print extension decisions or not
     * @param isStatistical
     *            flag to indicate whether the type of the report
     * @return array of report bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getSubjoinReportsBytes(long categoryId, String subjoinUnitFullName, String subjoinRegionDesc, String employeeUnitFullName, String employeeRegionDesc, String employeeRankDesc, String employeeMinorSpecDesc, Date fromDate, Date toDate, boolean newDecision, boolean extensionDecision, boolean terminationDecision, boolean cancellationDecision, int reasonType, boolean isStatistical) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = isStatistical ? ReportNamesEnum.MOVEMENTS_SUBJOIN_TRANSACTIONS_STATISTICS.getCode() : ReportNamesEnum.MOVEMENTS_SUBJOIN_TRANSACTIONS.getCode();
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_SUBJOIN_UNIT_FULL_NAME", (subjoinUnitFullName == null || subjoinUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : subjoinUnitFullName);
	    parameters.put("P_SUBJOIN_REGION_DESC", (subjoinRegionDesc == null || subjoinRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : subjoinRegionDesc);
	    parameters.put("P_EMPLOYEE_UNIT_FULL_NAME", (employeeUnitFullName == null || employeeUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeUnitFullName);
	    parameters.put("P_EMPLOYEE_REGION_DESC", (employeeRegionDesc == null || employeeRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeRegionDesc);
	    parameters.put("P_EMPLOYEE_RANK_DESC", (employeeRankDesc == null || employeeRankDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeRankDesc);
	    parameters.put("P_EMPLOYEE_MINOR_SPEC_DESC", (employeeMinorSpecDesc == null || employeeMinorSpecDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeMinorSpecDesc);
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_NEW_FLAG", newDecision ? 1 : 0);
	    parameters.put("P_EXTENSION_FLAG", extensionDecision ? 1 : 0);
	    parameters.put("P_TERMINATION_FLAG", terminationDecision ? 1 : 0);
	    parameters.put("P_CANCELLATION_FLAG", cancellationDecision ? 1 : 0);
	    parameters.put("P_REASON_TYPE", reasonType);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Gets report bytes for move transaction and statistical reports by sending report parameters
     * 
     * @param regionId
     *            physical region id of the employees in the transactions
     * @param categoryId
     *            category id of the transactions
     * @param employeeUnitFullName
     *            employee official unit full name during the movement transaction
     * @param employeeRegionDesc
     *            employee official region description during the movement transaction
     * @param fromDate
     *            start date period of the transactions
     * @param toDate
     *            end date period of the transactions
     * @param isStatistical
     *            flag to indicate whether the type of the report
     * @return array of report bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getMoveReportsBytes(long categoryId, String moveUnitFullName, String moveRegionDesc, String employeeUnitFullName, String employeeRegionDesc, String employeeRankDesc, String employeeMinorSpecDesc, Date fromDate, Date toDate, int reasonType, boolean isStatistical) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = isStatistical ? ReportNamesEnum.MOVEMENTS_INTERNAL_MOVES_TRANSACTIONS_STATISTICS.getCode() : ReportNamesEnum.MOVEMENTS_INTERNAL_MOVES_TRANSACTIONS.getCode();
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_MOVE_UNIT_FULL_NAME", (moveUnitFullName == null || moveUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : moveUnitFullName);
	    parameters.put("P_MOVE_REGION_DESC", (moveRegionDesc == null || moveRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : moveRegionDesc);
	    parameters.put("P_EMPLOYEE_UNIT_FULL_NAME", (employeeUnitFullName == null || employeeUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeUnitFullName);
	    parameters.put("P_EMPLOYEE_REGION_DESC", (employeeRegionDesc == null || employeeRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeRegionDesc);
	    parameters.put("P_EMPLOYEE_RANK_DESC", (employeeRankDesc == null || employeeRankDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeRankDesc);
	    parameters.put("P_EMPLOYEE_MINOR_SPEC_DESC", (employeeMinorSpecDesc == null || employeeMinorSpecDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeMinorSpecDesc);
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_REASON_TYPE", reasonType);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Gets report bytes for assignment move transaction reports by sending report parameters
     * 
     * @param categoryId
     *            category id of the transactions
     * @param moveUnitFullName
     *            employee movement transaction unit full name
     * @param moveRegionDesc
     *            employee movement transaction official region description
     * @param employeeUnitFullName
     *            employee official unit full name during the movement transaction
     * @param employeeRegionDesc
     *            employee official region description during the movement transaction
     * @param employeeRankDesc
     *            employee rank description during the movement transaction
     * @param employeeMinorSpecDesc
     *            employee minor specialization description during the movement transaction
     * @param fromDate
     *            start date period of the transactions
     * @param toDate
     *            end date period of the transactions
     * @param verbalOrderFlag
     *            flag to indicate whether the assignment based on verbal order
     * @return array of report bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getAssignmentReportsBytes(long categoryId, String moveUnitFullName, String moveRegionDesc, String employeeUnitFullName, String employeeRegionDesc, String employeeRankDesc, String employeeMinorSpecDesc, Date fromDate, Date toDate, int verbalOrderFlag) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    reportName = ReportNamesEnum.MOVEMENTS_OFFICERS_ASSIGNMENT_TRANSACTIONS.getCode();
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_MOVE_UNIT_FULL_NAME", (moveUnitFullName == null || moveUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : moveUnitFullName);
	    parameters.put("P_MOVE_REGION_DESC", (moveRegionDesc == null || moveRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : moveRegionDesc);
	    parameters.put("P_EMPLOYEE_UNIT_FULL_NAME", (employeeUnitFullName == null || employeeUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeUnitFullName);
	    parameters.put("P_EMPLOYEE_REGION_DESC", (employeeRegionDesc == null || employeeRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeRegionDesc);
	    parameters.put("P_EMPLOYEE_RANK_DESC", (employeeRankDesc == null || employeeRankDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeRankDesc);
	    parameters.put("P_EMPLOYEE_MINOR_SPEC_DESC", (employeeMinorSpecDesc == null || employeeMinorSpecDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeMinorSpecDesc);
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_VERBAL_ORDER_FLAG", verbalOrderFlag);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Gets report bytes for external movements reports by sending report parameters
     * 
     * @param categoryId
     *            category id of the transactions
     * @param location
     *            external location of the movements transaction
     * @param employeeUnitFullName
     *            employee official unit full name during the movement transaction
     * @param employeeRegionDesc
     *            employee official region description during the movement transaction
     * @param fromDate
     *            start date period of the transactions
     * @param toDate
     *            end date period of the transactions
     * @param movementTypesIds
     *            comma separated values of the movement types ids (Move, Subjoin, Officers assignment)
     * @return array of report bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getExternalMovementsReportsBytes(long categoryId, String location, String employeeUnitFullName, String employeeRegionDesc, Date fromDate, Date toDate, String movementTypesIds) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = ReportNamesEnum.MOVEMENTS_EXTERNAL_MOVEMENTS_TRANSACTIONS.getCode();
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_MOVEMENT_LOCATION", (location == null || location.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : location);
	    parameters.put("P_EMPLOYEE_UNIT_FULL_NAME", (employeeUnitFullName == null || employeeUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeUnitFullName);
	    parameters.put("P_EMPLOYEE_REGION_DESC", (employeeRegionDesc == null || employeeRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeRegionDesc);
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_MOVEMENT_TYPES_IDS", movementTypesIds);
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Gets internal assignment report bytes by sending the parameters
     * 
     * @param unitFullName
     *            unit full name of the transactions
     * @param fromDate
     *            start date period of the transactions
     * @param toDate
     *            end date period of the transactions
     * @param categoryId
     *            category id of the transactions
     * @param reportTitle
     *            title of the report
     * @return array of report bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getInternalAssignmentReportsBytes(String unitFullName, Date fromDate, Date toDate, Long categoryId, String reportTitle) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = ReportNamesEnum.MOVEMENTS_INTERNAL_ASSIGNMENTS_TRANSACTIONS.getCode();
	    parameters.put("P_UNIT_FULL_NAME", unitFullName);
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_REPORT_TITLE", reportTitle);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getJoiningDocumentBytes(long movementTransactionId, long movementTypeId) throws BusinessException {
	try {
	    String reportName = (movementTypeId == MovementTypesEnum.MOVE.getCode()) ? ReportNamesEnum.MOVEMENTS_MOVE_JOINING_DOCUMENT.getCode() : ReportNamesEnum.MOVEMENTS_SUBJOIN_JOINING_DOCUMENT.getCode();

	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_MVT_TRANSACTION_ID", movementTransactionId);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Gets report bytes for subjoin transaction according to end date report by sending report parameters
     * 
     * @param subjoinUnitFullName
     *            employee subjoin unit full name
     * @param employeeUnitFullName
     *            employee official unit full name during the movement transaction
     * @param subjoinRegionDesc
     *            employee subjoin region description
     * @param employeeRegionDesc
     *            employee official region description during the movement transaction
     * @param fromDate
     *            start date period of the transactions
     * @param toDate
     *            end date period of the transactions
     * @return array of report bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getReportOfSubjoinedEmployeesAccordingToTheirSubjoinEndDateBytes(String subjoinUnitFullName, String employeeUnitFullName, String subjoinRegionDesc, String employeeRegionDesc, Date fromDate, Date toDate) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = ReportNamesEnum.MOVEMENTS_SUBJOIN_TRANSACTIONS_ACCORDING_TO_END_DATE.getCode();
	    parameters.put("P_SUBJOIN_UNIT_FULL_NAME", (subjoinUnitFullName == null || subjoinUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : subjoinUnitFullName);
	    parameters.put("P_EMPLOYEE_UNIT_FULL_NAME", (employeeUnitFullName == null || employeeUnitFullName.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeUnitFullName);
	    parameters.put("P_SUBJOIN_REGION_DESC", (subjoinRegionDesc == null || subjoinRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : subjoinRegionDesc);
	    parameters.put("P_EMPLOYEE_REGION_DESC", (employeeRegionDesc == null || employeeRegionDesc.trim().isEmpty()) ? FlagsEnum.ALL.getCode() + "" : employeeRegionDesc);
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /*
     * ************************************ Movement transaction joining date methods ****************************************
     */
    public static void handleMovementTrasactionJoiningDate(long transactionId, Date joiningDate, Long approvedId, long loginUserId, int etrFlag, CustomSession... useSession) throws BusinessException {
	MovementTransactionData movementTransaction = getMovementTransactionById(transactionId);
	validateMovementTrasactionJoiningDate(movementTransaction, joiningDate, etrFlag);

	if (etrFlag == FlagsEnum.ON.getCode()) {
	    if (approvedId == null)
		throw new BusinessException("error_employeeDataError");

	    EmployeeData approvedEmployee = EmployeesService.getEmployeeData(approvedId);
	    if (approvedEmployee == null)
		throw new BusinessException("error_employeeDataError");
	    movementTransaction.setTransJoiningApprovedJobName(approvedEmployee.getJobDesc());
	}

	movementTransaction.setJoiningDate(joiningDate);
	List<MovementTransactionData> list = new ArrayList<MovementTransactionData>();
	movementTransaction.getMovementTransaction().setSystemUser(loginUserId + ""); // For auditing.
	list.add(movementTransaction);
	updateMovementTransactions(list, useSession);
	if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
	    doPayrollIntegration(list, FlagsEnum.OFF.getCode(), isSessionOpened(useSession) ? useSession[0] : null);
    }

    public static void handleMovementTrasactionReturnJoiningDate(long transactionId, Date returnJoiningDate, long loginUserId, CustomSession... useSession) throws BusinessException {
	MovementTransactionData movementTransaction = getMovementTransactionById(transactionId);

	movementTransaction.setReturnJoiningDate(returnJoiningDate);
	List<MovementTransactionData> list = new ArrayList<MovementTransactionData>();
	movementTransaction.getMovementTransaction().setSystemUser(loginUserId + ""); // For auditing.
	list.add(movementTransaction);
	updateMovementTransactions(list, useSession);
	if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
	    doPayrollIntegration(list, FlagsEnum.OFF.getCode(), isSessionOpened(useSession) ? useSession[0] : null);
    }

    /**
     * Updates the transaction joining date, used in the employee file
     * 
     * @param transactionId
     *            transaction id of the transaction to update its joining date
     * @param joiningDate
     *            joining update to be set in the transactions
     * @param login
     *            user id to be used in auditing the operation
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    public static void validateMovementTrasactionJoiningDate(MovementTransactionData movementTransaction, Date joiningDate, int etrFlag) throws BusinessException {
	if (movementTransaction == null)
	    throw new BusinessException("error_transactionDataError");

	if (joiningDate == null || !HijriDateService.isValidHijriDate(joiningDate))
	    throw new BusinessException("error_invalidHijriDate");

	if (joiningDate.after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_movementJoiningDateCannotBeAfterSystemDate");

	if (etrFlag == FlagsEnum.ON.getCode() && movementTransaction.getJoiningDate() != null)
	    throw new BusinessException("error_joiningDateExist");

	if (movementTransaction.getTransJoiningApprovedJobName() != null && movementTransaction.getTransJoiningApprovedJobName().trim().length() != 0)
	    throw new BusinessException("error_joiningDateAlreadyAddedByRequest");

	if (joiningDate.before(movementTransaction.getExecutionDate())) {
	    if (movementTransaction.getMovementTypeId() == MovementTypesEnum.MOVE.getCode())
		throw new BusinessException("error_movingDateCannotBeBeforeExecutionDate");

	    else if (movementTransaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode())
		throw new BusinessException("error_subjoiningDateCannotBeBeforeExecutionDate");
	}
	if (movementTransaction.getEndDate() != null && joiningDate.after(movementTransaction.getEndDate()))
	    throw new BusinessException("error_movementJoiningDateCannotBeAfterEndDate");
    }

    public static void validateMovementTrasactionReturnJoiningDate(MovementTransactionData movementTransaction, Date returnJoiningDate) throws BusinessException {
	if (movementTransaction == null)
	    throw new BusinessException("error_transactionDataError");

	if (returnJoiningDate == null || !HijriDateService.isValidHijriDate(returnJoiningDate))
	    throw new BusinessException("error_invalidHijriDate");

	if (returnJoiningDate.after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_movementJoiningDateCannotBeAfterSystemDate");

	if (!returnJoiningDate.after(movementTransaction.getEndDate())) {
	    if (movementTransaction.getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode())
		throw new BusinessException("error_subReturnjoiningDateCannotBeBeforeEndDate");
	}
    }

    /*
     * ************************************ Scheduler methods ****************************************
     */

    /**
     * Please don't use this method, Only used by the scheduler Executes the scheduler effect and roll back methods
     * 
     * @throws BusinessException
     *             if any error occurs
     */
    public static void executeScheduledTransactions() {
	// Do the effect of the not executed transactions
	doScheduledMovementsTransactionsEffect();

	// Roll back the effect of the ended transactions
	rollbackScheduledMovementsTransactionsEffect();
    }

    /**
     * Please don't use this method, Only used by the scheduler Executes the scheduler effect for movements transactions
     * 
     * @throws BusinessException
     *             if any error occurs
     */
    private static void doScheduledMovementsTransactionsEffect() {
	// select all transactions with execution/end date today or
	// before with effectFlag = false
	List<MovementTransactionData> notExecutesTransList;
	try {
	    notExecutesTransList = MovementsService.getNotExecutedMovementsTransactions();
	} catch (BusinessException e) {
	    return;
	}

	if (notExecutesTransList != null) {
	    List<MovementTransactionData> list = new ArrayList<MovementTransactionData>();
	    List<MovementTransactionData> excludedList = new ArrayList<MovementTransactionData>();
	    for (int i = 0; i < notExecutesTransList.size(); i++) {
		notExecutesTransList.get(i).setEffectFlag(FlagsEnum.ON.getCode());

		if (notExecutesTransList.get(i).getEmpPhysicalRegionId() == null && !notExecutesTransList.get(i).getMovementTypeId().equals(MovementTypesEnum.MOVE.getCode()))
		    excludedList.add(notExecutesTransList.get(i));
		else
		    list.add(notExecutesTransList.get(i));

		if (notExecutesTransList.size() > i + 1
			&& notExecutesTransList.get(i).getDecisionNumber().equals(notExecutesTransList.get(i + 1).getDecisionNumber())
			&& notExecutesTransList.get(i).getDecisionDateString().equals(notExecutesTransList.get(i + 1).getDecisionDateString())) {

		    continue;
		}

		// If we excluded all transactions from list as their all employees are terminated
		if (!list.isEmpty()) {
		    CustomSession session = DataAccess.getSession();
		    try {
			session.beginTransaction();

			if (list.get(0).getMovementTypeId() == MovementTypesEnum.MOVE.getCode()) {
			    // Do MOVE effect
			    doMoveEffects(list, list.get(0).getReplacementTransId() != null, session);
			} else if (list.get(0).getMovementTypeId() == MovementTypesEnum.SUBJOIN.getCode()) {
			    // Do SUBJOIN effect
			    doSubjoinEffects(list, session);
			} else if (list.get(0).getMovementTypeId() == MovementTypesEnum.ASSIGNMENT.getCode()) {
			    // Do ASSIGNMENT effect
			    doAssignmentEffect(list, session);
			} else if (list.get(0).getMovementTypeId() == MovementTypesEnum.MANDATE.getCode()) {
			    // Do MANDATE effect
			    doMandateEffects(list, session);
			} else if (list.get(0).getMovementTypeId() == MovementTypesEnum.SECONDMENT.getCode()) {
			    // Do SECONDMENT effect
			    doSecondmentEffects(list, session);
			}

			for (MovementTransactionData m : list) {
			    DataAccess.updateEntity(m.getMovementTransaction(), session);
			}

			session.commitTransaction();
		    } catch (Exception e) {
			session.rollbackTransaction();
			e.printStackTrace();
		    } finally {
			list = new ArrayList<MovementTransactionData>();

			try {
			    session.close();
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}
	    }

	    if (!excludedList.isEmpty()) {
		CustomSession session = DataAccess.getSession();
		try {
		    session.beginTransaction();

		    for (MovementTransactionData excludedTransaction : excludedList)
			DataAccess.updateEntity(excludedTransaction.getMovementTransaction(), session);

		    session.commitTransaction();
		} catch (Exception e) {
		    session.rollbackTransaction();
		    e.printStackTrace();
		} finally {
		    session.close();
		}
	    }

	}

    }

    /**
     * Please don't use this method, Only used by the scheduler Rolls back the scheduler effect for movements transactions
     * 
     * @throws BusinessException
     *             if any error occurs
     */
    private static void rollbackScheduledMovementsTransactionsEffect() {
	List<EmployeeData> employeesList;
	try {
	    employeesList = getEmployeesAndMovementsTransactionsForRollback();
	} catch (BusinessException e) {
	    return;
	}

	for (EmployeeData employee : employeesList) {
	    CustomSession session = DataAccess.getSession();
	    try {
		session.beginTransaction();

		Map<Long, UnitData> unitsMap = new HashMap<Long, UnitData>();
		adjustUnitsManagers(unitsMap, employee, null, false, false);
		// Apply the changes made in he adjust Units Managers method
		UnitsService.modifyUnitsManagers(unitsMap.values(), false, session);

		employee.setStatusId(EmployeeStatusEnum.ON_DUTY.getCode());
		employee.setPhysicalUnitId(employee.getOfficialUnitId());
		EmployeesService.updateEmployee(employee, session);
		EmployeeLog employeeLog = new EmployeeLog.Builder().setPhysicalUnitId(employee.getOfficialUnitId()).constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), null, null, HijriDateService.getHijriSysDate(), DataAccess.getTableName(MovementTransaction.class)).build();
		LogService.logEmployeeData(employeeLog, session);

		// Invalidate employee inbox and delegations
		BusinessWorkflowCooperation.invalidateEmployeesInboxAndDelegations(new Long[] { employee.getEmpId() }, TransactionClassesEnum.MOVEMENTS.getCode(), session);

		session.commitTransaction();
	    } catch (Exception e) {
		session.rollbackTransaction();
		e.printStackTrace();
	    } finally {
		session.close();
	    }
	}

    }
}
