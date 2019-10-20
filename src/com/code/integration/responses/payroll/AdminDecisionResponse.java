package com.code.integration.responses.payroll;

import java.util.List;

public class AdminDecisionResponse {

    private Integer id;
    private String name;
    private List<AdminDecisionVariable> variableArray;

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

    public List<AdminDecisionVariable> getVariableArray() {
	return variableArray;
    }

    public void setVariableArray(List<AdminDecisionVariable> variableArray) {
	this.variableArray = variableArray;
    }

}
