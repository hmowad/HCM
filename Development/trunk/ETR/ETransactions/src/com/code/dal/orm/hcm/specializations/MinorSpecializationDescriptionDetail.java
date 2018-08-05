package com.code.dal.orm.hcm.specializations;

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

/**
 * The <code>MinorSpecializationDescriptionDetail</code> class represents the descriptions cards duties for the jobs minor specializations.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_MINOR_SPECS_DESCS_DTLS")
public class MinorSpecializationDescriptionDetail extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private Long minorSpecializationDescriptionId;
    private String dutyDescription;

    @SequenceGenerator(name = "HCMMinSpecDescSeq", sequenceName = "HCM_MINOR_SPECS_DESCS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMMinSpecDescSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "MINOR_SPEC_DESC_ID")
    public Long getMinorSpecializationDescriptionId() {
	return minorSpecializationDescriptionId;
    }

    public void setMinorSpecializationDescriptionId(Long minorSpecializationDescriptionId) {
	this.minorSpecializationDescriptionId = minorSpecializationDescriptionId;
    }

    @Basic
    @Column(name = "DUTY_DESCRIPTION")
    public String getDutyDescription() {
	return dutyDescription;
    }

    public void setDutyDescription(String dutyDescription) {
	this.dutyDescription = dutyDescription;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "minorSpecDescId:" + minorSpecializationDescriptionId + AUDIT_SEPARATOR +
		"dutyDesc:" + dutyDescription;
    }

}
