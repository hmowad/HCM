package com.code.enums;

public enum GraduationPlacesTypesEnum {
    UNIVERSITIES(1),
    INSTITUTES(2),
    SCHOOLS(3);

    private int code;

    private GraduationPlacesTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
