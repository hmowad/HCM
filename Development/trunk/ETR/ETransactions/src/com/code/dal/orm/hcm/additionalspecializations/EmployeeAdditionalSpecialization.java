package com.code.dal.orm.hcm.additionalspecializations;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_PRS_EMPS_ADDITIONAL_SPECS")
@IdClass(EmployeeAdditionalSpecializationId.class)
public class EmployeeAdditionalSpecialization extends AuditEntity implements Serializable, InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long empId;
    private Long additionalSpecializationId;
    private Integer movementBlacklistFlag;

    @Id
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Id
    @Column(name = "ADDITIONAL_SPECIALIZATION_ID")
    public Long getAdditionalSpecializationId() {
	return additionalSpecializationId;
    }

    public void setAdditionalSpecializationId(Long additionalSpecializationId) {
	this.additionalSpecializationId = additionalSpecializationId;
    }

    @Basic
    @Column(name = "MOVEMENT_BLACK_LIST_FLAG")
    public Integer getMovementBlacklistFlag() {
	return movementBlacklistFlag;
    }

    public void setMovementBlacklistFlag(Integer movementBlacklistFlag) {
	this.movementBlacklistFlag = movementBlacklistFlag;
    }

    @Override
    public Long calculateContentId() {
	return empId;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"additionalSpecializationId:" + additionalSpecializationId + AUDIT_SEPARATOR +
		"movementBlacklistFlag:" + movementBlacklistFlag + AUDIT_SEPARATOR;
    }
}
