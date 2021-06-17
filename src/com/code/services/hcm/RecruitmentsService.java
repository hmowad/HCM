package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.payroll.PayrollSalary;
import com.code.dal.orm.hcm.recruitments.RecruitmentDistributionData;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransaction;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransactionData;
import com.code.dal.orm.hcm.recruitments.RecruitmentWishData;
import com.code.dal.orm.log.EmployeeLog;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RecruitmentClassesEnum;
import com.code.enums.RecruitmentTypeEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.SequenceNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.integration.PayrollEngineService;
import com.code.services.log.LogService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

public class RecruitmentsService extends BaseService {

    /**
     * private constructor to prevent instantiation
     * 
     */
    private RecruitmentsService() {
    }

    /************************************ Recruitment Wish ************************/

    /*---------------------------Operations---------------------------*/
    /**
     * Adds a recruitment wish in the database
     * 
     * @param recruitmentWishData
     *            recruitment wish data object to be added
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData
     */
    public static void addRecruitmentWish(RecruitmentWishData recruitmentWishData, CustomSession... useSession) throws BusinessException {
	if (recruitmentWishData == null)
	    throw new BusinessException("error_transactionDataError");

	validateRecruitmentWish(recruitmentWishData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    EmployeesService.deleteSoldiersDistribution(null, session);

	    DataAccess.addEntity(recruitmentWishData.getRecruitmentWish(), session);
	    recruitmentWishData.setId(recruitmentWishData.getRecruitmentWish().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    recruitmentWishData.setId(null);

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
     * Updates a recruitment wish in the database
     * 
     * @param recruitmentWishData
     *            recruitment wish data object to be updated
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData
     */
    public static void updateRecruitmentWish(RecruitmentWishData recruitmentWishData, CustomSession... useSession) throws BusinessException {
	if (recruitmentWishData == null)
	    throw new BusinessException("error_transactionDataError");

	validateRecruitmentWish(recruitmentWishData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    EmployeesService.deleteSoldiersDistribution(null, session);

	    DataAccess.updateEntity(recruitmentWishData.getRecruitmentWish(), session);

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
     * Constructs a recruitment wish data object using the employee id
     * 
     * @param employeeId
     *            employee that we construct a recruit wish for him
     * @return recruitment wish data object
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData
     */
    public static RecruitmentWishData constructRecruitmentWishByEmpId(long employeeId) throws BusinessException {
	EmployeeData employeeData = EmployeesService.getEmployeeData(employeeId);
	if (employeeData == null)
	    throw new BusinessException("error_employeeDataError");

	RecruitmentWishData recWish = new RecruitmentWishData();
	recWish.setEmpId(employeeId);
	recWish.setEmpName(employeeData.getName());
	recWish.setEmpStatusId(employeeData.getStatusId());
	recWish.setEmpStatusDesc(employeeData.getStatusDesc());
	recWish.setEmpRecRankId(employeeData.getRecruitmentRankId());
	recWish.setEmpRecRankDesc(employeeData.getRecruitmentRankDesc());
	recWish.setEmpRecMinorSpecId(employeeData.getRecruitmentMinorSpecId());
	recWish.setEmpRecMinorSpecDesc(employeeData.getRecruitmentMinorSpecDesc());
	return recWish;
    }

    /*---------------------------Validations--------------------------*/
    /**
     * Validates recruitment wish data object
     * 
     * @param recruitmentWishData
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData
     */
    private static void validateRecruitmentWish(RecruitmentWishData recruitmentWishData) throws BusinessException {
	validateRecruitmentWishMandatoryFields(recruitmentWishData);

	EmployeeData empData = EmployeesService.getEmployeeData(recruitmentWishData.getEmpId());
	if (!empData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode()) && !empData.getStatusId().equals(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode())) {
	    throw new BusinessException("error_employeeStatusForRecWishInvalid");
	}

	if (recruitmentWishData.getEvaluationDegree().longValue() < 1) {
	    throw new BusinessException("error_evaluationDegreeCannotBeLessThanOne");
	}

	HashSet<Long> wish = new HashSet<Long>();
	wish.add(recruitmentWishData.getRegionsFirstWishId());
	if (!wish.add(recruitmentWishData.getRegionsSecondWishId()) || !wish.add(recruitmentWishData.getRegionsThirdWishId()) || !wish.add(recruitmentWishData.getRegionsFourthWishId()) || !wish.add(recruitmentWishData.getRegionsFifthWishId()) || !wish.add(recruitmentWishData.getRegionsSixthWishId()) || !wish.add(recruitmentWishData.getRegionsSeventhWishId()) || !wish.add(recruitmentWishData.getRegionsEighthWishId()) || !wish.add(recruitmentWishData.getRegionsNinthWishId())
		|| !wish.add(recruitmentWishData.getRegionsTenthWishId()) || !wish.add(recruitmentWishData.getRegionsEleventhWishId()))
	    throw new BusinessException("error_recruitmentWishUniquenessViolation");

	RecruitmentWishData recWish = null;

	recWish = getRecruitmentWishByEmpId(recruitmentWishData.getEmpId());
	if (recWish != null && (recruitmentWishData.getId() == null || recWish.getId().longValue() != recruitmentWishData.getId().longValue()))
	    throw new BusinessException("error_recruitmentWishEmployeeUniquenessViolation");

	List<RecruitmentWishData> recWishes = getRecruitmentWishes(recruitmentWishData.getEmpRecMinorSpecId(), recruitmentWishData.getEmpRecRankId(), recruitmentWishData.getEvaluationDegree());
	recWish = recWishes.size() > 0 ? recWishes.get(0) : null;
	if (recWish != null && (recruitmentWishData.getId() == null || recWish.getId().longValue() != recruitmentWishData.getId().longValue()))
	    throw new BusinessException("error_recruitmentWishEvaluationDegreeUniquenessViolation");
    }

    public static void validateRecruitmentWishMandatoryFields(RecruitmentWishData recruitmentWishData) throws BusinessException {
	if (recruitmentWishData.getEmpId() == null)
	    throw new BusinessException("error_empSelectionManadatory");
	if (recruitmentWishData.getEvaluationDegree() == null)
	    throw new BusinessException("error_evaluationDegreeMandatory");
	if (recruitmentWishData.getRegionsFirstWishId() == null || recruitmentWishData.getRegionsSecondWishId() == null || recruitmentWishData.getRegionsThirdWishId() == null || recruitmentWishData.getRegionsFourthWishId() == null || recruitmentWishData.getRegionsFifthWishId() == null || recruitmentWishData.getRegionsSixthWishId() == null || recruitmentWishData.getRegionsSeventhWishId() == null || recruitmentWishData.getRegionsEighthWishId() == null
		|| recruitmentWishData.getRegionsNinthWishId() == null || recruitmentWishData.getRegionsTenthWishId() == null || recruitmentWishData.getRegionsEleventhWishId() == null)
	    throw new BusinessException("error_recruitmentWishesMandatory");
    }

    /*---------------------------Queries------------------------------*/
    /**
     * Wrapper for {@link #searchRecruitmentWishes(long, String, long, long, int, Long[])}, gets recruitment wishes for soldiers who their statuses are new student ranked soldier and on duty under recruitment soldier
     * 
     * @return list of recruitment wish data
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData, #searchRecruitmentWishes(long, String, long, long, int, Long[])
     */
    public static List<RecruitmentWishData> getRecruitmentWishes() throws BusinessException {
	Long[] statusesIds = { EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode(), EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode() };
	return searchRecruitmentWishes(FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), statusesIds);
    }

    /**
     * Wrapper for {@link #searchRecruitmentWishes(long, String, long, long, int, Long[])}, Gets recruitment wish list by using search parameters
     * 
     * @param minorSpecId
     *            minor specialization id of the employee of the recruitment wish
     * @param rankId
     *            rank id of the employee of the recruitment wish
     * @param evaluationDegree
     *            evaluation degree of the employee of the recruitment wish
     * @return list of recruitment wish data
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData, #searchRecruitmentWishes(long, String, long, long, int, Long[])
     */
    public static List<RecruitmentWishData> getRecruitmentWishes(long minorSpecId, long rankId, int evaluationDegree) throws BusinessException {
	Long[] statusesIds = { EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode(), EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode() };
	return searchRecruitmentWishes(FlagsEnum.ALL.getCode(), null, minorSpecId, rankId, evaluationDegree, statusesIds);
    }

    /**
     * Wrapper for {@link #searchRecruitmentWishes(long, String, long, long, int, Long[])}, gets recruitment wish list by using search parameters
     * 
     * @param empName
     *            employee name
     * @param minorSpecId
     *            minor specialization id of the employee of the recruitment wish
     * @param rankId
     *            rank id of the employee of the recruitment wish
     * @return list of recruitment wish data
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData, #searchRecruitmentWishes(long, String, long, long, int, Long[])
     */
    public static List<RecruitmentWishData> getRecruitmentWishes(String empName, long minorSpecId, long rankId) throws BusinessException {
	Long[] statusesIds = { EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode(), EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode() };
	return searchRecruitmentWishes(FlagsEnum.ALL.getCode(), empName, minorSpecId, rankId, FlagsEnum.ALL.getCode(), statusesIds);
    }

    /**
     * Wrapper for {@link #searchRecruitmentWishes(long, String, long, long, int, Long[])}, gets recruitment wish for an employee using its id
     * 
     * @param empId
     *            employee id
     * @return recruitment wish data object if there is a wish for the employee, else null
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData
     */
    public static RecruitmentWishData getRecruitmentWishByEmpId(long empId) throws BusinessException {
	List<RecruitmentWishData> recruitmentWishData = searchRecruitmentWishes(empId, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null);
	if (recruitmentWishData.size() > 0)
	    return recruitmentWishData.get(0);
	else
	    return null;
    }

    /**
     * Query to search for recruitment wishes using search parameters
     * 
     * @param empId
     *            employee id
     * @param empName
     *            employee name
     * @param minorSpecId
     *            minor specialization id of the employee of the recruitment wish
     * @param rankId
     *            rank id of the employee of the recruitment wish
     * @param evaluationDegree
     *            evaluation degree of the employee of the recruitment wish
     * @param statusesIds
     *            statuses ids of the employees
     * @return list of recruitment wish data list
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData
     */
    private static List<RecruitmentWishData> searchRecruitmentWishes(long empId, String empName, long minorSpecId, long rankId, int evaluationDegree, Long[] statusesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_EMP_NAME", (empName == null || empName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    qParams.put("P_MINOR_SPEC_ID", minorSpecId);
	    qParams.put("P_RANK_ID", rankId);
	    qParams.put("P_EVALUATION_DEGREE", evaluationDegree);
	    if (statusesIds != null && statusesIds.length > 0) {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUSES_IDS", statusesIds);
	    } else {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUSES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(RecruitmentWishData.class, QueryNamesEnum.HCM_SEARCH_RECRUITMENT_WISHES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets recruitment wishes for employees that don't have a recruitment region id yet
     * 
     * @return list of objects, earch object is an array that contain 2 objects(RecruitmentWishData, EmployeeData)
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentWishData, EmployeeData
     */
    public static List<Object> getAllNonDistributedSoldiersWishes() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.HCM_GET_ALL_NON_DISTRIBUTED_SOLDIERS_WISHES.getCode(), new HashMap<String, Object>());
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /************************************** Recruitment Distribution ****************************************/

    /* ------------------ Operations ------------------ */
    /**
     * Adds a recruitment distribution data object in the database
     * 
     * @param recruitmentDistributionData
     *            recruitment distribution data object
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentDistributionData
     */
    public static void addRecruitmentDistribution(RecruitmentDistributionData recruitmentDistributionData, CustomSession... useSession) throws BusinessException {
	if (recruitmentDistributionData == null)
	    throw new BusinessException("error_transactionDataError");

	validateRecruitmentDistribution(recruitmentDistributionData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    EmployeesService.deleteSoldiersDistribution(null, session);

	    DataAccess.addEntity(recruitmentDistributionData.getRecruitmentDistribution(), session);
	    recruitmentDistributionData.setId(recruitmentDistributionData.getRecruitmentDistribution().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    recruitmentDistributionData.setId(null);

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
     * Updates a recruitment distribution data object in the database
     * 
     * @param recruitmentDistributionData
     *            recruitment distribution data object to be updated
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentDistributionData
     */
    public static void updateRecruitmentDistribution(RecruitmentDistributionData recruitmentDistributionData, CustomSession... useSession) throws BusinessException {
	if (recruitmentDistributionData == null)
	    throw new BusinessException("error_transactionDataError");

	validateRecruitmentDistribution(recruitmentDistributionData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    EmployeesService.deleteSoldiersDistribution(null, session);

	    DataAccess.updateEntity(recruitmentDistributionData.getRecruitmentDistribution(), session);

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
     * Deletes a recruitment distribution data object in the database
     * 
     * @param recruitmentDistributionData
     *            recruitment distribution data object to be deleted
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentDistributionData
     */
    public static void deleteRecruitmentDistribution(RecruitmentDistributionData recruitmentDistributionData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    EmployeesService.deleteSoldiersDistribution(null, session);

	    if (recruitmentDistributionData != null)
		DataAccess.deleteEntity(recruitmentDistributionData.getRecruitmentDistribution(), session);

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

    /* ------------------ Validations ----------------- */
    /**
     * Validates a recruitment distribution data object
     * 
     * @param recruitmentDistributionData
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentDistributionData
     */
    private static void validateRecruitmentDistribution(RecruitmentDistributionData recruitmentDistributionData) throws BusinessException {
	if (recruitmentDistributionData.getRegionId() == null)
	    throw new BusinessException("error_regionMandatory");
	if (recruitmentDistributionData.getMinorSpecializationId() == null)
	    throw new BusinessException("error_minorSpecializationMandatory");
	if (recruitmentDistributionData.getRankId() == null)
	    throw new BusinessException("error_rankIsMandatory");
	if (recruitmentDistributionData.getCount() == null)
	    throw new BusinessException("error_soldiersCountMandatory");
	RecruitmentDistributionData recDistribution = getRecruitmentDistribution(recruitmentDistributionData.getRegionId(), recruitmentDistributionData.getMinorSpecializationId(), recruitmentDistributionData.getRankId());
	if (recDistribution != null && (recruitmentDistributionData.getId() == null || recDistribution.getId().longValue() != recruitmentDistributionData.getId().longValue()))
	    throw new BusinessException("error_recruitmentDistributionUniquenessViolation");
    }

    /* -------------------- Queries ------------------- */
    /**
     * Wrapper for {@link #searchRecruitmentDistributions(long, long, long)}, Gets all recruitment distributions
     * 
     * @return list of recruitment distribution data objects
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentDistributionData, #searchRecruitmentDistributions(long, long, long)
     */
    public static List<RecruitmentDistributionData> getAllRecruitmentDistributions() throws BusinessException {
	return searchRecruitmentDistributions((long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Wrapper for {@link #searchRecruitmentDistributions(long, long, long)}, gets recruitment distribution object using search parameters
     * 
     * @param regionId
     *            region id of the recruitment distribution data object
     * @param minorSpecId
     *            minor specialization id of the recruitment distribution data object
     * @param rankId
     *            rank id of the recruitment distribution data object
     * @return recruitment distribution data object
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentDistributionData, #searchRecruitmentDistributions(long, long, long)
     */
    private static RecruitmentDistributionData getRecruitmentDistribution(long regionId, long minorSpecId, long rankId) throws BusinessException {
	List<RecruitmentDistributionData> recruitmentDistributions = searchRecruitmentDistributions(regionId, minorSpecId, rankId);
	if (recruitmentDistributions.size() > 0)
	    return recruitmentDistributions.get(0);
	return null;
    }

    /**
     * Query to search for recruitment distribution data objects using search parameters
     * 
     * @param regionId
     *            region id of the recruitment distribution data object
     * @param minorSpecId
     *            minor specialization id of the recruitment distribution data object
     * @param rankId
     *            rank id of the recruitment distribution data object
     * @return recruitment distribution object list
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentDistributionData
     */
    private static List<RecruitmentDistributionData> searchRecruitmentDistributions(long regionId, long minorSpecId, long rankId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_MINOR_SPEC_ID", minorSpecId);
	    qParams.put("P_RANK_ID", rankId);

	    return DataAccess.executeNamedQuery(RecruitmentDistributionData.class, QueryNamesEnum.HCM_SEARCH_RECRUITMENT_DISTRIBUTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets count of all needs of all regions
     * 
     * @return count of the regions needs
     * @throws BusinessException
     *             if any error occurs
     */
    public static long countRegionsNeeds() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_REGIONS_NEEDS.getCode(), new HashMap<String, Object>()).get(0).longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /**
     * Returns true if soldiers(That have evaluation degrees and wishes) count are suitable for regions needs considering their ranks and minor specializations
     * 
     * @throws BusinessException
     *             if any error occurs
     */
    public static boolean checkSoldiersAndRegionsNeedsCount() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_CHECK_SOLDIERS_AND_REGIONS_NEEDS_COUNT.getCode(), new HashMap<String, Object>()).isEmpty() ? true : false;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*
     * ************************************** Recruitment Transaction ***************************************
     */

    /* ------------------- Operations ----------------- */
    /**
     * Adds a recruitment transaction list in the database
     * 
     * @param recruitmentTransactionsData
     *            list of recruitment trasaction data object to be added
     * @param processName
     *            process name of the workflow that created the transactions
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentTransactionData
     */
    private static void addRecruitmentTransactions(List<RecruitmentTransactionData> recruitmentTransactionsData, String processName, CustomSession... useSession) throws BusinessException {
	if (recruitmentTransactionsData != null && !(recruitmentTransactionsData.size() == 1 && (recruitmentTransactionsData.get(0).getRecruitmentType() == RecruitmentTypeEnum.RE_RECRUITMENT.getCode() || recruitmentTransactionsData.get(0).getRecruitmentType() == RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode())))
	    validateRecruitmentTransactions(recruitmentTransactionsData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (recruitmentTransactionsData != null && recruitmentTransactionsData.size() > 0) {
		if (!(recruitmentTransactionsData.size() == 1 && (recruitmentTransactionsData.get(0).getRecruitmentType() == RecruitmentTypeEnum.RE_RECRUITMENT.getCode() || recruitmentTransactionsData.get(0).getRecruitmentType() == RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode())))
		    if (processName != null) {
			String[] etrCorInfo = ETRCorrespondence.doETRCorOut(processName, session);
			for (RecruitmentTransactionData recruitmentTransactionData : recruitmentTransactionsData) {
			    recruitmentTransactionData.setDecisionNumber(etrCorInfo[0]);
			    recruitmentTransactionData.setDecisionDateString(etrCorInfo[1]);

			    if (recruitmentTransactionData.getDegreeId() != null) {
				PayrollSalary payrollSalary = PayrollsService.getPayrollSalary(recruitmentTransactionData.getRankId(), recruitmentTransactionData.getDegreeId());
				if (payrollSalary != null) {
				    recruitmentTransactionData.setTransSalary(payrollSalary.getBasicSalary());
				    recruitmentTransactionData.setTransTransportationAllowance(payrollSalary.getTransportationAllowance());
				}
			    }
			}
		    }
		for (RecruitmentTransactionData recruitmentTransactionData : recruitmentTransactionsData) {
		    DataAccess.addEntity(recruitmentTransactionData.getRecruitmentTransaction(), session);
		    recruitmentTransactionData.setId(recruitmentTransactionData.getRecruitmentTransaction().getId());
		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    for (RecruitmentTransactionData recruitmentTransactionData : recruitmentTransactionsData) {
		recruitmentTransactionData.setId(null);
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
     * Updates a recruitment transaction list in the database
     * 
     * @param recruitmentTransactionsData
     *            list of recruitment trasaction data object to be updated
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     * @see RecruitmentTransactionData
     */
    public static void updateRecruitmentTransactions(List<RecruitmentTransactionData> recruitmentTransactionsData, CustomSession... useSession) throws BusinessException {
	if (recruitmentTransactionsData != null)
	    validateRecruitmentTransactions(recruitmentTransactionsData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (recruitmentTransactionsData != null && recruitmentTransactionsData.size() > 0) {

		for (RecruitmentTransactionData recruitmentTransactionData : recruitmentTransactionsData) {
		    DataAccess.updateEntity(recruitmentTransactionData.getRecruitmentTransaction(), session);
		}
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
     * Updates joining data in a recruitment transaction object
     * 
     * @param recTransactionId
     *            id of the recruitment transactions
     * @param joiningDate
     *            new joining date to be updated
     * @param loginUserId
     *            login user id used to audit the operation
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    public static void updateRecruitmentTransactionJoiningDate(long recTransactionId, Date joiningDate, long loginUserId, CustomSession... useSession) throws BusinessException {
	RecruitmentTransactionData recTransactionData = getRecruitmentTransactionById(recTransactionId);
	if (recTransactionData == null)
	    throw new BusinessException("error_transactionDataError");

	if (joiningDate == null || !HijriDateService.isValidHijriDate(joiningDate))
	    throw new BusinessException("error_invalidHijriDate");

	if (joiningDate.after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_recruitmentJoiningDateCannotBeAfterSystemDate");

	EmployeeData emp = EmployeesService.getEmployeeData(recTransactionData.getEmployeeId());

	if (recTransactionData.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || (recTransactionData.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && (recTransactionData.getRecruitmentClass() != RecruitmentClassesEnum.INSPECTORS_RECRUITMENT.getCode() && recTransactionData.getRecruitmentClass() != RecruitmentClassesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode()))) {
	    if (joiningDate.before(recTransactionData.getRecruitmentDate()))
		throw new BusinessException("error_recruitmentJoiningDateCannotBeBeforeRecruitmentnDate");
	} else {
	    if (recTransactionData.getFirstRecruitmentDate() != null && joiningDate.before(recTransactionData.getFirstRecruitmentDate()))
		throw new BusinessException("error_recruitmentJoiningDateCannotBeBeforeFirstRecruitmentnDate");

	    recTransactionData.setRecruitmentDate(joiningDate);
	    emp.setRecruitmentDate(joiningDate);

	    if (recTransactionData.getFirstRecruitmentDate() == null) {
		recTransactionData.setFirstRecruitmentDate(joiningDate);
		emp.setFirstRecruitmentDate(joiningDate);
	    }

	    emp.setPromotionDueDate(PromotionsService.calculateNewPromotionDueDate(recTransactionData.getRankId().longValue(), emp.getCategoryId().longValue(), recTransactionData.getRecruitmentDate(), recTransactionData.getFirstRecruitmentDate(), recTransactionData.getLastPromotionDate(), emp.getGender(), recTransactionData.getSeniorityDays(), recTransactionData.getSeniorityMonths()));
	    emp.setServiceTerminationDueDate(TerminationsService.calculateEmpTerminationDueDate(emp.getCategoryId().longValue(), recTransactionData.getRankId().longValue(), emp.getBirthDate()));
	}

	recTransactionData.setJoiningDate(joiningDate);
	if (recTransactionData.getRecruitmentType() == FlagsEnum.ON.getCode()) {
	    emp.setRecruitmentJoiningDate(joiningDate);
	}

	recTransactionData.getRecruitmentTransaction().setSystemUser(loginUserId + ""); // For auditing.
	emp.getEmployee().setSystemUser(loginUserId + ""); // For auditing.

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(recTransactionData.getRecruitmentTransaction(), session);
	    DataAccess.updateEntity(emp.getEmployee(), session);
	    JobData job = JobsService.getJobById(recTransactionData.getJobId());
	    EmployeeLog log = new EmployeeLog.Builder().setJobId(recTransactionData.getJobId()).setDegreeId(recTransactionData.getDegreeId()).setRankId(recTransactionData.getRankId()).setRankTitleId(recTransactionData.getRankTitleId()).setSocialStatus(emp.getSocialStatus()).setOfficialUnitId(emp.getOfficialUnitId()).setGeneralSpecialization(emp.getGeneralSpecialization()).setPhysicalUnitId(emp.getPhysicalUnitId())
		    .setBasicJobNameId(job.getBasicJobNameId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), recTransactionData.getDecisionNumber(), recTransactionData.getDecisionDate(), joiningDate, DataAccess.getTableName(RecruitmentTransaction.class)).build();
	    LogService.logEmployeeData(log, session);

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
     * Handles recruitment transactions effects
     * 
     * @param recruitmentTransactions
     *            recruitment transactions data list
     * @param processName
     *            process name of the workflow that created the recruitment transactions
     * @param session
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any error occurs
     */
    public static void handleRecRequests(List<RecruitmentTransactionData> recruitmentTransactions, String processName, CustomSession session) throws BusinessException {
	try {
	    if (recruitmentTransactions != null && recruitmentTransactions.size() > 0) {

		EmployeeData employee = EmployeesService.getEmployeeData(recruitmentTransactions.get(0).getEmployeeId());
		if (employee == null)
		    throw new BusinessException("error_noEmployeeDataForThisId", new Object[] { recruitmentTransactions.get(0).getEmployeeId() });

		RecruitmentsService.addRecruitmentTransactions(recruitmentTransactions, processName, session);

		for (RecruitmentTransactionData recruitmentTransaction : recruitmentTransactions) {
		    JobData job = JobsService.getJobById(recruitmentTransaction.getJobId());
		    EmployeeData emp = EmployeesService.getEmployeeData(recruitmentTransaction.getEmployeeId());
		    recruitmentTransaction.setEmployeeName(emp.getName());
		    JobsService.changeJobStatus(job, JobStatusEnum.OCCUPIED.getCode(), session);

		    if (recruitmentTransaction.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && recruitmentTransaction.getRecruitmentType().equals(RecruitmentTypeEnum.GRADUATION_LETTER.getCode())) {
			JobData oldTrainingJob = JobsService.getJobById(emp.getJobId());
			JobsService.changeJobStatus(oldTrainingJob, JobStatusEnum.VACANT.getCode(), session);
		    }

		    EmployeeQualificationsData employeeQualificationsData = null;

		    if (recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode()) {
			emp.setStatusId(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode());

		    } else {
			emp.setStatusId(EmployeeStatusEnum.ON_DUTY.getCode());
			emp.setPhysicalUnitId(job.getUnitId());

			employeeQualificationsData = EmployeesService.getEmployeeQualificationsByEmpId(emp.getEmpId());
			if (employeeQualificationsData == null)
			    throw new BusinessException("error_noQualDataForEmployee", new Object[] { emp.getName() });

			employeeQualificationsData.setCurGraduationPlaceDetailId(employeeQualificationsData.getRecGraduationPlaceDetailId());
			employeeQualificationsData.setCurGraduationYear(employeeQualificationsData.getRecGraduationYear());
			employeeQualificationsData.setCurQualLevelId(employeeQualificationsData.getRecQualLevelId());
			employeeQualificationsData.setCurQualMinorSpecId(employeeQualificationsData.getRecQualMinorSpecId());
			employeeQualificationsData.setCurQualPercentage(employeeQualificationsData.getRecQualPercentage());
			employeeQualificationsData.setCurStudyCountryId(employeeQualificationsData.getRecStudyCountryId());
		    }

		    if (emp.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode()) {
			emp.setRankId(recruitmentTransaction.getRankId());
			emp.setRecruitmentRankId(recruitmentTransaction.getRankId());
			emp.setRankTitleId(recruitmentTransaction.getRankTitleId());
			emp.setJobModifiedFlag(FlagsEnum.ON.getCode());
		    } else {
			emp.setRankId(job.getRankId());
			emp.setRecruitmentRankId(job.getRankId());
		    }

		    emp.setDegreeId(recruitmentTransaction.getDegreeId());

		    emp.setJobId(job.getId());

		    emp.setOfficialUnitId(job.getUnitId());

		    if (recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RECRUITMENT.getCode()) {
			emp.setRecruitmentDate(recruitmentTransaction.getRecruitmentDate());
			emp.setFirstRecruitmentDate(recruitmentTransaction.getFirstRecruitmentDate());

			if (recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode())
			    emp.setRecruitmentJoiningDate(recruitmentTransaction.getRecruitmentDate());

			// adjust employee promotion due date
			if (recruitmentTransaction.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || (recruitmentTransaction.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && (recruitmentTransaction.getRecruitmentClass() != RecruitmentClassesEnum.INSPECTORS_RECRUITMENT.getCode() && recruitmentTransaction.getRecruitmentClass() != RecruitmentClassesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode()))) {
			    emp.setPromotionDueDate(PromotionsService.calculateNewPromotionDueDate(recruitmentTransaction.getRankId().longValue(), emp.getCategoryId().longValue(), recruitmentTransaction.getRecruitmentDate(), recruitmentTransaction.getFirstRecruitmentDate(), recruitmentTransaction.getLastPromotionDate(), emp.getGender(), recruitmentTransaction.getSeniorityDays(), recruitmentTransaction.getSeniorityMonths()));
			    emp.setServiceTerminationDueDate(TerminationsService.calculateEmpTerminationDueDate(recruitmentTransaction.getCategoryId().longValue(), recruitmentTransaction.getRankId().longValue(), emp.getBirthDate()));
			}
			emp.setRecruitmentDecisionDate(recruitmentTransaction.getDecisionDate());
			emp.setRecruitmentDecisionNumber(recruitmentTransaction.getDecisionNumber());

			// adding last promotion Date to employee data
			emp.setLastPromotionDate(recruitmentTransaction.getLastPromotionDate());

			if (emp.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
			    emp.setSequenceNumber(DataAccess.getNextValFromSequence(SequenceNamesEnum.SOLDIERS_SEQUENCE_NUMBER.getCode()));
		    }

		    if (employeeQualificationsData == null)
			EmployeesService.updateEmployee(emp, session);
		    else
			EmployeesService.updateEmployeeAndHisQualifications(emp, employeeQualificationsData, session);
		    if (recruitmentTransaction.getRecruitmentDate() == null)
			recruitmentTransaction.setRecruitmentDate(HijriDateService.gregToHijriDate(new Date()));
		    if (recruitmentTransaction.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || recruitmentTransaction.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
			EmployeeLog log = new EmployeeLog.Builder().setJobId(recruitmentTransaction.getJobId()).setDegreeId(recruitmentTransaction.getDegreeId()).setRankId(recruitmentTransaction.getRankId()).setRankTitleId(recruitmentTransaction.getRankTitleId()).setSocialStatus(emp.getSocialStatus()).setOfficialUnitId(emp.getOfficialUnitId()).setGeneralSpecialization(emp.getGeneralSpecialization())
				.setPhysicalUnitId((emp.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && emp.getPhysicalUnitId() == null) ? emp.getOfficialUnitId() : emp.getPhysicalUnitId())
				.setBasicJobNameId(job.getBasicJobNameId()).constructCommonFields(emp.getEmpId(), FlagsEnum.ON.getCode(), recruitmentTransaction.getDecisionNumber(), recruitmentTransaction.getDecisionDate(), recruitmentTransaction.getRecruitmentDate(), DataAccess.getTableName(RecruitmentTransaction.class)).build();
			LogService.logEmployeeData(log, session);
		    }
		}
		if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode())) {
		    doPayrollIntegration(recruitmentTransactions, FlagsEnum.OFF.getCode(), session);
		}
	    }
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void doPayrollIntegration(List<RecruitmentTransactionData> recruitmentTransactions, Integer resendFlag, CustomSession session) throws BusinessException {
	Long adminDecisionId = null;
	if (recruitmentTransactions.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
	    if (recruitmentTransactions.get(0).getRecruitmentType().equals(RecruitmentTypeEnum.RECRUITMENT.getCode())) {
		adminDecisionId = AdminDecisionsEnum.OFFICERS_RECRUITMENT.getCode();
	    } else if (recruitmentTransactions.get(0).getRecruitmentType().equals(RecruitmentTypeEnum.RE_RECRUITMENT.getCode())) {
		adminDecisionId = AdminDecisionsEnum.OFFICERS_RE_RECRUITMENT.getCode();
	    } else if (recruitmentTransactions.get(0).getRecruitmentType().equals(RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode())) {
		adminDecisionId = AdminDecisionsEnum.OFFICERS_RECRUITMENT_BY_EXTERNAL_MOVE.getCode();
	    }
	}
	if (adminDecisionId != null) {
	    session.flushTransaction();
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>();
	    EmployeeData emp = null;
	    for (RecruitmentTransactionData recruitmentTransactionData : recruitmentTransactions) {
		String gregRecDateString = HijriDateService.hijriToGregDateString(recruitmentTransactionData.getRecruitmentDateString());
		emp = EmployeesService.getEmployeeData(recruitmentTransactionData.getEmployeeId());
		adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(recruitmentTransactionData.getEmployeeId(), emp.getName(), recruitmentTransactionData.getId(), null, gregRecDateString, null, recruitmentTransactionData.getDecisionNumber(), null));
	    }
	    String gregRecDateString = HijriDateService.hijriToGregDateString(recruitmentTransactions.get(0).getRecruitmentDateString());
	    String gregDecDateString = HijriDateService.hijriToGregDateString(recruitmentTransactions.get(0).getDecisionDateString());
	    PayrollEngineService.doPayrollIntegration(adminDecisionId, recruitmentTransactions.get(0).getCategoryId(), gregRecDateString, adminDecisionEmployeeDataList, recruitmentTransactions.get(0).getAttachments(),
		    recruitmentTransactions != null && recruitmentTransactions.size() > 0 && recruitmentTransactions.get(0).getTransUnitFullName() != null ? UnitsService.getUnitByExactFullName(recruitmentTransactions.get(0).getTransUnitFullName()).getId() : UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId(), gregDecDateString, DataAccess.getTableName(RecruitmentTransaction.class), resendFlag, FlagsEnum.OFF.getCode(), session);

	}
    }

    public static void payrollIntegrationFailureHandle(String decisionNumber, Date decisionDate, CustomSession session) throws BusinessException {
	List<RecruitmentTransactionData> recruitmentTransactionDataList = getRecruitmentTransactionsByDecisionNumberAndDecisionDate(decisionNumber, decisionDate, decisionDate, null);
	if (recruitmentTransactionDataList != null && recruitmentTransactionDataList.size() != 0) {
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(recruitmentTransactionDataList, FlagsEnum.ON.getCode(), session);
	} else {
	    throw new BusinessException("error_transactionDataRetrievingError");
	}
    }

    /**
     * Distributes soldiers on region considering their wishes, region needs
     * 
     * @throws BusinessException
     *             if any error occurs
     */
    public static void distributeSoldiers() throws BusinessException {
	CustomSession session = DataAccess.getSession();

	if (EmployeesService.countSoldiersForRecruitment(FlagsEnum.ON.getCode()) > 0)
	    throw new BusinessException("error_previousDistributionAlreadyexists");

	long soldiersNeedToBeDistributedCount = EmployeesService.countSoldiersForRecruitment(FlagsEnum.OFF.getCode());
	if (soldiersNeedToBeDistributedCount == 0)
	    throw new BusinessException("error_noSoldiersToDistribute");

	if (soldiersNeedToBeDistributedCount != countRegionsNeeds())
	    throw new BusinessException("error_soldiersNumbersAreNotSuitableToRegionsNeeds");

	if (!checkSoldiersAndRegionsNeedsCount())
	    throw new BusinessException("error_soldiersNumbersAreNotSuitableToRegionsNeeds");

	List<RecruitmentDistributionData> distributionsList = RecruitmentsService.getAllRecruitmentDistributions();
	Map<String, RecruitmentDistributionData> distributionsMap = new HashMap<String, RecruitmentDistributionData>();
	for (RecruitmentDistributionData distribution : distributionsList)
	    distributionsMap.put(distribution.getRegionId().toString() + distribution.getRankId().toString() + distribution.getMinorSpecializationId().toString(), distribution);

	List<Object> recruitmentWishEmpObjectsList = (ArrayList<Object>) RecruitmentsService.getAllNonDistributedSoldiersWishes();

	try {
	    session.beginTransaction();

	    for (Object recruitmentWishEmpObject : recruitmentWishEmpObjectsList) {
		RecruitmentWishData recWish = (RecruitmentWishData) ((Object[]) recruitmentWishEmpObject)[0];
		EmployeeData emp = (EmployeeData) ((Object[]) recruitmentWishEmpObject)[1];
		Long[] regionsIds = recWish.getRegionsIds();
		String temp = recWish.getEmpRecRankId().toString() + recWish.getEmpRecMinorSpecId().toString();
		for (int i = 0; i < regionsIds.length; i++) {
		    RecruitmentDistributionData distribution = distributionsMap.get(regionsIds[i].toString() + temp);
		    if (distribution != null && distribution.getCount() > 0) {
			distribution.setCount(distribution.getCount() - 1);
			emp.setRecruitmentRegionId(distribution.getRegionId());
			EmployeesService.updateEmployee(emp, session);
			break;
		    }
		}
	    }

	    session.commitTransaction();

	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} finally {
	    session.close();
	}
    }

    /* ------------------ Validations ----------------- */
    /**
     * Validates business rules for a recuitment transaction
     * 
     * @param recruitmentDate
     *            recruitment data of the recruitment transaction
     * @param firstRecruitmentDate
     *            first recruitment date of the recruitment transaction
     * @param lastPromotionDate
     *            last promotion of the recruitment transaction
     * @param basedOnOrderDate
     *            based on oder date of the recruitment transaction
     * @param jobId
     *            job id of the recruitment transaction
     * @param employeeId
     *            employee id of the recruitment transaction
     * @throws BusinessException
     *             if any error occurs
     */
    public static void validateBusinessRules(Date recruitmentDate, Date firstRecruitmentDate, Date lastPromotionDate, Date basedOnOrderDate, Long jobId) throws BusinessException {

	if (firstRecruitmentDate != null && recruitmentDate != null && recruitmentDate.before(firstRecruitmentDate))
	    throw new BusinessException("error_firstRecDateBeforeOrEqualsRecDate");
	if (lastPromotionDate != null && firstRecruitmentDate != null && !lastPromotionDate.after(firstRecruitmentDate))
	    throw new BusinessException("error_lastPromotionDateMustBeAfterFirstRecruitmentDate");
	if (lastPromotionDate != null && recruitmentDate != null && !lastPromotionDate.before(recruitmentDate))
	    throw new BusinessException("error_lastPromotionDateMustBeBeforeRecruitmentDate");

	if (basedOnOrderDate != null && basedOnOrderDate.after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_incorrectBasedOnOrderDate");

	if (recruitmentDate != null && recruitmentDate.after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_invalidRecruitmentDate");

	if (jobId != null) {
	    JobData job = JobsService.getJobById(jobId);
	    if (job == null)
		throw new BusinessException("error_transactionDataError");

	    if (job.getStatus().intValue() != JobStatusEnum.VACANT.getCode())
		throw new BusinessException("error_recAllowedOnVacantJobsOnly");
	}
    }

    /**
     * Main method to validate list of movement transactions(validate mandatory fields, hijri dates and business rules)
     * 
     * @param recruitmentTransactions
     *            list of recruitment transactions data
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateRecruitmentTransactions(List<RecruitmentTransactionData> recruitmentTransactions) throws BusinessException {
	validateMandatoryFields(recruitmentTransactions);
	validateHijriDates(recruitmentTransactions);
	validateBusinessRules(recruitmentTransactions);
    }

    /**
     * Validates mandatory fields for list of recruitment transactions
     * 
     * @param recruitmentTransactions
     *            list of recruimtent transcations data
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateMandatoryFields(List<RecruitmentTransactionData> recruitmentTransactions) throws BusinessException {
	for (RecruitmentTransactionData recruitmentTransaction : recruitmentTransactions) {
	    if (recruitmentTransaction.getBasedOnOrderNumber() == null || recruitmentTransaction.getBasedOnOrderNumber().length() == 0)
		throw new BusinessException("error_basedOnOrderNumberMandatory");
	    if (recruitmentTransaction.getBasedOnOrderDate() == null)
		throw new BusinessException("error_basedOnOrderDateMandatory");
	    if (recruitmentTransaction.getCategoryId() == null)
		throw new BusinessException("error_categoryMandatory");
	    if (recruitmentTransaction.getEmployeeId() == null)
		throw new BusinessException("error_empSelectionManadatory");
	    if (recruitmentTransaction.getJobId() == null || recruitmentTransaction.getTransJobCode() == null || recruitmentTransaction.getTransJobCode().length() == 0 || recruitmentTransaction.getTransJobName() == null || recruitmentTransaction.getTransJobName().length() == 0)
		throw new BusinessException("error_jobIsMandatory");
	    if (recruitmentTransaction.getTransUnitFullName() == null || recruitmentTransaction.getTransUnitFullName().length() == 0)
		throw new BusinessException("error_unitIsMandatory");
	    if (recruitmentTransaction.getRankId() == null)
		throw new BusinessException("error_rankIsMandatory");
	    if (recruitmentTransaction.getCategoryId().longValue() != CategoriesEnum.OFFICERS.getCode() && recruitmentTransaction.getDegreeId() == null)
		throw new BusinessException("error_degreeMandatory");

	    if (recruitmentTransaction.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || (recruitmentTransaction.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && !recruitmentTransaction.getRecruitmentClass().equals(RecruitmentClassesEnum.INSPECTORS_RECRUITMENT.getCode()) && !recruitmentTransaction.getRecruitmentClass().equals(RecruitmentClassesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode()))) {

		if (recruitmentTransaction.getRecruitmentDate() == null)
		    throw new BusinessException("error_recDateMandatory");

		if (recruitmentTransaction.getRecruitmentType().equals(RecruitmentTypeEnum.RECRUITMENT.getCode()) && recruitmentTransaction.getFirstRecruitmentDate() == null)
		    throw new BusinessException("error_firstRecDateMandatory");
	    }

	    if (recruitmentTransaction.getSeniorityDays() == null)
		throw new BusinessException("error_seniorityDaysMandatory");
	    if (recruitmentTransaction.getSeniorityMonths() == null)
		throw new BusinessException("error_seniorityMonthsMandatory");
	    if (recruitmentTransaction.getDecisionApprovedId() == null)
		throw new BusinessException("error_decisionApprovedMandatory");
	    if (recruitmentTransaction.getTransMinorSpecializationDesc() == null || recruitmentTransaction.getTransMinorSpecializationDesc().length() == 0)
		throw new BusinessException("error_minorSpecializationIsMandatory");
	    if (recruitmentTransaction.getAttachments() == null && (recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() ||
		    recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode() ||
		    recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() ||
		    recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode() ||
		    recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode() ||
		    recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode() ||
		    recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode() ||
		    recruitmentTransaction.getRecruitmentClass() == RecruitmentClassesEnum.INSPECTORS_RECRUITMENT.getCode()))
		throw new BusinessException("error_attachmentsMissing");
	}
    }

    /**
     * Validates busines rules for list of recruimtent transactions
     * 
     * @param recruitmentTransactions
     *            list of recruimtent transactions data
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateBusinessRules(List<RecruitmentTransactionData> recruitmentTransactions) throws BusinessException {
	int graduationGroupNumber = 0;
	int graduationGroupPlace = 0;

	if (recruitmentTransactions.size() > 0 && recruitmentTransactions.get(0).getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
	    graduationGroupNumber = recruitmentTransactions.get(0).getGraduationGroupNumber();
	    graduationGroupPlace = recruitmentTransactions.get(0).getGraduationGroupPlaceId();
	}
	HashSet<Long> employeesIDs = new HashSet<Long>();
	HashSet<Long> jobsIDs = new HashSet<Long>();
	for (int i = 0; i < recruitmentTransactions.size(); i++) {
	    if (!employeesIDs.add(recruitmentTransactions.get(i).getEmployeeId()))
		throw new BusinessException("error_recruitmentEmployeeRepeated");
	    if (!jobsIDs.add(recruitmentTransactions.get(i).getJobId()))
		throw new BusinessException("error_recruitmentJobRepeated");
	    if (recruitmentTransactions.get(i).getCategoryId() == CategoriesEnum.OFFICERS.getCode() && (graduationGroupNumber != recruitmentTransactions.get(i).getGraduationGroupNumber() || graduationGroupPlace != recruitmentTransactions.get(i).getGraduationGroupPlaceId())) {
		throw new BusinessException("error_officersRecruitmentDataNotSuitable");
	    }

	    EmployeeData employee = EmployeesService.getEmployeeData(recruitmentTransactions.get(i).getEmployeeId());
	    JobData job = JobsService.getJobById(recruitmentTransactions.get(i).getJobId());
	    if (employee == null)
		throw new BusinessException("error_employeeDataError");
	    if (job == null)
		throw new BusinessException("error_transactionDataError");

	    long empStatusId = employee.getStatusId().longValue();
	    if (empStatusId != EmployeeStatusEnum.UNDER_RECRUITMENT.getCode() && empStatusId != EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode() && empStatusId != EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode() && empStatusId != EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode())
		throw new BusinessException("error_recruitmentEmployeesStatusesIncorrect");

	    if (employee.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {

		if (employee.getRecruitmentRankId().longValue() != recruitmentTransactions.get(i).getRankId().longValue())
		    throw new BusinessException("error_jobsAreNotSuitableForSoldiers");
		if (!(recruitmentTransactions.get(i).getRecruitmentType().equals(RecruitmentTypeEnum.RECRUITMENT.getCode()) && employee.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode())) && employee.getRecruitmentMinorSpecId().longValue() != job.getMinorSpecializationId().longValue())
		    throw new BusinessException("error_jobsAreNotSuitableForSoldiers");
	    }

	    validateBusinessRules(recruitmentTransactions.get(i).getRecruitmentDate(), recruitmentTransactions.get(i).getFirstRecruitmentDate(), recruitmentTransactions.get(i).getLastPromotionDate(), recruitmentTransactions.get(i).getBasedOnOrderDate(), recruitmentTransactions.get(i).getJobId());
	}
    }

    /**
     * Validates hijri dates for list of recruitment transactions
     * 
     * @param recruitmentTransactions
     *            list of recruitment transactions data
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateHijriDates(List<RecruitmentTransactionData> recruitmentTransactions) throws BusinessException {
	for (RecruitmentTransactionData recruitmentTransaction : recruitmentTransactions) {
	    if (!((recruitmentTransaction.getRecruitmentDate() == null || HijriDateService.isValidHijriDate(recruitmentTransaction.getRecruitmentDate())) && (recruitmentTransaction.getFirstRecruitmentDate() == null || HijriDateService.isValidHijriDate(recruitmentTransaction.getFirstRecruitmentDate())) && HijriDateService.isValidHijriDate(recruitmentTransaction.getBasedOnOrderDate())
		    && (recruitmentTransaction.getLastPromotionDate() == null || HijriDateService.isValidHijriDate(recruitmentTransaction.getLastPromotionDate())) && (recruitmentTransaction.getJoiningDate() == null || HijriDateService.isValidHijriDate(recruitmentTransaction.getJoiningDate()))))
		throw new BusinessException("error_invalidHijriDate");
	}
    }

    /* -------------------- Reports ------------------- */
    /**
     * Gets the report decision bytes of a transactions to be printed
     * 
     * @param transaction
     *            recruitment transactions that we need to print its report
     * @return decision bytes array
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getRecruitmentDecisionBytes(RecruitmentTransactionData transaction) throws BusinessException {
	try {
	    String reportName = "";
	    boolean isSingleReport;
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    List<RecruitmentTransactionData> transactions = getRecruitmentTransactionsByDecisionNumberAndDecisionDate(transaction.getDecisionNumber(), transaction.getDecisionDate(), transaction.getDecisionDate(), FlagsEnum.ON.getCode());
	    if (transactions.size() == 1) {
		isSingleReport = true;
	    } else {
		isSingleReport = false;
	    }
	    parameters.put("P_TRANSACTION_ID", transaction.getId());
	    if (transaction.getCategoryId().intValue() == CategoriesEnum.OFFICERS.getCode() && isSingleReport) {
		reportName = ReportNamesEnum.RECRUITMENT_DECISION_OFFICERS.getCode();
	    } else if (transaction.getCategoryId().intValue() == CategoriesEnum.OFFICERS.getCode() && !isSingleReport) {
		reportName = ReportNamesEnum.RECRUITMENT_DECISION_OFFICERS_COLLECTIVE.getCode();

	    } else if (transaction.getCategoryId().intValue() == CategoriesEnum.SOLDIERS.getCode() && isSingleReport) {
		if (transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode()) {
		    reportName = ReportNamesEnum.RECRUITMENT_DECISION_SOLIDERS.getCode();
		}
		if (transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode()) {
		    reportName = ReportNamesEnum.RECRUITMENT_DECISION_SOLDIERS_GRADUATION_LETTER.getCode();
		}
		if (transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode()) {
		    reportName = ReportNamesEnum.RECRUITMENT_DECISION_SOLDIERS_EXECPTIONAL_GRADUATION_LETTER.getCode();
		}
		if (transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode() || transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.INSPECTORS_RECRUITMENT.getCode() || transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode()
			|| transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode()) {
		    reportName = ReportNamesEnum.RECRUITMENT_DECISION_SOLIDERS_EXCEPTIONAL_AND_INSPECTORS.getCode();
		}

	    } else if (transaction.getCategoryId().intValue() == CategoriesEnum.SOLDIERS.getCode() && !isSingleReport) {

		if (transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode() || transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode()) {
		    reportName = ReportNamesEnum.RECRUITMENT_DECISION_SOLIDERS_COLLECTIVE.getCode();
		    parameters.put("P_FIRST_NAME", transactions.get(0).getEmployeeName());
		    parameters.put("P_FIRST_CODE", transactions.get(0).getTransJobCode());
		    parameters.put("P_LAST_NAME", transactions.get(transactions.size() - 1).getEmployeeName());
		    parameters.put("P_LAST_CODE", transactions.get(transactions.size() - 1).getTransJobCode());

		}
		if (transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode()) {
		    reportName = ReportNamesEnum.RECRUITMENT_DECISION_SOLDIERS_COLLECTIVE_GRADUATION_LETTER.getCode();
		    parameters.put("P_FIRST_NAME", transactions.get(0).getEmployeeName());
		    parameters.put("P_FIRST_CODE", transactions.get(0).getTransJobCode());
		    parameters.put("P_LAST_NAME", transactions.get(transactions.size() - 1).getEmployeeName());
		    parameters.put("P_LAST_CODE", transactions.get(transactions.size() - 1).getTransJobCode());

		}
		if (transaction.getRecruitmentClass().longValue() == RecruitmentClassesEnum.INSPECTORS_RECRUITMENT.getCode()) {
		    reportName = ReportNamesEnum.RECRUITMENT_DECISION_SOLDIERS_INSPECTORS_COLLECTIVE.getCode();
		}

		parameters.put("P_COUNT", transactions.size() + "");

	    } else if (transaction.getCategoryId().intValue() == CategoriesEnum.CONTRACTORS.getCode()) {
		reportName = ReportNamesEnum.RECRUITMENT_DECISION_EMPLOYEES_CONTRACTOR.getCode();

	    } else if (isSingleReport) {
		reportName = ReportNamesEnum.RECRUITMENT_DECISION_EMPLOYEES.getCode();

	    } else {
		reportName = ReportNamesEnum.RECRUITMENT_DECISION_EMPLOYEES_COLLECTIVE.getCode();

	    }

	    return getReportData(reportName, parameters);

	} catch (BusinessException e) {
	    e.printStackTrace();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Query to get decision bytes of a report that counts with status new student ranked soldiers and on duty under recruitment, That will be distributed
     * 
     * @return report decision bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getNewSoldiersCountStatementDecisionBytes() throws BusinessException {
	try {
	    return getReportData(ReportNamesEnum.RECRUITMENT_SOLDIERS_COUNT_STATEMENT.getCode(), new HashMap<String, Object>());
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Query to get decision bytes of a report that prints the data of officers that are under recruitment
     * 
     * @return report decision bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getOfficersUnderRecDataBytes(long graduationGroupNumber, long graduationGroupPlace, long recruitmentRegionId) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.RECRUITMENT_OFFICERS_UNDER_RECRUITMENT_DATA.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_GRADUATION_GROUP_NUMBER", graduationGroupNumber);
	    parameters.put("P_GRADUATION_GROUP_PLACE", graduationGroupPlace);
	    parameters.put("P_REC_REGION_ID", recruitmentRegionId);

	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Query to get decision bytes of a report that prints the data of soldiers that are under recruitment or not distributed yet
     * 
     * @return report decision bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getSoldiersUnderRecAndDistribDataBytes(List<Long> statusIds, long recRankId, long recMinorSpecId, long recRegionId, long recTrainUnitID, int distributionStatus) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.RECRUITMENT_SOLDIERS_UNDER_RECRUITMENT_AND_DISTRIBUTION_DATA.getCode();

	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String statuses = statusIds.size() == 0 ? FlagsEnum.ALL.getCode() + "" : statusIds.toString().replaceAll("\\[|\\]", "");
	    parameters.put("P_STATUS_IDS", statuses);
	    StringBuilder statusDescs = new StringBuilder();
	    if (statusIds.contains(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode())) {
		statusDescs.append(getMessage("label_newStudent")).append("/ ");
	    }
	    if (statusIds.contains(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode())) {
		statusDescs.append(getMessage("label_newStudentRankedSoldier")).append("/ ");
	    }
	    if (statusIds.contains(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode())) {
		statusDescs.append(getMessage("label_onDutyUnderRecruitment"));
	    }
	    if (statusDescs.length() != 0 && statusDescs.substring(statusDescs.length() - 2) == "/ ") {
		statusDescs.delete(statusDescs.length() - 2, statusDescs.length());
	    }

	    parameters.put("P_STATUS_DESCS", statusDescs.toString());
	    parameters.put("P_REC_RANK_ID", recRankId);
	    parameters.put("P_REC_MINOR_SPEC_ID", recMinorSpecId);
	    parameters.put("P_REC_REGION_ID", recRegionId);
	    parameters.put("P_REC_TRAINING_UNIT_ID", recTrainUnitID);
	    parameters.put("P_DIST_STATUS", distributionStatus);
	    parameters.put("P_DIST_STATUS_DESC", distributionStatus == FlagsEnum.ALL.getCode() ? getMessage("label_all") : distributionStatus == FlagsEnum.ON.getCode() ? getMessage("label_distributed") : getMessage("label_notDistributed"));
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Query to get decision bytes of a report that prints statistics of recruited soldiers
     * 
     * @return report decision bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getRecruitedSoldiersStatisticalDataBytes(long recruitmentRankId, long recruitmentSpecId, long recruitmentRegionId, String recruitmentDateFrom, String recruitmentDateTo, long recruitmentType) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.RECRUITMENT_SOLDIERS_RECRUITED_STATISTICAL_REPORT.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_REC_REGION_ID", recruitmentRegionId);
	    parameters.put("P_REC_SPEC_ID", recruitmentSpecId);
	    parameters.put("P_REC_RANK_ID", recruitmentRankId);
	    parameters.put("P_REC_DATE_FROM", recruitmentDateFrom);
	    parameters.put("P_REC_DATE_TO", recruitmentDateTo);
	    parameters.put("P_REC_TYPE", recruitmentType);
	    parameters.put("P_REC_REGION_DESC", recruitmentRegionId == -1 ? getMessage("label_all") : CommonService.getRegionById(recruitmentRegionId).getDescription());
	    parameters.put("P_REC_SPEC_DESC", recruitmentSpecId == -1 ? getMessage("label_all") : SpecializationsService.getMinorSpecializationsDescStringByIdsString(recruitmentSpecId + ""));
	    parameters.put("P_REC_RANK_DESC", recruitmentRankId == -1 ? getMessage("label_all") : CommonService.getRankById(recruitmentRankId).getDescription());
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Query to get decision bytes of a report that prints a detailed data of recruited soldiers
     * 
     * @return report decision bytes
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getRecruitedSoldiersDetailedDataBytes(long recruitmentRankId, long recruitmentSpecId, long recruitmentRegionId, String recruitmentDateFrom, String recruitmentDateTo, long recruitmentType) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.RECRUITMENT_SOLDIERS_RECRUITED_DETAILED_REPORT.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_REC_REGION_ID", recruitmentRegionId);
	    parameters.put("P_REC_SPEC_ID", recruitmentSpecId);
	    parameters.put("P_REC_RANK_ID", recruitmentRankId);
	    parameters.put("P_REC_DATE_FROM", recruitmentDateFrom);
	    parameters.put("P_REC_DATE_TO", recruitmentDateTo);
	    parameters.put("P_REC_TYPE", recruitmentType);
	    parameters.put("P_REC_REGION_DESC", recruitmentRegionId == -1 ? getMessage("label_all") : CommonService.getRegionById(recruitmentRegionId).getDescription());
	    parameters.put("P_REC_SPEC_DESC", recruitmentSpecId == -1 ? getMessage("label_all") : SpecializationsService.getMinorSpecializationsDescStringByIdsString(recruitmentSpecId + ""));
	    parameters.put("P_REC_RANK_DESC", recruitmentRankId == -1 ? getMessage("label_all") : CommonService.getRankById(recruitmentRankId).getDescription());
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /* -------------------- Queries ------------------- */
    /**
     * Wrapper for {@link #searchRecruitmentTransactions(Long[], long, String, String, Date, Date, int, int, Date, Date, int, int)}. Returns only one record of recruitment transaction data for each (decision number and decision date), using the following search paramters
     * 
     * @param categoryId
     * @param employeeId
     * @param decisionNumber
     * @param basedOnOrderNumber
     * @param decisionDateFrom
     * @param decisionDateTo
     * @param graduationGroupNumber
     * @param graduationGroupPlace
     * @return list of recruitment transactions
     * @throws BusinessException
     *             if any error occurs
     * @see #searchRecruitmentTransactions(Long[], long, String, String, Date, Date, int, int, Date, Date, int, int)
     */
    public static List<RecruitmentTransactionData> getRecruitmentTransactions(Long[] categoriesIds, long employeeId, String decisionNumber, String basedOnOrderNumber, Date decisionDateFrom, Date decisionDateTo, int graduationGroupNumber, int graduationGroupPlace, long regionId, Integer[] recruitmentTypes) throws BusinessException {
	return searchRecruitmentTransactions(categoriesIds, employeeId, decisionNumber, basedOnOrderNumber, decisionDateFrom, decisionDateTo, graduationGroupNumber, graduationGroupPlace, null, null, regionId, FlagsEnum.ON.getCode(), recruitmentTypes);
    }

    /**
     * Wrapper for {@link #searchRecruitmentTransactions(Long[], long, String, String, Date, Date, int, int, Date, Date, int, int)}. Returns only one record of recruitment transaction for each (employee id, recruitment date from and recruitment date to)
     * 
     * @param employeeId
     * @param recruitmentDateFrom
     * @param recruitmentDateTo
     * @return list of recruitment transactions
     * @throws BusinessException
     *             if any error occurs
     * @see #searchRecruitmentTransactions(Long[], long, String, String, Date, Date, int, int, Date, Date, int, int)
     */
    public static List<RecruitmentTransactionData> getRecruitmentTransactionsByEmployeeIdAndRecDateAndRecTypes(long employeeId, Date recruitmentDateFrom, Date recruitmentDateTo, Integer[] recruitmentTypes) throws BusinessException {
	return searchRecruitmentTransactions(null, employeeId, null, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), recruitmentDateFrom, recruitmentDateTo, FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode(), recruitmentTypes);
    }

    /**
     * Wrapper for {@link #searchRecruitmentTransactions(Long[], long, String, String, Date, Date, int, int, Date, Date, int, int)}. Used in employee file to get employee recruitment transactions history
     * 
     * @param employeeId
     * @return list of recruitment transactions
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<RecruitmentTransactionData> getRecruitmentTransactionsHistory(long employeeId, Integer[] recruitmentTypes) throws BusinessException {
	return searchRecruitmentTransactions(null, employeeId, null, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), recruitmentTypes);
    }

    /**
     * Query that return For each (decision number and decision date), one record for single recruitment transactions and multiple records for multiple recruitment transactions
     * 
     * @param decisionNumber
     * @param decisionDateFrom
     * @param decisionDateTo
     * @return list of recruitment transaction data object
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<RecruitmentTransactionData> getRecruitmentTransactionsByDecisionNumberAndDecisionDate(String decisionNumber, Date decisionDateFrom, Date decisionDateTo, Integer eflag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    qParams.put("P_E_FLAG", (eflag == null) ? FlagsEnum.ALL.getCode() : eflag);
	    if (decisionDateFrom != null) {
		qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriDateString(decisionDateFrom));
	    } else {
		qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (decisionDateFrom != null) {
		qParams.put("P_DECISION_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriDateString(decisionDateFrom));
	    } else {
		qParams.put("P_DECISION_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    return DataAccess.executeNamedQuery(RecruitmentTransactionData.class, QueryNamesEnum.HCM_GET_RECRUITMENT_TRANSACTIONS_BY_DECISION_NUMBER_AND_DECISION_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /**
     * Query to get recruitment transaction by id
     * 
     * @param id
     *            recruitment transaction id
     * @return
     * @throws BusinessException
     *             if any error occurs
     */
    public static RecruitmentTransactionData getRecruitmentTransactionById(long id) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRANS_ID", id);
	    List<RecruitmentTransactionData> result = DataAccess.executeNamedQuery(RecruitmentTransactionData.class, QueryNamesEnum.HCM_GET_RECRUITMENT_TRANSACTIONS_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<RecruitmentTransaction> getRecruitmentTransactionsByEmployeeIdAndRecDate(Long employeeId, Date recruitmentDateFrom, Date recruitmentDateTo) throws BusinessException {
	try {
	    return getRecruitmentTransactionsByEmployeeIdAndRecDateAndRecTypes(employeeId, recruitmentDateFrom, recruitmentDateTo, new Integer[] { RecruitmentTypeEnum.RECRUITMENT.getCode(), RecruitmentTypeEnum.RE_RECRUITMENT.getCode(), RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode() });
	} catch (BusinessException e) {
	    throw new BusinessException("error_general");
	}
    }

    private static List<RecruitmentTransaction> getRecruitmentTransactionsByEmployeeIdAndRecDateAndRecTypes(Long employeeId, Date recruitmentDateFrom, Date recruitmentDateTo, Integer[] recruitmentTypes) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    if (recruitmentDateFrom != null) {
		qParams.put("P_RECRUITMENT_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RECRUITMENT_DATE_FROM", HijriDateService.getHijriDateString(recruitmentDateFrom));
	    } else {
		qParams.put("P_RECRUITMENT_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RECRUITMENT_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (recruitmentDateTo != null) {
		qParams.put("P_RECRUITMENT_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RECRUITMENT_DATE_TO", HijriDateService.getHijriDateString(recruitmentDateTo));
	    } else {
		qParams.put("P_RECRUITMENT_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RECRUITMENT_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    if (recruitmentTypes == null) {
		qParams.put("P_RECRUITMENT_TYPE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RECRUITMENT_TYPES", new Integer[] { FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_RECRUITMENT_TYPE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RECRUITMENT_TYPES", recruitmentTypes);
	    }

	    return DataAccess.executeNamedQuery(RecruitmentTransaction.class, QueryNamesEnum.HCM_GET_RECRUITMENT_TRANSACTIONS_BY_EMPLOYEE_ID_AND_REC_DATE_AND_REC_TYPES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /**
     * Query to search for recruitment transactions data using the following search parameters
     * 
     * @param categoriesIds
     * @param employeeId
     * @param decisionNumber
     * @param basedOnOrderNumber
     * @param decisionDateFrom
     * @param decisionDateTo
     * @param graduationGroupNumber
     * @param graduationGroupPlace
     * @param recruitmentDateFrom
     * @param recruitmentDateTo
     * @param etrFlag
     *            electronic flag
     * @param recruitmentType
     * @return list of recruitment transaction data object
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<RecruitmentTransactionData> searchRecruitmentTransactions(Long[] categoriesIds, long employeeId, String decisionNumber, String basedOnOrderNumber, Date decisionDateFrom, Date decisionDateTo, int graduationGroupNumber, int graduationGroupPlace, Date recruitmentDateFrom, Date recruitmentDateTo, long regionId, int etrFlag, Integer[] recruitmentTypes) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_ETR_FLAG", etrFlag);
	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    qParams.put("P_BASED_ON_ORDER_NUMBER", (basedOnOrderNumber == null || basedOnOrderNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : basedOnOrderNumber);
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

	    if (recruitmentDateFrom != null) {
		qParams.put("P_RECRUITMENT_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RECRUITMENT_DATE_FROM", HijriDateService.getHijriDateString(recruitmentDateFrom));
	    } else {
		qParams.put("P_RECRUITMENT_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RECRUITMENT_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (recruitmentDateTo != null) {
		qParams.put("P_RECRUITMENT_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RECRUITMENT_DATE_TO", HijriDateService.getHijriDateString(recruitmentDateTo));
	    } else {
		qParams.put("P_RECRUITMENT_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RECRUITMENT_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_GRADUATION_GROUP_NUMBER", graduationGroupNumber);
	    qParams.put("P_GRADUATION_GROUP_PLACE_ID", graduationGroupPlace);

	    if (recruitmentTypes == null) {
		qParams.put("P_RECRUITMENT_TYPE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RECRUITMENT_TYPES", new Integer[] { FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_RECRUITMENT_TYPE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RECRUITMENT_TYPES", recruitmentTypes);
	    }

	    return DataAccess.executeNamedQuery(RecruitmentTransactionData.class, QueryNamesEnum.HCM_SEARCH_RECRUITMENT_TRANSACTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /************************************************ Migration methods *****************************************/
    /**
     * Migrates recruitment wishes and evaluation degrees from admission system
     * 
     * @param wsRecruitmentWishes
     *            list of recruitment wishes and evaluation degress
     * @throws BusinessException
     *             if any error occurs
     */
    public static void migrateRecruitmentWishes(List<RecruitmentWishData> recWishes, Map<Long, EmployeeData> empsHashMap) throws BusinessException {
	if (recWishes != null && recWishes.size() > 0) {
	    validateMigratedRecWishes(recWishes, empsHashMap);

	    CustomSession session = DataAccess.getSession();

	    try {
		session.beginTransaction();

		for (RecruitmentWishData recWish : recWishes) {
		    DataAccess.addEntity(recWish.getRecruitmentWish(), session);
		    recWish.setId(recWish.getRecruitmentWish().getId());
		}

		EmployeesService.deleteSoldiersDistribution(null, session);

		session.commitTransaction();
	    } catch (Exception e) {
		session.rollbackTransaction();

		for (RecruitmentWishData recWish : recWishes) {
		    recWish.setId(null);
		}

		if (e instanceof BusinessException)
		    throw (BusinessException) e;

		e.printStackTrace();
		throw new BusinessException("error_general");

	    } finally {
		session.close();
	    }
	}
    }

    private static void validateMigratedRecWishes(List<RecruitmentWishData> migratedRecWishesData, Map<Long, EmployeeData> empsHashMap) throws BusinessException {

	HashSet<Long> empsIdsSet = new HashSet<Long>();
	HashSet<String> degreesSet = new HashSet<String>();

	for (RecruitmentWishData migratedRecWishData : migratedRecWishesData) {
	    validateRecruitmentWishMandatoryFields(migratedRecWishData);

	    if (!empsHashMap.get(migratedRecWishData.getEmpId()).getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode()) && !empsHashMap.get(migratedRecWishData.getEmpId()).getStatusId().equals(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode())) {
		throw new BusinessException("error_recWSErrorMessage", new Object[] { empsHashMap.get(migratedRecWishData.getEmpId()).getSocialID(), EmployeesService.getEmployeeFullName(empsHashMap.get(migratedRecWishData.getEmpId())), getMessage("error_employeeStatusForRecWishInvalid") });
	    }

	    HashSet<Long> empRegionWishSet = new HashSet<Long>();

	    empRegionWishSet.add(migratedRecWishData.getRegionsFirstWishId());

	    if (!empRegionWishSet.add(migratedRecWishData.getRegionsSecondWishId()) || !empRegionWishSet.add(migratedRecWishData.getRegionsThirdWishId()) || !empRegionWishSet.add(migratedRecWishData.getRegionsFourthWishId()) || !empRegionWishSet.add(migratedRecWishData.getRegionsFifthWishId()) || !empRegionWishSet.add(migratedRecWishData.getRegionsSixthWishId()) || !empRegionWishSet.add(migratedRecWishData.getRegionsSeventhWishId())
		    || !empRegionWishSet.add(migratedRecWishData.getRegionsEighthWishId()) || !empRegionWishSet.add(migratedRecWishData.getRegionsNinthWishId())
		    || !empRegionWishSet.add(migratedRecWishData.getRegionsTenthWishId()) || !empRegionWishSet.add(migratedRecWishData.getRegionsEleventhWishId()))
		throw new BusinessException("error_recWSErrorMessage", new Object[] { empsHashMap.get(migratedRecWishData.getEmpId()).getSocialID(), EmployeesService.getEmployeeFullName(empsHashMap.get(migratedRecWishData.getEmpId())), getMessage("error_recruitmentWishUniquenessViolation") });

	    if (!empsIdsSet.add(migratedRecWishData.getEmpId()))
		throw new BusinessException("error_recWSErrorMessage", new Object[] { empsHashMap.get(migratedRecWishData.getEmpId()).getSocialID(), EmployeesService.getEmployeeFullName(empsHashMap.get(migratedRecWishData.getEmpId())), getMessage("error_recruitmentWishEmployeeUniquenessViolation") });

	    if (!degreesSet.add(migratedRecWishData.getEvaluationDegree().toString() + "-" + migratedRecWishData.getEmpRecMinorSpecId().toString() + "-" + migratedRecWishData.getEmpRecRankId().toString()))
		throw new BusinessException("error_recWSErrorMessage", new Object[] { empsHashMap.get(migratedRecWishData.getEmpId()).getSocialID(), EmployeesService.getEmployeeFullName(empsHashMap.get(migratedRecWishData.getEmpId())), getMessage("error_recruitmentWishEvaluationDegreeUniquenessViolation") });
	}

	List<RecruitmentWishData> allRecWishes = getRecruitmentWishes(null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());

	for (RecruitmentWishData recWish : allRecWishes) {
	    if (!degreesSet.add(recWish.getEvaluationDegree().toString() + "-" + recWish.getEmpRecMinorSpecId().toString() + "-" + recWish.getEmpRecRankId().toString()))
		throw new BusinessException("error_recruitmentWishEvaluationDegreeUniquenessViolationMigration");

	    if (!empsIdsSet.add(recWish.getEmpId())) {
		throw new BusinessException("error_recWSErrorMessage", new Object[] { empsHashMap.get(recWish.getEmpId()).getSocialID(), EmployeesService.getEmployeeFullName(empsHashMap.get(recWish.getEmpId())), getMessage("error_recruitmentWishEmployeeUniquenessViolation") });
	    }
	}
    }

    /************************************************ ReRecruitment methods *****************************************/
    public static void constructReRecruitmentTransaction(RecruitmentTransactionData recruitment, EmployeeData employee, Integer recruitmentType) {
	recruitment.setEmployeeId(employee.getEmpId());
	recruitment.setCategoryId(employee.getCategoryId());
	recruitment.setGraduationGroupNumber(employee.getGraduationGroupNumber());
	recruitment.setGraduationGroupPlaceId(employee.getGraduationGroupPlace());
	recruitment.setEmployeeMilitaryNumber(employee.getMilitaryNumber());
	recruitment.setSeniorityDays(0);
	recruitment.setSeniorityMonths(0);
	recruitment.setEtrFlag(FlagsEnum.OFF.getCode());
	recruitment.setMigrationFlag(FlagsEnum.OFF.getCode());
	recruitment.setRecruitmentType(recruitmentType);
	if (employee.getEmpId() != null) {
	    recruitment.setFirstRecruitmentDate(employee.getFirstRecruitmentDate());
	    recruitment.setRankId(employee.getRankId());
	}
    }

    public static void handleReRecruitmentRequests(RecruitmentTransactionData recruitmentTransaction, EmployeeData empData, EmployeeQualificationsData employeeQualificationsData, int existingEmployee, CustomSession... useSession) throws BusinessException {
	if (empData.getEmpId() == null)
	    empData.setInsertionDate(HijriDateService.addSubHijriDays(recruitmentTransaction.getRecruitmentDate(), -1));

	validateReRecruitmentTransaction(recruitmentTransaction, empData, employeeQualificationsData, existingEmployee);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {

	    if (!isOpenedSession)
		session.beginTransaction();

	    JobData job = JobsService.getJobById(recruitmentTransaction.getJobId());
	    if (job != null) {
		empData.setGeneralSpecialization(job.getGeneralSpecialization());
		empData.setPhysicalUnitId(job.getUnitId());
		empData.setJobId(job.getId());
		empData.setOfficialUnitId(job.getUnitId());
	    }

	    empData.setStatusId(EmployeeStatusEnum.ON_DUTY.getCode());
	    empData.setServiceTerminationDate(null);
	    empData.setLastExtensionEndDate(null);

	    empData.setDegreeId(recruitmentTransaction.getDegreeId());
	    empData.setRecruitmentDecisionDate(recruitmentTransaction.getDecisionDate());
	    empData.setRecruitmentDecisionNumber(recruitmentTransaction.getDecisionNumber());

	    if (empData.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode()) {
		empData.setRankId(recruitmentTransaction.getRankId());
		empData.setRankTitleId(recruitmentTransaction.getRankTitleId());
		empData.setJobModifiedFlag(FlagsEnum.ON.getCode());
	    } else {
		empData.setRankId(job.getRankId());
		if (empData.getEmpId() == null)
		    empData.setRecruitmentRankId(job.getRankId());
	    }
	    if (empData.getEmpId() == null) {
		empData.setFirstRecruitmentDate(recruitmentTransaction.getFirstRecruitmentDate());
	    }
	    if (recruitmentTransaction.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || (recruitmentTransaction.getCategoryId() == CategoriesEnum.SOLDIERS.getCode())) {
		empData.setServiceTerminationDueDate(TerminationsService.calculateEmpTerminationDueDate(recruitmentTransaction.getCategoryId().longValue(), recruitmentTransaction.getRankId().longValue(), empData.getBirthDate()));
	    }

	    if (empData.getEmpId() == null || empData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
		empData.setSequenceNumber(DataAccess.getNextValFromSequence(SequenceNamesEnum.SOLDIERS_SEQUENCE_NUMBER.getCode()));

	    if (recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode())
		empData.setRecruitmentDate(recruitmentTransaction.getRecruitmentDate());

	    if (empData.getEmpId() == null)
		EmployeesService.addEmployee(empData, employeeQualificationsData, session);
	    else
		EmployeesService.updateEmployee(empData, session);
	    EmployeeLog log = new EmployeeLog.Builder().setJobId(recruitmentTransaction.getJobId()).setDegreeId(recruitmentTransaction.getDegreeId()).setRankId(recruitmentTransaction.getRankId()).setRankTitleId(recruitmentTransaction.getRankTitleId()).setSocialStatus(empData.getSocialStatus()).setOfficialUnitId(empData.getOfficialUnitId()).setGeneralSpecialization(empData.getGeneralSpecialization()).setPhysicalUnitId(empData.getPhysicalUnitId())
		    .setBasicJobNameId(job.getBasicJobNameId()).constructCommonFields(empData.getEmpId(), FlagsEnum.ON.getCode(), recruitmentTransaction.getDecisionNumber(), recruitmentTransaction.getDecisionDate(), recruitmentTransaction.getRecruitmentDate(), DataAccess.getTableName(RecruitmentTransaction.class)).build();
	    LogService.logEmployeeData(log, session);
	    JobsService.changeJobStatus(job, JobStatusEnum.OCCUPIED.getCode(), session);

	    // set employee in case it did't exist before
	    recruitmentTransaction.setEmployeeId(empData.getEmpId());
	    List<RecruitmentTransactionData> recruitmentTransctions = new ArrayList<>();
	    recruitmentTransctions.add(recruitmentTransaction);
	    addRecruitmentTransactions(recruitmentTransctions, null, session);
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode())) {
		doPayrollIntegration(recruitmentTransctions, FlagsEnum.OFF.getCode(), session);
	    }
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    private static void validateReRecruitmentTransaction(RecruitmentTransactionData recruitmentTransaction, EmployeeData empData, EmployeeQualificationsData employeeQualificationsData, int existingEmployee) throws BusinessException {
	// Mandatory Fields
	if (empData == null)
	    throw new BusinessException("error_employeeDataError");
	if ((recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RE_RECRUITMENT.getCode() && empData.getEmpId() == null)
		|| (recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode() && empData.getEmpId() == null && existingEmployee == FlagsEnum.ON.getCode()))
	    throw new BusinessException("error_empSelectionManadatory");
	if (recruitmentTransaction.getCategoryId() == null)
	    throw new BusinessException("error_categoryMandatory");
	if (existingEmployee == FlagsEnum.ON.getCode() && empData.getStatusId() != EmployeeStatusEnum.SERVICE_TERMINATED.getCode() && empData.getStatusId() != EmployeeStatusEnum.MOVED_EXTERNALLY.getCode())
	    throw new BusinessException("error_empShouldBeTerminatedOrMovedExternally");
	if (recruitmentTransaction.getJobId() == null || recruitmentTransaction.getTransJobCode() == null || recruitmentTransaction.getTransJobCode().length() == 0 || recruitmentTransaction.getTransJobName() == null || recruitmentTransaction.getTransJobName().length() == 0 || recruitmentTransaction.getTransUnitFullName() == null || recruitmentTransaction.getTransUnitFullName().length() == 0 || recruitmentTransaction.getTransMinorSpecializationDesc() == null
		|| recruitmentTransaction.getTransMinorSpecializationDesc().length() == 0)
	    throw new BusinessException("error_jobIsMandatory");
	if (recruitmentTransaction.getRankId() == null)
	    throw new BusinessException("error_rankIsMandatory");
	if (recruitmentTransaction.getDegreeId() == null)
	    throw new BusinessException("error_degreeMandatory");
	if (recruitmentTransaction.getDecisionNumber() == null || recruitmentTransaction.getDecisionNumber().trim().isEmpty())
	    throw new BusinessException("error_decisionNumberMandatory");
	if (recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode()) {
	    if (recruitmentTransaction.getExternalPartyMovedFrom() == null || recruitmentTransaction.getExternalPartyMovedFrom().length() == 0)
		throw new BusinessException("error_movedFromExternalParty");
	}

	// Dates validations
	validateReRecruitmentHijriDates(recruitmentTransaction, empData);

	// Business checks
	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(empData.getEmpId() != null ? new Long[] { empData.getEmpId() } : null, new Long[] { recruitmentTransaction.getJobId() }, TransactionClassesEnum.RECRUITMENT.getCode(), false, null, null);
    }

    private static void validateReRecruitmentHijriDates(RecruitmentTransactionData recruitmentTransaction, EmployeeData empData) throws BusinessException {
	// Dates Checks

	//////// reRecuitmentDate > emp serviceTerminationDate and <= sysDate
	if (recruitmentTransaction.getRecruitmentDate() == null)
	    throw new BusinessException(recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RE_RECRUITMENT.getCode() ? "error_rerecruitmentDateMandatory" : "error_movementDateMandatory");
	else if (!HijriDateService.isValidHijriDate(recruitmentTransaction.getRecruitmentDate()))
	    throw new BusinessException("error_invalidHijriDate");
	else if (recruitmentTransaction.getRecruitmentDate().after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException(recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RE_RECRUITMENT.getCode() ? "error_rerecruitmentDateBeforeTodayDate" : "error_movementDateBeforeToday");

	if (empData.getInsertionDate() != null && empData.getInsertionDate().after(recruitmentTransaction.getRecruitmentDate() == null ? HijriDateService.getHijriSysDate() : recruitmentTransaction.getRecruitmentDate()))
	    throw new BusinessException("error_registerationDateAfterRecruitmentDate");

	if (empData.getPromotionDueDate() == null)
	    throw new BusinessException("error_promotionDateMandatory");
	else if (!HijriDateService.isValidHijriDate(empData.getPromotionDueDate()))
	    throw new BusinessException("error_invalidNewLastPromotionDateOfficers");

	if (recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode() && empData.getEmpId() == null) {
	    if (recruitmentTransaction.getFirstRecruitmentDate() == null)
		throw new BusinessException("error_firstRecDateMandatory");
	    else if (!HijriDateService.isValidHijriDate(recruitmentTransaction.getFirstRecruitmentDate()))
		throw new BusinessException("error_invalidHijriDate");
	    else if (recruitmentTransaction.getFirstRecruitmentDate().after(recruitmentTransaction.getRecruitmentDate()))
		throw new BusinessException("error_firstRecDateBeforeOrEqualsMoveDate");

	    if (!empData.getPromotionDueDate().after(recruitmentTransaction.getFirstRecruitmentDate()))
		throw new BusinessException("error_promotionDueDateAfterFirstRecruitmentDate");
	}

	if (empData.getEmpId() != null) {
	    if (empData.getLastPromotionDate() != null && !empData.getPromotionDueDate().after(empData.getLastPromotionDate()))
		throw new BusinessException("error_promotionDueDateBeforeLastPromotionDate");
	    if (empData.getLastPromotionDate() == null && !empData.getPromotionDueDate().after(empData.getRecruitmentDate()))
		throw new BusinessException("error_dueDateAfterMovementDate");
	    if (empData.getServiceTerminationDate() == null || !recruitmentTransaction.getRecruitmentDate().after(empData.getServiceTerminationDate()))
		throw new BusinessException(recruitmentTransaction.getRecruitmentType() == RecruitmentTypeEnum.RE_RECRUITMENT.getCode() ? "error_rerecruitmentDateAfterTerminationDate" : "error_movementDateAfterTerminationDate");
	}

	if (recruitmentTransaction.getDecisionDate() == null)
	    throw new BusinessException("error_decDateMandatory");
	else if (!HijriDateService.isValidHijriDate(recruitmentTransaction.getDecisionDate()))
	    throw new BusinessException("error_invalidDecisionDate");
	else if (recruitmentTransaction.getDecisionDate().after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_decDateViolation");

    }
}