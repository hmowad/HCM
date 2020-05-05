package com.code.enums;

public enum TerminationInjuryTypeEnum {
    AS_MARTYR(1),
    IN_SERVICE_DUE_TO_SERVICE(2),
    IN_SERVICE_NOT_DUE_TO_SERVICE(3),
    IN_SERVICE_SAVING_SECURITY_OPERATIONS(4),
    IN_SERVICE_SAVING_SECURITY_OPERATIONS_SOUTH_BORDER(5),
    IN_SERVICE_SAVING_SECURITY_TERRORISM_OPERATIONS(6),
    IN_SERVICE_SAVING_SECURITY_OPERATIONS_SOUTH_BORDER_AS_MARTYR(7),
    IN_SERVICE_SAVING_SECURITY_TERRORISM_OPERATIONS_AS_MARTYR(8);

    private int code;

    private TerminationInjuryTypeEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
