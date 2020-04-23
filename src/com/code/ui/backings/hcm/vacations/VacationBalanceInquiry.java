package com.code.ui.backings.hcm.vacations;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "vacationBalanceInquiry")
@ViewScoped
public class VacationBalanceInquiry extends BaseBacking implements Serializable {

    private List<VacationType> vacationTypes;
    private Long selectedVacationType;
    private Date balanceInquiryDate;
    private String vacationBalance;
    private EmployeeData selectedEmployee;
    private List<EmployeeMenuAction> employeeMenuActions;

    public VacationBalanceInquiry() {
	super();
	this.setScreenTitle(getMessage("title_vacationBalanceInquiry"));

	try {
	    selectedVacationType = VacationTypesEnum.REGULAR.getCode();
	    balanceInquiryDate = HijriDateService.getHijriSysDate();

	    selectedEmployee = new EmployeeData();
	    selectedEmployee.setEmpId(this.loginEmpData.getEmpId());
	    selectedEmployee.setCategoryId(this.loginEmpData.getCategoryId());

	    setVacationTypes();

	    employeeMenuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_BALANCE_INQUIRY.getCode());
	    inquiryBalance();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void inquiryBalance() {
	try {
	    selectedEmployee = EmployeesService.getEmployeeData(selectedEmployee.getEmpId());

	    if (!(this.loginEmpData.getEmpId().equals(selectedEmployee.getEmpId())
		    || (this.loginEmpData.getIsManager().equals(FlagsEnum.ON.getCode()) && selectedEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.loginEmpData.getPhysicalUnitHKey())))
		    || isEmployeeAuthorizedViewCategory())) {
		vacationBalance = "";
		throw new BusinessException("error_notAuthorized");
	    }

	    vacationBalance = VacationsService.inquireVacationBalance(selectedEmployee, selectedVacationType, balanceInquiryDate).replace("\n", "<br/>");

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private boolean isEmployeeAuthorizedViewCategory() throws BusinessException {
	long categoryId = selectedEmployee.getCategoryId().longValue();
	long physicalRegionId = selectedEmployee.getPhysicalRegionId() == null ? FlagsEnum.ALL.getCode() : selectedEmployee.getPhysicalRegionId();
	String physicalUnitHkey = selectedEmployee.getPhysicalUnitHKey();

	if (CategoriesEnum.OFFICERS.getCode() == categoryId
		&& SecurityService.isEmployeeMenuActionGranted(physicalRegionId, physicalUnitHkey, MenuActionsEnum.VAC_BALANCE_INQUIRY_SHOW_ALL_OFFICERS.getCode(), employeeMenuActions))
	    return true;

	else if (CategoriesEnum.SOLDIERS.getCode() == categoryId
		&& SecurityService.isEmployeeMenuActionGranted(physicalRegionId, physicalUnitHkey, MenuActionsEnum.VAC_BALANCE_INQUIRY_SHOW_ALL_SOLDIERS.getCode(), employeeMenuActions))
	    return true;

	else if ((CategoriesEnum.PERSONS.getCode() == categoryId || CategoriesEnum.USERS.getCode() == categoryId
		|| CategoriesEnum.WAGES.getCode() == categoryId || CategoriesEnum.CONTRACTORS.getCode() == categoryId
		|| CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId)
		&& SecurityService.isEmployeeMenuActionGranted(physicalRegionId, physicalUnitHkey, MenuActionsEnum.VAC_BALANCE_INQUIRY_SHOW_ALL_CIVILIANS.getCode(), employeeMenuActions))
	    return true;

	return false;
    }

    public void setVacationTypes() {
	try {
	    this.vacationTypes = VacationsService.getVacationTypes(selectedEmployee.getCategoryId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void setVacationTypes(List<VacationType> vacationTypes) {
	this.vacationTypes = vacationTypes;
    }

    public List<VacationType> getVacationTypes() {
	return vacationTypes;
    }

    public void setSelectedVacationType(Long selectedVacationType) {
	this.selectedVacationType = selectedVacationType;
    }

    public Long getSelectedVacationType() {
	return selectedVacationType;
    }

    public void setBalanceInquiryDate(Date balanceInquiryDate) {
	this.balanceInquiryDate = balanceInquiryDate;
    }

    public Date getBalanceInquiryDate() {
	return balanceInquiryDate;
    }

    public String getVacationBalance() {
	return vacationBalance;
    }

    public void setVacationBalanceString(String vacationBalance) {
	this.vacationBalance = vacationBalance;
    }

    public EmployeeData getSelectedEmployee() {
	return selectedEmployee;
    }

    public void setSelectedEmployee(EmployeeData selectedEmployee) {
	this.selectedEmployee = selectedEmployee;
    }

    public List<EmployeeMenuAction> getEmployeeMenuActions() {
	return employeeMenuActions;
    }

    public void setEmployeeMenuActions(List<EmployeeMenuAction> employeeMenuActions) {
	this.employeeMenuActions = employeeMenuActions;
    }
}
