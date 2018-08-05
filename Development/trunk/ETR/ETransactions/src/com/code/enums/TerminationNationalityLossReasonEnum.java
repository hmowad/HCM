package com.code.enums;

public enum TerminationNationalityLossReasonEnum {
    NOT_OFFICIALLY_ISSUED(1),
    NATIONALITY_LOSS(2);

    private int code;

    private TerminationNationalityLossReasonEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
