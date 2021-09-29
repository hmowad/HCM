package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransaction;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionDetailData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.CountriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingCourseEventStatusEnum;
import com.code.enums.TrainingTransactionCategoryEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.TrainingYearStatusEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.CommonService;
import com.code.services.util.CountryService;
import com.code.services.util.HijriDateService;

/**
 * Service to handle the employee training transactions.
 */
public class TrainingEmployeesService extends BaseService {

    private static final int MAX_STRING_LENGTH = 500;
    private static final int MAX_MONTHS_PERIOD = 99;
    private static final int MAX_OTHER_PURPOSES_LENGTH = 250;
    private static final int MAX_SCHOLARSHIP_DAYS_COUNT = 29;
    private static final int MAX_EVENING_COURSE_NOMINATION_PERIOD_DAYS = 90;

    /**
     * private constructor to prevent instantiation
     * 
     */
    private TrainingEmployeesService() {
    }

    /***************************** Object *****************************/
    /*---------------------------Operations---------------------------*/
    public static void handleTrainingRequests(List<TrainingTransactionData> trainingTransactionsList, List<TrainingTransactionDetailData> trainingTransactionDetails, String processName, TrainingCourseEventData courseEventData, int trainingTransactionCategory, CustomSession... useSession) throws BusinessException {
	validateTrainingTransactions(trainingTransactionsList, courseEventData, trainingTransactionCategory);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (courseEventData != null && courseEventData.getId() == null)
		TrainingCoursesEventsService.addTrainingCourseEvent(courseEventData, trainingTransactionCategory, session);

	    for (int i = 0; i < trainingTransactionsList.size(); i++) {
		if (courseEventData != null)
		    trainingTransactionsList.get(i).setCourseEventId(courseEventData.getId());
		if (trainingTransactionsList.get(i).getId() == null)
		    addTrainingTransaction(trainingTransactionsList.get(i), session);
		else
		    updateTrainingTransaction(trainingTransactionsList.get(i), session);
	    }

	    if (trainingTransactionDetails != null) {
		for (int i = 0; i < trainingTransactionDetails.size(); i++) {
		    if (trainingTransactionDetails.get(i).getTrainingTransactionId() == null)
			trainingTransactionDetails.get(i).setTrainingTransactionId(trainingTransactionsList.get(0).getId());
		    addTrainingTransactionDetail(trainingTransactionDetails.get(i), processName, session);
		}
	    }
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doTrainingRequestsPayrollIntegration(trainingTransactionsList, session);
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void saveTrainingTransactionResult(TrainingTransactionData trainingTransaction, long courseEventId, CustomSession... useSession) throws BusinessException {
	List<TrainingTransactionData> loadedTrainingTransactions = TrainingEmployeesService.getTrainingTransactionsDataByIds(new Long[] { trainingTransaction.getId() });
	if (loadedTrainingTransactions.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	TrainingTransactionData loadedTrainingTransaction = loadedTrainingTransactions.get(0);

	TrainingEmployeesService.validateTrainingTransactionsForResultRegistration(Arrays.asList(trainingTransaction), courseEventId);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (trainingTransaction.getTrainingTypeId().longValue() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {
		TrainingCourseEventData loadedCourseEvent = TrainingCoursesEventsService.getCourseEventById(courseEventId);
		if (loadedCourseEvent == null)
		    throw new BusinessException("error_transactionDataError");

		if (loadedCourseEvent.getStatus().longValue() != TrainingCourseEventStatusEnum.COURSE_EVENT_HELD.getCode()) {

		    if (loadedCourseEvent.getStatus() != TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode())
			throw new BusinessException("error_trainingCourseStatusNotValid", new String[] { loadedCourseEvent.getCourseName() });

		    loadedCourseEvent.setStatus(TrainingCourseEventStatusEnum.COURSE_EVENT_HELD.getCode());
		    DataAccess.updateEntity(loadedCourseEvent.getTrainingCourseEvent(), session);
		}
	    }

	    loadedTrainingTransaction.setSuccessFlag(trainingTransaction.getSuccessFlag());
	    if (loadedTrainingTransaction.getAttachments() == null)
		loadedTrainingTransaction.setAttachments(trainingTransaction.getAttachments());

	    if (trainingTransaction.getTrainingTypeId().longValue() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()
		    || trainingTransaction.getTrainingTypeId().longValue() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {

		loadedTrainingTransaction.setSuccessRanking(trainingTransaction.getSuccessRanking());
		loadedTrainingTransaction.setSuccessRankingDesc(trainingTransaction.getSuccessRankingDesc());
		loadedTrainingTransaction.setQualificationGradePercentage(trainingTransaction.getQualificationGradePercentage());
		loadedTrainingTransaction.setQualificationGrade(trainingTransaction.getQualificationGrade());
		loadedTrainingTransaction.setAttendanceGradePercentage(trainingTransaction.getAttendanceGradePercentage());
		loadedTrainingTransaction.setAttendanceGrade(trainingTransaction.getAttendanceGrade());
		loadedTrainingTransaction.setBehaviorGradePercentage(trainingTransaction.getBehaviorGradePercentage());
		loadedTrainingTransaction.setBehaviorGrade(trainingTransaction.getBehaviorGrade());

		if (trainingTransaction.getTrainingTypeId().longValue() != TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode())
		    loadedTrainingTransaction.setStatus(TraineeStatusEnum.FINISHED.getCode());
	    } else {
		loadedTrainingTransaction.setQualificationGrade(trainingTransaction.getQualificationGrade());

		if (trainingTransaction.getSuccessFlagBoolean() && (trainingTransaction.getTrainingTypeId().longValue() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode()
			|| trainingTransaction.getTrainingTypeId().longValue() == TrainingTypesEnum.SCHOLARSHIP.getCode())) {
		    loadedTrainingTransaction.setStudyGraduationDate(trainingTransaction.getStudyGraduationDate());
		}
	    }

	    updateTrainingTransaction(loadedTrainingTransaction, session);
	    if (trainingTransaction.getTrainingTypeId().longValue() != TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doTrainingTransactionResultPayrollIntegration(loadedTrainingTransactions, session);
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

    public static void updateTrainingTransactionJoiningDate(long transactionId, Date joiningDate, long loginEmpId, CustomSession... useSession) throws BusinessException {

	TrainingTransactionData transaction = getTrainingTransactionsDataByIds(new Long[] { transactionId }).get(0);
	transaction.setJoiningDate(joiningDate);
	validateTrainingTransactionJoiningDate(transaction);
	transaction.getTrainingTransaction().setSystemUser(loginEmpId + "");// for auditing
	updateTrainingTransaction(transaction, useSession);
    }

    public static void updateTrainingTransactionTrainingJoiningDate(long transactionId, Date trainingJoiningDate, long loginEmpId, Integer newStatus, CustomSession... useSession) throws BusinessException {

	TrainingTransactionData transaction = getTrainingTransactionsDataByIds(new Long[] { transactionId }).get(0);
	validateTrainingTransactionTrainingJoiningDate(transaction, newStatus, trainingJoiningDate);
	transaction.setStatus(newStatus);
	transaction.setTrainingJoiningDate(trainingJoiningDate);
	transaction.getTrainingTransaction().setSystemUser(loginEmpId + "");// for auditing
	updateTrainingTransaction(transaction, useSession);
	doJoiningPayrollIntegration(transaction, isSessionOpened(useSession) ? useSession[0] : DataAccess.getSession());
    }

    public static void updateExternalMilitaryTraniningTransactionAcceptance(long transactionId, long loginEmpId, int newStatus, CustomSession... useSession) throws BusinessException {

	TrainingTransactionData transaction = getTrainingTransactionsDataByIds(new Long[] { transactionId }).get(0);
	validateExternalMilitaryTrainingTransactionForAcceptanceRegistration(transaction, newStatus);
	transaction.getTrainingTransaction().setSystemUser(loginEmpId + "");// for auditing
	transaction.setStatus(newStatus);
	updateTrainingTransaction(transaction, useSession);
    }

    public static void updateTraineeStatusForTraineeExtCourseEventCancellation(long transactionId, Integer reasonType, String reasons, long loginEmpId, CustomSession... useSession) throws BusinessException {

	TrainingTransactionData transaction = getTrainingTransactionsDataByIds(new Long[] { transactionId }).get(0);
	validateTraineeStatusForTraineeExtCourseEventCancellation(transaction, reasonType);
	transaction.getTrainingTransaction().setSystemUser(loginEmpId + "");// for auditing
	transaction.setStatus(TraineeStatusEnum.CANCELLED.getCode());
	transaction.setReasonType(reasonType);
	transaction.setReasons(reasons);
	updateTrainingTransaction(transaction, useSession);
    }

    public static void payrollIntegrationFailureHandle(Long transactionId, Long adminDecisionId, CustomSession session) throws BusinessException {
	List<TrainingTransactionData> trainingTransactionDataList = getTrainingTransactionsDataByIds(new Long[] { transactionId });
	if (trainingTransactionDataList == null || trainingTransactionDataList.size() == 0)
	    throw new BusinessException("error_transactionDataRetrievingError");
	TrainingTransactionData trainingTransactionData = trainingTransactionDataList.get(0);

	List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>();
	String gregStartDateString = null, gregEndDateString = null, originalDecisionNumber = null, decisionNumber = null, gregDecisionDateString = null;
	List<TrainingTransactionDetailData> trainingTransactionDetailDataList = getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionData.getId());
	TrainingTransactionDetailData lastTransactionDetail = trainingTransactionDetailDataList == null || trainingTransactionDetailDataList.size() == 0 ? null : trainingTransactionDetailDataList.get(0);
	TrainingTransactionDetailData firstTransactionDetail = trainingTransactionDetailDataList == null || trainingTransactionDetailDataList.size() == 0 ? null : trainingTransactionDetailDataList.get(trainingTransactionDetailDataList.size() - 1);
	EmployeeData employee = EmployeesService.getEmployeeData(trainingTransactionData.getEmployeeId());

	if (adminDecisionId == AdminDecisionsEnum.TRAINING_COURSE_WITHOUT_RANKING_RESULTS_REGISTERATION.getCode() || adminDecisionId == AdminDecisionsEnum.TRAINING_COURSE_WITH_RANKING_RESULTS_REGISTERATION.getCode()) {
	    if (trainingTransactionData.getTrainingTypeId().equals(TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode())) {
		decisionNumber = firstTransactionDetail.getDecisionNumber();
		gregDecisionDateString = HijriDateService.hijriToGregDateString(firstTransactionDetail.getDecisionDateString());
	    } else {
		decisionNumber = System.currentTimeMillis() + "";
		gregDecisionDateString = HijriDateService.hijriToGregDateString(HijriDateService.getHijriSysDateString());
	    }
	    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getActualStartDateString());
	    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getActualEndDateString());
	} else if (adminDecisionId == AdminDecisionsEnum.SCHOLARSHIP_RESULTS_REGISTERATION.getCode()) {
	    decisionNumber = System.currentTimeMillis() + "";
	    originalDecisionNumber = firstTransactionDetail.getDecisionNumber();
	    gregDecisionDateString = HijriDateService.hijriToGregDateString(firstTransactionDetail.getDecisionDateString());
	    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyStartDateString());
	    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
	} else if (adminDecisionId == AdminDecisionsEnum.SCHOLARSHIP_JOINING_REGISTERATION.getCode()) {
	    decisionNumber = System.currentTimeMillis() + "";
	    originalDecisionNumber = firstTransactionDetail.getDecisionNumber();
	    gregDecisionDateString = HijriDateService.hijriToGregDateString(firstTransactionDetail.getDecisionDateString());
	    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getTrainingJoiningDateString());
	} else if (adminDecisionId == AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_DECISION_REQUEST.getCode() || adminDecisionId == AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_EXTENSION_DECISION_REQUEST.getCode()
		|| adminDecisionId == AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_TERMINATION_DECISION_REQUEST.getCode() || adminDecisionId == AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_CANCELLATION_DECISION_REQUEST.getCode()
		||
		adminDecisionId == AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_DECISION_REQUEST.getCode() || adminDecisionId == AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_EXTENSION_DECISION_REQUEST.getCode()
		|| adminDecisionId == AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_TERMINATION_DECISION_REQUEST.getCode() || adminDecisionId == AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_CANCELLATION_DECISION_REQUEST.getCode()) {
	    decisionNumber = lastTransactionDetail.getDecisionNumber();
	    originalDecisionNumber = (adminDecisionId == AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_DECISION_REQUEST.getCode() || adminDecisionId == AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_DECISION_REQUEST.getCode()) ? null : firstTransactionDetail.getDecisionNumber();
	    gregDecisionDateString = HijriDateService.hijriToGregDateString(lastTransactionDetail.getDecisionDateString());
	    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyStartDateString());
	    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
	}
	adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(trainingTransactionData.getEmployeeId(), trainingTransactionData.getEmployeeName(), trainingTransactionData.getId(), null, gregStartDateString, gregEndDateString, decisionNumber, originalDecisionNumber));
	Long unitId = employee.getPhysicalUnitId() != null ? employee.getPhysicalUnitId() : UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId();
	PayrollEngineService.doPayrollIntegration(adminDecisionId, employee.getCategoryId(), gregStartDateString, adminDecisionEmployeeDataList, trainingTransactionData.getAttachments(), unitId, gregDecisionDateString, DataAccess.getTableName(TrainingTransaction.class), FlagsEnum.ON.getCode(), FlagsEnum.OFF.getCode(), session);
    }

    private static void doTrainingRequestsPayrollIntegration(List<TrainingTransactionData> trainingTransactionDataList, CustomSession session) throws BusinessException {
	session.flushTransaction();
	for (TrainingTransactionData trainingTransactionData : trainingTransactionDataList) {
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>();
	    Long adminDecision = null;
	    String gregStartDateString = null, gregEndDateString = null, originalDecisionNumber = null, decisionNumber = null, gregDecisionDateString = null;
	    List<TrainingTransactionDetailData> trainingTransactionDetailDataList = getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionData.getId());
	    TrainingTransactionDetailData lastTransactionDetail = trainingTransactionDetailDataList == null || trainingTransactionDetailDataList.size() == 0 ? null : trainingTransactionDetailDataList.get(0);
	    TrainingTransactionDetailData firstTransactionDetail = trainingTransactionDetailDataList == null || trainingTransactionDetailDataList.size() == 0 ? null : trainingTransactionDetailDataList.get(trainingTransactionDetailDataList.size() - 1);
	    EmployeeData employee = EmployeesService.getEmployeeData(trainingTransactionData.getEmployeeId());
	    if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		if (trainingTransactionData.getTrainingTypeId().equals(TrainingTypesEnum.SCHOLARSHIP.getCode()) && trainingTransactionDetailDataList != null && trainingTransactionDetailDataList.size() > 0) {
		    decisionNumber = lastTransactionDetail.getDecisionNumber();
		    originalDecisionNumber = firstTransactionDetail.getDecisionNumber();
		    gregDecisionDateString = HijriDateService.hijriToGregDateString(lastTransactionDetail.getDecisionDateString());
		    if (CountryService.getCountryByCode(CountriesEnum.SAUDI_ARABIA.getCode()).getId().equals(TrainingSetupService.getGraduationPlaceDetailDataById(trainingTransactionData.getGraduationPlaceDetailId()).getGraduationPlaceCountryId())) {
			if (lastTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
			    adminDecision = AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_DECISION_REQUEST.getCode();
			    originalDecisionNumber = null;
			    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyStartDateString());
			    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
			} else if (lastTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_EXTENSION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
			    adminDecision = AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_EXTENSION_DECISION_REQUEST.getCode();
			    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyStartDateString());
			    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
			} else if (lastTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TERMINATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
			    adminDecision = AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_TERMINATION_DECISION_REQUEST.getCode();
			    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
			} else if (lastTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_CANCELLATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
			    adminDecision = AdminDecisionsEnum.INTERNAL_SCHOLARSHIP_CANCELLATION_DECISION_REQUEST.getCode();
			    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
			}
		    } else {
			if (lastTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
			    adminDecision = AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_DECISION_REQUEST.getCode();
			    originalDecisionNumber = null;
			    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyStartDateString());
			    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
			} else if (lastTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_EXTENSION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
			    adminDecision = AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_EXTENSION_DECISION_REQUEST.getCode();
			    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyStartDateString());
			    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
			} else if (lastTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TERMINATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
			    adminDecision = AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_TERMINATION_DECISION_REQUEST.getCode();
			    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
			} else if (lastTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_CANCELLATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
			    adminDecision = AdminDecisionsEnum.EXTERNAL_SCHOLARSHIP_CANCELLATION_DECISION_REQUEST.getCode();
			    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyEndDateString());
			}
		    }
		}
	    }
	    if (adminDecision != null) {
		adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(trainingTransactionData.getEmployeeId(), employee.getName(), trainingTransactionData.getId(), null, gregStartDateString, gregEndDateString, decisionNumber, originalDecisionNumber));
		Long unitId = employee.getPhysicalUnitId() != null ? employee.getPhysicalUnitId() : UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId();
		Integer originalDecisionFlag = trainingTransactionData.getTrainingJoiningDate() != null ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode();
		PayrollEngineService.doPayrollIntegration(adminDecision, employee.getCategoryId(), gregStartDateString, adminDecisionEmployeeDataList, trainingTransactionData.getAttachments(), unitId, gregDecisionDateString, DataAccess.getTableName(TrainingTransaction.class), FlagsEnum.OFF.getCode(), originalDecisionFlag, session);
	    }
	}
    }

    private static void doTrainingTransactionResultPayrollIntegration(List<TrainingTransactionData> trainingTransactionDataList, CustomSession session) throws BusinessException {
	session.flushTransaction();
	for (TrainingTransactionData trainingTransactionData : trainingTransactionDataList) {
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>();
	    Long adminDecision = null;
	    String gregStartDateString = null, gregEndDateString = null, originalDecisionNumber = null, gregDecisionDateString = null;
	    List<TrainingTransactionDetailData> trainingTransactionDetailDataList = getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionData.getId());
	    TrainingTransactionDetailData firstTransactionDetail = trainingTransactionDetailDataList == null || trainingTransactionDetailDataList.size() == 0 ? null : trainingTransactionDetailDataList.get(trainingTransactionDetailDataList.size() - 1);
	    EmployeeData employee = EmployeesService.getEmployeeData(trainingTransactionData.getEmployeeId());
	    if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		if (trainingTransactionData.getTrainingTypeId().equals(TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) || trainingTransactionData.getTrainingTypeId().equals(TrainingTypesEnum.MORNING_COURSE.getCode())
			|| trainingTransactionData.getTrainingTypeId().equals(TrainingTypesEnum.EVENING_COURSE.getCode())) {
		    TrainingCourseEventData courseEvent = TrainingCoursesEventsService.getCourseEventById(trainingTransactionData.getCourseEventId());
		    if (courseEvent.getRankingFlag() == null || courseEvent.getRankingFlag().equals(FlagsEnum.OFF.getCode()))
			adminDecision = AdminDecisionsEnum.TRAINING_COURSE_WITHOUT_RANKING_RESULTS_REGISTERATION.getCode();
		    else
			adminDecision = AdminDecisionsEnum.TRAINING_COURSE_WITH_RANKING_RESULTS_REGISTERATION.getCode();
		    gregDecisionDateString = HijriDateService.hijriToGregDateString(HijriDateService.getHijriSysDateString());
		    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getActualStartDateString());
		    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getActualEndDateString());
		} else if (trainingTransactionData.getTrainingTypeId().equals(TrainingTypesEnum.SCHOLARSHIP.getCode())) {
		    if (trainingTransactionData.getSuccessFlag() != null && trainingTransactionData.getQualificationGrade() != null) {
			adminDecision = AdminDecisionsEnum.SCHOLARSHIP_RESULTS_REGISTERATION.getCode();
			originalDecisionNumber = firstTransactionDetail.getDecisionNumber();
			gregDecisionDateString = HijriDateService.hijriToGregDateString(firstTransactionDetail.getDecisionDateString());
			gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getStudyGraduationDateString());
		    }
		}
	    }
	    if (adminDecision != null) {
		adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(trainingTransactionData.getEmployeeId(), employee.getName(), trainingTransactionData.getId(), null, gregStartDateString, gregEndDateString, System.currentTimeMillis() + "", originalDecisionNumber));
		Long unitId = employee.getPhysicalUnitId() != null ? employee.getPhysicalUnitId() : UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId();
		PayrollEngineService.doPayrollIntegration(adminDecision, employee.getCategoryId(), gregStartDateString, adminDecisionEmployeeDataList, trainingTransactionData.getAttachments(), unitId, gregDecisionDateString, DataAccess.getTableName(TrainingTransaction.class), FlagsEnum.OFF.getCode(), FlagsEnum.OFF.getCode(), session);
	    }
	}
    }

    private static void doJoiningPayrollIntegration(TrainingTransactionData trainingTransactionData, CustomSession session) throws BusinessException {
	session.flushTransaction();
	List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>();
	Long adminDecision = null;
	List<TrainingTransactionDetailData> trainingTransactionDetailDataList = getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionData.getId());
	TrainingTransactionDetailData firstTransactionDetail = trainingTransactionDetailDataList == null || trainingTransactionDetailDataList.size() == 0 ? null : trainingTransactionDetailDataList.get(trainingTransactionDetailDataList.size() - 1);
	EmployeeData employee = EmployeesService.getEmployeeData(trainingTransactionData.getEmployeeId());
	if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
	    if (trainingTransactionData.getTrainingJoiningDate() != null)
		adminDecision = AdminDecisionsEnum.SCHOLARSHIP_JOINING_REGISTERATION.getCode();
	}
	if (adminDecision != null) {
	    String originalDecisionNumber = firstTransactionDetail.getDecisionNumber();
	    String gregDecisionDateString = HijriDateService.hijriToGregDateString(firstTransactionDetail.getDecisionDateString());
	    String gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getTrainingJoiningDateString());
	    adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(trainingTransactionData.getEmployeeId(), employee.getName(), trainingTransactionData.getId(), null, gregStartDateString, null, System.currentTimeMillis() + "", originalDecisionNumber));
	    Long unitId = employee.getPhysicalUnitId() != null ? employee.getPhysicalUnitId() : UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId();
	    PayrollEngineService.doPayrollIntegration(adminDecision, employee.getCategoryId(), gregStartDateString, adminDecisionEmployeeDataList, trainingTransactionData.getAttachments(), unitId, gregDecisionDateString, DataAccess.getTableName(TrainingTransaction.class), FlagsEnum.OFF.getCode(), FlagsEnum.ON.getCode(), session);
	}
    }

    private static void addTrainingTransaction(TrainingTransactionData trainingTransaction, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(trainingTransaction.getTrainingTransaction(), session);
	    trainingTransaction.setId(trainingTransaction.getTrainingTransaction().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    trainingTransaction.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    protected static void updateTrainingTransaction(TrainingTransactionData trainingTransaction, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(trainingTransaction.getTrainingTransaction(), session);

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

    public static void deleteTrainingTransactionClaim(long trainingTransactionId, long loginEmpId) throws BusinessException {
	TrainingTransactionData trainingTransaction = getTrainingTransactionsDataByIds(new Long[] { trainingTransactionId }).get(0);
	if (trainingTransaction.getEflag() == FlagsEnum.ON.getCode()) {
	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EVENING_COURSE.getCode() || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.MORNING_COURSE.getCode()) {
		if (trainingTransaction.getSuccessFlag() != null) {
		    throw new BusinessException("error_transactionMustBeWithoutResults");
		}
	    } else {
		throw new BusinessException("error_transactionMustBeClaim");
	    }
	}
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    trainingTransaction.getTrainingTransaction().setSystemUser(loginEmpId + "");
	    DataAccess.deleteEntity(trainingTransaction.getTrainingTransaction(), session);
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

    /*---------------------------Validations--------------------------*/
    public static void validateTrainingTransactionsForResultRegistration(List<TrainingTransactionData> trainingTransactionsList, Long courseEventId) throws BusinessException {
	if (trainingTransactionsList == null || trainingTransactionsList.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	for (TrainingTransactionData trainingTransaction : trainingTransactionsList) {

	    // Validate mandatory fields
	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()
		    || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {

		if (trainingTransaction.getTrainingJoiningDate() == null)
		    throw new BusinessException("error_cannotRegisterResultForNonJoinedCandidate", new Object[] { trainingTransaction.getEmployeeName() });
	    }

	    validateTransactionResultFields(trainingTransaction);

	    if (trainingTransaction.getTrainingTypeId() != TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && trainingTransaction.getTrainingTypeId() != TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {
		if (trainingTransaction.getAttachments() == null || trainingTransaction.getAttachments().isEmpty())
		    throw new BusinessException("error_trainingAttachmentsMandatory");
	    }

	    // Validate business rules
	    TrainingCourseEventData loadedCourseEvent = courseEventId == null ? null : TrainingCoursesEventsService.getCourseEventById(courseEventId);
	    if (courseEventId != null && loadedCourseEvent == null)
		throw new BusinessException("error_transactionDataError");

	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {

		if (loadedCourseEvent.getStatus() != TrainingCourseEventStatusEnum.COURSE_EVENT_HOLDING_DECISION_ISSUED.getCode())
		    throw new BusinessException("error_trainingCourseStatusNotValid", new String[] { loadedCourseEvent.getCourseName() });

		if (trainingTransaction.getSuccessRanking() != null) {
		    int joinedEmployeesCount = TrainingCoursesEventsService.getLastTrnCourseEventDecisionEmployees(courseEventId).size();
		    if (trainingTransaction.getSuccessRanking().intValue() > joinedEmployeesCount) {
			throw new BusinessException("error_invalidSuccessRanking");
		    }
		}
	    } else if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {

		if (!TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(), TraineeStatusEnum.NOMINATION_ACCEPTED.getCode() }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId).isEmpty())
		    throw new BusinessException("error_cannotRegisterResults");

	    } else if (trainingTransaction.getTrainingTypeId().longValue() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode() || trainingTransaction.getTrainingTypeId().longValue() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {

		if (trainingTransaction.getSuccessFlagBoolean() && !trainingTransaction.getStudyGraduationDate().before(HijriDateService.getHijriSysDate()))
		    throw new BusinessException("error_trainingGraduationDateAfterResultRegistrationDate");

		if (trainingTransaction.getTrainingTypeId().longValue() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode()) {
		    if (trainingTransaction.getSuccessFlagBoolean() && !trainingTransaction.getStudyGraduationDate().after(trainingTransaction.getStudyStartDate()))
			throw new BusinessException("error_trainingGraduationDateBeforeStudyStartDate");
		} else {
		    if (trainingTransaction.getSuccessFlagBoolean() && !trainingTransaction.getStudyGraduationDate().after(trainingTransaction.getTrainingJoiningDate()))
			throw new BusinessException("error_trainingGraduationDateBeforeTrainingJoiningDate");
		}
	    }
	}
    }

    public static void validateTrainingTransactions(List<TrainingTransactionData> trainingTransactionsList, TrainingCourseEventData courseEventData, int trainingTransactionCategory) throws BusinessException {
	if (trainingTransactionsList == null || trainingTransactionsList.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	for (TrainingTransactionData transactionData : trainingTransactionsList) {
	    validateTransactionMandatoryFields(transactionData, trainingTransactionCategory);
	    validateTransactionBusinessRules(transactionData, courseEventData, trainingTransactionCategory);
	}
    }

    private static void validateTransactionMandatoryFields(TrainingTransactionData trainingTransaction, int trainingTransactionCategory) throws BusinessException {
	if (trainingTransaction == null || trainingTransaction.getTrainingTypeId() == null || trainingTransaction.getStatus() == null)
	    throw new BusinessException("error_transactionDataError");

	if (trainingTransaction.getEmployeeId() == null)
	    throw new BusinessException("error_employeeMandatory");

	if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {

	    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_APOLOGY.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_REPLACEMENT.getCode()) {

		if (trainingTransaction.getCourseEventId() == null)
		    throw new BusinessException("error_trainingCourseIsMandatory");

		if (trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION.getCode()) {

		    if ((trainingTransaction.getTrainingPurpose() == null || trainingTransaction.getTrainingPurpose().trim().isEmpty()) && (trainingTransaction.getOtherPurpose() == null || trainingTransaction.getOtherPurpose().trim().isEmpty()))
			throw new BusinessException("error_trainingPurposeMandatoryForEmp", new Object[] { trainingTransaction.getEmployeeName() == null ? EmployeesService.getEmployeeData(trainingTransaction.getEmployeeId()).getName() : trainingTransaction.getEmployeeName() });

		    if (trainingTransaction.getOtherPurpose() != null && trainingTransaction.getOtherPurpose().length() > MAX_OTHER_PURPOSES_LENGTH)
			throw new BusinessException("error_otherPurposesInvalidLength");

		} else {
		    if (trainingTransaction.getReasons() == null || trainingTransaction.getReasons().trim().isEmpty() || trainingTransaction.getReasons().length() > MAX_STRING_LENGTH)
			throw new BusinessException("error_reasonsMandatory");

		    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_APOLOGY.getCode()) {
			if (trainingTransaction.getReasonType() == null)
			    throw new BusinessException("error_apologyReasonMandatory");
		    }

		    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_REPLACEMENT.getCode()) {

			if (trainingTransaction.getReplacementEmployeeId() == null)
			    throw new BusinessException("error_replacementEmployeeMandatory");

			if (trainingTransaction.getReasonType() == null)
			    throw new BusinessException("error_replacementReasonMandatory");
		    }
		}
	    } else if (trainingTransactionCategory == TrainingTransactionCategoryEnum.EMPLOYEE_COURSE_EVENT_CANCEL.getCode()) {
		if (trainingTransaction.getCourseEventId() == null)
		    throw new BusinessException("error_trainingCourseIsMandatory");

		if (trainingTransaction.getReasonType() == null)
		    throw new BusinessException("error_cancelReasonMandatory");
	    }
	} else {
	    if (trainingTransaction.getFundSource() == null && (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode()))
		throw new BusinessException("error_financeSourceMandatory");

	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode() || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {
		if (trainingTransaction.getGraduationPlaceDetailId() == null)
		    throw new BusinessException("error_trainingGraduationPlaceDetailMandatory");
		if (trainingTransaction.getStudyStartDate() == null || !HijriDateService.isValidHijriDate(trainingTransaction.getStudyStartDate()))
		    throw new BusinessException("error_trainingStudyStartDateInvalid");
		if (trainingTransaction.getStudyEndDate() == null || !HijriDateService.isValidHijriDate(trainingTransaction.getStudyEndDate()))
		    throw new BusinessException("error_trainingStudyEndDateInvalid");
		if (trainingTransaction.getQualificationLevelId() == null || trainingTransaction.getQualificationLevelId() == 0)
		    throw new BusinessException("error_trainingQualificationLevelMandatory");
		if (trainingTransaction.getStudySubject() == null || trainingTransaction.getStudySubject().length() == 0)
		    throw new BusinessException("error_trainingStudySubjectMandatory");
		if (trainingTransaction.getQualificationMinorSpecId() == null && trainingTransactionCategory == TrainingTransactionCategoryEnum.CLAIM.getCode())
		    throw new BusinessException("error_studyQualificationMinorSpecMandatory");
		if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode() &&
			((trainingTransaction.getStudyMonthsCount() == null || trainingTransaction.getStudyMonthsCount() == 0)
				&& (trainingTransaction.getStudyDaysCount() == null || trainingTransaction.getStudyDaysCount() == 0)))
		    throw new BusinessException("error_trainingScholarshipMonthsCountMandatory");
	    }
	}

	if (trainingTransactionCategory == TrainingTransactionCategoryEnum.CLAIM.getCode()) {
	    validateTransactionResultFields(trainingTransaction);

	    if (trainingTransaction.getTrainingTypeId() != TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && trainingTransaction.getTrainingTypeId() != TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {
		if (trainingTransaction.getAttachments() == null || trainingTransaction.getAttachments().isEmpty())
		    throw new BusinessException("error_trainingAttachmentsMandatory");
	    }
	}
    }

    private static void validateTransactionResultFields(TrainingTransactionData trainingTransaction) throws BusinessException {
	if (trainingTransaction.getSuccessFlag() == null)
	    throw new BusinessException("error_courseResultsMandatoryForEmployee", new Object[] { trainingTransaction.getEmployeeName() });

	if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {
	    if (trainingTransaction.getSuccessRanking() != null && trainingTransaction.getSuccessRanking() <= 0)
		throw new BusinessException("error_successRankingCannotBeZeroOrLess");

	    if (((trainingTransaction.getSuccessRankingDesc() == null || trainingTransaction.getSuccessRankingDesc().trim().isEmpty()) && trainingTransaction.getSuccessRanking() != null)
		    || (trainingTransaction.getSuccessRanking() == null && (trainingTransaction.getSuccessRankingDesc() != null && !trainingTransaction.getSuccessRankingDesc().trim().isEmpty())))
		throw new BusinessException("error_successRankMandatory");

	    if ((trainingTransaction.getQualificationGradePercentage() != null) && (trainingTransaction.getQualificationGradePercentage() > 100.0 || trainingTransaction.getQualificationGradePercentage() < 0.0 || (trainingTransaction.getSuccessFlag() == FlagsEnum.ON.getCode() && trainingTransaction.getQualificationGradePercentage() == 0.0)))
		throw new BusinessException("error_qualificationGradePercentageInvalid");

	    if (trainingTransaction.getQualificationGrade() == null)
		throw new BusinessException("error_militaryQualificationGradeMandatory");

	    if (trainingTransaction.getAttendanceGradePercentage() != null && (trainingTransaction.getAttendanceGradePercentage() > 100.0 || trainingTransaction.getAttendanceGradePercentage() < 0.0))
		throw new BusinessException("error_attendenceGradePercentageInvalid");

	    if (trainingTransaction.getAttendanceGrade() == null)
		throw new BusinessException("error_attendenceGradeMandatory");

	    if (trainingTransaction.getBehaviorGradePercentage() != null && (trainingTransaction.getBehaviorGradePercentage() > 100.0 || trainingTransaction.getBehaviorGradePercentage() < 0.0))
		throw new BusinessException("error_behaviorGradePercentageInvalid");

	    if (trainingTransaction.getBehaviorGrade() == null)
		throw new BusinessException("error_behaviorGradeMandatory");
	} else {
	    if (trainingTransaction.getQualificationGrade() == null)
		throw new BusinessException("error_courseGradeMandatory");

	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode() || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {
		if (trainingTransaction.getSuccessFlagBoolean() && (trainingTransaction.getStudyGraduationDate() == null || !HijriDateService.isValidHijriDate(trainingTransaction.getStudyGraduationDate())))
		    throw new BusinessException("error_trainingGraduationDateInvalid");
	    }
	}
    }

    private static void validateTransactionBusinessRules(TrainingTransactionData trainingTransaction, TrainingCourseEventData courseEventData, int trainingTransactionCategory) throws BusinessException {
	Long[] excludedIds = null;
	if (trainingTransaction.getId() != null) {
	    excludedIds = new Long[] { trainingTransaction.getId() };
	}

	EmployeeData employee = EmployeesService.getEmployeeData(trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_REPLACEMENT.getCode() ? trainingTransaction.getReplacementEmployeeId() : trainingTransaction.getEmployeeId());

	if (trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_APOLOGY.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_REPLACEMENT.getCode()) {
	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
		if (courseEventData.getStatus().intValue() != TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode() && courseEventData.getStatus().intValue() != TrainingCourseEventStatusEnum.COURSE_EVENT_HOLDING_DECISION_ISSUED.getCode() && courseEventData.getStatus().intValue() != TrainingCourseEventStatusEnum.COURSE_EVENT_POSTPONEMENT_DECISION_ISSUED.getCode())
		    throw new BusinessException("error_trainingCourseStatusNotValid", new String[] { courseEventData.getCourseName() });
	    } else if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {
		if (courseEventData.getStatus().intValue() != TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode())
		    throw new BusinessException("error_trainingCourseStatusNotValid", new String[] { courseEventData.getCourseName() });
	    }
	}

	if (trainingTransactionCategory == TrainingTransactionCategoryEnum.CLAIM.getCode() && courseEventData != null && courseEventData.getActualEndDate().after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_courseStartAndEndDateBeforeRequestDate");

	if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()
		|| trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {

	    if (countEmployeeConflictCoursesEventsDates(excludedIds, courseEventData.getTrainingTypeId(), trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_REPLACEMENT.getCode() ? trainingTransaction.getReplacementEmployeeId() : trainingTransaction.getEmployeeId(), courseEventData.getActualStartDateString(), courseEventData.getActualEndDateString()) > 0)
		throw new BusinessException("error_trainingDatesConflict", new Object[] { trainingTransaction.getEmployeeName() == null ? EmployeesService.getEmployeeData(trainingTransaction.getEmployeeId()).getName() : trainingTransaction.getEmployeeName() });

	    if (countConflictTrainingTransactions(trainingTransaction.getTrainingTypeId(), trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION_REPLACEMENT.getCode() ? trainingTransaction.getReplacementEmployeeId() : trainingTransaction.getEmployeeId(), courseEventData.getActualStartDateString(), courseEventData.getActualEndDateString(), trainingTransaction.getId() == null ? FlagsEnum.ALL.getCode() : trainingTransaction.getId()) > 0)
		throw new BusinessException("error_trainingDatesConflict", new Object[] { trainingTransaction.getEmployeeName() == null ? EmployeesService.getEmployeeData(trainingTransaction.getEmployeeId()).getName() : trainingTransaction.getEmployeeName() });

	    if (employee.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && employee.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()) {
		throw new BusinessException("error_militaryTrainingNotAllowedForCivilians", new Object[] { employee.getName() });
	    }

	    // Check allowed nomination for internal military nomination
	    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION.getCode()) {
		validateCourseEventPrerequistes(employee, courseEventData);
		if (courseEventData.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
		    List<TrainingUnitData> trainingUnits = TrainingSetupService.getAllowedForNominationTrainingUnits(employee.getPhysicalRegionId());
		    boolean found = false;
		    for (TrainingUnitData trainingUnit : trainingUnits) {
			if (trainingUnit.getUnitId().equals(courseEventData.getTrainingUnitId())) {
			    found = true;
			    break;
			}
		    }

		    if (!found)
			throw new BusinessException("error_noNominationAuthority", new String[] { employee.getName(), courseEventData.getTrainingUnitName() });
		} else {
		    if (HijriDateService.hijriDateDiff(HijriDateService.getHijriSysDate(), courseEventData.getActualEndDate()) < 0)
			throw new BusinessException("error_noNominationCourseEventEndDateBeforToday");
		}
	    }

	} else if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.MORNING_COURSE.getCode() || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EVENING_COURSE.getCode()) {
	    if (countEmployeeConflictCoursesEventsDates(excludedIds, courseEventData.getTrainingTypeId(), trainingTransaction.getEmployeeId(), courseEventData.getActualStartDateString(), courseEventData.getActualEndDateString()) > 0)
		throw new BusinessException("error_trainingDatesConflict", new Object[] { trainingTransaction.getEmployeeName() == null ? EmployeesService.getEmployeeData(trainingTransaction.getEmployeeId()).getName() : trainingTransaction.getEmployeeName() });

	    if (countConflictTrainingTransactions(trainingTransaction.getTrainingTypeId(), trainingTransaction.getEmployeeId(), courseEventData.getActualStartDateString(), courseEventData.getActualEndDateString(), trainingTransaction.getId() == null ? FlagsEnum.ALL.getCode() : trainingTransaction.getId()) > 0)
		throw new BusinessException("error_trainingDatesConflict", new Object[] { trainingTransaction.getEmployeeName() == null ? EmployeesService.getEmployeeData(trainingTransaction.getEmployeeId()).getName() : trainingTransaction.getEmployeeName() });

	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.MORNING_COURSE.getCode() && trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION.getCode()) {
		if (HijriDateService.hijriDateDiff(HijriDateService.getHijriSysDate(), courseEventData.getActualEndDate()) < 0)
		    throw new BusinessException("error_noNominationCourseEventEndDateBeforToday");
	    }

	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.EVENING_COURSE.getCode() && trainingTransactionCategory == TrainingTransactionCategoryEnum.NOMINATION.getCode()) {
		if (HijriDateService.hijriDateDiff(courseEventData.getActualStartDate(), courseEventData.getActualEndDate()) > MAX_EVENING_COURSE_NOMINATION_PERIOD_DAYS) {
		    throw new BusinessException("error_eveningCourseNominationMaxPeriod");
		}
	    }
	} else if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode() || trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {
	    if (trainingTransactionCategory != TrainingTransactionCategoryEnum.CLAIM.getCode() &&
		    (trainingTransaction.getStatus() == TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode() || trainingTransaction.getStatus() == TraineeStatusEnum.SCHOLARSHIP.getCode()))
		if (trainingTransaction.getStudyStartDate().before(HijriDateService.getHijriSysDate()))
		    throw new BusinessException("error_studyStartDateBeforeRequestDate");

	    if (!trainingTransaction.getStudyEndDate().after(trainingTransaction.getStudyStartDate()))
		throw new BusinessException("error_trainingStudyEndDateBeforeStudyStartDate");

	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode() && trainingTransaction.getStudyMonthsCount() != null && (trainingTransaction.getStudyMonthsCount() <= 0 || trainingTransaction.getStudyMonthsCount() > MAX_MONTHS_PERIOD))
		throw new BusinessException("error_minimumStudyMonthsRange");

	    if (trainingTransaction.getStudyGraduationDate() != null && !trainingTransaction.getStudyGraduationDate().after(trainingTransaction.getStudyStartDate()))
		throw new BusinessException("error_trainingGraduationDateBeforeStudyStartDate");

	    if (trainingTransaction.getStudyGraduationDate() != null && !trainingTransaction.getStudyGraduationDate().before(HijriDateService.getHijriSysDate()))
		throw new BusinessException("error_trainingGraduationDateAfterRequestDate");

	    if (countConflictTrainingTransactions(trainingTransaction.getTrainingTypeId(), trainingTransaction.getEmployeeId(), trainingTransaction.getStudyStartDateString(), trainingTransaction.getStudyEndDateString(), trainingTransaction.getId() == null ? FlagsEnum.ALL.getCode() : trainingTransaction.getId()) > 0)
		throw new BusinessException("error_trainingDatesConflict", new Object[] { trainingTransaction.getEmployeeName() == null ? EmployeesService.getEmployeeData(trainingTransaction.getEmployeeId()).getName() : trainingTransaction.getEmployeeName() });

	    if (trainingTransaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode() && countEmployeeConflictCoursesEventsDates(excludedIds, trainingTransaction.getTrainingTypeId(), trainingTransaction.getEmployeeId(), trainingTransaction.getStudyStartDateString(), trainingTransaction.getStudyEndDateString()) > 0)
		throw new BusinessException("error_trainingDatesConflict", new Object[] { trainingTransaction.getEmployeeName() == null ? EmployeesService.getEmployeeData(trainingTransaction.getEmployeeId()).getName() : trainingTransaction.getEmployeeName() });
	}
    }

    public static void validateCourseEventPrerequistes(EmployeeData employee, TrainingCourseEventData courseEvent) throws BusinessException {

	TrainingCourseData trainingCourse = TrainingCoursesService.getTrainingCoursesById(courseEvent.getCourseId());

	if (trainingCourse.getCategoryId() != null && employee.getCategoryId() != trainingCourse.getCategoryId()) {
	    throw new BusinessException("error_employeeCategoryNotValidForTrnCourse", new Object[] { employee.getName() });
	}

	if (trainingCourse.getFromRankId() != null && trainingCourse.getToRankId() != null && !(employee.getRankId() >= trainingCourse.getToRankId() && employee.getRankId() <= trainingCourse.getFromRankId())) {
	    throw new BusinessException("error_employeeRankNotValidForTrnCourse", new Object[] { employee.getName() });
	}

	if (trainingCourse.getGeneralSpecialization() != null && employee.getGeneralSpecialization().intValue() != trainingCourse.getGeneralSpecialization().intValue()) {
	    throw new BusinessException("error_employeeGeneralSpecNotValidForTrnCourse", new Object[] { employee.getName() });
	}

	if (trainingCourse.getMinorSpecializationIds() != null &&
		!new StringBuilder(trainingCourse.getMinorSpecializationIds())
			.insert(0, ",")
			.append(",")
			.toString().contains(',' + String.valueOf(employee.getMinorSpecId()) + ',')) {
	    throw new BusinessException("error_employeeJobMinorSpecNotValidForTrnCourse", new Object[] { employee.getName() });
	}

	if (trainingCourse.getJobGeneralType() != null && JobsService.getJobById(employee.getJobId()).getGeneralType() != trainingCourse.getJobGeneralType()) {
	    throw new BusinessException("error_employeeJobTypeNotValidForTrnCourse", new Object[] { employee.getName() });
	}

	if (trainingCourse.getQualificationLevelIds() != null &&
		!(new StringBuilder(trainingCourse.getQualificationLevelIds())
			.insert(0, ",")
			.append(",")
			.toString()).contains(',' + String.valueOf(EmployeesService.getEmployeeQualificationsByEmpId(employee.getEmpId()).getCurQualLevelId()) + ',')) {
	    throw new BusinessException("error_employeeQualificationLevelNotValidForTrnCourse", new Object[] { employee.getName() });
	}

	if (trainingCourse.getPromotionWitninMonths() != null) {
	    if (employee.getPromotionDueDate() == null)
		throw new BusinessException("error_cannotDetermineEmpPromotionDueDate", new Object[] { employee.getName() });

	    if (!employee.getPromotionDueDate().before(courseEvent.getActualStartDate()) && HijriDateService.addSubHijriMonthsDays(courseEvent.getActualStartDate(), trainingCourse.getPromotionWitninMonths(), 0).before(employee.getPromotionDueDate())) {
		throw new BusinessException("error_empPromotionDueDateAfterTrainingCoursePrerequiste", new Object[] { employee.getName() });
	    }
	}
    }

    /**
     * Validate update work joining date
     * 
     * @param transaction
     * @throws BusinessException
     */
    private static void validateTrainingTransactionJoiningDate(TrainingTransactionData transaction) throws BusinessException {
	if (transaction.getJoiningDate() == null || !HijriDateService.isValidHijriDate(transaction.getJoiningDate()))
	    throw new BusinessException("error_invalidHijriDate");
	if (transaction.getJoiningDate().after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_workJoiningDateCannotBeAfterSystemDate");
	if (transaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() || transaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {
	    if (transaction.getStatus() == TraineeStatusEnum.FINISHED.getCode() && !transaction.getJoiningDate().after(transaction.getActualEndDate()))
		throw new BusinessException("error_workJoiningDateMustBeAfterCourseEnd");
	    if (transaction.getStatus() == TraineeStatusEnum.CANCELLED.getCode() && transaction.getTrainingJoiningDate() != null && !transaction.getJoiningDate().after(transaction.getActualStartDate()))
		throw new BusinessException("error_workJoiningDateMustBeAfterCourseStart");
	}
	if (transaction.getTrainingTypeId() == TrainingTypesEnum.MORNING_COURSE.getCode() && !transaction.getJoiningDate().after(transaction.getActualEndDate())) {
	    throw new BusinessException("error_workJoiningDateMustBeAfterCourseEnd");
	}
	if (transaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {
	    if (transaction.getSuccessFlag() != null && transaction.getSuccessFlag() == FlagsEnum.ON.getCode() && !transaction.getJoiningDate().after(transaction.getStudyGraduationDate()))
		throw new BusinessException("error_workJoiningDateMustBeAfterScholarshipGraduation");
	    if (transaction.getSuccessFlag() != null && transaction.getSuccessFlag() == FlagsEnum.OFF.getCode() && !transaction.getJoiningDate().after(transaction.getStudyStartDate()))
		throw new BusinessException("error_workJoiningDateMustBeAfterScholarshipStart");
	    if (transaction.getStatus() == TraineeStatusEnum.SCHOLARSHIP_TERMINATION.getCode() && !transaction.getJoiningDate().after(transaction.getStudyEndDate()))
		throw new BusinessException("error_workJoiningDateMustBeAfterScholarshipEnd");
	}
    }

    /**
     * Validate Course joining date
     * 
     * @param transaction
     * @param newStatus
     * @param trainingJoiningDate
     * @throws BusinessException
     */
    private static void validateTrainingTransactionTrainingJoiningDate(TrainingTransactionData transaction, int newStatus, Date trainingJoiningDate) throws BusinessException {
	if (transaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() || transaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {
	    if (transaction.getStatus() != TraineeStatusEnum.NOMINATION_ACCEPTED.getCode() && transaction.getStatus() != TraineeStatusEnum.JOINED.getCode())
		throw new BusinessException("error_employeeStatusInNotSuitable");
	    if (newStatus != TraineeStatusEnum.NOMINATION_ACCEPTED.getCode() && newStatus != TraineeStatusEnum.JOINED.getCode())
		throw new BusinessException("error_transactionDataError");
	    if (newStatus == TraineeStatusEnum.JOINED.getCode() && (trainingJoiningDate == null || !HijriDateService.isValidHijriDate(trainingJoiningDate)))
		throw new BusinessException("error_invalidHijriDate");
	    if (newStatus == TraineeStatusEnum.JOINED.getCode() && (trainingJoiningDate.before(transaction.getActualStartDate()) || trainingJoiningDate.after(transaction.getActualEndDate())))
		throw new BusinessException("error_joiningDateNotBetweenCourseStartAndEndDates");
	} else if (transaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode()) {
	    if (trainingJoiningDate.before(transaction.getStudyStartDate()) || trainingJoiningDate.after(transaction.getStudyEndDate()))
		throw new BusinessException("error_joiningDateNotBetweenStudyStartAndEndDates");
	}
    }

    private static void validateExternalMilitaryTrainingTransactionForAcceptanceRegistration(TrainingTransactionData transaction, int newStatus) throws BusinessException {
	if (transaction.getTrainingTypeId() != TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode())
	    throw new BusinessException("error_transactionDataError");
	if (transaction.getStatus() != TraineeStatusEnum.NOMINATION_ACCEPTED.getCode() && transaction.getStatus() != TraineeStatusEnum.NOMINATION_REJECTED_FROM_PARTY.getCode() && transaction.getStatus() != TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode())
	    throw new BusinessException("error_employeeStatusInNotSuitable");
	if (newStatus != TraineeStatusEnum.NOMINATION_ACCEPTED.getCode() && newStatus != TraineeStatusEnum.NOMINATION_REJECTED_FROM_PARTY.getCode())
	    throw new BusinessException("error_nominationStatusMustBeAcceptedOrRejected");
    }

    private static void validateTraineeStatusForTraineeExtCourseEventCancellation(TrainingTransactionData transaction, Integer reasonType) throws BusinessException {
	if (transaction.getTrainingTypeId() != TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode())
	    throw new BusinessException("error_transactionDataError");
	if (transaction.getStatus() != TraineeStatusEnum.NOMINATION_ACCEPTED.getCode() && transaction.getStatus() != TraineeStatusEnum.JOINED.getCode() && transaction.getStatus() != TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode())
	    throw new BusinessException("error_employeeStatusInNotSuitable");
	if (reasonType == null)
	    throw new BusinessException("error_cancelReasonMandatory");

    }

    /*---------------------------Queries------------------------------*/
    public static boolean courseEventHasJoinedEmployees(long courseEventId, long trainingType) throws BusinessException {
	return searchTrainingTransactionsData(new Long[] { trainingType }, new Integer[] { TraineeStatusEnum.JOINED.getCode() }, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId, FlagsEnum.ALL.getCode(), null, null, null, null, null).size() > 0;
    }

    public static List<TrainingTransactionData> getTrainingTransactionsDataForCourseEventAndEmployeePhysicalUnit(Long[] trainigTypesIds, Integer[] statusIds, long courseEventId, String employeePhysicalUnitFullname) throws BusinessException {
	return searchTrainingTransactionsData(trainigTypesIds, statusIds, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId, FlagsEnum.ALL.getCode(), employeePhysicalUnitFullname, null, null, null, null);
    }

    public static List<TrainingTransactionData> getFinishedMilitaryTransactionsForEmployee(long employeeId) throws BusinessException {
	return searchTrainingTransactionsData(new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() }, new Integer[] { TraineeStatusEnum.FINISHED.getCode() }, null, employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getJoinedOrNominatedTransactionsCoursesForEmployee(long employeeId) throws BusinessException {
	return searchTrainingTransactionsData(new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(), TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() }, null, employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(),
		FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getTrainingTransactionsData(Long[] trainigTypesIds, Integer[] statusIds, long id, long employeeId, long graduationPlaceDetailId, long graduationPlaceId, long qualMajorSpecId, long qualMinorSpecId, long courseEventId) throws BusinessException {
	return searchTrainingTransactionsData(trainigTypesIds, statusIds, id == FlagsEnum.ALL.getCode() ? null : new Long[] { id }, employeeId, FlagsEnum.ALL.getCode(), graduationPlaceDetailId, graduationPlaceId, qualMajorSpecId, qualMinorSpecId, courseEventId, FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getTrainingTransactionsDataByPhysicalRegionId(Long[] trainigTypesIds, Integer[] statusIds, long employeeId, long courseEventId, long physicalRegionId) throws BusinessException {
	return searchTrainingTransactionsData(trainigTypesIds, statusIds, null, employeeId, physicalRegionId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId, FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getTrainingTransactionsDataByGraduationPlaceInfo(long graduationPlaceId, long graduationPlaceDetailId) throws BusinessException {
	return searchTrainingTransactionsData(null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), graduationPlaceDetailId, graduationPlaceId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getTrainingTransactionsDataByQualificationMajorSpecId(long qualMajorSpecId) throws BusinessException {
	return searchTrainingTransactionsData(null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), qualMajorSpecId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getTrainingTransactionsDataByQualificationMinorSpecId(long qualMinorSpecId) throws BusinessException {
	return searchTrainingTransactionsData(null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), qualMinorSpecId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getAcceptedNominatedEmployeesInCourseEventTransactionsList(long courseEventId) throws BusinessException {
	return searchTrainingTransactionsData(new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode() }, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId, FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getJoinedAndAcceptedEmployeesInCourseEventTransactionsList(long courseEventId) throws BusinessException {
	return searchTrainingTransactionsData(new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() }, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId, FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getJoinedAndFininshedEmployeesInCourseEventTransactionsList(long courseEventId) throws BusinessException {
	return searchTrainingTransactionsData(null, new Integer[] { TraineeStatusEnum.JOINED.getCode(), TraineeStatusEnum.FINISHED.getCode() }, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId, FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getTrainingTransactionsDataByIds(Long[] ids) throws BusinessException {
	return searchTrainingTransactionsData(null, null, ids, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getTrainingTransactionsForJoiningRegistration(Long[] trainigTypesIds, Integer[] statusIds, long courseEventId) throws BusinessException {
	return searchTrainingTransactionsData(trainigTypesIds, statusIds, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId, FlagsEnum.ALL.getCode(), null, null, null, null, null);
    }

    public static List<TrainingTransactionData> getEmployeesTrainingTransactions(Long[] trainigTypesIds, Integer[] statusIds, int successFlag, String employeePhysicalUnitFullname, Long[] categoryIds, String trainingCourseName, Date fromDate, Date toDate) throws BusinessException {
	return searchTrainingTransactionsData(trainigTypesIds, statusIds, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), successFlag, employeePhysicalUnitFullname, categoryIds, trainingCourseName, fromDate, toDate);
    }

    private static List<TrainingTransactionData> searchTrainingTransactionsData(Long[] trainigTypesIds, Integer[] statusIds, Long[] ids, long employeeId, long employeephysicalRegionId, long graduationPlaceDetailId, long graduationPlaceId, long qualMajorSpecId, long qualMinorSpecId, long courseEventId, int successFlag, String employeePhysicalUnitFullname, Long[] categoryIds, String trainingCourseName, Date fromDate, Date toDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (ids == null || ids.length == 0) {
		qParams.put("P_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_IDS", ids);
	    }
	    if (trainigTypesIds == null || trainigTypesIds.length == 0) {
		qParams.put("P_TRAINING_TYPE_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRAINING_TYPE_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_TRAINING_TYPE_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRAINING_TYPE_IDS", trainigTypesIds);
	    }
	    if (statusIds == null || statusIds.length == 0) {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUS_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUS_IDS", statusIds);
	    }
	    if (fromDate != null) {
		qParams.put("P_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DATE_FROM", HijriDateService.getHijriDateString(fromDate));
	    } else {
		qParams.put("P_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }
	    if (toDate != null) {
		qParams.put("P_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DATE_TO", HijriDateService.getHijriDateString(toDate));
	    } else {
		qParams.put("P_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    if (categoryIds == null || categoryIds.length == 0) {
		qParams.put("P_CATEGORY_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORY_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_CATEGORY_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORY_IDS", categoryIds);
	    }

	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_EMPLOYEE_PHYSICAL_REGION_ID", employeephysicalRegionId);
	    qParams.put("P_EMP_PHYSC_UNIT_NAME", (employeePhysicalUnitFullname == null || employeePhysicalUnitFullname.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + employeePhysicalUnitFullname + '%');
	    qParams.put("P_GRADUATION_PLACE_DETAIL_ID", graduationPlaceDetailId);
	    qParams.put("P_GRADUATION_PLACE_ID", graduationPlaceId);
	    qParams.put("P_QUAL_MAJOR_SPEC_ID", qualMajorSpecId);
	    qParams.put("P_QUAL_MINOR_SPEC_ID", qualMinorSpecId);
	    qParams.put("P_COURSE_EVENT_ID", courseEventId);
	    qParams.put("P_SUCCESS_FLAG", successFlag);
	    qParams.put("P_TRAINING_COURSE_NAME", (trainingCourseName == null || trainingCourseName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + trainingCourseName + '%');

	    return DataAccess.executeNamedQuery(TrainingTransactionData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_TRANSACTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static long countConflictTrainingTransactions(long trainingTypeId, long employeeId, String studyStartDateString, String studyEndDateString, long excludedTransactionId) throws BusinessException {

	Long[] conflictTrainigTypesIds;
	Integer[] conflictStatusIds;

	if (trainingTypeId == TrainingTypesEnum.SCHOLARSHIP.getCode() || trainingTypeId == TrainingTypesEnum.STUDY_ENROLLMENT.getCode())
	    conflictTrainigTypesIds = new Long[] { TrainingTypesEnum.SCHOLARSHIP.getCode(), TrainingTypesEnum.STUDY_ENROLLMENT.getCode() };
	else
	    conflictTrainigTypesIds = new Long[] { TrainingTypesEnum.SCHOLARSHIP.getCode() };

	conflictStatusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(),
		TraineeStatusEnum.SCHOLARSHIP.getCode(),
		TraineeStatusEnum.SCHOLARSHIP_EXTENSION.getCode(),
		TraineeStatusEnum.SCHOLARSHIP_TERMINATION.getCode() };

	return countEmployeeConflictTrainingTransactionsDates(conflictTrainigTypesIds, conflictStatusIds, employeeId, studyStartDateString, studyEndDateString, excludedTransactionId);

    }

    private static long countEmployeeConflictTrainingTransactionsDates(Long[] trainigTypesIds, Integer[] statusIds, long employeeId, String studyStartDateString, String studyEndDateString, long excludedId) throws BusinessException {
	{
	    try {
		Map<String, Object> qParams = new HashMap<String, Object>();
		if (trainigTypesIds == null || trainigTypesIds.length == 0) {
		    qParams.put("P_TRAINING_TYPE_IDS_FLAG", FlagsEnum.ALL.getCode());
		    qParams.put("P_TRAINING_TYPE_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
		} else {
		    qParams.put("P_TRAINING_TYPE_IDS_FLAG", FlagsEnum.ON.getCode());
		    qParams.put("P_TRAINING_TYPE_IDS", trainigTypesIds);
		}
		if (statusIds == null || statusIds.length == 0) {
		    qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ALL.getCode());
		    qParams.put("P_STATUS_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
		} else {
		    qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ON.getCode());
		    qParams.put("P_STATUS_IDS", statusIds);
		}

		qParams.put("P_EMPLOYEE_ID", employeeId);
		qParams.put("P_STUDY_START_DATE", studyStartDateString);
		qParams.put("P_STUDY_END_DATE", studyEndDateString);
		qParams.put("P_EXCLUDED_ID", excludedId);

		return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_CONFLICT_TRAINING_TRANSACTIONS_DATES.getCode(), qParams).get(0);
	    } catch (DatabaseException e) {
		e.printStackTrace();
		throw new BusinessException("error_general");
	    }
	}
    }

    public static Long countEmployeeConflictCoursesEventsDates(Long[] excludedIds, long trainingTypeId, long employeeId, String actualStartDateString, String actualEndDateString) throws BusinessException {
	Long[] conflictTrainigTypesIds;
	Integer[] conflictStatusIds;

	if (trainingTypeId != TrainingTypesEnum.STUDY_ENROLLMENT.getCode()) {

	    conflictTrainigTypesIds = new Long[] {
		    TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(),
		    TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode(),
		    TrainingTypesEnum.MORNING_COURSE.getCode(),
		    TrainingTypesEnum.EVENING_COURSE.getCode() };
	    conflictStatusIds = new Integer[] {
		    TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(),
		    TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(),
		    TraineeStatusEnum.JOINED.getCode(),
		    TraineeStatusEnum.FINISHED.getCode() };

	    return countEmployeeConflictCoursesEventsDates(excludedIds, conflictTrainigTypesIds, conflictStatusIds, employeeId, actualStartDateString, actualEndDateString);
	} else {
	    return Long.valueOf(0);
	}
    }

    private static Long countEmployeeConflictCoursesEventsDates(Long[] excludedIds, Long[] trainigTypesIds, Integer[] statusIds, long employeeId, String actualStartDate, String actualEndDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    if (excludedIds == null || excludedIds.length == 0) {
		qParams.put("P_EXCLUDED_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EXCLUDED_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_EXCLUDED_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EXCLUDED_IDS", excludedIds);

	    }
	    if (trainigTypesIds == null || trainigTypesIds.length == 0) {
		qParams.put("P_TRAINING_TYPE_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRAINING_TYPE_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_TRAINING_TYPE_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRAINING_TYPE_IDS", trainigTypesIds);

	    }
	    if (statusIds == null || statusIds.length == 0) {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUS_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUS_IDS", statusIds);
	    }

	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_ACTUAL_START_DATE", actualStartDate);
	    qParams.put("P_ACTUAL_END_DATE", actualEndDate);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_CONFLICT_COURSES_EVENTS_DATES.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**************************************************** TrainingTransactionDetail ***************************************/
    /*--------------------------- Operations ------------------------------*/
    public static void addTrainingTransactionDetail(TrainingTransactionDetailData trainingTransactionDetailData, String processName, CustomSession... useSession) throws BusinessException {
	validateTrainingTransactionDetail(trainingTransactionDetailData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    String[] etrCorInfo = null;
	    if (processName == null)
		throw new BusinessException("error_transactionDataError");

	    etrCorInfo = ETRCorrespondence.doETRCorOut(processName, session);

	    if (etrCorInfo == null)
		throw new BusinessException("error_transactionDataError");

	    trainingTransactionDetailData.setDecisionNumber(etrCorInfo[0]);
	    trainingTransactionDetailData.setDecisionDateString(etrCorInfo[1]);

	    DataAccess.addEntity(trainingTransactionDetailData.getTrainingTransactionDetail(), session);
	    trainingTransactionDetailData.setId(trainingTransactionDetailData.getTrainingTransactionDetail().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    trainingTransactionDetailData.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /*--------------------------- validations ------------------------------*/
    public static void validateTrainingTransactionDetail(TrainingTransactionDetailData trainingTransactionDetail) throws BusinessException {
	// Validate on mandatory fields
	if (trainingTransactionDetail.getTransactionTypeId() == null ||
		trainingTransactionDetail.getTransEmpCategoryId() == null ||
		trainingTransactionDetail.getTransEmpJobCode().trim().isEmpty() ||
		trainingTransactionDetail.getTransEmpJobName().trim().isEmpty() ||
		trainingTransactionDetail.getTransEmpRankDesc().trim().isEmpty() ||
		trainingTransactionDetail.getTransEmpUnitFullName().trim().isEmpty())
	    throw new BusinessException("error_transactionDataError");

	if (trainingTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()
		|| trainingTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_EXTENSION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()
		|| trainingTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TERMINATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()
		|| trainingTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_CANCELLATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {

	    if (trainingTransactionDetail.getStudyStartDate() == null || !HijriDateService.isValidHijriDate(trainingTransactionDetail.getStudyStartDate()))
		throw new BusinessException("error_trainingStudyStartDateInvalid");

	    if (trainingTransactionDetail.getStudyEndDate() == null || !HijriDateService.isValidHijriDate(trainingTransactionDetail.getStudyEndDate()))
		throw new BusinessException("error_trainingStudyEndDateInvalid");

	    if (trainingTransactionDetail.getMinistryDecisionNumber() == null || trainingTransactionDetail.getMinistryDecisionNumber().trim().isEmpty())
		throw new BusinessException("error_ministerialDecisionMandatory");

	    if (trainingTransactionDetail.getMinistryReportNumber() == null || trainingTransactionDetail.getMinistryReportNumber().trim().isEmpty())
		throw new BusinessException("error_reportNumberMandatory");

	    if (trainingTransactionDetail.getMinistryDecisionDate() == null || !HijriDateService.isValidHijriDate(trainingTransactionDetail.getMinistryDecisionDate()))
		throw new BusinessException("error_invalidHijriDate");

	    if (trainingTransactionDetail.getMinistryReportDate() == null || !HijriDateService.isValidHijriDate(trainingTransactionDetail.getMinistryReportDate()))
		throw new BusinessException("error_invalidHijriDate");

	    long transactionTypeNewDecisionId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	    long transactionTypeExtensionDecisionId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_EXTENSION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();

	    if ((transactionTypeNewDecisionId == trainingTransactionDetail.getTransactionTypeId() || transactionTypeExtensionDecisionId == trainingTransactionDetail.getTransactionTypeId())
		    && !trainingTransactionDetail.getStudyStartDate().after(trainingTransactionDetail.getMinistryDecisionDate()))
		throw new BusinessException("error_studyStartDateMustBeAfterMinistryDecisionDate");

	    if (transactionTypeExtensionDecisionId == trainingTransactionDetail.getTransactionTypeId() && !trainingTransactionDetail.getStudyStartDate().after(trainingTransactionDetail.getMinistryReportDate()))
		throw new BusinessException("error_studyStartDateMustBeAfterReportDate");

	    if (trainingTransactionDetail.getMinistryDecisionDate().after(trainingTransactionDetail.getMinistryReportDate()))
		throw new BusinessException("error_decisionDateMustBeBeforeOrEqualReportDate");

	    if (trainingTransactionDetail.getTransactionTypeId().longValue() != CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		List<TrainingTransactionDetailData> trainingTransactionsDetails = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionDetail.getTrainingTransactionId());
		if (trainingTransactionsDetails.isEmpty())
		    throw new BusinessException("error_transactionDataError");

		Date lastTrainingTransactionMinistryDecisionDate = trainingTransactionsDetails.get(trainingTransactionsDetails.size() - 1).getMinistryDecisionDate();
		if (!trainingTransactionDetail.getMinistryDecisionDate().after(lastTrainingTransactionMinistryDecisionDate))
		    throw new BusinessException("error_ministryDecisionDateMustBeAfterPreviousDecisions");

		TrainingTransactionData trainingTransaction = TrainingEmployeesService.getTrainingTransactionsDataByIds(new Long[] { trainingTransactionDetail.getTrainingTransactionId() }).get(0);

		if (trainingTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_CANCELLATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		    if (trainingTransaction.getTrainingJoiningDate() != null)
			throw new BusinessException("error_trainingJoiningDateIsRegistered");

		} else {
		    if (trainingTransaction.getTrainingJoiningDate() == null)
			throw new BusinessException("error_trainingJoiningDateIsNotRegistered");

		    if (trainingTransaction.getSuccessFlag() != null)
			throw new BusinessException("error_scholarshipResulsIsRegistered");
		}

		if (trainingTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TERMINATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		    if (!trainingTransactionDetail.getStudyEndDate().after(trainingTransaction.getStudyStartDate()) || !trainingTransactionDetail.getStudyEndDate().before(trainingTransaction.getStudyEndDate()))
			throw new BusinessException("error_scholarshipTerminationDateNotValid");

		    if (!trainingTransactionDetail.getStudyEndDate().before(trainingTransaction.getStudyEndDate()) || !trainingTransactionDetail.getStudyEndDate().after(trainingTransaction.getStudyStartDate()))
			throw new BusinessException("error_invalidScholarShipCancellationDateRange");
		}

		if (trainingTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_EXTENSION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId() || trainingTransactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		    if (trainingTransactionDetail.getStudyDaysCount() != null && trainingTransactionDetail.getStudyDaysCount() > MAX_SCHOLARSHIP_DAYS_COUNT)
			throw new BusinessException("error_invalidScholarshipExtensionDaysRange");
		    if (trainingTransactionDetail.getStudyMonthsCount() != null && trainingTransactionDetail.getStudyMonthsCount() > MAX_MONTHS_PERIOD)
			throw new BusinessException("error_minimumStudyMonthsRange");
		}
	    }
	}

    }

    // ---------------------------------------------------------queries --------------------------------------
    public static List<TrainingTransactionDetailData> getTrainingTransactionDetailDataByTrainingTransactionId(long trainingTransactionId) throws BusinessException {
	return searchTrainingTransactionDetail(null, null, null, trainingTransactionId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null, null);
    }

    public static List<TrainingTransactionDetailData> getTrainingTransactionDetailForScholarshipInquery(Long[] trainigTypesIds, Integer[] traineeStatusIds, long employeeId, long transactionTypeId, String decisionNumber) throws BusinessException {
	return searchTrainingTransactionDetail(transactionTypeId == FlagsEnum.ALL.getCode() ? null : new Long[] { transactionTypeId }, trainigTypesIds, traineeStatusIds, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), employeeId, null, null, null, decisionNumber, null, null);
    }

    public static List<TrainingTransactionDetailData> getTrainingTransactionDetailForScholarshipInqueryByMinistryDecisionNumberAndMinistryDecisonDate(Long[] trainigTypesIds, Integer[] traineeStatusIds, long employeeId, long transactionTypeId, String decisionNumber, String ministryDecisionNumber, Date ministryDecisionDate) throws BusinessException {
	return searchTrainingTransactionDetail(transactionTypeId == FlagsEnum.ALL.getCode() ? null : new Long[] { transactionTypeId }, trainigTypesIds, traineeStatusIds, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), employeeId, null, null, null, decisionNumber, ministryDecisionNumber, ministryDecisionDate);
    }

    public static List<TrainingTransactionDetailData> getTrainingTransactionDetailsForEmployeesDecision(Long[] transactionTypeIds, long trainingUnitId, long employeeId, String employeePhysicalUnitHKey, Date startDateFrom, Date startDateTo) throws BusinessException {
	return searchTrainingTransactionDetail(transactionTypeIds, null, null, FlagsEnum.ALL.getCode(), trainingUnitId, employeeId, employeePhysicalUnitHKey, startDateFrom, startDateTo, null, null, null);
    }

    private static List<TrainingTransactionDetailData> searchTrainingTransactionDetail(Long[] transactionTypeIds, Long[] trainigTypesIds, Integer[] traineeStatusIds, long trainingTransactionId, long trainingUnitId, long employeeId, String employeePhysicalUnitHKey, Date startDateFrom, Date startDateTo, String decisionNumber, String ministryDecisionNumber, Date ministryDecisionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_TRANSACTION_ID", trainingTransactionId);
	    qParams.put("P_DECISION_NUMBER", decisionNumber == null || decisionNumber.trim().length() == 0 ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (trainigTypesIds == null || trainigTypesIds.length == 0) {
		qParams.put("P_TRAINING_TYPE_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRAINING_TYPE_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_TRAINING_TYPE_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRAINING_TYPE_IDS", trainigTypesIds);

	    }
	    if (transactionTypeIds == null || transactionTypeIds.length == 0) {
		qParams.put("P_TRANSACTION_TYPE_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRANSACTION_TYPE_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_TRANSACTION_TYPE_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRANSACTION_TYPE_IDS", transactionTypeIds);
	    }
	    if (traineeStatusIds == null || traineeStatusIds.length == 0) {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUS_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUS_IDS", traineeStatusIds);
	    }
	    if (startDateFrom != null) {
		qParams.put("P_START_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE_FROM", HijriDateService.getHijriDateString(startDateFrom));
	    } else {
		qParams.put("P_START_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }
	    if (startDateTo != null) {
		qParams.put("P_START_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE_TO", HijriDateService.getHijriDateString(startDateTo));
	    } else {
		qParams.put("P_START_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_TRAINING_UNIT_ID", trainingUnitId);

	    qParams.put("P_EMP_PHYSC_UNIT_HKEY", (employeePhysicalUnitHKey == null || employeePhysicalUnitHKey.length() == 0) ? FlagsEnum.ALL.getCode() + "" : UnitsService.getHKeyPrefix(employeePhysicalUnitHKey) + '%');

	    qParams.put("P_MINISTRY_DECISION_NUMBER", ministryDecisionNumber == null || ministryDecisionNumber.trim().length() == 0 ? FlagsEnum.ALL.getCode() + "" : ministryDecisionNumber);

	    if (ministryDecisionDate != null) {
		qParams.put("P_MINISTRY_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_MINISTRY_DECISION_DATE", HijriDateService.getHijriDateString(ministryDecisionDate));
	    } else {
		qParams.put("P_MINISTRY_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_MINISTRY_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    }

	    return DataAccess.executeNamedQuery(TrainingTransactionDetailData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_TRANSACTIONS_DETAIL_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------Reports------------------------------*/
    private static String getTraineeCertificateReportName(long trainingTransactionDetailId) throws BusinessException {
	TrainingCourseData courseData = TrainingCoursesService.getTrainingCourseDataByTransactionDetailId(trainingTransactionDetailId);
	if (courseData.getElectronicCertificateFlag() != null && courseData.getElectronicCertificateFlag().equals(FlagsEnum.ON.getCode())) {
	    TrainingUnitData trainingUnitData = TrainingSetupService.getTrainingUnitDataByTransactionDetailId(trainingTransactionDetailId);
	    if (trainingUnitData.getRegionId() != null && (trainingUnitData.getRegionId().equals(RegionsEnum.ACADEMY.getCode()) || trainingUnitData.getRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())))
		return ReportNamesEnum.TRAINING_DECISION_TRAINEE_ELECTRONIC_CERTIFICATE.getCode();
	    else
		throw new BusinessException("error_cantPrintElectronicGraduationCertificate");
	} else
	    return ReportNamesEnum.TRAINING_DECISION_TRAINEE_CERTIFICATE.getCode();
    }

    public static byte[] getTraineeCertificateBytes(long trainingTransactionDetailId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = getTraineeCertificateReportName(trainingTransactionDetailId);
	    parameters.put("P_TRN_TRANSACTIONS_DTLS_ID", trainingTransactionDetailId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTraineeCourseCancellationDecisionBytes(long trainingTransactionDetailId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_DECISION_TRAINEE_COURSE_CANCELLATION.getCode();
	    parameters.put("P_TRN_TRANSACTIONS_DTLS_ID", trainingTransactionDetailId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getCancelledCourseTraineesBytes(long regionId, long employeeId, Date fromDate, Date toDate) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_CANCELED_TRAINEES_COURSES.getCode();

	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_EMPLOYEE_ID", employeeId);
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_FROM_DATE_FLAG", fromDate != null ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());
	    parameters.put("P_TO_DATE_FLAG", toDate != null ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingTransactionsBytes(long regionId, String regionDesc, long employeeId, String empName, Date fromDate, Date toDate, long courseId, String courseName, long qualMajorSpecId, String qualMajorSpecDesc, long qualMinorSpecId, String qualMinorSpecDesc, long physicalUnitId, String physicalUnitName, String trainingTypesIds, String trainingTypesDesc, int traineeSuccessFlag, int traineeStatus, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_EMPLOYEES_TRANSACTIONS.getCode();

	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_COURSE_NAME", (courseName == null || courseName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : courseName);
	    parameters.put("P_COURSE_ID", courseId);
	    parameters.put("P_PHYSICAL_UNIT_NAME", (physicalUnitName == null || physicalUnitName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : physicalUnitName);
	    parameters.put("P_PHYSICAL_UNIT_ID", physicalUnitId);
	    parameters.put("P_EMP_NAME", (empName == null || empName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : empName);
	    parameters.put("P_EMP_ID", employeeId);
	    parameters.put("P_REGION_DESC", (regionDesc == null || regionDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : regionDesc);
	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_QUAL_MAJOR_SPEC_ID", qualMajorSpecId);
	    parameters.put("P_QUAL_MAJOR_SPEC_DESC", (qualMajorSpecDesc == null || qualMajorSpecDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : qualMajorSpecDesc);
	    parameters.put("P_QUAL_MINOR_SPEC_ID", qualMinorSpecId);
	    parameters.put("P_QUAL_MINOR_SPEC_DESC", (qualMinorSpecDesc == null || qualMinorSpecDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : qualMinorSpecDesc);
	    parameters.put("P_TRAINING_TYPES_IDS", (trainingTypesIds == null || trainingTypesIds.length() == 0) ? FlagsEnum.ALL.getCode() + "" : trainingTypesIds);
	    parameters.put("P_TRAINING_TYPES_DESC", (trainingTypesDesc == null || trainingTypesDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : trainingTypesDesc);
	    parameters.put("P_START_DATE_FLAG", fromDate != null ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());
	    parameters.put("P_TO_DATE_FLAG", toDate != null ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());
	    parameters.put("P_TRAINING_TYPES_FLAG", (trainingTypesIds == null || trainingTypesIds.length() == 0) ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    parameters.put("P_TRAINEE_SUCCESS_FLAG", traineeSuccessFlag);
	    parameters.put("P_TRAINEE_STATUS", traineeStatus);
	    parameters.put("P_CATEGORY_ID", categoryId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getScholarshipDecisionBytes(long trainingTransactionDetailId, long transactionTypeId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName;
	    if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		reportName = ReportNamesEnum.TRAINING_DECISION_SCHOLARSHIP.getCode();
	    else if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_EXTENSION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		reportName = ReportNamesEnum.TRAINING_DECISION_SCHOLARSHIP_EXTENSION.getCode();
	    else if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TERMINATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		reportName = ReportNamesEnum.TRAINING_DECISION_SCHOLARSHIP_TERMINATION.getCode();
	    else
		reportName = ReportNamesEnum.TRAINING_DECISION_SCHOLARSHIP_CANCELLATION.getCode();
	    parameters.put("P_TRN_TRANSACTIONS_DTLS_ID", trainingTransactionDetailId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getJoiningApprovalBytes(long trainingTransactionId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_COURSE_JOINING_APPROVAL_DOCUMENT.getCode();
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_TRN_TRANSACTIONS_ID", trainingTransactionId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingYearDraftsBytes(long trainingYearId, int yearStatus, String excludedAction) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = "";
	    if (yearStatus == TrainingYearStatusEnum.INITIAL_DRAFT.getCode()) {
		reportName = ReportNamesEnum.TRAINING_PLAN_INITIAL_DRAFT_STATISTICS.getCode();
	    } else if (yearStatus == TrainingYearStatusEnum.FINAL_DRAFT.getCode()) {
		reportName = ReportNamesEnum.TRAINING_PLAN_FINAL_DRAFT_STATISTICS.getCode();
	    }
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_EXCLUDED_ACTION", excludedAction);
	    parameters.put("P_TRAINING_YEAR_ID", trainingYearId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingPlanCandidatesEmployeesBytes(long trainingYearId, long trainingUnitId, long courseEventId, long requestingRegionId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_PLAN_CANDIDATE_EMPLOYEES.getCode();
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_TRAINING_YEAR_ID", trainingYearId);
	    parameters.put("P_TRAINING_UNIT_ID", trainingUnitId);
	    parameters.put("P_COURSE_EVENT_ID", courseEventId);
	    parameters.put("P_REQUESTING_REGION_ID", requestingRegionId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingRequestsBytes(long regionId, String regionDesc, long employeeId, String empName, Date fromDate, Date toDate, long courseId, String courseName, long physicalUnitId, String physicalUnitName, String trainingTypesIds, String trainingTypesDesc, int requestStatus, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_EMPLOYEES_REQUESTS.getCode();

	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    parameters.put("P_COURSE_NAME", (courseName == null || courseName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : courseName);
	    parameters.put("P_COURSE_ID", courseId);
	    parameters.put("P_PHYSICAL_UNIT_NAME", (physicalUnitName == null || physicalUnitName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : physicalUnitName);
	    parameters.put("P_PHYSICAL_UNIT_ID", physicalUnitId);
	    parameters.put("P_EMP_NAME", (empName == null || empName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : empName);
	    parameters.put("P_EMP_ID", employeeId);
	    parameters.put("P_REGION_DESC", (regionDesc == null || regionDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : regionDesc);
	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_TRAINING_TYPES_IDS", (trainingTypesIds == null || trainingTypesIds.length() == 0) ? FlagsEnum.ALL.getCode() + "" : trainingTypesIds);
	    parameters.put("P_TRAINING_TYPES_DESC", (trainingTypesDesc == null || trainingTypesDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : trainingTypesDesc);
	    parameters.put("P_START_DATE_FLAG", fromDate != null ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());
	    parameters.put("P_TO_DATE_FLAG", toDate != null ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());
	    parameters.put("P_TRAINING_TYPES_FLAG", (trainingTypesIds == null || trainingTypesIds.length() == 0) ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    parameters.put("P_REQUEST_STATUS", requestStatus);
	    parameters.put("P_CATEGORY_ID", categoryId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

}
