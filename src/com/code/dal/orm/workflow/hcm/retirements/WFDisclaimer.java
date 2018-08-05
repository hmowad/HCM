package com.code.dal.orm.workflow.hcm.retirements;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "WF_DISCLAIMERS")
public class WFDisclaimer extends AuditEntity implements UpdatableAuditEntity {

    private Long instanceId;
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

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
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

    @Override
    public Long calculateContentId() {
	return instanceId;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"terminationTransactionId:" + terminationTransactionId + AUDIT_SEPARATOR +
		"basicAmount:" + basicAmount + AUDIT_SEPARATOR +
		"allowanceAmount:" + allowanceAmount + AUDIT_SEPARATOR +
		"deductionReason:" + deductionReason + AUDIT_SEPARATOR +
		"realEstateFundAmount:" + realEstateFundAmount + AUDIT_SEPARATOR +
		"creditBankAmount:" + creditBankAmount + AUDIT_SEPARATOR +
		"otherAmount:" + otherAmount + AUDIT_SEPARATOR +
		"decisionApprovedId:" + decisionApprovedId + AUDIT_SEPARATOR +
		"originalDecisionApprovedId:" + originalDecisionApprovedId;

    }

}
