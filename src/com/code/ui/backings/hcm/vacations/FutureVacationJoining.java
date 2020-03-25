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
    private boolean joiningSaveFlag = false;

    public FutureVacationJoining() {
	try {
	    super.init();
	    if (viewMode == 3) {
		vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
		this.futureVacation = FutureVacationsService.getFutureVacationTransactionDataById(vacationId);
		exceededFlag = 0;
		exceededFlagListener();
		this.futureVacation.setJoiningRemarks(null);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(e.getMessage());
	}
    }

    public void exceededFlagListener() {
	try {
	    if (exceededFlag == 0) {
		this.futureVacation.setExceededDays(0);
	    }
	    this.futureVacation.setJoiningDateString(HijriDateService.addSubStringHijriDays(futureVacation.getEndDateString(), futureVacation.getExceededDays() + 1));
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void exceededDaysListener(AjaxBehaviorEvent event) {
	exceededFlagListener();
    }

    public void saveJoiningVacation() {
	try {
	    FutureVacationsService.modifyFutureVacation(futureVacation.getTransientVacationTransaction(), currentEmployee, true, true);
	    joiningSaveFlag = true;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
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

    public boolean isJoiningSaveFlag() {
	return joiningSaveFlag;
    }

    public void setJoiningSaveFlag(boolean joiningSaveFlag) {
	this.joiningSaveFlag = joiningSaveFlag;
    }

}
