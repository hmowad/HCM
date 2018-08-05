package com.code.ui.backings.hcm.employees;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesPhotos")
@ViewScoped
public class EmployeesPhotos extends BaseBacking implements Serializable {

    private EmployeeData employeeData;
    private long selectedEmpId;

    private boolean hasPhoto;

    public EmployeesPhotos() {
	setScreenTitle(getMessage("title_employeesPhotoManagement"));
	reset();
    }

    public void getEmployee() {
	try {
	    if (selectedEmpId != FlagsEnum.ALL.getCode()) {
		employeeData = EmployeesService.getEmployeeData(selectedEmpId);
		hasPhoto = EmployeesService.getEmployeePhotoByEmpId(selectedEmpId) != null;
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	employeeData = null;
	selectedEmpId = FlagsEnum.ALL.getCode();
	hasPhoto = false;
    }

    public void updatePhoto() {
	try {
	    hasPhoto = EmployeesService.getEmployeePhotoByEmpId(selectedEmpId) != null;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public EmployeeData getEmployeeData() {
	return employeeData;
    }

    public long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public boolean isHasPhoto() {
	return hasPhoto;
    }

    public void setHasPhoto(boolean hasPhoto) {
	this.hasPhoto = hasPhoto;
    }

}
