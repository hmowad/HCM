package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.TransientVacationTransaction;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.hcm.VacationsService;

@ManagedBean(name = "addFutureVacation")
@ViewScoped
public class AddFutureVacation extends FutureVacationBase {

    public AddFutureVacation() {
	try {

	    super.init();
	    if (screenMode == 1)
		this.setScreenTitle(getMessage("title_addGeneralManagerVacation"));
	    else if (screenMode == 2)
		this.setScreenTitle(getMessage("title_addExternalEmployeesVacation"));
	    if (viewMode != 0)
		currentEmployee = EmployeesService.getEmployeeData(empId);

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
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

    public void reset() {
	currentEmployee = new EmployeeData();
	futureVacation = new TransientVacationTransaction();
	futureVacation.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	futureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
	futureVacation.setLocation(getMessage("label_ksa"));
	futureVacation.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());
	balance = "";
	try {
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

}
