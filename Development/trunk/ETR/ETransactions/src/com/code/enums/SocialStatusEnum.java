package com.code.enums;

public enum SocialStatusEnum {
    SINGLE(1),
    MARRIED(2),
    DIVORCEE(3),
    WIDOWED(3);

    private int code;

    private SocialStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
