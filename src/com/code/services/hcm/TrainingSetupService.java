package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.Rankings;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.trainings.GraduationPlaceData;
import com.code.dal.orm.hcm.trainings.GraduationPlaceDetailData;
import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.dal.orm.hcm.trainings.QualificationMajorSpec;
import com.code.dal.orm.hcm.trainings.QualificationMinorSpecData;
import com.code.dal.orm.hcm.trainings.TrainingAnnualCourseData;
import com.code.dal.orm.hcm.trainings.TrainingAnnualPartyData;
import com.code.dal.orm.hcm.trainings.TrainingExternalPartyData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.enums.FlagsEnum;
import com.code.enums.MilitaryCivillianEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TrainingYearStatusEnum;
import com.code.enums.TrainingYearTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

/**
 * Service to handle the setup objects of the training module.
 */
public class TrainingSetupService extends BaseService {

    private final static double MAX_TRAINING_YEAR_DURATION = 2.0;

    /**
     * private constructor to prevent instantiation
     * 
     */
    private TrainingSetupService() {
    }

    /***************************** Object *****************************/
    /*---------------------------Operations---------------------------*/
    /*---------------------------Validations--------------------------*/
    /*---------------------------Queries------------------------------*/
    /*---------------------------Reports------------------------------*/

    /********************************************* Training Years ************************************************/
    /*---------------------------Operations---------------------------*/
    /**
     * Add a training year object to database
     * 
     * @param trainingYear
     * @param useSession
     * @throws BusinessException
     */
    public static void addTrainingYear(TrainingYear trainingYear, long trainingYearType, CustomSession... useSession) throws BusinessException {
	validateTrainingYear(trainingYear, trainingYearType);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // Generate Name
	    trainingYear.setName(generateTrainingYearName(trainingYear.getStartDate(), trainingYear.getSemesterName(), true));
	    trainingYear.setNameEnglish(generateTrainingYearName(trainingYear.getStartDate(), trainingYear.getSemesterNameEnglish(), false));

	    // Add Training Year
	    DataAccess.addEntity(trainingYear, session);

	    if (trainingYearType == TrainingYearTypesEnum.NEW_SEMESTER_IN_NEW_TRAINING_YEAR.getCode())
		trainingYear.setRelatedYearId(trainingYear.getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    trainingYear.setId(null);
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
     * Updates a training year object in database
     * 
     * @param trainingYear
     * @param useSession
     * @throws BusinessException
     */
    public static void updateTrainingYear(TrainingYear trainingYear, long trainingYearType, CustomSession... useSession) throws BusinessException {
	validateTrainingYear(trainingYear, trainingYearType);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // Generate Name
	    trainingYear.setName(generateTrainingYearName(trainingYear.getStartDate(), trainingYear.getSemesterName(), true));
	    trainingYear.setNameEnglish(generateTrainingYearName(trainingYear.getStartDate(), trainingYear.getSemesterNameEnglish(), false));

	    // Update Training Year
	    DataAccess.updateEntity(trainingYear, session);

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
     * Construct a training year object with the default data
     * 
     * @return
     * @throws BusinessException
     */
    public static TrainingYear constructTrainingYear() throws BusinessException {
	TrainingYear trainingYear = new TrainingYear();
	trainingYear.setStatus(TrainingYearStatusEnum.INITIAL_DRAFT.getCode());
	trainingYear.setEFlag(FlagsEnum.ON.getCode());
	trainingYear.setMigFlag(FlagsEnum.OFF.getCode());

	trainingYear.setStartDate(HijriDateService.getHijriSysDate());
	trainingYear.setEndDate(HijriDateService.getHijriSysDate());

	return trainingYear;
    }

    /**
     * Method to generate the name of the training year using its start date
     * 
     * @param startDate
     *            the start date of the training year
     * @return the generated name of the training year
     */
    private static String generateTrainingYearName(Date startDate, String semesterName, Boolean arabicFlag) throws BusinessException {
	int startYear = HijriDateService.getHijriDateYear(startDate);
	StringBuilder trainingYearName;
	if (arabicFlag)
	    trainingYearName = new StringBuilder(getParameterizedMessage("label_trainingYearGeneratedName", startYear + "", (startYear + 1) + ""));
	else
	    trainingYearName = new StringBuilder(startYear + " / " + (startYear + 1));

	if (semesterName != null) {
	    trainingYearName.append(String.format(" - %s", semesterName));
	}

	return trainingYearName.toString();
    }

    public static void handleTrainingYearChanges(long trainingYearId, Long decisionApprovedId, Long originalDecisionApprovedId, boolean isApprovalProcess, String processName, CustomSession session) throws BusinessException {
	try {
	    TrainingYear trainingYear = getTrainingYearById(trainingYearId);

	    if (isApprovalProcess) {
		String[] etrCorInfo = null;
		if (processName != null) {
		    etrCorInfo = ETRCorrespondence.doETRCorOut(processName, session);
		    trainingYear.setDecisionNumber(etrCorInfo[0]);
		    trainingYear.setDecisioinDate(HijriDateService.getHijriDate(etrCorInfo[1]));
		    trainingYear.setDecisionApprovedId(decisionApprovedId);
		    trainingYear.setOriginalDecisionApprovedId(originalDecisionApprovedId);
		    trainingYear.setStatus(TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode());
		}
	    }

	    if (trainingYear.getStatus().equals(TrainingYearStatusEnum.INITIAL_DRAFT.getCode()))
		trainingYear.setStatus(TrainingYearStatusEnum.FINAL_DRAFT.getCode());
	    else if (trainingYear.getStatus().equals(TrainingYearStatusEnum.FINAL_DRAFT.getCode()))
		trainingYear.setStatus(TrainingYearStatusEnum.TRAINING_PLAN_PENDING_APPROVE.getCode());
	    long trainingYearType;
	    if (trainingYear.getRelatedYearId() == null)
		trainingYearType = TrainingYearTypesEnum.NEW_YEAR_WITH_NO_SEMESTERS.getCode();
	    else if (trainingYear.getRelatedYearId().equals(trainingYear.getId()))
		trainingYearType = TrainingYearTypesEnum.NEW_SEMESTER_IN_NEW_TRAINING_YEAR.getCode();
	    else
		trainingYearType = TrainingYearTypesEnum.NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR.getCode();

	    TrainingSetupService.updateTrainingYear(trainingYear, trainingYearType, session);
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------Validations--------------------------*/
    /**
     * Validates training year object
     * 
     * @param trainingYear
     */
    private static void validateTrainingYear(TrainingYear trainingYear, long trainingYearType) throws BusinessException {
	if (trainingYear == null)
	    throw new BusinessException("error_transactionDataError");

	if (trainingYear.getId() == null) {
	    if (TrainingSetupService.getUnApprovedTrainingYear() != null)
		throw new BusinessException("error_TrnMoreThanOneUnApprovedPlan");

	    if (trainingYearType == TrainingYearTypesEnum.NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR.getCode() && TrainingSetupService.getAllTrainingYears().isEmpty())
		throw new BusinessException("error_noExistingTrainingYear");
	}

	// validateTrainingYearHijriDates
	if (trainingYear.getStartDate() == null || !HijriDateService.isValidHijriDate(trainingYear.getStartDate()))
	    throw new BusinessException("error_invalidTrainingYearStartDate");
	if (trainingYear.getEndDate() == null || !HijriDateService.isValidHijriDate(trainingYear.getEndDate()))
	    throw new BusinessException("error_invalidTrainingYearEndDate");
	if (trainingYear.getNeedsStartDate() != null && !HijriDateService.isValidHijriDate(trainingYear.getNeedsStartDate()))
	    throw new BusinessException("error_invalidTrainingYearNeedsStartDate");
	if (trainingYear.getNeedsEndDate() != null && !HijriDateService.isValidHijriDate(trainingYear.getNeedsEndDate()))
	    throw new BusinessException("error_invalidTrainingYearNeedsEndDate");

	// validateTrainingYearBusinessRules
	try {
	    // Validate that trainingYear not used in any process of training
	    if (trainingYear.getId() != null) {
		if (trainingYear.getStatus().equals(TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode())) {
		    TrainingYear previousTrainingYear = TrainingSetupService.getTrainingYearById(trainingYear.getId());
		    if (previousTrainingYear.getStatus() == trainingYear.getStatus())
			throw new BusinessException("error_approvedTrainingYearsCannotBeUpdated");
		}

		if (TrainingCoursesEventsService.checkExistingTrainingCoursesEventsNotInTrainingYearInterval(trainingYear.getId(), trainingYear.getStartDateString(), trainingYear.getEndDateString()))
		    throw new BusinessException("error_trainingYearUsedinProcess");
	    }

	    // 1- validate on initial drafted
	    if (checkExistingDraftTrainingYear(trainingYear.getId()))
		throw new BusinessException("error_TrnMoreThanOneUnApprovedPlan");

	    TrainingYear lastApprovedTrainingYear = getLastApprovedTrainingYear();
	    if (lastApprovedTrainingYear != null) {
		Date lastApprovedTrainingYearStartDate = lastApprovedTrainingYear.getStartDate();
		if (lastApprovedTrainingYear.getRelatedYearId() != null)
		    lastApprovedTrainingYearStartDate = getTrainingYearById(lastApprovedTrainingYear.getRelatedYearId()).getStartDate();

		if (!trainingYear.getStartDate().after(lastApprovedTrainingYear.getStartDate()))
		    throw new BusinessException("error_newSemesterStartDateMustBeAfterLastSemesterStartDate");

		if (trainingYearType == TrainingYearTypesEnum.NEW_YEAR_WITH_NO_SEMESTERS.getCode() || trainingYearType == TrainingYearTypesEnum.NEW_SEMESTER_IN_NEW_TRAINING_YEAR.getCode()) {
		    if (HijriDateService.getHijriDateYear(lastApprovedTrainingYearStartDate) == HijriDateService.getHijriDateYear(trainingYear.getStartDate()))
			throw new BusinessException("error_TrnYearDuplicatedYear");
		    else if (HijriDateService.hijriDateDiffByHijriYear(trainingYear.getStartDate(), trainingYear.getEndDate()) > MAX_TRAINING_YEAR_DURATION)
			throw new BusinessException("error_TrnYearStartDateAndYearEndDateDiffLimitExceed");
		} else if (trainingYearType == TrainingYearTypesEnum.NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR.getCode()) {
		    if (HijriDateService.hijriDateDiffByHijriYear(lastApprovedTrainingYearStartDate, trainingYear.getEndDate()) > MAX_TRAINING_YEAR_DURATION)
			throw new BusinessException("error_TrnYearStartDateAndYearEndDateDiffLimitExceed");
		}
	    }

	    // 3- validate Dates
	    if (trainingYear.getEndDate().compareTo(trainingYear.getStartDate()) <= 0)
		throw new BusinessException("error_TrnYearEndDateBeforeYearStartDate");

	    if (trainingYear.getNeedsStartDate() != null && trainingYear.getNeedsEndDate() != null) {
		if (trainingYear.getNeedsEndDate().compareTo(trainingYear.getNeedsStartDate()) <= 0)
		    throw new BusinessException("error_TrnNeedsEndDateBeforeNeedsStartDate");

		if (trainingYear.getNeedsStartDate().compareTo(trainingYear.getStartDate()) <= 0)
		    throw new BusinessException("error_TrnNeedsStartDateBeforeYearStartDate");

		if (trainingYear.getEndDate().compareTo(trainingYear.getNeedsEndDate()) <= 0)
		    throw new BusinessException("error_TrnYearEndDateBeforeNeedsEndDate");
	    }

	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*---------------------------Queries------------------------------*/
    /**
     * Wrapper for {@link #searchTrainingYears()} to get all training years
     * 
     * @return array list of training years objects
     * @throws BusinessException
     */
    public static TrainingYear getTrainingYearById(long yearId) throws BusinessException {
	return searchTrainingYears(null, yearId, FlagsEnum.ALL.getCode(), null).get(0);
    }

    public static List<TrainingYear> getApprovedTrainingYears() throws BusinessException {
	return searchTrainingYears(new Integer[] { TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode() }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null);
    }

    public static List<TrainingYear> getAllTrainingYears() throws BusinessException {
	return searchTrainingYears(null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null);
    }

    private static boolean checkExistingDraftTrainingYear(Long excludedYearId) throws BusinessException {
	Integer[] StatusIds = new Integer[] { TrainingYearStatusEnum.INITIAL_DRAFT.getCode(),
		TrainingYearStatusEnum.FINAL_DRAFT.getCode(),
		TrainingYearStatusEnum.TRAINING_PLAN_PENDING_APPROVE.getCode() };
	return searchTrainingYears(StatusIds, FlagsEnum.ALL.getCode(), excludedYearId == null ? FlagsEnum.ALL.getCode() : excludedYearId, null).size() > 0;
    }

    public static TrainingYear getUnApprovedTrainingYear() throws BusinessException {
	Integer[] StatusIds = new Integer[] { TrainingYearStatusEnum.INITIAL_DRAFT.getCode(),
		TrainingYearStatusEnum.FINAL_DRAFT.getCode(),
		TrainingYearStatusEnum.TRAINING_PLAN_PENDING_APPROVE.getCode() };
	List<TrainingYear> years = searchTrainingYears(StatusIds, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null);
	if (years == null || years.isEmpty())
	    return null;
	return years.get(0);
    }

    public static Long countNumberOfSemesterInTrainingYear(long relatedTrainingYearId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RELATED_YEAR_ID", relatedTrainingYearId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_NUMBER_OF_SEMESTERS_IN_TRAINING_YEAR.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    public static TrainingYear getLastApprovedTrainingYear() throws BusinessException {
	List<TrainingYear> trnYears = searchTrainingYears(new Integer[] { TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode() }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null);
	if (trnYears.isEmpty())
	    return null;
	return trnYears.get(trnYears.size() - 1);
    }

    /**
     * Query to search for training years objects
     * 
     * @return array list of training years objects
     * @throws BusinessException
     */
    private static List<TrainingYear> searchTrainingYears(Integer[] statusIds, long yearId, long excludedYearId, String startYear) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_YEAR_ID", yearId);
	    qParams.put("P_EXCLUDED_YEAR_ID", excludedYearId);
	    if (statusIds == null || statusIds.length == 0) {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUS_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUS_IDS", statusIds);
	    }

	    qParams.put("P_START_YEAR", startYear == null ? FlagsEnum.ALL.getCode() + "" : startYear);
	    List<TrainingYear> result = DataAccess.executeNamedQuery(TrainingYear.class, QueryNamesEnum.HCM_SEARCH_TRAINING_YEARS.getCode(), qParams);
	    return result;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /************************************** Training External Party **********************************************/
    /*---------------------------Operations---------------------------*/
    /**
     * Adds a training external party object to database
     * 
     * @param trainingExternalPartyData
     * @throws BusinessException
     * 
     * @see TrainingExternalPartyData
     */
    public static void addTrainingExternalParty(TrainingExternalPartyData trainingExternalPartyData, CustomSession... useSession) throws BusinessException {
	validateAddUpdateTrainingExternalParty(trainingExternalPartyData, null);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(trainingExternalPartyData.getTrainingExternalParty(), session);
	    trainingExternalPartyData.setId(trainingExternalPartyData.getTrainingExternalParty().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    trainingExternalPartyData.setId(null);

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
     * Updates a training external party object in database
     * 
     * @param trainingExternalPartyData
     * @throws BusinessException
     * 
     * @see TrainingExternalPartyData
     */
    public static void updateTrainingExternalParty(TrainingExternalPartyData trainingExternalPartyData, String originalExternalPartyDesc, CustomSession... useSession) throws BusinessException {
	validateAddUpdateTrainingExternalParty(trainingExternalPartyData, originalExternalPartyDesc);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(trainingExternalPartyData.getTrainingExternalParty(), session);

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
     * Deletes a training external party object from database
     * 
     * @param trainingExternalPartyData
     * @throws BusinessException
     * 
     * @see TrainingExternalPartyData
     */
    public static void deleteTrainingExternalParty(TrainingExternalPartyData trainingExternalPartyData, CustomSession... useSession) throws BusinessException {
	validateDeleteTrainingExternalParty(trainingExternalPartyData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(trainingExternalPartyData.getTrainingExternalParty(), session);

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
     * Validates training external party object
     * 
     * @param trainingExternalPartyData
     * @throws BusinessException
     * @see TrainingExternalPartyData
     */
    private static void validateAddUpdateTrainingExternalParty(TrainingExternalPartyData trainingExternalPartyData, String originalExternalPartyDesc) throws BusinessException {
	if (trainingExternalPartyData == null)
	    throw new BusinessException("error_transactionDataError");

	// validate Training External Party mandatory fields
	if (trainingExternalPartyData.getDescription() == null || trainingExternalPartyData.getDescription().isEmpty())
	    throw new BusinessException("error_externalPartyDescriptionMandatory");
	if (trainingExternalPartyData.getCountryId() == null)
	    throw new BusinessException("error_externalPartyCountryMandatory");
	if (trainingExternalPartyData.getAddress() == null || trainingExternalPartyData.getAddress().isEmpty())
	    throw new BusinessException("error_externalPartyAddressMandatory");

	// validate Training External Party Business Rules
	// check for duplicate external party name
	if (checkExistingExternalPartyUnderCountry(trainingExternalPartyData.getDescription(), trainingExternalPartyData.getCountryId(), trainingExternalPartyData.getId() == null ? FlagsEnum.ALL.getCode() : trainingExternalPartyData.getId()))
	    throw new BusinessException("error_duplicateExternalPartyName");
	// check for civilian and military external parties
	if (trainingExternalPartyData.getId() != null && !trainingExternalPartyData.getDescription().equals(originalExternalPartyDesc)) {
	    if (TrainingCoursesEventsService.checkExistingCoursesEventsDataByExteranlPartyId(trainingExternalPartyData.getId()))
		throw new BusinessException("error_cannotEditExternalPartyName");

	    // check for military external parties in repeated annual external military parties
	    if (trainingExternalPartyData.getId() != null && trainingExternalPartyData.getType().equals(MilitaryCivillianEnum.Military.getCode()) && checkExistingExternalAnnualParty(FlagsEnum.ALL.getCode(), trainingExternalPartyData.getId()))
		throw new BusinessException("error_partyExistsInAnnualMilitaryParties");
	}

    }

    private static void validateDeleteTrainingExternalParty(TrainingExternalPartyData trainingExternalPartyData) throws BusinessException {
	if (trainingExternalPartyData == null || trainingExternalPartyData.getId() == null)
	    throw new BusinessException("error_transactionDataError");

	// validate Training External Party Business Rules
	// check for civilian and military external parties
	if (TrainingCoursesEventsService.checkExistingCoursesEventsDataByExteranlPartyId(trainingExternalPartyData.getId()))
	    throw new BusinessException("error_cannotDeleteExternalParty");

	// check for military external parties in annual external military parties
	if (trainingExternalPartyData.getType().equals(MilitaryCivillianEnum.Military.getCode()) && checkExistingExternalAnnualParty(FlagsEnum.ALL.getCode(), trainingExternalPartyData.getId()))
	    throw new BusinessException("error_partyExistsInAnnualMilitaryParties");
    }

    /*---------------------------Queries------------------------------*/
    /**
     * Wrapper for {@link #searchTrainingExternalParties(int)} to get all training external parties according to type
     * 
     * @param type
     *            type of the external party, 2 for military, 1 for civilian
     * @param description
     *            description of the external party
     * @param address
     *            address of the external party
     * @return array list of training external parties data objects
     * @throws BusinessException
     */
    public static List<TrainingExternalPartyData> getTrainingExternalParties(int type, String description, String address) throws BusinessException {
	return searchTrainingExternalParties(FlagsEnum.ALL.getCode(), type, description, address, false, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static TrainingExternalPartyData getTrainingExternalPartyById(long id) throws BusinessException {
	List<TrainingExternalPartyData> result = searchTrainingExternalParties(id, FlagsEnum.ALL.getCode(), null, null, false, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	return result.size() > 0 ? result.get(0) : null;
    }

    private static boolean checkExistingExternalPartyUnderCountry(String description, long countryId, long excludedId) throws BusinessException {
	return searchTrainingExternalParties(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), description, null, true, countryId, excludedId).size() > 0;
    }

    /**
     * Searches all training external parties according to type
     *
     * @param type
     *            type of the external party, 1 for military, 0 for civilian
     * @param description
     *            description of the external party
     * @param address
     *            address of the external party
     * @return array list of training external parties data objects
     * @return array list of training external parties data objects
     * @throws BusinessException
     */
    private static List<TrainingExternalPartyData> searchTrainingExternalParties(long id, int type, String description, String address, boolean isExactDescription, long countryId, long excludedId) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);
	    qParams.put("P_PARTY_TYPE", type);
	    qParams.put("P_COUNTRY_ID", countryId);
	    qParams.put("P_EXCLUDED_ID", excludedId);
	    if (isExactDescription)
		qParams.put("P_PARTY_DESCRIPTION", (description == null || description.equals("") || description.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : description);
	    else
		qParams.put("P_PARTY_DESCRIPTION", (description == null || description.equals("") || description.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + description + "%");
	    qParams.put("P_PARTY_ADDRESS", (address == null || address.equals("") || address.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + address + "%");
	    return DataAccess.executeNamedQuery(TrainingExternalPartyData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_EXTERNAL_PARTIES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /******************************************* Training Units **************************************************/
    /*---------------------------Operations---------------------------*/
    public static void updateTrainingUnit(TrainingUnitData trainingUnitData, CustomSession... useSession) throws BusinessException {
	validateTrainingUnit(trainingUnitData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(trainingUnitData.getTrainingUnit(), session);

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

    public static TrainingUnitData getTrainingUnitByPhysicalUnitHKey(String physicalUnitHKey) throws BusinessException {
	try {
	    ArrayList<UnitData> ancestorUnits = (ArrayList<UnitData>) UnitsService.getAncestorsUnitsByHKey(physicalUnitHKey);
	    ArrayList<TrainingUnitData> trainingUnitsData = (ArrayList<TrainingUnitData>) TrainingSetupService.getAllTrainingUnitsData();
	    for (UnitData ancestorUnit : ancestorUnits) {
		for (TrainingUnitData trainingUnitData : trainingUnitsData) {
		    if (ancestorUnit.getId().equals(trainingUnitData.getUnitId())) {
			return trainingUnitData;

		    }
		}
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

	return null;
    }

    public static Map<Long, Map<Long, Boolean>> getRegionsTrainingConfigMapForTrainingUnitsList(List<TrainingUnitData> trainingUnitsData) {
	Map<Long, Map<Long, Boolean>> regionsTrainingConfigMap = new HashMap<Long, Map<Long, Boolean>>();

	if (trainingUnitsData == null || trainingUnitsData.size() == 0)
	    return regionsTrainingConfigMap;

	for (TrainingUnitData trainingUnitData : trainingUnitsData)
	    regionsTrainingConfigMap.put(trainingUnitData.getUnitId(), getRegionsTrainingConfigMapForTrainingUnit(trainingUnitData));

	return regionsTrainingConfigMap;
    }

    private static Map<Long, Boolean> getRegionsTrainingConfigMapForTrainingUnit(TrainingUnitData trainingUnitData) {
	Map<Long, Boolean> regionsTrainingConfigMap = new HashMap<Long, Boolean>();
	String regionsTrainingConfig = trainingUnitData.getRegionsTrainingConfig();
	String[] regionsConfigArray = regionsTrainingConfig.split(",");

	for (int i = 0; i < regionsConfigArray.length; i++) {
	    if (regionsConfigArray[i].length() != 0) {
		String[] regionConfigArray = regionsConfigArray[i].split("-");
		regionsTrainingConfigMap.put(Long.parseLong(regionConfigArray[0]), Integer.parseInt(regionConfigArray[1]) == FlagsEnum.ON.getCode() ? true : false);
	    }
	}

	return regionsTrainingConfigMap;
    }

    /*---------------------------Validations--------------------------*/
    private static void validateTrainingUnit(TrainingUnitData trainingUnitData) throws BusinessException {
	if (trainingUnitData == null)
	    throw new BusinessException("error_transactionDataError");

	Map<Long, Boolean> regionsTrainingConfigMap = getRegionsTrainingConfigMapForTrainingUnit(trainingUnitData);

	if (trainingUnitData.getRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() || trainingUnitData.getRegionId() == RegionsEnum.ACADEMY.getCode()) {

	    for (Map.Entry<Long, Boolean> entry : regionsTrainingConfigMap.entrySet())
		if (!entry.getValue())
		    throw new BusinessException("error_cannotDisallowNominationOnPresidencyOrAcademy", new Object[] { CommonService.getRegionById(entry.getKey()).getDescription() });
	} else {
	    if (!regionsTrainingConfigMap.get(trainingUnitData.getRegionId()))
		throw new BusinessException("error_cannotDisallowNominationOnUnitForItsRegion");
	}
    }

    /*---------------------------Queries------------------------------*/
    public static List<TrainingUnitData> getMissingTrainingUnitsPlanApproval(long trainingYearId) throws BusinessException {
	return searchMissingTrainingUnitsPlanApproval(trainingYearId);
    }

    private static List<TrainingUnitData> searchMissingTrainingUnitsPlanApproval(long trainingYearId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_YEAR_ID", trainingYearId);
	    return DataAccess.executeNamedQuery(TrainingUnitData.class, QueryNamesEnum.HCM_SEARCH_MISSING_TRAINING_UNITS_PLAN_APPROVAL.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    public static List<TrainingUnitData> filterTrainingUnitsForCourseEventMiniSearch(long regionId) throws BusinessException {
	// Gets all training units in BG
	if (regionId == FlagsEnum.ALL.getCode())
	    return searchTrainingUnitsData(null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());

	Set<Long> regionIds = new HashSet<Long>();
	// In course event issue and modification, General directorate specialist can issue course decisions for general directorate and academy training units
	// If region specialist, He can issue for his training units only
	if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
	    regionIds.add(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    regionIds.add(RegionsEnum.ACADEMY.getCode());
	} else
	    regionIds.add(regionId);

	return searchTrainingUnitsData(regionIds.toArray(new Long[regionIds.size()]), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<TrainingUnitData> filterTrainingUnitsForCourseEventDecisions(long regionId) throws BusinessException {
	if (regionId == FlagsEnum.ALL.getCode())
	    return searchTrainingUnitsData(null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	Set<Long> trainingUnitsIds = new HashSet<Long>();
	if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
	    trainingUnitsIds.add(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    trainingUnitsIds.add(RegionsEnum.ACADEMY.getCode());
	} else {
	    trainingUnitsIds.add(regionId);
	}
	return searchTrainingUnitsData(trainingUnitsIds.toArray(new Long[trainingUnitsIds.size()]), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<TrainingUnitData> getTrainingUnitsByRegionId(long regionId) throws BusinessException {
	return searchTrainingUnitsData(regionId == FlagsEnum.ALL.getCode() ? null : new Long[] { regionId }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<TrainingUnitData> getAllTrainingUnitsData() throws BusinessException {
	return searchTrainingUnitsData(null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<TrainingUnitData> getAllowedForNominationTrainingUnits(long allowedNominationRegionId) throws BusinessException {
	return searchTrainingUnitsData(null, allowedNominationRegionId, FlagsEnum.ALL.getCode());
    }

    public static TrainingUnitData getTrainingUnitById(long unitId) throws BusinessException {
	List<TrainingUnitData> result = searchTrainingUnitsData(null, FlagsEnum.ALL.getCode(), unitId);
	return result.size() > 0 ? result.get(0) : null;
    }

    public static TrainingUnitData getTrainingUnitDataByTransactionDetailId(long trainingTransactionDetailId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_TRANSACTION_DETAIL_ID", trainingTransactionDetailId);
	    List<TrainingUnitData> result = DataAccess.executeNamedQuery(TrainingUnitData.class, QueryNamesEnum.HCM_GET_TRAINING_UNIT_DATA_BY_TRAINING_TRANSACTION_DETAIL_ID.getCode(), qParams);
	    return result == null || result.size() == 0 ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<TrainingUnitData> searchTrainingUnitsData(Long[] regionsIds, long allowedNominationRegionId, long unitId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    if (regionsIds != null && regionsIds.length > 0) {
		qParams.put("P_TRAINING_UNITS_REGIONS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRAINING_UNITS_REGIONS_IDS", regionsIds);
	    } else {
		qParams.put("P_TRAINING_UNITS_REGIONS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRAINING_UNITS_REGIONS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_ALLOWED_NOMINATION_REGION_ID", allowedNominationRegionId == FlagsEnum.ALL.getCode() ? allowedNominationRegionId + "" : "%," + String.valueOf(allowedNominationRegionId) + '-' + '1' + ",%");
	    qParams.put("P_UNIT_ID", unitId);
	    return DataAccess.executeNamedQuery(TrainingUnitData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_UNITS_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /******************************************** Qualification Levels *******************************************/
    /*---------------------------Queries------------------------------*/
    public static String getQualificationLevelsDescStringByIdsString(String idsString) throws BusinessException {
	List<QualificationLevel> qualificationLevelsList = searchQualificationLevels(splitStringToLongArray(idsString));

	StringBuilder qualificationLevelsDesc = new StringBuilder();

	for (QualificationLevel qualLevel : qualificationLevelsList)
	    qualificationLevelsDesc.append(qualLevel.getDescription()).append(",");

	return qualificationLevelsDesc.deleteCharAt(qualificationLevelsDesc.length() - 1).toString();
    }

    public static List<QualificationLevel> getAllQualificationLevels() throws BusinessException {
	return searchQualificationLevels(null);
    }

    private static List<QualificationLevel> searchQualificationLevels(Long[] ids) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (ids != null && ids.length > 0) {
		qParams.put("P_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_IDS", ids);
	    } else {
		qParams.put("P_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(QualificationLevel.class, QueryNamesEnum.HCM_SEARCH_QUALIFICATION_LEVELS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /********************************* Qualification Major Specializations ***************************************/
    /*---------------------------Operations---------------------------*/
    public static void addQualificationMajorSpec(QualificationMajorSpec qualMajorSpec, CustomSession... useSession) throws BusinessException {
	validateAddUpdateQualificationMajorSpec(qualMajorSpec);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(qualMajorSpec, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    qualMajorSpec.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateQualificationMajorSpec(QualificationMajorSpec qualMajorSpec, CustomSession... useSession) throws BusinessException {
	validateAddUpdateQualificationMajorSpec(qualMajorSpec);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(qualMajorSpec, session);

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

    public static void deleteQualificationMajorSpec(QualificationMajorSpec qualMajorSpec, CustomSession... useSession) throws BusinessException {
	validateDeleteQualificationMajorSpec(qualMajorSpec);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(qualMajorSpec, session);

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
    private static void validateAddUpdateQualificationMajorSpec(QualificationMajorSpec qualMajorSpec) throws BusinessException {
	if (qualMajorSpec == null)
	    throw new BusinessException("error_transactionDataError");

	// check mandatory fields
	if (qualMajorSpec.getMilitaryClassificationFlag() == null)
	    throw new BusinessException("error_transactionDataError");

	if (qualMajorSpec.getDescription() == null || qualMajorSpec.getDescription().trim().isEmpty())
	    throw new BusinessException("error_qualificationSpecNameIsMandatory");

	// business rules
	if (checkExistQualificationMajorSpecName(qualMajorSpec.getMilitaryClassificationFlag(), qualMajorSpec.getDescription(), qualMajorSpec.getId() != null ? qualMajorSpec.getId() : FlagsEnum.ALL.getCode()))
	    throw new BusinessException("error_qualMajorSpecDuplicateName");

	if (qualMajorSpec.getId() != null)
	    checkUsageOfQualificationMajorSpec(qualMajorSpec);

    }

    private static void validateDeleteQualificationMajorSpec(QualificationMajorSpec qualMajorSpec) throws BusinessException {
	if (qualMajorSpec == null)
	    throw new BusinessException("error_transactionDataError");

	// prevent deletion if majorSpec has minorSpecs
	if (!getQualificationMinorSpecsByMajorId(qualMajorSpec.getId()).isEmpty())
	    throw new BusinessException("error_qualMajorSpecHasMinorSpecs");
    }

    /*---------------------------Queries------------------------------*/
    public static List<QualificationMajorSpec> getQualificationMajorSpecsByMinorSpec(int militaryClassificationFlag, String qualificationMinorSpecDesc, String qualificationMajorSpecDesc) throws BusinessException {
	return searchQualificationMinorSpecsAndMajorSpecs(militaryClassificationFlag, qualificationMinorSpecDesc, qualificationMajorSpecDesc);
    }

    private static List<QualificationMajorSpec> searchQualificationMinorSpecsAndMajorSpecs(int militaryClassificationFlag, String qualificationMinorSpecDesc, String qualificationMajorSpecDesc) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MILITARY_CLASSIFICATION_FLAG", militaryClassificationFlag);
	    qParams.put("P_QUAL_MINOR_SPEC_DESC", (qualificationMinorSpecDesc == null || qualificationMinorSpecDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : "%" + qualificationMinorSpecDesc + "%");
	    qParams.put("P_QUAL_MAJOR_SPEC_DESC", (qualificationMajorSpecDesc == null || qualificationMajorSpecDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : "%" + qualificationMajorSpecDesc + "%");
	    if (qualificationMinorSpecDesc != null && qualificationMinorSpecDesc.length() > 0)
		qParams.put("P_MINOR_INCLUDED_FLAG", FlagsEnum.ON.getCode());
	    else
		qParams.put("P_MINOR_INCLUDED_FLAG", FlagsEnum.OFF.getCode());

	    return DataAccess.executeNamedQuery(QualificationMajorSpec.class, QueryNamesEnum.HCM_SEARCH_QUALIFICATION_MAJOR_AND_MINOR_SPEC.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void checkUsageOfQualificationMajorSpec(QualificationMajorSpec qualMajorSpec) throws BusinessException {
	// check usage in course
	// check usage in employeeQualification
	// check in training transaction
	if (qualMajorSpec.getId() != null) {
	    if (TrainingCoursesService.getTrainingCoursesByNameAndQualification(FlagsEnum.ALL.getCode(), null, null, FlagsEnum.ALL.getCode(), qualMajorSpec.getId(), null).size() > 0)
		throw new BusinessException("error_courseQualIsUsedInTheSystem");
	    if (EmployeesService.checkUsageOfQualificationInEmployeeQualifications(qualMajorSpec.getId(), FlagsEnum.ALL.getCode()))
		throw new BusinessException("error_courseQualIsUsedInTheSystem");
	    if (TrainingEmployeesService.getTrainingTransactionsDataByQualificationMajorSpecId(qualMajorSpec.getId()).size() > 0)
		throw new BusinessException("error_courseQualIsUsedInTheSystem");

	}
    }

    private static boolean checkExistQualificationMajorSpecName(int militaryClassificationFlag, String qualificationMajorSpecDesc, long excludedId) throws BusinessException {
	return searchQualificationMajorSpecs(militaryClassificationFlag, qualificationMajorSpecDesc, excludedId, FlagsEnum.ON.getCode()).size() > 0;
    }

    public static List<QualificationMajorSpec> getQualificationMajorSpecs(int militaryClassificationFlag, String qualificationMajorSpecDesc, long excludedId) throws BusinessException {
	try {
	    return searchQualificationMajorSpecs(militaryClassificationFlag, qualificationMajorSpecDesc, excludedId, FlagsEnum.OFF.getCode());

	} catch (BusinessException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<QualificationMajorSpec> searchQualificationMajorSpecs(int militaryClassificationFlag, String qualificationMajorSpecDesc, long excludedId, int exactQualDescFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MILITARY_CLASSIFICATION_FLAG", militaryClassificationFlag);
	    if (exactQualDescFlag == FlagsEnum.OFF.getCode())
		qParams.put("P_QUAL_MAJOR_SPEC_DESC", (qualificationMajorSpecDesc == null || qualificationMajorSpecDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : '%' + qualificationMajorSpecDesc + '%');
	    else
		qParams.put("P_QUAL_MAJOR_SPEC_DESC", qualificationMajorSpecDesc);
	    qParams.put("P_EXACT_DESC_FLAG", exactQualDescFlag);
	    qParams.put("P_EXCLUDED_ID", excludedId);

	    return DataAccess.executeNamedQuery(QualificationMajorSpec.class, QueryNamesEnum.HCM_SEARCH_QUALIFICATION_MAJOR_SPEC.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /********************************* Qualification Minor Specializations ***************************************/
    /*---------------------------Operations---------------------------*/
    public static void addQualificationMinorSpec(QualificationMinorSpecData QualMinorSpecData, CustomSession... useSession) throws BusinessException {
	validateAddUpdateQualificationMinorSpec(QualMinorSpecData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(QualMinorSpecData.getQualificationMinoSpec(), session);

	    QualMinorSpecData.setId(QualMinorSpecData.getQualificationMinoSpec().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    QualMinorSpecData.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateQualificationMinorSpec(QualificationMinorSpecData QualMinorSpecData, CustomSession... useSession) throws BusinessException {
	validateAddUpdateQualificationMinorSpec(QualMinorSpecData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(QualMinorSpecData.getQualificationMinoSpec(), session);

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

    public static void deleteQualificationMinorSpec(QualificationMinorSpecData qualMinorSpecData, CustomSession... useSession) throws BusinessException {
	validateDeleteQualificationMinorSpec(qualMinorSpecData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(qualMinorSpecData.getQualificationMinoSpec(), session);

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
    private static void validateAddUpdateQualificationMinorSpec(QualificationMinorSpecData qualMinorSpec) throws BusinessException {
	if (qualMinorSpec == null)
	    throw new BusinessException("error_transactionDataError");

	// check mandatory fields
	if (qualMinorSpec.getQualificationMajorSpecId() == null)
	    throw new BusinessException("error_transactionDataError");

	if (qualMinorSpec.getDescription() == null || qualMinorSpec.getDescription().trim().isEmpty())
	    throw new BusinessException("error_qualificationSpecNameIsMandatory");

	// business rules
	if (checkExistQualificationMinorSpecName(qualMinorSpec.getMilitaryClassificationFlag(), qualMinorSpec.getDescription(), qualMinorSpec.getId() != null ? qualMinorSpec.getId() : FlagsEnum.ALL.getCode()))
	    throw new BusinessException("error_qualMinorSpecDuplicateName");

	if (qualMinorSpec.getId() != null)
	    checkUsageOfQualificationMinorSpec(qualMinorSpec);
    }

    private static void validateDeleteQualificationMinorSpec(QualificationMinorSpecData qualMinorSpec) throws BusinessException {
	if (qualMinorSpec == null)
	    throw new BusinessException("error_transactionDataError");

	// prevent deletion if minorSpec is used in the system
	checkUsageOfQualificationMinorSpec(qualMinorSpec);
    }

    private static void checkUsageOfQualificationMinorSpec(QualificationMinorSpecData qualMinorSpec) throws BusinessException {
	// check usage in course
	// check usage in employeeQualification
	// check in training transaction
	if (qualMinorSpec.getId() != null) {
	    if (TrainingCoursesService.getTrainingCoursesByNameAndQualification(FlagsEnum.ALL.getCode(), null, null, qualMinorSpec.getId(), FlagsEnum.ALL.getCode(), null).size() > 0)
		throw new BusinessException("error_courseQualIsUsedInTheSystem");
	    if (EmployeesService.checkUsageOfQualificationInEmployeeQualifications(FlagsEnum.ALL.getCode(), qualMinorSpec.getId()))
		throw new BusinessException("error_courseQualIsUsedInTheSystem");
	    if (TrainingEmployeesService.getTrainingTransactionsDataByQualificationMinorSpecId(qualMinorSpec.getId()).size() > 0)
		throw new BusinessException("error_courseQualIsUsedInTheSystem");
	}
    }

    /*---------------------------Queries------------------------------*/
    public static List<QualificationMinorSpecData> getQualificationMinorSpecsByMajorId(long qualificationMajorSpecId) throws BusinessException {
	return searchQualificationMinorSpecs(FlagsEnum.ALL.getCode(), null, qualificationMajorSpecId, null, FlagsEnum.ALL.getCode(), false);
    }

    public static List<QualificationMinorSpecData> getQualificationMinorSpecs(int militaryClassificationFlag, String description, String qualificationMajorSpecDesc) throws BusinessException {
	return searchQualificationMinorSpecs(militaryClassificationFlag, description, FlagsEnum.ALL.getCode(), qualificationMajorSpecDesc, FlagsEnum.ALL.getCode(), false);
    }

    private static boolean checkExistQualificationMinorSpecName(int militaryClassificationFlag, String qualificationMinorSpecDesc, long excludedId) throws BusinessException {
	return searchQualificationMinorSpecs(militaryClassificationFlag, qualificationMinorSpecDesc, FlagsEnum.ALL.getCode(), null, excludedId, true).size() > 0;
    }

    private static List<QualificationMinorSpecData> searchQualificationMinorSpecs(int militaryClassificationFlag, String description, long qualificationMajorSpecId, String qualificationMajorSpecDesc, long excludedId, boolean isExactDesc) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MILITARY_CLASSIFICATION_FLAG", militaryClassificationFlag);
	    if (isExactDesc)
		qParams.put("P_DESC", (description == null || description.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : description);
	    else
		qParams.put("P_DESC", (description == null || description.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : "%" + description + "%");
	    qParams.put("P_QUAL_MAJOR_SPEC_ID", qualificationMajorSpecId);
	    qParams.put("P_QUAL_MAJOR_SPEC_DESC", (qualificationMajorSpecDesc == null || qualificationMajorSpecDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : "%" + qualificationMajorSpecDesc + "%");
	    qParams.put("P_EXCLUDED_ID", excludedId);

	    return DataAccess.executeNamedQuery(QualificationMinorSpecData.class, QueryNamesEnum.HCM_SEARCH_QUALIFICATION_MINOR_SPECS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*************************************** Graduation Places **************************************************/
    /*---------------------------Operations------------------------------*/
    public static void addGraduationPlace(GraduationPlaceData graduationPlace, CustomSession... useSession) throws BusinessException {
	validateGraduationPlace(graduationPlace, null);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(graduationPlace.getGraduationPlace(), session);
	    graduationPlace.setId(graduationPlace.getGraduationPlace().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    graduationPlace.setId(null);
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateGraduationPlace(GraduationPlaceData graduationPlace, String originalGraduationPlaceDesc, CustomSession... useSession) throws BusinessException {
	validateGraduationPlace(graduationPlace, originalGraduationPlaceDesc);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(graduationPlace.getGraduationPlace(), session);

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

    public static void deleteGraduationPlace(GraduationPlaceData graduationPlace, CustomSession... useSession) throws BusinessException {
	if (graduationPlace == null)
	    throw new BusinessException("error_transactionDataError");
	// prevent deletion if graduation place has details attached to it
	if (getGraduationPlacesDetailsByGraduationPlaceId(graduationPlace.getId()).size() > 0)
	    throw new BusinessException("error_graduationPlaceHasDetails");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(graduationPlace.getGraduationPlace(), session);

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

    public static void addGraduationPlaceDetail(GraduationPlaceDetailData graduationPlaceDetail, CustomSession... useSession) throws BusinessException {
	validateGraduationPlaceDetail(graduationPlaceDetail, null);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(graduationPlaceDetail.getGraduationPlaceDetail(), session);
	    graduationPlaceDetail.setId(graduationPlaceDetail.getGraduationPlaceDetail().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    graduationPlaceDetail.setId(null);
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateGraduationPlaceDetail(GraduationPlaceDetailData graduationPlaceDetail, String originalGraduationPlaceDetailDesc, CustomSession... useSession) throws BusinessException {
	validateGraduationPlaceDetail(graduationPlaceDetail, originalGraduationPlaceDetailDesc);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(graduationPlaceDetail.getGraduationPlaceDetail(), session);

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

    public static void deleteGraduationPlaceDetail(GraduationPlaceDetailData graduationPlaceDetail, CustomSession... useSession) throws BusinessException {
	if (graduationPlaceDetail == null)
	    throw new BusinessException("error_transactionDataError");
	// prevent deletion in case of usage in the system
	validateUsageOfGraduationPlaceDetail(graduationPlaceDetail);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(graduationPlaceDetail.getGraduationPlaceDetail(), session);

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

    /*---------------------------Validation------------------------------*/
    private static void validateGraduationPlace(GraduationPlaceData graduationPlace, String originalGraduationPlaceDesc) throws BusinessException {
	if (graduationPlace == null)
	    throw new BusinessException("error_transactionDataError");

	// check mandatory
	if (graduationPlace.getType() == null)
	    throw new BusinessException("error_transactionDataError");
	if (graduationPlace.getCountryId() == null)
	    throw new BusinessException("error_transactionDataError");
	if (graduationPlace.getDescription() == null || graduationPlace.getDescription().trim().isEmpty())
	    throw new BusinessException("error_graduationPlaceDescIsMandatory");

	// business rules
	if (checkRepeatedGraduationPlaceNameAndCountry(graduationPlace.getDescription(), graduationPlace.getCountryId(), graduationPlace.getId() == null ? FlagsEnum.ALL.getCode() : graduationPlace.getId()))
	    throw new BusinessException("error_graduationPlaceDescIsUsed");

	if (graduationPlace.getId() != null && !graduationPlace.getDescription().equals(originalGraduationPlaceDesc))
	    validateUsageOfGraduationPlace(graduationPlace);
    }

    private static void validateUsageOfGraduationPlace(GraduationPlaceData graduationPlace) throws BusinessException {
	if (graduationPlace.getId() != null) {
	    if (EmployeesService.checkUsageOfGraduationPlaceInEmployeeQualifications(graduationPlace.getId(), FlagsEnum.ALL.getCode()))
		throw new BusinessException("error_graduationPlaceIsUsedInTheSystem");
	    if (TrainingEmployeesService.getTrainingTransactionsDataByGraduationPlaceInfo(graduationPlace.getId(), FlagsEnum.ALL.getCode()).size() > 0)
		throw new BusinessException("error_graduationPlaceIsUsedInTheSystem");
	}
    }

    private static void validateGraduationPlaceDetail(GraduationPlaceDetailData graduationPlaceDetail, String originalGraduationPlaceDetailDesc) throws BusinessException {
	if (graduationPlaceDetail == null)
	    throw new BusinessException("error_transactionDataError");

	// check mandatory
	if (graduationPlaceDetail.getGraduationPlaceId() == null)
	    throw new BusinessException("error_transactionDataError");
	if (graduationPlaceDetail.getDescription() == null || graduationPlaceDetail.getDescription().trim().isEmpty())
	    throw new BusinessException("error_graduationPlaceDescIsMandatory");
	if (graduationPlaceDetail.getAddress() == null || graduationPlaceDetail.getAddress().trim().isEmpty())
	    throw new BusinessException("error_graduationPlaceDetailAddressIsMandatory");

	// business rules
	if (checkRepeatedGraduationPlaceDetailNameUnderGraduationPlace(graduationPlaceDetail.getDescription(), graduationPlaceDetail.getGraduationPlaceId(), graduationPlaceDetail.getId() == null ? FlagsEnum.ALL.getCode() : graduationPlaceDetail.getId()))
	    throw new BusinessException("error_graduationPlaceDescIsUsed");

	if (graduationPlaceDetail.getId() != null && !graduationPlaceDetail.getDescription().equals(originalGraduationPlaceDetailDesc))
	    validateUsageOfGraduationPlaceDetail(graduationPlaceDetail);
    }

    private static void validateUsageOfGraduationPlaceDetail(GraduationPlaceDetailData graduationPlaceDetail) throws BusinessException {
	if (graduationPlaceDetail.getId() != null) {
	    if (EmployeesService.checkUsageOfGraduationPlaceInEmployeeQualifications(FlagsEnum.ALL.getCode(), graduationPlaceDetail.getId()))
		throw new BusinessException("error_graduationPlaceIsUsedInTheSystem");
	    if (TrainingEmployeesService.getTrainingTransactionsDataByGraduationPlaceInfo(FlagsEnum.ALL.getCode(), graduationPlaceDetail.getId()).size() > 0)
		throw new BusinessException("error_graduationPlaceIsUsedInTheSystem");
	}
    }

    /*---------------------------Queries------------------------------*/
    public static List<GraduationPlaceData> getGraduationPlacesData(String graduationPlaceDesc, String graduationPlaceCountryName, int graduationPlaceType) throws BusinessException {
	return searchGraduationPlacesData(FlagsEnum.ALL.getCode(), graduationPlaceDesc, graduationPlaceCountryName, FlagsEnum.ALL.getCode(), graduationPlaceType, FlagsEnum.ALL.getCode(), false);
    }

    public static GraduationPlaceData getGraduationPlaceDataById(long id) throws BusinessException {
	List<GraduationPlaceData> l = searchGraduationPlacesData(id, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), false);
	return l.size() != 0 ? l.get(0) : null;
    }

    private static boolean checkRepeatedGraduationPlaceNameAndCountry(String graduationPlaceDesc, long graduationPlaceCountryId, long excludedId) throws BusinessException {
	return searchGraduationPlacesData(FlagsEnum.ALL.getCode(), graduationPlaceDesc, null, graduationPlaceCountryId, FlagsEnum.ALL.getCode(), excludedId, true).size() > 0;
    }

    private static List<GraduationPlaceData> searchGraduationPlacesData(long graduationPlaceId, String graduationPlaceDesc, String graduationPlaceCountryName, long graduationPlaceCountryId, int graduationPlaceType, long excludedId, boolean stringExact) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_ID", graduationPlaceId);

	    if (stringExact) {
		qParams.put("P_DESC", (graduationPlaceDesc == null || graduationPlaceDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : graduationPlaceDesc);
		qParams.put("P_COUNTRY_NAME", (graduationPlaceCountryName == null || graduationPlaceCountryName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : graduationPlaceCountryName);
	    } else {
		qParams.put("P_DESC", (graduationPlaceDesc == null || graduationPlaceDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + graduationPlaceDesc + "%");
		qParams.put("P_COUNTRY_NAME", (graduationPlaceCountryName == null || graduationPlaceCountryName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + graduationPlaceCountryName + "%");
	    }

	    qParams.put("P_COUNTRY_ID", graduationPlaceCountryId);
	    qParams.put("P_TYPE", graduationPlaceType);
	    qParams.put("P_EXCLUDED_ID", excludedId);

	    return DataAccess.executeNamedQuery(GraduationPlaceData.class, QueryNamesEnum.HCM_SEARCH_GRADUATION_PLACES_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<GraduationPlaceDetailData> getGraduationPlacesDetails(String graduationPlaceDetailDesc, String graduationPlaceDesc, String graduationPlaceCountryName) throws BusinessException {
	return searchGraduationPlacesDetailsData(FlagsEnum.ALL.getCode(), graduationPlaceDetailDesc, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), graduationPlaceDesc, graduationPlaceCountryName, FlagsEnum.ALL.getCode(), false);
    }

    public static GraduationPlaceDetailData getGraduationPlaceDetailDataById(long id) throws BusinessException {
	List<GraduationPlaceDetailData> l = searchGraduationPlacesDetailsData(id, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, FlagsEnum.ALL.getCode(), false);
	return l.size() != 0 ? l.get(0) : null;
    }

    public static List<GraduationPlaceDetailData> getGraduationPlacesDetailsByGraduationPlaceId(long graduationPlaceId) throws BusinessException {
	return searchGraduationPlacesDetailsData(FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), graduationPlaceId, null, null, FlagsEnum.ALL.getCode(), false);
    }

    private static boolean checkRepeatedGraduationPlaceDetailNameUnderGraduationPlace(String graduationPlaceDetailDesc, long graduationPlaceId, long excludedId) throws BusinessException {
	return searchGraduationPlacesDetailsData(FlagsEnum.ALL.getCode(), graduationPlaceDetailDesc, FlagsEnum.ALL.getCode(), graduationPlaceId, null, null, excludedId, true).size() > 0;
    }

    private static List<GraduationPlaceDetailData> searchGraduationPlacesDetailsData(long id, String graduationPlaceDetailDesc, int graduationPlaceType, long graduationPlaceId, String graduationPlaceDesc, String graduationPlaceCountryName, long excludedId, boolean stringExact) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);

	    if (stringExact) {
		qParams.put("P_DESC", (graduationPlaceDetailDesc == null || graduationPlaceDetailDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : graduationPlaceDetailDesc);
		qParams.put("P_GRADUATION_PLACE_DESC", (graduationPlaceDesc == null || graduationPlaceDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : graduationPlaceDesc);
		qParams.put("P_GRADUATION_PLACE_COUNTRY_NAME", (graduationPlaceCountryName == null || graduationPlaceCountryName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : graduationPlaceCountryName);
	    } else {
		qParams.put("P_DESC", (graduationPlaceDetailDesc == null || graduationPlaceDetailDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + graduationPlaceDetailDesc + "%");
		qParams.put("P_GRADUATION_PLACE_DESC", (graduationPlaceDesc == null || graduationPlaceDesc.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + graduationPlaceDesc + "%");
		qParams.put("P_GRADUATION_PLACE_COUNTRY_NAME", (graduationPlaceCountryName == null || graduationPlaceCountryName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + graduationPlaceCountryName + "%");
	    }

	    qParams.put("P_GRADUATION_PLACE_ID", graduationPlaceId);
	    qParams.put("P_GRADUATION_PLACE_TYPE", graduationPlaceType);
	    qParams.put("P_EXCLUDED_ID", excludedId);

	    return DataAccess.executeNamedQuery(GraduationPlaceDetailData.class, QueryNamesEnum.HCM_SEARCH_GRADUATION_PLACES_DETAILS_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /******************************************* Annual Parties **************************************************/
    /*---------------------------Operations---------------------------*/
    public static void addAnnualParty(TrainingAnnualPartyData trainingAnnualPartyData, CustomSession... useSession) throws BusinessException {
	if (trainingAnnualPartyData == null)
	    throw new BusinessException("error_transactionDataError");

	if (checkExistingExternalAnnualParty(trainingAnnualPartyData.getTrainingUnitId(), trainingAnnualPartyData.getExternalPartyId())) {
	    throw new BusinessException("error_annualPartyAlreadyExists");
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(trainingAnnualPartyData.getTrainingAnnualParty(), session);

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

    public static void deleteAnnualParty(TrainingAnnualPartyData trainingAnnualPartyData, CustomSession... useSession) throws BusinessException {
	if (trainingAnnualPartyData == null)
	    throw new BusinessException("error_transactionDataError");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(trainingAnnualPartyData.getTrainingAnnualParty(), session);

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

    /*---------------------------Queries------------------------------*/
    /**
     * Wrapper for {@link #searchAnnualPartyData(long,long)} to get all annual party according to partyId
     * 
     * @param externalPartyId
     * @return
     * @throws BusinessException
     */
    public static List<TrainingAnnualPartyData> getAnnualPartiesDataByTrainingUnitId(long trainingUnitID) throws BusinessException {
	return searchAnnualPartyData(trainingUnitID, FlagsEnum.ALL.getCode());
    }

    private static boolean checkExistingExternalAnnualParty(long trainingUnitID, long externalPartyId) throws BusinessException {
	return searchAnnualPartyData(trainingUnitID, externalPartyId).size() > 0;
    }

    private static List<TrainingAnnualPartyData> searchAnnualPartyData(long trainingUnitID, long externalPartyId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_EXTERNAL_PARTY_ID", externalPartyId);
	    qParams.put("P_TRAINING_UNIT_ID", trainingUnitID);

	    return DataAccess.executeNamedQuery(TrainingAnnualPartyData.class, QueryNamesEnum.HCM_SEARCH_TRAINING_ANNUAL_PARTIES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /***************************** Annual Military Courses *****************************/
    /*---------------------------Operations---------------------------*/
    public static void addAnnualMilitaryCourseData(TrainingAnnualCourseData annualMilitaryCourseData, CustomSession... useSession) throws BusinessException {
	if (annualMilitaryCourseData == null)
	    throw new BusinessException("error_transactionDataError");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(annualMilitaryCourseData.getTrainingAnnualCourse(), session);
	    annualMilitaryCourseData.setId(annualMilitaryCourseData.getTrainingAnnualCourse().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    annualMilitaryCourseData.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deleteAnnualMilitaryCourseData(TrainingAnnualCourseData annualMilitaryCourseData, CustomSession... useSession) throws BusinessException {
	if (annualMilitaryCourseData == null)
	    throw new BusinessException("error_transactionDataError");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(annualMilitaryCourseData.getTrainingAnnualCourse(), session);

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

    /*---------------------------Queries------------------------------*/
    public static List<TrainingAnnualCourseData> getAllAnnualMilitaryCourseDataByTrainingUnitId(long trainingUnitId) throws BusinessException {
	return searchAnnualMilitaryCourseData(trainingUnitId);
    }

    private static List<TrainingAnnualCourseData> searchAnnualMilitaryCourseData(long trainingUnitId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TRAINING_UNIT_ID", trainingUnitId);
	    return DataAccess.executeNamedQuery(TrainingAnnualCourseData.class, QueryNamesEnum.HCM_SEARCH_ANNUAL_COURSES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /******************************************* Success Rankings **************************************************/
    /*---------------------------Queries------------------------------*/
    public static List<Rankings> getRankings(long rankingId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", rankingId);
	    return DataAccess.executeNamedQuery(Rankings.class, QueryNamesEnum.HCM_RANKINGS_SEARCH_RANKINGS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
