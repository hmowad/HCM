package com.code.enums;

public enum EmployeesDeductionsEnum {

    IN(0),
    OUT(1);

    private int code;

    private EmployeesDeductionsEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
