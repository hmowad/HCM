package com.code.ui.backings.hcm.terminations;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.terminations.TerminationRecordData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.workflow.hcm.TerminationsWorkflow;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "terminationPrerequisiteData")
@ViewScoped
public class TerminationPrerequisiteData extends BaseBacking {

    public int categoryMode;
    public Long empId;
    public EmployeeData employeeData;
    public Date terminationDueDate;

    public TerminationPrerequisiteData() {

	if (getRequest().getParameter("mode") != null)
	    categoryMode = Integer.parseInt(getRequest().getParameter("mode"));
	resetForm();
    }

    public void resetForm() {
	empId = null;
	employeeData = null;
	terminationDueDate = null;
    }

    public void searchEmpData() {
	try {
	    if (empId != null) {
		employeeData = EmployeesService.getEmployeeData(empId);

		if (employeeData.getServiceTerminationDate() != null)
		    throw new BusinessException("error_canNotAddTerminatedEmployee");
		
		if (employeeData.getLastExtensionEndDate() != null)
		    throw new BusinessException("error_canNotAddExtendedEmployee");

		List<TerminationRecordData> terminationRecords = TerminationsService.getTerminationRecordsDataByEmpId(employeeData.getEmpId());

		if (terminationRecords != null && terminationRecords.size() != 0)
		    throw new BusinessException("error_conflictTerminationsRecordEmployee");

		List<WFInstance> runningRequestsInstances = TerminationsWorkflow.getRunningRequests(new Long[] { employeeData.getEmpId() }, null);
		if (runningRequestsInstances != null && runningRequestsInstances.size() != 0)
		    throw new BusinessException("error_conflictTerminationsRequestEmployee");

		terminationDueDate = employeeData.getServiceTerminationDueDate();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    resetForm();
	}
    }

    public void saveTerminationEmployeeData() {
	try {
	    TerminationsService.updateTerminationEmployeesDates(employeeData, terminationDueDate, loginEmpData.getEmpId());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getCategoryMode() {
	return categoryMode;
    }

    public void setCategoryMode(int categoryMode) {
	this.categoryMode = categoryMode;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public EmployeeData getEmployeeData() {
	return employeeData;
    }

    public void setEmployeeData(EmployeeData employeeData) {
	this.employeeData = employeeData;
    }

    public long getEmployeesSearchRegionId() {
	return getLoginEmpPhysicalRegionFlag(true);
    }

    public Date getTerminationDueDate() {
	return terminationDueDate;
    }

    public void setTerminationDueDate(Date terminationDueDate) {
	this.terminationDueDate = terminationDueDate;
    }
}
