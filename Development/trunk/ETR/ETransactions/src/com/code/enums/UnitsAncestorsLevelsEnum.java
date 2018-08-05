package com.code.enums;

public enum UnitsAncestorsLevelsEnum {
    LEVEL_ONE(1),
    LEVEL_TWO(2),
    LEVEL_THREE(3),
    LEVEL_FOUR(4);

    private int code;

    private UnitsAncestorsLevelsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
