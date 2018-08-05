package com.code.dal.orm.workflow.hcm.terminations;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQuery(name = "wf_serviceCancelMvt_getWFTerminationCancellationMovementByInstanceId",
	query = " select wf" +
		" from WFTerminationCancellationMovementData wf" +
		" where " +
		" (wf.instanceId = :P_INSTANCE_ID) ")

@SuppressWarnings("serial")
@Entity
@Table(name = "ETR_VW_WF_SERVICE_CANCEL_MVT")
public class WFTerminationCancellationMovementData extends BaseEntity {

    private Long instanceId;
    private Long empId;
    private String empName;
    private Long categoryId;
    private String reasons;
    private String referring;
    private Long jobId;
    private String jobName;
    private String jobCode;
    private Long cancelTransactionId;
    private String movedTo;
    private Date movementJoiningDate;
    private String movementJoiningDateString;
    private String movementJoiningDesc;
    private Date disclaimerDate;
    private String disclaimerDateString;
    private Date serviceTerminationDate;
    private String serviceTerminationDateString;
    private String internalCopies;
    private String externalCopies;
    private String remarks;

    private WFTerminationCancellationMovement wfServicesCancellationMovement;

    public WFTerminationCancellationMovementData() {
	wfServicesCancellationMovement = new WFTerminationCancellationMovement();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	wfServicesCancellationMovement.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	wfServicesCancellationMovement.setEmpId(empId);
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
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	wfServicesCancellationMovement.setReasons(reasons);
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	wfServicesCancellationMovement.setReferring(referring);
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	wfServicesCancellationMovement.setJobId(jobId);
    }

    @Basic
    @Column(name = "EMP_JOB_DESC")
    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
    }

    @Basic
    @Column(name = "EMP_JOB_CODE")
    public String getJobCode() {
	return jobCode;
    }

    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
    }

    @Basic
    @Column(name = "CANCEL_TRANSACTION_ID")
    public Long getCancelTransactionId() {
	return cancelTransactionId;
    }

    public void setCancelTransactionId(Long cancelTransactionId) {
	this.cancelTransactionId = cancelTransactionId;
	wfServicesCancellationMovement.setCancelTransactionId(cancelTransactionId);
    }

    @Basic
    @Column(name = "MOVED_TO")
    public String getMovedTo() {
	return movedTo;
    }

    public void setMovedTo(String movedTo) {
	this.movedTo = movedTo;
	wfServicesCancellationMovement.setMovedTo(movedTo);
    }

    @Basic
    @Column(name = "MOVEMENT_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMovementJoiningDate() {
	return movementJoiningDate;
    }

    public void setMovementJoiningDate(Date movementJoiningDate) {
	this.movementJoiningDate = movementJoiningDate;
	this.movementJoiningDateString = HijriDateService.getHijriDateString(movementJoiningDate);
	wfServicesCancellationMovement.setMovementJoiningDate(movementJoiningDate);
    }

    @Transient
    public String getMovementJoiningDateString() {
	return movementJoiningDateString;
    }

    public void setMovementJoiningDateString(String movementJoiningDateString) {
	this.movementJoiningDateString = movementJoiningDateString;
	this.setMovementJoiningDate(HijriDateService.getHijriDate(movementJoiningDateString));
    }

    @Basic
    @Column(name = "MOVEMENT_JOINING_DESC")
    public String getMovementJoiningDesc() {
	return movementJoiningDesc;
    }

    public void setMovementJoiningDesc(String movementJoiningDesc) {
	this.movementJoiningDesc = movementJoiningDesc;
	wfServicesCancellationMovement.setMovementJoiningDesc(movementJoiningDesc);
    }

    @Basic
    @Column(name = "DISCLAIMER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDisclaimerDate() {
	return disclaimerDate;
    }

    public void setDisclaimerDate(Date disclaimerDate) {
	this.disclaimerDate = disclaimerDate;
	this.disclaimerDateString = HijriDateService.getHijriDateString(disclaimerDate);
	wfServicesCancellationMovement.setDisclaimerDate(disclaimerDate);
    }

    @Transient
    public String getDisclaimerDateString() {
	return disclaimerDateString;
    }

    public void setDisclaimerDateString(String disclaimerDateString) {
	this.disclaimerDateString = disclaimerDateString;
	this.setDisclaimerDate(HijriDateService.getHijriDate(disclaimerDateString));
    }

    @Basic
    @Column(name = "SERVICE_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getServiceTerminationDate() {
	return serviceTerminationDate;
    }

    public void setServiceTerminationDate(Date serviceTerminationDate) {
	this.serviceTerminationDate = serviceTerminationDate;
	this.serviceTerminationDateString = HijriDateService.getHijriDateString(serviceTerminationDate);
	wfServicesCancellationMovement.setServiceTerminationDate(serviceTerminationDate);
    }

    @Transient
    public String getServiceTerminationDateString() {
	return serviceTerminationDateString;
    }

    public void setServiceTerminationDateString(String serviceTerminationDateString) {
	this.serviceTerminationDateString = serviceTerminationDateString;
	this.setServiceTerminationDate(HijriDateService.getHijriDate(serviceTerminationDateString));
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	wfServicesCancellationMovement.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	wfServicesCancellationMovement.setExternalCopies(externalCopies);
    }

    @Transient
    public WFTerminationCancellationMovement getWfServicesCancellationMovement() {
	return wfServicesCancellationMovement;
    }

    public void setWfServicesCancellationMovement(WFTerminationCancellationMovement wfServicesCancellationMovement) {
	this.wfServicesCancellationMovement = wfServicesCancellationMovement;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	wfServicesCancellationMovement.setRemarks(remarks);
    }

}
