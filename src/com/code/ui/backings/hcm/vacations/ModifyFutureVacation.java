package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "modifyFutureVacation")
@ViewScoped
public class ModifyFutureVacation extends FutureVacationBase {

    public ModifyFutureVacation() {
	super.init();
	if (screenMode == 1)
	    this.setScreenTitle(getMessage("title_modifyGeneralManagerVacation"));
	else if (screenMode == 2)
	    this.setScreenTitle(getMessage("title_modifyExternalEmployeesVacation"));

    }

}
