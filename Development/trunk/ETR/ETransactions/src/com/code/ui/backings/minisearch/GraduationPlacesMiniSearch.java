package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.GraduationPlaceDetailData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "graduationPlacesMiniSearch")
@ViewScoped
public class GraduationPlacesMiniSearch extends BaseBacking implements Serializable {

    private int rowsCount = 10;

    private Boolean multipleSelectFlag;

    private String graduationPlaceDetailDesc;
    private String graduationPlaceDesc;
    private String graduationPlaceCountryName;
    private List<GraduationPlaceDetailData> searchList;
    private List<GraduationPlaceDetailData> selectedGraduationPlacesList;
    private String graduationPlacesString;
    private String graduationPlacesIDString;

    public GraduationPlacesMiniSearch() {
	searchList = new ArrayList<GraduationPlaceDetailData>();

	if (getRequest().getParameter("multipleSelectFlag") != null) {
	    multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
	    if (multipleSelectFlag) {
		selectedGraduationPlacesList = new ArrayList<GraduationPlaceDetailData>();
		rowsCount = 5;
	    }
	}

	searchGraduationPlacesDetailsData();
    }

    public void searchGraduationPlacesDetailsData() {
	try {
	    searchList = TrainingSetupService.getGraduationPlacesDetails(graduationPlaceDetailDesc, graduationPlaceDesc, graduationPlaceCountryName);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addSelectedGraduationPlace(GraduationPlaceDetailData graduationPlace) {
	selectedGraduationPlacesList.add(graduationPlace);
	searchList.remove(graduationPlace);
	generateGraduationPlacesString();
    }

    public void removeSelectedGraduationPlace(GraduationPlaceDetailData graduationPlace) {
	selectedGraduationPlacesList.remove(graduationPlace);
	searchList.add(graduationPlace);
	generateGraduationPlacesString();
    }

    private void generateGraduationPlacesString() {
	graduationPlacesString = "";
	graduationPlacesIDString = "";
	if (selectedGraduationPlacesList.size() > 0) {
	    for (GraduationPlaceDetailData graduationPlace : selectedGraduationPlacesList) {
		graduationPlacesString += graduationPlace.getDescription() + " \\ " + graduationPlace.getGraduationPlaceDesc() + " \\ " + graduationPlace.getGraduationPlaceCountryName() + " , " + "\n";

		graduationPlacesIDString += graduationPlace.getId() + ",";
	    }

	    graduationPlacesString = graduationPlacesString.substring(0, graduationPlacesString.length() - 4);
	    graduationPlacesIDString = graduationPlacesIDString.substring(0, graduationPlacesIDString.length() - 1);
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public List<GraduationPlaceDetailData> getSearchList() {
	return searchList;
    }

    public void setSearchList(List<GraduationPlaceDetailData> searchList) {
	this.searchList = searchList;
    }

    public String getGraduationPlaceDesc() {
	return graduationPlaceDesc;
    }

    public void setGraduationPlaceDesc(String graduationPlaceDesc) {
	this.graduationPlaceDesc = graduationPlaceDesc;
    }

    public String getGraduationPlaceDetailDesc() {
	return graduationPlaceDetailDesc;
    }

    public void setGraduationPlaceDetailDesc(String graduationPlaceDetailDesc) {
	this.graduationPlaceDetailDesc = graduationPlaceDetailDesc;
    }

    public String getGraduationPlaceCountryName() {
	return graduationPlaceCountryName;
    }

    public void setGraduationPlaceCountryName(String graduationPlaceCountryName) {
	this.graduationPlaceCountryName = graduationPlaceCountryName;
    }

    public List<GraduationPlaceDetailData> getselectedGraduationPlacesList() {
	return selectedGraduationPlacesList;
    }

    public void setselectedGraduationPlacesList(List<GraduationPlaceDetailData> selectedGraduationPlacesList) {
	this.selectedGraduationPlacesList = selectedGraduationPlacesList;
    }

    public String getgraduationPlacesString() {
	return graduationPlacesString;
    }

    public void setgraduationPlacesString(String graduationPlacesString) {
	this.graduationPlacesString = graduationPlacesString;
    }

    public String getgraduationPlacesIDString() {
	return graduationPlacesIDString;
    }

    public void setgraduationPlacesIDString(String graduationPlacesIDString) {
	this.graduationPlacesIDString = graduationPlacesIDString;
    }

    public Boolean getMultipleSelectFlag() {
	return multipleSelectFlag;
    }

    public void setMultipleSelectFlag(Boolean multipleSelectFlag) {
	this.multipleSelectFlag = multipleSelectFlag;
    }

}
