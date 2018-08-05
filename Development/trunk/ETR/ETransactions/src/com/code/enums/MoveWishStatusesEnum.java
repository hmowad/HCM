package com.code.enums;

public enum MoveWishStatusesEnum {
    NEW(1),
    MODIFIED(2),
    CANCELED(4);

    private int code;

    private MoveWishStatusesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
