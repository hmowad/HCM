package com.code.dal.orm.hcm.raises;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@Entity
@Table(name = "HCM_VW_RAISES_EMPLOYEES")
public class RaiseEmployeeData extends BaseEntity {
    private Long id;
    private Long raiseId;
    private Long empId;
    private String exclusionReason;
    private String empJobName;
    private Long empJobId;
    private String empName;
    private Integer raiseType;
    private Date raiseExecutionDate;
    private String raiseExecutionDateString;
    private String raiseDecisionNumber;
    private Date raiseDecisionDate;
    private String raiseDecisionDateString;
    private String raiseCategoryDesc;
    private Long raiseCategoryId;
    private Long empMilitaryNumber;
    private Long empNewDegreeId;
    private String empNewDegreeDesc;
    private Long empPhysicalUnitId;
    private String empPhysicalUnitName;
    private Long empDegreeId;
    private String empDegreeDesc;
    private Long empRankId;
    private String empRankDesc;
    private RaiseEmployee raiseEmployee;

    public RaiseEmployeeData() {
	raiseEmployee = new RaiseEmployee();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	raiseEmployee.setId(id);
    }

    @Basic
    @Column(name = "RAISE_ID")
    public Long getRaiseId() {
	return raiseId;
    }

    public void setRaiseId(Long raiseId) {
	this.raiseId = raiseId;
	raiseEmployee.setRaiseId(raiseId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	raiseEmployee.setEmpId(empId);
    }

    @Basic
    @Column(name = "EXCLUSION_REASON")
    public String getExclusionReason() {
	return exclusionReason;
    }

    public void setExclusionReason(String exclusionReason) {
	this.exclusionReason = exclusionReason;
	raiseEmployee.setExclusionReason(exclusionReason);
    }

    @Basic
    @Column(name = "EMP_JOB_NAME")
    public String getEmpJobName() {
	return empJobName;
    }

    public void setEmpJobName(String empJobName) {
	this.empJobName = empJobName;
    }

    @Basic
    @Column(name = "EMP_JOB_ID")
    public Long getEmpJobId() {
	return empJobId;
    }

    public void setEmpJobId(Long empJobId) {
	this.empJobId = empJobId;
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
    @Column(name = "RAISE_TYPE")
    public Integer getRaiseType() {
	return raiseType;
    }

    public void setRaiseType(Integer raiseType) {
	this.raiseType = raiseType;
    }

    @Basic
    @Column(name = "RAISE_EXECUTION_DATE")
    public Date getRaiseExecutionDate() {
	return raiseExecutionDate;
    }

    public void setRaiseExecutionDate(Date raiseExecutionDate) {
	this.raiseExecutionDate = raiseExecutionDate;
	this.raiseExecutionDateString = HijriDateService.getHijriDateString(raiseExecutionDate);
    }

    @Transient
    public String getRaiseExecutionDateString() {
	return raiseExecutionDateString;
    }

    public void setRaiseExecutionDateString(String raiseExecutionDateString) {
	this.raiseExecutionDateString = raiseExecutionDateString;
	this.raiseExecutionDate = HijriDateService.getHijriDate(raiseExecutionDateString);
    }

    @Basic
    @Column(name = "RAISE_DECISION_NUMBER")
    public String getRaiseDecisionNumber() {
	return raiseDecisionNumber;
    }

    public void setRaiseDecisionNumber(String raiseDecisionNumber) {
	this.raiseDecisionNumber = raiseDecisionNumber;
    }

    @Basic
    @Column(name = "RAISE_DECISION_DATE")
    public Date getRaiseDecisionDate() {
	return raiseDecisionDate;
    }

    public void setRaiseDecisionDate(Date raiseDecisionDate) {
	this.raiseDecisionDate = raiseDecisionDate;
	this.raiseDecisionDateString = HijriDateService.getHijriDateString(raiseDecisionDate);
    }

    @Transient
    public String getRaiseDecisionDateString() {
	return raiseDecisionDateString;
    }

    public void setRaiseDecisionDateString(String raiseDecisionDateString) {
	this.raiseDecisionDateString = raiseDecisionDateString;
	this.raiseDecisionDate = HijriDateService.getHijriDate(raiseDecisionDateString);
    }

    @Basic
    @Column(name = "RAISE_CATEGORY_DESC")
    public String getRaiseCategoryDesc() {
	return raiseCategoryDesc;
    }

    public void setRaiseCategoryDesc(String raiseCategoryDesc) {
	this.raiseCategoryDesc = raiseCategoryDesc;
    }

    @Basic
    @Column(name = "RAISE_CATEGORY_ID")
    public Long getRaiseCategoryId() {
	return raiseCategoryId;
    }

    public void setRaiseCategoryId(Long raiseCategoryId) {
	this.raiseCategoryId = raiseCategoryId;
    }

    @Basic
    @Column(name = "EMP_MILITARY_NUMBER")
    public Long getEmpMilitaryNumber() {
	return empMilitaryNumber;
    }

    public void setEmpMilitaryNumber(Long empMilitaryNumber) {
	this.empMilitaryNumber = empMilitaryNumber;
    }

    @Basic
    @Column(name = "EMP_NEW_DEGREE_ID")
    public Long getEmpNewDegreeId() {
	return empNewDegreeId;
    }

    public void setEmpNewDegreeId(Long empNewDegreeId) {
	this.empNewDegreeId = empNewDegreeId;
	raiseEmployee.setNewDegreeId(empNewDegreeId);
    }

    @Basic
    @Column(name = "EMP_NEW_DEGREE_DESC")
    public String getEmpNewDegreeDesc() {
	return empNewDegreeDesc;
    }

    public void setEmpNewDegreeDesc(String empNewDegreeDesc) {
	this.empNewDegreeDesc = empNewDegreeDesc;
    }

    @Basic
    @Column(name = "EMP_PHYSICAL_UNIT_ID")
    public Long getEmpPhysicalUnitId() {
	return empPhysicalUnitId;
    }

    public void setEmpPhysicalUnitId(Long empPhysicalUnitId) {
	this.empPhysicalUnitId = empPhysicalUnitId;
    }

    @Basic
    @Column(name = "EMP_PHYSICAL_UNIT_NAME")
    public String getEmpPhysicalUnitName() {
	return empPhysicalUnitName;
    }

    public void setEmpPhysicalUnitName(String empPhysicalUnitName) {
	this.empPhysicalUnitName = empPhysicalUnitName;
    }

    @Basic
    @Column(name = "EMP_DEGREE_ID")
    public Long getEmpDegreeId() {
	return empDegreeId;
    }

    public void setEmpDegreeId(Long empDegreeId) {
	this.empDegreeId = empDegreeId;
    }

    @Basic
    @Column(name = "EMP_DEGREE_DESC")
    public String getEmpDegreeDesc() {
	return empDegreeDesc;
    }

    public void setEmpDegreeDesc(String empDegreeDesc) {
	this.empDegreeDesc = empDegreeDesc;
    }

    @Basic
    @Column(name = "EMP_RANK_ID")
    public Long getEmpRankId() {
	return empRankId;
    }

    public void setEmpRankId(Long empRankId) {
	this.empRankId = empRankId;
    }

    @Basic
    @Column(name = "EMP_RANK_DESC")
    public String getEmpRankDesc() {
	return empRankDesc;
    }

    public void setEmpRankDesc(String empRankDesc) {
	this.empRankDesc = empRankDesc;
    }

    @Transient
    public RaiseEmployee getRaiseEmployee() {
	return raiseEmployee;
    }

    public void setRaiseEmployee(RaiseEmployee raiseEmployee) {
	this.raiseEmployee = raiseEmployee;
    }

}
