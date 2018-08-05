package com.code.enums;

public enum MartyrdomTypesEnum {

    MARTYR(1),
    DEAD(2),
    MISSING(3),
    INJURED(4);

    private long code;

    private MartyrdomTypesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
