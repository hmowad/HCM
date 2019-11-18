package com.code.enums.eservices;

public enum ExtensionPeriodMonthsEnum {

    ONE_MONTH(1, "\u0634\u0647\u0631"),
    TWO_MONTHS(2, "\u0634\u0647\u0631\u064A\u0646");

    private String code;

    ExtensionPeriodMonthsEnum(int number, String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }

}
