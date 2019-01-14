package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.RankTitle;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeDataExtraTransactionData;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesDataExtraTransactions")
@ViewScoped
public class EmployeesDataExtraTransactions extends BaseBacking implements Serializable {

    private int rowsCount = 5;
    private Long employeeId;
    private EmployeeData employee;

    private List<RankTitle> ranksTitles;
    private List<Rank> salaryRanks;
    private List<Degree> salaryDegrees;
    private List<EmployeeDataExtraTransactionData> socialStatusList;
    private List<EmployeeDataExtraTransactionData> rankTitleList;
    private List<EmployeeDataExtraTransactionData> generalSpecList;
    private List<EmployeeDataExtraTransactionData> salaryRankList;

    public EmployeesDataExtraTransactions() {
	setScreenTitle(getMessage("title_employeesDataExtraTransactions"));
	ranksTitles = CommonService.getAllRanksTitles();
	socialStatusList = new ArrayList<EmployeeDataExtraTransactionData>();
	rankTitleList = new ArrayList<EmployeeDataExtraTransactionData>();
	generalSpecList = new ArrayList<EmployeeDataExtraTransactionData>();
	salaryRankList = new ArrayList<EmployeeDataExtraTransactionData>();
	reset();
    }

    public void reset() {
	employee = new EmployeeData();
    }

    public void searchEmployee() {
	try {
	    employee = EmployeesService.getEmployeeData(employeeId);
	    salaryRanks = CommonService.getRanks(null, new Long[] { employee.getCategoryId() });
	    salaryDegrees = PayrollsService.getAllDegrees();
	    EmployeesService.getEmployeeDataExtraTransactionLists(employeeId, socialStatusList, rankTitleList, generalSpecList, salaryRankList);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    e.printStackTrace();
	}
    }

    public void addNewEmployeeDataExtraTransaction(List<EmployeeDataExtraTransactionData> empDataExtraTransactionList) {
	EmployeeDataExtraTransactionData newEmpDataExtraTransaction = new EmployeeDataExtraTransactionData();
	empDataExtraTransactionList.add(0, newEmpDataExtraTransaction);
    }

    public void saveNewEmployeeDataExtraTransaction(EmployeeDataExtraTransactionData employeeDataExtraTransactionData) {
	try {
	    EmployeesService.addEmployeeDataExtraTransactions(employee, employeeDataExtraTransactionData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    e.printStackTrace();
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
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

    public List<RankTitle> getRanksTitles() {
	return ranksTitles;
    }

    public void setRanksTitles(List<RankTitle> ranksTitles) {
	this.ranksTitles = ranksTitles;
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

    public List<EmployeeDataExtraTransactionData> getSocialStatusList() {
	return socialStatusList;
    }

    public void setSocialStatusList(List<EmployeeDataExtraTransactionData> socialStatusList) {
	this.socialStatusList = socialStatusList;
    }

    public List<EmployeeDataExtraTransactionData> getRankTitleList() {
	return rankTitleList;
    }

    public void setRankTitleList(List<EmployeeDataExtraTransactionData> rankTitleList) {
	this.rankTitleList = rankTitleList;
    }

    public List<EmployeeDataExtraTransactionData> getGeneralSpecList() {
	return generalSpecList;
    }

    public void setGeneralSpecList(List<EmployeeDataExtraTransactionData> generalSpecList) {
	this.generalSpecList = generalSpecList;
    }

    public List<EmployeeDataExtraTransactionData> getSalaryRankList() {
	return salaryRankList;
    }

    public void setSalaryRankList(List<EmployeeDataExtraTransactionData> salaryRankList) {
	this.salaryRankList = salaryRankList;
    }

}
