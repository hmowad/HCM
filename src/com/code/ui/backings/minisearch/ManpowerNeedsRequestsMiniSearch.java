package com.code.ui.backings.minisearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.ManpowerNeedsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "manpowerNeedsRequestsMiniSearch")
@ViewScoped
public class ManpowerNeedsRequestsMiniSearch extends BaseBacking {

    private int rowsCount = 10;
    private boolean multipleSelectFlag;

    private long categoryId;
    private long regionId;

    private Date transactionDateFrom;
    private Date transactionDateTo;
    private List<ManpowerNeedData> manpowerNeedRequests;
    private List<ManpowerNeedData> selectedManpowerNeedRequests;

    private String selectedManpowerNeedRequestsIds;

    public ManpowerNeedsRequestsMiniSearch() {
	try {
	    if (getRequest().getParameter("categoryId") != null)
		categoryId = Long.parseLong(getRequest().getParameter("categoryId"));

	    if (getRequest().getParameter("regionId") != null)
		regionId = Long.parseLong(getRequest().getParameter("regionId"));

	    if (getRequest().getParameter("multipleSelectFlag") != null) {
		multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
		if (multipleSelectFlag) {
		    selectedManpowerNeedRequests = new ArrayList<ManpowerNeedData>();
		    rowsCount = 5;
		}
	    }

	    transactionDateFrom = HijriDateService.getHijriSysDate();
	    transactionDateTo = HijriDateService.getHijriSysDate();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

	searchManpowerNeedRequest();
    }

    public void searchManpowerNeedRequest() {
	try {
	    Long[] selectedManpowerNeedsRequestsIds = null;

	    if (selectedManpowerNeedRequests != null && selectedManpowerNeedRequests.size() > 0) {
		selectedManpowerNeedsRequestsIds = new Long[selectedManpowerNeedRequests.size()];
		for (int i = 0; i < selectedManpowerNeedRequests.size(); i++) {
		    selectedManpowerNeedsRequestsIds[i] = selectedManpowerNeedRequests.get(i).getId();
		}
	    }

	    manpowerNeedRequests = ManpowerNeedsService.getManpowerNeedsRequestsAndStudiesData(regionId, categoryId, transactionDateFrom, transactionDateTo, selectedManpowerNeedsRequestsIds);

	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	    manpowerNeedRequests = new ArrayList<ManpowerNeedData>();
	}
    }

    public void addSelectedManpowerNeedsRequest(ManpowerNeedData manpowerNeed) {
	selectedManpowerNeedRequests.add(manpowerNeed);
	manpowerNeedRequests.remove(manpowerNeed);
    }

    public void removeSelectedManpowerNeedsRequest(ManpowerNeedData manpowerNeed) {
	selectedManpowerNeedRequests.remove(manpowerNeed);
	manpowerNeedRequests.add(manpowerNeed);
    }

    public void generateManpowerNeedsRequestsInfo() {
	selectedManpowerNeedRequestsIds = "";
	String comma = "";
	if (selectedManpowerNeedRequests.size() > 0) {
	    for (ManpowerNeedData manpowerNeed : selectedManpowerNeedRequests) {
		selectedManpowerNeedRequestsIds += comma + manpowerNeed.getId();
		comma = ",";
	    }
	}
	if (selectedManpowerNeedRequestsIds.length() == 0) {
	    selectedManpowerNeedRequestsIds = "";
	}
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public boolean getMultipleSelectFlag() {
	return multipleSelectFlag;
    }

    public void setMultipleSelectFlag(boolean multipleSelectFlag) {
	this.multipleSelectFlag = multipleSelectFlag;
    }

    public long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(long categoryId) {
	this.categoryId = categoryId;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public Date getTransactionDateFrom() {
	return transactionDateFrom;
    }

    public void setTransactionDateFrom(Date transactionDateFrom) {
	this.transactionDateFrom = transactionDateFrom;
    }

    public Date getTransactionDateTo() {
	return transactionDateTo;
    }

    public void setTransactionDateTo(Date transactionDateTo) {
	this.transactionDateTo = transactionDateTo;
    }

    public List<ManpowerNeedData> getManpowerNeedRequests() {
	return manpowerNeedRequests;
    }

    public void setManpowerNeedRequests(List<ManpowerNeedData> manpowerNeedRequests) {
	this.manpowerNeedRequests = manpowerNeedRequests;
    }

    public List<ManpowerNeedData> getSelectedManpowerNeedRequests() {
	return selectedManpowerNeedRequests;
    }

    public void setSelectedManpowerNeedRequests(List<ManpowerNeedData> selectedManpowerNeedRequests) {
	this.selectedManpowerNeedRequests = selectedManpowerNeedRequests;
    }

    public String getSelectedManpowerNeedRequestsIds() {
	return selectedManpowerNeedRequestsIds;
    }

    public void setSelectedManpowerNeedRequestsIds(String selectedManpowerNeedRequestsIds) {
	this.selectedManpowerNeedRequestsIds = selectedManpowerNeedRequestsIds;
    }

}