package com.code.ui.backings.hcm.vacations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.hcm.VacationsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "vacationsCollectiveApproval")
@ViewScoped
public class VacationsCollectiveApproval extends BaseBacking implements Serializable {
    // object[0] - WFVacation
    // object[1] - WFTask
    // object[2] - WFInstance
    // object[3] - processName
    // object[4] - requester
    // object[5] - beneficiary
    // object[6] - delegatingName
    private ArrayList<Object> vacationsTasks;

    private boolean selectAll;
    private int vacationsTasksListSize;

    private final static String taskUrlParam = "&taskId=";
    private final static int pageSize = 10;

    private int mode = -1; // 1 means acceptance and 2 means approval

    public VacationsCollectiveApproval() {
	super();
	if (getRequest().getAttribute("mode") != null)
	    this.mode = Integer.parseInt(getRequest().getAttribute("mode").toString());

	if (mode == 1)
	    this.setScreenTitle(getMessage("title_VacationsCollectiveAcceptance"));
	else if (mode == 2)
	    this.setScreenTitle(getMessage("title_VacationsCollectiveApproval"));
	else
	    setServerSideErrorMessages(getMessage("error_general"));

	try {
	    // Load the vacations tasks data
	    searchVacationTasks();
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    // Load the vacations tasks data
    private void searchVacationTasks() {
	try {
	    selectAll = false;
	    vacationsTasks = (ArrayList<Object>) VacationsWorkFlow.getWFVacationsTasks(this.loginEmpData.getEmpId(), mode == 1 ? WFTaskRolesEnum.DIRECT_MANAGER.getCode() : WFTaskRolesEnum.SIGN_MANAGER.getCode());
	    vacationsTasksListSize = vacationsTasks.size();
	} catch (BusinessException e) {
	    vacationsTasks = new ArrayList<Object>();
	    vacationsTasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // called from the xhtml on change of the selectAll checkbox
    public void selectUnselectAllRows() {
	for (int i = 0; i < vacationsTasks.size(); i++) {
	    ((WFVacation) (((Object[]) vacationsTasks.get(i))[0])).setSelected(selectAll);
	}
    }

    // Loop over the selected tasks for approval or acceptance
    // called from the xhtml when "Approve" button clicked
    public void doVacationsCollectiveAction() {
	try {
	    List<Object> selectedVacationsTasks = new ArrayList<Object>();
	    for (Object obj : vacationsTasks) {
		if (((WFVacation) (((Object[]) obj)[0])).getSelected())
		    selectedVacationsTasks.add(obj);
	    }
	    VacationsWorkFlow.doVacationsCollectiveAction(selectedVacationsTasks, mode);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

	// Call the search method here to Reload the tasks
	searchVacationTasks();
    }

    public ArrayList<Object> getVacationsTasks() {
	return vacationsTasks;
    }

    public void setVacationsTasks(ArrayList<Object> vacationsTasks) {
	this.vacationsTasks = vacationsTasks;
    }

    public int getPageSize() {
	return pageSize;
    }

    public String getTaskUrlParam() {
	return taskUrlParam;
    }

    public boolean isSelectAll() {
	return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
	this.selectAll = selectAll;
    }

    public int getVacationsTasksListSize() {
	return vacationsTasksListSize;
    }

    public void setVacationsTasksListSize(int vacationsTasksListSize) {
	this.vacationsTasksListSize = vacationsTasksListSize;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

}