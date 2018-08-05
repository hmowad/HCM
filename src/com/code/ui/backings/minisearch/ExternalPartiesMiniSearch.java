package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingExternalPartyData;
import com.code.enums.MilitaryCivillianEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "externalPartiesMiniSearch")
@ViewScoped
public class ExternalPartiesMiniSearch extends BaseBacking implements Serializable {

    private List<TrainingExternalPartyData> searchExternalPartiesList;
    private List<TrainingExternalPartyData> selectedExtenralPartiesList;

    private String selectedExternalPartiesIds;
    private String selectedExternalPartiesDescriptions;

    private String partyDesc;
    private String partyAddress;

    private int rowsCount;
    private boolean multipleSelectFlag;
    private int mode;

    public ExternalPartiesMiniSearch() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		if (Integer.parseInt(getRequest().getParameter("mode")) == MilitaryCivillianEnum.Military.getCode()) {
		    mode = MilitaryCivillianEnum.Military.getCode();
		    setScreenTitle(getMessage("title_externalPartiesMiniSearch"));

		} else {
		    mode = MilitaryCivillianEnum.Civillian.getCode();
		    setScreenTitle(getMessage("title_civillianExternalPartiesMiniSearch"));
		}
	    }
	    resetForm();
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void searchExternalParties() throws BusinessException {
	searchExternalPartiesList = TrainingSetupService.getTrainingExternalParties(mode, partyDesc, partyAddress);
	searchExternalPartiesList.removeAll(selectedExtenralPartiesList);
	List<TrainingExternalPartyData> tempList = new ArrayList<>();
	for (TrainingExternalPartyData searchExternalParty : searchExternalPartiesList) {
	    boolean found = false;
	    for (TrainingExternalPartyData selectedExternalPartyData : selectedExtenralPartiesList) {
		if (searchExternalParty.getId().equals(selectedExternalPartyData.getId())) {
		    found = true;
		    break;
		}
	    }
	    if (!found)
		tempList.add(searchExternalParty);
	}
	searchExternalPartiesList = tempList;
    }

    public void addSelectedExternalParty(TrainingExternalPartyData trainingExternalPartyData) {
	selectedExtenralPartiesList.add(trainingExternalPartyData);
	searchExternalPartiesList.remove(trainingExternalPartyData);
    }

    public void removeSelectedExternalParty(TrainingExternalPartyData trainingExternalPartyData) {
	selectedExtenralPartiesList.remove(trainingExternalPartyData);

    }

    public void resetForm() throws BusinessException {
	partyAddress = "";
	partyDesc = "";
	selectedExtenralPartiesList = new ArrayList<>();

	searchExternalPartiesList = TrainingSetupService.getTrainingExternalParties(mode, partyDesc, partyAddress);

	if (getRequest().getParameter("multipleSelectFlag") != null) {
	    multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
	    if (multipleSelectFlag) {
		selectedExtenralPartiesList = new ArrayList<>();
		rowsCount = 5;
	    }
	}
    }

    public void generateExternalPartiesInfo() {

	selectedExternalPartiesIds = "";
	selectedExternalPartiesDescriptions = "";
	String comma = "";
	if (selectedExtenralPartiesList.size() > 0) {
	    for (TrainingExternalPartyData qualificationLevel : selectedExtenralPartiesList) {
		selectedExternalPartiesIds += comma + qualificationLevel.getId();
		selectedExternalPartiesDescriptions += comma + qualificationLevel.getDescription();
		comma = ",";
	    }
	}
    }

    public List<TrainingExternalPartyData> getSearchExternalPartiesList() {
	return searchExternalPartiesList;
    }

    public void setSearchExternalPartiesList(List<TrainingExternalPartyData> searchExternalPartiesList) {
	this.searchExternalPartiesList = searchExternalPartiesList;
    }

    public List<TrainingExternalPartyData> getSelectedExtenralPartiesList() {
	return selectedExtenralPartiesList;
    }

    public void setSelectedExtenralPartiesList(List<TrainingExternalPartyData> selectedExtenralPartiesList) {
	this.selectedExtenralPartiesList = selectedExtenralPartiesList;
    }

    public String getSelectedExternalPartiesIds() {
	return selectedExternalPartiesIds;
    }

    public void setSelectedExternalPartiesIds(String selectedExternalPartiesIds) {
	this.selectedExternalPartiesIds = selectedExternalPartiesIds;
    }

    public String getSelectedExternalPartiesDescriptions() {
	return selectedExternalPartiesDescriptions;
    }

    public void setSelectedExternalPartiesDescriptions(String selectedExternalPartiesDescriptions) {
	this.selectedExternalPartiesDescriptions = selectedExternalPartiesDescriptions;
    }

    public String getPartyAddress() {
	return partyAddress;
    }

    public void setPartyAddress(String partyAddress) {
	this.partyAddress = partyAddress;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public String getPartyDesc() {
	return partyDesc;
    }

    public void setPartyDesc(String partyDesc) {
	this.partyDesc = partyDesc;
    }

    public boolean isMultipleSelectFlag() {
	return multipleSelectFlag;
    }

    public void setMultipleSelectFlag(boolean multipleSelectFlag) {
	this.multipleSelectFlag = multipleSelectFlag;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

}