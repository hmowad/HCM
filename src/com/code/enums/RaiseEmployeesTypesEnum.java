package com.code.enums;

public enum RaiseEmployeesTypesEnum {
    DESERVED_EMPLOYEES(1),
    ALL_EXCLUDED_EMPLOYEES(2),
    EXCLUDED_EMPLOYEES_FOR_END_OF_LADDER(3),
    EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON(4),
    NOT_DESERVED_EMPLOYEES(5);

    private int code;

    private RaiseEmployeesTypesEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }

}
