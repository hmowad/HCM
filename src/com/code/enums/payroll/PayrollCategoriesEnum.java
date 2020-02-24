package com.code.enums.payroll;

public enum PayrollCategoriesEnum {

    MILITARY_MEDICAL_STAFF(20);

    private long code;

    private PayrollCategoriesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }

}
