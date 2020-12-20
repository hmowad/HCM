package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.payroll.PayrollSalary;
import com.code.dal.orm.hcm.promotions.PromotionEmployeeDegreeData;
import com.code.dal.orm.hcm.promotions.PromotionReport;
import com.code.dal.orm.hcm.promotions.PromotionReportData;
import com.code.dal.orm.hcm.promotions.PromotionReportDetail;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.dal.orm.hcm.promotions.PromotionSeniortyPoints;
import com.code.dal.orm.hcm.promotions.PromotionTransaction;
import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.dal.orm.hcm.promotions.RankPowerData;
import com.code.dal.orm.hcm.retirements.DisclaimerTransactionData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.log.EmployeeLog;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.CivilianReportRanksEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.PromotionCandidateStatusEnum;
import com.code.enums.PromotionMedicalTestStatusEnum;
import com.code.enums.PromotionRankDaysEnum;
import com.code.enums.PromotionReportStatusEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.ReportOutputFormatsEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.integration.webservicesclients.infosys.HCMWebService;
import com.code.integration.webservicesclients.infosys.HCMWebServiceService;
import com.code.integration.webservicesclients.promotiondrugtest.DrugsTestJMSClient;
import com.code.services.BaseService;
import com.code.services.buswfcoop.BusinessWorkflowCooperation;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.integration.PayrollEngineService;
import com.code.services.log.LogService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.util.Log4jService;

/**
 * This class consists of static methods to operate on and handle promotion services for officers, soldiers, and civilians.
 * 
 * It consists exclusively of static methods that operate on or return promotions services, "Wrappers",which return a new collection backed by a specified collection, and a few other odds and ends.
 * <p>
 * The methods of this class all throw a <tt>BusinessException</tt> if any problem occurred in sessionScope
 * <p>
 * 
 */
public class PromotionsService extends BaseService {

    // Static parameter promotion_degree of the female soldier
    // public static final int PROMOTION_DEGREE = 30;

    // private static final float SOLDIERS_QUALIFICATION_DEGREE_MAX = 18;
    // private static final float SOLDIERS_FILE_DEGREE_MIN = 10;
    // private static final float SOLDIERS_FILE_DEGREE_MAX = 20;
    // private static final float SOLDIERS_TRAINING_DEGREE_MAX = 10;

    private static final String PROMOTION_DRUGS_TEST_REQUEST_SYMBOL = "R";

    /**
     * Private constructor to prevent instantiation
     */
    private PromotionsService() {
    }

    /*----------------------------------------------------- Promotions Report  ---------------------------------------------*/
    /*------------------------------------------------------Operations------------------------------------------------------*/

    /**
     * Search for eligible employees list for promotion and add them at promotion report detail that will be associated with new report data
     * 
     * @param promotionReportData
     *            The promotion Report will be added in DB
     * @param promotionReportDetailDataList
     *            List of Report Detail that will be created from eligible employees list
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addPromotionReportAndReportDetails(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	boolean newReport = false;
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (promotionReportData.getId() == null) {
		newReport = true;
		validatePromotionReportData(promotionReportData);
		if (!promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode()) && !promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode()))
		    promotionReportDetailDataList = getPromotionDetailsForEligibleEmployees(promotionReportData, promotionReportDetailDataList);
	    }
	    addModifyPromotionReport(promotionReportData, loginEmpId, session);

	    if (promotionReportDetailDataList.size() != 0)
		addModifyPromotionReportDetails(promotionReportData.getId(), promotionReportDetailDataList, loginEmpId, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (newReport) {
		promotionReportData.setId(null);
		for (PromotionReportDetailData reportDetailData : promotionReportDetailDataList)
		    reportDetailData.setId(null);
	    }
	    throw new BusinessException(e.getMessage(), e.getParams());
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (newReport) {
		promotionReportData.setId(null);
		for (PromotionReportDetailData reportDetailData : promotionReportDetailDataList)
		    reportDetailData.setId(null);
	    }
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Add or modify promotion report data .
     * 
     * It handle also soldiers exceptional promotions report number from generated Id.
     * 
     * @param promotionReportData
     *            Promotion report Data which will be added
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addModifyPromotionReport(PromotionReportData promotionReportData, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	boolean newReportFlag = false;
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    promotionReportData.getPromotionReport().setSystemUser(loginEmpId + ""); // audit
	    if (promotionReportData.getId() != null) { // Update
		DataAccess.updateEntity(promotionReportData.getPromotionReport(), session);
	    } else {
		newReportFlag = true;
		DataAccess.addEntity(promotionReportData.getPromotionReport(), session);
		promotionReportData.setId(promotionReportData.getPromotionReport().getId());

		// this code regarding soldiers exceptional report in this case we add save id in report number
		if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode()) || promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
		    promotionReportData.setReportNumber(promotionReportData.getId().toString());
		    DataAccess.updateEntity(promotionReportData.getPromotionReport(), session);
		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (newReportFlag)
		promotionReportData.setId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * update promotion report status
     * 
     * @param promotionReportData
     *            promotion report data
     * @param status
     *            new report status
     * @param session
     *            Parameter used to access the database if no other session opened
     * @throws DatabaseException
     *             If any database exceptions or errors occurs
     */
    public static void updatePromotionReportStatus(PromotionReportData promotionReportData, long status, CustomSession session) throws DatabaseException {
	promotionReportData.setStatus(status);
	DataAccess.updateEntity(promotionReportData.getPromotionReport(), session);
    }

    public static void closePromotionReports(Long[] reportsIds, CustomSession session) throws BusinessException {
	try {
	    List<PromotionReport> promotionReportsList = searchPromotionReports(reportsIds);
	    for (PromotionReport promotionReportItr : promotionReportsList) {
		promotionReportItr.setStatus(PromotionReportStatusEnum.CLOSED.getCode());
		DataAccess.updateEntity(promotionReportItr, session);
	    }
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Wrapper for {@link #deletePromotionReport(List, PromotionReportData, long, CustomSession...)} to delete promotion report and details
     * 
     * @param PromotionReportData
     *            The promotion report data
     * @param loginEmpId
     *            Login Employee Id
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deletePromotionReport(PromotionReportData promotionReportData, long loginEmpId) throws BusinessException {

	List<PromotionReportDetailData> promotionReportDetailDataList = getPromotionReportDetailsDataByReportId(promotionReportData.getId());

	if (promotionReportData.getStatus().longValue() == PromotionReportStatusEnum.CLOSED.getCode()) {
	    if (promotionReportDetailDataList.size() == 0)
		deletePromotionReport(promotionReportDetailDataList, promotionReportData, loginEmpId);
	    else
		throw new BusinessException("error_canNotDeletePromotionReport");
	} else if (promotionReportData.getStatus().longValue() == PromotionReportStatusEnum.CURRENT.getCode()) {
	    boolean royalOrderIssuedFlag = false;
	    for (PromotionReportDetailData reportDetail : promotionReportDetailDataList) {
		if (reportDetail.getStatus().equals(PromotionCandidateStatusEnum.ROYAL_OREDER_ISSUED.getCode())) {
		    royalOrderIssuedFlag = true;
		    break;
		}
	    }
	    if (!royalOrderIssuedFlag)
		deletePromotionReport(promotionReportDetailDataList, promotionReportData, loginEmpId);
	    else
		throw new BusinessException("error_canNotDeletePromotionReport");

	} else {
	    throw new BusinessException("error_canNotDeletePromotionReport");
	}
    }

    /**
     * Delete the promotion report and its promotion report details list from DB, and audit it by login user
     * 
     * @param promotionReportDetailDataList
     *            List of promotion report detail data to be deleted.
     * @param promotionReportData
     *            The promotion report data
     * @param loginEmpId
     *            Login Employee Id
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void deletePromotionReport(List<PromotionReportDetailData> promotionReportDetailDataList, PromotionReportData promotionReportData, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // 1. Delete all details
	    for (PromotionReportDetailData promotionReportDetailData : promotionReportDetailDataList) {

		promotionReportDetailData.getPromotionReportDetail().setSystemUser(loginEmpId + "");
		DataAccess.deleteEntity(promotionReportDetailData.getPromotionReportDetail(), session);
	    }

	    // 2. Delete promotionReportData
	    promotionReportData.getPromotionReport().setSystemUser(loginEmpId + "");
	    DataAccess.deleteEntity(promotionReportData.getPromotionReport(), session);

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
     * update promotion report and promotion details status, and invalidate Employees inbox and delegations
     * 
     * @param promotionReportData
     *            The promotion report data
     * @param promotionReportDetailDataList
     *            The promotion report details list that will be managed
     * @param session
     *            parameter used to access the database
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * @throws DatabaseException
     *             if any database exceptions or errors occurs
     */
    public static void doPromotionReportAndDetailsEffect(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, CustomSession session) throws BusinessException, DatabaseException {

	List<Long> empsId = new ArrayList<Long>();

	// check this
	// change report details from candidate to promoted
	for (PromotionReportDetailData promotionReportDetailData : promotionReportDetailDataList) {
	    if (promotionReportDetailData.getStatus() == PromotionCandidateStatusEnum.CANDIDATE.getCode() || promotionReportDetailData.getStatus() == PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()) {
		updatePromotionReportDetailStatus(promotionReportDetailData, PromotionCandidateStatusEnum.PROMOTED.getCode(), session);

		if (promotionReportData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode()) || (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !promotionReportData.getScaleUpFlagBoolean()))
		    empsId.add(promotionReportDetailData.getEmpId());
	    }
	}

	// invalidate Employees inbox And Delegations for Promoted Soldiers and Civilians
	if (empsId.size() > 0)
	    BusinessWorkflowCooperation.invalidateEmployeesInboxAndDelegations(empsId.toArray(new Long[empsId.size()]), TransactionClassesEnum.PROMOTIONS.getCode(), session);

	updatePromotionReportStatus(promotionReportData, PromotionReportStatusEnum.CLOSED.getCode(), session);
    }

    private static void handlePromotionOfficersReportClosing(PromotionReportData promotionReportData, Long[] excludedIds, long loginEmpId, CustomSession session) throws BusinessException {

	Long[] notRoyalOrderedStatuses = new Long[] { PromotionCandidateStatusEnum.CANDIDATE.getCode(), PromotionCandidateStatusEnum.NON_CANDIDATE.getCode() };
	long notRoyalOrderIssuedCount = getPromotionReportDetailsStatusesCount(promotionReportData.getId(), notRoyalOrderedStatuses, excludedIds);

	if (notRoyalOrderIssuedCount == 0) {
	    promotionReportData.setStatus(PromotionReportStatusEnum.CLOSED.getCode());
	    addModifyPromotionReport(promotionReportData, loginEmpId, session);
	}
    }

    /**
     * Scale up promotion report details jobs for Soldiers only
     * 
     * @param promotionReportData
     *            promotion report data
     * @param originalPromotionReportDetailDataList
     *            list of all original promotion report detail (candidate, noncandidate, and promoted)
     * @param loginEmpId
     *            login employee id
     * @return
     * @throws BusinessException
     *             if any error occurs
     */
    public static String scaleUpPromotionDetailsJobs(PromotionReportData promotionReportData, List<PromotionReportDetailData> candidatePromotionReportDetailDataList, long loginEmpId) throws BusinessException {
	String detailsModifiedMessage = null;
	try {
	    promotionReportData.setScaleUpFlagBoolean(!promotionReportData.getScaleUpFlagBoolean());
	    for (PromotionReportDetailData promotionReportDetailDataItr : candidatePromotionReportDetailDataList) {
		handleScaleUpJob(promotionReportDetailDataItr, promotionReportData.getScaleUpFlagBoolean());
	    }

	    if (candidatePromotionReportDetailDataList.size() > 0)
		detailsModifiedMessage = promotionReportData.getScaleUpFlagBoolean() ? "notify_scalupAllSoldiersJobs" : "notify_notScalupAllSoldiersJobs";

	    addPromotionReportAndReportDetails(promotionReportData, candidatePromotionReportDetailDataList, loginEmpId);

	    return detailsModifiedMessage;
	} catch (BusinessException e) {
	    throw e;
	}
    }

    public static byte[] getRankPowerReportDataBytes(List<Integer> netVacant, List<Integer> allowedPromotionCount, List<Integer> loadedBalanceWithdrawnFromPromotion) throws BusinessException {
	try {
	    String reportName;
	    reportName = ReportNamesEnum.PROMOTIONS_RANK_POWER_DATA.getCode();

	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_NET_VACANT", netVacant);
	    parameters.put("P_ALLOWED_PROMOTION_COUNT", allowedPromotionCount);
	    parameters.put("P_LOADED_BALANCE_WITHDRAWEN_FROM_PROMOTION", loadedBalanceWithdrawnFromPromotion);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /*------------------------------------------------------Validations-----------------------------------------------------*/

    public static void validatePromotionReportData(PromotionReportData newPromotionReportData) throws BusinessException {

	if (newPromotionReportData.getReportNumber() == null || newPromotionReportData.getReportNumber().trim().isEmpty())
	    throw new BusinessException("error_reportNumberIsMandatory");

	// TODO: where to put update reportNumber with trimmed reportNumber line of code ??

	if (newPromotionReportData.getReportDate() == null)
	    throw new BusinessException("error_reportDateIsMandatory");

	if (!HijriDateService.isValidHijriDate(newPromotionReportData.getReportDate()))
	    throw new BusinessException("error_invalidReportDate");

	if (newPromotionReportData.getReportDate().after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_invalidReportDateAfterToday");

	if (newPromotionReportData.getDueDate() == null)
	    throw new BusinessException("error_dueDateIsMandatory");

	if (!HijriDateService.isValidHijriDate(newPromotionReportData.getDueDate()))
	    throw new BusinessException("error_invalidPromotionDueDate");

	if (newPromotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !newPromotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode())) {
	    if (newPromotionReportData.getPromotionDate() == null)
		throw new BusinessException("error_promotionDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(newPromotionReportData.getPromotionDate()))
		throw new BusinessException("error_invalidPromotionDate");
	}

	if (newPromotionReportData.getRankId() == null) {
	    if (newPromotionReportData.getCategoryId().equals(CategoryModesEnum.CIVILIANS.getCode()))
		throw new BusinessException("error_empRankIsMandatory");
	    else
		throw new BusinessException("error_rankIsMandatory");
	}

	if (newPromotionReportData.getCategoryId().equals(CategoryModesEnum.SOLDIERS.getCode()) && newPromotionReportData.getRegionId() == null)
	    throw new BusinessException("error_regionMandatory");

	validateReportNumberUniqueness(newPromotionReportData);
    }

    /**
     * Validate when modify or add new promotion report the uniqueness of the report number.
     * 
     * @param newPromotionReportData
     *            Promotion report data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateReportNumberUniqueness(PromotionReportData newPromotionReportData) throws BusinessException {
	// check for uniqueness in report number and category
	PromotionReportData promotionsReport = getPromotionReportDataByReportNumberAndCategory(newPromotionReportData.getReportNumber(), newPromotionReportData.getCategoryId());
	if (promotionsReport != null) {
	    if (newPromotionReportData.getId() == null || !promotionsReport.getId().equals(newPromotionReportData.getId()))
		throw new BusinessException("error_promotionReportSaveError", new String[] { newPromotionReportData.getReportNumber() });
	}
    }

    /*------------------------------------------------------Queries---------------------------------------------------------*/

    /**
     * Search for all promotion Reports with reports ids.
     * 
     * That method used to search for all promotion reports that are not included in workflow.
     * 
     * @param reportIds
     *            Promotion reports Ids
     * @return List with promotionReport
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static List<PromotionReport> searchPromotionReports(Long[] reportsIds) throws BusinessException {

	if (reportsIds == null || reportsIds.length == 0)
	    return new ArrayList<PromotionReport>();
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REPORTS_IDS", reportsIds);

	    return DataAccess.executeNamedQuery(PromotionReport.class, QueryNamesEnum.HCM_GET_PROMOTION_REPORTS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * @param reportId
     * @return Promotion Report data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     */
    public static PromotionReportData getPromotionReportDataById(long reportId) throws BusinessException {
	List<PromotionReportData> promotionReportDataList = searchPromotionReportsData(reportId, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, FlagsEnum.ALL.getCode(), false, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	return promotionReportDataList.isEmpty() ? null : promotionReportDataList.get(0);
    }

    /**
     * Wrapper for {@link #searchPromotionReportsWithoutWorkFlow(long, String, Date, Date, long, long, String, Date, long, Boolean, long, long)} to get List of promotion report data which have this report number and category Id
     * 
     * @param reportNumber
     *            Promotion report number
     * @param categoryId
     *            Promotion report category Id
     * @return promotion Report Data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see #searchPromotionReportsWithoutWorkFlow(long, String, Date, Date,long, long, String, Date, long, Boolean, long, long)
     */
    private static PromotionReportData getPromotionReportDataByReportNumberAndCategory(String reportNumber, long categoryId) throws BusinessException {
	List<PromotionReportData> promotionReportDataList = searchPromotionReportsData(FlagsEnum.ALL.getCode(), reportNumber, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, FlagsEnum.ALL.getCode(), false, categoryId, FlagsEnum.ALL.getCode());
	return promotionReportDataList.isEmpty() ? null : promotionReportDataList.get(0);
    }

    /**
     * Wrapper for{@link #searchPromotionReportsWithoutWorkFlow(long, String, Date, Date, long, long, String, Date, long, boolean, long, long)} to get the List of promotion report data which filtered by report date promotion due date, rank , promotion report status, promotion report decision number , promotion report decision date, Employee Id, category Id or at least has one employee
     * 
     * @param reportNumber
     *            Promotion report number
     * @param reportDate
     *            Promotion report date
     * @param dueDate
     *            Promotion report due date
     * @param rankId
     *            Promotion report rank
     * @param status
     *            Promotion report status
     * @param decisionNumber
     *            Promotion report decision number
     * @param decisionDate
     *            Promotion report decision date
     * @param empId
     *            Employee Id
     * @param allSubmittedOfficersFlag
     *            Flag to indicate all candidate officers
     * @param categoryId
     *            Promotion report category Id
     * @return List of promotion report data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see #searchPromotionReportsWithoutWorkFlow(long, String, Date, Date, long, long, String, Date, long, boolean, long, long)
     */
    public static List<PromotionReportData> getPromotionsReportsData(String reportNumber, Date reportDate, Date dueDate, long rankId, long status, String decisionNumber, Date decisionDate, long empId, boolean allSubmittedOfficersFlag, long categoryId, long regionId) throws BusinessException {
	return searchPromotionReportsData(FlagsEnum.ALL.getCode(), reportNumber, reportDate, dueDate, rankId, status, decisionNumber, decisionDate, empId, allSubmittedOfficersFlag, categoryId, regionId);
    }

    /**
     * Search for all promotion Report data which are filtered with some parameters .
     * 
     * That method used to search for all promotion reports that are not included in workflow.
     * 
     * @param reportId
     *            Promotion report Id
     * @param reportNumber
     *            Promotion report number
     * @param reportDate
     *            Promotion report date
     * @param promotionTypeId
     *            Promotion report type Id
     * @param dueDate
     *            Promotion report Due date
     * @param rankId
     *            Promotion rank Id
     * @param status
     *            Promotion status is it current, under review , ... etc
     * @param regionId
     *            Promotion report region Id
     * @param scaleUpFlag
     *            Flag used for soldiers only to indicate if their jobs has scaled to next rank or not
     * @param decisionNumber
     *            Promotion report Decision number
     * @param decisionDate
     *            Promotion report decision date
     * @param empId
     *            Employee Id that you can select the report which included specific employee
     * @param allSubmittedOfficers
     *            Flag to indicate all candidate officers
     * @param categoryId
     *            Category Id
     * @return List with promotionReportData
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static List<PromotionReportData> searchPromotionReportsData(long reportId, String reportNumber, Date reportDate, Date dueDate, long rankId, long status, String decisionNumber, Date decisionDate, long empId, boolean allSubmittedOfficersFlag, long categoryId, long regionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_REPORT_ID", reportId);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_REPORT_NUMBER", (reportNumber == null || reportNumber.equals("")) ? (FlagsEnum.ALL.getCode() + "") : reportNumber);
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_RANK_ID", rankId);
	    qParams.put("P_STATUS", status);
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.isEmpty()) ? (FlagsEnum.ALL.getCode() + "") : decisionNumber);
	    qParams.put("P_ALL_SUBMITTED_OFFICERS", allSubmittedOfficersFlag ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());

	    if (reportDate != null) {
		qParams.put("P_REPORT_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_REPORT_DATE", HijriDateService.getHijriDateString(reportDate));
	    } else {
		qParams.put("P_REPORT_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_REPORT_DATE", HijriDateService.getHijriSysDateString());
	    }
	    if (dueDate != null) {
		qParams.put("P_DUE_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DUE_DATE", HijriDateService.getHijriDateString(dueDate));
	    } else {
		qParams.put("P_DUE_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DUE_DATE", HijriDateService.getHijriSysDateString());
	    }
	    if (decisionDate != null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    }

	    return DataAccess.executeNamedQuery(PromotionReportData.class, QueryNamesEnum.HCM_GET_PROMOTION_REPORTS_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long getRunningPromotionReportsCount(long empId) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMP_ID", empId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_RUNNING_PROMOTION_REPORTS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*------------------------------------------------------Reports---------------------------------------------------------*/
    /**
     * Get all Data of Promotion report and set the Promotion_REPORT_ID and Logged Employee ID as a report parameters and set the report name according to category of transaction finally get the report data as a bytes and return them to print promotion Report
     * 
     * @param reportId
     *            report id
     * @param loggedEmpId
     *            The logged employee Data
     * @return bytes of report data to print it
     * @throws BusinessException
     *             If an error occurred in get the report or report data
     */
    public static byte[] getUnCandidatesPersonsPromotionBytes(long reportId, boolean isNoVacant) throws BusinessException {

	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_REPORT_ID", reportId);
	    if (isNoVacant)
		parameters.put("P_NO_VACANT", FlagsEnum.ON.getCode());
	    else
		parameters.put("P_NO_VACANT", FlagsEnum.OFF.getCode());

	    reportName = ReportNamesEnum.PROMOTIONS_CIVILIANS_COLLECTIVE_UNCANDIDATES_REPORT.getCode();
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

    public static byte[] getCandidatesSoldiersPromotionByDifferentiationBytes(PromotionReportData promotionReportData) throws BusinessException {
	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_REPORT_ID", promotionReportData.getId().intValue());
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_RANK_DESC", promotionReportData.getRankDesc());
	    reportName = ReportNamesEnum.PROMOTIONS_SOLDIERS_CANDIDATES_BY_DIFFERENTIATION.getCode();
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Get all Data of Promotion report and set the Promotion_REPORT_ID and Logged Employee ID as a report parameters and set the report name according to category of transaction finally get the report data as a bytes and return them to print promotion Report
     * 
     * @param promotionReportData
     *            Promotion report data that will be printed
     * @param reportOutputFormat
     *            Report Output Format
     * @return bytes of report data to print it
     * @throws BusinessException
     *             If an error occurred in get the report or report data
     */
    public static byte[] getDraftPromotionBytes(PromotionReportData promotionReportData, String reportOutputFormat) throws BusinessException {

	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_REPORT_ID", promotionReportData.getId().intValue());

	    if (promotionReportData.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {

		List<PromotionReportDetailData> candidateOfficers = getPromotionReportDetailsData(promotionReportData.getId(), FlagsEnum.ALL.getCode(), null, new Long[] { PromotionCandidateStatusEnum.CANDIDATE.getCode() });
		if (promotionReportData.getRankId() == RanksEnum.PRIME_SERGEANTS.getCode()) {
		    if (candidateOfficers.size() > 1)
			reportName = ReportNamesEnum.PROMOTIONS_PRIME_SERGEANTS_COLLECTIVE_REPORT.getCode();
		    else
			reportName = ReportNamesEnum.PROMOTIONS_PRIME_SERGEANTS_REPORT.getCode();
		} else {
		    if (candidateOfficers.size() > 1)
			reportName = ReportNamesEnum.PROMOTIONS_OFFICERS_COLLECTIVE_REPORT.getCode();
		    else
			reportName = ReportNamesEnum.PROMOTIONS_OFFICERS_REPORT.getCode();
		}
	    } else if (promotionReportData.getCategoryId() == CategoriesEnum.PERSONS.getCode()) {
		if (reportOutputFormat.equals(ReportOutputFormatsEnum.PDF.getCode()))
		    reportName = ReportNamesEnum.PROMOTIONS_CIVILIANS_COLLECTIVE_REPORT.getCode();
		else
		    reportName = ReportNamesEnum.PROMOTIONS_CIVILIANS_COLLECTIVE_REPORT_WORD.getCode();

	    } else if (promotionReportData.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
		reportName = ReportNamesEnum.PROMOTIONS_SOLDIERS_COLLECTIVE_REPORT.getCode();
	    }
	    if (reportOutputFormat.equals(ReportOutputFormatsEnum.PDF.getCode()))
		return getReportData(reportName, parameters);
	    else
		return getRTFReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

    /**
     * Get all Data of Promotion report and set the promotion_Report_Id and Logged_Employee_ID as a report parameters and set the report name according to category of transaction and the kind is it collective or singular finally get the report data as a bytes and return them to print promotion Report
     * 
     * @param reportId
     *            report id
     * @param loginEmpId
     *            The login employee Data
     * @return Bytes of report data to print it
     * @throws BusinessException
     *             If an error occurred in get the report or report data
     */
    public static byte[] getPrimeSergentsShowBytes(long reportId, long loginEmpId) throws BusinessException {

	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_REPORT_ID", reportId);
	    parameters.put("P_EMP_ID", loginEmpId);

	    reportName = ReportNamesEnum.PROMOTIONS_PRIME_SERGEANTS_SHOW_REPORT.getCode();
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * 
     * 
     * 
     * /** Get all data of promotions reports for officers, soldiers and persons
     * 
     * @param reportType
     *            report type
     * @param categoryId
     *            category Id (Officers, Soldiers and Persons)
     * @param rankId
     *            Rank id
     * @param regionId
     *            Region Id
     * @param promotionDueDateFrom
     *            promotion due date from
     * @param promotionDueDateTo
     *            promotion due date to
     * @param decisionDateFrom
     *            decision date from
     * @param decisionDateTo
     *            decision date to
     * @return report bytes
     * @throws BusinessException
     * @see {@link RegionsEnum}
     */
    public static byte[] getPromotionsReport(int reportType, long categoryId, long rankId, long regionId, Date promotionDueDateFrom, Date promotionDueDateTo, Date decisionDateFrom, Date decisionDateTo, Boolean scaleUpFlag, Date promotionDate, Long unitId, Long minorSpecsId) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    String reportName = "";

	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_RANK_ID", rankId);
	    qParams.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    if (reportType != 60) {
		qParams.put("P_UNIT_ID", unitId);
		qParams.put("P_MINOR_SPECS_ID", minorSpecsId);
	    }
	    if (reportType == 10 || reportType == 40 || reportType == 50 || reportType == 60) {
		if (promotionDueDateFrom != null) {
		    qParams.put("P_FROM_DATE_FLAG", FlagsEnum.ON.getCode());
		    qParams.put("P_FROM_DATE", HijriDateService.getHijriDateString(promotionDueDateFrom));
		} else {
		    qParams.put("P_FROM_DATE_FLAG", FlagsEnum.ALL.getCode());
		    qParams.put("P_FROM_DATE", null);
		}
		qParams.put("P_TO_DATE", HijriDateService.getHijriDateString(promotionDueDateTo));
		if (reportType == 10 || reportType == 50) {
		    reportName = ReportNamesEnum.PROMOTIONS_ELIGIBLE_INQUIRY.getCode();
		    qParams.put("P_REGION_ID", regionId);
		    qParams.put("P_OFFICIAL_UNIT_FLAG", reportType == 10 ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
		} else if (reportType == 40) {
		    reportName = ReportNamesEnum.PROMOTIONS_SOLDIERS_ELIGIBLE_STATISTICAL_INQUIRY.getCode();
		    qParams.put("P_OFFICIAL_REGION_SHORT_DESC", regionId == FlagsEnum.ALL.getCode() ? (FlagsEnum.ALL.getCode() + "") : CommonService.getRegionById(regionId).getShortDescription());
		    qParams.put("P_RANK_DESC", rankId == FlagsEnum.ALL.getCode() ? (FlagsEnum.ALL.getCode() + "") : CommonService.getRankById(rankId).getDescription());
		    qParams.put("P_OFFICIAL_REGION_ID", regionId);
		    qParams.put("P_UNIT_DESC", unitId == FlagsEnum.ALL.getCode() ? (FlagsEnum.ALL.getCode() + "") : UnitsService.getUnitById(unitId).getFullName());
		    qParams.put("P_MINOR_SPECS_DESC", minorSpecsId == FlagsEnum.ALL.getCode() ? (FlagsEnum.ALL.getCode() + "") : SpecializationsService.getMinorSpecializationsByIdsString(minorSpecsId + "").get(0).getDescription());
		} else if (reportType == 60) {
		    reportName = ReportNamesEnum.PROMOTIONS_SOLDIERS_ELIGIBLE_WITH_MOVEMENTS_UNDER_PROCESSING.getCode();
		    qParams.put("P_REGION_ID", regionId);
		}
	    } else if (reportType == 20 || reportType == 30) {
		qParams.put("P_OFFICIAL_REGION_ID", regionId);
		if (decisionDateFrom != null) {
		    qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		    qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriDateString(decisionDateFrom));
		} else {
		    qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		    qParams.put("P_DECISION_DATE_FROM", null);
		}
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriDateString(decisionDateTo));
		qParams.put("P_OFFICIAL_REGION_SHORT_DESC", regionId == FlagsEnum.ALL.getCode() ? (FlagsEnum.ALL.getCode() + "") : CommonService.getRegionById(regionId).getShortDescription());
		qParams.put("P_RANK_DESC", rankId == FlagsEnum.ALL.getCode() ? (FlagsEnum.ALL.getCode() + "") : CommonService.getRankById(rankId).getDescription());

		if (reportType == 20)
		    reportName = ReportNamesEnum.PROMOTIONS_PROMOTED_INQUIRY.getCode();
		else if (reportType == 30) {
		    if (promotionDate != null) {
			qParams.put("P_PROMOTION_DATE_FLAG", FlagsEnum.ON.getCode());
			qParams.put("P_PROMOTION_DATE", HijriDateService.getHijriDateString(promotionDate));
		    } else {
			qParams.put("P_PROMOTION_DATE_FLAG", FlagsEnum.ALL.getCode());
			qParams.put("P_PROMOTION_DATE", null);
		    }

		    if (scaleUpFlag)
			qParams.put("P_SCALE_UP_FLAG", FlagsEnum.ON.getCode());
		    else
			qParams.put("P_SCALE_UP_FLAG", FlagsEnum.OFF.getCode());
		    qParams.put("P_UNIT_DESC", unitId == FlagsEnum.ALL.getCode() ? (FlagsEnum.ALL.getCode() + "") : UnitsService.getUnitById(unitId).getFullName());
		    qParams.put("P_MINOR_SPECS_DESC", minorSpecsId == FlagsEnum.ALL.getCode() ? (FlagsEnum.ALL.getCode() + "") : SpecializationsService.getMinorSpecializationsByIdsString(minorSpecsId + "").get(0).getDescription());
		    reportName = ReportNamesEnum.PROMOTIONS_PROMOTED_STATISTICAL_INQUIRY.getCode();
		}
	    }
	    return getReportData(reportName, qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static List<Rank> getPromotionsRanksForReports(long categoryId, int reportType) throws BusinessException {
	List<Rank> ranks = new ArrayList<Rank>();
	if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
	    if (reportType == 20 || reportType == 30)
		ranks.add(CommonService.getRankById(RanksEnum.MAJOR_GENERAL.getCode()));

	    ranks.add(CommonService.getRankById(RanksEnum.BRIGADIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT_COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.CAPTAIN.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.FIRST_LIEUTENANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT.getCode()));
	} else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
	    ranks.add(CommonService.getRankById(RanksEnum.PRIME_SERGEANTS.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.STAFF_SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.UNDER_SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.CORPORAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.FIRST_SOLDIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.SOLDIER.getCode()));
	} else if (categoryId == CategoriesEnum.PERSONS.getCode()) {
	    try {
		ranks = CommonService.getRanks(null, new Long[] { (long) CategoryModesEnum.CIVILIANS.getCode() });
	    } catch (BusinessException e) {
		throw e;
	    }
	}
	return ranks;
    }

    /*----------------------------------------------------- Promotions Report Detail  ---------------------------------------------*/
    /*------------------------------------------------------Operations------------------------------------------------------*/

    /**
     * Wrapper for {@link #constructNewPromotionReportDetails(PromotionReportData, List, List)} to construct employee report detail .
     * 
     * That method validate employee data before constructing its report detail.
     * 
     * @param empId
     *            The employee which will be added
     * @param promotionReportData
     *            Promotion report data
     * @return Promotion report detail data which created
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see #constructNewPromotionReportDetails(PromotionReportData, List, List)
     */
    public static PromotionReportDetailData constructEmployeeReportDetail(long empId, PromotionReportData promotionReportData, List<PromotionTransactionData> promotionTransactionsData) throws BusinessException {

	EmployeeData employee = EmployeesService.getEmployeeData(empId);

	validateNewPromotionReportDetail(employee, promotionReportData, promotionTransactionsData);

	List<PromotionReportDetailData> reportDetailDataList = new ArrayList<PromotionReportDetailData>();
	List<EmployeeData> employeeList = new ArrayList<EmployeeData>();

	employeeList.add(employee);

	constructNewPromotionReportDetails(promotionReportData, reportDetailDataList, employeeList, (promotionTransactionsData != null && promotionTransactionsData.size() != 0) ? promotionTransactionsData.get(0) : null);
	if (reportDetailDataList != null && !(reportDetailDataList.isEmpty()))
	    return reportDetailDataList.get(0);
	else
	    throw new BusinessException("error_invalidEmployeeDegreeZero");
    }

    public static void modifyReportDetailsDrugTestResult(List<PromotionReportDetailData> promotionReportDetailDataList) throws BusinessException {
	Log4jService.traceInfo(PromotionsService.class, "Start of modifyReportDetailsDrugTestResult()");
	try {
	    if (promotionReportDetailDataList.equals(null) || promotionReportDetailDataList.size() == 0)
		return;
	    String comma = "";
	    String socialIds = "";
	    for (PromotionReportDetailData promotionReportDetailData : promotionReportDetailDataList) {
		socialIds += comma + promotionReportDetailData.getEmpSocialID();
		comma = ",";
	    }

	    Log4jService.traceInfo(PromotionsService.class, "socialIds: " + socialIds);
	    HCMWebServiceService infoSysGetDrugTestResultsWS = new HCMWebServiceService();
	    HCMWebService webService = infoSysGetDrugTestResultsWS.getHCMWebServicePort();

	    String result = webService.getLabCheckResults(socialIds);
	    Log4jService.traceInfo(PromotionsService.class, "result: " + result);
	    String[] resultParts = result.split(",");
	    Map<String, Integer> empsSocialIdsResultsMap = new HashMap<>();
	    for (String resultPart : resultParts) {
		String[] socialIdResult = resultPart.split("_");
		empsSocialIdsResultsMap.put(socialIdResult[0], (socialIdResult[1] == null || socialIdResult[1] == "" || !isValidPromotionMedicalTestStatus(Integer.parseInt(socialIdResult[1]))) ? PromotionMedicalTestStatusEnum.NON_TESTED.getCode() : Integer.parseInt(socialIdResult[1]));
	    }
	    for (PromotionReportDetailData promotionReportDetailData : promotionReportDetailDataList) {
		if (empsSocialIdsResultsMap.containsKey(promotionReportDetailData.getEmpSocialID() + ""))
		    promotionReportDetailData.setMedicalTest(empsSocialIdsResultsMap.get(promotionReportDetailData.getEmpSocialID() + ""));
	    }
	    Log4jService.traceInfo(PromotionsService.class, "End of modifyReportDetailsDrugTestResult()");
	} catch (Exception e) {
	    throw new BusinessException("error_promotionConnectionToInfoSysFailed");
	}
    }

    /**
     * Construct new promotion report details from employee data and calculate the degrees for soldiers and persons and others.
     * 
     * @param promotionReportDetailDataList
     *            The promotion report detail data list
     * @param employeeList
     *            Employee list which added to promotion report detail data list
     * @param promotionReportDueDate
     *            The promotion report due date
     * @param promotionReportDate
     *            The promotion report date
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void constructNewPromotionReportDetails(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, List<EmployeeData> employeeList, PromotionTransactionData promotionTransactionData) throws BusinessException {

	Log4jService.traceInfo(PromotionsService.class, "Start of constructNewPromotionReportDetails:");
	HashMap<Long, PayrollSalary> newPayrollSalaryMap = new HashMap<Long, PayrollSalary>();
	validateEmployeeData(employeeList, newPayrollSalaryMap);
	Log4jService.traceInfo(PromotionsService.class, "Report number: " + promotionReportData.getReportNumber());
	Long[] empsIds = new Long[employeeList.size()];
	Log4jService.traceInfo(PromotionsService.class, "Making a copy from employees list to correct degrees data");
	HashMap<Long, PromotionReportDetailData> employeesPromotionReportDetailDataMap = new HashMap<Long, PromotionReportDetailData>();
	List<EmployeeData> editedEmployeeList = new ArrayList<>();
	for (EmployeeData employeeData : employeeList) {
	    EmployeeData copiedEmployeeData = new EmployeeData();
	    copiedEmployeeData.setEmpId(employeeData.getEmpId());
	    copiedEmployeeData.setDegreeId(employeeData.getDegreeId());
	    editedEmployeeList.add(copiedEmployeeData);
	}
	Log4jService.traceInfo(PromotionsService.class, "Fetching the correct degrees data from raises transactions");
	if (promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()))
	    RaisesService.getEmployeesDegreesInGivenDate(editedEmployeeList, promotionReportData.getDueDate());
	int index = 0;
	int i = 0;
	String employeesLoggingData = "";
	for (EmployeeData employee : employeeList) {
	    PromotionReportDetailData reportDetailData = new PromotionReportDetailData();

	    reportDetailData.setReportId(promotionReportData.getId());
	    reportDetailData.setEmpId(employee.getEmpId());
	    reportDetailData.setBirthDate(employee.getBirthDate());
	    reportDetailData.setEmpName(employee.getName());
	    reportDetailData.setEmpSocialID(employee.getSocialID());
	    reportDetailData.setMilitaryNumber(employee.getMilitaryNumber());
	    reportDetailData.setGender(employee.getGender());

	    reportDetailData.setPromotionDueDate(employee.getPromotionDueDate());

	    reportDetailData.setOldRankId(employee.getRankId());
	    reportDetailData.setOldRankDesc(employee.getRankDesc());
	    if (promotionReportData.getPromotionTypeId() == PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode()) {
		reportDetailData.setNewRankId(promotionTransactionData.getOldRankId());
		reportDetailData.setNewRankDesc(promotionTransactionData.getOldRankDesc());
	    } else {
		Rank newRank = CommonService.getRankById(getNextRank(employee.getRankId()));
		reportDetailData.setNewRankId(newRank.getId());
		reportDetailData.setNewRankDesc(newRank.getDescription());
	    }

	    reportDetailData.setRankTitleId(employee.getRankTitleId());
	    reportDetailData.setRankTitleName(employee.getRankTitleDesc());

	    reportDetailData.setOldDegreeId(employee.getDegreeId());
	    reportDetailData.setOldDegreeDesc(employee.getDegreeDesc());
	    if (promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && employee.getDegreeId() > editedEmployeeList.get(index).getDegreeId()) {
		employeesLoggingData += "Retroactive promotion: EmpID: " + employee.getEmpId() + " EmployeeID: " + employee.getDegreeId() + " Degree from raises services: " + editedEmployeeList.get(index).getDegreeId();
		PayrollSalary oldRetroactivePayrollSalary = PayrollsService.getPayrollSalary(reportDetailData.getOldRankId(), editedEmployeeList.get(index).getDegreeId());
		PayrollSalary newRetroactivePayrollSalary = PayrollsService.getPayrollNewSalary(getNextRank(reportDetailData.getOldRankId()), oldRetroactivePayrollSalary.getBasicSalary());
		reportDetailData.setNewDegreeId(newRetroactivePayrollSalary.getDegreeId());
		employeesLoggingData += " Calculated degree: " + newRetroactivePayrollSalary.getDegreeId() + ", ";
	    } else {
		reportDetailData.setNewDegreeId(newPayrollSalaryMap.get(employee.getDegreeId()).getDegreeId());
		employeesLoggingData += "Non-Retroactive promotion: EmpId: " + employee.getEmpId() + " Calculated degree: " + newPayrollSalaryMap.get(employee.getDegreeId()).getDegreeId() + ", ";
	    }
	    reportDetailData.setOldJobId(employee.getJobId());
	    reportDetailData.setOldJobCode(employee.getJobCode());
	    reportDetailData.setOldJobDesc(employee.getJobDesc());
	    reportDetailData.setOldJobClassCode(employee.getJobClassificationCode());
	    reportDetailData.setOldJobClassDesc(employee.getJobClassificationDesc());

	    if (promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		if (!promotionReportData.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode())) {
		    reportDetailData.setNewJobId(employee.getJobId());
		    reportDetailData.setNewJobCode(employee.getJobCode());
		    reportDetailData.setNewJobDesc(employee.getJobDesc());
		}

		reportDetailData.setStatus(PromotionCandidateStatusEnum.CANDIDATE.getCode());
		reportDetailData.setMedicalTest(PromotionMedicalTestStatusEnum.NEGATIVE.getCode());

		reportDetailData.setRequirementsFlagBoolean(true);
		reportDetailData.setJudgmentFlagBoolean(false);
		reportDetailData.setEvaluationResult(FlagsEnum.ON.getCode());
		reportDetailData.setRetirementFlagBoolean(false);

		promotionReportDetailDataList.add(reportDetailData);

	    } else if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {

		if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode())) {
		    reportDetailData.setStatus(PromotionCandidateStatusEnum.CANDIDATE.getCode());
		    reportDetailData.setMedicalTest(PromotionMedicalTestStatusEnum.NEGATIVE.getCode());

		    promotionReportDetailDataList.add(reportDetailData);
		} else if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
		    if (promotionTransactionData != null) {
			reportDetailData.setNewRankId(promotionTransactionData.getOldRankId());
			reportDetailData.setNewRankDesc(CommonService.getRankById(promotionTransactionData.getOldRankId()).getDescription());
			reportDetailData.setNewDegreeId(promotionTransactionData.getOldDegreeId());
			reportDetailData.setPromotionDueDate(promotionTransactionData.getOldDueDate());
			reportDetailData.setBasedOnTransactionId(promotionTransactionData.getId());
			reportDetailData.setStatus(PromotionCandidateStatusEnum.CANDIDATE.getCode());
			reportDetailData.setMedicalTest(null);
		    }
		    // set all new jobs
		    promotionReportDetailDataList.add(reportDetailData);
		} else {
		    reportDetailData.setStatus(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode());
		    reportDetailData.setMedicalTest(PromotionMedicalTestStatusEnum.NON_TESTED.getCode());

		    empsIds[i++] = employee.getEmpId();
		    employeesPromotionReportDetailDataMap.put(employee.getEmpId(), reportDetailData);
		}

		reportDetailData.setRequirementsFlagBoolean(true);
		reportDetailData.setJudgmentFlagBoolean(false);
		reportDetailData.setEvaluationResult(FlagsEnum.ON.getCode());
		reportDetailData.setRetirementFlagBoolean(false);

	    } else if (promotionReportData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode())) {

		reportDetailData.setStatus(PromotionCandidateStatusEnum.CANDIDATE.getCode());

		reportDetailData.setSeniorityDegree(calculateCiviliansSeniorityDegrees(employee.getPromotionDueDate(), promotionReportData.getReportDate()));
		reportDetailData.setTrainingDegree(0.0);
		reportDetailData.setEducationDegree(0.0);
		reportDetailData.setEvaluationDegree(0.0);
		EmployeeQualificationsData employeeQualificationsData = EmployeesService.getEmployeeQualificationsByEmpId(employee.getEmpId());
		reportDetailData.setQualificationPersonDegree(employeeQualificationsData != null ? employeeQualificationsData.getCurQualLevelDesc() : null);

		ArrayList<Integer> hijiriDateDiff = getHijiridateDiff(promotionReportData.getReportDate(), employee.getRecruitmentJoiningDate());
		reportDetailData.setServiceDegreeYear(hijiriDateDiff.get(2));
		reportDetailData.setServiceDegreeMonth(hijiriDateDiff.get(1));
		reportDetailData.setServiceDegreeDay(hijiriDateDiff.get(0));

		reportDetailData.setExperincePresonsYear(0);
		reportDetailData.setExperincePresonsMonth(0);
		reportDetailData.setExperincePresonsDay(0);
		reportDetailData.setPromotionCommitteeOpinion(getMessage("label_candidateStatus"));

		promotionReportDetailDataList.add(reportDetailData);
	    }
	    index++;
	}
	Log4jService.traceInfo(PromotionsService.class, employeesLoggingData);

	if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode()) && !promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
	    // update soldiers degrees
	    List<PromotionEmployeeDegreeData> promotionEmployeeDegrees = getPromotionEmployeeDegreeDataByEmpsIds(empsIds, promotionReportData.getRankId());
	    for (PromotionEmployeeDegreeData promotionEmployeeDegreeDataItr : promotionEmployeeDegrees) {
		PromotionReportDetailData promotionReportDetailDataItr = employeesPromotionReportDetailDataMap.get(promotionEmployeeDegreeDataItr.getEmpId());
		if (promotionReportDetailDataItr != null) {
		    promotionReportDetailDataItr.setQualificationDegree(promotionEmployeeDegreeDataItr.getQualificationDegree());
		    promotionReportDetailDataItr.setTrainingDegree(promotionEmployeeDegreeDataItr.getTrainingDegree());
		    promotionReportDetailDataItr.setFileDegree(promotionEmployeeDegreeDataItr.getFileDegree());
		    promotionReportDetailDataItr.setFieldServiceDegree(promotionEmployeeDegreeDataItr.getFieldServiceDegree());
		    promotionReportDetailDataItr.setSeniorityDegree(promotionEmployeeDegreeDataItr.getSeniorityDegree());
		    promotionReportDetailDataItr.setPromotionExamDegree(promotionEmployeeDegreeDataItr.getExamDegree());
		    promotionReportDetailDataItr.setLeaderDegree(promotionEmployeeDegreeDataItr.getLeaderDegree());

		    promotionReportDetailDataItr.setGraduationEvaluation(promotionEmployeeDegreeDataItr.getRecruitmentCourseGraduationEvaluation());
		    promotionReportDetailDataItr.setGraduationOrder(promotionEmployeeDegreeDataItr.getRecruitmentCourseGraduationOrder());
		    promotionReportDetailDataItr.setGraduationDegree(promotionEmployeeDegreeDataItr.getRecruitmentCourseGraduationDegree());

		    promotionReportDetailDataList.add(promotionReportDetailDataItr);
		}
	    }
	}
	Log4jService.traceInfo(PromotionsService.class, "End of constructNewPromotionReportDetails:");
    }

    /**
     * Construct new report detail using employee id, and then save the report detail
     * 
     * @param empId
     *            employee id
     * @param promotionReportData
     *            promotion report data
     * @param originalPromotionReportDetailDataList
     *            list of all promotion report details, used for validation purpose
     * @param loginEmpId
     *            login employee id
     * @return added promotion report detail
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static PromotionReportDetailData addPromotionReportDetail(long empId, PromotionReportData promotionReportData, long loginEmpId, CustomSession session) throws BusinessException {

	PromotionReportDetailData newPromotionReportDetailData = constructEmployeeReportDetail(empId, promotionReportData, null);
	addModifyPromotionReportDetail(promotionReportData.getId(), newPromotionReportDetailData, loginEmpId, session);
	return newPromotionReportDetailData;
    }

    /**
     * Make collective Medical Exemption for candidate whom status is non-tested
     * 
     * @param promotionReportId
     *            promotion Report Id
     * @param medicalTestExemptionReason
     *            medical Test Exemption Reason
     * @param loginEmpId
     *            login employee id
     * @param useSession
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static List<PromotionReportDetailData> updatePromotionSoldiersReportDetailsMedicalExemption(Long promotionReportId, String medicalTestExemptionReason, long loginEmpId, CustomSession... useSession) throws BusinessException {

	List<PromotionReportDetailData> promotionReportDetailDataList = new ArrayList<PromotionReportDetailData>();
	if (promotionReportId != null) {
	    if (medicalTestExemptionReason == null || medicalTestExemptionReason.trim().isEmpty())
		throw new BusinessException("error_medicalTestExemptionReasonIsRequired");

	    try {
		promotionReportDetailDataList = PromotionsService.getRankedPromotionReportDetailsDataByReportId(promotionReportId, new Long[] { PromotionCandidateStatusEnum.CANDIDATE.getCode() }, PromotionMedicalTestStatusEnum.NON_TESTED.getCode(), null);

		for (PromotionReportDetailData promotionReportDetailDataListItr : promotionReportDetailDataList) {
		    promotionReportDetailDataListItr.setMedicalTest(PromotionMedicalTestStatusEnum.EXEMPT.getCode());
		    promotionReportDetailDataListItr.setMedicalTestExemptionReason(medicalTestExemptionReason);
		}
		addModifyPromotionReportDetails(promotionReportId, promotionReportDetailDataList, loginEmpId, useSession);

	    } catch (Exception e) {
		// roll back changes made to promotion report details
		for (PromotionReportDetailData promotionReportDetailDataListItr : promotionReportDetailDataList) {
		    promotionReportDetailDataListItr.setMedicalTest(PromotionMedicalTestStatusEnum.NON_TESTED.getCode());
		    promotionReportDetailDataListItr.setMedicalTestExemptionReason(null);
		}

		if (e instanceof BusinessException)
		    throw new BusinessException(e.getMessage(), ((BusinessException) e).getParams());

		e.printStackTrace();
		throw new BusinessException("error_general");
	    }
	}
	return promotionReportDetailDataList;
    }

    /**
     * Wrapper for {@link #addModifyPromotionReportDetail(Long, PromotionReportDetailData, long, CustomSession...)} to add or modify soldiers report details data in DB, and also make audit on changes by login user
     * 
     * 
     * @param promotionReportId
     *            The promotion report data of report detail
     * @param promotionReportDetailData
     *            The promotion report detail data that will added to DB
     * @param loginEmpId
     *            login employee id
     * @param useSession
     *            used session if any
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addModifyPromotionSoldiersReportDetail(Long promotionReportId, PromotionReportDetailData promotionReportDetailData, long loginEmpId, CustomSession session) throws BusinessException {

	validatePromotionReportDetailData(promotionReportDetailData);
	addModifyPromotionReportDetail(promotionReportId, promotionReportDetailData, loginEmpId, session);
    }

    /**
     * Wrapper for {@link #addModifyPromotionReportDetails(PromotionReportData, List, long, CustomSession...)} to add or modify report details data in DB, and also make audit on changes by login user
     * 
     * @param promotionReportData
     *            The promotion report data of report detail
     * @param promotionReportDetailData
     *            The promotion report detail data that will added to DB
     * @param loginEmpId
     *            login employee id
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see #addModifyPromotionReportDetails(PromotionReportData, List, long, CustomSession...)
     */
    public static void addModifyPromotionReportDetail(Long promotionReportId, PromotionReportDetailData promotionReportDetailData, long loginEmpId, CustomSession session) throws BusinessException {

	List<PromotionReportDetailData> reportDetailDataList = new ArrayList<PromotionReportDetailData>();
	reportDetailDataList.add(promotionReportDetailData);
	addModifyPromotionReportDetails(promotionReportId, reportDetailDataList, loginEmpId, session);
    }

    /**
     * This method is specified for officers, it's responsible for:
     * <ul>
     * <li>validate promotion officer details</li>
     * <li>add modify promotion report details</li>
     * <li>do Promotion Transaction Effect</li>
     * <li>invalidate Employees inbox And Delegations for PrimeSergant</li>
     * </ul>
     * 
     * @param promotionReportData
     *            Promotion report Data
     * @param promotionReportDetailDataList
     *            Promotion report details will be added to DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addModifyOfficersReportDetails(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {

	validatePromotionOfficerReportDetails(promotionReportData, promotionReportDetailDataList);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    addModifyPromotionReportDetails(promotionReportData.getId(), promotionReportDetailDataList, loginEmpId, session);
	    // add transaction in case officers order number and order date and PromotionStatusEnum ROYAL_OREDER_ISSUED
	    if (promotionReportDetailDataList.get(0).getStatus() != null && promotionReportDetailDataList.get(0).getStatus().equals(PromotionCandidateStatusEnum.ROYAL_OREDER_ISSUED.getCode())) {

		doPromotionTransactionEffect(promotionReportData, promotionReportDetailDataList, null, null, loginEmpId, session);

		Long[] excludedIds = new Long[promotionReportDetailDataList.size()];
		for (int i = 0; i < promotionReportDetailDataList.size(); i++)
		    excludedIds[i] = promotionReportDetailDataList.get(i).getId();

		handlePromotionOfficersReportClosing(promotionReportData, excludedIds, loginEmpId, session);

		// invalidate Employee inbox And Delegations for Promoted Prime Sergeant
		if (promotionReportData.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode()))
		    BusinessWorkflowCooperation.invalidateEmployeesInboxAndDelegations(new Long[] { promotionReportDetailDataList.get(0).getEmpId() }, TransactionClassesEnum.PROMOTIONS.getCode(), session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new BusinessException(e.getMessage(), e.getParams());
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

    public static void addModifyOfficersCollectiveReportDetails(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, String collectiveOfficersDecisionNumber, Date collectiveOfficersDecisionDate, long loginEmpId, CustomSession... useSession) throws BusinessException {

	if (collectiveOfficersDecisionNumber == null || collectiveOfficersDecisionNumber.equals("") || collectiveOfficersDecisionDate == null || collectiveOfficersDecisionDate.equals(""))
	    throw new BusinessException("error_royalNumberAndDate");

	if (!HijriDateService.isValidHijriDate(collectiveOfficersDecisionDate))
	    throw new BusinessException("error_invalidOfficerDecisionDate");

	changeOfficersCollectiveReportDetails(promotionReportDetailDataList, PromotionCandidateStatusEnum.ROYAL_OREDER_ISSUED.getCode(), collectiveOfficersDecisionNumber, collectiveOfficersDecisionDate);
	try {
	    addModifyOfficersReportDetails(promotionReportData, promotionReportDetailDataList, loginEmpId);
	} catch (BusinessException e) {
	    // roll back changes made to promotion report details
	    changeOfficersCollectiveReportDetails(promotionReportDetailDataList, PromotionCandidateStatusEnum.CANDIDATE.getCode(), null, null);
	    throw new BusinessException(e.getMessage(), e.getParams());
	} catch (Exception e) {
	    e.printStackTrace();
	    // roll back changes made to promotion report details
	    changeOfficersCollectiveReportDetails(promotionReportDetailDataList, PromotionCandidateStatusEnum.CANDIDATE.getCode(), null, null);
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Add or modify list of promotion report detail according to its promotion report , and audit these changes by login user
     * 
     * @param promotionReportDetailDataList
     *            The list of promotion report detail will be added in DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    // TODO: do we need to set id of details to null in case of exception ?!! in that case how to differentiate between new detail and updated detail ??
    public static void addModifyPromotionReportDetails(Long promotionReportId, List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (PromotionReportDetailData promotionReportDetailDataItr : promotionReportDetailDataList) {
		if (promotionReportDetailDataItr.getId() != null) { // Update
		    promotionReportDetailDataItr.getPromotionReportDetail().setSystemUser(loginEmpId + "");
		    DataAccess.updateEntity(promotionReportDetailDataItr.getPromotionReportDetail(), session);
		} else {
		    promotionReportDetailDataItr.setReportId(promotionReportId);
		    DataAccess.addEntity(promotionReportDetailDataItr.getPromotionReportDetail(), session);
		    promotionReportDetailDataItr.setId(promotionReportDetailDataItr.getPromotionReportDetail().getId());
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
     * update promotion employees(promotionDueDate, and lastPromotionDate) from PromotionPrerequiste screen
     * 
     * @param employeeData
     *            employee data
     * @param promotionDueDate
     *            promotion due date
     * @param lastPromotionDate
     *            last promotion date
     * @param loginEmpId
     *            login employee id for auditing
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void updatePromotionEmployeesDates(EmployeeData employeeData, Date promotionDueDate, Date lastPromotionDate, long loginEmpId) throws BusinessException {

	if (promotionDueDate == null)
	    throw new BusinessException("error_promotionDateMandatory");

	if (!HijriDateService.isValidHijriDate(promotionDueDate))
	    throw new BusinessException("error_invalidPromotionDueDate");

	if (lastPromotionDate != null) {
	    if (!HijriDateService.isValidHijriDate(lastPromotionDate))
		throw new BusinessException("error_invalidLastPromotionDate");
	    if (lastPromotionDate.after(HijriDateService.getHijriSysDate()))
		throw new BusinessException("error_lastPromotionAfterToday");
	}

	if (employeeData != null) {
	    if (lastPromotionDate != null) {
		if (employeeData.getRecruitmentDate() == null || lastPromotionDate.before(employeeData.getRecruitmentDate()))
		    throw new BusinessException("error_lastPromotionDateSaveViolation");
		if (!lastPromotionDate.before(promotionDueDate))
		    throw new BusinessException("error_promotionDueDateBeforeLastPromotionDate");
	    }

	    employeeData.getEmployee().setSystemUser(loginEmpId + "");
	    EmployeesService.updateEmployeePromotionData(employeeData, null, null, null, null, null, promotionDueDate, lastPromotionDate);
	}
    }

    public static void updateEmployeesMilitaryNumber(EmployeeData employeeData, Integer militaryNumber, long loginEmpId) throws BusinessException {

	if (militaryNumber == null)
	    throw new BusinessException("error_militaryNumberMandatory");

	employeeData.getEmployee().setSystemUser(loginEmpId + "");
	EmployeesService.updateEmployeePromotionData(employeeData, null, null, null, null, militaryNumber, null, null);

    }

    /**
     * update promotion report detail status
     * 
     * @param promotionReportDetailData
     *            promotion report detail data
     * @param status
     *            new report status
     * @param session
     *            Parameter used to access the database if no other session opened
     * @throws DatabaseException
     *             If any database exceptions or errors occurs
     */
    public static void updatePromotionReportDetailStatus(PromotionReportDetailData promotionReportDetailData, long status, CustomSession session) throws DatabaseException {
	promotionReportDetailData.setStatus(status);
	DataAccess.updateEntity(promotionReportDetailData.getPromotionReportDetail(), session);
    }

    public static void deletePromotionOfficersReportDetail(PromotionReportData promotionReportData, PromotionReportDetailData promotionReportDetailData, List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpId) throws BusinessException {

	CustomSession session = DataAccess.getSession();

	try {
	    session.beginTransaction();

	    deletePromotionReportDetail(promotionReportDetailData, promotionReportDetailDataList, loginEmpId, session);
	    handlePromotionOfficersReportClosing(promotionReportData, new Long[] { promotionReportDetailData.getId() }, loginEmpId, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void updatePromotionReportDetailsDrugsTestStatus(String drugsTestResult) throws BusinessException {
	String[] drugsTestResults = drugsTestResult.split(",");
	String[] socialIds = new String[drugsTestResults.length];
	Long[] drugRequestIds = new Long[drugsTestResults.length];
	Integer[] statusIds = new Integer[drugsTestResults.length];
	HashMap<String, Integer> empsSocialIdsIndexesMap = new HashMap<String, Integer>();

	for (int i = 0; i < drugsTestResults.length; i++) {
	    String[] tokens = drugsTestResults[i].split("_");
	    socialIds[i] = tokens[0];
	    drugRequestIds[i] = Long.parseLong(tokens[1]);
	    statusIds[i] = Integer.parseInt(tokens[2]);
	    empsSocialIdsIndexesMap.put(socialIds[i], i);
	}

	List<PromotionReportDetailData> promotionReportDetailDataList = searchPromotionReportDetailsBySocialIds(socialIds);
	if (promotionReportDetailDataList.isEmpty())
	    throw new BusinessException("error_general");

	boolean invalidDataFlag = false;
	List<PromotionReportDetailData> updatedPromotionReportDetailDataList = new ArrayList<PromotionReportDetailData>();
	for (PromotionReportDetailData promotionReportDetailData : promotionReportDetailDataList) {
	    Integer index = empsSocialIdsIndexesMap.get(promotionReportDetailData.getEmpSocialID());

	    // Validate these parameters
	    // 1- Check if received medicalTestStatus is valid value
	    // 2- Check for trials for changing final states like (negative, positive) can NOT be changed once set
	    // 3- in case of drugRequestId = null, set it with received value for currently_testing details only
	    // 4- In case of equality for drugRequestId in both promotionReprotDetail and received message, update medicalTest value
	    if (index == null) {
		invalidDataFlag = true;
	    } else if (promotionReportDetailData.getMedicalTest().equals(PromotionMedicalTestStatusEnum.NEGATIVE.getCode()) || promotionReportDetailData.getMedicalTest().equals(PromotionMedicalTestStatusEnum.POSITIVE.getCode()) || promotionReportDetailData.getMedicalTest().equals(PromotionMedicalTestStatusEnum.SAMPLE_CHEATING.getCode())) {
		invalidDataFlag = true;
	    } else {
		if (promotionReportDetailData.getDrugsRequestId() == null && promotionReportDetailData.getMedicalTest().equals(PromotionMedicalTestStatusEnum.CURRENTLY_TESTING.getCode())) {
		    promotionReportDetailData.setMedicalTest((statusIds[index] == null || !isValidPromotionMedicalTestStatus(statusIds[index])) ? PromotionMedicalTestStatusEnum.NON_TESTED.getCode() : statusIds[index]);
		    promotionReportDetailData.setDrugsRequestId(drugRequestIds[index]);
		    updatedPromotionReportDetailDataList.add(promotionReportDetailData);
		} else if (promotionReportDetailData.getDrugsRequestId() != null && promotionReportDetailData.getDrugsRequestId().equals(drugRequestIds[index])) {
		    if (!promotionReportDetailData.getMedicalTest().equals(statusIds[index])) {
			promotionReportDetailData.setMedicalTest((statusIds[index] == null || !isValidPromotionMedicalTestStatus(statusIds[index])) ? PromotionMedicalTestStatusEnum.NON_TESTED.getCode() : statusIds[index]);
			updatedPromotionReportDetailDataList.add(promotionReportDetailData);
		    }
		} else {
		    invalidDataFlag = true;
		}
	    }
	}

	long auditEmpId = promotionReportDetailDataList.get(0).getEmpId();
	addModifyPromotionReportDetails(null, updatedPromotionReportDetailDataList, auditEmpId);

	if (invalidDataFlag)
	    throw new BusinessException("error_general");
    }

    private static boolean isValidPromotionMedicalTestStatus(Integer medicalTest) {
	if (medicalTest.equals(PromotionMedicalTestStatusEnum.CURRENTLY_TESTING.getCode()))
	    return true;
	else if (medicalTest.equals(PromotionMedicalTestStatusEnum.NEGATIVE.getCode()))
	    return true;
	else if (medicalTest.equals(PromotionMedicalTestStatusEnum.POSITIVE.getCode()))
	    return true;
	else if (medicalTest.equals(PromotionMedicalTestStatusEnum.SENT_TO_HOSPITAL.getCode()))
	    return true;
	else if (medicalTest.equals(PromotionMedicalTestStatusEnum.SAMPLE_CHEATING.getCode()))
	    return true;
	else
	    return false;
    }

    /**
     * Send soldiers for drug test with medical test status (NON_TESTED , CURRENTLY_TESTING, SENT_TO_HOSPITAL) and candidates for higher ranks (UNDER_SERGEANT, SERGEANT, STAFF_SERGEANT).
     * 
     * Send soldiers for drug test with medical test status (NON_TESTED , CURRENTLY_TESTING, SENT_TO_HOSPITAL) and candidates for lower ranks (SOLDIER, FIRST_SOLDIER, CORPORAL) and also 20% for the non-candidates from the total report soldiers except exceptional soldiers .
     * 
     * @param promotionReportData
     *            Promotion report data
     * @param loginEmpId
     *            login employee Id
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void sendPromotionsSoldiersDrugsTestRequest(PromotionReportData promotionReportData, long loginEmpId) throws BusinessException {

	Log4jService.traceInfo(PromotionsService.class, "Start of sendPromotionsSoldiersDrugsTestRequest()");
	Log4jService.traceInfo(PromotionsService.class, promotionReportData.toString());
	Integer[] medicalTestStatuses = new Integer[] { null, PromotionMedicalTestStatusEnum.NON_TESTED.getCode() };
	List<PromotionReportDetailData> candidatePromotionReportDetailDataList = getPromotionReportDetailsDataForDrugsTest(promotionReportData.getId(), new Long[] { PromotionCandidateStatusEnum.CANDIDATE.getCode() }, medicalTestStatuses);

	if (promotionReportData.getRankId().equals(RanksEnum.SOLDIER.getCode()) || promotionReportData.getRankId().equals(RanksEnum.FIRST_SOLDIER.getCode()) || promotionReportData.getRankId().equals(RanksEnum.CORPORAL.getCode())) {
	    List<PromotionReportDetailData> nonCandidatePromotionReportDetailDataList = getPromotionReportDetailsDataForDrugsTest(promotionReportData.getId(), new Long[] { PromotionCandidateStatusEnum.NON_CANDIDATE.getCode() }, medicalTestStatuses);

	    int extraNonCandidateSentForDrugTestCount = (int) (ETRConfigurationService.getPromotionSoldiersExtraNonCandidatesDrugsTestPercentage() * candidatePromotionReportDetailDataList.size());

	    int chosenNonCandidateSentForDrugTestCount = 0;
	    // Choose the non candidates who have been sent to drugs test before.
	    for (int i = 0; i < nonCandidatePromotionReportDetailDataList.size() && chosenNonCandidateSentForDrugTestCount < extraNonCandidateSentForDrugTestCount; i++) {
		if (!nonCandidatePromotionReportDetailDataList.get(i).getMedicalTest().equals(PromotionMedicalTestStatusEnum.NON_TESTED.getCode()) && nonCandidatePromotionReportDetailDataList.get(i).getMedicalTest() != null) {
		    candidatePromotionReportDetailDataList.add(nonCandidatePromotionReportDetailDataList.get(i));
		    chosenNonCandidateSentForDrugTestCount++;
		}
	    }

	    // If chosen non candidates count is not sufficient yet, choose some new non candidates to send
	    for (int i = 0; i < nonCandidatePromotionReportDetailDataList.size() && chosenNonCandidateSentForDrugTestCount < extraNonCandidateSentForDrugTestCount; i++) {
		if (nonCandidatePromotionReportDetailDataList.get(i).getMedicalTest().equals(PromotionMedicalTestStatusEnum.NON_TESTED.getCode()) && nonCandidatePromotionReportDetailDataList.get(i).getMedicalTest() == null) {
		    candidatePromotionReportDetailDataList.add(nonCandidatePromotionReportDetailDataList.get(i));
		    chosenNonCandidateSentForDrugTestCount++;
		}
	    }
	}
	Log4jService.traceInfo(PromotionsService.class, "List of candidate Promotion Report Detail Data List to be sent to drug test");
	for (PromotionReportDetailData r : candidatePromotionReportDetailDataList) {
	    Log4jService.traceInfo(PromotionsService.class, r.toString());
	}
	if (candidatePromotionReportDetailDataList.isEmpty()) {
	    Log4jService.traceInfo(PromotionsService.class, "Candidate list is empty");
	    throw new BusinessException("error_noCandidatesInReportDidntPerformDrugTest");
	}
	String employeesSocialIDs = "";
	String comma = "";
	for (PromotionReportDetailData promotionReportDetailDataItr : candidatePromotionReportDetailDataList) {
	    String empSocialID = promotionReportDetailDataItr.getEmpSocialID() + "_";
	    empSocialID += promotionReportDetailDataItr.getDrugsRequestId() != null ? promotionReportDetailDataItr.getDrugsRequestId() : PROMOTION_DRUGS_TEST_REQUEST_SYMBOL;
	    employeesSocialIDs += comma + empSocialID;
	    comma = ",";
	}

	Log4jService.traceInfo(PromotionsService.class, "Drug test text message : " + (employeesSocialIDs.isEmpty() ? " _empty_ " : employeesSocialIDs));
	if (!employeesSocialIDs.isEmpty())
	    DrugsTestJMSClient.sendTextMessage(employeesSocialIDs);

	modifyReportDetailsDrugTestResult(candidatePromotionReportDetailDataList);
	for (PromotionReportDetailData promotionReportDetailDataItr : candidatePromotionReportDetailDataList) {
	    if (promotionReportDetailDataItr.getMedicalTest().equals(PromotionMedicalTestStatusEnum.NON_TESTED.getCode())) {
		promotionReportDetailDataItr.setMedicalTest(PromotionMedicalTestStatusEnum.CURRENTLY_TESTING.getCode());
	    }
	}
	Log4jService.traceInfo(PromotionsService.class, "Candidates after changing status from not tested to currrently testing");
	for (PromotionReportDetailData r : candidatePromotionReportDetailDataList) {
	    Log4jService.traceInfo(PromotionsService.class, r.toString());
	}
	addModifyPromotionReportDetails(promotionReportData.getId(), candidatePromotionReportDetailDataList, loginEmpId);
	Log4jService.traceInfo(PromotionsService.class, "End of sendPromotionsSoldiersDrugsTestRequest()");
    }

    /**
     * 
     * Delete promotion report details of search from DB, and audit it by login user
     * 
     * @param promotionReportDetailDataList
     *            The list of all promotion report details
     * @param loginEmpId
     *            Used to audit user action when delete details
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deletePromotionReportDetails(List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    int promotionReportDetailDataListSize = promotionReportDetailDataList.size();
	    for (int i = 0; i < promotionReportDetailDataListSize; i++)
		deletePromotionReportDetail(promotionReportDetailDataList.get(0), promotionReportDetailDataList, loginEmpId, session);

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
     * 
     * Delete promotion report detail from promotion report detail list and from DB, and audit it by login user
     * 
     * @param promotionReportDetailDataList
     *            The list of all promotion report details
     * @param promotionReportDetailData
     *            The promotion report detail that will be deleted
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deletePromotionReportDetail(PromotionReportDetailData promotionReportDetailData, List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {

	if (promotionReportDetailData.getId() == null) {
	    promotionReportDetailDataList.remove(promotionReportDetailData);
	    return;
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    promotionReportDetailData.getPromotionReportDetail().setSystemUser(loginEmpId + "");
	    DataAccess.deleteEntity(promotionReportDetailData.getPromotionReportDetail(), session);

	    if (!isOpenedSession)
		session.commitTransaction();

	    promotionReportDetailDataList.remove(promotionReportDetailData);

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
     * Wrapper fro {@link #searchPromotionEligibleEmployees(PromotionReportData, List, Integer, Long)} to get the eligible employees whom satisfy the promotion reasons like (rank, promotion due date ...etc)
     * 
     * @param promotionReportData
     *            The promotion report Data
     * @param reportDetailDataList
     *            The list of promotion report detail data to prevent the duplicate in the report
     * @return List of promotion report detail constructed by suitable employee found
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see #searchPromotionEligibleEmployees(PromotionReportData, List, Integer, Long)
     */
    private static List<PromotionReportDetailData> getPromotionDetailsForEligibleEmployees(PromotionReportData promotionReportData, List<PromotionReportDetailData> reportDetailDataList) throws BusinessException {

	int categoryId;
	if (promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && promotionReportData.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode()))
	    categoryId = (int) CategoriesEnum.SOLDIERS.getCode();
	else
	    categoryId = promotionReportData.getCategoryId().intValue();

	long regionId = promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) ? promotionReportData.getRegionId() : (long) FlagsEnum.ALL.getCode();

	List<Long> ranksIds = getRanksByReportRankIdAndCategoryId(promotionReportData.getCategoryId(), promotionReportData.getRankId());
	List<EmployeeData> employeeList = EmployeesService.getPromotionEligibleEmployees(ranksIds, promotionReportData.getDueDate(), categoryId, regionId);

	constructNewPromotionReportDetails(promotionReportData, reportDetailDataList, employeeList, null);
	if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
	    modifyReportDetailsDrugTestResult(reportDetailDataList);
	return reportDetailDataList;
    }

    public static void handleReportDetailsFreezedJobs(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetails, long loginEmpId, boolean addFreezedJobsFlag) throws BusinessException {

	List<PromotionReportDetailData> updatedPromotionReportDetails = new ArrayList<PromotionReportDetailData>();
	if (addFreezedJobsFlag) {
	    long newRankId = getNextRank(promotionReportData.getRankId());
	    List<JobData> jobsToBeFreezed = JobsService.getVacantJobsByPromotionInfo(null, null, null, CategoriesEnum.SOLDIERS.getCode(), newRankId, promotionReportData.getRegionId(), FlagsEnum.ALL.getCode(), null);
	    if (jobsToBeFreezed.size() > 0) {
		List<Long> freezedJobsIdsInPromotionReportDetails = getFreezedJobsIdsInPromotionReportDetails(newRankId);
		if (freezedJobsIdsInPromotionReportDetails.size() > 0) {
		    Map<Long, JobData> jobsReservedForPromotion = new HashMap<Long, JobData>();
		    for (JobData jobReservedForPromotion : jobsToBeFreezed)
			jobsReservedForPromotion.put(jobReservedForPromotion.getId(), jobReservedForPromotion);

		    for (Long freezedJobId : freezedJobsIdsInPromotionReportDetails)
			if (jobsReservedForPromotion.containsKey(freezedJobId))
			    jobsToBeFreezed.remove(jobsReservedForPromotion.get(freezedJobId));
		}
	    }

	    for (PromotionReportDetailData promotionReportDetailDataItr : promotionReportDetails)
		if (promotionReportDetailDataItr.getFreezedJobId() == null)
		    updatedPromotionReportDetails.add(promotionReportDetailDataItr);

	    int end = Math.min(jobsToBeFreezed.size(), updatedPromotionReportDetails.size());
	    for (int i = 0; i < end; i++) {
		updatedPromotionReportDetails.get(i).setFreezedJobId(jobsToBeFreezed.get(i).getId());
		updatedPromotionReportDetails.get(i).setFreezedJobCode(jobsToBeFreezed.get(i).getCode());
		updatedPromotionReportDetails.get(i).setFreezedJobName(jobsToBeFreezed.get(i).getName());
	    }
	} else {
	    for (PromotionReportDetailData promotionReportDetailDataItr : promotionReportDetails) {
		if (promotionReportDetailDataItr.getFreezedJobId() != null) {
		    promotionReportDetailDataItr.setFreezedJobId(null);
		    promotionReportDetailDataItr.setFreezedJobCode(null);
		    promotionReportDetailDataItr.setFreezedJobName(null);
		    updatedPromotionReportDetails.add(promotionReportDetailDataItr);
		}
	    }
	}
	if (updatedPromotionReportDetails.size() == 0)
	    return;

	addModifyPromotionReportDetails(promotionReportData.getId(), updatedPromotionReportDetails, loginEmpId);
    }

    public static void enrollSoldierPromotionReportDetails(Integer enrollNumber, PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {

	if (enrollNumber == null || enrollNumber <= 0)
	    throw new BusinessException("error_enrollNumberMandatory");

	Long[] statusIds = { PromotionCandidateStatusEnum.NON_CANDIDATE.getCode(), PromotionCandidateStatusEnum.CANDIDATE.getCode() };
	List<PromotionReportDetailData> rankedPromotionReportDetailDataList = getRankedPromotionReportDetailsDataByReportId(promotionReportData.getId(), statusIds, null, null);

	if (enrollNumber > rankedPromotionReportDetailDataList.size())
	    throw new BusinessException("error_enrollNumberExceed", new String[] { rankedPromotionReportDetailDataList.size() + "" });

	promotionReportDetailDataList.clear();

	for (int i = 0; i < enrollNumber; i++) {
	    rankedPromotionReportDetailDataList.get(i).setStatus(PromotionCandidateStatusEnum.CANDIDATE.getCode());
	    handleScaleUpJob(rankedPromotionReportDetailDataList.get(i), promotionReportData.getScaleUpFlagBoolean());
	    promotionReportDetailDataList.add(rankedPromotionReportDetailDataList.get(i));
	}

	for (int i = enrollNumber; i < rankedPromotionReportDetailDataList.size(); i++) {
	    rankedPromotionReportDetailDataList.get(i).setStatus(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode());
	    handleScaleUpJob(rankedPromotionReportDetailDataList.get(i), false);
	}

	addModifyPromotionReportDetails(promotionReportData.getId(), rankedPromotionReportDetailDataList, loginEmpId, useSession);
    }

    /*
     * public static void sortPromotionReportDetails(List<PromotionReportDetailData> list) {
     * 
     * Comparator<PromotionReportDetailData> soldierComparator = new Comparator<PromotionReportDetailData>() {
     * 
     * @Override public int compare(PromotionReportDetailData reportDetail1, PromotionReportDetailData reportDetail2) { if (reportDetail1.getStatus().compareTo(reportDetail2.getStatus()) == 0) { if (reportDetail1.getTotalDegree().compareTo(reportDetail2.getTotalDegree()) == 0) { if (reportDetail2.getSeniorityDegree().compareTo(reportDetail1.getSeniorityDegree()) == 0) { if (reportDetail2.getQualificationDegree().compareTo(reportDetail1.getQualificationDegree()) == 0) { if
     * (reportDetail2.getFileDegree().compareTo(reportDetail1.getFileDegree()) == 0) { if (reportDetail2.getTrainingDegree().compareTo(reportDetail1.getTrainingDegree()) == 0) { if (reportDetail2.getFieldServiceDegree().compareTo(reportDetail1.getFieldServiceDegree()) == 0) return reportDetail2.getEmpName().compareTo(reportDetail1.getEmpName()); return reportDetail2.getFieldServiceDegree().compareTo(reportDetail1.getFieldServiceDegree()); } return
     * reportDetail2.getTrainingDegree().compareTo(reportDetail1.getTrainingDegree()); } return reportDetail2.getFileDegree().compareTo(reportDetail1.getFileDegree()); } return reportDetail2.getQualificationDegree().compareTo(reportDetail1.getQualificationDegree()); } return reportDetail2.getSeniorityDegree().compareTo(reportDetail1.getSeniorityDegree()); } return reportDetail2.getTotalDegree().compareTo(reportDetail1.getTotalDegree()); } return
     * reportDetail2.getStatus().compareTo(reportDetail1.getStatus()); } };
     * 
     * calculateSoldiersTotalDegrees(list); Collections.sort(list, soldierComparator); }
     */
    // @Deprecated
    // private static void updateSoldierDegreesApprovals(PromotionReportDetailData promotionReportDetailData) {
    // // set approval names for degrees to be null
    // promotionReportDetailData.setQualificationApprovedId(null);
    // promotionReportDetailData.setQualificationApprovedName(null);
    // promotionReportDetailData.setFileApprovedId(null);
    // promotionReportDetailData.setFileApprovedName(null);
    // promotionReportDetailData.setTrainingApprovedId(null);
    // promotionReportDetailData.setTrainingApprovedName(null);
    //
    // }

    /*------------------------------------------------------Validations-----------------------------------------------------*/

    public static void validatePromotionsSoldiersDecisionCancellation(Long empId, List<PromotionTransactionData> promotionTransactionsData) throws BusinessException {
	if (promotionTransactionsData.get(0).getPromotionTypeId() == PromotionsTypesEnum.RANK_REDUCE.getCode()) {
	    throw new BusinessException("error_lastPromotionIsRankReduction");
	} else if (promotionTransactionsData.get(0).getEflag() == FlagsEnum.OFF.getCode()) {
	    throw new BusinessException("error_promotionTransactionMustBeElectronic");
	} else {
	    for (PromotionTransactionData promotionsTransactionData : promotionTransactionsData) {
		if (promotionsTransactionData.getPromotionTypeId() == PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode()) {
		    throw new BusinessException("error_onlyOneCancellation");
		}
	    }
	}
    }

    public static void validatePromotionsSoldiersDecisionCancellationWithOtherTransactions(Long empId, Date decisiondate) throws BusinessException {

	MovementTransactionData movementTransactionData = MovementsService.getLastMovementTransactionByEmployeeId(empId);
	if (movementTransactionData != null && movementTransactionData.getDecisionDate() != null && movementTransactionData.getDecisionDate().after(decisiondate))
	    throw new BusinessException("error_promotionCancellationNotValid");

	VacationData vacationData = VacationsService.getLastVacationData(empId, FlagsEnum.ALL.getCode());
	if (vacationData != null && vacationData.getDecisionDate() != null && vacationData.getDecisionDate().after(decisiondate))
	    throw new BusinessException("error_promotionCancellationNotValid");

	List<MissionDetailData> missionDetailData = MissionsService.getMissionTransactionsForEmployeeAfterDecisionDate(empId, decisiondate);
	if (missionDetailData != null && missionDetailData.size() != 0)
	    throw new BusinessException("error_promotionCancellationNotValid");

	List<TerminationTransactionData> terminationTransactionData = TerminationsService.getTerminationTransactionsAfterDecisionDate(empId, decisiondate);
	if (terminationTransactionData != null && terminationTransactionData.size() != 0) {
	    if (terminationTransactionData.size() != 2)
		throw new BusinessException("error_promotionCancellationNotValid");
	    else if (terminationTransactionData.get(0).getTransactionTypeCode() != TransactionTypesEnum.STE_TERMINATION.getCode() ||
		    terminationTransactionData.get(1).getTransactionTypeCode() != TransactionTypesEnum.STE_TERMINATION_CANCELLATION.getCode()) {
		throw new BusinessException("error_promotionCancellationNotValid");
	    }

	}

	List<TrainingTransactionData> trainingTransactionData = TrainingEmployeesService.getJoinedOrNominatedTransactionsCoursesForEmployee(empId);
	if (trainingTransactionData != null && trainingTransactionData.size() != 0
		&& trainingTransactionData.get(0).getActualStartDate() != null && trainingTransactionData.get(0).getActualStartDate().after(decisiondate))
	    throw new BusinessException("error_promotionCancellationNotValid");

	List<DisclaimerTransactionData> disclaimerTransactionData = RetirementsService.getDisclaimerTransactionsAfterDecisionDate(empId, decisiondate);
	if (disclaimerTransactionData != null && disclaimerTransactionData.size() != 0)
	    throw new BusinessException("error_promotionCancellationNotValid");

    }

    /**
     * Validate employee data before construct his detail in promotion detail data.
     * 
     * @param employee
     *            Employee object has basic employee data
     * @param promotionReportData
     *            promotion report data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateNewPromotionReportDetail(EmployeeData employee, PromotionReportData promotionReportData, List<PromotionTransactionData> promotionTransactionsData) throws BusinessException {

	if (employee == null)
	    throw new BusinessException("error_employeeDataError");

	// Check for Status
	if (employee.getStatusId() < EmployeeStatusEnum.ON_DUTY.getCode() || employee.getStatusId() >= EmployeeStatusEnum.MOVED_EXTERNALLY.getCode())
	    throw new BusinessException("error_employeeStatusNotSuitable");

	if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode())) {
	    if (employee.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode()))
		throw new BusinessException("error_primeSergeantscantAdded");
	    if (employee.getRankId().equals(RanksEnum.STUDENT_SOLDIER.getCode()))
		throw new BusinessException("error_NoPromotionForThisRank");

	} else if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
	    validatePromotionsSoldiersDecisionCancellation(employee.getEmpId(), promotionTransactionsData);
	} else {
	    // check rank, due date, check the official region of employee (soldiers only)
	    if (getRankRangeByRankId(promotionReportData.getRankId(), promotionReportData.getCategoryId()) != getRankRangeByRankId(employee.getRankId(), promotionReportData.getCategoryId()))
		throw new BusinessException("error_employeeRankNotSuitable");

	    if (employee.getPromotionDueDate() == null)
		throw new BusinessException("error_promotionDueDateIsMandatory");
	    else if (employee.getPromotionDueDate().after(promotionReportData.getDueDate()))
		throw new BusinessException("error_employeeDueDateNotSuitable");

	    if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !employee.getOfficialRegionId().equals(promotionReportData.getRegionId()))
		throw new BusinessException("error_empRegionNotSuitable", new Object[] { employee.getName() });

	    List<PromotionReportDetailData> promotionReportDetailDataList = getPromotionReportDetailsData(promotionReportData.getId(), employee.getEmpId(), null, null);
	    if (promotionReportDetailDataList != null && promotionReportDetailDataList.size() > 0) {
		if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		    if (promotionReportDetailDataList.get(0).getStatus() == PromotionCandidateStatusEnum.NON_CANDIDATE.getCode())
			throw new BusinessException("error_employeeExistInSameReportNonCandidate");
		    else
			throw new BusinessException("error_employeeExistInSameReport");
		} else
		    throw new BusinessException("error_employeeExistInSameReport");
	    }
	}
	// promotionReportData ID is null in case of Soldier Exceptional Promotion.
	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { employee.getEmpId() }, null, TransactionClassesEnum.PROMOTIONS.getCode(), promotionReportData.getPromotionTypeId() == PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode() ? true : false, promotionReportData.getId() == null ? null : promotionReportData.getId(), employee.getCategoryId());
    }

    /**
     * validate Employee data
     * 
     * @param employeeList
     *            employees list
     * @param newPayrollSalaryMap
     *            (degree, PayrollSalary) map
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateEmployeeData(List<EmployeeData> employeeList, HashMap<Long, PayrollSalary> newPayrollSalaryMap) throws BusinessException {

	String employeeString = "", comma = "", errorString = "";
	HashMap<Long, PayrollSalary> oldPayrollSalaryMap = new HashMap<Long, PayrollSalary>();

	for (EmployeeData employee : employeeList) {
	    if (employee.getDegreeId() != null) {
		if (!oldPayrollSalaryMap.containsKey(employee.getDegreeId())) {
		    PayrollSalary oldPayrollSalary = PayrollsService.getPayrollSalary(employee.getRankId(), employee.getDegreeId());
		    if (oldPayrollSalary != null) {
			oldPayrollSalaryMap.put(employee.getDegreeId(), oldPayrollSalary);
			newPayrollSalaryMap.put(employee.getDegreeId(), PayrollsService.getPayrollNewSalary(getNextRank(employee.getRankId()), oldPayrollSalaryMap.get(employee.getDegreeId()).getBasicSalary()));
		    }
		}
	    }
	}

	for (EmployeeData employee : employeeList) {

	    if ((errorString.isEmpty() || errorString.equals("error_degreeNotFound")) && employee.getDegreeId() == null) {
		errorString = "error_degreeNotFound";
		employeeString += comma + employee.getName();
		comma = ", ";
		continue;
	    }

	    if ((errorString.isEmpty() || errorString.equals("error_empGenderNotFound")) && employee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !employee.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode()) && employee.getGender() == null || employee.getGender().isEmpty()) {
		errorString = "error_empGenderNotFound";
		employeeString += comma + employee.getName();
		comma = ", ";
		continue;
	    }

	    if ((errorString.isEmpty() || errorString.equals("error_empOldSalaryNotFound")) && oldPayrollSalaryMap.get(employee.getDegreeId()) == null) {
		errorString = "error_empOldSalaryNotFound";
		employeeString += comma + employee.getName();
		comma = ", ";
		continue;
	    }

	    if ((errorString.isEmpty() || errorString.equals("error_empNewSalaryNotFound")) && newPayrollSalaryMap.get(employee.getDegreeId()) == null) {
		errorString = "error_empNewSalaryNotFound";
		employeeString += comma + employee.getName();
		comma = ", ";
		continue;
	    }
	}
	if (!employeeString.isEmpty() && !errorString.isEmpty())
	    throw new BusinessException(errorString, new Object[] { employeeString });
    }

    private static void validatePromotionReportDetailData(PromotionReportDetailData promotionReportDetailData) throws BusinessException {

	if (promotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode())) {

	    if (promotionReportDetailData.getJudgmentFlagBoolean() != null && promotionReportDetailData.getJudgmentFlagBoolean())
		throw new BusinessException("error_judgementRequired", new Object[] { promotionReportDetailData.getEmpName() });

	    if (promotionReportDetailData.getMedicalTest() != null && (promotionReportDetailData.getMedicalTest().equals(PromotionMedicalTestStatusEnum.POSITIVE.getCode()) || promotionReportDetailData.getMedicalTest().equals(PromotionMedicalTestStatusEnum.SAMPLE_CHEATING.getCode())))
		throw new BusinessException("error_invalidPromotionDrugTestResult", new Object[] { promotionReportDetailData.getEmpName() });

	    if (promotionReportDetailData.getMedicalTest() != null && promotionReportDetailData.getMedicalTest().equals(PromotionMedicalTestStatusEnum.EXEMPT.getCode()) && (promotionReportDetailData.getMedicalTestExemptionReason() == null || promotionReportDetailData.getMedicalTestExemptionReason().trim().isEmpty()))
		throw new BusinessException("error_medicalTestExemptionReasonIsRequiredParam", new Object[] { promotionReportDetailData.getEmpName() });
	}
    }

    /**
     * Validate Promotion Reports (Used and called from PromotionsWorkFlow )
     * 
     * @param promotionReportData
     *            promotion report data
     * @param promotionReportDetailDataList
     *            promotion report detail data list
     * @param excludedReportId
     *            instance Id
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void validatePromotionReport(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, Long excludedReportId) throws BusinessException {

	if (!promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode()) && !promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
	    if (promotionReportDetailDataList == null || promotionReportDetailDataList.size() == 0)
		throw new BusinessException("error_NoOneToPromote", new String[] { promotionReportData.getReportNumber() });
	}

	// Validate business
	if (promotionReportData.getCategoryId() == CategoriesEnum.SOLDIERS.getCode())
	    validatePromotionSoldierReportDetails(promotionReportData, promotionReportDetailDataList);
	else
	    validatePromotionCivilianReportDetails(promotionReportData, promotionReportDetailDataList);

	// check other promotion details under processing.
	validateRunningPromotionRequests(promotionReportData, promotionReportDetailDataList, excludedReportId);

	if (promotionReportData.getPromotionTypeId() == PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode()) {
	    PromotionTransactionData promotionTransactionData = PromotionsService.getPromotionTransactionById(promotionReportDetailDataList.get(0).getBasedOnTransactionId());
	    PromotionsService.validatePromotionsSoldiersDecisionCancellationWithOtherTransactions(promotionReportDetailDataList.get(0).getEmpId(), promotionTransactionData.getNewLastPromotionDate());
	}
    }

    /**
     * Check all officer promotion data are valid to promote
     * 
     * @param promotionReportData
     *            The promotion report Data
     * @param originalPromotionReportDetailDataList
     *            The list of promotion report detail to validate
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validatePromotionOfficerReportDetails(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList) throws BusinessException {

	if (promotionReportDetailDataList.get(0).getStatus() != null && promotionReportDetailDataList.get(0).getStatus().equals(PromotionCandidateStatusEnum.ROYAL_OREDER_ISSUED.getCode())) {

	    Long[] empsIds = new Long[promotionReportDetailDataList.size()];
	    HashMap<Long, PromotionReportDetailData> employeesPromotionReportDetailDataMap = new HashMap<Long, PromotionReportDetailData>();

	    for (int i = 0; i < promotionReportDetailDataList.size(); i++) {
		empsIds[i] = promotionReportDetailDataList.get(i).getEmpId();
		employeesPromotionReportDetailDataMap.put(empsIds[i], promotionReportDetailDataList.get(i));
	    }
	    List<EmployeeData> tempEmployees = EmployeesService.getEmployeesByEmpsIds(empsIds);
	    // check availability to promote
	    validateRanksPower(promotionReportData.getRankId(), promotionReportDetailDataList.size());

	    String employeesString = "", comma = "";
	    String errorString = "";

	    for (EmployeeData emp : tempEmployees) {
		PromotionReportDetailData promotionReportDetailDataItr = employeesPromotionReportDetailDataMap.get(emp.getEmpId());

		if ((errorString.isEmpty() || errorString.equals("error_employeeStatusIsNotSuitableForPromotion")) && (emp.getStatusId() < EmployeeStatusEnum.ON_DUTY.getCode() || emp.getStatusId() >= EmployeeStatusEnum.MOVED_EXTERNALLY.getCode())) {
		    errorString = "error_employeeStatusIsNotSuitableForPromotion";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}
		// check for physical unit and official unit assigned.
		if ((errorString.isEmpty() || errorString.equals("error_employeePhysicalUnitNotFound")) && emp.getPhysicalUnitId() == null) {
		    errorString = "error_employeePhysicalUnitNotFound";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_employeeOfficialUnitNotFound")) && emp.getOfficialUnitId() == null) {
		    errorString = "error_employeeOfficialUnitNotFound";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_employeeRankIsNotSuitableForPromotion")) && !emp.getRankId().equals(promotionReportDetailDataItr.getOldRankId())) {
		    errorString = "error_employeeRankIsNotSuitableForPromotion";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_employeeJobIsNotSuitableForPromotion")) && !emp.getJobId().equals(promotionReportDetailDataItr.getOldJobId())) {
		    errorString = "error_employeeJobIsNotSuitableForPromotion";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_employeeDegreeNotFound")) && promotionReportDetailDataItr.getOldDegreeId() == null) {
		    errorString = "error_employeeDegreeNotFound";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_militaryNumberPromotionRequired")) && (promotionReportDetailDataItr.getMilitaryNumber() == null || promotionReportDetailDataItr.getMilitaryNumber().intValue() == 0)) {
		    errorString = "error_militaryNumberPromotionRequired";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_requirementsOfficierRequired")) && !promotionReportDetailDataItr.getRequirementsFlagBoolean()) {
		    errorString = "error_requirementsOfficierRequired";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_promtionNotAllowedWhenRetire")) && promotionReportDetailDataItr.getRetirementFlagBoolean()) {
		    errorString = "error_promtionNotAllowedWhenRetire";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_evaluationOfficierNotSuitable")) && promotionReportDetailDataItr.getEvaluationResult() == FlagsEnum.OFF.getCode()) {
		    errorString = "error_evaluationOfficierNotSuitable";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_judgementRequired")) && promotionReportDetailDataItr.getJudgmentFlagBoolean()) {
		    errorString = "error_judgementRequired";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_medicalRequired")) && (promotionReportDetailDataItr.getMedicalTest().equals(PromotionMedicalTestStatusEnum.POSITIVE.getCode()) || promotionReportDetailDataItr.getMedicalTest().equals(PromotionMedicalTestStatusEnum.SAMPLE_CHEATING.getCode()))) {
		    errorString = "error_medicalRequired";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_officerDecisionDateDueViolation")) && promotionReportDetailDataItr.getExternalDecisionDate() != null && promotionReportDetailDataItr.getExternalDecisionDate().before(promotionReportDetailDataItr.getPromotionDueDate())) {
		    errorString = "error_officerDecisionDateDueViolation";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		// do not need parameters as they are the same for collective reasons
		if ((errorString.isEmpty() || errorString.equals("error_officerDecisionDateAndNumberIsMandatory"))
			&& (promotionReportDetailDataItr.getExternalDecisionNumber() == null || promotionReportDetailDataItr.getExternalDecisionNumber().equals("") || promotionReportDetailDataItr.getExternalDecisionDate() == null || promotionReportDetailDataItr.getExternalDecisionDate().equals(""))) {
		    throw new BusinessException("error_officerDecisionDateAndNumberIsMandatory");
		}

		if ((errorString.isEmpty() || errorString.equals("error_invalidOfficerDecisionDate")) && !HijriDateService.isValidHijriDate(promotionReportDetailDataItr.getExternalDecisionDate())) {
		    throw new BusinessException("error_invalidOfficerDecisionDate");
		}

		if ((errorString.isEmpty() || errorString.equals("error_officerDecisionDateSysViolation")) && promotionReportDetailDataItr.getExternalDecisionDate() != null && promotionReportDetailDataItr.getExternalDecisionDate().after(HijriDateService.getHijriSysDate())) {
		    throw new BusinessException("error_officerDecisionDateSysViolation");
		}
	    }

	    if (!errorString.isEmpty() && !employeesString.isEmpty())
		throw new BusinessException(errorString, new Object[] { employeesString });

	    if (promotionReportDetailDataList.get(0).getOldRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode())) {
		HashSet<Long> jobsIds = new HashSet<Long>();
		HashSet<Integer> militaryNumbers = new HashSet<Integer>();

		String errorParamString = "";
		comma = "";
		errorString = "";

		if (promotionReportDetailDataList.get(0).getNewJobId() == null)
		    throw new BusinessException("error_newJobIsMandatory", new Object[] { promotionReportDetailDataList.get(0).getEmpName() });

		if (HijriDateService.hijriDateDiffByHijriYear(promotionReportDetailDataList.get(0).getBirthDate(), HijriDateService.getHijriSysDate()) > ETRConfigurationService.getPromotionOfficersPrimeSergeantsMaxPromotionYears())
		    throw new BusinessException("error_ageNotSuitable", new Object[] { promotionReportDetailDataList.get(0).getEmpName(), ETRConfigurationService.getPromotionOfficersPrimeSergeantsMaxPromotionYears() });

		List<PromotionReportDetailData> originalPromotionReportDetailDataList = getPromotionReportDetailsDataByReportId(promotionReportData.getId());
		for (PromotionReportDetailData reportDetail : originalPromotionReportDetailDataList) {
		    // check for repeated job.
		    if ((errorString.isEmpty() || errorString.equals("error_prm_repeatedJobParameterized")) && reportDetail.getNewJobId() != null && !jobsIds.add(reportDetail.getNewJobId())) {
			errorString = "error_prm_repeatedJobParameterized";
			errorParamString += comma + reportDetail.getNewJobDesc();
			comma = ", ";
			continue;
		    }
		    // check for repeated military number in same report
		    if ((errorString.isEmpty() || errorString.equals("error_repeatedMilitaryNumber")) && reportDetail.getMilitaryNumber() != null && !militaryNumbers.add(reportDetail.getMilitaryNumber())) {
			errorString = "error_repeatedMilitaryNumber";
			errorParamString += comma + reportDetail.getEmpName();
			comma = ", ";
			continue;
		    }
		}
		if (!errorString.isEmpty())
		    throw new BusinessException(errorString, new Object[] { errorParamString });
	    }

	    Long[] officerJob = null;
	    if (promotionReportData.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode()))
		officerJob = new Long[] { promotionReportDetailDataList.get(0).getNewJobId() };

	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(empsIds, officerJob, TransactionClassesEnum.PROMOTIONS.getCode(), false, promotionReportData.getId(), CommonService.getRankById(promotionReportData.getRankId()).getCategoryId());
	}
    }

    /**
     * Check that all soldiers promotion data are valid to promote like job, degrees, .. etc
     * 
     * @param promotionReportData
     *            The promotion report Data
     * @param candiadatePromotionReportDetails
     *            The list of promotion report detail to validate
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validatePromotionSoldierReportDetails(PromotionReportData promotionReportData, List<PromotionReportDetailData> candiadatePromotionReportDetails) throws BusinessException {

	if (candiadatePromotionReportDetails == null || candiadatePromotionReportDetails.size() == 0)
	    throw new BusinessException("error_candidateEmpsValidation");

	HashSet<Long> jobsIds = new HashSet<Long>();
	HashSet<Long> jobsToFreezeIds = new HashSet<Long>();

	List<JobData> jobsToScale = new ArrayList<JobData>();
	List<JobData> jobsToFreeze = new ArrayList<JobData>();

	String employeesString = "", comma = "";
	String errorString = "";

	HashMap<Long, PromotionReportDetailData> employeesPromotionReportDetailDataMap = new HashMap<Long, PromotionReportDetailData>();
	Long[] empsIds = new Long[candiadatePromotionReportDetails.size()];

	for (int i = 0; i < candiadatePromotionReportDetails.size(); i++) {
	    empsIds[i] = candiadatePromotionReportDetails.get(i).getEmpId();
	    employeesPromotionReportDetailDataMap.put(empsIds[i], candiadatePromotionReportDetails.get(i));
	}

	List<EmployeeData> tempEmployees = EmployeesService.getEmployeesByEmpsIds(empsIds);

	for (EmployeeData emp : tempEmployees) {

	    PromotionReportDetailData promotionReportDetailDataItr = employeesPromotionReportDetailDataMap.get(emp.getEmpId());

	    if ((errorString.isEmpty() || errorString.equals("error_employeeStatusIsNotSuitableForPromotion")) && (emp.getStatusId() < EmployeeStatusEnum.ON_DUTY.getCode() || emp.getStatusId() >= EmployeeStatusEnum.MOVED_EXTERNALLY.getCode())) {
		errorString = "error_employeeStatusIsNotSuitableForPromotion";
		employeesString += comma + promotionReportDetailDataItr.getEmpName();
		comma = ", ";
		continue;
	    }

	    // check for physical unit and official unit assigned.
	    if ((errorString.isEmpty() || errorString.equals("error_employeePhysicalUnitNotFound")) && emp.getPhysicalUnitId() == null) {
		errorString = "error_employeePhysicalUnitNotFound";
		employeesString += comma + emp.getName();
		comma = ", ";
		continue;
	    }
	    if ((errorString.isEmpty() || errorString.equals("error_employeeOfficialUnitNotFound")) && emp.getOfficialUnitId() == null) {
		errorString = "error_employeeOfficialUnitNotFound";
		employeesString += comma + emp.getName();
		comma = ", ";
		continue;
	    }

	    if ((errorString.isEmpty() || errorString.equals("error_employeeRankIsNotSuitableForPromotion")) && !emp.getRankId().equals(promotionReportDetailDataItr.getOldRankId())) {
		errorString = "error_employeeRankIsNotSuitableForPromotion";
		employeesString += comma + emp.getName();
		comma = ", ";
		continue;
	    }

	    if ((errorString.isEmpty() || errorString.equals("error_employeeJobIsNotSuitableForPromotion")) && !emp.getJobId().equals(promotionReportDetailDataItr.getOldJobId())) {
		errorString = "error_employeeJobIsNotSuitableForPromotion";
		employeesString += comma + emp.getName();
		comma = ", ";
		continue;
	    }

	    // check for old degree (current)
	    if ((errorString.isEmpty() || errorString.equals("error_employeeDegreeNotFound")) && promotionReportDetailDataItr.getOldDegreeId() == null) {
		errorString = "error_employeeDegreeNotFound";
		employeesString += comma + emp.getName();
		comma = ", ";
		continue;
	    }

	    // check for non-existence of new job.
	    if ((errorString.isEmpty() || errorString.equals("error_choosePromotionJob")) && promotionReportDetailDataItr.getNewJobId() == null) {
		if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
		    throw new BusinessException("error_jobReturnedMandatory");
		} else {
		    errorString = "error_choosePromotionJob";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

	    }
	    // check for repeated job.
	    if ((errorString.isEmpty() || errorString.equals("error_repeatedPromotionJobParameterized")) && !promotionReportData.getScaleUpFlagBoolean() && promotionReportDetailDataItr.getNewJobId() != null && !jobsIds.add(promotionReportDetailDataItr.getNewJobId())) {
		errorString = "error_repeatedPromotionJobParameterized";
		employeesString += comma + emp.getName();
		comma = ", ";
		continue;
	    }

	    if ((errorString.isEmpty() || errorString.equals("error_freezedJobIsMandatoryParameterized")) && promotionReportData.getScaleUpFlagBoolean() && promotionReportDetailDataItr.getFreezedJobId() == null) {
		errorString = "error_freezedJobIsMandatoryParameterized";
		employeesString += comma + emp.getName();
		comma = ", ";
		continue;
	    }

	    // check for repeated freezed job.
	    if ((errorString.isEmpty() || errorString.equals("error_repeatedFreezeJobParameterized")) && promotionReportDetailDataItr.getFreezedJobId() != null && !jobsToFreezeIds.add(promotionReportDetailDataItr.getFreezedJobId())) {
		errorString = "error_repeatedFreezeJobParameterized";
		employeesString += comma + emp.getName();
		comma = ", ";
		continue;
	    }

	    JobData newJobData = promotionReportDetailDataItr.getNewJobId() == null ? null : JobsService.getJobById(promotionReportDetailDataItr.getNewJobId());
	    // Chosen job rank doesn't match Promotion report rank.
	    if (promotionReportData.getScaleUpFlagBoolean() == null || !promotionReportData.getScaleUpFlagBoolean()) {
		if ((errorString.isEmpty() || errorString.equals("error_choosenJobNotMatchWithNewRank")) && newJobData != null && !newJobData.getRankId().equals(promotionReportDetailDataItr.getNewRankId())) {
		    errorString = "error_choosenJobNotMatchWithNewRank";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_choosenJobInvalidStatus")) && newJobData != null && !newJobData.getStatus().equals(JobStatusEnum.VACANT.getCode())) {
		    errorString = "error_choosenJobInvalidStatus";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_choosenJobInvalidRegion")) && newJobData != null && !newJobData.getRegionId().equals(emp.getPhysicalRegionId())) {
		    errorString = "error_choosenJobInvalidRegion";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

	    } else {
		if (promotionReportDetailDataItr.getFreezedJobId() != null) {
		    JobData jobToBeFreezed = JobsService.getJobById(promotionReportDetailDataItr.getFreezedJobId());
		    jobsToFreeze.add(jobToBeFreezed);
		    newJobData.setNewRankId(promotionReportDetailDataItr.getNewRankId());
		    jobsToScale.add(newJobData);
		    if ((errorString.isEmpty() || errorString.equals("error_choosenFreezedJobNotMatchWithNewRank")) && !jobToBeFreezed.getRankId().equals(promotionReportDetailDataItr.getNewRankId())) {
			errorString = "error_choosenFreezedJobNotMatchWithNewRank";
			employeesString += comma + emp.getName();
			comma = ", ";
			continue;
		    }
		    if ((errorString.isEmpty() || errorString.equals("error_choosenFreezedJobInvalidStatus")) && !jobToBeFreezed.getStatus().equals(JobStatusEnum.VACANT.getCode())) {
			errorString = "error_choosenFreezedJobInvalidStatus";
			employeesString += comma + emp.getName();
			comma = ", ";
			continue;
		    }
		}
	    }

	    // Exceptional Promotion case
	    if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode())) {
		if (promotionReportDetailDataItr.getReferring() == null || promotionReportDetailDataItr.getReferring().trim().isEmpty())
		    throw new BusinessException("error_refferingToIsMandatory");

		if (promotionReportDetailDataItr.getExceptionalPromotionReason() == null || promotionReportDetailDataItr.getExceptionalPromotionReason().trim().isEmpty())
		    throw new BusinessException("error_exceptionalPromotionReason");

		if (promotionReportData.getPromotionDate() == null)
		    throw new BusinessException("error_exceptionalPromotionDate");

		if (!HijriDateService.isValidHijriDate(promotionReportData.getPromotionDate()))
		    throw new BusinessException("error_invalidExceptionalPromotionDate");

		Date lastPromotionDate = emp.getLastPromotionDate() != null ? emp.getLastPromotionDate() : emp.getRecruitmentDate();
		if (promotionReportData.getPromotionDate().after(HijriDateService.getHijriSysDate()) || promotionReportData.getPromotionDate().before(lastPromotionDate))
		    throw new BusinessException("error_promotionDateViolation");

		if (promotionReportDetailDataItr.getExternalDecisionNumber() == null || promotionReportDetailDataItr.getExternalDecisionNumber().trim().isEmpty())
		    throw new BusinessException("error_ministerialDecisionMandatory");

		if (promotionReportDetailDataItr.getExternalDecisionDate() == null)
		    throw new BusinessException("error_ministryDecisionDateMandatory");

		if (!HijriDateService.isValidHijriDate(promotionReportDetailDataItr.getExternalDecisionDate()))
		    throw new BusinessException("error_invalidMinistryDecisionDate");

		if (promotionReportDetailDataItr.getExternalDecisionDate().after(HijriDateService.getHijriSysDate()))
		    throw new BusinessException("error_externalDecisionDateViolation");
	    } else if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
		if (promotionReportDetailDataItr.getReferring() == null || promotionReportDetailDataItr.getReferring().trim().isEmpty())
		    throw new BusinessException("error_refferingToIsMandatory");
		if (promotionReportDetailDataItr.getRemarks() == null || promotionReportDetailDataItr.getRemarks().trim().isEmpty())
		    throw new BusinessException("error_requestJustificationsMandatory");
	    } else {

		/*
		 * if (promotionReportData.getStatus().equals(PromotionReportStatusEnum.UNDER_REVIEW.getCode())) { if ((errorString.isEmpty() || errorString.equals("error_approveAllFields")) && (promotionReportDetailDataItr.getQualificationApprovedId() == null || promotionReportDetailDataItr.getFileApprovedId() == null || promotionReportDetailDataItr.getTrainingApprovedId() == null)) { errorString = "error_approveAllFields"; employeesString += comma + emp.getName(); comma = ", "; continue; } }
		 */

		if ((errorString.isEmpty() || errorString.equals("error_invalidPromotionDateAfterDueDate")) && promotionReportData.getPromotionDate().before(promotionReportDetailDataItr.getPromotionDueDate())) {
		    errorString = "error_invalidPromotionDateAfterDueDate";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_judgementRequired")) && promotionReportDetailDataItr.getJudgmentFlagBoolean() != null && promotionReportDetailDataItr.getJudgmentFlagBoolean()) {
		    errorString = "error_judgementRequired";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_invalidPromotionDrugTestResult")) && promotionReportDetailDataItr.getMedicalTest() != null && (!promotionReportDetailDataItr.getMedicalTest().equals(PromotionMedicalTestStatusEnum.EXEMPT.getCode()) && !promotionReportDetailDataItr.getMedicalTest().equals(PromotionMedicalTestStatusEnum.NEGATIVE.getCode()))) {
		    errorString = "error_invalidPromotionDrugTestResult";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_medicalTestExemptionReasonIsRequiredParam")) && promotionReportDetailDataItr.getMedicalTest() != null && promotionReportDetailDataItr.getMedicalTest().equals(PromotionMedicalTestStatusEnum.EXEMPT.getCode()) && (promotionReportDetailDataItr.getMedicalTestExemptionReason() == null || promotionReportDetailDataItr.getMedicalTestExemptionReason().trim().isEmpty())) {
		    errorString = "error_medicalTestExemptionReasonIsRequiredParam";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		/*
		 * if ((errorString.isEmpty() || errorString.equals("error_qualificationRequired")) && (promotionReportDetailDataItr.getQualificationDegree() == null || promotionReportDetailDataItr.getQualificationDegree() <= 0.0 || promotionReportDetailDataItr.getQualificationDegree() > SOLDIERS_QUALIFICATION_DEGREE_MAX)) { errorString = "error_qualificationRequired"; employeesString += comma + emp.getName(); comma = ", "; continue; } if ((errorString.isEmpty() ||
		 * errorString.equals("error_fileDegreeRequired")) && (promotionReportDetailDataItr.getFileDegree() == null || promotionReportDetailDataItr.getFileDegree() > SOLDIERS_FILE_DEGREE_MAX || promotionReportDetailDataItr.getFileDegree() < SOLDIERS_FILE_DEGREE_MIN)) { errorString = "error_fileDegreeRequired"; employeesString += comma + emp.getName(); comma = ", "; continue; } if ((errorString.isEmpty() || errorString.equals("error_trainingDegreeRequired")) &&
		 * (promotionReportDetailDataItr.getTrainingDegree() == null || promotionReportDetailDataItr.getTrainingDegree() <= 0.0 || promotionReportDetailDataItr.getTrainingDegree() > SOLDIERS_TRAINING_DEGREE_MAX)) { errorString = "error_trainingDegreeRequired"; employeesString += comma + emp.getName(); comma = ", "; continue; }
		 */
		/*
		 * if (emp.getGender() != null) { if ((errorString.isEmpty() || errorString.equals("error_promotionDegreeRequired")) && (promotionReportDetailDataItr.getPromotionDegree() == null || (emp.getGender() == GendersEnum.FEMALE.getCode() && promotionReportDetailDataItr.getPromotionDegree() < PROMOTION_DEGREE))) { errorString = "error_promotionDegreeRequired"; employeesString += comma + emp.getName(); comma = ", "; continue; } } else
		 */
		if ((errorString.isEmpty() || errorString.equals("error_empGenderNotFound")) && emp.getGender() == null) {
		    errorString = "error_empGenderNotFound";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
	    }
	} // End of loop.

	if (!errorString.isEmpty() && !employeesString.isEmpty())
	    throw new BusinessException(errorString, new Object[] { employeesString });

	// Validate Jobs Balance
	if (promotionReportData.getScaleUpFlagBoolean())
	    JobsService.validateJobsToFreezeOccupiedJobsToScale(jobsToFreeze, jobsToScale);
    }

    /**
     * Check all civilians promotion data are valid to promote like job, degrees, .. etc
     * 
     * @param promotionReportData
     *            The promotion report Data
     * @param promotionReportDetailDataList
     *            The list of promotion report detail to validate
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validatePromotionCivilianReportDetails(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList) throws BusinessException {

	HashSet<Long> jobsIds = new HashSet<Long>();
	boolean allReportEmpsUnCandidateFlg = true;

	String employeesString = "", comma = "";
	String errorString = "";
	String extraErrorParameterString = "";

	HashMap<Long, PromotionReportDetailData> employeesPromotionReportDetailDataMap = new HashMap<Long, PromotionReportDetailData>();
	Long[] empsIds = new Long[promotionReportDetailDataList.size()];

	for (int i = 0; i < promotionReportDetailDataList.size(); i++) {
	    empsIds[i] = promotionReportDetailDataList.get(i).getEmpId();
	    employeesPromotionReportDetailDataMap.put(empsIds[i], promotionReportDetailDataList.get(i));
	}

	List<EmployeeData> tempEmployees = EmployeesService.getEmployeesByEmpsIds(empsIds);

	for (EmployeeData emp : tempEmployees) {
	    PromotionReportDetailData promotionReportDetailDataItr = employeesPromotionReportDetailDataMap.get(emp.getEmpId());
	    if (promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()) || promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode())) {

		allReportEmpsUnCandidateFlg = false;

		if ((errorString.isEmpty() || errorString.equals("error_employeeStatusIsNotSuitableForPromotion")) && (emp.getStatusId() < EmployeeStatusEnum.ON_DUTY.getCode() || emp.getStatusId() >= EmployeeStatusEnum.MOVED_EXTERNALLY.getCode())) {
		    errorString = "error_employeeStatusIsNotSuitableForPromotion";
		    employeesString += comma + promotionReportDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		// check for physical unit and official unit assigned.
		if ((errorString.isEmpty() || errorString.equals("error_employeePhysicalUnitNotFound")) && emp.getPhysicalUnitId() == null) {
		    errorString = "error_employeePhysicalUnitNotFound";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_employeeOfficialUnitNotFound")) && emp.getOfficialUnitId() == null) {
		    errorString = "error_employeeOfficialUnitNotFound";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_employeeRankIsNotSuitableForPromotion")) && !emp.getRankId().equals(promotionReportDetailDataItr.getOldRankId())) {
		    errorString = "error_employeeRankIsNotSuitableForPromotion";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_employeeJobIsNotSuitableForPromotion")) && !emp.getJobId().equals(promotionReportDetailDataItr.getOldJobId())) {
		    errorString = "error_employeeJobIsNotSuitableForPromotion";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		// check for old degree (current)
		if ((errorString.isEmpty() || errorString.equals("error_employeeDegreeNotFound")) && promotionReportDetailDataItr.getOldDegreeId() == null) {
		    errorString = "error_employeeDegreeNotFound";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		// check for non-existance of new job.
		if ((errorString.isEmpty() || errorString.equals("error_choosePromotionJob")) && promotionReportDetailDataItr.getNewJobId() == null) {
		    errorString = "error_choosePromotionJob";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		// check for repeated job.
		if ((errorString.isEmpty() || errorString.equals("error_repeatedJobParameterized")) && promotionReportDetailDataItr.getNewJobId() != null && !jobsIds.add(promotionReportDetailDataItr.getNewJobId())) {
		    errorString = "error_repeatedJobParameterized";
		    employeesString += comma + promotionReportDetailDataItr.getNewJobDesc();
		    comma = ", ";
		    continue;
		}
		// Chosen job rank doesn't match Promotion report rank.
		if ((errorString.isEmpty() || errorString.equals("error_choosenJobNotMatchWithNewRank")) && !JobsService.getJobById(promotionReportDetailDataItr.getNewJobId()).getRankId().equals(promotionReportDetailDataItr.getNewRankId())) {
		    errorString = "error_choosenJobNotMatchWithNewRank";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_serviceDegreeRequired")) && (promotionReportDetailDataItr.getServiceDegreeDay() == null || promotionReportDetailDataItr.getServiceDegreeMonth() == null || promotionReportDetailDataItr.getServiceDegreeYear() == null)) {
		    errorString = "error_serviceDegreeRequired";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_qualificationPersonDegree")) && (promotionReportDetailDataItr.getQualificationPersonDegree() == null || promotionReportDetailDataItr.getQualificationPersonDegree().isEmpty())) {
		    errorString = "error_qualificationPersonDegree";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_experienceYearsRequired")) && (promotionReportDetailDataItr.getExperincePresonsDay() == null || promotionReportDetailDataItr.getExperincePresonsMonth() == null || promotionReportDetailDataItr.getExperincePresonsYear() == null)) {
		    errorString = "error_experienceYearsRequired";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_trainingPersonDegreeRequired")) && (promotionReportDetailDataItr.getTrainingDegree() == null || promotionReportDetailDataItr.getTrainingDegree().doubleValue() == 0d)) {
		    errorString = "error_trainingPersonDegreeRequired";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_evaluationDegreeRequired")) && (promotionReportDetailDataItr.getEvaluationDegree() == null || promotionReportDetailDataItr.getEvaluationDegree().doubleValue() == 0d)) {
		    errorString = "error_evaluationDegreeRequired";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_educationDegreeRequired")) && (promotionReportDetailDataItr.getEducationDegree() == null || promotionReportDetailDataItr.getEducationDegree().doubleValue() == 0d)) {
		    errorString = "error_educationDegreeRequired";
		    employeesString += comma + emp.getName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_trainingDegreeMaxValue")) && (promotionReportDetailDataItr.getTrainingDegree().doubleValue() > ETRConfigurationService.getPromotionPersonsTrainingDegreeMax())) {
		    errorString = "error_trainingDegreeMaxValue";
		    employeesString += comma + emp.getName();
		    extraErrorParameterString = ETRConfigurationService.getPromotionPersonsTrainingDegreeMax() + "";
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_evaluationDegreeMaxValue")) && (promotionReportDetailDataItr.getEvaluationDegree().doubleValue() > ETRConfigurationService.getPromotionPersonsEvaluationDegreeMax())) {
		    errorString = "error_evaluationDegreeMaxValue";
		    employeesString += comma + emp.getName();
		    extraErrorParameterString = ETRConfigurationService.getPromotionPersonsEvaluationDegreeMax() + "";
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_educationDegreeMaxValue")) && (promotionReportDetailDataItr.getEducationDegree().doubleValue() > ETRConfigurationService.getPromotionPersonsEducationDegreeMax())) {
		    errorString = "error_educationDegreeMaxValue";
		    employeesString += comma + emp.getName();
		    extraErrorParameterString = ETRConfigurationService.getPromotionPersonsEducationDegreeMax() + "";
		    comma = ", ";
		    continue;
		}
	    } else if ((errorString.isEmpty() || errorString.equals("error_nonCandidateReasonsMandatory")) && promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode()) && (promotionReportDetailDataItr.getNoVacantFlag() == null || promotionReportDetailDataItr.getNoVacantFlag().equals(FlagsEnum.OFF.getCode()))
		    && (promotionReportDetailDataItr.getNonCandidateReasons() == null || promotionReportDetailDataItr.getNonCandidateReasons().isEmpty())) {
		errorString = "error_nonCandidateReasonsMandatory";
		employeesString += comma + promotionReportDetailDataItr.getEmpName();
		comma = ", ";
	    }
	} // End of loop.

	// Common Exceptions.
	if (allReportEmpsUnCandidateFlg)
	    throw new BusinessException("error_candidateEmpsValidation");

	if (!errorString.isEmpty() && !employeesString.isEmpty())
	    throw new BusinessException(errorString, extraErrorParameterString.isEmpty() ? new Object[] { employeesString } : new Object[] { employeesString, extraErrorParameterString });

	validateCandidateSequentially(promotionReportDetailDataList);
	validateUnprecedentsPoint(promotionReportDetailDataList);
    }

    /**
     * Validate candidate sequentially for employees jobs with others.
     * 
     * Candidate sequentially employees MUST take jobs of others (candidates/candidate_sequential)employees at the same promotion report .
     * 
     * @param promotionReportDetailDataList
     *            the list of promotion Report detail data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateCandidateSequentially(List<PromotionReportDetailData> promotionReportDetailDataList) throws BusinessException {
	for (PromotionReportDetailData promotionReportDetailData : promotionReportDetailDataList) {
	    if (promotionReportDetailData.getStatus() == PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()) {
		boolean isEqual = false;
		for (PromotionReportDetailData promotionReportDetailDataItr : promotionReportDetailDataList) {
		    if (!promotionReportDetailData.getId().equals(promotionReportDetailDataItr.getId())) {
			if (promotionReportDetailData.getNewJobId().equals(promotionReportDetailDataItr.getOldJobId()) && (promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()) || promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()))) {
			    isEqual = true;
			    break;
			}
		    }
		}
		if (isEqual == false)
		    throw new BusinessException("error_validateEmpJobCandidateSequentially", new Object[] { promotionReportDetailData.getEmpName() });
	    }
	}
    }

    /**
     * Validate precedent point employees from other employees.
     * 
     * Precedent point check the employees that has the same job and not equal at the total degrees , so the employee who has the highest degrees will be promoted, and others will not.
     * 
     * @param promotionReportDetailDataList
     *            The list of promotion Report detail data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateUnprecedentsPoint(List<PromotionReportDetailData> promotionReportDetailDataList) throws BusinessException {
	for (PromotionReportDetailData promotionReportDetailOuter : promotionReportDetailDataList) {
	    if (promotionReportDetailOuter.getStatus().equals(PromotionCandidateStatusEnum.PRECEDENTED_POINTS.getCode())) {
		if (promotionReportDetailOuter.getNewJobId() == null)
		    throw new BusinessException("error_choosePromotionJob", new Object[] { promotionReportDetailOuter.getEmpName() });

		boolean unPrecedentPointViolationExistFlg = true;
		for (PromotionReportDetailData promotionReportDetailInner : promotionReportDetailDataList) {

		    if (!promotionReportDetailOuter.getId().equals(promotionReportDetailInner.getId())) {
			if (promotionReportDetailOuter.getNewJobId().equals(promotionReportDetailInner.getNewJobId()) && (promotionReportDetailInner.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()) || promotionReportDetailInner.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()))) {
			    unPrecedentPointViolationExistFlg = false;

			    if (promotionReportDetailOuter.getTotalDegree() >= promotionReportDetailInner.getTotalDegree()) {
				throw new BusinessException("error_validatePrecedentPointTotalDegrees", new Object[] { promotionReportDetailOuter.getEmpName() });
			    }
			    break;
			}
		    }
		}
		if (unPrecedentPointViolationExistFlg) {
		    throw new BusinessException("error_validatePrecedentPointCandidates", new Object[] { promotionReportDetailOuter.getEmpName() });
		}
	    }
	}
    }

    /**
     * Validate the conflict between the employees or jobs promotion and other running requests like ( movement, future movement,... etc ).
     * 
     * @param promotionReportData
     *            Promotion report data
     * @param promotionReportDetails
     *            List of promotion report detail data that will be promoted
     * @param instance
     *            The instance of the promotion workflow that has the status of the workflow
     * @throws BusinessException
     *             If any excFeptions or errors occurs
     * 
     */
    private static void validateRunningPromotionRequests(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetails, Long excludedReportId) throws BusinessException {

	List<Long> empsIdsList = new ArrayList<Long>();
	List<Long> jobsIdsList = new ArrayList<Long>();

	for (int i = 0; i < promotionReportDetails.size(); i++) {
	    if (promotionReportDetails.get(i).getEmpId() != null)
		empsIdsList.add(promotionReportDetails.get(i).getEmpId());

	    if (promotionReportDetails.get(i).getNewJobId() != null)
		jobsIdsList.add(promotionReportDetails.get(i).getNewJobId());

	    if (promotionReportDetails.get(i).getFreezedJobId() != null)
		jobsIdsList.add(promotionReportDetails.get(i).getFreezedJobId());
	}

	Long[] empsIds = new Long[empsIdsList.size()];
	empsIds = empsIdsList.size() == 0 ? null : empsIdsList.toArray(empsIds);
	Long[] jobsIds = new Long[jobsIdsList.size()];
	jobsIds = jobsIdsList.size() == 0 ? null : jobsIdsList.toArray(jobsIds);

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(empsIds, jobsIds, TransactionClassesEnum.PROMOTIONS.getCode(), promotionReportData.getPromotionTypeId() == PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode() ? true : false, excludedReportId, CommonService.getRankById(promotionReportData.getRankId()).getCategoryId());
    }

    public static void validatePromotionPrerequisiteEligibility(long empId, Long categoryId, int mode) throws BusinessException {

	if (mode == CategoriesEnum.PERSONS.getCode() && !categoryId.equals(CategoriesEnum.PERSONS.getCode()))
	    throw new BusinessException("error_employeeMustBePerson");
	// 1- validate in running promotion reports !!
	if (getRunningPromotionReportsCount(empId) > 0)
	    throw new BusinessException("error_employeeExistAtPromotionReport");
	// 2- validate in transactions
	if (getPromotionTransactionsCountByEmpId(empId) > 0)
	    throw new BusinessException("error_employeeHasPromotionTransaction");
    }
    /*------------------------------------------------------Queries---------------------------------------------------------*/

    /**
     * Wrapper for {@link #searchPromotionReportDetails(long, long, String, long[])} to get the promotion report detail list by promotion report Id
     * 
     * @param reportId
     *            the promotion report Id
     * @return List of promotion report detail data
     * 
     * @see #searchPromotionReportDetails(long, long, String, long[])
     */
    public static List<PromotionReportDetailData> getPromotionReportDetailsDataByReportId(long reportId) throws BusinessException {
	return searchPromotionReportDetails(reportId, FlagsEnum.ALL.getCode(), null, null, null);
    }

    /**
     * Wrapper for {@link #searchPromotionReportDetails(long, long, String, long[])} to get the promotion report detail list by Employee Id
     * 
     * @param empId
     *            the Employee Id
     * @return List of promotion report detail data
     * @throws BusinessException
     * 
     * @see #searchPromotionReportDetails(long, long, String, long[])
     */
    public static List<PromotionReportDetailData> getPromotionReportDetailsDataByEmpId(long empId) throws BusinessException {
	return searchPromotionReportDetails(FlagsEnum.ALL.getCode(), empId, null, null, null);
    }

    /**
     * Wrapper for {@link #searchPromotionReportDetails(long, long, String, long[])} to get the promotion report detail List by promotion report Id, employee id, employee name, and report status
     * 
     * @param reportId
     *            Promotion report Id
     * @param empId
     *            Employee Id
     * @param empName
     *            Employee name
     * @param status
     *            Status of promotion report
     * @return List of promotion report detail data
     * 
     * @see #searchPromotionReportDetails(long, long, String, long[])
     */
    public static List<PromotionReportDetailData> getPromotionReportDetailsData(long reportId, long empId, String empName, Long[] status) throws BusinessException {
	return searchPromotionReportDetails(reportId, empId, empName, status, null);
    }

    public static List<PromotionReportDetailData> getPromotionReportDetailsDataForDrugsTest(long reportId, Long[] status, Integer[] medicalTestStatuses) throws BusinessException {
	return searchPromotionReportDetails(reportId, FlagsEnum.ALL.getCode(), null, status, medicalTestStatuses);
    }

    /**
     * Get the promotion report detail List by promotion report Id, employee id, employee name, and report status
     * 
     * @param reportId
     *            Promotion report Id
     * @param empId
     *            Employee Id
     * @param empName
     *            Employee Name
     * @param Status
     *            Status of promotion report
     * @return List of promotion report detail data
     * @throws BusinessException
     */
    private static List<PromotionReportDetailData> searchPromotionReportDetails(long reportId, long empId, String empName, Long[] status, Integer[] medicalTestStatuses) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_REPORT_ID", reportId);
	    qParams.put("P_EMP_ID", empId);

	    if (status != null && status.length > 0) {
		qParams.put("P_STATUS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUS", status);
	    } else {
		qParams.put("P_STATUS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    if (medicalTestStatuses != null && medicalTestStatuses.length > 0) {
		qParams.put("P_MEDICAL_TEST_STATUSES_FLAG", (medicalTestStatuses[0] == null ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode()));
		qParams.put("P_MEDICAL_TEST_STATUSES", medicalTestStatuses);
	    } else {
		qParams.put("P_MEDICAL_TEST_STATUSES_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_MEDICAL_TEST_STATUSES", new Integer[] { FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");

	    return DataAccess.executeNamedQuery(PromotionReportDetailData.class, QueryNamesEnum.HCM_GET_PROMOTION_REPORT_DETAIL_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<Long> getFreezedJobsIdsInPromotionReportDetails(long newRankId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_NEW_RANK_ID", newRankId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_GET_FREEZED_JOBS_IDS_IN_PROMOTION_REPORT_DETAILS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<PromotionReportDetailData> searchPromotionReportDetailsBySocialIds(String[] socialIds) throws BusinessException {

	List<Object> queryInfo = new ArrayList<Object>();
	queryInfo.add(QueryNamesEnum.HCM_GET_PROMOTION_REPORT_DETAIL_DATA_BY_SOCIAL_IDS.getCode());

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_EMPS_SOCIAL_IDS", socialIds);
	queryInfo.add(qParams);

	return getManyEntities(PromotionReportDetailData.class, queryInfo, socialIds);

    }

    /**
     * Get all opened reports using employees and jobs in report
     * 
     * @param employeesIds
     *            List of employees Id
     * @param jobsIds
     *            List of jobs Id
     * @param excludedReportId
     *            Promotion report Id
     * @return List of PromotionReportDetail in opened reports
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static List<PromotionReportDetail> searchRunningReports(Long[] employeesIds, Long[] jobsIds, Long excludedReportId, Long[] promotionTypesIds) throws BusinessException {
	if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
	    return new ArrayList<PromotionReportDetail>();

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    if (employeesIds != null && employeesIds.length > 0)
		qParams.put("P_EMPS_IDS", employeesIds);
	    else
		qParams.put("P_EMPS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });

	    if (jobsIds != null && jobsIds.length > 0)
		qParams.put("P_JOBS_IDS", jobsIds);
	    else
		qParams.put("P_JOBS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });

	    if (promotionTypesIds != null && promotionTypesIds.length > 0) {
		qParams.put("P_PROMOTION_TYPES_IDS", promotionTypesIds);
		qParams.put("P_PROMOTION_TYPES_IDS_FLAG", FlagsEnum.ON.getCode());
	    } else {
		qParams.put("P_PROMOTION_TYPES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
		qParams.put("P_PROMOTION_TYPES_IDS_FLAG", FlagsEnum.ALL.getCode());
	    }

	    qParams.put("P_EXCLUDED_REPORT_ID", excludedReportId == null ? (long) FlagsEnum.ALL.getCode() : excludedReportId);

	    return DataAccess.executeNamedQuery(PromotionReportDetail.class, QueryNamesEnum.HCM_GET_PROMOTION_OPENED_REPORT_DETAILS.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long getPromotionReportDetailsStatusesCount(long reportId, Long[] statuses, Long[] excludedIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_REPORT_ID", reportId);
	    qParams.put("P_REPORT_DETAILS_STATUSES", statuses);

	    if (excludedIds != null && excludedIds.length > 0) {
		qParams.put("P_EXCLUDED_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EXCLUDED_IDS", excludedIds);
	    } else {
		qParams.put("P_EXCLUDED_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EXCLUDED_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_PROMOTION_REPORT_DETAIL_STATUSES.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Search for seniority points for civilians.
     * 
     * @param year
     * @param month
     * @return Seniority points of civilians by year and month
     * 
     */
    private static PromotionSeniortyPoints searchPromotionSeniortyPoints(double year, double month) {

	Map<String, Object> qParams = new HashMap<String, Object>();

	qParams.put("P_YEAR", year);
	qParams.put("P_MONTH", month);
	try {
	    return DataAccess.executeNamedQuery(PromotionSeniortyPoints.class, QueryNamesEnum.HCM_GET_PROMOTION_SENIORTY_POINTS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    return null;
	} catch (Exception e) {
	    return null;
	}
    }

    /*------------------------------------------------------Utilities------------------------------------------------------*/

    /**
     * Change officers collective report details (status, decision number, and decision date)
     * 
     * @param promotionReportDetailDataList
     *            collective officers report details list
     * @param status
     *            report detail status
     * @param collectiveOfficersDecisionNumber
     *            officers decision number
     * @param collectiveOfficersDecisionDate
     *            officers decision date
     */
    private static void changeOfficersCollectiveReportDetails(List<PromotionReportDetailData> promotionReportDetailDataList, long status, String collectiveOfficersDecisionNumber, Date collectiveOfficersDecisionDate) {
	for (int i = 0; i < promotionReportDetailDataList.size(); i++) {
	    promotionReportDetailDataList.get(i).setStatus(status);
	    promotionReportDetailDataList.get(i).setExternalDecisionNumber(collectiveOfficersDecisionNumber);
	    promotionReportDetailDataList.get(i).setExternalDecisionDate(collectiveOfficersDecisionDate);
	}
    }

    /**
     * Get the difference between two hijri dates in years, months, and days .
     * 
     * @param firstDate
     * @param secondDate
     * @return ArrayList has 3 values difference between days as index 0 , months as index 1 and years as index 2
     */
    // TODO: move to HijriService after making sure it's true and valid
    private static ArrayList<Integer> getHijiridateDiff(Date startDate, Date endDate) {
	ArrayList<Integer> result = new ArrayList<Integer>();
	if (startDate == null || endDate == null) {
	    result.add(0, 0);
	    result.add(1, 0);
	    result.add(2, 0);
	    return result;
	}

	int totalDays = Math.abs(HijriDateService.hijriDateDiff(startDate, endDate));
	int totalMonths = 0;
	int totalYears = 0;
	if (totalDays > 354) {
	    totalYears = totalDays / 354;
	    totalDays = totalDays % 354;
	}
	if (totalDays > 30) {
	    totalMonths = totalDays / 30;
	    totalDays = totalDays % 30;
	}
	result.add(0, totalDays);
	result.add(1, totalMonths);
	result.add(2, totalYears);

	return result;
    }

    /**
     * Calculate some soldiers degrees automatically like seniority degree and field service degrees based on some business rules
     * 
     * @param promotionReportDetailData
     *            The promotion report detail data
     * @param empData
     *            Employee data to get the last promotion due date
     * @param promotionReportDueDate
     *            Promotion Report due date
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    /*
     * @Deprecated private static void calculateSoldierGrads(PromotionReportDetailData promotionReportDetailData, EmployeeData empData, Date promotionReportDueDate) throws BusinessException { double seniortyDegree, fieldServiceDegree; // calculate Seniority Degree if (empData.getPromotionDueDate() != null) { seniortyDegree = HijriDateService.hijriDateDiffByHijriYear(empData.getPromotionDueDate(), promotionReportDueDate) * 2.0; if (seniortyDegree < 18.0)
     * promotionReportDetailData.setSeniorityDegree(seniortyDegree); else promotionReportDetailData.setSeniorityDegree(18.0); } else promotionReportDetailData.setSeniorityDegree(0.0); // calculate Field service Degree if (empData.getLastPromotionDate() != null || empData.getRecruitmentDate() != null) { fieldServiceDegree = HijriDateService.hijriDateDiffByHijriYear(empData.getLastPromotionDate() != null ? empData.getLastPromotionDate() : empData.getRecruitmentDate(), promotionReportDueDate); int
     * type = JobsService.getJobById(empData.getJobId()).getGeneralType(); // if technical fieldDegree = fieldDegree * 0.8 , others (normal, filed) fieldDegree = fieldDegree * 1 if (type == JobTypesEnum.TECHNICAL.getCode()) fieldServiceDegree = fieldServiceDegree * 0.8;
     * 
     * if (fieldServiceDegree < 4.0) promotionReportDetailData.setFieldServiceDegree(fieldServiceDegree); else promotionReportDetailData.setFieldServiceDegree(4.0);
     * 
     * } else promotionReportDetailData.setFieldServiceDegree(0.0);
     * 
     * promotionReportDetailData.setSeniorityDegree(Double.valueOf(new DecimalFormat("#.##").format(promotionReportDetailData.getSeniorityDegree()))); promotionReportDetailData.setFieldServiceDegree(Double.valueOf(new DecimalFormat("#.##").format(promotionReportDetailData.getFieldServiceDegree())));
     * 
     * }
     */

    /**
     * Calculate seniority degree for civilians according to different between employee last promotion date and promotion report date
     * 
     * @param promotionReportDetailData
     *            The promotion report detail data
     * @param empData
     *            Employee data to get the last promotion date or recruitment date
     * @param promotionReportDueDate
     *            The report date
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see #getHijiridateDiff(Date, Date)
     */
    private static double calculateCiviliansSeniorityDegrees(Date promotionDueDate, Date promotionReportDate) {
	double seniorityDegrees = 0.0;
	double seniortyYear, seniortyMonth, seniortyDays;
	ArrayList<Integer> hijiriDateDiff;

	if (promotionDueDate != null && !promotionReportDate.before(promotionDueDate))
	    hijiriDateDiff = getHijiridateDiff(promotionReportDate, promotionDueDate);
	else
	    return 0.0;

	seniortyYear = hijiriDateDiff.get(2);
	seniortyMonth = hijiriDateDiff.get(1);
	seniortyDays = hijiriDateDiff.get(0);

	if (seniortyDays > 28) {
	    seniortyMonth += 1;
	    seniortyDays = 0;
	    if (seniortyMonth == 12) {
		seniortyYear += 1;
		seniortyMonth = 0;
	    }
	}
	if (seniortyYear >= 0 && seniortyYear < ETRConfigurationService.getPromotionPersonsSeniortyYearsMax())
	    seniorityDegrees = searchPromotionSeniortyPoints(seniortyYear, seniortyMonth).getPoints();
	else if (seniortyYear >= ETRConfigurationService.getPromotionPersonsSeniortyYearsMax())
	    seniorityDegrees = ETRConfigurationService.getPromotionPersonsSeniortyPointsMax();

	return seniorityDegrees;
    }

    /**
     * handle Promotion bonus for civilians only (bonusFlag can be checked or unchecked and we need to update newDegree for both cases)
     * 
     * @param promotionReportDetailData
     *            promotion report detail data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void handlePromotionCiviliansBonus(PromotionReportDetailData promotionReportDetailData) throws BusinessException {

	if (promotionReportDetailData.getBonusFlagBoolean()) {
	    PayrollSalary newPayrollSalaryWithBonus = PayrollsService.getPayrollSalary(promotionReportDetailData.getNewRankId(), promotionReportDetailData.getNewDegreeId() + 1);
	    if (newPayrollSalaryWithBonus == null) {
		promotionReportDetailData.setBonusFlag(FlagsEnum.OFF.getCode());
		throw new BusinessException("error_bonusFlagNotAllowed");
	    }
	    promotionReportDetailData.setNewDegreeId(newPayrollSalaryWithBonus.getDegreeId());
	} else {
	    PayrollSalary oldPayrollSalary = PayrollsService.getPayrollSalary(promotionReportDetailData.getOldRankId(), promotionReportDetailData.getOldDegreeId());
	    PayrollSalary newPayrollSalaryWithoutBonus = PayrollsService.getPayrollNewSalary(promotionReportDetailData.getNewRankId(), oldPayrollSalary.getBasicSalary());
	    promotionReportDetailData.setNewDegreeId(newPayrollSalaryWithoutBonus.getDegreeId());
	}
    }

    /**
     * Get ranks list associated with category id and report rank id
     * 
     * @param categoryId
     *            category id
     * @param reportRankId
     *            report rank id
     * @return list of rank ids list
     */
    public static List<Long> getRanksByReportRankIdAndCategoryId(Long categoryId, Long reportRankId) {
	List<Long> ranksIds = new ArrayList<Long>();
	if (categoryId == CategoriesEnum.PERSONS.getCode()) {
	    if (reportRankId == CivilianReportRanksEnum.THE_HIGHER_RANKS.getCode()) {
		ranksIds.add(RanksEnum.THIRTEENTH.getCode());
		ranksIds.add(RanksEnum.TWELFTH.getCode());
		ranksIds.add(RanksEnum.ELEVENTH.getCode());
		ranksIds.add(RanksEnum.TENTH.getCode());
	    } else if (reportRankId == CivilianReportRanksEnum.FROM_FIRST_TO_FOURTH.getCode()) {
		ranksIds.add(RanksEnum.FIRST.getCode());
		ranksIds.add(RanksEnum.SECOND.getCode());
		ranksIds.add(RanksEnum.THIRD.getCode());
		ranksIds.add(RanksEnum.FOURTH.getCode());
	    } else {
		ranksIds.add(RanksEnum.FIFTH.getCode());
		ranksIds.add(RanksEnum.SIXTH.getCode());
		ranksIds.add(RanksEnum.SEVENTH.getCode());
		ranksIds.add(RanksEnum.EIGHTH.getCode());
		ranksIds.add(RanksEnum.NINTH.getCode());
	    }
	} else if (reportRankId != null) {
	    ranksIds.add(reportRankId);
	}

	return ranksIds;
    }

    /**
     * handle jobs of promotion report details for soldiers
     * 
     * @param promotionReportDetailData
     *            promotion report detail data list
     * @param scalupFlag
     *            scale up flag of promotionReportData
     * @throws BusinessException
     */
    public static void handleScaleUpJob(PromotionReportDetailData promotionReportDetailData, boolean scalupFlag) throws BusinessException {
	try {
	    if (scalupFlag) {
		String newJobCode = promotionReportDetailData.getNewRankId() + promotionReportDetailData.getOldJobCode().substring(3);
		promotionReportDetailData.setNewJobId(promotionReportDetailData.getOldJobId());
		promotionReportDetailData.setNewJobCode(newJobCode);
		promotionReportDetailData.setNewJobDesc(promotionReportDetailData.getOldJobDesc());
	    } else {
		promotionReportDetailData.setNewJobId(null);
		promotionReportDetailData.setNewJobCode(null);
		promotionReportDetailData.setNewJobDesc(null);

		promotionReportDetailData.setFreezedJobId(null);
		promotionReportDetailData.setFreezedJobCode(null);
		promotionReportDetailData.setFreezedJobName(null);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*----------------------------------------------------- Promotions Transactions ---------------------------------------------*/

    /*------------------------------------------------------Operations------------------------------------------------------*/

    /**
     * Construct promotion transaction list for officers , soldiers or civilians based on their promotion status ( royal order issued ,candidate, or candidate sequentially ), and based on rules applied on each category.
     * 
     * @param promotionReportData
     *            The data of the main promotion report that determine the rank and report data
     * @param promotionReportDetailDataList
     *            The report detail list of the promotion report data which construct transactions if the reportDetial status 'ROYAL_OREDER_ISSUED,CANDIDATE , CANDIDATE_SEQUENTIALLY'
     * @param smTaskOriginalId
     *            Manager Report Id used to get the direct manger Id for this report
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @return List of promotion transaction data were constructed in function
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    private static List<PromotionTransactionData> constructPromotionTransactionList(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, Long smTaskOriginalId, CustomSession... useSession) throws BusinessException {

	List<PromotionTransactionData> promotionTransactionDataList = new ArrayList<PromotionTransactionData>();

	try {

	    if (!promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode()) && promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && promotionReportData.getPromotionDate().after(HijriDateService.getHijriSysDate()))
		throw new BusinessException("error_promotionDateAndDecisionDateViolation");

	    Map<Long, UnitData> unitsMap = new HashMap<Long, UnitData>();
	    Map<Long, PayrollSalary> oldPayrollSalaryMap = new HashMap<Long, PayrollSalary>();
	    Map<Long, PayrollSalary> newPayrollSalaryMap = new HashMap<Long, PayrollSalary>();

	    HashMap<Long, PromotionReportDetailData> employeesPromotionReportDetailDataMap = new HashMap<Long, PromotionReportDetailData>();
	    Long[] empsIds = new Long[promotionReportDetailDataList.size()];

	    for (int i = 0; i < promotionReportDetailDataList.size(); i++) {
		empsIds[i] = promotionReportDetailDataList.get(i).getEmpId();
		employeesPromotionReportDetailDataMap.put(empsIds[i], promotionReportDetailDataList.get(i));
	    }
	    List<EmployeeData> tempEmployees = EmployeesService.getEmployeesByEmpsIds(empsIds);

	    for (EmployeeData emp : tempEmployees) {
		PromotionReportDetailData promotionReportDetailDataItr = employeesPromotionReportDetailDataMap.get(emp.getEmpId());
		// construct only details with status 'ROYAL_OREDER_ISSUED, CANDIDATE, CANDIDATE_SEQUENTIALLY' those which must make transaction
		if (promotionReportDetailDataItr.getStatus() != null && (promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.ROYAL_OREDER_ISSUED.getCode()) || promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()) || promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()))) {

		    PromotionTransactionData promotionTransactionData = new PromotionTransactionData();

		    if (emp.getServiceTerminationDate() != null)
			throw new BusinessException("error_terminationPormotionConfilct", new Object[] { emp.getName() });

		    if (!promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode()) && !promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode()) && emp.getPromotionDueDate().after(HijriDateService.getHijriSysDate()))
			throw new BusinessException("error_decisionDateDueViolation", new Object[] { emp.getName() });

		    JobData oldJob = JobsService.getJobById(promotionReportDetailDataItr.getOldJobId());

		    JobData newJob;
		    if (emp.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()))
			newJob = oldJob;
		    else
			newJob = JobsService.getJobById(promotionReportDetailDataItr.getNewJobId());
		    // promotion Id set by addPromotionsTransactions
		    promotionTransactionData.setPromotionTypeId(promotionReportData.getPromotionTypeId());
		    promotionTransactionData.setPromotionDesc(promotionReportData.getPromotionTypeDesc());
		    promotionTransactionData.setExternalCopies(promotionReportData.getExternalCopies());
		    promotionTransactionData.setInternalCopies(promotionReportData.getInternalCopies());

		    promotionTransactionData.setReportDetailId(promotionReportDetailDataItr.getId());
		    promotionTransactionData.setEmpId(promotionReportDetailDataItr.getEmpId());
		    promotionTransactionData.setName(promotionReportDetailDataItr.getEmpName());
		    promotionTransactionData.setOldRankId(promotionReportDetailDataItr.getOldRankId());
		    promotionTransactionData.setOldRankDesc(promotionReportDetailDataItr.getOldRankDesc());
		    promotionTransactionData.setNewRankId(promotionReportDetailDataItr.getNewRankId());
		    promotionTransactionData.setNewRankDesc(promotionReportDetailDataItr.getNewRankDesc());
		    promotionTransactionData.setRankTitleId(promotionReportDetailDataItr.getRankTitleId());
		    promotionTransactionData.setBasedOnTransactionId(promotionReportDetailDataItr.getBasedOnTransactionId());

		    // Exceptional Promotions Data
		    promotionTransactionData.setExternalDecisionNumber(promotionReportDetailDataItr.getExternalDecisionNumber());
		    promotionTransactionData.setExternalDecisionDateString(promotionReportDetailDataItr.getExternalDecisionDateString());
		    promotionTransactionData.setReferring(promotionReportDetailDataItr.getReferring());
		    promotionTransactionData.setExceptionalPromotionReason(promotionReportDetailDataItr.getExceptionalPromotionReason());

		    if (promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
			promotionTransactionData.setAttachments(promotionReportData.getAttachments());
		    } else if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
			promotionTransactionData.setAttachments(promotionReportDetailDataItr.getAttachments());
		    }

		    promotionTransactionData.setOldJobId(promotionReportDetailDataItr.getOldJobId());
		    promotionTransactionData.setOldJobClassCode(promotionReportDetailDataItr.getOldJobClassCode());
		    promotionTransactionData.setOldJobClassDesc(promotionReportDetailDataItr.getOldJobClassDesc());
		    promotionTransactionData.setOldJobCode(promotionReportDetailDataItr.getOldJobCode());
		    promotionTransactionData.setOldJobDesc(promotionReportDetailDataItr.getOldJobDesc());

		    promotionTransactionData.setNewJobId(promotionReportDetailDataItr.getNewJobId());
		    promotionTransactionData.setNewJobClassCode(promotionReportDetailDataItr.getNewJobClassCode());
		    promotionTransactionData.setNewJobClassDesc(promotionReportDetailDataItr.getNewJobClassDesc());
		    promotionTransactionData.setNewJobCode(promotionReportDetailDataItr.getNewJobCode());
		    promotionTransactionData.setNewJobDesc(promotionReportDetailDataItr.getNewJobDesc());

		    promotionTransactionData.setOldUnitId(oldJob.getUnitId());
		    promotionTransactionData.setOldUnitFullName(oldJob.getUnitFullName());
		    promotionTransactionData.setNewUnitId(newJob.getUnitId());
		    promotionTransactionData.setNewUnitFullName(newJob.getUnitFullName());

		    if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && promotionReportData.getScaleUpFlagBoolean()) {
			JobData jobToBeFreezed = JobsService.getJobById(promotionReportDetailDataItr.getFreezedJobId());
			promotionTransactionData.setFreezedJobId(jobToBeFreezed.getId());
			promotionTransactionData.setFreezedJobCode(jobToBeFreezed.getCode());
			promotionTransactionData.setFreezedJobName(jobToBeFreezed.getName());
			promotionTransactionData.setFreezedJobUnitFullName(jobToBeFreezed.getUnitFullName());
		    }

		    promotionTransactionData.setOldDegreeId(promotionReportDetailDataItr.getOldDegreeId());
		    promotionTransactionData.setOldDegreeDesc(promotionReportDetailDataItr.getOldDegreeDesc());
		    promotionTransactionData.setNewDegreeId(promotionReportDetailDataItr.getNewDegreeId());

		    promotionTransactionData.setSocialId(emp.getSocialID());
		    promotionTransactionData.setCategoryId(emp.getCategoryId());
		    // new NewDegree set by promotion service
		    promotionTransactionData.setOldMilitaryNumber(emp.getMilitaryNumber());
		    promotionTransactionData.setNewMilitaryNumber(promotionReportDetailDataItr.getMilitaryNumber());
		    // NewLastPromotionDate set by addPromotionsTransactions
		    promotionTransactionData.setOldLastPromotionDate(emp.getLastPromotionDate());// check this from transaction
		    // NewDueDate set by addPromotionsTransactions
		    promotionTransactionData.setOldDueDate(emp.getPromotionDueDate());

		    // to prevent calling PayrollsService multiple times for the same (rank,degree)
		    if (!oldPayrollSalaryMap.containsKey(promotionReportDetailDataItr.getOldDegreeId()))
			oldPayrollSalaryMap.put(promotionReportDetailDataItr.getOldDegreeId(), PayrollsService.getPayrollSalary(promotionReportDetailDataItr.getOldRankId(), promotionReportDetailDataItr.getOldDegreeId()));

		    if (oldPayrollSalaryMap.get(promotionReportDetailDataItr.getOldDegreeId()) == null)
			throw new BusinessException("error_empOldSalaryNotFound", new Object[] { emp.getName() });

		    promotionTransactionData.setOldSalary(oldPayrollSalaryMap.get(promotionReportDetailDataItr.getOldDegreeId()).getBasicSalary());

		    if (!newPayrollSalaryMap.containsKey(promotionReportDetailDataItr.getNewDegreeId()))
			newPayrollSalaryMap.put(promotionReportDetailDataItr.getNewDegreeId(), PayrollsService.getPayrollSalary(promotionReportDetailDataItr.getNewRankId(), promotionReportDetailDataItr.getNewDegreeId()));
		    // newPayrollSalaryMap.put(emp.getDegreeId(), PayrollsService.getPayrollNewSalary(promotionReportDetailDataItr.getNewRankId(), oldPayrollSalaryMap.get(emp.getDegreeId()).getBasicSalary()));

		    if (newPayrollSalaryMap.get(promotionReportDetailDataItr.getNewDegreeId()) == null)
			throw new BusinessException("error_empNewSalaryNotFound", new Object[] { emp.getName() });

		    promotionTransactionData.setNewSalary(newPayrollSalaryMap.get(promotionReportDetailDataItr.getNewDegreeId()).getBasicSalary());

		    // OriginalDecisionApprovedId and DecisionApprovedId
		    promotionTransactionData.setDecisionApprovedId(smTaskOriginalId);

		    if (promotionReportData.getCategoryId() == CategoriesEnum.PERSONS.getCode()) {
			promotionTransactionData.setOriginalDecisionApprovedId(EmployeesService.getEmployeeDirectManager(smTaskOriginalId).getEmpId());

			// used for civilians promotion (report) region changed
			if (!newJob.getRegionId().equals(oldJob.getRegionId()))
			    promotionTransactionData.setRegionChangedFlag(FlagsEnum.ON.getCode());
			else
			    promotionTransactionData.setRegionChangedFlag(FlagsEnum.OFF.getCode());
		    } else {
			promotionTransactionData.setOriginalDecisionApprovedId(smTaskOriginalId);
		    }
		    // emp.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) &&
		    if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
			PromotionTransactionData promotionTransaction = new PromotionTransactionData();
			promotionTransaction = PromotionsService.getPromotionTransactionById(promotionReportDetailDataItr.getBasedOnTransactionId());
			promotionTransactionData.setNewLastPromotionDateString(promotionTransaction.getOldLastPromotionDateString());
			promotionTransactionData.setNewDueDate(promotionTransaction.getOldDueDate());
		    } else if (emp.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || emp.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode())) {
			promotionTransactionData.setDecisionNumber(promotionReportDetailDataItr.getExternalDecisionNumber().toString());
			promotionTransactionData.setDecisionDate(promotionReportDetailDataItr.getExternalDecisionDate());

			// rank title use only for officers
			promotionTransactionData.setRankTitleId(promotionReportDetailDataItr.getRankTitleId());

			if (emp.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()))
			    promotionTransactionData.setNewLastPromotionDateString(promotionReportDetailDataItr.getPromotionDueDateString());
			else // PRIME_SERGEANTS same as royal order date
			    promotionTransactionData.setNewLastPromotionDateString(promotionReportDetailDataItr.getExternalDecisionDateString());
		    }

		    if (promotionReportData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode())
			    || (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !promotionReportData.getScaleUpFlagBoolean())
			    || promotionReportData.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode())) {

			boolean moveAsManagerFlag = promotionReportData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode()) ? promotionReportDetailDataItr.getManagerFlagBoolean() : false;
			MovementsService.adjustUnitsManagers(unitsMap, emp, newJob.getUnitId(), moveAsManagerFlag, true);
		    }

		    promotionTransactionDataList.add(promotionTransactionData);
		}
	    }

	    // Apply the changes made in he adjust Units Managers method
	    if (promotionReportData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode())
		    || (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !promotionReportData.getScaleUpFlagBoolean())
		    || promotionReportData.getRankId().equals(RanksEnum.PRIME_SERGEANTS.getCode())) {
		doPromotionsUnitsIntegration(unitsMap, useSession);
	    }

	    return promotionTransactionDataList;
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage(), e.getParams());
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Fill Promotion transaction data from Employee data.
     * 
     * New salary and new degree will be set at transaction data for soldiers only .
     * 
     * @param promotionTransactionData
     *            The Promotion Transaction data that will be filled.
     * @param employee
     *            Object of basic Employee data.
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     */
    private static void fillPromotionTransaction(PromotionTransactionData promotionTransactionData, EmployeeData employee) throws BusinessException {

	if (employee == null)
	    throw new BusinessException("error_employeeDataError");

	promotionTransactionData.setEmpId(employee.getEmpId());
	promotionTransactionData.setName(employee.getName());
	promotionTransactionData.setCategoryId(employee.getCategoryId());

	promotionTransactionData.setOldRankId(employee.getRankId());
	promotionTransactionData.setOldRankDesc(employee.getRankDesc());

	promotionTransactionData.setOldJobId(employee.getJobId());
	promotionTransactionData.setOldJobCode(employee.getJobCode());
	promotionTransactionData.setOldJobDesc(employee.getJobDesc());

	promotionTransactionData.setOldMilitaryNumber(employee.getMilitaryNumber());
	if (promotionTransactionData.getNewMilitaryNumber() == null)
	    promotionTransactionData.setNewMilitaryNumber(employee.getMilitaryNumber());

	promotionTransactionData.setOldLastPromotionDate(employee.getLastPromotionDate());
	if (promotionTransactionData.getNewLastPromotionDate() == null)
	    promotionTransactionData.setNewLastPromotionDate(employee.getLastPromotionDate());

	promotionTransactionData.setOldDueDate(employee.getPromotionDueDate());
	promotionTransactionData.setOldUnitId(employee.getOfficialUnitId());
	promotionTransactionData.setOldUnitFullName(employee.getOfficialUnitFullName());
	if (promotionTransactionData.getNewUnitId() == null)
	    promotionTransactionData.setNewUnitId(employee.getOfficialUnitId());

	if (promotionTransactionData.getNewUnitFullName() == null)
	    promotionTransactionData.setNewUnitFullName(employee.getOfficialUnitFullName());

	// officers rank and degree not change
	if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
	    promotionTransactionData.setNewRankId(employee.getRankId());
	    promotionTransactionData.setNewRankDesc(employee.getRankDesc());

	    promotionTransactionData.setOldDegreeId(employee.getDegreeId());
	    promotionTransactionData.setNewDegreeId(employee.getDegreeId());
	    promotionTransactionData.setNewJobCode(employee.getJobCode());
	    promotionTransactionData.setNewJobDesc(employee.getJobDesc());

	    promotionTransactionData.setNewJobId(employee.getJobId());
	}

	if (employee.getCategoryId() == CategoryModesEnum.SOLDIERS.getCode()) {
	    promotionTransactionData.setNewDueDate(calculateNewPromotionDueDate(promotionTransactionData.getNewRankId(), CategoryModesEnum.SOLDIERS.getCode(), null, null, promotionTransactionData.getNewLastPromotionDate(), employee.getGender(), null, null));

	    // new and old degree calculation
	    PayrollSalary payrollOldSalary = PayrollsService.getPayrollSalary(promotionTransactionData.getOldRankId(), employee.getDegreeId());
	    if (payrollOldSalary == null)
		throw new BusinessException("error_empOldSalaryNotFound", new Object[] { employee.getName() });

	    promotionTransactionData.setOldDegreeId(employee.getDegreeId());
	    promotionTransactionData.setOldSalary(payrollOldSalary.getBasicSalary());

	    PayrollSalary payrollNewDemotingSalary = PayrollsService.getPayrollNewSalaryDemoting(promotionTransactionData.getNewRankId(), payrollOldSalary.getBasicSalary());
	    if (payrollNewDemotingSalary == null)
		throw new BusinessException("error_empNewSalaryNotFound", new Object[] { employee.getName() });

	    promotionTransactionData.setNewDegreeId(payrollNewDemotingSalary.getDegreeId());
	    promotionTransactionData.setNewSalary(payrollNewDemotingSalary.getBasicSalary());
	}

	promotionTransactionData.setEflag(FlagsEnum.OFF.getCode());
	promotionTransactionData.setMigFlag(FlagsEnum.OFF.getCode());
    }

    /**
     * Adjust the promotion due date for officers and soldiers by:
     * 
     * <ul>
     * <li>Extending or reducing promotion dates for officers</li>
     * <li>Increasing or reducing ranks for soldiers</li>
     * </ul>
     * 
     * @param promotionTransactionData
     *            The Promotion Transaction data used to adjust the promotion due date
     * @param promotionTransactionDataList
     *            Promotion Transactions List to check for duplicate
     * @param employee
     *            Employee object to get all basic employee data and update its value at the end
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    public static void doPromotionAdjustmentTransaction(PromotionTransactionData promotionTransactionData, List<PromotionTransactionData> promotionTransactionDataList, EmployeeData employee, long loginEmpId) throws BusinessException {

	validatePromotionAdjustment(promotionTransactionData, promotionTransactionDataList, employee);
	fillPromotionTransaction(promotionTransactionData, employee);

	CustomSession session = DataAccess.getSession();
	try {

	    session.beginTransaction();

	    promotionTransactionData.getPromotionTransaction().setSystemUser(loginEmpId + ""); // audit
	    DataAccess.addEntity(promotionTransactionData.getPromotionTransaction(), session);
	    promotionTransactionData.setId(promotionTransactionData.getPromotionTransaction().getId());

	    if (promotionTransactionData.getCategoryId().intValue() == CategoryModesEnum.OFFICERS.getCode()) {
		EmployeesService.updateEmployeePromotionData(employee, null, null, null, null, promotionTransactionData.getNewMilitaryNumber(), promotionTransactionData.getNewDueDate(), null, session);
	    } else if (promotionTransactionData.getCategoryId().intValue() == CategoryModesEnum.SOLDIERS.getCode()) {
		Map<Long, UnitData> unitsMap = new HashMap<Long, UnitData>();

		JobData newJob = JobsService.getJobById(promotionTransactionData.getNewJobId());
		EmployeesService.updateEmployeePromotionData(employee, promotionTransactionData.getNewJobId(), promotionTransactionData.getNewRankId(), null, promotionTransactionData.getNewDegreeId(), null, promotionTransactionData.getNewDueDate(), null, session);
		EmployeeLog log = new EmployeeLog.Builder().setJobId(promotionTransactionData.getNewJobId()).setRankId(promotionTransactionData.getNewRankId()).setDegreeId(promotionTransactionData.getNewDegreeId()).setBasicJobNameId(newJob.getBasicJobNameId())
			.constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), promotionTransactionData.getDecisionNumber(), promotionTransactionData.getDecisionDate(), promotionTransactionData.getNewLastPromotionDate(), DataAccess.getTableName(PromotionTransaction.class))
			.build();
		LogService.logEmployeeData(log, session);

		// old job
		JobData oldJob = JobsService.getJobById(promotionTransactionData.getOldJobId());
		JobsService.changeJobStatus(oldJob, JobStatusEnum.VACANT.getCode(), session);
		// new job
		JobsService.changeJobStatus(newJob, JobStatusEnum.OCCUPIED.getCode(), session);

		// to adjust unit managers
		MovementsService.adjustUnitsManagers(unitsMap, employee, newJob.getUnitId(), false, true);
		doPromotionsUnitsIntegration(unitsMap, session);

		// invalidate Employee inbox And Delegations for DownGrading Soldier
		BusinessWorkflowCooperation.invalidateEmployeesInboxAndDelegations(new Long[] { employee.getEmpId() }, TransactionClassesEnum.PROMOTIONS.getCode(), session);
	    }

	    session.commitTransaction();

	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw new BusinessException(e.getMessage());
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /**
     * construct promotions transactions and save it
     * 
     * @param promotionReportData
     *            The promotion report data
     * @param promotionReportDetailDataList
     *            The promotion report details list that will be managed
     * @param subject
     *            Promotion process name like officer promotion, soldier promotion, ... etc
     * @param smTaskOriginalId
     *            Sign Manager that will sign the promotion decision
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @return list of promotion transaction data were constructed and added to DB in function
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * @throws Exeption
     *             if any general exceptions or errors occurs
     * 
     * 
     */

    public static void doPromotionTransactionEffect(PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, String subject, Long smTaskOriginalId, Long loginEmpId, CustomSession session) throws BusinessException {

	List<PromotionTransactionData> promotionTransactionList = constructPromotionTransactionList(promotionReportData, promotionReportDetailDataList, smTaskOriginalId, session);
	addPromotionsTransactions(promotionReportData, promotionTransactionList, subject, loginEmpId, session);
    }

    /**
     * Generate promotion transaction after constructing it, that also generate new decision number and decisoin date.
     * 
     * @param promotionReportData
     *            The promotion report data that all transactions and details using its data
     * @param promotionTransactionDataList
     *            The promotion transaction data of the report that will added to DB
     * @param subject
     *            Promotion process name like officer promotion, soldier promotion, ... etc
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    private static void addPromotionsTransactions(PromotionReportData promotionReportData, List<PromotionTransactionData> promotionTransactionDataList, String subject, Long loginEmpId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Map<Long, JobData> empsJobsMap = new HashMap<Long, JobData>();

	    StringBuilder jobsToFreezeIds = new StringBuilder("");
	    List<JobData> jobsToScaleUp = new ArrayList<JobData>();
	    String[] etrCorInfo = new String[2];
	    int index = 0;
	    // All soldiers in the report has one decision, but civilians we make decision for every employee
	    if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		etrCorInfo = ETRCorrespondence.doETRCorOut(subject, session);
		promotionReportData.setDecisionNumber(etrCorInfo[0]);
		promotionReportData.setDecisionDateString(etrCorInfo[1]);
	    }

	    List<EmployeeData> editedEmployeeList = new ArrayList<>();
	    if (promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		for (PromotionTransactionData employeeTransactionData : promotionTransactionDataList) {
		    EmployeeData copiedEmployeeData = new EmployeeData();
		    copiedEmployeeData.setEmpId(employeeTransactionData.getEmpId());
		    copiedEmployeeData.setDegreeId(employeeTransactionData.getNewDegreeId());
		    copiedEmployeeData.setRankId(employeeTransactionData.getNewRankId());
		    copiedEmployeeData.setCategoryId(employeeTransactionData.getCategoryId());
		    copiedEmployeeData.setRankDesc(employeeTransactionData.getNewRankDesc());
		    editedEmployeeList.add(copiedEmployeeData);
		}
		RaisesService.raisesModificationsAfterPromotions(editedEmployeeList, promotionReportData.getDueDate(), promotionReportData.getDecisionDate(), promotionReportData.getDecisionNumber(), loginEmpId + "", session);
	    }
	    for (PromotionTransactionData promotionTransactionDataItr : promotionTransactionDataList) {

		EmployeeData employee = EmployeesService.getEmployeeData(promotionTransactionDataItr.getEmpId());
		// TODO: remove this line after testing, as I think it's NOT IMPORTANT at all !!!!!!!!!!!!!!!!!!!!!!!!
		// transaction happened before.
		if (employee.getRankId().equals(promotionTransactionDataItr.getNewRankId()))
		    continue;

		// Check for validate with Extensions
		if (doPromotionsTerminationsIntegration(employee)) {
		    Date terminationDueDate = TerminationsService.calculateEmpTerminationDueDate(promotionReportData.getCategoryId(), promotionTransactionDataItr.getNewRankId(), employee.getBirthDate());
		    employee.setServiceTerminationDueDate(terminationDueDate);
		}

		if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && promotionReportData.getScaleUpFlagBoolean()) {
		    JobData jobData = JobsService.getJobById(promotionTransactionDataItr.getNewJobId());
		    jobData.setNewRankId(promotionTransactionDataItr.getNewRankId());
		    jobsToScaleUp.add(jobData);

		    jobsToFreezeIds.append(promotionTransactionDataItr.getFreezedJobId() + ",");
		}

		if (promotionReportData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode()))
		    etrCorInfo = ETRCorrespondence.doETRCorOut(subject, session);

		// all cases regarding soldiers and civilians Flag ON except raise and lower rank for soldiers and officers
		promotionTransactionDataItr.setEflag(subject == null ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode());
		promotionTransactionDataItr.setMigFlag(FlagsEnum.OFF.getCode());

		// decisionNumber and decisionDate for officers order number and order date from external decision
		if (!promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		    promotionTransactionDataItr.setDecisionNumber(etrCorInfo[0]);
		    promotionTransactionDataItr.setDecisionDateString(etrCorInfo[1]);

		    if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
			promotionTransactionDataItr.setNewLastPromotionDateString(promotionReportData.getPromotionDateString());// check this
		    else // PERSONS
			promotionTransactionDataItr.setNewLastPromotionDateString(null);
		}

		// calculate new due date for soldiers based on decision date
		// and for civilians based on joining date set by user from Master Data for WF only and for officers based on old due date
		if ((employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()))) {
		    promotionTransactionDataItr.setNewDueDate(calculateNewPromotionDueDate(promotionTransactionDataItr.getNewRankId(), CategoriesEnum.OFFICERS.getCode(), null, null, employee.getPromotionDueDate(), employee.getGender(), null, null));
		    promotionTransactionDataItr.getPromotionTransaction().setSystemUser(loginEmpId + "");

		    // Effect in employee
		    EmployeesService.updateEmployeePromotionData(employee, null, getNextRank(employee.getRankId()), promotionTransactionDataItr.getRankTitleId(), editedEmployeeList.get(index).getDegreeId(), promotionTransactionDataItr.getNewMilitaryNumber(), promotionTransactionDataItr.getNewDueDate(), promotionTransactionDataItr.getNewLastPromotionDate(), session);
		    EmployeeLog log = new EmployeeLog.Builder().setRankId(employee.getRankId()).setRankTitleId(promotionTransactionDataItr.getRankTitleId()).setDegreeId(promotionTransactionDataItr.getNewDegreeId()).constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), promotionTransactionDataItr.getDecisionNumber(), promotionTransactionDataItr.getDecisionDate(), promotionTransactionDataItr.getNewLastPromotionDate(), DataAccess.getTableName(PromotionTransaction.class))
			    .build();
		    LogService.logEmployeeData(log, session);

		} else if (employee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && promotionReportData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		    promotionTransactionDataItr.setNewDueDate(HijriDateService.addSubHijriDays(promotionTransactionDataItr.getDecisionDate(), PromotionRankDaysEnum.LIEUTENANT.getCode(), true));

		    // In case of promotiong PRIME_SERGEANTS to LIEUTENANT, setOldDueDate to be the same as decision date
		    promotionTransactionDataItr.setOldDueDate(promotionTransactionDataItr.getDecisionDate());
		    // Change job status
		    JobData oldJob = JobsService.getJobById(promotionTransactionDataItr.getOldJobId());
		    JobData newJob = JobsService.getJobById(promotionTransactionDataItr.getNewJobId());

		    JobsService.changeJobStatus(oldJob, JobStatusEnum.VACANT.getCode(), session);
		    JobsService.changeJobStatus(newJob, JobStatusEnum.OCCUPIED.getCode(), session);
		    // effect in employee
		    employee.setCategoryId(CategoriesEnum.OFFICERS.getCode());
		    employee.setRecruitmentAsOfficerDate(promotionTransactionDataItr.getDecisionDate());

		    EmployeesService.updateEmployeePromotionData(employee, promotionTransactionDataItr.getNewJobId(), getNextRank(employee.getRankId()), promotionTransactionDataItr.getRankTitleId(), promotionTransactionDataItr.getNewDegreeId(), promotionTransactionDataItr.getNewMilitaryNumber(), promotionTransactionDataItr.getNewDueDate(), promotionTransactionDataItr.getNewLastPromotionDate(), session);
		    EmployeeLog log = new EmployeeLog.Builder().setRankId(getNextRank(employee.getRankId())).setJobId(promotionTransactionDataItr.getNewJobId()).setRankTitleId(promotionTransactionDataItr.getRankTitleId()).setDegreeId(promotionTransactionDataItr.getNewDegreeId())
			    .setBasicJobNameId(newJob.getBasicJobNameId()).constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), promotionTransactionDataItr.getDecisionNumber(), promotionTransactionDataItr.getDecisionDate(), promotionTransactionDataItr.getNewLastPromotionDate(), DataAccess.getTableName(PromotionTransaction.class)).build();
		    LogService.logEmployeeData(log, session);

		} else if (employee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && subject != null) {
		    // check this when the commit
		    if (!promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode()))
			promotionTransactionDataItr.setNewDueDate(calculateNewPromotionDueDate(promotionTransactionDataItr.getNewRankId(), CategoriesEnum.SOLDIERS.getCode(), null, null, promotionTransactionDataItr.getNewLastPromotionDate(), employee.getGender(), null, null));

		    // change job status
		    JobData oldJob = JobsService.getJobById(promotionTransactionDataItr.getOldJobId());
		    JobData newJob = JobsService.getJobById(promotionTransactionDataItr.getNewJobId());
		    if (promotionReportData.getScaleUpFlagBoolean() != null && !promotionReportData.getScaleUpFlagBoolean()) {
			JobsService.changeJobStatus(oldJob, JobStatusEnum.VACANT.getCode(), session);
			JobsService.changeJobStatus(newJob, JobStatusEnum.OCCUPIED.getCode(), session);
		    }

		    // effect in employee
		    if (promotionTransactionDataItr.getNewLastPromotionDate() == null)
			employee.setLastPromotionDate(null);
		    EmployeesService.updateEmployeePromotionData(employee, promotionTransactionDataItr.getNewJobId(), promotionTransactionDataItr.getNewRankId(), null, promotionTransactionDataItr.getNewDegreeId(), null, promotionTransactionDataItr.getNewDueDate(), promotionTransactionDataItr.getNewLastPromotionDate(), session);

		    if (promotionReportData.getPromotionTypeId().equals(PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())) {
			EmployeeLog log = new EmployeeLog.Builder().setRankId(promotionTransactionDataItr.getNewRankId()).setJobId(promotionTransactionDataItr.getNewJobId()).setDegreeId(promotionTransactionDataItr.getNewDegreeId()).setBasicJobNameId(newJob.getBasicJobNameId())
				.constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), promotionTransactionDataItr.getDecisionNumber(), promotionTransactionDataItr.getDecisionDate(), promotionTransactionDataItr.getOldLastPromotionDate(), DataAccess.getTableName(PromotionTransaction.class)).build();
			LogService.logEmployeeData(log, session);
		    } else {
			EmployeeLog log = new EmployeeLog.Builder().setRankId(promotionTransactionDataItr.getNewRankId()).setJobId(promotionTransactionDataItr.getNewJobId()).setDegreeId(promotionTransactionDataItr.getNewDegreeId()).setBasicJobNameId(newJob.getBasicJobNameId())
				.constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), promotionTransactionDataItr.getDecisionNumber(), promotionTransactionDataItr.getDecisionDate(), promotionTransactionDataItr.getNewLastPromotionDate(), DataAccess.getTableName(PromotionTransaction.class)).build();
			LogService.logEmployeeData(log, session);
		    }
		} else if (employee.getCategoryId().equals(CategoriesEnum.PERSONS.getCode()) && subject != null) {
		    // NewDueDate needed to updated (calculated) when set joining date set by user from Master Data
		    promotionTransactionDataItr.setNewDueDate(null);
		    employee.setPromotionDueDate(null);
		    employee.setLastPromotionDate(null);

		    // As for persons emp_a's new_job may be emp_b's old_job, we load non repeated jobs only to save time
		    if (!empsJobsMap.containsKey(promotionTransactionDataItr.getOldJobId()))
			empsJobsMap.put(promotionTransactionDataItr.getOldJobId().longValue(), JobsService.getJobById(promotionTransactionDataItr.getOldJobId()));

		    if (!empsJobsMap.containsKey(promotionTransactionDataItr.getNewJobId()))
			empsJobsMap.put(promotionTransactionDataItr.getNewJobId().longValue(), JobsService.getJobById(promotionTransactionDataItr.getNewJobId()));

		    JobData oldJob = empsJobsMap.get(promotionTransactionDataItr.getOldJobId());
		    JobData newJob = empsJobsMap.get(promotionTransactionDataItr.getNewJobId());

		    JobsService.changeJobStatus(oldJob, JobStatusEnum.VACANT.getCode(), session);
		    JobsService.changeJobStatus(newJob, JobStatusEnum.OCCUPIED.getCode(), session);

		    EmployeesService.updateEmployeePromotionData(employee, promotionTransactionDataItr.getNewJobId(), getNextRank(employee.getRankId()), null, promotionTransactionDataItr.getNewDegreeId(), null, null, null, session);
		}
		// add promotion transaction
		DataAccess.addEntity(promotionTransactionDataItr.getPromotionTransaction(), session);
		promotionTransactionDataItr.setId(promotionTransactionDataItr.getPromotionTransaction().getId());
		index++;
	    }
	    // scale up jobs
	    if (promotionReportData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && promotionReportData.getScaleUpFlagBoolean()) {
		Long userId = (promotionTransactionDataList != null && promotionTransactionDataList.size() > 0 && promotionTransactionDataList.get(0) != null) ? promotionTransactionDataList.get(0).getDecisionApprovedId() : null;

		List<JobData> jobsToFreeze = JobsService.getJobsByIdsString(jobsToFreezeIds.substring(0, jobsToFreezeIds.length() - 1));
		// TODO: make sure that executionDate for jobs transaction is promotionDate NOT (decisionDate)
		JobsService.handleJobsTransactions(null, null, null, jobsToFreeze, null, jobsToScaleUp, null, null, etrCorInfo[0], HijriDateService.getHijriDate(etrCorInfo[1]), promotionReportData.getPromotionDate(), userId, false, false, true, true, session);
	    }

	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(promotionTransactionDataList, promotionReportData.getCategoryId(), FlagsEnum.OFF.getCode(), session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (PromotionTransactionData promotionTransactionDataItr : promotionTransactionDataList)
		promotionTransactionDataItr.setId(null);

	    throw new BusinessException(e.getMessage(), e.getParams());
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (PromotionTransactionData promotionTransactionDataItr : promotionTransactionDataList)
		promotionTransactionDataItr.setId(null);

	    e.printStackTrace();
	    throw new BusinessException("error_general");

	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void doPayrollIntegration(List<PromotionTransactionData> promotionTransactionDataList, Long promotionReportCategoryId, Integer resendFlag, CustomSession session) throws BusinessException {
	Long adminDecisionId = null;
	if (promotionReportCategoryId.equals(CategoriesEnum.OFFICERS.getCode()))
	    adminDecisionId = AdminDecisionsEnum.OFFICERS_PROMOTION_REPORT.getCode();

	if (adminDecisionId != null) {
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<>();
	    String gregNewLastPromotionDateString = null;
	    for (PromotionTransactionData promotionTransactionData : promotionTransactionDataList) {
		EmployeeData empData = EmployeesService.getEmployeeData(promotionTransactionData.getEmpId());
		gregNewLastPromotionDateString = HijriDateService.hijriToGregDateString(promotionTransactionData.getNewLastPromotionDateString());
		adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(empData.getEmpId(), empData.getName(), promotionTransactionData.getId(), null, gregNewLastPromotionDateString, null, promotionTransactionData.getDecisionNumber(), null));
	    }
	    String gregDecisionDateString = HijriDateService.hijriToGregDateString(promotionTransactionDataList.get(0).getDecisionDateString());
	    session.flushTransaction();
	    PayrollEngineService.doPayrollIntegration(adminDecisionId, promotionTransactionDataList.get(0).getCategoryId(), gregNewLastPromotionDateString, adminDecisionEmployeeDataList, promotionTransactionDataList.get(0).getAttachments(), promotionTransactionDataList.get(0).getNewUnitId(), gregDecisionDateString, DataAccess.getTableName(PromotionTransaction.class), resendFlag, FlagsEnum.OFF.getCode(), session);
	}

    }

    public static void payrollIntegrationFailureHandle(String decisionNumber, Date decisionDate, Long categoryId, CustomSession session) throws BusinessException {
	List<PromotionTransactionData> promotionTransactionDataList = getPromotionTransactionsByDecisionNumberAndDecisionDate(decisionNumber, decisionDate);
	if (promotionTransactionDataList != null && promotionTransactionDataList.size() != 0) {
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(promotionTransactionDataList, categoryId, FlagsEnum.ON.getCode(), session);
	} else {
	    throw new BusinessException("error_transactionDataRetrievingError");
	}
    }

    /**
     * Apply changes done for units managers in effect of Promotion Civilians
     * 
     * @param unitsMap
     *            map of units that already modified
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    private static void doPromotionsUnitsIntegration(Map<Long, UnitData> unitsMap, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    UnitsService.modifyUnitsManagers(unitsMap.values(), true, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new BusinessException(e.getMessage(), e.getParams());
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
     * integrate with Terminations
     * 
     * @param emp
     *            employee data
     * @return
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    private static boolean doPromotionsTerminationsIntegration(EmployeeData emp) throws BusinessException {
	boolean setTerminationDate = true;
	try {
	    if (EmployeesService.countFutureServiceTerminatedEmployees(new Long[] { emp.getEmpId() }) > 0)
		throw new BusinessException("error_terminationPormotionConfilct", new Object[] { emp.getName() });
	    if (emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
		List<TerminationTransactionData> extentions = TerminationsService.getTerminationExtensionTransactionsByEmpId(emp.getEmpId());
		if (extentions != null && extentions.size() > 0) {
		    if (emp.getPromotionDueDate() == null || emp.getServiceTerminationDueDate() == null)
			throw new BusinessException("error_promotionTerminationDueDatesMandatory", new Object[] { emp.getName() });
		    if (extentions.get(extentions.size() - 1).getReasonId().longValue() == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode() || emp.getPromotionDueDate().before(emp.getServiceTerminationDueDate())) {
			emp.setLastExtensionEndDate(null);
			setTerminationDate = true;
		    } else
			setTerminationDate = false;
		}

	    } else {
		if (emp.getLastExtensionEndDate() != null)
		    return false;
	    }
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
	return setTerminationDate;
    }

    /**
     * Submit and modify joining date of the employee for his current issued promotion for all categories ( officers, soldiers, and civilians ).
     * 
     * Only civilians joining date is the effected promotion date, other categories have other efficitive dates .
     * 
     * @param transactionId
     *            The promotion transaction id to get the promotion transaction from DB
     * @param joiningDate
     *            The new joining date that will be affect the promotion
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    public static void modifyPromotionJoiningDate(long transactionId, Date joiningDate, long loginEmpId, CustomSession... useSession) throws BusinessException {
	if (joiningDate == null)
	    throw new BusinessException("error_joiningDateIsNull");

	if (!HijriDateService.isValidHijriDate(joiningDate))
	    throw new BusinessException("error_DateError");
	PromotionTransactionData promotionTransactionData = getPromotionTransactionById(transactionId);
	if (promotionTransactionData == null)
	    throw new BusinessException("error_noData");

	if (joiningDate.before(promotionTransactionData.getDecisionDate()))
	    throw new BusinessException("error_invalidPromotionJoiningDate");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // for civilians when joining date set newDueDate/newLastPromotionDate needed to set and promotionDueDate/lastPrompotionDate
	    if (promotionTransactionData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode()) && promotionTransactionData.getNewDueDate() == null) {

		EmployeeData employee = EmployeesService.getEmployeeData(promotionTransactionData.getEmpId());

		Date newDueDate = calculateNewPromotionDueDate(promotionTransactionData.getNewRankId(), CategoriesEnum.PERSONS.getCode(), null, null, joiningDate, employee.getGender(), null, null);
		promotionTransactionData.setNewDueDate(newDueDate);
		promotionTransactionData.setNewLastPromotionDate(joiningDate);

		if (promotionTransactionData.getEflag().equals(FlagsEnum.ON.getCode()) && employee.getPromotionDueDate() == null) {
		    EmployeesService.updateEmployeePromotionData(employee, null, null, null, null, null, newDueDate, joiningDate, session);
		    JobData newJob = JobsService.getJobById(promotionTransactionData.getNewJobId());
		    EmployeeLog log = new EmployeeLog.Builder().setRankId(promotionTransactionData.getNewRankId()).setJobId(promotionTransactionData.getNewJobId()).setDegreeId(promotionTransactionData.getNewDegreeId()).setBasicJobNameId(newJob.getBasicJobNameId())
			    .constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), promotionTransactionData.getDecisionNumber(), promotionTransactionData.getDecisionDate(), joiningDate, DataAccess.getTableName(PromotionTransaction.class)).build();
		    LogService.logEmployeeData(log, session);
		}
	    }

	    promotionTransactionData.getPromotionTransaction().setSystemUser(loginEmpId + ""); // audit
	    promotionTransactionData.setJoiningDate(joiningDate);
	    DataAccess.updateEntity(promotionTransactionData.getPromotionTransaction(), session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
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

    /*------------------------------------------------------Validations-----------------------------------------------------*/

    /**
     * Validate promotion adjustment transaction
     * 
     * <ul>
     * <li>Check Conflict validator for employee and job (if exist)</li>
     * <li>Check for duplicate decision number and decision date in previous transactions</li>
     * <li>Apply business validations (required fields, and dates validation)</li>
     * </ul>
     * 
     * @param promotionTransactionData
     *            Promotion Transaction data
     * @param promotionTransactionDataList
     *            List of history promotion transactions
     * @param employee
     *            employee data
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */
    private static void validatePromotionAdjustment(PromotionTransactionData promotionTransactionData, List<PromotionTransactionData> promotionTransactionDataList, EmployeeData employee) throws BusinessException {

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { employee.getEmpId() }, promotionTransactionData.getNewJobId() == null ? null : new Long[] { promotionTransactionData.getNewJobId() }, TransactionClassesEnum.PROMOTIONS.getCode(), false, null, employee.getCategoryId());

	// common validations for officers and soldiers

	if (promotionTransactionData.getDecisionNumber() == null || promotionTransactionData.getDecisionNumber().trim().isEmpty())
	    throw new BusinessException("error_ministerialDecisionMandatory");

	if (promotionTransactionData.getDecisionDate() == null)
	    throw new BusinessException("error_ministryDecisionDateMandatory");

	if (!HijriDateService.isValidHijriDate(promotionTransactionData.getDecisionDate()))
	    throw new BusinessException("error_invalidMinistryDecisionDate");

	// check for duplicate
	if (promotionTransactionDataList != null && promotionTransactionDataList.size() > 0) {
	    for (PromotionTransactionData promotionTransactionDataItr : promotionTransactionDataList) {
		if (promotionTransactionDataItr.getDecisionDateString() != null && promotionTransactionDataItr.getDecisionDateString().equals(promotionTransactionData.getDecisionDateString()) &&
			promotionTransactionDataItr.getDecisionNumber() != null && promotionTransactionDataItr.getDecisionNumber().equals(promotionTransactionData.getDecisionNumber()))
		    throw new BusinessException("error_DuplicateOperation");
	    }
	}

	// validate promotion data
	if (employee.getCategoryId().intValue() == CategoryModesEnum.OFFICERS.getCode()) {

	    if (promotionTransactionData.getNewMilitaryNumber() == null)
		throw new BusinessException("error_militaryNumberRequired");

	    if (!HijriDateService.isValidHijriDate(promotionTransactionData.getNewDueDate()))
		throw new BusinessException("error_invalidNewLastPromotionDateOfficers");

	    if (promotionTransactionData.getPromotionTypeId().equals(PromotionsTypesEnum.GRANTING_SENIORITY.getCode())) {
		if (employee.getPromotionDueDate() != null && promotionTransactionData.getNewDueDate().after(employee.getPromotionDueDate()))
		    throw new BusinessException("error_newDueDateBeforePromotionDueDate");

		if (employee.getRecruitmentDate() != null && promotionTransactionData.getNewDueDate().before(employee.getRecruitmentDate()))
		    throw new BusinessException("error_dueDateAfterRecruitmentDate");

	    } else if (promotionTransactionData.getPromotionTypeId().equals(PromotionsTypesEnum.REVOKING_SENIORITY.getCode())) {
		if (employee.getPromotionDueDate() != null && promotionTransactionData.getNewDueDate().before(employee.getPromotionDueDate()))
		    throw new BusinessException("error_newDueDateAfterPromotionDueDate");
	    }
	} else if (employee.getCategoryId().intValue() == CategoryModesEnum.SOLDIERS.getCode()) {
	    if (employee.getJobId() == null)
		throw new BusinessException("error_thereIsNoJob", new Object[] { employee.getName() });

	    if (promotionTransactionData.getNewJobId() == null)
		throw new BusinessException("error_reducerHerJob");

	    if (promotionTransactionData.getNewRankId() != getPreviousRank(employee.getRankId()))
		throw new BusinessException("error_rankAdiustment");

	    JobData newJob = JobsService.getJobById(promotionTransactionData.getNewJobId());
	    if (!newJob.getRankId().equals(promotionTransactionData.getNewRankId()))
		throw new BusinessException("error_rankJobNotsuitableForSoldier");

	    if (promotionTransactionData.getNewLastPromotionDate() == null)
		throw new BusinessException("error_newLastPromotionDateMandatory");

	    if (!HijriDateService.isValidHijriDate(promotionTransactionData.getNewLastPromotionDate()))
		throw new BusinessException("error_invalidNewLastPromotionDate");

	    if (promotionTransactionData.getNewLastPromotionDate().after(HijriDateService.getHijriSysDate()))
		throw new BusinessException("error_dateRankReducerAfterToday");

	    if (employee.getRecruitmentDate() != null && promotionTransactionData.getNewLastPromotionDate().before(employee.getRecruitmentDate()))
		throw new BusinessException("error_dueDateAfterRecruitmentDate");

	    PromotionTransactionData previousPromotionTransactionData = getPromotionTransactionByNewRankId(employee.getEmpId(), promotionTransactionData.getNewRankId());

	    if (previousPromotionTransactionData != null && promotionTransactionData.getNewLastPromotionDate().before(previousPromotionTransactionData.getNewLastPromotionDate()))
		throw new BusinessException("error_newLastPromotionDateBeforeLastPromotionTransaction");
	}
    }

    /*------------------------------------------------------Queries---------------------------------------------------------*/

    public static List<PromotionTransactionData> getPromotionTransactionDataByEmpID(Long empId) throws BusinessException {
	return searchPromotionTransactions(FlagsEnum.ALL.getCode(), empId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<PromotionTransactionData> getPromotionTransactionsDecisions(String decisionNumber, Date decisionDate, long empId, int categoryId, long promotionTypeId) throws BusinessException {
	return searchPromotionTransactionsDecisions(decisionNumber, decisionDate, empId, categoryId, promotionTypeId);
    }

    public static PromotionTransactionData getPromotionTransactionByDecisionNumberAndDecisionDate(String decisionNumber, Date decisionDate, long promotionTypeId) throws BusinessException {
	List<PromotionTransactionData> promotionTransactions = searchPromotionTransactionsDecisions(decisionNumber, decisionDate, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), promotionTypeId);
	return promotionTransactions.isEmpty() ? null : promotionTransactions.get(0);
    }

    public static List<PromotionTransactionData> getPromotionTransactionsByDecisionNumberAndDecisionDate(String decisionNumber, Date decisionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (decisionDate == null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode() + "");
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode() + "");
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    }
	    return DataAccess.executeNamedQuery(PromotionTransactionData.class, QueryNamesEnum.HCM_GET_PROMOTION_TRANSACTION_DATA_BY_DECISION_NUMBER_AND_DECISION_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    private static List<PromotionTransactionData> searchPromotionTransactionsDecisions(String decisionNumber, Date decisionDate, long empId, int categoryId, long promotionTypeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_PROMOTION_TYPE_ID", promotionTypeId);

	    if (decisionDate != null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    }

	    return DataAccess.executeNamedQuery(PromotionTransactionData.class, QueryNamesEnum.HCM_GET_PROMOTIONS_DECISIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static PromotionTransactionData getPromotionTransactionById(long transactionId) throws BusinessException {
	List<PromotionTransactionData> promotionTransactionsList = searchPromotionTransactions(transactionId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	return promotionTransactionsList.isEmpty() ? null : promotionTransactionsList.get(0);
    }

    public static PromotionTransactionData getPromotionTransactionByNewRankId(long empId, long newRankId) throws BusinessException {
	List<PromotionTransactionData> promotionTransactionsList = searchPromotionTransactions(FlagsEnum.ALL.getCode(), empId, FlagsEnum.ALL.getCode(), newRankId);
	return promotionTransactionsList.isEmpty() ? null : promotionTransactionsList.get(0);
    }

    public static List<PromotionTransactionData> getPromotionsHistory(long empId, long promotionTypeId) throws BusinessException {
	return searchPromotionTransactions(FlagsEnum.ALL.getCode(), empId, promotionTypeId, FlagsEnum.ALL.getCode());
    }

    private static List<PromotionTransactionData> searchPromotionTransactions(long transactionId, long empId, long promotionTypeId, long newRankId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_ID", transactionId);
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_PROMOTION_TYPE_ID", promotionTypeId);
	    qParams.put("P_NEW_RANK_ID", newRankId);

	    return DataAccess.executeNamedQuery(PromotionTransactionData.class, QueryNamesEnum.HCM_GET_PROMOTION_TRANSACTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long getPromotionTransactionsDecisionsCount(String decisionNumber, Date decisionDate) throws BusinessException {
	return getPromotionTransactionsCount(decisionNumber, decisionDate, FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode());
    }

    private static long getPromotionTransactionsCountByEmpId(long empId) throws BusinessException {
	return getPromotionTransactionsCount(null, null, empId, FlagsEnum.ALL.getCode());
    }

    private static long getPromotionTransactionsCount(String decisionNumber, Date decisionDate, long empId, int eFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (decisionDate != null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    }

	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_E_FLAG", eFlag);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_PROMOTION_TRANSACTIONS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*------------------------------------------------------Reports---------------------------------------------------------*/

    /**
     * Get all data of promotion transaction and set the promotion_transaction_Id as a report parameters
     * 
     * and set the report name according to category of transaction and the kind is it collective or singular finally
     * 
     * get the report data as a bytes and return them to print promotion Report
     * 
     * @param promotionTransactionData
     *            Promotion Transaction Data that will be printed
     * @return Bytes of report data to print it
     * @throws BusinessException
     *             If an error occurred in get the report or report data
     */
    public static byte[] getPromotionBytes(PromotionTransactionData promotionTransactionData, Date cancelledTransactionDecisionDate, String cancelledTransactionDecisionNumber) throws BusinessException {

	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_TRANSACTION_ID", promotionTransactionData.getId());
	    // SOLDIERS REPORTS
	    if (promotionTransactionData.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		long transactionsCount = getPromotionTransactionsDecisionsCount(promotionTransactionData.getDecisionNumber(), promotionTransactionData.getDecisionDate());

		if (promotionTransactionData.getPromotionTypeId() == 6) {
		    parameters.put("P_REFERRING", promotionTransactionData.getReferring());
		    parameters.put("P_CANCELLED_TRANSACTION_DECISION_DATE", cancelledTransactionDecisionDate);
		    parameters.put("P_CANCELLED_TRANSACTION_DECISION_NUMBER", cancelledTransactionDecisionNumber);
		    parameters.put("P_PROMOTION_DATE", promotionTransactionData.getOldLastPromotionDate());
		    reportName = ReportNamesEnum.PROMOTIONS_SOLDIERS_DECISION_CANCELLATION.getCode();
		} else if (transactionsCount == 1) {
		    reportName = ReportNamesEnum.PROMOTIONS_DECISION_SOLDIERS.getCode();
		} else {
		    // SCALE_UP = true
		    if (promotionTransactionData.getOldJobId().equals(promotionTransactionData.getNewJobId()))
			reportName = ReportNamesEnum.PROMOTIONS_DECISION_SOLDIERS_COLLECTIVE_SCALE_UP.getCode();
		    else
			reportName = ReportNamesEnum.PROMOTIONS_DECISION_SOLDIERS_COLLECTIVE.getCode();
		}

	    } else if (promotionTransactionData.getCategoryId() == CategoriesEnum.PERSONS.getCode()) {
		reportName = ReportNamesEnum.PROMOTIONS_DECISION_CIVILIANS.getCode();
	    }

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

    /*----------------------------------------------------- Promotions Ranks Power  ---------------------------------------------*/
    /*------------------------------------------------------Operations------------------------------------------------------*/

    /**
     * Adjust power rank data, which calculate vacant ,net vacant, and allowed promotion count for certain rank .
     * 
     * @param rankPowerList
     *            All rank power data from database
     * @return Ranks power list after calculate vacant and netVacent and allowed promotion count
     */
    public static List<RankPowerData> adjustRanksPower() throws BusinessException {

	List<RankPowerData> rankPowerList = getRanksPowerData();
	// calculate vacant and net vacant for all list
	int count = rankPowerList.size() - 1;
	int netVacant = 0;

	for (; count >= 0; count--) {
	    // calculate Vacant
	    rankPowerList.get(count).setVacant(rankPowerList.get(count).getPower() - rankPowerList.get(count).getOccupied());

	    // calculate netVacant
	    netVacant = rankPowerList.get(count).getVacant() + rankPowerList.get(count).getNetVacant();
	    if (netVacant <= 0) {
		rankPowerList.get(count).setNetVacant(0);
		if ((count - 1) != -1)
		    rankPowerList.get(count - 1).setNetVacant(netVacant);
	    } else
		rankPowerList.get(count).setNetVacant(netVacant);
	    if (rankPowerList.get(count).getVacant() < 0)
		rankPowerList.get(count).setLoadedBalanceWithdrawnFromPromotion(0);
	    else
		rankPowerList.get(count).setLoadedBalanceWithdrawnFromPromotion(rankPowerList.get(count).getVacant() - rankPowerList.get(count).getNetVacant());
	}

	// initialize i before use it
	rankPowerList.get(0).setAllowedPromotionCount(rankPowerList.get(0).getNetVacant());
	for (count = 1; count < rankPowerList.size(); count++) {
	    rankPowerList.get(count).setAllowedPromotionCount(rankPowerList.get(count).getNetVacant() + rankPowerList.get(count - 1).getAllowedPromotionCount());
	}

	return rankPowerList;
    }

    /**
     * update list of rank power data and audit these updates by login user
     * 
     * @param oldRankPowerData
     *            Old rank power data that will be checked with new rank power
     * @param newRankPowerData
     *            New rank power data will that be checked with old rank power and updated
     * @param loginEmpId
     *            Used to audit user action when modify rank
     * @param useSession
     *            Optional parameter use it if don't equal null else create a new session
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void modifyRanksPower(RankPowerData oldRankPowerData, RankPowerData newRankPowerData, long loginEmpId) throws BusinessException {

	if (newRankPowerData.getPower().intValue() < (oldRankPowerData.getOccupied().intValue() + oldRankPowerData.getVacant().intValue()) - oldRankPowerData.getAllowedPromotionCount().intValue())
	    throw new BusinessException("error_newRankPowerNotSuitable", new Object[] { newRankPowerData.getRankDescription() });

	CustomSession session = DataAccess.getSession();

	try {
	    session.beginTransaction();

	    newRankPowerData.getRankPower().setSystemUser(loginEmpId + "");
	    DataAccess.updateEntity(newRankPowerData.getRankPower(), session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /*------------------------------------------------------Validations-----------------------------------------------------*/

    /**
     * 
     * Validate number of officers that can be promoted on specific rank
     * 
     * @param rankId
     *            Ranks Id to be validate on it
     * @return How many officer can promoted on specific rank
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */

    public static void validateRanksPower(long rankId, int desiredPromotionCount) throws BusinessException {
	int allowedPromotionCount = 0;
	List<RankPowerData> ranksPower = adjustRanksPower();
	rankId = getNextRank(rankId);
	for (RankPowerData rankPower : ranksPower) {
	    if (rankPower != null && rankPower.getRankId().equals(rankId)) {
		allowedPromotionCount = rankPower.getAllowedPromotionCount();
		break;
	    }
	}
	if (allowedPromotionCount < desiredPromotionCount)
	    throw new BusinessException("error_rankPowerIsFull");
    }

    /*------------------------------------------------------Queries-----------------------------------------------------*/

    /**
     * Get all ranks Data
     * 
     * @return List of ranks power data
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */

    private static List<RankPowerData> getRanksPowerData() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(RankPowerData.class, QueryNamesEnum.HCM_GET_RANKS_POWER_DATA.getCode(), null);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*----------------------------------------------------- PromotionEmployeeDegreeData ---------------------------------------------*/

    public static List<PromotionReportDetailData> getRankedPromotionReportDetailsDataByReportId(long reportId, Long[] statusIds, Integer medicalTest, String empName) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REPORT_ID", reportId);
	    qParams.put("P_STATUS_IDS", statusIds);
	    qParams.put("P_MEDICAL_TEST", medicalTest == null ? FlagsEnum.ALL.getCode() : medicalTest);
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    return DataAccess.executeNamedQuery(PromotionReportDetailData.class, QueryNamesEnum.HCM_GET_RANKED_PROMOTION_DETAILS_DATA_BY_REPORT_ID.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*----------------------------------------------------- PromotionEmployeeDegreeData ---------------------------------------------*/

    private static List<PromotionEmployeeDegreeData> getPromotionEmployeeDegreeDataByEmpsIds(Long[] empsIds, long fromRankId) throws BusinessException {
	List<Object> queryInfo = new ArrayList<Object>();
	queryInfo.add(QueryNamesEnum.HCM_GET_PROMOTION_EMPLOYEE_DEGREE_DATA_BY_EMPS_IDS.getCode());

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_EMPS_IDS", empsIds);
	qParams.put("P_FROM_RANK_ID", fromRankId);
	queryInfo.add(qParams);

	return getManyEntities(PromotionEmployeeDegreeData.class, queryInfo, empsIds);
    }
    /*----------------------------------------------------- Utilities ---------------------------------------------*/

    /**
     * Get the next rank using rank Id
     * 
     * @param rankId
     *            Rank Id
     * @return The next rank
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */

    public static long getNextRank(long rankId) throws BusinessException {

	if (rankId == RanksEnum.FIRST_LIEUTENANT_GENERAL.getCode())
	    throw new BusinessException("error_NoHigherRankIsFound");
	else if (rankId == RanksEnum.LIEUTENANT_GENERAL.getCode())
	    return RanksEnum.FIRST_LIEUTENANT_GENERAL.getCode();
	else if (rankId == RanksEnum.MAJOR_GENERAL.getCode())
	    return RanksEnum.LIEUTENANT_GENERAL.getCode();
	else if (rankId == RanksEnum.BRIGADIER.getCode())
	    return RanksEnum.MAJOR_GENERAL.getCode();
	else if (rankId == RanksEnum.COLONEL.getCode())
	    return RanksEnum.BRIGADIER.getCode();
	else if (rankId == RanksEnum.LIEUTENANT_COLONEL.getCode())
	    return RanksEnum.COLONEL.getCode();
	else if (rankId == RanksEnum.MAJOR.getCode())
	    return RanksEnum.LIEUTENANT_COLONEL.getCode();
	else if (rankId == RanksEnum.CAPTAIN.getCode())
	    return RanksEnum.MAJOR.getCode();
	else if (rankId == RanksEnum.FIRST_LIEUTENANT.getCode())
	    return RanksEnum.CAPTAIN.getCode();
	else if (rankId == RanksEnum.LIEUTENANT.getCode())
	    return RanksEnum.FIRST_LIEUTENANT.getCode();
	else if (rankId == RanksEnum.PRIME_SERGEANTS.getCode())
	    return RanksEnum.LIEUTENANT.getCode();

	else if (rankId == RanksEnum.STAFF_SERGEANT.getCode())
	    return RanksEnum.PRIME_SERGEANTS.getCode();
	else if (rankId == RanksEnum.SERGEANT.getCode())
	    return RanksEnum.STAFF_SERGEANT.getCode();
	else if (rankId == RanksEnum.UNDER_SERGEANT.getCode())
	    return RanksEnum.SERGEANT.getCode();
	else if (rankId == RanksEnum.CORPORAL.getCode())
	    return RanksEnum.UNDER_SERGEANT.getCode();
	else if (rankId == RanksEnum.FIRST_SOLDIER.getCode())
	    return RanksEnum.CORPORAL.getCode();
	else if (rankId == RanksEnum.SOLDIER.getCode())
	    return RanksEnum.FIRST_SOLDIER.getCode();

	else if (rankId == RanksEnum.FIFTEENTH.getCode())
	    throw new BusinessException("error_NoHigherRankIsFound");
	else if (rankId == RanksEnum.FOURTEENTH.getCode())
	    return RanksEnum.FIFTEENTH.getCode();
	else if (rankId == RanksEnum.THIRTEENTH.getCode())
	    return RanksEnum.FOURTEENTH.getCode();
	else if (rankId == RanksEnum.TWELFTH.getCode())
	    return RanksEnum.THIRTEENTH.getCode();
	else if (rankId == RanksEnum.ELEVENTH.getCode())
	    return RanksEnum.TWELFTH.getCode();
	else if (rankId == RanksEnum.TENTH.getCode())
	    return RanksEnum.ELEVENTH.getCode();
	else if (rankId == RanksEnum.NINTH.getCode())
	    return RanksEnum.TENTH.getCode();
	else if (rankId == RanksEnum.EIGHTH.getCode())
	    return RanksEnum.NINTH.getCode();
	else if (rankId == RanksEnum.SEVENTH.getCode())
	    return RanksEnum.EIGHTH.getCode();
	else if (rankId == RanksEnum.SIXTH.getCode())
	    return RanksEnum.SEVENTH.getCode();
	else if (rankId == RanksEnum.FIFTH.getCode())
	    return RanksEnum.SIXTH.getCode();
	else if (rankId == RanksEnum.FOURTH.getCode())
	    return RanksEnum.FIFTH.getCode();
	else if (rankId == RanksEnum.THIRD.getCode())
	    return RanksEnum.FOURTH.getCode();
	else if (rankId == RanksEnum.SECOND.getCode())
	    return RanksEnum.THIRD.getCode();
	else if (rankId == RanksEnum.FIRST.getCode())
	    return RanksEnum.SECOND.getCode();

	else if (rankId == RanksEnum.THIRTY_THIRD.getCode())
	    throw new BusinessException("error_NoHigherRankIsFound");
	else if (rankId == RanksEnum.THIRTY_SECOND.getCode())
	    return RanksEnum.THIRTY_THIRD.getCode();
	else if (rankId == RanksEnum.THIRTY_ONE.getCode())
	    return RanksEnum.THIRTY_SECOND.getCode();
	else if (rankId == RanksEnum.WORKER_D.getCode())
	    throw new BusinessException("error_NoHigherRankIsFound");
	else if (rankId == RanksEnum.WORKER_C.getCode())
	    return RanksEnum.WORKER_D.getCode();
	else if (rankId == RanksEnum.WORKER_B.getCode())
	    return RanksEnum.WORKER_C.getCode();
	else if (rankId == RanksEnum.WORKER_A.getCode())
	    return RanksEnum.WORKER_B.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_SIXTEENTH.getCode())
	    throw new BusinessException("error_NoHigherRankIsFound");
	else if (rankId == RanksEnum.CONTRACTOR_FIFTEENTH.getCode())
	    return RanksEnum.CONTRACTOR_SIXTEENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FOURTEENTH.getCode())
	    return RanksEnum.CONTRACTOR_FIFTEENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_THIRTEENTH.getCode())
	    return RanksEnum.CONTRACTOR_FOURTEENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_TWELFTH.getCode())
	    return RanksEnum.CONTRACTOR_THIRTEENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_ELEVENTH.getCode())
	    return RanksEnum.CONTRACTOR_TWELFTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_TENTH.getCode())
	    return RanksEnum.CONTRACTOR_ELEVENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_NINTH.getCode())
	    return RanksEnum.CONTRACTOR_TENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_EIGHTH.getCode())
	    return RanksEnum.CONTRACTOR_NINTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_SEVENTH.getCode())
	    return RanksEnum.CONTRACTOR_EIGHTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_SIXTH.getCode())
	    return RanksEnum.CONTRACTOR_SEVENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FIFTH.getCode())
	    return RanksEnum.CONTRACTOR_SIXTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FOURTH.getCode())
	    return RanksEnum.CONTRACTOR_FIFTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_THIRD.getCode())
	    return RanksEnum.CONTRACTOR_FOURTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_SECOND.getCode())
	    return RanksEnum.CONTRACTOR_THIRD.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FIRST.getCode())
	    return RanksEnum.CONTRACTOR_SECOND.getCode();
	else
	    throw new BusinessException("error_NoRankIsFound");
    }

    /**
     * Get the Previous rank using rank Id
     * 
     * @param rankId
     *            Rank Id
     * @return The Previous rank
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */

    public static long getPreviousRank(long rankId) throws BusinessException {

	if (rankId == RanksEnum.FIRST_LIEUTENANT_GENERAL.getCode())
	    return RanksEnum.LIEUTENANT_GENERAL.getCode();
	else if (rankId == RanksEnum.LIEUTENANT_GENERAL.getCode())
	    return RanksEnum.MAJOR_GENERAL.getCode();
	else if (rankId == RanksEnum.MAJOR_GENERAL.getCode())
	    return RanksEnum.BRIGADIER.getCode();
	else if (rankId == RanksEnum.BRIGADIER.getCode())
	    return RanksEnum.COLONEL.getCode();
	else if (rankId == RanksEnum.COLONEL.getCode())
	    return RanksEnum.LIEUTENANT_COLONEL.getCode();
	else if (rankId == RanksEnum.LIEUTENANT_COLONEL.getCode())
	    return RanksEnum.MAJOR.getCode();
	else if (rankId == RanksEnum.MAJOR.getCode())
	    return RanksEnum.CAPTAIN.getCode();
	else if (rankId == RanksEnum.CAPTAIN.getCode())
	    return RanksEnum.FIRST_LIEUTENANT.getCode();
	else if (rankId == RanksEnum.FIRST_LIEUTENANT.getCode())
	    return RanksEnum.LIEUTENANT.getCode();
	else if (rankId == RanksEnum.LIEUTENANT.getCode())
	    throw new BusinessException("error_NoLowerRankIsFound");
	else if (rankId == RanksEnum.PRIME_SERGEANTS.getCode())
	    return RanksEnum.STAFF_SERGEANT.getCode();
	else if (rankId == RanksEnum.STAFF_SERGEANT.getCode())
	    return RanksEnum.SERGEANT.getCode();
	else if (rankId == RanksEnum.SERGEANT.getCode())
	    return RanksEnum.UNDER_SERGEANT.getCode();
	else if (rankId == RanksEnum.UNDER_SERGEANT.getCode())
	    return RanksEnum.CORPORAL.getCode();
	else if (rankId == RanksEnum.CORPORAL.getCode())
	    return RanksEnum.FIRST_SOLDIER.getCode();
	else if (rankId == RanksEnum.FIRST_SOLDIER.getCode())
	    return RanksEnum.SOLDIER.getCode();
	else if (rankId == RanksEnum.SOLDIER.getCode())
	    throw new BusinessException("error_NoLowerRankIsFound");
	else if (rankId == RanksEnum.FIFTEENTH.getCode())
	    return RanksEnum.FOURTEENTH.getCode();
	else if (rankId == RanksEnum.FOURTEENTH.getCode())
	    return RanksEnum.THIRTEENTH.getCode();
	else if (rankId == RanksEnum.THIRTEENTH.getCode())
	    return RanksEnum.TWELFTH.getCode();
	else if (rankId == RanksEnum.TWELFTH.getCode())
	    return RanksEnum.ELEVENTH.getCode();
	else if (rankId == RanksEnum.ELEVENTH.getCode())
	    return RanksEnum.TENTH.getCode();
	else if (rankId == RanksEnum.TENTH.getCode())
	    return RanksEnum.NINTH.getCode();
	else if (rankId == RanksEnum.NINTH.getCode())
	    return RanksEnum.EIGHTH.getCode();
	else if (rankId == RanksEnum.EIGHTH.getCode())
	    return RanksEnum.SEVENTH.getCode();
	else if (rankId == RanksEnum.SEVENTH.getCode())
	    return RanksEnum.SIXTH.getCode();
	else if (rankId == RanksEnum.SIXTH.getCode())
	    return RanksEnum.FIFTH.getCode();
	else if (rankId == RanksEnum.FIFTH.getCode())
	    return RanksEnum.FOURTH.getCode();
	else if (rankId == RanksEnum.FOURTH.getCode())
	    return RanksEnum.THIRD.getCode();
	else if (rankId == RanksEnum.THIRD.getCode())
	    return RanksEnum.SECOND.getCode();
	else if (rankId == RanksEnum.SECOND.getCode())
	    return RanksEnum.FIRST.getCode();
	else if (rankId == RanksEnum.FIRST.getCode())
	    throw new BusinessException("error_NoLowerRankIsFound");
	else if (rankId == RanksEnum.THIRTY_THIRD.getCode())
	    return RanksEnum.THIRTY_SECOND.getCode();
	else if (rankId == RanksEnum.THIRTY_SECOND.getCode())
	    return RanksEnum.THIRTY_ONE.getCode();
	else if (rankId == RanksEnum.THIRTY_ONE.getCode())
	    throw new BusinessException("error_NoLowerRankIsFound");
	else if (rankId == RanksEnum.WORKER_D.getCode())
	    return RanksEnum.WORKER_C.getCode();
	else if (rankId == RanksEnum.WORKER_C.getCode())
	    return RanksEnum.WORKER_B.getCode();
	else if (rankId == RanksEnum.WORKER_B.getCode())
	    return RanksEnum.WORKER_A.getCode();
	else if (rankId == RanksEnum.WORKER_A.getCode())
	    throw new BusinessException("error_NoLowerRankIsFound");
	else if (rankId == RanksEnum.CONTRACTOR_SIXTEENTH.getCode())
	    return RanksEnum.CONTRACTOR_FIFTEENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FIFTEENTH.getCode())
	    return RanksEnum.CONTRACTOR_FOURTEENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FOURTEENTH.getCode())
	    return RanksEnum.CONTRACTOR_THIRTEENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_THIRTEENTH.getCode())
	    return RanksEnum.CONTRACTOR_TWELFTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_TWELFTH.getCode())
	    return RanksEnum.CONTRACTOR_ELEVENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_ELEVENTH.getCode())
	    return RanksEnum.CONTRACTOR_TENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_TENTH.getCode())
	    return RanksEnum.CONTRACTOR_NINTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_NINTH.getCode())
	    return RanksEnum.CONTRACTOR_EIGHTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_EIGHTH.getCode())
	    return RanksEnum.CONTRACTOR_SEVENTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_SEVENTH.getCode())
	    return RanksEnum.CONTRACTOR_SIXTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_SIXTH.getCode())
	    return RanksEnum.CONTRACTOR_FIFTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FIFTH.getCode())
	    return RanksEnum.CONTRACTOR_FOURTH.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FOURTH.getCode())
	    return RanksEnum.CONTRACTOR_THIRD.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_THIRD.getCode())
	    return RanksEnum.CONTRACTOR_SECOND.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_SECOND.getCode())
	    return RanksEnum.CONTRACTOR_FIRST.getCode();
	else if (rankId == RanksEnum.CONTRACTOR_FIRST.getCode())
	    throw new BusinessException("error_NoLowerRankIsFound");
	else
	    throw new BusinessException("error_NoRankIsFound");
    }

    /**
     * Get range of ranks specific in civilians ranks .
     * 
     * @param rankId
     *            rank Id
     * @param categoryId
     *            Id of employee category
     * @return result of new rank group
     * 
     * @see CivilianReportRanksEnum
     */

    private static long getRankRangeByRankId(long rankId, long categoryId) {

	if (categoryId == CategoriesEnum.OFFICERS.getCode() || categoryId == CategoriesEnum.SOLDIERS.getCode())
	    return rankId;

	if (categoryId == CategoriesEnum.PERSONS.getCode()) {
	    if (rankId == RanksEnum.FIRST.getCode() || rankId == RanksEnum.SECOND.getCode() || rankId == RanksEnum.THIRD.getCode() || rankId == RanksEnum.FOURTH.getCode())
		return CivilianReportRanksEnum.FROM_FIRST_TO_FOURTH.getCode();

	    if (rankId == RanksEnum.FIFTH.getCode() || rankId == RanksEnum.SIXTH.getCode() || rankId == RanksEnum.SEVENTH.getCode() || rankId == RanksEnum.EIGHTH.getCode() || rankId == RanksEnum.NINTH.getCode())
		return CivilianReportRanksEnum.FROM_FIFTH_TO_NINTH.getCode();

	    if (rankId == RanksEnum.TENTH.getCode() || rankId == RanksEnum.ELEVENTH.getCode() || rankId == RanksEnum.TWELFTH.getCode() || rankId == RanksEnum.THIRTEENTH.getCode())
		return CivilianReportRanksEnum.THE_HIGHER_RANKS.getCode();
	}
	return rankId;
    }

    /**
     * Calculate the employee next promotion due date according to category, rank, recruitment date, first recruitment date ,and last promotion date
     * 
     * @param rankId
     *            rank Id
     * @param categoryId
     *            category Id
     * @param recruitmentDate
     *            recruitment date
     * @param firstRecruitmentDate
     *            government date in case recruitment was from outside FG
     * @param lastPromotionDate
     *            last promotion date
     * @param gender
     *            Gender type to specify due date
     * @return The new promotion due date which calculated
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static Date calculateNewPromotionDueDate(long rankId, long categoryId, Date recruitmentDate, Date firstRecruitmentDate, Date lastPromotionDate, String gender, Integer seniorityDays, Integer seniorityMonths) throws BusinessException {

	Date promotionDueDate = null;
	Date dateOfRankBeginning;

	try {
	    dateOfRankBeginning = calculateCurrentRankDaysSpent(recruitmentDate, firstRecruitmentDate, lastPromotionDate);

	    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
		if (rankId == RanksEnum.LIEUTENANT.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.LIEUTENANT.getCode(), true);
		else if (rankId == RanksEnum.FIRST_LIEUTENANT.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.FIRST_LIEUTENANT.getCode(), true);
		else if (rankId == RanksEnum.CAPTAIN.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.CAPTAIN.getCode(), true);
		else if (rankId == RanksEnum.MAJOR.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.MAJOR.getCode(), true);
		else if (rankId == RanksEnum.LIEUTENANT_COLONEL.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.LIEUTENANT_COLONEL.getCode(), true);
		else if (rankId == RanksEnum.COLONEL.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.COLONEL.getCode(), true);
		else if (rankId == RanksEnum.BRIGADIER.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.BRIGADIER.getCode(), true);

		promotionDueDate = HijriDateService.addSubHijriMonthsDays(promotionDueDate, seniorityMonths != null ? seniorityMonths.intValue() * -1 : FlagsEnum.OFF.getCode(), seniorityDays != null ? seniorityDays.intValue() * -1 : FlagsEnum.OFF.getCode());
		// TODO That check must be done at Recruitment service to handle parametrized message
		if (firstRecruitmentDate != null && recruitmentDate != null) {
		    if (firstRecruitmentDate.before(recruitmentDate) && promotionDueDate.before(firstRecruitmentDate))
			throw new BusinessException("error_promotionDueDateBeforeGovRecruitmentDate");
		    else if (firstRecruitmentDate.compareTo(recruitmentDate) == 0 && promotionDueDate.before(recruitmentDate))
			throw new BusinessException("error_promotionDueDateBeforeRuitmentDate");
		}

	    } else if (categoryId == CategoriesEnum.SOLDIERS.getCode() && gender.equals(GendersEnum.MALE.getCode())) {
		if (rankId == RanksEnum.SOLDIER.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.SOLDIER.getCode(), true);
		else if (rankId == RanksEnum.FIRST_SOLDIER.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.FIRST_SOLDIER.getCode(), true);
		else if (rankId == RanksEnum.CORPORAL.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.CORPORAL.getCode(), true);
		else if (rankId == RanksEnum.UNDER_SERGEANT.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.UNDER_SERGEANT.getCode(), true);
		else if (rankId == RanksEnum.SERGEANT.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.SERGEANT.getCode(), true);
		else if (rankId == RanksEnum.STAFF_SERGEANT.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.STAFF_SERGEANT.getCode(), true);
		else if (rankId == RanksEnum.PRIME_SERGEANTS.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.PRIME_SERGEANTS.getCode(), true);

	    } else if (categoryId == CategoriesEnum.SOLDIERS.getCode() && gender.equals(GendersEnum.FEMALE.getCode())) {
		if (rankId == RanksEnum.SOLDIER.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.SOLDIER_FEMALE.getCode(), true);
		else if (rankId == RanksEnum.FIRST_SOLDIER.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.FIRST_SOLDIER_FEMALE.getCode(), true);
		else if (rankId == RanksEnum.CORPORAL.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.CORPORAL_FEMALE.getCode(), true);
		else if (rankId == RanksEnum.UNDER_SERGEANT.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.UNDER_SERGEANT_FEMALE.getCode(), true);
		else if (rankId == RanksEnum.SERGEANT.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.SERGEANT_FEMALE.getCode(), true);
		else if (rankId == RanksEnum.STAFF_SERGEANT.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.STAFF_SERGEANT_FEMALE.getCode(), true);

	    } else if (categoryId == CategoriesEnum.PERSONS.getCode()) {
		if (rankId == RanksEnum.FIRST.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.FIRST.getCode(), true);
		else if (rankId == RanksEnum.SECOND.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.SECOND.getCode(), true);
		else if (rankId == RanksEnum.THIRD.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.THIRD.getCode(), true);
		else if (rankId == RanksEnum.FOURTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.FOURTH.getCode(), true);
		else if (rankId == RanksEnum.FIFTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.FIFTH.getCode(), true);
		else if (rankId == RanksEnum.SIXTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.SIXTH.getCode(), true);
		else if (rankId == RanksEnum.SEVENTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.SEVENTH.getCode(), true);
		else if (rankId == RanksEnum.EIGHTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.EIGHTH.getCode(), true);
		else if (rankId == RanksEnum.NINTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.NINTH.getCode(), true);
		else if (rankId == RanksEnum.TENTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.TENTH.getCode(), true);
		else if (rankId == RanksEnum.ELEVENTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.ELEVENTH.getCode(), true);
		else if (rankId == RanksEnum.TWELFTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.TWELFTH.getCode(), true);
		else if (rankId == RanksEnum.THIRTEENTH.getCode())
		    promotionDueDate = HijriDateService.addSubHijriDays(dateOfRankBeginning, PromotionRankDaysEnum.THIRTEENTH.getCode(), true);

	    } else {
		return null;
	    }
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

	return promotionDueDate;
    }

    /**
     * Get the existing date of the following ( recruitment date, government date, or last promotion date ) that will effect the next promotion date of the employee.
     * 
     * @param recruitmentDate
     *            recruitment date
     * @param firstRecruitmentDate
     *            government first recruitment date
     * @param lastPromotionDate
     *            last promotion date
     * @return
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static Date calculateCurrentRankDaysSpent(Date recruitmentDate, Date firstRecruitmentDate, Date lastPromotionDate) {
	Date dateOfRankBeginning;
	if (lastPromotionDate == null) {
	    if (firstRecruitmentDate == null)
		dateOfRankBeginning = recruitmentDate;
	    else
		dateOfRankBeginning = firstRecruitmentDate;
	} else {
	    dateOfRankBeginning = lastPromotionDate;
	}
	return dateOfRankBeginning;
    }

    public static List<Rank> getPromotionReportRanksByCategoryId(long categoryId) {
	List<Rank> ranks = new ArrayList<Rank>();
	if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
	    ranks.add(CommonService.getRankById(RanksEnum.BRIGADIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT_COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.CAPTAIN.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.FIRST_LIEUTENANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.PRIME_SERGEANTS.getCode()));
	} else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {

	    ranks.add(CommonService.getRankById(RanksEnum.STAFF_SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.UNDER_SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.CORPORAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.FIRST_SOLDIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.SOLDIER.getCode()));
	} else if (categoryId == CategoriesEnum.PERSONS.getCode()) {
	    Rank rank = CommonService.getRankById(CivilianReportRanksEnum.THE_HIGHER_RANKS.getCode());
	    rank.setDescription(getMessage("label_theHigherRanks"));
	    ranks.add(rank);

	    rank = CommonService.getRankById(CivilianReportRanksEnum.FROM_FIFTH_TO_NINTH.getCode());
	    rank.setDescription(getMessage("label_fromTheFifthToTheNinth"));
	    ranks.add(rank);

	    rank = CommonService.getRankById(CivilianReportRanksEnum.FROM_FIRST_TO_FOURTH.getCode());
	    rank.setDescription(getMessage("label_fromFirstToFourth"));
	    ranks.add(rank);
	}

	return ranks;
    }

}