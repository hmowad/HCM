package com.code.ui.backings.hcm.employees;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesPhotos")
@ViewScoped
public class EmployeesPhotos extends BaseBacking implements Serializable {

    private EmployeeData employeeData;
    private long selectedEmpId;

    private boolean hasPhoto;
    private boolean isModifyAdmin;
    private boolean isModifyOfficer;
    private boolean isModifyMaleSoldier;
    private boolean isModifyFemaleSoldier;
    private boolean isModifyMaleCivillian;
    private boolean isModifyFemaleCivillian;

    public EmployeesPhotos() {
	try {
	    setScreenTitle(getMessage("title_employeesPhotoManagement"));
	    isModifyOfficer = SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_PHOTOS.getCode(), MenuActionsEnum.PRS_EMPS_PHOTOS_MODIFY_OFFICERS.getCode());
	    isModifyMaleSoldier = SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_PHOTOS.getCode(), MenuActionsEnum.PRS_EMPS_PHOTOS_MODIFY_MALE_SOLDIERS.getCode());
	    isModifyFemaleSoldier = SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_PHOTOS.getCode(), MenuActionsEnum.PRS_EMPS_PHOTOS_MODIFY_FEMALE_SOLDIERS.getCode());
	    isModifyMaleCivillian = SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_PHOTOS.getCode(), MenuActionsEnum.PRS_EMPS_PHOTOS_MODIFY_MALE_CIVILLIANS.getCode());
	    isModifyFemaleCivillian = SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_PHOTOS.getCode(), MenuActionsEnum.PRS_EMPS_PHOTOS_MODIFY_FEMALE_CIVILLIANS.getCode());
	    isModifyAdmin = isModifyOfficer || isModifyMaleSoldier || isModifyFemaleSoldier || isModifyMaleCivillian || isModifyFemaleCivillian;
	    reset();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getEmployee() {
	try {
	    if (selectedEmpId != FlagsEnum.ALL.getCode()) {
		EmployeeData tempEmployeeData = EmployeesService.getEmployeeData(selectedEmpId);
		if ((tempEmployeeData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && isModifyOfficer)
			|| (tempEmployeeData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) &&
				((GendersEnum.MALE.getCode().equals(tempEmployeeData.getGender()) && isModifyMaleSoldier) || (GendersEnum.FEMALE.getCode().equals(tempEmployeeData.getGender()) && isModifyFemaleSoldier)))
			|| ((CategoriesEnum.PERSONS.getCode() == tempEmployeeData.getCategoryId() || CategoriesEnum.USERS.getCode() == tempEmployeeData.getCategoryId()
				|| CategoriesEnum.WAGES.getCode() == tempEmployeeData.getCategoryId() || CategoriesEnum.CONTRACTORS.getCode() == tempEmployeeData.getCategoryId()
				|| CategoriesEnum.MEDICAL_STAFF.getCode() == tempEmployeeData.getCategoryId()) &&
				((GendersEnum.MALE.getCode().equals(tempEmployeeData.getGender()) && isModifyMaleCivillian) || (GendersEnum.FEMALE.getCode().equals(tempEmployeeData.getGender()) && isModifyFemaleCivillian)))) {
		    employeeData = tempEmployeeData;
		    hasPhoto = EmployeesService.getEmployeePhotoByEmpId(selectedEmpId) != null;
		} else {
		    setServerSideErrorMessages(getMessage("error_notAuthorized"));
		}
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

    public boolean isModifyAdmin() {
	return isModifyAdmin;
    }

    public void setModifyAdmin(boolean isModifyAdmin) {
	this.isModifyAdmin = isModifyAdmin;
    }

}
