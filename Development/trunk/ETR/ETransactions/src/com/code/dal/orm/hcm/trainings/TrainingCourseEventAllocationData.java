package com.code.dal.orm.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingCourseEventAllocationData_searchTrainingCourseEventAllocationData",
		query = " select a from TrainingCourseEventAllocationData a " +
			" where (:P_COURSE_EVENT_FLAG = -1 or a.courseEventId in (:P_COURSE_EVENTS_IDS) )"
			+ "and (:P_EXTERNAL_PARTY_ID = -1 or a.externalPartyId = :P_EXTERNAL_PARTY_ID )"
			+ "and (:P_REGION_ID = -1 or a.regionId = :P_REGION_ID )"
			+ "and (:P_REGION_ALLOCATION_FLAG = -1 or (:P_REGION_ALLOCATION_FLAG = 1 and a.regionId is not null) or (:P_REGION_ALLOCATION_FLAG = 0 and a.externalPartyId is not null))"
			+ "order by a.regionId,a.externalPartyId"),

	@NamedQuery(name = "hcm_trainingCourseEventAllocationData_getExternalPartiesNotHavingAllocation",
		query = " select sum(a.allocationCount), a.externalPartyDesc, e.trainingUnitName from TrainingCourseEventAllocationData a, TrainingCourseEventData e  " +
			"where e.id = a.courseEventId " +
			"and a.externalPartyId is not null " +
			"and e.trainingYearId = :P_TRAINING_YEAR_ID " +
			"group by e.trainingUnitId,e.trainingUnitName,a.externalPartyId,a.externalPartyDesc")

})

@Entity
@Table(name = "HCM_VW_TRN_CRS_EVNTS_ALLCTNS")
public class TrainingCourseEventAllocationData extends BaseEntity {
    private Long id;
    private Long courseEventId;
    private Long regionId;
    private String regionShortDesc;
    private Long externalPartyId;
    private String externalPartyDesc;
    private Integer allocationCount;
    private String allocationDesc;
    private Boolean allocationCountFlag;
    private Long version;

    private List<TrainingCourseEventAllocationEmployeeData> allocationEmployees;
    private TrainingCourseEventAllocation trainingCourseEventAllocation;

    public TrainingCourseEventAllocationData() {
	trainingCourseEventAllocation = new TrainingCourseEventAllocation();
	allocationEmployees = new ArrayList<>();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingCourseEventAllocation.setId(id);
    }

    @Basic
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
	this.trainingCourseEventAllocation.setCourseEventId(courseEventId);
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	this.trainingCourseEventAllocation.setRegionId(regionId);
    }

    @Basic
    @Column(name = "REGION_SHORT_DESCRIPTION")
    public String getRegionShortDesc() {
	return regionShortDesc;
    }

    public void setRegionShortDesc(String regionShortDesc) {
	this.regionShortDesc = regionShortDesc;
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_ID")
    public Long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(Long externalPartyId) {
	this.externalPartyId = externalPartyId;
	this.trainingCourseEventAllocation.setExternalPartyId(externalPartyId);
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_DESCRIPTION")
    public String getExternalPartyDesc() {
	return externalPartyDesc;
    }

    public void setExternalPartyDesc(String externalPartyDesc) {
	this.externalPartyDesc = externalPartyDesc;
    }

    @Basic
    @Column(name = "ALLOCATION_COUNT")
    public Integer getAllocationCount() {
	return allocationCount;
    }

    public void setAllocationCount(Integer allocationCount) {
	this.allocationCount = allocationCount;
	//
	// if (allocationCount != null) {
	// allocationCountFlag = true;
	// } else {
	// allocationCountFlag = false;
	// }

	this.trainingCourseEventAllocation.setAllocationCount(allocationCount);
    }

    @Basic
    @Column(name = "ALLOCATION_DESC")
    public String getAllocationDesc() {
	return allocationDesc;
    }

    public void setAllocationDesc(String allocationDesc) {
	this.allocationDesc = allocationDesc;

	if (allocationDesc != null && !allocationDesc.isEmpty()) {
	    allocationCountFlag = false;
	} else {
	    allocationCountFlag = true;
	}

	this.trainingCourseEventAllocation.setAllocationDesc(allocationDesc);
    }

    @Transient
    public Boolean getAllocationCountFlag() {
	return allocationCountFlag;
    }

    public void setAllocationCountFlag(Boolean allocationCountFlag) {
	this.allocationCountFlag = allocationCountFlag;

	if (allocationCountFlag != null && allocationCountFlag.equals(true)) {
	    setAllocationDesc(null);
	} else {
	    setAllocationCount(null);
	}
    }

    @Basic
    @Column(name = "VERSION")
    public Long getVersion() {
	return version;
    }

    public void setVersion(Long version) {
	this.version = version;
	trainingCourseEventAllocation.setVersion(version);
    }

    @Transient
    public List<TrainingCourseEventAllocationEmployeeData> getAllocationEmployees() {
	return allocationEmployees;
    }

    public void setAllocationEmployees(List<TrainingCourseEventAllocationEmployeeData> allocationEmployees) {
	this.allocationEmployees = allocationEmployees;
    }

    @Transient
    public TrainingCourseEventAllocation getTrainingCourseEventAllocation() {
	return trainingCourseEventAllocation;
    }

    public void setTrainingCourseEventAllocation(TrainingCourseEventAllocation trainingCourseEventAllocation) {
	this.trainingCourseEventAllocation = trainingCourseEventAllocation;
    }
}
