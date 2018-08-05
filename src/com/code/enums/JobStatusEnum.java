package com.code.enums;

public enum JobStatusEnum {

    VACANT(1),
    OCCUPIED(2),
    FROZEN(3),
    CANCELED(4);

    private int code;

    private JobStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
