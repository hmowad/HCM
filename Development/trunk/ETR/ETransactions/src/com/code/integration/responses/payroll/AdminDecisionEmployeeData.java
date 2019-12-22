package com.code.integration.responses.payroll;

public class AdminDecisionEmployeeData {
    private Long empId;
    private String empName;
    private Long transactionId;
    private String gregStartDateString;
    private String gregEndDateString;
    private String decisionNumber;

    public AdminDecisionEmployeeData(Long empId, String empName, Long transactionId, String gregStartDateString, String gregEndDateString, String decisionNumber) {
	this.empId = empId;
	this.empName = empName;
	this.transactionId = transactionId;
	this.gregStartDateString = gregStartDateString;
	this.gregEndDateString = gregEndDateString;
	this.decisionNumber = decisionNumber;
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

}
