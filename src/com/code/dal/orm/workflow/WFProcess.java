package com.code.dal.orm.workflow;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>WFProcess</code> class represents the WorkFlow process data which map all the request types.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_process_getGroupProcesses",
		query = " select p from WFProcess p " +
			" where (:P_GROUP_ID = 0 OR p.processGroupId = :P_GROUP_ID) " +
			" and (:P_PROCESS_NAME = '-1' or p.processName like :P_PROCESS_NAME ) " +
			" and (:P_PROCESSES_IDS_FLAG = -1 or p.processId not in ( :P_PROCESSES_IDS )) " +
			" order by p.processId "
	),
	@NamedQuery(name = "wf_process_getWFProcess",
		query = " select p from WFProcess p where p.processId = :P_PROCESS_ID "
	)
})
@Entity
@Table(name = "WF_PROCESSES")
public class WFProcess extends BaseEntity implements Serializable {
    private Long processId;
    private String processName;
    private Long processGroupId;
    
    private String latinName;
    private String approveCallback;
    private String rejectCallback;
    private String cancelCallback;
    private Integer enableFlag;

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    @Id
    @Column(name = "ID")
    public Long getProcessId() {
	return processId;
    }

    public void setProcessName(String processName) {
	this.processName = processName;
    }

    @Basic
    @Column(name = "NAME")
    public String getProcessName() {
	return processName;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    @Basic
    @Column(name = "GROUP_ID")
    public Long getProcessGroupId() {
	return processGroupId;
    }

    @Transient
	@XmlTransient
	public String getApproveCallback() {
		return approveCallback;
	}

	public void setApproveCallback(String approveCallback) {
		this.approveCallback = approveCallback;
	}

    @Transient
	@XmlTransient
	public String getRejectCallback() {
		return rejectCallback;
	}

	public void setRejectCallback(String rejectCallback) {
		this.rejectCallback = rejectCallback;
	}

    @Transient
	@XmlTransient
	public String getCancelCallback() {
		return cancelCallback;
	}

	public void setCancelCallback(String cancelCallback) {
		this.cancelCallback = cancelCallback;
	}

    @Transient
	@XmlTransient
	public Integer getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(Integer enableFlag) {
		this.enableFlag = enableFlag;
	}

    @Transient
	@XmlTransient
	public String getLatinName() {
		return latinName;
	}

	public void setLatinName(String latinName) {
		this.latinName = latinName;
	}
    
    
}