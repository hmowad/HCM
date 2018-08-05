package com.code.dal.orm.hcm.terminations;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQueries({

	@NamedQuery(name = "hcm_terminationRecord_searchTerminationRecords",
		query = " select tr from TerminationRecord tr " +
			" where tr.id in (:P_RECORDS_IDS) " +
			" and tr.status =  10 " +
			" order by tr.id ")
})

@Entity
@Table(name = "HCM_STE_RECORDS")
public class TerminationRecord extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private Long categoryId;
    private Long reasonId;
    private Integer recordNumber;
    private Date recordDate;
    private Long status;
    private Long regionId;
    private Integer typeFlag;
    private String internalCopies;
    private String externalCopies;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String attachments;

    @SequenceGenerator(name = "HCMSTERecordSeq",
	    sequenceName = "HCM_STE_RECORD_SEQ")
    @Id
    @GeneratedValue(generator = "HCMSTERecordSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "REASON_ID")
    public Long getReasonId() {
	return reasonId;
    }

    public void setReasonId(Long reasonId) {
	this.reasonId = reasonId;
    }

    @Basic
    @Column(name = "RECORD_NUMBER")
    public Integer getRecordNumber() {
	return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
	this.recordNumber = recordNumber;
    }

    @Basic
    @Column(name = "RECORD_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecordDate() {
	return recordDate;
    }

    public void setRecordDate(Date recordDate) {
	this.recordDate = recordDate;
    }

    @Basic
    @Column(name = "STATUS")
    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "TYPE_FLAG")
    public Integer getTypeFlag() {
	return typeFlag;
    }

    public void setTypeFlag(Integer typeFlag) {
	this.typeFlag = typeFlag;
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
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
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
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
	return "reasonId:" + reasonId + AUDIT_SEPARATOR +
		"recordNumber:" + recordNumber + AUDIT_SEPARATOR +
		"recordDate:" + recordDate + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"typeFlag:" + typeFlag + AUDIT_SEPARATOR +
		"internalCopies:" + internalCopies + AUDIT_SEPARATOR +
		"externalCopies:" + externalCopies + AUDIT_SEPARATOR +
		"attachments:" + attachments + AUDIT_SEPARATOR;
    }

}
