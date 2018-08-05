package com.code.dal.orm.workflow.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "wf_trainingPlanNeedData_searchWFTrainingPlanNeedData",
		query = "select p from WFTrainingPlanNeedData p " +
			" where (:P_INSTANCE_ID = -1 or p.instanceId = :P_INSTANCE_ID ) " +
			" and (:P_REQUESTING_UNIT_ID = -1 or p.requestingUnitId = :P_REQUESTING_UNIT_ID) " +
			" and (:P_TRAINING_UNIT_ID = -1 or p.trainingUnitId = :P_TRAINING_UNIT_ID)" +
			" and (:P_COURSE_EVENT_ID = -1 or p.courseEventId = :P_COURSE_EVENT_ID)" +
			" and (:P_STATUS = -1 or p.instanceStatus = :P_STATUS)" +
			" order by p.hijriRequestDate"),
	@NamedQuery(name = "wf_trainingPlanNeedData_searchWFTrainingPlanNeedRequest",
		query = "select distinct p.instanceId,p.requestingUnitFullName,p.hijriRequestDate,p.remarks from WFTrainingPlanNeedData p " +
			" where (:P_REQUESTING_UNIT_ID = -1 or p.requestingUnitId = :P_REQUESTING_UNIT_ID)" +
			" and (:P_TRAINING_UNIT_ID = -1 or p.trainingUnitId = :P_TRAINING_UNIT_ID)" +
			" and (:P_TRAINING_YEAR_ID = -1 or p.trainingYearId = :P_TRAINING_YEAR_ID)" +
			" and (:P_REQUESTING_UNIT_REGION_ID = -1 or p.requestingUnitRegionId = :P_REQUESTING_UNIT_REGION_ID)" +
			" and (:P_COURSE_EVENT_ID = -1 or p.courseEventId = :P_COURSE_EVENT_ID)" +
			" and (p.instanceStatus <> 1)" +
			" and ((select count(t.id) from WFTask t where t.action = :P_EXCLUDED_ACTION and t.instanceId = p.instanceId )=0 )"),

})

@Entity
@Table(name = "ETR_VW_WF_TRAINING_PLANS_NEEDS")
public class WFTrainingPlanNeedData extends BaseEntity {
    private Long instanceId;
    private Integer instanceStatus;
    private Long requestingUnitId;
    private String requestingUnitFullName;
    private Long requestingUnitRegionId;
    private Long courseEventId;
    private Long courseId;
    private String courseName;
    private Long trainingUnitId;
    private String trainingUnitName;
    private Date plannedStartDate;
    private String plannedStartDateString;
    private Date plannedEndDate;
    private String plannedEndDateString;
    private String prerquisites;
    private Integer needsCount;
    private String remarks;
    private Date hijriRequestDate;
    private String hijriRequestDateString;
    private Long trainingYearId;
    private WFTrainingPlanNeed wfTrainingPlansNeed;

    public WFTrainingPlanNeedData() {
	wfTrainingPlansNeed = new WFTrainingPlanNeed();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	this.wfTrainingPlansNeed.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "INSTANCE_STATUS")
    public Integer getInstanceStatus() {
	return instanceStatus;
    }

    public void setInstanceStatus(Integer instanceStatus) {
	this.instanceStatus = instanceStatus;
    }

    @Id
    @Column(name = "REQUESTING_UNIT_ID")
    public Long getRequestingUnitId() {
	return requestingUnitId;
    }

    public void setRequestingUnitId(Long requestingUnitId) {
	this.requestingUnitId = requestingUnitId;
	this.wfTrainingPlansNeed.setRequestingUnitId(requestingUnitId);
    }

    @Basic
    @Column(name = "REQUESTING_UNIT_FULL_NAME")
    public String getRequestingUnitFullName() {
	return requestingUnitFullName;
    }

    public void setRequestingUnitFullName(String requestingUnitFullName) {
	this.requestingUnitFullName = requestingUnitFullName;
    }

    @Basic
    @Column(name = "REQUESTING_UNIT_REGION_ID")
    public Long getRequestingUnitRegionId() {
	return requestingUnitRegionId;
    }

    public void setRequestingUnitRegionId(Long requestingUnitRegionId) {
	this.requestingUnitRegionId = requestingUnitRegionId;
    }

    @Id
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
	this.wfTrainingPlansNeed.setCourseEventId(courseEventId);
    }

    @Basic
    @Column(name = "COURSE_ID")
    public Long getCourseId() {
	return courseId;
    }

    public void setCourseId(Long courseId) {
	this.courseId = courseId;
	this.wfTrainingPlansNeed.setCourseId(courseId);
    }

    @Basic
    @Column(name = "COURSE_NAME")
    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
	this.wfTrainingPlansNeed.setTrainingUnitId(trainingUnitId);
    }

    @Basic
    @Column(name = "TRAINING_UNIT_NAME")
    public String getTrainingUnitName() {
	return trainingUnitName;
    }

    public void setTrainingUnitName(String trainingUnitName) {
	this.trainingUnitName = trainingUnitName;
    }

    @Basic
    @Column(name = "PLANNED_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPlannedStartDate() {
	return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
	this.plannedStartDate = plannedStartDate;
	this.plannedStartDateString = HijriDateService.getHijriDateString(plannedStartDate);
	this.wfTrainingPlansNeed.setPlannedStartDate(plannedStartDate);
    }

    @Transient
    public String getPlannedStartDateString() {
	return plannedStartDateString;
    }

    public void setPlannedStartDateString(String plannedStartDateString) {
	this.plannedStartDateString = plannedStartDateString;
	this.plannedStartDate = HijriDateService.getHijriDate(plannedStartDateString);
	this.wfTrainingPlansNeed.setPlannedStartDate(plannedStartDate);
    }

    @Basic
    @Column(name = "PLANNED_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPlannedEndDate() {
	return plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
	this.plannedEndDate = plannedEndDate;
	this.plannedEndDateString = HijriDateService.getHijriDateString(plannedEndDate);
	this.wfTrainingPlansNeed.setPlannedEndDate(plannedEndDate);
    }

    @Transient
    public String getPlannedEndDateString() {
	return plannedEndDateString;
    }

    public void setPlannedEndDateString(String plannedEndDateString) {
	this.plannedEndDateString = plannedEndDateString;
	this.plannedEndDate = HijriDateService.getHijriDate(plannedEndDateString);
	this.wfTrainingPlansNeed.setPlannedEndDate(plannedEndDate);
    }

    @Basic
    @Column(name = "PRERQUISITES")
    public String getPrerquisites() {
	return prerquisites;
    }

    public void setPrerquisites(String prerquisites) {
	this.prerquisites = prerquisites;
	this.wfTrainingPlansNeed.setPrerquisites(prerquisites);
    }

    @Basic
    @Column(name = "NEEDS_COUNT")
    public Integer getNeedsCount() {
	return needsCount;
    }

    public void setNeedsCount(Integer needsCount) {
	this.needsCount = needsCount;
	this.wfTrainingPlansNeed.setNeedsCount(needsCount);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	this.wfTrainingPlansNeed.setRemarks(remarks);
    }

    @Transient
    public WFTrainingPlanNeed getWfTrainingPlansNeed() {
	return wfTrainingPlansNeed;
    }

    public void setWfTrainingPlansNeed(WFTrainingPlanNeed wfTrainingPlansNeed) {
	this.wfTrainingPlansNeed = wfTrainingPlansNeed;
    }

    @Basic
    @Column(name = "HIJRI_REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHijriRequestDate() {
	return hijriRequestDate;
    }

    public void setHijriRequestDate(Date hijriRequestDate) {
	this.hijriRequestDate = hijriRequestDate;
	if (this.hijriRequestDate != null) {
	    this.hijriRequestDateString = HijriDateService.getHijriDateString(hijriRequestDate);
	}
    }

    @Transient
    public String getHijriRequestDateString() {
	return hijriRequestDateString;
    }

    public void setHijriRequestDateString(String hijriRequestDateString) {
	this.hijriRequestDateString = hijriRequestDateString;
    }

    @Basic
    @Column(name = "TRAINING_YEAR_ID")
    public Long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(Long trainingYearId) {
	this.trainingYearId = trainingYearId;
	this.wfTrainingPlansNeed.setTrainingYearId(trainingYearId);

    }

}
