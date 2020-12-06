package com.code.integration.requests.payroll;

public class VariableDto {

    private String id;
    private String value;
    private String variableMapping;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public String getVariableMapping() {
	return variableMapping;
    }

    public void setVariableMapping(String variableMapping) {
	this.variableMapping = variableMapping;
    }

}
