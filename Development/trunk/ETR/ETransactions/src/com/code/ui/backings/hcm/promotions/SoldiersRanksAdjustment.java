package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "soldiersRanksAdjustment")
@ViewScoped
public class SoldiersRanksAdjustment extends BaseBacking {

    private String empId;
    private EmployeeData employee;

    private PromotionTransactionData promotionTransaction;
    private List<PromotionTransactionData> promotionTransactionDataList;

    private boolean saveFlag;
    private long previousRankId;
    private final int pageSize = 10;

    public SoldiersRanksAdjustment() {
	resetForm();
    }

    public void resetForm() {
	try {
	    empId = null;
	    employee = new EmployeeData();
	    promotionTransaction = new PromotionTransactionData();
	    promotionTransaction.setDecisionDate(HijriDateService.getHijriSysDate());
	    promotionTransaction.setNewLastPromotionDate(null);
	    promotionTransactionDataList = new ArrayList<PromotionTransactionData>();
	    previousRankId = FlagsEnum.ALL.getCode();
	    saveFlag = false;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchSoldierData() {

	try {
	    if (empId != null && !empId.equals("")) {
		employee = EmployeesService.getEmployeeData(Long.parseLong(empId));
		promotionTransaction = new PromotionTransactionData();
		promotionTransaction.setPromotionTypeId(PromotionsTypesEnum.RANK_REDUCE.getCode());
		promotionTransaction.setDecisionDate(HijriDateService.getHijriSysDate());
		promotionTransaction.setNewLastPromotionDate(null);
		saveFlag = true;
		previousRankId = PromotionsService.getPreviousRank(employee.getRankId());
		promotionTransactionDataList = PromotionsService.getPromotionsHistory(employee.getEmpId(), PromotionsTypesEnum.RANK_REDUCE.getCode());
	    }

	} catch (BusinessException e) {
	    resetForm();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addPromotionTransaction() {
	try {
	    PromotionsService.doPromotionAdjustmentTransaction(promotionTransaction, promotionTransactionDataList, employee, this.loginEmpData.getEmpId());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    
	    promotionTransactionDataList = PromotionsService.getPromotionsHistory((empId == null || empId.equals("")) ? FlagsEnum.ALL.getCode() : Long.parseLong(empId), PromotionsTypesEnum.RANK_REDUCE.getCode());
	    saveFlag = false;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public long getPreviousRankId() {
	return previousRankId;
    }

    public void setPreviousRankId(long previousRankId) {
	this.previousRankId = previousRankId;
    }

    public String getEmpId() {
	return empId;
    }

    public void setEmpId(String empId) {
	this.empId = empId;
    }

    public PromotionTransactionData getPromotionTransaction() {
	return promotionTransaction;
    }

    public List<PromotionTransactionData> getPromotionTransactionDataList() {
	return promotionTransactionDataList;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public int getPageSize() {
	return pageSize;
    }

    public boolean isSaveFlag() {
	return saveFlag;
    }

    public void setSaveFlag(boolean saveFlag) {
	this.saveFlag = saveFlag;
    }
}
