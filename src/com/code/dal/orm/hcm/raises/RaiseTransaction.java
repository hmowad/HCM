package com.code.dal.orm.hcm.raises;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQueries({

	@NamedQuery(name = "hcm_raiseTransaction_getAllRaisesForEmployeesAfterGivenTime",
		query = "select rt from RaiseTransaction rt where executionDate > to_date(:P_PROMOTION_DATE, 'MI/MM/YYYY') and rt.empId in (:P_EMP_IDS) and rt.deservedFlag = 1" +
			" order by empId, executionDate"),

	@NamedQuery(name = "hcm_raiseTransaction_getEmployeesDegreeAtGivenTime",
		query = "select a from RaiseTransaction a" +
			" where a.executionDate > to_date(:P_PROMOTION_DATE, 'MI/MM/YYYY')" +
			" and a.executionDate = (select min(b.executionDate) from RaiseTransaction b where b.executionDate > to_date(:P_PROMOTION_DATE, 'MI/MM/YYYY') and a.empId = b.empId)" +
			" and a.empId in (:P_EMP_IDS)" +
			" order by a.empId")

})

@Entity
@Table(name = "HCM_RAISE_TRANSACTIONS")
public class RaiseTransaction extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {
    private Long id;
    private Long empId;
    private Long categoryId;
    private Integer type;
    private Integer deservedFlag;
    private String exclusionReason;
    private Long newDegreeId;
    private Date decisionDate;
    private String decisionNumber;
    private String basedOnDecisionNumber;
    private Date basedOnDecisionDate;
    private Date executionDate;
    private String remarks;
    private Integer effectFlag;
    private Integer eFlag;
    private Integer migFlag;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Integer status;
    private String transEmpJobName;
    private String transEmpUnitFullName;
    private String transEmpJobRankDesc;
    private String transEmpRankDesc;
    private String transEmpDegreeDesc;
    private Long transEmpDegreeId;

    @GenericGenerator(name = "HCMRaiseSeq",
	    strategy = "enhanced-sequence",
	    parameters = {
		    @org.hibernate.annotations.Parameter(
			    name = "sequence_name",
			    value = "HCM_RAISE_SEQ"),
		    @org.hibernate.annotations.Parameter(
			    name = "optimizer",
			    value = "pooled-lo"),
		    @org.hibernate.annotations.Parameter(
			    name = "increment_size",
			    value = "30") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "HCMRaiseSeq")
    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "TYPE")
    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
    }

    @Basic
    @Column(name = "DESERVED_FLAG")
    public Integer getDeservedFlag() {
	return deservedFlag;
    }

    public void setDeservedFlag(Integer deservedFlag) {
	this.deservedFlag = deservedFlag;
    }

    @Basic
    @Column(name = "EXCLUSION_REASON")
    public String getExclusionReason() {
	return exclusionReason;
    }

    public void setExclusionReason(String exclusionReason) {
	this.exclusionReason = exclusionReason;
    }

    @Basic
    @Column(name = "NEW_DEGREE_ID")
    public Long getNewDegreeId() {
	return newDegreeId;
    }

    public void setNewDegreeId(Long newDegreeId) {
	this.newDegreeId = newDegreeId;
    }

    @Basic
    @Column(name = "EXECUTION_DATE")
    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
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
    @Column(name = "EFFECT_FLAG")
    public Integer getEffectFlag() {
	return effectFlag;
    }

    public void setEffectFlag(Integer effectFlag) {
	this.effectFlag = effectFlag;
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
    @Column(name = "TRANS_EMP_JOB_NAME")
    public String getTransEmpJobName() {
	return transEmpJobName;
    }

    public void setTransEmpJobName(String transEmpJobName) {
	this.transEmpJobName = transEmpJobName;
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
    @Column(name = "TRANS_EMP_JOB_RANK_DESC")
    public String getTransEmpJobRankDesc() {
	return transEmpJobRankDesc;
    }

    public void setTransEmpJobRankDesc(String transEmpJobRankDesc) {
	this.transEmpJobRankDesc = transEmpJobRankDesc;
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
    @Column(name = "TRANS_EMP_DEGREE_DESC")
    public String getTransEmpDegreeDesc() {
	return transEmpDegreeDesc;
    }

    public void setTransEmpDegreeDesc(String transEmpDegreeDesc) {
	this.transEmpDegreeDesc = transEmpDegreeDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_DEGREE_ID")
    public Long getTransEmpDegreeId() {
	return transEmpDegreeId;
    }

    public void setTransEmpDegreeId(Long transEmpDegreeId) {
	this.transEmpDegreeId = transEmpDegreeId;
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
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "BASED_ON_DECISION_DATE")
    public Date getBasedOnDecisionDate() {
	return basedOnDecisionDate;
    }

    public void setBasedOnDecisionDate(Date basedOnDecisionDate) {
	this.basedOnDecisionDate = basedOnDecisionDate;
    }

    @Basic
    @Column(name = "BASED_ON_DECISION_NUMBER")
    public String getBasedOnDecisionNumber() {
	return basedOnDecisionNumber;
    }

    public void setBasedOnDecisionNumber(String basedOnDecisionNumber) {
	this.basedOnDecisionNumber = basedOnDecisionNumber;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"categoryId:" + categoryId + AUDIT_SEPARATOR +
		"type:" + type + AUDIT_SEPARATOR +
		"exclusionReason:" + exclusionReason + AUDIT_SEPARATOR +
		"decisionDate:" + decisionDate + AUDIT_SEPARATOR +
		"basedOnDecisionDate:" + basedOnDecisionDate + AUDIT_SEPARATOR +
		"newDegreeId:" + newDegreeId + AUDIT_SEPARATOR +
		"deservedFlag:" + deservedFlag + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"basedOnDecisionNumber:" + basedOnDecisionNumber + AUDIT_SEPARATOR +
		"executionDate:" + executionDate + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR +
		"effectFlag:" + effectFlag + AUDIT_SEPARATOR +
		"eFlag:" + eFlag + AUDIT_SEPARATOR +
		"migFlag:" + migFlag + AUDIT_SEPARATOR +
		"decisionApprovedId:" + decisionApprovedId + AUDIT_SEPARATOR +
		"originalDecisionApprovedId:" + originalDecisionApprovedId + AUDIT_SEPARATOR +
		"transEmpJobName:" + transEmpJobName + AUDIT_SEPARATOR +
		"transEmpJobRankDesc:" + transEmpJobRankDesc + AUDIT_SEPARATOR +
		"transEmpRankDesc:" + transEmpRankDesc + AUDIT_SEPARATOR +
		"transEmpDegreeDesc:" + transEmpDegreeDesc + AUDIT_SEPARATOR +
		"transEmpUnitFullName:" + transEmpUnitFullName + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR;
    }

}
