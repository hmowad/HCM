package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "cancelFutureVacation")
@ViewScoped
public class CancelFutureVacation extends FutureVacationBase {

    public CancelFutureVacation() {
	super.init();
	if (screenMode == 1)
	    this.setScreenTitle(getMessage("title_cancelGeneralManagerVacation"));
	else if (screenMode == 2)
	    this.setScreenTitle(getMessage("title_cancelExternalEmployeesVacation"));
    }

}
