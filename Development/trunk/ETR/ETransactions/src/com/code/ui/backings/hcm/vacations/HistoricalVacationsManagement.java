package com.code.ui.backings.hcm.vacations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.HistoricalVacationTransactionData;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.HistoricalVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "historicalVacationsManagement")
@ViewScoped
public class HistoricalVacationsManagement extends BaseBacking {
    private EmployeeData currentEmployee;
    private long vacationTypeId;
    private List<VacationType> vacTypeList;
    private Date fromDate;
    private Date toDate;
    private Integer period;
    private Integer requestType;
    private String decisionNumber;
    private Integer locationFlag;
    private Integer approvedFlag;
    private StringBuilder categoriesIds;
    private boolean isAdmin;
    private int pageSize = 5;

    private List<HistoricalVacationTransactionData> historicalVacationData;

    public HistoricalVacationsManagement() {
	try {
	    super.init();
	    categoriesIds = new StringBuilder();
	    reset();
	    validateEmployeesAdmins();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void validateEmployeesAdmins() throws BusinessException {
	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_HISTORICAL_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_OFFICERS.getCode()))
	    categoriesIds.append(CategoriesEnum.OFFICERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_HISTORICAL_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_SOLDIERS.getCode()))
	    categoriesIds.append(CategoriesEnum.SOLDIERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_HISTORICAL_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_PERSONS.getCode()))
	    categoriesIds.append(CategoriesEnum.PERSONS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_HISTORICAL_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_USERS.getCode()))
	    categoriesIds.append(CategoriesEnum.USERS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_HISTORICAL_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_WAGES.getCode()))
	    categoriesIds.append(CategoriesEnum.WAGES.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_HISTORICAL_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_CONTRACTORS.getCode()))
	    categoriesIds.append(CategoriesEnum.CONTRACTORS.getCode() + ",");

	if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.VAC_HISTORICAL_VAC_HISTORICAL_VACATION_MANAGEMENT.getCode(), MenuActionsEnum.VAC_HISTORICAL_VACATIONS_MEDICAL_STAFF.getCode()))
	    categoriesIds.append(CategoriesEnum.MEDICAL_STAFF.getCode() + ",");

	if (categoriesIds.length() != 0 && categoriesIds.substring(categoriesIds.length() - 1).equals(","))
	    categoriesIds.delete(categoriesIds.length() - 1, categoriesIds.length());

	this.setAdmin(categoriesIds.length() != 0 ? true : false);
    }

    public void searchHistoricalVacations() {
	try {
	    historicalVacationData = HistoricalVacationsService.searchHistoricalVacations(currentEmployee, requestType, requestType, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), new Integer[] { FlagsEnum.ALL.getCode() }, decisionNumber, FlagsEnum.ALL.getCode(), HijriDateService.getHijriSysDate(), vacationTypeId, new Long[] { vacationTypeId }, fromDate, toDate, period, approvedFlag, locationFlag, "", FlagsEnum.ALL.getCode());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	historicalVacationData = new ArrayList<HistoricalVacationTransactionData>();
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
	    historicalVacationData = new ArrayList<HistoricalVacationTransactionData>();
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

    public void deleteVacation(HistoricalVacationTransactionData deletedVacation) {

	try {
	    if (deletedVacation.getId() != null) {
		HistoricalVacationsService.deleteHistoricalVacationTransaction(deletedVacation.getHistoricalVacationTransaction());
	    }
	    historicalVacationData.remove(deletedVacation);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public List<HistoricalVacationTransactionData> getHistoricalVacationData() {
	return historicalVacationData;
    }

    public void setHistoricalVacationData(List<HistoricalVacationTransactionData> historicalVacationData) {
	this.historicalVacationData = historicalVacationData;
    }

    public EmployeeData getCurrentEmployee() {
	return currentEmployee;
    }

    public void setCurrentEmployee(EmployeeData currentEmployee) {
	this.currentEmployee = currentEmployee;
    }

    public long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    public List<VacationType> getVacTypeList() {
	return vacTypeList;
    }

    public void setVacTypeList(List<VacationType> vacTypeList) {
	this.vacTypeList = vacTypeList;
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

    public StringBuilder getCategoriesIds() {
	return categoriesIds;
    }

    public void setCategoriesIds(StringBuilder categoriesIds) {
	this.categoriesIds = categoriesIds;
    }

    public boolean isAdmin() {
	return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
	this.isAdmin = isAdmin;
    }

    public int getPageSize() {
	return pageSize;
    }

}
