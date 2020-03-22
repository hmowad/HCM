package com.code.ui.backings.hcm.payroll;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.integration.payroll.PayrollIntegrationFailureLogData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "nonSentAdminDecisionsManagement")
@ViewScoped
public class NonSentAdminDecisionsManagement extends BaseBacking {

    private long categoryId;
    private long adminDecisionId;
    private String decisionNumber;
    private Date decisionDate;

    private List<Category> categories;

    private List<PayrollIntegrationFailureLogData> payrollIntegrationFailureLogDataList;

    private int rowsCount = 10;

    public NonSentAdminDecisionsManagement() {
	try {
	    categories = CommonService.getAllCategories();
	    resetForm();
	} catch (Exception e) {
	    setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void resendTransaction(PayrollIntegrationFailureLogData payrollIntegrationFailureLogData) {
	try {
	    PayrollEngineService.resendFailedTransaction(payrollIntegrationFailureLogData);
	    setServerSideSuccessMessages(getMessage("notify_adminDecisionSentSuccessfully"));
	} catch (BusinessException e) {
	    if (e.getParams().length > 0)
		setServerSideErrorMessages(e.getMessage());
	    else
		setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void search() {
	try {
	    payrollIntegrationFailureLogDataList = PayrollEngineService.getPayrollIntegrationFailureLog(categoryId, adminDecisionId, decisionNumber, HijriDateService.getHijriDateString(decisionDate));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void resetForm() throws BusinessException {
	categoryId = FlagsEnum.ALL.getCode();
	adminDecisionId = FlagsEnum.ALL.getCode();
	decisionNumber = "";
	decisionDate = null;
	search();
    }

    public long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(long categoryId) {
	this.categoryId = categoryId;
    }

    public long getAdminDecisionId() {
	return adminDecisionId;
    }

    public void setAdminDecisionId(long adminDecisionId) {
	this.adminDecisionId = adminDecisionId;
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

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public List<PayrollIntegrationFailureLogData> getPayrollIntegrationFailureLogDataList() {
	return payrollIntegrationFailureLogDataList;
    }

    public void setPayrollIntegrationFailureLogDataList(List<PayrollIntegrationFailureLogData> payrollIntegrationFailureLogDataList) {
	this.payrollIntegrationFailureLogDataList = payrollIntegrationFailureLogDataList;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

}
