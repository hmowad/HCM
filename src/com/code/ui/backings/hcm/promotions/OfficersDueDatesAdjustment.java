package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "officersDueDatesAdjustment")
@ViewScoped
public class OfficersDueDatesAdjustment extends BaseBacking {

    private String empId;
    private boolean saveFlag;
    private PromotionTransactionData promotionTransaction;
    private List<PromotionTransactionData> promotionTransactionDataList;
    private EmployeeData employee;

    private final int pageSize = 10;

    public OfficersDueDatesAdjustment() {
	resetForm();
	try {
	    promotionTransaction.setDecisionDate(HijriDateService.getHijriSysDate());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void resetForm() {
	try {
	    empId = null;
	    employee = new EmployeeData();
	    promotionTransaction = new PromotionTransactionData();
	    promotionTransaction.setDecisionDate(HijriDateService.getHijriSysDate());
	    promotionTransaction.setNewDueDate(HijriDateService.getHijriSysDate());
	    promotionTransactionDataList = new ArrayList<PromotionTransactionData>();
	    saveFlag = false;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchOfficerData() {

	try {
	    if (empId != null && !empId.equals("")) {
		employee = EmployeesService.getEmployeeData(Long.parseLong(empId));

		if (employee.getStatusId().equals(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode()))
		    throw new BusinessException("error_officersPromotionAdjustmentRankNotValid");
		promotionTransaction = new PromotionTransactionData();
		promotionTransaction.setDecisionDate(HijriDateService.getHijriSysDate());
		promotionTransaction.setNewDueDate(employee.getPromotionDueDate() == null ? HijriDateService.getHijriSysDate() : employee.getPromotionDueDate());
		saveFlag = true;
		promotionTransactionDataList = PromotionsService.getPromotionsHistory(employee.getEmpId(), FlagsEnum.ALL.getCode());
	    }

	} catch (BusinessException ee) {
	    resetForm();
	    this.setServerSideErrorMessages(getMessage(ee.getMessage()));
	}
    }

    public void addPromotionTransactionData() {
	try {
	    PromotionsService.doPromotionAdjustmentTransaction(promotionTransaction, promotionTransactionDataList, employee, this.loginEmpData.getEmpId());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    promotionTransactionDataList = PromotionsService.getPromotionsHistory((empId == null || empId.equals("")) ? FlagsEnum.ALL.getCode() : Long.parseLong(empId), FlagsEnum.ALL.getCode());
	    saveFlag = false;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
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
