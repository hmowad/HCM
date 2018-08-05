package com.code.dal.orm.hcm.organization.units;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>TargetTaskData</code> class represents the targets and tasks of a unit in detailed format.
 * 
 */
@Entity
@Table(name = "HCM_VW_ORG_TARGETS_TASKS")
public class OrganizationTargetTaskData extends BaseEntity {

    private Long id;
    private Long unitId;
    private Long unitTypeId;
    private String unitHKey;
    private String unitFullName;
    private Integer activeFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;

    private OrganizationTargetTask organizationTargetTask;

    public OrganizationTargetTaskData() {
	organizationTargetTask = new OrganizationTargetTask();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.organizationTargetTask.setId(id);
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
	this.organizationTargetTask.setUnitId(unitId);
    }

    @Basic
    @Column(name = "UNIT_TYPE_ID")
    public Long getUnitTypeId() {
	return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
	this.unitTypeId = unitTypeId;
    }

    @Basic
    @Column(name = "UNIT_HKEY")
    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHKey) {
	this.unitHKey = unitHKey;
    }

    @Basic
    @Column(name = "UNIT_FULL_NAME")
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
	this.organizationTargetTask.setActiveFlag(activeFlag);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.organizationTargetTask.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
	this.organizationTargetTask.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	this.organizationTargetTask.setDecisionDateString(decisionDateString);
    }

    @Transient
    public OrganizationTargetTask getOrganizationTargetTask() {
	return organizationTargetTask;
    }

    public void setOrganizationTargetTask(OrganizationTargetTask organizationTargetTask) {
	this.organizationTargetTask = organizationTargetTask;
    }

}