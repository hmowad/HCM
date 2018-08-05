package com.code.dal.orm.workflow;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

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
}