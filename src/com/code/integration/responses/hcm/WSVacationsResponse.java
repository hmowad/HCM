package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.integration.responses.WSResponseBase;

public class WSVacationsResponse extends WSResponseBase {

    private List<Vacation> vacations;

    @XmlElementWrapper(name = "vacations")
    @XmlElement(name = "vacation")
    public List<Vacation> getVacations() {
	return vacations;
    }

    public void setVacations(List<Vacation> vacations) {
	this.vacations = vacations;
    }
}
