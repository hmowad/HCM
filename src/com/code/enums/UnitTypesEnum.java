package com.code.enums;

public enum UnitTypesEnum {
    PRESIDENCY(100),
    ASSISTANT(80),
    REGION_COMMANDER(75),
    GOVERNER(70),
    SECTOR_COMMANDER(65),
    GENERAL_DEPARTMENT(60),
    INSTITUTE(50),
    DEPARTMENT(40),
    TRAINING_CENTER(38),
    TRAINING_CENTER_MANAGEMENT(36),
    NAVY_UNITS(45),
    CAMPS_FORCE(30),
    FORCE(27),
    NAVY_UNIT(25),
    BATTALION(24),
    CAMP(23),
    PATROLS(22),
    BOATS_GROUPS(20),
    COMPANY_PATROLS(17),
    SUB_DEPARTMENT(15),
    SECTION(13),
    OFFICE(11);

    private int code;

    private UnitTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
