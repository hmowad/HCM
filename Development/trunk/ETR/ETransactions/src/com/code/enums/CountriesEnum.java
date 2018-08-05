package com.code.enums;

public enum CountriesEnum {

    SAUDI_ARABIA("SA");
    
    private String code;
    
    private CountriesEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
