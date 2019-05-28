package com.code.enums;

public enum RaiseStatusEnum {
    INITIAL(1),
    APPROVED(2),
    CONFIRMED(3);

    private int code;

    private RaiseStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
