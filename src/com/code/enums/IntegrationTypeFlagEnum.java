package com.code.enums;

public enum IntegrationTypeFlagEnum {
    
    NO_INTEGRATON(0),
    INTEGRATE_ALLOWANCES(1),
    INTEGRATE_PAYROLL(2);

    private int code;

    private IntegrationTypeFlagEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
