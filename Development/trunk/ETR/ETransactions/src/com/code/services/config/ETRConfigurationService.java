package com.code.services.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.config.ETRConfig;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;

public class ETRConfigurationService extends BaseService {
    // Initialization.
    // Transactions.
    // Queries.
    // General configuration getters.
    // System configuration getters and setters zone per each system.
    // Integration codes for external systems.

    /*------------------------------------------------------Initialization------------------------------------------------------*/
    private static Map<String, String> configurationMap;

    public static void init() {
	List<ETRConfig> configurationList;
	try {
	    configurationMap = new HashMap<String, String>();
	    configurationList = getETRConfigsByTransactionClass(FlagsEnum.ALL.getCode());

	    for (ETRConfig config : configurationList) {
		configurationMap.put(config.getCode(), config.getValue());
	    }

	} catch (Exception e) {
	    System.out.println("########### ETR config Service initialization failed !!");
	    e.printStackTrace();
	}
    }

    /*------------------------------------------------------Transactions------------------------------------------------------*/
    private static void setConfigValue(String configCode, String configValue, Long transactionEmployeeId, CustomSession... useSession) {
	try {
	    ETRConfig etrConfig = getETRConfigByCode(configCode);
	    etrConfig.setValue(configValue);
	    updateETRConfig(etrConfig, transactionEmployeeId, useSession);
	    configurationMap.put(configCode, configValue);
	} catch (Exception e) {
	    System.out.println("########### Configuration Setting FAILED");
	    e.printStackTrace();
	}
    }

    public static void updateETRConfig(ETRConfig etrConfig, Long transactionEmployeeId, CustomSession... useSession) throws Exception {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (transactionEmployeeId != null)
		etrConfig.setSystemUser(transactionEmployeeId + ""); // audit
	    DataAccess.updateEntity(etrConfig, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /*------------------------------------------------------Queries------------------------------------------------------*/

    public static String getETRConfigValueByCode(String code) {
	return getETRConfigByCode(code).getValue();
    }

    private static ETRConfig getETRConfigByCode(String code) {
	List<ETRConfig> res = searchETRConfig(code, FlagsEnum.ALL.getCode());
	return res.size() == 0 ? null : res.get(0);
    }

    public static List<ETRConfig> getETRConfigsByTransactionClass(int transactionClass) {
	return searchETRConfig(FlagsEnum.ALL.getCode() + "", transactionClass);
    }

    private static List<ETRConfig> searchETRConfig(String code, int transactionClass) {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CODE", code);
	    qParams.put("P_TRANSACTION_CLASS", transactionClass);

	    return DataAccess.executeNamedQuery(ETRConfig.class, QueryNamesEnum.CONFG_GET_ETR_CONFIGURATIONS.getCode(), qParams);

	} catch (Exception e) {
	    e.printStackTrace();
	    return new ArrayList<ETRConfig>();
	}
    }

    /*------------------------------------------------------General configuration getters------------------------------------------------------*/
    public static String getLDAPConnectionType() {
	return configurationMap.get("LDAP_CONNECTION_TYPE");
    }

    public static String getLDAPDomain() {
	return configurationMap.get("LDAP_DOMAIN");
    }

    public static String getLDAPIPS() {
	return configurationMap.get("LDAP_IPS");
    }

    public static String getLDAPPort() {
	return configurationMap.get("LDAP_PORT");
    }

    public static String getLDAPBase() {
	return configurationMap.get("LDAP_BASE");
    }

    public static String getLDAPAdminPassword() {
	return configurationMap.get("LDAP_ADMIN_PASSWORD");
    }

    public static String getLDAPAdminUsername() {
	return configurationMap.get("LDAP_ADMIN_USER");
    }

    public static String getLDAPIdentityAttribute() {
	return configurationMap.get("LDAP_IDENTITY_ATTRIBUTE");
    }

    public static String getLDAPTelephoneNumberAttribute() {
	return configurationMap.get("LDAP_TELEPHONE_NUMBER");
    }

    public static String getLDAPManagerAttribute() {
	return configurationMap.get("LDAP_MANAGER");
    }

    public static String getLDAPMobileAttribute() {
	return configurationMap.get("LDAP_MOBILE");
    }

    public static String getLDAPMailAttribute() {
	return configurationMap.get("LDAP_MAIL");
    }

    public static String getLDAPPhysicalDeliveryOfficeAttribute() {
	return configurationMap.get("LDAP_PHYSICAL_DELIVERY_OFFICE");
    }

    public static String getReportsRoot() {
	return configurationMap.get("REPORTS_ROOT");
    }

    public static String getAttachmentsAddURL() {
	if (getConfig("internalFlag").equals(FlagsEnum.ON.getCode() + ""))
	    return configurationMap.get("ATTACHEMENTS_ADD_INTERNAL_URL");
	else
	    return configurationMap.get("ATTACHEMENTS_ADD_EXTERNAL_URL");
    }

    public static String getAttachmentsViewURL() {
	if (getConfig("internalFlag").equals(FlagsEnum.ON.getCode() + ""))
	    return configurationMap.get("ATTACHEMENTS_VIEW_INTERNAL_URL");
	else
	    return configurationMap.get("ATTACHEMENTS_VIEW_EXTERNAL_URL");
    }

    public static String getFollowingProcessesMenuId() {
	return getConfig("module").equals("1") ? configurationMap.get("FOLLOWING_PROCESSES_MENU_ID").split(",")[0] : configurationMap.get("FOLLOWING_PROCESSES_MENU_ID").split(",")[1];
    }

    public static String getDelegationsMenuId() {
	return getConfig("module").equals("1") ? configurationMap.get("DELEGATIONS_MENU_ID").split(",")[0] : configurationMap.get("DELEGATIONS_MENU_ID").split(",")[1];
    }

    public static String getMedicalCenterURL() {
	if (getConfig("internalFlag").equals(FlagsEnum.ON.getCode() + ""))
	    return configurationMap.get("MEDICAL_CENTER_INTERNAL_URL");
	else
	    return configurationMap.get("MEDICAL_CENTER_EXTERNAL_URL");
    }

    public static String getDualSecurityFlag() {
	return configurationMap.get("DUAL_SECURITY_FLAG");
    }

    public static String getMobilePushFlag() {
	return configurationMap.get("MOBILE_PUSH_FLAG");
    }

    public static String getPushNotificationServerConfigurations() {
	return configurationMap.get("PUSH_NOTIFICATION_SERVER_CONFIGS");
    }

    public static String getEReservationsWSDLLocation() {
	return configurationMap.get("ERESERVATION_WSDL_LOCATION");
    }

    public static String getTrainingCurriculumCommitteeMembers() {
	return getETRConfigByCode("TRN_CURRICULUM_COMMITTEE_MEMBERS").getValue();
    }

    public static String getLogFilePath() {
	return getETRConfigByCode("LOG_FILE_PATH").getValue();
    }

    public static int getSocialIdRenewalPeriodWarning() {
	return Integer.parseInt(configurationMap.get("SOCIAL_ID_RENEWAL_PERIOD_WARNING"));
    }

    public static String getPayrollRestServiceGetAdminDecisionURL() {
	return configurationMap.get("PAYROLL_REST_SERVICES_GET_ADMIN_DECISION_URL");
    }

    public static String getESBPayrollRestServiceGetAdminDecisionURL() {
	return configurationMap.get("ESB_PAYROLL_WEBSERVICE_GET_ADMIN_DECISION_URL");
    }

    public static String getAllowanceRestServiceGetAdminDecisionURL() {
	return configurationMap.get("ALLOWANCE_REST_SERVICES_GET_ADMIN_DECISION_URL");
    }

    public static String getESBAllowanceRestServiceGetAdminDecisionURL() {
	return configurationMap.get("ESB_ALLOWANCE_WEBSERVICE_GET_ADMIN_DECISION_URL");
    }

    public static String getPayrollRestServiceApplyAdminDecisionURL() {
	return configurationMap.get("PAYROLL_REST_SERVICES_APPLY_ADMIN_DECISION_URL");
    }

    public static String getESBPayrollRestServiceApplyAdminDecisionURL() {
	return configurationMap.get("ESB_PAYROLL_WEBSERVICE_APPLY_ADMIN_DECISION_URL");
    }

    public static String getAllowanceRestServiceApplyAdminDecisionURL() {
	return configurationMap.get("ALLOWANCE_REST_SERVICES_APPLY_ADMIN_DECISION_URL");
    }

    public static String getESBAllowanceRestServiceApplyAdminDecisionURL() {
	return configurationMap.get("ESB_ALLOWANCE_WEBSERVICE_APPLY_ADMIN_DECISION_URL");
    }

    public static Integer getESBEnabledFlag() {
	return Integer.parseInt(configurationMap.get("ESB_ENABLED_FLAG"));
    }

    public static String getESBUsername() {
	return configurationMap.get("ESB_PAYROLL_WEBSERVICE_USERNAME");
    }

    public static String getESBPassword() {
	return configurationMap.get("ESB_PAYROLL_WEBSERVICE_PASSWORD");
    }

    public static String getTempNoLdapPassKey() {
	return configurationMap.get("TEMP_NO_LDAP_PASS_KEY");
    }

    public static String getProcessesIdsExcludedFromInvalidation() {
	return configurationMap.get("PROCESSES_IDS_EXCLUDED_FROM_INVALIDATION");
    }
    /*------------------------------------------------------General Notifications Configurations------------------------------------------------------*/

    public static String getGeneralMessagesUrl() {
	return configurationMap.get("GENERAL_NOTIFICATIONS_GENERAL_MESSAGES_URL");
    }

    public static Long getETransactionsRequesterId() {
	return Long.parseLong(configurationMap.get("ETRANSACTIONS_REQUESTER_ID"));
    }

    /*------------------------------------------------------Units Configurations------------------------------------------------------*/
    public static String getFieldUnitsIds() {
	return getETRConfigByCode("FIELD_UNITS_IDS").getValue();
    }

    public static void setFieldUnitsIds(String value, long transactionEmployeeId) {
	setConfigValue("FIELD_UNITS_IDS", value, transactionEmployeeId);
    }

    /*------------------------------------------------------Movements Configurations------------------------------------------------------*/
    public static boolean getMovementOpenWishesFlag() {
	return Integer.parseInt(getETRConfigByCode("MVT_OPEN_WISHES_FLAG").getValue()) == FlagsEnum.ON.getCode();
    }

    public static int getMovementWishesMinServicePeriod() {
	return Integer.parseInt(getETRConfigByCode("MVT_WISHES_MIN_SERVICE_PERIOD").getValue());
    }

    public static void setMovementOpenWishesFlag(boolean openWishesFlagBoolean, long transactionEmployeeId, CustomSession session) {
	String openWishesFlag = (openWishesFlagBoolean ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode()) + "";
	setConfigValue("MVT_OPEN_WISHES_FLAG", openWishesFlag, transactionEmployeeId, session);
    }

    public static void setMovementWishesMinServicePeriod(int minServicePeriod, long transactionEmployeeId, CustomSession session) {
	setConfigValue("MVT_WISHES_MIN_SERVICE_PERIOD", minServicePeriod + "", transactionEmployeeId, session);
    }

    public static int getMovementPeriodBetweenMovementAndServiceTerminationDueDate() {
	return Integer.parseInt(getETRConfigByCode("MVT_MIN_PERIOD_BETWEEN_MOVEMENT_AND_SERVICE_TERMINATION_DUE_DATE").getValue());
    }

    public static String getMovementTerminationJoiningApplyDate() {
	return configurationMap.get("MVT_MOVE_TERMINATION_JOINING_APPLY_DATE");
    }

    /*------------------------------------------------------Promotions Configurations------------------------------------------------------*/
    public static String[] getDrugsTestConfigInfo() {
	return configurationMap.get("DRUGS_TEST_CONFIG_INFO").split(",");
    }

    public static float getPromotionOfficersPrimeSergeantsMaxPromotionYears() {
	return Float.parseFloat(configurationMap.get("PRM_OFFICERS_PRIME_SERGEANTS_MAX_PROMOTION_YEARS"));
    }

    public static float getPromotionPersonsSeniortyPointsMax() {
	return Float.parseFloat(configurationMap.get("PRM_PERSONS_SENIORTY_POINTS_MAX"));
    }

    public static int getPromotionPersonsSeniortyYearsMax() {
	return Integer.parseInt(configurationMap.get("PRM_PERSONS_SENIORTY_YEARS_MAX"));
    }

    public static float getPromotionPersonsTrainingDegreeMax() {
	return Float.parseFloat(configurationMap.get("PRM_PERSONS_TRAINING_DEGREE_MAX"));
    }

    public static float getPromotionPersonsEvaluationDegreeMax() {
	return Float.parseFloat(configurationMap.get("PRM_PERSONS_EVALUATION_DEGREE_MAX"));
    }

    public static float getPromotionPersonsEducationDegreeMax() {
	return Float.parseFloat(configurationMap.get("PRM_PERSONS_EDUCATION_DEGREE_MAX"));
    }

    public static float getPromotionSoldiersExtraNonCandidatesDrugsTestPercentage() {
	return Float.parseFloat(configurationMap.get("PRM_SOLDIERS_EXTRA_NON_CANDIDATES_DRUGS_TEST_PERCENTAGE"));
    }

    public static String getHCMWebServiceWSDLLocation() {
	return configurationMap.get("HCM_WEBSERVICE_WSDL_LOCATION");
    }

    /*------------------------------------------------------Missions Configurations------------------------------------------------------*/
    public static String getMissionActivationStartDate() {
	return configurationMap.get("MSN_ACTIVATION_START_DATE");
    }

    public static int getMissionOfficersInternalVicePresidentPeriodMax() {
	return Integer.parseInt(configurationMap.get("MSN_OFFICERS_INTERNAL_VP_PERIOD_MAX"));
    }

    public static int getMissionSoldiersInternalVicePresidentPeriodMax() {
	return Integer.parseInt(configurationMap.get("MSN_SOLDIERS_INTERNAL_VP_PERIOD_MAX"));
    }

    public static int getMissionCiviliansInternalVicePresidentPeriodMax() {
	return Integer.parseInt(configurationMap.get("MSN_CIVILIANS_INTERNAL_VP_PERIOD_MAX"));
    }

    public static int getMissionOfficersInternalRegionCommanderPeriodMax() {
	return Integer.parseInt(configurationMap.get("MSN_OFFICERS_INTERNAL_RC_PERIOD_MAX"));
    }

    public static int getMissionSoldiersInternalRegionCommanderPeriodMax() {
	return Integer.parseInt(configurationMap.get("MSN_SOLDIERS_INTERNAL_RC_PERIOD_MAX"));
    }

    public static int getMissionCiviliansInternalRegionCommanderPeriodMax() {
	return Integer.parseInt(configurationMap.get("MSN_CIVILIANS_INTERNAL_RC_PERIOD_MAX"));
    }

    public static long getMissionOfficersInternalDMVicePresidentRankLessThan() {
	return Long.parseLong(configurationMap.get("MSN_OFFICERS_INTERNAL_DM_VP_RANK_LT"));
    }

    public static long getMissionCiviliansInternalDMVicePresidentRankLessThan() {
	return Long.parseLong(configurationMap.get("MSN_CIVILIANS_INTERNAL_DM_VP_RANK_LT"));
    }

    public static long getMissionOfficersInternalSMRegionCommanderRankLessThan() {
	return Long.parseLong(configurationMap.get("MSN_OFFICERS_INTERNAL_SM_RC_RANK_LT"));
    }

    public static long getMissionCiviliansInternalSMVicePresidentRankLessThan() {
	return Long.parseLong(configurationMap.get("MSN_CIVILIANS_INTERNAL_SM_VP_RANK_LT"));
    }

    public static int getMissionBalance() {
	return Integer.parseInt(configurationMap.get("MSN_BALANCE"));
    }

    public static int getMissionBalanceDouble() {
	return Integer.parseInt(configurationMap.get("MSN_BALANCE_DOUBLE"));
    }

    /*------------------------------------------------------Terminations Configurations------------------------------------------------------*/
    public static float getTerminationsServiceTerminationDueDateInYears() {
	return Float.parseFloat(configurationMap.get("STE_SERVICE_TERMINATION_DUE_DATE_IN_YEARS"));
    }

    public static float getTerminationsOfficersPeriodUntilDesiredTerminationDateDaysMax() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_PERIOD_UNTIL_DESIRED_TERMINATION_DATE_DAYS_MAX"));
    }

    public static float getTerminationsOfficersServicePeriodRetirementYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_SERVICE_PERIOD_RETIREMENT_YEARS_MIN"));
    }

    public static float getTerminationsOfficersServicePeriodRetirementYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_SERVICE_PERIOD_RETIREMENT_YEARS_MAX"));
    }

    public static int getTerminationsOfficersRankPeriodYearsMax() {
	return Integer.parseInt(configurationMap.get("STE_OFFICERS_RANK_PERIOD_YEARS_MAX"));
    }

    public static int getTerminationsOfficersRankPeriodMajorGeneralYearsMax() {
	return Integer.parseInt(configurationMap.get("STE_OFFICERS_RANK_PERIOD_MAJOR_GENERAL_YEARS_MAX"));
    }

    public static float getTerminationsOfficersServiceTerminationDateRecordDateDiffYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_SERVICE_TERMINATION_DATE_RECORD_DATE_DIFF_YEARS_MIN"));
    }

    public static float getTerminationsOfficersServiceTerminationDateRecordDateDiffYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_SERVICE_TERMINATION_DATE_RECORD_DATE_DIFF_YEARS_MAX"));
    }

    public static float getTerminationsOfficersExtensionPeriodMonthsMax() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_EXTENSION_PERIOD_MONTHS_MAX"));
    }

    public static float getTerminationsOfficersRequestDesiredTerminationDateRecordDateDiffYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_REQUEST_DESIRED_TERMINATION_DATE_RECORD_DATE_DIFF_YEARS_MIN"));
    }

    public static float getTerminationsOfficersRequestDesiredTerminationDateRecordDateDiffYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_REQUEST_DESIRED_TERMINATION_DATE_RECORD_DATE_DIFF_YEARS_MAX"));
    }

    public static float getTerminationsOfficersRequestDesiredAndServiceTerminationDateValidYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_OFFICERS_REQUEST_DESIRED_AND_SERVICE_TERMINATION_DATE_VALID_YEARS_MAX"));
    }

    public static float getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_PERIOD_UNTIL_DESIRED_TERMINATION_DATE_DAYS_MIN"));
    }

    public static float getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_PERIOD_UNTIL_DESIRED_TERMINATION_DATE_DAYS_MAX"));
    }

    public static float getTerminationsSoldiersServicePeriodRetirementYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_SERVICE_PERIOD_RETIREMENT_YEARS_MIN"));
    }

    public static float getTerminationsSoldierServicePeriodBeforeTerminationYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIER_SERVICE_PERIOD_BEFORE_TERMINATION_YEARS_MIN"));
    }

    public static float getTerminationsSoldiersDismissDateYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_DISMISS_DATE_YEARS_MIN"));
    }

    public static float getTerminationsSoldiersDismissDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_DISMISS_DATE_YEARS_MAX"));
    }

    public static float getTerminationsSoldiersTerminationDeathDateYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_TERMINATION_DEATH_DATE_YEARS_MIN"));
    }

    public static float getTerminationsSoldiersTerminationDeathDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_TERMINATION_DEATH_DATE_YEARS_MAX"));
    }

    public static float getTerminationsSoldiersAbsenceConnectedPeriodDaysMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_ABSENCE_CONNECTED_PERIOD_DAYS_MIN"));
    }

    public static float getTerminationsSoldiersAbsenceDisconnectedPeriodDaysMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_ABSENCE_DISCONNECTED_PERIOD_DAYS_MIN"));
    }

    public static float getTerminationsSoldiersAbsenceStartYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_ABSENCE_START_YEARS_MIN"));
    }

    public static float getTerminationsSoldiersAbsenceStartYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_ABSENCE_START_YEARS_MAX"));
    }

    public static float getTerminationsSoldiersAbsenceServiceTerminationDaysMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_ABSENCE_SERVICE_TERMINATION_DAYS_MAX"));
    }

    public static float getTerminationsSoldiersLossServiceTerminationYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_LOSS_SERVICE_TERMINATION_YEARS_MIN"));
    }

    public static float getTerminationsSoldiersLossServiceTerminationYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_LOSS_SERVICE_TERMINATION_YEARS_MAX"));
    }

    public static float getTerminationsSoldiersLackMedicalServiceTerminationYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_LACK_MEDICAL_SERVICE_TERMINATION_YEARS_MIN"));
    }

    public static float getTerminationsSoldiersLackMedicalServiceTerminationYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_LACK_MEDICAL_SERVICE_TERMINATION_YEARS_MAX"));
    }

    public static float getTerminationsSoldiersForeignerMarriageServiceTerminationYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_FOREIGNER_MARRIAGE_SERVICE_TERMINATION_YEARS_MIN"));
    }

    public static float getTerminationsSoldiersForeignerMarriageServiceTerminationYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_FOREIGNER_MARRIAGE_SERVICE_TERMINATION_YEARS_MAX"));
    }

    public static float getTerminationsSoldiersForeignerMarriageRecruitmentTerminationYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_FOREIGNER_MARRIAGE_RECRUITMENT_TERMINATION_YEARS_MAX"));
    }

    public static float getTerminationsSoldiersDisqualificationMilitaryRecruitmentRecordDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_SOLDIERS_DISQUALIFICATION_MILITARY_RECRUITMENT_RECORD_DATE_YEARS_MAX"));
    }

    public static int getTerminationsSoldiersTerminationExtensionRegistrationMax() {
	return Integer.parseInt(configurationMap.get("STE_SOLDIERS_TERMINATION_EXTENSION_REGISTRATION_MAX"));
    }

    public static float getTerminationsCiviliansPeriodUntilDesiredTerminationDateDaysMin() {
	return Integer.parseInt(configurationMap.get("STE_CIVILIANS_PERIOD_UNTIL_DESIRED_TERMINATION_DATE_DAYS_MIN"));
    }

    public static float getTerminationsCiviliansPeriodUntilDesiredTerminationDateDaysMax() {
	return Integer.parseInt(configurationMap.get("STE_CIVILIANS_PERIOD_UNTIL_DESIRED_TERMINATION_DATE_DAYS_MAX"));
    }

    public static float getTerminationsCiviliansRecordDesiredTerminationDateRecordDateDiffYearsMin() {
	return Integer.parseInt(configurationMap.get("STE_CIVILIANS_RECORD_DESIRED_TERMINATION_DATE_RECORD_DATE_DIFF_YEARS_MIN"));
    }

    public static float getTerminationsCiviliansRecordDesiredTerminationDateRecordDateDiffYearsMax() {
	return Integer.parseInt(configurationMap.get("STE_CIVILIANS_RECORD_DESIRED_TERMINATION_DATE_RECORD_DATE_DIFF_YEARS_MAX"));
    }

    public static int getTerminationsCiviliansDisclaimerDateDaysMax() {
	return Integer.parseInt(configurationMap.get("STE_CIVILIANS_DISCLAIMER_DATE_DAYS_MAX"));
    }

    public static float getTerminationsCiviliansTerminationRequestReqDesiredDiffYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_TERMINATION_REQUEST_REQ_DESIRED_DIFF_YEARS_MIN"));
    }

    public static float getTerminationsCiviliansServicePeriodRetirementYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_SERVICE_PERIOD_RETIREMENT_YEARS_MIN"));
    }

    public static float getTerminationsCiviliansLackMedicalTerminationDateMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_LACK_MEDICAL_TERMINATION_DATE_MAX"));
    }

    public static float getTerminationsCiviliansAbsenceYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_ABSENCE_YEARS_MAX"));
    }

    public static float getTerminationsCiviliansAbsenceTerminationDateDaysMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_ABSENCE_TERMINATION_DATE_DAYS_MAX"));
    }

    public static float getTerminationsCiviliansAbsenceContinuousDaysMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_ABSENCE_CONTINUOUS_DAYS_MIN"));
    }

    public static float getTerminationsCiviliansAbsenceDiscontinuousDaysMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_ABSENCE_DISCONTINUOUS_DAYS_MIN"));
    }

    public static float getTerminationsCiviliansDismissDateTerminationDiffMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_DISMISS_DATE_TERMINATION_DIFF_MAX"));
    }

    public static float getTerminationsCiviliansDismissServiceDateRecordDateDiffMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_DISMISS_SERVICE_DATE_RECORD_DATE_DIFF_MAX"));
    }

    public static float getTerminationsCiviliansPersonsDismissPublicInterestTerminationDateYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_PERSONS_DISMISS_PUBLIC_INTEREST_TERMINATION_DATE_YEARS_MIN"));
    }

    public static float getTerminationsCiviliansPersonsDismissPublicInterestTerminationDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_PERSONS_DISMISS_PUBLIC_INTEREST_TERMINATION_DATE_YEARS_MAX"));
    }

    public static float getTerminationsCiviliansDisqualificationMilitaryServiceDateRecordDateDiffYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_DISQUALIFICATION_MILITARY_SERVICE_DATE_RECORD_DATE_DIFF_YEARS_MIN"));
    }

    public static float getTerminationsCiviliansDisqualificationMilitaryServiceDateRecordDateDiffYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_DISQUALIFICATION_MILITARY_SERVICE_DATE_RECORD_DATE_DIFF_YEARS_MAX"));
    }

    public static float getTerminationsCiviliansJobCancelationDateYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_JOB_CANCELATION_DATE_YEARS_MIN"));
    }

    public static float getTerminationsCiviliansJobCancelationDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_JOB_CANCELATION_DATE_YEARS_MAX"));
    }

    public static float getTerminationsCiviliansJobCancelationTerminationDateYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_JOB_CANCELATION_TERMINATION_DATE_YEARS_MIN"));
    }

    public static float getTerminationsCiviliansJobCancelationTerminationDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_JOB_CANCELATION_TERMINATION_DATE_YEARS_MAX"));
    }

    public static float getTerminationsCiviliansDeathDeathDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_DEATH_DEATH_DATE_YEARS_MAX"));
    }

    public static float getTerminationsCiviliansNationalityLossTerminationDateYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_NATIONALITY_LOSS_TERMINATION_DATE_YEARS_MIN"));
    }

    public static float getTerminationsCiviliansNationalityLossTerminationDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_CIVILIANS_NATIONALITY_LOSS_TERMINATION_DATE_YEARS_MAX"));
    }

    public static int getTerminationsCiviliansTerminationExtensionRegistrationMax() {
	return Integer.parseInt(configurationMap.get("STE_CIVILIANS_TERMINATION_EXTENSION_REGISTRATION_MAX"));
    }

    public static float getTerminationsContractorsEndContractTerminationDateYearsMin() {
	return Float.parseFloat(configurationMap.get("STE_CONTRACTORS_END_CONTRACT_TERMINATION_DATE_YEARS_MIN"));
    }

    public static float getTerminationsContractorsEndContractTerminationDateYearsMax() {
	return Float.parseFloat(configurationMap.get("STE_CONTRACTORS_END_CONTRACT_TERMINATION_DATE_YEARS_MAX"));
    }

    /*------------------------------------------------------Training Configurations------------------------------------------------------*/
    public static String getTrainingAllowance() {
	return configurationMap.get("TRN_TRAINING_ALLOWANCE");
    }

    /*--------------------------------------------- Vacations Configurations ------------------------------------------------------------*/

    public static int getVacationMaxNumberOfCountriesForSoldiersPerRequest() {
	return Integer.parseInt(configurationMap.get("VAC_MAX_NUMBER_OF_COUNTERIES_FOR_SOLDIERS_PER_REQUEST"));
    }

    public static int getVacationRequestAllowance() {
	return Integer.parseInt(configurationMap.get("VAC_REQUEST_ALLOWANCE"));
    }

    public static int getVacationEmployeesServiceYears() {
	return Integer.parseInt(configurationMap.get("VAC_EMPLOYEES_SERVICE_YEARS"));
    }

    public static int getVacationEmployeesAgeYears() {
	return Integer.parseInt(configurationMap.get("VAC_EMPLOYEES_AGE_YEARS"));
    }

    public static int getVacationRequestRequireJoining() {
	return Integer.parseInt(configurationMap.get("VAC_REQUEST_REQUIRE_JOINING"));
    }

    public static int getVacationMaxSickVacationPeriodApprovedDirectly() {
	return Integer.parseInt(configurationMap.get("VAC_MAX_SICK_VAC_PERIOD_APPROVED_DIRECTLY"));
    }

    public static long getVacationOfficersExceptionalDmAllVpRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_OFFICERS_EXCEPTIONAL_DM_ALL_VP_RANKS_GT"));
    }

    public static long getVacationCiviliansExceptionalStudyDmAllVpRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_CIVILIANS_EXCEPTIONAL_STUDY_DM_ALL_VP_RANKS_GT"));
    }

    public static long getVacationOfficersFieldSmRegionsRcRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_OFFICERS_FIELD_SM_REGIONS_RC_RANKS_GT"));
    }

    public static long getVacationOfficersAllExceptFieldAndExceptionalSmHqVpRanksLessThan() {
	return Long.parseLong(configurationMap.get("VAC_OFFICERS_ALL_EXCEPT_FIELD_EXCEPTIONAL_SM_HQ_VP_RANKS_LT"));
    }

    public static long getVacationOfficersAllExceptFieldAndExceptionalSmHqAssistantRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_OFFICERS_ALL_EXCEPT_FIELD_EXCEPTIONAL_SM_HQ_ASSISTANT_RANKS_GT"));
    }

    public static long getVacationOfficersAllExceptFieldAndExceptionalSmRegionsVpRanksLessThan() {
	return Long.parseLong(configurationMap.get("VAC_OFFICERS_ALL_EXCEPT_FIELD_EXCEPTIONAL_SM_REGIONS_VP_RANKS_LT"));
    }

    public static long getVacationOfficersAllExceptFieldAndExceptionalSmRegionsRcRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_OFFICERS_ALL_EXCEPT_FIELD_EXCEPTIONAL_SM_REGIONS_RC_RANKS_GT"));
    }

    public static long getVacationCiviliansAllExceptStudyAndExceptionalSmHqVpRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_CIVILIANS_ALL_EXCEPT_STUDY_EXCEPTIONAL_SM_HQ_VP_RANKS_GT"));
    }

    public static long getVacationCiviliansAllExceptStudyAndExceptionalSmHqExecFinAffGmRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_CIVILIANS_ALL_EXCEPT_STUDY_EXCEPTIONAL_SM_HQ_EXEC_FIN_AFF_GM_RANKS_GT"));
    }

    public static long getVacationCiviliansAllExceptStudyAndExceptionalSmHqEmpAffGmRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_CIVILIANS_ALL_EXCEPT_STUDY_EXCEPTIONAL_SM_HQ_EMP_AFF_GM_RANKS_GT"));
    }

    public static long getVacationCiviliansAllExceptStudyAndExceptionalSmRegionsRcRanksGreaterThan() {
	return Long.parseLong(configurationMap.get("VAC_CIVILIANS_ALL_EXCEPT_STUDY_EXCEPTIONAL_SM_REGIONS_RC_RANKS_GT"));
    }

    public static int getFirstPeriodRemainingBalanceForRulesOneAndTwo() {
	return Integer.parseInt(configurationMap.get("FIRST_PERIOD_REMAINING_BALANCE_RULE_1_2"));
    }

    public static int getThirdPeriodMaxBalanceToBeCompensatedForRuleOne() {
	return Integer.parseInt(configurationMap.get("THIRD_PERIOD_MAX_BALANCE_COMPENSATED_RULE_1"));
    }

    public static int getFirstAndSecondPeriodsMaxBalanceToBeCompensatedForRuleTwo() {
	return Integer.parseInt(configurationMap.get("FIRST_SECOND_PERIOD_MAX_BALANCE_COMPENSATED_RULE_2"));
    }

    public static int getThirdPeriodMaxBalanceToBeCompensatedForRuleTwo() {
	return Integer.parseInt(configurationMap.get("THIRD_PERIOD_MAX_BALANCE_COMPENSATED_RULE_2"));
    }

    public static int getSecondAndThirdPeriodsMaxBalanceToBeCompensatedForRulesThreeAndFour() {
	return Integer.parseInt(configurationMap.get("SECOND_THIRD_PERIOD_MAX_BALANCE_COMPENSATED_RULE_3_4"));
    }

    public static int getSecondPeriodMaxBalanceToBeCompensatedForRuleFive() {
	return Integer.parseInt(configurationMap.get("SECOND_PERIOD_MAX_BALANCE_COMPENSATED_RULE_5"));
    }

    /*--------------------------------------------- End of Vacations Configurations -----------------------------------------------------*/

    /*--------------------------------------------Start of Movements Configurations------------------------------------------------------*/

    public static int getMovementsSoldiersSubjoinPeriodDaysMax() {
	return Integer.parseInt(configurationMap.get("MVT_SOLDIERS_SUBJOIN_PERIOD_DAYS_MAX"));
    }

    public static int getMovementsPersonsSubjoinPeriodDaysMax() {
	return Integer.parseInt(configurationMap.get("MVT_PERSONS_SUBJOIN_PERIOD_DAYS_MAX"));
    }

    public static int getMovementsPersonsMinPeriodDaysToMove() {
	return Integer.parseInt(configurationMap.get("MVT_PERSONS_MIN_PERIOD_DAYS_TO_MOVE"));
    }

    public static int getMovementsPersonsMinPeriodDaysToSubjoin() {
	return Integer.parseInt(configurationMap.get("MVT_PERSONS_MIN_PERIOD_DAYS_TO_SUBJOIN"));
    }

    /*--------------------------------------------End of Movements Configurations------------------------------------------------------*/

    /*------------------------------------------------------Start of Retirement Configurations------------------------------------------------------*/
    public static String getDisclaimerUnitsIds(Long physicalRegionId, Long categoryId) throws BusinessException {
	String unitsIds;
	if (physicalRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
	    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
		unitsIds = configurationMap.get("DISCLAIMER_GENERAL_DIRECTORATE_OF_BORDER_GUARDS_UNITS_IDS_OFFICERS");
	    } else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
		unitsIds = configurationMap.get("DISCLAIMER_GENERAL_DIRECTORATE_OF_BORDER_GUARDS_UNITS_IDS_SOLDIERS");
	    } else
		throw new BusinessException("error_transactionDataError");
	} else if (physicalRegionId == RegionsEnum.ALJOOF_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_ALJOOF_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.ALMADEENAH_ALMONAWRAH_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_ALMADEENAH_ALMONAWRAH_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.ASEER_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_ASEER_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.EASTERN_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_EASTERN_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.JAZAN_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_JAZAN_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.MAKKAH_ALMOKARRAMAH_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_MAKKAH_ALMOKARRAMAH_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.NAJRAN_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_NAJRAN_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.NORTHERN_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_NORTHERN_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.TABOOK_REGION.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_TABOOK_REGION_UNITS_IDS");
	} else if (physicalRegionId == RegionsEnum.ACADEMY.getCode()) {
	    unitsIds = configurationMap.get("DISCLAIMER_ACADEMY_UNITS_IDS");
	} else
	    throw new BusinessException("error_transactionDataError");
	return unitsIds;
    }

    public static String getEServicesURL() {
	return configurationMap.get("ESERVICES_URL");
    }

    public static String getEServicesFlag() {
	return configurationMap.get("ESERVICES_FLAG");
    }

    /*------------------------------------------------------End of Retirement Configurations------------------------------------------------------*/
    /*------------------------------------------------------Integration codes for external systems------------------------------------------------------*/
    public static String getExternalServiceAuthValue(String externalServiceAuthCode) {
	return getETRConfigByCode(externalServiceAuthCode).getValue();
    }

    public static String getExternalServiceSoldiersRecruitmentsAuthCode() {
	return "EXTERNAL_SERVICE_SOLDIERS_RECRUITMENTS_AUTH_CODE";
    }

    public static void setExternalServiceAuthValue(String externalServiceAuthCode, String externalServiceAuthValue) {
	setConfigValue(externalServiceAuthCode, externalServiceAuthValue, null);
    }

    public static String getYaqeenSystemCode() {
	return getETRConfigByCode("SYSTEM_CODE_FOR_YAQEEN").getValue();
    }

    public static String getYaqeenWsdlLocation() {
	return getETRConfigByCode("YAKEEN_FOR_BORDER_GUARD_WSDL_LOCATION").getValue();
    }

    public static Integer getIntegrationWithAllowanceAndDeductionFlag() {
	return Integer.parseInt(configurationMap.get("INTEGRATION_WITH_ALLOWANCE_AND_DEDUCTION_FLAG"));
    }
}