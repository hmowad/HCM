package com.code.ui.backings.hcm.vacations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.TransientVacationTransactionData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.FutureVacationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "futureVacationsManagement")
@ViewScoped
public class FutureVacationsManagement extends FutureVacationBase {
    // private EmployeeData currentEmployee;
    private long vacationTypeId;
    // private List<VacationType> vacTypeList;
    private Date fromDate;
    private Date toDate;
    private Integer period;
    private Integer requestType;
    private String decisionNumber;
    private Integer locationFlag;
    private Integer approvedFlag;
    // screenMode= 1 open General Manager Screen
    // screenMode= 2 open External Employees Screen
    // int screenMode;
    private Date currentDate;
    private int pageSize = 5;

    private List<TransientVacationTransactionData> futureVacations;

    public FutureVacationsManagement() {
	super.init();
	resetForm();
	try {
	    currentDate = HijriDateService.getHijriSysDate();
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
	if (this.getRequest().getParameter("mode") != null)
	    screenMode = Integer.parseInt(this.getRequest().getParameter("mode"));
	switch (screenMode) {
	case 1:
	    this.setScreenTitle(this.getMessage("title_generalManagerVacationsManagement"));
	    break;
	case 2:
	    this.setScreenTitle(this.getMessage("title_externalEmployeesVacationsManagement"));
	    break;
	default:
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }

    public void searchFutureVacations() {
	try {
	    futureVacations = FutureVacationsService.getFutureVacations(currentEmployee, decisionNumber, requestType, vacationTypeId, approvedFlag, fromDate, toDate, period, locationFlag);
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
