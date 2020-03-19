package com.code.ui.backings.hcm.vacations;

import java.util.List;

import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.TransientVacationTransactionData;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

public class FutureVacationBase extends BaseBacking {

    protected EmployeeData currentEmployee;
    protected TransientVacationTransactionData futureVacation;
    protected TransientVacationTransactionData newFutureVacation;
    protected String balance;
    protected StringBuilder categoriesIds;
    protected List<VacationType> vacTypeList;
    // screenMode= 1 open General Manager Screen
    // screenMode= 2 open External Employees Screen
    protected int screenMode = 0;
    // viewMode= 0 open the page from menu
    // viewMode =1 opened as detail from management page
    // viewMode = 2 opened as edit from management page
    protected int viewMode = 0;
    protected Long vacationId;
    protected Long empId;
    protected Long vacationTypeId;
    protected String employeeIds;
    protected boolean isAdmin;
    protected boolean isSignAdmin;

    public void init() {
	try {
	    if (this.getRequest().getParameter("mode") != null)
		screenMode = Integer.parseInt(this.getRequest().getParameter("mode"));
	    if (this.getRequest().getParameter("viewMode") != null)
		viewMode = Integer.parseInt(this.getRequest().getParameter("viewMode").trim());
	    if (viewMode != 0) {
		if (this.getRequest().getParameter("vacId") != null)
		    vacationId = Long.parseLong(this.getRequest().getParameter("vacId").trim());
		if (this.getRequest().getParameter("employeeId") != null)
		    empId = Long.parseLong(this.getRequest().getParameter("employeeId").trim());

		currentEmployee = EmployeesService.getEmployeeData(empId);
		futureVacation = FutureVacationsService.getFutureVacationTransactionDataById(vacationId);
		vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	    } else {
		reset();
	    }
	    if (screenMode == 1)
		this.employeeIds = VacationsService.getPresidencyManagers();

	    categoriesIds = new StringBuilder();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }

    public void inquiryBalance() {
	try {
	    if (futureVacation.getVacationTypeId() == VacationTypesEnum.SICK.getCode()
		    || (futureVacation.getVacationTypeId() == VacationTypesEnum.EXCEPTIONAL.getCode()
			    && (CategoriesEnum.PERSONS.getCode() == currentEmployee.getCategoryId()
				    || CategoriesEnum.USERS.getCode() == currentEmployee.getCategoryId()
				    || CategoriesEnum.WAGES.getCode() == currentEmployee.getCategoryId()
				    || CategoriesEnum.MEDICAL_STAFF.getCode() == currentEmployee.getCategoryId()))) {
		if (futureVacation.getSubVacationType() == null)
		    futureVacation.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_ONE.getCode());
	    } else {
		futureVacation.setSubVacationType(null);
	    }
	    balance = VacationsService.calculateVacationBalance(futureVacation.getVacationTypeId(), futureVacation.getSubVacationType(), futureVacation.getLocationFlag(), currentEmployee, futureVacation.getStartDate());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectEmployee() {
	try {
	    reset();
	    currentEmployee = EmployeesService.getEmployeeData(empId);
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	    inquiryBalance();
	    validateSignAdmins();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectVacation() {
	try {
	    TransientVacationTransactionData selectedVacation = FutureVacationsService.getFutureVacationTransactionDataByVacType(empId, vacationTypeId, FlagsEnum.ON.getCode());
	    if (selectedVacation != null) {
		if ((selectedVacation.getRequestType() == RequestTypesEnum.MODIFY.getCode() || selectedVacation.getRequestType() == RequestTypesEnum.CANCEL.getCode()) && selectedVacation.getApprovedFlag() == FlagsEnum.OFF.getCode()) {
		    newFutureVacation = selectedVacation;
		    futureVacation = FutureVacationsService.getFutureVacationTransactionDataByParentId(newFutureVacation.getTransientVacationParentId(), vacationId);

		} else {
		    futureVacation = selectedVacation;
		    newFutureVacation = new TransientVacationTransactionData();
		    newFutureVacation.setStartDate(futureVacation.getStartDate());
		    newFutureVacation.setVacationTypeId(futureVacation.getVacationTypeId());
		    newFutureVacation.setPaidVacationType(futureVacation.getPaidVacationType());
		    newFutureVacation.setSubVacationType(futureVacation.getSubVacationType());
		    newFutureVacation.setContactAddress(futureVacation.getContactAddress());
		    newFutureVacation.setContactNumber(futureVacation.getContactNumber());
		    newFutureVacation.setEmpId(futureVacation.getEmpId());
		    newFutureVacation.setVacationTransactionId(futureVacation.getVacationTransactionId());
		    newFutureVacation.setStartDate(futureVacation.getStartDate());
		    newFutureVacation.setLocation(futureVacation.getLocation());
		    newFutureVacation.setLocationFlag(futureVacation.getLocationFlag());
		    newFutureVacation.setTransientVacationParentId(futureVacation.getId());
		    newFutureVacation.setPaidVacationType(futureVacation.getPaidVacationType());
		    newFutureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());

		}
	    } else {
		selectedVacation = new TransientVacationTransactionData();
		selectedVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
		newFutureVacation = selectedVacation;
		futureVacation = selectedVacation;
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    protected void validateGeneralManagerAdmins() throws BusinessException {
	this.setAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_GENEAL_MANAGER_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_GENEAL_MANAGER.getCode()));
	this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_GENEAL_MANAGER_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_GENEAL_MANAGER.getCode()));

    }

    protected void validateExternalEmployeesAdmins() throws BusinessException {

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_OFFICERS.getCode()))
	    categoriesIds.append(CategoriesEnum.OFFICERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_SOLDIERS.getCode()))
	    categoriesIds.append(CategoriesEnum.SOLDIERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_PERSONS.getCode()))
	    categoriesIds.append(CategoriesEnum.PERSONS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_USERS.getCode()))
	    categoriesIds.append(CategoriesEnum.USERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_WAGES.getCode()))
	    categoriesIds.append(CategoriesEnum.WAGES.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_CONTRACTORS.getCode()))
	    categoriesIds.append(CategoriesEnum.CONTRACTORS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_ADD_EXTERNAL_MEDICAL_STAFF.getCode()))
	    categoriesIds.append(CategoriesEnum.MEDICAL_STAFF.getCode() + ",");

	if (categoriesIds.length() != 0 && categoriesIds.substring(categoriesIds.length() - 1).equals(","))
	    categoriesIds.delete(categoriesIds.length() - 1, categoriesIds.length());

	this.setAdmin(categoriesIds.length() != 0 ? true : false);

    }

    private void validateSignAdmins() throws BusinessException {
	if (currentEmployee.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_OFFICERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_SOLDIERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.PERSONS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_PERSONS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.USERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_USERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.WAGES.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_WAGES.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_CONTRACTORS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_ADD_EXTERNAL_EMPLOYEES_VACATION.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_SIGN_EXTERNAL_MEDICAL_STAFF.getCode()));
	}
    }

    public void startDateAndPeriodChangeListener(TransientVacationTransactionData futureVac) {
	try {
	    if (futureVac.getStartDate() != null && futureVac.getPeriod() != null && futureVac.getPeriod() > 0) {
		futureVac.setEndDateString(HijriDateService.addSubStringHijriDays(futureVac.getStartDateString(), futureVac.getPeriod() - 1));
	    } else {
		futureVac.setEndDateString("");
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void vacationTypeListener() {
	futureVacation.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	futureVacation.setLocation(getMessage("label_ksa"));
	inquiryBalance();
    }

    public void locationFlagChangeListner(AjaxBehaviorEvent event) {
	if (futureVacation.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode())) {
	    futureVacation.setLocation(null);
	} else {
	    futureVacation.setLocation(getMessage("label_ksa"));
	}
    }

    public void reset() {
	try {
	    currentEmployee = new EmployeeData();
	    futureVacation = new TransientVacationTransactionData();
	    newFutureVacation = new TransientVacationTransactionData();
	    futureVacation.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	    futureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
	    futureVacation.setLocation(getMessage("label_ksa"));
	    futureVacation.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());
	    futureVacation.setStartDate(HijriDateService.getHijriSysDate());
	    futureVacation.setDecisionDate(HijriDateService.getHijriSysDate());
	    vacationTypeId = VacationTypesEnum.REGULAR.getCode();
	    balance = "";
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public EmployeeData getCurrentEmployee() {
	return currentEmployee;
    }

    public void setCurrentEmployee(EmployeeData currentEmployee) {
	this.currentEmployee = currentEmployee;
    }

    public TransientVacationTransactionData getFutureVacation() {
	return futureVacation;
    }

    public void setFutureVacation(TransientVacationTransactionData futureVacation) {
	this.futureVacation = futureVacation;
    }

    public TransientVacationTransactionData getNewFutureVacation() {
	return newFutureVacation;
    }

    public void setNewFutureVacation(TransientVacationTransactionData newFutureVacation) {
	this.newFutureVacation = newFutureVacation;
    }

    public String getBalance() {
	return balance;
    }

    public void setBalance(String balance) {
	this.balance = balance;
    }

    public StringBuilder getCategoriesIds() {
	return categoriesIds;
    }

    public void setCategoriesIds(StringBuilder categoriesIds) {
	this.categoriesIds = categoriesIds;
    }

    public List<VacationType> getVacTypeList() {
	return vacTypeList;
    }

    public void setVacTypeList(List<VacationType> vacTypeList) {
	this.vacTypeList = vacTypeList;
    }

    public int getScreenMode() {
	return screenMode;
    }

    public void setScreenMode(int screenMode) {
	this.screenMode = screenMode;
    }

    public int getViewMode() {
	return viewMode;
    }

    public void setViewMode(int viewMode) {
	this.viewMode = viewMode;
    }

    public Long getVacationId() {
	return vacationId;
    }

    public void setVacationId(Long vacationId) {
	this.vacationId = vacationId;
    }

    public Long getEmpId() {
	return empId;
    }

    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public String getEmployeeIds() {
	return employeeIds;
    }

    public void setEmployeeIds(String employeeIds) {
	this.employeeIds = employeeIds;
    }

    public boolean isAdmin() {
	return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
	this.isAdmin = isAdmin;
    }

    public boolean isSignAdmin() {
	return isSignAdmin;
    }

    public void setSignAdmin(boolean isSignAdmin) {
	this.isSignAdmin = isSignAdmin;
    }

}
