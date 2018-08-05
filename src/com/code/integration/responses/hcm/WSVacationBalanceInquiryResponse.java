package com.code.integration.responses.hcm;

import com.code.integration.responses.WSResponseBase;

public class WSVacationBalanceInquiryResponse extends WSResponseBase {

    private String vacationBalance;

    public String getVacationBalance() {
	return vacationBalance;
    }

    public void setVacationBalance(String vacationBalance) {
	this.vacationBalance = vacationBalance;
    }

}
