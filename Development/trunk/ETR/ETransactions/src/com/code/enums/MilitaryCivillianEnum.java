package com.code.enums;

public enum MilitaryCivillianEnum {

    Civillian(1),
    Military(2);

    private int code;

    private MilitaryCivillianEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
