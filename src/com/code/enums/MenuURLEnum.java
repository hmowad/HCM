package com.code.enums;

public enum MenuURLEnum {
    EMPLOYEES_INQUIRY("/Employees/EmployeesInquiry.jsf");

    private String code;

    private MenuURLEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
