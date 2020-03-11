package com.code.ui.backings.hcm.vacations;

import java.util.List;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.TransientVacationTransaction;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.VacationsService;
import com.code.ui.backings.base.BaseBacking;

public class FutureVacationBase extends BaseBacking {

    protected EmployeeData currentEmployee;
    protected TransientVacationTransaction futureVacation;
    protected String balance;
    protected List<VacationType> vacTypeList;
    // screenMode= 1 open General Manager Screen
    // screenMode= 2 open External Employees Screen
    protected int screenMode;
    // viewMode= 0 open the page from menu
    // viewMode =1 opened as detail from management page
    // viewMode = 2 opened as edit from management page
    protected int viewMode;
    protected Long vacationId;
    protected Long empId;

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
		// futureVacation = FutureVacationsService.getTransientVacationTransactionById(vacationId);
		vacTypeList = VacationsService.getVacationTypes(currentEmployee.getEmpId() == null ? FlagsEnum.ALL.getCode() : currentEmployee.getCategoryId());
	    } else {
		currentEmployee = new EmployeeData();
		viewMode = 0;
		futureVacation = new TransientVacationTransaction();
		futureVacation.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		futureVacation.setApprovedFlag(FlagsEnum.OFF.getCode());
		futureVacation.setLocation(getMessage("label_ksa"));
		futureVacation.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }

    public EmployeeData getCurrentEmployee() {
	return currentEmployee;
    }

    public void setCurrentEmployee(EmployeeData currentEmployee) {
	this.currentEmployee = currentEmployee;
    }

    public TransientVacationTransaction getFutureVacation() {
	return futureVacation;
    }

    public void setFutureVacation(TransientVacationTransaction futureVacation) {
	this.futureVacation = futureVacation;
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

    public Long getVacationId() {
	return vacationId;
    }

    public void setVacationId(Long vacationId) {
	this.vacationId = vacationId;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

}
