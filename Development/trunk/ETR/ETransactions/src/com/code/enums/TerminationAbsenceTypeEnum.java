package com.code.enums;

public enum TerminationAbsenceTypeEnum {
    CONTINUOUS_DAYS(1),
    DISCONTINUOUS_DAYS(2),
    CERTAIN_DATE(3),
    WITHOUT_ATTEND_WITHOUT_EXCUSE(4),
    ATTEND_WITHOUT_EXCUSE(5);

    private int code;

    private TerminationAbsenceTypeEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
