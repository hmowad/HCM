package com.code.integration.parameters.eservices.workflow;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WFProcessStepData extends WFBaseParam implements Serializable{
    private Long id;
    private String participants;
    private String stepRole;
    private Integer stepOrder;
    private Long processId;
    private String participantsNames;
    private Boolean approve;
    private Boolean reject;
    private Boolean redirect;

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

    public String getParticipants() {
	return participants;
    }

    public void setParticipants(String participants) {
	this.participants = participants;
    }

    public String getStepRole() {
	return stepRole;
    }

    public void setStepRole(String stepRole) {
	this.stepRole = stepRole;
    }

    public Integer getStepOrder() {
	return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
	this.stepOrder = stepOrder;
    }

    public String getParticipantsNames() {
	return participantsNames;
    }

    public void setParticipantsNames(String participantsNames) {
	this.participantsNames = participantsNames;
    }

    public Boolean getApprove() {
	return approve;
    }

    public void setApprove(Boolean approve) {
	this.approve = approve;
    }

    public Boolean getReject() {
	return reject;
    }

    public void setReject(Boolean reject) {
	this.reject = reject;
    }

    public Boolean getRedirect() {
	return redirect;
    }

    public void setRedirect(Boolean redirect) {
	this.redirect = redirect;
    }

}
