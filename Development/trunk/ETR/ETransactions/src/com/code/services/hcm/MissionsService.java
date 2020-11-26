package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.buswfcoop.EmployeesTransactionsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.HijriDateService;

/**
 * 
 * This class consists of static methods to operate on and handle mission services for officers, soldiers, and civilians.
 * 
 * It consists exclusively of static methods that operate on or return promotions services, "Wrappers",which return a new collection backed by a specified collection, and a few other odds and ends.
 * <p>
 * The methods of this class all throw a <tt>BusinessException</tt> if any problem occurred in sessionScope
 * <p>
 * 
 */

public class MissionsService extends BaseService {

    /**
     * Private constructor to prevent instantiation
     */

    private MissionsService() {
    }

    /*-----------------------------------------------------Mission Transactions operations---------------------------------------------*/
    /**
     * Add mission transaction and mission's details to DB using 2 Private methods {@link #addMission(MissionData, String, CustomSession...)} and {@link #addMissionDetails(long, List, CustomSession...)} and after integration with workflow tables by getting all data from these workflow tables.
     * 
     * @param missionData
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetailsData
     *            List of {@link MissionDetailData} Objects which will be added to DB
     * @param subject
     *            The wfProcess Name using to generate ETRCor
     * @param useSession
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * @see ETRCorrespondence
     */

    public static void addMissionAndDetails(MissionData missionData, List<MissionDetailData> missionDetailsData, String subject, CustomSession... useSession) throws BusinessException {
	addMission(missionData, subject, useSession);
	addMissionDetails(missionData.getId(), missionDetailsData, useSession);
    }

    /**
     * Add new MissionData to DB
     * 
     * @param missionData
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param subject
     *            The wfProcess Name using to generate ETRCor
     * @param useSession
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see ETRCorrespondence
     */

    private static void addMission(MissionData missionData, String subject, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    missionData.setEflag(FlagsEnum.ON.getCode());
	    missionData.setMigFlag(FlagsEnum.OFF.getCode());

	    String[] etrCorInfo = ETRCorrespondence.doETRCorOut(subject, session);
	    missionData.setDecisionNumber(etrCorInfo[0]);
	    missionData.setDecisionDateString(etrCorInfo[1]);

	    DataAccess.addEntity(missionData.getMission(), session);
	    missionData.setId(missionData.getMission().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    missionData.setId(null);
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Wrapper for {@link #searchMissions(long, long, String, int, String, String, Date, Date, int, long)}, it Get all missions that match one, some, or all of the following parameters:
     * 
     * @param empId
     *            Employee Id which has a mission
     * @param decisionNumber
     *            Mission decision Number
     * @param locationFlag
     *            Mission location flag which is external or internal
     * @param destination
     *            Mission Destination
     * @param purpose
     *            Mission purpose
     * @param fromDate
     *            Mission Start Date
     * @param toDate
     *            Mission End Date
     * @param categoryMode
     *            Category mode (officers missions, soldiers missions, or civilians missions)
     * @param regionId
     *            Mission Region Id
     * @return List of {@link MissionData} List of mission transaction which have all of this terms
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     * @see #searchMissions(long, long, String, int, String, String, Date, Date, int, long)
     */

    public static List<MissionDetailData> getMissionTransactionsForEmployeeAfterDecisionDate(Long empId, Date decisiondate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    if (decisiondate == null) {
		qParams.put("P_MISSION_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_MISSION_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_MISSION_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_MISSION_DECISION_DATE", HijriDateService.getHijriDateString(decisiondate));
	    }
	    return DataAccess.executeNamedQuery(MissionDetailData.class, QueryNamesEnum.HCM_GET_MISSION_DETAIL_DATA_AFTER_DECISION_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<MissionData> getMissions(long empId, String decisionNumber, int locationFlag, String destination, String purpose, Date fromDate, Date toDate, int categoryMode, long regionId) throws BusinessException {
	return searchMissions(FlagsEnum.ALL.getCode(), empId, decisionNumber, locationFlag, destination, purpose, fromDate, toDate, categoryMode, regionId);
    }

    /**
     * Wrapper for {@link #searchMissions(long, long, String, int, String, String, Date, Date, int, long)} to Get certain mission transaction using its Id
     * 
     * @param missionId
     *            Mission transaction Id
     * @return {@link MissionData} transaction associated with this id
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #searchMissions(long, long, String, int, String, String, Date, Date, int, long)
     */

    public static MissionData getMissionsById(long missionId) throws BusinessException {
	List<MissionData> missions = searchMissions(missionId, FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), null, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	if (missions.size() != 0)
	    return missions.get(0);

	return null;
    }

    /**
     * Wrapper for {@link #searchMissions(long, long, String, int, String, String, Date, Date, int, long)} to Get mission transaction using decision number, decision date, categoryId, and regionId
     * 
     * @param decisionNumber
     *            Mission decision number
     * @param decisionDate
     *            Mission decision date
     * @param categoryMode
     *            Category mode (officers missions, soldiers missions, or civilians missions)
     * @param regionId
     *            Mission region Id
     * @return {@link MissionData} transaction which has all of these terms
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     * @see #searchMissions(long, long, String, int, String, String, Date, Date, int, long)
     */

    public static MissionData getMissionByDecisionNoAndDecisionDate(String decisionNumber, Date decisionDate, int categoryMode, long regionId) throws BusinessException {
	List<MissionData> missions = searchMissions(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), decisionNumber, FlagsEnum.ALL.getCode(), null, null, decisionDate, decisionDate, categoryMode, regionId);
	if (missions.size() != 0)
	    return missions.get(0);

	return null;
    }

    /**
     * Search for missions transactions in DB by all missions parameters
     * 
     * @param missionId
     *            Mission Id
     * @param empId
     *            Employee Id which has a mission
     * @param decisionNumber
     *            Mission decision Number
     * @param locationFlag
     *            Mission location flag which is external or internal
     * @param destination
     *            Mission destination
     * @param purpose
     *            Mission purpose
     * @param fromDate
     *            Mission Start Date
     * @param toDate
     *            Mission End Date
     * @param categoryMode
     *            Category mode (officers missions, soldiers missions, or civilians missions)
     * @param regionId
     *            Mission Region Id
     * @return List of {@link MissionData} List of mission transaction which have all of these terms
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    private static List<MissionData> searchMissions(long missionId, long empId, String decisionNumber, int locationFlag, String destination, String purpose, Date fromDate, Date toDate, int categoryMode, long regionId) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_ID", missionId);
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.equals("")) ? (FlagsEnum.ALL.getCode() + "") : decisionNumber);
	    qParams.put("P_LOCATION_FLAG", locationFlag);
	    qParams.put("P_DESTINATION", (destination == null || destination.equals("")) ? (FlagsEnum.ALL.getCode() + "") : ("%" + destination + "%"));
	    qParams.put("P_PURPOSE", (purpose == null || purpose.equals("")) ? (FlagsEnum.ALL.getCode() + "") : ("%" + purpose + "%"));
	    if (fromDate != null) {
		qParams.put("P_FROM_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    } else {
		qParams.put("P_FROM_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_FROM_DATE", HijriDateService.getHijriSysDateString());
	    }
	    if (toDate != null) {
		qParams.put("P_TO_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    } else {
		qParams.put("P_TO_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TO_DATE", HijriDateService.getHijriSysDateString());
	    }
	    qParams.put("P_CATEGORY_MODE", categoryMode);

	    qParams.put("P_REGION_ID", regionId);

	    return DataAccess.executeNamedQuery(MissionData.class, QueryNamesEnum.HCM_SEARCH_MISSION_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Wrapper for {@link #searchMissionRequest(long, Date, Date)} to search for missions transactions by some missions parameters like employee Id ,from date , and to date
     * 
     * @param empId
     *            Employee Id at that mission
     * @param fromDate
     *            Mission Start Date
     * @param toDate
     *            Mission End Date
     * @return List of {@link MissionData} List of mission transaction which have all of these terms
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #searchMissionRequest(long, Date, Date)
     */

    public static MissionData getMissionRequest(long empId, Date fromDate, Date toDate) throws BusinessException {
	List<MissionData> missionData = searchMissionRequest(empId, fromDate, toDate);
	if (missionData.size() != 0)
	    return missionData.get(0);

	return null;
    }

    /**
     * Search for missions transactions by some missions parameters like employee Id ,from date , and to date
     * 
     * @param empId
     *            Employee Id at that mission
     * @param fromDate
     *            Mission Start Date
     * @param toDate
     *            Mission End Date
     * @return List of {@link MissionData} List of Mission Transaction which have all of these terms
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    private static List<MissionData> searchMissionRequest(long empId, Date fromDate, Date toDate) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    if (fromDate != null) {
		qParams.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    } else {
		qParams.put("P_FROM_DATE", HijriDateService.getHijriSysDateString());
	    }
	    if (toDate != null) {
		qParams.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    } else {
		qParams.put("P_TO_DATE", HijriDateService.getHijriSysDateString());
	    }
	    return DataAccess.executeNamedQuery(MissionData.class, QueryNamesEnum.HCM_SEARCH_MISSION_REQUEST.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-----------------------------------------------------Mission Details operations---------------------------------------------*/

    /**
     * Add mission details to DB
     * 
     * @param missionId
     *            Mission Id to set at missionDetail object
     * @param missionDetailsData
     *            List of {@link MissionDetailData} that will added to DB
     * @param useSession
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static void addMissionDetails(long missionId, List<MissionDetailData> missionDetailsData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (MissionDetailData missionDetailData : missionDetailsData) {
		missionDetailData.setMissionId(missionId);
		DataAccess.addEntity(missionDetailData.getMissionDetail(), session);
		missionDetailData.setId(missionDetailData.getMissionDetail().getId());
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
     * Modify actual {@link MissionDetailData} , but before that some validation must be applied like
     * 
     * <ul>
     * <li>Check for actual period</li>
     * <li>Check mission periods overlapping</li>
     * <li>Check mission actual end date can't exceed the new financial year</li>
     * <li>Check hajj missions and double balance</li>
     * </ul>
     * 
     * @param missionData
     *            Mission transaction data {@link MissionData} object
     * @param missionDetailsData
     *            {@link MissionDetailData} object that will be modified
     * @param useSession
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void modifyActualMissionDetails(MissionData missionData, MissionDetailData missionDetailsData, Long adminDecisionId, CustomSession... useSession) throws BusinessException {

	if (missionDetailsData.getActualPeriod().intValue() > missionDetailsData.getPeriod().intValue())
	    throw new BusinessException("error_empMissionPeriod");

	if (missionDetailsData.getActualStartDate().before(HijriDateService.getHijriDate(ETRConfigurationService.getMissionActivationStartDate())))
	    throw new BusinessException("error_actualMissionStartDateInvalid", new Object[] { ETRConfigurationService.getMissionActivationStartDate() });

	if (!missionDetailsData.getAbsenceFlagBoolean()) {

	    EmployeesTransactionsConflictValidator.validateEmployeesTransactionsConflicts(new Long[] { missionDetailsData.getEmpId() }, new String[] { missionDetailsData.getEmpName() }, TransactionClassesEnum.MISSIONS.getCode(), TransactionTypesEnum.MISSION_DECISION.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), new String[] { missionDetailsData.getActualStartDateString() }, new String[] { missionDetailsData.getActualEndDateString() }, missionDetailsData.getMissionId(), null);

	    if (missionDetailsData.getActualEndDate().after(HijriDateService.getHijriDate(getFinYearStartAndEndHijriDates(HijriDateService.getHijriDateString(missionDetailsData.getActualStartDate())).get(1))))
		throw new BusinessException("error_missionEndDateAfterNewFinYear", new Object[] { missionDetailsData.getEmpName() });

	    if ((missionData.getHajjFlagBoolean() == null || missionData.getHajjFlagBoolean() == false) && (missionDetailsData.getExceptionalApprovalDate() == null && (missionDetailsData.getExceptionalApprovalNumber() == null || missionDetailsData.getExceptionalApprovalNumber().equals("")))) {

		int remainingBalance = calculateEmpMissionsBalanceWithOutCurrent(missionDetailsData.getMissionId(), missionDetailsData.getEmpId(), missionDetailsData.getActualStartDate());
		int totalMissionPeriod = missionDetailsData.getActualPeriod() + missionData.getRoadPeriod();

		if (missionData.getDoubleBalanceFlagBoolean() != null && missionData.getDoubleBalanceFlagBoolean() == true)
		    remainingBalance += ETRConfigurationService.getMissionBalanceDouble();

		if (remainingBalance - totalMissionPeriod < 0)
		    throw new BusinessException("error_empRemainingBalanceNotAllow", new Object[] { missionDetailsData.getEmpName() });
	    }

	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    DataAccess.updateEntity(missionDetailsData.getMissionDetail(), session);
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(missionDetailsData, adminDecisionId, FlagsEnum.OFF.getCode(), session);

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

    private static void doPayrollIntegration(MissionDetailData missionDetailsData, Long adminDecisionId, Integer resendFlag, CustomSession session) throws BusinessException {
	if (adminDecisionId != null) {
	    if (adminDecisionId.equals(AdminDecisionsEnum.OFFICERS_MISSION_CANCELLATION_DECISION.getCode()) && !missionDetailsData.getActualDataSavedFlagBoolean())
		return;
	    EmployeeData employee = EmployeesService.getEmployeeData(missionDetailsData.getEmpId());
	    if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		session.flushTransaction();
		String gregDecisionDateString = HijriDateService.hijriToGregDateString(missionDetailsData.getMissionDecisionDateString());
		String gregStartDateString = HijriDateService.hijriToGregDateString(missionDetailsData.getActualStartDateString());
		String gregEndDateString = HijriDateService.hijriToGregDateString(missionDetailsData.getActualEndDateString());
		String requestDecisionNumber = System.currentTimeMillis() + "";
		List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>(
			Arrays.asList(new AdminDecisionEmployeeData(missionDetailsData.getEmpId(), missionDetailsData.getEmpName(), missionDetailsData.getId(), null, gregStartDateString, gregEndDateString, requestDecisionNumber, null)));
		UnitData empUnit = UnitsService.getUnitByExactFullName(missionDetailsData.getTransEmpUnitDesc());
		Long unitId = empUnit != null ? empUnit.getId() : UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId();

		PayrollEngineService.doPayrollIntegration(adminDecisionId, employee.getCategoryId(), gregStartDateString, adminDecisionEmployeeDataList, unitId, gregDecisionDateString, DataAccess.getTableName(MissionData.class), resendFlag, FlagsEnum.ON.getCode(), session);
	    }
	}

    }

    public static void payrollIntegrationFailureHandle(Long missionDetailId, Long adminDecisionId, CustomSession session) throws BusinessException {
	MissionDetailData missionDetailData = getMissionsDetailById(missionDetailId);
	if (missionDetailData != null) {
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(missionDetailData, adminDecisionId, FlagsEnum.ON.getCode(), session);
	} else {
	    throw new BusinessException("error_transactionDataRetrievingError");
	}
    }

    // *********************** Integration with employee files (mission) ************************************

    /**
     * Integration with employee files for applying mission joining date for the employee after finishing teh mission .
     * 
     * @param transactionId
     *            Transaction Id which will setting/modifying its joining date
     * @param joiningDate
     *            New Joining date
     * @param loginUserId
     *            Login user Id to set it in System user for audit option
     * @param useSession
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void modifyMissionJoiningDate(long transactionId, Date joiningDate, long loginUserId, CustomSession... useSession) throws BusinessException {
	if (joiningDate == null)
	    throw new BusinessException("error_joiningDateIsNull");

	if (!HijriDateService.isValidHijriDate(joiningDate))
	    throw new BusinessException("error_DateError");

	MissionDetailData missionDetailData = getMissionsDetailById(transactionId);
	if (missionDetailData == null || missionDetailData.getAbsenceFlag().equals(FlagsEnum.ON.getCode()))
	    throw new BusinessException("error_noMissions");

	if (!joiningDate.after(missionDetailData.getActualEndDate()))
	    throw new BusinessException("error_joinigDate");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    missionDetailData.setJoiningDate(joiningDate);
	    missionDetailData.getMissionDetail().setSystemUser(loginUserId + ""); // For auditing.
	    DataAccess.updateEntity(missionDetailData.getMissionDetail(), session);

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
     * Validate on employees missions balances.
     * <ul>
     * <li>60 days balance for normal missions.</li>
     * <li>120 days balance for civilians employees.</li>
     * <li>More than 60 days for exceptional approvals from ministry of interior .</li>
     * </ul>
     * 
     * 
     * @param empId
     *            Employee Id that is used to search for his mission balance
     * @param empName
     *            Employee name used in error massage
     * @param missionDetailStartDate
     *            Mission start date to calculate employee balance
     * @param missionDetailEndDate
     *            Mission end date to validate end is after start
     * @param missionDetailPeriod
     *            Mission period to calculate the allowed balance of employee
     * @param missionDetailExceptionalApprovalDate
     *            Mission exceptional date if the employee get an Exception for balance
     * @param missionDetailExceptionalApprovalNumber
     *            Mission exceptional number if the employee get an Exception for balance
     * @param missionRoadPeriod
     *            Mission road period "If it is an external mission"
     * @param missionHajjFlag
     *            Hajj Flag if this mission just for hajj
     * @param missionDoubleBalanceFlag
     *            Mission double balance flag, that used for civilian employees only
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void validateEmpMissionBalance(long empId, String empName, Date missionDetailStartDate, Date missionDetailEndDate, int missionDetailPeriod, String missionDetailExceptionalApprovalDate, String missionDetailExceptionalApprovalNumber, int missionRoadPeriod, boolean missionHajjFlag, boolean missionDoubleBalanceFlag) throws BusinessException {
	if (missionDetailEndDate.after(HijriDateService.getHijriDate(getFinYearStartAndEndHijriDates(HijriDateService.getHijriDateString(missionDetailStartDate)).get(1))))
	    throw new BusinessException("error_missionEndDateAfterNewFinYear", new Object[] { empName });

	if (missionDetailExceptionalApprovalDate != null && !missionDetailExceptionalApprovalDate.equals("") && missionDetailExceptionalApprovalNumber != null && !missionDetailExceptionalApprovalNumber.equals(""))
	    return;

	if (!missionHajjFlag) {

	    int remainingBalance = calculateEmpMissionsBalance(empId, missionDetailStartDate);
	    int totalMissionPeriod = missionDetailPeriod + missionRoadPeriod;

	    /*
	     * may be for civilians mission period 120 day if doubleBalanceFlag was chosen
	     */
	    if (missionDoubleBalanceFlag == true)
		remainingBalance += ETRConfigurationService.getMissionBalanceDouble();

	    if (remainingBalance - totalMissionPeriod < 0)
		throw new BusinessException("error_empRemainingBalanceNotAllow", new Object[] { empName });
	}
    }

    /**
     * Calculate start and end date of the financial year regarding mission start date
     * 
     * @param missionDate
     *            Mission start date to calculate the start and end date of the financial year
     * @return List of string of 2 entries, first entry is start date and second entry is end date
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static List<String> getFinYearStartAndEndHijriDates(String missionDate) throws BusinessException {
	if (missionDate == null)
	    throw new BusinessException("error_missionStartDateEmpty");

	String missionStartDateString = HijriDateService.hijriToGregDateString(missionDate);

	String[] dateArray = missionStartDateString.split("/");
	String year = dateArray[2];

	String firstDateOfYear = "01/01/" + year;
	String endDateOfYear = "31/12/" + year;

	List<String> startAndEnd = new ArrayList<String>();

	startAndEnd.add(HijriDateService.gregToHijriDateString(firstDateOfYear));
	startAndEnd.add(HijriDateService.gregToHijriDateString(endDateOfYear));

	return startAndEnd;
    }

    /**
     * Calculate start and end date of the current, previous or next financial year regarding mission start date according to financialyear parameter
     * 
     * @param financialYearFlag
     *            1 for the current year , 0 for previous year , 2 for the next year
     * @return List of string of 2 entries, first entry is start date and second entry is end date
     * 
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static List<String> getFinYearStartAndEndHijriDates(int financialYearFlag, Date specificFinancialYearDate) {
	Calendar cal = Calendar.getInstance();
	int year = cal.get(Calendar.YEAR);

	if (financialYearFlag == 0)
	    year = year - 1;
	else if (financialYearFlag == 2)
	    year = year + 1;
	else if (financialYearFlag == 3) {
	    String specificFinancialYearDateString = HijriDateService.hijriToGregDateString(HijriDateService.getHijriDateString(specificFinancialYearDate));
	    String[] dateArray = specificFinancialYearDateString.split("/");
	    year = Integer.parseInt(dateArray[2]);
	}

	String firstDateOfYear = "01/01/" + year;
	String endDateOfYear = "31/12/" + year;

	List<String> startAndEnd = new ArrayList<String>();

	startAndEnd.add(HijriDateService.gregToHijriDateString(firstDateOfYear));
	startAndEnd.add(HijriDateService.gregToHijriDateString(endDateOfYear));

	return startAndEnd;
    }

    /**
     * 
     * Wrapper for {@link #getEmpMissionsBalance(long, long, Date)}, it calculate employee mission balance
     * 
     * @param empId
     *            Employee Id to calculate employee mission balance
     * @param missionDate
     *            Mission start date to calculate employee balance
     * @return The rest of mission employee balance for the current financial year
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #getEmpMissionsBalance(long, long, Date)
     */

    public static Integer calculateEmpMissionsBalance(long empId, Date missionDate) throws BusinessException {
	return (ETRConfigurationService.getMissionBalance() - getEmpMissionsBalance(FlagsEnum.ALL.getCode(), empId, missionDate));
    }

    /**
     * Wrapper for {@link #getEmpMissionsBalance(long, long, Date)}, it calculate employee mission balance excluding the period of the current mission
     * 
     * @param missionId
     *            Mission Id to exclude the balance of the employee form it
     * @param empId
     *            Employee Id to select employee in specific mission
     * @param missionDate
     *            Mission start date
     * @return The rest of mission employee Balance
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #getEmpMissionsBalance(long, long, Date)
     */

    public static Integer calculateEmpMissionsBalanceWithOutCurrent(long missionId, long empId, Date missionDate) throws BusinessException {
	return (ETRConfigurationService.getMissionBalance() - getEmpMissionsBalance(missionId, empId, missionDate));
    }

    /**
     * Get employee mission balance in all missions within certain financial year.
     * 
     * @param missionId
     *            Mission Id to exclude the balance of the employee form it
     * @param empId
     *            Employee Id to select employee in specific mission
     * @param missionDate
     *            Mission start date , that date used to specify the start and the end of the current financial year
     * @return The rest of mission employee balance for the current financial year
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static Integer getEmpMissionsBalance(long missionId, long empId, Date missionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    List<String> startAndEnd = new ArrayList<String>();
	    startAndEnd = getFinYearStartAndEndHijriDates(HijriDateService.getHijriDateString(missionDate));
	    qParams.put("P_MISSION_ID", missionId);
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_FROM_DATE", startAndEnd.get(0));
	    qParams.put("P_TO_DATE", startAndEnd.get(1));
	    Long sum = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_MISSION_DETAIL_DATA_SUM_ACTUAL_AND_ROAD_PARIOD.getCode(), qParams).get(0);
	    return (int) sum.longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /**
     * Wrapper for {@link #searchMissionDetails(long, long)}, it get all mission history of specific employee.
     * 
     * @param empId
     *            Employee Id to get the mission employee history
     * @return List of {@link MissionDetailData} that have all information about the employee mission
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #searchMissionDetails(long)
     */

    public static List<MissionDetailData> getMissionsHistory(long empId) throws BusinessException {
	return searchMissionDetails(FlagsEnum.ALL.getCode(), empId, FlagsEnum.OFF.getCode());
    }

    /**
     * Wrapper for {@link #searchMissionDetails(long, long)} ,it get {@link MissionDetailData} object by Id, that object has all information about employee mission
     * 
     * @param missionDetailId
     *            Mission Detail Id to select mission detail
     * @return {@link MissionDetailData} object or null if not found
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #searchMissionDetails(long, long)
     */

    public static MissionDetailData getMissionsDetailById(long missionDetailId) throws BusinessException {
	List<MissionDetailData> missionDetails = searchMissionDetails(missionDetailId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	if (missionDetails.size() != 0)
	    return missionDetails.get(0);

	return null;
    }

    public static List<MissionDetailData> getMissionsDetailsByEmpId(long empId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    return DataAccess.executeNamedQuery(MissionDetailData.class, QueryNamesEnum.HCM_SEARCH_MISSION_DETAIL_DATA_BY_EMP_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * Get list of {@link MissionDetailData} that have no incentives transactions using Employee Id, used by Incentives Web Service
     * 
     * @param empId
     *            Employee Id to select employee's mission details
     * @return List of {@link MissionDetailData} to Incentives Web Service
     * @throws DatabaseException
     */
    public static List<MissionDetailData> getMissionsDetailsWithNoIncentivesByEmpId(long empId) throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_EMP_ID", empId);
	return DataAccess.executeNamedQuery(MissionDetailData.class, QueryNamesEnum.HCM_MISSION_DETAIL_DATA_GET_MISSIONS_DETAILS_WITH_NO_INCENTIVES_BY_EMP_ID.getCode(), qParams);
    }

    /**
     * 
     * Get list of {@link MissionDetailData} from DB using Id and Employee Id
     * 
     * @param missionDetailId
     *            Mission Detail Data Id to get mission details for certain employee
     * @param empId
     *            Employee Id to select employee's mission details
     * @return List of {@link MissionDetailData} or null if not found
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static List<MissionDetailData> searchMissionDetails(long missionDetailId, long empId, int absenceFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", missionDetailId);
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_ABSENCE_FLAG", absenceFlag);
	    return DataAccess.executeNamedQuery(MissionDetailData.class, QueryNamesEnum.HCM_SEARCH_MISSION_DETAIL_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Wrapper for {@link #searchMissionDetailsByMissionId(long)}, it get {@link MissionDetailData} using mission Id
     * 
     * @param missionId
     *            Mission Id to get the mission detail
     * @return List of {@link MissionDetailData} or an empty list if not found
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #searchMissionDetailsByMissionId(long)
     */

    public static List<MissionDetailData> getMissionDetailsByMissionId(long missionId) throws BusinessException {
	return searchMissionDetailsByMissionId(missionId);
    }

    /**
     * Get list of {@link MissionDetailData} using mission Id
     * 
     * @param missionId
     *            Mission Id to get selected mission detail
     * @return List of {@link MissionDetailData} or an empty list if not found
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static List<MissionDetailData> searchMissionDetailsByMissionId(long missionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MISSION_ID", missionId);

	    return DataAccess.executeNamedQuery(MissionDetailData.class, QueryNamesEnum.HCM_SEARCH_MISSION_DETAIL_DATA_BY_MISSION_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Get missions that have overlap in the start and end dates if found.
     * 
     * Check if These employees have any overlapping missions in period from "startDate" to "endDate"
     * 
     * @param excludedMissionId
     *            Excluded mission transaction id
     * @param empsIds
     *            Employees Ids to get the mission which includes these employees
     * @param startDateString
     *            The start date of the mission
     * @param endDateString
     *            The end date of the mission
     * @return Count of overlap if exists
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    public static long getEmpMissionsOverlap(long empId, String startDateString, String endDateString, Long excludedMissionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_START_DATE", startDateString);
	    qParams.put("P_END_DATE", endDateString);
	    qParams.put("P_EXCLUDED_MISSION_ID", excludedMissionId == null ? FlagsEnum.ALL.getCode() : excludedMissionId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_MISSION_DETAIL_OVERLAP.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*-----------------------------------------------------Mission Utilities--------------------------------------------*/

    /**
     * Calculate mission's end date using start date, period, and road period.
     * 
     * That utility {@link HijriDateService} use period and road period for the mission to calculate the end date.
     * 
     * @param startDateString
     *            Mission start date in string format
     * @param period
     *            Mission period to calculate the end date
     * @param roadPeriod
     *            Mission road period to calculate the end date
     * @return End date in string format
     * 
     * @see HijriDateService
     */

    public static String calculateMissionEndDate(String startDateString, Integer period, Integer roadPeriod) {
	if (startDateString != null && period != null && roadPeriod != null) {
	    try {
		return HijriDateService.addSubStringHijriDays(startDateString, (period + roadPeriod) - 1);
	    } catch (Exception e) {
		return null;
	    }
	}

	return null;
    }

    /*-----------------------------------------------------Mission Printing---------------------------------------------*/

    /**
     * Get mission's data in bytes to print individual or collective mission decisions for categories officers, soldiers, or civilians.
     * 
     * This method use the mission object to get its associated mission details to get some parameters to send them to the report needed to be printed.
     * 
     * @param mission
     *            {@link MissionData} transaction object
     * @return Array of bytes to print mission decision
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    public static byte[] getMissionDecisionBytes(long missionId, int categoryMode) throws BusinessException {
	try {

	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_MISSION_ID", missionId);

	    List<MissionDetailData> MissionDetailData = MissionsService.getMissionDetailsByMissionId(missionId);

	    if (categoryMode == CategoryModesEnum.OFFICERS.getCode()) {
		if (MissionDetailData.size() == 1) {
		    reportName = ReportNamesEnum.MISSIONS_DECISION_OFFICERS.getCode();
		} else if (MissionDetailData.size() > 1) {
		    reportName = ReportNamesEnum.MISSIONS_DECISION_OFFICERS_COLLECTIVE.getCode();

		    parameters.put("P_COUNT", String.valueOf(MissionDetailData.size()));
		    parameters.put("P_FIRST_CODE", MissionDetailData.get(0).getMilitaryNumber().toString());
		    parameters.put("P_FIRST_NAME", MissionDetailData.get(0).getTransEmpRankDesc() + " / " + MissionDetailData.get(0).getEmpName());
		    parameters.put("P_LAST_CODE", MissionDetailData.get(MissionDetailData.size() - 1).getMilitaryNumber().toString());
		    parameters.put("P_LAST_NAME", MissionDetailData.get(MissionDetailData.size() - 1).getTransEmpRankDesc() + " / " + MissionDetailData.get(MissionDetailData.size() - 1).getEmpName());
		}
	    } else if (categoryMode == CategoryModesEnum.SOLDIERS.getCode()) {
		if (MissionDetailData.size() == 1) {
		    reportName = ReportNamesEnum.MISSIONS_DECISION_SOLDIERS.getCode();
		} else if (MissionDetailData.size() > 1) {
		    reportName = ReportNamesEnum.MISSIONS_DECISION_SOLDIERS_COLLECTIVE.getCode();

		    parameters.put("P_COUNT", String.valueOf(MissionDetailData.size()));
		    parameters.put("P_FIRST_CODE", MissionDetailData.get(0).getTransEmpJobCode());
		    parameters.put("P_FIRST_NAME", MissionDetailData.get(0).getTransEmpRankDesc() + " / " + MissionDetailData.get(0).getEmpName());
		    parameters.put("P_LAST_CODE", MissionDetailData.get(MissionDetailData.size() - 1).getTransEmpJobCode());
		    parameters.put("P_LAST_NAME", MissionDetailData.get(MissionDetailData.size() - 1).getTransEmpRankDesc() + " / " + MissionDetailData.get(MissionDetailData.size() - 1).getEmpName());
		}
	    } else if (categoryMode == CategoryModesEnum.CIVILIANS.getCode()) {
		if (MissionDetailData.size() == 1) {
		    reportName = ReportNamesEnum.MISSIONS_DECISION_CIVILIANS.getCode();
		} else if (MissionDetailData.size() > 1) {
		    reportName = ReportNamesEnum.MISSIONS_DECISION_CIVILIANS_COLLECTIVE.getCode();

		    parameters.put("P_COUNT", String.valueOf(MissionDetailData.size()));
		    parameters.put("P_FIRST_CODE", MissionDetailData.get(0).getTransEmpJobCode());
		    parameters.put("P_FIRST_NAME", MissionDetailData.get(0).getTransEmpRankDesc() + " / " + MissionDetailData.get(0).getEmpName());
		    parameters.put("P_LAST_CODE", MissionDetailData.get(MissionDetailData.size() - 1).getTransEmpJobCode());
		    parameters.put("P_LAST_NAME", MissionDetailData.get(MissionDetailData.size() - 1).getTransEmpRankDesc() + " / " + MissionDetailData.get(MissionDetailData.size() - 1).getEmpName());
		}
	    }

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Get mission's payment in bytes to print individual or collective mission payment decisions for categories officers, or soldiers.
     * 
     * This method uses the mission object to get its associated mission details to get some parameters and send them to the report needed to print.
     * 
     * @param mission
     *            {@link MissionData} transaction object
     * @return Array of bytes to print mission payment decision
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    public static byte[] getMissionPaymentDecisionBytes(MissionData mission) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    List<MissionDetailData> missionDetailDataList = MissionsService.getMissionDetailsByMissionId(mission.getId());
	    for (int i = 0; i < missionDetailDataList.size(); i++) {
		if (missionDetailDataList.get(i).getAbsenceFlag().equals(FlagsEnum.ON.getCode())) {
		    missionDetailDataList.remove(i);
		    i--;
		}
	    }

	    if (missionDetailDataList.size() == 0)
		throw new BusinessException("error_allAbsencePaymentDecision");

	    if (missionDetailDataList.size() == 1 && mission.getCategoryMode().longValue() == CategoriesEnum.OFFICERS.getCode()) {

		reportName = ReportNamesEnum.MISSIONS_OFFICERS_PAYMENT.getCode();
		parameters.put("P_MISSION_ID", mission.getId());

	    } else if (missionDetailDataList.size() > 1 && mission.getCategoryMode().longValue() == CategoriesEnum.OFFICERS.getCode()) {

		reportName = ReportNamesEnum.MISSIONS_OFFICERS_PAYMENT_COLLECTIVE.getCode();
		parameters.put("P_MISSION_ID", mission.getId());
		parameters.put("P_COUNT", String.valueOf(missionDetailDataList.size()));
		parameters.put("P_FIRST_CODE", missionDetailDataList.get(0).getMilitaryNumber().toString());
		parameters.put("P_FIRST_NAME", missionDetailDataList.get(0).getTransEmpRankDesc() + " / " + missionDetailDataList.get(0).getEmpName());
		parameters.put("P_LAST_CODE", missionDetailDataList.get(missionDetailDataList.size() - 1).getMilitaryNumber().toString());
		parameters.put("P_LAST_NAME", missionDetailDataList.get(missionDetailDataList.size() - 1).getTransEmpRankDesc() + " / " + missionDetailDataList.get(missionDetailDataList.size() - 1).getEmpName());

	    } else if (missionDetailDataList.size() == 1 && mission.getCategoryMode().longValue() == CategoriesEnum.SOLDIERS.getCode()) {

		reportName = ReportNamesEnum.MISSIONS_SOLDIERS_PAYMENT.getCode();
		parameters.put("P_MISSION_ID", mission.getId());

	    } else if (missionDetailDataList.size() > 1 && mission.getCategoryMode().longValue() == CategoriesEnum.SOLDIERS.getCode()) {

		reportName = ReportNamesEnum.MISSIONS_SOLDIERS_PAYMENT_COLLECTIVE.getCode();
		parameters.put("P_MISSION_ID", mission.getId());
		parameters.put("P_COUNT", String.valueOf(missionDetailDataList.size()));
		parameters.put("P_FIRST_CODE", missionDetailDataList.get(0).getTransEmpJobCode());
		parameters.put("P_FIRST_NAME", missionDetailDataList.get(0).getTransEmpRankDesc() + " / " + missionDetailDataList.get(0).getEmpName());
		parameters.put("P_LAST_CODE", missionDetailDataList.get(missionDetailDataList.size() - 1).getTransEmpJobCode());
		parameters.put("P_LAST_NAME", missionDetailDataList.get(missionDetailDataList.size() - 1).getTransEmpRankDesc() + " / " + missionDetailDataList.get(missionDetailDataList.size() - 1).getEmpName());

	    }

	    return getRTFReportData(reportName, parameters);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

    /**
     * Get Mission's financial link data in bytes to print individual or collective mission financial link for officers.
     * 
     * This method uses the mission object to get its associated mission details to get some parameters and send them to the report needed to print
     * 
     * @param mission
     *            {@link MissionData} transaction object
     * @return Array of bytes to print mission financial link
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    public static byte[] getMissionFinancialLinkBytes(MissionData mission) throws BusinessException {
	try {
	    List<MissionDetailData> missionDetailDataList = MissionsService.getMissionDetailsByMissionId(mission.getId());
	    for (int i = 0; i < missionDetailDataList.size(); i++) {
		if (missionDetailDataList.get(i).getAbsenceFlag().equals(FlagsEnum.ON.getCode())) {
		    missionDetailDataList.remove(i);
		    i--;
		}
	    }

	    if (missionDetailDataList.size() == 0)
		throw new BusinessException("error_allAbsenceFinancialLink");

	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    if (missionDetailDataList.size() == 1) {

		reportName = ReportNamesEnum.MISSIONS_OFFICERS_FINANCIAL_LINK.getCode();
		parameters.put("P_MISSION_ID", mission.getId());

	    } else if (missionDetailDataList.size() > 1) {

		reportName = ReportNamesEnum.MISSIONS_OFFICERS_FINANCIAL_LINK_COLLECTIVE.getCode();
		parameters.put("P_MISSION_ID", mission.getId());
		parameters.put("P_FIRST_CODE", missionDetailDataList.get(0).getMilitaryNumber().toString());
		parameters.put("P_FIRST_NAME", missionDetailDataList.get(0).getTransEmpRankDesc() + " / " + missionDetailDataList.get(0).getEmpName());
		parameters.put("P_LAST_CODE", missionDetailDataList.get(missionDetailDataList.size() - 1).getMilitaryNumber().toString());
		parameters.put("P_LAST_NAME", missionDetailDataList.get(missionDetailDataList.size() - 1).getTransEmpRankDesc() + " / " + missionDetailDataList.get(missionDetailDataList.size() - 1).getEmpName());

	    }

	    return getRTFReportData(reportName, parameters);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

    /**
     * Get bytes of mission's official performance to print it for categories officers, soldiers, or civilians .
     * 
     * Admin user can print all official performance for all employees, others can print for themselve only .
     * 
     * @param mission
     *            {@link MissionData} transaction object to print official performance of it
     * @param emp
     *            {@link EmployeeData} object to print official performance of him, not that this parameter used in case of adminUser = 0
     * @param adminUser
     *            Flag to indicate if user has privileges to print official performance for all employees or not
     * @return Array of bytes to print
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    public static byte[] getMissionOfficialPerformanceBytes(MissionData mission, EmployeeData emp, int adminUser) throws BusinessException {
	try {

	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    if (mission.getCategoryMode().longValue() == CategoriesEnum.OFFICERS.getCode())
		reportName = ReportNamesEnum.MISSIONS_OFFICERS_OFFICIAL_PERFORMANCE.getCode();
	    else if (mission.getCategoryMode().longValue() == CategoriesEnum.SOLDIERS.getCode())
		reportName = ReportNamesEnum.MISSIONS_SOLDIERS_OFFICIAL_PERFORMANCE.getCode();
	    else if (mission.getCategoryMode().longValue() == CategoriesEnum.PERSONS.getCode())
		reportName = ReportNamesEnum.MISSIONS_CIVILIANS_OFFICIAL_PERFORMANCE.getCode();

	    parameters.put("P_ADMIN_USER", adminUser);
	    parameters.put("P_MISSION_ID", mission.getId());
	    parameters.put("P_EMP_ID", emp.getEmpId());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

    /**
     * Get mission balance inquiry to print it categories officers, soldiers, or civilians, by sending parameters to report like (system date, employee Id,financial year start date,financial year end date).
     * 
     * This report inquiry the missions balance of certain employee within certain financial year.
     * 
     * @param searchByEmp
     *            {@link EmployeeData} object to send its Id and to specify which category to print
     * @param financialYearFlag
     *            1 for the current year , 0 for previous year , 2 for the next year, 3 for specified financial year
     * @return Array of bytes to print
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    public static byte[] getMissionBalanceInquiryBytes(int hajjFlag, EmployeeData searchByEmp, int financialYearFlag, Date specificFinancialYearDate) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    List<String> startAndEnd = getFinYearStartAndEndHijriDates(financialYearFlag, specificFinancialYearDate);

	    if (searchByEmp.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()))
		reportName = ReportNamesEnum.MISSIONS_OFFICERS_BALANCE_INQUIRY.getCode();
	    else if (searchByEmp.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
		reportName = ReportNamesEnum.MISSIONS_SOLDIERS_BALANCE_INQUIRY.getCode();
	    else
		reportName = ReportNamesEnum.MISSIONS_CIVILIANS_BALANCE_INQUIRY.getCode();

	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDate());
	    parameters.put("P_EMP_ID", searchByEmp.getEmpId());
	    parameters.put("P_START_DATE", startAndEnd.get(0));
	    parameters.put("P_END_DATE", startAndEnd.get(1));
	    parameters.put("P_HAJJ_FLAG", hajjFlag);
	    parameters.put("P_MISSION_BALANCE", ETRConfigurationService.getMissionBalance());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Get mission statistical data to print it, using some parameters to report like (system date, category, from date, to date, region Id).
     * 
     * These statistic data are like count mission location (internal/ external) for each category per region or all regions.
     * 
     * @param category
     *            Category mode
     * @param regionId
     *            Mission region Id to get mission in specific region or all regions
     * @param toDate
     *            Mission end date
     * @param fromDate
     *            Mission start date
     * @return Array of bytes to print
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    public static byte[] getMissionStatisicalReport(int category, long regionId, Date toDate, Date fromDate) throws BusinessException {
	try {

	    String reportName = ReportNamesEnum.MISSIONS_STATISICAL_REPORT.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_CATEGORY", category);
	    parameters.put("P_REGION_ID", regionId);

	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getMissionEmployeesStatisicalReport(int category, long regionId, Date toDate, Date fromDate, Long empId, String empName, String unitHKeyPrefix, String unitDesc) throws BusinessException {
	try {

	    String reportName = ReportNamesEnum.MISSIONS_EMPLOYEES_STATISICAL_REPORT.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_CATEGORY", category);
	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    parameters.put("P_EMP_NAME", empName == null ? FlagsEnum.ALL.getCode() + "" : empName);
	    parameters.put("P_UNIT_HKEY_PREFIX", unitHKeyPrefix == null ? FlagsEnum.ALL.getCode() + "" : unitHKeyPrefix);
	    parameters.put("P_UNIT_DESC", unitDesc == null ? FlagsEnum.ALL.getCode() + "" : unitDesc);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }
}