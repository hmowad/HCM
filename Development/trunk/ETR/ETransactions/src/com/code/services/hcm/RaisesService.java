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
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.dal.orm.hcm.payroll.PayrollSalary;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.dal.orm.hcm.raises.RaiseEmployee;
import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.dal.orm.hcm.raises.RaiseTransaction;
import com.code.dal.orm.hcm.raises.RaiseTransactionData;
import com.code.dal.orm.hcm.raises.RaiseTransactionLog;
import com.code.dal.orm.log.EmployeeLog;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RaiseEmployeesTypesEnum;
import com.code.enums.RaiseStatusEnum;
import com.code.enums.RaiseTransactionTypesEnum;
import com.code.enums.RaiseTypesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.log.LogService;
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
	    for (RaiseEmployeeData raiseEmployee : raiseEmployeeDataList) {
		raiseEmployee.setRaiseExecutionDate(raise.getExecutionDate());
		raiseEmployee.setRaiseCategoryId(raise.getCategoryId());
	    }
	    addRaiseEmployees(raiseEmployeeDataList, raise.getId(), session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {

	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static List<RaiseEmployeeData> addAnnualRaise(Raise raise, CustomSession... useSession) throws BusinessException {
	List<RaiseEmployeeData> raiseEmployees = new ArrayList<>();
	if (raise.getType() != RaiseTypesEnum.ANNUAL.getCode())
	    throw new BusinessException("error_general");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {

	    if (!isOpenedSession)
		session.beginTransaction();
	    addRaise(raise, session);
	    raiseEmployees = generateRaiseEmployeesForAnnualRaise(raise, session);
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    raise.setId(null);
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
	return raiseEmployees;

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
	validateRaiseMandatoryFields(newRaise);
	validateRaiseBusiness(newRaise);
    }

    private static void validateRaiseMandatoryFields(Raise raise) throws BusinessException {
	if (raise.getDecisionNumber() == null || raise.getDecisionNumber().equals(""))
	    throw new BusinessException("error_decisionNumberIsMandatory");
	if (raise.getDecisionDate() == null)
	    throw new BusinessException("error_decisionDateIsMandatory");
	if (raise.getCategoryId() == null)
	    throw new BusinessException("error_categoryIdIsMandatory");
	if (raise.getExecutionDate() == null)
	    throw new BusinessException("error_raiseDateIsMandatory");
    }

    private static void validateRaiseBusiness(Raise raise) throws BusinessException {
	List<Raise> raisesList = getRaises(raise.getId() == null ? FlagsEnum.ALL.getCode() : raise.getId(), raise.getDecisionDate(), raise.getDecisionNumber(), FlagsEnum.ALL.getCode());
	if (raisesList.size() != 0)
	    throw new BusinessException("error_decisionNumberAndDecisionDateCannotBeRepeated");
	if (raise.getType() == RaiseTypesEnum.ANNUAL.getCode()) {
	    List<Raise> initialRaise = getInitialRaiseForTheSameCategory(raise.getCategoryId());
	    if (!initialRaise.isEmpty() && raise.getId() == null)
		throw new BusinessException("error_cannotSaveTwoInitialAnnualRaiseForTheSameCategory");

	    if (HijriDateService.getHijriSysDate().before(raise.getExecutionDate())) {
		List<Raise> futureRaise = getFutureRaisesForCategory(raise.getId() == null ? FlagsEnum.ALL.getCode() : raise.getId(), raise.getCategoryId(), RaiseTypesEnum.ANNUAL.getCode());
		if (!futureRaise.isEmpty()) {
		    throw new BusinessException("error_cannotSaveTwoFutureAnnualRaisesForTheSameCategory");
		}
	    }
	}
    }

    public static void validateEmployeeFutureRaises(long empId, Date executionDate) throws BusinessException {
	if (HijriDateService.getHijriSysDate().before(executionDate)) {
	    List<RaiseTransactionData> transactions = getFutureRaisesByEmpId(empId);
	    if (!transactions.isEmpty()) {
		throw new BusinessException("error_cannotSaveTwoFutureAdditionalRaisesForTheSameEmployee");
	    }
	}
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

    private static List<Raise> getInitialRaiseForTheSameCategory(long categoryId) throws BusinessException {
	return searchRaises(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null, categoryId, RaiseTypesEnum.ANNUAL.getCode(), RaiseStatusEnum.INITIAL.getCode());
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

    private static List<Raise> getFutureRaisesForCategory(long excludedId, long categoryId, long type) throws BusinessException {
	return searchRaises(excludedId, FlagsEnum.ALL.getCode(), null, null, null, HijriDateService.getHijriSysDate(), null, categoryId, type, FlagsEnum.ALL.getCode());
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
    public static void saveAdditionalRaiseData(Raise raise, List<RaiseEmployeeData> raiseEmployeeDataToAddList, List<RaiseEmployeeData> raiseEmployeeDataToDeleteList, List<RaiseEmployeeData> currRaiseEmployees, CustomSession... useSession) throws BusinessException {
	if (currRaiseEmployees == null || currRaiseEmployees.isEmpty())
	    throw new BusinessException("error_mustAddOneEmployeeAtLeastToSaveAdditionalRaise");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    updateRaise(raise, session);
	    for (RaiseEmployeeData raiseEmployee : raiseEmployeeDataToAddList) {
		raiseEmployee.setRaiseExecutionDate(raise.getExecutionDate());
		raiseEmployee.setRaiseCategoryId(raise.getCategoryId());
	    }
	    addRaiseEmployees(raiseEmployeeDataToAddList, raise.getId(), session);

	    if (raiseEmployeeDataToDeleteList != null && !raiseEmployeeDataToDeleteList.isEmpty()) {
		deleteRaiseEmployees(raiseEmployeeDataToDeleteList, session);
	    }

	    updateRaiseEmployeesList(currRaiseEmployees, session);

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
	for (RaiseEmployeeData raiseEmployee : raiseEmployeesData) {
	    validateRaiseEmployee(raiseEmployee);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<RaiseEmployee> beans = new ArrayList<>();
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
    public static void updateRaiseEmployeesList(List<RaiseEmployeeData> raiseEmployeeDataToUpdateList, CustomSession... useSession) throws BusinessException {
	if (raiseEmployeeDataToUpdateList != null && !raiseEmployeeDataToUpdateList.isEmpty()) {
	    for (RaiseEmployeeData raiseEmployee : raiseEmployeeDataToUpdateList) {
		validateRaiseEmployee(raiseEmployee);
	    }
	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();
		if (raiseEmployeeDataToUpdateList.get(0).getRaiseType() == RaiseTypesEnum.ANNUAL.getCode()) {
		    List<RaiseEmployeeData> deservedEmp = getAnnualRaiseDeservedEmployees(null, null, null, null, FlagsEnum.ALL.getCode(), raiseEmployeeDataToUpdateList.get(0).getRaiseDecisionDateString(), raiseEmployeeDataToUpdateList.get(0).getRaiseDecisionNumber(), new Integer[] { RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode() });
		    if (deservedEmp.size() == raiseEmployeeDataToUpdateList.size())
			throw new BusinessException("error_annualRaiseMustHaveAtLeastOneDeservedEmployee");
		}
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
    public static RaiseEmployeeData constructRaiseEmployeeData(EmployeeData empData, Long raiseId, int raiseType, int deservedFlag, Date raiseExecutionDate) throws BusinessException {
	RaiseEmployeeData raiseEmployeeData = new RaiseEmployeeData();
	raiseEmployeeData.setRaiseId(raiseId);
	raiseEmployeeData.setRaiseCategoryId(empData.getCategoryId());
	raiseEmployeeData.setRaiseExecutionDate(raiseExecutionDate);
	raiseEmployeeData.setEmpId(empData.getEmpId());
	raiseEmployeeData.setEmpDegreeId(empData.getDegreeId());
	raiseEmployeeData.setEmpDegreeDesc(empData.getDegreeDesc());
	raiseEmployeeData.setRaiseType(raiseType);
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

    public static List<RaiseEmployeeData> generateRaiseEmployeesForAnnualRaise(Raise raise, CustomSession session) throws BusinessException {
	List<RaiseEmployeeData> endOfLadderEmpRaiseData = new ArrayList<>();
	List<RaiseEmployeeData> allRaiseEmployees = new ArrayList<>();
	List<RaiseEmployeeData> deservedEmpRaiseData = new ArrayList<>();
	List<RaiseEmployeeData> unDeservedEmpRaiseData = new ArrayList<>();
	List<EmployeeData> unDeservedEmpData = new ArrayList<>();

	Map<Long, Long> rankDegressHashMap = getEndOfLadderDegreesMap(raise.getCategoryId());

	List<EmployeeData> allDeservedEmpData = getDeservedEmployees(raise.getExecutionDate(), FlagsEnum.ALL.getCode(), raise.getType(), raise.getCategoryId());
	unDeservedEmpData = getUnDeservedEmployeesForAnnualRaise(raise.getExecutionDate(), raise.getCategoryId());

	for (EmployeeData emp : allDeservedEmpData) {
	    if (emp.getDegreeId().equals(rankDegressHashMap.get(emp.getRankId()))) {
		endOfLadderEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseTypesEnum.ANNUAL.getCode(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode(), raise.getExecutionDate()));
	    } else {
		deservedEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseTypesEnum.ANNUAL.getCode(), RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode(), raise.getExecutionDate()));
	    }
	}
	if (deservedEmpRaiseData.isEmpty()) {
	    throw new BusinessException("error_annualRaiseMustHaveAtLeastOneDeservedEmployee");
	}

	for (EmployeeData emp : unDeservedEmpData) {
	    unDeservedEmpRaiseData.add(constructRaiseEmployeeData(emp, raise.getId(), RaiseTypesEnum.ANNUAL.getCode(), RaiseEmployeesTypesEnum.NOT_DESERVED_EMPLOYEES.getCode(), raise.getExecutionDate()));
	}

	allRaiseEmployees.addAll(deservedEmpRaiseData);
	allRaiseEmployees.addAll(unDeservedEmpRaiseData);
	allRaiseEmployees.addAll(endOfLadderEmpRaiseData);
	addRaiseEmployees(allRaiseEmployees, raise.getId(), session);

	return endOfLadderEmpRaiseData;
    }

    public static List<RaiseEmployeeData> regenerateRaiseEmployeesForAnnualRaise(Raise raise, CustomSession... useSession) throws BusinessException {
	List<RaiseEmployeeData> raiseEmployees = new ArrayList<>();
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    deleteRaiseEmployeesByRaiseId(raise.getId(), session);
	    raiseEmployees = generateRaiseEmployeesForAnnualRaise(raise, session);
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

	return raiseEmployees;
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
	validateRaiseEmployeeMandatoryFields(raiseEmployeeData);
	validateRaiseEmployeeBusiness(raiseEmployeeData);
    }

    private static void validateRaiseEmployeeMandatoryFields(RaiseEmployeeData raiseEmployeeData) throws BusinessException {
	if ((raiseEmployeeData.getExclusionReason() == null || raiseEmployeeData.getExclusionReason().equals("")) && raiseEmployeeData.getEmpDeservedFlag() == RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode())
	    throw new BusinessException("error_exclusionReasonIsMandatory");
	if (raiseEmployeeData.getEmpDeservedFlag() == null)
	    throw new BusinessException("error_deservedFlagIsMandatory");
    }

    private static void validateRaiseEmployeeBusiness(RaiseEmployeeData raiseEmployeeData) throws BusinessException {
	if (raiseEmployeeData.getRaiseType() == RaiseTypesEnum.ADDITIONAL.getCode()) {
	    List<EmployeeData> employeeData = getDeservedEmployees(raiseEmployeeData.getRaiseExecutionDate(), raiseEmployeeData.getEmpId(), raiseEmployeeData.getRaiseType(), raiseEmployeeData.getRaiseCategoryId());
	    if (employeeData == null || employeeData.isEmpty())
		throw new BusinessException("error_employeeIsUndeservedForAdditionalRaise", new Object[] { raiseEmployeeData.getEmpName() });
	    Long getEndOfLadderOfRank = PayrollsService.getEndOfLadderOfRank(raiseEmployeeData.getEmpRankId());
	    if (raiseEmployeeData.getEmpNewDegreeId() > (getEndOfLadderOfRank == null ? raiseEmployeeData.getEmpNewDegreeId() : getEndOfLadderOfRank))
		throw new BusinessException("error_employeeNewDegreeIsHigherThanEndOfLadderDegreeOfRank", new Object[] { raiseEmployeeData.getEmpName() });

	    validateEmployeeFutureRaises(raiseEmployeeData.getEmpId(), raiseEmployeeData.getRaiseExecutionDate());
	}
	if (raiseEmployeeData.getEmpNewDegreeId() <= raiseEmployeeData.getEmpDegreeId() && raiseEmployeeData.getEmpDeservedFlag() == RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode())
	    throw new BusinessException("error_newDegreeOfEmployeeMustBeBiggerThanCurrentDegreeOfEmployee", new Object[] { raiseEmployeeData.getEmpName() });

    }

    public static void validateAddedEmployees(RaiseEmployeeData newRaiseEmployee, List<RaiseEmployeeData> raiseEmployees) throws BusinessException {
	for (RaiseEmployeeData raiseEmployee : raiseEmployees) {
	    if (raiseEmployee.getEmpId().equals(newRaiseEmployee.getEmpId()))
		throw new BusinessException("error_empDuplicateSameProcess");
	}
	if (newRaiseEmployee.getRaiseType() == RaiseTypesEnum.ADDITIONAL.getCode()) {
	    if (newRaiseEmployee.getEmpDegreeId() == PayrollsService.getEndOfLadderOfRank(newRaiseEmployee.getEmpRankId()))
		throw new BusinessException("error_employeeReachedTheEndOfLadderDegree");
	}
    }

    public static void isStillValidAdditionalRaiseEmployee(List<RaiseEmployeeData> raiseEmployees) throws BusinessException {
	if (raiseEmployees == null || raiseEmployees.isEmpty())
	    throw new BusinessException("error_mustAddOneEmployeeAtLeastToSaveAdditionalRaise");

	Map<Long, Long> rankDegreesHashMap = getEndOfLadderDegreesMap(raiseEmployees.get(0).getRaiseCategoryId());
	List<EmployeeData> allDeservedEmployees = getDeservedEmployees(raiseEmployees.get(0).getRaiseExecutionDate(), FlagsEnum.ALL.getCode(), raiseEmployees.get(0).getRaiseType(), raiseEmployees.get(0).getRaiseCategoryId());

	for (RaiseEmployeeData raiseEmp : raiseEmployees) {
	    if (raiseEmp.getEmpDegreeId().equals(rankDegreesHashMap.get(raiseEmp.getEmpRankId())))
		throw new BusinessException("error_deservedEmployeesHaveChanged");
	}
	for (RaiseEmployeeData raiseEmp : raiseEmployees) {
	    boolean found = false;
	    for (EmployeeData emp : allDeservedEmployees) {
		if (raiseEmp.getEmpId().equals(emp.getEmpId())) {
		    found = true;
		    break;
		}
	    }
	    if (!found)
		throw new BusinessException("error_deservedEmployeesHaveChanged");
	}
    }

    private static Map<Long, Long> getEndOfLadderDegreesMap(Long categoryId) throws BusinessException {
	Map<Long, Long> rankDegreesHashMap = new HashMap<>();
	List<PayrollSalary> allEndOfLadderDegreesForCategory = PayrollsService.getEndOfLadderForAllRanksOfCategory(categoryId);
	for (PayrollSalary endOfLadderDegree : allEndOfLadderDegreesForCategory) {
	    rankDegreesHashMap.put(endOfLadderDegree.getRankId(), endOfLadderDegree.getDegreeId());
	}
	return rankDegreesHashMap;
    }

    public static void isStillValidAnnualRaiseEmployee(Raise raise) throws BusinessException {
	try {
	    List<EmployeeData> allDeservedEmployees = getDeservedEmployees(raise.getExecutionDate(), FlagsEnum.ALL.getCode(), raise.getType(), raise.getCategoryId());
	    List<EmployeeData> currDeservedEmpData = new ArrayList<>();
	    List<RaiseEmployeeData> excludedforEndOfLadder = new ArrayList<>();
	    List<RaiseEmployeeData> prevDeservedEmpData = getAnnualRaiseDeservedEmployees(null, null, null, null, FlagsEnum.ALL.getCode(), raise.getDecisionDateString(), raise.getDecisionNumber(), new Integer[] { RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode(), RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode() });
	    Map<Long, Long> rankDegreesHashMap = getEndOfLadderDegreesMap(raise.getCategoryId());

	    for (RaiseEmployeeData raiseEmployee : prevDeservedEmpData) {
		if (raiseEmployee.getEmpDeservedFlag() == RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER.getCode())
		    excludedforEndOfLadder.add(raiseEmployee);
	    }
	    int excludedForEndOfLadderCount = 0;
	    for (EmployeeData emp : allDeservedEmployees) {
		boolean found = false;
		if (!emp.getDegreeId().equals(rankDegreesHashMap.get(emp.getRankId()))) {
		    currDeservedEmpData.add(emp);
		} else {
		    excludedForEndOfLadderCount++;
		    for (RaiseEmployeeData raiseEmployee : excludedforEndOfLadder) {
			if (raiseEmployee.getEmpId().equals(emp.getEmpId())) {
			    found = true;
			    break;
			}
		    }
		    if (!found) {
			raise.setDirtyFlag(true);
			throw new BusinessException("error_deservedEmployeesHaveChanged");
		    }
		}
	    }
	    if (excludedForEndOfLadderCount != excludedforEndOfLadder.size()) {
		raise.setDirtyFlag(true);
		throw new BusinessException("error_deservedEmployeesHaveChanged");
	    }
	    if (currDeservedEmpData.size() != (prevDeservedEmpData.size() - excludedForEndOfLadderCount)) {
		raise.setDirtyFlag(true);
		throw new BusinessException("error_deservedEmployeesHaveChanged");
	    }
	    for (EmployeeData emp : currDeservedEmpData) {
		boolean found = false;
		for (RaiseEmployeeData raiseEmp : prevDeservedEmpData) {
		    if (raiseEmp.getEmpId().equals(emp.getEmpId())) {
			found = true;
			break;
		    }
		}
		if (!found) {
		    raise.setDirtyFlag(true);
		    throw new BusinessException("error_deservedEmployeesHaveChanged");
		}
	    }

	    // check if annual raise still has deserved employees
	    boolean atLeastOneDeserved = false;
	    for (RaiseEmployeeData raiseEmp : prevDeservedEmpData) {
		if (raiseEmp.getEmpDeservedFlag().equals(RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode())) {
		    atLeastOneDeserved = true;
		    break;
		}
	    }
	    if (!atLeastOneDeserved) {
		throw new BusinessException("error_annualRaiseMustHaveAtLeastOneDeservedEmployee");
	    }

	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
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
    private static List<EmployeeData> getDeservedEmployees(Date executionDate, long empId, long raiseType, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXECUTION_DATE", raiseType == RaiseTypesEnum.ADDITIONAL.getCode() ? HijriDateService.getHijriDateString(executionDate) : HijriDateService.getHijriDateString(calculateDateBeforeGivenExecutionDateByOneYear(executionDate)));
	    qParams.put("P_ACTUAL_EXECUTION_DATE", HijriDateService.getHijriDateString(executionDate));
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_CATEGORY_ID", categoryId);
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
    private static List<EmployeeData> getUnDeservedEmployeesForAnnualRaise(Date executionDate, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriDateString(calculateDateBeforeGivenExecutionDateByOneYear(executionDate)));
	    qParams.put("P_ACTUAL_EXECUTION_DATE", HijriDateService.getHijriDateString(executionDate));
	    qParams.put("P_CATEGORY_ID", categoryId);
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
	validateRaiseTransactionMandatoryFields(raiseTransactionData);

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
	transaction.setTransEmpDegreeId(raiseEmployeeData.getEmpDegreeId());
	transaction.setEmpDecisionApprovedId(approvedEmployeeId);
	transaction.setEmpOriginalDecisionApprovedId(approvedEmployeeId);
	transaction.seteFlag(FlagsEnum.OFF.getCode());
	transaction.setMigFlag(FlagsEnum.OFF.getCode());
	transaction.setStatus(RaiseTransactionTypesEnum.VALID.getCode());
	transaction.setEffectFlag(raise.getExecutionDate().after(HijriDateService.getHijriSysDate()) ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode());
	return transaction;
    }

    private static RaiseTransactionLog constructRaiseTransactionLogDecision(RaiseEmployeeData raiseEmployeeData) {
	RaiseTransactionLog transactionLog = new RaiseTransactionLog();
	transactionLog.setNewDegreeId(raiseEmployeeData.getEmpNewDegreeId());
	transactionLog.setTransEmpDegreeDesc(raiseEmployeeData.getEmpDegreeDesc());
	transactionLog.setTransEmpRankDesc(raiseEmployeeData.getEmpRankDesc());
	transactionLog.setTransEmpDegreeId(raiseEmployeeData.getEmpDegreeId());
	return transactionLog;
    }

    public static RaiseTransactionLog constructRaiseTransactionLogModification(Long raiseTransactionId, Date basedOnDecisionDate, Long oldDegree, Long newDegree, String basedOnDecisionNumber, String rank, String oldDegreeDesc) {
	RaiseTransactionLog transactionLog = new RaiseTransactionLog();
	transactionLog.setRaiseTransactionId(raiseTransactionId);
	transactionLog.setBasedOnDecisionDate(basedOnDecisionDate);
	transactionLog.setBasedOnDecisionNumber(basedOnDecisionNumber);
	transactionLog.setNewDegreeId(newDegree);
	transactionLog.setTransEmpDegreeDesc(oldDegreeDesc);
	transactionLog.setTransEmpRankDesc(rank);
	transactionLog.setTransEmpDegreeId(oldDegree);

	return transactionLog;
    }

    public static List<RaiseEmployeeData> approveAdditionalRaise(Raise raise, long managerId, String loginEmpId, CustomSession... useSession) throws BusinessException {
	List<RaiseEmployeeData> deserved = getRaiseEmployeeByRaiseId(raise.getId());
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	List<RaiseTransactionLog> raiseTransactionsLog = new ArrayList<>();
	List<RaiseTransaction> raiseTransactions = new ArrayList<>();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    isStillValidAdditionalRaiseEmployee(deserved);

	    for (RaiseEmployeeData raiseEmployee : deserved) {
		RaiseTransactionData transaction = constructRaiseTransaction(raise, raiseEmployee, managerId);
		validateRaiseTransactionMandatoryFields(transaction);
		RaiseTransactionLog transactionLog = constructRaiseTransactionLogDecision(raiseEmployee);
		transaction.getRaiseTransaction().setSystemUser(raise.getSystemUser());
		transactionLog.setSystemUser(raise.getSystemUser());
		raiseTransactionsLog.add(transactionLog);
		doRaiseEffect(transaction, loginEmpId, session);
		raiseTransactions.add(transaction.getRaiseTransaction());
	    }
	    DataAccess.addMultipleEntities(raiseTransactions, session);
	    raiseTransactions.clear();
	    for (int i = 0; i < raiseTransactions.size(); i++) {
		raiseTransactions.get(i).setId(raiseTransactions.get(i).getId());
		raiseTransactionsLog.get(i).setRaiseTransactionId(raiseTransactions.get(i).getId());
	    }
	    DataAccess.addMultipleEntities(raiseTransactionsLog, session);

	    raise.setStatus(RaiseStatusEnum.APPROVED.getCode());
	    updateRaise(raise, session);
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (int i = 0; i < raiseTransactions.size(); i++) {
		raiseTransactions.get(i).setId(null);
		raiseTransactionsLog.get(i).setId(null);
	    }
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
	return deserved;
    }

    public static void approveAnnualRaise(Raise raise, long managerId, String loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	List<RaiseEmployeeData> allEmployees = getRaiseEmployeeByRaiseId(raise.getId());
	List<RaiseTransactionLog> raiseTransactionsLog = new ArrayList<>();
	List<RaiseTransaction> raiseTransactions = new ArrayList<>();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    isStillValidAnnualRaiseEmployee(raise);

	    for (RaiseEmployeeData raiseEmployee : allEmployees) {
		RaiseTransactionData transaction = constructRaiseTransaction(raise, raiseEmployee, managerId);
		validateRaiseTransactionMandatoryFields(transaction);
		transaction.getRaiseTransaction().setSystemUser(loginEmpId);
		RaiseTransactionLog transactionLog = constructRaiseTransactionLogDecision(raiseEmployee);
		transactionLog.setSystemUser(raise.getSystemUser());
		raiseTransactions.add(transaction.getRaiseTransaction());
		raiseTransactionsLog.add(transactionLog);
	    }
	    DataAccess.addMultipleEntities(raiseTransactions, session);

	    for (int i = 0; i < raiseTransactions.size(); i++) {
		raiseTransactions.get(i).setId(raiseTransactions.get(i).getId());
		raiseTransactionsLog.get(i).setRaiseTransactionId(raiseTransactions.get(i).getId());
	    }
	    DataAccess.addMultipleEntities(raiseTransactionsLog, session);

	    raise.setStatus(RaiseStatusEnum.APPROVED.getCode());
	    doAnnualRaiseEffect(raise, loginEmpId, session);

	    updateRaise(raise, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (RaiseTransaction raiseTransaction : raiseTransactions) {
		raiseTransaction.setId(null);
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
	    EmployeeLog logEmp = new EmployeeLog.Builder()
		    .setDegreeId(transaction.getEmpNewDegreeId())
		    .constructCommonFields(emp.getEmpId(), transaction.getRaiseDecisionNumber(), transaction.getRaiseDecisionDate(), transaction.getRaiseExecutionDate())
		    .build();
	    LogService.logEmployeeData(logEmp, session);
	}
    }

    private static void doAnnualRaiseEffect(Raise raise, String loginEmpId, CustomSession session) throws BusinessException, DatabaseException {
	if (!raise.getExecutionDate().after(HijriDateService.getHijriSysDate())) {
	    List<EmployeeData> employees = getEmployeesByRaiseId(raise.getId());
	    List<BaseEntity> beans = new ArrayList<>();
	    for (int x = 0; x < employees.size(); x++) {
		employees.get(x).getEmployee().setSystemUser(loginEmpId);
	    }
	    beans.addAll(employees);
	    for (int i = 0; i < beans.size(); i++) {
		beans.set(i, ((EmployeeData) beans.get(i)).getEmployee());
	    }
	    updateEmployeesDueToAnnualRaiseEffect(beans, raise.getExecutionDate(), raise.getId(), session);
	    for (EmployeeData employee : employees) {
		employee.setDegreeId(employee.getDegreeId() + 1);
		EmployeeLog logEmp = new EmployeeLog.Builder()
			.setDegreeId(employee.getDegreeId() + 1)
			.constructCommonFields(employee.getEmpId(), raise.getDecisionNumber(), raise.getDecisionDate(), raise.getExecutionDate())
			.build();
		LogService.logEmployeeData(logEmp, session);
	    }
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

    public static void getEmployeesDegreesInGivenDate(List<EmployeeData> employees, Date date) throws BusinessException {
	if (employees != null && !employees.isEmpty()) {
	    List<RaiseTransaction> transactions = new ArrayList<>();
	    List<Long> empIds = new ArrayList<>();
	    for (int i = 0; i < employees.size(); i++) {
		empIds.add(employees.get(i).getEmpId());
		if (i == employees.size() - 1 || (i % 1000 == 0 && i != 0)) {
		    transactions.addAll(getFirstRaiseTransactionAtGivenDate(empIds.toArray(), date));
		    empIds.clear();
		}
	    }
	    Map<Long, Integer> employeesMap = new HashMap<>();
	    for (int i = 0; i < employees.size(); i++) {
		employeesMap.put(employees.get(i).getEmpId(), i);
	    }
	    for (RaiseTransaction transaction : transactions) {
		employees.get(employeesMap.get(transaction.getEmpId())).setDegreeId(transaction.getTransEmpDegreeId());
	    }
	}
    }

    public static void raisesModificationsAfterPromotions(List<EmployeeData> employees, Date promotionDecisionDate, String promotionDecisionNumber, String loginEmpId, CustomSession session) throws BusinessException {
	try {
	    List<Degree> degrees = PayrollsService.getAllDegrees();
	    if (employees != null && !employees.isEmpty()) {
		List<RaiseTransactionLog> addedTransactionLogList = new ArrayList<>();
		List<RaiseTransaction> updatedTransactionList = new ArrayList<>();
		List<RaiseTransaction> transactions = new ArrayList<>();
		List<Long> empIds = new ArrayList<>();
		for (int i = 0; i < employees.size(); i++) {
		    empIds.add(employees.get(i).getEmpId());
		    if (i == employees.size() - 1 || (i % 1000 == 0 && i != 0)) {
			transactions.addAll(getAllRaisesForEmployeesAfterGivenDate(empIds.toArray(), promotionDecisionDate));
			empIds.clear();
		    }
		}
		if (transactions != null && transactions.size() != 0) {
		    Map<Long, Long> rankDegreesHashMap = getEndOfLadderDegreesMap(employees.get(0).getCategoryId());
		    Map<Long, EmployeeData> employeesMap = new HashMap<>();
		    for (int i = 0; i < employees.size(); i++) {
			employeesMap.put(employees.get(i).getEmpId(), employees.get(i));
		    }
		    long lastEmpId = transactions.get(0).getEmpId();
		    long newDegree = employeesMap.get(transactions.get(0).getEmpId()).getDegreeId();
		    long oldDegree = employeesMap.get(transactions.get(0).getEmpId()).getDegreeId();
		    boolean invalid = false;
		    for (int i = 0; i <= transactions.size(); i++) {

			if (i >= transactions.size() || !transactions.get(i).getEmpId().equals(lastEmpId)) {
			    if (transactions.get(i - 1).getEffectFlag().equals(FlagsEnum.ON.getCode()))
				employeesMap.get(lastEmpId).setDegreeId(newDegree);
			    if (i >= transactions.size())
				break;
			    invalid = false;
			    newDegree = employeesMap.get(transactions.get(i).getEmpId()).getDegreeId();
			    oldDegree = employeesMap.get(transactions.get(i).getEmpId()).getDegreeId();
			}
			EmployeeData employee = employeesMap.get(transactions.get(i).getEmpId());
			if (invalid || (rankDegreesHashMap.get(employee.getRankId()) == null ? degrees.get(degrees.size() - 1).getId() : rankDegreesHashMap.get(employee.getRankId())) == newDegree) {
			    RaiseTransactionLog transactionLog = constructRaiseTransactionLogModification(transactions.get(i).getId(), promotionDecisionDate, transactions.get(i).getTransEmpDegreeId(), transactions.get(i).getNewDegreeId(), promotionDecisionNumber, transactions.get(i).getTransEmpRankDesc(), transactions.get(0).getTransEmpDegreeDesc());
			    transactionLog.setSystemUser(loginEmpId);
			    addedTransactionLogList.add(transactionLog);
			    transactions.get(i).setStatus(RaiseTransactionTypesEnum.INVALID.getCode());
			    transactions.get(i).setSystemUser(loginEmpId);
			    updatedTransactionList.add(transactions.get(i));
			    EmployeeLog logEmp = new EmployeeLog.Builder()
				    .setDegreeId(rankDegreesHashMap.get(employee.getRankId()) == null ? degrees.get(degrees.size() - 1).getId() : rankDegreesHashMap.get(employee.getRankId()))
				    .constructCommonFields(employee.getEmpId(), transactions.get(i).getDecisionNumber(), transactions.get(i).getDecisionDate(), transactions.get(i).getExecutionDate())
				    .build();
			    LogService.logEmployeeData(logEmp, session);
			    invalid = true;
			} else {
			    newDegree += transactions.get(i).getNewDegreeId() - transactions.get(i).getTransEmpDegreeId();
			    if (newDegree >= (rankDegreesHashMap.get(employee.getRankId()) == null ? degrees.get(degrees.size() - 1).getId() : rankDegreesHashMap.get(employee.getRankId()))) {
				newDegree = rankDegreesHashMap.get(employee.getRankId()) == null ? degrees.get(degrees.size() - 1).getId() : rankDegreesHashMap.get(employee.getRankId());
				invalid = true;
			    }
			    transactions.get(i).setNewDegreeId(newDegree);
			    transactions.get(i).setBasedOnDecisionDate(promotionDecisionDate);
			    transactions.get(i).setBasedOnDecisionNumber(promotionDecisionNumber);
			    transactions.get(i).setTransEmpDegreeId(oldDegree);
			    transactions.get(i).setTransEmpDegreeDesc(degrees.get((int) oldDegree - 1).getDescription());
			    transactions.get(i).setTransEmpRankDesc(employee.getRankDesc());
			    transactions.get(i).setStatus(RaiseTransactionTypesEnum.VALID.getCode());
			    transactions.get(i).setSystemUser(loginEmpId);
			    updatedTransactionList.add(transactions.get(i));
			    RaiseTransactionLog transactionLog = constructRaiseTransactionLogModification(transactions.get(i).getId(), promotionDecisionDate, oldDegree, newDegree, promotionDecisionNumber, employee.getRankDesc(), degrees.get((int) oldDegree - 1).getDescription());
			    transactionLog.setSystemUser(loginEmpId);
			    addedTransactionLogList.add(transactionLog);
			    EmployeeLog logEmp = new EmployeeLog.Builder()
				    .setDegreeId(newDegree)
				    .constructCommonFields(employee.getEmpId(), transactions.get(i).getDecisionNumber(), transactions.get(i).getDecisionDate(), transactions.get(i).getExecutionDate())
				    .build();
			    LogService.logEmployeeData(logEmp, session);
			    oldDegree = newDegree;
			}
			lastEmpId = transactions.get(i).getEmpId();
		    }
		    DataAccess.addMultipleEntities(addedTransactionLogList, session);
		    DataAccess.updateMultipleEntities(updatedTransactionList, session);
		}
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
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
    private static void validateRaiseTransactionMandatoryFields(RaiseTransactionData raiseTransactionData) throws BusinessException {
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

    private static void updateEmployeesDueToAnnualRaiseEffect(List<BaseEntity> auditBeans, Date lastAnnualRaiseDate, long raiseId, CustomSession session) throws BusinessException {
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

    private static List<EmployeeData> getEmployeesByRaiseId(long raiseId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RAISE_ID", raiseId);

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_GET_DESERVED_EMPLOYEES_BY_RAISE_ID.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<RaiseTransactionData> getFutureRaisesByEmpId(long empId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriSysDateString());

	    return DataAccess.executeNamedQuery(RaiseTransactionData.class, QueryNamesEnum.HCM_GET_FUTURE_RAISES_BY_EMP_ID.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<RaiseTransaction> getFirstRaiseTransactionAtGivenDate(Object[] employeesList, Date promotionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_IDS", employeesList);
	    qParams.put("P_PROMOTION_DATE", HijriDateService.getHijriDateString(promotionDate));
	    return DataAccess.executeNamedQuery(RaiseTransaction.class, QueryNamesEnum.HCM_GET_FIRST_RAISE_TRRANSACTION_AFTER_GIVEN_DATE.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<RaiseTransaction> getAllRaisesForEmployeesAfterGivenDate(Object[] employeesList, Date promotionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_IDS", employeesList);
	    qParams.put("P_PROMOTION_DATE", HijriDateService.getHijriDateString(promotionDate));
	    return DataAccess.executeNamedQuery(RaiseTransaction.class, QueryNamesEnum.HCM_GET_ALL_RAISES_FOR_EMPLOYEES_AFTER_GIVEN_DATE.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
