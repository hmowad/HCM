package com.code.dal.orm.hcm.raises;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_RAISES_EMPLOYEES")
public class RaiseEmployee extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long raiseId;
    private Long empId;
    private String exclusionReason;
    private Long newDegreeId;

    @SequenceGenerator(name = "HCMRaiseSeq",
	    sequenceName = "HCM_RAISE_SEQ")
    @Id
    @GeneratedValue(generator = "HCMRaiseSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "RAISE_ID")
    public Long getRaiseId() {
	return raiseId;
    }

    public void setRaiseId(Long raiseId) {
	this.raiseId = raiseId;
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
    @Column(name = "EXCLUSION_REASON")
    public String getExclusionReason() {
	return exclusionReason;
    }

    public void setExclusionReason(String exclusionReason) {
	this.exclusionReason = exclusionReason;
    }

    @Basic
    @Column(name = "NEW_DEGREE_ID")
    public Long getNewDegreeId() {
	return newDegreeId;
    }

    public void setNewDegreeId(Long newDegreeId) {
	this.newDegreeId = newDegreeId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "raiseId:" + raiseId + AUDIT_SEPARATOR +
		"empId:" + empId + AUDIT_SEPARATOR +
		"exclusionReason:" + exclusionReason + AUDIT_SEPARATOR +
		"newDegreeId:" + newDegreeId + AUDIT_SEPARATOR;
    }
}
