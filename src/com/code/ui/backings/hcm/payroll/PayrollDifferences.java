package com.code.ui.backings.hcm.payroll;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.payroll.SummaryDifferenceData;
import com.code.dal.orm.hcm.payroll.SummaryDifferenceDetailData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PayrollsService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "payrollDifferences")
@ViewScoped
public class PayrollDifferences extends BaseBacking implements Serializable {

    private List<SummaryDifferenceData> summaryDifferencesList;
    private String summaryCode;
    private SummaryDifferenceData selectedSummaryPayrollDiff;
    private List<SummaryDifferenceDetailData> summaryDifferenceDetailDataList;

    private int pageSize = 10;
    private int mode = 0;

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
	summaryCode = "";
	search();
    }

    public void search() {
	selectedSummaryPayrollDiff = null;
	summaryDifferenceDetailDataList = null;
	getSelectedDifferencesForLoginEmployee();
    }

    public void getSelectedDifferencesForLoginEmployee() {
	try {
	    summaryDifferencesList = PayrollsService.getSummaryPayrollDifferencesBySummaryCodeAndEmployeeIdAndOrderStatus(summaryCode, this.loginEmpData.getEmpId(), mode);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectSummaryPayrollDiff(SummaryDifferenceData summaryDifferenceData) {
	try {
	    selectedSummaryPayrollDiff = summaryDifferenceData;
	    if (summaryDifferenceData.getCode() != null && !summaryDifferenceData.getCode().isEmpty())
		summaryDifferenceDetailDataList = PayrollsService.getSummaryDifferenceDetailDataBySummaryCodeAndEmployeeId(summaryDifferenceData.getCode(), this.loginEmpData.getEmpId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));

	    selectedSummaryPayrollDiff = null;
	    summaryDifferenceDetailDataList = null;
	}
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public String getSummaryCode() {
	return summaryCode;
    }

    public void setSummaryCode(String summaryCode) {
	this.summaryCode = summaryCode;
    }

    public List<SummaryDifferenceData> getSummaryDifferencesList() {
	return summaryDifferencesList;
    }

    public SummaryDifferenceData getSelectedSummaryPayrollDiff() {
	return selectedSummaryPayrollDiff;
    }

    public void setSelectedSummaryPayrollDiff(SummaryDifferenceData selectedSummaryPayrollDiff) {
	this.selectedSummaryPayrollDiff = selectedSummaryPayrollDiff;
    }

    public List<SummaryDifferenceDetailData> getSummaryDifferenceDetailDataList() {
	return summaryDifferenceDetailDataList;
    }

    public void setSummaryDifferenceDetailDataList(List<SummaryDifferenceDetailData> summaryDifferenceDetailDataList) {
	this.summaryDifferenceDetailDataList = summaryDifferenceDetailDataList;
    }

    public int getMode() {
	return mode;
    }
}
