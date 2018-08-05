package com.code.enums;

public enum MovementTypesEnum {

    MOVE(1), // Na2l
    SUBJOIN(2), // 2el7ak
    ASSIGNMENT(3), // 2amr Takleef
    MANDATE(4), // Nadb
    SECONDMENT(5), // 2e3arah
    INTERNAL_ASSIGNMENT(6); // Takleef Da5ly

    private long code;

    private MovementTypesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}