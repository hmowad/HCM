package com.code.ui.backings.hcm.vacations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.TransientVacationTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "futureVacationsManagement")
@ViewScoped
public class FutureVacationsManagement extends FutureVacationBase {
    private long vacationTypeId;
    private Date fromDate;
    private Date toDate;
    private Integer period;
    private Integer requestType;
    private String decisionNumber;
    private Integer locationFlag;
    private Integer approvedFlag;
    // screenMode= 1 open General Manager Screen
    // screenMode= 2 open External Employees Screen
    private Date currentDate;
    private int pageSize = 5;

    private List<TransientVacationTransactionData> futureVacations;

    public FutureVacationsManagement() {
	super.init();
	resetForm();
	try {
	    currentDate = HijriDateService.getHijriSysDate();

	    if (this.getRequest().getParameter("mode") != null)
		screenMode = Integer.parseInt(this.getRequest().getParameter("mode"));
	    switch (screenMode) {
	    case 1:
		this.setScreenTitle(this.getMessage("title_generalManagerVacationsManagement"));
		validateGeneralManagerAdmins();
		break;
	    case 2:
		this.setScreenTitle(this.getMessage("title_externalEmployeesVacationsManagement"));
		validateExternalEmployeesAdmins();
		break;
	    default:
		this.setServerSideErrorMessages(this.getMessage("error_general"));
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    public void searchFutureVacations() {
	try {
	    if (currentEmployee.getEmpId() == null)
		throw new BusinessException("error_employeeMandatory");
	    futureVacations = FutureVacationsService.searchFutureVacations(currentEmployee, decisionNumber, requestType, vacationTypeId, approvedFlag, fromDate, toDate, period, locationFlag);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	futureVacations = new ArrayList<TransientVacationTransactionData>();
	currentEmployee = new EmployeeData();
	decisionNumber = null;
	requestType = FlagsEnum.ALL.getCode();
	fromDate = null;
	toDate = null;
	approvedFlag = FlagsEnum.ALL.getCode();
	locationFlag = FlagsEnum.ALL.getCode();
	period = null;
	vacationTypeId = FlagsEnum.ALL.getCode();
	getVacationTypesByEmpCategoryId();
    }

    public void selectEmployee() {
	try {
	    futureVacations = new ArrayList<TransientVacationTransactionData>();
	    currentEmployee = EmployeesService.getEmployeeData(currentEmployee.getEmpId());
	    getVacationTypesByEmpCategoryId();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void getVacationTypesByEmpCategoryId() {
	try {
	    vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteVacation(TransientVacationTransactionData deletedVacation) {

	try {
	    if (deletedVacation.getId() != null) {
		FutureVacationsService.deleteFutureVacation(deletedVacation.getTransientVacationTransaction());
	    }
	    futureVacations.remove(deletedVacation);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    private void validateGeneralManagerAdmins() throws BusinessException {
	this.setAdmin(SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_GENEAL_MANAGER_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_GENEAL_MANAGER.getCode()));

    }

    private void validateExternalEmployeesAdmins() throws BusinessException {
	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_EXTERNAL_EMPLOYEES_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_OFFICERS.getCode()))
	    categoriesIds.append(CategoriesEnum.OFFICERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_EXTERNAL_EMPLOYEES_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_SOLDIERS.getCode()))
	    categoriesIds.append(CategoriesEnum.SOLDIERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_EXTERNAL_EMPLOYEES_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_PERSONS.getCode()))
	    categoriesIds.append(CategoriesEnum.PERSONS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_EXTERNAL_EMPLOYEES_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_USERS.getCode()))
	    categoriesIds.append(CategoriesEnum.USERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_EXTERNAL_EMPLOYEES_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_WAGES.getCode()))
	    categoriesIds.append(CategoriesEnum.WAGES.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_EXTERNAL_EMPLOYEES_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_CONTRACTORS.getCode()))
	    categoriesIds.append(CategoriesEnum.CONTRACTORS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_FUTURE_VAC_EXTERNAL_EMPLOYEES_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_FUTURE_VACATIONS_EXTERNAL_MEDICAL_STAFF.getCode()))
	    categoriesIds.append(CategoriesEnum.MEDICAL_STAFF.getCode() + ",");

	if (categoriesIds.length() != 0 && categoriesIds.substring(categoriesIds.length() - 1).equals(","))
	    categoriesIds.delete(categoriesIds.length() - 1, categoriesIds.length());

	this.setAdmin(categoriesIds.length() != 0 ? true : false);
    }

    public List<TransientVacationTransactionData> getFutureVacations() {
	return futureVacations;
    }

    public void setFutureVacations(List<TransientVacationTransactionData> futureVacations) {
	this.futureVacations = futureVacations;
    }

    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    public Integer getRequestType() {
	return requestType;
    }

    public void setRequestType(Integer requestType) {
	this.requestType = requestType;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
    }

    public int getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(int approvedFlag) {
	this.approvedFlag = approvedFlag;
    }

    public Date getCurrentDate() {
	return currentDate;
    }

    public int getPageSize() {
	return pageSize;
    }

}
