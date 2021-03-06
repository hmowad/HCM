package com.code.enums;

public enum TerminationReasonsEnum {

    OFFICERS_REACHING_RETIREMENT_AGE(5),
    OFFICERS_COMPLETION_PERIOD_CURRENT_RANK(10),
    OFFICERS_RETIREMENT(12),
    OFFICERS_TERMINATION_REQUEST(15),
    OFFICERS_DISPENSING_SERVICES(20),
    OFFICERS_DISMISS_MILITARY_SERVICE(25),
    OFFICERS_JUDGMENT(30),
    OFFICERS_LACK_MEDICAL_FITNESS(35),
    OFFICERS_NATIONALITY_LOSS(40),
    OFFICERS_DEATH_LOSS(45),
    OFFICERS_ABSENCE(50),

    SOLDIERS_REACHING_RETIREMENT_AGE(55),
    SOLDIERS_TERMINATION_REQUEST(60),
    SOLDIERS_DISPENSING_SERVICES(65),
    SOLDIERS_DISMISS(70),
    SOLDIERS_DEATH(75),
    SOLDIERS_ABSENCE(80),
    SOLDIERS_LOSS(85),
    SOLDIERS_LACK_MEDICAL_FITNESS(90),
    SOLDIERS_FOREIGNER_MARRIAGE(95),
    SOLDIERS_DISQUALIFICATION_MILITARY_SERVICE(100),

    PERSONS_REACHING_RETIREMENT_AGE(105),
    PERSONS_TERMINATION_REQUEST(110),
    PERSONS_LACK_MEDICAL_FITNESS(115),
    PERSONS_ABSENCE(120),
    PERSONS_JUDGMENT(125),
    PERSONS_DISMISS_PUBLIC_INTEREST(130),
    PERSONS_DISQUALIFICATION(135),
    PERSONS_JOB_CANCELLATION(140),
    PERSONS_DEATH(145),
    PERSONS_NATIONALITY_LOSS(150),

    CONTRACTORS_END_CONTRACT(305);

    private long code;

    private TerminationReasonsEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }

}
