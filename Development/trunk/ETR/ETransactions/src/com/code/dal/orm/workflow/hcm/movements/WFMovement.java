package com.code.dal.orm.workflow.hcm.movements;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "wf_movement_searchWFMovementsInstancesDataByBeneficiaryId",
		query = " select i from WFMovement m, WFInstanceData i " +
			" where m.instanceId = i.instanceId " +
			"   and  (m.employeeId = :P_BENEFICIARY_ID or m.replacementEmployeeId = :P_BENEFICIARY_ID) " +
			"   and i.status in (:P_STATUS_VALUES) " +
			" order by i.requestDate desc "),
	@NamedQuery(name = "wf_movement_checkExistingMovementProcesses",
		query = " select m from WFMovement m, WFInstance i where " +
			" m.instanceId = i.instanceId " +
			" and i.status = 1 " +
			" and ( m.employeeId in ( :P_EMPS_IDS ) or m.replacementEmployeeId in ( :P_EMPS_IDS ) or (m.jobId in ( :P_JOBS_IDS ) and m.movementTypeId = 1 ) or m.freezeJobId in ( :P_JOBS_IDS )) " + // To skip jobs in officers assignment and persons subjoin
			" and (:P_EXCLUDED_INSTANCE_ID = -1 or m.instanceId <> :P_EXCLUDED_INSTANCE_ID) "),
	@NamedQuery(name = "wf_movement_searchWFMovementsTasksDataByBeneficiaryId",
		query = " select t from WFMovement m, WFTaskData t " +
			" where m.instanceId = t.instanceId " +
			"   and (m.employeeId = :P_BENEFICIARY_ID or m.replacementEmployeeId = :P_BENEFICIARY_ID)" +
			"   and t.assigneeId = :P_ASSIGNEE_ID" +
			"   and ((:P_RUNNING = 1 and t.action is null) OR (:P_RUNNING = 0 and t.action is not null))" +
			"   and (:P_TASK_ROLE = -1 or (:P_TASK_ROLE = 1 and t.assigneeWfRole <> 'Notification') or (:P_TASK_ROLE = 2 and t.assigneeWfRole = 'Notification') ) " +
			" order by t.assignDate ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "WF_MOVEMENTS")
@IdClass(WFMovementId.class)
public class WFMovement extends AuditEntity implements Serializable, InsertableAuditEntity, UpdatableAuditEntity {
    private Long instanceId;
    private Long movementTypeId;
    private Long transactionTypeId;
    private Long employeeId;
    private Long categoryId;
    private Long unitId;
    private Long jobId;
    private Long freezeJobId;
    private Integer managerFlag;
    private Integer executionDateFlag;
    private Date executionDate;
    private String executionDateString;
    private Date endDate;
    private String endDateString;
    private Integer periodDays;
    private Integer periodMonths;
    private Integer locationFlag;
    private String location;
    private String reasons;
    private Integer reasonType;
    private String remarks;
    private String decisionApprovalRemarks;
    private String specialRemarks;
    private String specialAttachments;
    private Long replacementEmployeeId;
    private Integer vacationGrantFlag;
    private String ministryApprovalNumber;
    private Date ministryApprovalDate;
    private String ministryApprovalDateString;
    private String internalCopies;
    private String externalCopies;
    private Long oldMovementId;
    private String basedOn;
    private String basedOnOrderNumber;
    private Date basedOnOrderDate;
    private String basedOnOrderDateString;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Long decisionRegionId;
    private String basedOnDecisionNumber;
    private Date basedOnDecisionDate;
    private String basedOnDecisionDateString;
    private String warningMessages;
    private Integer verbalOrderFlag;
    private Integer sequentialMvtFlag;
    private Integer transferAllowanceFlag;
    private Date joiningDate;
    private Date returnJoiningDate;
    private Long transactionId;
    private String extraParams;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
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

    @Id
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
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
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
    @Column(name = "MANAGER_FLAG")
    public Integer getManagerFlag() {
	return managerFlag;
    }

    public void setManagerFlag(Integer managerFlag) {
	this.managerFlag = managerFlag;
    }

    @Basic
    @Column(name = "EXECUTION_DATE_FLAG")
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
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Basic
    @Column(name = "DECISION_APPROVAL_REMARKS")
    public String getDecisionApprovalRemarks() {
	return decisionApprovalRemarks;
    }

    public void setDecisionApprovalRemarks(String decisionApprovalRemarks) {
	this.decisionApprovalRemarks = decisionApprovalRemarks;
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
    @Column(name = "REPLACEMENT_EMPLOYEE_ID")
    public Long getReplacementEmployeeId() {
	return replacementEmployeeId;
    }

    public void setReplacementEmployeeId(Long replacementEmployeeId) {
	this.replacementEmployeeId = replacementEmployeeId;
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
    @Column(name = "OLD_MOVEMENT_ID")
    public Long getOldMovementId() {
	return oldMovementId;
    }

    public void setOldMovementId(Long oldMovementId) {
	this.oldMovementId = oldMovementId;
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
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
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
    @Column(name = "WARNING_MESSAGES")
    public String getWarningMessages() {
	return warningMessages;
    }

    public void setWarningMessages(String warningMessages) {
	this.warningMessages = warningMessages;
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
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
    }

    @Basic
    @Column(name = "RETURN_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getReturnJoiningDate() {
	return returnJoiningDate;
    }

    public void setReturnJoiningDate(Date returnJoiningDate) {
	this.returnJoiningDate = returnJoiningDate;
    }

    @Basic
    @Column(name = "TRANSACTION_ID")
    public Long getTransactionId() {
	return transactionId;
    }

    public void setTransactionId(Long transactionId) {
	this.transactionId = transactionId;
    }

    @Basic
    @Column(name = "EXTRA_PARAMS")
    public String getExtraParams() {
	return extraParams;
    }

    public void setExtraParams(String extraParams) {
	this.extraParams = extraParams;
    }

    @Override
    public Long calculateContentId() {
	return instanceId;
    }

    @Override
    public String calculateContent() {
	return "employeeId:" + employeeId + AUDIT_SEPARATOR +
		"unitId:" + unitId + AUDIT_SEPARATOR +
		"jobId:" + jobId + AUDIT_SEPARATOR +
		"periodMonths:" + periodMonths + AUDIT_SEPARATOR +
		"periodDays:" + periodDays + AUDIT_SEPARATOR +
		"executionDate:" + executionDate + AUDIT_SEPARATOR +
		"endDate:" + endDate + AUDIT_SEPARATOR;
    }
}
