package com.code.dal.orm.workflow.hcm.recruitments;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WFRecruitmentId implements Serializable{
    private Long instanceId;
    private Long employeeId;

    public WFRecruitmentId() {
    }

    public WFRecruitmentId(Long instanceId, Long employeeId) {
	this.instanceId = instanceId;
	this.employeeId = employeeId;
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
	return ((o instanceof WFRecruitmentId) && (instanceId.equals(((WFRecruitmentId) o).getInstanceId())) && (employeeId.equals(((WFRecruitmentId) o).getEmployeeId())));
    }

    public int hashCode() {
	return instanceId.hashCode() + employeeId.hashCode();
    }
}