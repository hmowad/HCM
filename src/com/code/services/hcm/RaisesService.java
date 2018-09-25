package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.dal.orm.hcm.raises.RaiseTransactionData;
import com.code.enums.DegreesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RaiseEmployeesTypesEnum;
import com.code.enums.RaiseStatusEnum;
import com.code.enums.RaiseTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

public class RaisesService extends BaseService {

    /**
     * private constructor to prevent instantiation
     * 
     */
    private RaisesService() {

    }

    /***************************** Object *****************************/
    /*---------------------------Operations---------------------------*/
    /*---------------------------Validations--------------------------*/
    /*---------------------------Queries------------------------------*/
    /*---------------------------Reports------------------------------*/

    /******************************************* Raise ************************************************/
    /*----------------------------------------Operations----------------------------------------------*/
    /**
     * Add a raise object to database
     * 
     * @param raise
     *            The raise will be added in DB
     * @param raise
     *            The raise will be added in DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void addRaise(Raise raise, CustomSession... useSession) throws BusinessException {

	validateRaise(raise);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    raise.setStatus(RaiseStatusEnum.INITIAL.getCode());
	    DataAccess.addEntity(raise, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    raise.setId(null);
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    /**
     * Add a raise employee object to database
     * 
     * @param raiseEmployeeData
     *            The raise employee will be added in DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void addRaiseEmployee(RaiseEmployeeData raiseEmployeeData, CustomSession... useSession) throws BusinessException {
	validateRaiseEmployee(raiseEmployeeData);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(raiseEmployeeData.getRaiseEmployee(), session);
	    raiseEmployeeData.setId(raiseEmployeeData.getRaiseEmployee().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    raiseEmployeeData.setId(null);

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Add a Annual Raise object to database
     * 
     * @param raise
     *            The raise will be added in DB
     */
    public static void addAnnualRaise(Raise raise) {
	try {
	    addRaise(raise);
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Add a Additional Raise object to database
     * 
     * @param raise
     *            The raise will be added in DB
     * @param raiseEmployeeDataList
     *            The raiseEmployeeDataList will be added in DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addAdditionalRaise(Raise raise, List<RaiseEmployeeData> raiseEmployeeDataList, CustomSession... useSession) throws BusinessException {
	if (raise.getType() != RaiseTypesEnum.ADDITIONAL.getCode()) {
	    throw new BusinessException("error_general");
	}
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (raiseEmployeeDataList.size() != 0) {
		addRaise(raise);
		for (RaiseEmployeeData raiseEmployeeData : raiseEmployeeDataList) {
		    raiseEmployeeData.setRaiseId(raise.getId());
		    addRaiseEmployee(raiseEmployeeData, session);
		}
	    } else {
		throw new BusinessException("error_mustAddOneEmployeeAtLeastToSaveAdditionalRaise");
	    }

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

    /**
     * Updates a raise object in database
     * 
     * @param raise
     *            The raise will be updated in DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void updateRaise(Raise raise, CustomSession... useSession) throws BusinessException {
	validateRaise(raise);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(raise, session);

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

    /**
     * Deletes a raise object from database
     * 
     * @param raise
     *            The raise will be deleted from DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deleteRaise(Raise raise, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    List<RaiseEmployeeData> raiseEmployeeData = getRaiseEmployeeByRaiseId(raise.getId());
	    deleteRaiseEmployees(raiseEmployeeData, session);
	    DataAccess.deleteEntity(raise, session);

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

    /*----------------------------------------Validations----------------------------------------------*/

    /**
     * Validates raise object
     * 
     * @param raise
     * 
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateRaise(Raise newRaise) throws BusinessException {
	List<Raise> raisesList;
	if (newRaise.getId() == null)
	    raisesList = getRaises(-1, -1, null, null, newRaise.getDecisionNumber(), newRaise.getExecutionDate(), newRaise.getExecutionDate(), -1, -1, -1);
	else
	    raisesList = getRaises(newRaise.getId(), -1, null, null, newRaise.getDecisionNumber(), newRaise.getExecutionDate(), newRaise.getExecutionDate(), -1, -1, -1);
	if (raisesList.size() != 0)
	    throw new BusinessException("error_decisionNumberAndExecutionDateCannotBeRepeated");
	if (newRaise.getDecisionNumber() == null)
	    throw new BusinessException("error_decisionNumberIsMandatory");
	if (newRaise.getDecisionDate() == null)
	    throw new BusinessException("error_decisionDateIsMandatory");
	if (newRaise.getCategoryId() == null)
	    throw new BusinessException("error_categoryIdIsMandatory");
	if (newRaise.getExecutionDate() == null)
	    throw new BusinessException("error_raiseDateIsMandatory");
    }

    /*------------------------------------------Queries------------------------------------------------*/
    /**
     * Wrapper for {@link #searchRaises()} to get all raises
     * 
     * @return array list of raises objects
     * @throws BusinessException
     */
    public static List<Raise> getAllRaises() throws BusinessException {
	return searchRaises(-1, -1, null, null, null, null, null, -1, -1, -1);

    }

    public static Raise getRaiseById(long id) throws BusinessException {
	List<Raise> result = searchRaises(-1, id, null, null, null, null, null, -1, -1, -1);
	if (result == null || result.size() == 0)
	    return null;
	return result.get(0);
    }

    public static List<Raise> getRaises(long excludedId, long id, Date decisionDateFrom, Date decisionDateTo, String decisionNumber, Date executionDateFrom, Date executionDateTo, long categoryId, long type, long status) throws BusinessException {
	return searchRaises(excludedId, id, decisionDateFrom, decisionDateTo, decisionNumber, executionDateFrom, executionDateTo, categoryId, type, status);
    }

    private static List<Raise> searchRaises(long excludedId, long id, Date decisionDateFrom, Date decisionDateTo, String decisionNumber, Date executionDateFrom, Date executionDateTo, long categoryId, long type, long status) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EXCLUDED_ID", excludedId);
	    qParams.put("P_ID", id);
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + decisionNumber + '%');
	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_TYPE", type);
	    qParams.put("P_STATUS", status);

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
	    return DataAccess.executeNamedQuery(Raise.class, QueryNamesEnum.HCM_RAISES_SEARCH_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*************************************** Raise Employee ********************************************/
    /*----------------------------------------Operations----------------------------------------------*/

    /**
     * Updates a raiseEmployeeData object in database
     * 
     * @param raise
     *            The raise will be updated in DB
     * @param raiseEmployeeDataToAddList
     *            The raise employees to be added to the DB
     * @param raiseEmployeeDataToDeleteList
     *            The raise employees to be deleted from DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */

    public static void updateRaiseAndEmployees(Raise raise, List<RaiseEmployeeData> raiseEmployeeDataToAddList, List<RaiseEmployeeData> raiseEmployeeDataToDeleteList, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    updateRaise(raise, session);
	    for (RaiseEmployeeData raiseEmployeeDataToAdd : raiseEmployeeDataToAddList) {
		raiseEmployeeDataToAdd.setRaiseId(raise.getId());
		addRaiseEmployee(raiseEmployeeDataToAdd, session);
	    }

	    deleteRaiseEmployees(raiseEmployeeDataToDeleteList, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    /**
     * Deletes a raiseEmployeeData object from database
     * 
     * @param raiseEmployeeData
     *            The raiseEmployeeData will be deleted from DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deleteRaiseEmployee(RaiseEmployeeData raiseEmployeeData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    DataAccess.deleteEntity(raiseEmployeeData.getRaiseEmployee(), session);

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

    /**
     * Deletes raiseEmployeeDataList of objects from database
     * 
     * @param raiseEmployeeDataList
     *            The raiseEmployeeDataList will be deleted from DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deleteRaiseEmployees(List<RaiseEmployeeData> raiseEmployeeDataList, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (RaiseEmployeeData raiseEmployeeData : raiseEmployeeDataList) {
		deleteRaiseEmployee(raiseEmployeeData, session);
	    }

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

    /**
     * construct a new deserved Employee object
     * 
     * @param employeeId
     *            deserved employee id
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static RaiseEmployeeData constructDeservedEmployee(Long employeeId) throws BusinessException {
	RaiseEmployeeData newDeservedEmployee = new RaiseEmployeeData();
	EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	if (employee == null)
	    throw new BusinessException("error_general");
	newDeservedEmployee.setEmpId(employeeId);
	newDeservedEmployee.setEmpJobName(employee.getJobDesc());
	newDeservedEmployee.setEmpName(employee.getName());
	newDeservedEmployee.setEmpDegreeId(employee.getDegreeId());
	newDeservedEmployee.setEmpDegreeDesc(employee.getDegreeDesc());
	newDeservedEmployee.setEmpRankDesc(employee.getRankDesc());
	return newDeservedEmployee;
    }
    /*----------------------------------------Validations----------------------------------------------*/

    /**
     * Validates raiseEmployeeData object
     * 
     * @param raiseEmployeeData
     * 
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateRaiseEmployee(RaiseEmployeeData raiseEmployeeData) throws BusinessException {
	Raise raise = getRaiseById(raiseEmployeeData.getRaiseId());
	if (raiseEmployeeData.getEmpDegreeId() == DegreesEnum.FIFTEENTH.getCode())
	    throw new BusinessException("error_thisEmployeeMustBeExcludedForEndOfLadder");
	if (raise.getType() == RaiseTypesEnum.ADDITIONAL.getCode()) {
	    if (raiseEmployeeData.getEmpNewDegreeId() <= raiseEmployeeData.getEmpDegreeId())
		throw new BusinessException("error_newDegreeOfEmployeeMustBeBiggerThanCurrentDegreeOfEmployee");
	}
    }

    /*------------------------------------------Queries------------------------------------------------*/
    /**
     * get raise employees by raiseId
     * 
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployeeData> getRaiseEmployeeByRaiseId(long raiseId) throws BusinessException {
	Map<String, Object> qParams = new HashMap<String, Object>();

	try {
	    qParams.put("P_RAISE_ID", raiseId);
	    return DataAccess.executeNamedQuery(RaiseEmployeeData.class, QueryNamesEnum.HCM_RAISES_GET_RAISE_EMPLOYEES_BY_RAISE_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*------------------------------------------Reports------------------------------------------------*/
    public static byte[] getRaiseEmployeesReportBytes(String decisionNumber, Date decisionDate, Integer deservedFlag) throws BusinessException {
	return null;
    }

    /*************************************** Raise Transaction *****************************************/
    /*----------------------------------------Operations----------------------------------------------*/

    /**
     * Add a raiseTransactionData object to database
     * 
     * @param raiseTransactionData
     *            The raiseTransactionData will be added in DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addRaiseTransaction(RaiseTransactionData raiseTransactionData, CustomSession... useSession) throws BusinessException {
	validateRaiseTransactionData(raiseTransactionData);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(raiseTransactionData.getRaiseTransaction(), session);
	    raiseTransactionData.setId(raiseTransactionData.getRaiseTransaction().getId());
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    raiseTransactionData.setId(null);
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateRaiseTransaction(RaiseTransactionData raiseTransactionData, CustomSession... useSession) throws BusinessException {
	validateRaiseTransactionData(raiseTransactionData);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(raiseTransactionData.getRaiseTransaction(), session);
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static RaiseTransactionData constructRaiseTransaction(long managerId, Raise raise, long employeeId, int deservedFlag, Long newDegreeId, String exclusionReason) throws BusinessException {
	RaiseTransactionData transaction = new RaiseTransactionData();

	EmployeeData emp = EmployeesService.getEmployeeData(employeeId);

	transaction.setRaiseCategoryId(raise.getCategoryId());
	transaction.setRaiseType(raise.getType());
	transaction.setRaiseDecisionNumber(raise.getDecisionNumber());
	transaction.setRaiseDecisionDate(raise.getDecisionDate());
	transaction.setRaiseExecutionDate(raise.getExecutionDate());

	transaction.setEmpId(emp.getEmpId());
	transaction.setDeservedFlag(deservedFlag);
	if (deservedFlag == RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode())
	    transaction.setExclusionReason(exclusionReason);

	if (raise.getType().intValue() == RaiseTypesEnum.ANNUAL.getCode())
	    transaction.setEmpNewDegreeId(emp.getDegreeId() + 1);
	else
	    transaction.setEmpNewDegreeId(newDegreeId);

	transaction.setTransEmpDegreeDesc(emp.getDegreeDesc());
	transaction.setTransEmpJobName(emp.getJobDesc());
	transaction.setTransEmpRankDesc(emp.getRankDesc());
	// TODO set transempjobrankdesc
	transaction.setTransEmpUnitFullName(emp.getPhysicalUnitFullName());

	transaction.setEmpDecisionApprovedId(managerId);
	transaction.setEmpOriginalDecisionApprovedId(managerId);
	transaction.seteFlag(FlagsEnum.OFF.getCode());
	transaction.setMigFlag(FlagsEnum.OFF.getCode());

	transaction.setEffectFlag(raise.getExecutionDate().after(HijriDateService.getHijriSysDate()) ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode());

	return transaction;

    }

    public static void approve(Raise raise, long managerId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	// TODO get employees
	List<RaiseEmployeeData> deserved = new ArrayList<>();
	List<RaiseEmployeeData> excluded = new ArrayList<>();
	List<RaiseEmployeeData> anotherExcluded = new ArrayList<>();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    for (RaiseEmployeeData raiseEmployee : deserved) {
		RaiseTransactionData transaction = constructRaiseTransaction(managerId, raise, raiseEmployee.getEmpId(), RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode(), raiseEmployee.getEmpNewDegreeId(), raiseEmployee.getExclusionReason());
		addRaiseTransaction(transaction, session);
		doRaiseEffect(transaction, session);
	    }
	    for (RaiseEmployeeData raiseEmployee : excluded) {
		RaiseTransactionData transaction = constructRaiseTransaction(managerId, raise, raiseEmployee.getEmpId(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode(), raiseEmployee.getEmpNewDegreeId(), raiseEmployee.getExclusionReason());
		addRaiseTransaction(transaction, session);
		doRaiseEffect(transaction, session);
	    }
	    for (RaiseEmployeeData raiseEmployee : anotherExcluded) {
		RaiseTransactionData transaction = constructRaiseTransaction(managerId, raise, raiseEmployee.getEmpId(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode(), raiseEmployee.getEmpNewDegreeId(), raiseEmployee.getExclusionReason());
		addRaiseTransaction(transaction, session);
		doRaiseEffect(transaction, session);
	    }
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    private static void doRaiseEffect(RaiseTransactionData transaction, CustomSession session) throws BusinessException {
	if (transaction.getEffectFlag().intValue() == FlagsEnum.ON.getCode()) {
	    if (transaction.getRaiseType().intValue() == RaiseTypesEnum.ANNUAL.getCode()) {
		EmployeeData emp = EmployeesService.getEmployeeData(transaction.getId());
		emp.setLastAnnualRaiseDate(transaction.getRaiseExecutionDate());
		EmployeesService.updateEmployee(emp, session);
	    }
	}
    }

    public static void executeScheduledRaiseTransactions() throws BusinessException {
	List<RaiseTransactionData> list = getNotExecutedRaisesTransactions();
	for (RaiseTransactionData transaction : list) {
	    CustomSession session = DataAccess.getSession();
	    try {
		session.beginTransaction();

		transaction.setEffectFlag(FlagsEnum.ON.getCode());
		updateRaiseTransaction(transaction, session);
		doRaiseEffect(transaction, session);

		session.commitTransaction();
	    } catch (Exception e) {
		e.printStackTrace();
		session.rollbackTransaction();
	    } finally {
		try {
		    session.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	}
    }

    /*----------------------------------------Validations----------------------------------------------*/

    /**
     * Validates raiseTransactionData object
     * 
     * @param raiseTransactionData
     * 
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateRaiseTransactionData(RaiseTransactionData raiseTransactionData) throws BusinessException {
	/* To Do */
    }

    /*------------------------------------------Queries------------------------------------------------*/
    private static List<RaiseTransactionData> getNotExecutedRaisesTransactions() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriSysDateString());
	    return DataAccess.executeNamedQuery(RaiseTransactionData.class, QueryNamesEnum.HCM_RAISE_TRANSACTION_DATA_GET_NOT_EXECUTED_RAISES_TRANSACTIONS.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
