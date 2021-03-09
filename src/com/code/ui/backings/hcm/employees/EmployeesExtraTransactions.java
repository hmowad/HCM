package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.RankTitle;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeExtraTransactionData;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesDataExtraTransactions")
@ViewScoped
public class EmployeesExtraTransactions extends BaseBacking implements Serializable {

    private int mode;
    private int rowsCount = 5;
    private Long regionId;
    private Long employeeId;
    private EmployeeData employee;

    private List<RankTitle> ranksTitles;
    private List<EmployeeExtraTransactionData> socialStatusList;
    private List<EmployeeExtraTransactionData> rankTitleList;
    private List<EmployeeExtraTransactionData> generalSpecList;

    public EmployeesExtraTransactions() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersDataExtraTransactions"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersDataExtraTransactions"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsDataExtraTransactions"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
		break;
	    }
	    ranksTitles = CommonService.getAllRanksTitles();
	    socialStatusList = new ArrayList<EmployeeExtraTransactionData>();
	    rankTitleList = new ArrayList<EmployeeExtraTransactionData>();
	    generalSpecList = new ArrayList<EmployeeExtraTransactionData>();
	    regionId = getLoginEmpPhysicalRegionFlag(true);
	    reset();
	}
    }

    public void reset() {
	employee = new EmployeeData();
	socialStatusList = new ArrayList<EmployeeExtraTransactionData>();
	rankTitleList = new ArrayList<EmployeeExtraTransactionData>();
	generalSpecList = new ArrayList<EmployeeExtraTransactionData>();
    }

    public void searchEmployee() {
	try {
	    reset();
	    employee = EmployeesService.getEmployeeData(employeeId);
	    socialStatusList = EmployeesService.getEmployeeDataExtraTransactionByEmpIdAndTransactionType(employeeId, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SOCIAL_STATUS.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId());
	    rankTitleList = EmployeesService.getEmployeeDataExtraTransactionByEmpIdAndTransactionType(employeeId, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_RANK_TITLE.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId());
	    generalSpecList = EmployeesService.getEmployeeDataExtraTransactionByEmpIdAndTransactionType(employeeId, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_GENERAL_SPECIALIZATION.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId());
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

    public void saveNewEmployeeDataExtraTransaction(int transactionTypeCode,
	    EmployeeExtraTransactionData employeeExtraTransactionData, int index) {
	try {
	    employeeExtraTransactionData.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(transactionTypeCode, TransactionClassesEnum.EMPLOYEES.getCode()).getId());
	    EmployeesService.addEmployeeDataExtraTransaction(employee, employeeExtraTransactionData, null);
	    if (transactionTypeCode == TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_GENERAL_SPECIALIZATION.getCode())
		generalSpecList.set(index, employeeExtraTransactionData);
	    else if (transactionTypeCode == TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_RANK_TITLE.getCode())
		rankTitleList.set(index, employeeExtraTransactionData);
	    else if (transactionTypeCode == TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SOCIAL_STATUS.getCode())
		socialStatusList.set(index, employeeExtraTransactionData);
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

    public List<RankTitle> getRanksTitles() {
	return ranksTitles;
    }

    public void setRanksTitles(List<RankTitle> ranksTitles) {
	this.ranksTitles = ranksTitles;
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

}
