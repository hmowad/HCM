package com.code.integration.responses.payroll;

public class AdminDecisionEmployeeData {
    private Long empId;
    private String empName;
    private Long transactionId;
    private Long originalTransactionId;
    private String gregStartDateString;
    private String gregEndDateString;
    private String decisionNumber;
    private String originalAdminDecisionNumber;

    public AdminDecisionEmployeeData(Long empId, String empName, Long transactionId, Long originalTransactionId, String gregStartDateString, String gregEndDateString, String decisionNumber, String originalAdminDecisionNumber) {
	this.empId = empId;
	this.empName = empName;
	this.transactionId = transactionId;
	this.originalTransactionId = originalTransactionId;
	this.gregStartDateString = gregStartDateString;
	this.gregEndDateString = gregEndDateString;
	this.decisionNumber = decisionNumber;
	this.originalAdminDecisionNumber = originalAdminDecisionNumber;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public Long getTransactionId() {
	return transactionId;
    }

    public void setTransactionId(Long transactionId) {
	this.transactionId = transactionId;
    }

    public Long getOriginalTransactionId() {
	return originalTransactionId;
    }

    public void setOriginalTransactionId(Long originalTransactionId) {
	this.originalTransactionId = originalTransactionId;
    }

    public String getGregStartDateString() {
	return gregStartDateString;
    }

    public void setGregStartDateString(String gregStartDateString) {
	this.gregStartDateString = gregStartDateString;
    }

    public String getGregEndDateString() {
	return gregEndDateString;
    }

    public void setGregEndDateString(String gregEndDateString) {
	this.gregEndDateString = gregEndDateString;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public String getOriginalAdminDecisionNumber() {
	return originalAdminDecisionNumber;
    }

    public void setOriginalAdminDecisionNumber(String originalAdminDecisionNumber) {
	this.originalAdminDecisionNumber = originalAdminDecisionNumber;
    }

}
