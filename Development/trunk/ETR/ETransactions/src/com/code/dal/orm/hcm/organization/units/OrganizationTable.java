package com.code.dal.orm.hcm.organization.units;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>OrganizationTable</code> class represents the organization table of a unit.
 * 
 */

@NamedQueries({
	@NamedQuery(name = "hcm_organizationTable_getOrganizationTablesByCategoryClassAndUnitsIds",
		query = " select t from OrganizationTable t " +
			" where t.unitId in (:P_UNITS_IDS)" +
			" and t.categoryClass = :P_CATEGORY_CLASS" +
			" and t.activeFlag = 1 "
	),
	@NamedQuery(name = "hcm_organizationTable_countOrganizationTablesByCategoryClassAndUnitId",
		query = " select count(t.id) from OrganizationTable t " +
			" where t.unitId = :P_UNIT_ID" +
			" and t.categoryClass = :P_CATEGORY_CLASS" +
			" and t.activeFlag = 1 "
	)
})
@Entity
@Table(name = "HCM_ORG_TABLES")
public class OrganizationTable extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {

    private Long id;
    private Long unitId;
    private Integer categoryClass;
    private Integer activeFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;

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
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "CATEGORY_CLASS")
    public Integer getCategoryClass() {
	return categoryClass;
    }

    public void setCategoryClass(Integer categoryClass) {
	this.categoryClass = categoryClass;
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
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
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "unitId:" + unitId + AUDIT_SEPARATOR +
		"categoryClass:" + categoryClass + AUDIT_SEPARATOR +
		"activeFlag:" + activeFlag + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"decisionDate:" + decisionDate;
    }
}
