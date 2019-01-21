package com.code.dal.orm.hcm.employees.medicalstuff;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_PRS_EMPS_MED_STAFF_DATA")
public class EmployeeMedicalStaff extends AuditEntity implements Serializable {

    private Long empId;
    private Long medStaffDegreeId;
    private Long medStaffLevelId;
    private Long medStaffRankId;

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "MED_STAFF_DEGREE_ID")
    public Long getMedStaffDegreeId() {
	return medStaffDegreeId;
    }

    public void setMedStaffDegreeId(Long medStaffDegreeId) {
	this.medStaffDegreeId = medStaffDegreeId;
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
    @Column(name = "MED_STAFF_RANK_ID")
    public Long getMedStaffRankId() {
	return medStaffRankId;
    }

    public void setMedStaffRankId(Long medStaffRankId) {
	this.medStaffRankId = medStaffRankId;
    }

    @Override
    public Long calculateContentId() {
	return empId;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"medStaffDegreeId:" + medStaffDegreeId + AUDIT_SEPARATOR +
		"medStaffLevelId:" + medStaffLevelId + AUDIT_SEPARATOR +
		"medStaffRankId:" + medStaffRankId + AUDIT_SEPARATOR;
    }

}
