package com.code.dal.orm.workflow.hcm.organization.jobs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_jobData_getWFJobsByInstanceId",
		query = " select j from WFJobData j " +
			" where (:P_INSTANCE_ID = -1 or j.instanceId = :P_INSTANCE_ID) " +
			" order by j.id ")

})
@Entity
@Table(name = "ETR_VW_WF_JOBS")
public class WFJobData extends BaseEntity {

    private Long id;
    private Long instanceId;
    private Long jobId;
    private String code;
    private Long basicJobNameId;
    private String name;
    private Long rankId;
    private String rankDesc;
    private Long minorSpecializationId;
    private String minorSpecializationDesc;
    private Long unitId;
    private String unitFullName;
    private Integer approvedFlag;

    private Integer jobsCount;
    private Long transactionTypeId;
    private Long newBasicJobNameId;
    private String newName;
    private Long newUnitId;
    private String newUnitFullName;
    private Long newRegionId;
    private Long newRankId;
    private String newRankDesc;
    private Long newMinorSpecializationId;
    private String newMinorSpecializationDesc;
    private Integer newGeneralSpecialization;
    private Integer newGeneralType;
    private Integer newApprovedFlag;
    private Integer newReservationStatus;
    private String referring;
    private String reasons;
    private String remarks;
    private String internalCopies;
    private String externalCopies;
    private String decisionReference;

    private WFJob wfJob;

    public WFJobData() {
	wfJob = new WFJob();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.wfJob.setId(id);
    }

    @Basic
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	this.wfJob.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	this.wfJob.setJobId(jobId);
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME_ID")
    public Long getBasicJobNameId() {
	return basicJobNameId;
    }

    public void setBasicJobNameId(Long basicJobNameId) {
	this.basicJobNameId = basicJobNameId;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    @Basic
    @Column(name = "RANK_DESC")
    public String getRankDesc() {
	return rankDesc;
    }

    public void setRankDesc(String rankDesc) {
	this.rankDesc = rankDesc;
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_DESC")
    public String getMinorSpecializationDesc() {
	return minorSpecializationDesc;
    }

    public void setMinorSpecializationDesc(String minorSpecDesc) {
	this.minorSpecializationDesc = minorSpecDesc;
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
    @Column(name = "UNIT_FULL_NAME")
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    @Basic
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
    }

    @Basic
    @Column(name = "JOBS_COUNT")
    public Integer getJobsCount() {
	return jobsCount;
    }

    public void setJobsCount(Integer jobsCount) {
	this.jobsCount = jobsCount;
	this.wfJob.setJobsCount(jobsCount);
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
	this.wfJob.setTransactionTypeId(transactionTypeId);
    }

    @Basic
    @Column(name = "NEW_BASIC_JOB_NAME_ID")
    public Long getNewBasicJobNameId() {
	return newBasicJobNameId;
    }

    public void setNewBasicJobNameId(Long newBasicJobNameId) {
	this.newBasicJobNameId = newBasicJobNameId;
	this.wfJob.setNewBasicJobNameId(newBasicJobNameId);
    }

    @Basic
    @Column(name = "NEW_NAME")
    public String getNewName() {
	return newName;
    }

    public void setNewName(String newName) {
	this.newName = newName;
	this.wfJob.setNewName(newName);
    }

    @Basic
    @Column(name = "NEW_UNIT_ID")
    public Long getNewUnitId() {
	return newUnitId;
    }

    public void setNewUnitId(Long newUnitId) {
	this.newUnitId = newUnitId;
	this.wfJob.setNewUnitId(newUnitId);
    }

    @Basic
    @Column(name = "NEW_UNIT_FULL_NAME")
    public String getNewUnitFullName() {
	return newUnitFullName;
    }

    public void setNewUnitFullName(String newUnitFullName) {
	this.newUnitFullName = newUnitFullName;
    }

    @Basic
    @Column(name = "NEW_REGION_ID")
    public Long getNewRegionId() {
	return newRegionId;
    }

    public void setNewRegionId(Long newRegionId) {
	this.newRegionId = newRegionId;
    }

    @Basic
    @Column(name = "NEW_RANK_ID")
    public Long getNewRankId() {
	return newRankId;
    }

    public void setNewRankId(Long newRankId) {
	this.newRankId = newRankId;
	this.wfJob.setNewRankId(newRankId);
    }

    @Basic
    @Column(name = "NEW_RANK_DESC")
    public String getNewRankDesc() {
	return newRankDesc;
    }

    public void setNewRankDesc(String newRankDesc) {
	this.newRankDesc = newRankDesc;
    }

    @Basic
    @Column(name = "NEW_MINOR_SPECIALIZATION_ID")
    public Long getNewMinorSpecializationId() {
	return newMinorSpecializationId;
    }

    public void setNewMinorSpecializationId(Long newMinorSpecializationId) {
	this.newMinorSpecializationId = newMinorSpecializationId;
	this.wfJob.setNewMinorSpecializationId(newMinorSpecializationId);
    }

    @Basic
    @Column(name = "NEW_MINOR_SPECIALIZATION_DESC")
    public String getNewMinorSpecializationDesc() {
	return newMinorSpecializationDesc;
    }

    public void setNewMinorSpecializationDesc(String newMinorSpecializationDesc) {
	this.newMinorSpecializationDesc = newMinorSpecializationDesc;
    }

    @Basic
    @Column(name = "NEW_GENERAL_SPEC")
    public Integer getNewGeneralSpecialization() {
	return newGeneralSpecialization;
    }

    public void setNewGeneralSpecialization(Integer newGeneralSpecialization) {
	this.newGeneralSpecialization = newGeneralSpecialization;
    }

    @Basic
    @Column(name = "NEW_GENERAL_TYPE")
    public Integer getNewGeneralType() {
	return newGeneralType;
    }

    public void setNewGeneralType(Integer newGeneralType) {
	this.newGeneralType = newGeneralType;
    }

    @Basic
    @Column(name = "NEW_APPROVED_FLAG")
    public Integer getNewApprovedFlag() {
	return newApprovedFlag;
    }

    public void setNewApprovedFlag(Integer newApprovedFlag) {
	this.newApprovedFlag = newApprovedFlag;
	this.wfJob.setNewApprovedFlag(newApprovedFlag);
    }

    @Basic
    @Column(name = "NEW_RESERVATION_STATUS")
    public Integer getNewReservationStatus() {
	return newReservationStatus;
    }

    public void setNewReservationStatus(Integer newReservationStatus) {
	this.newReservationStatus = newReservationStatus;
	this.wfJob.setNewReservationStatus(newReservationStatus);
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	this.wfJob.setReferring(referring);
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	this.wfJob.setReasons(reasons);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	this.wfJob.setRemarks(remarks);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	this.wfJob.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	this.wfJob.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "DECISION_REFERENCE")
    public String getDecisionReference() {
	return decisionReference;
    }

    public void setDecisionReference(String decisionReference) {
	this.decisionReference = decisionReference;
	this.wfJob.setDecisionReference(decisionReference);
    }

    @Transient
    public WFJob getWfJob() {
	return wfJob;
    }

    public void setWfJob(WFJob wfJob) {
	this.wfJob = wfJob;
    }

}
