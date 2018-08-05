package com.code.integration.responses.workflow;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class WSWFInstanceTaskInfo implements Serializable {
    private String employeeNameAndRank;
    private String employeeJobDescription;
    private String employeeStatus;

    private String requestHijriDateString;

    private String taskAction;
    private String taskHijriAssignDateString;
    private String taskHijriActionDateString;
    private String taskLevel;
    private boolean isDone;
    private boolean isEnd;

    public String getEmployeeNameAndRank() {
	return employeeNameAndRank;
    }

    public void setEmployeeNameAndRank(String employeeNameAndRank) {
	this.employeeNameAndRank = employeeNameAndRank;
    }

    @XmlElement(nillable = true)
    public String getEmployeeJobDescription() {
	return employeeJobDescription;
    }

    public void setEmployeeJobDescription(String employeeJobDescription) {
	this.employeeJobDescription = employeeJobDescription;
    }

    @XmlElement(nillable = true)
    public String getEmployeeStatus() {
	return employeeStatus;
    }

    public void setEmployeeStatus(String employeeStatus) {
	this.employeeStatus = employeeStatus;
    }

    @XmlElement(nillable = true)
    public String getRequestHijriDateString() {
	return requestHijriDateString;
    }

    public void setRequestHijriDateString(String requestHijriDateString) {
	this.requestHijriDateString = requestHijriDateString;
    }

    @XmlElement(nillable = true)
    public String getTaskAction() {
	return taskAction;
    }

    public void setTaskAction(String taskAction) {
	this.taskAction = taskAction;
    }

    @XmlElement(nillable = true)
    public String getTaskHijriAssignDateString() {
	return taskHijriAssignDateString;
    }

    public void setTaskHijriAssignDateString(String taskHijriAssignDateString) {
	this.taskHijriAssignDateString = taskHijriAssignDateString;
    }

    @XmlElement(nillable = true)
    public String getTaskHijriActionDateString() {
	return taskHijriActionDateString;
    }

    public void setTaskHijriActionDateString(String taskHijriActionDateString) {
	this.taskHijriActionDateString = taskHijriActionDateString;
    }

    public String getTaskLevel() {
	return taskLevel;
    }

    public void setTaskLevel(String taskLevel) {
	this.taskLevel = taskLevel;
    }

    public boolean getIsDone() {
	return isDone;
    }

    public void setIsDone(boolean isDone) {
	this.isDone = isDone;
    }

    public boolean getIsEnd() {
	return isEnd;
    }

    public void setIsEnd(boolean isEnd) {
	this.isEnd = isEnd;
    }

}