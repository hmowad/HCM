package com.code.ui.backings.hcm.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.jobs.JobTransactionData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "jobsInquiry")
@ViewScoped
public class JobsInquiry extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;

    List<JobData> jobsList;
    private JobData selectedJob;
    private EmployeeData selectedJobEmployee;
    List<JobTransactionData> jobTransactionsList;

    // For Search
    private Long unitId;
    private String unitHKey;
    private boolean viewAllLevelsFlag;
    private String jobCode;
    private Integer jobStatus;
    private String jobName;
    private Date executionDateFrom;
    private Date executionDateTo;
    private Integer reservationStatus;
    private Integer approved;

    private int pageSize = 10;

    public JobsInquiry() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersJobInquiry"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersJobInquiry"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsJobInquiry"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_URLError"));
	}

	resetForm();
    }

    public void resetForm() {
	unitId = (long) FlagsEnum.ALL.getCode();
	unitHKey = "";
	viewAllLevelsFlag = false;
	jobCode = "";
	jobStatus = FlagsEnum.ALL.getCode();
	jobName = "";
	executionDateFrom = null;
	executionDateTo = null;
	reservationStatus = FlagsEnum.ALL.getCode();
	approved = FlagsEnum.ALL.getCode();
	jobsList = new ArrayList<JobData>();
	selectedJob = null;
	selectedJobEmployee = null;
	jobTransactionsList = new ArrayList<JobTransactionData>();
    }

    public void searchJobs() {
	try {
	    Integer[] jobStatusArr = null;
	    if (jobStatus != -1) {
		jobStatusArr = new Integer[1];
		jobStatusArr[0] = jobStatus;
	    }

	    Integer[] reservationStatusArr = null;
	    if (reservationStatus != -1) {
		reservationStatusArr = new Integer[1];
		reservationStatusArr[0] = reservationStatus;
	    }

	    jobsList = JobsService.getJobs(viewAllLevelsFlag ? (long) FlagsEnum.ALL.getCode() : unitId, viewAllLevelsFlag ? unitHKey : null, jobStatusArr, jobCode, jobName, getCategoriesIdsArrayByMode(mode), executionDateFrom, executionDateTo, approved, reservationStatusArr);
	} catch (BusinessException e) {
	    jobsList = new ArrayList<JobData>();
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

	selectedJob = null;
	selectedJobEmployee = null;
	jobTransactionsList = new ArrayList<JobTransactionData>();
    }

    public void showJobDetails(JobData job) {
	try {
	    selectedJob = job;
	    if (mode == 1 && selectedJob.getEmployeeId() != null)
		selectedJobEmployee = EmployeesService.getEmployeeData(selectedJob.getEmployeeId());
	    else
		selectedJobEmployee = null;
	    jobTransactionsList = JobsService.getJobTransactionsDataByJobId(job.getId());
	} catch (BusinessException e) {
	    selectedJob = null;
	    selectedJobEmployee = null;
	    jobTransactionsList = new ArrayList<JobTransactionData>();
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public JobData getSelectedJob() {
	return selectedJob;
    }

    public void setSelectedJob(JobData selectedJob) {
	this.selectedJob = selectedJob;
    }

    public String getSelectedJobEmployeeInfo() {
	return (selectedJobEmployee == null) ? "" : selectedJobEmployee.getRankDesc() + (selectedJobEmployee.getRankTitleDesc() == null ? "" : " " + selectedJobEmployee.getRankTitleDesc()) + " " + getMessage("label_number") + " " + selectedJobEmployee.getMilitaryNumber().toString() + " / " + selectedJobEmployee.getName();
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Date getExecutionDateFrom() {
	return executionDateFrom;
    }

    public void setExecutionDateFrom(Date executionDateFrom) {
	this.executionDateFrom = executionDateFrom;
    }

    public Date getExecutionDateTo() {
	return executionDateTo;
    }

    public void setExecutionDateTo(Date executionDateTo) {
	this.executionDateTo = executionDateTo;
    }

    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHKey) {
	this.unitHKey = unitHKey;
    }

    public boolean isViewAllLevelsFlag() {
	return viewAllLevelsFlag;
    }

    public void setViewAllLevelsFlag(boolean viewAllLevelsFlag) {
	this.viewAllLevelsFlag = viewAllLevelsFlag;
    }

    public String getJobCode() {
	return jobCode;
    }

    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
    }

    public Integer getJobStatus() {
	return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
	this.jobStatus = jobStatus;
    }

    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
    }

    public List<JobTransactionData> getJobTransactionsList() {
	return jobTransactionsList;
    }

    public void setJobTransactionsList(List<JobTransactionData> jobTransactionsList) {
	this.jobTransactionsList = jobTransactionsList;
    }

    public List<JobData> getJobsList() {
	return jobsList;
    }

    public void setJobsList(List<JobData> jobsList) {
	this.jobsList = jobsList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public Integer getReservationStatus() {
	return reservationStatus;
    }

    public void setReservationStatus(Integer reservationStatus) {
	this.reservationStatus = reservationStatus;
    }

    public Integer getApproved() {
	return approved;
    }

    public void setApproved(Integer approved) {
	this.approved = approved;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

}
