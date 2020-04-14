package com.code.integration.responses.payroll;

public class AdminDecisionVariable {

    private Integer variableId;
    private String variableName;
    private String variableMapping;
    private Integer isLOV;

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

    public Integer getIsLOV() {
	return isLOV;
    }

    public void setIsLOV(Integer isLOV) {
	this.isLOV = isLOV;
    }

}
