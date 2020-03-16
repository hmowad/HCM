package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.FlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.FutureVacationsService;

@ManagedBean(name = "addFutureVacation")
@ViewScoped
public class AddFutureVacation extends FutureVacationBase {

    public AddFutureVacation() {

	super.init();
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
		break;
	    case 2:
		this.setScreenTitle(this.getMessage("title_addExternalEmployeesVacation"));
		break;
	    default:
		this.setServerSideErrorMessages(this.getMessage("error_general"));
	    }
	}
    }

    public void saveVacation() {
	try {
	    if (futureVacation.getId() == null) {
		futureVacation.setRequestType(RequestTypesEnum.NEW.getCode());
		futureVacation.setActiveFlag(FlagsEnum.ON.getCode());
		futureVacation.setEmpId(currentEmployee.getEmpId());

		FutureVacationsService.insertFutureVacation(futureVacation, currentEmployee);
	    } else {

		FutureVacationsService.modifyFutureVacation(futureVacation, currentEmployee, false, false);
	    }

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void signVacation() {
	try {
	    futureVacation.setApprovedFlag(FlagsEnum.ON.getCode());

	    FutureVacationsService.modifyFutureVacation(futureVacation, currentEmployee, true, false);
	    inquiryBalance();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    futureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }
}
