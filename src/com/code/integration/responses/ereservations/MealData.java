package com.code.integration.responses.ereservations;

import java.io.Serializable;

public class MealData implements Serializable {

    private String code;
    private String value;

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

}
