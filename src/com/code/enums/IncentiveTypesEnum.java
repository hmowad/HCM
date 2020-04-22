package com.code.enums;

public enum IncentiveTypesEnum {

    FINANCIAL_LOSS_COMPENSATION(1),
    ADVISORY_COUNCILS(2),
    GOVERNMENTAL_COMMITTEES(3),
    DRUGS_DESTRUCTION(4),
    TEST_COMMITTEES(5),
    HAJJ(6),
    COMPUTER(7),
    MILITARY_VACATIONS(8),
    MISSIONS_DIFFERENCES(9);

    private long code;

    private IncentiveTypesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
