package com.code.dal.orm.hcm.additionalspecializations;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;

@NamedQueries({
	@NamedQuery(name = "hcm_employeeAdditionalSpecializationData_searchEmployeeAdditionalSpecializationData",
		query = " select s from EmployeeAdditionalSpecializationData s where " +
			" (:P_EMP_ID = -1 or s.empId = :P_EMP_ID) " +
			" and (:P_ADDITIONAL_SPECIALIZATION_ID = -1 or s.additionalSpecializationId = :P_ADDITIONAL_SPECIALIZATION_ID) " +
			" and (:P_PHYSICAL_REGION_ID = -1 or s.physicalRegionId = :P_PHYSICAL_REGION_ID) " +
			" order by s.empName, s.additionalSpecializationDesc "
	)
})
@Entity
@Table(name = "HCM_VW_PRS_EMPS_ADD_SPECS")
@IdClass(EmployeeAdditionalSpecializationId.class)
public class EmployeeAdditionalSpecializationData extends BaseEntity implements Serializable {
    private Long empId;
    private String empName;
    private String empJobName;
    private String empRankDescription;
    private String physicalUnitFullName;
    private Long physicalRegionId;
    private Long additionalSpecializationId;
    private String additionalSpecializationDesc;
    private Integer movementBlacklistFlag;
    private Boolean movementBlacklistFlagBoolean;

    private EmployeeAdditionalSpecialization employeeAdditionalSpecialization;

    public EmployeeAdditionalSpecializationData() {
	employeeAdditionalSpecialization = new EmployeeAdditionalSpecialization();
    }

    @Id
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	employeeAdditionalSpecialization.setEmpId(empId);
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
    @Column(name = "EMP_JOB_NAME")
    public String getEmpJobName() {
	return empJobName;
    }

    public void setEmpJobName(String empJobName) {
	this.empJobName = empJobName;
    }

    @Basic
    @Column(name = "EMP_RANK_DESCRIPTION")
    public String getEmpRankDescription() {
	return empRankDescription;
    }

    public void setEmpRankDescription(String empRankDescription) {
	this.empRankDescription = empRankDescription;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_FULL_NAME")
    public String getPhysicalUnitFullName() {
	return physicalUnitFullName;
    }

    public void setPhysicalUnitFullName(String physicalUnitFullName) {
	this.physicalUnitFullName = physicalUnitFullName;
    }

    @Basic
    @Column(name = "PHYSICAL_REGION_ID")
    public Long getPhysicalRegionId() {
	return physicalRegionId;
    }

    public void setPhysicalRegionId(Long physicalRegionId) {
	this.physicalRegionId = physicalRegionId;
    }

    @Id
    @Column(name = "ADDITIONAL_SPECIALIZATION_ID")
    public Long getAdditionalSpecializationId() {
	return additionalSpecializationId;
    }

    public void setAdditionalSpecializationId(Long additionalSpecializationId) {
	this.additionalSpecializationId = additionalSpecializationId;
	employeeAdditionalSpecialization.setAdditionalSpecializationId(additionalSpecializationId);
    }

    @Basic
    @Column(name = "ADDITIONAL_SPECIALIZATION_DESC")
    public String getAdditionalSpecializationDesc() {
	return additionalSpecializationDesc;
    }

    public void setAdditionalSpecializationDesc(String additionalSpecializationDesc) {
	this.additionalSpecializationDesc = additionalSpecializationDesc;
    }

    @Basic
    @Column(name = "MOVEMENT_BLACK_LIST_FLAG")
    public Integer getMovementBlacklistFlag() {
	return movementBlacklistFlag;
    }

    public void setMovementBlacklistFlag(Integer movementBlacklistFlag) {
	this.movementBlacklistFlag = movementBlacklistFlag;
	if (this.movementBlacklistFlag == null || this.movementBlacklistFlag == FlagsEnum.OFF.getCode())
	    this.movementBlacklistFlagBoolean = false;
	else
	    this.movementBlacklistFlagBoolean = true;
	employeeAdditionalSpecialization.setMovementBlacklistFlag(movementBlacklistFlag);
    }

    @Transient
    public Boolean getMovementBlacklistFlagBoolean() {
	return movementBlacklistFlagBoolean;
    }

    public void setMovementBlacklistFlagBoolean(Boolean movementBlacklistFlagBoolean) {
	this.movementBlacklistFlagBoolean = movementBlacklistFlagBoolean;

	if (this.movementBlacklistFlagBoolean == null || !this.movementBlacklistFlagBoolean) {
	    this.movementBlacklistFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.movementBlacklistFlag = FlagsEnum.ON.getCode();
	}

	employeeAdditionalSpecialization.setMovementBlacklistFlag(movementBlacklistFlag);
    }

    @Transient
    public EmployeeAdditionalSpecialization getEmployeeAdditionalSpecialization() {
	return employeeAdditionalSpecialization;
    }

    public void setEmployeeAdditionalSpecialization(EmployeeAdditionalSpecialization employeeAdditionalSpecialization) {
	this.employeeAdditionalSpecialization = employeeAdditionalSpecialization;
    }
}