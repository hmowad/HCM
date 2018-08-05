package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.navyformations.NavyFormation;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.NavyFormationsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "navyFormationsMiniSearch")
@ViewScoped
public class NavyFormationsMiniSearch extends BaseBacking implements Serializable {

    private int rowsCount = 10;
    private Integer selectedType;
    private String description;
    private Long regionId;
    private List<NavyFormation> searchList;

    public NavyFormationsMiniSearch() {
	if (getRequest().getParameter("regionId") != null)
	    regionId = Long.parseLong(getRequest().getParameter("regionId"));
	selectedType = 0;
	searchNavyFormations();
    }

    public void searchNavyFormations() {
	try {
	    if (selectedType == FlagsEnum.OFF.getCode()) {
		searchList = NavyFormationsService.getNavyVessels(description, regionId);
	    } else if (selectedType == FlagsEnum.ON.getCode()) {
		searchList = NavyFormationsService.getNavyCommittees(description);
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public Integer getselectedType() {
	return selectedType;
    }

    public void setselectedType(Integer selectedType) {
	this.selectedType = selectedType;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public List<NavyFormation> getSearchList() {
	return searchList;
    }

    public void setSearchList(List<NavyFormation> searchList) {
	this.searchList = searchList;
    }

}
