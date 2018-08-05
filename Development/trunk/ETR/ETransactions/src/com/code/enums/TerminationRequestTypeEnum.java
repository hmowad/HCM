package com.code.enums;

public enum TerminationRequestTypeEnum {
    RETIREMENT(1),
    RESIGNATION(2);

    private long code;

    private TerminationRequestTypeEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
