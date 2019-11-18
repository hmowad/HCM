package com.code.enums.eservices;

public enum ServiceURLEnum {
    NEXT_SEQUENCE_VALUE("/common/getNextSequenceValue"),
    ;
    
    private String code;

    ServiceURLEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
