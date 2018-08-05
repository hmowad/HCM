package com.code.enums;

public enum WFProcessesGroupsEnum {
    VACATIONS(5),
    DEFINITION_LETTERS(10),
    RECRUITMENTS(20),
    MOVEMENTS(30),
    MISSIONS(50),
    PROMOTIONS(60),
    MANPOWER_NEEDS(70),
    TERMINATIONS(80),
    TRAINING_AND_SCHOLARSHIP(90),
    RETIREMENTS(100),
    ORG_JOBS(150),
    GENERAL_NOTIFICATIONS(200);

    private int code;

    private WFProcessesGroupsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
