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
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "wf_movementData_getWFMovementDataByInstanceId",
		query = " from WFMovementData m where " +
			" m.instanceId in (:P_INSTANCE_ID) " +
			" order by m.employeeMilitaryNumber, m.employeeRankId, m.employeeRecruitmentDate, m.employeeName "),
	@NamedQuery(name = "wf_movementData_getMovementsTasks",
		query = " select  t, i, p.processName, requester, " +
			" case when t.originalId <> t.assigneeId then " +
			" (delegator.firstName || ' ' || delegator.secondName || ' ' || delegator.thirdName || case when delegator.thirdName is null then '' else ' ' end || delegator.lastName) " +
			" else null end " +
			" from  WFTask t, WFInstance i, WFProcess p, EmployeeData requester, Employee delegator " +
			" where t.instanceId	= i.id " +
			"   and i.processId  	= p.id " +
			"   and i.requesterId 	= requester.id " +
			"   and t.originalId 	= delegator.id " +
			"   and t.action is null " +
			"   and p.processGroupId   = :P_PROCESS_GROUP_ID " +
			"   and t.assigneeId       = :P_ASSIGNEE_ID " +
			"   and t.assigneeWfRole   in (:P_ASSIGNEE_WF_ROLES) " +
			"   and (:P_PROCESS_IDS_FLAG = '-1' or p.id in (:P_PROCESS_IDS)) " +
			"   order by t.taskId ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "ETR_VW_WF_MOVEMENTS")
@IdClass(WFMovementId.class)
public class WFMovementData extends BaseEntity implements Serializable {
    private Long instanceId;
    private Long movementTypeId;
    private String movementTypeDesc;
    private Long transactionTypeId;
    private Long employeeId;
    private String employeeName;
    private String EmployeeSocialID;
    private Integer employeeMilitaryNumber;
    private Long employeeRankId;
    private String employeeRankDesc;
    private Long employeeJobId;
    private String employeeJobCode;
    private String employeeJobName;
    private Long employeeUnitId;
    private String employeeUnitName;
    private Date employeeRecruitmentDate;
    private Long categoryId;
    private String categoryDesc;
    private Long unitId;
    private String unitFullName;
    private String unitHKey;
    private Long jobId;
    private String jobCode;
    private String jobName;
    private String freezeJobCode;
    private String freezeJobName;
    private Long freezeJobId;
    private Integer managerFlag;
    private Boolean managerFlagBoolean;
    private Integer executionDateFlag;
    private Boolean executionDateFlagBoolean;
    private Date executionDate;
    private String executionDateString;
    private Date endDate;
    private String endDateString;
    private Integer periodDays;
    private Integer periodMonths;
    private Integer locationFlag;
    private Boolean locationFlagBoolean;
    private String location;
    private String reasons;
    private Integer reasonType;
    private String remarks;
    private String decisionApprovalRemarks;
    private String specialRemarks;
    private String specialAttachments;
    private Long replacementEmployeeId;
    private Integer vacationGrantFlag;
    private Boolean vacationGrantFlagBoolean;
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
    private Boolean sequentialMvtFlagBoolean;
    private Integer transferAllowanceFlag;
    private Boolean transferAllowanceFlagBoolean;
    private Date joiningDate;
    private String joiningDateString;
    private Date returnJoiningDate;
    private String returnJoiningDateString;
    private Long transactionId;
    private String extraParams;
    private WFMovement wfMovement;

    public WFMovementData() {
	wfMovement = new WFMovement();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	wfMovement.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "MOVEMENT_TYPE_ID")
    @XmlTransient
    public Long getMovementTypeId() {
	return movementTypeId;
    }

    public void setMovementTypeId(Long movementTypeId) {
	this.movementTypeId = movementTypeId;
	wfMovement.setMovementTypeId(movementTypeId);
    }

    @Basic
    @Column(name = "MOVEMENT_TYPE_DESC")
    @XmlTransient
    public String getMovementTypeDesc() {
	return movementTypeDesc;
    }

    public void setMovementTypeDesc(String movementTypeDesc) {
	this.movementTypeDesc = movementTypeDesc;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    @XmlTransient
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
	wfMovement.setTransactionTypeId(transactionTypeId);
    }

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	wfMovement.setEmployeeId(employeeId);
    }

    @Basic
    @Column(name = "EMPLOYEE_NAME")
    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    @XmlTransient
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	wfMovement.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "CATEGORY_DESC")
    @XmlTransient
    public String getCategoryDesc() {
	return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
	this.categoryDesc = categoryDesc;
    }

    @Basic
    @Column(name = "UNIT_ID")
    @XmlTransient
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
	wfMovement.setUnitId(unitId);
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
    @Column(name = "UNIT_HKEY")
    @XmlTransient
    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHKey) {
	this.unitHKey = unitHKey;
    }

    @Basic
    @Column(name = "JOB_ID")
    @XmlTransient
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	wfMovement.setJobId(jobId);
    }

    @Basic
    @Column(name = "FREEZE_JOB_ID")
    @XmlTransient
    public Long getFreezeJobId() {
	return freezeJobId;
    }

    public void setFreezeJobId(Long freezeJobId) {
	this.freezeJobId = freezeJobId;
	wfMovement.setFreezeJobId(freezeJobId);
    }

    @Basic
    @Column(name = "FREEZE_JOB_CODE")
    @XmlTransient
    public String getFreezeJobCode() {
	return freezeJobCode;
    }

    public void setFreezeJobCode(String freezeJobCode) {
	this.freezeJobCode = freezeJobCode;
    }

    @Basic
    @Column(name = "FREEZE_JOB_NAME")
    @XmlTransient
    public String getFreezeJobName() {
	return freezeJobName;
    }

    public void setFreezeJobName(String freezeJobName) {
	this.freezeJobName = freezeJobName;
    }

    @Basic
    @Column(name = "MANAGER_FLAG")
    public Integer getManagerFlag() {
	return managerFlag;
    }

    public void setManagerFlag(Integer managerFlag) {
	this.managerFlag = managerFlag;

	if (this.managerFlag == null || this.managerFlag == FlagsEnum.OFF.getCode())
	    this.managerFlagBoolean = false;
	else
	    this.managerFlagBoolean = true;

	wfMovement.setManagerFlag(managerFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getManagerFlagBoolean() {
	return managerFlagBoolean;
    }

    public void setManagerFlagBoolean(Boolean managerFlagBoolean) {
	this.managerFlagBoolean = managerFlagBoolean;

	if (this.managerFlagBoolean == null || !this.managerFlagBoolean) {
	    this.managerFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.managerFlag = FlagsEnum.ON.getCode();
	}

	wfMovement.setManagerFlag(managerFlag);
    }

    @Basic
    @Column(name = "EXECUTION_DATE_FLAG")
    @XmlTransient
    public Integer getExecutionDateFlag() {
	return executionDateFlag;
    }

    public void setExecutionDateFlag(Integer executionDateFlag) {
	this.executionDateFlag = executionDateFlag;
	if (this.executionDateFlag == null || this.executionDateFlag == FlagsEnum.OFF.getCode())
	    this.executionDateFlagBoolean = false;
	else
	    this.executionDateFlagBoolean = true;
	wfMovement.setExecutionDateFlag(executionDateFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getExecutionDateFlagBoolean() {
	return executionDateFlagBoolean;
    }

    public void setExecutionDateFlagBoolean(Boolean executionDateFlagBoolean) {
	this.executionDateFlagBoolean = executionDateFlagBoolean;

	if (this.executionDateFlagBoolean == null || !this.executionDateFlagBoolean) {
	    this.executionDateFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.executionDateFlag = FlagsEnum.ON.getCode();
	}

	wfMovement.setExecutionDateFlag(executionDateFlag);
    }

    @Basic
    @Column(name = "EXECUTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
	this.executionDateString = HijriDateService.getHijriDateString(executionDate);
	wfMovement.setExecutionDate(executionDate);
    }

    @Transient
    public String getExecutionDateString() {
	return executionDateString;
    }

    public void setExecutionDateString(String executionDateString) {
	this.executionDateString = executionDateString;
	this.executionDate = HijriDateService.getHijriDate(executionDateString);
	wfMovement.setExecutionDate(this.executionDate);
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
	wfMovement.setEndDate(endDate);
    }

    @Transient
    @XmlTransient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
	wfMovement.setEndDate(this.endDate);
    }

    @Basic
    @Column(name = "PERIOD_DAYS")
    @XmlTransient
    public Integer getPeriodDays() {
	return periodDays;
    }

    public void setPeriodDays(Integer periodDays) {
	this.periodDays = periodDays;
	wfMovement.setPeriodDays(periodDays);
    }

    @Basic
    @Column(name = "PERIOD_MONTHS")
    @XmlTransient
    public Integer getPeriodMonths() {
	return periodMonths;
    }

    public void setPeriodMonths(Integer periodMonths) {
	this.periodMonths = periodMonths;
	wfMovement.setPeriodMonths(periodMonths);
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    @XmlTransient
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
	if (this.locationFlag == null || this.locationFlag == FlagsEnum.OFF.getCode())
	    this.locationFlagBoolean = false;
	else
	    this.locationFlagBoolean = true;
	wfMovement.setLocationFlag(locationFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getLocationFlagBoolean() {
	return locationFlagBoolean;
    }

    public void setLocationFlagBoolean(Boolean locationFlagBoolean) {
	this.locationFlagBoolean = locationFlagBoolean;

	if (this.locationFlagBoolean == null || !this.locationFlagBoolean) {
	    this.locationFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.locationFlag = FlagsEnum.ON.getCode();
	}

	wfMovement.setLocationFlag(locationFlag);
    }

    @Basic
    @Column(name = "LOCATION")
    @XmlTransient
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
	wfMovement.setLocation(location);
    }

    @Basic
    @Column(name = "REASONS")
    @XmlTransient
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	wfMovement.setReasons(reasons);
    }

    @Basic
    @Column(name = "REASON_TYPE")
    @XmlTransient
    public Integer getReasonType() {
	return reasonType;
    }

    public void setReasonType(Integer reasonType) {
	this.reasonType = reasonType;
	wfMovement.setReasonType(reasonType);
    }

    @Basic
    @Column(name = "REMARKS")
    @XmlTransient
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	wfMovement.setRemarks(remarks);
    }

    @Basic
    @Column(name = "DECISION_APPROVAL_REMARKS")
    @XmlTransient
    public String getDecisionApprovalRemarks() {
	return decisionApprovalRemarks;
    }

    public void setDecisionApprovalRemarks(String decisionApprovalRemarks) {
	this.decisionApprovalRemarks = decisionApprovalRemarks;
	wfMovement.setDecisionApprovalRemarks(decisionApprovalRemarks);
    }

    @Basic
    @Column(name = "SPECIAL_REMARKS")
    @XmlTransient
    public String getSpecialRemarks() {
	return specialRemarks;
    }

    public void setSpecialRemarks(String specialRemarks) {
	this.specialRemarks = specialRemarks;
	wfMovement.setSpecialRemarks(specialRemarks);
    }

    @Basic
    @Column(name = "SPECIAL_ATTACHMENTS")
    @XmlTransient
    public String getSpecialAttachments() {
	return specialAttachments;
    }

    public void setSpecialAttachments(String specialAttachments) {
	this.specialAttachments = specialAttachments;
	wfMovement.setSpecialAttachments(specialAttachments);
    }

    @Basic
    @Column(name = "REPLACEMENT_EMPLOYEE_ID")
    @XmlTransient
    public Long getReplacementEmployeeId() {
	return replacementEmployeeId;
    }

    public void setReplacementEmployeeId(Long replacementEmployeeId) {
	this.replacementEmployeeId = replacementEmployeeId;
	wfMovement.setReplacementEmployeeId(replacementEmployeeId);
    }

    @Basic
    @Column(name = "VACATION_GRANT_FLAG")
    @XmlTransient
    public Integer getVacationGrantFlag() {
	return vacationGrantFlag;
    }

    public void setVacationGrantFlag(Integer vacationGrantFlag) {
	this.vacationGrantFlag = vacationGrantFlag;
	if (this.vacationGrantFlag == null || this.vacationGrantFlag == FlagsEnum.OFF.getCode())
	    this.vacationGrantFlagBoolean = false;
	else
	    this.vacationGrantFlagBoolean = true;
	wfMovement.setVacationGrantFlag(vacationGrantFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getVacationGrantFlagBoolean() {
	return vacationGrantFlagBoolean;
    }

    public void setVacationGrantFlagBoolean(Boolean vacationGrantFlagBoolean) {
	this.vacationGrantFlagBoolean = vacationGrantFlagBoolean;

	if (this.vacationGrantFlagBoolean == null || !this.vacationGrantFlagBoolean) {
	    this.vacationGrantFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.vacationGrantFlag = FlagsEnum.ON.getCode();
	}

	wfMovement.setVacationGrantFlag(vacationGrantFlag);
    }

    @Basic
    @Column(name = "MINISTRY_APPROVAL_NUMBER")
    @XmlTransient
    public String getMinistryApprovalNumber() {
	return ministryApprovalNumber;
    }

    public void setMinistryApprovalNumber(String ministryApprovalNumber) {
	this.ministryApprovalNumber = ministryApprovalNumber;
	wfMovement.setMinistryApprovalNumber(ministryApprovalNumber);
    }

    @Basic
    @Column(name = "MINISTRY_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getMinistryApprovalDate() {
	return ministryApprovalDate;
    }

    public void setMinistryApprovalDate(Date ministryApprovalDate) {
	this.ministryApprovalDate = ministryApprovalDate;
	this.ministryApprovalDateString = HijriDateService.getHijriDateString(ministryApprovalDate);
	wfMovement.setMinistryApprovalDate(ministryApprovalDate);
    }

    @Transient
    @XmlTransient
    public String getMinistryApprovalDateString() {
	return ministryApprovalDateString;
    }

    public void setMinistryApprovalDateString(String ministryApprovalDateString) {
	this.ministryApprovalDateString = ministryApprovalDateString;
	this.ministryApprovalDate = HijriDateService.getHijriDate(ministryApprovalDateString);
	wfMovement.setMinistryApprovalDate(this.ministryApprovalDate);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    @XmlTransient
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	wfMovement.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    @XmlTransient
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	wfMovement.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "OLD_MOVEMENT_ID")
    @XmlTransient
    public Long getOldMovementId() {
	return oldMovementId;
    }

    public void setOldMovementId(Long oldMovementId) {
	this.oldMovementId = oldMovementId;
	wfMovement.setOldMovementId(oldMovementId);
    }

    @Basic
    @Column(name = "BASED_ON")
    @XmlTransient
    public String getBasedOn() {
	return basedOn;
    }

    public void setBasedOn(String basedOn) {
	this.basedOn = basedOn;
	wfMovement.setBasedOn(basedOn);
    }

    @Basic
    @Column(name = "BASED_ON_ORDER_NUMBER")
    @XmlTransient
    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
	wfMovement.setBasedOnOrderNumber(basedOnOrderNumber);
    }

    @Basic
    @Column(name = "BASED_ON_ORDER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getBasedOnOrderDate() {
	return basedOnOrderDate;
    }

    public void setBasedOnOrderDate(Date basedOnOrderDate) {
	this.basedOnOrderDate = basedOnOrderDate;
	this.basedOnOrderDateString = HijriDateService.getHijriDateString(basedOnOrderDate);
	wfMovement.setBasedOnOrderDate(basedOnOrderDate);
    }

    @Transient
    @XmlTransient
    public String getBasedOnOrderDateString() {
	return basedOnOrderDateString;
    }

    public void setBasedOnOrderDateString(String basedOnOrderDateString) {
	this.basedOnOrderDateString = basedOnOrderDateString;
	this.basedOnOrderDate = HijriDateService.getHijriDate(basedOnOrderDateString);
	wfMovement.setBasedOnOrderDate(this.basedOnOrderDate);
    }

    @Basic
    @Column(name = "APPROVED_ID")
    @XmlTransient
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
	wfMovement.setApprovedId(approvedId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    @XmlTransient
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	wfMovement.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    @XmlTransient
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	wfMovement.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    @XmlTransient
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	wfMovement.setDecisionRegionId(decisionRegionId);
    }

    @Basic
    @Column(name = "BASED_ON_DECISION_NUMBER")
    @XmlTransient
    public String getBasedOnDecisionNumber() {
	return basedOnDecisionNumber;
    }

    public void setBasedOnDecisionNumber(String basedOnDecisionNumber) {
	this.basedOnDecisionNumber = basedOnDecisionNumber;
	wfMovement.setBasedOnDecisionNumber(basedOnDecisionNumber);
    }

    @Basic
    @Column(name = "BASED_ON_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getBasedOnDecisionDate() {
	return basedOnDecisionDate;
    }

    public void setBasedOnDecisionDate(Date basedOnDecisionDate) {
	this.basedOnDecisionDate = basedOnDecisionDate;
	this.basedOnDecisionDateString = HijriDateService.getHijriDateString(basedOnDecisionDate);
	wfMovement.setBasedOnDecisionDate(basedOnDecisionDate);
    }

    @Transient
    @XmlTransient
    public String getBasedOnDecisionDateString() {
	return basedOnDecisionDateString;
    }

    public void setBasedOnDecisionDateString(String basedOnDecisionDateString) {
	this.basedOnDecisionDateString = basedOnDecisionDateString;
	this.basedOnDecisionDate = HijriDateService.getHijriDate(basedOnDecisionDateString);
	wfMovement.setBasedOnDecisionDate(this.basedOnDecisionDate);
    }

    @Transient
    @XmlTransient
    public WFMovement getWfMovement() {
	return wfMovement;
    }

    public void setWfMovement(WFMovement wfMovement) {
	this.wfMovement = wfMovement;
    }

    @Basic
    @Column(name = "EMPLOYEE_SOCIAL_ID")
    @XmlTransient
    public String getEmployeeSocialID() {
	return EmployeeSocialID;
    }

    public void setEmployeeSocialID(String employeeSocialID) {
	EmployeeSocialID = employeeSocialID;
    }

    @Basic
    @Column(name = "EMPLOYEE_MILITARY_NUMBER")
    @XmlTransient
    public Integer getEmployeeMilitaryNumber() {
	return employeeMilitaryNumber;
    }

    public void setEmployeeMilitaryNumber(Integer employeeMilitaryNumber) {
	this.employeeMilitaryNumber = employeeMilitaryNumber;
    }

    @Basic
    @Column(name = "EMPLOYEE_RANK_ID")
    @XmlTransient
    public Long getEmployeeRankId() {
	return employeeRankId;
    }

    public void setEmployeeRankId(Long employeeRankId) {
	this.employeeRankId = employeeRankId;
    }

    @Basic
    @Column(name = "EMPLOYEE_RANK_DESC")
    public String getEmployeeRankDesc() {
	return employeeRankDesc;
    }

    public void setEmployeeRankDesc(String employeeRankDesc) {
	this.employeeRankDesc = employeeRankDesc;
    }

    @Basic
    @Column(name = "EMPLOYEE_JOB_ID")
    @XmlTransient
    public Long getEmployeeJobId() {
	return employeeJobId;
    }

    public void setEmployeeJobId(Long employeeJobId) {
	this.employeeJobId = employeeJobId;
    }

    @Basic
    @Column(name = "EMPLOYEE_JOB_CODE")
    @XmlTransient
    public String getEmployeeJobCode() {
	return employeeJobCode;
    }

    public void setEmployeeJobCode(String employeeJobCode) {
	this.employeeJobCode = employeeJobCode;
    }

    @Basic
    @Column(name = "EMPLOYEE_JOB_NAME")
    public String getEmployeeJobName() {
	return employeeJobName;
    }

    public void setEmployeeJobName(String employeeJobName) {
	this.employeeJobName = employeeJobName;
    }

    @Basic
    @Column(name = "EMPLOYEE_UNIT_ID")
    @XmlTransient
    public Long getEmployeeUnitId() {
	return employeeUnitId;
    }

    public void setEmployeeUnitId(Long employeeUnitId) {
	this.employeeUnitId = employeeUnitId;
    }

    @Basic
    @Column(name = "EMPLOYEE_UNIT_NAME")
    public String getEmployeeUnitName() {
	return employeeUnitName;
    }

    public void setEmployeeUnitName(String employeeUnitName) {
	this.employeeUnitName = employeeUnitName;
    }

    @Basic
    @Column(name = "RECRUITMENT_DATE")
    @XmlTransient
    public Date getEmployeeRecruitmentDate() {
	return employeeRecruitmentDate;
    }

    public void setEmployeeRecruitmentDate(Date employeeRecruitmentDate) {
	this.employeeRecruitmentDate = employeeRecruitmentDate;
    }

    @Basic
    @Column(name = "JOB_CODE")
    @XmlTransient
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
    @Column(name = "WARNING_MESSAGES")
    @XmlTransient
    public String getWarningMessages() {
	return warningMessages;
    }

    public void setWarningMessages(String warningMessages) {
	this.warningMessages = warningMessages;
	wfMovement.setWarningMessages(warningMessages);
    }

    @Basic
    @Column(name = "VERBAL_ORDER_FLAG")
    @XmlTransient
    public Integer getVerbalOrderFlag() {
	return verbalOrderFlag;
    }

    public void setVerbalOrderFlag(Integer verbalOrderFlag) {
	this.verbalOrderFlag = verbalOrderFlag;
	wfMovement.setVerbalOrderFlag(verbalOrderFlag);
    }

    @Basic
    @Column(name = "SEQUENTIAL_MVT_FLAG")
    @XmlTransient
    public Integer getSequentialMvtFlag() {
	return sequentialMvtFlag;
    }

    public void setSequentialMvtFlag(Integer sequentialMvtFlag) {
	this.sequentialMvtFlag = sequentialMvtFlag;

	if (this.sequentialMvtFlag == null || this.sequentialMvtFlag == FlagsEnum.OFF.getCode())
	    this.sequentialMvtFlagBoolean = false;
	else
	    this.sequentialMvtFlagBoolean = true;

	wfMovement.setSequentialMvtFlag(sequentialMvtFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getSequentialMvtFlagBoolean() {
	return sequentialMvtFlagBoolean;
    }

    public void setSequentialMvtFlagBoolean(Boolean sequentialMvtFlagBoolean) {
	this.sequentialMvtFlagBoolean = sequentialMvtFlagBoolean;

	if (this.sequentialMvtFlagBoolean == null || !this.sequentialMvtFlagBoolean) {
	    this.sequentialMvtFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.sequentialMvtFlag = FlagsEnum.ON.getCode();
	}

	wfMovement.setSequentialMvtFlag(sequentialMvtFlag);
    }

    @Basic
    @Column(name = "TRANSFER_ALLOWANCE_FLAG")
    @XmlTransient
    public Integer getTransferAllowanceFlag() {
	return transferAllowanceFlag;
    }

    public void setTransferAllowanceFlag(Integer transferAllowanceFlag) {
	this.transferAllowanceFlag = transferAllowanceFlag;

	if (this.transferAllowanceFlag == null || this.transferAllowanceFlag == FlagsEnum.OFF.getCode())
	    this.transferAllowanceFlagBoolean = false;
	else
	    this.transferAllowanceFlagBoolean = true;

	wfMovement.setTransferAllowanceFlag(transferAllowanceFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getTransferAllowanceFlagBoolean() {
	return transferAllowanceFlagBoolean;
    }

    public void setTransferAllowanceFlagBoolean(Boolean transferAllowanceFlagBoolean) {
	this.transferAllowanceFlagBoolean = transferAllowanceFlagBoolean;

	if (this.transferAllowanceFlagBoolean == null || !this.transferAllowanceFlagBoolean) {
	    this.transferAllowanceFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.transferAllowanceFlag = FlagsEnum.ON.getCode();
	}

	wfMovement.setTransferAllowanceFlag(transferAllowanceFlag);
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
	this.joiningDateString = HijriDateService.getHijriDateString(joiningDate);
	wfMovement.setJoiningDate(joiningDate);
    }

    @Transient
    @XmlTransient
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
    @XmlTransient
    public Date getReturnJoiningDate() {
	return returnJoiningDate;
    }

    public void setReturnJoiningDate(Date returnJoiningDate) {
	this.returnJoiningDate = returnJoiningDate;
	this.returnJoiningDateString = HijriDateService.getHijriDateString(returnJoiningDate);
	wfMovement.setReturnJoiningDate(returnJoiningDate);
    }

    @Transient
    @XmlTransient
    public String getReturnJoiningDateString() {
	return returnJoiningDateString;
    }

    public void setReturnJoiningDateString(String returnJoiningDateString) {
	this.returnJoiningDateString = returnJoiningDateString;
	this.returnJoiningDate = HijriDateService.getHijriDate(returnJoiningDateString);
    }

    @Basic
    @Column(name = "TRANSACTION_ID")
    @XmlTransient
    public Long getTransactionId() {
	return transactionId;
    }

    public void setTransactionId(Long transactionId) {
	this.transactionId = transactionId;
	wfMovement.setTransactionId(transactionId);
    }

    @Basic
    @Column(name = "EXTRA_PARAMS")
    @XmlTransient
    public String getExtraParams() {
	return extraParams;
    }

    public void setExtraParams(String extraParams) {
	this.extraParams = extraParams;
	wfMovement.setExtraParams(extraParams);
    }
}