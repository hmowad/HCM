package com.code.ui.backings.hcm.navyformations;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.CategoryModesEnum;
import com.code.enums.GeneralSpecializationsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesNavyFormationData")
@ViewScoped
public class EmployeesNavyFormationData extends BaseBacking implements Serializable {

    private int mode;

    private EmployeeData employeeData;
    private String selectedEmpId;

    public EmployeesNavyFormationData() {

	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		setScreenTitle(getMessage("title_officersNavyFormationData"));
	    } else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		setScreenTitle(getMessage("title_soldiersNavyFormationData"));
	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	    reset();
	} else {
	    setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void getEmployee() {
	try {
	    if (selectedEmpId != null && !selectedEmpId.trim().equals("")) {
		employeeData = EmployeesService.getEmployeeData(Long.parseLong(selectedEmpId));
		if (employeeData == null)
		    throw new BusinessException("error_general");
		else if (employeeData.getGeneralSpecialization() != GeneralSpecializationsEnum.NAVY.getCode()) {
		    reset();
		    if (mode == CategoryModesEnum.OFFICERS.getCode()) {
			throw new BusinessException("error_generalSpecializationMissMatch");
		    } else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
			throw new BusinessException("error_generalSpecializationMissMatchSoldiers");
		    }
		}
	    }

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void save() {
	try {
	    employeeData.getEmployee().setSystemUser(this.loginEmpData.getEmpId() + ""); // For auditing.
	    EmployeesService.updateEmployee(employeeData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void reset() {
	selectedEmpId = null;
	employeeData = new EmployeeData();
	employeeData.setCategoryId(new Long(mode));
    }

    public EmployeeData getEmployeeData() {
	return employeeData;
    }

    public String getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(String selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

}
