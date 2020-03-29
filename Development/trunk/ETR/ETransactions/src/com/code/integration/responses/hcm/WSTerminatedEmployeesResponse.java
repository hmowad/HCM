package com.code.integration.responses.hcm;

public class WSTerminatedEmployeesResponse {

    private Long empId;
    private String empName;
    private Long serviceTerminationReasonId;
    private String serviceTerminationReasonDesc;

    public WSTerminatedEmployeesResponse(Long empId, String empName, Long serviceTerminationReasonId, String serviceTerminationReasonDesc) {
	this.empId = empId;
	this.empName = empName;
	this.serviceTerminationReasonId = serviceTerminationReasonId;
	this.serviceTerminationReasonDesc = serviceTerminationReasonDesc;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public Long getServiceTerminationReasonId() {
	return serviceTerminationReasonId;
    }

    public void setServiceTerminationReasonId(Long serviceTerminationReasonId) {
	this.serviceTerminationReasonId = serviceTerminationReasonId;
    }

    public String getServiceTerminationReasonDesc() {
	return serviceTerminationReasonDesc;
    }

    public void setServiceTerminationReasonDesc(String serviceTerminationReasonDesc) {
	this.serviceTerminationReasonDesc = serviceTerminationReasonDesc;
    }

}
