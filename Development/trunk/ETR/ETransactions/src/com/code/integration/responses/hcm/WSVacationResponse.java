package com.code.integration.responses.hcm;

import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.integration.responses.WSResponseBase;

public class WSVacationResponse extends WSResponseBase {

    private Vacation vacation;

    public Vacation getVacation() {
	return vacation;
    }

    public void setVacation(Vacation vacation) {
	this.vacation = vacation;
    }

}
