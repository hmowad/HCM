package com.code.enums;

public enum TransactionTypesEnum {

    /************************** Employees ***********************************/
    EMPLOYEES_EXTRA_DATA_SOCIAL_STATUS(10),
    EMPLOYEES_EXTRA_DATA_RANK_TITLE(20),
    EMPLOYEES_EXTRA_DATA_GENERAL_SPECIALIZATION(30),
    EMPLOYEES_EXTRA_DATA_SALARY_RANK(40),
    EMPLOYEE_MEDICAL_STAFF_DATA(50),

    /************************** Units ***********************************/
    UNIT_CONSTRUCTION(10),
    UNIT_NAME_MODIFICATION(20),
    UNIT_MOVE(30),
    UNIT_MERGE(40),
    UNIT_SEPARATION(50),
    UNIT_SCALE_UP(60),
    UNIT_SCALE_DOWN(70),
    UNIT_CANCELLATION(80),

    ORG_TABLE_CONSTRUCTION(100),
    ORG_TABLE_MODIFICATION(110),
    ORG_TABLE_CANCELLATION(120),
    TARGET_TASK_CONSTRUCTION(130),
    TARGET_TASK_MODIFICATION(140),
    TARGET_TASK_CANCELLATION(150),
    /************************** Jobs ************************************/
    JOB_CONSTRUCTION(10),
    JOB_RENAME(20),
    JOB_MODIFY_MINOR_SPECIALIZATION(25),
    JOB_MOVE(30),
    JOB_FREEZE(45),
    JOB_UNFREEZE(55),
    JOB_SCALE_UP(60),
    JOB_SCALE_DOWN(70),
    JOB_CANCELLATION(80),
    JOB_RESERVE(85),
    JOB_UNRESERVE(90),
    /************************** Vacations *******************************/
    VACATION_CONSTRUCTION(1),
    VACATION_MODIFICATION(2),
    VACATION_CANCELLATION(4),
    /************************** Recruitment *****************************/
    RECRUITMENT_CREATION(10),

    /************************** Missions *****************************/
    MISSION_DECISION(10),
    MISSION_PAYMENT(20),
    MISSION_FINANCIAL_LINK(30),

    /************************** Movement ********************************/
    MVT_NEW_DECISION(1),
    MVT_EXTENSION_DECISION(2),
    MVT_TERMINATION_DECISION(3),
    MVT_CANCEL_DECISION(4),
    /************************** TERMINATION **********************************/
    STE_TERMINATION(10),
    STE_EXTENSTION(20),
    STE_TERMINATION_MOVEMENT(30),
    STE_TERMINATION_CANCELLATION(40),

    /************************** RETIREMENT **********************************/
    RET_DISCLAIMER_DESCISION(75),

    /************************** TRAINING **********************************/
    // For scholarship decisions
    TRN_NEW_DECISION(1),
    TRN_EXTENSION_DECISION(2),
    TRN_TERMINATION_DECISION(3),
    TRN_CANCELLATION_DECISION(4),

    // For courses event decisions
    TRN_COURSE_EVENT_NEW_DECISION(11),
    TRN_COURSE_EVENT_POSTPONE_DECISION(12),
    TRN_COURSE_EVENT_CANCEL_DECISION(13),
    TRN_COURSE_EVENT_MODIFY_DECISION(14),
    // For Electronic certifications
    TRN_COURSE_EVENT_RESULTS(10),

    // For trainee course cancel
    TRN_TRAINEE_COURSE_EVENT_CANCEL(20),

    // For training course syllabus attachment
    TRN_SYLLABUS_ATTACHMENT_APPROVE_DECISION(30),
    TRN_SYLLABUS_ATTACHMENT_CANCEL_DECISION(31);

    private int code;

    private TransactionTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
