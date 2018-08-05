package com.code.dal.orm.security;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;

@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "sec_decisionPrivilegeData_searchDecisionPrivilegeData",
		query = " select d from DecisionPrivilegeData d" +
			" where ((:P_PROCESS_GROUP_ID = -1 OR d.processGroupId = :P_PROCESS_GROUP_ID)"
			+ " AND (:P_CATEGORY_ID = -1 OR d.beneficiaryCategoryId = :P_CATEGORY_ID))"
	),
	@NamedQuery(name = "sec_decisionPrivilegeData_countDecisionPrivilegeData",
		query = "select count(d) from DecisionPrivilegeData d where  " +
			"(:P_EXCLUDED_ID = -1 or d.id <> :P_EXCLUDED_ID)" +
			"and ((:P_EMP_ID = -1 and d.empId is null) or d.empId = :P_EMP_ID)" +
			"and (d.processGroupId = :P_PROCESS_GROUP_ID)" +
			"and (d.beneficiaryCategoryId = :P_BENEFICIARY_CATEGORY_ID)" +
			"and ((:P_BENEFICIARY_REGION_ID = -1 and d.beneficiaryRegionId is null) or d.beneficiaryRegionId = :P_BENEFICIARY_REGION_ID)" +
			"and ((:P_BENEFICIARY_UNIT_ID = -1 and d.beneficiaryUnitId is null) or d.beneficiaryUnitId = :P_BENEFICIARY_UNIT_ID)" +
			"and (d.unitId = :P_UNIT_ID)" +
			"and ((:P_JOB_ID = -1 and d.jobId is null) or d.jobId = :P_JOB_ID)"
	)
})
@Entity
@Table(name = "ETR_VW_DECISIONS_PRIVILEGES")
public class DecisionPrivilegeData extends BaseEntity implements Serializable {
    private Long id;
    private Long empId;
    private String empName;
    private Long processGroupId;
    private String ProcessGroupName;
    private Long beneficiaryCategoryId;
    private String beneficiaryCategoryDesc;
    private Long beneficiaryRegionId;
    private String beneficiaryRegionDesc;
    private Long beneficiaryUnitId;
    private String beneficiaryUnitFullName;
    private Long unitId;
    private String unitFullName;
    private Long jobId;
    private String jobName;

    private DecisionPrivilege decisionPrivilege;

    public DecisionPrivilegeData() {
	decisionPrivilege = new DecisionPrivilege();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	decisionPrivilege.setId(id);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	decisionPrivilege.setEmpId(empId);
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
    @Column(name = "BENEFICAIRY_CATEGORY_ID")
    public Long getBeneficiaryCategoryId() {
	return beneficiaryCategoryId;
    }

    public void setBeneficiaryCategoryId(Long beneficiaryCategoryId) {
	this.beneficiaryCategoryId = beneficiaryCategoryId;
	decisionPrivilege.setBeneficiaryCategoryId(beneficiaryCategoryId);
    }

    @Basic
    @Column(name = "BENEFICAIRY_CATEGORY_DESC")
    public String getBeneficiaryCategoryDesc() {
	return beneficiaryCategoryDesc;
    }

    public void setBeneficiaryCategoryDesc(String beneficiaryCategoryDesc) {
	this.beneficiaryCategoryDesc = beneficiaryCategoryDesc;
    }

    @Basic
    @Column(name = "PROCESS_GROUP_ID")
    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
	decisionPrivilege.setProcessGroupId(processGroupId);
    }

    @Basic
    @Column(name = "PROCESS_GROUP_NAME")
    public String getProcessGroupName() {
	return ProcessGroupName;
    }

    public void setProcessGroupName(String processGroupName) {
	ProcessGroupName = processGroupName;
    }

    @Basic
    @Column(name = "BENEFICAIRY_REGION_ID")
    public Long getBeneficiaryRegionId() {
	return beneficiaryRegionId;
    }

    public void setBeneficiaryRegionId(Long beneficiaryRegionId) {
	this.beneficiaryRegionId = beneficiaryRegionId;
	decisionPrivilege.setBeneficiaryRegionId(beneficiaryRegionId);
    }

    @Basic
    @Column(name = "BENEFICAIRY_REGION_DESC")
    public String getBeneficiaryRegionDesc() {
	return beneficiaryRegionDesc;
    }

    public void setBeneficiaryRegionDesc(String beneficiaryRegionDesc) {
	this.beneficiaryRegionDesc = beneficiaryRegionDesc;
    }

    @Basic
    @Column(name = "BENEFICAIRY_UNIT_ID")
    public Long getBeneficiaryUnitId() {
	return beneficiaryUnitId;
    }

    public void setBeneficiaryUnitId(Long beneficiaryUnitId) {
	this.beneficiaryUnitId = beneficiaryUnitId;
	decisionPrivilege.setBeneficiaryUnitId(beneficiaryUnitId);
    }

    @Basic
    @Column(name = "BENEFICAIRY_UNIT_FULL_NAME")
    public String getBeneficiaryUnitFullName() {
	return beneficiaryUnitFullName;
    }

    public void setBeneficiaryUnitFullName(String beneficiaryUnitFullName) {
	this.beneficiaryUnitFullName = beneficiaryUnitFullName;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
	decisionPrivilege.setUnitId(unitId);
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
	decisionPrivilege.setJobId(jobId);
    }

    @Basic
    @Column(name = "JOB_NAME")
    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
    }

    @Transient
    @XmlTransient
    public DecisionPrivilege getDecisionPrivilege() {
	return decisionPrivilege;
    }

    public void setDecisionPrivilege(DecisionPrivilege decisionPrivilege) {
	this.decisionPrivilege = decisionPrivilege;
    }
}
