package com.code.ui.backings.transactionstimeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.TransactionTimeline;
import com.code.dal.orm.hcm.employees.EmployeePreferences;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesPreferencesService;
import com.code.services.hcm.TransactionsTimelineService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "transactionsTimeline")
@ViewScoped
public class TransactionsTimeline extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private List<TransactionTimeline> transactionsTimelineList;
    private EmployeePreferences empPreferences;

    public TransactionsTimeline() {
	try {
	    transactionsTimelineList = new ArrayList<TransactionTimeline>();
	    empPreferences = EmployeesPreferencesService.getEmployeePreferences(loginEmpData.getEmpId());
	    searchTransactionsTimeline();
	} catch (BusinessException e) {
	    e.printStackTrace();
	}

    }

    public void searchTransactionsTimeline() {
	try {
	    transactionsTimelineList = TransactionsTimelineService.getAllFutureTransactions(loginEmpData.getEmpId());
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateEmployeePreferences() {
	try {
	    EmployeesPreferencesService.updateEmployeePreferences(empPreferences);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public List<TransactionTimeline> getTransactionTimelineList() {
	return transactionsTimelineList;
    }

    public void setTransactionsTimelineList(List<TransactionTimeline> transactionsTimelineList) {
	this.transactionsTimelineList = transactionsTimelineList;
    }

    public EmployeePreferences getEmpPreferences() {
	return empPreferences;
    }

    public void setEmpPreferences(EmployeePreferences empPreferences) {
	this.empPreferences = empPreferences;
    }

}
