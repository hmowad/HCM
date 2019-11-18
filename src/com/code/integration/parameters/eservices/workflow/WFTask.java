package com.code.integration.parameters.eservices.workflow;

import java.io.Serializable;
import java.util.Date;

import com.code.services.util.HijriDateService;

@SuppressWarnings("serial")
public class WFTask implements Serializable{

    private Long id;
    private Long instanceId;
    private Long assigneeId;
    private Long originalId;
    private Date assignDate;
    private Date assignGregDate;
    private String assignDateString;
    private String taskURL;
    private String assigneeWfRole;
    private String action;
    private Date actionDate;
    private Date actionGregDate;
    private String actionDateString;
    private String notes;
    private String refuseReasons;
    private Long externalLocationId;
    private Boolean emailNotified;
    private String flexField1;
    private String flexField2;
    private String flexField3;
    private Integer priority;
    private Long processGroupId;
    private Integer stepOrder;
    private String arabicDetailsSummary;
    private String englishDetailsSummary;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    public Long getAssigneeId() {
	return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
	this.assigneeId = assigneeId;
    }

    public Long getOriginalId() {
	return originalId;
    }

    public void setOriginalId(Long originalId) {
	this.originalId = originalId;
    }

    public Date getAssignDate() {
	return assignDate;
    }

    public void setAssignDate(Date assignDate) {
	this.assignDate = assignDate;
	this.assignDateString = HijriDateService.getHijriDateString(assignDate);
    }

    public String getAssignDateString() {
	return assignDateString;
    }

    public void setAssignDateString(String assignDateString) {
	this.assignDateString = assignDateString;
	this.assignDate = HijriDateService.getHijriDate(assignDateString);
    }

    public Date getAssignGregDate() {
	return assignGregDate;
    }

    public void setAssignGregDate(Date assignGregDate) {
	this.assignGregDate = assignGregDate;
    }

    public String getTaskURL() {
	return taskURL;
    }

    public void setTaskURL(String taskURL) {
	this.taskURL = taskURL;
    }

    public String getAssigneeWfRole() {
	return assigneeWfRole;
    }

    public void setAssigneeWfRole(String assigneeWfRole) {
	this.assigneeWfRole = assigneeWfRole;
    }

    public String getAction() {
	return action;
    }

    public void setAction(String action) {
	this.action = action;
    }

    public Date getActionDate() {
	return actionDate;
    }

    public void setActionDate(Date actionDate) {
	this.actionDate = actionDate;
	this.actionDateString = HijriDateService.getHijriDateString(actionDate);
    }

    public String getActionDateString() {
	return actionDateString;
    }

    public void setActionDateString(String actionDateString) {
	this.actionDateString = actionDateString;
	this.actionDate = HijriDateService.getHijriDate(actionDateString);
    }

    public Date getActionGregDate() {
	return actionGregDate;
    }

    public void setActionGregDate(Date actionGregDate) {
	this.actionGregDate = actionGregDate;
    }

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    public String getRefuseReasons() {
	return refuseReasons;
    }

    public void setRefuseReasons(String refuseReasons) {
	this.refuseReasons = refuseReasons;
    }

    public Long getExternalLocationId() {
	return externalLocationId;
    }

    public void setExternalLocationId(Long externalLocationId) {
	this.externalLocationId = externalLocationId;
    }

    public Boolean getEmailNotified() {
	return emailNotified;
    }

    public void setEmailNotified(Boolean emailNotified) {
	this.emailNotified = emailNotified;
    }

    public String getFlexField1() {
	return flexField1;
    }

    public void setFlexField1(String flexField1) {
	this.flexField1 = flexField1;
    }

    public String getFlexField2() {
	return flexField2;
    }

    public void setFlexField2(String flexField2) {
	this.flexField2 = flexField2;
    }

    public String getFlexField3() {
	return flexField3;
    }

    public void setFlexField3(String flexField3) {
	this.flexField3 = flexField3;
    }

    public Integer getPriority() {
	return priority;
    }

    public void setPriority(Integer priority) {
	this.priority = priority;
    }

    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    public Integer getStepOrder() {
	return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
	this.stepOrder = stepOrder;
    }

    public String getArabicDetailsSummary() {
	return arabicDetailsSummary;
    }

    public void setArabicDetailsSummary(String arabicDetailsSummary) {
	this.arabicDetailsSummary = arabicDetailsSummary;
    }

    public String getEnglishDetailsSummary() {
	return englishDetailsSummary;
    }

    public void setEnglishDetailsSummary(String englishDetailsSummary) {
	this.englishDetailsSummary = englishDetailsSummary;
    }

}