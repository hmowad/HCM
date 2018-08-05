package com.code.services.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.workflow.WFNotificationsConfigData;
import com.code.dal.orm.workflow.WFNotificationsConfigDetailData;
import com.code.dal.orm.workflow.WFNotificationsConfigDetailEmployeeData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;

public class NotificationsConfigService extends BaseService {

    /**
     * private constructor to prevent instantiation
     * 
     */
    private NotificationsConfigService() {

    }

    // ************************************************************operations***********************************************************************

    /**
     * add new WFNotification Configuration
     * 
     * @param notificationData
     *            new WFNotification Configuration
     * @param useSession
     *            optional database session
     * @throws BusinessException
     *             if any error occur
     */
    public static void addNotificationConfig(WFNotificationsConfigData notificationData, List<WFNotificationsConfigDetailData> notificationDetailsData, long userId, CustomSession... useSession) throws BusinessException {
	validateNotificationConfig(notificationData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    notificationData.getWfNotificationConfig().setSystemUser(userId + ""); // For Auditing.
	    DataAccess.addEntity(notificationData.getWfNotificationConfig(), session);
	    notificationData.setId(notificationData.getWfNotificationConfig().getId());

	    for (WFNotificationsConfigDetailData wfNotificationsConfigDetailData : notificationDetailsData) {
		wfNotificationsConfigDetailData.setWfNotificationsConfigId(notificationData.getId());
	    }
	    addNotificationConfigDetails(notificationDetailsData, userId, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    notificationData.setId(null);
	    for (WFNotificationsConfigDetailData wfNotificationsConfigDetailData : notificationDetailsData) {
		wfNotificationsConfigDetailData.setId(null);
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
     * updates WFNotification Configuration
     * 
     * @param notificationData
     *            WFNotification Configuration to be updated
     * @param useSession
     *            optional database session
     * @throws BusinessException
     *             if any error occur
     */
    public static void updateNotificationConfig(WFNotificationsConfigData notificationData, long userId, CustomSession... useSession) throws BusinessException {
	validateNotificationConfig(notificationData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    notificationData.getWfNotificationConfig().setSystemUser(userId + ""); // For Auditing.
	    DataAccess.updateEntity(notificationData.getWfNotificationConfig(), session);

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
     * delete WFNotification Configuration
     * 
     * @param notificationData
     *            WFNotification Configuration to be deleted
     * @param useSession
     *            optional database session
     * @throws BusinessException
     *             if any errors occur
     */
    public static void deleteNotificationConfig(WFNotificationsConfigData notificationData, long userId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<WFNotificationsConfigDetailData> notificationDetails = NotificationsConfigService.searchNotificationsConfigDetailData(notificationData.getId());
	    for (WFNotificationsConfigDetailData wfNotificationsConfigDetailData : notificationDetails) {
		wfNotificationsConfigDetailData.getWfNotificationsConfigDetail().setSystemUser(userId + ""); // For Auditing.
		DataAccess.deleteEntity(wfNotificationsConfigDetailData.getWfNotificationsConfigDetail(), session);
	    }

	    notificationData.getWfNotificationConfig().setSystemUser(userId + ""); // For Auditing.
	    DataAccess.deleteEntity(notificationData.getWfNotificationConfig(), session);

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
     * Adds a given notifications configuration detail object
     * 
     * @param notificationDetailData
     *            the {@link WFNotificationsConfigDetailData} object to be added
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void addNotificationConfigDetails(List<WFNotificationsConfigDetailData> notificationDetailsData, long userId, CustomSession... useSession) throws BusinessException {
	validateAddNotificationConfigDetails(notificationDetailsData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (WFNotificationsConfigDetailData notificationDetailData : notificationDetailsData) {
		notificationDetailData.getWfNotificationsConfigDetail().setSystemUser(userId + ""); // For Auditing.
		DataAccess.addEntity(notificationDetailData.getWfNotificationsConfigDetail(), session);
		notificationDetailData.setId(notificationDetailData.getWfNotificationsConfigDetail().getId());
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (WFNotificationsConfigDetailData wfNotificationsConfigDetailData : notificationDetailsData) {
		wfNotificationsConfigDetailData.setId(null);
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
     * Deletes a given notifications configuration detail object
     * 
     * @param notificationDetailData
     *            the {@link WFNotificationsConfigDetailData} object to be deleted
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void deleteNotificationConfigDetail(WFNotificationsConfigDetailData notificationDetailData, List<WFNotificationsConfigDetailData> notificationDetailsData, long userId, CustomSession... useSession) throws BusinessException {
	validateDeleteNotificationConfigDetail(notificationDetailsData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    notificationDetailData.getWfNotificationsConfigDetail().setSystemUser(userId + ""); // For Auditing.
	    DataAccess.deleteEntity(notificationDetailData.getWfNotificationsConfigDetail(), session);

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

    // ************************************************************validation***********************************************************************

    /**
     * validates WFNotification Configuration, validates the mandatory fields,checks for notification configuration duplication
     * 
     * @param notificationData
     *            WFNotification Configuration to be validated
     * @throws BusinessException
     *             if any errors occur
     */
    private static void validateNotificationConfig(WFNotificationsConfigData notificationData) throws BusinessException {
	if (notificationData == null) {
	    throw new BusinessException("error_general");
	}

	// Validate mandatory fields
	if (notificationData.getDescription() == null || notificationData.getDescription().trim().isEmpty()) {
	    throw new BusinessException("error_notificationConfigDescMandatory");
	}

	if (notificationData.getWfProcessGroupId() == null) {
	    throw new BusinessException("error_notificationConfigProcessGroupMandatory");
	}

	if (notificationData.getBeneficiaryCategoryId() == null) {
	    throw new BusinessException("error_notificationConfigCategoryMandatory");
	}

	if (notificationData.getDecisionRegionId() != null && (notificationData.getBeneficiaryRegionId() != null || notificationData.getBeneficiaryUnitId() != null)) {
	    throw new BusinessException("error_notificationConfigBeneficiariesMandatory");
	}

	if (notificationData.getDecisionRegionId() == null && (notificationData.getBeneficiaryRegionId() == null || notificationData.getBeneficiaryUnitId() == null)) {
	    throw new BusinessException("error_notificationConfigBeneficiariesMandatory");
	}

	// Check for duplicates
	if (countNotificationConfig(notificationData.getId(), notificationData.getWfProcessGroupId(), notificationData.getWfProcessId(), notificationData.getBeneficiaryCategoryId(), notificationData.getDecisionRegionId(), notificationData.getBeneficiaryRegionId(), notificationData.getBeneficiaryUnitId()) > 0) {
	    throw new BusinessException("error_repeatedNotificationConfig");
	}
    }

    /**
     * Validates Addition of WFNotification Configuration Details
     * 
     * @param notificationDetailData
     *            WFNotification Configuration Detail to be validated
     * @throws BusinessException
     *             if any errors occurs
     */
    private static void validateAddNotificationConfigDetails(List<WFNotificationsConfigDetailData> notificationDetailsData) throws BusinessException {
	if (notificationDetailsData == null || notificationDetailsData.size() == 0)
	    throw new BusinessException("error_notificationConfigDetailsEmpty");

	for (WFNotificationsConfigDetailData notificationDetailData : notificationDetailsData) {

	    // Validate mandatory fields
	    if (notificationDetailData.getWfNotificationsConfigId() == null)
		throw new BusinessException("error_notificationConfigMandatory");

	    if (notificationDetailData.getUnitId() == null)
		throw new BusinessException("error_notificationConfigDetailUnitIsMandatory");

	    // WfNotificationsConfigId, category, unit and job should be unique
	    if (countNotificationConfigDetail(notificationDetailData.getWfNotificationsConfigId(), notificationDetailData.getJobId(), notificationDetailData.getUnitId(), notificationDetailData.getCategoryId()) > 0) {
		throw new BusinessException("error_repeatedNotificationConfig");
	    }
	}
    }

    /**
     * Validates Deletion of WFNotification Configuration Details
     * 
     * @param notificationDetailData
     *            WFNotification Configuration Detail to be validated
     * @throws BusinessException
     *             if any errors occurs
     */
    private static void validateDeleteNotificationConfigDetail(List<WFNotificationsConfigDetailData> notificationDetails) throws BusinessException {
	if (notificationDetails.size() == 1)
	    throw new BusinessException("error_deleteLastNotificationConfigDetail");
    }

    // ************************************************************queries***********************************************************************

    /**
     * wrapper for {@link #searchNotificationsConfigData(long, long, String)}, used in notification configuration back bean
     * 
     * @param processGroupId
     *            workflow process group id in WFNotification Configuration
     * @param categoryId
     *            beneficiary category id in WFNotification Configuration
     * @param description
     *            description of WFNotification Configuration
     * @return list of WFNotification Configuration
     * @throws BusinessException
     *             if any errors occur
     */

    public static List<WFNotificationsConfigData> getNotificationsConfigData(long processGroupId, long categoryId, String description) throws BusinessException {
	return searchNotificationsConfigData(processGroupId, categoryId, description);
    }

    /**
     * searches for WFNotification Configuration, used in notification configuration back bean
     * 
     * @param processGroupId
     *            workflow process group id in WFNotification Configuration
     * @param employeeId
     *            member employee id in WFNotification Configuration
     * @param description
     *            description of WFNotification Configuration
     * @return list of WFNotification Configuration
     * @throws BusinessException
     *             if any errors occur
     */
    private static List<WFNotificationsConfigData> searchNotificationsConfigData(long processGroupId, long categoryId, String description) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PROCESS_GROUP_ID", processGroupId);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_DESCRIPTION", description == null || description.isEmpty() ? FlagsEnum.ALL.getCode() + "" : "%" + description + "%");

	    return DataAccess.executeNamedQuery(WFNotificationsConfigData.class, QueryNamesEnum.WF_SEARCH_NOTIFICATIONS_CONFIG_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * count WFNotification Configuration
     * 
     * @param excludedId
     *            id WFNotification Configuration to be excluded from the count
     * @param processGroupId
     *            workflow process group id in WFNotification Configuration
     * 
     * @param processId
     *            workflow process id in WFNotification Configuration
     * @param beneficiaryCategoryId
     *            beneficiary Category Id in WFNotification Configuration
     * @param decisionRegionId
     *            decision region id in WFNotification Configuration
     * @param beneficiaryRegionId
     *            beneficiary region id in WFNotification Configuration
     * @param beneficiaryUnitId
     *            beneficiary unit id in WFNotification Configuration
     * @return count of WFNotification Configuration that have the passed parameters
     * @throws BusinessException
     *             if any errors occur
     */
    private static long countNotificationConfig(Long excludedId, long processGroupId, Long processId, long beneficiaryCategoryId, Long decisionRegionId, Long beneficiaryRegionId, Long beneficiaryUnitId) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXCLUDED_ID", excludedId == null ? FlagsEnum.ALL.getCode() : excludedId);
	    qParams.put("P_PROCESS_GROUP_ID", processGroupId);
	    qParams.put("P_PROCESS_ID", processId == null ? FlagsEnum.ALL.getCode() : processId);
	    qParams.put("P_BENEFICIARY_CATEGORY_ID", beneficiaryCategoryId);
	    qParams.put("P_DECISION_REGION_ID", decisionRegionId == null ? FlagsEnum.ALL.getCode() : decisionRegionId);
	    qParams.put("P_BENEFICIARY_REGION_ID", beneficiaryRegionId == null ? FlagsEnum.ALL.getCode() : beneficiaryRegionId);
	    qParams.put("P_BENEFICIARY_UNIT_ID", beneficiaryUnitId == null ? FlagsEnum.ALL.getCode() : beneficiaryUnitId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_COUNT_NOTIFICATIONS_CONFIG.getCode(), qParams).get(0).longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Search for notification configuration Details for a specific notification configuration
     * 
     * @param wfNotificationsConfigId
     *            a long value contains the ID of the notification configuration Object to get its details
     * @return a List of {@link WFNotificationsConfigDetailData} contains the details of the notification configuration object
     * @throws BusinessException
     *             if any errors occurs
     */
    public static List<WFNotificationsConfigDetailData> searchNotificationsConfigDetailData(long wfNotificationsConfigId) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_WF_NOTIFICATIONS_CONFIG_ID", wfNotificationsConfigId);

	    return DataAccess.executeNamedQuery(WFNotificationsConfigDetailData.class, QueryNamesEnum.WF_SEARCH_NOTIFICATIONS_CONFIG_DETAIL_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long countNotificationConfigDetail(long wfNotificationsConfigId, Long jobId, Long unitId, Long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_NOTIFICATION_CONFIG_ID", wfNotificationsConfigId);
	    qParams.put("P_JOB_ID", jobId == null ? FlagsEnum.ALL.getCode() : jobId);
	    qParams.put("P_UNIT_ID", unitId == null ? FlagsEnum.ALL.getCode() : unitId);
	    qParams.put("P_CATEGORY_ID", categoryId == null ? FlagsEnum.ALL.getCode() : categoryId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_COUNT_NOTIFICATIONS_CONFIG_DETAILS.getCode(), qParams).get(0).longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * search for WFNotification Configuration, used in sending notifications in workflows
     * 
     * @param categoriesIds
     *            list of categories ids
     * @param decisionRegionId
     *            decision region
     * @param processId
     *            workflow process id
     * @param beneficairyEmployeesIds
     *            list of employees ids
     * @return list of WFNotification Configuration based on the passed parameters
     * @throws BusinessException
     *             if any errors occur
     * @see BaseWorkFlow.#computeInstanceNotifications(List, long, long, List, List)
     */
    public static List<WFNotificationsConfigDetailEmployeeData> searchNotificationsConfigDetailEmployeeForWfInstance(List<Long> categoriesIds, long decisionRegionId, long processId, List<Long> beneficairyEmployeesIds) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CATEGORIES_IDS", categoriesIds.toArray(new Long[categoriesIds.size()]));
	    qParams.put("P_DECISION_REGION_ID", decisionRegionId);
	    qParams.put("P_PROCESS_ID", processId);
	    qParams.put("P_EMP_IDS", beneficairyEmployeesIds.toArray(new Long[beneficairyEmployeesIds.size()]));

	    return DataAccess.executeNamedQuery(WFNotificationsConfigDetailEmployeeData.class, QueryNamesEnum.WF_SEARCH_NOTIFICATIONS_CONFIG_DETAIL_EMPLOYEE_DATA_FOR_WF_INSTANCE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
