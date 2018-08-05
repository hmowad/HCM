package com.code.ui.backings.hcm.units;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitTransactionData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.AttachmentsService;
import com.code.ui.util.UnitTreeNode;

@SuppressWarnings("serial")
@ManagedBean(name = "organizationHierarchy")
@SessionScoped
public class OrganizationHierarchy extends UnitTreeBase implements Serializable {
    private List<JobData> jobDataList;
    private List<EmployeeData> physicalEmployeeDataList;
    private List<EmployeeData> officialEmployeeDataList;
    private List<UnitTransactionData> unitTransactionDataList;

    private int pageSize = 10;

    public OrganizationHierarchy() {
	super();
    }

    public void init() {
	super.init();
	loadUnitListData();
    }

    public void click(UnitTreeNode unitItem) {
	super.click(unitItem);
	loadUnitListData();
    }

    public void search() {
	super.search();
	if (selectedUnitData != null && selectedUnitData.getId() != null) {
	    loadUnitListData();
	} else {
	    jobDataList = new ArrayList<JobData>();
	    physicalEmployeeDataList = new ArrayList<EmployeeData>();
	    officialEmployeeDataList = new ArrayList<EmployeeData>();
	    unitTransactionDataList = new ArrayList<UnitTransactionData>();
	}
    }

    private void loadUnitListData() {
	try {
	    physicalEmployeeDataList = EmployeesService.getEmpDataByPhysicalUnitId(selectedUnitData.getId());
	    officialEmployeeDataList = EmployeesService.getEmpDataByOfficialUnitId(selectedUnitData.getId());
	    unitTransactionDataList = UnitsService.getUnitTransactionsByUnitId(selectedUnitData.getId());
	    jobDataList = JobsService.getVacantAndFozenJobsByUnitId(selectedUnitData.getId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void addUnitAttachmentsId() {
	try {
	    selectedUnitData.setAttachments(AttachmentsService.getNextAttachmentsId());
	    UnitsService.modifyUnitNonTransactionalData(selectedUnitData);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void update() {
	try {
	    selectedUnitData.getUnit().setSystemUser(this.loginEmpData.getEmpId() + ""); // for Audit
	    UnitsService.modifyUnitNonTransactionalData(selectedUnitData);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<JobData> getJobDataList() {
	return jobDataList;
    }

    public void setJobDataList(List<JobData> jobDataList) {
	this.jobDataList = jobDataList;
    }

    public List<EmployeeData> getPhysicalEmployeeDataList() {
	return physicalEmployeeDataList;
    }

    public void setPhysicalEmployeeDataList(List<EmployeeData> physicalEmployeeDataList) {
	this.physicalEmployeeDataList = physicalEmployeeDataList;
    }

    public List<EmployeeData> getOfficialEmployeeDataList() {
	return officialEmployeeDataList;
    }

    public void setOfficialEmployeeDataList(List<EmployeeData> officialEmployeeDataList) {
	this.officialEmployeeDataList = officialEmployeeDataList;
    }

    public List<UnitTransactionData> getUnitTransactionDataList() {
	return unitTransactionDataList;
    }

    public void setUnitTransactionDataList(List<UnitTransactionData> unitTransactionDataList) {
	this.unitTransactionDataList = unitTransactionDataList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
}
