package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
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

	    addRaise(raise);
	    for (RaiseEmployeeData raiseEmployeeData : raiseEmployeeDataList) {
		raiseEmployeeData.setRaiseId(raise.getId());
		addRaiseEmployee(raiseEmployeeData, null, session);
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
	if (raise.getStatus().intValue() == RaiseStatusEnum.APPROVED.getCode())
	    throw new BusinessException("error_general");

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
	List<Raise> raisesList = validateRaiseData(newRaise.getId() == null ? FlagsEnum.ALL.getCode() : newRaise.getId(), newRaise.getDecisionDate(), newRaise.getDecisionNumber(), newRaise.getType());
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
	return searchRaises(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());

    }

    public static Raise getRaiseById(long id) throws BusinessException {
	List<Raise> result = searchRaises(FlagsEnum.ALL.getCode(), id, null, null, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	return result.isEmpty() ? null : result.get(0);
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

    private static List<Raise> validateRaiseData(long excludedId, Date decisionDate, String decisionNumber, long type) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EXCLUDED_ID", excludedId);
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + decisionNumber + '%');
	    qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    qParams.put("P_TYPE", type);

	    return DataAccess.executeNamedQuery(Raise.class, QueryNamesEnum.HCM_RAISES_VALIDATE_RAISE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*************************************** Raise Employee ********************************************/
    /*----------------------------------------Operations----------------------------------------------*/

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
    private static void addRaiseEmployee(RaiseEmployeeData raiseEmployeeData, List<PayrollSalary> allEndOfLadderDegreesForCategory, CustomSession... useSession) throws BusinessException {
	validateRaiseEmployee(raiseEmployeeData, allEndOfLadderDegreesForCategory);
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
    public static void updateRaiseAndEmployees(Raise raise, List<RaiseEmployeeData> raiseEmployeeDataToAddList, List<RaiseEmployeeData> raiseEmployeeDataToDeleteList, List<PayrollSalary> allEndOfLadderDegreesForCategory, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (allEndOfLadderDegreesForCategory == null) {

	    }
	    updateRaise(raise, session);

	    if ((allEndOfLadderDegreesForCategory == null || allEndOfLadderDegreesForCategory.isEmpty()) && raise.getType() != RaiseTypesEnum.ADDITIONAL.getCode()) {
		allEndOfLadderDegreesForCategory = PayrollsService.getEndOfLadderForAllRanksOfCategory(raise.getCategoryId());
	    }
	    for (RaiseEmployeeData raiseEmployeeDataToAdd : raiseEmployeeDataToAddList) {
		raiseEmployeeDataToAdd.setRaiseId(raise.getId());
		addRaiseEmployee(raiseEmployeeDataToAdd, allEndOfLadderDegreesForCategory, session);
	    }
	    if (raiseEmployeeDataToDeleteList != null && !raiseEmployeeDataToDeleteList.isEmpty())
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
    public static RaiseEmployeeData constructRaiseEmployeeData(EmployeeData empData, Long raiseId, int deservedFlag) {

	RaiseEmployeeData raiseEmployeeData = new RaiseEmployeeData();
	raiseEmployeeData.setRaiseId(raiseId);
	raiseEmployeeData.setEmpId(empData.getEmpId());
	raiseEmployeeData.setEmpDegreeId(empData.getDegreeId());
	raiseEmployeeData.setEmpDegreeDesc(empData.getDegreeDesc());
	raiseEmployeeData.setEmpName(empData.getName());
	raiseEmployeeData.setEmpJobId(empData.getJobId());
	raiseEmployeeData.setEmpJobName(empData.getJobDesc());
	raiseEmployeeData.setEmpRankId(empData.getRankId());
	raiseEmployeeData.setEmpRankDesc(empData.getRankDesc());
	raiseEmployeeData.setEmpDeservedFlag(deservedFlag);
	return raiseEmployeeData;
    }

    public static List<RaiseEmployeeData> saveAllEmployeesAndGetEmployeesEndOfLadder(Raise raise, Date executionDate) {
	List<RaiseEmployeeData> endOfLadderEmpRaiseData = new ArrayList<>();
	try {
	    boolean endOfLadder = false;
	    List<RaiseEmployeeData> deservedEmpRaiseData = new ArrayList<>();
	    List<RaiseEmployeeData> unDeservedEmpRaiseData = new ArrayList<>();
	    List<EmployeeData> unDeservedEmpData = new ArrayList<>();
	    List<PayrollSalary> allEndOfLadderDegreesForCategory = PayrollsService.getEndOfLadderForAllRanksOfCategory(raise.getCategoryId());

	    List<EmployeeData> allDeservedEmpData = getAllDeservedEmployees(raise.getId(), executionDate);
	    unDeservedEmpData = getAllUnDeservedEmployees(raise.getId(), executionDate);
	    for (EmployeeData emp : allDeservedEmpData) {
		endOfLadder = false;
		for (PayrollSalary endOfLadderDegree : allEndOfLadderDegreesForCategory) {
		    if (emp.getDegreeId() == endOfLadderDegree.getDegreeId() && emp.getRankId() == endOfLadderDegree.getRankId()) {
			endOfLadder = true;
			break;
		    }
		}
		if (endOfLadder) {
		    // convert list of EmployeeData to RaiseEmployeeData
		    endOfLadderEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode()));
		} else {
		    deservedEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode()));
		}

	    }
	    for (EmployeeData emp : unDeservedEmpData)
		unDeservedEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseEmployeesTypesEnum.NOT_DESERVED_EMPLOYEES.getCode()));
	    // add deserved , undeserved and end of ladder employees
	    updateRaiseAndEmployees(raise, deservedEmpRaiseData, null, allEndOfLadderDegreesForCategory);
	    updateRaiseAndEmployees(raise, endOfLadderEmpRaiseData, null, allEndOfLadderDegreesForCategory);
	    updateRaiseAndEmployees(raise, unDeservedEmpRaiseData, null, allEndOfLadderDegreesForCategory);
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
	return endOfLadderEmpRaiseData;
    }

    public static List<RaiseEmployeeData> manipulationRaiseEmployees(Raise raise, Date executionDate) {
	try {
	    // delete the old records in DB
	    deleteRaiseEmployees(getRaiseEmployeeByRaiseId(raise.getId()));
	    // re-calculate all employees
	    return saveAllEmployeesAndGetEmployeesEndOfLadder(raise, executionDate);
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
	return new ArrayList<>();
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
    private static void validateRaiseEmployee(RaiseEmployeeData raiseEmployeeData, List<PayrollSalary> allEndOfLadderDegreesForCategory) throws BusinessException {
	Raise raise = getRaiseById(raiseEmployeeData.getRaiseId());

	if (raise.getType() == RaiseTypesEnum.ANNUAL.getCode()) {
	    for (PayrollSalary endOfLadderDegree : allEndOfLadderDegreesForCategory) {
		if (raiseEmployeeData.getEmpNewDegreeId() == endOfLadderDegree.getDegreeId() && raiseEmployeeData.getEmpRankId() == endOfLadderDegree.getRankId()) {
		    throw new BusinessException("error_thisEmployeeMustBeExcludedForEndOfLadder");
		}
	    }
	}
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

    /**
     * get annual raise deserved employees
     * 
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployeeData> getAnnualRaiseDeservedEmployees(String socialId, String empName, String jobDesc, String physicalUnitFullName, int empNumber, String decisionDate, String decisionNumber, Integer deservedFlag) throws BusinessException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	try {
	    qParams.put("P_SOCIAL_ID", (socialId == null || socialId.equals("")) ? FlagsEnum.ALL.getCode() + "" : socialId);
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + jobDesc + "%");
	    qParams.put("P_PHYSICAL_UNIT_FULL_NAME", (physicalUnitFullName == null || physicalUnitFullName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + physicalUnitFullName + "%");
	    qParams.put("P_EMP_NUMBER", empNumber);
	    qParams.put("P_DECISION_DATE", (decisionDate == null || decisionDate.equals("")) ? FlagsEnum.ALL.getCode() + "" : decisionDate);
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || socialId.equals("")) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    qParams.put("P_DESERVED_FLAG", deservedFlag);
	    return DataAccess.executeNamedQuery(RaiseEmployeeData.class, QueryNamesEnum.HCM_RAISES_GET_ANNUAL_RAISE_DESERVED_EMPLOYEES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
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
	transaction.setTransEmpJobRankDesc(JobsService.getJobById(emp.getJobId()).getRankDescription());
	transaction.setTransEmpUnitFullName(emp.getPhysicalUnitFullName());

	transaction.setEmpDecisionApprovedId(managerId);
	transaction.setEmpOriginalDecisionApprovedId(managerId);
	transaction.seteFlag(FlagsEnum.OFF.getCode());
	transaction.setMigFlag(FlagsEnum.OFF.getCode());

	transaction.setEffectFlag(raise.getExecutionDate().after(HijriDateService.getHijriSysDate()) ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode());

	return transaction;

    }

    public static void approveAdditionalRaise(Raise raise, long managerId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	List<RaiseEmployeeData> deserved = getRaiseEmployeeByRaiseId(raise.getId());

	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    for (RaiseEmployeeData raiseEmployee : deserved) {
		RaiseTransactionData transaction = constructRaiseTransaction(managerId, raise, raiseEmployee.getEmpId(), RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode(), raiseEmployee.getEmpNewDegreeId(), null);
		addRaiseTransaction(transaction, session);
		doRaiseEffect(transaction, session);
	    }
	    raise.setStatus(RaiseStatusEnum.APPROVED.getCode());
	    updateRaise(raise, session);
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

    public static void approveAnnualRaise(Raise raise, long managerId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	List<EmployeeData> deserved = getDeservedEmployees(raise.getId(), raise.getExecutionDate());
	List<EmployeeData> excluded = getExcludedEmployeesForEndOfLadder(raise.getId(), raise.getExecutionDate());
	List<RaiseEmployeeData> anotherExcluded = getRaiseEmployeeByRaiseId(raise.getId());

	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    for (EmployeeData raiseEmployee : deserved) {
		RaiseTransactionData transaction = constructRaiseTransaction(managerId, raise, raiseEmployee.getEmpId(), RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode(), raiseEmployee.getDegreeId() + 1, null);
		addRaiseTransaction(transaction, session);
		doRaiseEffect(transaction, session);
	    }
	    for (EmployeeData raiseEmployee : excluded) {
		RaiseTransactionData transaction = constructRaiseTransaction(managerId, raise, raiseEmployee.getEmpId(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode(), raiseEmployee.getDegreeId(), null);
		addRaiseTransaction(transaction, session);

	    }
	    for (RaiseEmployeeData raiseEmployee : anotherExcluded) {
		RaiseTransactionData transaction = constructRaiseTransaction(managerId, raise, raiseEmployee.getEmpId(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode(), raiseEmployee.getEmpNewDegreeId(), raiseEmployee.getExclusionReason());
		addRaiseTransaction(transaction, session);

	    }
	    raise.setStatus(RaiseStatusEnum.APPROVED.getCode());
	    updateRaise(raise, session);
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
		emp.getEmployee().setDegreeId(transaction.getEmpNewDegreeId());
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

    private static List<EmployeeData> getAllDeservedEmployees(long raiseId, Date executionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);
	    qParams.put("P_EXECUTION_DATE", calculateExecutionBeforeYear(executionDate));

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_RAISES_GET_ALL_DESERVED_EMPLOYEES.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<EmployeeData> getAllUnDeservedEmployees(long raiseId, Date executionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);
	    qParams.put("P_EXECUTION_DATE", calculateExecutionBeforeYear(executionDate));

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_RAISES_GET_ALL_UNDESERVED_EMPLOYEES.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static Date calculateExecutionBeforeYear(Date executionDate) {

	Calendar c = Calendar.getInstance();
	c.setTime(HijriDateService.hijriToGregDate(executionDate));
	c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
	return HijriDateService.gregToHijriDate(c.getTime());
    }

    public static List<RaiseTransactionData> getRaiseEmployeesByDeservedFlag(String decisionNumber, Date decisionDate, long deservedFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DESERVED_FLAG", deservedFlag);
	    qParams.put("P_RAISE_DECISION_NUMBER", decisionNumber);
	    qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));

	    return DataAccess.executeNamedQuery(RaiseTransactionData.class, QueryNamesEnum.HCM_RAISE_TRANSACTION_DATA_GET_EMPLOYEES_BY_DESERVED_FLAG.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getExcludedEmployeesForEndOfLadder(long raiseId, Date executionDate) throws BusinessException {
	return searchExcludedEmployeesForEndOfLadder(raiseId, executionDate);
    }

    private static List<EmployeeData> searchExcludedEmployeesForEndOfLadder(long raiseId, Date executionDate) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriDateString(calculateExecutionBeforeYear(executionDate)));

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_RAISES_GET_EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<EmployeeData> getDeservedEmployees(long raiseId, Date executionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriDateString(calculateExecutionBeforeYear(executionDate)));

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_RAISES_GET_DESERVED_EMPLOYEES.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
