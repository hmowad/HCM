package com.code.integration.responses.hcm;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;

public class WSTerminatedEmployeesResponse {

    private Long empId;
    private String empName;
    private Long empDepartmentId;
    private String empDepartmentName;
    private String empSocialId;
    private Long empJobId;
    private String empJobName;
    private Long serviceTerminationReasonId;
    private String serviceTerminationReasonDesc;

    public WSTerminatedEmployeesResponse(TerminationTransactionData terminationTransactionData, EmployeeData employee) {
	this.empId = terminationTransactionData.getEmpId();
	this.empName = terminationTransactionData.getEmpName();
	this.empDepartmentId = terminationTransactionData.getTransEmpUnitId();
	this.empDepartmentName = terminationTransactionData.getTransEmpUnitFullName();
	this.empSocialId = employee.getSocialID();
	this.empJobId = terminationTransactionData.getJobId();
	this.empJobName = terminationTransactionData.getJobName();
	this.serviceTerminationReasonId = terminationTransactionData.getReasonId();
	this.serviceTerminationReasonDesc = terminationTransactionData.getReasonDesc();
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

    public Long getEmpDepartmentId() {
	return empDepartmentId;
    }

    public void setEmpDepartmentId(Long empDepartmentId) {
	this.empDepartmentId = empDepartmentId;
    }

    public String getEmpDepartmentName() {
	return empDepartmentName;
    }

    public void setEmpDepartmentName(String empDepartmentName) {
	this.empDepartmentName = empDepartmentName;
    }

    public String getEmpSocialId() {
	return empSocialId;
    }

    public void setEmpSocialId(String empSocialId) {
	this.empSocialId = empSocialId;
    }

    public Long getEmpJobId() {
	return empJobId;
    }

    public void setEmpJobId(Long empJobId) {
	this.empJobId = empJobId;
    }

    public String getEmpJobName() {
	return empJobName;
    }

    public void setEmpJobName(String empJobName) {
	this.empJobName = empJobName;
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
