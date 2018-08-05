package com.code.enums;

public enum GeneralSpecializationsEnum {

    OVERLAND(1),
    NAVY(2),
    AERIAL(3);

    private int code;

    private GeneralSpecializationsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
