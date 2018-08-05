package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.specializations.MinorSpecializationData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.SpecializationsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "minorSpecializationsMiniSearch")
@ViewScoped
public class MinorSpecializationsMiniSearch extends BaseBacking implements Serializable {

    private int rowsCount = 10;
    private boolean multipleSelectFlag;
    private int mode = 1;

    private String searchMinorSpecDesc;
    private String searchMajorSpecDesc;

    private int searchGeneralSpecialization;
    private int searchGeneralType;

    private List<MinorSpecializationData> searchList;
    private List<MinorSpecializationData> selectedMinorSpecsList;

    private String selectedMinorSpecsIds;
    private String selectedMinorSpecsDescriptions;

    public MinorSpecializationsMiniSearch() {
	searchMinorSpecDesc = "";
	searchMajorSpecDesc = "";

	searchGeneralSpecialization = FlagsEnum.ALL.getCode();
	searchGeneralType = FlagsEnum.ALL.getCode();

	if (getRequest().getParameter("mode") != null)
	    mode = Integer.parseInt(getRequest().getParameter("mode"));

	if (getRequest().getParameter("multipleSelectFlag") != null) {
	    multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
	    if (multipleSelectFlag) {
		selectedMinorSpecsList = new ArrayList<MinorSpecializationData>();
		rowsCount = 5;
	    }
	}

	searchJobMinorSpecialization();
    }

    public void searchJobMinorSpecialization() {
	try {
	    switch (mode) {
	    case 1:
		// search all the minor specializations with all possible general types
		searchList = SpecializationsService.getMinorSpecializations(FlagsEnum.ALL.getCode(), searchMinorSpecDesc, FlagsEnum.ALL.getCode(), searchMajorSpecDesc, searchGeneralSpecialization, searchGeneralType);
		break;
	    case 2:
		// exclude the general type "FIELD" strictly from the search criteria as it is not allowed for the employees.
		searchList = SpecializationsService.getMinorSpecializationsForCivilians(FlagsEnum.ALL.getCode(), searchMinorSpecDesc, FlagsEnum.ALL.getCode(), searchMajorSpecDesc, FlagsEnum.ALL.getCode(), searchGeneralType);
		break;
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addSelectedMinorSpecialization(MinorSpecializationData minorSpecializationData) {
	selectedMinorSpecsList.add(minorSpecializationData);
	searchList.remove(minorSpecializationData);
    }

    public void removeSelectedMinorSpecialization(MinorSpecializationData minorSpecializationData) {
	selectedMinorSpecsList.remove(minorSpecializationData);
	searchList.add(minorSpecializationData);
    }

    public void generateMinorSpecializationsInfo() {
	selectedMinorSpecsIds = "";
	selectedMinorSpecsDescriptions = "";
	String comma = "";
	if (selectedMinorSpecsList.size() > 0) {
	    for (MinorSpecializationData minorSpecializationData : selectedMinorSpecsList) {
		selectedMinorSpecsIds += comma + minorSpecializationData.getId();
		selectedMinorSpecsDescriptions += comma + minorSpecializationData.getDescription();
		comma = ",";
	    }
	}
	if (selectedMinorSpecsIds.length() == 0) {
	    selectedMinorSpecsIds = "";
	    selectedMinorSpecsDescriptions = "";
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public boolean getMultipleSelectFlag() {
	return multipleSelectFlag;
    }

    public void setMultipleSelectFlag(boolean multipleSelectFlag) {
	this.multipleSelectFlag = multipleSelectFlag;
    }

    public List<MinorSpecializationData> getSearchList() {
	return searchList;
    }

    public void setSearchList(List<MinorSpecializationData> searchList) {
	this.searchList = searchList;
    }

    public List<MinorSpecializationData> getSelectedMinorSpecsList() {
	return selectedMinorSpecsList;
    }

    public void setSelectedMinorSpecsList(List<MinorSpecializationData> selectedMinorSpecsList) {
	this.selectedMinorSpecsList = selectedMinorSpecsList;
    }

    public String getSelectedMinorSpecsIds() {
	return selectedMinorSpecsIds;
    }

    public void setSelectedMinorSpecsIds(String selectedMinorSpecsIds) {
	this.selectedMinorSpecsIds = selectedMinorSpecsIds;
    }

    public String getSelectedMinorSpecsDescriptions() {
	return selectedMinorSpecsDescriptions;
    }

    public void setSelectedMinorSpecsDescriptions(String selectedMinorSpecsDescriptions) {
	this.selectedMinorSpecsDescriptions = selectedMinorSpecsDescriptions;
    }

    public String getSearchMinorSpecDesc() {
	return searchMinorSpecDesc;
    }

    public void setSearchMinorSpecDesc(String searchMinorSpecDesc) {
	this.searchMinorSpecDesc = searchMinorSpecDesc;
    }

    public String getSearchMajorSpecDesc() {
	return searchMajorSpecDesc;
    }

    public void setSearchMajorSpecDesc(String searchMajorSpecDesc) {
	this.searchMajorSpecDesc = searchMajorSpecDesc;
    }

    public int getSearchGeneralSpecialization() {
	return searchGeneralSpecialization;
    }

    public void setSearchGeneralSpecialization(int searchGeneralSpecialization) {
	this.searchGeneralSpecialization = searchGeneralSpecialization;
    }

    public int getSearchGeneralType() {
	return searchGeneralType;
    }

    public void setSearchGeneralType(int searchGeneralType) {
	this.searchGeneralType = searchGeneralType;
    }

    public int getMode() {
	return mode;
    }
}