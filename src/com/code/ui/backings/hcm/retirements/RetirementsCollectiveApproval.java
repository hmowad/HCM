package com.code.ui.backings.hcm.retirements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.retirements.WFDisclaimerData;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.hcm.RetirementsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "retirementsCollectiveApproval")
@ViewScoped

public class RetirementsCollectiveApproval extends BaseBacking implements Serializable {

    // object[0] - WFTask
    // object[1] - WFInstance
    // object[2] - processName
    // object[3] - requester
    // object[4] - delegatingName
    private List<Object> retirementsTasks;

    private boolean selectAll;
    private int retirementsTasksListSize;

    private final static String taskUrlParam = "&taskId=";
    private final static int pageSize = 10;

    public RetirementsCollectiveApproval() {
	super();
	// TODO Auto-generated constructor stub
	this.setScreenTitle(getMessage("title_retirementsCollectiveApproval"));
	try {
	    // Load the Retirements tasks data
	    searchRetirementTasks();
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    // Load the Retirements tasks data
    private void searchRetirementTasks() {
	try {
	    selectAll = false;
	    retirementsTasks = (ArrayList<Object>) RetirementsWorkFlow.getWFRetirementsTasks(this.loginEmpData.getEmpId(), WFTaskRolesEnum.EXTRA_SIGN_MANAGER.getCode());
	    retirementsTasksListSize = retirementsTasks.size();
	} catch (BusinessException e) {
	    retirementsTasks = new ArrayList<Object>();
	    retirementsTasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // called from the xhtml on change of the selectAll checkbox
    public void selectUnselectAllRows() {
	for (int i = 0; i < retirementsTasks.size(); i++) {
	    ((WFTask) (((Object[]) retirementsTasks.get(i))[0])).setSelected(selectAll);
	}
    }

    // Loop over the selected tasks for approval
    // called from the xhtml when "Approve" button clicked
    public void doRetirementsCollectiveApprovalSM() {
	try {
	    String unsuccessfulTaskIdsIfAny = "";
	    String comma = "";
	    int unsuccessfulTasksCount = 0;
	    List<Long> selectedRetirementRequestsInstanceIds = new ArrayList<Long>();
	    Map<Long, WFDisclaimerData> wfDisclaimerDataMap = new HashMap<Long, WFDisclaimerData>();
	    List<Object> selectedRetirementsTasks = new ArrayList<Object>();

	    for (Object obj : retirementsTasks) {
		WFTask task = (WFTask) (((Object[]) obj)[0]);
		if (task.getSelected()) {
		    selectedRetirementRequestsInstanceIds.add(task.getInstanceId());
		    selectedRetirementsTasks.add(obj);
		}
	    }

	    Long[] selectedRetirementRequestsInstanceIdsArray = new Long[selectedRetirementRequestsInstanceIds.size()];
	    List<WFDisclaimerData> wfRetirementsData = RetirementsWorkFlow.getWFDisclaimerDataByInstanceIds(selectedRetirementRequestsInstanceIds.toArray(selectedRetirementRequestsInstanceIdsArray));

	    for (WFDisclaimerData wfRetirementData : wfRetirementsData) {
		wfDisclaimerDataMap.put(wfRetirementData.getInstanceId(), wfRetirementData);
	    }
	    for (Object obj : selectedRetirementsTasks) {
		WFTask task = null;
		try {
		    task = (WFTask) (((Object[]) obj)[0]);
		    WFInstance instance = (WFInstance) (((Object[]) obj)[1]);
		    EmployeeData requester = (EmployeeData) (((Object[]) obj)[3]);
		    WFDisclaimerData wfDisclaimerData = wfDisclaimerDataMap.get(task.getInstanceId());
		    if (wfDisclaimerData == null)
			wfDisclaimerData = new WFDisclaimerData();
		    RetirementsWorkFlow.doESM(requester, instance, wfDisclaimerData, task);
		} catch (BusinessException e) {
		    unsuccessfulTaskIdsIfAny += comma + task.getTaskId();
		    unsuccessfulTasksCount++;
		    comma = ", ";
		}
	    }

	    if (unsuccessfulTasksCount > 0)
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfTasks", new Object[] { unsuccessfulTasksCount, unsuccessfulTaskIdsIfAny }));
	    else
		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	    // Call the search method here to Reload the tasks
	    searchRetirementTasks();

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public List<Object> getRetirementsTasks() {
	return retirementsTasks;
    }

    public void setRetirementsTasks(List<Object> retirementsTasks) {
	this.retirementsTasks = retirementsTasks;
    }

    public boolean isSelectAll() {
	return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
	this.selectAll = selectAll;
    }

    public int getRetirementsTasksListSize() {
	return retirementsTasksListSize;
    }

    public void setRetirementsTasksListSize(int retirementsTasksListSize) {
	this.retirementsTasksListSize = retirementsTasksListSize;
    }

    public String getTaskUrlParam() {
	return taskUrlParam;
    }

    public int getPageSize() {
	return pageSize;
    }

}
