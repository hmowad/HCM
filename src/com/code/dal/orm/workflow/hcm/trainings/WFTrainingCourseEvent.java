package com.code.dal.orm.workflow.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "WF_TRAINING_COURSES_EVENTS")
public class WFTrainingCourseEvent extends BaseEntity {
    private Long id;
    private Long instanceId;
    private Long courseEventId;
    private Date newStartDate;
    private Date newEndDate;
    private String reasons;
    private String notes;
    private Long trainingTransactionId;
    private String externalEmployeeName;
    private Long externalEmployeeRankId;
    private String externalEmployeeNumber;
    private String externalEmployeeJobName;
    private String externalEmployeePartyDesc;
    private String internalCopies;
    private String externalCopies;

    @SequenceGenerator(name = "WFGeneralSeq",
	    sequenceName = "WF_GENERAL_SEQ")
    @GeneratedValue(generator = "WFGeneralSeq")
    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
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
    @Column(name = "NEW_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewStartDate() {
	return newStartDate;
    }

    public void setNewStartDate(Date newStartDate) {
	this.newStartDate = newStartDate;
    }

    @Basic
    @Column(name = "NEW_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewEndDate() {
	return newEndDate;
    }

    public void setNewEndDate(Date newEndDate) {
	this.newEndDate = newEndDate;
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    @Basic
    @Column(name = "NOTES")
    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    @Basic
    @Column(name = "TRAINING_TRANSACTION_ID")
    public Long getTrainingTransactionId() {
	return trainingTransactionId;
    }

    public void setTrainingTransactionId(Long trainingTransactionId) {
	this.trainingTransactionId = trainingTransactionId;
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_NAME")
    public String getExternalEmployeeName() {
	return externalEmployeeName;
    }

    public void setExternalEmployeeName(String externalEmployeeName) {
	this.externalEmployeeName = externalEmployeeName;
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_RANK_ID")
    public Long getExternalEmployeeRankId() {
	return externalEmployeeRankId;
    }

    public void setExternalEmployeeRankId(Long externalEmployeeRankId) {
	this.externalEmployeeRankId = externalEmployeeRankId;
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_NUMBER")
    public String getExternalEmployeeNumber() {
	return externalEmployeeNumber;
    }

    public void setExternalEmployeeNumber(String externalEmployeeNumber) {
	this.externalEmployeeNumber = externalEmployeeNumber;
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_JOB_NAME")
    public String getExternalEmployeeJobName() {
	return externalEmployeeJobName;
    }

    public void setExternalEmployeeJobName(String externalEmployeeJobName) {
	this.externalEmployeeJobName = externalEmployeeJobName;
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_PARTY_DESC")
    public String getExternalEmployeePartyDesc() {
	return externalEmployeePartyDesc;
    }

    public void setExternalEmployeePartyDesc(String externalEmployeePartyDesc) {
	this.externalEmployeePartyDesc = externalEmployeePartyDesc;
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }
}
