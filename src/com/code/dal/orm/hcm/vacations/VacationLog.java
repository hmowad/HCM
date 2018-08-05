package com.code.dal.orm.hcm.vacations;

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
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>VacationLog</code> class keeps the history of all the operations that have been occurred on each vacation transaction (like Modifying and Canceling).
 * 
 * 
 */
@Entity
@Table(name = "HCM_VAC_LOG")
public class VacationLog extends BaseEntity {
    private Long vacationLogId;
    private Long vacationId;
    private Date endDate;
    private String endDateString;
    private Integer period;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Integer status;
    private Date extStartDate;
    private String extStartDateString;
    private Date extEndDate;
    private String extEndDateString;
    private Integer extPeriod;
    private String extLocation;
    private Integer locationFlag;
    private Long approvedId;
    private String transactionApprovedRankDesc;
    private String transactionApprovedJobDesc;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Long decisionRegionId;
    private String transactionEmpRankDesc;
    private String transactionEmpJobCode;
    private String transactionEmpJobDesc;
    private String transactionEmpUnitDesc;
    private Long transactionEmpCategoryId;
    private String decisionData;
    private Date contractualYearStartDate;
    private String contractualYearStartDateString;
    private Date contractualYearEndDate;
    private String contractualYearEndDateString;
    private String externalCopies;
    private String referring;

    public void setVacationLogId(Long vacationLogId) {
	this.vacationLogId = vacationLogId;
    }

    @SequenceGenerator(name = "HCMVacationsSeq",
	    sequenceName = "HCM_VACATIONS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMVacationsSeq")
    @Column(name = "ID")
    public Long getVacationLogId() {
	return vacationLogId;
    }

    public void setVacationId(Long vacationId) {
	this.vacationId = vacationId;
    }

    @Basic
    @Column(name = "VACATION_ID")
    public Long getVacationId() {
	return vacationId;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    @Basic
    @Column(name = "EXT_START_DATE")
    public Date getExtStartDate() {
	return extStartDate;
    }

    public void setExtStartDate(Date extStartDate) {
	this.extStartDate = extStartDate;
	this.extStartDateString = HijriDateService.getHijriDateString(extStartDate);
    }

    @Transient
    public String getExtStartDateString() {
	return extStartDateString;
    }

    public void setExtStartDateString(String extStartDateString) {
	this.extStartDateString = extStartDateString;
	this.extStartDate = HijriDateService.getHijriDate(extStartDateString);
    }

    @Basic
    @Column(name = "EXT_END_DATE")
    public Date getExtEndDate() {
	return extEndDate;
    }

    public void setExtEndDate(Date extEndDate) {
	this.extEndDate = extEndDate;
	this.extEndDateString = HijriDateService.getHijriDateString(extEndDate);

    }

    @Transient
    public String getExtEndDateString() {
	return extEndDateString;
    }

    public void setExtEndDateString(String extEndDateString) {
	this.extEndDateString = extEndDateString;
	this.extEndDate = HijriDateService.getHijriDate(extEndDateString);

    }

    @Basic
    @Column(name = "EXT_PERIOD")
    public Integer getExtPeriod() {
	return extPeriod;
    }

    public void setExtPeriod(Integer extPeriod) {
	this.extPeriod = extPeriod;
    }

    @Basic
    @Column(name = "EXT_LOCATION")
    public String getExtLocation() {
	return extLocation;
    }

    public void setExtLocation(String extLocation) {
	this.extLocation = extLocation;
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
    @Column(name = "APPROVED_ID")
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
    }

    @Basic
    @Column(name = "TRANS_APPROVED_RANK_DESC")
    public String getTransactionApprovedRankDesc() {
	return transactionApprovedRankDesc;
    }

    public void setTransactionApprovedRankDesc(String transactionApprovedRankDesc) {
	this.transactionApprovedRankDesc = transactionApprovedRankDesc;
    }

    @Basic
    @Column(name = "TRANS_APPROVED_JOB_DESC")
    public String getTransactionApprovedJobDesc() {
	return transactionApprovedJobDesc;
    }

    public void setTransactionApprovedJobDesc(String transactionApprovedJobDesc) {
	this.transactionApprovedJobDesc = transactionApprovedJobDesc;
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
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransactionEmpRankDesc() {
	return transactionEmpRankDesc;
    }

    public void setTransactionEmpRankDesc(String transactionEmpRankDesc) {
	this.transactionEmpRankDesc = transactionEmpRankDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    public String getTransactionEmpJobCode() {
	return transactionEmpJobCode;
    }

    public void setTransactionEmpJobCode(String transactionEmpJobCode) {
	this.transactionEmpJobCode = transactionEmpJobCode;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_DESC")
    public String getTransactionEmpJobDesc() {
	return transactionEmpJobDesc;
    }

    public void setTransactionEmpJobDesc(String transactionEmpJobDesc) {
	this.transactionEmpJobDesc = transactionEmpJobDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_DESC")
    public String getTransactionEmpUnitDesc() {
	return transactionEmpUnitDesc;
    }

    public void setTransactionEmpUnitDesc(String transactionEmpUnitDesc) {
	this.transactionEmpUnitDesc = transactionEmpUnitDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_CATEGORY_ID")
    public Long getTransactionEmpCategoryId() {
	return transactionEmpCategoryId;
    }

    public void setTransactionEmpCategoryId(Long transactionEmpCategoryId) {
	this.transactionEmpCategoryId = transactionEmpCategoryId;
    }

    @Basic
    @Column(name = "DECISION_DATA")
    public String getDecisionData() {
	return decisionData;
    }

    public void setDecisionData(String decisionData) {
	this.decisionData = decisionData;
    }

    @Basic
    @Column(name = "CONTRACTUAL_YEAR_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getContractualYearStartDate() {
	return contractualYearStartDate;
    }

    public void setContractualYearStartDate(Date contractualYearStartDate) {
	this.contractualYearStartDate = contractualYearStartDate;
	this.contractualYearStartDateString = HijriDateService.getHijriDateString(contractualYearStartDate);
    }

    @Transient
    public String getContractualYearStartDateString() {
	return contractualYearStartDateString;
    }

    public void setContractualYearStartDateString(String contractualYearStartDateString) {
	this.contractualYearStartDateString = contractualYearStartDateString;
	this.contractualYearStartDate = HijriDateService.getHijriDate(contractualYearStartDateString);
    }

    @Basic
    @Column(name = "CONTRACTUAL_YEAR_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getContractualYearEndDate() {
	return contractualYearEndDate;
    }

    public void setContractualYearEndDate(Date contractualYearEndDate) {
	this.contractualYearEndDate = contractualYearEndDate;
	this.contractualYearEndDateString = HijriDateService.getHijriDateString(contractualYearEndDate);
    }

    @Transient
    public String getContractualYearEndDateString() {
	return contractualYearEndDateString;
    }

    public void setContractualYearEndDateString(String contractualYearEndDateString) {
	this.contractualYearEndDateString = contractualYearEndDateString;
	this.contractualYearEndDate = HijriDateService.getHijriDate(contractualYearEndDateString);
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
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
    }

}