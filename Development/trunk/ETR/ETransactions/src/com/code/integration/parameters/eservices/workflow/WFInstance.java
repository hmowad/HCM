package com.code.integration.parameters.eservices.workflow;

import java.io.Serializable;
import java.util.Date;
import com.code.services.util.HijriDateService;

@SuppressWarnings("serial")
public class WFInstance implements Serializable{

    private Long id;
    private Long requesterId;
    private Date requestDate;
    private String requestDateString;
    private Long creatorId;
    private String summary;
    private Integer status;
    private Long parentInstanceId;
    private Long processId;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Long getProcessId() {
	return processId;
    }

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    public Long getRequesterId() {
	return requesterId;
    }

    public void setRequesterId(Long requesterId) {
	this.requesterId = requesterId;
    }

    public Date getRequestDate() {
	return requestDate;
    }

    public void setRequestDate(Date requestDate) {
	this.requestDate = requestDate;
	this.requestDateString = HijriDateService.getHijriDateString(requestDate);
    }

    public String getRequestDateString() {
	return requestDateString;
    }

    public void setRequestDateString(String requestDateString) {
	this.requestDateString = requestDateString;
	this.requestDate = HijriDateService.getHijriDate(requestDateString);
    }

    public Long getCreatorId() {
	return creatorId;
    }

    public void setCreatorId(Long creatorId) {
	this.creatorId = creatorId;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    public Long getParentInstanceId() {
	return parentInstanceId;
    }

    public void setParentInstanceId(Long parentInstanceId) {
	this.parentInstanceId = parentInstanceId;
    }
}