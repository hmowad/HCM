package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "internalAssignmentReports")
@ViewScoped
public class InternalAssignmentReports extends BaseBacking implements Serializable {

    private int mode;

    private Long selectedUnitId;
    private String selectedUnitFullName;

    private Date fromDate;
    private Date toDate;

    private boolean hasPrivilege;
    private Long equivalentUnitId;

    public InternalAssignmentReports() {
	try {
	    if (getRequest().getParameter("mode") != null) {

		mode = Integer.parseInt(getRequest().getParameter("mode"));
		hasPrivilege = loginEmpData.getIsManager().equals(FlagsEnum.ON.getCode());
		equivalentUnitId = loginEmpData.getPhysicalUnitId();

		EmployeeData equivalentPrivilegeManager = loginEmpData;

		switch (mode) {

		case 1:
		    setScreenTitle(getMessage("title_officersInternalAssignmentReports"));

		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.MVT_INTERNAL_ASSIGNMENT_REGISTRATION_OFFICERS.getCode(), MenuActionsEnum.MVT_INTERNAL_ASSIGNMENT_MANAGER_OFFICERS.getCode())) {
			initInternalAssignmentReportsParameters(equivalentPrivilegeManager);
		    }

		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersInternalAssignmentReports"));

		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.MVT_INTERNAL_ASSIGNMENT_REGISTRATION_SOLDIERS.getCode(), MenuActionsEnum.MVT_INTERNAL_ASSIGNMENT_MANAGER_SOLDIERS.getCode())) {
			initInternalAssignmentReportsParameters(equivalentPrivilegeManager);
		    }

		    break;
		case 3:
		    setScreenTitle(getMessage("title_civiliansInternalAssignmentReports"));

		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.MVT_INTERNAL_ASSIGNMENT_REGISTRATION_CIVILIANS.getCode(), MenuActionsEnum.MVT_INTERNAL_ASSIGNMENT_MANAGER_CIVILIANS.getCode())) {
			initInternalAssignmentReportsParameters(equivalentPrivilegeManager);
		    }

		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
		}

		if (hasPrivilege)
		    resetForm();

	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	try {
	    selectedUnitId = null;
	    selectedUnitFullName = null;
	    fromDate = toDate = HijriDateService.getHijriSysDate();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    byte[] bytes = MovementsService.getInternalAssignmentReportsBytes(selectedUnitFullName, fromDate, toDate, (long) mode, getScreenTitle());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void initInternalAssignmentReportsParameters(EmployeeData equivalentPrivilegeManager) throws BusinessException {

	UnitData equivalentPrivilegeManagerUnit = UnitsService.getUnitById(equivalentPrivilegeManager.getPhysicalUnitId());

	while (equivalentPrivilegeManagerUnit.getUnitTypeCode() != UnitTypesEnum.SECTOR_COMMANDER.getCode() &&
		equivalentPrivilegeManagerUnit.getUnitTypeCode() != UnitTypesEnum.REGION_COMMANDER.getCode() &&
		equivalentPrivilegeManagerUnit.getUnitTypeCode() != UnitTypesEnum.PRESIDENCY.getCode()) {

	    equivalentPrivilegeManagerUnit = UnitsService.getUnitById(equivalentPrivilegeManagerUnit.getParentUnitId());
	}

	hasPrivilege = true;
	equivalentUnitId = equivalentPrivilegeManagerUnit.getId();
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Long getSelectedUnitId() {
	return selectedUnitId;
    }

    public void setSelectedUnitId(Long selectedUnitId) {
	this.selectedUnitId = selectedUnitId;
    }

    public String getSelectedUnitFullName() {
	return selectedUnitFullName;
    }

    public void setSelectedUnitFullName(String selectedUnitFullName) {
	this.selectedUnitFullName = selectedUnitFullName;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public boolean isHasPrivilege() {
	return hasPrivilege;
    }

    public void setHasPrivilege(boolean hasPrivilege) {
	this.hasPrivilege = hasPrivilege;
    }

    public Long getEquivalentUnitId() {
	return equivalentUnitId;
    }

    public void setEquivalentUnitId(Long equivalentUnitId) {
	this.equivalentUnitId = equivalentUnitId;
    }

}
