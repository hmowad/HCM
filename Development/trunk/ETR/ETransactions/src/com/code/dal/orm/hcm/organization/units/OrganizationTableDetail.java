package com.code.dal.orm.hcm.organization.units;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>OrganizationTableDetail</code> class represents the organization table details.
 * 
 */
@Entity
@Table(name = "HCM_ORG_TABLES_DETAILS")
public class OrganizationTableDetail extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {

    private Long id;
    private Long organizationTableId;
    private String basicJobName;
    private Long rankId;
    private Integer jobsCount;
    private Integer generalSpecialization;
    private Long minorSpecializationId;
    private Long classificationId;
    private Integer generalType;
    private Integer approvedFlag;
    private Integer activeFlag;

    @SequenceGenerator(name = "HCMOrgTableSeq", sequenceName = "HCM_ORG_TABLES_SEQ")
    @Id
    @GeneratedValue(generator = "HCMOrgTableSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "ORG_TABLE_ID")
    public Long getOrganizationTableId() {
	return organizationTableId;
    }

    public void setOrganizationTableId(Long organizationTableId) {
	this.organizationTableId = organizationTableId;
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME")
    public String getBasicJobName() {
	return basicJobName;
    }

    public void setBasicJobName(String basicJobName) {
	this.basicJobName = basicJobName;
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
    @Column(name = "JOBS_COUNT")
    public Integer getJobsCount() {
	return jobsCount;
    }

    public void setJobsCount(Integer jobsCount) {
	this.jobsCount = jobsCount;
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
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
    }

    @Basic
    @Column(name = "CLASSIFICATION_ID")
    public Long getClassificationId() {
	return classificationId;
    }

    public void setClassificationId(Long classificationId) {
	this.classificationId = classificationId;
    }

    @Basic
    @Column(name = "GENERAL_TYPE")
    public Integer getGeneralType() {
	return generalType;
    }

    public void setGeneralType(Integer generalType) {
	this.generalType = generalType;
    }

    @Basic
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "orgTableId:" + organizationTableId + AUDIT_SEPARATOR +
		"basicJobName:" + basicJobName + AUDIT_SEPARATOR +
		"rankId:" + rankId + AUDIT_SEPARATOR +
		"jobsCount:" + jobsCount + AUDIT_SEPARATOR +
		"generalSpec:" + generalSpecialization + AUDIT_SEPARATOR +
		"minorSpecId:" + minorSpecializationId + AUDIT_SEPARATOR +
		"classificationId:" + classificationId + AUDIT_SEPARATOR +
		"generalType:" + generalType + AUDIT_SEPARATOR +
		"approvedFlag:" + approvedFlag + AUDIT_SEPARATOR +
		"activeFlag:" + activeFlag;
    }

}
