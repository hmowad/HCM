package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingExternalPartyData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "externalPartiesMangement")
@ViewScoped
public class ExternalPartiesManagement extends BaseBacking {

    private String externalPartyDescription;
    private String externalPartyAddress;
    private int pageSize = 10;
    private int mode;
    private List<TrainingExternalPartyData> externalPartiesList;

    private int pageNum = 1;

    private String originalExternalPartyDesc; // description before editing object

    public ExternalPartiesManagement() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_externalPartiesManagementCivilianTraining"));
		    break;
		case 2:
		    setScreenTitle(getMessage("title_externalPartiesManagementMilitaryTraining"));
		    break;
		default:
		    break;
		}

		resetForm();

	    } else {
		throw new BusinessException("error_URLError");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void resetForm() throws BusinessException {
	externalPartyDescription = "";
	externalPartyAddress = "";
	externalPartiesList = TrainingSetupService.getTrainingExternalParties(mode, null, null);
    }

    public void searchExternalParties() throws BusinessException {
	externalPartiesList = TrainingSetupService.getTrainingExternalParties(mode, externalPartyDescription, externalPartyAddress);
    }

    public void saveExternalParty(TrainingExternalPartyData trainingExternalPartyData) {
	try {
	    if (trainingExternalPartyData.getId() != null)
		originalExternalPartyDesc = TrainingSetupService.getTrainingExternalPartyById(trainingExternalPartyData.getId()).getDescription();

	    TrainingEmployeesWorkFlow.validateExternalPartyWFBusinessRules(trainingExternalPartyData, originalExternalPartyDesc, false);

	    trainingExternalPartyData.getTrainingExternalParty().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.

	    if (trainingExternalPartyData.getId() == null) {
		TrainingSetupService.addTrainingExternalParty(trainingExternalPartyData);
	    } else {
		TrainingSetupService.updateTrainingExternalParty(trainingExternalPartyData, originalExternalPartyDesc);
	    }

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void deleteExternalParty(TrainingExternalPartyData trainingExternalPartyData) {
	try {
	    if (trainingExternalPartyData.getId() != null) {
		TrainingEmployeesWorkFlow.validateExternalPartyWFBusinessRules(trainingExternalPartyData, null, true);

		trainingExternalPartyData.getTrainingExternalParty().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
		TrainingSetupService.deleteTrainingExternalParty(trainingExternalPartyData);
	    }

	    externalPartiesList.remove(trainingExternalPartyData);

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void addExternalParty() {
	TrainingExternalPartyData newExternalParty = new TrainingExternalPartyData();
	newExternalParty.setType(mode);
	externalPartiesList.add(0, newExternalParty);
	pageNum = 1;

    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public String getExternalPartyAddress() {
	return externalPartyAddress;
    }

    public void setExternalPartyAddress(String externalPartyAddress) {
	this.externalPartyAddress = externalPartyAddress;
    }

    public List<TrainingExternalPartyData> getExternalPartiesList() {
	return externalPartiesList;
    }

    public void setExternalPartiesList(List<TrainingExternalPartyData> externalPartiesList) {
	this.externalPartiesList = externalPartiesList;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public String getExternalPartyDescription() {
	return externalPartyDescription;
    }

    public void setExternalPartyDescription(String externalPartyDescription) {
	this.externalPartyDescription = externalPartyDescription;
    }

    public int getPageNum() {
	return pageNum;
    }

    public void setPageNum(int pageNum) {
	this.pageNum = pageNum;
    }

}
