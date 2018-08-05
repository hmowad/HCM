package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.jobs.JobTransaction;
import com.code.enums.CategoriesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "jobsTransactionsDecisionsInquiry")
@ViewScoped
public class JobsTransactionsDecisionsInquiry extends BaseBacking {

    /**
     * 1 for Officers and 2 for Soldiers
     **/
    private int mode;

    private String decisionNumber;
    private Date decisionDateFrom;
    private Date decisionDateTo;

    private List<JobTransaction> jobsDecisionsList;

    private int pageSize = 10;

    public JobsTransactionsDecisionsInquiry() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersJobsTransactionsDecisionsInquiry"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersJobsTransactionsDecisionsInquiry"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_general"));
	}
	resetForm();
    }

    public void resetForm() {
	try {
	    decisionNumber = "";
	    String[] todayDate = HijriDateService.getHijriSysDateString().split("/");
	    decisionDateFrom = HijriDateService.getHijriDate(todayDate[0] + "/" + todayDate[1] + "/" + (Integer.parseInt(todayDate[2]) - 1));
	    decisionDateTo = HijriDateService.getHijriSysDate();
	    jobsDecisionsList = new ArrayList<JobTransaction>();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchJobsReservationDecisions() {
	try {
	    jobsDecisionsList = JobsService.getJobsTransactionsDecisions(getLoginEmpPhysicalRegionFlag(true), mode, decisionNumber, decisionDateFrom, decisionDateTo);
	} catch (BusinessException e) {
	    jobsDecisionsList = new ArrayList<JobTransaction>();
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printJobDecision(JobTransaction jobTransaction) {
	try {
	    byte[] bytes = JobsService.getJobDecisionBytes(mode == CategoriesEnum.OFFICERS.getCode() ? 10 : 20, jobTransaction.getDecisionNumber(), jobTransaction.getDecisionDate());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDateFrom() {
	return decisionDateFrom;
    }

    public void setDecisionDateFrom(Date decisionDateFrom) {
	this.decisionDateFrom = decisionDateFrom;
    }

    public Date getDecisionDateTo() {
	return decisionDateTo;
    }

    public void setDecisionDateTo(Date decisionDateTo) {
	this.decisionDateTo = decisionDateTo;
    }

    public List<JobTransaction> getJobsDecisionsList() {
	return jobsDecisionsList;
    }

    public void setJobsDecisionsList(List<JobTransaction> jobsDecisionsList) {
	this.jobsDecisionsList = jobsDecisionsList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
}
