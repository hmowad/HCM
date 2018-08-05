package com.code.integration.responses.workflow.hcm;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class WSWFVacationTaskInfo implements Serializable {

    private Long taskId;
    private Long processId;
    private String processName;
    private String requesterName;
    private String delegatingName;
    private String beneficiaryName;
    private Integer period;
    private String startDateString;
    private String location;

    public Long getTaskId() {
	return taskId;
    }

    public void setTaskId(Long taskId) {
	this.taskId = taskId;
    }

    public Long getProcessId() {
	return processId;
    }

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    public String getProcessName() {
	return processName;
    }

    public void setProcessName(String processName) {
	this.processName = processName;
    }

    public String getRequesterName() {
	return requesterName;
    }

    public void setRequesterName(String requesterName) {
	this.requesterName = requesterName;
    }

    @XmlElement(nillable = true)
    public String getDelegatingName() {
	return delegatingName;
    }

    public void setDelegatingName(String delegatingName) {
	this.delegatingName = delegatingName;
    }

    public String getBeneficiaryName() {
	return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
	this.beneficiaryName = beneficiaryName;
    }

    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
    }

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

}