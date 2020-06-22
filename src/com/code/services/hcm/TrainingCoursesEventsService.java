package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.trainings.TrainingAnnualCourseData;
import com.code.dal.orm.hcm.trainings.TrainingAnnualPartyData;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventAllocationData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventAllocationEmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventDecisionData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventDecisionEmployee;
import com.code.dal.orm.hcm.trainings.TrainingExternalPartyData;
import com.code.dal.orm.hcm.trainings.TrainingTransaction;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionDetailData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.ReportOutputFormatsEnum;
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
import com.code.services.util.HijriDateService;

/**
 * Service to handle the training plans and courses transactions.
 */
public class TrainingCoursesEventsService extends BaseService {
    private final static int WEEKS_COUNT_LIMIT = 52;
    private final static int CANDIDATES_LIMIT = 99;
    private final static int MAX_PREREQUISITES_LENGTH = 500;
    private final static int MAX_NOTES_LENGTH = 500;
    private static final int MAX_REASONS_LENGTH = 100;
    private static final int MAX_ALLOCATION_DESC_LENGTH = 100;
    private final static int MAX_JOB_NUMBER_LENGTH = 10;

    /**
     * private constructor to prevent instantiation
     * 
     */
    private TrainingCoursesEventsService() {
    }

    /***************************** Object *****************************/
    /*---------------------------Operations---------------------------*/
    /*---------------------------Validations--------------------------*/
    /*---------------------------Queries------------------------------*/
    /*---------------------------Reports------------------------------*/

    /************************************************************ Course Event *******************************************************************/
    /*---------------------------Operations---------------------------*/
    public static void addTrainingCourseEvent(TrainingCourseEventData courseEvent, int trainingTransactionCategory, CustomSession... useSession) throws BusinessException {
	validateCourseEvent(courseEvent, trainingTransactionCategory);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(courseEvent.getTrainingCourseEvent(), session);
	    courseEvent.setId(courseEvent.getTrainingCourseEvent().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    courseEvent.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void addTrainingCourseEventForTrainingPlan(TrainingCourseEventData courseEvent, List<TrainingCourseEventData> trainingUnitCourseEvents, List<TrainingCourseEventAllocationData> regionsAllocation, List<TrainingCourseEventAllocationData> externalPartiesAllocations, long loginEmployeeId, CustomSession... useSession) throws BusinessException {
	validateCourseEvent(courseEvent, TrainingTransactionCategoryEnum.TRAINING_PLAN.getCode());

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(courseEvent.getTrainingCourseEvent(), session);
	    courseEvent.setId(courseEvent.getTrainingCourseEvent().getId());
	    /*---------------------------Save Allocations---------------------------*/

	    for (TrainingCourseEventAllocationData allocation : regionsAllocation) {
		allocation.setCourseEventId(courseEvent.getId());
	    }

	    for (TrainingCourseEventAllocationData allocation : externalPartiesAllocations) {
		allocation.setCourseEventId(courseEvent.getId());
	    }

	    maintainTraininCourseEventAllocations(courseEvent, regionsAllocation, externalPartiesAllocations, trainingUnitCourseEvents, true, loginEmployeeId, session);
	    /*---------------------------------------------------------------------*/
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    courseEvent.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateTrainingCourseEvent(TrainingCourseEventData courseEvent, int trainingTransactionCategory, CustomSession... useSession) throws BusinessException {
	validateCourseEvent(courseEvent, trainingTransactionCategory);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(courseEvent.getTrainingCourseEvent(), session);

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

    public static void updateTrainingCourseEvent(TrainingCourseEventData courseEvent, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(courseEvent.getTrainingCourseEvent(), session);

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

    public static void updateTrainingCourseEventForTrainingPlan(TrainingCourseEventData courseEvent, List<TrainingCourseEventData> trainingUnitCourseEvents, List<TrainingCourseEventAllocationData> regionsAllocation, List<TrainingCourseEventAllocationData> externalPartiesAllocations, long loginEmployeeId, CustomSession... useSession) throws BusinessException {
	validateCourseEvent(courseEvent, TrainingTransactionCategoryEnum.TRAINING_PLAN.getCode());

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(courseEvent.getTrainingCourseEvent(), session);
	    /*---------------------------Save Allocations---------------------------*/
	    maintainTraininCourseEventAllocations(courseEvent, regionsAllocation, externalPartiesAllocations, trainingUnitCourseEvents, false, loginEmployeeId, session);
	    /*---------------------------------------------------------------------*/
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

    public static void deleteTrainingCourseEvent(TrainingCourseEventData courseEvent, long loginEmployeeId, CustomSession... useSession) throws BusinessException {
	if (courseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && courseEvent.getTrainingYearStatus().equals(TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode()))
	    throw new BusinessException("error_transactionDataError");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<TrainingCourseEventAllocationData> courseEventAllocations = getTrainingCourseEventAllocations(courseEvent.getId());
	    if (courseEventAllocations.size() > 0) {
		for (TrainingCourseEventAllocationData allocation : courseEventAllocations) {
		    if (allocation.getRegionId() != null && allocation.getAllocationCount() != null) {
			List<TrainingCourseEventAllocationEmployeeData> allocationEmployees = TrainingCoursesEventsService.getTrainingCoursesEventsAllocationEmps(allocation.getId());
			for (TrainingCourseEventAllocationEmployeeData allocationEmployee : allocationEmployees)
			    deleteTrainingCourseEventAllocationEmployee(allocationEmployee, loginEmployeeId, useSession);
		    }
		    allocation.getTrainingCourseEventAllocation().setSystemUser(loginEmployeeId + "");// For auditing
		    deleteTrainingCourseEventAllocation(allocation);
		}
	    }
	    courseEvent.getTrainingCourseEvent().setSystemUser(loginEmployeeId + "");// For auditing
	    DataAccess.deleteEntity(courseEvent.getTrainingCourseEvent(), session);

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

    public static void generateCourseEventSerialNumbers(List<TrainingCourseEventData> courseEvents, CustomSession session) throws BusinessException {
	long courseId = FlagsEnum.ALL.getCode();
	long trainingUnitId = FlagsEnum.ALL.getCode();
	int serialNumber = FlagsEnum.ALL.getCode();

	try {
	    for (int i = 0; i < courseEvents.size(); i++) {
		if (courseEvents.get(i).getTrainingUnitId() != trainingUnitId || courseEvents.get(i).getCourseId() != courseId) {
		    courseId = courseEvents.get(i).getCourseId();
		    trainingUnitId = courseEvents.get(i).getTrainingUnitId();
		    TrainingCourseEventData lastCourseEvent = TrainingCoursesEventsService.getLastGeneratedCourseEvent(trainingUnitId, courseId);
		    serialNumber = lastCourseEvent.getSerial() + 1;
		} else {
		    serialNumber++;
		}

		courseEvents.get(i).setSerial(serialNumber);
		DataAccess.updateEntity(courseEvents.get(i).getTrainingCourseEvent(), session);

	    }
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * For importing annual courses in training plan initiation
     * 
     * @param trainingYearId
     * @param trainingUnitId
     * @return
     * @throws BusinessException
     */
    public static List<TrainingCourseEventData> generateNewCoursesEventsByAnnualCourses(long trainingYearId, long trainingUnitId, long logInEmpId) throws BusinessException {
	List<TrainingCourseEventData> courseEvents = new ArrayList<TrainingCourseEventData>();
	for (TrainingAnnualCourseData annualCourse : TrainingSetupService.getAllAnnualMilitaryCourseDataByTrainingUnitId(trainingUnitId)) {
	    TrainingCourseEventData courseEvent = generateNewCourseEvent(annualCourse.getCourseId(), trainingYearId, trainingUnitId, null, TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), false, logInEmpId);
	    courseEvents.add(courseEvent);
	}
	return courseEvents;
    }

    /**
     * For adding new course event in all training plan screens
     * 
     * @param saveFlag
     * @param generateAllocations
     * @param courseId
     * @param trainingYearId
     * @param trainingUnitId
     * @param externalPartyId
     * @param trainingTypeId
     * @param useSession
     * @return
     * @throws BusinessException
     */
    public static TrainingCourseEventData generateNewCourseEvent(long courseId, Long trainingYearId, Long trainingUnitId, Long externalPartyId, long trainingTypeId, boolean generateAllocations, long logInEmpId, CustomSession... useSession) throws BusinessException {
	TrainingCourseData course = TrainingCoursesService.getTrainingCoursesById(courseId);
	if (course == null)
	    throw new BusinessException("error_transactionDataError");
	TrainingCourseEventData courseEvent = new TrainingCourseEventData();
	courseEvent.setCourseId(course.getId());
	courseEvent.setCourseName(course.getName());
	courseEvent.setWeeksCount(course.getWeeksCount());
	courseEvent.setCandidatesMin(course.getCandidatesMin());
	courseEvent.setCandidatesMax(course.getCandidatesMax());
	courseEvent.setPrerquisites(course.getPrerquisites());
	courseEvent.setRankingFlag(course.getRankingFlag());

	if (trainingTypeId == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
	    if (TrainingCoursesEventsService.checkCourseEventForAutoGenerate(trainingUnitId, courseId))
		courseEvent.setAutoGeneratedSerialFlag(FlagsEnum.ON.getCode());
	    else
		courseEvent.setAutoGeneratedSerialFlag(FlagsEnum.OFF.getCode());
	} else {
	    courseEvent.setAutoGeneratedSerialFlag(FlagsEnum.OFF.getCode());
	}
	courseEvent.setTrainingYearId(trainingYearId);
	courseEvent.setTrainingUnitId(trainingUnitId);
	courseEvent.setExternalPartyId(externalPartyId);
	courseEvent.setTrainingTypeId(trainingTypeId);
	courseEvent.setTrainingYearStatus(TrainingSetupService.getTrainingYearById(trainingYearId).getStatus());

	courseEvent.setStatus(TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode());
	courseEvent.setMigFlag(FlagsEnum.OFF.getCode());
	courseEvent.setEflag(FlagsEnum.ON.getCode());

	courseEvent.getTrainingCourseEvent().setSystemUser(logInEmpId + ""); // For Auditing.
	// Save CourseEvent in DB without validation
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(courseEvent.getTrainingCourseEvent(), session);
	    courseEvent.setId(courseEvent.getTrainingCourseEvent().getId());

	    if (generateAllocations)
		addTrainingCoursesEventsAllocations(Arrays.asList(courseEvent), true, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    courseEvent.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

	return courseEvent;
    }

    public static String manipulateDateNeglectWeekend(String hijriDate) throws BusinessException {
	Date gregDate = HijriDateService.hijriToGregDate(HijriDateService.getHijriDate(hijriDate));

	Calendar cal = Calendar.getInstance();
	cal.setTime(gregDate);
	int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
	if (dayOfWeek == Calendar.FRIDAY) {
	    return HijriDateService.addSubStringHijriDays(hijriDate, -1);
	} else if (dayOfWeek == Calendar.SATURDAY) {
	    return HijriDateService.addSubStringHijriDays(hijriDate, -2);
	}
	return hijriDate;
    }

    public static TrainingCourseEventData constructCivilianCourseEvent(long trainingTypeId, long courseId) throws BusinessException {
	TrainingCourseData course = TrainingCoursesService.getTrainingCoursesById(courseId);

	TrainingCourseEventData courseEvent = new TrainingCourseEventData();
	courseEvent.setTrainingTypeId(trainingTypeId);
	courseEvent.setAutoGeneratedSerialFlag(FlagsEnum.OFF.getCode());
	courseEvent.setRankingFlag(course.getRankingFlag());

	courseEvent.setMigFlag(FlagsEnum.OFF.getCode());
	courseEvent.setEflag(FlagsEnum.OFF.getCode());
	courseEvent.setStatus(TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode());

	courseEvent.setCourseId(course.getId());
	courseEvent.setCourseName(course.getName());

	return courseEvent;
    }
    /*---------------------------Validations--------------------------*/

    public static void validateCourseEventSerialNumbers(List<TrainingCourseEventData> courseEvents) throws BusinessException {
	long courseId = courseEvents.get(0).getCourseId();
	long trainingUnitId = courseEvents.get(0).getTrainingUnitId();
	for (int i = 1; i < courseEvents.size(); i++) {
	    if (courseEvents.get(i).getCourseId() != courseId || courseEvents.get(i).getTrainingUnitId() != trainingUnitId) {
		courseId = courseEvents.get(i).getCourseId();
		trainingUnitId = courseEvents.get(i).getTrainingUnitId();
	    } else {
		if (courseEvents.get(i).getSerial() != courseEvents.get(i - 1).getSerial() + 1) {
		    throw new BusinessException("error_trainingCourseSerialsNotAccumlative", new Object[] { courseEvents.get(i).getCourseName(), courseEvents.get(i).getTrainingUnitName() });
		}
	    }
	}
    }

    public static void validateCourseEvent(TrainingCourseEventData courseEvent, int trainingTransactionCategory) throws BusinessException {
	validateCourseEventMandatoryFields(courseEvent, trainingTransactionCategory);
	validateCourseEventHijriDates(courseEvent);
	validateCourseEventBusinessRules(courseEvent, trainingTransactionCategory);
    }

    private static void validateCourseEventMandatoryFields(TrainingCourseEventData courseEvent, int trainingTransactionCategory) throws BusinessException {
	if (courseEvent == null || courseEvent.getTrainingTypeId() == null || courseEvent.getStatus() == null)
	    throw new BusinessException("error_transactionDataError");

	if (courseEvent.getCourseId() == null)
	    throw new BusinessException("error_courseMandatory");

	if (courseEvent.getPlannedStartDate() == null || courseEvent.getActualStartDate() == null)
	    throw new BusinessException("error_mandatoryCourseStartDate", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	else if (courseEvent.getPlannedEndDate() == null || courseEvent.getActualEndDate() == null)
	    throw new BusinessException("error_mandatoryCourseEndDate", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });

	if (courseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
	    if (courseEvent.getTrainingUnitId() == null)
		throw new BusinessException("error_trainingUnitMandatory");
	    else if (courseEvent.getAutoGeneratedSerialFlag().equals(FlagsEnum.OFF.getCode()) && courseEvent.getSerial() == null)
		throw new BusinessException("error_mandatoryCourseSerial", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.TRAINING_PLAN.getCode()) {
		if (courseEvent.getWeeksCount() == null)
		    throw new BusinessException("error_mandatoryWeeksCount", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
		if (courseEvent.getCandidatesMax() == null)
		    throw new BusinessException("error_mandatoryMaximumCandidatesNumber", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
		if (courseEvent.getCandidatesMin() == null)
		    throw new BusinessException("error_mandatoryMinimumCandidatesNumber", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
		if (courseEvent.getPrerquisites() == null || courseEvent.getPrerquisites().trim().isEmpty())
		    throw new BusinessException("error_mandatoryCoursePrequisities", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	    }
	} else {
	    if (courseEvent.getExternalPartyId() == null)
		throw new BusinessException("error_externalPartyMandatory");
	    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_ADDITION.getCode()) {
		if (courseEvent.getWeeksCount() == null)
		    throw new BusinessException("error_mandatoryWeeksCount", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
		if (courseEvent.getCandidatesMax() == null)
		    throw new BusinessException("error_mandatoryMaximumCandidatesNumber", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
		if (courseEvent.getCandidatesMin() == null)
		    throw new BusinessException("error_mandatoryMinimumCandidatesNumber", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
		if (courseEvent.getPrerquisites() == null || courseEvent.getPrerquisites().trim().isEmpty())
		    throw new BusinessException("error_mandatoryCoursePrequisities", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	    }

	}
    }

    private static void validateCourseEventHijriDates(TrainingCourseEventData courseEvent) throws BusinessException {
	if (!HijriDateService.isValidHijriDate(courseEvent.getPlannedStartDate())) {
	    throw new BusinessException("error_invalidPlannedStartDate", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	}
	if (!HijriDateService.isValidHijriDate(courseEvent.getPlannedEndDate())) {
	    throw new BusinessException("error_invalidPlannedEndDate", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	}
	if (!HijriDateService.isValidHijriDate(courseEvent.getActualStartDate())) {
	    throw new BusinessException("error_invalidActualStartDate", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	}
	if (!HijriDateService.isValidHijriDate(courseEvent.getActualEndDate())) {
	    throw new BusinessException("error_invalidActualEndDate", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	}
    }

    private static void validateCourseEventBusinessRules(TrainingCourseEventData courseEvent, int trainingTransactionCategory) throws BusinessException {
	if (courseEvent.getActualEndDate().before(courseEvent.getActualStartDate()) || courseEvent.getPlannedEndDate().before(courseEvent.getPlannedStartDate()))
	    throw new BusinessException("error_courseEndDateAfterCourseStartDate", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });

	if (courseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() || courseEvent.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {
	    if (courseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && courseEvent.getAutoGeneratedSerialFlag().equals(FlagsEnum.OFF.getCode()) && courseEvent.getSerial().intValue() <= 0) {
		throw new BusinessException("error_courseSerialMustBeGreaterThanZero");
	    }

	    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.TRAINING_PLAN.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_ADDITION.getCode()) {
		if (courseEvent.getCandidatesMin() <= 0 || courseEvent.getCandidatesMin() > CANDIDATES_LIMIT)
		    throw new BusinessException("error_minimumCandidatesNumberRange");
		if (courseEvent.getCandidatesMax() <= 0 || courseEvent.getCandidatesMax() > CANDIDATES_LIMIT)
		    throw new BusinessException("error_maximumCandidatesNumberRange");
		if (courseEvent.getCandidatesMin() > courseEvent.getCandidatesMax())
		    throw new BusinessException("error_minCandidatesNumGreaterThanMaxCandidatesNum");
		if (courseEvent.getPrerquisites().length() > MAX_PREREQUISITES_LENGTH)
		    throw new BusinessException("error_coursePrerquisitesLength");
		if (courseEvent.getWeeksCount() <= 0 || courseEvent.getWeeksCount() > WEEKS_COUNT_LIMIT)
		    throw new BusinessException("error_weeksCountRange");
		int weeksCountCalculated = (int) Math.ceil((HijriDateService.hijriDateDiff(courseEvent.getActualStartDate(), courseEvent.getActualEndDate()) + 1) / 7.0);
		if (weeksCountCalculated != courseEvent.getWeeksCount())
		    throw new BusinessException("error_weeksCountNotEqualDatesDiff", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
		if (trainingTransactionCategory == TrainingTransactionCategoryEnum.TRAINING_PLAN.getCode()) {
		    TrainingYear trainingYear = TrainingSetupService.getTrainingYearById(courseEvent.getTrainingYearId());
		    if (courseEvent.getActualStartDate().before(trainingYear.getStartDate()) || courseEvent.getActualEndDate().after(trainingYear.getEndDate()))
			throw new BusinessException("error_trainingCourseNotInTrainingYearRange", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
		}
	    } else if (trainingTransactionCategory == TrainingTransactionCategoryEnum.COURSE_EVENT_DECISION_REQUEST.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_ADDITION.getCode()) {
		TrainingCourseEventData courseEventToBeChecked = courseEvent.getId() == null ? courseEvent : getCourseEventById(courseEvent.getId());
		if (courseEventToBeChecked.getStatus().intValue() != TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode() && courseEventToBeChecked.getStatus().intValue() != TrainingCourseEventStatusEnum.COURSE_EVENT_POSTPONEMENT_DECISION_ISSUED.getCode())
		    throw new BusinessException("error_trainingCourseStatusNotValid", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	    } else if (trainingTransactionCategory == TrainingTransactionCategoryEnum.COURSE_EVENT_DECISION_MODIFY_REQUEST.getCode()) {
		TrainingCourseEventData courseEventToBeChecked = courseEvent.getId() == null ? courseEvent : getCourseEventById(courseEvent.getId());
		if (courseEventToBeChecked.getStatus().intValue() != TrainingCourseEventStatusEnum.COURSE_EVENT_HOLDING_DECISION_ISSUED.getCode())
		    throw new BusinessException("error_trainingCourseStatusNotValid", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	    } else if (trainingTransactionCategory == TrainingTransactionCategoryEnum.COURSE_EVENT_POSTPONEMENT_DECISION_REQUEST.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.COURSE_EVENT_CANCEL_DECISION_REQUEST.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_POSTPONEMENT.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_CANCELLATION.getCode()) {
		TrainingCourseEventData courseEventToBeChecked = courseEvent.getId() == null ? courseEvent : getCourseEventById(courseEvent.getId());
		if (courseEventToBeChecked.getStatus().intValue() != TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode() && courseEventToBeChecked.getStatus().intValue() != TrainingCourseEventStatusEnum.COURSE_EVENT_HOLDING_DECISION_ISSUED.getCode() && courseEventToBeChecked.getStatus().intValue() != TrainingCourseEventStatusEnum.COURSE_EVENT_POSTPONEMENT_DECISION_ISSUED.getCode())
		    throw new BusinessException("error_trainingCourseStatusNotValid", new String[] { getCourseEventCourseName(courseEvent.getCourseName(), courseEvent.getCourseId()) });
	    }

	    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_POSTPONEMENT.getCode()) {
		// check if the course event new end date after new start date
		if (!courseEvent.getActualEndDate().after(courseEvent.getActualStartDate()))
		    throw new BusinessException("error_courseEventNewEndDateIsBeforeCourseEventNewStartDate");

		// check if the course event new start date is before or the same as the actual date
		if (courseEvent.getActualStartDate().before(courseEvent.getPlannedStartDate()) || courseEvent.getActualStartDate().equals(courseEvent.getPlannedStartDate()))
		    throw new BusinessException("error_courseEventNewStartDateIsBeforeCourseEventOldStartDate");

	    }
	}

	if (courseEvent.getTrainingTypeId() == TrainingTypesEnum.MORNING_COURSE.getCode()) {
	    List<TrainingCourseEventData> courseEvents = TrainingCoursesEventsService.getPreviousCourseEvents(courseEvent.getCourseId(), courseEvent.getTrainingTypeId(), FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode(), courseEvent.getExternalPartyId(), HijriDateService.getHijriDateString(courseEvent.getActualStartDate()), HijriDateService.getHijriDateString(courseEvent.getActualEndDate()), FlagsEnum.OFF.getCode());
	    if (courseEvents.size() > 0 && (courseEvent.getId() == null || !courseEvents.get(0).getId().equals(courseEvent.getId()))) {
		throw new BusinessException("error_courseEventAlreadyExist");
	    }
	}
    }

    private static String getCourseEventCourseName(String courseName, Long courseId) {
	try {
	    if (courseName != null)
		return courseName;

	    return TrainingCoursesService.getTrainingCoursesById(courseId).getName();
	} catch (Exception e) {
	    return "";
	}
    }

    /*---------------------------Queries------------------------------*/
    public static List<TrainingCourseEventData> getMilitaryTrainingCoursesEventsForInquiry(long trainingTypeId, String trainingUnitPartyDesc, String countryName, String courseName, String startDateFrom, String startDateTo) throws BusinessException {
	return searchMilitaryCoursesEventsDataForBrowsing(trainingTypeId, trainingUnitPartyDesc, countryName, courseName, startDateFrom, startDateTo);
    }

    public static List<TrainingCourseEventData> getTrainingCoursesEventsWithStartDateRange(long courseEventId, long trainingYearId, long trainingUnitId, long externalPartyId, long trainingTypeId, String courseName, Integer[] statusesIds, String startDateFrom, String startDateTo, String externalPartyDesc) throws BusinessException {
	return searchCoursesEventsData(courseEventId, FlagsEnum.ALL.getCode(), trainingTypeId, FlagsEnum.ALL.getCode(), trainingUnitId, externalPartyId, externalPartyDesc, null, null, trainingYearId, null, courseName, statusesIds, startDateFrom, startDateTo, null, FlagsEnum.ALL.getCode());
    }

    public static List<TrainingCourseEventData> getTrainingCoursesEvents(long courseEventId, long trainingYearId, long trainingUnitId, long externalPartyId, String externalPartyDesc, String courseName, long trainingTypeId, Integer[] statusesIds, String hijriStartDate, String hijriEndDate, int serial, String hijriEndDateFrom) throws BusinessException {
	return searchCoursesEventsData(courseEventId, FlagsEnum.ALL.getCode(), trainingTypeId, serial, trainingUnitId, externalPartyId, externalPartyDesc, hijriStartDate, hijriEndDate, trainingYearId, null, courseName, statusesIds, null, null, hijriEndDateFrom, FlagsEnum.ALL.getCode());
    }

    public static boolean checkCourseEventForAutoGenerate(long trainingUnitId, long courseId) throws BusinessException {
	return searchCoursesEventsData(FlagsEnum.ALL.getCode(), courseId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), trainingUnitId, FlagsEnum.ALL.getCode(), null, null, null, FlagsEnum.ALL.getCode(), new Integer[] { TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode() }, null, null, null, null, null, FlagsEnum.ALL.getCode()).size() > 0;
    }

    public static TrainingCourseEventData getLastGeneratedCourseEvent(long trainingUnitId, long courseId) throws BusinessException {
	List<TrainingCourseEventData> l = searchCoursesEventsData(FlagsEnum.ALL.getCode(), courseId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), trainingUnitId, FlagsEnum.ALL.getCode(), null, null, null, FlagsEnum.ALL.getCode(), new Integer[] { TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode() }, null, null, null, null, null, FlagsEnum.ALL.getCode());
	return l.get(l.size() - 1);
    }

    public static List<TrainingCourseEventData> getCoursesEventsByTrainingYearId(long trainingYearId) throws BusinessException {
	return searchCoursesEventsData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, trainingYearId, null, null, null, null, null, null, FlagsEnum.ALL.getCode());
    }

    public static List<TrainingCourseEventData> getCoursesEventsByTrainingYearIdAndTrainingUnitId(long trainingYearId, long trainingUnitId) throws BusinessException {
	return searchCoursesEventsData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), trainingUnitId, FlagsEnum.ALL.getCode(), null, null, null, trainingYearId, null, null, null, null, null, null, FlagsEnum.ALL.getCode());
    }

    public static boolean checkExistingCoursesEventsDataByCourseId(long courseId) throws BusinessException {
	return searchCoursesEventsData(FlagsEnum.ALL.getCode(), courseId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, FlagsEnum.ALL.getCode(), null, null, null, null, null, null, FlagsEnum.ALL.getCode()).size() > 0;
    }

    public static boolean checkExistingCoursesEventsDataByExteranlPartyId(long externalPartyId) throws BusinessException {
	return searchCoursesEventsData(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), externalPartyId, null, null, null, FlagsEnum.ALL.getCode(), null, null, null, null, null, null, FlagsEnum.ALL.getCode()).size() > 0;
    }

    public static List<TrainingCourseEventData> getPreviousCourseEvents(long courseId, long trainingTypeId, Integer serial, Long trainingUnitId, Long externalPartyId, String startDate, String endDate, int eFlag) throws BusinessException {
	return searchCoursesEventsData(FlagsEnum.ALL.getCode(), courseId, trainingTypeId, serial == null ? FlagsEnum.ALL.getCode() : serial, trainingUnitId == null ? FlagsEnum.ALL.getCode() : trainingUnitId, externalPartyId == null ? FlagsEnum.ALL.getCode() : externalPartyId, null, startDate, endDate, FlagsEnum.ALL.getCode(), null, null, null, null, null, null, eFlag);
    }

    public static TrainingCourseEventData getCourseEventById(long id) throws BusinessException {
	List<TrainingCourseEventData> courseEventsList = searchCoursesEventsData(id, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, FlagsEnum.ALL.getCode(), null, null, null, null, null, null, FlagsEnum.ALL.getCode());
	return courseEventsList.isEmpty() ? null : courseEventsList.get(0);
    }

    public static boolean checkExistingCourseEventById(long id) throws BusinessException {
	return searchCoursesEventsData(id, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, FlagsEnum.ALL.getCode(), null, null, null, null, null, null, FlagsEnum.ALL.getCode()).size() > 0;
    }

    private static List<TrainingCourseEventData> searchCoursesEventsData(long id, long courseId, long trainingTypeId, int serial, long trainingUnitId, long externalPartyId, String externalPartyDesc, String startDate, String endDate, long trainingYearId, Integer[] trainingYearStatusIds, String courseName, Integer[] statusesIds, String startDateFrom, String startDateTo, String endDateFrom, int eFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);
	    qParams.put("P_COURSE_ID", courseId);
	    qParams.put("P_TRAINING_TYPE_ID", trainingTypeId);
	    qParams.put("P_TRAINING_UNIT_ID", trainingUnitId);
	    qParams.put("P_TRAINING_EXTERNAL_PARTY_ID", externalPartyId);
	    qParams.put("P_TRAINING_EXTERNAL_PARTY_DESC", (externalPartyDesc == null || externalPartyDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + externalPartyDesc + '%');
	    if (startDate != null) {
		qParams.put("P_START_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE", startDate);
	    } else {
		qParams.put("P_START_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE", HijriDateService.getHijriSysDateString());
	    }

	    if (startDateFrom != null) {
		qParams.put("P_START_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE_FROM", startDateFrom);
	    } else {
		qParams.put("P_START_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }
	    if (startDateTo != null) {
		qParams.put("P_START_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE_TO", startDateTo);
	    } else {
		qParams.put("P_START_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    if (endDate != null) {
		qParams.put("P_END_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_END_DATE", endDate);
	    } else {
		qParams.put("P_END_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_END_DATE", HijriDateService.getHijriSysDateString());
	    }

	    if (endDateFrom != null) {
		qParams.put("P_END_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_END_DATE_FROM", endDateFrom);

	    } else {
		qParams.put("P_END_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_END_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    qParams.put("P_SERIAL", serial);
	    qParams.put("P_TRAINING_YEAR_ID", trainingYearId);

	    if (statusesIds != null && statusesIds.length > 0) {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUSES_IDS", statusesIds);
	    } else {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUSES_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
	    }

	    if (trainingYearStatusIds != null && trainingYearStatusIds.length > 0) {
		qParams.put("P_TRAINING_YEAR_STATUS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRAINING_YEAR_STATUS_IDS", trainingYearStatusIds);
	    } else {
		qParams.put("P_TRAINING_YEAR_STATUS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRAINING_YEAR_STATUS_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_COURSE_NAME", (courseName == null || courseName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + courseName + '%');
	    qParams.put("P_E_FLAG", eFlag);
	    return DataAccess.executeNamedQuery(TrainingCourseEventData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_COURSES_EVENTS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<TrainingCourseEventData> searchMilitaryCoursesEventsDataForBrowsing(long trainingTypeId, String trainingUnityPartyDesc, String countryName, String courseName, String startDateFrom, String startDateTo) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_TYPE_ID", trainingTypeId);
	    qParams.put("P_TRAINING_UNIT_PARTY_DESC", (trainingUnityPartyDesc == null || trainingUnityPartyDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + trainingUnityPartyDesc + '%');

	    if (startDateFrom != null) {
		qParams.put("P_START_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE_FROM", startDateFrom);
	    } else {
		qParams.put("P_START_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }
	    if (startDateTo != null) {
		qParams.put("P_START_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE_TO", startDateTo);
	    } else {
		qParams.put("P_START_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    qParams.put("P_COUNTRY_NAME", (countryName == null || countryName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + countryName + '%');
	    qParams.put("P_COURSE_NAME", (courseName == null || courseName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : '%' + courseName + '%');
	    return DataAccess.executeNamedQuery(TrainingCourseEventData.class, QueryNamesEnum.HCM_SEARCH_MILITARY_TRAINING_COURSES_EVENTS_FOR_BROWSING.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static boolean checkExistingTrainingCoursesEventsNotInTrainingYearInterval(long trainingYearId, String startDateString, String endDateString) throws BusinessException {
	return countTrainingCoursesEventsNotInTrainingYearInterval(trainingYearId, startDateString, endDateString) > 0;
    }

    private static Long countTrainingCoursesEventsNotInTrainingYearInterval(long trainingYearId, String startDateString, String endDateString) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_TRAINING_YEAR_ID", trainingYearId);
	    qParams.put("P_YEAR_START_DATE", startDateString);
	    qParams.put("P_YEAR_END_DATE", endDateString);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_GET_TRAINING_COURSES_EVENTS_NOT_IN_TRAINING_YEAR_INTERVAL.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<TrainingCourseEventData> getCoursesEventsDataForSerialGeneration(long trainingUnitId, long trainingYearId) throws BusinessException {
	return searchCoursesEventsDataForSerialGeneration(trainingUnitId, trainingYearId);
    }

    private static List<TrainingCourseEventData> searchCoursesEventsDataForSerialGeneration(long trainingUnitId, long trainingYearId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_UNIT_ID", trainingUnitId);
	    qParams.put("P_TRAINING_YEAR_ID", trainingYearId);

	    return DataAccess.executeNamedQuery(TrainingCourseEventData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_COURSES_EVENTS_FOR_SERIAL_GENERATION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<TrainingCourseEventData> getCoursesEventsDataForNomination(long employeeId, long trainingTypeId, Integer[] empStatusIds, long trainingUnitRegionId) throws BusinessException {
	return searchCoursesEventsDataForNomination(employeeId, trainingTypeId, empStatusIds, trainingUnitRegionId);
    }

    private static List<TrainingCourseEventData> searchCoursesEventsDataForNomination(long employeeId, long trainingTypeId, Integer[] empStatusesIds, long trainingUnitRegionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_TRAINING_TYPE_ID", trainingTypeId);
	    qParams.put("P_TRAINING_UNIT_REGION_ID", trainingUnitRegionId);

	    if (empStatusesIds == null || empStatusesIds.length == 0) {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUSES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUSES_IDS", empStatusesIds);

	    }

	    return DataAccess.executeNamedQuery(TrainingCourseEventData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_COURSES_EVENTS_FOR_NOMINATION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static String calculateTrainingPlanApprovalErrors(long trainingYearId) throws BusinessException {
	StringBuilder courseEventsErrors = new StringBuilder();
	StringBuilder extPartyErrors = new StringBuilder();
	String spaces = "      ";
	// 0 course name
	// 1 training unit
	List<Object> courseEvents = getCoursesEventsNotHavingAllocation(trainingYearId);

	if (courseEvents.size() > 0) {
	    courseEventsErrors.append(getMessage("error_courseEventInTrainingUnitNotHavingAllocationsPrefix"));
	    courseEventsErrors.append(System.lineSeparator());
	    for (int i = 0; i < courseEvents.size(); i++) {
		Object[] row = (Object[]) courseEvents.get(i);
		courseEventsErrors.append(spaces + "- " + getParameterizedMessage("error_courseEventInTrainingUnitNotHavingAllocations", new Object[] { row[0], row[1] }));
		courseEventsErrors.append(System.lineSeparator());
	    }
	    courseEventsErrors.append(System.lineSeparator());
	}

	// sum(a.allocationCount) null
	// 1 externalPartyDesc,
	// 2 trainingUnitName
	// error_externalPartyNotHavingAllocations
	List<Object> externalParties = getExternalPartiessNotHavingAllocation(trainingYearId);
	boolean externalParyErrorsFlag = false;
	if (externalParties.size() > 0) {
	    extPartyErrors.append(getMessage("error_externalPartyNotHavingAllocationsPrefix"));
	    extPartyErrors.append(System.lineSeparator());
	    for (int i = 0; i < externalParties.size(); i++) {
		Object[] row = (Object[]) externalParties.get(i);
		if (row[0] == null) {
		    extPartyErrors.append(spaces + "- " + getParameterizedMessage("error_externalPartyNotHavingAllocations", new Object[] { row[1], row[2] }));
		    extPartyErrors.append(System.lineSeparator());
		    externalParyErrorsFlag = true;
		}
	    }

	    if (!externalParyErrorsFlag)
		extPartyErrors = new StringBuilder();
	}

	return courseEventsErrors.toString() + extPartyErrors.toString();
    }

    private static List<Object> getCoursesEventsNotHavingAllocation(long trainingYearId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_YEAR_ID", trainingYearId);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.HCM_GET_COURSE_EVENTS_DATA_NOT_HAVING_ALLOCATION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<Object> getExternalPartiessNotHavingAllocation(long trainingYearId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_YEAR_ID", trainingYearId);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.HCM_GET_EXTERNAL_PARTIES_NOT_HAVING_ALLOCATION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------Reports---------------------------*/
    public static byte[] getTrainingCourseEventPlanBasedBytes(long courseEventId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_COURSE_DATA_PLAN_BASED.getCode();
	    parameters.put("P_TRN_COURSE_EVENT_ID", courseEventId);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingCourseEventJoiningBytes(long courseEventId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_COURSE_JOINING.getCode();
	    parameters.put("P_TRN_COURSE_EVENT_ID", courseEventId);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingCourseEventResultsBytes(long courseEventId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_COURSE_RESULTS.getCode();
	    parameters.put("P_TRN_COURSE_EVENT_ID", courseEventId);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingCourseEventCandidateEmployeesStatistics(long trainingYearId, long trainingUnitId, long courseId, String courseName, Date startDateFrom, Date startDateTo, String courseEventStatusesIds, String courseEventStatusesDesc) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = ReportNamesEnum.TRAINING_COURSE_EVENT_CANDIDATE_EMPLOYEES_STATISTICS.getCode();
	    parameters.put("P_TRAINING_YEAR_ID", trainingYearId);
	    parameters.put("P_TRAINING_UNIT_ID", trainingUnitId);
	    parameters.put("P_COURSE_ID", courseId);
	    parameters.put("P_COURSE_NAME", (courseName == null || courseName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : courseName);
	    parameters.put("P_FROM_DATE", startDateFrom == null ? HijriDateService.getHijriSysDateString() : HijriDateService.getHijriDateString(startDateFrom));
	    parameters.put("P_TO_DATE", startDateTo == null ? HijriDateService.getHijriSysDateString() : HijriDateService.getHijriDateString(startDateTo));
	    parameters.put("P_FROM_DATE_FLAG", startDateFrom != null ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());
	    parameters.put("P_TO_DATE_FLAG", startDateTo != null ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());
	    parameters.put("P_STATUS_IDS", (courseEventStatusesIds == null || courseEventStatusesIds.length() == 0) ? FlagsEnum.ALL.getCode() + "" : courseEventStatusesIds);
	    parameters.put("P_STATUS_DESCS", (courseEventStatusesDesc == null || courseEventStatusesDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : courseEventStatusesDesc);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /********************************************** training courses events allocations **********************************************/
    /*---------------------------Operations---------------------------*/
    /**
     * Used To add allocations to All Courses Events In Initial Draft plan
     * 
     * @param coursesEvents
     * @param session
     * @throws BusinessException
     */
    public static void addTrainingCoursesEventsAllocations(List<TrainingCourseEventData> coursesEvents, boolean copyFromExistCourseEvent, CustomSession... session) throws BusinessException {
	for (TrainingCourseEventData courseEvent : coursesEvents) {
	    for (TrainingCourseEventAllocationData allocation : constructExternalPartiesCourseAllocations(courseEvent.getTrainingYearId(), courseEvent.getTrainingUnitId(), courseEvent.getId(), copyFromExistCourseEvent)) {
		TrainingCoursesEventsService.addTrainingCourseEventAllocation(allocation, session);
	    }

	    for (TrainingCourseEventAllocationData allocation : constructRegionsCourseAllocations(courseEvent.getId())) {
		TrainingCoursesEventsService.addTrainingCourseEventAllocation(allocation, session);
	    }
	}

    }

    public static List<TrainingCourseEventAllocationData> constructExternalPartiesCourseAllocations(long trainingYearId, long trainingUnitId, Long courseEventId, boolean copyFromExistCourseEvent) throws BusinessException {
	// Get an existing course event in the same trainingUnit to get all external parties that he added
	List<TrainingCourseEventData> courseEvents = getCoursesEventsByTrainingYearIdAndTrainingUnitId(trainingYearId, trainingUnitId);
	Long existingCourseEventId = null;
	if (courseEvents.size() != 0)
	    existingCourseEventId = courseEvents.get(0).getId();

	List<TrainingCourseEventAllocationData> listTrainingCourseEventAllocationData = new ArrayList<TrainingCourseEventAllocationData>();

	TrainingCourseEventAllocationData allocation;
	// if there is no existing course event we get externalparties from the annual ones
	if (!copyFromExistCourseEvent || existingCourseEventId == null) {
	    List<TrainingAnnualPartyData> annualExternalParties = TrainingSetupService.getAnnualPartiesDataByTrainingUnitId(trainingUnitId);
	    for (TrainingAnnualPartyData externalParty : annualExternalParties) {
		allocation = new TrainingCourseEventAllocationData();
		allocation.setExternalPartyId(externalParty.getExternalPartyId());
		allocation.setExternalPartyDesc(externalParty.getExternalPartyDesc());
		allocation.setCourseEventId(courseEventId);
		listTrainingCourseEventAllocationData.add(allocation);
	    }
	} else {// else we get all external parties from the allocations of the existing course event
	    List<TrainingCourseEventAllocationData> allocations = getTrainingCourseEventAllocations(existingCourseEventId);

	    for (TrainingCourseEventAllocationData obj : allocations)
		if (obj.getExternalPartyId() != null) {
		    allocation = new TrainingCourseEventAllocationData();
		    allocation.setExternalPartyId(obj.getExternalPartyId());
		    allocation.setExternalPartyDesc(obj.getExternalPartyDesc());
		    allocation.setCourseEventId(courseEventId);
		    listTrainingCourseEventAllocationData.add(allocation);
		}
	}

	// get external parties from other course events in the training unit
	return listTrainingCourseEventAllocationData;
    }

    public static List<TrainingCourseEventAllocationData> constructRegionsCourseAllocations(Long courseEventId) throws BusinessException {
	List<TrainingCourseEventAllocationData> listTrainingCourseEventAllocationData = new ArrayList<TrainingCourseEventAllocationData>();
	List<Region> regions = CommonService.getAllRegions();
	TrainingCourseEventAllocationData allocation;

	for (Region currentRegion : regions) {
	    allocation = new TrainingCourseEventAllocationData();
	    allocation.setCourseEventId(courseEventId);
	    allocation.setRegionId(currentRegion.getId());
	    allocation.setAllocationCountFlag(true); // for default value in ui
	    allocation.setRegionShortDesc(currentRegion.getDescription());

	    listTrainingCourseEventAllocationData.add(allocation);
	}
	return listTrainingCourseEventAllocationData;
    }

    public static TrainingCourseEventAllocationData constructExternalPartyCourseAllocation(long externalPartyId, long courseEventId) throws BusinessException {
	TrainingExternalPartyData externalParty = TrainingSetupService.getTrainingExternalPartyById(externalPartyId);
	TrainingCourseEventAllocationData allocation = new TrainingCourseEventAllocationData();
	allocation.setExternalPartyId(externalParty.getId());
	allocation.setExternalPartyDesc(externalParty.getDescription());
	allocation.setCourseEventId(courseEventId);
	return allocation;
    }

    public static List<TrainingCourseEventAllocationData> addExternalPartiesAllocationsForCourseEvents(TrainingCourseEventAllocationData externalPartyAllocation, List<TrainingCourseEventData> courseEvents, long empId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	List<TrainingCourseEventAllocationData> trainingCourseEventsAllocationsData = new ArrayList<TrainingCourseEventAllocationData>();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    for (TrainingCourseEventData courseEvent : courseEvents) {
		TrainingCourseEventAllocationData allocation = null;
		if (externalPartyAllocation.getCourseEventId().equals(courseEvent.getId()))
		    allocation = externalPartyAllocation;
		else
		    allocation = constructExternalPartyCourseAllocation(externalPartyAllocation.getExternalPartyId(), courseEvent.getId());
		allocation.getTrainingCourseEventAllocation().setSystemUser(empId + ""); // For Auditing.
		addTrainingCourseEventAllocation(allocation, true, session);
		trainingCourseEventsAllocationsData.add(allocation);
	    }
	    if (!isOpenedSession)
		session.commitTransaction();
	    return trainingCourseEventsAllocationsData;
	} catch (BusinessException e) {
	    e.printStackTrace();
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
	return null;
    }

    public static void maintainTraininCourseEventAllocations(TrainingCourseEventData courseEvent, List<TrainingCourseEventAllocationData> regionsAllocations, List<TrainingCourseEventAllocationData> externalPartiesAllocations, List<TrainingCourseEventData> trainingUnitCourseEvents, boolean newCourseEvent, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (regionsAllocations != null) {
		for (TrainingCourseEventAllocationData regionAllocation : regionsAllocations) {
		    if (regionAllocation.getAllocationCountFlag()) {
			regionAllocation.setAllocationDesc(null);
			if (newCourseEvent) {
			    for (TrainingCourseEventAllocationEmployeeData allocationEmployee : regionAllocation.getAllocationEmployees()) {
				validateTrainingCourseEventAllocationEmployeesBusinessRules(allocationEmployee.getEmpId(), courseEvent);
				addTrainingCourseEventAllocationEmployee(allocationEmployee, loginEmpId, session);

			    }
			    if (courseEvent.getEflag().longValue() == FlagsEnum.ON.getCode())
				regionAllocation.setAllocationCount(regionAllocation.getAllocationEmployees().size() == 0 ? null : regionAllocation.getAllocationEmployees().size());
			    addTrainingCourseEventAllocation(regionAllocation, session);
			} else {
			    List<TrainingCourseEventAllocationEmployeeData> databaseAllocationEmployees = TrainingCoursesEventsService.getTrainingCoursesEventsAllocationEmps(regionAllocation.getId());
			    List<TrainingCourseEventAllocationEmployeeData> allocationEmployees = regionAllocation.getAllocationEmployees();

			    List<TrainingCourseEventAllocationEmployeeData> listToAdd = new ArrayList<>();
			    List<TrainingCourseEventAllocationEmployeeData> listToDelete = new ArrayList<>();
			    List<TrainingCourseEventAllocationEmployeeData> listToKeep = new ArrayList<>();
			    Set<Long> allocationEmployeesIds = new HashSet<>();
			    for (TrainingCourseEventAllocationEmployeeData allocationEmployee : allocationEmployees) {
				if (allocationEmployee.getId() == null)
				    listToAdd.add(allocationEmployee);
				else
				    allocationEmployeesIds.add(allocationEmployee.getId());
			    }

			    for (TrainingCourseEventAllocationEmployeeData databaseAllocationEmployee : databaseAllocationEmployees) {
				if (!allocationEmployeesIds.contains(databaseAllocationEmployee.getId())) {
				    listToDelete.add(databaseAllocationEmployee);
				} else {
				    listToKeep.add(databaseAllocationEmployee);
				}
			    }

			    for (TrainingCourseEventAllocationEmployeeData allocationEmployee : listToDelete) {
				deleteTrainingCourseEventAllocationEmployee(allocationEmployee, loginEmpId, session);
			    }

			    for (TrainingCourseEventAllocationEmployeeData allocationEmployee : listToAdd) {
				validateTrainingCourseEventAllocationEmployeesBusinessRules(allocationEmployee.getEmpId(), courseEvent);
				addTrainingCourseEventAllocationEmployee(allocationEmployee, loginEmpId, session);

			    }
			    for (TrainingCourseEventAllocationEmployeeData allocationEmployee : listToKeep) {
				validateTrainingCourseEventAllocationEmployeesBusinessRules(allocationEmployee.getEmpId(), courseEvent);
			    }

			    if (courseEvent.getEflag().longValue() == FlagsEnum.ON.getCode())
				regionAllocation.setAllocationCount(regionAllocation.getAllocationEmployees().size() == 0 ? null : regionAllocation.getAllocationEmployees().size());
			    updateTrainingCourseEventAllocation(regionAllocation, session);
			}
		    } else {
			regionAllocation.setAllocationCount(null);
			if (newCourseEvent) {
			    addTrainingCourseEventAllocation(regionAllocation, session);
			} else {
			    List<TrainingCourseEventAllocationEmployeeData> databaseAllocationEmployees = TrainingCoursesEventsService.getTrainingCoursesEventsAllocationEmps(regionAllocation.getId());
			    regionAllocation.setAllocationEmployees(new ArrayList<>());
			    for (TrainingCourseEventAllocationEmployeeData allocationEmployee : databaseAllocationEmployees) {
				deleteTrainingCourseEventAllocationEmployee(allocationEmployee, loginEmpId, session);
			    }
			    updateTrainingCourseEventAllocation(regionAllocation, session);
			}
		    }
		}
	    }

	    if (externalPartiesAllocations != null) {
		if (newCourseEvent) {
		    for (TrainingCourseEventAllocationData externalPartyAllocation : externalPartiesAllocations) {
			TrainingCoursesEventsService.addTrainingCourseEventAllocation(externalPartyAllocation, session);
		    }
		} else {
		    List<TrainingCourseEventAllocationData> databaseExternalPartiesAllocations = TrainingCoursesEventsService.getTrainingCoursesEventsExternalPartiesAllocations(regionsAllocations.get(0).getCourseEventId());

		    List<TrainingCourseEventAllocationData> listToAdd = new ArrayList<>();
		    List<TrainingCourseEventAllocationData> listToUpdate = new ArrayList<>();
		    List<TrainingCourseEventAllocationData> listToDelete = new ArrayList<>();

		    Set<Long> externalPartyAllocationsIds = new HashSet<>();
		    for (TrainingCourseEventAllocationData externalPartyAllocation : externalPartiesAllocations) {
			if (externalPartyAllocation.getId() == null)
			    listToAdd.add(externalPartyAllocation);
			else {
			    externalPartyAllocationsIds.add(externalPartyAllocation.getId());
			    listToUpdate.add(externalPartyAllocation);
			}
		    }

		    for (TrainingCourseEventAllocationData databaseExternalPartyAllocation : databaseExternalPartiesAllocations) {
			if (!externalPartyAllocationsIds.contains(databaseExternalPartyAllocation.getId())) {
			    listToDelete.add(databaseExternalPartyAllocation);
			}
		    }

		    for (TrainingCourseEventAllocationData externalPartyAllocation : listToDelete) {
			deleteExternalPartiesAllocationsForCourseEvents(externalPartyAllocation.getExternalPartyId(), trainingUnitCourseEvents, loginEmpId, session);
		    }

		    for (TrainingCourseEventAllocationData externalPartyAllocation : listToUpdate) {
			updateTrainingCourseEventAllocation(externalPartyAllocation, session);
		    }

		    for (TrainingCourseEventAllocationData externalPartyAllocation : listToAdd) {
			addExternalPartiesAllocationsForCourseEvents(externalPartyAllocation, trainingUnitCourseEvents, loginEmpId, session);
		    }
		}
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

    public static void addTrainingCourseEventAllocation(TrainingCourseEventAllocationData trainingCourseEventAllocationData, CustomSession... useSession) throws BusinessException {
	addTrainingCourseEventAllocation(trainingCourseEventAllocationData, false, useSession);
    }

    private static void addTrainingCourseEventAllocation(TrainingCourseEventAllocationData trainingCourseEventAllocationData, boolean skipValidation, CustomSession... useSession) throws BusinessException {
	if (!skipValidation)
	    validateTrainingCourseEventAllocation(trainingCourseEventAllocationData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(trainingCourseEventAllocationData.getTrainingCourseEventAllocation(), session);
	    trainingCourseEventAllocationData.setId(trainingCourseEventAllocationData.getTrainingCourseEventAllocation().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    trainingCourseEventAllocationData.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateTrainingCourseEventAllocation(TrainingCourseEventAllocationData trainingCourseEventAllocationData, CustomSession... useSession) throws BusinessException {
	validateTrainingCourseEventAllocation(trainingCourseEventAllocationData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(trainingCourseEventAllocationData.getTrainingCourseEventAllocation(), session);

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

    public static void deleteExternalPartiesAllocationsForCourseEvents(long externalPartyId, List<TrainingCourseEventData> courseEvents, long loginEmployeeId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	Long[] courseEventsIds = new Long[courseEvents.size()];
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    for (int i = 0; i < courseEvents.size(); i++)
		courseEventsIds[i] = courseEvents.get(i).getId();
	    List<TrainingCourseEventAllocationData> trainingCourseEventAllocationsData = getTrainingCoursesEventsAllocationsByExternalPartyIdAndRegionId(courseEventsIds, FlagsEnum.ALL.getCode(), externalPartyId);
	    for (TrainingCourseEventAllocationData allocation : trainingCourseEventAllocationsData) {
		allocation.getTrainingCourseEventAllocation().setSystemUser(loginEmployeeId + ""); // For Auditing.
		deleteTrainingCourseEventAllocation(allocation, session);
	    }
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    e.printStackTrace();
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deleteTrainingCourseEventAllocation(TrainingCourseEventAllocationData trainingCourseEventAllocationData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(trainingCourseEventAllocationData.getTrainingCourseEventAllocation(), session);

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
    public static void validateTrainingCourseEventAllocation(TrainingCourseEventAllocationData allocation) throws BusinessException {
	if (allocation.getCourseEventId() == null)
	    throw new BusinessException("error_transactionDataError");
	if (allocation.getRegionId() == null && allocation.getExternalPartyId() == null)
	    throw new BusinessException("error_transactionDataError");
	if (allocation.getRegionId() != null && allocation.getExternalPartyId() != null)
	    throw new BusinessException("error_transactionDataError");
	if (allocation.getAllocationDesc() != null && allocation.getAllocationDesc().length() > MAX_ALLOCATION_DESC_LENGTH)
	    throw new BusinessException("error_trnCourseEventAllocationDescExceeded");

	if (allocation.getId() == null && getTrainingCoursesEventsAllocationsByExternalPartyIdAndRegionId(new Long[] { allocation.getCourseEventId() }, allocation.getRegionId() == null ? FlagsEnum.ALL.getCode() : allocation.getRegionId(), allocation.getExternalPartyId() == null ? FlagsEnum.ALL.getCode() : allocation.getExternalPartyId()).size() > 0)
	    throw new BusinessException("error_duplicatedExternalPartyAllocation");

    }

    /*---------------------------Queries------------------------------*/
    public static List<TrainingCourseEventAllocationData> getTrainingCourseEventAllocations(long courseEventId) throws BusinessException {
	return searchTrainingCoursesEventsAllocations(new Long[] { courseEventId }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<TrainingCourseEventAllocationData> getTrainingCoursesEventsAllocationsByExternalPartyIdAndRegionId(Long[] courseEventsIds, long regionId, long externalPartyId) throws BusinessException {
	return searchTrainingCoursesEventsAllocations(courseEventsIds, regionId, externalPartyId, FlagsEnum.ALL.getCode());
    }

    public static List<TrainingCourseEventAllocationData> getTrainingCoursesEventsExternalPartiesAllocations(long courseEventId) throws BusinessException {
	return searchTrainingCoursesEventsAllocations(new Long[] { courseEventId }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.OFF.getCode());
    }

    private static List<TrainingCourseEventAllocationData> searchTrainingCoursesEventsAllocations(Long[] courseEventsIds, long regionId, long externalPartyId, int regionAllocationFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (courseEventsIds == null || courseEventsIds.length == 0) {
		qParams.put("P_COURSE_EVENT_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_COURSE_EVENTS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_COURSE_EVENT_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_COURSE_EVENTS_IDS", courseEventsIds);

	    }

	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_EXTERNAL_PARTY_ID", externalPartyId);
	    qParams.put("P_REGION_ALLOCATION_FLAG", regionAllocationFlag);
	    return DataAccess.executeNamedQuery(TrainingCourseEventAllocationData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_COURSES_EVENTS_ALLOCATIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /********************************************** training courses events allocations employees **********************************************/
    /*---------------------------Operations---------------------------*/
    private static void addTrainingCourseEventAllocationEmployee(TrainingCourseEventAllocationEmployeeData allocationEmployee, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    allocationEmployee.getTrainingCourseEventAllocationEmployee().setSystemUser(loginEmpId + ""); // For Auditing.
	    DataAccess.addEntity(allocationEmployee.getTrainingCourseEventAllocationEmployee(), session);
	    allocationEmployee.setId(allocationEmployee.getTrainingCourseEventAllocationEmployee().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    allocationEmployee.setId(null);
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    private static void deleteTrainingCourseEventAllocationEmployee(TrainingCourseEventAllocationEmployeeData allocationEmp, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    allocationEmp.getTrainingCourseEventAllocationEmployee().setSystemUser(loginEmpId + ""); // For Auditing.
	    DataAccess.deleteEntity(allocationEmp.getTrainingCourseEventAllocationEmployee(), session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    e.printStackTrace();
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static TrainingCourseEventAllocationEmployeeData constructTrainingCourseEventAllocationsEmp(long allocationId, EmployeeData emp) throws BusinessException {

	TrainingCourseEventAllocationEmployeeData trainingCourseEventAllocationEmpsData = new TrainingCourseEventAllocationEmployeeData();
	trainingCourseEventAllocationEmpsData.setAllocationId(allocationId);
	trainingCourseEventAllocationEmpsData.setName(emp.getName());
	trainingCourseEventAllocationEmpsData.setEmpId(emp.getEmpId());
	trainingCourseEventAllocationEmpsData.setPhysicalUnitFullName(emp.getPhysicalUnitFullName());
	trainingCourseEventAllocationEmpsData.setRankDesc(emp.getRankDesc());
	trainingCourseEventAllocationEmpsData.setJobDesc(emp.getJobDesc());

	return trainingCourseEventAllocationEmpsData;
    }

    /*--------------------------- Queries ------------------------------*/
    public static List<TrainingCourseEventAllocationEmployeeData> getTrainingCoursesEventsAllocationEmps(long allocationId) throws BusinessException {
	return searchTrainingCoursesEventsAllocationEmps(allocationId);
    }

    private static List<TrainingCourseEventAllocationEmployeeData> searchTrainingCoursesEventsAllocationEmps(long allocationId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_ALLOCATION_ID", allocationId);

	    return DataAccess.executeNamedQuery(TrainingCourseEventAllocationEmployeeData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_COURSES_EVENTS_ALLOCATIONS_EMPS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    private static Long countAllocationEmployeeConflictCoursesEventsDates(Long[] excludedIds, long employeeId, String actualStartDate, String actualEndDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    if (excludedIds == null || excludedIds.length == 0) {
		qParams.put("P_EXCLUDED_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EXCLUDED_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_EXCLUDED_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EXCLUDED_IDS", excludedIds);
	    }
	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_ACTUAL_START_DATE", actualStartDate);
	    qParams.put("P_ACTUAL_END_DATE", actualEndDate);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_CONFLICT_COURSES_EVENTS_DATES_FOR_TRAINGING_PLAN.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*--------------------------- validations ------------------------------*/
    public static void validateTrainingCourseEventAllocationEmployeesBusinessRules(long employeeId, TrainingCourseEventData courseEvent) throws BusinessException {

	EmployeeData employeeData = EmployeesService.getEmployeeData(employeeId);
	if (TrainingEmployeesService.countEmployeeConflictCoursesEventsDates(null, TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), employeeId, courseEvent.getActualStartDateString(), courseEvent.getActualEndDateString()) > 0)
	    throw new BusinessException("error_trainingDatesConflict", new Object[] { employeeData.getName() });

	if (TrainingEmployeesService.countConflictTrainingTransactions(TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), employeeId, courseEvent.getActualStartDateString(), courseEvent.getActualEndDateString(), FlagsEnum.ALL.getCode()) > 0)
	    throw new BusinessException("error_trainingDatesConflict", new Object[] { employeeData.getName() });

	if (countAllocationEmployeeConflictCoursesEventsDates(courseEvent.getId() == null ? null : new Long[] { courseEvent.getId() }, employeeId, courseEvent.getActualStartDateString(), courseEvent.getActualEndDateString()) > 0)
	    throw new BusinessException("error_trainingDatesConflict", new Object[] { employeeData.getName() });

	TrainingEmployeesService.validateCourseEventPrerequistes(employeeData, courseEvent);

    }

    /***************************************************************
     * Training Plans
     * 
     * @throws BusinessException
     ***************************************************************/
    /*--------------------------- validations ------------------------------*/
    public static void validateTrainingPlanBusinessRules(List<TrainingCourseEventData> courseEvents, List<TrainingCourseEventData> courseEventsNotAutoGenerated, Long trainingUnitId, Long trainingYearId) throws BusinessException {
	if (trainingYearId == null)
	    throw new BusinessException("error_transactionDataError");

	TrainingYear trainingYear = TrainingSetupService.getTrainingYearById(trainingYearId);

	if ((trainingYear.getStatus().equals(TrainingYearStatusEnum.INITIAL_DRAFT.getCode()) || trainingYear.getStatus().equals(TrainingYearStatusEnum.FINAL_DRAFT.getCode())) && trainingUnitId == null)
	    throw new BusinessException("error_chooseTrainingUnitMandatory");

	if (trainingYear.getStatus().equals(TrainingYearStatusEnum.TRAINING_PLAN_PENDING_APPROVE.getCode())) {
	    List<TrainingUnitData> trainingUnitsWithNoCourses = TrainingSetupService.getMissingTrainingUnitsPlanApproval(trainingYearId);
	    if (trainingUnitsWithNoCourses.size() > 0) {
		StringBuilder notValidTrainingUnitsNames = new StringBuilder();

		for (TrainingUnitData unit : trainingUnitsWithNoCourses) {
		    notValidTrainingUnitsNames.append(unit.getName()).append(",");
		}

		notValidTrainingUnitsNames.deleteCharAt(notValidTrainingUnitsNames.length() - 1);

		throw new BusinessException("error_invalidTrainingUnitCourseEvents", new String[] { notValidTrainingUnitsNames.toString() });
	    }
	}

	for (TrainingCourseEventData courseEvent : courseEvents) {
	    validateCourseEvent(courseEvent, TrainingTransactionCategoryEnum.TRAINING_PLAN.getCode());
	}

	if (!courseEventsNotAutoGenerated.isEmpty())
	    validateCourseEventSerialNumbers(courseEventsNotAutoGenerated);
    }

    /*---------------------------Reports------------------------------*/
    public static byte[] getTrainingPlanDecisionApprovalBytes(long trainingYearId) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = ReportNamesEnum.TRAINING_DECISION_PLAN_APPROVAL.getCode();
	    parameters.put("P_TRAINING_YEAR_ID", trainingYearId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingPlanDecisionDetailsBytes(long trainingYearId, String reportOutputFormat) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = ReportNamesEnum.TRAINING_DECISION_PLAN_DETAILS.getCode();
	    parameters.put("P_TRN_YEAR_ID", trainingYearId);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    if (reportOutputFormat.equals(ReportOutputFormatsEnum.PDF.getCode()))
		return getReportData(reportName, parameters);
	    else if (reportOutputFormat.equals(ReportOutputFormatsEnum.RTF.getCode()))
		return getRTFReportData(reportName, parameters);
	    else
		return getXlsReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingPlanInitialDraftBytes(long trainingYearId, long trainingUnitId) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = ReportNamesEnum.TRAINING_PLAN_INITIAL_DRAFT.getCode();
	    parameters.put("P_TRN_YEAR_ID", trainingYearId);
	    parameters.put("P_TRN_UNIT_ID", trainingUnitId);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getTrainingPlanFinalDraftBytes(long trainingYearId, long trainingUnitId, long regionId) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    reportName = ReportNamesEnum.TRAINING_PLAN_FINAL_DRAFT.getCode();
	    parameters.put("P_TRN_YEAR_ID", trainingYearId);
	    parameters.put("P_TRN_UNIT_ID", trainingUnitId);
	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**************************************************** Courses Events Decisions ********************************************************************/
    /*--------------------------- Operations ------------------------------*/
    public static void addTrainingCourseEventDecision(TrainingCourseEventDecisionData courseEventDecision, String processName, CustomSession... useSession) throws BusinessException {
	validateTrainingCourseEventDecision(courseEventDecision);

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

	    courseEventDecision.setDecisionNumber(etrCorInfo[0]);
	    courseEventDecision.setDecisionDateString(etrCorInfo[1]);

	    DataAccess.addEntity(courseEventDecision.getTrainingCourseEventDecision(), session);
	    courseEventDecision.setId(courseEventDecision.getTrainingCourseEventDecision().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    courseEventDecision.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateTrainingCourseEventDecision(TrainingCourseEventDecisionData courseEventDecision, CustomSession... useSession) throws BusinessException {
	validateTrainingCourseEventDecision(courseEventDecision);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(courseEventDecision.getTrainingCourseEventDecision(), session);

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

    public static void handleCourseEventDecisionRequests(TrainingCourseEventDecisionData courseEventDecision, List<TrainingCourseEventDecisionEmployee> trainingCourseEventDecisionEmployeesList, TrainingCourseEventData courseEventData, String processName, int trainingTransactionCategory, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    addTrainingCourseEventDecision(courseEventDecision, processName, session);
	    if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId() || courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_MODIFY_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		for (TrainingCourseEventDecisionEmployee trainingCourseEventDecisionEmployee : trainingCourseEventDecisionEmployeesList) {
		    trainingCourseEventDecisionEmployee.setCourseEventDecisionId(courseEventDecision.getId());
		    addTrainingCourseEventDecisionEmployee(trainingCourseEventDecisionEmployee, session);
		}
	    }

	    // Update course event status
	    if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		courseEventData.setStatus(TrainingCourseEventStatusEnum.COURSE_EVENT_HOLDING_DECISION_ISSUED.getCode());
	    else if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		courseEventData.setStatus(TrainingCourseEventStatusEnum.COURSE_EVENT_POSTPONEMENT_DECISION_ISSUED.getCode());
	    else if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		courseEventData.setStatus(TrainingCourseEventStatusEnum.COURSE_EVENT_CANCEL_DECISION_ISSUED.getCode());
	    else if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_RESULTS.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		courseEventData.setStatus(TrainingCourseEventStatusEnum.COURSE_EVENT_HELD.getCode());

	    // Update courseEvent Actual start and Actual end date in case of postponement
	    if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		courseEventData.setActualStartDate(courseEventDecision.getNewStartDate());
		courseEventData.setActualEndDate(courseEventDecision.getNewEndDate());
	    }

	    updateTrainingCourseEvent(courseEventData, trainingTransactionCategory, useSession);

	    // Update trainees status in case of course cancellation and course postponement
	    if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId() || courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		List<TrainingTransactionData> transactions = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.TRAINING_COURSE_EVENT_POSTPONED.getCode() }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(),
			courseEventDecision.getCourseEventId());
		for (TrainingTransactionData transaction : transactions) {
		    transaction.setStatus(courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId() ? TraineeStatusEnum.TRAINING_COURSE_EVENT_CANCELLED.getCode() : TraineeStatusEnum.TRAINING_COURSE_EVENT_POSTPONED.getCode());
		    TrainingEmployeesService.updateTrainingTransaction(transaction, useSession);
		}
	    }

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

    /*--------------------------- validations ------------------------------*/
    public static void validateTrainingCourseEventDecision(TrainingCourseEventDecisionData courseEventDecision) throws BusinessException {
	validateTrainingCourseEventDecisionMandatoryFields(courseEventDecision);
	validateTrainingCourseEventDecisionHijriDates(courseEventDecision);
	validateTrainingCourseEventDecisionBusinessRules(courseEventDecision);
    }

    private static void validateTrainingCourseEventDecisionMandatoryFields(TrainingCourseEventDecisionData courseEventDecision) throws BusinessException {
	if (courseEventDecision.getTransactionTypeId() == null || courseEventDecision.getTransStartDate() == null || courseEventDecision.getTransEndDate() == null)
	    throw new BusinessException("error_transactionDataError");

	if (courseEventDecision.getCourseEventId() == null)
	    throw new BusinessException("error_trainingCourseMandatory");

	if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
	    if (courseEventDecision.getNewStartDate() == null)
		throw new BusinessException("error_newStartDateIsMandatory");

	    if (courseEventDecision.getNewEndDate() == null)
		throw new BusinessException("error_newEndDateIsMandatory");

	}

	// check on reasons for case of course event postpone and cancellation
	if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()
		|| courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
	    if (courseEventDecision.getReasons() == null || courseEventDecision.getReasons().trim().isEmpty())
		throw new BusinessException("error_courseDecisionReasonIsMandatory");
	}
    }

    private static void validateTrainingCourseEventDecisionHijriDates(TrainingCourseEventDecisionData courseEventDecision) throws BusinessException {
	if (!HijriDateService.isValidHijriDate(courseEventDecision.getTransStartDate())) {
	    throw new BusinessException("error_startDateInvalid");
	}

	if (!HijriDateService.isValidHijriDate(courseEventDecision.getTransEndDate())) {
	    throw new BusinessException("error_endDateInvalid");
	}

	if (courseEventDecision.getNewStartDate() != null && !HijriDateService.isValidHijriDate(courseEventDecision.getNewStartDate())) {
	    throw new BusinessException("error_newStartDateInvalid");
	}

	if (courseEventDecision.getNewEndDate() != null && !HijriDateService.isValidHijriDate(courseEventDecision.getNewEndDate())) {
	    throw new BusinessException("error_newEndDateInvalid");
	}
    }

    private static void validateTrainingCourseEventDecisionBusinessRules(TrainingCourseEventDecisionData courseEventDecision) throws BusinessException {
	if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()
		|| courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_CANCELLATION_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) { // cancellation and postponement
	    // check if there is a trainee who joined the course that needs to be postponed or cancelled
	    if (TrainingEmployeesService.courseEventHasJoinedEmployees(courseEventDecision.getCourseEventId(), TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()))
		throw new BusinessException("error_traineesHasJoinedCourseEvent");

	    if (courseEventDecision.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId()) {
		// check if the course event new end date after new start date
		if (!courseEventDecision.getNewEndDate().after(courseEventDecision.getNewStartDate()))
		    throw new BusinessException("error_courseEventNewEndDateIsBeforeCourseEventNewStartDate");

		// check if the course event new start date is before or the same as the actual date
		if (courseEventDecision.getNewStartDate().before(courseEventDecision.getTransStartDate()) || courseEventDecision.getNewStartDate().equals(courseEventDecision.getTransStartDate()))
		    throw new BusinessException("error_courseEventNewStartDateIsBeforeCourseEventOldStartDate");

		// check if the course event new start date and new end date is within training year start and end dates
		TrainingYear trainingYear = TrainingSetupService.getTrainingYearById(TrainingCoursesEventsService.getCourseEventById(courseEventDecision.getCourseEventId()).getTrainingYearId());
		if (courseEventDecision.getNewStartDate().before(trainingYear.getStartDate()) || courseEventDecision.getNewEndDate().after(trainingYear.getEndDate())) {
		    throw new BusinessException("error_coursesDecisionsDatesMismatchTrainingYearDates", new String[] { trainingYear.getStartDateString(), trainingYear.getEndDateString() });
		}
	    }

	    // check for fields size limit
	    if (courseEventDecision.getReasons().length() > MAX_REASONS_LENGTH)
		throw new BusinessException("error_reasonExceedMaximumLength");
	    if (courseEventDecision.getNotes() != null && courseEventDecision.getNotes().length() > MAX_NOTES_LENGTH)
		throw new BusinessException("error_notesExceedMaximumLength");
	}
    }

    /*--------------------------- Queries ------------------------------*/
    private static TrainingCourseEventDecisionData getLastValidTrainingCourseEventDecision(long courseEventId) throws BusinessException {

	long transactionTypeNewDecision = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	long transactionTypeModifyDecision = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_MODIFY_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();

	List<TrainingCourseEventDecisionData> courseEventDecisions = getTrainingCourseEventDecisionsByCourseEventId(courseEventId);
	if (courseEventDecisions.isEmpty())
	    return null;
	TrainingCourseEventDecisionData lastDecision = courseEventDecisions.get(courseEventDecisions.size() - 1);
	if (lastDecision.getTransactionTypeId() != transactionTypeNewDecision && lastDecision.getTransactionTypeId() != transactionTypeModifyDecision)
	    return null;
	return lastDecision;
    }

    public static List<TrainingCourseEventDecisionData> getTrainingCourseEventDecisionsByCourseEventId(long courseEventId) throws BusinessException {
	return searchTrainingCourseEventDecision(courseEventId, FlagsEnum.ALL.getCode(), null, null, null);
    }

    public static TrainingCourseEventDecisionData getTrainingCourseEventDecision(long courseEventId, long transactionTypeId, String transStartDate, String newStartDate, String decisionDate) throws BusinessException {
	return searchTrainingCourseEventDecision(courseEventId, transactionTypeId, transStartDate, newStartDate, decisionDate).get(0);
    }

    private static List<TrainingCourseEventDecisionData> searchTrainingCourseEventDecision(long courseEventId, long transactionTypeId, String transStartDate, String newStartDate, String decisionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_COURSE_EVENT_ID", courseEventId);
	    qParams.put("P_TRANSACTION_TYPE_ID", transactionTypeId);

	    if (newStartDate != null) {
		qParams.put("P_NEW_START_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_NEW_START_DATE", newStartDate);
	    } else {
		qParams.put("P_NEW_START_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_NEW_START_DATE", HijriDateService.getHijriSysDateString());
	    }

	    if (transStartDate != null) {
		qParams.put("P_TRANS_START_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRANS_START_DATE", transStartDate);
	    } else {
		qParams.put("P_TRANS_START_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRANS_START_DATE", HijriDateService.getHijriSysDateString());
	    }
	    if (decisionDate != null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", decisionDate);
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    }
	    return DataAccess.executeNamedQuery(TrainingCourseEventDecisionData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_COURSE_EVENT_DECISION_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    public static List<TrainingCourseEventDecisionData> getTrainingCoursesEventDecisionsForInquiry(long trainingYearId, long trainingUnitId, long courseEventId, long transactionTypeId, Date startDateFrom, Date startDateTo) throws BusinessException {
	return searchTrainingCoursesEventsDecisionsForInquiry(trainingYearId, trainingUnitId, courseEventId, transactionTypeId, startDateFrom, startDateTo);
    }

    private static List<TrainingCourseEventDecisionData> searchTrainingCoursesEventsDecisionsForInquiry(long trainingYearId, long trainingUnitId, long courseEventId, long transactionTypeId, Date startDateFrom, Date startDateTo) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_COURSE_EVENT_ID", courseEventId);
	    qParams.put("P_TRANSACTION_TYPE_ID", transactionTypeId);
	    qParams.put("P_TRANINING_YEAR_ID", trainingYearId);
	    qParams.put("P_TRANINING_UNIT_ID", trainingUnitId);

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

	    return DataAccess.executeNamedQuery(TrainingCourseEventDecisionData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_COURSE_EVENT_DECISION_DATA_FOR_INQUIRY.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*--------------------------- Reports ------------------------------*/
    public static byte[] getTrainingCourseEventDecisionBytes(long courseEventDecisionId, long transactionTypeId) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		reportName = ReportNamesEnum.TRAINING_DECISION_COURSE_START.getCode();
	    else if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_MODIFY_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		reportName = ReportNamesEnum.TRAINING_DECISION_COURSE_MODIFICATION.getCode();
	    else if (transactionTypeId == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		reportName = ReportNamesEnum.TRAINING_DECISION_COURSE_POSTPONEMENT.getCode();
	    else
		reportName = ReportNamesEnum.TRAINING_DECISION_COURSE_CANCELLATION.getCode();
	    // TODO Refactor Parameter Name
	    parameters.put("P_TRN_COURSE_DECISION_ID", courseEventDecisionId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**************************************************** Courses Decisions Employees ********************************************************************/
    /*--------------------------- Operations ------------------------------*/
    public static void addTrainingCourseEventDecisionEmployee(TrainingCourseEventDecisionEmployee trainingCourseEventDecisionEmployee, CustomSession... useSession) throws BusinessException {
	validateTrainingCourseEventDecisionEmployee(trainingCourseEventDecisionEmployee);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(trainingCourseEventDecisionEmployee, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    trainingCourseEventDecisionEmployee.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateTrainingCourseEventDecisionEmployee(TrainingCourseEventDecisionEmployee trainingCourseEventDecisionEmployee, CustomSession... useSession) throws BusinessException {
	validateTrainingCourseEventDecisionEmployee(trainingCourseEventDecisionEmployee);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(trainingCourseEventDecisionEmployee, session);

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

    /*--------------------------- validations ------------------------------*/
    public static void validateTrainingCourseEventDecisionEmployees(List<TrainingCourseEventDecisionEmployee> trainingCourseEventDecisionEmployeesList) throws BusinessException {

	Set<String> employeesNames = new HashSet<String>();

	for (TrainingCourseEventDecisionEmployee trainingCourseEventDecisionEmployee : trainingCourseEventDecisionEmployeesList) {
	    validateTrainingCourseEventDecisionEmployee(trainingCourseEventDecisionEmployee);

	    if (trainingCourseEventDecisionEmployee.getTrainingTransactionId() == null && !employeesNames.add(trainingCourseEventDecisionEmployee.getExternalEmployeeName()))
		throw new BusinessException("error_externalEmployeeNameRepeated", new Object[] { trainingCourseEventDecisionEmployee.getExternalEmployeeName() });
	}
    }

    private static void validateTrainingCourseEventDecisionEmployee(TrainingCourseEventDecisionEmployee trainingCourseEventDecisionEmployee) throws BusinessException {
	if (trainingCourseEventDecisionEmployee.getTrainingTransactionId() == null) {

	    if (trainingCourseEventDecisionEmployee.getExternalEmployeeName() == null || trainingCourseEventDecisionEmployee.getExternalEmployeeName().trim().isEmpty())
		throw new BusinessException("error_externalEmployeeNameIsMandatory");

	    if (trainingCourseEventDecisionEmployee.getTransEmpRankDesc() == null || trainingCourseEventDecisionEmployee.getTransEmpRankDesc().trim().isEmpty())
		throw new BusinessException("error_externalEmployeeRankIsMandatory");

	    if (trainingCourseEventDecisionEmployee.getTransEmpJobCode() == null || trainingCourseEventDecisionEmployee.getTransEmpJobCode().trim().isEmpty())
		throw new BusinessException("error_externalEmployeeNumberIsMandatory");

	    if (trainingCourseEventDecisionEmployee.getTransEmpJobCode().length() > MAX_JOB_NUMBER_LENGTH)
		throw new BusinessException("error_maximumExternalEmployeeNumber");

	    if (trainingCourseEventDecisionEmployee.getTransEmpJobName() == null || trainingCourseEventDecisionEmployee.getTransEmpJobName().trim().isEmpty())
		throw new BusinessException("error_externalEmployeeJobIsMandatory");

	    if (trainingCourseEventDecisionEmployee.getExternalEmployeePartyDesc() == null || trainingCourseEventDecisionEmployee.getExternalEmployeePartyDesc().trim().isEmpty())
		throw new BusinessException("error_externalEmployeePartyIsMandatory");
	} else {
	    if (trainingCourseEventDecisionEmployee.getTransEmpJobName() == null || trainingCourseEventDecisionEmployee.getTransEmpJobName().trim().isEmpty()) {
		TrainingTransactionData transaction = TrainingEmployeesService.getTrainingTransactionsDataByIds(new Long[] { trainingCourseEventDecisionEmployee.getTrainingTransactionId() }).get(0);
		EmployeeData employee = EmployeesService.getEmployeeData(transaction.getEmployeeId());
		throw new BusinessException("error_employeeSpecifiedServiceTerminated", new Object[] { employee.getName() });
	    }
	}
    }

    /*--------------------------- Queries ------------------------------*/
    public static List<TrainingCourseEventDecisionEmployee> getLastValidTrnCourseEventDecisionExternalEmployees(long courseEventId) throws BusinessException {
	try {
	    // TODO no valid decision exception
	    TrainingCourseEventDecisionData courseEventDecision = getLastValidTrainingCourseEventDecision(courseEventId);
	    if (courseEventDecision == null)
		throw new BusinessException("error_transactionDataError");

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_COURSE_EVENT_DECISION_ID", courseEventDecision.getId());

	    return DataAccess.executeNamedQuery(TrainingCourseEventDecisionEmployee.class, QueryNamesEnum.HCM_GET_TRN_COURSE_EVENT_DECISION_EXTERNAL_EMPLOYEES.getCode(), qParams);
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<TrainingCourseEventDecisionEmployee> getLastTrnCourseEventDecisionEmployees(long courseEventId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_COURSE_EVENT_ID", courseEventId);

	    return DataAccess.executeNamedQuery(TrainingCourseEventDecisionEmployee.class, QueryNamesEnum.HCM_GET_LAST_TRN_COURSE_EVENT_DECISION_EMPLOYEES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**************************************************** External courses ********************************************************************/
    /*--------------------------- Operations ------------------------------*/
    public static void handleExternalCoursesOperations(TrainingCourseEventData courseEventData, int trainingTransactionCategory) throws BusinessException {
	if (trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_CANCELLATION.getCode() || trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_POSTPONEMENT.getCode())
	    if (TrainingEmployeesService.courseEventHasJoinedEmployees(courseEventData.getId(), TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()))
		throw new BusinessException("error_traineesHasJoinedCourseEvent");

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    // update course event fields
	    if (trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_ADDITION.getCode()) {
		courseEventData.setActualStartDate(courseEventData.getPlannedStartDate());
		courseEventData.setActualEndDate(courseEventData.getPlannedEndDate());
		courseEventData.setCandidatesMin(courseEventData.getCandidatesMax());
		courseEventData.setStatus(TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode());
		courseEventData.setEflag(FlagsEnum.OFF.getCode());
		courseEventData.setMigFlag(FlagsEnum.OFF.getCode());
		courseEventData.setAutoGeneratedSerialFlag(FlagsEnum.OFF.getCode());
		courseEventData.setTrainingTypeId(TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode());
	    } else if (trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_POSTPONEMENT.getCode())
		courseEventData.setStatus(TrainingCourseEventStatusEnum.COURSE_EVENT_POSTPONEMENT_DECISION_ISSUED.getCode());
	    else if (trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_CANCELLATION.getCode())
		courseEventData.setStatus(TrainingCourseEventStatusEnum.COURSE_EVENT_CANCEL_DECISION_ISSUED.getCode());

	    if (courseEventData.getId() == null) {
		addTrainingCourseEvent(courseEventData, trainingTransactionCategory, session);
	    } else {
		updateTrainingCourseEvent(courseEventData, trainingTransactionCategory, session);

		// Update trainees status in case of course cancellation and course postponement

		List<TrainingTransactionData> transactions = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(), TraineeStatusEnum.TRAINING_COURSE_EVENT_POSTPONED.getCode() }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(),
			FlagsEnum.ALL.getCode(),
			FlagsEnum.ALL.getCode(), courseEventData.getId());
		for (TrainingTransactionData transaction : transactions) {
		    transaction.setStatus(trainingTransactionCategory == TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_CANCELLATION.getCode() ? TraineeStatusEnum.TRAINING_COURSE_EVENT_CANCELLED.getCode() : TraineeStatusEnum.TRAINING_COURSE_EVENT_POSTPONED.getCode());
		    TrainingEmployeesService.updateTrainingTransaction(transaction, session);
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

    /**************************************************** Course result registration ********************************************************************/
    /*--------------------------- Operations ------------------------------*/
    public static void handleInternalTrainingCourseEventResults(List<TrainingTransactionData> trainingTransactionsList, List<TrainingTransactionDetailData> trainingTransactionsDetailsList, TrainingCourseEventData courseEvent, String processName, CustomSession... useSession) throws BusinessException {

	TrainingEmployeesService.validateTrainingTransactionsForResultRegistration(trainingTransactionsList, courseEvent.getId());

	TrainingCourseEventData loadedCourseEvent = getCourseEventById(courseEvent.getId());
	if (loadedCourseEvent == null)
	    throw new BusinessException("error_transactionDataError");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    loadedCourseEvent.setStatus(TrainingCourseEventStatusEnum.COURSE_EVENT_HELD.getCode());
	    updateTrainingCourseEvent(loadedCourseEvent, session);

	    for (TrainingTransactionData trans : trainingTransactionsList) {
		trans.setStatus(TraineeStatusEnum.FINISHED.getCode());
		TrainingEmployeesService.updateTrainingTransaction(trans, useSession);
	    }

	    for (TrainingTransactionDetailData trainingTransactionDetailData : trainingTransactionsDetailsList) {
		TrainingEmployeesService.addTrainingTransactionDetail(trainingTransactionDetailData, processName, useSession);
	    }
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doInternalTrainingPayrollIntegration(trainingTransactionsList, session);

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

    private static void doInternalTrainingPayrollIntegration(List<TrainingTransactionData> trainingTransactionsList, CustomSession session) throws BusinessException {
	session.flushTransaction();
	for (TrainingTransactionData trainingTransactionData : trainingTransactionsList) {
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>();
	    Long adminDecision = null;
	    String gregStartDateString = null, gregEndDateString = null, originalDecisionNumber = null, decisionNumber = null, gregDecisionDateString = null;
	    List<TrainingTransactionDetailData> trainingTransactionDetailDataList = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionData.getId());
	    TrainingTransactionDetailData firstTransactionDetail = trainingTransactionDetailDataList == null || trainingTransactionDetailDataList.size() == 0 ? null : trainingTransactionDetailDataList.get(trainingTransactionDetailDataList.size() - 1);
	    EmployeeData employee = EmployeesService.getEmployeeData(trainingTransactionData.getEmployeeId());
	    if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		if (trainingTransactionData.getTrainingTypeId().equals(TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode())) {
		    TrainingCourseEventData courseEvent = TrainingCoursesEventsService.getCourseEventById(trainingTransactionData.getCourseEventId());
		    if (courseEvent.getRankingFlag() == null || courseEvent.getRankingFlag().equals(FlagsEnum.OFF.getCode()))
			adminDecision = AdminDecisionsEnum.TRAINING_COURSE_WITHOUT_RANKING_RESULTS_REGISTERATION.getCode();
		    else
			adminDecision = AdminDecisionsEnum.TRAINING_COURSE_WITH_RANKING_RESULTS_REGISTERATION.getCode();
		    decisionNumber = firstTransactionDetail.getDecisionNumber();
		    gregDecisionDateString = HijriDateService.hijriToGregDateString(firstTransactionDetail.getDecisionDateString());
		    gregStartDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getActualStartDateString());
		    gregEndDateString = HijriDateService.hijriToGregDateString(trainingTransactionData.getActualEndDateString());
		}
	    }
	    if (adminDecision != null) {
		adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(trainingTransactionData.getEmployeeId(), employee.getName(), trainingTransactionData.getId(), null, gregStartDateString, gregEndDateString, decisionNumber, originalDecisionNumber));
		Long unitId = employee.getPhysicalUnitId() != null ? employee.getPhysicalUnitId() : UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId();
		PayrollEngineService.doPayrollIntegration(adminDecision, employee.getCategoryId(), gregStartDateString, adminDecisionEmployeeDataList, unitId, gregDecisionDateString, DataAccess.getTableName(TrainingTransaction.class), FlagsEnum.OFF.getCode(), FlagsEnum.OFF.getCode(), session);
	    }
	}
    }
}