package com.code.enums;

public enum ManpowerNeedStatusEnum {

    NOT_SUBMITTED(1),
    UNDER_PROCESSING(2),
    SUBMITTED_TO_STUDY(3),
    APPROVED(4),
    REJECTED(5);

    private int code;

    private ManpowerNeedStatusEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
