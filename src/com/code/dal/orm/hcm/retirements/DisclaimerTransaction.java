package com.code.dal.orm.hcm.retirements;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "HCM_RET_DSCLMR_TRANSACTIONS")
public class DisclaimerTransaction extends BaseEntity {
    private Long id;
    private String decisionNumber;
    private Date decisionDate;
    private Long empId;
    private Long terminationTransactionId;
    private Double basicAmount;
    private Double allowanceAmount;
    private String deductionReason;
    private Double realEstateFundAmount;
    private Double creditBankAmount;
    private Double otherAmount;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Integer migFlag;
    private Integer eFlag;

    @SequenceGenerator(name = "HcmRetSeq",
	    sequenceName = "HCM_RET_SEQ")
    @GeneratedValue(generator = "HcmRetSeq")
    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "TERMINATION_TRANSACTION_ID")
    public Long getTerminationTransactionId() {
	return terminationTransactionId;
    }

    public void setTerminationTransactionId(Long terminationTransactionId) {
	this.terminationTransactionId = terminationTransactionId;
    }

    @Basic
    @Column(name = "BASIC_AMOUNT")
    public Double getBasicAmount() {
	return basicAmount;
    }

    public void setBasicAmount(Double basicAmount) {
	this.basicAmount = basicAmount;
    }

    @Basic
    @Column(name = "ALLOWANCE_AMOUNT")
    public Double getAllowanceAmount() {
	return allowanceAmount;
    }

    public void setAllowanceAmount(Double allowanceAmount) {
	this.allowanceAmount = allowanceAmount;
    }

    @Basic
    @Column(name = "DEDUCTION_REASON")
    public String getDeductionReason() {
	return deductionReason;
    }

    public void setDeductionReason(String deductionReason) {
	this.deductionReason = deductionReason;
    }

    @Basic
    @Column(name = "REAL_ESTATE_FUND_AMOUNT")
    public Double getRealEstateFundAmount() {
	return realEstateFundAmount;
    }

    public void setRealEstateFundAmount(Double realEstateFundAmount) {
	this.realEstateFundAmount = realEstateFundAmount;
    }

    @Basic
    @Column(name = "CREDIT_BANK_AMOUNT")
    public Double getCreditBankAmount() {
	return creditBankAmount;
    }

    public void setCreditBankAmount(Double creditBankAmount) {
	this.creditBankAmount = creditBankAmount;
    }

    @Basic
    @Column(name = "OTHER_AMOUNT")
    public Double getOtherAmount() {
	return otherAmount;
    }

    public void setOtherAmount(Double otherAmount) {
	this.otherAmount = otherAmount;
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
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
    }

    @Basic
    @Column(name = "E_FLAG")
    public Integer geteFlag() {
	return eFlag;
    }

    public void seteFlag(Integer eFlag) {
	this.eFlag = eFlag;
    }

}
