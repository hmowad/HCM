package com.code.enums;

public enum FundSourceEnum {

    COST_ON_EMPLOYEE(1),
    COST_ON_BORDER_GUARD(2);

    private Integer code;

    private FundSourceEnum(Integer code) {
	this.code = code;
    }

    public Integer getCode() {
	return code;
    }
}
