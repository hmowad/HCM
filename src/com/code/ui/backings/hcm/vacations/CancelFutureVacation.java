package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.FlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.FutureVacationsService;

@ManagedBean(name = "cancelFutureVacation")
@ViewScoped
public class CancelFutureVacation extends FutureVacationBase {

    public CancelFutureVacation() {

	super.init();
	adjustProcess();

    }

    public void adjustProcess() {
	try {
	    if (viewMode == 1) {
		switch (screenMode) {
		case 1:
		    this.setScreenTitle(this.getMessage("title_cancelGeneralManagerVacationDetails"));
		    break;
		case 2:
		    this.setScreenTitle(this.getMessage("title_cancelExternalEmployeesVacationDetails"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (screenMode) {
		case 1:
		    this.setScreenTitle(this.getMessage("title_cancelGeneralManagerVacation"));
		    validateGeneralManagerAdmins();
		    break;
		case 2:
		    this.setScreenTitle(this.getMessage("title_cancelExternalEmployeesVacation"));
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

    public void selectEmployee() {
	super.selectEmployee();
	selectVacation();
    }

    public void saveVacation() {
	try {

	    FutureVacationsService.validateOldFutureVacationActivationStatus(newFutureVacation.getTransientVacationParentId(), newFutureVacation.getId());
	    newFutureVacation.setRequestType(RequestTypesEnum.CANCEL.getCode());
	    newFutureVacation.setActiveFlag(FlagsEnum.ON.getCode());
	    newFutureVacation.setEndDate(futureVacation.getEndDate());
	    newFutureVacation.setPeriod(futureVacation.getPeriod());
	    newFutureVacation.setExceededDays(null);
	    newFutureVacation.setJoiningDateString(null);

	    if (newFutureVacation.getId() == null) {
		FutureVacationsService.insertFutureVacation(newFutureVacation.getTransientVacationTransaction(), currentEmployee);
		newFutureVacation.setId(newFutureVacation.getTransientVacationTransaction().getId());
	    } else
		FutureVacationsService.modifyFutureVacation(newFutureVacation.getTransientVacationTransaction(), currentEmployee, false, false);

	    futureVacation.setActiveFlag(FlagsEnum.OFF.getCode());
	    FutureVacationsService.modifyFutureVacation(futureVacation.getTransientVacationTransaction(), currentEmployee, false, false);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void signVacation() {
	try {

	    FutureVacationsService.validateOldFutureVacationActivationStatus(newFutureVacation.getTransientVacationParentId(), newFutureVacation.getId());

	    newFutureVacation.setApprovedFlag(FlagsEnum.ON.getCode());
	    FutureVacationsService.modifyFutureVacation(newFutureVacation.getTransientVacationTransaction(), currentEmployee, false, false);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    futureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }
}
