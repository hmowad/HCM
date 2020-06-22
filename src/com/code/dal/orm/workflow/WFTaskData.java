package com.code.dal.orm.workflow;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>WFTaskData</code> class represents the WorkFlow task data for any instance <code>WFInstance</code> of any process <code>WFProcess</code> in detailed format.</br>
 * The task either needs a human interaction (like Approve, Reject and etc... ) or just a notification.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_taskData_searchWFTasksData",
		query = " select t from WFTaskData t, WFInstanceBeneficiary b" +
			" where t.instanceId = b.instanceId " +
			"   and b.beneficiaryId = (select max(ib.beneficiaryId) from WFInstanceBeneficiary ib where ib.instanceId = t.instanceId and (:P_BENEFICIRY_ID = -1 OR ib.beneficiaryId = :P_BENEFICIRY_ID)) " +
			"   and t.assigneeId = :P_ASSIGNEE_ID " +
			"   and t.taskOwnerName like :P_TASK_OWNER_NAME " +
			"   and (:P_PROCESS_GROUP_ID = 0 OR t.processGroupId = :P_PROCESS_GROUP_ID) " +
			"   and (:P_PROCESS_ID = 0 OR t.processId = :P_PROCESS_ID) " +
			"   and ((:P_RUNNING = 1 and t.action is null) OR (:P_RUNNING = 0 and t.action is not null))" +
			"   and (:P_TASK_ROLE = -1 or (:P_TASK_ROLE = 1 and t.assigneeWfRole <> 'Notification') or (:P_TASK_ROLE = 2 and t.assigneeWfRole = 'Notification') ) " +
			" order by t.assignDate "),

	@NamedQuery(name = "wf_taskData_getWFInstanceTasksData",
		query = " select t from WFTaskData t " +
			" where t.instanceId = :P_INSTANCE_ID " +
			" and (:P_TASK_ROLE = -1 or (:P_TASK_ROLE = 1 and t.assigneeWfRole <> 'Notification') ) " +
			" order by t.level, t.assignDate "),

	@NamedQuery(name = "wf_taskData_getWFInstanceCompletedTasksData",
		query = " select t from WFTaskData t " +
			" where t.instanceId = :P_INSTANCE_ID" +
			"   and t.action is not null " +
			"   and t.taskId < :P_TASK_ID " +
			"   and (:P_LEVELS_FLAG = -1 or (:P_LEVELS_FLAG = 1 and t.level in (:P_LEVELS))) " +
			" order by t.level, t.assignDate "),

	@NamedQuery(name = "wf_taskData_getWFInstanceCompletedTasksDataOrderedByLevelLength",
		query = " select t from WFTaskData t " +
			" where t.instanceId = :P_INSTANCE_ID" +
			"   and t.action is not null " +
			"   and t.taskId < :P_TASK_ID " +
			" order by LENGTH(t.level), t.level, t.assignDate "),

	@NamedQuery(name = "wf_taskData_getWFTaskDataById",
		query = " select t from WFTaskData t " +
			" where t.taskId = :P_TASK_ID ")
})
@Entity
@Table(name = "ETR_VW_TASKS")
public class WFTaskData extends BaseEntity {
    private Long taskId;
    private Long instanceId;
    private Long processId;
    private String processName;
    private Long processGroupId;
    private String taskOwnerName;
    private String requesterReferenceName;
    private String requesterReferenceRankDesc;
    private Long assigneeId;
    private Long delegatingId;
    private String delegatingName;
    private Long originalId;
    private String originalNumber;
    private String originalName;
    private String originalRankDesc;
    private String originalJobDesc;
    private String originalUnitFullName;
    private Long originalCategoryId;
    private Date assignDate;
    private Date hijriAssignDate;
    private String hijriAssignDateString;
    private String taskUrl;
    private String assigneeWfRole;
    private String action;
    private Date actionDate;
    private Date hijriActionDate;
    private String hijriActionDateString;
    private String notes;
    private String refuseReasons;
    private String attachments;
    private String flexField1;
    private String flexField2;
    private String flexField3;
    private String level;
    private Long originalUnitId;

    private Long externalLocationId;
    private Boolean emailNotified;
    private Integer priority;
    private String arabicDetailsSummary;
    private String englishDetailsSummary;
    private Long stoppingPoint;

    private String summary;
    private String empNo;
    private String taskOwnerEmpNo;

    public void setTaskId(Long taskId) {
	this.taskId = taskId;
    }

    @Id
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

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    @Basic
    @Column(name = "PROCESS_ID")
    public Long getProcessId() {
	return processId;
    }

    public void setProcessName(String processName) {
	this.processName = processName;
    }

    @Basic
    @Column(name = "PROCESS_NAME")
    public String getProcessName() {
	return processName;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    @Basic
    @Column(name = "PROCESS_GROUP_ID")
    @XmlTransient
    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setTaskOwnerName(String taskOwnerName) {
	this.taskOwnerName = taskOwnerName;
    }

    @Basic
    @Column(name = "TASK_OWNER_NAME")
    public String getTaskOwnerName() {
	return taskOwnerName;
    }

    public void setRequesterReferenceName(String requesterReferenceName) {
	this.requesterReferenceName = requesterReferenceName;
    }

    @Basic
    @Column(name = "REQUESTER_REF_NAME")
    @XmlTransient
    public String getRequesterReferenceName() {
	return requesterReferenceName;
    }

    public void setRequesterReferenceRankDesc(String requesterReferenceRankDesc) {
	this.requesterReferenceRankDesc = requesterReferenceRankDesc;
    }

    @Basic
    @Column(name = "REQUESTER_REF_RANK_DESC")
    @XmlTransient
    public String getRequesterReferenceRankDesc() {
	return requesterReferenceRankDesc;
    }

    public void setAssigneeId(Long assigneeId) {
	this.assigneeId = assigneeId;
    }

    @Basic
    @Column(name = "ASSIGNEE_ID")
    @XmlTransient
    public Long getAssigneeId() {
	return assigneeId;
    }

    public void setDelegatingId(Long delegatingId) {
	this.delegatingId = delegatingId;
    }

    @Basic
    @Column(name = "DELEGATING_ID")
    @XmlElement(nillable = true)
    public Long getDelegatingId() {
	return delegatingId;
    }

    public void setDelegatingName(String delegatingName) {
	this.delegatingName = delegatingName;
    }

    @Basic
    @Column(name = "DELEGATING_NAME")
    @XmlElement(nillable = true)
    public String getDelegatingName() {
	return delegatingName;
    }

    public void setOriginalId(Long originalId) {
	this.originalId = originalId;
    }

    @Basic
    @Column(name = "ORIGINAL_ID")
    @XmlTransient
    public Long getOriginalId() {
	return originalId;
    }

    public void setOriginalNumber(String originalNumber) {
	this.originalNumber = originalNumber;
    }

    @Basic
    @Column(name = "ORIGINAL_NUMBER")
    @XmlTransient
    public String getOriginalNumber() {
	return originalNumber;
    }

    public void setOriginalName(String originalName) {
	this.originalName = originalName;
    }

    @Basic
    @Column(name = "ORIGINAL_NAME")
    @XmlTransient
    public String getOriginalName() {
	return originalName;
    }

    public void setOriginalRankDesc(String originalRankDesc) {
	this.originalRankDesc = originalRankDesc;
    }

    @Basic
    @Column(name = "ORIGINAL_RANK_DESC")
    @XmlTransient
    public String getOriginalRankDesc() {
	return originalRankDesc;
    }

    public void setOriginalJobDesc(String originalJobDesc) {
	this.originalJobDesc = originalJobDesc;
    }

    @Basic
    @Column(name = "ORIGINAL_JOB_DESC")
    @XmlTransient
    public String getOriginalJobDesc() {
	return originalJobDesc;
    }

    public void setOriginalUnitFullName(String originalUnitFullName) {
	this.originalUnitFullName = originalUnitFullName;
    }

    @Basic
    @Column(name = "ORIGINAL_UNIT_FULL_NAME")
    @XmlTransient
    public String getOriginalUnitFullName() {
	return originalUnitFullName;
    }

    public void setOriginalCategoryId(Long originalCategoryId) {
	this.originalCategoryId = originalCategoryId;
    }

    @Basic
    @Column(name = "ORIGINAL_CATEGORY_ID")
    @XmlTransient
    public Long getOriginalCategoryId() {
	return originalCategoryId;
    }

    public void setAssignDate(Date assignDate) {
	this.assignDate = assignDate;

	if (this.assignDate != null && this.hijriAssignDate != null) {
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    this.hijriAssignDateString = HijriDateService.getHijriDateString(hijriAssignDate) + " "
		    + sdf.format(assignDate);
	}
    }

    @Basic
    @Column(name = "ASSIGN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getAssignDate() {
	return assignDate;
    }

    public void setHijriAssignDate(Date hijriAssignDate) {
	this.hijriAssignDate = hijriAssignDate;

	if (this.assignDate != null && this.hijriAssignDate != null) {
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    this.hijriAssignDateString = HijriDateService.getHijriDateString(hijriAssignDate) + " "
		    + sdf.format(assignDate);
	}
    }

    @Basic
    @Column(name = "HIJRI_ASSIGN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getHijriAssignDate() {
	return hijriAssignDate;
    }

    public void setTaskUrl(String taskUrl) {
	this.taskUrl = taskUrl;
    }

    @Basic
    @Column(name = "TASK_URL")
    @XmlTransient
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
    @XmlTransient
    public String getAction() {
	return action;
    }

    public void setActionDate(Date actionDate) {
	this.actionDate = actionDate;

	if (this.actionDate != null && this.hijriActionDate != null) {
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    this.hijriActionDateString = HijriDateService.getHijriDateString(hijriActionDate) + " "
		    + sdf.format(actionDate);
	}
    }

    @Basic
    @Column(name = "ACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getActionDate() {
	return actionDate;
    }

    public void setHijriActionDate(Date hijriActionDate) {
	this.hijriActionDate = hijriActionDate;

	if (this.actionDate != null && this.hijriActionDate != null) {
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    this.hijriActionDateString = HijriDateService.getHijriDateString(hijriActionDate) + " "
		    + sdf.format(actionDate);
	}
    }

    @Basic
    @Column(name = "HIJRI_ACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getHijriActionDate() {
	return hijriActionDate;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    @Basic
    @Column(name = "NOTES")
    @XmlTransient
    public String getNotes() {
	return notes;
    }

    public void setRefuseReasons(String refuseReasons) {
	this.refuseReasons = refuseReasons;
    }

    @Basic
    @Column(name = "REFUSE_REASONS")
    @XmlTransient
    public String getRefuseReasons() {
	return refuseReasons;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    @XmlTransient
    public String getAttachments() {
	return attachments;
    }

    public void setFlexField1(String flexField1) {
	this.flexField1 = flexField1;
    }

    @Basic
    @Column(name = "FLEX_FIELD1")
    @XmlTransient
    public String getFlexField1() {
	return flexField1;
    }

    public void setFlexField2(String flexField2) {
	this.flexField2 = flexField2;
    }

    @Basic
    @Column(name = "FLEX_FIELD2")
    @XmlTransient
    public String getFlexField2() {
	return flexField2;
    }

    public void setFlexField3(String flexField3) {
	this.flexField3 = flexField3;
    }

    @Basic
    @Column(name = "FLEX_FIELD3")
    @XmlTransient
    public String getFlexField3() {
	return flexField3;
    }

    public void setLevel(String level) {
	this.level = level;
    }

    @Basic
    @Column(name = "TASK_LEVEL")
    @XmlTransient
    public String getLevel() {
	return level;
    }

    @Basic
    @Column(name = "ORIGINAL_UNIT_ID")
    public Long getOriginalUnitId() {
	return originalUnitId;
    }

    public void setOriginalUnitId(Long originalUnitId) {
	this.originalUnitId = originalUnitId;
    }

    public void setHijriAssignDateString(String hijriAssignDateString) {
	this.hijriAssignDateString = hijriAssignDateString;
    }

    @Transient
    @XmlTransient
    public String getHijriAssignDateString() {
	return hijriAssignDateString;
    }

    public void setHijriActionDateString(String hijriActionDateString) {
	this.hijriActionDateString = hijriActionDateString;
    }

    @Transient
    @XmlTransient
    public String getHijriActionDateString() {
	return hijriActionDateString;
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
    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

    @Transient
    @XmlTransient
    public String getEmpNo() {
	return empNo;
    }

    public void setEmpNo(String empNo) {
	this.empNo = empNo;
    }

    @Transient
    @XmlTransient
    public String getTaskOwnerEmpNo() {
	return taskOwnerEmpNo;
    }

    public void setTaskOwnerEmpNo(String taskOwnerEmpNo) {
	this.taskOwnerEmpNo = taskOwnerEmpNo;
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