package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.TransientVacationTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "addFutureVacation")
@ViewScoped
public class AddFutureVacation extends FutureVacationBase {

    public AddFutureVacation() {
	try {
	    super.init();
	    if (viewMode != 0) {
		futureVacation = FutureVacationsService.getFutureVacationTransactionDataById(vacationId);
	    }
	    adjustProcess();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(e.getMessage());
	}
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
		    validateGeneralManagerAdmins();
		    break;
		case 2:
		    this.setScreenTitle(this.getMessage("title_addExternalEmployeesVacation"));
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
	inquiryBalance();

    }

    public void startDateAndPeriodChangeListener(TransientVacationTransactionData futureVac) {
	super.startDateAndPeriodChangeListener(futureVacation);
	inquiryBalance();
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

    public void reset() {
	try {
	    super.reset();
	    futureVacation.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	    futureVacation.setLocation(getMessage("label_ksa"));
	    futureVacation.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());
	    futureVacation.setStartDate(HijriDateService.getHijriSysDate());
	    futureVacation.setDecisionDate(HijriDateService.getHijriSysDate());
	    balance = "";
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }
}
