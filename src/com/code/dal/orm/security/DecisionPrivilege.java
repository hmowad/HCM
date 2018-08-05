package com.code.dal.orm.security;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "ETR_DECISIONS_PRIVILEGES")
public class DecisionPrivilege extends AuditEntity implements Serializable, InsertableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long empId;
    private Long processGroupId;
    private Long beneficiaryCategoryId;
    private Long beneficiaryRegionId;
    private Long beneficiaryUnitId;
    private Long unitId;
    private Long jobId;

    @SequenceGenerator(name = "ETRDecisionsPrvsSeq", sequenceName = "ETR_DECISIONS_PRVS_SEQ ")
    @GeneratedValue(generator = "ETRDecisionsPrvsSeq")
    @Id
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
    @Column(name = "BENEFICAIRY_CATEGORY_ID")
    public Long getBeneficiaryCategoryId() {
	return beneficiaryCategoryId;
    }

    public void setBeneficiaryCategoryId(Long beneficiaryCategoryId) {
	this.beneficiaryCategoryId = beneficiaryCategoryId;
    }

    @Basic
    @Column(name = "PROCESS_GROUP_ID")
    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    @Basic
    @Column(name = "BENEFICAIRY_REGION_ID")
    public Long getBeneficiaryRegionId() {
	return beneficiaryRegionId;
    }

    public void setBeneficiaryRegionId(Long beneficiaryRegionId) {
	this.beneficiaryRegionId = beneficiaryRegionId;
    }

    @Basic
    @Column(name = "BENEFICAIRY_UNIT_ID")
    public Long getBeneficiaryUnitId() {
	return beneficiaryUnitId;
    }

    public void setBeneficiaryUnitId(Long beneficiaryUnitId) {
	this.beneficiaryUnitId = beneficiaryUnitId;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"processGroupId:" + processGroupId + AUDIT_SEPARATOR +
		"beneficiaryCategoryId:" + beneficiaryCategoryId + AUDIT_SEPARATOR +
		"beneficiaryRegionId:" + beneficiaryRegionId + AUDIT_SEPARATOR +
		"beneficiaryUnitId:" + beneficiaryUnitId + AUDIT_SEPARATOR +
		"unitId:" + unitId + AUDIT_SEPARATOR +
		"jobId:" + jobId + AUDIT_SEPARATOR;
    }
}
