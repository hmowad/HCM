package com.code.services.hcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.specializations.MinorSpecializationData;
import com.code.dal.orm.hcm.specializations.MinorSpecializationDescriptionData;
import com.code.dal.orm.hcm.specializations.MinorSpecializationDescriptionDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.JobTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

/**
 * The class <code>SpecializationsService</code> provides methods for handling all the specializations like major specialization, minor specialization and minor specialization description.
 * 
 */
public class SpecializationsService extends BaseService {

    /**
     * Constructs a newly allocated <code>SpecializationsService</code> object.
     */
    private SpecializationsService() {
    }

    /*---------------------------------------------- Minor Specialization --------------------------------------------------------*/

    public static List<MinorSpecializationData> getMinorSpecializations(int code, String description, long majorSpecializationId, String majorSpecializationDesc, int generalSpecialization, int generalType) throws BusinessException {
	Integer[] generalTypes;
	if (FlagsEnum.ALL.getCode() == generalType)
	    generalTypes = null;
	else
	    generalTypes = new Integer[] { generalType };

	return searchMinorSpecialization(code, description, majorSpecializationId, majorSpecializationDesc, generalSpecialization, generalTypes);
    }

    public static List<MinorSpecializationData> getMinorSpecializationsForCivilians(int code, String description, long majorSpecializationId, String majorSpecializationDesc, int generalSpecialization, int generalType) throws BusinessException {
	Integer[] generalTypes;
	if (FlagsEnum.ALL.getCode() == generalType)
	    generalTypes = new Integer[] { JobTypesEnum.NORMAL.getCode(), JobTypesEnum.TECHNICAL.getCode() };
	else
	    generalTypes = new Integer[] { generalType };

	return searchMinorSpecialization(code, description, majorSpecializationId, majorSpecializationDesc, generalSpecialization, generalTypes);
    }

    private static List<MinorSpecializationData> searchMinorSpecialization(int code, String description, long majorSpecializationId, String majorSpecializationDesc, Integer generalSpecialization, Integer[] generalTypes) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CODE", code);
	    qParams.put("P_DESC", (description == null || description.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : "%" + description + "%");
	    qParams.put("P_MAJOR_SPEC_ID", majorSpecializationId);
	    qParams.put("P_MAJOR_SPEC_DESC", (majorSpecializationDesc == null || majorSpecializationDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : "%" + majorSpecializationDesc + "%");

	    qParams.put("P_GENERAL_SPECIALIZATION", generalSpecialization);

	    if (generalTypes != null && generalTypes.length > 0) {
		qParams.put("P_GENERAL_TYPES_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_GENERAL_TYPES", generalTypes);
	    } else {
		qParams.put("P_GENERAL_TYPES_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_GENERAL_TYPES", new Integer[] { FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(MinorSpecializationData.class, QueryNamesEnum.HCM_SEARCH_MINOR_SPECIALIZATIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static String getMinorSpecializationsDescStringByIdsString(String minorSpecializationsIdsString) throws BusinessException {
	StringBuilder minorSpecializationsDesc = new StringBuilder();
	List<MinorSpecializationData> minorSpecializationsList = getMinorSpecializationsByIdsString(minorSpecializationsIdsString);

	for (MinorSpecializationData minSpec : minorSpecializationsList)
	    minorSpecializationsDesc.append(minSpec.getDescription()).append(",");

	return minorSpecializationsDesc.deleteCharAt(minorSpecializationsDesc.length() - 1).toString();
    }

    public static List<MinorSpecializationData> getMinorSpecializationsByIdsString(String minorSpecializationsIdsString) throws BusinessException {
	return searchMinorSpecializationsByIds(splitStringToLongArray(minorSpecializationsIdsString));
    }

    private static List<MinorSpecializationData> searchMinorSpecializationsByIds(Long[] minorSpecializationsIds) throws BusinessException {
	try {
	    if (minorSpecializationsIds == null || minorSpecializationsIds.length == 0)
		return new ArrayList<MinorSpecializationData>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MINOR_SPECIALIZATIONS_IDS", minorSpecializationsIds);

	    return DataAccess.executeNamedQuery(MinorSpecializationData.class, QueryNamesEnum.HCM_SEARCH_MINOR_SPECIALIZATIONS_BY_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*---------------------------------------------- Minor Specialization Description --------------------------------------------*/

    /***
     * Add new minor specialization description
     * 
     * @param minorSpecializationDescriptionData
     * @param minorSpecializationDescriptionDetailsData
     * @param userId
     * @param useSession
     * @throws BusinessException
     */
    public static void addMinorSpecializationDescription(MinorSpecializationDescriptionData minorSpecializationDescriptionData, List<MinorSpecializationDescriptionDetailData> minorSpecializationDescriptionDetailsData, Long userId, CustomSession... useSession) throws BusinessException {

	trimMinorSpecializationDescriptionData(minorSpecializationDescriptionData);
	trimMinorSpecializationDescriptionDetailData(minorSpecializationDescriptionDetailsData);

	validateMinorSpecializationDescription(minorSpecializationDescriptionData, minorSpecializationDescriptionDetailsData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    minorSpecializationDescriptionData.getMinorSpecializationDescriptionEntity().setSystemUser(userId + ""); // for Auditing
	    DataAccess.addEntity(minorSpecializationDescriptionData.getMinorSpecializationDescriptionEntity(), session);
	    minorSpecializationDescriptionData.setId(minorSpecializationDescriptionData.getMinorSpecializationDescriptionEntity().getId());

	    for (MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetailData : minorSpecializationDescriptionDetailsData) {
		minorSpecializationDescriptionDetailData.setMinorSpecializationDescriptionId(minorSpecializationDescriptionData.getId());
		minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail().setSystemUser(userId + ""); // for Auditing
		DataAccess.addEntity(minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail(), session);
		minorSpecializationDescriptionDetailData.setId(minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail().getId());
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {

	    minorSpecializationDescriptionData.setId(null);
	    for (MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetailData : minorSpecializationDescriptionDetailsData) {
		minorSpecializationDescriptionDetailData.setId(null);
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

    /***
     * update minor specialization description
     * 
     * @param minorSpecializationDescriptionData
     * @param minorSpecializationDescriptionDetailsData
     * @param removedMinorSpecializationDescriptionDetailsData
     * @param userId
     * @param useSession
     * @throws BusinessException
     */
    public static void modifyMinorSpecializationDescription(MinorSpecializationDescriptionData minorSpecializationDescriptionData, List<MinorSpecializationDescriptionDetailData> minorSpecializationDescriptionDetailsData, Long userId, CustomSession... useSession) throws BusinessException {

	trimMinorSpecializationDescriptionData(minorSpecializationDescriptionData);
	trimMinorSpecializationDescriptionDetailData(minorSpecializationDescriptionDetailsData);

	validateMinorSpecializationDescription(minorSpecializationDescriptionData, minorSpecializationDescriptionDetailsData);

	List<MinorSpecializationDescriptionDetailData> addedMinorSpecializationDescriptionDetails = new ArrayList<MinorSpecializationDescriptionDetailData>();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    minorSpecializationDescriptionData.getMinorSpecializationDescriptionEntity().setSystemUser(userId + ""); // for Auditing
	    DataAccess.updateEntity(minorSpecializationDescriptionData.getMinorSpecializationDescriptionEntity(), session);

	    for (MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetailData : minorSpecializationDescriptionDetailsData) {
		minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail().setSystemUser(userId + ""); // for Auditing

		if (minorSpecializationDescriptionDetailData.getId() == null) {
		    minorSpecializationDescriptionDetailData.setMinorSpecializationDescriptionId(minorSpecializationDescriptionData.getId());
		    DataAccess.addEntity(minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail(), session);
		    minorSpecializationDescriptionDetailData.setId(minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail().getId());
		    addedMinorSpecializationDescriptionDetails.add(minorSpecializationDescriptionDetailData);
		} else {
		    DataAccess.updateEntity(minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail(), session);
		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {

	    for (MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetailData : addedMinorSpecializationDescriptionDetails) {
		minorSpecializationDescriptionDetailData.setId(null);
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

    /***
     * delete minor specialization description detail
     * 
     * @param minorSpecializationDescriptionDetailData
     * @param userId
     * @param useSession
     * @throws BusinessException
     */
    public static void deleteMinorSpecializationDescriptionDetail(List<MinorSpecializationDescriptionDetailData> minorSpecializationDescriptionDetailsData, MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetailData, Long userId, CustomSession... useSession) throws BusinessException {

	validateMinorSpecializationDescriptionDetailDeletion(minorSpecializationDescriptionDetailsData, minorSpecializationDescriptionDetailData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail().setSystemUser(userId + ""); // for Auditing
	    DataAccess.deleteEntity(minorSpecializationDescriptionDetailData.getMinorSpecializationDescriptionDetail(), session);

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

    /***
     * to trim MinorSpecializationDescriptionData object and return it back.
     * 
     * @param minorSpecializationDescriptionData
     * @throws BusinessException
     */
    private static void trimMinorSpecializationDescriptionData(MinorSpecializationDescriptionData minorSpecializationDescriptionData) throws BusinessException {

	minorSpecializationDescriptionData.setTargetedUnits(minorSpecializationDescriptionData.getTargetedUnits().trim());
	minorSpecializationDescriptionData.setTargetedRanks(minorSpecializationDescriptionData.getTargetedRanks().trim());
	minorSpecializationDescriptionData.setGeneralPurpose(minorSpecializationDescriptionData.getGeneralPurpose().trim());
	minorSpecializationDescriptionData.setRequestedQualifications(minorSpecializationDescriptionData.getRequestedQualifications().trim());
	minorSpecializationDescriptionData.setRequestedExperiences(minorSpecializationDescriptionData.getRequestedExperiences().trim());
	minorSpecializationDescriptionData.setRequestedSkills(minorSpecializationDescriptionData.getRequestedSkills().trim());
	minorSpecializationDescriptionData.setUsedTools(minorSpecializationDescriptionData.getUsedTools().trim());
	minorSpecializationDescriptionData.setRequestedTrainings(minorSpecializationDescriptionData.getRequestedTrainings().trim());
    }

    /***
     * to trim dutyDescription in the list and return it back
     * 
     * @param minorSpecializationDescriptionDetailsData
     * @throws BusinessException
     */
    private static void trimMinorSpecializationDescriptionDetailData(List<MinorSpecializationDescriptionDetailData> minorSpecializationDescriptionDetailsData) throws BusinessException {
	for (MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetail : minorSpecializationDescriptionDetailsData) {
	    minorSpecializationDescriptionDetail.setDutyDescription(minorSpecializationDescriptionDetail.getDutyDescription().trim());
	}
    }

    /***
     * validation of minor specialization description
     * 
     * @param minorSpecializationDescriptionData
     * @param minorSpecializationDescriptionDetailsData
     * @param minorSpecializationId
     * @throws BusinessException
     */
    private static void validateMinorSpecializationDescription(MinorSpecializationDescriptionData minorSpecializationDescriptionData, List<MinorSpecializationDescriptionDetailData> minorSpecializationDescriptionDetailsData) throws BusinessException {

	if (minorSpecializationDescriptionData.getMinorSpecializationId() == null)
	    throw new BusinessException("error_minorSpecializationIsMandatory");

	if (minorSpecializationDescriptionData.getTargetedUnits() == null || minorSpecializationDescriptionData.getTargetedUnits().isEmpty())
	    throw new BusinessException("error_targetedUnitsRequired");

	if (minorSpecializationDescriptionData.getTargetedRanks() == null || minorSpecializationDescriptionData.getTargetedRanks().isEmpty())
	    throw new BusinessException("error_targetedRanksRequired");

	if (minorSpecializationDescriptionData.getGeneralPurpose() == null || minorSpecializationDescriptionData.getGeneralPurpose().isEmpty())
	    throw new BusinessException("error_generalPurposeRequired");

	if (minorSpecializationDescriptionData.getRequestedQualifications() == null || minorSpecializationDescriptionData.getRequestedQualifications().isEmpty())
	    throw new BusinessException("error_requestedQualificationsRequired");

	if (minorSpecializationDescriptionData.getRequestedExperiences() == null || minorSpecializationDescriptionData.getRequestedExperiences().isEmpty())
	    throw new BusinessException("error_requestedExperiencesRequired");

	if (minorSpecializationDescriptionData.getRequestedSkills() == null || minorSpecializationDescriptionData.getRequestedSkills().isEmpty())
	    throw new BusinessException("error_requestedSkillsRequired");

	if (minorSpecializationDescriptionData.getUsedTools() == null || minorSpecializationDescriptionData.getUsedTools().isEmpty())
	    throw new BusinessException("error_usedToolsRequired");

	if (minorSpecializationDescriptionData.getRequestedTrainings() == null || minorSpecializationDescriptionData.getRequestedTrainings().isEmpty())
	    throw new BusinessException("error_requestedTrainingsRequired");

	if (minorSpecializationDescriptionDetailsData.isEmpty())
	    throw new BusinessException("error_dutyAndResponsibilityRequired");

	HashSet<String> dutySet = new HashSet<String>();
	for (MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetail : minorSpecializationDescriptionDetailsData) {

	    if (minorSpecializationDescriptionDetail.getDutyDescription() == null || minorSpecializationDescriptionDetail.getDutyDescription().isEmpty())
		throw new BusinessException("error_dutyAndResponsibilityRequired");

	    // duty description is unique.
	    if (!dutySet.add(minorSpecializationDescriptionDetail.getDutyDescription()))
		throw new BusinessException("error_repeatedDutyAndResponsibilityDescription");
	}

    }

    /***
     * validation of minor specialization description detail deletion
     * 
     * @param minorSpecializationDescriptionDetailsData
     * @param minorSpecializationDescriptionDetailData
     * @throws BusinessException
     */
    private static void validateMinorSpecializationDescriptionDetailDeletion(List<MinorSpecializationDescriptionDetailData> minorSpecializationDescriptionDetailsData, MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetailData) throws BusinessException {
	for (MinorSpecializationDescriptionDetailData detailData : minorSpecializationDescriptionDetailsData) {
	    if (detailData.getId() != null && !detailData.getId().equals(minorSpecializationDescriptionDetailData.getId())) {
		return;
	    }
	}
	throw new BusinessException("error_dutyAndResponsibilityRequired");
    }

    /***
     * 
     * get minor specialization description
     * 
     * @param minorSpecializationId
     * @return
     * @throws BusinessException
     */
    public static MinorSpecializationDescriptionData getMinorSpecializationDescription(Long minorSpecializationId) throws BusinessException {
	List<MinorSpecializationDescriptionData> minorSpecializationDescriptions = new ArrayList<MinorSpecializationDescriptionData>();
	try {
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("P_MINOR_SPECIALIZATION_ID", minorSpecializationId);

	    minorSpecializationDescriptions = DataAccess.executeNamedQuery(MinorSpecializationDescriptionData.class, QueryNamesEnum.HCM_GET_MINOR_SPECIALIZATION_DESCRIPTION.getCode(), param);
	    if (!minorSpecializationDescriptions.isEmpty()) {
		return minorSpecializationDescriptions.get(0);
	    } else {
		return null;
	    }
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /***
     * get list of minor specialization description detail
     * 
     * @param minorSpecializationDescriptionId
     * @return
     * @throws BusinessException
     */
    public static List<MinorSpecializationDescriptionDetailData> getMinorSpecializationDescriptionDetails(Long minorSpecializationDescriptionId) throws BusinessException {

	try {
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("P_MINOR_SPECIALIZATION_DESCRIPTION_ID", minorSpecializationDescriptionId);
	    return DataAccess.executeNamedQuery(MinorSpecializationDescriptionDetailData.class, QueryNamesEnum.HCM_GET_MINOR_SPECIALIZATION_DESCRIPTION_DETAILS.getCode(), param);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*---------------------------------------------------------- Reports --------------------------------------------------*/
    /***
     * generate report for minor specialization description
     * 
     * @param minorSpecializationId
     * @return
     * @throws BusinessException
     */
    public static byte[] getMinorSpecializationDescriptionReportBytes(Long minorSpecializationId) throws BusinessException {
	try {

	    String reportName = ReportNamesEnum.JOBS_MINOR_SPECIALIZATION_DESCRIPTION.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_MINOR_SPEC_ID", minorSpecializationId);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
