package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "addFutureVacation")
@ViewScoped
public class AddFutureVacation extends FutureVacationBase {

    public AddFutureVacation() {
	super.init();
	if (screenMode == 1)
	    this.setScreenTitle(getMessage("title_addGeneralManagerVacation"));
	else if (screenMode == 2)
	    this.setScreenTitle(getMessage("title_addExternalEmployeesVacation"));
    }

}
