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
	@NamedQuery(name = "wf_trainingCourseEventData_searchWFTrainingCourseEventData",
		query = "select t from WFTrainingCourseEventData t , WFInstance i " +
			"where (t.instanceId = i.id)" +
			" and (:P_INSTANCE_ID = -1 or t.instanceId = :P_INSTANCE_ID)" +
			" and (:P_STATUS = -1 or i.status = :P_STATUS)" +
			" and (:P_COURSE_EVENT_ID =-1 or t.courseEventId = :P_COURSE_EVENT_ID)" +
			" and (:P_EXCLUDED_INSTANCE_ID = -1 or t.instanceId <> :P_EXCLUDED_INSTANCE_ID)"),

	@NamedQuery(name = "wf_trainingCourseEventData_searchWFTrainingCourseEventTasks",
		query = " select trn, t, i, p.processName, requester, " +
			" case when t.originalId <> t.assigneeId then " +
			" (delegator.firstName || ' ' || delegator.secondName || ' ' || delegator.thirdName || case when delegator.thirdName is null then '' else ' ' end || delegator.lastName) " +
			" else null end " +
			" from WFTrainingCourseEventData trn , WFTask t, WFInstance i, WFProcess p, EmployeeData requester, Employee delegator " +
			" where trn.instanceId = t.instanceId " +
			"   and trn.instanceId = i.id " +
			"   and i.processId = p.id " +
			"   and i.requesterId = requester.id " +
			"   and t.originalId = delegator.id " +
			"   and t.action is null " +
			"   and t.assigneeId = :P_ASSIGNEE_ID " +
			"   and t.assigneeWfRole in (:P_ASSIGNEE_WF_ROLES) " +
			"   order by t.taskId ")
})

@Entity
@Table(name = "ETR_VW_WF_TRN_COURSES_EVENTS")
public class WFTrainingCourseEventData extends BaseEntity {
    private Long id;
    private Long instanceId;
    private Long courseEventId;
    private String eventCourseName;
    private Date newStartDate;
    private String newStartDateString;
    private Date newEndDate;
    private String newEndDateString;
    private String reasons;
    private String notes;
    private Long trainingTransactionId;
    private Long employeeId;
    private Long employeeCategoryId;
    private String employeeName;
    private String employeeRankDesc;
    private String employeeNumber;
    private String employeeJobName;
    private String employeePhysicalUnitFullName;
    private String externalEmployeeName;
    private Long externalEmployeeRankId;
    private String externalEmployeeNumber;
    private String externalEmployeeJobName;
    private String externalEmployeePartyDesc;
    private String internalCopies;
    private String externalCopies;
    private WFTrainingCourseEvent wfTrainingCourseEvent;

    public WFTrainingCourseEventData() {
	wfTrainingCourseEvent = new WFTrainingCourseEvent();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.wfTrainingCourseEvent.setId(id);
    }

    @Basic
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	this.wfTrainingCourseEvent.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
	this.wfTrainingCourseEvent.setCourseEventId(courseEventId);
    }

    @Basic
    @Column(name = "EVENT_COURSE_NAME")
    public String getEventCourseName() {
	return eventCourseName;
    }

    public void setEventCourseName(String eventCourseName) {
	this.eventCourseName = eventCourseName;
    }

    @Basic
    @Column(name = "NEW_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewStartDate() {
	return newStartDate;
    }

    public void setNewStartDate(Date newStartDate) {
	this.newStartDate = newStartDate;
	this.newStartDateString = HijriDateService.getHijriDateString(newStartDate);
	this.wfTrainingCourseEvent.setNewStartDate(newStartDate);
    }

    @Transient
    public String getNewStartDateString() {
	return newStartDateString;
    }

    public void setNewStartDateString(String newStartDateString) {
	this.newStartDateString = newStartDateString;
	this.newStartDate = HijriDateService.getHijriDate(newStartDateString);
	this.wfTrainingCourseEvent.setNewStartDate(newStartDate);
    }

    @Basic
    @Column(name = "NEW_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewEndDate() {
	return newEndDate;
    }

    public void setNewEndDate(Date newEndDate) {
	this.newEndDate = newEndDate;
	this.newEndDateString = HijriDateService.getHijriDateString(newEndDate);
	this.wfTrainingCourseEvent.setNewEndDate(newEndDate);
    }

    @Transient
    public String getNewEndDateString() {
	return newEndDateString;
    }

    public void setNewEndDateString(String newEndDateString) {
	this.newEndDateString = newEndDateString;
	this.newEndDate = HijriDateService.getHijriDate(newEndDateString);
	this.wfTrainingCourseEvent.setNewEndDate(newEndDate);
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	this.wfTrainingCourseEvent.setReasons(reasons);
    }

    @Basic
    @Column(name = "NOTES")
    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
	this.wfTrainingCourseEvent.setNotes(notes);
    }

    @Basic
    @Column(name = "TRAINING_TRANSACTION_ID")
    public Long getTrainingTransactionId() {
	return trainingTransactionId;
    }

    public void setTrainingTransactionId(Long trainingTransactionId) {
	this.trainingTransactionId = trainingTransactionId;
	this.wfTrainingCourseEvent.setTrainingTransactionId(trainingTransactionId);
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "EMPLOYEE_CATEGORY_ID")
    public Long getEmployeeCategoryId() {
	return employeeCategoryId;
    }

    public void setEmployeeCategoryId(Long employeeCategoryId) {
	this.employeeCategoryId = employeeCategoryId;
    }

    @Basic
    @Column(name = "EMPLOYEE_NAME")
    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    @Basic
    @Column(name = "EMPLOYEE_RANK_DESC")
    public String getEmployeeRankDesc() {
	return employeeRankDesc;
    }

    public void setEmployeeRankDesc(String employeeRankDesc) {
	this.employeeRankDesc = employeeRankDesc;
    }

    @Basic
    @Column(name = "EMPLOYEE_NUMBER")
    public String getEmployeeNumber() {
	return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
	this.employeeNumber = employeeNumber;
    }

    @Basic
    @Column(name = "EMPLOYEE_JOB_NAME")
    public String getEmployeeJobName() {
	return employeeJobName;
    }

    public void setEmployeeJobName(String employeeJobName) {
	this.employeeJobName = employeeJobName;
    }

    @Basic
    @Column(name = "EMPLOYEE_PHYSCL_UNIT_FULL_NAME")
    public String getEmployeePhysicalUnitFullName() {
	return employeePhysicalUnitFullName;
    }

    public void setEmployeePhysicalUnitFullName(String employeePhysicalUnitFullName) {
	this.employeePhysicalUnitFullName = employeePhysicalUnitFullName;
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_NAME")
    public String getExternalEmployeeName() {
	return externalEmployeeName;
    }

    public void setExternalEmployeeName(String externalEmployeeName) {
	this.externalEmployeeName = externalEmployeeName;
	this.wfTrainingCourseEvent.setExternalEmployeeName(externalEmployeeName);
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_RANK_ID")
    public Long getExternalEmployeeRankId() {
	return externalEmployeeRankId;
    }

    public void setExternalEmployeeRankId(Long externalEmployeeRankId) {
	this.externalEmployeeRankId = externalEmployeeRankId;
	this.wfTrainingCourseEvent.setExternalEmployeeRankId(externalEmployeeRankId);
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_NUMBER")
    public String getExternalEmployeeNumber() {
	return externalEmployeeNumber;
    }

    public void setExternalEmployeeNumber(String externalEmployeeNumber) {
	this.externalEmployeeNumber = externalEmployeeNumber;
	this.wfTrainingCourseEvent.setExternalEmployeeNumber(externalEmployeeNumber);
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_JOB_NAME")
    public String getExternalEmployeeJobName() {
	return externalEmployeeJobName;
    }

    public void setExternalEmployeeJobName(String externalEmployeeJobName) {
	this.externalEmployeeJobName = externalEmployeeJobName;
	this.wfTrainingCourseEvent.setExternalEmployeeJobName(externalEmployeeJobName);
    }

    @Basic
    @Column(name = "EXT_EMPLOYEE_PARTY_DESC")
    public String getExternalEmployeePartyDesc() {
	return externalEmployeePartyDesc;
    }

    public void setExternalEmployeePartyDesc(String externalEmployeePartyDesc) {
	this.externalEmployeePartyDesc = externalEmployeePartyDesc;
	this.wfTrainingCourseEvent.setExternalEmployeePartyDesc(externalEmployeePartyDesc);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	this.wfTrainingCourseEvent.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	this.wfTrainingCourseEvent.setExternalCopies(externalCopies);
    }

    @Transient
    public WFTrainingCourseEvent getWfTrainingCourseEvent() {
	return wfTrainingCourseEvent;
    }

    public void setWfTrainingCourseEvent(WFTrainingCourseEvent wfTrainingCourseEvent) {
	this.wfTrainingCourseEvent = wfTrainingCourseEvent;
    }

}
