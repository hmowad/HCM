package com.code.dal.orm.workflow.hcm.terminations;

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

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "wf_terminationData_getWFTerminationByInstanceId",
		query = " select t from WFTerminationData t " +
			" where t.instanceId = :P_INSTANCE_ID "),

	@NamedQuery(name = "wf_terminationData_getTerminationsTasks",
		query = " select  w, t, i, p.processName, requester, " +
			" case when t.originalId <> t.assigneeId then " +
			" (delegator.firstName || ' ' || delegator.secondName || ' ' || delegator.thirdName || case when delegator.thirdName is null then '' else ' ' end || delegator.lastName) " +
			" else null end " +
			" from WFTerminationData w, WFTask t, WFInstance i, WFProcess p, EmployeeData requester, Employee delegator " +
			" where w.instanceId 	= t.instanceId " +
			"   and w.instanceId 	= i.id " +
			"   and i.processId  	= p.id " +
			"   and i.requesterId 	= requester.id " +
			"   and t.originalId 	= delegator.id " +
			"   and t.action is null " +
			"   and t.assigneeId       = :P_ASSIGNEE_ID " +
			"   and t.assigneeWfRole   in (:P_ASSIGNEE_WF_ROLES) " +
			"   order by t.taskId ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "ETR_VW_WF_SERVICE_TERMINATIONS")
public class WFTerminationData extends BaseEntity {

    private Long instanceId;
    private Long recordId;
    private Date desiredTerminationDate;
    private String desiredTerminationDateString;
    private String reasons;

    private WFTermination wfTermination;

    public WFTerminationData() {
	wfTermination = new WFTermination();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	wfTermination.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "STE_RECORD_ID")
    public Long getRecordId() {
	return recordId;
    }

    public void setRecordId(Long recordId) {
	this.recordId = recordId;
	wfTermination.setRecordId(recordId);
    }

    @Basic
    @Column(name = "DESIRED_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDesiredTerminationDate() {
	return desiredTerminationDate;
    }

    public void setDesiredTerminationDate(Date desiredTerminationDate) {
	this.desiredTerminationDate = desiredTerminationDate;
	this.desiredTerminationDateString = HijriDateService.getHijriDateString(desiredTerminationDate);
	wfTermination.setDesiredTerminationDate(desiredTerminationDate);
    }

    @Transient
    public String getDesiredTerminationDateString() {
	return desiredTerminationDateString;
    }

    public void setDesiredTerminationDateString(String desiredTerminationDateString) {
	this.desiredTerminationDateString = desiredTerminationDateString;
	this.desiredTerminationDate = HijriDateService.getHijriDate(desiredTerminationDateString);
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	wfTermination.setReasons(reasons);
    }

    @Transient
    public WFTermination getWfTermination() {
	return wfTermination;
    }

    public void setWfTermination(WFTermination wfTermination) {
	this.wfTermination = wfTermination;
    }

}
