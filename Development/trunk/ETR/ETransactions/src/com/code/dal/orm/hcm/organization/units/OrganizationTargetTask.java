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
 * The <code>TargetTask</code> class represents the targets and tasks of a unit.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_organizationTargetTask_getOrganizationTargetsTasksByUnitsIds",
		query = " select t from OrganizationTargetTask t " +
			" where t.unitId in (:P_UNITS_IDS)" +
			" and t.activeFlag = 1 "
	)
})
@Entity
@Table(name = "HCM_ORG_TARGETS_TASKS")
public class OrganizationTargetTask extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {

    private Long id;
    private Long unitId;
    private Integer activeFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;

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
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
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
		"activeFlag:" + activeFlag + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"decisionDate:" + decisionDate + AUDIT_SEPARATOR;
    }

}
