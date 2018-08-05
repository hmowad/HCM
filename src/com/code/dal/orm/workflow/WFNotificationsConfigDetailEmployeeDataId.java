package com.code.dal.orm.workflow;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WFNotificationsConfigDetailEmployeeDataId implements Serializable {

    private Long employeeId;
    private Long wfNotificationsConfigDetailId;

    public WFNotificationsConfigDetailEmployeeDataId() {

    }

    public WFNotificationsConfigDetailEmployeeDataId(Long employeeId, Long wfNotificationsConfigDetailId) {
	this.employeeId = employeeId;
	this.wfNotificationsConfigDetailId = wfNotificationsConfigDetailId;
    }

    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    public Long getWfNotificationsConfigDetailId() {
	return wfNotificationsConfigDetailId;
    }

    public void setWfNotificationsConfigDetailId(Long wfNotificationsConfigDetailId) {
	this.wfNotificationsConfigDetailId = wfNotificationsConfigDetailId;
    }

    public boolean equals(Object o) {
	return ((o instanceof WFNotificationsConfigDetailEmployeeDataId) && (wfNotificationsConfigDetailId.equals(((WFNotificationsConfigDetailEmployeeDataId) o).getWfNotificationsConfigDetailId())) && (employeeId.equals(((WFNotificationsConfigDetailEmployeeDataId) o).getEmployeeId())));
    }

    public int hashCode() {
	return wfNotificationsConfigDetailId.hashCode() + employeeId.hashCode();
    }
}