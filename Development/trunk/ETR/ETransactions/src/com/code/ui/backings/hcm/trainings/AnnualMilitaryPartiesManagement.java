package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingAnnualPartyData;
import com.code.dal.orm.hcm.trainings.TrainingExternalPartyData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "annualMilitaryPartiesManagement")
@ViewScoped
public class AnnualMilitaryPartiesManagement extends BaseBacking {

    private List<TrainingAnnualPartyData> annualExternalPartiesList;
    private TrainingExternalPartyData externalPartyData;
    private int pageSize;
    private String trainingUnitName;
    private Long trainingUnitId;
    List<TrainingUnitData> trainingUnits;

    public AnnualMilitaryPartiesManagement() throws BusinessException {
	pageSize = 10;
	setScreenTitle(getMessage("title_annualMilitaryPartiesManagement"));
	trainingUnits = TrainingSetupService.getAllTrainingUnitsData();
	trainingUnitId = trainingUnits.get(0).getUnitId();
	selectTrainingUnit();
    }

    public void addAnnualExternalParty() {
	try {
	    TrainingAnnualPartyData party = new TrainingAnnualPartyData();
	    party.setExternalPartyId(externalPartyData.getId());
	    party.setTrainingUnitId(trainingUnitId);
	    party.setExternalPartyDesc(externalPartyData.getDescription());
	    party.getTrainingAnnualParty().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    TrainingSetupService.addAnnualParty(party);
	    annualExternalPartiesList.add(party);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void deleteAnnualExternalParty(TrainingAnnualPartyData annualPartyData) {
	try {
	    annualPartyData.getTrainingAnnualParty().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    TrainingSetupService.deleteAnnualParty(annualPartyData);
	    annualExternalPartiesList.remove(annualPartyData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void selectTrainingUnit() throws BusinessException {
	externalPartyData = new TrainingExternalPartyData();
	if (trainingUnitId != null) {
	    annualExternalPartiesList = TrainingSetupService.getAnnualPartiesDataByTrainingUnitId(trainingUnitId);
	}
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<TrainingAnnualPartyData> getAnnualExternalPartiesList() {
	return annualExternalPartiesList;
    }

    public void setAnnualExternalPartiesList(List<TrainingAnnualPartyData> annualExternalPartiesList) {
	this.annualExternalPartiesList = annualExternalPartiesList;
    }

    public String getTrainingUnitName() {
	return trainingUnitName;
    }

    public void setTrainingUnitName(String trainingUnitName) {
	this.trainingUnitName = trainingUnitName;
    }

    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    public TrainingExternalPartyData getExternalPartyData() {
	return externalPartyData;
    }

    public void setExternalPartyData(TrainingExternalPartyData externalPartyData) {
	this.externalPartyData = externalPartyData;
    }

    public List<TrainingUnitData> getTrainingUnits() {
	return trainingUnits;
    }

    public void setTrainingUnits(List<TrainingUnitData> trainingUnits) {
	this.trainingUnits = trainingUnits;
    }

}
