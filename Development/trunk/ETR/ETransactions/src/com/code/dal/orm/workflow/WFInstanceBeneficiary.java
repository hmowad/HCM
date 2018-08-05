package com.code.dal.orm.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_instanceBeneficiary_getWFInstanceBeneficiariesByInstanceId",
		query = " select ib from WFInstanceBeneficiary ib " +
			" where ib.instanceId = :P_INSTANCE_ID "),
})

@Entity
@Table(name = "WF_INSTANCES_BENEFICIARIES")
@IdClass(WFInstanceBeneficiaryId.class)
public class WFInstanceBeneficiary extends BaseEntity {
    private Long instanceId;
    private Long beneficiaryId;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Id
    @Column(name = "BENEFICIARY_ID")
    public Long getBeneficiaryId() {
	return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
	this.beneficiaryId = beneficiaryId;
    }

}