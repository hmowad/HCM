package com.code.integration.responses.payroll;

public class AdminDecisionVariable {

    private Integer variableId;
    private String variableName;
    private String variableMapping;
    private Integer isLov;

    public Integer getVariableId() {
	return variableId;
    }

    public void setVariableId(Integer variableId) {
	this.variableId = variableId;
    }

    public String getVariableName() {
	return variableName;
    }

    public void setVariableName(String variableName) {
	this.variableName = variableName;
    }

    public String getVariableMapping() {
	return variableMapping;
    }

    public void setVariableMapping(String variableMapping) {
	this.variableMapping = variableMapping;
    }

    public Integer getIsLov() {
	return isLov;
    }

    public void setIsLov(Integer isLov) {
	this.isLov = isLov;
    }

}
