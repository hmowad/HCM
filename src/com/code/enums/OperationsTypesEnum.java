package com.code.enums;

public enum OperationsTypesEnum {

    INSERT(1),
    UPDATE(2),
    DELETE(3);

    private int code;

    private OperationsTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
