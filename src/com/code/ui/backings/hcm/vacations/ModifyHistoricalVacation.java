package com.code.ui.backings.hcm.vacations;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.vacations.HistoricalVacationTransaction;
import com.code.dal.orm.hcm.vacations.VacationType;
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
    boolean saveAuthorized;
    boolean signAuthorized;
    // mode= 0 open the page from menu
    // mode =1 opened as detail from management page
    // mode = 2 opened as edit from management page
    private int viewMode;
    Long vacationId;
    Long empId;

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
	    } else {
		currentEmployee = new EmployeeData();
		historicalVacationTransaction = new HistoricalVacationTransaction();
		newHistoricalVacationTransaction = new HistoricalVacationTransaction();
	    }
	    decisionRegions = CommonService.getAllRegions();
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	    saveAuthorized = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_SAVE_MODIFIED_HISTORICAL_VACATION.getCode());
	    signAuthorized = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_MODIFY_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_SIGN_MODIFIED_HISTORICAL_VACATION.getCode());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    /*---------------------------------------------------------- Operation  -----------------------------------------------*/
    public void saveVacation() {
	try {
	    if (newHistoricalVacationTransaction.getId() == null) {
		newHistoricalVacationTransaction.setRequestType(RequestTypesEnum.MODIFY.getCode());
		newHistoricalVacationTransaction.setActiveFlag(FlagsEnum.ON.getCode());
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
		newHistoricalVacationTransaction.setExceededDays(historicalVacationTransaction.getExceededDays());
		newHistoricalVacationTransaction.setLocation(historicalVacationTransaction.getLocation());
		newHistoricalVacationTransaction.setLocationFlag(historicalVacationTransaction.getLocationFlag());
		newHistoricalVacationTransaction.setHistoricalVacationParentId(historicalVacationTransaction.getId());
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

    public boolean isSaveAuthorized() {
	return saveAuthorized;
    }

    public boolean isSignAuthorized() {
	return signAuthorized;
    }

    public int getViewMode() {
	return viewMode;
    }

}
