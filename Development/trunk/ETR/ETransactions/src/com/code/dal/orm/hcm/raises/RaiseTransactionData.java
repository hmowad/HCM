package com.code.dal.orm.hcm.raises;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_raiseTransactionData_getNotExecutedRaisesTransactions",
		query = "select r from RaiseTransactionData r where r.effectFlag = 0 and r.raiseExecutionDate <= to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY')")
})
@Entity
@Table(name = "HCM_VW_RAISE_TRANSACTIONS")
public class RaiseTransactionData extends BaseEntity {
    private Long id;
    private Long empId;
    private String empName;
    private Integer deservedFlag;
    private String exclusionReason;
    private Long empNewDegreeId;
    private String empNewDegreeDesc;
    private Integer raiseType;
    private Date raiseExecutionDate;
    private String raiseExecutionDateString;
    private String raiseDecisionNumber;
    private Date raiseDecisionDate;
    private String raiseDecisionDateString;
    private Long raiseCategoryId;
    private String raiseCategoryDesc;
    private String remarks;
    private Integer effectFlag;
    private Integer eFlag;
    private Integer migFlag;
    private Long empDecisionApprovedId;
    private String empDecisionApprovedName;
    private Long empOriginalDecisionApprovedId;
    private String empOriginalDecisionApprovedName;
    private String transEmpJobName;
    private String transEmpUnitFullName;
    private String transEmpJobRankDesc;
    private String transEmpRankDesc;
    private String transEmpDegreeDesc;
    private RaiseTransaction raiseTransaction;

    public RaiseTransactionData() {
	raiseTransaction = new RaiseTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	raiseTransaction.setId(id);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	raiseTransaction.setEmpId(empId);
    }

    @Basic
    @Column(name = "EMP_NAME")
    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "DESERVED_FLAG")
    public Integer getDeservedFlag() {
	return deservedFlag;
    }

    public void setDeservedFlag(Integer deservedFlag) {
	this.deservedFlag = deservedFlag;
	raiseTransaction.setDeservedFlag(deservedFlag);
    }

    @Basic
    @Column(name = "EXCLUSION_REASON")
    public String getExclusionReason() {
	return exclusionReason;
    }

    public void setExclusionReason(String exclusionReason) {
	this.exclusionReason = exclusionReason;
	raiseTransaction.setExclusionReason(exclusionReason);
    }

    @Basic
    @Column(name = "EMP_NEW_DEGREE_ID")
    public Long getEmpNewDegreeId() {
	return empNewDegreeId;
    }

    public void setEmpNewDegreeId(Long empNewDegreeId) {
	this.empNewDegreeId = empNewDegreeId;
	raiseTransaction.setNewDegreeId(empNewDegreeId);
    }

    @Basic
    @Column(name = "EMP_NEW_DEGREE_DESC")
    public String getEmpNewDegreeDesc() {
	return empNewDegreeDesc;
    }

    public void setEmpNewDegreeDesc(String empNewDegreeDesc) {
	this.empNewDegreeDesc = empNewDegreeDesc;
    }

    @Basic
    @Column(name = "RAISE_TYPE")
    public Integer getRaiseType() {
	return raiseType;
    }

    public void setRaiseType(Integer raiseType) {
	this.raiseType = raiseType;
	raiseTransaction.setType(raiseType);
    }

    @Basic
    @Column(name = "RAISE_EXECUTION_DATE")
    public Date getRaiseExecutionDate() {
	return raiseExecutionDate;
    }

    public void setRaiseExecutionDate(Date raiseExecutionDate) {
	this.raiseExecutionDate = raiseExecutionDate;
	this.raiseExecutionDateString = HijriDateService.getHijriDateString(raiseExecutionDate);
	raiseTransaction.setExecutionDate(raiseExecutionDate);
    }

    @Transient
    public String getRaiseExecutionDateString() {
	return raiseExecutionDateString;
    }

    public void setRaiseExecutionDateString(String raiseExecutionDateString) {
	this.raiseExecutionDateString = raiseExecutionDateString;
	this.raiseExecutionDate = HijriDateService.getHijriDate(raiseExecutionDateString);
	raiseTransaction.setExecutionDate(raiseExecutionDate);
    }

    @Basic
    @Column(name = "RAISE_DECISION_NUMBER")
    public String getRaiseDecisionNumber() {
	return raiseDecisionNumber;
    }

    public void setRaiseDecisionNumber(String raiseDecisionNumber) {
	this.raiseDecisionNumber = raiseDecisionNumber;
	raiseTransaction.setDecisionNumber(raiseDecisionNumber);
    }

    @Basic
    @Column(name = "RAISE_DECISION_DATE")
    public Date getRaiseDecisionDate() {
	return raiseDecisionDate;
    }

    public void setRaiseDecisionDate(Date raiseDecisionDate) {
	this.raiseDecisionDate = raiseDecisionDate;
	this.raiseDecisionDateString = HijriDateService.getHijriDateString(raiseDecisionDate);
	raiseTransaction.setDecisionDate(raiseDecisionDate);
    }

    @Transient
    public String getRaiseDecisionDateString() {
	return raiseDecisionDateString;
    }

    public void setRaiseDecisionDateString(String raiseDecisionDateString) {
	this.raiseDecisionDateString = raiseDecisionDateString;
	this.raiseDecisionDate = HijriDateService.getHijriDate(raiseDecisionDateString);
	raiseTransaction.setDecisionDate(raiseDecisionDate);
    }

    @Basic
    @Column(name = "RAISE_CATEGORY_ID")
    public Long getRaiseCategoryId() {
	return raiseCategoryId;
    }

    public void setRaiseCategoryId(Long raiseCategoryId) {
	this.raiseCategoryId = raiseCategoryId;
	raiseTransaction.setCategoryId(raiseCategoryId);
    }

    @Basic
    @Column(name = "RAISE_CATEGORY_DESC")
    public String getRaiseCategoryDesc() {
	return raiseCategoryDesc;
    }

    public void setRaiseCategoryDesc(String raiseCategoryDesc) {
	this.raiseCategoryDesc = raiseCategoryDesc;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	raiseTransaction.setRemarks(remarks);
    }

    @Basic
    @Column(name = "EFFECT_FLAG")
    public Integer getEffectFlag() {
	return effectFlag;
    }

    public void setEffectFlag(Integer effectFlag) {
	this.effectFlag = effectFlag;
	raiseTransaction.setEffectFlag(effectFlag);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer geteFlag() {
	return eFlag;
    }

    public void seteFlag(Integer eFlag) {
	this.eFlag = eFlag;
	raiseTransaction.seteFlag(eFlag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
	raiseTransaction.setMigFlag(migFlag);
    }

    @Basic
    @Column(name = "EMP_DECS_APPROVED_ID")
    public Long getEmpDecisionApprovedId() {
	return empDecisionApprovedId;
    }

    public void setEmpDecisionApprovedId(Long empDecisionApprovedId) {
	this.empDecisionApprovedId = empDecisionApprovedId;
	raiseTransaction.setDecisionApprovedId(empDecisionApprovedId);
    }

    @Basic
    @Column(name = "EMP_DECS_APPROVED_NAME")
    public String getEmpDecisionApprovedName() {
	return empDecisionApprovedName;
    }

    public void setEmpDecisionApprovedName(String empDecisionApprovedName) {
	this.empDecisionApprovedName = empDecisionApprovedName;
    }

    @Basic
    @Column(name = "EMP_ORG_DECS_APPROVED_ID")
    public Long getEmpOriginalDecisionApprovedId() {
	return empOriginalDecisionApprovedId;
    }

    public void setEmpOriginalDecisionApprovedId(Long empOriginalDecisionApprovedId) {
	this.empOriginalDecisionApprovedId = empOriginalDecisionApprovedId;
	raiseTransaction.setOriginalDecisionApprovedId(empOriginalDecisionApprovedId);
    }

    @Basic
    @Column(name = "EMP_ORG_DECS_APPROVED_NAME")
    public String getEmpOriginalDecisionApprovedName() {
	return empOriginalDecisionApprovedName;
    }

    public void setEmpOriginalDecisionApprovedName(String empOriginalDecisionApprovedName) {
	this.empOriginalDecisionApprovedName = empOriginalDecisionApprovedName;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_NAME")
    public String getTransEmpJobName() {
	return transEmpJobName;
    }

    public void setTransEmpJobName(String transEmpJobName) {
	this.transEmpJobName = transEmpJobName;
	raiseTransaction.setTransEmpJobName(transEmpJobName);
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_FULL_NAME")
    public String getTransEmpUnitFullName() {
	return transEmpUnitFullName;
    }

    public void setTransEmpUnitFullName(String transEmpUnitFullName) {
	this.transEmpUnitFullName = transEmpUnitFullName;
	raiseTransaction.setTransEmpUnitFullName(transEmpUnitFullName);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_RANK_DESC")
    public String getTransEmpJobRankDesc() {
	return transEmpJobRankDesc;
    }

    public void setTransEmpJobRankDesc(String transEmpJobRankDesc) {
	this.transEmpJobRankDesc = transEmpJobRankDesc;
	raiseTransaction.setTransEmpJobRankDesc(transEmpJobRankDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
	raiseTransaction.setTransEmpRankDesc(transEmpRankDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_DEGREE_DESC")
    public String getTransEmpDegreeDesc() {
	return transEmpDegreeDesc;
    }

    public void setTransEmpDegreeDesc(String transEmpDegreeDesc) {
	this.transEmpDegreeDesc = transEmpDegreeDesc;
	raiseTransaction.setTransEmpDegreeDesc(transEmpDegreeDesc);
    }

    @Transient
    public RaiseTransaction getRaiseTransaction() {
	return raiseTransaction;
    }

    public void setRaiseTransaction(RaiseTransaction raiseTransaction) {
	this.raiseTransaction = raiseTransaction;
    }

}
