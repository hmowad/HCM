package com.code.enums;

public enum TerminationDueDateYearsEnum {

    MAJOR_GENERAL(58), // lwa2
    BRIGADIER(56), // 3amed
    COLONEL(54), // 3aqeed
    LIEUTENANT_COLONEL(52), // Moqadem
    MAJOR(50), // Raed
    CAPTAIN(48), // Naqeeb
    FIRST_LIEUTENANT(44), // Molazem awal
    LIEUTENANT(44), // Molazem

    /* Soldiers ranks */
    PRIME_SERGEANTS(52), // Raaes roabaa
    STAFF_SERGEANT(50), // Raqeeb awal
    SERGEANT(50), // Raqeeb
    UNDER_SERGEANT(48), // Wakeel raqeeb
    CORPORAL(46), // 3areef
    FIRST_SOLDIER(44), // Gondy awal
    SOLDIER(44), // Gondy

    PERSONS(60);

    private int code;

    private TerminationDueDateYearsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
