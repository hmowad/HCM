package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.TransactionsTimeline;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TransactionsTimelineService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "transactionsTimeLineMiniSearch")
@ViewScoped
public class TransactionsTimeLineMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private List<TransactionsTimeline> transactionsTimeLineList;

    public TransactionsTimeLineMiniSearch() {
	transactionsTimeLineList = new ArrayList<TransactionsTimeline>();
	searchTransactionsTimeLine();

    }

    public void searchTransactionsTimeLine() {
	try {
	    transactionsTimeLineList = TransactionsTimelineService.getAllFutureTransactions(loginEmpData.getEmpId());
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

    public List<TransactionsTimeline> getTransactionsTimeLineList() {
	return transactionsTimeLineList;
    }

    public void setTransactionsTimeLineList(List<TransactionsTimeline> transactionsTimeLineList) {
	this.transactionsTimeLineList = transactionsTimeLineList;
    }

}
