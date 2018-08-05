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

import com.code.dal.orm.BaseEntity;

/**
 * The <code>WFInstance</code> class represents the WorkFlow instance data for any request.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_instance_getWFInstanceById",
		query = " select i from WFInstance i " +
			" where i.instanceId = :P_INSTANCE_ID "),

	@NamedQuery(name = "wf_instance_getWFInstancesByIds",
		query = " select i from WFInstance i " +
			" where i.instanceId in (:P_INSTANCES_IDS) " +
			" order by i.instanceId "),

	@NamedQuery(name = "wf_instance_countInstancesByProcessesIds",
		query = " select count(i.instanceId) from WFInstance i where " +
			" (:P_PROCESSES_IDS_FLAG = -1 or i.processId in ( :P_PROCESSES_IDS )) " +
			" and (:P_STATUSES_IDS_FLAG = -1 or i.status in ( :P_STATUSES_IDS )) ")
})
@Entity
@Table(name = "WF_INSTANCES")
public class WFInstance extends BaseEntity implements Serializable {
    private Long instanceId;
    private Long processId;
    private Long requesterId;
    private Date requestDate;
    private Date hijriRequestDate;
    private Integer status;
    private String attachments;

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @SequenceGenerator(name = "WFInstancesSeq",
	    sequenceName = "WF_INSTANCES_SEQ")
    @Id
    @GeneratedValue(generator = "WFInstancesSeq")
    @Column(name = "ID")
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

    public void setRequesterId(Long requesterId) {
	this.requesterId = requesterId;
    }

    @Basic
    @Column(name = "REQUESTER_ID")
    public Long getRequesterId() {
	return requesterId;
    }

    public void setRequestDate(Date requestDate) {
	this.requestDate = requestDate;
    }

    @Basic
    @Column(name = "REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRequestDate() {
	return requestDate;
    }

    public void setHijriRequestDate(Date hijriRequestDate) {
	this.hijriRequestDate = hijriRequestDate;
    }

    @Basic
    @Column(name = "HIJRI_REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHijriRequestDate() {
	return hijriRequestDate;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }
}