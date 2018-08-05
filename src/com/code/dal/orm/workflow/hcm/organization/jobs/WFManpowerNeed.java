package com.code.dal.orm.workflow.hcm.organization.jobs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_manpowerNeed_getWFManpowerNeedByInstanceId",
		query = " select m from WFManpowerNeed m " +
			" where m.instanceId = :P_INSTANCE_ID ")
})
@Entity
@Table(name = "WF_MANPOWER_NEEDS")
public class WFManpowerNeed extends BaseEntity {

    private Long instanceId;
    private Long manpowerNeedId;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "MANPOWER_NEED_ID")
    public Long getManpowerNeedId() {
	return manpowerNeedId;
    }

    public void setManpowerNeedId(Long manpowerNeedId) {
	this.manpowerNeedId = manpowerNeedId;
    }

}
