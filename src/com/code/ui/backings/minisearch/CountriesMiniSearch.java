package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.setup.Country;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.util.CountryService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "countriesMiniSearch")
@ViewScoped
public class CountriesMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 5;
    private String searchCountryName;
    private int mode;

    private List<Country> searchCountryList;

    private List<Country> selectedCountryList = new ArrayList<Country>();

    private String totalCountriesNames;

    public CountriesMiniSearch() {
	searchCountryName = "";
	mode = Integer.parseInt(getRequest().getParameter("mode"));
	searchCountry();
    }

    public void searchCountry() {
	try {
	    super.init();
	    searchCountryList = null;
	    searchCountryList = CountryService.getCountryByCountryDesc(searchCountryName, FlagsEnum.OFF.getCode());
	    if (searchCountryList.isEmpty()) {
		super.setServerSideErrorMessages(getMessage("error_noData"));
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addSelectedCountry(Country country) {
	selectedCountryList.add(country);
	searchCountryList.remove(country);
	generateCountryNames();
    }

    public void removeSelectedCountry(Country country) {
	selectedCountryList.remove(country);
	searchCountryList.add(country);
	generateCountryNames();
    }

    private void generateCountryNames() {
	totalCountriesNames = "";
	if (selectedCountryList.size() > 0) {
	    for (Country country : selectedCountryList) {
		totalCountriesNames += country.getName() + "/";
	    }

	    totalCountriesNames = totalCountriesNames.substring(0, totalCountriesNames.length() - 1);
	}

	if (totalCountriesNames.length() > 100 || totalCountriesNames.length() == 0) {
	    super.setServerSideErrorMessages(getMessage("error_countriesListMax"));
	    totalCountriesNames = "";
	}
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public String getSearchCountryName() {
	return searchCountryName;
    }

    public void setSearchCountryName(String searchCountryName) {
	this.searchCountryName = searchCountryName;
    }

    public List<Country> getSearchCountryList() {
	return searchCountryList;
    }

    public void setSearchCountryList(List<Country> searchCountryList) {
	this.searchCountryList = searchCountryList;
    }

    public List<Country> getSelectedCountryList() {
	return selectedCountryList;
    }

    public void setSelectedCountryList(List<Country> selectedCountryList) {
	this.selectedCountryList = selectedCountryList;
    }

    public String getTotalCountriesNames() {
	return totalCountriesNames;
    }

    public void setTotalCountriesNames(String totalCountriesNames) {
	this.totalCountriesNames = totalCountriesNames;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}