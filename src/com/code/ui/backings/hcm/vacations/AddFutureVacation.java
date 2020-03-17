package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.security.SecurityService;

@ManagedBean(name = "addFutureVacation")
@ViewScoped
public class AddFutureVacation extends FutureVacationBase {

    private StringBuilder categoriesIds;

    public AddFutureVacation() {
	categoriesIds = new StringBuilder();
	super.init();
	adjustProcess();
    }

    public void adjustProcess() {
	try {
	    if (viewMode == 1) {
		switch (screenMode) {
		case 1:
		    this.setScreenTitle(this.getMessage("title_addGeneralManagerVacationDetails"));
		    break;
		case 2:
		    this.setScreenTitle(this.getMessage("title_addExternalEmployeesVacationDetails"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (screenMode) {
		case 1:
		    this.setScreenTitle(this.getMessage("title_addGeneralManagerVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_GENEAL_MANAGER_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_GENEAL_MANAGER.getCode()));
		    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_GENEAL_MANAGER_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_GENEAL_MANAGER.getCode()));
		    break;
		case 2:
		    this.setScreenTitle(this.getMessage("title_addExternalEmployeesVacation"));
		    validateAdmins();
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    private void validateAdmins() throws BusinessException {

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_OFFICERS.getCode()))
	    categoriesIds.append(CategoriesEnum.OFFICERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_SOLDIERS.getCode()))
	    categoriesIds.append(CategoriesEnum.SOLDIERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_PERSONS.getCode()))
	    categoriesIds.append(CategoriesEnum.PERSONS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_USERS.getCode()))
	    categoriesIds.append(CategoriesEnum.USERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_WAGES.getCode()))
	    categoriesIds.append(CategoriesEnum.WAGES.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_CONTRACTORS.getCode()))
	    categoriesIds.append(CategoriesEnum.CONTRACTORS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_MEDICAL_STAFF.getCode()))
	    categoriesIds.append(CategoriesEnum.MEDICAL_STAFF.getCode() + ",");

	if (categoriesIds.length() != 0 && categoriesIds.substring(categoriesIds.length() - 1).equals(","))
	    categoriesIds.delete(categoriesIds.length() - 1, categoriesIds.length());

	this.setAdmin(categoriesIds.length() != 0 ? true : false);

    }

    public void saveVacation() {
	try {
	    if (futureVacation.getId() == null) {
		futureVacation.setRequestType(RequestTypesEnum.NEW.getCode());
		futureVacation.setActiveFlag(FlagsEnum.ON.getCode());
		futureVacation.setEmpId(currentEmployee.getEmpId());

		FutureVacationsService.insertFutureVacation(futureVacation.getTransientVacationTransaction(), currentEmployee);
		futureVacation.setId(futureVacation.getTransientVacationTransaction().getId());
	    } else {

		FutureVacationsService.modifyFutureVacation(futureVacation.getTransientVacationTransaction(), currentEmployee, false, false);
	    }

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void signVacation() {
	try {
	    futureVacation.setApprovedFlag(FlagsEnum.ON.getCode());

	    FutureVacationsService.modifyFutureVacation(futureVacation.getTransientVacationTransaction(), currentEmployee, true, false);
	    inquiryBalance();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    futureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public StringBuilder getCategoriesIds() {
	return categoriesIds;
    }

    public void setCategoriesIds(StringBuilder categoriesIds) {
	this.categoriesIds = categoriesIds;
    }

}
