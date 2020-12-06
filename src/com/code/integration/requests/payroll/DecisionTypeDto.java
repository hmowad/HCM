package com.code.integration.requests.payroll;

import java.util.List;

public class DecisionTypeDto {

    private String decisionTypeId;
    private String name;
    private String departmentId;
    private String decisionDate;
    private String decisionStartDate;
    private String decisionEndDate;
    private String categoryId;
    private String adminDecisionId;
    private String adminDecisionNumber;
    private String originalAdminDecisionNumber;
    private List<VariableDto> variablesList;
    private List<EmployeeDto> employeesList;
    private List<AttachmentDto> AttachmentList;

    public String getDecisionTypeId() {
	return decisionTypeId;
    }

    public void setDecisionTypeId(String decisionTypeId) {
	this.decisionTypeId = decisionTypeId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDepartmentId() {
	return departmentId;
    }

    public void setDepartmentId(String departmentId) {
	this.departmentId = departmentId;
    }

    public String getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(String decisionDate) {
	this.decisionDate = decisionDate;
    }

    public String getDecisionStartDate() {
	return decisionStartDate;
    }

    public void setDecisionStartDate(String decisionStartDate) {
	this.decisionStartDate = decisionStartDate;
    }

    public String getDecisionEndDate() {
	return decisionEndDate;
    }

    public void setDecisionEndDate(String decisionEndDate) {
	this.decisionEndDate = decisionEndDate;
    }

    public String getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(String categoryId) {
	this.categoryId = categoryId;
    }

    public String getAdminDecisionId() {
	return adminDecisionId;
    }

    public void setAdminDecisionId(String adminDecisionId) {
	this.adminDecisionId = adminDecisionId;
    }

    public String getAdminDecisionNumber() {
	return adminDecisionNumber;
    }

    public void setAdminDecisionNumber(String adminDecisionNumber) {
	this.adminDecisionNumber = adminDecisionNumber;
    }

    public String getOriginalAdminDecisionNumber() {
	return originalAdminDecisionNumber;
    }

    public void setOriginalAdminDecisionNumber(String originalAdminDecisionNumber) {
	this.originalAdminDecisionNumber = originalAdminDecisionNumber;
    }

    public List<VariableDto> getVariablesList() {
	return variablesList;
    }

    public void setVariablesList(List<VariableDto> variablesList) {
	this.variablesList = variablesList;
    }

    public List<EmployeeDto> getEmployeesList() {
	return employeesList;
    }

    public void setEmployeesList(List<EmployeeDto> employeesList) {
	this.employeesList = employeesList;
    }

    public List<AttachmentDto> getAttachmentList() {
	return AttachmentList;
    }

    public void setAttachmentList(List<AttachmentDto> attachmentList) {
	AttachmentList = attachmentList;
    }

}
