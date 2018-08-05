package com.code.ui.backings.worklist;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFDelegationData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "wfDelegationToManagement")
@ViewScoped
public class WFDelegationToManagement extends BaseBacking implements Serializable {

    private List<WFDelegationData> totalDelegationList;
    private List<WFDelegationData> partialDelegationList;
    private EmployeeData curEmployee;
    private int rowsCount = 10;

    public WFDelegationToManagement() {
	super.init();
	super.setScreenTitle(getMessage("title_wfDelegationToManagement"));
	try {
	    curEmployee = EmployeesService.getEmployeeData(this.loginEmpData.getEmpId());
	    totalDelegationList = BaseWorkFlow.getWFTotalDelegationTo(curEmployee.getEmpId());
	    partialDelegationList = BaseWorkFlow.getWFPartialDelegationTo(curEmployee.getEmpId());
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<WFDelegationData> getTotalDelegationList() {
	return totalDelegationList;
    }

    public void setTotalDelegationList(List<WFDelegationData> totalDelegationList) {
	this.totalDelegationList = totalDelegationList;
    }

    public List<WFDelegationData> getPartialDelegationList() {
	return partialDelegationList;
    }

    public void setPartialDelegationList(List<WFDelegationData> partialDelegationList) {
	this.partialDelegationList = partialDelegationList;
    }

    public EmployeeData getCurEmployee() {
	return curEmployee;
    }

    public void setCurEmployee(EmployeeData curEmployee) {
	this.curEmployee = curEmployee;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }
}
