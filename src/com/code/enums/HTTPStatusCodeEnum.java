package com.code.enums;

public enum HTTPStatusCodeEnum {

    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    CONFLICT(409);

    private int code;

    HTTPStatusCodeEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
