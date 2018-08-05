package com.code.dal.orm.workflow;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WFInstanceBeneficiaryId implements Serializable {
    private Long instanceId;
    private Long beneficiaryId;

    public WFInstanceBeneficiaryId() {
    }

    public WFInstanceBeneficiaryId(Long instanceId, Long beneficiaryId) {
	this.instanceId = instanceId;
	this.beneficiaryId = beneficiaryId;
    }

    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    public Long getBeneficiaryId() {
	return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
	this.beneficiaryId = beneficiaryId;
    }

    public boolean equals(Object o) {
	return ((o instanceof WFInstanceBeneficiaryId) && (instanceId.equals(((WFInstanceBeneficiaryId) o).getInstanceId())) && (beneficiaryId.equals(((WFInstanceBeneficiaryId) o).getBeneficiaryId())));
    }

    public int hashCode() {
	return instanceId.hashCode() + beneficiaryId.hashCode();
    }
}