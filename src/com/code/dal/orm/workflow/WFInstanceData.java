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
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>WFInstance</code> class represents the WorkFlow instance data for any request in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_instanceData_searchWFInstancesData",
		query = " select i from WFInstanceData i, WFInstanceBeneficiary b " +
			" where i.instanceId = b.instanceId " +
			"   and b.beneficiaryId = (select max(ib.beneficiaryId) from WFInstanceBeneficiary ib where ib.instanceId = i.id and (:P_BENEFICIARY_ID = -1 or ib.beneficiaryId = :P_BENEFICIARY_ID)) " +
			"   and (:P_REQUESTER_ID = -1 OR i.requesterId = :P_REQUESTER_ID) " +
			"   and (:P_PROCESS_GROUP_ID = 0 OR i.processGroupId = :P_PROCESS_GROUP_ID) " +
			"   and (:P_PROCESS_ID = 0 OR i.processId = :P_PROCESS_ID) " +
			"   and i.status in :P_STATUS_VALUES " +
			" order by i.requestDate desc "),

	@NamedQuery(name = "wf_instanceData_getWFInstanceDataById",
		query = " select i from WFInstanceData i " +
			" where i.instanceId = :P_INSTANCE_ID "),

	@NamedQuery(name = "wf_instanceData_getWFInstancesUnderProcessingCount",
		query = " select count(i.instanceId) from WFInstanceData i " +
			" where i.requesterId = :P_REQUESTER_ID " +
			" and i.status = 1 ")
})
@Entity
@Table(name = "ETR_VW_INSTANCES")
public class WFInstanceData extends BaseEntity {
    private Long instanceId;
    private Long processId;
    private String processName;
    private Long processGroupId;
    private Long requesterId;
    private String requesterName;
    private String requesterRankDesc;
    private Date requestDate;
    private Date hijriRequestDate;
    private String hijriRequestDateString;
    private Integer status;
    private String attachments;

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Id
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

    public void setRequesterId(Long requesterId) {
	this.requesterId = requesterId;
    }

    @Basic
    @Column(name = "REQUESTER_ID")
    public Long getRequesterId() {
	return requesterId;
    }

    public void setRequesterName(String requesterName) {
	this.requesterName = requesterName;
    }

    @Basic
    @Column(name = "REQUESTER_NAME")
    public String getRequesterName() {
	return requesterName;
    }

    public void setRequesterRankDesc(String requesterRankDesc) {
	this.requesterRankDesc = requesterRankDesc;
    }

    @Basic
    @Column(name = "REQUESTER_RANK_DESC")
    @XmlTransient
    public String getRequesterRankDesc() {
	return requesterRankDesc;
    }

    public void setRequestDate(Date requestDate) {
	this.requestDate = requestDate;

	if (this.requestDate != null && this.hijriRequestDate != null) {
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    this.hijriRequestDateString = HijriDateService.getHijriDateString(hijriRequestDate) + " " + sdf.format(requestDate);
	}
    }

    @Basic
    @Column(name = "REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getRequestDate() {
	return requestDate;
    }

    public void setHijriRequestDate(Date hijriRequestDate) {
	this.hijriRequestDate = hijriRequestDate;

	if (this.requestDate != null && this.hijriRequestDate != null) {
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    this.hijriRequestDateString = HijriDateService.getHijriDateString(hijriRequestDate) + " " + sdf.format(requestDate);
	}
    }

    @Basic
    @Column(name = "HIJRI_REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getHijriRequestDate() {
	return hijriRequestDate;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "STATUS")
    @XmlTransient
    public Integer getStatus() {
	return status;
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

    public void setHijriRequestDateString(String hijriRequestDateString) {
	this.hijriRequestDateString = hijriRequestDateString;
    }

    @Transient
    public String getHijriRequestDateString() {
	return hijriRequestDateString;
    }
}