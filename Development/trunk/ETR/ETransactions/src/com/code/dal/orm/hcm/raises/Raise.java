package com.code.dal.orm.hcm.raises;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_RAISES")
public class Raise extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Date decisionDate;
    private String decisionNumber;
    private Date executionDate;
    private Integer type;
    private Integer status;
    private Long categoryId;
    private String remarks;

    @SequenceGenerator(name = "HCMRaiseSeq",
	    sequenceName = "HCM_RAISE_SEQ")
    @Id
    @GeneratedValue(generator = "HCMRaiseSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "EXECUTION_DATE")
    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
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
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
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
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "decisionDate:" + decisionDate + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"executionDate:" + executionDate + AUDIT_SEPARATOR +
		"type:" + type + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"categoryId:" + categoryId + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR;
    }

}