package com.code.enums;

/**
 * This enumeration contains a unique menu code string for every menu item.
 * <p>
 * Naming convention as follows:<br>
 * Constant: Module abbreviation + "_" + Screen name<br>
 * Value: "ETR_HCM_" + Module abbreviation + "_" + Screen name (Except ETR menus "ETR_ not ETR_HCM_").
 */
public enum MenuCodesEnum {

    // ETR_menus
    ETR_DECISION_TEMPLATES("ETR_DECISION_TEMPLATES"),
    ETR_SIGNATURES_MANAGEMENT("ETR_SIGNATURES_MANAGEMENT"),
    ETR_STAMPS_MANAGEMENT("ETR_STAMPS_MANAGEMENT"),
    ETR_SYSTEM_CONFIGURATIONS("ETR_SYSTEM_CONFIGURATIONS"),
    // WF menus
    WF_OUTBOX("ETR_HCM_WF_OUTBOX"),
    WF_INBOX("ETR_HCM_WF_INBOX"),
    WF_NOTIFICATION_MESSAGES("ETR_HCM_WF_NOTIFICATION_MESSAGES"),

    // VAC menus
    VAC_VACATIONS_CONFIGURATIONS("ETR_HCM_VAC_VACATIONS_CONFIGURATIONS"),
    VAC_BALANCE_INQUIRY("ETR_HCM_VAC_BALANCE_INQUIRY"),
    VAC_VACATIONS_SEARCH_FOR_OFFICERS("ETR_HCM_VAC_VACATIONS_SEARCH_FOR_OFFICERS"),
    VAC_VACATIONS_SEARCH_FOR_SOLDIERS("ETR_HCM_VAC_VACATIONS_SEARCH_FOR_SOLDIERS"),
    VAC_VACATIONS_SEARCH_FOR_EMPLOYEES("ETR_HCM_VAC_VACATIONS_SEARCH_FOR_EMPLOYEES"),
    VAC_OFFICERS_REGULAR_VACATION("ETR_HCM_VAC_OFFICERS_REGULAR_VACATION"),
    VAC_SOLDIERS_REGULAR_VACATION("ETR_HCM_VAC_SOLDIERS_REGULAR_VACATION"),
    VAC_EMPLOYEES_REGULAR_VACATION("ETR_HCM_VAC_EMPLOYEES_REGULAR_VACATION"),
    VAC_OFFICERS_COMPELLING_VACATION("ETR_HCM_VAC_OFFICERS_COMPELLING_VACATION"),
    VAC_SOLDIERS_COMPELLING_VACATION("ETR_HCM_VAC_SOLDIERS_COMPELLING_VACATION"),
    VAC_EMPLOYEES_COMPELLING_VACATION("ETR_HCM_VAC_EMPLOYEES_COMPELLING_VACATION"),
    VAC_CONTRACTORS_WITHOUT_SALARY_VACATION("ETR_HCM_VAC_CONTRACTORS_WITHOUT_SALARY_VACATION"),
    VAC_OFFICERS_SICK_VACATION("ETR_HCM_VAC_OFFICERS_SICK_VACATION"),
    VAC_SOLDIERS_SICK_VACATION("ETR_HCM_VAC_SOLDIERS_SICK_VACATION"),
    VAC_SOLDIERS_WORK_INJURY_SICK_VACATION("ETR_HCM_VAC_SOLDIERS_WORK_INJURY_SICK_VACATION"),
    VAC_EMPLOYEES_SICK_VACATION("ETR_HCM_VAC_EMPLOYEES_SICK_VACATION"),

    VAC_OFFICERS_FIELD_VACATION("ETR_HCM_VAC_OFFICERS_FIELD_VACATION"),
    VAC_SOLDIERS_FIELD_VACATION("ETR_HCM_VAC_SOLDIERS_FIELD_VACATION"),
    VAC_OFFICERS_EXCEPTIONAL_VACATION("ETR_HCM_VAC_OFFICERS_EXCEPTIONAL_VACATION"),
    VAC_SOLDIERS_EXCEPTIONAL_VACATION("ETR_HCM_VAC_SOLDIERS_EXCEPTIONAL_VACATION"),
    VAC_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION("ETR_HCM_VAC_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION"),
    VAC_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION("ETR_HCM_VAC_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION"),
    VAC_EMPLOYEES_ACCOMPANY_VACATION("ETR_HCM_VAC_EMPLOYEES_ACCOMPANY_VACATION"),
    VAC_EMPLOYEES_STUDY_VACATION("ETR_HCM_VAC_EMPLOYEES_STUDY_VACATION"),
    VAC_SOLDIERS_EXAM_VACATION("ETR_HCM_VAC_SOLDIERS_EXAM_VACATION"),
    VAC_EMPLOYEES_EXAM_VACATION("ETR_HCM_VAC_EMPLOYEES_EXAM_VACATION"),
    VAC_SOLDIERS_MATERNITY_VACATION("ETR_HCM_VAC_SOLDIERS_MATERNITY_VACATION"),
    VAC_EMPLOYEES_MATERNITY_VACATION("ETR_HCM_VAC_EMPLOYEES_MATERNITY_VACATION"),
    VAC_SOLDIERS_MOTHERHOOD_VACATION("ETR_HCM_VAC_SOLDIERS_MOTHERHOOD_VACATION"),
    VAC_EMPLOYEES_MOTHERHOOD_VACATION("ETR_HCM_VAC_EMPLOYEES_MOTHERHOOD_VACATION"),
    VAC_SOLDIERS_DEATH_WAITING_PERIOD_VACATION("ETR_HCM_VAC_SOLDIERS_DEATH_WAITING_PERIOD_VACATION"),
    VAC_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION("ETR_HCM_VAC_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION"),
    VAC_EMPLOYEES_RELIEF_VACATION("ETR_HCM_VAC_EMPLOYEES_RELIEF_VACATION"),
    VAC_EMPLOYEES_SPORTIVE_VACATION("ETR_HCM_VAC_EMPLOYEES_SPORTIVE_VACATION"),
    VAC_EMPLOYEES_CULTURAL_VACATION("ETR_HCM_VAC_EMPLOYEES_CULTURAL_VACATION"),
    VAC_EMPLOYEES_SOCIAL_VACATION("ETR_HCM_VAC_EMPLOYEES_SOCIAL_VACATION"),
    VAC_EMPLOYEES_COMPENSATION_VACATION("ETR_HCM_VAC_EMPLOYEES_COMPENSATION_VACATION"),
    VAC_EMPLOYEES_ORPHAN_CARE_VACATION("ETR_HCM_VAC_EMPLOYEES_ORPHAN_CARE_VACATION"),
    VAC_EMPLOYEES_DEATH_OF_RELATIVE_VACATION("ETR_HCM_VAC_EMPLOYEES_DEATH_OF_RELATIVE_VACATION"),
    VAC_EMPLOYEES_NEW_BABY_VACATION("ETR_HCM_VAC_EMPLOYEES_NEW_BABY_VACATION"),

    // PRS menus
    EMPS_DATA_VIEW("ETR_HCM_EMPS_DATA_VIEW"),

    EMPS_OFFICER_FILE("ETR_HCM_EMPS_OFFICER_FILE"),
    EMPS_SOLDIER_FILE("ETR_HCM_EMPS_SOLDIER_FILE"),
    EMPS_CIVILIAN_FILE("ETR_HCM_EMPS_CIVILIAN_FILE"),

    EMPS_FILE_REPORT("ETR_HCM_EMPS_FILE_REPORT"),

    EMPS_COMMUNCATIONS_DATA_VIEW("ETR_HCM_EMPS_COMMUNICATIONS_DATA"),

    EMPS_OFFICER_DATA_REPORTS_VIEW("ETR_HCM_EMPS_OFFICER_DATA_REPORTS_VIEW"),
    EMPS_SOLDIER_DATA_REPORTS_VIEW("ETR_HCM_EMPS_SOLDIER_DATA_REPORTS_VIEW"),
    EMPS_CIVILIAN_DATA_REPORTS_VIEW("ETR_HCM_EMPS_CIVILIAN_DATA_REPORTS_VIEW"),

    // Additional specializations menus
    EMPS_ADDITIONAL_SPECS("ETR_HCM_EMPS_ADDITIONAL_SPECS"),

    // REC menus
    REC_NEW_OFFICER_REGISTRATION("ETR_HCM_REC_NEW_OFFICER_REGISTRATION"),
    REC_NEW_SOLDIER_REGISTRATION("ETR_HCM_REC_NEW_SOLDIER_REGISTRATION"),
    REC_NEW_CIVILIAN_REGISTRATION("ETR_HCM_REC_NEW_CIVILIAN_REGISTRATION"),
    REC_RECRUITMENT_WISHES("ETR_HCM_REC_RECRUITMENT_WISHES"),

    // MVT menus
    MVT_INTERNAL_ASSIGNMENT_REGISTRATION_OFFICERS("ETR_HCM_MVT_INTERNAL_ASSIGNMENT_REGISTRATION_OFFICERS"),
    MVT_INTERNAL_ASSIGNMENT_REGISTRATION_SOLDIERS("ETR_HCM_MVT_INTERNAL_ASSIGNMENT_REGISTRATION_SOLDIERS"),
    MVT_INTERNAL_ASSIGNMENT_REGISTRATION_CIVILIANS("ETR_HCM_MVT_INTERNAL_ASSIGNMENT_REGISTRATION_CIVILIANS"),
    MVT_MOVE_WISH_REQUEST("ETR_HCM_MVT_MOVE_WISH_REQUEST"),
    MVT_MOVE_WISH_MODIFICATION_REQUEST("ETR_HCM_MVT_MOVE_WISH_MODIFICATION_REQUEST"),
    MVT_MOVE_WISH_CANCELLATION_REQUEST("ETR_HCM_MVT_MOVE_WISH_CANCELLATION_REQUEST"),
    // PRM menus
    PRM_OFFICERS_REPORT("ETR_HCM_PRM_OFFICERS_REPORT"),
    PRM_SOLDIERS_CANCELLATION("ETR_HCM_PRM_SOLDIERS_CANCELLATION"),

    // MSN Menus
    MSN_BALANCE_INQUIRY("ETR_HCM_MSN_BALANCE_INQUIRY"),
    MSN_REQUEST_OFFICERS_SEND("ETR_HCM_MSN_REQUEST_OFFICERS_SEND"),
    MSN_REQUEST_SOLDIERS_SEND("ETR_HCM_MSN_REQUEST_SOLDIERS_SEND"),
    MSN_REQUEST_PERSONS_SEND("ETR_HCM_MSN_REQUEST_PERSONS_SEND"),
    MSN_INQUIRY_REPORT("ETR_HCM_MISSION_STATISTICAL_REPORT"),

    // DEF LETTER
    DEF_LETTER("ETR_HCM_DEFINITION_LETTER"),

    // Jobs Menu Codes
    JOBS_REPORTS_FOR_OFFICERS("ETR_HCM_JOBS_REPORTS_FOR_OFFICERS"),
    JOBS_REPORTS_FOR_SOLDIERS("ETR_HCM_JOBS_REPORTS_FOR_SOLDIERS"),
    JOBS_REPORTS_FOR_CIVILIANS("ETR_HCM_JOBS_REPORTS_FOR_CIVILIANS"),

    JOBS_BALANCES_FOR_OFFICERS("ETR_HCM_JOBS_BALANCES_FOR_OFFICERS"),
    JOBS_BALANCES_FOR_SOLDIERS("ETR_HCM_JOBS_BALANCES_FOR_SOLDIERS"),
    JOBS_RANKS_POWER_MANAGMENT("ETR_HCM_JOBS_RANKS_POWER_MANAGMENT"),

    // Training Menu Codes
    TRN_MILITARY_COURSES_REGISTERATION("ETR_HCM_TRN_MILITARY_COURSES_REGISTERATION"),
    TRN_INTERNAL_MILITARY_CLAIM_REQUEST("ETR_HCM_TRN_INTERNAL_MILITARY_CLAIM_REQUEST"),
    TRN_EXTERNAL_MILITARY_CLAIM_REQUEST("ETR_HCM_TRN_EXTERNAL_MILITARY_CLAIM_REQUEST"),
    TRN_EVENING_CLAIM_REQUEST("ETR_HCM_TRN_EVENING_CLAIM_REQUEST"),
    TRN_MORNING_CLAIM_REQUEST("ETR_HCM_TRN_MORNING_CLAIM_REQUEST"),
    TRN_STUDY_CLAIM_REQUEST("ETR_HCM_TRN_STUDY_CLAIM_REQUEST"),
    TRN_SCHOLARSHIP_CLAIM_REQUEST("ETR_HCM_TRN_SCHOLARSHIP_CLAIM_REQUEST"),
    TRN_EMPLOYEE_TRAINING_FILE("ETR_HCM_TRN_EMPLOYEE_TRAINING_FILE"),
    TRN_INTERNAL_MILITARY_TRAINING_REQUEST("ETR_HCM_TRN_INTERNAL_MILITARY_TRAINING_REQUEST"),
    TRN_EXTERNAL_MILITARY_TRAINING_REQUEST("ETR_HCM_TRN_EXTERNAL_MILITARY_TRAINING_REQUEST"),
    TRN_INTERNAL_MILITARY_REPLACEMENT_REQUEST("ETR_HCM_TRN_INTERNAL_MILITARY_REPLACEMENT_REQUEST"),
    TRN_EXTERNAL_MILITARY_REPLACEMENT_REQUEST("ETR_HCM_TRN_EXTERNAL_MILITARY_REPLACEMENT_REQUEST"),
    TRN_INTERNAL_MILITARY_APOLOGY_REQUEST("ETR_HCM_TRN_INTERNAL_MILITARY_APOLOGY_REQUEST"),
    TRN_EXTERNAL_MILITARY_APOLOGY_REQUEST("ETR_HCM_TRN_EXTERNAL_MILITARY_APOLOGY_REQUEST"),
    TRN_EMPLOYEES_TRAINING_DECISIONS_INQUIRY("ETR_HCM_TRN_EMPLOYEES_TRAINING_DECISIONS_INQUIRY"),
    TRN_EMPLOYEES_TRAINING_TRANSACTIONS_INQUIRY("ETR_HCM_TRN_EMPLOYEES_TRAINING_TRANSACTIONS_INQUIRY"),
    // Martyrs Menu Codes
    MARTYRS_OFFICIERS_TRANSACTION_REGISTRATION("ETR_HCM_OFFICIERS_MARTYRS_TRANS_REGISTRATION"),
    MARTYRS_SOLDIERS_TRANSACTION_REGISTRATION("ETR_HCM_SOLDIERS_MARTYRS_TRANS_REGISTRATION"),

    RET_OFFICIERS_DISCLAIMER_REQUEST("ETR_HCM_RET_OFFICERS_DISCLAIMER_REQUEST"),
    RET_SOLDIERS_DISCLAIMER_REQUEST("ETR_HCM_RET_SOLDIERS_DISCLAIMER_REQUEST"),
    // Raises Menu Codes
    RAISES_ANNUAL_RAISES_REGISTRATION("ETR_HCM_ANNUAL_RAISES_REGISTRATION"),
    RAISES_ADDITIONAL_RAISES_REGISTERATION("ETR_HCM_RAISES_ADDITIONAL_RAISES_REGISTERATION");

    private String code;

    private MenuCodesEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
