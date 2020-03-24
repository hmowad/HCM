package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "futureVacationJoining")
@ViewScoped
public class FutureVacationJoining extends FutureVacationBase {

    private int exceededFlag = 0;

    public FutureVacationJoining() {
	try {
	    super.init();
	    if (viewMode != 0) {
		vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
		futureVacation = FutureVacationsService.getFutureVacationTransactionDataById(vacationId);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(e.getMessage());
	}
    }

    public void exceededFlagListener() {
	try {
	    if (exceededFlag == 0) {
		this.futureVacation.setExceededDays(0);
		this.futureVacation.setJoiningDateString(HijriDateService.addSubStringHijriDays(futureVacation.getStartDateString(), futureVacation.getPeriod()));
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void exceededDaysListener(AjaxBehaviorEvent event) {
	try {
	    if (futureVacation.getExceededDays() == null || futureVacation.getExceededDays() < 0)
		throw new BusinessException("error_exceededDaysPositive");

	    futureVacation.setJoiningDateString(HijriDateService.addSubStringHijriDays(futureVacation.getEndDateString(), futureVacation.getExceededDays() + 1));

	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void saveJoiningVacation() {
	try {
	    FutureVacationsService.modifyFutureVacation(futureVacation.getTransientVacationTransaction(), currentEmployee, true, false);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public int getExceededFlag() {
	return exceededFlag;
    }

    public void setExceededFlag(int exceededFlag) {
	this.exceededFlag = exceededFlag;
    }

}
