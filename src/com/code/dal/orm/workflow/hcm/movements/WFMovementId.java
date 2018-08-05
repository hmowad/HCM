package com.code.dal.orm.workflow.hcm.movements;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WFMovementId implements Serializable {

    private Long instanceId;
    private Long employeeId;

    public WFMovementId() {
    }

    public WFMovementId(Long instanceId, Long employeeId) {
	this.employeeId = employeeId;
	this.instanceId = instanceId;
    }

    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    public boolean equals(Object o) {
	return ((o instanceof WFMovementId) && (instanceId.equals(((WFMovementId) o).getInstanceId())) && (employeeId.equals(((WFMovementId) o).getEmployeeId())));
    }

    public int hashCode() {
	return instanceId.hashCode() + employeeId.hashCode();
    }
}
