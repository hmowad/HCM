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
    OFFICERS_MODIFY_REGULAR_VACATION_REQUEST(3),
    OFFICERS_CANCEL_REGULAR_VACATION_REQUEST(4),
    OFFICERS_COMPELLING_VACATION_REQUEST(5),
    OFFICERS_CANCEL_COMPELLING_VACATION_REQUEST(6),
    OFFICERS_SICK_VACATION_REQUEST(7),
    OFFICERS_MODIFY_SICK_VACATION_REQUEST(8),
    OFFICERS_CANCEL_SICK_VACATION_REQUEST(9),
    SOLDIERS_REGULAR_VACATION_REQUEST(30),
    SOLDIERS_COMPELLING_VACATION_REQUEST(31),
    /****************************************** Termination ********************************************/
    SOLDIERS_TERMINATION_DECISION_CANCELLATION(13),
    CIVILLIANS_TERMINATION_RECORD(14),
    CIVILLIANS_TERMINATION_DECISION_CANCELLATION(15),
    /****************************************** Movements ********************************************/
    OFFICERS_MOVE_DECISION_REQUEST(18),
    OFFICERS_MOVE_REGISTERATION(25),
    OFFICERS_SUBJOIN_DECISION_REQUEST(19),
    OFFICERS_EXTEND_SUBJOIN_DECISION_REQUEST(20),
    OFFICERS_CANCEL_SUBJOIN_DECISION_REQUEST(33),
    OFFICERS_SUBJOIN_REGISTERATION(26),
    OFFICERS_EXTEND_SUBJOIN_REGISTERATION(27),
    OFFICERS_CANCEL_SUBJOIN_REGISTERATION(34),
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
