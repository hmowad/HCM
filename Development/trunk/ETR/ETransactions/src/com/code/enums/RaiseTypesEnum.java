package com.code.enums;

public enum RaiseTypesEnum {
    ANNUAL(1),
    ADDITIONAL(2);

    private int code;

    private RaiseTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
