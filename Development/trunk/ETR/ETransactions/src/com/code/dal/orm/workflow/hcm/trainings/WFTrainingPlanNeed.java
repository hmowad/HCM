package com.code.dal.orm.workflow.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "WF_TRAINING_PLANS_NEEDS")
public class WFTrainingPlanNeed extends BaseEntity {
    private Long instanceId;
    private Long requestingUnitId;
    private Long courseEventId;
    private Long courseId;
    private Long trainingUnitId;
    private Date plannedStartDate;
    private Date plannedEndDate;
    private String prerquisites;
    private Integer needsCount;
    private String remarks;
    private Long trainingYearId;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Id
    @Column(name = "REQUESTING_UNIT_ID")
    public Long getRequestingUnitId() {
	return requestingUnitId;
    }

    public void setRequestingUnitId(Long requestingUnitId) {
	this.requestingUnitId = requestingUnitId;
    }

    @Id
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
    }

    @Basic
    @Column(name = "COURSE_ID")
    public Long getCourseId() {
	return courseId;
    }

    public void setCourseId(Long courseId) {
	this.courseId = courseId;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    @Basic
    @Column(name = "PLANNED_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPlannedStartDate() {
	return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
	this.plannedStartDate = plannedStartDate;
    }

    @Basic
    @Column(name = "PLANNED_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPlannedEndDate() {
	return plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
	this.plannedEndDate = plannedEndDate;
    }

    @Basic
    @Column(name = "PRERQUISITES")
    public String getPrerquisites() {
	return prerquisites;
    }

    public void setPrerquisites(String prerquisites) {
	this.prerquisites = prerquisites;
    }

    @Basic
    @Column(name = "NEEDS_COUNT")
    public Integer getNeedsCount() {
	return needsCount;
    }

    public void setNeedsCount(Integer needsCount) {
	this.needsCount = needsCount;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Basic
    @Column(name = "TRAINING_YEAR_ID")
    public Long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(Long trainingYearId) {
	this.trainingYearId = trainingYearId;
    }
}
