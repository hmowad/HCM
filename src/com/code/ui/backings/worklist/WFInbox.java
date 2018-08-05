package com.code.ui.backings.worklist;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.dal.orm.workflow.WFProcess;
import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "wfInbox")
@SessionScoped
public class WFInbox extends BaseBacking {
    private boolean admin;
    private List<EmployeeMenuAction> employeeMenuActions;
    private long searchByAssigneeId;
    private String searchByAssigneeName;
    private Long searchByBeneficiaryId;
    private String searchByBeneficiaryName;

    private List<WFProcessGroup> processesGroups;
    private long selectedProcessGroupId;
    private List<WFProcess> processes;
    private long selectedProcessId;
    private int selectedTaskRole;

    private String searchByTaskOwnerName;
    private boolean isDESC;
    private boolean runningFlag;

    private Integer selectedGridIndex;
    private Long searchEmpId;

    private List<WFTaskData> tasks;
    private int pageSize = 10;

    private boolean selectAll;
    private int tasksListSize;

    public WFInbox() {
	super();
	try {
	    processesGroups = BaseWorkFlow.getWFProcessesGroups();
	    employeeMenuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.WF_INBOX.getCode());
	    if (employeeMenuActions != null && !employeeMenuActions.isEmpty())
		admin = true;

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void init() {
	super.init();

	searchByAssigneeId = this.loginEmpData.getEmpId();
	searchByAssigneeName = this.loginEmpData.getName();
	searchByBeneficiaryId = null;
	searchByBeneficiaryName = "";

	selectedProcessGroupId = 0;
	selectedTaskRole = FlagsEnum.ALL.getCode();
	if (getRequest().getParameter("role") != null) {
	    selectedTaskRole = Integer.parseInt(getRequest().getParameter("role"));
	}

	searchByTaskOwnerName = "";
	isDESC = false;
	runningFlag = true;

	searchProcesses(null);

	if (getRequest().getParameter("init") == null)
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    public void searchProcesses(AjaxBehaviorEvent event) {
	try {
	    processes = BaseWorkFlow.getWFGroupProcesses(selectedProcessGroupId);
	    selectedProcessId = 0;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

	searchTasks();
    }

    public void searchTasks() {
	try {
	    if (!this.loginEmpData.getEmpId().equals(searchByAssigneeId) && !isEmployeeAuthorizedViewCategory()) {
		EmployeeData assigneeEmployee = EmployeesService.getEmployeeData(searchByAssigneeId);

		if (FlagsEnum.ON.getCode() != this.loginEmpData.getIsManager()
			|| !assigneeEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.loginEmpData.getPhysicalUnitHKey()))) // TODO handle null?
		    throw new BusinessException("error_notAuthorized");
	    }

	    tasks = BaseWorkFlow.searchWFTasksData(searchByAssigneeId, searchByBeneficiaryId, searchByTaskOwnerName, selectedProcessGroupId, selectedProcessId, runningFlag, selectedTaskRole, isDESC);
	    tasksListSize = tasks.size();
	} catch (Exception e) {
	    tasks = new ArrayList<WFTaskData>();
	    tasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	}
	selectAll = false;
    }

    private boolean isEmployeeAuthorizedViewCategory() throws BusinessException {
	EmployeeData assigneeEmployee = EmployeesService.getEmployeeData(searchByAssigneeId);
	long categoryId = assigneeEmployee.getCategoryId().longValue();
	long physicalRegionId = assigneeEmployee.getPhysicalRegionId() == null ? FlagsEnum.ALL.getCode() : assigneeEmployee.getPhysicalRegionId();
	String physicalUnitHkey = assigneeEmployee.getPhysicalUnitHKey();

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
	searchTasks();
    }

    public void searchByTaskRole(AjaxBehaviorEvent event) {
	searchTasks();
    }

    public void sortASC() {
	isDESC = false;
	searchTasks();
    }

    public void sortDESC() {
	isDESC = true;
	searchTasks();
    }

    public void getRunningTasks() {
	runningFlag = true;
	isDESC = false;
	searchTasks();
    }

    public void getDoneCompletedTasks() {
	runningFlag = false;
	isDESC = true;
	searchTasks();
    }

    public void delegateTaskEmployee() {
	if (searchEmpId != null && searchEmpId != 0 && selectedGridIndex != null && !tasks.isEmpty()) {
	    try {
		List<Long> tasksIds = new ArrayList<Long>();
		tasksIds.add(tasks.get(selectedGridIndex).getTaskId());
		BaseWorkFlow.delegateWFTasks(tasksIds, searchByAssigneeId, searchEmpId, loginEmpData.getEmpId());
		searchTasks();
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	    searchEmpId = null;
	    selectedGridIndex = null;
	}
    }

    // -----------------------------------------------------------------------------------------------------------------

    // called from the xhtml on change of the selectAll checkbox
    public void selectUnselectAllRows() {
	for (int i = 0; i < tasks.size(); i++) {
	    tasks.get(i).setSelected(selectAll);
	}
    }

    public void closeProcesses() {
	try {
	    List<WFTaskData> selectedTasks = new ArrayList<WFTaskData>();
	    List<Long> selectedTasksIds = new ArrayList<Long>();
	    List<Long> selectedInstancesIds = new ArrayList<Long>();

	    for (WFTaskData task : tasks) {
		if (task.getSelected()) {
		    selectedTasks.add(task);
		    selectedTasksIds.add(task.getTaskId());
		    selectedInstancesIds.add(task.getInstanceId());
		}
	    }

	    BaseWorkFlow.closeWFInstancesByNotification(BaseWorkFlow.getWFInstancesByIds(selectedInstancesIds), BaseWorkFlow.getWFTasksByIds(selectedTasksIds));

	    for (WFTaskData task : selectedTasks) {
		tasks.remove(task);
	    }
	    tasksListSize -= selectedTasks.size();
	    selectAll = false;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void setTasks(List<WFTaskData> tasks) {
	this.tasks = tasks;
    }

    public List<WFTaskData> getTasks() {
	return tasks;
    }

    public void setSearchByTaskOwnerName(String searchByTaskOwnerName) {
	this.searchByTaskOwnerName = searchByTaskOwnerName;
    }

    public String getSearchByTaskOwnerName() {
	return searchByTaskOwnerName;
    }

    public void setSearchByAssigneeId(long searchByAssigneeId) {
	this.searchByAssigneeId = searchByAssigneeId;
    }

    public long getSearchByAssigneeId() {
	return searchByAssigneeId;
    }

    public void setSearchByAssigneeName(String searchByAssigneeName) {
	this.searchByAssigneeName = searchByAssigneeName;
    }

    public String getSearchByAssigneeName() {
	return searchByAssigneeName;
    }

    public Long getSearchByBeneficiaryId() {
	return searchByBeneficiaryId;
    }

    public void setSearchByBeneficiaryId(Long searchByBeneficiaryId) {
	this.searchByBeneficiaryId = searchByBeneficiaryId;
    }

    public String getSearchByBeneficiaryName() {
	return searchByBeneficiaryName;
    }

    public void setSearchByBeneficiaryName(String searchByBeneficiaryName) {
	this.searchByBeneficiaryName = searchByBeneficiaryName;
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

    public void setSearchEmpId(Long searchEmpId) {
	this.searchEmpId = searchEmpId;
    }

    public Long getSearchEmpId() {
	return searchEmpId;
    }

    public void setSelectedGridIndex(Integer selectedGridIndex) {
	this.selectedGridIndex = selectedGridIndex;
    }

    public Integer getSelectedGridIndex() {
	return selectedGridIndex;
    }

    public int getSelectedTaskRole() {
	return selectedTaskRole;
    }

    public void setSelectedTaskRole(int selectedTaskRole) {
	this.selectedTaskRole = selectedTaskRole;
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

    public boolean isSelectAll() {
	return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
	this.selectAll = selectAll;
    }

    public int getTasksListSize() {
	return tasksListSize;
    }

    public void setTasksListSize(int tasksListSize) {
	this.tasksListSize = tasksListSize;
    }
}
