package com.code.enums;

public enum FlagsEnum {
    
    ON(1),
    OFF(0),
    ALL(-1);

    private int code;

    private FlagsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
