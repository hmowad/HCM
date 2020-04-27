package com.code.ui.backings.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.security.DecisionPrivilegeData;
import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "decisionsPrivileges")
@ViewScoped
public class DecisionsPrivileges extends BaseBacking implements Serializable {

    private List<Category> categories;
    private List<WFProcessGroup> wfProcessGroups;
    private List<DecisionPrivilegeData> decisionsPrivilegesData;
    private List<Region> regions;

    private Long selectedCategoryId;
    private Long selectedWFProcessGroupId;

    private Long privilegeOwnerType; // 1 Employee, 2 Job, 3 Unit
    private Long privilegeType; // 1 Full privilege, 2 Region privilege, 3 Unit privilege

    private DecisionPrivilegeData selectedDecisionPrivilege;

    private int pageSize = 10;

    public DecisionsPrivileges() {
	setScreenTitle(getMessage("title_decisionsPrivileges"));

	categories = new ArrayList<Category>();
	categories.add(CommonService.getCategoryById(CategoriesEnum.OFFICERS.getCode()));
	categories.add(CommonService.getCategoryById(CategoriesEnum.SOLDIERS.getCode()));
	categories.add(CommonService.getCategoryById(CategoriesEnum.PERSONS.getCode()));

	privilegeOwnerType = 1L;
	privilegeType = 1L;

	reset();

	try {
	    wfProcessGroups = BaseWorkFlow.getWFProcessesGroups();
	    regions = CommonService.getAllRegions();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchDecisionsPrivileges() {
	try {
	    decisionsPrivilegesData = SecurityService.getDecisionsPrivileges(selectedWFProcessGroupId, selectedCategoryId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	selectedCategoryId = (long) FlagsEnum.ALL.getCode();
	selectedWFProcessGroupId = (long) FlagsEnum.ALL.getCode();
	decisionsPrivilegesData = new ArrayList<DecisionPrivilegeData>();
    }

    public void deleteDecisionPrivilege(DecisionPrivilegeData decisionPrivilegeData) {
	try {
	    decisionPrivilegeData.getDecisionPrivilege().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    SecurityService.deleteDecisionPrivilege(decisionPrivilegeData);
	    decisionsPrivilegesData.remove(decisionPrivilegeData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void instantiateNewDecisionPrivilegeData() {
	selectedDecisionPrivilege = new DecisionPrivilegeData();
    }

    public void saveSelectedDecisionPrivilege() {
	try {
	    selectedDecisionPrivilege.getDecisionPrivilege().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    SecurityService.addDecisionPrivilege(selectedDecisionPrivilege);
	    selectedDecisionPrivilege = null;
	    privilegeOwnerType = 1L;
	    privilegeType = 1L;
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetPrivilegeOwnerData() {
	selectedDecisionPrivilege.setEmpId(null);
	selectedDecisionPrivilege.setEmpName(null);
	selectedDecisionPrivilege.setJobId(null);
	selectedDecisionPrivilege.setJobName(null);
	selectedDecisionPrivilege.setUnitId(null);
	selectedDecisionPrivilege.setUnitFullName(null);
    }

    public void resetPrivilegeTypeData() {
	selectedDecisionPrivilege.setBeneficiaryRegionId(null);
	selectedDecisionPrivilege.setBeneficiaryRegionDesc(null);
	selectedDecisionPrivilege.setBeneficiaryUnitId(null);
	selectedDecisionPrivilege.setBeneficiaryUnitFullName(null);
    }

    public void getPrivilegeOwner() {
	try {
	    EmployeeData employeeData = EmployeesService.getEmployeeData(selectedDecisionPrivilege.getEmpId());
	    if (employeeData == null) {
		setServerSideErrorMessages(getMessage("error_UIError"));
		return;
	    }
	    if (employeeData.getPhysicalUnitId() == null) {
		selectedDecisionPrivilege.setEmpId(null);
		selectedDecisionPrivilege.setEmpName(null);
		selectedDecisionPrivilege.setUnitId(null);
		selectedDecisionPrivilege.setUnitFullName(null);
		setServerSideErrorMessages(getMessage("error_employeeStatusInNotSuitable"));
		return;
	    }
	    selectedDecisionPrivilege.setUnitId(employeeData.getPhysicalUnitId());
	    selectedDecisionPrivilege.setUnitFullName(employeeData.getPhysicalUnitFullName());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<Category> getCategories() {
	return categories;
    }

    public List<WFProcessGroup> getWfProcessGroups() {
	return wfProcessGroups;
    }

    public List<DecisionPrivilegeData> getDecisionsPrivilegesData() {
	return decisionsPrivilegesData;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public Long getSelectedCategoryId() {
	return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
	this.selectedCategoryId = selectedCategoryId;
    }

    public Long getSelectedWFProcessGroupId() {
	return selectedWFProcessGroupId;
    }

    public void setSelectedWFProcessGroupId(Long selectedWFProcessGroupId) {
	this.selectedWFProcessGroupId = selectedWFProcessGroupId;
    }

    public Long getPrivilegeOwnerType() {
	return privilegeOwnerType;
    }

    public void setPrivilegeOwnerType(Long privilegeOwnerType) {
	this.privilegeOwnerType = privilegeOwnerType;
    }

    public Long getPrivilegeType() {
	return privilegeType;
    }

    public void setPrivilegeType(Long privilegeType) {
	this.privilegeType = privilegeType;
    }

    public DecisionPrivilegeData getSelectedDecisionPrivilege() {
	return selectedDecisionPrivilege;
    }

    public int getPageSize() {
	return pageSize;
    }
}
