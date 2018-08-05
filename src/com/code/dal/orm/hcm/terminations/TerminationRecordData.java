package com.code.dal.orm.hcm.terminations;

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
	@NamedQuery(name = "hcm_terminationRecordData_searchTerminationRecordData",
		query = " select tr from TerminationRecordData tr " +
			" where (:P_RECORD_NUMBER = '-1' or tr.recordNumber = :P_RECORD_NUMBER) " +
			" and (:P_EMP_ID = -1 or tr.id in (select trd.recordId from TerminationRecordDetailData trd where trd.recordId = tr.id and trd.empId = :P_EMP_ID)) " +
			" and (:P_RECORD_DATE_FROM_FLAG = -1 or to_date(:P_RECORD_DATE_FROM, 'MI/MM/YYYY') <= tr.recordDate) " +
			" and (:P_RECORD_DATE_TO_FLAG = -1 or to_date(:P_RECORD_DATE_TO, 'MI/MM/YYYY') >= tr.recordDate) " +
			" and (:P_REASON_ID = -1 or tr.reasonId = :P_REASON_ID) " +
			" and (:P_RANK_ID = -1 or tr.id in (select trd.recordId from TerminationRecordDetailData trd where trd.recordId = tr.id and trd.empRankId = :P_RANK_ID)) " +
			" and (:P_CATEGORY_ID = -1 or tr.categoryId = :P_CATEGORY_ID) " +
			" and (:P_STATUS = -1 or  tr.status = :P_STATUS) " +
			" and (:P_REGION_ID = -1 or  tr.regionId = :P_REGION_ID) " +
			" and (:P_TERMINATION_DUE_DATE_FROM_FLAG = -1 or tr.id in (select trd.recordId from TerminationRecordDetailData trd where trd.recordId = tr.id and to_date(:P_TERMINATION_DUE_DATE_FROM, 'MI/MM/YYYY') <= trd.empTerminationDueDate)) " +
			" and (:P_TERMINATION_DUE_DATE_TO_FLAG = -1 or tr.id in (select trd.recordId from TerminationRecordDetailData trd where trd.recordId = tr.id and to_date(:P_TERMINATION_DUE_DATE_TO, 'MI/MM/YYYY') >= trd.empTerminationDueDate)) " +
			" order by tr.recordDate desc, tr.recordNumber desc "),

	@NamedQuery(name = "hcm_terminationRecordData_searchTerminationRecordDataById",
		query = " select tr from TerminationRecordData tr " +
			" where (tr.id = :P_ID) "),

	@NamedQuery(name = "hcm_terminationRecordData_getMaxRecordNumber",
		query = " select nvl(max(tr.recordNumber),0) from TerminationRecordData tr " +
			" where (tr.categoryId in (:P_CATEGORY_ID)) " +
			" and (to_date(:P_YEAR_FROM, 'MI/MM/YYYY') <= tr.recordDate) " +
			" and (to_date(:P_YEAR_TO, 'MI/MM/YYYY') > tr.recordDate) "),

})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_STE_RECORDS")
public class TerminationRecordData extends BaseEntity {

    private Long id;
    private Long categoryId;
    private Long reasonId;
    private String reasonDesc;
    private Integer recordNumber;
    private Date recordDate;
    private String recordDateString;
    private Long status;
    private Long regionId;
    private Integer typeFlag;
    private String internalCopies;
    private String externalCopies;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String attachments;

    private TerminationRecord terminationRecord;

    public TerminationRecordData() {
	terminationRecord = new TerminationRecord();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	terminationRecord.setId(id);
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	terminationRecord.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "REASON_ID")
    public Long getReasonId() {
	return reasonId;
    }

    public void setReasonId(Long reasonId) {
	this.reasonId = reasonId;
	terminationRecord.setReasonId(reasonId);
    }

    @Basic
    @Column(name = "REASON_DESC")
    public String getReasonDesc() {
	return reasonDesc;
    }

    public void setReasonDesc(String reasonDesc) {
	this.reasonDesc = reasonDesc;
    }

    @Basic
    @Column(name = "RECORD_NUMBER")
    public Integer getRecordNumber() {
	return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
	this.recordNumber = recordNumber;
	terminationRecord.setRecordNumber(recordNumber);
    }

    @Basic
    @Column(name = "RECORD_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecordDate() {
	return recordDate;
    }

    public void setRecordDate(Date recordDate) {
	this.recordDate = recordDate;
	this.recordDateString = HijriDateService.getHijriDateString(recordDate);
	terminationRecord.setRecordDate(recordDate);
    }

    @Transient
    public String getRecordDateString() {
	return recordDateString;
    }

    public void setRecordDateString(String recordDateString) {
	this.recordDateString = recordDateString;
	this.setRecordDate(HijriDateService.getHijriDate(recordDateString));
    }

    @Basic
    @Column(name = "STATUS")
    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
	terminationRecord.setStatus(status);
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	terminationRecord.setRegionId(regionId);
    }

    @Basic
    @Column(name = "TYPE_FLAG")
    public Integer getTypeFlag() {
	return typeFlag;
    }

    public void setTypeFlag(Integer typeFlag) {
	this.typeFlag = typeFlag;
	terminationRecord.setTypeFlag(typeFlag);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	terminationRecord.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	terminationRecord.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "APPROVED_ID")
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
	terminationRecord.setApprovedId(approvedId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	terminationRecord.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	terminationRecord.setOriginalDecisionApprovedId(originalDecisionApprovedId);

    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	terminationRecord.setAttachments(attachments);

    }

    @Transient
    public TerminationRecord getTerminationRecord() {
	return terminationRecord;
    }

    public void setTerminationRecord(TerminationRecord terminationRecord) {
	this.terminationRecord = terminationRecord;
    }

}
