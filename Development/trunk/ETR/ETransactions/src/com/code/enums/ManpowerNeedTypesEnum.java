package com.code.enums;

public enum ManpowerNeedTypesEnum {

    REQUEST(1),
    STUDY(2),
    MAIN_STUDY(3);

    private int code;

    private ManpowerNeedTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
