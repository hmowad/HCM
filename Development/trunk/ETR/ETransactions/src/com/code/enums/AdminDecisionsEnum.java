package com.code.enums;

public enum AdminDecisionsEnum {

    /****************************************** Disclaimer ********************************************/
    OFFICERS_DISCLAIMER(1),
    SOLDIERS_DISCLAIMER(12),

    /****************************************** Recruitment ********************************************/
    OFFICERS_RECRUITMENT(11),

    /****************************************** Employees ********************************************/
    OFFICERS_REGISTRATION(10),

    /****************************************** Vacations ********************************************/
    OFFICERS_REGULAR_VACATION_REQUEST(2),
    OFFICERS_COMPELLING_VACATION_REQUEST(5),
    OFFICERS_SICK_VACATION_REQUEST(7),
    SOLDIERS_REGULAR_VACATION_REQUEST(30),
    SOLDIERS_COMPELLING_VACATION_REQUEST(31),
    /****************************************** Termination ********************************************/
    CIVILLIANS_TERMINATION_RECORD(14),

    /****************************************** Movements ********************************************/
    OFFICERS_MOVE_DECISION_REQUEST(18),
    OFFICERS_MOVE_REGISTERATION(25),
    OFFICERS_SUBJOIN_DECISION_REQUEST(19),
    OFFICERS_SUBJOIN_REGISTERATION(26),
    OFFICERS_INTERNAL_ASSIGNMENT_DECISION_REQUEST(22),
    OFFICERS_EXTERNAL_ASSIGNMENT_REGISTRATION(29),
    OFFICERS_MOVE_JOINING_DATE_REQUEST(23),
    OFFICERS_SUBJOIN_JOINING_DATE_REQUEST(24);

    private long code;

    private AdminDecisionsEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
