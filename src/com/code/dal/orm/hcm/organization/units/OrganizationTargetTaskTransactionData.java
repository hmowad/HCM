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
 * The <code>OrganizationTargetTaskTransactionData</code> class represents the targets and tasks transactions history in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_organizationTargetTaskTransactionData_getOrganizationTargetTaskTransactionsByUnitId",
		query = "select t from OrganizationTargetTaskTransactionData t " +
			" where t.unitId = :P_UNIT_ID " +
			" order by t.transactionDate desc, t.targetTaskFlag desc, t.id desc"
	)
})
@Entity
@Table(name = "HCM_VW_ORG_TARGETS_TASKS_TRANS")
public class OrganizationTargetTaskTransactionData extends BaseEntity {

    private Long id;
    private Long unitId;
    private Long organizationTargetTaskId;
    private Long organizationTargetTaskDetailId;
    private Integer targetTaskFlag;
    private String description;
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
    @Column(name = "TARGET_TASK_ID")
    public Long getOrganizationTargetTaskId() {
	return organizationTargetTaskId;
    }

    public void setOrganizationTargetTaskId(Long organizationTargetTaskId) {
	this.organizationTargetTaskId = organizationTargetTaskId;
    }

    @Basic
    @Column(name = "TARGET_TASK_DETAIL_ID")
    public Long getOrganizationTargetTaskDetailId() {
	return organizationTargetTaskDetailId;
    }

    public void setOrganizationTargetTaskDetailId(Long organizationTargetTaskDetailId) {
	this.organizationTargetTaskDetailId = organizationTargetTaskDetailId;
    }

    @Basic
    @Column(name = "TARGET_TASK_FLAG")
    public Integer getTargetTaskFlag() {
	return targetTaskFlag;
    }

    public void setTargetTaskFlag(Integer targetTaskFlag) {
	this.targetTaskFlag = targetTaskFlag;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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
	this.decisionDate = HijriDateService.getHijriDate(this.decisionDateString);
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
