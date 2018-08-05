package com.code.ui.backings.worklist;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.workflow.WFProcess;
import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.BaseWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "wfTasksDelegation")
@ViewScoped
public class WFTasksDelegation extends BaseBacking {

    private List<WFTaskData> tasks;
    private int tasksListSize;

    private long searchByDelegatorId;
    private String searchByDelegatorName;
    private long searchByDelegatorRegionId;

    private List<WFProcessGroup> processesGroups;
    private long selectedProcessGroupId;

    private List<WFProcess> processes;
    private long selectedProcessId;

    private long delegateeId;
    private String delegateeName;

    private boolean selectAll;
    private int pageSize = 10;

    public WFTasksDelegation() {
	super();
	try {
	    processesGroups = BaseWorkFlow.getWFProcessesGroups();

	    searchByDelegatorId = this.loginEmpData.getEmpId();
	    searchByDelegatorName = this.loginEmpData.getName();
	    searchByDelegatorRegionId = this.loginEmpData.getPhysicalRegionId();

	    selectedProcessGroupId = FlagsEnum.OFF.getCode();

	    delegateeId = FlagsEnum.ALL.getCode();
	    delegateeName = "";

	    searchProcesses(null);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchProcesses(AjaxBehaviorEvent event) {
	try {
	    processes = BaseWorkFlow.getWFGroupProcesses(selectedProcessGroupId);
	    selectedProcessId = FlagsEnum.OFF.getCode();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

	searchTasks();
    }

    public void searchByDelegatorId() {
	searchTasks();
    }

    public void searchByProcess(AjaxBehaviorEvent event) {
	searchTasks();
    }

    private void searchTasks() {
	selectAll = false;
	try {
	    tasks = BaseWorkFlow.searchWFTasksData(searchByDelegatorId, null, "", selectedProcessGroupId, selectedProcessId, true, FlagsEnum.ON.getCode(), false);
	    tasksListSize = tasks.size();
	} catch (BusinessException e) {
	    tasks = new ArrayList<WFTaskData>();
	    tasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // called from the xhtml on change of the selectAll checkbox
    public void selectUnselectAllRows() {
	for (WFTaskData task : tasks) {
	    task.setSelected(selectAll);
	}
    }

    public void delegateTasks() {
	try {
	    List<Long> selectedTasksIds = new ArrayList<Long>();
	    for (WFTaskData task : tasks) {
		if (task.getSelected())
		    selectedTasksIds.add(task.getTaskId());
	    }
	    BaseWorkFlow.delegateWFTasks(selectedTasksIds, searchByDelegatorId, delegateeId, loginEmpData.getEmpId());
	    searchTasks();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // -----------------------------------------------------------------------------------------------------------------

    public List<WFTaskData> getTasks() {
	return tasks;
    }

    public void setTasks(List<WFTaskData> tasks) {
	this.tasks = tasks;
    }

    public int getTasksListSize() {
	return tasksListSize;
    }

    public void setTasksListSize(int tasksListSize) {
	this.tasksListSize = tasksListSize;
    }

    public long getSearchByDelegatorId() {
	return searchByDelegatorId;
    }

    public void setSearchByDelegatorId(long searchByDelegatorId) {
	this.searchByDelegatorId = searchByDelegatorId;
    }

    public String getSearchByDelegatorName() {
	return searchByDelegatorName;
    }

    public void setSearchByDelegatorName(String searchByDelegatorName) {
	this.searchByDelegatorName = searchByDelegatorName;
    }

    public long getSearchByDelegatorRegionId() {
	return searchByDelegatorRegionId;
    }

    public void setSearchByDelegatorRegionId(long searchByDelegatorRegionId) {
	this.searchByDelegatorRegionId = searchByDelegatorRegionId;
    }

    public List<WFProcessGroup> getProcessesGroups() {
	return processesGroups;
    }

    public long getSelectedProcessGroupId() {
	return selectedProcessGroupId;
    }

    public void setSelectedProcessGroupId(long selectedProcessGroupId) {
	this.selectedProcessGroupId = selectedProcessGroupId;
    }

    public List<WFProcess> getProcesses() {
	return processes;
    }

    public long getSelectedProcessId() {
	return selectedProcessId;
    }

    public void setSelectedProcessId(long selectedProcessId) {
	this.selectedProcessId = selectedProcessId;
    }

    public long getDelegateeId() {
	return delegateeId;
    }

    public void setDelegateeId(long delegateeId) {
	this.delegateeId = delegateeId;
    }

    public String getDelegateeName() {
	return delegateeName;
    }

    public void setDelegateeName(String delegateeName) {
	this.delegateeName = delegateeName;
    }

    public boolean isSelectAll() {
	return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
	this.selectAll = selectAll;
    }

    public int getPageSize() {
	return pageSize;
    }

}
