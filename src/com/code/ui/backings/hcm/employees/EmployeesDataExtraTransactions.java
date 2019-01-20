package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.RankTitle;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeExtraTransactionData;
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
    private List<EmployeeExtraTransactionData> socialStatusList;
    private List<EmployeeExtraTransactionData> rankTitleList;
    private List<EmployeeExtraTransactionData> generalSpecList;
    private List<EmployeeExtraTransactionData> salaryRankList;

    public EmployeesDataExtraTransactions() {
	setScreenTitle(getMessage("title_employeesDataExtraTransactions"));
	ranksTitles = CommonService.getAllRanksTitles();
	socialStatusList = new ArrayList<EmployeeExtraTransactionData>();
	rankTitleList = new ArrayList<EmployeeExtraTransactionData>();
	generalSpecList = new ArrayList<EmployeeExtraTransactionData>();
	salaryRankList = new ArrayList<EmployeeExtraTransactionData>();
	reset();
    }

    public void reset() {
	employee = new EmployeeData();
	socialStatusList = new ArrayList<EmployeeExtraTransactionData>();
	rankTitleList = new ArrayList<EmployeeExtraTransactionData>();
	generalSpecList = new ArrayList<EmployeeExtraTransactionData>();
	salaryRankList = new ArrayList<EmployeeExtraTransactionData>();
    }

    public void searchEmployee() {
	try {
	    reset();
	    employee = EmployeesService.getEmployeeData(employeeId);
	    salaryRanks = CommonService.getRanks(null, new Long[] { employee.getCategoryId() });
	    salaryDegrees = PayrollsService.getAllDegrees();
	    EmployeesService.getEmployeeDataExtraTransactionLists(employeeId, socialStatusList, rankTitleList, generalSpecList, salaryRankList);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    e.printStackTrace();
	}
    }

    public void addNewEmployeeDataExtraTransaction(List<EmployeeExtraTransactionData> empDataExtraTransactionList) {
	EmployeeExtraTransactionData newEmpDataExtraTransaction = new EmployeeExtraTransactionData();
	empDataExtraTransactionList.add(0, newEmpDataExtraTransaction);
    }

    public void saveNewEmployeeDataExtraTransaction(List<EmployeeExtraTransactionData> employeeDataExtraTransactionList,
	    EmployeeExtraTransactionData employeeExtraTransactionData, int index) {
	try {
	    EmployeesService.addEmployeeDataExtraTransaction(employee, employeeExtraTransactionData);
	    employeeDataExtraTransactionList.set(index, EmployeesService.getEmployeeExtraTransactionByDecisionNumber(employeeExtraTransactionData.getDecisionNumber()).get(0));
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

    public List<EmployeeExtraTransactionData> getSocialStatusList() {
	return socialStatusList;
    }

    public void setSocialStatusList(List<EmployeeExtraTransactionData> socialStatusList) {
	this.socialStatusList = socialStatusList;
    }

    public List<EmployeeExtraTransactionData> getRankTitleList() {
	return rankTitleList;
    }

    public void setRankTitleList(List<EmployeeExtraTransactionData> rankTitleList) {
	this.rankTitleList = rankTitleList;
    }

    public List<EmployeeExtraTransactionData> getGeneralSpecList() {
	return generalSpecList;
    }

    public void setGeneralSpecList(List<EmployeeExtraTransactionData> generalSpecList) {
	this.generalSpecList = generalSpecList;
    }

    public List<EmployeeExtraTransactionData> getSalaryRankList() {
	return salaryRankList;
    }

    public void setSalaryRankList(List<EmployeeExtraTransactionData> salaryRankList) {
	this.salaryRankList = salaryRankList;
    }

}
