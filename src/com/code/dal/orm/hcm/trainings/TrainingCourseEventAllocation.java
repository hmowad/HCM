package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_TRN_CRS_EVENTS_ALLOCATIONS")
public class TrainingCourseEventAllocation extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long courseEventId;
    private Long regionId;
    private Long externalPartyId;
    private Integer allocationCount;
    private String allocationDesc;
    private Long version;

    @SequenceGenerator(name = "HCMTrainingPlanSeq",
	    sequenceName = "HCM_TRN_PLAN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingPlanSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_ID")
    public Long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(Long externalPartyId) {
	this.externalPartyId = externalPartyId;
    }

    @Basic
    @Column(name = "ALLOCATION_COUNT")
    public Integer getAllocationCount() {
	return allocationCount;
    }

    public void setAllocationCount(Integer allocationCount) {
	this.allocationCount = allocationCount;
    }

    @Basic
    @Column(name = "ALLOCATION_DESC")
    public String getAllocationDesc() {
	return allocationDesc;
    }

    public void setAllocationDesc(String allocationDesc) {
	this.allocationDesc = allocationDesc;
    }

    public void setVersion(Long version) {
	this.version = version;
    }

    @Version
    @Column(name = "VERSION")
    /*
     * This attribute handles the optimistic transaction management for the allocation entity to prevent data incosistency.
     */
    public Long getVersion() {
	return version;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "courseEventId:" + courseEventId + AUDIT_SEPARATOR +
		"regionId:" + regionId + AUDIT_SEPARATOR +
		"externalPartyId:" + externalPartyId + AUDIT_SEPARATOR +
		"allocationCount:" + allocationCount + AUDIT_SEPARATOR +
		"allocationDesc:" + allocationDesc + AUDIT_SEPARATOR;
    }
}
