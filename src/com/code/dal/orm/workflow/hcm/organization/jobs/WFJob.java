package com.code.dal.orm.workflow.hcm.organization.jobs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQueries({
	@NamedQuery(name = "wf_job_getRunningJobsRequests",
		query = " select j from WFJob j, WFInstance i " +
			" where j.instanceId = i.id " +
			" and i.status = 1 " +
			" and j.jobId in ( :P_JOBS_IDS ) " +
			" and (:P_EXCLUDED_INSTANCE_ID = -1 or j.instanceId <> :P_EXCLUDED_INSTANCE_ID) ")
})
@Entity
@Table(name = "WF_JOBS")
public class WFJob extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private Long instanceId;
    private Long jobId;
    private Integer jobsCount;
    private Long transactionTypeId;
    private String newName;
    private Long newRankId;
    private Long newUnitId;
    private Long newMinorSpecializationId;
    private Integer newApprovedFlag;
    private Integer newReservationStatus;
    private String referring;
    private String reasons;
    private String remarks;
    private String internalCopies;
    private String externalCopies;
    private String decisionReference;

    @SequenceGenerator(name = "WFOrgSeq",
	    sequenceName = "WF_ORG_SEQ")
    @GeneratedValue(generator = "WFOrgSeq")
    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    @Basic
    @Column(name = "JOBS_COUNT")
    public Integer getJobsCount() {
	return jobsCount;
    }

    public void setJobsCount(Integer jobsCount) {
	this.jobsCount = jobsCount;
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
    @Column(name = "NEW_NAME")
    public String getNewName() {
	return newName;
    }

    public void setNewName(String newName) {
	this.newName = newName;
    }

    @Basic
    @Column(name = "NEW_RANK_ID")
    public Long getNewRankId() {
	return newRankId;
    }

    public void setNewRankId(Long newRankId) {
	this.newRankId = newRankId;
    }

    @Basic
    @Column(name = "NEW_UNIT_ID")
    public Long getNewUnitId() {
	return newUnitId;
    }

    public void setNewUnitId(Long newUnitId) {
	this.newUnitId = newUnitId;
    }

    @Basic
    @Column(name = "NEW_MINOR_SPECIALIZATION_ID")
    public Long getNewMinorSpecializationId() {
	return newMinorSpecializationId;
    }

    public void setNewMinorSpecializationId(Long newMinorSpecializationId) {
	this.newMinorSpecializationId = newMinorSpecializationId;
    }

    @Basic
    @Column(name = "NEW_APPROVED_FLAG")
    public Integer getNewApprovedFlag() {
	return newApprovedFlag;
    }

    public void setNewApprovedFlag(Integer newApprovedFlag) {
	this.newApprovedFlag = newApprovedFlag;
    }

    @Basic
    @Column(name = "NEW_RESERVATION_STATUS")
    public Integer getNewReservationStatus() {
	return newReservationStatus;
    }

    public void setNewReservationStatus(Integer newReservationStatus) {
	this.newReservationStatus = newReservationStatus;
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
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
    @Column(name = "DECISION_REFERENCE")
    public String getDecisionReference() {
	return decisionReference;
    }

    public void setDecisionReference(String decisionReference) {
	this.decisionReference = decisionReference;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "instanceId:" + instanceId + AUDIT_SEPARATOR +
		"jobId:" + jobId + AUDIT_SEPARATOR +
		"jobsCount:" + jobsCount + AUDIT_SEPARATOR +
		"transactionTypeId:" + transactionTypeId + AUDIT_SEPARATOR +
		"newName:" + newName + AUDIT_SEPARATOR +
		"newRankId:" + newRankId + AUDIT_SEPARATOR +
		"newUnitId:" + newUnitId + AUDIT_SEPARATOR +
		"newMinorSpecializationId:" + newMinorSpecializationId + AUDIT_SEPARATOR +
		"newApprovedFlag:" + newApprovedFlag + AUDIT_SEPARATOR +
		"newReservationStatus:" + newReservationStatus + AUDIT_SEPARATOR +
		"referring:" + referring + AUDIT_SEPARATOR +
		"reasons:" + reasons + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR +
		"internalCopies:" + internalCopies + AUDIT_SEPARATOR +
		"externalCopies:" + externalCopies;
    }

}