package com.code.integration.responses.payroll;

public class AdminDecisionResponse {

    private Integer id;
    private String name;
    private AdminDecisionVariable[] variableArray;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public AdminDecisionVariable[] getVariableArray() {
	return variableArray;
    }

    public void setVariableArray(AdminDecisionVariable[] variableArray) {
	this.variableArray = variableArray;
    }

}
