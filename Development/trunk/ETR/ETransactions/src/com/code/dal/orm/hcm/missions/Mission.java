package com.code.dal.orm.hcm.missions;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.orm.BaseEntity;

/**
 * Provides all information regarding mission transaction such as mission purpose, its location, start date, and end date, .. etc <br/>
 * 
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_MSN_TRANSACTIONS")
public class Mission extends BaseEntity {

    private Long id;
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
    private Integer eflag;
    private Integer migFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String attachments;
    private Integer categoryMode;
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

    @SequenceGenerator(name = "HCMMissionsSeq", sequenceName = "HCM_MSN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMMissionsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "CATEGORY_MODE")
    public Integer getCategoryMode() {
	return categoryMode;
    }

    public void setCategoryMode(Integer categoryMode) {
	this.categoryMode = categoryMode;
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

}
