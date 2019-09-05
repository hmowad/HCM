package com.code.ui.backings.hcm.vacations;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.vacations.HistoricalVacationTransactionData;
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
import com.code.services.hcm.HistoricalVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "historicalVacation")
@ViewScoped
public class AddHistoricalVacation extends BaseBacking {
    private EmployeeData currentEmployee;
    private HistoricalVacationTransactionData historicalVacationTransactionData;
    private String balance;
    private List<VacationType> vacTypeList;
    private List<Region> decisionRegions;
    private int exceededFlag = 0;
    boolean saveAuthorized;
    boolean signAuthorized;
    // mode= 0 open the page from menu
    // mode =1 opened as detail from management page
    // mode = 2 opened as edit from management page
    int viewMode;
    Long vacationId;
    Long empId;

    public AddHistoricalVacation() {
	super.init();
	this.setScreenTitle(this.getMessage("title_addNewHistoricalVacation"));
	try {
	    if (this.getRequest().getParameter("mode") != null)
		viewMode = Integer.parseInt(this.getRequest().getParameter("mode").trim());
	    if (viewMode != 0) {
		if (this.getRequest().getParameter("vacId") != null)
		    vacationId = Long.parseLong(this.getRequest().getParameter("vacId").trim());
		if (this.getRequest().getParameter("employeeId") != null)
		    empId = Long.parseLong(this.getRequest().getParameter("employeeId").trim());
		if (viewMode == 1)
		    this.setScreenTitle(this.getMessage("title_newHistoricalVacationDetail"));
		currentEmployee = EmployeesService.getEmployeeData(empId);
		historicalVacationTransactionData = HistoricalVacationsService.getHistoricalVacationTransactionDataById(vacationId);
		if (historicalVacationTransactionData.getExceededDays() != null && historicalVacationTransactionData.getExceededDays() > 0)
		    exceededFlag = FlagsEnum.ON.getCode();
		inquiryBalance();
		vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	    } else {
		currentEmployee = new EmployeeData();
		historicalVacationTransactionData = new HistoricalVacationTransactionData();
		historicalVacationTransactionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		historicalVacationTransactionData.setApprovedFlag(FlagsEnum.OFF.getCode());
		historicalVacationTransactionData.setLocation(getMessage("label_ksa"));
		historicalVacationTransactionData.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());

	    }
	    decisionRegions = CommonService.getAllRegions();
	    saveAuthorized = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_ADD_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_SAVE_NEW_HISTORICAL_VACATION.getCode());
	    signAuthorized = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_ADD_HISTORICAL_VACATION.getCode(), MenuActionsEnum.VAC_SIGN_NEW_HISTORICAL_VACATION.getCode());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));

	}
    }

    /*---------------------------------------------------------- Operation  -----------------------------------------------*/
    public void saveVacation() {
	try {
	    if (historicalVacationTransactionData.getId() == null) {
		historicalVacationTransactionData.setRequestType(RequestTypesEnum.NEW.getCode());
		historicalVacationTransactionData.setActiveFlag(FlagsEnum.ON.getCode());
		historicalVacationTransactionData.setEmpId(currentEmployee.getEmpId());

		HistoricalVacationsService.insertHistoricalVacationTransaction(historicalVacationTransactionData.getHistoricalVacationTransaction(), currentEmployee);

		historicalVacationTransactionData.setId(historicalVacationTransactionData.getHistoricalVacationTransaction().getId());
	    } else {
		HistoricalVacationsService.modifyHistoricalVacationTransaction(historicalVacationTransactionData.getHistoricalVacationTransaction(), currentEmployee, false, false);
	    }

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void signVacation() {
	try {
	    historicalVacationTransactionData.setApprovedFlag(FlagsEnum.ON.getCode());

	    HistoricalVacationsService.modifyHistoricalVacationTransaction(historicalVacationTransactionData.getHistoricalVacationTransaction(), currentEmployee, true, false);

	    inquiryBalance();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    historicalVacationTransactionData.setApprovedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /*---------------------------------------------------------- end of operation  -----------------------------------------------*/
    public void selectEmployee() {
	try {
	    currentEmployee = EmployeesService.getEmployeeData(currentEmployee.getEmpId());
	    historicalVacationTransactionData = new HistoricalVacationTransactionData();
	    historicalVacationTransactionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	    historicalVacationTransactionData.setApprovedFlag(FlagsEnum.OFF.getCode());
	    historicalVacationTransactionData.setStartDate(currentEmployee.getRecruitmentDate());
	    historicalVacationTransactionData.setLocation(getMessage("label_ksa"));
	    historicalVacationTransactionData.setDecisionRegionId(currentEmployee.getPhysicalRegionId());
	    historicalVacationTransactionData.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void startDateAndPeriodChangeListener(AjaxBehaviorEvent event) {
	try {
	    if (historicalVacationTransactionData.getStartDate() != null && historicalVacationTransactionData.getPeriod() != null && historicalVacationTransactionData.getPeriod() > 0) {
		historicalVacationTransactionData.setEndDateString(HijriDateService.addSubStringHijriDays(historicalVacationTransactionData.getStartDateString(), historicalVacationTransactionData.getPeriod() - 1));
		historicalVacationTransactionData.setJoiningDateString(HijriDateService.addSubStringHijriDays(historicalVacationTransactionData.getStartDateString(), historicalVacationTransactionData.getPeriod()));
	    }

	    inquiryBalance();
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void exceededDaysListener(AjaxBehaviorEvent event) {
	try {
	    if (historicalVacationTransactionData.getExceededDays() == null || historicalVacationTransactionData.getExceededDays() < 0)
		throw new BusinessException("error_exceededDaysPositive");

	    historicalVacationTransactionData.setJoiningDateString(HijriDateService.addSubStringHijriDays(historicalVacationTransactionData.getEndDateString(), historicalVacationTransactionData.getExceededDays() + 1));

	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void exceededFlagListener() {
	try {
	    if (exceededFlag == 0) {
		this.historicalVacationTransactionData.setExceededDays(0);
		historicalVacationTransactionData.setJoiningDateString(HijriDateService.addSubStringHijriDays(historicalVacationTransactionData.getStartDateString(), historicalVacationTransactionData.getPeriod()));
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void inquiryBalance() {
	try {
	    if (historicalVacationTransactionData.getVacationTypeId() == VacationTypesEnum.SICK.getCode()
		    || (historicalVacationTransactionData.getVacationTypeId() == VacationTypesEnum.EXCEPTIONAL.getCode()
			    && (CategoriesEnum.PERSONS.getCode() == currentEmployee.getCategoryId()
				    || CategoriesEnum.USERS.getCode() == currentEmployee.getCategoryId()
				    || CategoriesEnum.WAGES.getCode() == currentEmployee.getCategoryId()
				    || CategoriesEnum.MEDICAL_STAFF.getCode() == currentEmployee.getCategoryId()))) {
		if (historicalVacationTransactionData.getSubVacationType() == null)
		    historicalVacationTransactionData.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_ONE.getCode());
	    } else {
		historicalVacationTransactionData.setSubVacationType(null);
	    }
	    balance = HistoricalVacationsService.calculateHistoricalVacationBalance(historicalVacationTransactionData.getVacationTypeId(), historicalVacationTransactionData.getSubVacationType(), historicalVacationTransactionData.getLocationFlag(), currentEmployee, historicalVacationTransactionData.getStartDate());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void locationFlagChangeListner(AjaxBehaviorEvent event) {
	if (historicalVacationTransactionData.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode())) {
	    historicalVacationTransactionData.setLocation(null);
	} else {
	    historicalVacationTransactionData.setLocation(getMessage("label_ksa"));
	}
    }

    public void reset() {
	currentEmployee = new EmployeeData();
	historicalVacationTransactionData = new HistoricalVacationTransactionData();
	historicalVacationTransactionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	historicalVacationTransactionData.setApprovedFlag(FlagsEnum.OFF.getCode());
	historicalVacationTransactionData.setLocation(getMessage("label_ksa"));
	historicalVacationTransactionData.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());

	balance = "";
	try {
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
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

    public HistoricalVacationTransactionData getHistoricalVacationTransactionData() {
	return historicalVacationTransactionData;
    }

    public void setHistoricalVacationTransactionData(HistoricalVacationTransactionData historicalVacationTransactionData) {
	this.historicalVacationTransactionData = historicalVacationTransactionData;
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

    public List<Region> getDecisionRegions() {
	return decisionRegions;
    }

    public void setDecisionRegions(List<Region> decisionRegions) {
	this.decisionRegions = decisionRegions;
    }

    public int getExceededFlag() {
	return exceededFlag;
    }

    public void setExceededFlag(int exceededFlag) {
	this.exceededFlag = exceededFlag;
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
