package com.code.ui.backings.hcm.vacations;

import java.util.Iterator;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;

@ManagedBean(name = "modifyFutureVacation")
@ViewScoped
public class ModifyFutureVacation extends FutureVacationBase {

    public ModifyFutureVacation() {
	try {
	    super.init();
	    if (viewMode != 0) {
		getVacationTypesByEmpCategoryId();
		newFutureVacation = FutureVacationsService.getFutureVacationTransactionDataById(vacationId);
		futureVacation = FutureVacationsService.getFutureVacationTransactionDataById(newFutureVacation.getTransientVacationParentId());
	    }
	    adjustProcess();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(e.getMessage());
	}
    }

    private void getVacationTypesByEmpCategoryId() {
	try {
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	    Iterator<VacationType> iter = vacTypeList.iterator();
	    while (iter.hasNext()) {
		if (iter.next().getVacationTypeId() != VacationTypesEnum.REGULAR.getCode() || iter.next().getVacationTypeId() != VacationTypesEnum.SICK.getCode()) {
		    iter.remove();
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void adjustProcess() {
	try {
	    if (viewMode == 1) {
		switch (screenMode) {
		case 1:
		    this.setScreenTitle(this.getMessage("title_modifyGeneralManagerVacationDetails"));
		    break;
		case 2:
		    this.setScreenTitle(this.getMessage("title_modifyExternalEmployeesVacationDetails"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (screenMode) {
		case 1:
		    this.setScreenTitle(this.getMessage("title_modifyGeneralManagerVacation"));
		    validateGeneralManagerAdmins();
		    break;
		case 2:
		    this.setScreenTitle(this.getMessage("title_modifyExternalEmployeesVacation"));
		    validateExternalEmployeesAdmins();
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    private void validateGeneralManagerAdmins() throws BusinessException {
	this.setAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_GENEAL_MANAGER_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_GENEAL_MANAGER.getCode()));
	this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_GENEAL_MANAGER_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_GENEAL_MANAGER.getCode()));
    }

    private void validateExternalEmployeesAdmins() throws BusinessException {
	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_OFFICERS.getCode()))
	    categoriesIds.append(CategoriesEnum.OFFICERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_SOLDIERS.getCode()))
	    categoriesIds.append(CategoriesEnum.SOLDIERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_PERSONS.getCode()))
	    categoriesIds.append(CategoriesEnum.PERSONS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_USERS.getCode()))
	    categoriesIds.append(CategoriesEnum.USERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_WAGES.getCode()))
	    categoriesIds.append(CategoriesEnum.WAGES.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_CONTRACTORS.getCode()))
	    categoriesIds.append(CategoriesEnum.CONTRACTORS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_MEDICAL_STAFF.getCode()))
	    categoriesIds.append(CategoriesEnum.MEDICAL_STAFF.getCode() + ",");

	if (categoriesIds.length() != 0 && categoriesIds.substring(categoriesIds.length() - 1).equals(","))
	    categoriesIds.delete(categoriesIds.length() - 1, categoriesIds.length());

	this.setAdmin(categoriesIds.length() != 0 ? true : false);

    }

    public void validateSignAdmins() throws BusinessException {
	if (currentEmployee.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_OFFICERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_SOLDIERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.PERSONS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_PERSONS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.USERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_USERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.WAGES.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_WAGES.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_CONTRACTORS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_MODIFY_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_MEDICAL_STAFF.getCode()));
	}
    }

    public void selectEmployee() {
	super.selectEmployee();
	getVacationTypesByEmpCategoryId();
	selectVacation();
    }

    public void saveVacation() {
	try {
	    FutureVacationsService.validateOldFutureVacationActivationStatus(newFutureVacation.getTransientVacationParentId(), newFutureVacation.getId());
	    if (newFutureVacation.getId() == null) {
		newFutureVacation.setRequestType(RequestTypesEnum.MODIFY.getCode());
		newFutureVacation.setActiveFlag(FlagsEnum.ON.getCode());
		FutureVacationsService.insertFutureVacation(newFutureVacation.getTransientVacationTransaction(), currentEmployee);
		newFutureVacation.setId(newFutureVacation.getTransientVacationTransaction().getId());

	    } else {
		FutureVacationsService.modifyFutureVacation(newFutureVacation.getTransientVacationTransaction(), currentEmployee, false, false);
	    }
	    futureVacation.setActiveFlag(FlagsEnum.OFF.getCode());
	    FutureVacationsService.modifyFutureVacation(futureVacation.getTransientVacationTransaction(), currentEmployee, false, true);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void signVacation() {
	try {

	    FutureVacationsService.validateOldFutureVacationActivationStatus(newFutureVacation.getTransientVacationParentId(), newFutureVacation.getId());
	    newFutureVacation.setApprovedFlag(FlagsEnum.ON.getCode());

	    FutureVacationsService.modifyFutureVacation(newFutureVacation.getTransientVacationTransaction(), currentEmployee, true, false);

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    futureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

}
