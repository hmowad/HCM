package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.FlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.FutureVacationsService;

@ManagedBean(name = "modifyFutureVacation")
@ViewScoped
public class ModifyFutureVacation extends FutureVacationBase {

    public ModifyFutureVacation() {

	super.init();
	adjustProcess();

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

    public void selectEmployee() {
	super.selectEmployee();
	selectVacation();
    }

    public void saveVacation() {
	try {
	    if (futureVacation.getId() == null)
		throw new BusinessException("error_noPrevVacation");
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
	    // todo wfskipFlag=true
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

	    FutureVacationsService.modifyFutureVacation(newFutureVacation.getTransientVacationTransaction(), currentEmployee, true, false);

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    futureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

}
