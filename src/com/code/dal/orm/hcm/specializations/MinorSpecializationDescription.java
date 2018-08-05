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
 * The <code>MinorSpecializationDescription</code> class represents the descriptions cards for the jobs minor specializations.
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_MINOR_SPECS_DESCS")
public class MinorSpecializationDescription extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private Long minorSpecializationId;
    private String targetedUnits;
    private String targetedRanks;
    private String generalPurpose;
    private String requestedQualifications;
    private String requestedExperiences;
    private String requestedSkills;
    private String requestedTrainings;
    private String usedTools;

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
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
    }

    @Basic
    @Column(name = "TARGETED_UNITS")
    public String getTargetedUnits() {
	return targetedUnits;
    }

    public void setTargetedUnits(String targetedUnits) {
	this.targetedUnits = targetedUnits;
    }

    @Basic
    @Column(name = "TARGETED_RANKS")
    public String getTargetedRanks() {
	return targetedRanks;
    }

    public void setTargetedRanks(String targetedRanks) {
	this.targetedRanks = targetedRanks;
    }

    @Basic
    @Column(name = "GENERAL_PURPOSE")
    public String getGeneralPurpose() {
	return generalPurpose;
    }

    public void setGeneralPurpose(String generalPurpose) {
	this.generalPurpose = generalPurpose;
    }

    @Basic
    @Column(name = "REQUESTED_QUALIFICATIONS")
    public String getRequestedQualifications() {
	return requestedQualifications;
    }

    public void setRequestedQualifications(String requestedQualifications) {
	this.requestedQualifications = requestedQualifications;
    }

    @Basic
    @Column(name = "REQUESTED_EXPERIENCES")
    public String getRequestedExperiences() {
	return requestedExperiences;
    }

    public void setRequestedExperiences(String requestedExperiences) {
	this.requestedExperiences = requestedExperiences;
    }

    @Basic
    @Column(name = "REQUESTED_SKILLS")
    public String getRequestedSkills() {
	return requestedSkills;
    }

    public void setRequestedSkills(String requestedSkills) {
	this.requestedSkills = requestedSkills;
    }

    @Basic
    @Column(name = "REQUESTED_TRAININGS")
    public String getRequestedTrainings() {
	return requestedTrainings;
    }

    public void setRequestedTrainings(String requestedTrainings) {
	this.requestedTrainings = requestedTrainings;
    }

    @Basic
    @Column(name = "USED_TOOLS")
    public String getUsedTools() {
	return usedTools;
    }

    public void setUsedTools(String usedTools) {
	this.usedTools = usedTools;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "minorSpecializationId:" + minorSpecializationId + AUDIT_SEPARATOR +
		"targetedUnits:" + targetedUnits + AUDIT_SEPARATOR +
		"targetedRanks:" + targetedRanks + AUDIT_SEPARATOR +
		"generalPurpose:" + generalPurpose + AUDIT_SEPARATOR +
		"requestedQualifications:" + requestedQualifications + AUDIT_SEPARATOR +
		"requestedExperiences:" + requestedExperiences + AUDIT_SEPARATOR +
		"requestedSkills:" + requestedSkills + AUDIT_SEPARATOR +
		"requestedTrainings:" + requestedTrainings + AUDIT_SEPARATOR +
		"usedTools:" + usedTools;
    }

}
