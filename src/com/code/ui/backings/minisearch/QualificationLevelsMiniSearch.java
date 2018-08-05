package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "qualificationLevelsMiniSearch")
@ViewScoped
public class QualificationLevelsMiniSearch extends BaseBacking implements Serializable {

    private List<QualificationLevel> searchQualificationLevelsList;
    private List<QualificationLevel> selectedQualificationLevelsList;

    private String selectedQualificationLevelsIds;
    private String selectedQualificationLevelsDescriptions;

    private int rowsCount;

    public QualificationLevelsMiniSearch() {
	try {
	    rowsCount = 5;
	    selectedQualificationLevelsList = new ArrayList<>();
	    searchQualificationLevelsList = TrainingSetupService.getAllQualificationLevels();
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void addSelectedQualificationLevel(QualificationLevel qualificationLevel) {
	selectedQualificationLevelsList.add(qualificationLevel);
	searchQualificationLevelsList.remove(qualificationLevel);
    }

    public void removeSelectedQualificationLevel(QualificationLevel qualificationLevel) {
	selectedQualificationLevelsList.remove(qualificationLevel);
	searchQualificationLevelsList.add(qualificationLevel);
    }

    public void generateQualificationLevelsInfo() {

	selectedQualificationLevelsIds = "";
	selectedQualificationLevelsDescriptions = "";
	String comma = "";
	if (selectedQualificationLevelsList.size() > 0) {
	    for (QualificationLevel qualificationLevel : selectedQualificationLevelsList) {
		selectedQualificationLevelsIds += comma + qualificationLevel.getId();
		selectedQualificationLevelsDescriptions += comma + qualificationLevel.getDescription();
		comma = ",";
	    }
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public List<QualificationLevel> getSearchQualificationLevelsList() {
	return searchQualificationLevelsList;
    }

    public void setSearchQualificationLevelsList(List<QualificationLevel> searchQualificationLevelsList) {
	this.searchQualificationLevelsList = searchQualificationLevelsList;
    }

    public List<QualificationLevel> getSelectedQualificationLevelsList() {
	return selectedQualificationLevelsList;
    }

    public void setSelectedQualificationLevelsList(List<QualificationLevel> selectedQualificationLevelsList) {
	this.selectedQualificationLevelsList = selectedQualificationLevelsList;
    }

    public String getSelectedQualificationLevelsIds() {
	return selectedQualificationLevelsIds;
    }

    public void setSelectedQualificationLevelsIds(String selectedQualificationLevelsIds) {
	this.selectedQualificationLevelsIds = selectedQualificationLevelsIds;
    }

    public String getselectedQualificationLevelsDescriptions() {
	return selectedQualificationLevelsDescriptions;
    }

    public void setselectedQualificationLevelsDescriptions(String selectedQualificationLevelsDescriptions) {
	this.selectedQualificationLevelsDescriptions = selectedQualificationLevelsDescriptions;
    }

}