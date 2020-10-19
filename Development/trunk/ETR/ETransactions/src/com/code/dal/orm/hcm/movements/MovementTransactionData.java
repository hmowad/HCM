package com.code.dal.orm.hcm.movements;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_movementTransactionData_searchMovementTransactionData",
		query = " select m from MovementTransactionData m where " +
			" (:P_CATEGORIES_IDS_FLAG = -1 or m.categoryId in ( :P_CATEGORIES_IDS )) " +
			" and (:P_DECISION_NUMBER = '-1' or m.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_DECISION_DATE_FROM_FLAG = -1 or to_date(:P_DECISION_DATE_FROM, 'MI/MM/YYYY') <= m.decisionDate) " +
			" and (:P_DECISION_DATE_TO_FLAG = -1 or to_date(:P_DECISION_DATE_TO, 'MI/MM/YYYY') >= m.decisionDate) " +
			" and (:P_MOVEMENT_TYPE_ID = -1 or m.movementTypeId = :P_MOVEMENT_TYPE_ID) " +
			" and (:P_REGION_ID = -1 or :P_REGION_ID = m.decisionRegionId or :P_REGION_ID = m.empPhysicalRegionId)" +
			" and (m.id = (select min(mt.id) from MovementTransactionData mt where (:P_EMPLOYEE_ID = -1 or mt.employeeId = :P_EMPLOYEE_ID) " +
			"               and mt.decisionNumber = m.decisionNumber and mt.decisionDate = m.decisionDate and mt.etrFlag = m.etrFlag" +
			"               and (:P_REGION_ID = -1 or :P_REGION_ID = mt.decisionRegionId or :P_REGION_ID = mt.empPhysicalRegionId) )) " +
			" and (:P_ETR_FLAG = -1 or m.etrFlag = :P_ETR_FLAG) " +
			" order by m.executionDate DESC,m.id DESC "),
	@NamedQuery(name = "hcm_movementTransactionData_getMovementTransactionDataByDecisionNumberAndDecisionDate",
		query = " select m from MovementTransactionData m where " +
			" (:P_DECISION_NUMBER = '-1' or m.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_DECISION_DATE_FROM_FLAG = -1 or to_date(:P_DECISION_DATE_FROM, 'MI/MM/YYYY') <= m.decisionDate) " +
			" and (:P_DECISION_DATE_TO_FLAG = -1 or to_date(:P_DECISION_DATE_TO, 'MI/MM/YYYY') >= m.decisionDate) " +
			" and (:P_EMPLOYEES_IDS_FLAG = -1 or m.employeeId in (:P_EMPLOYEES_IDS)) " +
			" and (:P_MOVEMENT_TYPE_ID = -1 or m.movementTypeId = :P_MOVEMENT_TYPE_ID) " +
			" and (:P_TRANS_TYPE_ID = -1 or m.transactionTypeId = :P_TRANS_TYPE_ID) " +
			" and (:P_EXECUTION_DATE_FROM_FLAG = -1 or to_date(:P_EXECUTION_DATE_FROM, 'MI/MM/YYYY') <= m.executionDate) " +
			" and (:P_EXECUTION_DATE_TO_FLAG = -1 or to_date(:P_EXECUTION_DATE_TO, 'MI/MM/YYYY') >= m.executionDate) " +
			" order by m.id "),
	@NamedQuery(name = "hcm_movementTransactionData_getCancelledAndTerminatedMovementTransactionDataByDecisionNumberAndDecisionDate",
		query = " select m, e from MovementTransactionData m, EmployeeData e where " +
			" e.id = m.employeeId" +
			" and m.decisionNumber = :P_DECISION_NUMBER " +
			" and to_date(:P_DECISION_DATE, 'MI/MM/YYYY') = m.decisionDate " +
			" and m.employeeId in (:P_EMPLOYEES_IDS) " +
			" order by m.id "),
	@NamedQuery(name = "hcm_movementTransactionData_getValidDecisionMembers",
		query = " select m from MovementTransactionData m where " +
			" m.decisionNumber = :P_DECISION_NUMBER " +
			" and to_date(:P_DECISION_DATE, 'MI/MM/YYYY') = m.decisionDate " +
			" and (:P_CATEGORIES_IDS_FLAG = -1 or m.categoryId in ( :P_CATEGORIES_IDS )) " +
			" and (m.movementTypeId = :P_MOVEMENT_TYPE_ID) " +
			" and (:P_REGION_ID = -1 or m.decisionRegionId = :P_REGION_ID) " +
			" and (m.etrFlag = :P_ETR_FLAG) " +
			" and m.employeeId not in ( " +
			" select mt.employeeId from MovementTransactionData mt where " +
			" mt.basedOnDecisionNumber = m.decisionNumber " +
			" and mt.basedOnDecisionDate = m.decisionDate " +
			" and ( mt.successorDecisionEffectFlag is null or mt.successorDecisionEffectFlag <> 4 ) " +
			" and mt.movementTypeId = m.movementTypeId " +
			" and (:P_REGION_ID = -1 or mt.decisionRegionId = m.decisionRegionId) " +
			" and mt.etrFlag = m.etrFlag " +
			" ) order by m.employeeMilitaryNumber, m.transEmpRankId, m.employeeRecruitmentDate, m.employeeName "),
	@NamedQuery(name = "hcm_movementTransactionData_getLastValidMovementTransaction",
		query = " select m from MovementTransactionData m where " +
			" (m.employeeId = :P_EMP_ID) " +
			" and (:P_MOVEMENT_TYPE_ID = -1 or m.movementTypeId = :P_MOVEMENT_TYPE_ID) " +
			" and (m.transactionTypeId in (:P_TRANS_TYPES_IDS)) " +
			" and (m.successorDecisionEffectFlag is null) " +
			" and (:P_ETR_FLAG = -1 or m.etrFlag = :P_ETR_FLAG) " +
			" and m.executionDate = ( " +
			" select max(mt.executionDate) from MovementTransactionData mt where " +
			" (mt.employeeId = :P_EMP_ID) " +
			" and (:P_MOVEMENT_TYPE_ID = -1 or mt.movementTypeId = :P_MOVEMENT_TYPE_ID) " +
			" and (mt.transactionTypeId in (:P_TRANS_TYPES_IDS)) " +
			" and (mt.successorDecisionEffectFlag is null) " +
			" ) "),
	@NamedQuery(name = "hcm_movementTransactionData_countConflictDatesTransactions",
		query = " select count(m.id) from MovementTransactionData m where " +
			" m.employeeId = :P_EMP_ID " +
			" and m.transactionTypeCode <> 4 " +
			" and m.movementTypeId <> 6 " +
			" and (m.successorDecisionEffectFlag is null or m.successorDecisionEffectFlag not in (3, 4)) " +
			" and ( (m.endDate is null and ( (:P_END_DATE_FLAG = -1 and (to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') = m.executionDate)) or " +
			" ( (:P_END_DATE_FLAG = 1 and m.executionDate between to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') and to_date(:P_END_DATE, 'MI/MM/YYYY'))) ) ) " +
			" or " +
			" ( m.endDate is not null and ( (to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') between m.executionDate and m.endDate) or " +
			" (:P_END_DATE_FLAG = 1 and m.executionDate between to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') and to_date(:P_END_DATE, 'MI/MM/YYYY')) ) ) ) "),
	@NamedQuery(name = "hcm_movementTransactionData_getNotExecutedMovementsTransactions",
		query = " select m from MovementTransactionData m where " +
			" m.executionDate <= to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') " +
			" and m.transactionTypeCode in (1,2) " +
			" and (m.successorDecisionEffectFlag is null or m.successorDecisionEffectFlag <> 4) " +
			" and m.effectFlag = 0 " +
			" order by m.decisionNumber,m.decisionDate "),
	@NamedQuery(name = "hcm_movementTransactionData_countEmployeeTransactionsAfterGivenExecutionDate",
		query = " select count(m.id) from MovementTransactionData m where " +
			" m.employeeId = :P_EMP_ID " +
			" and m.transactionTypeCode <> 4 " +
			" and m.movementTypeId <> 6 " +
			" and (m.successorDecisionEffectFlag is null or m.successorDecisionEffectFlag not in (3,4)) " +
			" and ( (m.endDate is null and m.executionDate >= to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') ) or " +
			" ( m.endDate is not null and m.endDate >= to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') ) ) "),
	@NamedQuery(name = "hcm_movementTransactionData_getEmployeesAndMovementsTransactionsForRollback",
		query = " select e from EmployeeData e where " +
			" e.statusId in (20,22,30,35,40,42,45) " +
			" and (select count(m.id) from MovementTransactionData m where " +
			" m.employeeId = e.id " +
			" and m.transactionTypeCode <> 4 " +
			" and (m.successorDecisionEffectFlag is null or m.successorDecisionEffectFlag not in (3,4)) " +
			" and ((m.endDate is not null and to_date(:P_HIJRI_SYS_DATE, 'MI/MM/YYYY') between m.executionDate and m.endDate))" +
			") = 0 "),
	@NamedQuery(name = "hcm_movementTransactionData_getMovementTransactionDataByById",
		query = " select m from MovementTransactionData m where " +
			" (:P_TRANS_ID = '-1' or m.id = :P_TRANS_ID) " +
			" order by m.id "),

	@NamedQuery(name = "hcm_movementTransactionData_getLastMovementTransactionForJoiningDate",
		query = "select m from MovementTransactionData m where " +
			" (m.employeeId = :P_EMP_ID) " +
			" and m.executionDate= (select max(mt.executionDate) from MovementTransactionData mt where " +
			" (mt.employeeId = :P_EMP_ID) " +
			" and (mt.movementTypeId <> 6) " +
			" and (mt.effectFlag = 1))" +
			" and (m.etrFlag = 1) " +
			" and (m.locationFlag = 0)" +
			" and (m.movementTypeId = :P_MOVEMENT_TYPE_ID)" +
			" and (m.joiningDate is null) " +
			" and (m.transactionTypeCode = 1) "),
	@NamedQuery(name = "hcm_movementTransactionData_getLastMovementTransactionForReturnJoiningDate",
		query = "select m from MovementTransactionData m where " +
			" (m.employeeId = :P_EMP_ID) " +
			" and m.executionDate= (select max(mt.executionDate) from MovementTransactionData mt where " +
			" (mt.employeeId = :P_EMP_ID) " +
			" and (mt.movementTypeId <> 6) " +
			" and (mt.effectFlag = 1))" +
			" and (  (m.transactionTypeCode = 1 and m.endDate < to_date(:P_HIJRI_SYS_DATE, 'MI/MM/YYYY') and ((m.locationFlag = 1) or (m.locationFlag = 0 and (m.joiningDate is not null)))) " +
			"     or (m.transactionTypeCode = 2 and m.endDate < to_date(:P_HIJRI_SYS_DATE, 'MI/MM/YYYY')) " +
			"     or (m.transactionTypeCode = 3))" +
			" and (m.movementTypeId = :P_MOVEMENT_TYPE_ID) " +
			" and (:P_HIJRI_APPLY_DATE = '-1' or m.endDate > to_date(:P_HIJRI_APPLY_DATE, 'MI/MM/YYYY'))" +
			" and (m.returnJoiningDate is null)"),
	@NamedQuery(name = "hcm_movementTransactionData_getMovementTransactionForJoiningReport",
		query = "select m from MovementTransactionData m where " +
			"(m.employeeId = :P_EMPLOYEE_ID) " +
			"and (m.decisionNumber = :P_DECISION_NUMBER)" +
			"and (m.decisionDate= to_date(:P_DECISION_DATE, 'MI/MM/YYYY'))" +
			"and (m.movementTypeId in (1, 2)) " +
			"and (m.transJoiningApprovedJobName is not null)" +
			"and (m.etrFlag = 1) " +
			"and (m.locationFlag = 0)")

})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_MVT_TRANSACTIONS")
public class MovementTransactionData extends BaseEntity implements Serializable {
    private Long id;
    private Long movementTypeId;
    private String movementTypeDesc;
    private Long transactionTypeId;
    private Integer transactionTypeCode;
    private String transactionTypeDesc;
    private Long employeeId;
    private String employeeName;
    private Long empPhysicalRegionId;
    private Date employeeRecruitmentDate;
    private Integer employeeMilitaryNumber;
    private Long categoryId;
    private String categoryDesc;
    private Long unitId;
    private String unitFullName;
    private Long jobId;
    private String jobCode;
    private String jobName;
    private Long jobRankId;
    private String jobRankDesc;
    private Long jobMinorSpecId;
    private String jobMinorSpecDesc;
    private Long jobClassificationId;
    private String jobDesc;
    private Long freezeJobId;
    private String freezeJobCode;
    private String freezeJobName;
    private Long freezeJobRankId;
    private String freezeJobUnitFullName;
    private Boolean managerFlagBoolean;
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
    private Boolean sequentialMvtFlagBoolean;
    private Integer transferAllowanceFlag;
    private Boolean transferAllowanceFlagBoolean;
    private String transJoiningApprovedJobName;
    private String extraParams;
    private MovementTransaction movementTransaction;

    public MovementTransactionData() {
	movementTransaction = new MovementTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	movementTransaction.setId(id);
    }

    @Basic
    @Column(name = "MOVEMENT_TYPE_ID")
    @XmlTransient
    public Long getMovementTypeId() {
	return movementTypeId;
    }

    public void setMovementTypeId(Long movementTypeId) {
	this.movementTypeId = movementTypeId;
	movementTransaction.setMovementTypeId(movementTypeId);
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
	movementTransaction.setTransactionTypeId(transactionTypeId);
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_CODE")
    @XmlTransient
    public Integer getTransactionTypeCode() {
	return transactionTypeCode;
    }

    public void setTransactionTypeCode(Integer transactionTypeCode) {
	this.transactionTypeCode = transactionTypeCode;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_DESC")
    @XmlTransient
    public String getTransactionTypeDesc() {
	return transactionTypeDesc;
    }

    public void setTransactionTypeDesc(String transactionTypeDesc) {
	this.transactionTypeDesc = transactionTypeDesc;
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    @XmlTransient
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	movementTransaction.setEmployeeId(employeeId);
    }

    @Basic
    @Column(name = "EMPLOYEE_NAME")
    @XmlTransient
    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    @Basic
    @Column(name = "EMPLOYEE_RECRUITMENT_DATE")
    @XmlTransient
    public Date getEmployeeRecruitmentDate() {
	return employeeRecruitmentDate;
    }

    public void setEmployeeRecruitmentDate(Date employeeRecruitmentDate) {
	this.employeeRecruitmentDate = employeeRecruitmentDate;
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
    @Column(name = "EMP_PHYSICAL_REGION_ID")
    @XmlTransient
    public Long getEmpPhysicalRegionId() {
	return empPhysicalRegionId;
    }

    public void setEmpPhysicalRegionId(Long empPhysicalRegionId) {
	this.empPhysicalRegionId = empPhysicalRegionId;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    @XmlTransient
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	movementTransaction.setCategoryId(categoryId);
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
	movementTransaction.setUnitId(unitId);
    }

    @Basic
    @Column(name = "UNIT_FULL_NAME")
    @XmlTransient
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
	movementTransaction.setUnitFullName(unitFullName);
    }

    @Basic
    @Column(name = "JOB_ID")
    @XmlTransient
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	movementTransaction.setJobId(jobId);
    }

    @Basic
    @Column(name = "JOB_DESC")
    @XmlTransient
    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }

    @Basic
    @Column(name = "JOB_CODE")
    @XmlTransient
    public String getJobCode() {
	return jobCode;
    }

    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
	movementTransaction.setJobCode(jobCode);
    }

    @Basic
    @Column(name = "JOB_NAME")
    @XmlTransient
    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
	movementTransaction.setJobName(jobName);
    }

    @Basic
    @Column(name = "JOB_RANK_ID")
    @XmlTransient
    public Long getJobRankId() {
	return jobRankId;
    }

    public void setJobRankId(Long jobRankId) {
	this.jobRankId = jobRankId;
	movementTransaction.setJobRankId(jobRankId);
    }

    @Basic
    @Column(name = "JOB_RANK_DESC")
    @XmlTransient
    public String getJobRankDesc() {
	return jobRankDesc;
    }

    public void setJobRankDesc(String jobRankDesc) {
	this.jobRankDesc = jobRankDesc;
    }

    @Basic
    @Column(name = "JOB_MINOR_SPEC_ID")
    @XmlTransient
    public Long getJobMinorSpecId() {
	return jobMinorSpecId;
    }

    public void setJobMinorSpecId(Long jobMinorSpecId) {
	this.jobMinorSpecId = jobMinorSpecId;
	movementTransaction.setJobMinorSpecId(jobMinorSpecId);
    }

    @Basic
    @Column(name = "JOB_MINOR_SPEC_DESC")
    @XmlTransient
    public String getJobMinorSpecDesc() {
	return jobMinorSpecDesc;
    }

    public void setJobMinorSpecDesc(String jobMinorSpecDesc) {
	this.jobMinorSpecDesc = jobMinorSpecDesc;
	movementTransaction.setJobMinorSpecDesc(jobMinorSpecDesc);
    }

    @Basic
    @Column(name = "JOB_CLASSIFICATION_ID")
    @XmlTransient
    public Long getJobClassificationId() {
	return jobClassificationId;
    }

    public void setJobClassificationId(Long jobClassificationId) {
	this.jobClassificationId = jobClassificationId;
	movementTransaction.setJobClassificationId(jobClassificationId);
    }

    @Basic
    @Column(name = "FREEZE_JOB_ID")
    @XmlTransient
    public Long getFreezeJobId() {
	return freezeJobId;
    }

    public void setFreezeJobId(Long freezeJobId) {
	this.freezeJobId = freezeJobId;
	movementTransaction.setFreezeJobId(freezeJobId);
    }

    @Basic
    @Column(name = "FREEZE_JOB_CODE")
    @XmlTransient
    public String getFreezeJobCode() {
	return freezeJobCode;
    }

    public void setFreezeJobCode(String freezeJobCode) {
	this.freezeJobCode = freezeJobCode;
	movementTransaction.setFreezeJobCode(freezeJobCode);
    }

    @Basic
    @Column(name = "FREEZE_JOB_NAME")
    @XmlTransient
    public String getFreezeJobName() {
	return freezeJobName;
    }

    public void setFreezeJobName(String freezeJobName) {
	this.freezeJobName = freezeJobName;
	movementTransaction.setFreezeJobName(freezeJobName);
    }

    @Basic
    @Column(name = "FREEZE_JOB_RANK_ID")
    @XmlTransient
    public Long getFreezeJobRankId() {
	return freezeJobRankId;
    }

    public void setFreezeJobRankId(Long freezeJobRankId) {
	this.freezeJobRankId = freezeJobRankId;
	movementTransaction.setFreezeJobRankId(freezeJobRankId);
    }

    @Basic
    @Column(name = "FREEZE_JOB_UNIT_FULL_NAME")
    @XmlTransient
    public String getFreezeJobUnitFullName() {
	return freezeJobUnitFullName;
    }

    public void setFreezeJobUnitFullName(String freezeJobUnitFullName) {
	this.freezeJobUnitFullName = freezeJobUnitFullName;
	movementTransaction.setFreezeJobUnitFullName(freezeJobUnitFullName);
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

	movementTransaction.setManagerFlag(managerFlag);
    }

    @Basic
    @Column(name = "MANAGER_FLAG")
    @XmlTransient
    public Integer getManagerFlag() {
	return managerFlag;
    }

    public void setManagerFlag(Integer managerFlag) {
	this.managerFlag = managerFlag;

	if (this.managerFlag == null || this.managerFlag == FlagsEnum.OFF.getCode())
	    this.managerFlagBoolean = false;
	else
	    this.managerFlagBoolean = true;

	movementTransaction.setManagerFlag(managerFlag);
    }

    @Basic
    @Column(name = "EXECUTIN_DATE_FLAG")
    @XmlTransient
    public Integer getExecutionDateFlag() {
	return executionDateFlag;
    }

    public void setExecutionDateFlag(Integer executionDateFlag) {
	this.executionDateFlag = executionDateFlag;
	movementTransaction.setExecutionDateFlag(executionDateFlag);
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
	movementTransaction.setExecutionDate(executionDate);
    }

    @Transient
    public String getExecutionDateString() {
	return executionDateString;
    }

    public void setExecutionDateString(String executionDateString) {
	this.executionDateString = executionDateString;
	this.executionDate = HijriDateService.getHijriDate(executionDateString);
	movementTransaction.setExecutionDateString(executionDateString);
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
	movementTransaction.setEndDate(endDate);
    }

    @Transient
    @XmlTransient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
	movementTransaction.setEndDateString(endDateString);
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
	movementTransaction.setJoiningDate(joiningDate);
    }

    @Transient
    @XmlTransient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
	movementTransaction.setJoiningDateString(joiningDateString);
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
	movementTransaction.setReturnJoiningDate(returnJoiningDate);
    }

    @Transient
    @XmlTransient
    public String getReturnJoiningDateString() {
	return returnJoiningDateString;
    }

    public void setReturnJoiningDateString(String returnJoiningDateString) {
	this.returnJoiningDateString = returnJoiningDateString;
	this.returnJoiningDate = HijriDateService.getHijriDate(returnJoiningDateString);
	movementTransaction.setReturnJoiningDateString(returnJoiningDateString);
    }

    @Basic
    @Column(name = "PERIOD_DAYS")
    @XmlTransient
    public Integer getPeriodDays() {
	return periodDays;
    }

    public void setPeriodDays(Integer periodDays) {
	this.periodDays = periodDays;
	movementTransaction.setPeriodDays(periodDays);
    }

    @Basic
    @Column(name = "PERIOD_MONTHS")
    @XmlTransient
    public Integer getPeriodMonths() {
	return periodMonths;
    }

    public void setPeriodMonths(Integer periodMonths) {
	this.periodMonths = periodMonths;
	movementTransaction.setPeriodMonths(periodMonths);
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    @XmlTransient
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
	movementTransaction.setLocationFlag(locationFlag);
    }

    @Basic
    @Column(name = "LOCATION")
    @XmlTransient
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
	movementTransaction.setLocation(location);
    }

    @Basic
    @Column(name = "REASONS")
    @XmlTransient
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	movementTransaction.setReasons(reasons);
    }

    @Basic
    @Column(name = "REASON_TYPE")
    @XmlTransient
    public Integer getReasonType() {
	return reasonType;
    }

    public void setReasonType(Integer reasonType) {
	this.reasonType = reasonType;
	movementTransaction.setReasonType(reasonType);
    }

    @Basic
    @Column(name = "VACATION_GRANT_FLAG")
    @XmlTransient
    public Integer getVacationGrantFlag() {
	return vacationGrantFlag;
    }

    public void setVacationGrantFlag(Integer vacationGrantFlag) {
	this.vacationGrantFlag = vacationGrantFlag;
	movementTransaction.setVacationGrantFlag(vacationGrantFlag);
    }

    @Basic
    @Column(name = "MINISTRY_APPROVAL_NUMBER")
    @XmlTransient
    public String getMinistryApprovalNumber() {
	return ministryApprovalNumber;
    }

    public void setMinistryApprovalNumber(String ministryApprovalNumber) {
	this.ministryApprovalNumber = ministryApprovalNumber;
	movementTransaction.setMinistryApprovalNumber(ministryApprovalNumber);
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
	movementTransaction.setMinistryApprovalDate(ministryApprovalDate);
    }

    @Transient
    @XmlTransient
    public String getMinistryApprovalDateString() {
	return ministryApprovalDateString;
    }

    public void setMinistryApprovalDateString(String ministryApprovalDateString) {
	this.ministryApprovalDateString = ministryApprovalDateString;
	this.ministryApprovalDate = HijriDateService.getHijriDate(ministryApprovalDateString);
    }

    @Basic
    @Column(name = "REMARKS")
    @XmlTransient
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	movementTransaction.setRemarks(remarks);
    }

    @Basic
    @Column(name = "SPECIAL_REMARKS")
    @XmlTransient
    public String getSpecialRemarks() {
	return specialRemarks;
    }

    public void setSpecialRemarks(String specialRemarks) {
	this.specialRemarks = specialRemarks;
	movementTransaction.setSpecialRemarks(specialRemarks);
    }

    @Basic
    @Column(name = "SPECIAL_ATTACHMENTS")
    @XmlTransient
    public String getSpecialAttachments() {
	return specialAttachments;
    }

    public void setSpecialAttachments(String specialAttachments) {
	this.specialAttachments = specialAttachments;
	movementTransaction.setSpecialAttachments(specialAttachments);
    }

    @Basic
    @Column(name = "REPLACEMENT_TRANS_ID")
    @XmlTransient
    public Long getReplacementTransId() {
	return replacementTransId;
    }

    public void setReplacementTransId(Long replacementTransId) {
	this.replacementTransId = replacementTransId;
	movementTransaction.setReplacementTransId(replacementTransId);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    @XmlTransient
    public String getInternalCopies() {
	return InternalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	InternalCopies = internalCopies;
	movementTransaction.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    @XmlTransient
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	movementTransaction.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    @XmlTransient
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	movementTransaction.setAttachments(attachments);
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    @XmlTransient
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
	movementTransaction.setDirectedToJobName(directedToJobName);
    }

    @Basic
    @Column(name = "BASED_ON")
    @XmlTransient
    public String getBasedOn() {
	return basedOn;
    }

    public void setBasedOn(String basedOn) {
	this.basedOn = basedOn;
	movementTransaction.setBasedOn(basedOn);
    }

    @Basic
    @Column(name = "BASED_ON_ORDER_NUMBER")
    @XmlTransient
    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
	movementTransaction.setBasedOnOrderNumber(basedOnOrderNumber);
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
	movementTransaction.setBasedOnOrderDate(basedOnOrderDate);
    }

    @Transient
    @XmlTransient
    public String getBasedOnOrderDateString() {
	return basedOnOrderDateString;
    }

    public void setBasedOnOrderDateString(String basedOnOrderDateString) {
	this.basedOnOrderDateString = basedOnOrderDateString;
	this.basedOnOrderDate = HijriDateService.getHijriDate(basedOnOrderDateString);
    }

    @Basic
    @Column(name = "EFLAG")
    @XmlTransient
    public Integer getEtrFlag() {
	return etrFlag;
    }

    public void setEtrFlag(Integer etrflag) {
	this.etrFlag = etrflag;
	movementTransaction.setEtrFlag(etrflag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    @XmlTransient
    public Integer getMigrationFlag() {
	return migrationFlag;
    }

    public void setMigrationFlag(Integer migFlag) {
	this.migrationFlag = migFlag;
	movementTransaction.setMigrationFlag(migFlag);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	movementTransaction.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
	movementTransaction.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	movementTransaction.setDecisionDateString(decisionDateString);
    }

    @Basic
    @Column(name = "APPROVED_ID")
    @XmlTransient
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
	movementTransaction.setApprovedId(approvedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    @XmlTransient
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	movementTransaction.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    @XmlTransient
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	movementTransaction.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    @XmlTransient
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	movementTransaction.setDecisionRegionId(decisionRegionId);
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_ID")
    @XmlTransient
    public Long getTransEmpRankId() {
	return transEmpRankId;
    }

    public void setTransEmpRankId(Long transEmpRankId) {
	this.transEmpRankId = transEmpRankId;
	movementTransaction.setTransEmpRankId(transEmpRankId);
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    @XmlTransient
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
	movementTransaction.setTransEmpRankDesc(transEmpRankDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_MINOR_SPEC_DESC")
    @XmlTransient
    public String getTransEmpJobMinorSpecDesc() {
	return transEmpJobMinorSpecDesc;
    }

    public void setTransEmpJobMinorSpecDesc(String transEmpJobMinorSpecDesc) {
	this.transEmpJobMinorSpecDesc = transEmpJobMinorSpecDesc;
	movementTransaction.setTransEmpJobMinorSpecDesc(transEmpJobMinorSpecDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    @XmlTransient
    public String getTransEmpJobCode() {
	return transEmpJobCode;
    }

    public void setTransEmpJobCode(String transEmpJobCode) {
	this.transEmpJobCode = transEmpJobCode;
	movementTransaction.setTransEmpJobCode(transEmpJobCode);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CLASS_JOB_CODE")
    @XmlTransient
    public String getTransEmpJobClassJobCode() {
	return transEmpJobClassJobCode;
    }

    public void setTransEmpJobClassJobCode(String transEmpJobClassJobCode) {
	this.transEmpJobClassJobCode = transEmpJobClassJobCode;
	movementTransaction.setTransEmpJobClassJobCode(transEmpJobClassJobCode);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CLASS_JOB_DESC")
    @XmlTransient
    public String getTransEmpJobClassJobDesc() {
	return transEmpJobClassJobDesc;
    }

    public void setTransEmpJobClassJobDesc(String transEmpJobClassJobDesc) {
	this.transEmpJobClassJobDesc = transEmpJobClassJobDesc;
	movementTransaction.setTransEmpJobClassJobDesc(transEmpJobClassJobDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_NAME")
    @XmlTransient
    public String getTransEmpJobName() {
	return transEmpJobName;
    }

    public void setTransEmpJobName(String transEmpJobName) {
	this.transEmpJobName = transEmpJobName;
	movementTransaction.setTransEmpJobName(transEmpJobName);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_RANK_DESC")
    @XmlTransient
    public String getTransEmpJobRankDesc() {
	return transEmpJobRankDesc;
    }

    public void setTransEmpJobRankDesc(String transEmpJobRankDesc) {
	this.transEmpJobRankDesc = transEmpJobRankDesc;
	movementTransaction.setTransEmpJobRankDesc(transEmpJobRankDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_FULL_NAME")
    @XmlTransient
    public String getTransEmpUnitFullName() {
	return transEmpUnitFullName;
    }

    public void setTransEmpUnitFullName(String transEmpUnitFullName) {
	this.transEmpUnitFullName = transEmpUnitFullName;
	movementTransaction.setTransEmpUnitFullName(transEmpUnitFullName);
    }

    @Basic
    @Column(name = "TRANS_APPROVED_RANK_DESC")
    @XmlTransient
    public String getTransApprovedRankDesc() {
	return transApprovedRankDesc;
    }

    public void setTransApprovedRankDesc(String transApprovedRankDesc) {
	this.transApprovedRankDesc = transApprovedRankDesc;
	movementTransaction.setTransApprovedRankDesc(transApprovedRankDesc);
    }

    @Basic
    @Column(name = "TRANS_APPROVED_JOB_NAME")
    @XmlTransient
    public String getTransApprovedJobName() {
	return transApprovedJobName;
    }

    public void setTransApprovedJobName(String transApprovedJobName) {
	this.transApprovedJobName = transApprovedJobName;
	movementTransaction.setTransApprovedJobName(transApprovedJobName);
    }

    @Basic
    @Column(name = "BASED_ON_DECISION_NUMBER")
    @XmlTransient
    public String getBasedOnDecisionNumber() {
	return basedOnDecisionNumber;
    }

    public void setBasedOnDecisionNumber(String basedOnDecisionNumber) {
	this.basedOnDecisionNumber = basedOnDecisionNumber;
	movementTransaction.setBasedOnDecisionNumber(basedOnDecisionNumber);
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
	movementTransaction.setBasedOnDecisionDate(basedOnDecisionDate);
    }

    @Transient
    @XmlTransient
    public String getBasedOnDecisionDateString() {
	return basedOnDecisionDateString;
    }

    public void setBasedOnDecisionDateString(String basedOnDecisionDateString) {
	this.basedOnDecisionDateString = basedOnDecisionDateString;
	this.basedOnDecisionDate = HijriDateService.getHijriDate(basedOnDecisionDateString);
    }

    @Basic
    @Column(name = "SUCCESSOR_DECISION_EFFECT_FLAG")
    @XmlTransient
    public Integer getSuccessorDecisionEffectFlag() {
	return successorDecisionEffectFlag;
    }

    public void setSuccessorDecisionEffectFlag(Integer successorDecisionEffectFlag) {
	this.successorDecisionEffectFlag = successorDecisionEffectFlag;
	movementTransaction.setSuccessorDecisionEffectFlag(successorDecisionEffectFlag);
    }

    @Basic
    @Column(name = "EFFECT_FLAG")
    @XmlTransient
    public Integer getEffectFlag() {
	return effectFlag;
    }

    public void setEffectFlag(Integer effectFlag) {
	this.effectFlag = effectFlag;
	movementTransaction.setEffectFlag(effectFlag);
    }

    @Basic
    @Column(name = "SECURITY_UNIT_FLAG")
    @XmlTransient
    public Integer getSecurityUnitFlag() {
	return securityUnitFlag;
    }

    public void setSecurityUnitFlag(Integer securityUnitFlag) {
	this.securityUnitFlag = securityUnitFlag;
	movementTransaction.setSecurityUnitFlag(securityUnitFlag);
    }

    @Basic
    @Column(name = "VERBAL_ORDER_FLAG")
    @XmlTransient
    public Integer getVerbalOrderFlag() {
	return verbalOrderFlag;
    }

    public void setVerbalOrderFlag(Integer verbalOrderFlag) {
	this.verbalOrderFlag = verbalOrderFlag;
	movementTransaction.setVerbalOrderFlag(verbalOrderFlag);
    }

    @Basic
    @Column(name = "REQUEST_TRANSACTION_FLAG")
    @XmlTransient
    public Integer getRequestTransactionFlag() {
	return requestTransactionFlag;
    }

    public void setRequestTransactionFlag(Integer requestTransactionFlag) {
	this.requestTransactionFlag = requestTransactionFlag;
	movementTransaction.setRequestTransactionFlag(requestTransactionFlag);
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

	movementTransaction.setSequentialMvtFlag(sequentialMvtFlag);
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

	movementTransaction.setSequentialMvtFlag(sequentialMvtFlag);
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

	movementTransaction.setTransferAllowanceFlag(transferAllowanceFlag);
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

	movementTransaction.setTransferAllowanceFlag(transferAllowanceFlag);
    }

    @Basic
    @Column(name = "TRANS_JOIN_APPROVED_JOB_NAME")
    @XmlTransient
    public String getTransJoiningApprovedJobName() {
	return transJoiningApprovedJobName;
    }

    public void setTransJoiningApprovedJobName(String transJoiningApprovedJobName) {
	this.transJoiningApprovedJobName = transJoiningApprovedJobName;
	movementTransaction.setTransJoiningApprovedJobName(transJoiningApprovedJobName);
    }

    @Transient
    @XmlTransient
    public String getExtraParams() {
	return extraParams;
    }

    public void setExtraParams(String extraParams) {
	this.extraParams = extraParams;
    }

    @Transient
    @XmlTransient
    public MovementTransaction getMovementTransaction() {
	return movementTransaction;
    }

    public void setMovementTransaction(MovementTransaction movementTransaction) {
	this.movementTransaction = movementTransaction;
    }
}