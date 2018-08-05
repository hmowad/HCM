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
 * The <code>OrganizationTableData</code> class represents the organization table of a unit in detailed format.
 * 
 */

@Entity
@Table(name = "HCM_VW_ORG_TABLES")
public class OrganizationTableData extends BaseEntity {

    private Long id;
    private Long unitId;
    private String unitFullName;
    private Integer categoryClass;
    private Integer activeFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;

    private OrganizationTable organizationTable;

    public OrganizationTableData() {
	organizationTable = new OrganizationTable();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.organizationTable.setId(id);
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
	this.organizationTable.setUnitId(unitId);
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
    @Column(name = "CATEGORY_CLASS")
    public Integer getCategoryClass() {
	return categoryClass;
    }

    public void setCategoryClass(Integer categoryClass) {
	this.categoryClass = categoryClass;
	this.organizationTable.setCategoryClass(categoryClass);
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
	this.organizationTable.setActiveFlag(activeFlag);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.organizationTable.setDecisionNumber(decisionNumber);
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
	this.organizationTable.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	this.organizationTable.setDecisionDateString(decisionDateString);
    }

    @Transient
    public OrganizationTable getOrganizationTable() {
	return organizationTable;
    }

    public void setOrganizationTable(OrganizationTable organizationTable) {
	this.organizationTable = organizationTable;
    }
}
