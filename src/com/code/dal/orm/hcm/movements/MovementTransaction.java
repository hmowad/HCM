package com.code.dal.orm.hcm.movements;

import java.io.Serializable;
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
import javax.persistence.Transient;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_movementTransaction_getFutureEffectMoveTransactions",
		query = " select m from MovementTransaction m, TransactionType t where " +
			" (m.employeeId in ( :P_EMPS_IDS ) or (m.jobId in ( :P_JOBS_IDS ) and m.movementTypeId = 1) or m.freezeJobId in ( :P_JOBS_IDS ))" +
			" and m.effectFlag = 0 " +
			" and m.transactionTypeId = t.id " +
			" and t.code <> 4 " +
			" and (m.successorDecisionEffectFlag is null or m.successorDecisionEffectFlag <> 4 )" +
			" and (:P_MOVEMENT_TYPE_ID = -1 or m.movementTypeId = :P_MOVEMENT_TYPE_ID )"),
	@NamedQuery(name = "hcm_movementTransaction_getLastMoveTransactionForWishes",
		query = " select m from MovementTransaction m where " +
			" m.employeeId = :P_EMPLOYEE_ID " +
			" and m.movementTypeId = 1 " +
			" and m.effectFlag = 1 " +
			" and m.locationFlag = 0 " +
			" and m.unitFullName like :P_OFFICIAL_REGION_SHORT_DESC " +
			" and m.transEmpUnitFullName not like :P_OFFICIAL_REGION_SHORT_DESC " +
			" order by executionDate desc"),
	@NamedQuery(name = "hcm_movementTransaction_getExternalMovementTransactions",
		query = " select m from MovementTransaction m where " +
			" m.employeeId = :P_EMPLOYEE_ID " +
			" and (to_date(:P_EXECUTION_DATE_FROM, 'MI/MM/YYYY') <= m.executionDate) " +
			" and (to_date(:P_EXECUTION_DATE_TO, 'MI/MM/YYYY') >= m.executionDate) " +
			" and m.movementTypeId = 1 " +
			" and m.effectFlag = 1 " +
			" and m.locationFlag = 1 " +
			" order by executionDate asc")
})
@Entity
@Table(name = "HCM_MVT_TRANSACTIONS")
public class MovementTransaction extends AuditEntity implements Serializable, UpdatableAuditEntity {
    private Long id;
    private Long movementTypeId;
    private Long transactionTypeId;
    private Long employeeId;
    private Long categoryId;
    private Long unitId;
    private String unitFullName;
    private Long jobId;
    private String jobCode;
    private String jobName;
    private Long jobRankId;
    private Long jobMinorSpecId;
    private String jobMinorSpecDesc;
    private Long jobClassificationId;
    private Long freezeJobId;
    private String freezeJobCode;
    private String freezeJobName;
    private Long freezeJobRankId;
    private String freezeJobUnitFullName;
    private Integer managerFlag;
    private Integer executionDateFlag;
    private Date executionDate;
    private String executionDateString;
    private Date endDate;
    private String endDateString;
    private Date joiningDate;
    private String joiningDateString;
    private Date returnJoiningDate;
    private String returnJoiningDateString;
    private Integer periodDays;
    private Integer periodMonths;
    private Integer locationFlag;
    private String location;
    private String reasons;
    private Integer reasonType;
    private Integer vacationGrantFlag;
    private String ministryApprovalNumber;
    private Date ministryApprovalDate;
    private String ministryApprovalDateString;
    private String remarks;
    private String specialRemarks;
    private String specialAttachments;
    private Long replacementTransId;
    private String InternalCopies;
    private String externalCopies;
    private String attachments;
    private String directedToJobName;
    private String basedOn;
    private String basedOnOrderNumber;
    private Date basedOnOrderDate;
    private String basedOnOrderDateString;
    private Integer etrFlag;
    private Integer migrationFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long approvedId;
    private Long originalDecisionApprovedId;
    private Long decisionApprovedId;
    private Long decisionRegionId;
    private Long transEmpRankId;
    private String transEmpRankDesc;
    private String transEmpJobMinorSpecDesc;
    private String transEmpJobCode;
    private String transEmpJobClassJobCode;
    private String transEmpJobClassJobDesc;
    private String transEmpJobName;
    private String transEmpJobRankDesc;
    private String transEmpUnitFullName;
    private String transApprovedRankDesc;
    private String transApprovedJobName;
    private String basedOnDecisionNumber;
    private Date basedOnDecisionDate;
    private String basedOnDecisionDateString;
    private Integer successorDecisionEffectFlag;
    private Integer effectFlag;
    private Integer securityUnitFlag;
    private Integer verbalOrderFlag;
    private Integer requestTransactionFlag;
    private Integer sequentialMvtFlag;
    private Integer transferAllowanceFlag;
    private String transJoiningApprovedJobName;

    @SequenceGenerator(name = "HCMMvtSeq",
	    sequenceName = "HCM_MVT_SEQ")
    @Id
    @GeneratedValue(generator = "HCMMvtSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "MOVEMENT_TYPE_ID")
    public Long getMovementTypeId() {
	return movementTypeId;
    }

    public void setMovementTypeId(Long movementTypeId) {
	this.movementTypeId = movementTypeId;
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
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
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
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    @Basic
    @Column(name = "JOB_CODE")
    public String getJobCode() {
	return jobCode;
    }

    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
    }

    @Basic
    @Column(name = "JOB_NAME")
    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
    }

    @Basic
    @Column(name = "JOB_RANK_ID")
    public Long getJobRankId() {
	return jobRankId;
    }

    public void setJobRankId(Long jobRankId) {
	this.jobRankId = jobRankId;
    }

    @Basic
    @Column(name = "JOB_MINOR_SPEC_ID")
    public Long getJobMinorSpecId() {
	return jobMinorSpecId;
    }

    public void setJobMinorSpecId(Long jobMinorSpecId) {
	this.jobMinorSpecId = jobMinorSpecId;
    }

    @Basic
    @Column(name = "JOB_MINOR_SPEC_DESC")
    public String getJobMinorSpecDesc() {
	return jobMinorSpecDesc;
    }

    public void setJobMinorSpecDesc(String jobMinorSpecDesc) {
	this.jobMinorSpecDesc = jobMinorSpecDesc;
    }

    @Basic
    @Column(name = "JOB_CLASSIFICATION_ID")
    public Long getJobClassificationId() {
	return jobClassificationId;
    }

    public void setJobClassificationId(Long jobClassificationId) {
	this.jobClassificationId = jobClassificationId;
    }

    @Basic
    @Column(name = "FREEZE_JOB_ID")
    public Long getFreezeJobId() {
	return freezeJobId;
    }

    public void setFreezeJobId(Long freezeJobId) {
	this.freezeJobId = freezeJobId;
    }

    @Basic
    @Column(name = "FREEZE_JOB_CODE")
    public String getFreezeJobCode() {
	return freezeJobCode;
    }

    public void setFreezeJobCode(String freezeJobCode) {
	this.freezeJobCode = freezeJobCode;
    }

    @Basic
    @Column(name = "FREEZE_JOB_NAME")
    public String getFreezeJobName() {
	return freezeJobName;
    }

    public void setFreezeJobName(String freezeJobName) {
	this.freezeJobName = freezeJobName;
    }

    @Basic
    @Column(name = "FREEZE_JOB_RANK_ID")
    public Long getFreezeJobRankId() {
	return freezeJobRankId;
    }

    public void setFreezeJobRankId(Long freezeJobRankId) {
	this.freezeJobRankId = freezeJobRankId;
    }

    @Basic
    @Column(name = "FREEZE_JOB_UNIT_FULL_NAME")
    public String getFreezeJobUnitFullName() {
	return freezeJobUnitFullName;
    }

    public void setFreezeJobUnitFullName(String freezeJobUnitFullName) {
	this.freezeJobUnitFullName = freezeJobUnitFullName;
    }

    @Basic
    @Column(name = "MANAGER_FLAG")
    public Integer getManagerFlag() {
	return managerFlag;
    }

    public void setManagerFlag(Integer managerFlag) {
	this.managerFlag = managerFlag;
    }

    @Basic
    @Column(name = "EXECUTIN_DATE_FLAG")
    public Integer getExecutionDateFlag() {
	return executionDateFlag;
    }

    public void setExecutionDateFlag(Integer executionDateFlag) {
	this.executionDateFlag = executionDateFlag;
    }

    @Basic
    @Column(name = "EXECUTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
	this.executionDateString = HijriDateService.getHijriDateString(executionDate);
    }

    @Transient
    public String getExecutionDateString() {
	return executionDateString;
    }

    public void setExecutionDateString(String executionDateString) {
	this.executionDateString = executionDateString;
	this.executionDate = HijriDateService.getHijriDate(executionDateString);
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
	this.joiningDateString = HijriDateService.getHijriDateString(joiningDate);
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
    }

    @Basic
    @Column(name = "RETURN_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getReturnJoiningDate() {
	return returnJoiningDate;
    }

    public void setReturnJoiningDate(Date returnJoiningDate) {
	this.returnJoiningDate = returnJoiningDate;
	this.returnJoiningDateString = HijriDateService.getHijriDateString(returnJoiningDate);
    }

    @Transient
    public String getReturnJoiningDateString() {
	return returnJoiningDateString;
    }

    public void setReturnJoiningDateString(String returnJoiningDateString) {
	this.returnJoiningDateString = returnJoiningDateString;
	this.returnJoiningDate = HijriDateService.getHijriDate(returnJoiningDateString);
    }

    @Basic
    @Column(name = "PERIOD_DAYS")
    public Integer getPeriodDays() {
	return periodDays;
    }

    public void setPeriodDays(Integer periodDays) {
	this.periodDays = periodDays;
    }

    @Basic
    @Column(name = "PERIOD_MONTHS")
    public Integer getPeriodMonths() {
	return periodMonths;
    }

    public void setPeriodMonths(Integer periodMonths) {
	this.periodMonths = periodMonths;
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
    }

    @Basic
    @Column(name = "LOCATION")
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
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
    @Column(name = "REASON_TYPE")
    public Integer getReasonType() {
	return reasonType;
    }

    public void setReasonType(Integer reasonType) {
	this.reasonType = reasonType;
    }

    @Basic
    @Column(name = "VACATION_GRANT_FLAG")
    public Integer getVacationGrantFlag() {
	return vacationGrantFlag;
    }

    public void setVacationGrantFlag(Integer vacationGrantFlag) {
	this.vacationGrantFlag = vacationGrantFlag;
    }

    @Basic
    @Column(name = "MINISTRY_APPROVAL_NUMBER")
    public String getMinistryApprovalNumber() {
	return ministryApprovalNumber;
    }

    public void setMinistryApprovalNumber(String ministryApprovalNumber) {
	this.ministryApprovalNumber = ministryApprovalNumber;
    }

    @Basic
    @Column(name = "MINISTRY_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryApprovalDate() {
	return ministryApprovalDate;
    }

    public void setMinistryApprovalDate(Date ministryApprovalDate) {
	this.ministryApprovalDate = ministryApprovalDate;
	this.ministryApprovalDateString = HijriDateService.getHijriDateString(ministryApprovalDate);
    }

    @Transient
    public String getMinistryApprovalDateString() {
	return ministryApprovalDateString;
    }

    public void setMinistryApprovalDateString(String ministryApprovalDateString) {
	this.ministryApprovalDateString = ministryApprovalDateString;
	this.ministryApprovalDate = HijriDateService.getHijriDate(ministryApprovalDateString);
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
    @Column(name = "SPECIAL_REMARKS")
    public String getSpecialRemarks() {
	return specialRemarks;
    }

    public void setSpecialRemarks(String specialRemarks) {
	this.specialRemarks = specialRemarks;
    }

    @Basic
    @Column(name = "SPECIAL_ATTACHMENTS")
    public String getSpecialAttachments() {
	return specialAttachments;
    }

    public void setSpecialAttachments(String specialAttachments) {
	this.specialAttachments = specialAttachments;
    }

    @Basic
    @Column(name = "REPLACEMENT_TRANS_ID")
    public Long getReplacementTransId() {
	return replacementTransId;
    }

    public void setReplacementTransId(Long replacementTransId) {
	this.replacementTransId = replacementTransId;
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return InternalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	InternalCopies = internalCopies;
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
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
    }

    @Basic
    @Column(name = "BASED_ON")
    public String getBasedOn() {
	return basedOn;
    }

    public void setBasedOn(String basedOn) {
	this.basedOn = basedOn;
    }

    @Basic
    @Column(name = "BASED_ON_ORDER_NUMBER")
    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
    }

    @Basic
    @Column(name = "BASED_ON_ORDER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getBasedOnOrderDate() {
	return basedOnOrderDate;
    }

    public void setBasedOnOrderDate(Date basedOnOrderDate) {
	this.basedOnOrderDate = basedOnOrderDate;
	this.basedOnOrderDateString = HijriDateService.getHijriDateString(basedOnOrderDate);
    }

    @Transient
    public String getBasedOnOrderDateString() {
	return basedOnOrderDateString;
    }

    public void setBasedOnOrderDateString(String basedOnOrderDateString) {
	this.basedOnOrderDateString = basedOnOrderDateString;
	this.basedOnOrderDate = HijriDateService.getHijriDate(basedOnOrderDateString);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEtrFlag() {
	return etrFlag;
    }

    public void setEtrFlag(Integer etrflag) {
	this.etrFlag = etrflag;
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigrationFlag() {
	return migrationFlag;
    }

    public void setMigrationFlag(Integer migFlag) {
	this.migrationFlag = migFlag;
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
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
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
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
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
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_ID")
    public Long getTransEmpRankId() {
	return transEmpRankId;
    }

    public void setTransEmpRankId(Long transEmpRankId) {
	this.transEmpRankId = transEmpRankId;
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
    @Column(name = "TRANS_EMP_JOB_MINOR_SPEC_DESC")
    public String getTransEmpJobMinorSpecDesc() {
	return transEmpJobMinorSpecDesc;
    }

    public void setTransEmpJobMinorSpecDesc(String transEmpJobMinorSpecDesc) {
	this.transEmpJobMinorSpecDesc = transEmpJobMinorSpecDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    public String getTransEmpJobCode() {
	return transEmpJobCode;
    }

    public void setTransEmpJobCode(String transEmpJobCode) {
	this.transEmpJobCode = transEmpJobCode;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CLASS_JOB_CODE")
    public String getTransEmpJobClassJobCode() {
	return transEmpJobClassJobCode;
    }

    public void setTransEmpJobClassJobCode(String transEmpJobClassJobCode) {
	this.transEmpJobClassJobCode = transEmpJobClassJobCode;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CLASS_JOB_DESC")
    public String getTransEmpJobClassJobDesc() {
	return transEmpJobClassJobDesc;
    }

    public void setTransEmpJobClassJobDesc(String transEmpJobClassJobDesc) {
	this.transEmpJobClassJobDesc = transEmpJobClassJobDesc;
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
    @Column(name = "TRANS_EMP_JOB_RANK_DESC")
    public String getTransEmpJobRankDesc() {
	return transEmpJobRankDesc;
    }

    public void setTransEmpJobRankDesc(String transEmpJobRankDesc) {
	this.transEmpJobRankDesc = transEmpJobRankDesc;
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
    @Column(name = "TRANS_APPROVED_RANK_DESC")
    public String getTransApprovedRankDesc() {
	return transApprovedRankDesc;
    }

    public void setTransApprovedRankDesc(String transApprovedRankDesc) {
	this.transApprovedRankDesc = transApprovedRankDesc;
    }

    @Basic
    @Column(name = "TRANS_APPROVED_JOB_NAME")
    public String getTransApprovedJobName() {
	return transApprovedJobName;
    }

    public void setTransApprovedJobName(String transApprovedJobName) {
	this.transApprovedJobName = transApprovedJobName;
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
    @Column(name = "BASED_ON_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getBasedOnDecisionDate() {
	return basedOnDecisionDate;
    }

    public void setBasedOnDecisionDate(Date basedOnDecisionDate) {
	this.basedOnDecisionDate = basedOnDecisionDate;
	this.basedOnDecisionDateString = HijriDateService.getHijriDateString(basedOnDecisionDate);
    }

    @Transient
    public String getBasedOnDecisionDateString() {
	return basedOnDecisionDateString;
    }

    public void setBasedOnDecisionDateString(String basedOnDecisionDateString) {
	this.basedOnDecisionDateString = basedOnDecisionDateString;
	this.basedOnDecisionDate = HijriDateService.getHijriDate(basedOnDecisionDateString);
    }

    @Basic
    @Column(name = "SUCCESSOR_DECISION_EFFECT_FLAG")
    public Integer getSuccessorDecisionEffectFlag() {
	return successorDecisionEffectFlag;
    }

    public void setSuccessorDecisionEffectFlag(Integer successorDecisionEffectFlag) {
	this.successorDecisionEffectFlag = successorDecisionEffectFlag;
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
    @Column(name = "SECURITY_UNIT_FLAG")
    public Integer getSecurityUnitFlag() {
	return securityUnitFlag;
    }

    public void setSecurityUnitFlag(Integer securityUnitFlag) {
	this.securityUnitFlag = securityUnitFlag;
    }

    @Basic
    @Column(name = "VERBAL_ORDER_FLAG")
    public Integer getVerbalOrderFlag() {
	return verbalOrderFlag;
    }

    public void setVerbalOrderFlag(Integer verbalOrderFlag) {
	this.verbalOrderFlag = verbalOrderFlag;
    }

    @Basic
    @Column(name = "REQUEST_TRANSACTION_FLAG")
    public Integer getRequestTransactionFlag() {
	return requestTransactionFlag;
    }

    public void setRequestTransactionFlag(Integer requestTransactionFlag) {
	this.requestTransactionFlag = requestTransactionFlag;
    }

    @Basic
    @Column(name = "SEQUENTIAL_MVT_FLAG")
    public Integer getSequentialMvtFlag() {
	return sequentialMvtFlag;
    }

    public void setSequentialMvtFlag(Integer sequentialMvtFlag) {
	this.sequentialMvtFlag = sequentialMvtFlag;
    }

    @Basic
    @Column(name = "TRANSFER_ALLOWANCE_FLAG")
    public Integer getTransferAllowanceFlag() {
	return transferAllowanceFlag;
    }

    public void setTransferAllowanceFlag(Integer transferAllowanceFlag) {
	this.transferAllowanceFlag = transferAllowanceFlag;
    }

    @Basic
    @Column(name = "TRANS_JOIN_APPROVED_JOB_NAME")
    public String getTransJoiningApprovedJobName() {
	return transJoiningApprovedJobName;
    }

    public void setTransJoiningApprovedJobName(String transJoiningApprovedJobName) {
	this.transJoiningApprovedJobName = transJoiningApprovedJobName;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "employeeId:" + employeeId + AUDIT_SEPARATOR +
		"joiningDate:" + joiningDate;
    }
}
