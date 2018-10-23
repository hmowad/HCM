package com.code.dal.orm.log;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "HCM_EMPLOYEES_LOG")
public class EmployeeLog extends BaseEntity {
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

    @SequenceGenerator(name = "HCMLogSeq",
	    sequenceName = "HCM_LOG_SEQ",
	    allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "HCMLogSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
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
    @Column(name = "PHYSICAL_UNIT_ID")
    public Long getPhysicalUnitId() {
	return physicalUnitId;
    }

    public void setPhysicalUnitId(Long physicalUnitId) {
	this.physicalUnitId = physicalUnitId;
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
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
    }

    @Basic
    @Column(name = "SALARY_RANK_ID")
    public Long getSalaryRankId() {
	return salaryRankId;
    }

    public void setSalaryRankId(Long salaryRankId) {
	this.salaryRankId = salaryRankId;
    }

    @Basic
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
    }

    @Basic
    @Column(name = "SOCIAL_STATUS")
    public Integer getSocialStatus() {
	return socialStatus;
    }

    public void setSocialStatus(Integer socialStatus) {
	this.socialStatus = socialStatus;
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
    }

    @Basic
    @Column(name = "EFFECTIVE_GREG_DATE")
    public Date getEffectiveGregDate() {
	return effectiveGregDate;
    }

    public void setEffectiveGregDate(Date effectiveGregDate) {
	this.effectiveGregDate = effectiveGregDate;
    }

    @Basic
    @Column(name = "EFFECTIVE_HIJRI_DATE")
    public Date getEffectiveHijriDate() {
	return effectiveHijriDate;
    }

    public void setEffectiveHijriDate(Date effectiveHijriDate) {
	this.effectiveHijriDate = effectiveHijriDate;
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
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }
}
