package com.code.enums.eservices;

public enum EmployeeRegionEnum {

    ALL(-1, "\u0627\u0644\u0643\u0644"),
    DIRECTORATE_EMPLOYEE(1, "\u0645\u0646\u0633\u0648\u0628\u064A \u0627\u0644\u0645\u062F\u064A\u0631\u064A\u0629"),
    REGION_EMPLOYEE(2, "\u0645\u0646\u0633\u0648\u0628\u064A \u0627\u0644\u0645\u0646\u0627\u0637\u0642");

    private int code;
    private String codeDescription;

    EmployeeRegionEnum(int code, String codeDescription) {
	this.code = code;
	this.codeDescription = codeDescription;
    }

    public int getCode() {
	return code;
    }

    public String getCodeDescription() {
	return codeDescription;
    }

    public static String valueOfNumber(Long number) {
	for (EmployeeRegionEnum e : values()) {
	    if (e.code == number) {
		return e.codeDescription;
	    }
	}
	return null;
    }

}

