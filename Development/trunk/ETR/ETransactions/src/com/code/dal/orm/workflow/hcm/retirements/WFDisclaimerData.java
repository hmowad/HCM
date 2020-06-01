package com.code.dal.orm.workflow.hcm.retirements;

import java.math.BigDecimal;
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

	@NamedQuery(name = "wf_disclaimerData_getWFDisclaimerData",
		query = "select d from WFDisclaimerData d "
			+ " where "
			+ " (:P_INSTANCE_ID_FLAG = -1 or d.instanceId in (:P_INSTANCE_ID))"
			+ " AND (:P_TERMINATION_TRANS_ID = -1 or d.terminationTransactionId = :P_TERMINATION_TRANS_ID)"),

	@NamedQuery(name = "wf_disclaimerData_getRunningDisclaimerRequests",
		query = "select d from WFDisclaimerData d , WFInstance i "
			+ " where d.instanceId = i.id "
			+ " and i.status = 1 "
			+ " and d.empId in ( :P_EMPS_IDS )  "
			+ " and (:P_EXCLUDED_INSTANCE_ID = -1 or d.instanceId <> :P_EXCLUDED_INSTANCE_ID)"),

	@NamedQuery(name = "wf_disclaimerData_getRetirementsTasks",
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
			"   and t.assigneeWfRole   = :P_ASSIGNEE_WF_ROLE " +
			"   order by t.taskId ")
})

@Entity
@Table(name = "ETR_VW_WF_DISCLAIMERS")
public class WFDisclaimerData extends BaseEntity {

    private Long instanceId;
    private Long empId;
    private Long empCategoryId;
    private Long empPhysicalRegionId;
    private Long terminationTransactionId;

    private String empRankDesc;
    private String empJobDesc;
    private String empOfficialUnitFullName;
    private Long empOfficialUnitId;
    private String terminationReason;
    private Date serviceTerminationDate;
    private String serviceTerminationDateString;
    private Integer terminationMigFlag;

    private Double basicAmount;
    private Double allowanceAmount;
    private String deductionReason;
    private Double realEstateFundAmount;
    private Double creditBankAmount;
    private Double otherAmount;
    private BigDecimal totalDueAmount;
    private BigDecimal totalGovernmentalDueAmount;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String sentBackUnitsString;

    private WFDisclaimer WFDisclaimer;

    public WFDisclaimerData() {
	WFDisclaimer = new WFDisclaimer();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	WFDisclaimer.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	WFDisclaimer.setEmpId(empId);
    }

    @Basic
    @Column(name = "EMP_CATEGORY_ID")
    public Long getEmpCategoryId() {
	return empCategoryId;
    }

    public void setEmpCategoryId(Long empCategoryId) {
	this.empCategoryId = empCategoryId;
    }

    @Basic
    @Column(name = "EMP_PHYSICAL_REGION_ID")
    public Long getEmpPhysicalRegionId() {
	return empPhysicalRegionId;
    }

    public void setEmpPhysicalRegionId(Long empPhysicalRegionId) {
	this.empPhysicalRegionId = empPhysicalRegionId;
    }

    @Basic
    @Column(name = "TERMINATION_TRANSACTION_ID")
    public Long getTerminationTransactionId() {
	return terminationTransactionId;
    }

    public void setTerminationTransactionId(Long terminationTransactionId) {
	this.terminationTransactionId = terminationTransactionId;
	WFDisclaimer.setTerminationTransactionId(terminationTransactionId);
    }

    @Basic
    @Column(name = "EMP_RANK_DESC")
    public String getEmpRankDesc() {
	return empRankDesc;
    }

    public void setEmpRankDesc(String empRankDesc) {
	this.empRankDesc = empRankDesc;
    }

    @Basic
    @Column(name = "EMP_JOB_DESC")
    public String getEmpJobDesc() {
	return empJobDesc;
    }

    public void setEmpJobDesc(String empJobDesc) {
	this.empJobDesc = empJobDesc;
    }

    @Basic
    @Column(name = "EMP_OFFICIAL_UNIT_FULL_NAME")
    public String getEmpOfficialUnitFullName() {
	return empOfficialUnitFullName;
    }

    public void setEmpOfficialUnitFullName(String empOfficialUnitFullName) {
	this.empOfficialUnitFullName = empOfficialUnitFullName;
    }

    @Basic
    @Column(name = "EMP_OFFICIAL_UNIT_ID")
    public Long getEmpOfficialUnitId() {
	return empOfficialUnitId;
    }

    public void setEmpOfficialUnitId(Long empOfficialUnitId) {
	this.empOfficialUnitId = empOfficialUnitId;
    }

    @Basic
    @Column(name = "TERMINATION_REASON")
    public String getTerminationReason() {
	return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
	this.terminationReason = terminationReason;
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
    }

    @Transient
    public String getServiceTerminationDateString() {
	return serviceTerminationDateString;
    }

    public void setServiceTerminationDateString(String serviceTerminationDateString) {
	this.serviceTerminationDateString = serviceTerminationDateString;
	this.serviceTerminationDate = HijriDateService.getHijriDate(serviceTerminationDateString);
    }

    @Basic
    @Column(name = "TERMINATION_MIG_FLAG")
    public Integer getTerminationMigFlag() {
	return terminationMigFlag;
    }

    public void setTerminationMigFlag(Integer terminationMigFlag) {
	this.terminationMigFlag = terminationMigFlag;
    }

    @Basic
    @Column(name = "BASIC_AMOUNT")
    public Double getBasicAmount() {
	return basicAmount;
    }

    public void setBasicAmount(Double basicAmount) {
	this.basicAmount = basicAmount;
	WFDisclaimer.setBasicAmount(basicAmount);
    }

    @Basic
    @Column(name = "ALLOWANCE_AMOUNT")
    public Double getAllowanceAmount() {
	return allowanceAmount;
    }

    public void setAllowanceAmount(Double allowanceAmount) {
	this.allowanceAmount = allowanceAmount;
	WFDisclaimer.setAllowanceAmount(allowanceAmount);
    }

    @Basic
    @Column(name = "DEDUCTION_REASON")
    public String getDeductionReason() {
	return deductionReason;
    }

    public void setDeductionReason(String deductionReason) {
	this.deductionReason = deductionReason;
	WFDisclaimer.setDeductionReason(deductionReason);
    }

    @Basic
    @Column(name = "REAL_ESTATE_FUND_AMOUNT")
    public Double getRealEstateFundAmount() {
	return realEstateFundAmount;
    }

    public void setRealEstateFundAmount(Double realEstateFundAmount) {
	this.realEstateFundAmount = realEstateFundAmount;
	WFDisclaimer.setRealEstateFundAmount(realEstateFundAmount);
    }

    @Basic
    @Column(name = "CREDIT_BANK_AMOUNT")
    public Double getCreditBankAmount() {
	return creditBankAmount;
    }

    public void setCreditBankAmount(Double creditBankAmount) {
	this.creditBankAmount = creditBankAmount;
	WFDisclaimer.setCreditBankAmount(creditBankAmount);
    }

    @Basic
    @Column(name = "OTHER_AMOUNT")
    public Double getOtherAmount() {
	return otherAmount;
    }

    public void setOtherAmount(Double otherAmount) {
	this.otherAmount = otherAmount;
	WFDisclaimer.setOtherAmount(otherAmount);
    }

    @Transient
    public BigDecimal getTotalDueAmount() {
	return totalDueAmount;
    }

    public void setTotalDueAmount(BigDecimal totalDueAmount) {
	this.totalDueAmount = totalDueAmount;
    }

    @Transient
    public BigDecimal getTotalGovernmentalDueAmount() {
	return this.totalGovernmentalDueAmount;
    }

    public void setTotalGovernmentalDueAmount(BigDecimal totalGovernmentalDueAmount) {
	this.totalGovernmentalDueAmount = totalGovernmentalDueAmount;
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	WFDisclaimer.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	WFDisclaimer.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "SENT_BACK_UNITS_STRING")
    public String getSentBackUnitsString() {
	return sentBackUnitsString;
    }

    public void setSentBackUnitsString(String sentBackUnitsString) {
	this.sentBackUnitsString = sentBackUnitsString;
	this.WFDisclaimer.setSentBackUnitsString(sentBackUnitsString);
    }

    @Transient
    public WFDisclaimer getWFDisclaimer() {
	return WFDisclaimer;
    }

    public void setWFDisclaimer(WFDisclaimer wfDisclaimer) {
	WFDisclaimer = wfDisclaimer;
    }

}
