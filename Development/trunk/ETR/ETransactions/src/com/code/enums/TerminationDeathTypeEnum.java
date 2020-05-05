package com.code.enums;

public enum TerminationDeathTypeEnum {
    IN_SERVICE(1),
    OUT_SERVICE(2),
    MARTYRDOM(3),
    IN_SERVICE_SAVING_SECURITY_OPERATIONS(4),
    IN_SERVICE_SAVING_SECURITY_OPERATIONS_SOUTH_BORDER_AS_MARTYR(5),
    IN_SERVICE_SAVING_SECURITY_TERRORISM_OPERATIONS_AS_MARTYR(6);

    private int code;

    private TerminationDeathTypeEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
