package com.code.ui.backings.minisearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.HistoricalVacationTransactionData;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.enums.FlagsEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.HistoricalVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "historicalVacationsMiniSearch")
@ViewScoped
public class HistoricalVacationsMiniSearch extends BaseBacking {

    private long vacationTypeId;
    private List<VacationType> vacTypeList;
    private Integer period;
    private String decisionNumber;
    private Date decisionDate;
    private Date fromDate;
    private Date toDate;
    private int vacationLocationFlag;
    private String countryName;
    // 0 : search for modify
    // 1 : search for cancelation
    private int searchMode;

    private long categoryMode;
    private long employeeId;

    private int rowsCount = 10;

    private List<HistoricalVacationTransactionData> searchHistoricalVacationsList;

    public HistoricalVacationsMiniSearch() {
	if (this.getRequest().getParameter("categoryMode") != null)
	    categoryMode = Long.parseLong(this.getRequest().getParameter("categoryMode"));
	if (this.getRequest().getParameter("searchMode") != null)
	    searchMode = Integer.parseInt(this.getRequest().getParameter("searchMode"));
	if (this.getRequest().getParameter("empId") != null)
	    employeeId = Long.parseLong(this.getRequest().getParameter("empId"));
	vacationLocationFlag = FlagsEnum.ALL.getCode();
	getVacationTypesByEmpCategoryId();
    }

    private void getVacationTypesByEmpCategoryId() {
	try {
	    vacTypeList = VacationsService.getVacationTypes(categoryMode);
	    if (searchMode == 0) {
		Iterator<VacationType> iter = vacTypeList.iterator();
		while (iter.hasNext()) {
		    if (iter.next().getVacationTypeId() != VacationTypesEnum.REGULAR.getCode() || iter.next().getVacationTypeId() != VacationTypesEnum.SICK.getCode()) {
			iter.remove();
		    }

		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchHistoricalVacations() {

	try {
	    EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	    searchHistoricalVacationsList = new ArrayList<HistoricalVacationTransactionData>();
	    if (searchMode == 0) {
		if (vacationTypeId == FlagsEnum.ALL.getCode())
		    searchHistoricalVacationsList = HistoricalVacationsService.searchHistoricalVacations(employee, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode(), FlagsEnum.ON.getCode(), FlagsEnum.ALL.getCode(), new Integer[] { FlagsEnum.ON.getCode(), FlagsEnum.OFF.getCode() },
			    decisionNumber, decisionDate == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode(), decisionDate, FlagsEnum.ON.getCode(),
			    new Long[] { VacationTypesEnum.REGULAR.getCode(), VacationTypesEnum.SICK.getCode() }, fromDate, toDate, period,
			    FlagsEnum.ALL.getCode(), vacationLocationFlag, countryName, FlagsEnum.ON.getCode());
		else
		    searchHistoricalVacationsList = HistoricalVacationsService.searchHistoricalVacations(employee, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode(), FlagsEnum.ON.getCode(), FlagsEnum.ALL.getCode(), new Integer[] { FlagsEnum.ON.getCode(), FlagsEnum.OFF.getCode() },
			    decisionNumber, decisionDate == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode(), decisionDate, vacationTypeId,
			    new Long[] { vacationTypeId }, fromDate, toDate, period,
			    FlagsEnum.ALL.getCode(), vacationLocationFlag, countryName, FlagsEnum.ON.getCode());
	    } else {

		searchHistoricalVacationsList = HistoricalVacationsService.searchHistoricalVacations(employee, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode(), FlagsEnum.ON.getCode(), FlagsEnum.ON.getCode(), new Integer[] { FlagsEnum.ON.getCode() },
			decisionNumber, decisionDate == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode(), decisionDate, vacationTypeId,
			new Long[] { vacationTypeId }, fromDate, toDate, period,
			FlagsEnum.ALL.getCode(), vacationLocationFlag, countryName, FlagsEnum.ON.getCode());

	    }
	    if (searchHistoricalVacationsList == null || searchHistoricalVacationsList.isEmpty())
		throw new BusinessException("error_noVacationMatchThisCriteria");
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public List<HistoricalVacationTransactionData> getSearchHistoricalVacationsList() {
	return searchHistoricalVacationsList;
    }

    public void setSearchHistoricalVacationsList(List<HistoricalVacationTransactionData> searchHistoricalVacationsList) {
	this.searchHistoricalVacationsList = searchHistoricalVacationsList;
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

    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
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

    public int getVacationLocationFlag() {
	return vacationLocationFlag;
    }

    public void setVacationLocationFlag(int vacationLocationFlag) {
	this.vacationLocationFlag = vacationLocationFlag;
    }

    public String getCountryName() {
	return countryName;
    }

    public void setCountryName(String countryName) {
	this.countryName = countryName;
    }

    public int getSearchMode() {
	return searchMode;
    }

    public void setSearchMode(int searchMode) {
	this.searchMode = searchMode;
    }

    public long getCategoryMode() {
	return categoryMode;
    }

    public long getEmployeeId() {
	return employeeId;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

}
