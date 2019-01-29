package com.code.enums;

public enum RaiseTransactionTypesEnum {
    VALID(1),
    INVALID(2);

    private int code;

    private RaiseTransactionTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
