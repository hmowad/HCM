package com.code.enums;

public enum SignaturesTypesEnum {

    SIGNATURE(1),
    STAMP(2);

    private int code;

    private SignaturesTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
