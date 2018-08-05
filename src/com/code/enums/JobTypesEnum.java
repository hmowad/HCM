package com.code.enums;

public enum JobTypesEnum {

    NORMAL(1),
    TECHNICAL(2),
    FIELD(3);

    private int code;

    private JobTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
