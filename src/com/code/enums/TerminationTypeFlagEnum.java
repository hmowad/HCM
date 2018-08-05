package com.code.enums;

public enum TerminationTypeFlagEnum {
    REQUEST(1),
    RECORD(0);

    private int code;

    private TerminationTypeFlagEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
