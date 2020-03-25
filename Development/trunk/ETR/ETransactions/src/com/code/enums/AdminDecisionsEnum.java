package com.code.enums;

public enum AdminDecisionsEnum {

    /****************************************** Disclaimer ********************************************/
    OFFICERS_DISCLAIMER(1),
    SOLDIERS_DISCLAIMER(12),

    /****************************************** Recruitment ********************************************/
    OFFICERS_RECRUITMENT(11),
    OFFICERS_RE_RECRUITMENT(51),
    OFFICERS_RECRUITMENT_BY_EXTERNAL_MOVE(52),

    /****************************************** Employees ********************************************/
    OFFICERS_REGISTRATION(10),

    /****************************************** Vacations ********************************************/
    OFFICERS_REGULAR_VACATION_REQUEST(2),
    OFFICERS_MODIFY_REGULAR_VACATION_REQUEST(3),
    OFFICERS_CANCEL_REGULAR_VACATION_REQUEST(4),
    OFFICERS_COMPELLING_VACATION_REQUEST(5),
    OFFICERS_CANCEL_COMPELLING_VACATION_REQUEST(6),
    OFFICERS_FULL_PAID_SICK_VACATION_REQUEST(35),
    OFFICERS_HALF_PAID_SICK_VACATION_REQUEST(36),
    OFFICERS_HALF_PAID_SICK_VACATION_JOINING(41),
    OFFICERS_MODIFY_FULL_PAID_SICK_VACATION_REQUEST(37),
    OFFICERS_MODIFY_HALF_PAID_SICK_VACATION_REQUEST(38),
    OFFICERS_CANCEL_FULL_PAID_SICK_VACATION_REQUEST(39),
    OFFICERS_CANCEL_HALF_PAID_SICK_VACATION_REQUEST(40),
    SOLDIERS_REGULAR_VACATION_REQUEST(30),
    SOLDIERS_COMPELLING_VACATION_REQUEST(31),
    /****************************************** Termination ********************************************/
    SOLDIERS_TERMINATION_DECISION_CANCELLATION(13),
    CIVILLIANS_TERMINATION_RECORD(14),
    CIVILLIANS_TERMINATION_DECISION_CANCELLATION(15),
    /****************************************** Movements ********************************************/
    OFFICERS_MOVE_DECISION_REQUEST(18),
    OFFICERS_MOVE_REQUEST(42),
    OFFICERS_MOVE_REGISTERATION(25),
    OFFICERS_SUBJOIN_DECISION_REQUEST(19),
    OFFICERS_SUBJOIN_REQUEST(43),
    OFFICERS_EXTEND_SUBJOIN_DECISION_REQUEST(20),
    OFFICERS_EXTEND_SUBJOIN_REQUEST(44),
    OFFICERS_CANCEL_SUBJOIN_DECISION_REQUEST(33),
    OFFICERS_CANCEL_SUBJOIN_REQUEST(46),
    OFFICERS_TERMINATE_SUBJOIN_DECISION_REQUEST(21),
    OFFICERS_TERMINATE_SUBJOIN_REQUEST(45),
    OFFICERS_SUBJOIN_REGISTERATION(26),
    OFFICERS_EXTEND_SUBJOIN_REGISTERATION(27),
    OFFICERS_CANCEL_SUBJOIN_REGISTERATION(34),
    OFFICERS_TERMINATE_SUBJOIN_REGISTERATION(28),
    OFFICERS_INTERNAL_ASSIGNMENT_DECISION_REQUEST(22),
    OFFICERS_EXTERNAL_ASSIGNMENT_REGISTRATION(29),
    OFFICERS_MOVE_JOINING_DATE_REQUEST(23),
    OFFICERS_SUBJOIN_JOINING_DATE_REQUEST(24),
    OFFICERS_SUBJOIN_RETURN_JOINING_DATE_REQUEST(47),
    /****************************************** Promotion ********************************************/
    OFFICERS_PROMOTION_REPORT(50),

    /****************************************** Raises ********************************************/
    OFFICERS_ANNUAL_RAISE(48);

    private long code;

    private AdminDecisionsEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
