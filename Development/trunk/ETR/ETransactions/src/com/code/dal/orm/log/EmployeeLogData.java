package com.code.dal.orm.log;

import java.util.Date;

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
	@NamedQuery(name = "hcm_employeeLogData_searchEmployeesLog",
		query = "select e from EmployeeLogData e " +
			" where (:P_ID = -1 or e.id = :P_ID) " +
			" and (:P_EMP_ID = -1 or e.empId = :P_EMP_ID) " +
			" and (:P_RANK_ID = -1 or e.rankId = :P_RANK_ID) " +
			" and (:P_JOB_ID = -1 or e.jobId = :P_JOB_ID) " +
			" and (:P_BASIC_JOB_NAME_ID = -1 or e.basicJobNameId = :P_BASIC_JOB_NAME_ID) " +
			" and (:P_PHYSICAL_UNIT_ID = -1 or e.physicalUnitId = :P_PHYSICAL_UNIT_ID) " +
			" and (:P_DEGREE_ID = -1 or e.degreeId = :P_DEGREE_ID) " +
			" and (:P_SALARY_RANK_ID = -1 or e.salaryRankId = :P_SALARY_RANK_ID) " +
			" and (:P_SOCIAL_STATUS = -1 or e.socialStatus = :P_SOCIAL_STATUS) " +
			" and (:P_RANK_TITLE_ID = -1 or e.rankTitleId = :P_RANK_TITLE_ID) " +
			" and (:P_GENERAL_SPECIALIZATION = -1 or e.generalSpecialization = :P_GENERAL_SPECIALIZATION) " +
			" and (:P_EFFECTIVE_HIJRI_DATE_FLAG = -1 or e.effectiveHijriDate = (TO_DATE(:P_EFFECTIVE_HIJRI_DATE, 'MI/MM/YYYY')))" +
			" and (:P_EFFECTIVE_GREG_DATE_FLAG = -1 or e.effectiveGregDate = (TO_DATE(:P_EFFECTIVE_GREG_DATE, 'MI/MM/YYYY')))" +
			" and (:P_DECISION_NUMBER = '-1' or e.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_DECISION_DATE_FLAG = -1 or e.decisionDate = (TO_DATE(:P_DECISION_DATE, 'MI/MM/YYYY')))" +
			" order by e.empId")

})
@Entity
@Table(name = "HCM_VW_EMPLOYEES_LOG")
public class EmployeeLogData extends BaseEntity {
    private Long id;
    private Long empId;
    private Long jobId;
    private Long basicJobNameId;
    private Long physicalUnitId;
    private Long rankId;
    private Long rankTitleId;
    private Long salaryRankId;
    private Long degreeId;
    private Integer socialStatus;
    private Integer generalSpecialization;
    private Date effectiveGregDate;
    private Date effectiveHijriDate;
    private String decisionNumber;
    private Date decisionDate;
    private Long insertionTime;
    private Long officialUnitId;
    private Long medStaffRankId;
    private Long medStaffLevelId;
    private Long medStaffDegreeId;
    private EmployeeLog employeelog;

    public EmployeeLogData() {
	employeelog = new EmployeeLog();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	employeelog.setId(id);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	employeelog.setEmpId(empId);
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	employeelog.setJobId(jobId);
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME_ID")
    public Long getBasicJobNameId() {
	return basicJobNameId;
    }

    public void setBasicJobNameId(Long basicJobNameId) {
	this.basicJobNameId = basicJobNameId;
	employeelog.setBasicJobNameId(basicJobNameId);
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_ID")
    public Long getPhysicalUnitId() {
	return physicalUnitId;
    }

    public void setPhysicalUnitId(Long physicalUnitId) {
	this.physicalUnitId = physicalUnitId;
	employeelog.setPhysicalUnitId(physicalUnitId);
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	employeelog.setRankId(rankId);
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
	employeelog.setRankTitleId(rankTitleId);
    }

    @Basic
    @Column(name = "SALARY_RANK_ID")
    public Long getSalaryRankId() {
	return salaryRankId;
    }

    public void setSalaryRankId(Long salaryRankId) {
	this.salaryRankId = salaryRankId;
	employeelog.setSalaryRankId(salaryRankId);
    }

    @Basic
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
	employeelog.setDegreeId(degreeId);
    }

    @Basic
    @Column(name = "SOCIAL_STATUS")
    public Integer getSocialStatus() {
	return socialStatus;
    }

    public void setSocialStatus(Integer socialStatus) {
	this.socialStatus = socialStatus;
	employeelog.setSocialStatus(socialStatus);
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
	employeelog.setGeneralSpecialization(generalSpecialization);
    }

    @Basic
    @Column(name = "EFFECTIVE_GREG_DATE")
    public Date getEffectiveGregDate() {
	return effectiveGregDate;
    }

    public void setEffectiveGregDate(Date effectiveGregDate) {
	this.effectiveGregDate = effectiveGregDate;
	employeelog.setEffectiveGregDate(effectiveGregDate);
    }

    @Basic
    @Column(name = "EFFECTIVE_HIJRI_DATE")
    public Date getEffectiveHijriDate() {
	return effectiveHijriDate;
    }

    public void setEffectiveHijriDate(Date effectiveHijriDate) {
	this.effectiveHijriDate = effectiveHijriDate;
	employeelog.setEffectiveHijriDate(effectiveHijriDate);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	employeelog.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	employeelog.setDecisionDate(decisionDate);
    }

    @Basic
    @Column(name = "INSERTION_TIME")
    public Long getInsertionTime() {
	return insertionTime;
    }

    public void setInsertionTime(Long insertionTime) {
	this.insertionTime = insertionTime;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_ID")
    public Long getOfficialUnitId() {
	return officialUnitId;
    }

    public void setOfficialUnitId(Long officialUnitId) {
	this.officialUnitId = officialUnitId;
    }

    @Basic
    @Column(name = "MED_STAFF_RANK_ID")
    public Long getMedStaffRankId() {
	return medStaffRankId;
    }

    public void setMedStaffRankId(Long medStaffRankId) {
	this.medStaffRankId = medStaffRankId;
    }

    @Basic
    @Column(name = "MED_STAFF_LEVEL_ID")
    public Long getMedStaffLevelId() {
	return medStaffLevelId;
    }

    public void setMedStaffLevelId(Long medStaffLevelId) {
	this.medStaffLevelId = medStaffLevelId;
    }

    @Basic
    @Column(name = "MED_STAFF_DEGREE_ID")
    public Long getMedStaffDegreeId() {
	return medStaffDegreeId;
    }

    public void setMedStaffDegreeId(Long medStaffDegreeId) {
	this.medStaffDegreeId = medStaffDegreeId;
    }

    @Transient
    public EmployeeLog getEmployeelog() {
	return employeelog;
    }

    public void setEmployeelog(EmployeeLog employeelog) {
	this.employeelog = employeelog;
    }
}
