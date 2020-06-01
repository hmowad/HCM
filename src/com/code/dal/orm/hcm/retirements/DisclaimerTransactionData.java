package com.code.dal.orm.hcm.retirements;

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

@NamedQueries({
	@NamedQuery(name = "hcm_disclaimerTransactionData_getDisclaimerTransaction",
		query = " select d from DisclaimerTransactionData d " +
			" where (:P_EMP_ID = -1 or d.empId = :P_EMP_ID) "
			+ "and (:P_TERM_TRANS_ID = -1 or terminationTransactionId = :P_TERM_TRANS_ID)" +
			" order by d.transServiceTerminationDate"),

	@NamedQuery(name = "hcm_disclaimerTransactionData_getDisclaimerTransactionBasedOnDecisionDate",
		query = " select d from DisclaimerTransactionData d "
			+ " where (:P_EMP_ID = -1 or d.empId = :P_EMP_ID) "
			+ " and (:P_DECISION_DATE_FLAG = -1 or to_date(:P_DECISION_DATE, 'MI/MM/YYYY') <= d.decisionDate) "
			+ " order by d.decisionDate"),
	@NamedQuery(name = "hcm_disclaimerTransactionData_getDisclaimerTransactionBasedOnDecisionDateAndDecisionNumber",
		query = " select d from DisclaimerTransactionData d " +
			" where (:P_DECISION_NUMBER = '-1' or d.decisionNumber = :P_DECISION_NUMBER) "
			+ "and (:P_DECISION_DATE_FLAG = -1 or to_date(:P_DECISION_DATE, 'MI/MM/YYYY') = d.decisionDate)" +
			" order by d.decisionDate")
})

@Entity
@Table(name = "HCM_VW_RET_DSCLMR_TRANSACTIONS")
public class DisclaimerTransactionData extends BaseEntity {

    private Long id;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long empId;
    private Long transEmpCategoryId;
    private Long terminationTransactionId;
    private String transTerminationDecisionNumber;
    private Date transTerminationDecisionDate;
    private String transTerminationDecisionDateString;
    private String transEmpRankDesc;
    private String transEmpJobDesc;
    private String transEmpUnitFullName;
    private Long transEmpUnitId;
    private String transTerminationReason;
    private Date transServiceTerminationDate;
    private String transServiceTerminationDateString;
    private Double basicAmount;
    private Double allowanceAmount;
    private String deductionReason;
    private Double realEstateFundAmount;
    private Double creditBankAmount;
    private Double otherAmount;
    private Double totalDueAmount;
    private Double totalGovernmentalDueAmount;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Integer migFlag;
    private Integer eFlag;

    private DisclaimerTransaction disclaimerTransaction;

    public DisclaimerTransactionData() {
	disclaimerTransaction = new DisclaimerTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	disclaimerTransaction.setId(id);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	disclaimerTransaction.setDecisionNumber(decisionNumber);
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
	disclaimerTransaction.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	disclaimerTransaction.setDecisionDate(decisionDate);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	disclaimerTransaction.setEmpId(empId);
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
    @Column(name = "TERMINATION_TRANSACTION_ID")
    public Long getTerminationTransactionId() {
	return terminationTransactionId;
    }

    public void setTerminationTransactionId(Long terminationTransactionId) {
	this.terminationTransactionId = terminationTransactionId;
	disclaimerTransaction.setTerminationTransactionId(terminationTransactionId);
    }

    @Basic
    @Column(name = "TERMINATION_DECISION_NUMBER")
    public String getTransTerminationDecisionNumber() {
	return transTerminationDecisionNumber;
    }

    public void setTransTerminationDecisionNumber(String transTerminationDecisionNumber) {
	this.transTerminationDecisionNumber = transTerminationDecisionNumber;
    }

    @Basic
    @Column(name = "TRANS_SERVICE_TERM_DEC_DATE")
    public Date getTransTerminationDecisionDate() {
	return transTerminationDecisionDate;
    }

    public void setTransTerminationDecisionDate(Date transTerminationDecisionDate) {
	this.transTerminationDecisionDate = transTerminationDecisionDate;
	this.transTerminationDecisionDateString = HijriDateService.getHijriDateString(transTerminationDecisionDate);
    }

    @Transient
    public String getTransTerminationDecisionDateString() {
	return transTerminationDecisionDateString;
    }

    public void setTransTerminationDecisionDateString(String transTerminationDecisionDateString) {
	this.transTerminationDecisionDateString = transTerminationDecisionDateString;
	this.transServiceTerminationDate = HijriDateService.getHijriDate(transTerminationDecisionDateString);
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
    @Column(name = "TRANS_EMP_JOB_DESC")
    public String getTransEmpJobDesc() {
	return transEmpJobDesc;
    }

    public void setTransEmpJobDesc(String transEmpJobDesc) {
	this.transEmpJobDesc = transEmpJobDesc;
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
    @Column(name = "TRANS_EMP_UNIT_ID")
    public Long getTransEmpUnitId() {
	return transEmpUnitId;
    }

    public void setTransEmpUnitId(Long transEmpUnitId) {
	this.transEmpUnitId = transEmpUnitId;
    }

    @Basic
    @Column(name = "TRANS_TERMINATION_REASON")
    public String getTransTerminationReason() {
	return transTerminationReason;
    }

    public void setTransTerminationReason(String transTerminationReason) {
	this.transTerminationReason = transTerminationReason;
    }

    @Basic
    @Column(name = "TRANS_SERVICE_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransServiceTerminationDate() {
	return transServiceTerminationDate;
    }

    public void setTransServiceTerminationDate(Date transServiceTerminationDate) {
	this.transServiceTerminationDate = transServiceTerminationDate;
	this.transServiceTerminationDateString = HijriDateService.getHijriDateString(transServiceTerminationDate);
    }

    @Transient
    public String getTransServiceTerminationDateString() {
	return transServiceTerminationDateString;
    }

    public void setTransServiceTerminationDateString(String transServiceTerminationDateString) {
	this.transServiceTerminationDateString = transServiceTerminationDateString;
	this.transServiceTerminationDate = HijriDateService.getHijriDate(transServiceTerminationDateString);
    }

    @Basic
    @Column(name = "BASIC_AMOUNT")
    public Double getBasicAmount() {
	return basicAmount;
    }

    public void setBasicAmount(Double basicAmount) {
	this.basicAmount = basicAmount;
	disclaimerTransaction.setBasicAmount(basicAmount);
    }

    @Basic
    @Column(name = "ALLOWANCE_AMOUNT")
    public Double getAllowanceAmount() {
	return allowanceAmount;
    }

    public void setAllowanceAmount(Double allowanceAmount) {
	this.allowanceAmount = allowanceAmount;
	disclaimerTransaction.setAllowanceAmount(allowanceAmount);
    }

    @Basic
    @Column(name = "DEDUCTION_REASON")
    public String getDeductionReason() {
	return deductionReason;
    }

    public void setDeductionReason(String deductionReason) {
	this.deductionReason = deductionReason;
	disclaimerTransaction.setDeductionReason(deductionReason);
    }

    @Basic
    @Column(name = "REAL_ESTATE_FUND_AMOUNT")
    public Double getRealEstateFundAmount() {
	return realEstateFundAmount;
    }

    public void setRealEstateFundAmount(Double realEstateFundAmount) {
	this.realEstateFundAmount = realEstateFundAmount;
	disclaimerTransaction.setRealEstateFundAmount(realEstateFundAmount);
    }

    @Basic
    @Column(name = "CREDIT_BANK_AMOUNT")
    public Double getCreditBankAmount() {
	return creditBankAmount;
    }

    public void setCreditBankAmount(Double creditBankAmount) {
	this.creditBankAmount = creditBankAmount;
	disclaimerTransaction.setCreditBankAmount(creditBankAmount);
    }

    @Basic
    @Column(name = "OTHER_AMOUNT")
    public Double getOtherAmount() {
	return otherAmount;
    }

    public void setOtherAmount(Double otherAmount) {
	this.otherAmount = otherAmount;
	disclaimerTransaction.setOtherAmount(otherAmount);
    }

    @Transient
    public Double getTotalDueAmount() {
	totalDueAmount = this.basicAmount + this.allowanceAmount;
	return totalDueAmount;
    }

    public void setTotalDueAmount(Double totalDueAmount) {
	this.totalDueAmount = totalDueAmount;
    }

    @Transient
    public Double getTotalGovernmentalDueAmount() {
	totalGovernmentalDueAmount = this.realEstateFundAmount + this.creditBankAmount;
	return totalGovernmentalDueAmount;
    }

    public void setTotalGovernmentalDueAmount(Double totalGovernmentalDueAmount) {
	this.totalGovernmentalDueAmount = totalGovernmentalDueAmount;
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	disclaimerTransaction.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	disclaimerTransaction.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
	disclaimerTransaction.setMigFlag(migFlag);
    }

    @Basic
    @Column(name = "E_FLAG")
    public Integer geteFlag() {
	return eFlag;
    }

    public void seteFlag(Integer eFlag) {
	this.eFlag = eFlag;
	disclaimerTransaction.seteFlag(eFlag);
    }

    @Transient
    public DisclaimerTransaction getDisclaimerTransaction() {
	return disclaimerTransaction;
    }

    public void setDisclaimerTransaction(DisclaimerTransaction disclaimerTransaction) {
	this.disclaimerTransaction = disclaimerTransaction;
    }

}
