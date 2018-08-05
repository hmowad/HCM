package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.jobs.JobTransaction;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "jobsReservationDecisionsInquiry")
@ViewScoped
public class JobsReservationDecisionsInquiry extends BaseBacking {

    private String decisionNumber;
    private int decisionType;
    private Date decisionDateFrom;
    private Date decisionDateTo;

    private List<JobTransaction> jobsDecisionsList;

    private int pageSize = 10;

    public JobsReservationDecisionsInquiry() {
	resetForm();
    }

    public void resetForm() {
	try {
	    decisionNumber = "";
	    decisionType = FlagsEnum.ALL.getCode();
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
	    jobsDecisionsList = JobsService.getJobsReservationDecisions(getLoginEmpPhysicalRegionFlag(true), decisionNumber, decisionType, decisionDateFrom, decisionDateTo);
	} catch (BusinessException e) {
	    jobsDecisionsList = new ArrayList<JobTransaction>();
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printJobDecision(JobTransaction jobTransaction) {
	try {
	    byte[] bytes = JobsService.getJobDecisionBytes(30, jobTransaction.getDecisionNumber(), jobTransaction.getDecisionDate());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public int getDecisionType() {
	return decisionType;
    }

    public void setDecisionType(int decisionType) {
	this.decisionType = decisionType;
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
