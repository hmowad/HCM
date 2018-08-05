package com.code.ui.backings.hcm.additionalspecializations;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.additionalspecializations.AdditionalSpecialization;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.AdditionalSpecializationsService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "additionalSpecializationsSetup")
@ViewScoped
public class AdditionalSpecializationsSetup extends BaseBacking {

    private int rowsCount = 10;
    private int pageNum;

    private List<AdditionalSpecialization> additionalSpecializationsList;
    private String searchAdditionalSpecializationDesc;

    public AdditionalSpecializationsSetup() {
	reset();
    }

    public void reset() {
	searchAdditionalSpecializationDesc = null;
	searchAdditionalSpecialization();
    }

    public void searchAdditionalSpecialization() {
	try {
	    additionalSpecializationsList = AdditionalSpecializationsService.getAdditionalSpecializations(searchAdditionalSpecializationDesc);
	    pageNum = 1;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void add() {
	additionalSpecializationsList.add(0, new AdditionalSpecialization());
	pageNum = 1;
    }

    public void save(AdditionalSpecialization additionalSpecialization) {
	try {
	    if (additionalSpecialization.getId() == null)
		AdditionalSpecializationsService.addAdditionalSpecialization(additionalSpecialization);
	    else
		AdditionalSpecializationsService.updateAdditionalSpecialization(additionalSpecialization);

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void delete(AdditionalSpecialization additionalSpecialization) {
	try {
	    if (additionalSpecialization.getId() != null)
		AdditionalSpecializationsService.deleteAdditionalSpecialization(additionalSpecialization);
	    additionalSpecializationsList.remove(additionalSpecialization);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
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

    public int getPageNum() {
	return pageNum;
    }

    public void setPageNum(int pageNum) {
	this.pageNum = pageNum;
    }
}
