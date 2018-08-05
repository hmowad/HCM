package com.code.dal.orm.hcm.organization.jobs;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>JobTransactionData</code> class represents the transactions that have been occurred on the jobs (like creation, renaming, cancellation and etc...) in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_jobTransactionData_getJobTransactionsByJobId",
		query = " from JobTransactionData t" +
			" where t.jobId = :P_JOB_ID " +
			" order by t.executionDate desc, t.id desc ")
})
@Entity
@Table(name = "HCM_VW_ORG_JOBS_TRANSACTIONS")
public class JobTransactionData extends BaseEntity {
    private Long id;
    private Long jobId;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long transactionTypeId;
    private Integer transactionTypeCode;
    private String transactionTypeDescription;
    private Integer transactionClass;
    private String code;
    private String name;
    private Long rankId;
    private String rankDescription;
    private Long regionId;
    private String regionCode;
    private String regionDescription;
    private Long unitId;
    private String unitFullName;
    private Long classificationId;
    private String classificationJobCode;
    private String classificationJobDescription;
    private String dutiesDesc;
    private String jobDesc;
    private Integer generalSpecialization;
    private Long minorSpecializationId;
    private Integer minorSpecializationCode;
    private String minorSpecializationDescription;
    private Long majorSpecializationId;
    private Integer majorSpecializationCode;
    private String majorSpecializationDescription;
    private Integer generalType;
    private Date executionDate;
    private String executionDateString;
    private String remarks;
    private String reasons;
    private Long transactionUserId;
    private String transactionUserFullName;
    private Date transactionDate;
    private String transactionDateString;
    private Integer status;
    private Integer reservationStatus;

    private String referring;
    private String attachments;
    private String internalCopies;
    private String externalCopies;
    private Integer eFlag;
    private Integer migFlag;
    private String transCode;
    private String transName;
    private Long transUnitId;
    private String transUnitFullName;
    private Long transRankId;
    private String transRankDesc;
    private Long transMinorSpecId;
    private String transMinorSpecDesc;
    private Long decisionRegionId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
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
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
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
    @Column(name = "TRANSACTION_TYPE_CODE")
    public Integer getTransactionTypeCode() {
	return transactionTypeCode;
    }

    public void setTransactionTypeCode(Integer transactionTypeCode) {
	this.transactionTypeCode = transactionTypeCode;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_DESCRIPTION")
    public String getTransactionTypeDescription() {
	return transactionTypeDescription;
    }

    public void setTransactionTypeDescription(String transactionTypeDescription) {
	this.transactionTypeDescription = transactionTypeDescription;
    }

    @Basic
    @Column(name = "TRANSACTION_CLASS")
    public Integer getTransactionClass() {
	return transactionClass;
    }

    public void setTransactionClass(Integer transactionClass) {
	this.transactionClass = transactionClass;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    @Basic
    @Column(name = "RANK_DESCRIPTION")
    public String getRankDescription() {
	return rankDescription;
    }

    public void setRankDescription(String rankDescription) {
	this.rankDescription = rankDescription;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "REGION_CODE")
    public String getRegionCode() {
	return regionCode;
    }

    public void setRegionCode(String regionCode) {
	this.regionCode = regionCode;
    }

    @Basic
    @Column(name = "REGION_DESCRIPTION")
    public String getRegionDescription() {
	return regionDescription;
    }

    public void setRegionDescription(String regionDescription) {
	this.regionDescription = regionDescription;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "UNIT_FULL_NAME")
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    @Basic
    @Column(name = "CLASSIFICATION_ID")
    public Long getClassificationId() {
	return classificationId;
    }

    public void setClassificationId(Long classificationId) {
	this.classificationId = classificationId;
    }

    @Basic
    @Column(name = "CLASSIFICATION_JOB_CODE")
    public String getClassificationJobCode() {
	return classificationJobCode;
    }

    public void setClassificationJobCode(String classificationJobCode) {
	this.classificationJobCode = classificationJobCode;
    }

    @Basic
    @Column(name = "CLASSIFICATION_JOB_DESCRIPTION")
    public String getClassificationJobDescription() {
	return classificationJobDescription;
    }

    public void setClassificationJobDescription(String classificationJobDescription) {
	this.classificationJobDescription = classificationJobDescription;
    }

    @Basic
    @Column(name = "DUTIES_DESC")
    public String getDutiesDesc() {
	return dutiesDesc;
    }

    public void setDutiesDesc(String dutiesDesc) {
	this.dutiesDesc = dutiesDesc;
    }

    @Basic
    @Column(name = "JOB_DESC")
    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
    }

    @Basic
    @Column(name = "MINOR_SPEC_CODE")
    public Integer getMinorSpecializationCode() {
	return minorSpecializationCode;
    }

    public void setMinorSpecializationCode(Integer minorSpecializationCode) {
	this.minorSpecializationCode = minorSpecializationCode;
    }

    @Basic
    @Column(name = "MINOR_SPEC_DESCRIPTION")
    public String getMinorSpecializationDescription() {
	return minorSpecializationDescription;
    }

    public void setMinorSpecializationDescription(String minorSpecializationDescription) {
	this.minorSpecializationDescription = minorSpecializationDescription;
    }

    @Basic
    @Column(name = "MAJOR_SPECIALIZATION_ID")
    public Long getMajorSpecializationId() {
	return majorSpecializationId;
    }

    public void setMajorSpecializationId(Long majorSpecializationId) {
	this.majorSpecializationId = majorSpecializationId;
    }

    @Basic
    @Column(name = "MAJOR_SPEC_CODE")
    public Integer getMajorSpecializationCode() {
	return majorSpecializationCode;
    }

    public void setMajorSpecializationCode(Integer majorSpecializationCode) {
	this.majorSpecializationCode = majorSpecializationCode;
    }

    @Basic
    @Column(name = "MAJOR_SPEC_DESCRIPTION")
    public String getMajorSpecializationDescription() {
	return majorSpecializationDescription;
    }

    public void setMajorSpecializationDescription(String majorSpecializationDescription) {
	this.majorSpecializationDescription = majorSpecializationDescription;
    }

    @Basic
    @Column(name = "GENERAL_TYPE")
    public Integer getGeneralType() {
	return generalType;
    }

    public void setGeneralType(Integer generalType) {
	this.generalType = generalType;
    }

    @Basic
    @Column(name = "EXECUTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
	this.executionDateString = HijriDateService.getHijriDateString(executionDate);
    }

    @Transient
    public String getExecutionDateString() {
	return executionDateString;
    }

    public void setExecutionDateString(String executionDateString) {
	this.executionDateString = executionDateString;
	this.executionDate = HijriDateService.getHijriDate(executionDateString);
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
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    @Basic
    @Column(name = "TRANSACTION_USER_ID")
    public Long getTransactionUserId() {
	return transactionUserId;
    }

    public void setTransactionUserId(Long transactionUserId) {
	this.transactionUserId = transactionUserId;
    }

    @Basic
    @Column(name = "TRANSACTION_USER_FULL_NAME")
    public String getTransactionUserFullName() {
	return transactionUserFullName;
    }

    public void setTransactionUserFullName(String transactionUserFullName) {
	this.transactionUserFullName = transactionUserFullName;
    }

    @Basic
    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransactionDate() {
	return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
	this.transactionDateString = HijriDateService.getHijriDateString(transactionDate);
    }

    @Transient
    public String getTransactionDateString() {
	return transactionDateString;
    }

    public void setTransactionDateString(String transactionDateString) {
	this.transactionDateString = transactionDateString;
	this.transactionDate = HijriDateService.getHijriDate(transactionDateString);
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "RESERVATION_STATUS")
    public Integer getReservationStatus() {
	return reservationStatus;
    }

    public void setReservationStatus(Integer reservationStatus) {
	this.reservationStatus = reservationStatus;
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
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
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
    @Column(name = "EFLAG")
    public Integer geteFlag() {
	return eFlag;
    }

    public void seteFlag(Integer eFlag) {
	this.eFlag = eFlag;
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
    @Column(name = "TRANS_CODE")
    public String getTransCode() {
	return transCode;
    }

    public void setTransCode(String transCode) {
	this.transCode = transCode;
    }

    @Basic
    @Column(name = "TRANS_NAME")
    public String getTransName() {
	return transName;
    }

    public void setTransName(String transName) {
	this.transName = transName;
    }

    @Basic
    @Column(name = "TRANS_UNIT_ID")
    public Long getTransUnitId() {
	return transUnitId;
    }

    public void setTransUnitId(Long transUnitId) {
	this.transUnitId = transUnitId;
    }

    @Basic
    @Column(name = "TRANS_UNIT_FULL_NAME")
    public String getTransUnitFullName() {
	return transUnitFullName;
    }

    public void setTransUnitFullName(String transUnitFullName) {
	this.transUnitFullName = transUnitFullName;
    }

    @Basic
    @Column(name = "TRANS_RANK_ID")
    public Long getTransRankId() {
	return transRankId;
    }

    public void setTransRankId(Long transRankId) {
	this.transRankId = transRankId;
    }

    @Basic
    @Column(name = "TRANS_RANK_DESC")
    public String getTransRankDesc() {
	return transRankDesc;
    }

    public void setTransRankDesc(String transRankDesc) {
	this.transRankDesc = transRankDesc;
    }

    @Basic
    @Column(name = "TRANS_MINOR_SPEC_ID")
    public Long getTransMinorSpecId() {
	return transMinorSpecId;
    }

    public void setTransMinorSpecId(Long transMinorSpecId) {
	this.transMinorSpecId = transMinorSpecId;
    }

    @Basic
    @Column(name = "TRANS_MINOR_SPEC_DESC")
    public String getTransMinorSpecDesc() {
	return transMinorSpecDesc;
    }

    public void setTransMinorSpecDesc(String transMinorSpecDesc) {
	this.transMinorSpecDesc = transMinorSpecDesc;
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

}