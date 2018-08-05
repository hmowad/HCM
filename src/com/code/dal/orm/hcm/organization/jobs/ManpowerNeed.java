package com.code.dal.orm.hcm.organization.jobs;

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

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>ManpowerNeed</code> class represents the manpower needs data.
 * 
 */
@Entity
@Table(name = "HCM_ORG_MANPOWER_NEEDS")
public class ManpowerNeed extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private Long requestingUnitId;
    private Date transactionDate;
    private String transactionDateString;
    private Long categoryId;
    private Integer needType;
    private Integer status;
    private Long studyId;
    private Long approvedId;
    private Long decisionApprovedId;
    private String attachments;

    @SequenceGenerator(name = "HCMOrgManpowerNeedSeq",
	    sequenceName = "HCM_ORG_MANPOWER_NEEDS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMOrgManpowerNeedSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "REQUESTING_UNIT_ID")
    public Long getRequestingUnitId() {
	return requestingUnitId;
    }

    public void setRequestingUnitId(Long requestingUnitId) {
	this.requestingUnitId = requestingUnitId;
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
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "NEED_TYPE")
    public Integer getNeedType() {
	return needType;
    }

    public void setNeedType(Integer needType) {
	this.needType = needType;
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
    @Column(name = "STUDY_ID")
    public Long getStudyId() {
	return studyId;
    }

    public void setStudyId(Long studyId) {
	this.studyId = studyId;
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
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "requestingUnitId:" + requestingUnitId + AUDIT_SEPARATOR +
		"transactionDate:" + transactionDate + AUDIT_SEPARATOR +
		"categoryId:" + categoryId + AUDIT_SEPARATOR +
		"needType:" + needType + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"studyId:" + studyId + AUDIT_SEPARATOR +
		"approvedId:" + approvedId + AUDIT_SEPARATOR +
		"decisionApprovedId:" + decisionApprovedId + AUDIT_SEPARATOR +
		"attachments:" + attachments;
    }

}
