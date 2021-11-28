package com.code.dal.orm.hcm.raises;

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
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_raiseEmployeeData_searchRaiseEmployees",
		query = "select r from RaiseEmployeeData r " +
			" where (:P_SOCIAL_ID = '-1' or r.empSocialID = :P_SOCIAL_ID) " +
			" and (:P_RAISE_ID = -1 or r.raiseId = :P_RAISE_ID) " +
			" and (:P_RAISE_EMP_ID = -1 or r.empId = :P_RAISE_EMP_ID) " +
			" and (:P_EMP_NAME = '-1' or r.empName like :P_EMP_NAME ) " +
			" and (:P_JOB_DESC = '-1' or r.empJobName like :P_JOB_DESC ) " +
			" and (:P_PHYSICAL_UNIT_FULL_NAME = '-1' or r.empPhysicalUnitName like :P_PHYSICAL_UNIT_FULL_NAME ) " +
			" and (:P_EMP_NUMBER = -1 or r.empNumber = :P_EMP_NUMBER) " +
			" and (:P_REGION_ID = -1 or r.empRegionId = :P_REGION_ID) " +
			" and (:P_CATEGORY_ID = -1 or r.raiseCategoryId = :P_CATEGORY_ID) " +
			" and (:P_DECISION_NUMBER = '-1' or r.raiseDecisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_DECISION_DATE_FLAG = -1 or r.raiseDecisionDate = (TO_DATE(:P_DECISION_DATE, 'MI/MM/YYYY')))" +
			" and (:P_DESERVED_FLAG_VALUES_FLAG = -1 or r.empDeservedFlag in ( :P_DESERVED_FLAG_VALUES )) " +
			" order by r.empNumber")

})
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
    private String empSocialID;
    private Integer raiseType;
    private Integer empDeservedFlag;
    private Date raiseExecutionDate;
    private String raiseExecutionDateString;
    private String raiseDecisionNumber;
    private Date raiseDecisionDate;
    private String raiseDecisionDateString;
    private String raiseCategoryDesc;
    private Long raiseCategoryId;
    private Long empNumber;
    private Long empNewDegreeId;
    private String empNewDegreeDesc;
    private Long empPhysicalUnitId;
    private String empPhysicalUnitName;
    private Long empDegreeId;
    private String empDegreeDesc;
    private Long empRankId;
    private String empRankDesc;
    private String empJobRankDesc;
    private Long empRegionId;
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
    @Column(name = "EMP_SOCIAL_ID")
    public String getEmpSocialID() {
	return empSocialID;
    }

    public void setEmpSocialID(String empSocialID) {
	this.empSocialID = empSocialID;
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
    @Column(name = "DESERVED_FLAG")
    public Integer getEmpDeservedFlag() {
	return empDeservedFlag;
    }

    public void setEmpDeservedFlag(Integer empDeservedFlag) {
	this.empDeservedFlag = empDeservedFlag;
	raiseEmployee.setDeservedFlag(empDeservedFlag);

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
    @Column(name = "EMP_NUMBER")
    public Long getEmpNumber() {
	return empNumber;
    }

    public void setEmpNumber(Long empNumber) {
	this.empNumber = empNumber;
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
	raiseEmployee.setTransDegreeId(empDegreeId);
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
	raiseEmployee.setTransRankId(empRankId);
    }

    public void setEmpJobRankDesc(String empJobRankDesc) {
	this.empJobRankDesc = empJobRankDesc;
    }

    @Basic
    @Column(name = "EMP_JOB_RANK_DESC")
    public String getEmpJobRankDesc() {
	return empJobRankDesc;
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
    @Column(name = "EMP_REGION_ID")
    public Long getEmpRegionId() {
	return empRegionId;
    }

    public void setEmpRegionId(Long empRegionId) {
	this.empRegionId = empRegionId;
    }

    @Transient
    public RaiseEmployee getRaiseEmployee() {
	return raiseEmployee;
    }

    public void setRaiseEmployee(RaiseEmployee raiseEmployee) {
	this.raiseEmployee = raiseEmployee;
    }

}
