package com.code.integration.responses.workflow.hcm;

import javax.xml.bind.annotation.XmlElement;

public class WSWFMissionTaskInfo {
    private Long taskId;
    private Long processId;
    private String processName;
    private String requesterName;
    private String delegatingName;
    private Integer locationFlag;
    private String location;
    private String destination;
    private String startDateString;
    private Integer period;

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

    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
    }

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public String getDestination() {
	return destination;
    }

    public void setDestination(String destination) {
	this.destination = destination;
    }

    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
    }

    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

}
