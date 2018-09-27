package com.code.enums;

public enum RaiseEmployeesTypesEnum {
    DESERVED_EMPLOYEES(1),
    EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER(2),
    EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON(3),
    NOT_DESERVED_EMPLOYEES(4),
    ALL_EXCLUDED_EMPLOYEES(5);

    private int code;

    private RaiseEmployeesTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }

}
