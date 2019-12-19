package com.code.integration.parameters.eservices.workflow;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class WFProcessCycle implements Serializable {

    private Long id;
    private Long processId;
    private String processArabicName;
    private String processCode;
    private Long regionId;
    private String regionName;
    private Long jobId;
    private String jobDescription;
    private String rankId;
    private String rankDescription;
    private Long stoppingPoint;
    private Boolean enabled;
    private Date createDate;
    private Date editDate;
    private List<Long> selectedRanksIds;
    private List<String> selectedRanksDescriptions;
    private boolean newRow;

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

    public String getProcessArabicName() {
	return processArabicName;
    }

    public void setProcessArabicName(String processArabicName) {
	this.processArabicName = processArabicName;
    }

    public String getProcessCode() {
	return processCode;
    }

    public void setProcessCode(String processCode) {
	this.processCode = processCode;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public String getRegionName() {
	return regionName;
    }

    public void setRegionName(String regionName) {
	this.regionName = regionName;
    }

    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    public String getJobDescription() {
	return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
	this.jobDescription = jobDescription;
    }

    public String getRankId() {
	return rankId;
    }

    public void setRankId(String rankId) {
	this.rankId = rankId;
    }

    public String getRankDescription() {
	return rankDescription;
    }

    public void setRankDescription(String rankDescription) {
	this.rankDescription = rankDescription;
    }

    public Long getStoppingPoint() {
	return stoppingPoint;
    }

    public void setStoppingPoint(Long stoppingPoint) {
	this.stoppingPoint = stoppingPoint;
    }

    public Boolean getEnabled() {
	return enabled;
    }

    public void setEnabled(Boolean enabled) {
	this.enabled = enabled;
    }

    public Date getCreateDate() {
	return createDate;
    }

    public void setCreateDate(Date createDate) {
	this.createDate = createDate;
    }

    public Date getEditDate() {
	return editDate;
    }

    public void setEditDate(Date editDate) {
	this.editDate = editDate;
    }

    public List<Long> getSelectedRanksIds() {
	return selectedRanksIds;
    }

    public void setSelectedRanksIds(List<Long> selectedRanksIds) {
	this.selectedRanksIds = selectedRanksIds;
    }

    public List<String> getSelectedRanksDescriptions() {
	return selectedRanksDescriptions;
    }

    public void setSelectedRanksDescriptions(List<String> selectedRanksDescriptions) {
	this.selectedRanksDescriptions = selectedRanksDescriptions;
    }

    public boolean isNewRow() {
        return newRow;
    }

    public void setNewRow(boolean newRow) {
        this.newRow = newRow;
    }

    
}
