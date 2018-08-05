package com.code.dal.orm.hcm.organization.jobs;

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
 * The <code>ManpowerNeedDetail</code> class represents the manpower needs details data.
 * 
 */
@Entity
@Table(name = "HCM_ORG_MANPOWER_NEEDS_DTLS")
public class ManpowerNeedDetail extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private Long manpowerNeedId;
    private Long unitId;
    private Long minorSpecializationId;
    private Integer requiredCount;
    private String requestReasons;
    private Integer requestSuggestedCount;
    private Integer studySuggestedCount;
    private String studyReasons;
    private Integer mainStudySuggestedCount;
    private String mainStudyReasons;
    private Integer addedOnStudyFlag;
    private Integer officialEmployeesCount;
    private Integer physicalEmployeesCount;

    @SequenceGenerator(name = "HCMOrgManpowerNeedSeq",
	    sequenceName = "HCM_ORG_MANPOWER_NEEDS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMOrgManpowerNeedSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "MANPOWER_NEED_ID")
    public Long getManpowerNeedId() {
	return manpowerNeedId;
    }

    public void setManpowerNeedId(Long manpowerNeedId) {
	this.manpowerNeedId = manpowerNeedId;
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
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
    }

    @Basic
    @Column(name = "REQUIRED_COUNT")
    public Integer getRequiredCount() {
	return requiredCount;
    }

    public void setRequiredCount(Integer requiredCount) {
	this.requiredCount = requiredCount;
    }

    @Basic
    @Column(name = "REQUEST_REASONS")
    public String getRequestReasons() {
	return requestReasons;
    }

    public void setRequestReasons(String requestReasons) {
	this.requestReasons = requestReasons;
    }

    @Basic
    @Column(name = "REQUEST_SUGGESTED_COUNT")
    public Integer getRequestSuggestedCount() {
	return requestSuggestedCount;
    }

    public void setRequestSuggestedCount(Integer requestSuggestedCount) {
	this.requestSuggestedCount = requestSuggestedCount;
    }

    @Basic
    @Column(name = "STUDY_SUGGESTED_COUNT")
    public Integer getStudySuggestedCount() {
	return studySuggestedCount;
    }

    public void setStudySuggestedCount(Integer studySuggestedCount) {
	this.studySuggestedCount = studySuggestedCount;
    }

    @Basic
    @Column(name = "STUDY_REASONS")
    public String getStudyReasons() {
	return studyReasons;
    }

    public void setStudyReasons(String studyReasons) {
	this.studyReasons = studyReasons;
    }

    @Basic
    @Column(name = "MAIN_STUDY_SUGGESTED_COUNT")
    public Integer getMainStudySuggestedCount() {
	return mainStudySuggestedCount;
    }

    public void setMainStudySuggestedCount(Integer mainStudySuggestedCount) {
	this.mainStudySuggestedCount = mainStudySuggestedCount;
    }

    @Basic
    @Column(name = "MAIN_STUDY_REASONS")
    public String getMainStudyReasons() {
	return mainStudyReasons;
    }

    public void setMainStudyReasons(String mainStudyReasons) {
	this.mainStudyReasons = mainStudyReasons;
    }

    @Basic
    @Column(name = "ADDED_ON_STUDY_FLAG")
    public Integer getAddedOnStudyFlag() {
	return addedOnStudyFlag;
    }

    public void setAddedOnStudyFlag(Integer addedOnStudyFlag) {
	this.addedOnStudyFlag = addedOnStudyFlag;
    }

    @Basic
    @Column(name = "OFFICIAL_EMPLOYEES_COUNT")
    public Integer getOfficialEmployeesCount() {
	return officialEmployeesCount;
    }

    public void setOfficialEmployeesCount(Integer officialEmployeesCount) {
	this.officialEmployeesCount = officialEmployeesCount;
    }

    @Basic
    @Column(name = "PHYSICAL_EMPLOYEES_COUNT")
    public Integer getPhysicalEmployeesCount() {
	return physicalEmployeesCount;
    }

    public void setPhysicalEmployeesCount(Integer physicalEmployeesCount) {
	this.physicalEmployeesCount = physicalEmployeesCount;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "manpowerNeedId:" + manpowerNeedId + AUDIT_SEPARATOR +
		"unitId:" + unitId + AUDIT_SEPARATOR +
		"minorSpecializationId:" + minorSpecializationId + AUDIT_SEPARATOR +
		"requiredCount:" + requiredCount + AUDIT_SEPARATOR +
		"requestReasons:" + requestReasons + AUDIT_SEPARATOR +
		"requestSuggestedCount:" + requestSuggestedCount + AUDIT_SEPARATOR +
		"studySuggestedCount:" + studySuggestedCount + AUDIT_SEPARATOR +
		"studyReasons:" + studyReasons + AUDIT_SEPARATOR +
		"mainStudySuggestedCount:" + mainStudySuggestedCount + AUDIT_SEPARATOR +
		"mainStudyReasons:" + mainStudyReasons + AUDIT_SEPARATOR +
		"addedOnStudyFlag:" + addedOnStudyFlag + AUDIT_SEPARATOR +
		"officialEmployeesCount:" + officialEmployeesCount + AUDIT_SEPARATOR +
		"physicalEmployeesCount:" + physicalEmployeesCount;
    }

}
