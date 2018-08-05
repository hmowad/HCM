package com.code.ui.backings.hcm.trainings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.GraduationPlaceData;
import com.code.dal.orm.hcm.trainings.GraduationPlaceDetailData;
import com.code.enums.GraduationPlacesTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "graduationPlacesManagement")
@ViewScoped
public class GraduationPlacesManagement extends BaseBacking implements Serializable {
    private int pageSize = 10;

    private int graduationPlaceType;
    private String graduationPlaceDesc;
    private String graduationPlaceCountryName;

    private Long selectedCountryId;
    private String selectedCountryName;

    private List<GraduationPlaceData> graduationPlaces;
    private List<GraduationPlaceDetailData> graduationPlaceDetails;

    private GraduationPlaceData selectedGraduationPlace;

    private String originalDesc;

    private int graduationPlacePageNum = 1;
    private int graduationPlaceDetailPageNum = 1;

    public GraduationPlacesManagement() {
	graduationPlaceType = GraduationPlacesTypesEnum.UNIVERSITIES.getCode();
	reset();
    }

    public void search() {
	try {
	    graduationPlaces = TrainingSetupService.getGraduationPlacesData(graduationPlaceDesc, graduationPlaceCountryName, graduationPlaceType);
	    if (!graduationPlaces.isEmpty()) {
		selectGraduationPlace(graduationPlaces.get(0));
	    } else {
		selectedGraduationPlace = null;
		graduationPlaceDetails = new ArrayList<GraduationPlaceDetailData>();
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	graduationPlaceDesc = graduationPlaceCountryName = null;
	search();
    }

    public void selectGraduationPlace(GraduationPlaceData graduationPlace) {
	try {
	    selectedGraduationPlace = graduationPlace;
	    graduationPlaceDetails = TrainingSetupService.getGraduationPlacesDetailsByGraduationPlaceId(selectedGraduationPlace.getId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addGraduationPlace() {
	selectedGraduationPlace = new GraduationPlaceData();
	selectedGraduationPlace.setType(graduationPlaceType);
	selectedGraduationPlace.setCountryId(selectedCountryId);
	selectedGraduationPlace.setCountryName(selectedCountryName);

	if (graduationPlaceType == GraduationPlacesTypesEnum.INSTITUTES.getCode()) {
	    selectedGraduationPlace.setDescription(getMessage("label_militaryFacultiesAndInstitutes") + " " + getMessage("label_in") + selectedCountryName);
	}

	if (graduationPlaceType == GraduationPlacesTypesEnum.SCHOOLS.getCode()) {
	    selectedGraduationPlace.setDescription(getMessage("label_schools") + " " + selectedCountryName);
	}

	graduationPlaces.add(0, selectedGraduationPlace);
	graduationPlacePageNum = 1;
	graduationPlaceDetails = new ArrayList<GraduationPlaceDetailData>();

    }

    public void saveGraduationPlace(GraduationPlaceData graduationPlace) {
	try {
	    graduationPlace.getGraduationPlace().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.

	    if (graduationPlace.getId() == null) {
		TrainingSetupService.addGraduationPlace(graduationPlace);
	    } else {
		originalDesc = TrainingSetupService.getGraduationPlaceDataById(graduationPlace.getId()).getDescription();
		TrainingEmployeesWorkFlow.validateGraduatioPlaceWFBusinessRules(graduationPlace, originalDesc);
		TrainingSetupService.updateGraduationPlace(graduationPlace, originalDesc);
	    }

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteGraduationPlace(GraduationPlaceData graduationPlace) {
	try {
	    if (graduationPlace.getId() != null) {
		graduationPlace.getGraduationPlace().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
		TrainingSetupService.deleteGraduationPlace(graduationPlace);
	    }

	    graduationPlaces.remove(graduationPlace);

	    if (selectedGraduationPlace == graduationPlace)
		selectedGraduationPlace = null;
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addGraduationPlaceDetail() {
	GraduationPlaceDetailData graduationPlaceDetail = new GraduationPlaceDetailData();
	graduationPlaceDetail.setGraduationPlaceId(selectedGraduationPlace.getId());
	graduationPlaceDetail.setGraduationPlaceType(graduationPlaceType);
	graduationPlaceDetails.add(0, graduationPlaceDetail);
	graduationPlaceDetailPageNum = 1;
    }

    public void saveGraduationPlaceDetail(GraduationPlaceDetailData graduationPlaceDetail) {
	try {
	    graduationPlaceDetail.getGraduationPlaceDetail().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.

	    if (graduationPlaceDetail.getId() == null) {
		TrainingSetupService.addGraduationPlaceDetail(graduationPlaceDetail);
	    } else {
		originalDesc = TrainingSetupService.getGraduationPlaceDetailDataById(graduationPlaceDetail.getId()).getDescription();
		TrainingEmployeesWorkFlow.validateGraduatioPlaceDetailWFBusinessRules(graduationPlaceDetail, originalDesc, false);
		TrainingSetupService.updateGraduationPlaceDetail(graduationPlaceDetail, originalDesc);
	    }

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteGraduationPlaceDetail(GraduationPlaceDetailData graduationPlaceDetail) {
	try {
	    if (graduationPlaceDetail.getId() != null) {
		graduationPlaceDetail.getGraduationPlaceDetail().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
		TrainingEmployeesWorkFlow.validateGraduatioPlaceDetailWFBusinessRules(graduationPlaceDetail, null, true);
		TrainingSetupService.deleteGraduationPlaceDetail(graduationPlaceDetail);
	    }

	    graduationPlaceDetails.remove(graduationPlaceDetail);

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public int getPageSize() {
	return pageSize;
    }

    public String getGraduationPlaceDesc() {
	return graduationPlaceDesc;
    }

    public void setGraduationPlaceDesc(String graduationPlaceDesc) {
	this.graduationPlaceDesc = graduationPlaceDesc;
    }

    public String getGraduationPlaceCountryName() {
	return graduationPlaceCountryName;
    }

    public void setGraduationPlaceCountryName(String graduationPlaceCountryName) {
	this.graduationPlaceCountryName = graduationPlaceCountryName;
    }

    public List<GraduationPlaceData> getGraduationPlaces() {
	return graduationPlaces;
    }

    public void setGraduationPlaces(List<GraduationPlaceData> graduationPlaces) {
	this.graduationPlaces = graduationPlaces;
    }

    public List<GraduationPlaceDetailData> getGraduationPlaceDetails() {
	return graduationPlaceDetails;
    }

    public void setGraduationPlaceDetails(List<GraduationPlaceDetailData> graduationPlaceDetails) {
	this.graduationPlaceDetails = graduationPlaceDetails;
    }

    public GraduationPlaceData getSelectedGraduationPlace() {
	return selectedGraduationPlace;
    }

    public void setSelectedGraduationPlace(GraduationPlaceData selectedGraduationPlace) {
	this.selectedGraduationPlace = selectedGraduationPlace;
    }

    public int getGraduationPlaceType() {
	return graduationPlaceType;
    }

    public void setGraduationPlaceType(int graduationPlaceType) {
	this.graduationPlaceType = graduationPlaceType;
    }

    public Long getSelectedCountryId() {
	return selectedCountryId;
    }

    public void setSelectedCountryId(Long selectedCountryId) {
	this.selectedCountryId = selectedCountryId;
    }

    public String getSelectedCountryName() {
	return selectedCountryName;
    }

    public void setSelectedCountryName(String selectedCountryName) {
	this.selectedCountryName = selectedCountryName;
    }

    public String getGraduationPlaceDetailsHeader() {
	if (selectedGraduationPlace == null || selectedGraduationPlace.getId() == null) {
	    if (graduationPlaceType == GraduationPlacesTypesEnum.UNIVERSITIES.getCode())
		return getMessage("label_theFaculties");
	    if (graduationPlaceType == GraduationPlacesTypesEnum.INSTITUTES.getCode())
		return getMessage("label_militaryFacultiesAndInstitutes");
	    if (graduationPlaceType == GraduationPlacesTypesEnum.SCHOOLS.getCode())
		return getMessage("label_theSchools");

	} else if (graduationPlaceType == GraduationPlacesTypesEnum.UNIVERSITIES.getCode()) {
	    return getParameterizedMessage("label_theFacultiesOfUniversity", selectedGraduationPlace.getDescription());
	} else {
	    return selectedGraduationPlace.getDescription();
	}

	return null;
    }

    public int getGraduationPlacePageNum() {
	return graduationPlacePageNum;
    }

    public int getGraduationPlaceDetailPageNum() {
	return graduationPlaceDetailPageNum;
    }

    public void setGraduationPlacePageNum(int graduationPlacePageNum) {
	this.graduationPlacePageNum = graduationPlacePageNum;
    }

    public void setGraduationPlaceDetailPageNum(int graduationPlaceDetailPageNum) {
	this.graduationPlaceDetailPageNum = graduationPlaceDetailPageNum;
    }
}
