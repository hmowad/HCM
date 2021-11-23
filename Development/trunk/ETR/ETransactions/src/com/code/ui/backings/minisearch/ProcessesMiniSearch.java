package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.workflow.WFProcess;
import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.EServicesBaseWorkFlowService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "processesMiniSearch")
@ViewScoped
public class ProcessesMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 5;
    private long searchProcessGroupId;
    private String searchProcessName;
    private int mode; // 1:multi selection; 2:single selection

    private List<WFProcessGroup> processesGroups;
    private List<WFProcess> searchProcessList;
    private List<WFProcess> selectedProcessList = new ArrayList<WFProcess>();
    private String totalProcessesIDs;
    private String totalProcessesNames;

    public ProcessesMiniSearch() {
	searchProcessGroupId = 0;
	searchProcessName = "";
	mode = Integer.parseInt(getRequest().getParameter("mode"));
	try {
	    if (eservicesFlag == FlagsEnum.OFF.getCode()) {
		processesGroups = BaseWorkFlow.getWFProcessesGroups();
	    } else if (eservicesFlag == FlagsEnum.ON.getCode()) {
		processesGroups = EServicesBaseWorkFlowService.getEservicesProcessGroups();
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
	searchProcess();
    }

    public void searchProcess() {
	try {
	    super.init();
	    if (eservicesFlag == FlagsEnum.OFF.getCode()) {
		searchProcessList = null;
		if (selectedProcessList.size() > 0) {
		    Long[] processesIds = new Long[selectedProcessList.size()];
		    for (int i = 0; i < selectedProcessList.size(); i++) {
			processesIds[i] = selectedProcessList.get(i).getProcessId();
		    }
		    searchProcessList = BaseWorkFlow.getWFGroupProcesses(searchProcessGroupId, searchProcessName, processesIds);
		} else
		    searchProcessList = BaseWorkFlow.getWFGroupProcesses(searchProcessGroupId, searchProcessName, null);
	    } else if (eservicesFlag == FlagsEnum.ON.getCode()) {
		searchProcessList = EServicesBaseWorkFlowService.getEservicesProcesses(searchProcessGroupId, searchProcessName);
	    }

	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addSelectedProcess(WFProcess process) {
	selectedProcessList.add(process);
	searchProcessList.remove(process);
    }

    public void addAllSearchList() {
	for (WFProcess searchProcess : searchProcessList) {
	    selectedProcessList.add(searchProcess);
	}
	searchProcessList = new ArrayList<>();
    }

    public void removeSelectedProcess(WFProcess process) {
	selectedProcessList.remove(process);
	searchProcessList.add(process);
    }

    public void generateProcessesInfo() {
	totalProcessesIDs = "";
	totalProcessesNames = "";
	String comma = "";
	if (selectedProcessList.size() > 0) {
	    for (WFProcess process : selectedProcessList) {
		totalProcessesIDs += comma + process.getProcessId();
		totalProcessesNames += comma + process.getProcessName();
		comma = ",";
	    }
	}
	if (totalProcessesIDs.length() == 0) {
	    super.setServerSideErrorMessages(getMessage("error_processesListMandatory"));
	    totalProcessesIDs = "";
	    totalProcessesNames = "";
	}
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public String getSearchProcessName() {
	return searchProcessName;
    }

    public void setSearchProcessName(String searchProcessName) {
	this.searchProcessName = searchProcessName;
    }

    public List<WFProcessGroup> getProcessesGroups() {
	return processesGroups;
    }

    public void setProcessesGroups(List<WFProcessGroup> processesGroups) {
	this.processesGroups = processesGroups;
    }

    public long getSearchProcessGroupId() {
	return searchProcessGroupId;
    }

    public void setSearchProcessGroupId(long searchProcessGroupId) {
	this.searchProcessGroupId = searchProcessGroupId;
    }

    public List<WFProcess> getSearchProcessList() {
	return searchProcessList;
    }

    public void setSearchProcessList(List<WFProcess> searchProcessList) {
	this.searchProcessList = searchProcessList;
    }

    public List<WFProcess> getSelectedProcessList() {
	return selectedProcessList;
    }

    public void setSelectedProcessList(List<WFProcess> selectedProcessList) {
	this.selectedProcessList = selectedProcessList;
    }

    public String getTotalProcessesIDs() {
	return totalProcessesIDs;
    }

    public void setTotalProcessesIDs(String totalProcessesIDs) {
	this.totalProcessesIDs = totalProcessesIDs;
    }

    public String getTotalProcessesNames() {
	return totalProcessesNames;
    }

    public void setTotalProcessesNames(String totalProcessesNames) {
	this.totalProcessesNames = totalProcessesNames;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }
}