package com.code.dal.orm.hcm.trainings;

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

@Entity
@Table(name = "HCM_TRN_COURSES_EVENTS_DECS")
public class TrainingCourseEventDecision extends BaseEntity {
    private Long id;
    private Long courseEventId;
    private Long transactionTypeId;
    private Date transStartDate;
    private Date transEndDate;
    private Date newStartDate;
    private Date newEndDate;
    private String reasons;
    private String notes;
    private String attachments;
    private Integer eflag;
    private Integer migFlag;
    private Date decisionDate;
    private String decisionNumber;
    private Long decisionRegionId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String internalCopies;
    private String externalCopies;
    private String allowance;

    @SequenceGenerator(name = "HCMTrainingPlanSeq",
	    sequenceName = "HCM_TRN_PLAN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingPlanSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
    }

    @Basic
    @Column(name = "TRANS_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransStartDate() {
	return transStartDate;
    }

    public void setTransStartDate(Date transStartDate) {
	this.transStartDate = transStartDate;
    }

    @Basic
    @Column(name = "TRANS_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransEndDate() {
	return transEndDate;
    }

    public void setTransEndDate(Date transEndDate) {
	this.transEndDate = transEndDate;
    }

    @Basic
    @Column(name = "NEW_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewStartDate() {
	return newStartDate;
    }

    public void setNewStartDate(Date newStartDate) {
	this.newStartDate = newStartDate;
    }

    @Basic
    @Column(name = "NEW_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewEndDate() {
	return newEndDate;
    }

    public void setNewEndDate(Date newEndDate) {
	this.newEndDate = newEndDate;
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    @Basic
    @Column(name = "NOTES")
    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
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
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
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
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
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
    @Column(name = "ALLOWANCE")
    public String getAllowance() {
	return allowance;
    }

    public void setAllowance(String allowance) {
	this.allowance = allowance;
    }
}
