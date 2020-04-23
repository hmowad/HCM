package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsCollectiveService;
import com.code.services.hcm.JobsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "jobsMovingTransactionsCollective")
@ViewScoped
public class JobsMovingTransactionsCollective extends BaseBacking {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;

    private String decisionNumber;
    private Date decisionDate;
    private Date executionDate;

    private String selectedJobsIds;
    private List<JobData> selectedJobs;

    private UnitData selectedNewUnit;
    private String reasons;

    private boolean done;
    private final int pageSize = 10;

    public JobsMovingTransactionsCollective() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersJobsMovingTransactionsCollective"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersJobsMovingTransactionsCollective"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsJobsMovingTransactionsCollective"));
		break;
	    default:
		this.setServerSideErrorMessages(getMessage("error_URLError"));
	    }

	    resetForm();

	} else {
	    this.setServerSideErrorMessages(getMessage("error_URLError"));
	}
    }

    public void resetForm() {
	try {
	    decisionNumber = null;
	    executionDate = decisionDate = HijriDateService.getHijriSysDate();

	    selectedJobsIds = null;
	    selectedJobs = new ArrayList<JobData>();

	    selectedNewUnit = new UnitData();
	    reasons = null;

	    done = false;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void addSelectedJobs() {
	if (selectedJobsIds != null && !selectedJobsIds.isEmpty()) {
	    String duplicatedJobsIfAny = "";
	    int duplicatedJobsCount = 0;
	    String comma = "";

	    List<JobData> jobs = new ArrayList<JobData>();
	    try {
		jobs = JobsService.getJobsByIdsString(selectedJobsIds);
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
		return;
	    }

	    for (int i = 0; i < jobs.size(); i++) {
		try {
		    validateDuplicateJob(jobs.get(i).getId());
		    selectedJobs.add(jobs.get(i));

		} catch (BusinessException e) {
		    duplicatedJobsIfAny += comma + " - " + jobs.get(i).getCode();
		    duplicatedJobsCount++;
		    comma = ", \n ";
		}
	    }

	    if (duplicatedJobsCount > 0) {
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfJobs", new Object[] { duplicatedJobsCount,
			duplicatedJobsIfAny }));
	    }
	}
    }

    private void validateDuplicateJob(Long jobId) throws BusinessException {
	for (JobData job : selectedJobs) {
	    if (jobId.equals(job.getId()))
		throw new BusinessException("error_repeatedJob");
	}
    }

    public void deleteSelectedJob(JobData job) {
	selectedJobs.remove(job);
	if (selectedJobs.size() == 0) {
	    resetForm();
	}
    }

    public void save() {
	try {
	    if (selectedJobs.isEmpty())
		throw new BusinessException("error_emptyJobsTransactionsCollective");

	    JobsCollectiveService.moveJobs(decisionNumber, decisionDate, executionDate, selectedJobs, selectedNewUnit, reasons, this.loginEmpData.getEmpId());
	    done = true;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
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

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
    }

    public List<JobData> getSelectedJobs() {
	return selectedJobs;
    }

    public void setSelectedJobs(List<JobData> selectedJobs) {
	this.selectedJobs = selectedJobs;
    }

    public String getSelectedJobsIds() {
	return selectedJobsIds;
    }

    public void setSelectedJobsIds(String selectedJobsIds) {
	this.selectedJobsIds = selectedJobsIds;
    }

    public UnitData getSelectedNewUnit() {
	return selectedNewUnit;
    }

    public void getSelectedNewUnit(UnitData selectedNewUnit) {
	this.selectedNewUnit = selectedNewUnit;
    }

    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    public boolean isDone() {
	return done;
    }

    public int getPageSize() {
	return pageSize;
    }

}
