package com.code.ui.backings.hcm.missions;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MissionsService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "missionBalanceInquiry")
@ViewScoped
public class MissionBalanceInquiry extends BaseBacking {

    private EmployeeData searchEmp;
    private List<EmployeeMenuAction> employeeMenuActions;
    private long empId;
    private int financialYear; // 1 for the current year , 0 for previous year , 2 for the next year, 3 for specified financial year
    private Date specificFinancialYearDate;

    public MissionBalanceInquiry() {
	super();
	this.setScreenTitle(getMessage("title_missionBalanceInquiry"));
	this.financialYear = 1;
	try {
	    searchEmp = EmployeesService.getEmployeeData(this.loginEmpData.getEmpId());
	    employeeMenuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.MSN_BALANCE_INQUIRY.getCode());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} 
    }

    public void resetSpecificFinancialYearDate() {
	specificFinancialYearDate = null;
    }

    public void printMissionBalance(int hajjFlag) {
	try {
	    if (!(this.loginEmpData.getEmpId().equals(searchEmp.getEmpId())
		    || (this.loginEmpData.getIsManager().equals(FlagsEnum.ON.getCode()) && searchEmp.getPhysicalUnitHKey() != null && searchEmp.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.loginEmpData.getPhysicalUnitHKey())))
		    || isRequesterAuthorized()))
		throw new BusinessException("error_notAuthorized");

	    if (financialYear == 3 && specificFinancialYearDate == null)
		throw new BusinessException("error_specificFinancialYearDateIsManatory");

	    byte[] bytes = MissionsService.getMissionBalanceInquiryBytes(hajjFlag, searchEmp, financialYear, specificFinancialYearDate);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} 
    }

    private boolean isRequesterAuthorized() throws BusinessException {
	long categoryId = searchEmp.getCategoryId().longValue();
	long searchEmpPhysicalRegionId = searchEmp.getPhysicalRegionId() == null ? FlagsEnum.ALL.getCode() : searchEmp.getPhysicalRegionId();
	if (CategoriesEnum.OFFICERS.getCode() == categoryId &&
		SecurityService.isEmployeeMenuActionGranted(searchEmpPhysicalRegionId, searchEmp.getPhysicalUnitHKey(), MenuActionsEnum.MSN_BALANCE_INQUIRY_SHOW_ALL_OFFICERS.getCode(), employeeMenuActions)) {
	    return true;
	} else if (CategoriesEnum.SOLDIERS.getCode() == categoryId
		&& SecurityService.isEmployeeMenuActionGranted(searchEmpPhysicalRegionId, searchEmp.getPhysicalUnitHKey(), MenuActionsEnum.MSN_BALANCE_INQUIRY_SHOW_ALL_SOLDIERS.getCode(), employeeMenuActions)) {
	    return true;
	} else if ((CategoriesEnum.PERSONS.getCode() == categoryId || CategoriesEnum.USERS.getCode() == categoryId
		|| CategoriesEnum.WAGES.getCode() == categoryId || CategoriesEnum.CONTRACTORS.getCode() == categoryId
		|| CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId)
		&& SecurityService.isEmployeeMenuActionGranted(searchEmpPhysicalRegionId, searchEmp.getPhysicalUnitHKey(), MenuActionsEnum.MSN_BALANCE_INQUIRY_SHOW_ALL_PERSONS.getCode(), employeeMenuActions)) {
	    return true;
	}
	return false;
    }

    public void searchEmployee() {
	try {
	    searchEmp = EmployeesService.getEmployeeData(empId);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} 
    }

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public EmployeeData getSearchEmp() {
	return searchEmp;
    }

    public void setSearchEmp(EmployeeData searchEmp) {
	this.searchEmp = searchEmp;
    }

    public List<EmployeeMenuAction> getEmployeeMenuActions() {
	return employeeMenuActions;
    }

    public void setEmployeeMenuActions(List<EmployeeMenuAction> employeeMenuActions) {
	this.employeeMenuActions = employeeMenuActions;
    }

    public int getFinancialYear() {
	return financialYear;
    }

    public void setFinancialYear(int financialYear) {
	this.financialYear = financialYear;
    }

    public Date getSpecificFinancialYearDate() {
	return specificFinancialYearDate;
    }

    public void setSpecificFinancialYearDate(Date specificFinancialYearDate) {
	this.specificFinancialYearDate = specificFinancialYearDate;
    }

}
