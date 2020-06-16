package com.code.services.hcm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseDecision;
import com.code.enums.FlagsEnum;
import com.code.enums.MilitaryCivillianEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TrainingCourseContentTypeEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

/**
 * Service to handle the training plans and courses transactions.
 */
public class TrainingCoursesService extends BaseService {

    /**
     * private constructor to prevent instantiation
     * 
     */
    private TrainingCoursesService() {
    }

    /***************************** Object *****************************/
    /*---------------------------Operations---------------------------*/
    /*---------------------------Validations--------------------------*/
    /*---------------------------Queries------------------------------*/
    /*---------------------------Reports------------------------------*/

    /********************************************* Training Course ***********************************************/
    /*---------------------------Operations---------------------------*/
    /**
     * Adds a training course to database
     * 
     * @param trainingCourseData
     * @see TrainingCourseData
     */
    public static void addTrainingCourse(TrainingCourseData trainingCourseData, CustomSession... useSession) throws BusinessException {
	validateTrainingCourse(trainingCourseData, null);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(trainingCourseData.getTrainingCourse(), session);
	    trainingCourseData.setId(trainingCourseData.getTrainingCourse().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    trainingCourseData.setId(null);
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
     * Updates a training course in database
     * 
     * @param trainingCourseData
     * @see TrainingCourseData
     */
    public static void updateTrainingCourse(TrainingCourseData trainingCourseData, String originalCourseName, CustomSession... useSession) throws BusinessException {
	validateTrainingCourse(trainingCourseData, originalCourseName);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(trainingCourseData.getTrainingCourse(), session);

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

    private static void updateTrainingCourse(TrainingCourseData trainingCourseData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(trainingCourseData.getTrainingCourse(), session);

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

    public static void deleteTrainingCourse(TrainingCourseData trainingCourseData, CustomSession... useSession) throws BusinessException {
	if (TrainingCoursesEventsService.checkExistingCoursesEventsDataByCourseId(trainingCourseData.getId())) {
	    throw new BusinessException("error_cannotEditOrDeleteTrainingCourse");
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(trainingCourseData.getTrainingCourse(), session);

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

    /*---------------------------Validations--------------------------*/
    /**
     * Checks for business rules in a training course object
     * 
     * @param trainingCourseData
     * @throws BusinessException
     */
    public static void validateTrainingCourse(TrainingCourseData trainingCourseData, String originalCourseName) throws BusinessException {
	if (trainingCourseData == null)
	    throw new BusinessException("error_transactionDataError");

	validateTrainingCourseMandatoryFields(trainingCourseData);
	validateTrainingCourseBusinessRules(trainingCourseData, originalCourseName);
    }

    private static void validateTrainingCourseMandatoryFields(TrainingCourseData trainingCourseData) throws BusinessException {

	// validate common Mandatory Fields
	if (trainingCourseData.getName() == null || trainingCourseData.getName().length() == 0)
	    throw new BusinessException("error_courseNameMandatory");
	if (trainingCourseData.getQualificationMinorSpecId() == null)
	    throw new BusinessException("error_qualificationMinorSpecMandatory");
	if (trainingCourseData.isElectronicCertificateBoolean() && (trainingCourseData.getNameEnglish() == null || trainingCourseData.getNameEnglish().length() == 0))
	    throw new BusinessException("error_courseNameEnglishMandatory");

    }

    private static void validateTrainingCourseBusinessRules(TrainingCourseData trainingCourseData, String originalCourseName) throws BusinessException {

	List<TrainingCourseData> result = getTrainingCoursesByExactNameAndExcludedId(trainingCourseData.getType(), trainingCourseData.getName(), trainingCourseData.getId() == null ? FlagsEnum.ALL.getCode() : trainingCourseData.getId());
	if (result.size() > 0)
	    throw new BusinessException("error_duplicateCourseName");

	if (trainingCourseData.getId() != null && !trainingCourseData.getName().equals(originalCourseName) && TrainingCoursesEventsService.checkExistingCoursesEventsDataByCourseId(trainingCourseData.getId()))
	    throw new BusinessException("error_cannotEditCourseName");

	if (trainingCourseData.getType().equals(MilitaryCivillianEnum.Military.getCode())) {
	    if (trainingCourseData.getCandidatesMin() != null && trainingCourseData.getCandidatesMax() != null && trainingCourseData.getCandidatesMin() > trainingCourseData.getCandidatesMax())
		throw new BusinessException("error_minCandidatesNumGreaterThanMaxCandidatesNum");
	    if (trainingCourseData.getCandidatesMin() != null && (trainingCourseData.getCandidatesMin() <= 0 || trainingCourseData.getCandidatesMin() > 99))
		throw new BusinessException("error_minimumCandidatesNumberRange");
	    if (trainingCourseData.getCandidatesMax() != null && (trainingCourseData.getCandidatesMax() <= 0 || trainingCourseData.getCandidatesMax() > 99))
		throw new BusinessException("error_maximumCandidatesNumberRange");
	    if (trainingCourseData.getPrerquisites() != null && trainingCourseData.getPrerquisites().length() > 500)
		throw new BusinessException("error_coursePrerquisitesLength");
	    if (trainingCourseData.getCategoryId() != null && trainingCourseData.getToRankId() != null && trainingCourseData.getFromRankId() != null && trainingCourseData.getToRankId() > trainingCourseData.getFromRankId())
		throw new BusinessException("error_rankOrderMismatch");
	    if (trainingCourseData.getWeeksCount() != null && (trainingCourseData.getWeeksCount() <= 0 || trainingCourseData.getWeeksCount() > 52))
		throw new BusinessException("error_weeksCountRange");
	    if (trainingCourseData.getPromotionWitninMonths() != null && (trainingCourseData.getPromotionWitninMonths() <= 0 || trainingCourseData.getPromotionWitninMonths() > 12))
		throw new BusinessException("error_promotionWithinMonthesRange");
	}

    }

    /*---------------------------Queries------------------------------*/

    /**
     * Wrapper for {@link #searchTrainingCourses(int, String, long)}, that gets training courses by search parameters
     * 
     * @param type
     *            type of the training course, 1 for civilian, 2 for military
     * @param name
     *            name of the training course
     * @param qualificationMinorSpecId
     *            qualification minor specialization of the training course
     * @return
     * @throws BusinessException
     */
    public static List<TrainingCourseData> getTrainingCoursesByNameAndQualification(int type, String name, String qualificationMinorSpecDesc, long qualificationMinorSpecId, long qualificationMajorSpecId, String qualificationMajorSpecDesc) throws BusinessException {
	return searchTrainingCourses(FlagsEnum.ALL.getCode(), type, name, false, qualificationMinorSpecId, qualificationMinorSpecDesc, qualificationMajorSpecId, qualificationMajorSpecDesc, FlagsEnum.ALL.getCode());
    }

    public static TrainingCourseData getTrainingCoursesById(long id) throws BusinessException {
	List<TrainingCourseData> trainingCourses = searchTrainingCourses(id, FlagsEnum.ALL.getCode(), null, false, FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode());
	return trainingCourses.isEmpty() ? null : trainingCourses.get(0);
    }

    public static List<TrainingCourseData> getTrainingCoursesByExactNameAndExcludedId(int type, String name, long excludedCourseId) throws BusinessException {
	return searchTrainingCourses(FlagsEnum.ALL.getCode(), type, name, true, FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), null, excludedCourseId);
    }

    private static List<TrainingCourseData> searchTrainingCourses(long courseId, int type, String name, boolean exactNameFlag, long qualificationMinorSpecId, String qualificationMinorSpecDesc, long qualificationMajorSpecId, String qualificationMajorSpecDesc, long excludedCourseId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_COURSE_ID", courseId);
	    qParams.put("P_COURSE_TYPE", type);
	    if (exactNameFlag)
		qParams.put("P_COURSE_NAME", (name == null || name.length() == 0) ? FlagsEnum.ALL.getCode() + "" : name);

	    else
		qParams.put("P_COURSE_NAME", (name == null || name.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + name + '%');

	    qParams.put("P_QUAL_MINOR_SPEC_ID", qualificationMinorSpecId);
	    qParams.put("P_QUAL_MINOR_SPEC_DESC", (qualificationMinorSpecDesc == null || qualificationMinorSpecDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + qualificationMinorSpecDesc + '%');
	    qParams.put("P_QUAL_MAJOR_SPEC_ID", qualificationMajorSpecId);
	    qParams.put("P_QUAL_MAJOR_SPEC_DESC", (qualificationMajorSpecDesc == null || qualificationMajorSpecDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + qualificationMajorSpecDesc + '%');
	    qParams.put("P_EXCLUDED_ID", excludedCourseId);
	    List<TrainingCourseData> result = DataAccess.executeNamedQuery(TrainingCourseData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_COURSES.getCode(), qParams);
	    return result;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static TrainingCourseData getTrainingCourseDataByTransactionDetailId(long trainingTransactionDetailId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_TRANSACTION_DETAIL_ID", trainingTransactionDetailId);
	    List<TrainingCourseData> result = DataAccess.executeNamedQuery(TrainingCourseData.class, QueryNamesEnum.HCM_GET_TRAINING_COURSE_BY_TRAINING_TRANSACTION_DETAIL_ID.getCode(), qParams);
	    return result == null || result.size() == 0 ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /********************************************* Training Course Decision ***********************************************/
    /*---------------------------Operations---------------------------*/
    public static void addTrainingCourseDecision(TrainingCourseDecision trainingCourseDecision, String processName, CustomSession... useSession) throws BusinessException {

	validateTrainingCourseDecision(trainingCourseDecision);
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

	    trainingCourseDecision.setDecisionNumber(etrCorInfo[0]);
	    trainingCourseDecision.setDecisionDate(HijriDateService.getHijriDate(etrCorInfo[1]));

	    DataAccess.addEntity(trainingCourseDecision, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    trainingCourseDecision.setId(null);
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");

	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static List<EmployeeData> getTrainingCurriculumCommitteeMembers() throws BusinessException {
	String empIds = ETRConfigurationService.getTrainingCurriculumCommitteeMembers();
	return EmployeesService.getEmployeesByIdsString(empIds);
    }

    public static void handleTrainingCourseDecision(TrainingCourseDecision trainingCourseDecision, String processName, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    TrainingCourseData courseData = getTrainingCoursesById(trainingCourseDecision.getCourseId());

	    if (trainingCourseDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_APPROVE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		courseData.setSyllabusAttachments(trainingCourseDecision.getSyllabusAttachments());
	    } else {
		trainingCourseDecision.setSyllabusAttachments(courseData.getSyllabusAttachments());
		courseData.setSyllabusAttachments(null);
	    }
	    addTrainingCourseDecision(trainingCourseDecision, processName, session);
	    updateTrainingCourse(courseData, session);
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /*---------------------------Validations--------------------------*/
    public static void validateCourseSyllabusAttachment(Long courseId, Integer contentType, long transactionTypeId) throws BusinessException {
	if (courseId == null)
	    throw new BusinessException("error_courseNameMandatory");
	if (contentType == null)
	    throw new BusinessException("error_contentTypeMandatory");
	TrainingCourseData courseData = getTrainingCoursesById(courseId);
	if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId() && courseData.getSyllabusAttachments() == null)
	    throw new BusinessException("error_courseSyllabusAttachmentNull");
    }

    private static void validateTrainingCourseDecision(TrainingCourseDecision trainingCourseDecision) throws BusinessException {

	validateTrainingCourseDecisionMandatoryFields(trainingCourseDecision);
	validateTrainingCourseDecisionBusinessRules(trainingCourseDecision);

    }

    private static void validateTrainingCourseDecisionMandatoryFields(TrainingCourseDecision trainingCourseDecision) throws BusinessException {
	// system error
	if (trainingCourseDecision.getTransactionTypeId() == null || trainingCourseDecision.getDecisionApprovedId() == null ||
		trainingCourseDecision.getOriginalDecisionApprovedId() == null || trainingCourseDecision.getTransactionCourseName() == null) {
	    throw new BusinessException("error_transactionDataError");
	}
	// user doesn't enter mandatory fields
	if (trainingCourseDecision.getCourseId() == null) {
	    throw new BusinessException("error_courseNameMandatory");
	}
	if (trainingCourseDecision.getContentType() == null) {
	    throw new BusinessException("error_contentTypeMandatory");
	}
	if (trainingCourseDecision.getSyllabusAttachments() == null && trainingCourseDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_APPROVE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
	    throw new BusinessException("error_syllabusAttachmentsMandatory");
	}
    }

    private static void validateTrainingCourseDecisionBusinessRules(TrainingCourseDecision trainingCourseDecision) throws BusinessException {
	if (!(trainingCourseDecision.getContentType().equals(TrainingCourseContentTypeEnum.CURRICULUM.getCode()) ||
		trainingCourseDecision.getContentType().equals(TrainingCourseContentTypeEnum.PROGRAM.getCode()) ||
		trainingCourseDecision.getContentType().equals(TrainingCourseContentTypeEnum.SYLLABUS.getCode()))) {
	    throw new BusinessException("error_transactionDataError");
	}
	if (trainingCourseDecision.getSyllabusAttachments() == null && trainingCourseDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
	    throw new BusinessException("error_courseSyllabusAttachmentNull");
	}
    }

    private static void validateTrainingCourseSearch(Long courseId, Date startDate, Date endDate) throws BusinessException {

	if (courseId == null && (startDate == null || endDate == null)) {
	    throw new BusinessException("error_courseStartDateOrEndDateOrCourseIdMandatory");
	} else {
	    if (startDate != null && endDate != null) {
		if (HijriDateService.isValidHijriDate(startDate) && HijriDateService.isValidHijriDate(endDate)) {
		    if (endDate.before(startDate))
			throw new BusinessException("error_courseDecision_startDateBeforeEndDate");
		} else {
		    throw new BusinessException("error_invalidStartorEndDate");
		}
	    }
	}
    }

    /*---------------------------Queries------------------------------*/

    public static List<TrainingCourseDecision> getTrainingCoursesDecisions(Long courseId, Long transactionTypeId, Date startDate, Date endDate, Date decisionDate) throws BusinessException {
	validateTrainingCourseSearch(courseId, startDate, endDate);
	return searchTrainingCoursesDecisions(courseId, transactionTypeId, startDate, endDate, decisionDate);
    }

    private static List<TrainingCourseDecision> searchTrainingCoursesDecisions(Long courseId, Long transactionTypeId, Date startDate, Date endDate, Date decisionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_COURSE_ID", courseId == null ? FlagsEnum.ALL.getCode() : courseId);
	    qParams.put("P_TRANSACTION_TYPE_ID", transactionTypeId == null ? FlagsEnum.ALL.getCode() : transactionTypeId);
	    if (decisionDate != null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    }
	    if (startDate != null) {
		qParams.put("P_START_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE", HijriDateService.getHijriDateString(startDate));
	    } else {
		qParams.put("P_START_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE", HijriDateService.getHijriSysDateString());
	    }

	    if (endDate != null) {
		qParams.put("P_END_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_END_DATE", HijriDateService.getHijriDateString(endDate));
	    } else {
		qParams.put("P_END_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_END_DATE", HijriDateService.getHijriSysDateString());
	    }

	    return DataAccess.executeNamedQuery(TrainingCourseDecision.class, QueryNamesEnum.HCM_GET_TRAINING_COURSES_DECISIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------Reports------------------------------*/

    public static byte[] getTrainingCourseDecisionSyllabusBytes(long courseDecisionId, long transactionTypeId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName;
	    if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_APPROVE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		reportName = ReportNamesEnum.TRAINING_DECISION_COURSE_SYLLABUS_APPROVAL.getCode();
	    else if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		reportName = ReportNamesEnum.TRAINING_DECISION_COURSE_SYLLABUS_CANCELLATION.getCode();
	    else
		throw new BusinessException("error_transactionDataError");

	    parameters.put("P_DECISION_ID", courseDecisionId);

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

}