package com.code.dal.orm.hcm.specializations;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>MinorSpecializationDescriptionData</code> class represents the descriptions cards for the jobs minor specializations in detailed format.
 * 
 */
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_minorSpecializationDescriptionData_getMinorSpecializationDescriptionData",
		query = " select m from MinorSpecializationDescriptionData m " +
			" where m.minorSpecializationId = :P_MINOR_SPECIALIZATION_ID "
	)
})
@Entity
@Table(name = "HCM_VW_MINOR_SPECS_DESCS")
public class MinorSpecializationDescriptionData extends BaseEntity {

    private Long id;
    private Long minorSpecializationId;
    private String minorSpecializationDescription;
    private String targetedUnits;
    private String targetedRanks;
    private String generalPurpose;
    private String requestedQualifications;
    private String requestedExperiences;
    private String requestedSkills;
    private String requestedTrainings;
    private String usedTools;

    private MinorSpecializationDescription minorSpecializationDescriptionEntity;

    public MinorSpecializationDescriptionData() {
	minorSpecializationDescriptionEntity = new MinorSpecializationDescription();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.minorSpecializationDescriptionEntity.setId(id);
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
	this.minorSpecializationDescriptionEntity.setMinorSpecializationId(minorSpecializationId);
    }

    @Basic
    @Column(name = "MINOR_SPEC_DESCRIPTION")
    public String getMinorSpecializationDescription() {
	return minorSpecializationDescription;
    }

    public void setMinorSpecializationDescription(String minorSpecializationDescription) {
	this.minorSpecializationDescription = minorSpecializationDescription;
    }

    @Basic
    @Column(name = "TARGETED_UNITS")
    public String getTargetedUnits() {
	return targetedUnits;
    }

    public void setTargetedUnits(String targetedUnits) {
	this.targetedUnits = targetedUnits;
	this.minorSpecializationDescriptionEntity.setTargetedUnits(targetedUnits);
    }

    @Basic
    @Column(name = "TARGETED_RANKS")
    public String getTargetedRanks() {
	return targetedRanks;
    }

    public void setTargetedRanks(String targetedRanks) {
	this.targetedRanks = targetedRanks;
	this.minorSpecializationDescriptionEntity.setTargetedRanks(targetedRanks);
    }

    @Basic
    @Column(name = "GENERAL_PURPOSE")
    public String getGeneralPurpose() {
	return generalPurpose;
    }

    public void setGeneralPurpose(String generalPurpose) {
	this.generalPurpose = generalPurpose;
	this.minorSpecializationDescriptionEntity.setGeneralPurpose(generalPurpose);
    }

    @Basic
    @Column(name = "REQUESTED_QUALIFICATIONS")
    public String getRequestedQualifications() {
	return requestedQualifications;
    }

    public void setRequestedQualifications(String requestedQualifications) {
	this.requestedQualifications = requestedQualifications;
	this.minorSpecializationDescriptionEntity.setRequestedQualifications(requestedQualifications);
    }

    @Basic
    @Column(name = "REQUESTED_EXPERIENCES")
    public String getRequestedExperiences() {
	return requestedExperiences;
    }

    public void setRequestedExperiences(String requestedExperiences) {
	this.requestedExperiences = requestedExperiences;
	this.minorSpecializationDescriptionEntity.setRequestedExperiences(requestedExperiences);
    }

    @Basic
    @Column(name = "REQUESTED_SKILLS")
    public String getRequestedSkills() {
	return requestedSkills;
    }

    public void setRequestedSkills(String requestedSkills) {
	this.requestedSkills = requestedSkills;
	this.minorSpecializationDescriptionEntity.setRequestedSkills(requestedSkills);
    }

    @Basic
    @Column(name = "REQUESTED_TRAININGS")
    public String getRequestedTrainings() {
	return requestedTrainings;
    }

    public void setRequestedTrainings(String requestedTrainings) {
	this.requestedTrainings = requestedTrainings;
	this.minorSpecializationDescriptionEntity.setRequestedTrainings(requestedTrainings);
    }

    @Basic
    @Column(name = "USED_TOOLS")
    public String getUsedTools() {
	return usedTools;
    }

    public void setUsedTools(String usedTools) {
	this.usedTools = usedTools;
	this.minorSpecializationDescriptionEntity.setUsedTools(usedTools);
    }

    @Transient
    public MinorSpecializationDescription getMinorSpecializationDescriptionEntity() {
	return minorSpecializationDescriptionEntity;
    }

    public void setMinorSpecializationDescriptionEntity(MinorSpecializationDescription minorSpecializationDescriptionEntity) {
	this.minorSpecializationDescriptionEntity = minorSpecializationDescriptionEntity;
    }
}
