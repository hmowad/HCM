package com.code.enums;

public enum SubVacationTypesEnum {

    SUB_TYPE_ONE(1), // Sick section A or Exceptional For Circumstances.
    SUB_TYPE_TWO(2); // Sick section B or Exceptional For Accompany.

    private int code;

    private SubVacationTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
