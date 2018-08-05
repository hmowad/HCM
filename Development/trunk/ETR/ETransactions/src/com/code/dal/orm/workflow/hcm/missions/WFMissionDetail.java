package com.code.dal.orm.workflow.hcm.missions;

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

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * 
 * Provides all information regarding mission detail workflow such as mission employee id, mission period, start date, and end date, .. etc <br/>
 * There is one or more {@link WFMissionDetail} for every {@link WFMission} linked by missionId
 */

@NamedQueries({
	@NamedQuery(name = "wf_missionDetails_searchWFMissionDetailsInstancesDataByBeneficiaryId",
		query = " select i from WFMissionDetail md, WFInstanceData i " +
			" where md.instanceId = i.instanceId " +
			" and md.empId = :P_BENEFICIARY_ID " +
			" and i.status in (:P_STATUS_VALUES) " +
			" order by i.requestDate desc "),

	@NamedQuery(name = "wf_missionDetails_searchWFMissionDetailsTasksDataByBeneficiaryId",
		query = " select t from WFMissionDetail md, WFTaskData t " +
			" where md.instanceId = t.instanceId " +
			" and md.empId = :P_BENEFICIARY_ID " +
			" and t.assigneeId = :P_ASSIGNEE_ID" +
			" and ((:P_RUNNING = 1 and t.action is null) OR (:P_RUNNING = 0 and t.action is not null))" +
			" and (:P_TASK_ROLE = -1 or (:P_TASK_ROLE = 1 and t.assigneeWfRole <> 'Notification') or (:P_TASK_ROLE = 2 and t.assigneeWfRole = 'Notification') ) " +
			" order by t.assignDate "),

	@NamedQuery(name = "wf_missionDetail_validateRunningProcesses",
		query = " select md from WFMissionDetail md, WFInstance i " +
			" where md.instanceId = i.instanceId " +
			" and md.empId in (:P_BENEFICIARY_IDS) " +
			" and ( :P_DATES_FLAG = -1 or ( " +
			" (to_date(:P_START_DATE, 'MI/MM/YYYY') between md.startDate and md.endDate) " +
			"      or (to_date(:P_END_DATE, 'MI/MM/YYYY') between md.startDate and md.endDate) " +
			"      or (to_date(:P_START_DATE, 'MI/MM/YYYY') <= md.startDate and to_date(:P_END_DATE, 'MI/MM/YYYY') >= md.endDate) " +
			" )) " +
			" and i.instanceId <> :P_EXCLUDED_INSTANCE_ID " +
			" and i.status = 1 ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "WF_MISSIONS_DETAILS")
public class WFMissionDetail extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long instanceId;
    private Long empId;
    private Integer balance;
    private Integer period;
    private Integer roadPeriod;
    private Date startDate;
    private Date endDate;
    private String exceptionalApprovalNumber;
    private Date exceptionalApprovalDate;
    private String roadLine;
    private String remarks;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Id
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "BALANCE")
    public Integer getBalance() {
	return balance;
    }

    public void setBalance(Integer balance) {
	this.balance = balance;
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    @Basic
    @Column(name = "ROAD_PERIOD")
    public Integer getRoadPeriod() {
	return roadPeriod;
    }

    public void setRoadPeriod(Integer roadPeriod) {
	this.roadPeriod = roadPeriod;
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    @Basic
    @Column(name = "EXCEPTIONAL_APPROVAL_NUMBER")
    public String getExceptionalApprovalNumber() {
	return exceptionalApprovalNumber;
    }

    public void setExceptionalApprovalNumber(String exceptionalApprovalNumber) {
	this.exceptionalApprovalNumber = exceptionalApprovalNumber;
    }

    @Basic
    @Column(name = "EXCEPTIONAL_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExceptionalApprovalDate() {
	return exceptionalApprovalDate;
    }

    public void setExceptionalApprovalDate(Date exceptionalApprovalDate) {
	this.exceptionalApprovalDate = exceptionalApprovalDate;
    }

    @Basic
    @Column(name = "ROAD_LINE")
    public String getRoadLine() {
	return roadLine;
    }

    public void setRoadLine(String roadLine) {
	this.roadLine = roadLine;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Override
    public Long calculateContentId() {
	return instanceId;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"period:" + period + AUDIT_SEPARATOR +
		"startDate:" + startDate + AUDIT_SEPARATOR +
		"exceptionalApprovalNumber:" + exceptionalApprovalNumber + AUDIT_SEPARATOR +
		"exceptionalApprovalDate:" + exceptionalApprovalDate + AUDIT_SEPARATOR +
		"roadLine:" + roadLine + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR;
    }
}
