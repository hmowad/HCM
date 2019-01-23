package com.code.dal.orm.hcm.employees.medicalstuff;

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

@NamedQueries({
	@NamedQuery(name = "hcm_empData_searchEmployeeMedicalStaffData",
		query = " select e from EmployeeMedicalStaffData e where e.empId = :P_EMP_ID")
})

@Entity
@Table(name = "HCM_INTEG_EMPS_MED_STAFF_DATA")
public class EmployeeMedicalStaffData extends BaseEntity implements Serializable {

    private Long empId;
    private String empName;
    private Long medStaffDegreeId;
    private String medStaffDegreeDesc;
    private Long medStaffLevelId;
    private String medStaffLevelDesc;
    private Long medStaffRankId;
    private String medStaffRankDesc;
    private EmployeeMedicalStaff employeeMedicalStuff;

    public EmployeeMedicalStaffData() {
	employeeMedicalStuff = new EmployeeMedicalStaff();
    }

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	employeeMedicalStuff.setEmpId(empId);
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
    @Column(name = "MED_STAFF_DEGREE_ID")
    public Long getMedStaffDegreeId() {
	return medStaffDegreeId;
    }

    public void setMedStaffDegreeId(Long medStaffDegreeId) {
	this.medStaffDegreeId = medStaffDegreeId;
	employeeMedicalStuff.setMedStaffDegreeId(medStaffDegreeId);
    }

    @Basic
    @Column(name = "MED_STAFF_DEGREE_DESCRIPTION")
    public String getMedStaffDegreeDesc() {
	return medStaffDegreeDesc;
    }

    public void setMedStaffDegreeDesc(String medStaffDegreeDesc) {
	this.medStaffDegreeDesc = medStaffDegreeDesc;
    }

    @Basic
    @Column(name = "MED_STAFF_LEVEL_ID")
    public Long getMedStaffLevelId() {
	return medStaffLevelId;
    }

    public void setMedStaffLevelId(Long medStaffLevelId) {
	this.medStaffLevelId = medStaffLevelId;
	employeeMedicalStuff.setMedStaffLevelId(medStaffLevelId);
    }

    @Basic
    @Column(name = "MED_STAFF_LEVEL_DESCRIPTION")
    public String getMedStaffLevelDesc() {
	return medStaffLevelDesc;
    }

    public void setMedStaffLevelDesc(String medStaffLevelDesc) {
	this.medStaffLevelDesc = medStaffLevelDesc;
    }

    @Basic
    @Column(name = "MED_STAFF_RANK_ID")
    public Long getMedStaffRankId() {
	return medStaffRankId;
    }

    public void setMedStaffRankId(Long medStaffRankId) {
	this.medStaffRankId = medStaffRankId;
	employeeMedicalStuff.setMedStaffRankId(medStaffRankId);
    }

    @Basic
    @Column(name = "MED_STAFF_RANK_DESCRIPTION")
    public String getMedStaffRankDesc() {
	return medStaffRankDesc;
    }

    public void setMedStaffRankDesc(String medStaffRankDesc) {
	this.medStaffRankDesc = medStaffRankDesc;
    }

    @Transient
    @XmlTransient
    public EmployeeMedicalStaff getEmployeeMedicalStuff() {
	return employeeMedicalStuff;
    }

    public void setEmployeeMedicalStuff(EmployeeMedicalStaff employeeMedicalStuff) {
	this.employeeMedicalStuff = employeeMedicalStuff;
    }

}
