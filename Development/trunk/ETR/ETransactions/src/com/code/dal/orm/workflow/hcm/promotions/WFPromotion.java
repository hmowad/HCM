package com.code.dal.orm.workflow.hcm.promotions;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_wfPromotion_getWFPromotionByInstanceId",
		query = " select p from WFPromotion p " +
			" where p.instanceId = :P_INSTANCE_ID "),

	@NamedQuery(name = "wf_wfPromotion_getWFPromotionsTasks",
		query = " select  w,t, i, p.processName, requester, " +
			" case when t.originalId <> t.assigneeId then " +
			" (delegator.firstName || ' ' || delegator.secondName || ' ' || delegator.thirdName || case when delegator.thirdName is null then '' else ' ' end || delegator.lastName) " +
			" else null end " +
			" from WFPromotion w ,WFTask t, WFInstance i, WFProcess p, EmployeeData requester, Employee delegator " +
			" where w.instanceId 	= t.instanceId " +
			" and w.instanceId 	= i.id " +
			" and i.processId  	= p.id " +
			" and i.requesterId 	= requester.id " +
			" and t.originalId 	= delegator.id " +
			" and t.action is null " +
			" and t.assigneeId       = :P_ASSIGNEE_ID " +
			" and t.assigneeWfRole 	= :P_ASSIGNEE_WF_ROLE " +
			" order by t.taskId "),

	@NamedQuery(name = "wf_wfPromotion_getPromotionsReportsIdsByInstancesIds",
		query = " select  w.reportId from WFPromotion w " +
			" where w.instanceId in (:P_INSTANCES_IDS) " +
			" order by w.reportId ")
})

@Entity
@Table(name = "WF_PROMOTIONS")
public class WFPromotion extends BaseEntity {

    private Long instanceId;
    private Long reportId;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "REPORT_ID")
    public Long getReportId() {
	return reportId;
    }

    public void setReportId(Long reportId) {
	this.reportId = reportId;
    }

}
