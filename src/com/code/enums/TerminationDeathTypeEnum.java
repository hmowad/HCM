package com.code.enums;

public enum TerminationDeathTypeEnum {
    IN_SERVICE(1),
    OUT_SERVICE(2),
    MARTYRDOM(3),
    IN_SERVICE_SAVING_SECURITY_OPERATIONS(4);

    private int code;

    private TerminationDeathTypeEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
