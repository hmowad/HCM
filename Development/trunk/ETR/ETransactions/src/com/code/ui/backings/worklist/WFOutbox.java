package com.code.ui.backings.worklist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.dal.orm.workflow.WFInstanceData;
import com.code.dal.orm.workflow.WFProcess;
import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.SessionAttributesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "wfOutbox")
@SessionScoped
public class WFOutbox extends BaseBacking {
    private boolean admin;
    private List<EmployeeMenuAction> employeeMenuActions;
    private Long searchByRequesterId;
    private String searchByRequesterName;
    private Long searchByBeneficiaryId;
    private String searchByBeneficiaryName;

    private List<WFProcessGroup> processesGroups;
    private long selectedProcessGroupId;
    private List<WFProcess> processes;
    private long selectedProcessId;
    private boolean isASC;
    private boolean runningFlag;

    private List<WFInstanceData> instances;
    private int pageSize = 10;

    public WFOutbox() {
	super();
	try {
	    processesGroups = BaseWorkFlow.getWFProcessesGroups();
	    employeeMenuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.WF_OUTBOX.getCode());
	    if (employeeMenuActions != null && !employeeMenuActions.isEmpty())
		admin = true;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void init() {
	super.init();

	searchByRequesterId = this.loginEmpData.getEmpId();
	searchByRequesterName = this.loginEmpData.getName();
	searchByBeneficiaryId = null;
	searchByBeneficiaryName = "";
	selectedProcessGroupId = 0;
	isASC = false;
	runningFlag = true;

	searchProcesses(null);

	if (getRequest().getParameter("init") == null)
	    this.setServerSideSuccessMessages(getMessage("notify_requestSent"));
    }

    public void searchProcesses(AjaxBehaviorEvent event) {
	try {
	    processes = BaseWorkFlow.getWFGroupProcesses(selectedProcessGroupId);
	    selectedProcessId = 0;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

	searchInstances();
    }

    public void searchInstances() {
	try {
	    if (searchByRequesterId == null && searchByBeneficiaryId == null)
		throw new BusinessException("error_shouldSelectRequesterOrBeneficiary");

	    if (!this.loginEmpData.getEmpId().equals(searchByRequesterId) && !this.loginEmpData.getEmpId().equals(searchByBeneficiaryId) && !isEmployeeAuthorizedViewCategory()) {
		if (searchByRequesterId != null) {
		    EmployeeData requesterEmployee = EmployeesService.getEmployeeData(searchByRequesterId);

		    if (FlagsEnum.ON.getCode() != this.loginEmpData.getIsManager()
			    || !requesterEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.loginEmpData.getPhysicalUnitHKey())))
			throw new BusinessException("error_notAuthorized");
		} else {
		    EmployeeData beneficiaryEmployee = EmployeesService.getEmployeeData(searchByBeneficiaryId);

		    if (FlagsEnum.ON.getCode() != this.loginEmpData.getIsManager()
			    || !beneficiaryEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.loginEmpData.getPhysicalUnitHKey())))
			throw new BusinessException("error_notAuthorized");
		}
	    }

	    instances = BaseWorkFlow.searchWFInstancesData(searchByRequesterId, searchByBeneficiaryId, selectedProcessGroupId, selectedProcessId, runningFlag, isASC);
	} catch (Exception e) {
	    instances = new ArrayList<WFInstanceData>();
	    this.setServerSideErrorMessages(getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	}
    }

    private boolean isEmployeeAuthorizedViewCategory() throws BusinessException {
	EmployeeData selectedEmployee = EmployeesService.getEmployeeData(searchByRequesterId == null ? searchByBeneficiaryId : searchByRequesterId);
	long categoryId = selectedEmployee.getCategoryId().longValue();
	long physicalRegionId = selectedEmployee.getPhysicalRegionId() == null ? FlagsEnum.ALL.getCode() : selectedEmployee.getPhysicalRegionId();
	String physicalUnitHkey = selectedEmployee.getPhysicalUnitHKey();

	if (CategoriesEnum.OFFICERS.getCode() == categoryId
		&& SecurityService.isEmployeeMenuActionGranted(physicalRegionId, physicalUnitHkey, MenuActionsEnum.WF_INBOX_OUTBOX_ADMIN_OFFICERS.getCode(), employeeMenuActions))
	    return true;

	else if (CategoriesEnum.SOLDIERS.getCode() == categoryId
		&& SecurityService.isEmployeeMenuActionGranted(physicalRegionId, physicalUnitHkey, MenuActionsEnum.WF_INBOX_OUTBOX_ADMIN_SOLDIERS.getCode(), employeeMenuActions))
	    return true;

	else if ((CategoriesEnum.PERSONS.getCode() == categoryId || CategoriesEnum.USERS.getCode() == categoryId
		|| CategoriesEnum.WAGES.getCode() == categoryId || CategoriesEnum.CONTRACTORS.getCode() == categoryId
		|| CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId)
		&& SecurityService.isEmployeeMenuActionGranted(physicalRegionId, physicalUnitHkey, MenuActionsEnum.WF_INBOX_OUTBOX_ADMIN_CIVILIANS.getCode(), employeeMenuActions))
	    return true;

	return false;
    }

    public void searchByProcess(AjaxBehaviorEvent event) {
	searchInstances();
    }

    public void sortASC() {
	isASC = true;
	searchInstances();
    }

    public void sortDESC() {
	isASC = false;
	searchInstances();
    }

    public void getRunningInstances() {
	runningFlag = true;
	searchInstances();
    }

    public void getDoneCompletedInstances() {
	runningFlag = false;
	searchInstances();
    }

    @SuppressWarnings("unchecked")
    public void addInstanceIdToSession(Long instanceId) {
	HashSet<Long> instancesIds = (HashSet<Long>) getSession().getAttribute(SessionAttributesEnum.ACCESSED_WF_INSTANCES_IDS.getCode());
	if (instancesIds == null) {
	    instancesIds = new HashSet<Long>();
	    getSession().setAttribute(SessionAttributesEnum.ACCESSED_WF_INSTANCES_IDS.getCode(), instancesIds);
	}
	instancesIds.add(instanceId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void setInstances(List<WFInstanceData> instances) {
	this.instances = instances;
    }

    public List<WFInstanceData> getInstances() {
	return instances;
    }

    public void setSearchByRequesterId(Long searchByRequesterId) {
	this.searchByRequesterId = searchByRequesterId;
    }

    public Long getSearchByRequesterId() {
	return searchByRequesterId;
    }

    public Long getSearchByBeneficiaryId() {
	return searchByBeneficiaryId;
    }

    public void setSearchByBeneficiaryId(Long searchByBeneficiaryId) {
	this.searchByBeneficiaryId = searchByBeneficiaryId;
    }

    public void setProcessesGroups(List<WFProcessGroup> processesGroups) {
	this.processesGroups = processesGroups;
    }

    public List<WFProcessGroup> getProcessesGroups() {
	return processesGroups;
    }

    public void setSelectedProcessGroupId(long selectedProcessGroupId) {
	this.selectedProcessGroupId = selectedProcessGroupId;
    }

    public long getSelectedProcessGroupId() {
	return selectedProcessGroupId;
    }

    public void setProcesses(List<WFProcess> processes) {
	this.processes = processes;
    }

    public List<WFProcess> getProcesses() {
	return processes;
    }

    public void setSelectedProcessId(long selectedProcessId) {
	this.selectedProcessId = selectedProcessId;
    }

    public long getSelectedProcessId() {
	return selectedProcessId;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getPageSize() {
	return pageSize;
    }

    public String getSearchByRequesterName() {
	return searchByRequesterName;
    }

    public void setSearchByRequesterName(String searchByRequesterName) {
	this.searchByRequesterName = searchByRequesterName;
    }

    public String getSearchByBeneficiaryName() {
	return searchByBeneficiaryName;
    }

    public void setSearchByBeneficiaryName(String searchByBeneficiaryName) {
	this.searchByBeneficiaryName = searchByBeneficiaryName;
    }

    public boolean isRunningFlag() {
	return runningFlag;
    }

    public void setRunningFlag(boolean runningFlag) {
	this.runningFlag = runningFlag;
    }

    public boolean isAdmin() {
	return admin;
    }
}