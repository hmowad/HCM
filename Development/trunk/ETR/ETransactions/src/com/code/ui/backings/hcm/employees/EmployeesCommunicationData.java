package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesCommunicationData")
@ViewScoped
public class EmployeesCommunicationData extends BaseBacking implements Serializable {

    private EmployeeData employeeData;
    private boolean selectPrivilege;
    private boolean editPrivilege;
    private Long selectedEmpId;
    private String socialId;
    private String empName;
    private String physicalUnitName;
    private String jobDesc;
    private String searchOfficialMobileNumber;
    private String searchDirectPhoneNumber;
    private String searchPrivateMobileNumber;
    private String searchPhoneExt;
    private String searchShieldMobileNumber;
    private String searchIpPhoneExt;
    private String searchEmail;
    private String searchUserAccount;
    private List<EmployeeData> employeesList;
    private int pageSize = 10;

    public EmployeesCommunicationData() throws BusinessException {
	// view privilege
	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_COMMUNCATIONS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_SHOW_COMMUNICATIONS.getCode()))
	    selectPrivilege = true;
	else
	    selectPrivilege = false;
	// edit privilege
	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_COMMUNCATIONS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_MODIFY_COMMUNICATIONS.getCode()))
	    editPrivilege = true;
	else
	    editPrivilege = false;
    }

    public void selectEmployee(EmployeeData employeeData) {
	this.employeeData = employeeData;
	this.selectedEmpId = this.employeeData.getEmpId();
    }

    public void clearUserAccount() {
	employeeData.setUserAccount(null);
    }

    public void searchEmployees() {
	try {
	    employeeData = null;
	    selectedEmpId = null;
	    employeesList = EmployeesService.getEmployeesByEmpCommunicationData(socialId, empName, physicalUnitName, jobDesc, searchOfficialMobileNumber, searchDirectPhoneNumber, searchPrivateMobileNumber, searchPhoneExt, searchShieldMobileNumber, searchIpPhoneExt, searchEmail, searchUserAccount);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    employeeData.getEmployee().setSystemUser(this.loginEmpData.getEmpId() + ""); // For auditing.
	    EmployeesService.updateEmployee(employeeData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void reset() {
	employeeData = null;
	selectedEmpId = null;
	socialId = null;
	empName = null;
	physicalUnitName = null;
	jobDesc = null;
	searchOfficialMobileNumber = null;
	searchDirectPhoneNumber = null;
	searchPrivateMobileNumber = null;
	searchPhoneExt = null;
	searchShieldMobileNumber = null;
	searchIpPhoneExt = null;
	searchEmail = null;
	searchUserAccount = null;
	employeesList = new ArrayList<>();
    }

    public void setEmployeeData(EmployeeData employeeData) {
	this.employeeData = employeeData;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public void setSocialId(String socialId) {
	this.socialId = socialId;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public EmployeeData getEmployeeData() {
	return employeeData;
    }

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public String getSocialId() {
	return socialId;
    }

    public String getEmpName() {
	return empName;
    }

    public int getPageSize() {
	return pageSize;
    }

    public String getPhysicalUnitName() {
	return physicalUnitName;
    }

    public void setPhysicalUnitName(String physicalUnitName) {
	this.physicalUnitName = physicalUnitName;
    }

    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }

    public String getSearchOfficialMobileNumber() {
	return searchOfficialMobileNumber;
    }

    public void setSearchOfficialMobileNumber(String searchOfficialMobileNumber) {
	this.searchOfficialMobileNumber = searchOfficialMobileNumber;
    }

    public String getSearchDirectPhoneNumber() {
	return searchDirectPhoneNumber;
    }

    public void setSearchDirectPhoneNumber(String searchDirectPhoneNumber) {
	this.searchDirectPhoneNumber = searchDirectPhoneNumber;
    }

    public String getSearchPrivateMobileNumber() {
	return searchPrivateMobileNumber;
    }

    public void setSearchPrivateMobileNumber(String searchPrivateMobileNumber) {
	this.searchPrivateMobileNumber = searchPrivateMobileNumber;
    }

    public String getSearchPhoneExt() {
	return searchPhoneExt;
    }

    public void setSearchPhoneExt(String searchPhoneExt) {
	this.searchPhoneExt = searchPhoneExt;
    }

    public String getSearchShieldMobileNumber() {
	return searchShieldMobileNumber;
    }

    public void setSearchShieldMobileNumber(String searchShieldMobileNumber) {
	this.searchShieldMobileNumber = searchShieldMobileNumber;
    }

    public String getSearchIpPhoneExt() {
	return searchIpPhoneExt;
    }

    public void setSearchIpPhoneExt(String searchIpPhoneExt) {
	this.searchIpPhoneExt = searchIpPhoneExt;
    }

    public String getSearchEmail() {
	return searchEmail;
    }

    public void setSearchEmail(String searchEmail) {
	this.searchEmail = searchEmail;
    }

    public String getSearchUserAccount() {
	return searchUserAccount;
    }

    public void setSearchUserAccount(String searchUserAccount) {
	this.searchUserAccount = searchUserAccount;
    }

    public boolean isSelectPrivilege() {
	return selectPrivilege;
    }

    public void setSelectPrivilege(boolean selectPrivilege) {
	this.selectPrivilege = selectPrivilege;
    }

    public boolean isEditPrivilege() {
	return editPrivilege;
    }

    public void setEditPrivilege(boolean editPrivilege) {
	this.editPrivilege = editPrivilege;
    }

    public List<EmployeeData> getEmployeesList() {
	return employeesList;
    }

    public void setEmployeesList(List<EmployeeData> employeesList) {
	this.employeesList = employeesList;
    }

}
