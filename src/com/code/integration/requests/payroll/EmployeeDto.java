package com.code.integration.requests.payroll;

import java.util.List;

public class EmployeeDto {

    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private List<VariableDto> variablesList;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getStartDate() {
	return startDate;
    }

    public void setStartDate(String startDate) {
	this.startDate = startDate;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String endDate) {
	this.endDate = endDate;
    }

    public List<VariableDto> getVariablesList() {
	return variablesList;
    }

    public void setVariablesList(List<VariableDto> variablesList) {
	this.variablesList = variablesList;
    }

}
