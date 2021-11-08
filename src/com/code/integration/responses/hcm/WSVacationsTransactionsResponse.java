package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.integration.responses.WSResponseBase;

public class WSVacationsTransactionsResponse extends WSResponseBase {

    private List<VacationData> vacationsTransactions;

    @XmlElementWrapper(name = "vacationsTransactions")
    @XmlElement(name = "vacationTransaction")
    public List<VacationData> getVacationsTransactions() {
	return vacationsTransactions;
    }

    public void setVacationsTransactions(List<VacationData> vacationsTransactions) {
	this.vacationsTransactions = vacationsTransactions;
    }
}
