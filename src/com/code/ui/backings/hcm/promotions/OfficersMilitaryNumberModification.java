
package com.code.ui.backings.hcm.promotions;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PromotionsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "officersMilitaryNumberModification")
@ViewScoped
public class OfficersMilitaryNumberModification extends BaseBacking {

    public Long empId;
    public EmployeeData employeeData;
    public Integer militaryNumber;

    public OfficersMilitaryNumberModification() {
	resetForm();
    }

    public void resetForm() {
	empId = null;
	employeeData = null;
	militaryNumber = null;

    }

    public void searchEmpData() {
	try {
	    if (empId != null)
		employeeData = EmployeesService.getEmployeeData(empId);
	    	militaryNumber = employeeData.getMilitaryNumber();

	} catch (BusinessException e) {
	    resetForm();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    PromotionsService.updateEmployeesMilitaryNumber(employeeData, militaryNumber, loginEmpData.getEmpId());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public EmployeeData getEmployeeData() {
	return employeeData;
    }

    public void setEmployeeData(EmployeeData employeeData) {
	this.employeeData = employeeData;
    }

    public Integer getMilitaryNumber() {
        return militaryNumber;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
        this.militaryNumber = militaryNumber;
    }

}
