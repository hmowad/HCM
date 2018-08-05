package com.code.integration.responses.setup;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.setup.Country;
import com.code.integration.responses.WSResponseBase;

public class WSCountriesResponse extends WSResponseBase {

    private List<Country> countries;

    @XmlElementWrapper(name = "countries")
    @XmlElement(name = "country")
    public List<Country> getCountries() {
	return countries;
    }

    public void setCountries(List<Country> countries) {
	this.countries = countries;
    }

}
