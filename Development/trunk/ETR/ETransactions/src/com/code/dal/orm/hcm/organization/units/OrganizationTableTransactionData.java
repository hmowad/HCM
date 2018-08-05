package com.code.dal.orm.hcm.organization.units;

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
 * The <code>OrganizationTableTransactionData</code> class represents the organization tables transactions history in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_organizationTableTransactionData_getOrganizationTableTransactionsByUnitIdAndCatgeoryClass",
		query = " select t from OrganizationTableTransactionData t " +
			" where t.unitId = :P_UNIT_ID " +
			" and t.organizationTableCategoryClass = :P_CATEGORY_CLASS " +
			" order by t.transactionDate desc, t.id desc"
	)
})
@Entity
@Table(name = "HCM_VW_ORG_TABLES_TRANSACTIONS")
public class OrganizationTableTransactionData extends BaseEntity {

    private Long id;
    private Long unitId;
    private Long organizationTableId;
    private Integer organizationTableCategoryClass;
    private Long organizationTableDetailId;
    private String basicJobName;
    private Long rankId;
    private String rankDescription;
    private Integer jobsCount;
    private Integer generalSpecialization;
    private Long minorSpecializationId;
    private String minorSpecializationDescription;
    private Long classificationId;
    private String classificationJobCode;
    private String classificationDescription;
    private Integer generalType;
    private Integer approvedFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long transactionTypeId;
    private String transactionTypeDescription;
    private Long transactionUserId;
    private String transactionUserFullName;
    private Date transactionDate;
    private String transactionDateString;
    private Integer activeFlag;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "ORG_TABLE_CATEGORY_CLASS")
    public Integer getOrganizationTableCategoryClass() {
	return organizationTableCategoryClass;
    }

    public void setOrganizationTableCategoryClass(Integer organizationTableCategoryClass) {
	this.organizationTableCategoryClass = organizationTableCategoryClass;
    }

    @Basic
    @Column(name = "ORG_TABLE_ID")
    public Long getOrganizationTableId() {
	return organizationTableId;
    }

    public void setOrganizationTableId(Long organizationTableId) {
	this.organizationTableId = organizationTableId;
    }

    @Basic
    @Column(name = "ORG_TABLE_DETAIL_ID")
    public Long getOrganizationTableDetailId() {
	return organizationTableDetailId;
    }

    public void setOrganizationTableDetailId(Long organizationTableDetailId) {
	this.organizationTableDetailId = organizationTableDetailId;
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME")
    public String getBasicJobName() {
	return basicJobName;
    }

    public void setBasicJobName(String basicJobName) {
	this.basicJobName = basicJobName;
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
    @Column(name = "JOBS_COUNT")
    public Integer getJobsCount() {
	return jobsCount;
    }

    public void setJobsCount(Integer jobsCount) {
	this.jobsCount = jobsCount;
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
    @Column(name = "MINOR_SPEC_DESCRIPTION")
    public String getMinorSpecializationDescription() {
	return minorSpecializationDescription;
    }

    public void setMinorSpecializationDescription(String minorSpecializationDescription) {
	this.minorSpecializationDescription = minorSpecializationDescription;
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
    public String getClassificationDescription() {
	return classificationDescription;
    }

    public void setClassificationDescription(String classificationDescription) {
	this.classificationDescription = classificationDescription;
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
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
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
    @Column(name = "TRANSACTION_TYPE_DESCRIPTION")
    public String getTransactionTypeDescription() {
	return transactionTypeDescription;
    }

    public void setTransactionTypeDescription(String transactionTypeDescription) {
	this.transactionTypeDescription = transactionTypeDescription;
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
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
    }

}
