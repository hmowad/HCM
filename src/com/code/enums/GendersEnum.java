package com.code.enums;

public enum GendersEnum {

    MALE("M"),
    FEMALE("F");

    private String code;

    private GendersEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
