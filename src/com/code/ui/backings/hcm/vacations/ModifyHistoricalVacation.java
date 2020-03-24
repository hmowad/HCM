package com.code.ui.backings.hcm.vacations;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.vacations.HistoricalVacationTransaction;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.HistoricalVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "modifyHistoricalVacation")
@ViewScoped
public class ModifyHistoricalVacation extends BaseBacking {
    private EmployeeData currentEmployee;
    private HistoricalVacationTransaction historicalVacationTransaction;
    private HistoricalVacationTransaction newHistoricalVacationTransaction;
    private String balance;
    private List<VacationType> vacTypeList;
    private List<Region> decisionRegions;
    // mode= 0 open the page from menu
    // mode =1 opened as detail from management page
    // mode = 2 opened as edit from management page
    private int viewMode;
    Long vacationId;
    Long empId;

    private StringBuilder categoriesIds;
    private boolean isAdmin;
    private boolean isSignAdmin;

    public ModifyHistoricalVacation() {

	try {
	    super.init();
	    this.setScreenTitle(this.getMessage("title_modifyHistoricalVacation"));
	    if (this.getRequest().getParameter("mode") != null)
		viewMode = Integer.parseInt(this.getRequest().getParameter("mode").trim());
	    if (viewMode != 0) {
		if (this.getRequest().getParameter("vacId") != null)
		    vacationId = Long.parseLong(this.getRequest().getParameter("vacId").trim());
		if (this.getRequest().getParameter("employeeId") != null)
		    empId = Long.parseLong(this.getRequest().getParameter("employeeId").trim());
		if (viewMode == 1)
		    this.setScreenTitle(this.getMessage("title_modifiedHistoricalVacationDetail"));
		currentEmployee = EmployeesService.getEmployeeData(empId);
		newHistoricalVacationTransaction = HistoricalVacationsService.getHistoricalVacationTransactionDataById(vacationId).getHistoricalVacationTransaction();
		historicalVacationTransaction = HistoricalVacationsService.getHistoricalVacationTransactionDataById(newHistoricalVacationTransaction.getHistoricalVacationParentId()).getHistoricalVacationTransaction();
		vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
		if (viewMode == 2)
		    validateSignAdmins();
	    } else {
		currentEmployee = new EmployeeData();
		viewMode = 0;
		historicalVacationTransaction = new HistoricalVacationTransaction();
		newHistoricalVacationTransaction = new HistoricalVacationTransaction();
		newHistoricalVacationTransaction.setApprovedFlag(FlagsEnum.OFF.getCode());

	    }
	    decisionRegions = CommonService.getAllRegions();
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	    categoriesIds = new StringBuilder();
	    validateEmployeesAdmins();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    private void validateEmployeesAdmins() throws BusinessException {
	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_OFFICERS.getCode()))
	    categoriesIds.append(CategoriesEnum.OFFICERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SOLDIERS.getCode()))
	    categoriesIds.append(CategoriesEnum.SOLDIERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_PERSONS.getCode()))
	    categoriesIds.append(CategoriesEnum.PERSONS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_USERS.getCode()))
	    categoriesIds.append(CategoriesEnum.USERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_WAGES.getCode()))
	    categoriesIds.append(CategoriesEnum.WAGES.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_CONTRACTORS.getCode()))
	    categoriesIds.append(CategoriesEnum.CONTRACTORS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_MEDICAL_STAFF.getCode()))
	    categoriesIds.append(CategoriesEnum.MEDICAL_STAFF.getCode() + ",");

	if (categoriesIds.length() != 0 && categoriesIds.substring(categoriesIds.length() - 1).equals(","))
	    categoriesIds.delete(categoriesIds.length() - 1, categoriesIds.length());

	this.setAdmin(categoriesIds.length() != 0 ? true : false);
    }

    public void validateSignAdmins() throws BusinessException {
	if (currentEmployee.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SIGN_OFFICERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SIGN_SOLDIERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.PERSONS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SIGN_PERSONS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.USERS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SIGN_USERS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.WAGES.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SIGN_WAGES.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SIGN_CONTRACTORS.getCode()));
	} else if (currentEmployee.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {
	    this.setSignAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SIGN_MEDICAL_STAFF.getCode()));
	}
    }

    /*---------------------------------------------------------- Operation  -----------------------------------------------*/
    public void saveVacation() {
	try {
	    if (currentEmployee.getEmpId().equals(this.loginEmpData.getEmpId()))
		throw new BusinessException("error_selfHistoricalVacationIsinvalid");
	    if (historicalVacationTransaction.getId() == null)
		throw new BusinessException("error_vacationId");
	    HistoricalVacationsService.validateOldHistoricalVacationActivationStatus(newHistoricalVacationTransaction.getHistoricalVacationParentId(), newHistoricalVacationTransaction.getId());
	    if (newHistoricalVacationTransaction.getId() == null) {
		newHistoricalVacationTransaction.setRequestType(RequestTypesEnum.MODIFY.getCode());
		newHistoricalVacationTransaction.setActiveFlag(FlagsEnum.ON.getCode());
		newHistoricalVacationTransaction.setExceededDays(0);
		newHistoricalVacationTransaction.setJoiningDateString(HijriDateService.addSubStringHijriDays(newHistoricalVacationTransaction.getEndDateString(), 1));
		HistoricalVacationsService.insertHistoricalVacationTransaction(newHistoricalVacationTransaction, currentEmployee);
	    } else {
		HistoricalVacationsService.modifyHistoricalVacationTransaction(newHistoricalVacationTransaction, currentEmployee, false, false);
	    }
	    historicalVacationTransaction.setActiveFlag(FlagsEnum.OFF.getCode());
	    HistoricalVacationsService.modifyHistoricalVacationTransaction(historicalVacationTransaction, currentEmployee, false, true);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void signVacation() {
	try {
	    if (currentEmployee.getEmpId().equals(this.loginEmpData.getEmpId()))
		throw new BusinessException("error_selfHistoricalVacationIsinvalid");
	    HistoricalVacationsService.validateOldHistoricalVacationActivationStatus(newHistoricalVacationTransaction.getHistoricalVacationParentId(), newHistoricalVacationTransaction.getId());

	    newHistoricalVacationTransaction.setApprovedFlag(FlagsEnum.ON.getCode());
	    HistoricalVacationsService.modifyHistoricalVacationTransaction(newHistoricalVacationTransaction, currentEmployee, true, false);

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    historicalVacationTransaction.setApprovedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /*---------------------------------------------------------- end of operation  -----------------------------------------------*/

    public void selectEmployee() {
	try {
	    currentEmployee = EmployeesService.getEmployeeData(currentEmployee.getEmpId());
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	    historicalVacationTransaction = new HistoricalVacationTransaction();
	    newHistoricalVacationTransaction = new HistoricalVacationTransaction();
	    newHistoricalVacationTransaction.setApprovedFlag(FlagsEnum.OFF.getCode());
	    validateSignAdmins();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectVacation() {
	try {
	    HistoricalVacationTransaction selectedVacation = HistoricalVacationsService.getHistoricalVacationTransactionDataById(historicalVacationTransaction.getId()).getHistoricalVacationTransaction();
	    if (selectedVacation.getRequestType() == RequestTypesEnum.MODIFY.getCode() && selectedVacation.getApprovedFlag() == FlagsEnum.OFF.getCode()) {
		newHistoricalVacationTransaction = selectedVacation;
		historicalVacationTransaction = HistoricalVacationsService.getHistoricalVacationTransactionDataById(newHistoricalVacationTransaction.getHistoricalVacationParentId()).getHistoricalVacationTransaction();
	    } else {
		historicalVacationTransaction = selectedVacation;
		newHistoricalVacationTransaction = new HistoricalVacationTransaction();
		newHistoricalVacationTransaction.setVacationTypeId(historicalVacationTransaction.getVacationTypeId());
		newHistoricalVacationTransaction.setPaidVacationType(historicalVacationTransaction.getPaidVacationType());
		newHistoricalVacationTransaction.setSubVacationType(historicalVacationTransaction.getSubVacationType());
		newHistoricalVacationTransaction.setContactAddress(historicalVacationTransaction.getContactAddress());
		newHistoricalVacationTransaction.setContactNumber(historicalVacationTransaction.getContactNumber());
		newHistoricalVacationTransaction.setEmpId(historicalVacationTransaction.getEmpId());
		newHistoricalVacationTransaction.setVacationTransactionId(historicalVacationTransaction.getVacationTransactionId());
		newHistoricalVacationTransaction.setStartDate(historicalVacationTransaction.getStartDate());
		newHistoricalVacationTransaction.setJoiningDate(historicalVacationTransaction.getJoiningDate());
		newHistoricalVacationTransaction.setJoiningRemarks(historicalVacationTransaction.getJoiningRemarks());
		newHistoricalVacationTransaction.setExceededDays(historicalVacationTransaction.getExceededDays());
		newHistoricalVacationTransaction.setLocation(historicalVacationTransaction.getLocation());
		newHistoricalVacationTransaction.setLocationFlag(historicalVacationTransaction.getLocationFlag());
		newHistoricalVacationTransaction.setHistoricalVacationParentId(historicalVacationTransaction.getId());
		newHistoricalVacationTransaction.setPaidVacationType(historicalVacationTransaction.getPaidVacationType());
		newHistoricalVacationTransaction.setApprovedFlag(FlagsEnum.OFF.getCode());

	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	currentEmployee = new EmployeeData();
	historicalVacationTransaction = new HistoricalVacationTransaction();
	newHistoricalVacationTransaction = new HistoricalVacationTransaction();
	newHistoricalVacationTransaction.setApprovedFlag(FlagsEnum.OFF.getCode());
    }

    public void periodChangeListener(AjaxBehaviorEvent event) {
	try {
	    if (newHistoricalVacationTransaction.getStartDate() != null && newHistoricalVacationTransaction.getPeriod() != null && newHistoricalVacationTransaction.getPeriod() > 0)
		newHistoricalVacationTransaction.setEndDateString(HijriDateService.addSubStringHijriDays(newHistoricalVacationTransaction.getStartDateString(), newHistoricalVacationTransaction.getPeriod() - 1));

	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public EmployeeData getCurrentEmployee() {
	return currentEmployee;
    }

    public void setCurrentEmployee(EmployeeData currentEmployee) {
	this.currentEmployee = currentEmployee;
    }

    public HistoricalVacationTransaction getHistoricalVacationTransaction() {
	return historicalVacationTransaction;
    }

    public void setHistoricalVacationTransaction(HistoricalVacationTransaction historicalVacationTransaction) {
	this.historicalVacationTransaction = historicalVacationTransaction;
    }

    public HistoricalVacationTransaction getNewHistoricalVacationTransaction() {
	return newHistoricalVacationTransaction;
    }

    public void setNewHistoricalVacationTransaction(HistoricalVacationTransaction newHistoricalVacationTransaction) {
	this.newHistoricalVacationTransaction = newHistoricalVacationTransaction;
    }

    public String getBalance() {
	return balance;
    }

    public void setBalance(String balance) {
	this.balance = balance;
    }

    public List<VacationType> getVacTypeList() {
	return vacTypeList;
    }

    public void setVacTypeList(List<VacationType> vacTypeList) {
	this.vacTypeList = vacTypeList;
    }

    public List<Region> getDecisionRegions() {
	return decisionRegions;
    }

    public void setDecisionRegions(List<Region> decisionRegions) {
	this.decisionRegions = decisionRegions;
    }

    public int getViewMode() {
	return viewMode;
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

    public StringBuilder getCategoriesIds() {
	return categoriesIds;
    }

    public void setCategoriesIds(StringBuilder categoriesIds) {
	this.categoriesIds = categoriesIds;
    }

}
