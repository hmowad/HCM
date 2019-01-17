package com.code.ui.backings.transactionstimeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.TransactionTimeline;
import com.code.dal.orm.hcm.employees.EmployeePrefrences;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesPrefrencesService;
import com.code.services.hcm.TransactionsTimelineService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "transactionsTimeline")
@ViewScoped
public class TransactionsTimeline extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private List<TransactionTimeline> transactionsTimelineList;
    private EmployeePrefrences empPrefrences;

    public TransactionsTimeline() {
	try {
	    transactionsTimelineList = new ArrayList<TransactionTimeline>();
	    empPrefrences = EmployeesPrefrencesService.getEmployeePrefrences(loginEmpData.getEmpId());
	    searchTransactionsTimeline();
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void searchTransactionsTimeline() {
	try {
	    transactionsTimelineList = TransactionsTimelineService.getAllFutureTransactions(loginEmpData.getEmpId());
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateEmployeePrefrences() {
	try {
	    EmployeesPrefrencesService.updateEmployeePrefrences(empPrefrences);
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

    public EmployeePrefrences getEmpPrefrences() {
	return empPrefrences;
    }

    public void setEmpPrefrences(EmployeePrefrences empPrefrences) {
	this.empPrefrences = empPrefrences;
    }

}
