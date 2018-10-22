package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.BaseEntity;
import com.code.dal.orm.hcm.employees.Employee;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.payroll.PayrollSalary;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.dal.orm.hcm.raises.RaiseTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RaiseEmployeesTypesEnum;
import com.code.enums.RaiseStatusEnum;
import com.code.enums.RaiseTypesEnum;
import com.code.enums.ReportNamesEnum;
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
    public static void addRaise(Raise raise, CustomSession... useSession) throws BusinessException {
	validateRaise(raise);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    DataAccess.addEntity(raise, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    e.printStackTrace();
	    raise.setId(null);
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
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
	if (raise.getType() != RaiseTypesEnum.ADDITIONAL.getCode())
	    throw new BusinessException("error_general");

	if (raiseEmployeeDataList.isEmpty())
	    throw new BusinessException("error_mustAddOneEmployeeAtLeastToSaveAdditionalRaise");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    addRaise(raise, session);
	    addRaiseEmployees(raiseEmployeeDataList, raise.getId(), session);

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
	if (raise.getStatus().intValue() == RaiseStatusEnum.APPROVED.getCode())
	    throw new BusinessException("error_general");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    deleteRaiseEmployeesByRaiseId(raise.getId(), session);
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

    /**
     * Construct a Raise object
     * 
     * @param type
     *            Type of raise annual/additional
     * 
     * @return new object of Raise
     */
    public static Raise constructRaise(int type) {
	Raise raise = new Raise();
	raise.setType(type);
	raise.setStatus(RaiseStatusEnum.INITIAL.getCode());
	return raise;
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
	List<Raise> raisesList = getRaises(newRaise.getId() == null ? FlagsEnum.ALL.getCode() : newRaise.getId(), newRaise.getDecisionDate(), newRaise.getDecisionNumber(), newRaise.getType());
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
     * Wrapper for {@link #searchRaises()} to get raise by id
     * 
     * @param id
     * @return array list of raises objects
     * @throws BusinessException
     */
    public static Raise getRaiseById(long id) throws BusinessException {
	List<Raise> result = searchRaises(FlagsEnum.ALL.getCode(), id, null, null, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Wrapper for {@link #searchRaises()} to validate Raise Data
     * 
     * @param excludedId
     * @param decisionDate
     * @param decisionNumber
     * @param type
     * @return array list of raises objects
     * @throws BusinessException
     */
    private static List<Raise> getRaises(long excludedId, Date decisionDate, String decisionNumber, long type) throws BusinessException {
	return searchRaises(excludedId, FlagsEnum.ALL.getCode(), decisionDate, decisionDate, decisionNumber, null, null, FlagsEnum.ALL.getCode(), type, FlagsEnum.ALL.getCode());
    }

    /**
     * Wrapper for {@link #searchRaises()} to get raises
     * 
     * @param excludedId
     * @param id
     * @param decisionDateFrom
     * @param decisionDateTo
     * @param decisionNumber
     * @param executionDateFrom
     * @param executionDateTo
     * @param categoryId
     * @param type
     * @param status
     * @return array list of raises objects
     * @throws BusinessException
     */
    public static List<Raise> getRaises(long excludedId, long id, Date decisionDateFrom, Date decisionDateTo, String decisionNumber, Date executionDateFrom, Date executionDateTo, long categoryId, long type, long status) throws BusinessException {
	return searchRaises(excludedId, id, decisionDateFrom, decisionDateTo, decisionNumber, executionDateFrom, executionDateTo, categoryId, type, status);
    }

    /**
     * search raises
     * 
     * @param excludedId
     * @param id
     * @param decisionDateFrom
     * @param decisionDateTo
     * @param decisionNumber
     * @param executionDateFrom
     * @param executionDateTo
     * @param categoryId
     * @param type
     * @param status
     * @return array list of raises objects
     * @throws BusinessException
     */
    private static List<Raise> searchRaises(long excludedId, long id, Date decisionDateFrom, Date decisionDateTo, String decisionNumber, Date executionDateFrom, Date executionDateTo, long categoryId, long type, long status) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EXCLUDED_ID", excludedId);
	    qParams.put("P_ID", id);
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
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
	    return DataAccess.executeNamedQuery(Raise.class, QueryNamesEnum.HCM_SEARCH_RAISES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*************************************** Raise Employee ********************************************/
    /*----------------------------------------Operations----------------------------------------------*/

    /**
     * Saves additional raise in database by updating raise and manipulating raise employees
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
    public static void saveAdditionalRaiseData(Raise raise, List<RaiseEmployeeData> raiseEmployeeDataToAddList, List<RaiseEmployeeData> raiseEmployeeDataToDeleteList, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    updateRaise(raise, session);
	    addRaiseEmployees(raiseEmployeeDataToAddList, raise.getId(), session);

	    if (raiseEmployeeDataToDeleteList != null && !raiseEmployeeDataToDeleteList.isEmpty()) {
		deleteRaiseEmployees(raiseEmployeeDataToDeleteList, session);
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

    public static void addRaiseEmployees(List<RaiseEmployeeData> raiseEmployeesData, long raiseId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<BaseEntity> beans = new ArrayList<>();
	    for (RaiseEmployeeData raiseEmployeeData : raiseEmployeesData) {
		raiseEmployeeData.setRaiseId(raiseId);
		beans.add(raiseEmployeeData.getRaiseEmployee());
	    }
	    DataAccess.addMultipleEntities(beans, session);
	    for (RaiseEmployeeData raiseEmployeeData : raiseEmployeesData) {
		raiseEmployeeData.setId(raiseEmployeeData.getRaiseEmployee().getId());
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (RaiseEmployeeData raiseEmployeeData : raiseEmployeesData) {
		raiseEmployeeData.setId(null);
	    }
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
     * Updates a raiseEmployeeData list from database
     * 
     * @param raiseEmployeeData
     *            The raiseEmployeeData will be deleted from DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void updateRaiseEmployeesList(List<RaiseEmployeeData> raiseEmployeeDataToUpdateList, String loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    for (RaiseEmployeeData raiseEmp : raiseEmployeeDataToUpdateList) {
		DataAccess.updateEntity(raiseEmp.getRaiseEmployee(), session);
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
		DataAccess.deleteEntity(raiseEmployeeData.getRaiseEmployee(), session);
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
     * construct a new raise employee object
     * 
     * @param empDate
     *            employee object
     * @param raiseId
     *            id of the master raise object
     * @param deservedFlag
     *            employee status if he is deserved or excluded
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static RaiseEmployeeData constructRaiseEmployeeData(EmployeeData empData, Long raiseId, long raiseType, int deservedFlag) throws BusinessException {
	RaiseEmployeeData raiseEmployeeData = new RaiseEmployeeData();
	raiseEmployeeData.setRaiseId(raiseId);
	raiseEmployeeData.setEmpId(empData.getEmpId());
	raiseEmployeeData.setEmpDegreeId(empData.getDegreeId());
	raiseEmployeeData.setEmpDegreeDesc(empData.getDegreeDesc());
	if (raiseType == RaiseTypesEnum.ANNUAL.getCode()) {
	    if (deservedFlag == RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode())
		raiseEmployeeData.setEmpNewDegreeId(empData.getDegreeId() + 1);
	    else
		raiseEmployeeData.setEmpNewDegreeId(empData.getDegreeId());
	}
	raiseEmployeeData.setEmpName(empData.getName());
	raiseEmployeeData.setEmpJobId(empData.getJobId());
	raiseEmployeeData.setEmpJobName(empData.getJobDesc());
	raiseEmployeeData.setEmpRankId(empData.getRankId());
	raiseEmployeeData.setEmpRankDesc(empData.getRankDesc());
	raiseEmployeeData.setEmpDeservedFlag(deservedFlag);
	return raiseEmployeeData;
    }

    public static List<RaiseEmployeeData> generateRaiseEmployeesForAnnualRaise(Raise raise, Date executionDate) throws BusinessException {
	List<RaiseEmployeeData> endOfLadderEmpRaiseData = new ArrayList<>();
	List<RaiseEmployeeData> allRaiseEmployees = new ArrayList<>();
	List<RaiseEmployeeData> deservedEmpRaiseData = new ArrayList<>();
	List<RaiseEmployeeData> unDeservedEmpRaiseData = new ArrayList<>();
	List<EmployeeData> unDeservedEmpData = new ArrayList<>();

	Map<Long, Long> rankDegressHashMap = new HashMap<Long, Long>();

	List<PayrollSalary> allEndOfLadderDegreesForCategory = PayrollsService.getEndOfLadderForAllRanksOfCategory(raise.getCategoryId());
	for (PayrollSalary endOfLadderDegree : allEndOfLadderDegreesForCategory) {
	    rankDegressHashMap.put(endOfLadderDegree.getRankId(), endOfLadderDegree.getDegreeId());
	}

	List<EmployeeData> allDeservedEmpData = getDeservedEmployeesForAnnualRaise(raise.getId(), executionDate, null);
	unDeservedEmpData = getUnDeservedEmployeesForAnnualRaise(raise.getId(), executionDate);

	for (EmployeeData emp : allDeservedEmpData) {
	    if (emp.getDegreeId().equals(rankDegressHashMap.get(emp.getRankId()))) {
		// convert list of EmployeeData to RaiseEmployeeData
		endOfLadderEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseTypesEnum.ANNUAL.getCode(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode()));
	    } else {
		deservedEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseTypesEnum.ANNUAL.getCode(), RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode()));
	    }
	}

	for (EmployeeData emp : unDeservedEmpData) {
	    unDeservedEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseTypesEnum.ANNUAL.getCode(), RaiseEmployeesTypesEnum.NOT_DESERVED_EMPLOYEES.getCode()));
	}

	// add deserved , undeserved and end of ladder employees
	allRaiseEmployees.addAll(deservedEmpRaiseData);
	allRaiseEmployees.addAll(unDeservedEmpRaiseData);
	allRaiseEmployees.addAll(endOfLadderEmpRaiseData);
	addRaiseEmployees(allRaiseEmployees, raise.getId());

	return endOfLadderEmpRaiseData;
    }

    public static List<RaiseEmployeeData> regenerateRaiseEmployeesForAnnualRaise(Raise raise, Date executionDate, CustomSession... useSession) throws BusinessException {
	// delete the old records in DB
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    deleteRaiseEmployeesByRaiseId(raise.getId(), session);
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

	// re-calculate all employees
	return generateRaiseEmployeesForAnnualRaise(raise, executionDate);
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
	if (raiseEmployeeData.getEmpNewDegreeId() <= raiseEmployeeData.getEmpDegreeId())
	    throw new BusinessException("error_newDegreeOfEmployeeMustBeBiggerThanCurrentDegreeOfEmployee");

	// validate mandatory fields
	// Check that raise has at least one deserved employee
	// In addition raise that employee didn't reach end of ladder
	// TODO check if addition raise for employee is the last raise/promotion
    }

    private static void isStillValidRaiseEmployee(RaiseEmployeeData raiseEmployeeData) throws BusinessException {
	List<EmployeeData> employeeData = getDeservedEmployeesForAnnualRaise(raiseEmployeeData.getRaiseId(), raiseEmployeeData.getRaiseExecutionDate(), raiseEmployeeData.getEmpId());
	if (employeeData.size() == 0)
	    throw new BusinessException("emp_isNotDeserved");
	else {
	    EmployeeData empData = employeeData.get(0);
	    if (empData.getDegreeId().equals(PayrollsService.getEndOfLadderOfRank(empData.getRankId())))
		throw new BusinessException("error_thisEmployeeMustBeExcludedForEndOfLadder");
	}
    }

    /*------------------------------------------Queries------------------------------------------------*/
    /**
     * Wrapper for {@link #searchRaiseEmployees()} to get raise employees by raiseId
     * 
     * @param raiseId
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployeeData> getRaiseEmployeeByRaiseId(long raiseId) throws BusinessException {
	return searchRaiseEmployees(null, null, null, null, FlagsEnum.ALL.getCode(), null, null, null, raiseId, FlagsEnum.ALL.getCode());
    }

    /**
     * Wrapper for {@link #searchRaiseEmployees()} to get end of ladder and excluded for another reason raise employees by raiseId
     * 
     * @param raiseId
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployeeData> getEndOfLadderAndExcludedForAnotherReasonEmployees(long raiseId) throws BusinessException {
	Integer[] deservedFlagValues = new Integer[] { RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode() };
	return searchRaiseEmployees(null, null, null, null, FlagsEnum.ALL.getCode(), null, null, deservedFlagValues, raiseId, FlagsEnum.ALL.getCode());
    }

    /**
     * Wrapper for {@link #searchRaiseEmployees()} to get raise employees by raiseId and empId
     * 
     * @param raiseId
     * @param empId
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployeeData> getRaiseEmployeeByRaiseIdAndEmpId(long raiseId, long empId) throws BusinessException {
	return searchRaiseEmployees(null, null, null, null, FlagsEnum.ALL.getCode(), null, null, null, raiseId, empId);
    }

    /**
     * Wrapper for {@link #searchRaiseEmployees()} to get annual raise deserved employees
     * 
     * @param socialId
     * @param empName
     * @param jobDesc
     * @param physicalUnitFullName
     * @param empNumber
     * @param decisionDate
     * @param decisionNumber
     * @param deservedFlagValues
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployeeData> getAnnualRaiseDeservedEmployees(String socialId, String empName, String jobDesc, String physicalUnitFullName, long empNumber, String decisionDateString, String decisionNumber, Integer[] deservedFlagValues) throws BusinessException {
	return searchRaiseEmployees(socialId, empName, jobDesc, physicalUnitFullName, empNumber, HijriDateService.getHijriDate(decisionDateString), decisionNumber, deservedFlagValues, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    /**
     * search raise employees
     * 
     * @param socialId
     * @param empName
     * @param jobDesc
     * @param physicalUnitFullName
     * @param empNumber
     * @param decisionDate
     * @param decisionNumber
     * @param deservedFlagValues
     * @param raiseId
     * @param empId
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployeeData> searchRaiseEmployees(String socialId, String empName, String jobDesc, String physicalUnitFullName, long empNumber, Date decisionDate, String decisionNumber, Integer[] deservedFlagValues, long raiseId, long empId) throws BusinessException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	try {
	    qParams.put("P_SOCIAL_ID", (socialId == null || socialId.equals("")) ? FlagsEnum.ALL.getCode() + "" : socialId);
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + jobDesc + "%");
	    qParams.put("P_PHYSICAL_UNIT_FULL_NAME", (physicalUnitFullName == null || physicalUnitFullName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + physicalUnitFullName + "%");
	    qParams.put("P_EMP_NUMBER", empNumber);
	    if (decisionDate != null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    }
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.equals("")) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (deservedFlagValues != null && deservedFlagValues.length > 0) {
		qParams.put("P_DESERVED_FLAG_VALUES_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DESERVED_FLAG_VALUES", deservedFlagValues);
	    } else {
		qParams.put("P_DESERVED_FLAG_VALUES_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DESERVED_FLAG_VALUES", new Integer[] { FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_RAISE_ID", raiseId);
	    qParams.put("P_RAISE_EMP_ID", empId);
	    return DataAccess.executeNamedQuery(RaiseEmployeeData.class, QueryNamesEnum.HCM_SEARCH_RAISE_EMPLOYEES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void deleteRaiseEmployeesByRaiseId(long raiseId, CustomSession session) throws BusinessException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	try {
	    qParams.put("P_RAISE_ID", raiseId);
	    DataAccess.executeDeleteNamedQuery(QueryNamesEnum.HCM_DELETE_RAISE_EMPLOYEES_BY_RAISE_ID.getCode(), qParams, null, session);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * get deserved employees
     * 
     * @param raiseId
     * @param executionDate
     * @return array list of employees objects
     * @throws BusinessException
     */
    private static List<EmployeeData> getDeservedEmployeesForAnnualRaise(long raiseId, Date executionDate, Long empId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);
	    qParams.put("P_EXECUTION_DATE", calculateDateBeforeGivenExecutionDateByOneYear(executionDate));
	    qParams.put("P_EMP_ID", (empId == null) ? FlagsEnum.ALL.getCode() + "" : empId);
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_GET_DESERVED_EMPLOYEES.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * get undeserved employees
     * 
     * @param raiseId
     * @param executionDate
     * @return array list of employees objects
     * @throws BusinessException
     */
    private static List<EmployeeData> getUnDeservedEmployeesForAnnualRaise(long raiseId, Date executionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);
	    qParams.put("P_EXECUTION_DATE", calculateDateBeforeGivenExecutionDateByOneYear(executionDate));

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_GET_UNDESERVED_EMPLOYEES.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static Date calculateDateBeforeGivenExecutionDateByOneYear(Date executionDate) {

	Calendar c = Calendar.getInstance();
	c.setTime(HijriDateService.hijriToGregDate(executionDate));
	c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
	return HijriDateService.gregToHijriDate(c.getTime());
    }

    /*------------------------------------------Reports------------------------------------------------*/
    public static byte[] getRaiseEmployeesReportBytes(String decisionNumber, Date decisionDate, int deservedFlag, int type) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    if (type == RaiseTypesEnum.ADDITIONAL.getCode())
		reportName = ReportNamesEnum.ADDITIONAL_RAISE_DESERVED_EMPLOYEES.getCode();
	    else {
		if (deservedFlag == RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode())
		    reportName = ReportNamesEnum.ANNUAL_RAISE_DESERVED_EMPLOYEES.getCode();
		else if (deservedFlag == RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode())
		    reportName = ReportNamesEnum.ANNUAL_RAISE_EXCLUDED_FOR_END_LADDER_EMPLOYEES.getCode();
		else if (deservedFlag == RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode())
		    reportName = ReportNamesEnum.ANNUAL_RAISE_EXCLUDED_FOR_ANOTHER_REASON_EMPLOYEES.getCode();
		else if (deservedFlag == RaiseEmployeesTypesEnum.NOT_DESERVED_EMPLOYEES.getCode())
		    reportName = ReportNamesEnum.ANNUAL_RAISE_NOT_DESERVED_EMPLOYEES.getCode();
		else if (deservedFlag == RaiseEmployeesTypesEnum.ALL_EXCLUDED_EMPLOYEES.getCode())
		    reportName = ReportNamesEnum.ANNUAL_RAISE_ALL_EXCLUDED_EMPLOYEES.getCode();
	    }
	    parameters.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    parameters.put("P_DECISION_NUMBER", decisionNumber);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*************************************** Raise Transaction *****************************************/
    /*----------------------------------------Operations----------------------------------------------*/

    private static void updateRaiseTransaction(RaiseTransactionData raiseTransactionData, CustomSession... useSession) throws BusinessException {
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

    public static RaiseTransactionData constructRaiseTransaction(Raise raise, RaiseEmployeeData raiseEmployeeData, long approvedEmployeeId) throws BusinessException {
	RaiseTransactionData transaction = new RaiseTransactionData();

	transaction.setRaiseCategoryId(raise.getCategoryId());
	transaction.setRaiseType(raise.getType());
	transaction.setRaiseDecisionNumber(raise.getDecisionNumber());
	transaction.setRaiseDecisionDate(raise.getDecisionDate());
	transaction.setRaiseExecutionDate(raise.getExecutionDate());

	transaction.setEmpId(raiseEmployeeData.getEmpId());
	transaction.setDeservedFlag(raiseEmployeeData.getEmpDeservedFlag());
	if (raiseEmployeeData.getEmpDeservedFlag().intValue() == RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode())
	    transaction.setExclusionReason(raiseEmployeeData.getExclusionReason());

	transaction.setEmpNewDegreeId(raiseEmployeeData.getEmpNewDegreeId());
	transaction.setTransEmpDegreeDesc(raiseEmployeeData.getEmpDegreeDesc());
	transaction.setTransEmpJobName(raiseEmployeeData.getEmpJobName());
	transaction.setTransEmpRankDesc(raiseEmployeeData.getEmpRankDesc());
	transaction.setTransEmpJobRankDesc(raiseEmployeeData.getEmpJobRankDesc());
	transaction.setTransEmpUnitFullName(raiseEmployeeData.getEmpPhysicalUnitName());

	transaction.setEmpDecisionApprovedId(approvedEmployeeId);
	transaction.setEmpOriginalDecisionApprovedId(approvedEmployeeId);

	transaction.seteFlag(FlagsEnum.OFF.getCode());
	transaction.setMigFlag(FlagsEnum.OFF.getCode());

	transaction.setEffectFlag(raise.getExecutionDate().after(HijriDateService.getHijriSysDate()) ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode());

	return transaction;
    }

    public static void approveAdditionalRaise(Raise raise, List<RaiseEmployeeData> deserved, long managerId, String loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	List<RaiseTransactionData> raiseTransactionsData = new ArrayList<>();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<BaseEntity> beans = new ArrayList<>();
	    for (RaiseEmployeeData raiseEmployee : deserved) {
		RaiseTransactionData transaction = constructRaiseTransaction(raise, raiseEmployee, managerId);
		validateRaiseTransactionData(transaction);
		transaction.getRaiseTransaction().setSystemUser(raise.getSystemUser());
		raiseTransactionsData.add(transaction);
		doRaiseEffect(transaction, loginEmpId, session);
		beans.add(transaction.getRaiseTransaction());
	    }
	    DataAccess.addMultipleEntities(beans, session);
	    for (RaiseTransactionData raiseTransactionData : raiseTransactionsData) {
		raiseTransactionData.setId(raiseTransactionData.getRaiseTransaction().getId());
	    }

	    raise.setStatus(RaiseStatusEnum.APPROVED.getCode());
	    updateRaise(raise, session);
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (RaiseTransactionData raiseTransactionData : raiseTransactionsData) {
		raiseTransactionData.setId(null);
	    }
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void approveAnnualRaise(Raise raise, long managerId, String loginEmpId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	List<RaiseEmployeeData> allEmployees = getRaiseEmployeeByRaiseId(raise.getId());
	List<RaiseTransactionData> raiseTransactionsData = new ArrayList<>();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<BaseEntity> beans = new ArrayList<>();

	    for (RaiseEmployeeData raiseEmployee : allEmployees) {
		RaiseTransactionData transaction = constructRaiseTransaction(raise, raiseEmployee, managerId);
		transaction.getRaiseTransaction().setSystemUser(loginEmpId);
		beans.add(transaction.getRaiseTransaction());
		raiseTransactionsData.add(transaction);
	    }
	    DataAccess.addMultipleEntities(beans, session);

	    for (RaiseTransactionData raiseTransactionData : raiseTransactionsData) {
		raiseTransactionData.setId(raiseTransactionData.getRaiseTransaction().getId());
	    }

	    raise.setStatus(RaiseStatusEnum.APPROVED.getCode());
	    doAnnualRaiseEffect(raise, loginEmpId, session);

	    updateRaise(raise, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (RaiseTransactionData raiseTransactionData : raiseTransactionsData) {
		raiseTransactionData.setId(null);
	    }
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    private static void doRaiseEffect(RaiseTransactionData transaction, String systemUser, CustomSession session) throws BusinessException {
	if (transaction.getEffectFlag().intValue() == FlagsEnum.ON.getCode()) {

	    EmployeeData emp = EmployeesService.getEmployeeData(transaction.getEmpId());

	    if (transaction.getRaiseType().intValue() == RaiseTypesEnum.ANNUAL.getCode()) {
		emp.getEmployee().setLastAnnualRaiseDate(transaction.getRaiseExecutionDate());
	    }
	    emp.getEmployee().setDegreeId(transaction.getEmpNewDegreeId());
	    emp.getEmployee().setSystemUser(systemUser);
	    EmployeesService.updateEmployee(emp, session);
	}
    }

    private static void doAnnualRaiseEffect(Raise raise, String loginEmpId, CustomSession session) throws BusinessException, DatabaseException {
	if (!raise.getExecutionDate().after(HijriDateService.getHijriSysDate())) {
	    List<Employee> employees = getEmployeesByRaiseId(raise.getId());
	    List<BaseEntity> beans = new ArrayList<>();
	    for (int x = 0; x < employees.size(); x++) {
		employees.get(x).setSystemUser(loginEmpId);
	    }
	    beans.addAll(employees);
	    updateEmployeesDueToAnnualRaiseEffect(beans, raise.getExecutionDate(), raise.getId(), session);

	}
    }

    public static void executeScheduledRaiseTransactions(String systemUser) throws BusinessException {
	List<RaiseTransactionData> list = getNotExecutedRaisesTransactions();
	for (RaiseTransactionData transaction : list) {
	    CustomSession session = DataAccess.getSession();
	    try {
		session.beginTransaction();

		transaction.setEffectFlag(FlagsEnum.ON.getCode());
		updateRaiseTransaction(transaction, session);
		doRaiseEffect(transaction, systemUser, session);

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
     */
    private static void validateRaiseTransactionData(RaiseTransactionData raiseTransactionData) throws BusinessException {
	if (raiseTransactionData.getEmpId() == null)
	    throw new BusinessException("error_general");
	if (raiseTransactionData.getRaiseCategoryId() == null)
	    throw new BusinessException("error_general");
	if (raiseTransactionData.getRaiseType() == null)
	    throw new BusinessException("error_general");
	if (raiseTransactionData.getDeservedFlag() == null)
	    throw new BusinessException("error_general");
	if (raiseTransactionData.getEmpNewDegreeId() == null)
	    throw new BusinessException("error_general");
	if (raiseTransactionData.getRaiseDecisionDate() == null)
	    throw new BusinessException("error_general");
	if (raiseTransactionData.getRaiseDecisionNumber() == null)
	    throw new BusinessException("error_general");
	if (raiseTransactionData.getRaiseExecutionDate() == null)
	    throw new BusinessException("error_general");

	// Check if employees are still valid(Not reached end of ladder, has no promotion/raise after execution date, not service terminated)
    }

    /*------------------------------------------Queries------------------------------------------------*/
    /**
     * get not executed raises transactions
     * 
     * @return list of raises transactions objects
     * @throws BusinessException
     */
    private static List<RaiseTransactionData> getNotExecutedRaisesTransactions() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriSysDateString());
	    return DataAccess.executeNamedQuery(RaiseTransactionData.class, QueryNamesEnum.HCM_GET_NOT_EXECUTED_RAISES_TRANSACTIONS.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void updateEmployeesDueToAnnualRaiseEffect(List<BaseEntity> auditBeans, Date lastAnnualRaiseDate, long raiseId, CustomSession session) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);
	    qParams.put("P_LAST_ANNUAL_RAISE_DATE", HijriDateService.getHijriDateString(lastAnnualRaiseDate));

	    DataAccess.executeUpdateNamedQuery(QueryNamesEnum.HCM_UPDATE_EMPLOYEES_DUE_TO_ANNUAL_RAISE_EFFECT.getCode(), qParams, auditBeans, session);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<Employee> getEmployeesByRaiseId(long raiseId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);

	    return DataAccess.executeNamedQuery(Employee.class, QueryNamesEnum.HCM_GET_DESERVED_EMPLOYEES_BY_RAISE_ID.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
