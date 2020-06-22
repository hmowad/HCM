package com.code.dal.orm.workflow;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>WFTask</code> class represents the WorkFlow task data for any instance <code>WFInstance</code> of any process <code>WFProcess</code>.</br>
 * The task either needs a human interaction (like Approve, Reject and etc... ) or just a notification.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_task_getWFTaskById",
		query = " select t from WFTask t " +
			" where t.taskId = :P_TASK_ID "),

	@NamedQuery(name = "wf_task_getWFTasksByIds",
		query = " select t from WFTask t " +
			" where t.taskId in (:P_TASKS_IDS) " +
			" order by t.instanceId "),

	@NamedQuery(name = "wf_task_getWFInstanceTasks",
		query = " select t from WFTask t " +
			" where t.instanceId = :P_INSTANCE_ID " +
			" order by t.assignDate "),

	@NamedQuery(name = "wf_task_getWFInstanceTasksByRole",
		query = " select t from WFTask t " +
			" where t.instanceId = :P_INSTANCE_ID " +
			"   and t.assigneeWfRole = :P_ROLE " +
			" order by t.assignDate "),

	@NamedQuery(name = "wf_task_canChangeWFInstanceStatusToDone",
		query = " select count(t.id) from WFTask t " +
			" where t.instanceId = :P_INSTANCE_ID " +
			"   and t.action is NULL " +
			"   and t.assigneeWfRole <> 'Notification' "),

	@NamedQuery(name = "wf_task_canChangeWFInstanceStatusToComplete",
		query = " select count(t.id) from WFTask t " +
			" where t.instanceId = :P_INSTANCE_ID " +
			"   and t.action is NULL "),

	@NamedQuery(name = "wf_task_countTasks",
		query = " select count(t.taskId) from WFTask t " +
			" where t.assigneeId = :P_ASSIGNEE_ID" +
			" and((:P_NOTIFICATION_FLAG = -1 ) or (:P_NOTIFICATION_FLAG = 1 and t.assigneeWfRole = :P_NOTIFICATION_ROLE ) or (:P_NOTIFICATION_FLAG = 0 and t.assigneeWfRole <> :P_NOTIFICATION_ROLE ) )" +
			" and t.action is null"),

	@NamedQuery(name = "wf_task_taskSecurity",
		query = " select distinct t.taskUrl from WFTask t " +
			" where t.assigneeId = :P_ASSIGNEE_ID " +
			" and t.taskUrl like :P_TASK_URL " +
			" and (:P_TASK_ID = -1 or t.taskId = :P_TASK_ID) " +
			" and (:P_RUNNING = -1 or t.action is null) "),

	@NamedQuery(name = "wf_task_getRunningWFTasksForInvalidationByAssigneesOrOriginals",
		query = " select t2, i from WFTask t1, WFInstance i, WFTask t2 " +
			" where (t1.assigneeId in (:P_ASSIGNEES_IDS_ORIGINALS_IDS) or t1.originalId in (:P_ASSIGNEES_IDS_ORIGINALS_IDS)) " +
			" and t1.instanceId = i.instanceId " +
			" and t2.instanceId = i.instanceId " +
			" and t1.action is null " +
			" and t1.assigneeWfRole <> 'Notification' " +
			" and t2.action is null " +
			" and t2.assigneeWfRole <> 'Notification' " +
			" order by i.instanceId "),

	@NamedQuery(name = "wf_task_getRunningWFTasksForInvalidationByProcesses",
		query = " select t, i from WFTask t, WFInstance i " +
			" where t.instanceId = i.instanceId  " +
			" and i.status = 1 " +
			" and i.processId in (:P_PROCESSES_IDS) " +
			" and t.action is null " +
			" order by i.instanceId "),

	@NamedQuery(name = "wf_task_getWFInstanceCompletedTasksByLevelAndOriginalId",
		query = "select t from WFTask t "
			+ " where t.instanceId = :P_INSTANCE_ID "
			+ " and t.level = :P_LEVEL "
			+ " and t.originalId = :P_ORIGINAL_ID "
			+ " and t.action is not null "
			+ " order by t.assignDate desc"),

	@NamedQuery(name = "wf_task_getUnassignedWFTasks",
		query = "select t from WFTask t "
			+ " where t.originalId is null "
			+ " AND (:P_INSTANCE_ID = -1 OR t.instanceId = :P_INSTANCE_ID) ")
})
@Entity
@Table(name = "WF_TASKS")
public class WFTask extends AuditEntity implements Serializable, UpdatableAuditEntity {
    private Long taskId;
    private Long instanceId;
    private Long assigneeId;
    private Long originalId;
    private Date assignDate;
    private Date hijriAssignDate;
    private String taskUrl;
    private String assigneeWfRole;
    private String action;
    private Date actionDate;
    private Date hijriActionDate;
    private String notes;
    private String refuseReasons;
    private String attachments;
    private String flexField1;
    private String flexField2;
    private String flexField3;
    private String level;
    private Long version;
    private Long originalUnitId;

    // For mapping to wftask table for eservices
    private String hijriAssignDateString;
    private String hijriActionDateString;
    private Long externalLocationId;
    private Boolean emailNotified;
    private Integer priority;
    private Long processGroupId;
    private String arabicDetailsSummary;
    private String englishDetailsSummary;
    private Long stoppingPoint;

    public void setTaskId(Long taskId) {
	this.taskId = taskId;
    }

    @SequenceGenerator(name = "WFTasksSeq",
	    sequenceName = "WF_TASKS_SEQ")
    @Id
    @GeneratedValue(generator = "WFTasksSeq")
    @Column(name = "ID")
    public Long getTaskId() {
	return taskId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setAssigneeId(Long assigneeId) {
	this.assigneeId = assigneeId;
    }

    @Basic
    @Column(name = "ASSIGNEE_ID")
    public Long getAssigneeId() {
	return assigneeId;
    }

    public void setOriginalId(Long originalId) {
	this.originalId = originalId;
    }

    @Basic
    @Column(name = "ORIGINAL_ID")
    public Long getOriginalId() {
	return originalId;
    }

    public void setAssignDate(Date assignDate) {
	this.assignDate = assignDate;
    }

    @Basic
    @Column(name = "ASSIGN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getAssignDate() {
	return assignDate;
    }

    public void setHijriAssignDate(Date hijriAssignDate) {
	this.hijriAssignDate = hijriAssignDate;
    }

    @Basic
    @Column(name = "HIJRI_ASSIGN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHijriAssignDate() {
	return hijriAssignDate;
    }

    public void setTaskUrl(String taskUrl) {
	this.taskUrl = taskUrl;
    }

    @Basic
    @Column(name = "TASK_URL")
    public String getTaskUrl() {
	return taskUrl;
    }

    public void setAssigneeWfRole(String assigneeWfRole) {
	this.assigneeWfRole = assigneeWfRole;
    }

    @Basic
    @Column(name = "ASSIGNEE_WF_ROLE")
    public String getAssigneeWfRole() {
	return assigneeWfRole;
    }

    public void setAction(String action) {
	this.action = action;
    }

    @Basic
    @Column(name = "ACTION")
    public String getAction() {
	return action;
    }

    public void setActionDate(Date actionDate) {
	this.actionDate = actionDate;
    }

    @Basic
    @Column(name = "ACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getActionDate() {
	return actionDate;
    }

    public void setHijriActionDate(Date hijriActionDate) {
	this.hijriActionDate = hijriActionDate;
    }

    @Basic
    @Column(name = "HIJRI_ACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHijriActionDate() {
	return hijriActionDate;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    @Basic
    @Column(name = "NOTES")
    public String getNotes() {
	return notes;
    }

    public void setRefuseReasons(String refuseReasons) {
	this.refuseReasons = refuseReasons;
    }

    @Basic
    @Column(name = "REFUSE_REASONS")
    public String getRefuseReasons() {
	return refuseReasons;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setFlexField1(String flexField1) {
	this.flexField1 = flexField1;
    }

    @Basic
    @Column(name = "FLEX_FIELD1")
    public String getFlexField1() {
	return flexField1;
    }

    public void setFlexField2(String flexField2) {
	this.flexField2 = flexField2;
    }

    @Basic
    @Column(name = "FLEX_FIELD2")
    public String getFlexField2() {
	return flexField2;
    }

    public void setFlexField3(String flexField3) {
	this.flexField3 = flexField3;
    }

    @Basic
    @Column(name = "FLEX_FIELD3")
    public String getFlexField3() {
	return flexField3;
    }

    public void setLevel(String level) {
	this.level = level;
    }

    @Basic
    @Column(name = "TASK_LEVEL")
    public String getLevel() {
	return level;
    }

    public void setVersion(Long version) {
	this.version = version;
    }

    @Version
    @Column(name = "VERSION")
    /*
     * This attribute handles the optimistic transaction management for the task entity.
     */
    public Long getVersion() {
	return version;
    }

    @Basic
    @Column(name = "ORIGINAL_UNIT_ID")
    public Long getOriginalUnitId() {
	return originalUnitId;
    }

    public void setOriginalUnitId(Long originalUnitId) {
	this.originalUnitId = originalUnitId;
    }

    @Override
    public Long calculateContentId() {
	return taskId;
    }

    @Override
    public String calculateContent() {
	return "assigneeId:" + assigneeId + AUDIT_SEPARATOR;
    }

    @Transient
    @XmlTransient
    public String getHijriAssignDateString() {
	return hijriAssignDateString;
    }

    public void setHijriAssignDateString(String hijriAssignDateString) {
	this.hijriAssignDateString = hijriAssignDateString;
    }

    @Transient
    @XmlTransient
    public String getHijriActionDateString() {
	return hijriActionDateString;
    }

    public void setHijriActionDateString(String hijriActionDateString) {
	this.hijriActionDateString = hijriActionDateString;
    }

    @Transient
    @XmlTransient
    public Long getExternalLocationId() {
	return externalLocationId;
    }

    public void setExternalLocationId(Long externalLocationId) {
	this.externalLocationId = externalLocationId;
    }

    @Transient
    @XmlTransient
    public Boolean getEmailNotified() {
	return emailNotified;
    }

    public void setEmailNotified(Boolean emailNotified) {
	this.emailNotified = emailNotified;
    }

    @Transient
    @XmlTransient
    public Integer getPriority() {
	return priority;
    }

    public void setPriority(Integer priority) {
	this.priority = priority;
    }

    @Transient
    @XmlTransient
    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    @Transient
    @XmlTransient
    public String getArabicDetailsSummary() {
	return arabicDetailsSummary;
    }

    public void setArabicDetailsSummary(String arabicDetailsSummary) {
	this.arabicDetailsSummary = arabicDetailsSummary;
    }

    @Transient
    @XmlTransient
    public String getEnglishDetailsSummary() {
	return englishDetailsSummary;
    }

    public void setEnglishDetailsSummary(String englishDetailsSummary) {
	this.englishDetailsSummary = englishDetailsSummary;
    }

    @Transient
    @XmlTransient
    public Long getStoppingPoint() {
	return stoppingPoint;
    }

    public void setStoppingPoint(Long stoppingPoint) {
	this.stoppingPoint = stoppingPoint;
    }

}