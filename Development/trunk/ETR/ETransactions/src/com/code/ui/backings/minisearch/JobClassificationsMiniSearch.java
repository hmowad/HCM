package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.organization.jobs.JobClassification;
import com.code.enums.CategoriesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "jobClassificationsMiniSearch")
@ViewScoped
public class JobClassificationsMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private String searchMajorGroupDescription;
    private String searchMinorGroupDescription;
    private String searchSeriesDescription;
    private String searchJobDescription;
    private List<String> majorGroupDescriptionList;
    private List<String> minorGroupDescriptionList;
    private List<String> seriesDescriptionList;
    private String rankCodeToUse;

    private List<JobClassification> jobClassificationsList;

    public JobClassificationsMiniSearch() {
	searchMajorGroupDescription = "";
	searchMinorGroupDescription = "";
	searchSeriesDescription = "";
	searchJobDescription = "";
	String rankCode = getRequest().getParameter("rankCode");
	if (rankCode.substring(0, 1).equals((CategoriesEnum.PERSONS.getCode() + ""))) {
	    rankCodeToUse = rankCode.substring(1);
	    rankCodeToUse = (16 - Integer.parseInt(rankCodeToUse)) + "";
	    if (rankCodeToUse.length() == 1)
		rankCodeToUse = "0" + rankCodeToUse;
	} else
	    rankCodeToUse = "";
	searchJobClassification();
    }

    public void searchJobClassification() {
	try {
	    if (rankCodeToUse.equals("")) {
		setServerSideErrorMessages(getMessage("error_jobClassificationsExistsOnlyForPersons"));
		return;
	    }
	    super.init();
	    majorGroupDescriptionList = JobsService.getJobClassificationMajorGroupDescriptions(rankCodeToUse);
	    minorGroupDescriptionList = JobsService.getJobClassificationMinorGroupDescriptions(searchMajorGroupDescription, rankCodeToUse);
	    seriesDescriptionList = JobsService.getJobClassificationSeriesDescriptions(searchMajorGroupDescription, searchMinorGroupDescription, rankCodeToUse);
	    jobClassificationsList = JobsService.getJobClassifications(searchMajorGroupDescription, searchMinorGroupDescription, searchSeriesDescription, searchJobDescription, rankCodeToUse);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void majorGroupChanged(AjaxBehaviorEvent e) {
	try {
	    searchMinorGroupDescription = "";
	    minorGroupDescriptionList = JobsService.getJobClassificationMinorGroupDescriptions(searchMajorGroupDescription, rankCodeToUse);
	    searchSeriesDescription = "";
	    seriesDescriptionList = JobsService.getJobClassificationSeriesDescriptions(searchMajorGroupDescription, searchMinorGroupDescription, rankCodeToUse);
	} catch (BusinessException exception) {
	    super.setServerSideErrorMessages(getMessage(exception.getMessage()));
	}
    }

    public void minorGroupChanged(AjaxBehaviorEvent e) {
	try {
	    searchSeriesDescription = "";
	    seriesDescriptionList = JobsService.getJobClassificationSeriesDescriptions(searchMajorGroupDescription, searchMinorGroupDescription, rankCodeToUse);
	} catch (BusinessException exception) {
	    super.setServerSideErrorMessages(getMessage(exception.getMessage()));
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public String getSearchMajorGroupDescription() {
	return searchMajorGroupDescription;
    }

    public void setSearchMajorGroupDescription(String searchMajorGroupDescription) {
	this.searchMajorGroupDescription = searchMajorGroupDescription;
    }

    public String getSearchMinorGroupDescription() {
	return searchMinorGroupDescription;
    }

    public void setSearchMinorGroupDescription(String searchMinorGroupDescription) {
	this.searchMinorGroupDescription = searchMinorGroupDescription;
    }

    public String getSearchSeriesDescription() {
	return searchSeriesDescription;
    }

    public void setSearchSeriesDescription(String searchSeriesDescription) {
	this.searchSeriesDescription = searchSeriesDescription;
    }

    public String getSearchJobDescription() {
	return searchJobDescription;
    }

    public void setSearchJobDescription(String searchJobDescription) {
	this.searchJobDescription = searchJobDescription;
    }

    public List<JobClassification> getJobClassificationsList() {
	return jobClassificationsList;
    }

    public void setJobClassificationsList(List<JobClassification> jobClassificationsList) {
	this.jobClassificationsList = jobClassificationsList;
    }

    public List<String> getMajorGroupDescriptionList() {
	return majorGroupDescriptionList;
    }

    public void setMajorGroupDescriptionList(List<String> majorGroupDescriptionList) {
	this.majorGroupDescriptionList = majorGroupDescriptionList;
    }

    public List<String> getMinorGroupDescriptionList() {
	return minorGroupDescriptionList;
    }

    public void setMinorGroupDescriptionList(List<String> minorGroupDescriptionList) {
	this.minorGroupDescriptionList = minorGroupDescriptionList;
    }

    public List<String> getSeriesDescriptionList() {
	return seriesDescriptionList;
    }

    public void setSeriesDescriptionList(List<String> seriesDescriptionList) {
	this.seriesDescriptionList = seriesDescriptionList;
    }
}