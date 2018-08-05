package com.code.enums;

public enum TransactionClassesEnum {

    UNITS(10),
    JOBS(20),
    RECRUITMENT(30),
    MOVEMENTS(40),
    PROMOTIONS(50),
    VACATIONS(60),
    MISSIONS(70),
    TERMINATIONS(80),
    TRAININGS(90),
    RETIREMENTS(100);

    private int code;

    private TransactionClassesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}