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
 * The <code>TargetTaskDetail</code> class represents the targets and tasks details of a unit.
 * 
 */
@Entity
@Table(name = "HCM_ORG_TARGETS_TASKS_DETAILS")
public class OrganizationTargetTaskDetail extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {

    private Long id;
    private Long organizationTargetTaskId;
    private Integer organizationTargetTaskFlag;
    private String description;
    private Integer activeFlag;

    @SequenceGenerator(name = "HCMOrgTargetTaskSeq", sequenceName = "HCM_ORG_TARGETS_TASKS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMOrgTargetTaskSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "TARGET_TASK_ID")
    public Long getOrganizationTargetTaskId() {
	return organizationTargetTaskId;
    }

    public void setOrganizationTargetTaskId(Long organizationTargetTaskId) {
	this.organizationTargetTaskId = organizationTargetTaskId;
    }

    @Basic
    @Column(name = "TARGET_TASK_FLAG")
    public Integer getOrganizationTargetTaskFlag() {
	return organizationTargetTaskFlag;
    }

    public void setOrganizationTargetTaskFlag(Integer organizationTargetTaskFlag) {
	this.organizationTargetTaskFlag = organizationTargetTaskFlag;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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
	return "organizationTargetTaskId:" + organizationTargetTaskId + AUDIT_SEPARATOR +
		"organizationTargetTaskFlag:" + organizationTargetTaskFlag + AUDIT_SEPARATOR +
		"description:" + description + AUDIT_SEPARATOR +
		"activeFlag:" + activeFlag + AUDIT_SEPARATOR;
    }

}
