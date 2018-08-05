package com.code.dal.orm.workflow.hcm.movements;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_movementWish_searchWFMovementWishes",
		query = " select m from WFMovementWish m, WFInstanceData i " +
			" where m.instanceId = i.instanceId " +
			" and (:P_INSTANCE_ID =-1 or m.instanceId =:P_INSTANCE_ID ) " +
			" and (:P_EMPLOYEE_ID = -1 or m.employeeId = :P_EMPLOYEE_ID) " +
			" and (:P_STATUS = -1 or i.status = :P_STATUS)" +
			" and (:P_EXCLUDED_INSTANCE_ID = -1 or m.instanceId <> :P_EXCLUDED_INSTANCE_ID) "),

	@NamedQuery(name = "wf_movementWish_getWFMovementWishesByInstancesIds",
		query = " select m from WFMovementWish m " +
			" where m.instanceId in (:P_INSTANCES_IDS) ")
})

@SuppressWarnings("serial")
@Entity
@Table(name = "WF_MOVEMENTS_WISHES")
public class WFMovementWish extends BaseEntity {

    private Long instanceId;
    private Long employeeId;
    private Long fromRegionId;
    private Long toRegionId;
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
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "FROM_REGION_ID")
    public Long getFromRegionId() {
	return fromRegionId;
    }

    public void setFromRegionId(Long fromRegionId) {
	this.fromRegionId = fromRegionId;
    }

    @Basic
    @Column(name = "TO_REGION_ID")
    public Long getToRegionId() {
	return toRegionId;
    }

    public void setToRegionId(Long toRegionId) {
	this.toRegionId = toRegionId;
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
