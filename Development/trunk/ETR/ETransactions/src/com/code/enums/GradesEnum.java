package com.code.enums;

public enum GradesEnum {
    EXCELLENT(1),
    VERY_GOOD(2),
    GOOD(3),
    ACCEPTED(4),
    FAILED(5),
    NOT_AVAILABLE(6);

    private int code;

    private GradesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
