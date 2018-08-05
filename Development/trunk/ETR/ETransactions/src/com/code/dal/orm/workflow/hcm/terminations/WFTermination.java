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

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_termination_getRunningTerminationRequests",
		query = " select i " +
			" from WFTermination wf, WFInstance i" +
			" where wf.instanceId = i.instanceId " +
			" and i.status = 1 " +
			" and i.requesterId in ( :P_EMPS_IDS )" +
			" and i.processId in ( :P_PROCESSES_IDS )" +
			" and (:P_EXCLUDED_INSTANCE_ID = -1 or wf.instanceId  <> :P_EXCLUDED_INSTANCE_ID) "),

	@NamedQuery(name = "wf_wfTermination_getTerminationsRecordsIdsByInstancesIds",
		query = " select w.recordId " +
			" from WFTermination w " +
			" where w.instanceId in (:P_INSTANCES_IDS) " +
			" order by w.recordId ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "WF_SERVICES_TERMINATIONS")
public class WFTermination extends BaseEntity {

    private Long instanceId;
    private Long recordId;
    private Date desiredTerminationDate;
    private String reasons;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "STE_RECORD_ID")
    public Long getRecordId() {
	return recordId;
    }

    public void setRecordId(Long recordId) {
	this.recordId = recordId;
    }

    @Basic
    @Column(name = "DESIRED_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDesiredTerminationDate() {
	return desiredTerminationDate;
    }

    public void setDesiredTerminationDate(Date desiredTerminationDate) {
	this.desiredTerminationDate = desiredTerminationDate;
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

}
