package com.code.ui.backings.worklist;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.workflow.WFDelegationData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "wfDelegationFromManagement")
@ViewScoped
public class WFDelegationFromManagement extends BaseBacking implements Serializable {

    private List<WFDelegationData> totalDelegationList;
    private List<WFDelegationData> partialDelegationList;

    private EmployeeData curEmployee;

    private Long searchEmpId;
    private EmployeeData searchEmployee;

    private UnitData searchUnit;

    private String selectedProcessesIds;
    private String selectedProcessesNames;

    private int rowsCount = 10;

    public WFDelegationFromManagement() {
	super.init();
	super.setScreenTitle(getMessage("title_wfDelegationFromManagement"));
	try {
	    searchUnit = new UnitData();
	    curEmployee = EmployeesService.getEmployeeData(this.loginEmpData.getEmpId());

	    totalDelegationList = BaseWorkFlow.getWFTotalDelegationsFrom(curEmployee.getEmpId());
	    partialDelegationList = BaseWorkFlow.getWFPartialDelegationsFrom(curEmployee.getEmpId());

	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchEmployee() {
	try {
	    searchEmployee = null;
	    searchEmployee = EmployeesService.getEmployeeData(searchEmpId);
	    if (searchEmployee == null)
		throw new BusinessException("error_noEmpFound");
	    if (!curEmployee.getPhysicalRegionId().equals(searchEmployee.getPhysicalRegionId()))
		this.setServerSideErrorMessages(getMessage("error_notAuthorized"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void saveTotalDelegation() {
	if (searchEmployee != null) {
	    try {
		BaseWorkFlow.saveTotalWFDelegation(curEmployee.getEmpId(), searchEmployee.getEmpId(), searchUnit.getId(), searchUnit.gethKey(), totalDelegationList, partialDelegationList);
		totalDelegationList = BaseWorkFlow.getWFTotalDelegationsFrom(curEmployee.getEmpId());
		super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    } catch (Exception e) {
		super.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	}
    }

    public void savePartialDelegation() {
	if (searchEmployee != null && selectedProcessesIds != null) {
	    try {

		String[] processesIDs = selectedProcessesIds.split(",");
		String[] processesNames = selectedProcessesNames.split(",");
		if (processesIDs != null && processesIDs.length > 0) {
		    String unsuccessfulProcessesIfAny = "";
		    int unsuccessfulProcessesCount = 0;
		    String comma = "";
		    for (int i = 0; i < processesIDs.length; i++) {
			try {
			    BaseWorkFlow.saveWFDelegation(curEmployee.getEmpId(), searchEmployee.getEmpId(), Long.parseLong(processesIDs[i]), searchUnit.getId(), searchUnit.gethKey(), totalDelegationList, partialDelegationList);
			} catch (BusinessException e) {
			    unsuccessfulProcessesIfAny += comma + getMessage(e.getMessage()) + " - " + processesNames[i];
			    unsuccessfulProcessesCount++;
			    comma = ", \n ";
			}
		    }

		    if (unsuccessfulProcessesCount > 0)
			this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfProcesses", new Object[] { unsuccessfulProcessesCount, unsuccessfulProcessesIfAny }));
		    else
			this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

		    partialDelegationList = BaseWorkFlow.getWFPartialDelegationsFrom(curEmployee.getEmpId());
		}

	    } catch (Exception e) {
		super.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	}
    }

    public void cancelTotalDelegation(int selectedGridIndex) {
	try {
	    WFDelegationData delegation = totalDelegationList.get(selectedGridIndex);
	    BaseWorkFlow.deleteWFDelegation(delegation.getId(), curEmployee.getEmpId());
	    totalDelegationList.remove(delegation);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void cancelPartialDelegation(int selectedGridIndex) {
	try {
	    WFDelegationData delegation = partialDelegationList.get(selectedGridIndex);
	    BaseWorkFlow.deleteWFDelegation(delegation.getId(), curEmployee.getEmpId());
	    partialDelegationList.remove(delegation);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public EmployeeData getCurEmployee() {
	return curEmployee;
    }

    public void setCurEmployee(EmployeeData curEmployee) {
	this.curEmployee = curEmployee;
    }

    public String getSelectedProcessesIds() {
	return selectedProcessesIds;
    }

    public void setSelectedProcessesIds(String selectedProcessesIds) {
	this.selectedProcessesIds = selectedProcessesIds;
    }

    public String getSelectedProcessesNames() {
	return selectedProcessesNames;
    }

    public void setSelectedProcessesNames(String selectedProcessesNames) {
	this.selectedProcessesNames = selectedProcessesNames;
    }

    public Long getSearchEmpId() {
	return searchEmpId;
    }

    public void setSearchEmpId(Long searchEmpId) {
	this.searchEmpId = searchEmpId;
    }

    public EmployeeData getSearchEmployee() {
	return searchEmployee;
    }

    public void setSearchEmployee(EmployeeData searchEmployee) {
	this.searchEmployee = searchEmployee;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public List<WFDelegationData> getPartialDelegationList() {
	return partialDelegationList;
    }

    public void setPartialDelegationList(List<WFDelegationData> partialDelegationList) {
	this.partialDelegationList = partialDelegationList;
    }

    public List<WFDelegationData> getTotalDelegationList() {
	return totalDelegationList;
    }

    public void setTotalDelegationList(List<WFDelegationData> totalDelegationList) {
	this.totalDelegationList = totalDelegationList;
    }

    public UnitData getSearchUnit() {
	return searchUnit;
    }

    public void setSearchUnit(UnitData searchUnit) {
	this.searchUnit = searchUnit;
    }
}
