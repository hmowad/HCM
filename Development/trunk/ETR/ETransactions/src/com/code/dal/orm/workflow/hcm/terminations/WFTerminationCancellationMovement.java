package com.code.dal.orm.workflow.hcm.terminations;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQuery(name = "wf_serviceCancelMvt_getRunningTerminationCancellationMovements",
	query = " select wf " +
		" from WFTerminationCancellationMovement wf, WFInstance i" +
		" where wf.instanceId = i.instanceId " +
		" and i.status = 1 " +
		" and (wf.empId in ( :P_EMPS_IDS ) or wf.jobId in ( :P_JOBS_IDS )) " +
		" and (:P_EXCLUDED_INSTANCE_ID = -1 or i.instanceId <> :P_EXCLUDED_INSTANCE_ID) ")

@SuppressWarnings("serial")
@Entity
@Table(name = "WF_SERVICES_CANCELLATION_MVT")
public class WFTerminationCancellationMovement extends BaseEntity {

    private Long instanceId;
    private Long empId;
    private String reasons;
    private String referring;
    private Long jobId;
    private Long cancelTransactionId;
    private String movedTo;
    private Date movementJoiningDate;
    private String movementJoiningDesc;
    private Date disclaimerDate;
    private Date serviceTerminationDate;
    private String internalCopies;
    private String externalCopies;
    private String remarks;

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
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
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
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    @Basic
    @Column(name = "CANCEL_TRANSACTION_ID")
    public Long getCancelTransactionId() {
	return cancelTransactionId;
    }

    public void setCancelTransactionId(Long cancelTransactionId) {
	this.cancelTransactionId = cancelTransactionId;
    }

    @Basic
    @Column(name = "MOVED_TO")
    public String getMovedTo() {
	return movedTo;
    }

    public void setMovedTo(String movedTo) {
	this.movedTo = movedTo;
    }

    @Basic
    @Column(name = "MOVEMENT_JOINING_DATE")
    public Date getMovementJoiningDate() {
	return movementJoiningDate;
    }

    public void setMovementJoiningDate(Date movementJoiningDate) {
	this.movementJoiningDate = movementJoiningDate;
    }

    @Basic
    @Column(name = "MOVEMENT_JOINING_DESC")
    public String getMovementJoiningDesc() {
	return movementJoiningDesc;
    }

    public void setMovementJoiningDesc(String movementJoiningDesc) {
	this.movementJoiningDesc = movementJoiningDesc;
    }

    @Basic
    @Column(name = "DISCLAIMER_DATE")
    public Date getDisclaimerDate() {
	return disclaimerDate;
    }

    public void setDisclaimerDate(Date disclaimerDate) {
	this.disclaimerDate = disclaimerDate;
    }

    @Basic
    @Column(name = "SERVICE_TERMINATION_DATE")
    public Date getServiceTerminationDate() {
	return serviceTerminationDate;
    }

    public void setServiceTerminationDate(Date serviceTerminationDate) {
	this.serviceTerminationDate = serviceTerminationDate;
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
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }
}
