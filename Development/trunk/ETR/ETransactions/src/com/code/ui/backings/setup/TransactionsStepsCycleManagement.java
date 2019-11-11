package com.code.ui.backings.setup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.enums.WFProcessStepRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.extensionrequest.WFProcess;
import com.code.integration.parameters.extensionrequest.WFProcessStepData;
import com.code.integration.webservicesclients.eservices.WFProcessStepsClient;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = "transactionsStepsCycleManagement")
public class TransactionsStepsCycleManagement extends BaseBacking implements Serializable {

	private int rowsCount = 10;
	private List<Category> employeeCategoryList;
	private String name;
	private Long selectedBasicJopNameId;
	private String selectedBasicJopNameName;
	private WFProcess selectedwfProcessData;
	private List<WFProcess> wfProcessDataList;
	private List<WFProcessStepData> wfProcessStepDataListToBeDisplayed;
	private List<WFProcessStepData> wfProcessStepDataListToBeDeleted;

	private Boolean showWfProcessDetails;

	public TransactionsStepsCycleManagement() throws Exception {
		wfProcessStepDataListToBeDisplayed = new ArrayList<>();
		wfProcessStepDataListToBeDeleted = new ArrayList<>();
		wfProcessDataList = WFProcessStepsClient.getAllWfProcess();
		selectedwfProcessData = new WFProcess();
		showWfProcessDetails = false;
	}

	public void showWfTransactionDetails(WFProcess wfProcess) {
		try {
			showWfProcessDetails = true;
			selectedwfProcessData = wfProcess;
			wfProcessStepDataListToBeDisplayed = WFProcessStepsClient.getWfProcessDetails(wfProcess.getId());
			wfProcessStepDataListToBeDeleted.clear();
		} catch (BusinessException e) {
			setServerSideErrorMessages(getMessage(e.getMessage()));
		}
	}

	public void getSelectedWFProcessData() {
		for (WFProcessStepData wfProcessStepData : wfProcessStepDataListToBeDisplayed) {
		    if (wfProcessStepData.getParticipants().equals(selectedBasicJopNameId.toString())) {
			    setServerSideErrorMessages(getMessage("error_repeatedValue"));
			    return;
		    }
		}
		
		WFProcessStepData wfProcessStep = new WFProcessStepData();
		wfProcessStep.setParticipants(selectedBasicJopNameId.toString());
		wfProcessStep.setParticipantsNames(selectedBasicJopNameName);
		wfProcessStep.setApprove(true);
		wfProcessStep.setReject(true);
		if (wfProcessStepDataListToBeDisplayed == null || wfProcessStepDataListToBeDisplayed.size() == 0)
			wfProcessStep.setStepOrder(1);
		else {
			wfProcessStep.setStepOrder( wfProcessStepDataListToBeDisplayed.get(wfProcessStepDataListToBeDisplayed.size() - 1).getStepOrder() + 1);
		}
		wfProcessStep.setProcessId(selectedwfProcessData.getId());
		wfProcessStep.setStepRole(WFProcessStepRolesEnum.APPROVAL.getCode());
		wfProcessStepDataListToBeDisplayed.add(wfProcessStep);
	}

	public void deleteWfProcessStep(WFProcessStepData wfProcessStep) {

		int stepIndex = wfProcessStepDataListToBeDisplayed.indexOf(wfProcessStep);
		int stepOrder = wfProcessStep.getStepOrder();
		for (int i = stepIndex + 1; i < wfProcessStepDataListToBeDisplayed.size(); i++) {
			wfProcessStepDataListToBeDisplayed.get(i).setStepOrder(stepOrder);
			stepOrder++;
		}
		wfProcessStepDataListToBeDisplayed.remove(wfProcessStep);

		if (wfProcessStep.getId() != null) {
			wfProcessStep.setToBeDeleted(true);
			wfProcessStepDataListToBeDeleted.add(wfProcessStep);
		}

		setServerSideSuccessMessages(getMessage("notify_successOperation"));
	}

	public void enableWfProcess(WFProcess wfProcess) {
		try {
			if (wfProcess.getEnableFlagBoolean()) {
				WFProcessStepsClient.enableWfProcess(wfProcess);
			}
		} catch (BusinessException e) {
			setServerSideErrorMessages(getMessage(e.getMessage()));
		}
	}

	public void save() {
		try {
			List<WFProcessStepData> wfProcessStepToBeSaved = new ArrayList<>();
			wfProcessStepToBeSaved.addAll(wfProcessStepDataListToBeDeleted);
			wfProcessStepToBeSaved.addAll(wfProcessStepDataListToBeDisplayed);

			WFProcessStepsClient.saveOrUpdateWfProcessStep(wfProcessStepToBeSaved);
			wfProcessStepDataListToBeDeleted = new ArrayList<>();
			setServerSideSuccessMessages(getMessage("notify_successOperation"));
		} catch (BusinessException e) {
			setServerSideErrorMessages(getMessage(e.getMessage()));
		}
	}

	/*****************************************************
	 * Getters and Setters
	 ********************************************************/

	public List<Category> getEmployeeCategoryList() {
		return employeeCategoryList;
	}

	public void setEmployeeCategoryList(List<Category> employeeCategoryList) {
		this.employeeCategoryList = employeeCategoryList;
	}

	public Boolean getShowWfProcessDetails() {
		return showWfProcessDetails;
	}

	public void setShowWfProcessDetails(Boolean showWfProcessDetails) {
		this.showWfProcessDetails = showWfProcessDetails;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WFProcess> getWfProcessDataList() {
		return wfProcessDataList;
	}

	public void setWfProcessDataList(List<WFProcess> wfProcessDataList) {
		this.wfProcessDataList = wfProcessDataList;
	}

	public List<WFProcessStepData> getWfProcessStepDataListToBeDisplayed() {
		return wfProcessStepDataListToBeDisplayed;
	}

	public void setWfProcessStepDataListToBeDisplayed(List<WFProcessStepData> wfProcessStepDataListToBeDisplayed) {
		this.wfProcessStepDataListToBeDisplayed = wfProcessStepDataListToBeDisplayed;
	}

	public List<WFProcessStepData> getWfProcessStepDataListToBeDeleted() {
		return wfProcessStepDataListToBeDeleted;
	}

	public void setWfProcessStepDataListToBeDeleted(List<WFProcessStepData> wfProcessStepDataListToBeDeleted) {
		this.wfProcessStepDataListToBeDeleted = wfProcessStepDataListToBeDeleted;
	}

	public WFProcess getSelectedwfProcessData() {
		return selectedwfProcessData;
	}

	public void setSelectedwfProcessData(WFProcess selectedwfProcessData) {
		this.selectedwfProcessData = selectedwfProcessData;
	}

	public int getRowsCount() {
		return rowsCount;
	}

	public void setRowsCount(int rowsCount) {
		this.rowsCount = rowsCount;
	}

	public Long getSelectedBasicJopNameId() {
		return selectedBasicJopNameId;
	}

	public void setSelectedBasicJopNameId(Long selectedBasicJopNameId) {
		this.selectedBasicJopNameId = selectedBasicJopNameId;
	}

	public String getSelectedBasicJopNameName() {
		return selectedBasicJopNameName;
	}

	public void setSelectedBasicJopNameName(String selectedBasicJopNameName) {
		this.selectedBasicJopNameName = selectedBasicJopNameName;
	}

}