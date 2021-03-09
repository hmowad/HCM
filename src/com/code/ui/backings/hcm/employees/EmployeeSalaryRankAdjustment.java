package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeExtraTransactionData;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.enums.CategoriesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeeSalaryRankAdjustment")
@ViewScoped
public class EmployeeSalaryRankAdjustment extends BaseBacking implements Serializable {

    private int mode;
    private int rowsCount = 5;
    private Long regionId;
    private Long employeeId;
    private EmployeeData employee;

    private List<Rank> salaryRanks;
    private List<Degree> salaryDegrees;
    private List<EmployeeExtraTransactionData> salaryRankList;

    public EmployeeSalaryRankAdjustment() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersSalaryRankAdjustment"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersSalaryRankAdjustment"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_civilliansSalaryRankAdjustment"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
		break;
	    }

	    salaryRankList = new ArrayList<EmployeeExtraTransactionData>();
	    regionId = getLoginEmpPhysicalRegionFlag(true);
	    reset();
	}
    }

    public void reset() {
	employee = new EmployeeData();
	salaryRankList = new ArrayList<EmployeeExtraTransactionData>();
    }

    public void searchEmployee() {
	try {
	    reset();
	    employee = EmployeesService.getEmployeeData(employeeId);
	    salaryRanks = CommonService.getRanks(null, new Long[] { EmployeesService.getEmployeeMedicalStaffDataByEmpId(employee.getEmpId()) != null ? CategoriesEnum.MEDICAL_STAFF.getCode() : employee.getCategoryId() });
	    salaryDegrees = PayrollsService.getAllDegrees();
	    salaryRankList = EmployeesService.getEmployeeDataExtraTransactionByEmpIdAndTransactionType(employeeId, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SALARY_RANK.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    e.printStackTrace();
	}
    }

    public void addNewEmployeeDataExtraTransaction(List<EmployeeExtraTransactionData> empDataExtraTransactionList) {
	EmployeeExtraTransactionData newEmpDataExtraTransaction = new EmployeeExtraTransactionData();
	newEmpDataExtraTransaction.setEmpId(employee.getEmpId());
	empDataExtraTransactionList.add(0, newEmpDataExtraTransaction);
    }

    public void saveNewEmployeeDataExtraTransaction(EmployeeExtraTransactionData employeeExtraTransactionData, int index) {
	try {
	    employeeExtraTransactionData.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SALARY_RANK.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId());
	    EmployeesService.addEmployeeDataExtraTransaction(employee, employeeExtraTransactionData, null);
	    salaryRankList.set(index, employeeExtraTransactionData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void stopSalaryRankTransaction(int index) {
	try {
	    EmployeesService.stopEmployeeDataExtraTransaction(salaryRankList.get(index));
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public List<Rank> getSalaryRanks() {
	return salaryRanks;
    }

    public void setSalaryRanks(List<Rank> salaryRanks) {
	this.salaryRanks = salaryRanks;
    }

    public List<Degree> getSalaryDegrees() {
	return salaryDegrees;
    }

    public void setSalaryDegrees(List<Degree> salaryDegrees) {
	this.salaryDegrees = salaryDegrees;
    }

    public List<EmployeeExtraTransactionData> getSalaryRankList() {
	return salaryRankList;
    }

    public void setSalaryRankList(List<EmployeeExtraTransactionData> salaryRankList) {
	this.salaryRankList = salaryRankList;
    }

}
