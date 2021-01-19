package com.code.ui.backings.hcm.payroll;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.payroll.EmployeePayrollDifferenceData;
import com.code.dal.orm.integration.finance.PaidIssueOrderData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PayrollsService;
import com.code.services.integration.FinanceService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "payrollDifferences")
@ViewScoped
public class PayrollDifferences extends BaseBacking implements Serializable {

    private List<EmployeePayrollDifferenceData> payrollDifferencesList;
    private String elementDescription;
    private float amountSum = 0;

    private int pageSize = 10;
    private int mode = 0;

    private EmployeePayrollDifferenceData selectedPayrollDiff;
    private PaidIssueOrderData issueOrderData;
    private final static String EMPTY_CELL_STRING = "- -";

    public PayrollDifferences() {
	mode = Integer.valueOf(getRequest().getParameter("mode"));
	if (mode == FlagsEnum.OFF.getCode()) {
	    super.setScreenTitle(getMessage("title_currentEmployeePayrollDifferences"));
	} else if (mode == FlagsEnum.ON.getCode()) {
	    super.setScreenTitle(getMessage("title_paidEmployeePayrollDifferences"));
	}
	resetForm();
    }

    public void resetForm() {
	elementDescription = "";
	search();
    }

    public void search() {
	try {
	    payrollDifferencesList = PayrollsService.getEmployeePayrollDifferences(this.loginEmpData.getEmpId(), mode, elementDescription);
	    calculateAmountSum();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

	selectedPayrollDiff = null;
	issueOrderData = null;
    }

    private void calculateAmountSum() {
	amountSum = 0;
	for (EmployeePayrollDifferenceData payrollDifferences : payrollDifferencesList) {
	    amountSum += payrollDifferences.getTotalAmount() == null ? 0 : payrollDifferences.getTotalAmount();
	}
    }

    public void selectPayrollDiff(EmployeePayrollDifferenceData payrollDiff) {
	try {
	    selectedPayrollDiff = payrollDiff;
	    if (payrollDiff.getSummarySerial() != null)
		issueOrderData = FinanceService.getPaidIssueOrder(payrollDiff.getSummarySerial());
	    
	    if (payrollDiff.getSummarySerial() == null || issueOrderData == null)
		issueOrderData = new PaidIssueOrderData();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    
	    selectedPayrollDiff = null;
	    issueOrderData = null;
	}
    }

    public List<EmployeePayrollDifferenceData> getPayrollDifferencesList() {
	return payrollDifferencesList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public double getAmountSum() {
	DecimalFormat twoDForm = new DecimalFormat("#.##");
	return Double.valueOf(twoDForm.format(amountSum));
    }

    public void setAmountSum(float amountSum) {
	this.amountSum = amountSum;
    }

    public EmployeePayrollDifferenceData getSelectedPayrollDiff() {
	return selectedPayrollDiff;
    }

    public void setSelectedPayrollDiff(EmployeePayrollDifferenceData selectedPayrollDiff) {
	this.selectedPayrollDiff = selectedPayrollDiff;
    }

    public PaidIssueOrderData getIssueOrderData() {
	return issueOrderData;
    }

    public void setIssueOrderData(PaidIssueOrderData issueOrderData) {
	this.issueOrderData = issueOrderData;
    }

    public String getElementDescription() {
	return elementDescription;
    }

    public void setElementDescription(String elementDescription) {
	this.elementDescription = elementDescription;
    }

    public int getMode() {
	return mode;
    }

    public String getEmptyCellString() {
	return EMPTY_CELL_STRING;
    }
}
