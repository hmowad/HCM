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
@Table(name = "HCM_TRN_TRANSACTIONS_DTLS")
public class TrainingTransactionDetail extends BaseEntity {
    private Long id;
    private Long trainingTransactionId;
    private Long transactionTypeId;
    private Date decisionDate;
    private String decisionNumber;
    private Long decisionRegionId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Long transEmpCategoryId;
    private String transEmpJobCode;
    private String transEmpJobName;
    private String transEmpRankDesc;
    private String transEmpUnitFullName;
    private String directedToJobName;
    private String ministryDecisionNumber;
    private Date ministryDecisionDate;
    private String ministryReportNumber;
    private Date ministryReportDate;
    private Date studyStartDate;
    private Date studyEndDate;
    private Integer studyMonthsCount;
    private Integer studyDaysCount;
    private String attachments;
    private String reasons;
    private String internalCopies;
    private String externalCopies;

    @SequenceGenerator(name = "HCMTrainingSeq",
	    sequenceName = "HCM_TRN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingSeq")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "TRAINING_TRANSACTION_ID")
    public Long getTrainingTransactionId() {
	return trainingTransactionId;
    }

    public void setTrainingTransactionId(Long trainingTransactionId) {
	this.trainingTransactionId = trainingTransactionId;
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
    @Column(name = "TRANS_EMP_CATEGORY_ID")
    public Long getTransEmpCategoryId() {
	return transEmpCategoryId;
    }

    public void setTransEmpCategoryId(Long transEmpCategoryId) {
	this.transEmpCategoryId = transEmpCategoryId;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    public String getTransEmpJobCode() {
	return transEmpJobCode;
    }

    public void setTransEmpJobCode(String transEmpJobCode) {
	this.transEmpJobCode = transEmpJobCode;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_NAME")
    public String getTransEmpJobName() {
	return transEmpJobName;
    }

    public void setTransEmpJobName(String transEmpJobName) {
	this.transEmpJobName = transEmpJobName;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_FULL_NAME")
    public String getTransEmpUnitFullName() {
	return transEmpUnitFullName;
    }

    public void setTransEmpUnitFullName(String transEmpUnitFullName) {
	this.transEmpUnitFullName = transEmpUnitFullName;
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
    }

    @Basic
    @Column(name = "MINISTRY_DECISION_NUMBER")
    public String getMinistryDecisionNumber() {
	return ministryDecisionNumber;
    }

    public void setMinistryDecisionNumber(String ministryDecisionNumber) {
	this.ministryDecisionNumber = ministryDecisionNumber;
    }

    @Basic
    @Column(name = "MINISTRY_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryDecisionDate() {
	return ministryDecisionDate;
    }

    public void setMinistryDecisionDate(Date ministryDecisionDate) {
	this.ministryDecisionDate = ministryDecisionDate;
    }

    @Basic
    @Column(name = "MINISTRY_REPORT_NUMBER")
    public String getMinistryReportNumber() {
	return ministryReportNumber;
    }

    public void setMinistryReportNumber(String ministryReportNumber) {
	this.ministryReportNumber = ministryReportNumber;
    }

    @Basic
    @Column(name = "MINISTRY_REPORT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryReportDate() {
	return ministryReportDate;
    }

    public void setMinistryReportDate(Date ministryReportDate) {
	this.ministryReportDate = ministryReportDate;
    }

    @Basic
    @Column(name = "STUDY_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyStartDate() {
	return studyStartDate;
    }

    public void setStudyStartDate(Date studyStartDate) {
	this.studyStartDate = studyStartDate;
    }

    @Basic
    @Column(name = "STUDY_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyEndDate() {
	return studyEndDate;
    }

    public void setStudyEndDate(Date studyEndDate) {
	this.studyEndDate = studyEndDate;
    }

    @Basic
    @Column(name = "STUDY_MONTHS_COUNT")
    public Integer getStudyMonthsCount() {
	return studyMonthsCount;
    }

    public void setStudyMonthsCount(Integer studyMonthsCount) {
	this.studyMonthsCount = studyMonthsCount;
    }

    @Basic
    @Column(name = "STUDY_DAYS_COUNT")
    public Integer getStudyDaysCount() {
	return studyDaysCount;
    }

    public void setStudyDaysCount(Integer studyDaysCount) {
	this.studyDaysCount = studyDaysCount;
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
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
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
}
