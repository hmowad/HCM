package com.code.enums;

public enum WSResponseStatusEnum {

    SUCCESS(1),
    INVALID_SESSION(0),
    FAILED(-1);

    private int code;

    private WSResponseStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
