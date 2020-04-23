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
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedData;
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.ManpowerNeedStatusEnum;
import com.code.enums.ManpowerNeedTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

/**
 * Provides methods for handling the manpower needs requests and studies.
 * 
 */
public class ManpowerNeedsService extends BaseService {

    /**
     * Constructs the manpower needs service.
     */
    private ManpowerNeedsService() {
    }

    /************************************ Manpower Needs Request *****************************************************/

    public static void saveManpowerNeedRequest(ManpowerNeedData manpowerNeedRequestData, List<ManpowerNeedDetailData> manpowerNeedDetailsData, long userId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    manpowerNeedRequestData.setStatus(ManpowerNeedStatusEnum.UNDER_PROCESSING.getCode());
	    manpowerNeedRequestData.getManpowerNeed().setSystemUser(userId + ""); // For Audit.
	    DataAccess.addEntity(manpowerNeedRequestData.getManpowerNeed(), session);

	    manpowerNeedRequestData.setId(manpowerNeedRequestData.getManpowerNeed().getId());

	    for (ManpowerNeedDetailData manpowerNeedDetailData : manpowerNeedDetailsData) {
		manpowerNeedDetailData.setManpowerNeedId(manpowerNeedRequestData.getId());
		manpowerNeedDetailData.setRequestReasons(manpowerNeedDetailData.getRequestReasons().trim());
		manpowerNeedDetailData.getManpowerNeedDetail().setSystemUser(userId + ""); // For Audit.
		DataAccess.addEntity(manpowerNeedDetailData.getManpowerNeedDetail(), session);

		manpowerNeedDetailData.setId(manpowerNeedDetailData.getManpowerNeedDetail().getId());
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

    public static void updateManpowerNeedRequestSuggestedCounts(List<ManpowerNeedDetailData> manpowerNeedDetailsData, Long userId, CustomSession session) throws BusinessException {
	try {
	    for (ManpowerNeedDetailData manpowerNeedDetailData : manpowerNeedDetailsData) {
		manpowerNeedDetailData.getManpowerNeedDetail().setSystemUser(userId + ""); // For Audit.
		DataAccess.updateEntity(manpowerNeedDetailData.getManpowerNeedDetail(), session); // to set the suggested count
	    }
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void validateManpowerNeedRequest(ManpowerNeedData manpowerNeedRequestData, List<ManpowerNeedDetailData> manpowerNeedDetailsData) throws BusinessException {
	if (manpowerNeedDetailsData.isEmpty())
	    throw new BusinessException("error_emptyManpowerNeedDetails");

	Set<String> hashSet = new HashSet<String>();
	List<Long> minorSpecsIds = new ArrayList<Long>();

	for (ManpowerNeedDetailData manpowerNeedDetailData : manpowerNeedDetailsData) {
	    // the unit is mandatory
	    if (manpowerNeedDetailData.getUnitId() == null)
		throw new BusinessException("error_missingUnit");

	    // the minor specialization is mandatory
	    if (manpowerNeedDetailData.getMinorSpecializationId() == null)
		throw new BusinessException("error_minorSpecializationMandatory");

	    // the unit should be unique with the minor specialization
	    if (!hashSet.add(manpowerNeedDetailData.getUnitId().longValue() + "," + manpowerNeedDetailData.getMinorSpecializationId().longValue()))
		throw new BusinessException("error_repeatedUnitWithMinorSpecialization");

	    minorSpecsIds.add(manpowerNeedDetailData.getMinorSpecializationId());

	    // the required count is mandatory and should be greater than zero
	    if (manpowerNeedDetailData.getRequiredCount() == null || manpowerNeedDetailData.getRequiredCount().intValue() <= 0)
		throw new BusinessException("error_requiredCountMandatoryAndGreaterThanZero");

	    // the request reasons is mandatory
	    if (manpowerNeedDetailData.getRequestReasons() == null || manpowerNeedDetailData.getRequestReasons().trim().isEmpty())
		throw new BusinessException("error_reasonsMandatory");

	    // the user can make a manpower need request for his physical unit or its children, or the units that he has privilege on it
	    if (!manpowerNeedDetailData.getUnitHKey().startsWith(UnitsService.getHKeyPrefix(manpowerNeedRequestData.getRequestingUnitHKey())))
		throw new BusinessException("error_requesterMustRequestNeedForHisUnitOrItsChildren");
	}

	validateMinorSpecAvailabilityInRequestingUnit(manpowerNeedRequestData.getRequestingUnitHKey(), minorSpecsIds.toArray(new Long[0]));
    }

    public static void validateMinorSpecAvailabilityInRequestingUnit(String requestingUnitHKey, Long[] minorSpecsIds) throws BusinessException {
	if (JobsService.getJobsCountByUnitHKeyAndMinorSpecsIds(requestingUnitHKey, minorSpecsIds).size() != minorSpecsIds.length)
	    throw new BusinessException("error_manpowerNeedRequestingUnitHierarchyNotHaveMinorSpec");
    }

    public static void validateManpowerNeedRequestSuggestedCounts(List<ManpowerNeedDetailData> manpowerNeedDetailsData) throws BusinessException {
	for (ManpowerNeedDetailData manpowerNeedDetailData : manpowerNeedDetailsData) {
	    // the request suggested count is mandatory and should be greater than or equal zero
	    if (manpowerNeedDetailData.getRequestSuggestedCount() == null || manpowerNeedDetailData.getRequestSuggestedCount().intValue() < 0)
		throw new BusinessException("error_suggestedCountMandatory");
	}
    }

    /************************************ Manpower Needs Study *******************************************************/

    public static void saveManpowerNeedStudy(ManpowerNeedData manpowerNeedStudyData, List<ManpowerNeedData> manpowerNeedRequestsAndStudies, Map<Long, List<ManpowerNeedDetailData>> manpowerNeedRequestsAndStudiesDetailsMap, List<ManpowerNeedDetailData> manpowerNeedStudyDetails, long userId, CustomSession... useSession) throws BusinessException {

	validateManpowerNeedStudy(manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails);

	if (manpowerNeedStudyData.getId() == null)
	    validateManpowerNeedsRunningStudies(manpowerNeedStudyData);

	List<ManpowerNeedDetailData> addedManpowerNeedStudyDetails = new ArrayList<ManpowerNeedDetailData>();
	boolean isNewManpowerNeedStudy = false;

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (manpowerNeedStudyData.getId() == null) {
		// the study object: add it if it's not added yet
		manpowerNeedStudyData.setStatus(ManpowerNeedStatusEnum.NOT_SUBMITTED.getCode());
		manpowerNeedStudyData.getManpowerNeed().setSystemUser(userId + ""); // For Audit.
		DataAccess.addEntity(manpowerNeedStudyData.getManpowerNeed(), session);

		manpowerNeedStudyData.setId(manpowerNeedStudyData.getManpowerNeed().getId());
		isNewManpowerNeedStudy = true;
	    }

	    // the requests and studies of the study object: update their study id
	    for (ManpowerNeedData manpowerNeed : manpowerNeedRequestsAndStudies) {
		manpowerNeed.setStudyId(manpowerNeedStudyData.getId()); // set the study id
		manpowerNeed.getManpowerNeed().setSystemUser(userId + ""); // For Audit.
		DataAccess.updateEntity(manpowerNeed.getManpowerNeed(), session);
	    }

	    // the details of all requests: update the suggested counts and the reasons
	    for (Map.Entry<Long, List<ManpowerNeedDetailData>> entry : manpowerNeedRequestsAndStudiesDetailsMap.entrySet()) {
		for (ManpowerNeedDetailData manpowerNeedDetailData : entry.getValue()) {
		    manpowerNeedDetailData.getManpowerNeedDetail().setSystemUser(userId + ""); // For Audit.
		    DataAccess.updateEntity(manpowerNeedDetailData.getManpowerNeedDetail(), session); // TODO mark as updated
		}
	    }

	    // the added on the study details
	    for (ManpowerNeedDetailData manpowerNeedDetailData : manpowerNeedStudyDetails) {
		manpowerNeedDetailData.getManpowerNeedDetail().setSystemUser(userId + ""); // For Audit.
		if (manpowerNeedDetailData.getId() == null) {
		    manpowerNeedDetailData.setManpowerNeedId(manpowerNeedStudyData.getId());
		    DataAccess.addEntity(manpowerNeedDetailData.getManpowerNeedDetail(), session);
		    manpowerNeedDetailData.setId(manpowerNeedDetailData.getManpowerNeedDetail().getId());
		    addedManpowerNeedStudyDetails.add(manpowerNeedDetailData);
		} else
		    DataAccess.updateEntity(manpowerNeedDetailData.getManpowerNeedDetail(), session); // TODO mark as updated
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {

	    if (isNewManpowerNeedStudy)
		manpowerNeedStudyData.setId(null);
	    for (ManpowerNeedDetailData manpowerNeedDetailData : addedManpowerNeedStudyDetails) {
		manpowerNeedDetailData.setId(null);
	    }

	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void disjoinManpowerNeedRequestOrStudy(List<ManpowerNeedData> manpowerNeedRequestsAndStudies, ManpowerNeedData manpowerNeedData, long userId, CustomSession... useSession) throws BusinessException {

	validateDisjoinManpowerNeedRequestOrStudy(manpowerNeedRequestsAndStudies, manpowerNeedData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    manpowerNeedData.setStudyId(null); // nullify the study id
	    manpowerNeedData.getManpowerNeed().setSystemUser(userId + ""); // For Audit.
	    DataAccess.updateEntity(manpowerNeedData.getManpowerNeed(), session);

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

    public static void deleteManpowerNeedStudyDetail(ManpowerNeedDetailData manpowerNeedStudyDetailData, long userId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    manpowerNeedStudyDetailData.getManpowerNeedDetail().setSystemUser(userId + ""); // For Audit.
	    DataAccess.deleteEntity(manpowerNeedStudyDetailData.getManpowerNeedDetail(), session);

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

    public static void deleteOrRejectManpowerNeedStudy(ManpowerNeedData manpowerNeedStudyData, boolean deleteFlag, CustomSession... useSession) throws BusinessException {

	if (deleteFlag)
	    validateDeleteStudy(manpowerNeedStudyData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // store the requests IDs of the study to be deleted to get their details to update their suggested counts
	    // and if the study to be deleted is a main study, then store also its studies IDs to get their requests and details to update their suggested counts
	    List<Long> manpowerNeedRequestsAndStudiesIds = new ArrayList<Long>();

	    if (deleteFlag)
		manpowerNeedRequestsAndStudiesIds.add(manpowerNeedStudyData.getId());

	    // if the study to be deleted is a main study, then store its studies IDs to get their requests and details
	    List<Long> manpowerNeedStudiesIds = new ArrayList<Long>();

	    // Load the (main) study requests (and studies) to Nullify their study id
	    List<ManpowerNeedData> manpowerNeedRequestsAndStudiesData = getManpowerNeedsDataByStudyId(manpowerNeedStudyData.getId());
	    for (ManpowerNeedData manpowerNeedRequestOrStudyData : manpowerNeedRequestsAndStudiesData) {
		manpowerNeedRequestOrStudyData.setStudyId(null);
		DataAccess.updateEntity(manpowerNeedRequestOrStudyData.getManpowerNeed(), session);

		manpowerNeedRequestsAndStudiesIds.add(manpowerNeedRequestOrStudyData.getId());

		if (ManpowerNeedTypesEnum.STUDY.getCode() == manpowerNeedRequestOrStudyData.getNeedType().intValue())
		    manpowerNeedStudiesIds.add(manpowerNeedRequestOrStudyData.getId());
	    }

	    if (manpowerNeedStudiesIds != null && !manpowerNeedStudiesIds.isEmpty()) {
		List<ManpowerNeedData> manpowerNeedRequestsData = getManpowerNeedsDataByStudiesIds(manpowerNeedStudiesIds.toArray(new Long[0]));
		for (ManpowerNeedData manpowerNeedRequest : manpowerNeedRequestsData) {
		    manpowerNeedRequestsAndStudiesIds.add(manpowerNeedRequest.getId());
		}
	    }

	    List<ManpowerNeedDetailData> manpowerNeedRequestsAndStudiesDetailsData = getManpowerNeedDetailsDataByManpowerNeedsIds(manpowerNeedRequestsAndStudiesIds.toArray(new Long[0]));
	    for (ManpowerNeedDetailData manpowerNeedRequestsAndStudiesDetailData : manpowerNeedRequestsAndStudiesDetailsData) {
		if (deleteFlag && manpowerNeedStudyData.getId().equals(manpowerNeedRequestsAndStudiesDetailData.getManpowerNeedId())) {
		    // Delete the added on (main) study details
		    DataAccess.deleteEntity(manpowerNeedRequestsAndStudiesDetailData.getManpowerNeedDetail(), session);
		} else {
		    if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType().intValue()) {
			manpowerNeedRequestsAndStudiesDetailData.setMainStudySuggestedCount(null);
			manpowerNeedRequestsAndStudiesDetailData.setMainStudyReasons(null);
		    } else {
			manpowerNeedRequestsAndStudiesDetailData.setStudySuggestedCount(null);
			manpowerNeedRequestsAndStudiesDetailData.setStudyReasons(null);
		    }
		    DataAccess.updateEntity(manpowerNeedRequestsAndStudiesDetailData.getManpowerNeedDetail(), session);
		}
	    }

	    // Delete the (main) study object
	    if (deleteFlag)
		DataAccess.deleteEntity(manpowerNeedStudyData.getManpowerNeed(), session);

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

    private static void validateManpowerNeedStudy(ManpowerNeedData manpowerNeedStudyData, List<ManpowerNeedData> manpowerNeedRequestsAndStudies, Map<Long, List<ManpowerNeedDetailData>> manpowerNeedRequestsAndStudiesDetailsMap, List<ManpowerNeedDetailData> manpowerNeedStudyDetails) throws BusinessException {

	if ((manpowerNeedRequestsAndStudies == null || manpowerNeedRequestsAndStudies.isEmpty())
		&& (manpowerNeedStudyDetails == null || manpowerNeedStudyDetails.isEmpty()))
	    throw new BusinessException("error_emptyManpowerNeeds");

	for (Map.Entry<Long, List<ManpowerNeedDetailData>> entry : manpowerNeedRequestsAndStudiesDetailsMap.entrySet()) {
	    for (ManpowerNeedDetailData manpowerNeedDetailData : entry.getValue()) {
		if (ManpowerNeedTypesEnum.STUDY.getCode() == manpowerNeedStudyData.getNeedType()) {
		    // the study suggested count is mandatory and should be greater than or equal zero
		    if (manpowerNeedDetailData.getStudySuggestedCount() == null || manpowerNeedDetailData.getStudySuggestedCount().intValue() < 0)
			throw new BusinessException("error_suggestedCountMandatory");
		} else if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType()) {
		    // the main study suggested count is mandatory and should be greater than or equal zero
		    if (manpowerNeedDetailData.getMainStudySuggestedCount() == null || manpowerNeedDetailData.getMainStudySuggestedCount().intValue() < 0)
			throw new BusinessException("error_suggestedCountMandatory");
		}
	    }
	}

	Set<String> hashSet = new HashSet<String>();

	for (ManpowerNeedDetailData manpowerNeedStudyDetail : manpowerNeedStudyDetails) {

	    // the unit is mandatory
	    if (manpowerNeedStudyDetail.getUnitId() == null)
		throw new BusinessException("error_missingUnit");

	    // the minor specialization is mandatory
	    if (manpowerNeedStudyDetail.getMinorSpecializationId() == null)
		throw new BusinessException("error_minorSpecializationMandatory");

	    // the unit should be unique with the minor specialization
	    if (!hashSet.add(manpowerNeedStudyDetail.getUnitId().longValue() + "," + manpowerNeedStudyDetail.getMinorSpecializationId().longValue()))
		throw new BusinessException("error_repeatedUnitWithMinorSpecialization");

	    if (ManpowerNeedTypesEnum.STUDY.getCode() == manpowerNeedStudyData.getNeedType()) {
		// the study suggested count is mandatory and should be greater than or equal zero
		if (manpowerNeedStudyDetail.getStudySuggestedCount() == null || manpowerNeedStudyDetail.getStudySuggestedCount().intValue() < 0)
		    throw new BusinessException("error_suggestedCountMandatory");
		// the study reasons is mandatory
		if (manpowerNeedStudyDetail.getStudyReasons() == null || manpowerNeedStudyDetail.getStudyReasons().isEmpty())
		    throw new BusinessException("error_reasonsMandatory");
	    } else if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType()) {
		// the main study suggested count is mandatory and should be greater than or equal zero
		if (manpowerNeedStudyDetail.getMainStudySuggestedCount() == null || manpowerNeedStudyDetail.getMainStudySuggestedCount().intValue() < 0)
		    throw new BusinessException("error_suggestedCountMandatory");
		// the main study reasons is mandatory
		if (manpowerNeedStudyDetail.getMainStudyReasons() == null || manpowerNeedStudyDetail.getMainStudyReasons().isEmpty())
		    throw new BusinessException("error_reasonsMandatory");
	    }
	}
    }

    private static void validateManpowerNeedsRunningStudies(ManpowerNeedData manpowerNeedStudyData) throws BusinessException {
	if (countUnderProcessingStudies(manpowerNeedStudyData.getRequestingRegionId(), manpowerNeedStudyData.getCategoryId()) > 0) {
	    if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType().intValue())
		throw new BusinessException("error_ThereIsAnotherUnderProcessingMainStudy");
	    else
		throw new BusinessException("error_ThereIsAnotherUnderProcessingStudyAtTheSameRegion");
	}
    }

    private static void validateDisjoinManpowerNeedRequestOrStudy(List<ManpowerNeedData> manpowerNeedRequestsAndStudies, ManpowerNeedData manpowerNeedData) throws BusinessException {
	for (ManpowerNeedData manpowerNeedDataObject : manpowerNeedRequestsAndStudies) {
	    if (manpowerNeedDataObject.getStudyId() != null && !manpowerNeedDataObject.getId().equals(manpowerNeedData.getId()))
		return;
	}
	throw new BusinessException("error_emptyManpowerNeeds");
    }

    private static void validateDeleteStudy(ManpowerNeedData manpowerNeedStudyData) throws BusinessException {
	if (ManpowerNeedStatusEnum.NOT_SUBMITTED.getCode() != manpowerNeedStudyData.getStatus().intValue())
	    throw new BusinessException("error_CannotDeleteSubmittedStudy");
    }

    /************************************ Commons ********************************************************************/

    public static void updateManpowerNeedData(ManpowerNeedData manpowerNeedData, Integer status, Long approvedId, Long decisionApprovedId, long userId, CustomSession session) throws BusinessException {
	try {
	    manpowerNeedData.setStatus(status);
	    manpowerNeedData.setApprovedId(approvedId);
	    manpowerNeedData.setDecisionApprovedId(decisionApprovedId);
	    manpowerNeedData.getManpowerNeed().setSystemUser(userId + ""); // For Audit.

	    DataAccess.updateEntity(manpowerNeedData.getManpowerNeed(), session);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void updateAttachments(ManpowerNeedData manpowerNeedData, CustomSession session) throws BusinessException {
	try {
	    DataAccess.updateEntity(manpowerNeedData.getManpowerNeed(), session);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /************************************ Inquiry Methods ************************************************************/

    // used in mini search
    public static List<ManpowerNeedData> getManpowerNeedsRequestsAndStudiesData(long regionId, long categoryId, Date transactionDateFrom, Date transactionDateTo, Long[] manpowerNeedsIdsToExclude) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_CATEGORY_ID", categoryId);

	    if (transactionDateFrom != null) {
		qParams.put("P_TRANSACTION_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRANSACTION_DATE_FROM", HijriDateService.getHijriDateString(transactionDateFrom));
	    } else {
		qParams.put("P_TRANSACTION_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRANSACTION_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (transactionDateTo != null) {
		qParams.put("P_TRANSACTION_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TRANSACTION_DATE_TO", HijriDateService.getHijriDateString(transactionDateTo));
	    } else {
		qParams.put("P_TRANSACTION_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TRANSACTION_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    if (manpowerNeedsIdsToExclude != null && manpowerNeedsIdsToExclude.length > 0) {
		qParams.put("P_MANPOWER_NEEDS_EXCLUDED_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_MANPOWER_NEEDS_EXCLUDED_IDS", manpowerNeedsIdsToExclude);
	    } else {
		qParams.put("P_MANPOWER_NEEDS_EXCLUDED_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_MANPOWER_NEEDS_EXCLUDED_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(ManpowerNeedData.class, QueryNamesEnum.HCM_GET_MANPOWER_NEED_REQUESTS_AND_STUDIES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // used in inquiry screen
    public static List<ManpowerNeedData> getManpowerNeedsStudiesData(long regionId, long categoryId, Date studyDateFrom, Date studyDateTo) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_CATEGORY_ID", categoryId);

	    if (studyDateFrom != null) {
		qParams.put("P_STUDY_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STUDY_DATE_FROM", HijriDateService.getHijriDateString(studyDateFrom));
	    } else {
		qParams.put("P_STUDY_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STUDY_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (studyDateTo != null) {
		qParams.put("P_STUDY_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STUDY_DATE_TO", HijriDateService.getHijriDateString(studyDateTo));
	    } else {
		qParams.put("P_STUDY_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STUDY_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    return DataAccess.executeNamedQuery(ManpowerNeedData.class, QueryNamesEnum.HCM_GET_MANPOWER_NEED_STUDIES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // used in request and study screens
    public static ManpowerNeedData getManpowerNeedDataById(long manpowerNeedId) throws BusinessException {
	List<ManpowerNeedData> result = searchManpowerNeedsDataByIds(new Long[] { manpowerNeedId });
	if (result == null || result.size() == 0)
	    return null;
	return result.get(0);
    }

    // used in study screen
    public static List<ManpowerNeedData> getManpowerNeedsDataByIdsString(String ManpowerNeedDataIdsString) throws BusinessException {
	List<ManpowerNeedData> ManpowerNeedDataList = new ArrayList<ManpowerNeedData>();
	if (ManpowerNeedDataIdsString != null && ManpowerNeedDataIdsString.length() > 0) {
	    String[] manpowerNeedDataIdsStrings = ManpowerNeedDataIdsString.split(",");
	    Long[] manpowerNeedDataIds = new Long[manpowerNeedDataIdsStrings.length];
	    for (int i = 0; i < manpowerNeedDataIdsStrings.length; i++)
		manpowerNeedDataIds[i] = Long.parseLong(manpowerNeedDataIdsStrings[i]);

	    ManpowerNeedDataList = searchManpowerNeedsDataByIds(manpowerNeedDataIds);
	}
	return ManpowerNeedDataList;
    }

    // used in the delete method only
    private static List<ManpowerNeedData> searchManpowerNeedsDataByIds(Long[] manpowerNeedDataIds) throws BusinessException {
	try {
	    if (manpowerNeedDataIds == null || manpowerNeedDataIds.length == 0)
		return new ArrayList<ManpowerNeedData>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MANPOWER_NEEDS_IDS", manpowerNeedDataIds);

	    return DataAccess.executeNamedQuery(ManpowerNeedData.class, QueryNamesEnum.HCM_GET_MANPOWER_NEEDS_BY_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // used in study screen and in the delete method
    public static List<ManpowerNeedData> getManpowerNeedsDataByStudyId(long studyId) throws BusinessException {
	return searchManpowerNeedsDataByStudiesIds(new Long[] { studyId });
    }

    // used in the delete method only
    private static List<ManpowerNeedData> getManpowerNeedsDataByStudiesIds(Long[] studiesIds) throws BusinessException {
	return searchManpowerNeedsDataByStudiesIds(studiesIds);
    }

    // used in the delete method only
    private static List<ManpowerNeedData> searchManpowerNeedsDataByStudiesIds(Long[] studiesIds) throws BusinessException {
	try {
	    if (studiesIds == null || studiesIds.length == 0)
		return new ArrayList<ManpowerNeedData>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_STUDIES_IDS", studiesIds);

	    return DataAccess.executeNamedQuery(ManpowerNeedData.class, QueryNamesEnum.HCM_GET_MANPOWER_NEEDS_BY_STUDIES_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // used in request and study screens
    public static List<ManpowerNeedDetailData> getManpowerNeedDetailsDataByManpowerNeedId(long manpowerNeedId) throws BusinessException {
	return searchManpowerNeedDetailsDataByManpowerNeedsIds(new Long[] { manpowerNeedId });
    }

    // used in the study screen and the delete method
    public static List<ManpowerNeedDetailData> getManpowerNeedDetailsDataByManpowerNeedsIds(Long[] manpowerNeedsDataIds) throws BusinessException {
	return searchManpowerNeedDetailsDataByManpowerNeedsIds(manpowerNeedsDataIds);
    }

    // used in the delete method only
    private static List<ManpowerNeedDetailData> searchManpowerNeedDetailsDataByManpowerNeedsIds(Long[] manpowerNeedsDataIds) throws BusinessException {
	try {
	    if (manpowerNeedsDataIds == null || manpowerNeedsDataIds.length == 0)
		return new ArrayList<ManpowerNeedDetailData>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MANPOWER_NEEDS_IDS", manpowerNeedsDataIds);

	    return DataAccess.executeNamedQuery(ManpowerNeedDetailData.class, QueryNamesEnum.HCM_GET_MANPOWER_NEED_DETAILS_DATA_BY_MANPOWER_NEEDS_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long countUnderProcessingStudies(long requestingRegionId, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REQUESTING_REGION_ID", requestingRegionId);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_UNDER_PROCESSING_MANPOWER_NEED_DATA_STUDIES.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /************************************ Reports ********************************************************************/

    public static byte[] getManpowerNeedsRequestsReportsBytes(int reportType, long categoryId, String requestingUnitHKeyPrefix, int allRequestsFlag, String allRequestsFlagDesc, Date fromDate, Date toDate) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    if (reportType == 10) {
		reportName = ReportNamesEnum.JOBS_MANPOWER_NEEDS_REQUESTS.getCode();
		parameters.put("P_CATEGORY_ID", categoryId);
		parameters.put("P_REQ_UNIT_HKEY_PREFIX", requestingUnitHKeyPrefix);
		parameters.put("P_ALL_REQUESTS_FLAG", allRequestsFlag);
		parameters.put("P_ALL_REQUESTS_FLAG_DESC", allRequestsFlagDesc);
		parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
		parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    }

	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getManpowerNeedsStudiesReportsBytes(int reportType, long studyId, int studyNeedType, Date studyDate, long categoryId, long regionId, String regionDesc, String minorSpecsIds, String minorSpecsDescs) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    if (reportType == 10)
		reportName = ReportNamesEnum.JOBS_MANPOWER_NEEDS_STUDIES_SUMMARY.getCode();
	    else if (reportType == 20)
		reportName = ReportNamesEnum.JOBS_MANPOWER_NEEDS_STUDIES_DETAILED.getCode();

	    parameters.put("P_STUDY_ID", studyId);
	    parameters.put("P_STUDY_NEED_TYPE", studyNeedType); // it was needed to check whether to read from study suggested column or main study suggested column
	    parameters.put("P_STUDY_DATE", HijriDateService.getHijriDateString(studyDate));
	    parameters.put("P_CATEGODY_ID", categoryId);
	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_REGION_DESC", regionDesc);
	    parameters.put("P_MINOR_SPECS_IDS", minorSpecsIds.isEmpty() ? FlagsEnum.ALL.getCode() + "" : minorSpecsIds);
	    parameters.put("P_MINOR_SPECS_DESCS", minorSpecsDescs);

	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }
}