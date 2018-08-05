package com.code.enums;

public enum MartyrsHonorsTypesEnum {

    FINANCIAL(1),
    MORAL(2);

    private long code;

    private MartyrsHonorsTypesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
