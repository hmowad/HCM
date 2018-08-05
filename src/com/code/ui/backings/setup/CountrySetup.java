package com.code.ui.backings.setup;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.setup.Country;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.util.CountryService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "countrySetup")
@ViewScoped
public class CountrySetup extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private Country country = new Country();

    private List<Country> searchCountryList;

    public CountrySetup() {
	country.setName("");
	searchCountry();
    }

    public void searchCountry() {
	try {
	    super.init();
	    searchCountryList = null;
	    searchCountryList = CountryService.getCountryByCountryDesc(country.getName(), FlagsEnum.ALL.getCode());
	    if (searchCountryList.isEmpty()) {
		super.setServerSideErrorMessages(getMessage("error_noData"));
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addCountry() {
	try {
	    country.setBlackListFlag(0);
	    country.setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    CountryService.saveCountry(country);
	    searchCountryList.add(country);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateCountry(Country country) {
	try {
	    country.setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    CountryService.updateCountry(country);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void removeCountry(Country country) {
	try {
	    country.setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    CountryService.deleteCountry(country);
	    searchCountryList.remove(country);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public Country getCountry() {
	return country;
    }

    public void setCountry(Country country) {
	this.country = country;
    }

    public List<Country> getSearchCountryList() {
	return searchCountryList;
    }

    public void setSearchCountryList(List<Country> searchCountryList) {
	this.searchCountryList = searchCountryList;
    }
}
