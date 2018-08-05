package com.code.dal.orm.workflow.hcm.missions;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * Provides all information regarding mission workflow Object such as mission purpose, its location, start date, and end date, .. etc <br/>
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "WF_MISSIONS")
public class WFMission extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {

    private Long instanceId;
    private String referring;
    private Integer locationFlag;
    private String location;
    private String regionsCodes;
    private String destination;
    private String purpose;
    private Integer empExternalFlag;
    private Integer empExternalStatus;
    private Integer period;
    private Integer roadPeriod;
    private Date startDate;
    private Date endDate;
    private String ministeryApprovalNumber;
    private Date ministeryApprovalDate;
    private String roadLine;
    private Integer doubleBalanceFlag;
    private Integer hajjFlag;
    private String remarks;
    private String internalCopies;
    private String externalCopies;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Long decisionRegionId;
    private String directedToJobName;

    private Integer previousFlag;
    private String previousDescription;
    private Integer previousExecutedFlag;
    private Integer relatedFlag;
    private Integer notExecutedRelatedFlag;
    private String relatedDescription;
    private Integer basedOnFlag;
    private String basedOnNumber;
    private Date basedOnDate;
    private Integer resultReportFlag;
    private Integer resultLetterFlag;
    private Integer resultOtherFlag;
    private String resultLetterDescription;
    private String resultOtherDescription;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
    }

    @Basic
    @Column(name = "LOCATION")
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    @Basic
    @Column(name = "REGIONS_CODES")
    public String getRegionsCodes() {
	return regionsCodes;
    }

    public void setRegionsCodes(String regionsCodes) {
	this.regionsCodes = regionsCodes;
    }

    @Basic
    @Column(name = "DESTINATION")
    public String getDestination() {
	return destination;
    }

    public void setDestination(String destination) {
	this.destination = destination;
    }

    @Basic
    @Column(name = "PURPOSE")
    public String getPurpose() {
	return purpose;
    }

    public void setPurpose(String purpose) {
	this.purpose = purpose;
    }

    @Basic
    @Column(name = "EMP_EXTERNAL_FLAG")
    public Integer getEmpExternalFlag() {
	return empExternalFlag;
    }

    public void setEmpExternalFlag(Integer empExternalFlag) {
	this.empExternalFlag = empExternalFlag;
    }

    @Basic
    @Column(name = "EMP_EXTERNAL_STATUS")
    public Integer getEmpExternalStatus() {
	return empExternalStatus;
    }

    public void setEmpExternalStatus(Integer empExternalStatus) {
	this.empExternalStatus = empExternalStatus;
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    @Basic
    @Column(name = "ROAD_PERIOD")
    public Integer getRoadPeriod() {
	return roadPeriod;
    }

    public void setRoadPeriod(Integer roadPeriod) {
	this.roadPeriod = roadPeriod;
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    @Basic
    @Column(name = "MINISTERY_APPROVAL_NUMBER")
    public String getMinisteryApprovalNumber() {
	return ministeryApprovalNumber;
    }

    public void setMinisteryApprovalNumber(String ministeryApprovalNumber) {
	this.ministeryApprovalNumber = ministeryApprovalNumber;
    }

    @Basic
    @Column(name = "MINISTERY_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinisteryApprovalDate() {
	return ministeryApprovalDate;
    }

    public void setMinisteryApprovalDate(Date ministeryApprovalDate) {
	this.ministeryApprovalDate = ministeryApprovalDate;
    }

    @Basic
    @Column(name = "ROAD_LINE")
    public String getRoadLine() {
	return roadLine;
    }

    public void setRoadLine(String roadLine) {
	this.roadLine = roadLine;
    }

    @Basic
    @Column(name = "DOUBLE_BALANCE_FLAG")
    public Integer getDoubleBalanceFlag() {
	return doubleBalanceFlag;
    }

    public void setDoubleBalanceFlag(Integer doubleBalanceFlag) {
	this.doubleBalanceFlag = doubleBalanceFlag;
    }

    @Basic
    @Column(name = "HAJJ_FLAG")
    public Integer getHajjFlag() {
	return hajjFlag;
    }

    public void setHajjFlag(Integer hajjFlag) {
	this.hajjFlag = hajjFlag;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }

    @Basic
    @Column(name = "APPROVED_ID")
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
    }

    @Basic
    @Column(name = "PREVIOUS_FLAG")
    public Integer getPreviousFlag() {
	return previousFlag;
    }

    public void setPreviousFlag(Integer previousFlag) {
	this.previousFlag = previousFlag;
    }

    @Basic
    @Column(name = "PREVIOUS_DESC")
    public String getPreviousDescription() {
	return previousDescription;
    }

    public void setPreviousDescription(String previousDescription) {
	this.previousDescription = previousDescription;
    }

    @Basic
    @Column(name = "PREVIOUS_EXECUTED_FLAG")
    public Integer getPreviousExecutedFlag() {
	return previousExecutedFlag;
    }

    public void setPreviousExecutedFlag(Integer previousExecutedFlag) {
	this.previousExecutedFlag = previousExecutedFlag;
    }

    @Basic
    @Column(name = "RELATED_FLAG")
    public Integer getRelatedFlag() {
	return relatedFlag;
    }

    public void setRelatedFlag(Integer relatedFlag) {
	this.relatedFlag = relatedFlag;
    }

    @Basic
    @Column(name = "NOT_EXECUTED_RELATED_FLAG")
    public Integer getNotExecutedRelatedFlag() {
	return notExecutedRelatedFlag;
    }

    public void setNotExecutedRelatedFlag(Integer notExecutedRelatedFlag) {
	this.notExecutedRelatedFlag = notExecutedRelatedFlag;
    }

    @Basic
    @Column(name = "RELATED_DESC")
    public String getRelatedDescription() {
	return relatedDescription;
    }

    public void setRelatedDescription(String relatedDescription) {
	this.relatedDescription = relatedDescription;
    }

    @Basic
    @Column(name = "BASED_ON_FLAG")
    public Integer getBasedOnFlag() {
	return basedOnFlag;
    }

    public void setBasedOnFlag(Integer basedOnFlag) {
	this.basedOnFlag = basedOnFlag;
    }

    @Basic
    @Column(name = "BASED_ON_NUMBER")
    public String getBasedOnNumber() {
	return basedOnNumber;
    }

    public void setBasedOnNumber(String basedOnNumber) {
	this.basedOnNumber = basedOnNumber;
    }

    @Basic
    @Column(name = "BASED_ON_DATE")
    public Date getBasedOnDate() {
	return basedOnDate;
    }

    public void setBasedOnDate(Date basedOnDate) {
	this.basedOnDate = basedOnDate;
    }

    @Basic
    @Column(name = "RESULT_REPORT_FLAG")
    public Integer getResultReportFlag() {
	return resultReportFlag;
    }

    public void setResultReportFlag(Integer resultReportFlag) {
	this.resultReportFlag = resultReportFlag;
    }

    @Basic
    @Column(name = "RESULT_LETTER_FLAG")
    public Integer getResultLetterFlag() {
	return resultLetterFlag;
    }

    public void setResultLetterFlag(Integer resultLetterFlag) {
	this.resultLetterFlag = resultLetterFlag;
    }

    @Basic
    @Column(name = "RESULT_OTHER_FLAG")
    public Integer getResultOtherFlag() {
	return resultOtherFlag;
    }

    public void setResultOtherFlag(Integer resultOtherFlag) {
	this.resultOtherFlag = resultOtherFlag;
    }

    @Basic
    @Column(name = "RESULT_LETTER_DESC")
    public String getResultLetterDescription() {
	return resultLetterDescription;
    }

    public void setResultLetterDescription(String resultLetterDescription) {
	this.resultLetterDescription = resultLetterDescription;
    }

    @Basic
    @Column(name = "RESULT_OTHER_DESC")
    public String getResultOtherDescription() {
	return resultOtherDescription;
    }

    public void setResultOtherDescription(String resultOtherDescription) {
	this.resultOtherDescription = resultOtherDescription;
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
    }

    @Override
    public Long calculateContentId() {
	return instanceId;
    }

    @Override
    public String calculateContent() {
	return "referring:" + referring + AUDIT_SEPARATOR +
		"location:" + location + AUDIT_SEPARATOR +
		"regionsCodes:" + regionsCodes + AUDIT_SEPARATOR +
		"destination:" + destination + AUDIT_SEPARATOR +
		"purpose:" + purpose + AUDIT_SEPARATOR +
		"empExternalFlag:" + empExternalFlag + AUDIT_SEPARATOR +
		"empExternalStatus:" + empExternalStatus + AUDIT_SEPARATOR +
		"period:" + period + AUDIT_SEPARATOR +
		"roadPeriod:" + roadPeriod + AUDIT_SEPARATOR +
		"startDate:" + startDate + AUDIT_SEPARATOR +
		"ministeryApprovalNumber:" + ministeryApprovalNumber + AUDIT_SEPARATOR +
		"ministeryApprovalDate:" + ministeryApprovalDate + AUDIT_SEPARATOR +
		"roadLine:" + roadLine + AUDIT_SEPARATOR +
		"doubleBalanceFlag:" + doubleBalanceFlag + AUDIT_SEPARATOR +
		"hajjFlag:" + hajjFlag + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR +
		"internalCopies:" + internalCopies + AUDIT_SEPARATOR +
		"externalCopies:" + externalCopies + AUDIT_SEPARATOR +
		"previousFlag:" + previousFlag + AUDIT_SEPARATOR +
		"previousDescription:" + previousDescription + AUDIT_SEPARATOR +
		"previousExecutedFlag:" + previousExecutedFlag + AUDIT_SEPARATOR +
		"relatedFlag:" + relatedFlag + AUDIT_SEPARATOR +
		"notExecutedRelatedFlag:" + notExecutedRelatedFlag + AUDIT_SEPARATOR +
		"relatedDescription:" + relatedDescription + AUDIT_SEPARATOR +
		"basedOnFlag:" + basedOnFlag + AUDIT_SEPARATOR +
		"basedOnNumber:" + basedOnNumber + AUDIT_SEPARATOR +
		"basedOnDate:" + basedOnDate + AUDIT_SEPARATOR +
		"resultReportFlag:" + resultReportFlag + AUDIT_SEPARATOR +
		"resultLetterFlag:" + resultLetterFlag + AUDIT_SEPARATOR +
		"resultOtherFlag:" + resultOtherFlag + AUDIT_SEPARATOR +
		"resultLetterDescription:" + resultLetterDescription + AUDIT_SEPARATOR +
		"resultOtherDescription:" + resultOtherDescription + AUDIT_SEPARATOR;
    }
}
