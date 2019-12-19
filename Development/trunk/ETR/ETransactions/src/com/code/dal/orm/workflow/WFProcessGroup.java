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
 * The <code>WFProcessGroup</code> class represents the WorkFlow process group data.</br>
 * The process group denotes one or more processes <code>WFProcess</code>.</br>
 * The process groups categorize the processes in different groups based on the business area.</br>
 * The available process groups are listed at <code>WFProcessesGroupsEnum</code>
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_group_getProcessesGroups",
		query = " select g from WFProcessGroup g " +
			" order by g.processGroupId "),
	@NamedQuery(name = "wf_group_getProcessesGroupById",
	query = " select g from WFProcessGroup g "
		+ " where g.processGroupId = :P_ID " 
		+ " order by g.processGroupId "),
	@NamedQuery(name = "wf_group_getProcessesGroupsApprovalCounts",
		query = " select pg.processGroupId, pg.processGroupName, count(t.taskId) " +
			" from WFTask t,WFInstance inst,WFProcess proc,WFProcessGroup pg " +
			" where pg.processGroupId = proc.processGroupId " +
			"   and proc.processId = inst.processId " +
			"   and inst.instanceId = t.instanceId " +
			"   and t.action is NULL " +
			"   and t.assigneeId = :P_ASSIGNEE_ID " +
			"   and t.assigneeWfRole in (:P_ASSIGNEE_WF_ROLES) " +
			"   and pg.processGroupId in (:P_PROCESS_GROUPS_IDS) " +
			"   and proc.id not in (:P_EXCLUDED_PROCESS_IDS) " +
			" group by pg.processGroupId, pg.processGroupName " +
			" order by pg.processGroupId ")

})

@Entity
@Table(name = "WF_PROCESSES_GROUPS")
public class WFProcessGroup extends BaseEntity implements Serializable {
    private Long processGroupId;
    private String processGroupName;

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    @Id
    @Column(name = "ID")
    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setProcessGroupName(String processGroupName) {
	this.processGroupName = processGroupName;
    }

    @Basic
    @Column(name = "NAME")
    public String getProcessGroupName() {
	return processGroupName;
    }

}