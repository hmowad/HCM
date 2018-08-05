package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.additionalspecializations.AdditionalSpecialization;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.AdditionalSpecializationsService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "additionalSpecializationsMiniSearch")
@ViewScoped
public class AdditionalSpecializationsMiniSearch extends BaseBacking implements Serializable {

    private int rowsCount = 10;

    private List<AdditionalSpecialization> additionalSpecializationsList;
    private String searchAdditionalSpecializationDesc;

    public AdditionalSpecializationsMiniSearch() {
	try {
	    additionalSpecializationsList = AdditionalSpecializationsService.getAdditionalSpecializations(null);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchAdditionalSpecialization() {
	try {
	    additionalSpecializationsList = AdditionalSpecializationsService.getAdditionalSpecializations(searchAdditionalSpecializationDesc);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<AdditionalSpecialization> getAdditionalSpecializationsList() {
	return additionalSpecializationsList;
    }

    public void setAdditionalSpecializationsList(List<AdditionalSpecialization> additionalSpecializationsList) {
	this.additionalSpecializationsList = additionalSpecializationsList;
    }

    public String getSearchAdditionalSpecializationDesc() {
	return searchAdditionalSpecializationDesc;
    }

    public void setSearchAdditionalSpecializationDesc(String searchAdditionalSpecializationDesc) {
	this.searchAdditionalSpecializationDesc = searchAdditionalSpecializationDesc;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }
}
