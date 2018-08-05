package com.code.ui.backings.hcm.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "jobsTransactions")
@ViewScoped
public class JobsTransactions extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;

    private String decisionNumber;
    private Date decisionDate;
    private Date executionDate;

    private List<JobData> newJobs;
    private List<JobData> modifiedJobs;
    private List<JobData> minorSpecsModifiedJobs;
    private List<JobData> freezedJobs;
    private List<JobData> unFreezedJobs;
    private List<JobData> movedJobs;
    private List<JobData> cancelledJobs;

    private String selectedJobsIds;
    private JobData selectedNewJob;

    private List<Rank> ranks;

    private Date today;

    private boolean done;

    private final int pageSize = 10;

    public JobsTransactions() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersJobTransactions"));
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersJobTransactions"));
		    break;
		case 3:
		    setScreenTitle(getMessage("title_personsJobTransactions"));
		    break;
		default:
		    this.setServerSideErrorMessages(getMessage("error_general"));
		}
	    } else {
		this.setServerSideErrorMessages(getMessage("error_general"));
	    }

	    today = HijriDateService.getHijriSysDate();

	    if (mode != 3) {
		ranks = getJobsRanks(getCategoriesIdsArrayByMode(mode)[0]);
		ranks.remove(ranks.size() - 1);
	    }

	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	decisionNumber = null;
	executionDate = decisionDate = today;

	newJobs = new ArrayList<JobData>();
	modifiedJobs = new ArrayList<JobData>();
	minorSpecsModifiedJobs = new ArrayList<JobData>();
	cancelledJobs = new ArrayList<JobData>();
	freezedJobs = new ArrayList<JobData>();
	unFreezedJobs = new ArrayList<JobData>();
	movedJobs = new ArrayList<JobData>();
	selectedNewJob = null;

	done = false;
    }

    public void save() {
	try {
	    if (newJobs.isEmpty() && modifiedJobs.isEmpty() && minorSpecsModifiedJobs.isEmpty() && freezedJobs.isEmpty() && unFreezedJobs.isEmpty() && movedJobs.isEmpty() && cancelledJobs.isEmpty())
		throw new BusinessException("error_emptyJobsTransactions");
	    List<JobData> renamedJobs = new ArrayList<JobData>();
	    List<JobData> scaledJobs = new ArrayList<JobData>();

	    if (!modifiedJobs.isEmpty()) {
		for (JobData j : modifiedJobs) {
		    if ((j.getNewRankId() != null && !j.getNewRankId().equals((long) FlagsEnum.ALL.getCode()) && !j.getRankId().equals(j.getNewRankId()))) {
			// scaledJob
			scaledJobs.add(j);
		    } else {
			// renamedJob
			renamedJobs.add(j);
		    }
		}
	    }

	    JobsService.handleJobsTransactions(newJobs, renamedJobs, cancelledJobs, freezedJobs, unFreezedJobs, scaledJobs, movedJobs, minorSpecsModifiedJobs, decisionNumber, decisionDate, executionDate, this.loginEmpData.getEmpId(), true, true, true, true);
	    done = true;

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void jobNameChanged(JobData j) {
	// Handles the change of the job name in the employees mode in new jobs only.
	// Reset rank and classification.
	j.setRankId(null);
	j.setRankDescription(null);
	j.setClassificationId(null);
	j.setClassificationJobCode(null);
	j.setClassificationJobDescription(null);
    }

    public List<Rank> getJobsRanks(Long categoryId) {
	try {
	    return CommonService.getRanks(null, new Long[] { categoryId });
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return new ArrayList<Rank>();
	}
    }

    public void jobRankChanged(JobData j) {
	// Handles the change of the job rank in new jobs only.
	// Reset classification.
	j.setClassificationId(null);
	j.setClassificationJobCode(null);
	j.setClassificationJobDescription(null);
    }

    public void jobNewRankChanged(JobData j) {
	// Handles the change of the job new rank in modify-scale jobs only.
	// Reset new classification.
	j.setNewClassificationId(null);
	j.setNewClassificationJobCode(null);
	j.setNewClassificationJobDescription(null);
    }

    public void selectNewJob(JobData job) {
	selectedNewJob = job;
    }

    public void addModifiedJobs() {
	addJobs(selectedJobsIds, modifiedJobs);
    }

    public void addMinorSpecsModifiedJobs() {
	addJobs(selectedJobsIds, minorSpecsModifiedJobs);
    }

    public void addFreezedJobs() {
	addJobs(selectedJobsIds, freezedJobs);
    }

    public void addCancelledJobs() {
	addJobs(selectedJobsIds, cancelledJobs);
    }

    public void addUnFreezedJobs() {
	addJobs(selectedJobsIds, unFreezedJobs);
    }

    public void addMovedJobs() {
	addJobs(selectedJobsIds, movedJobs);
    }

    private void addJobs(String selectedJobsIds, List<JobData> jobsList) {
	if (selectedJobsIds != null && !selectedJobsIds.isEmpty()) {
	    String duplicatedJobsIfAny = "";
	    int duplicatedJobsCount = 0;
	    String comma = "";

	    List<JobData> jobs = new ArrayList<JobData>();
	    try {
		jobs = JobsService.getJobsByIdsString(selectedJobsIds);
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }

	    for (int i = 0; i < jobs.size(); i++) {
		try {
		    validateDuplicateJob(jobs.get(i).getId());
		    // Used in Jobs Renaming for Employees Only
		    jobs.get(i).setSequence(jobs.get(i).getSerial().substring(3));
		    jobsList.add(jobs.get(i));
		} catch (BusinessException e) {
		    duplicatedJobsIfAny += comma + " - " + jobs.get(i).getCode();
		    duplicatedJobsCount++;
		    comma = ", \n ";
		}
	    }
	    if (duplicatedJobsCount > 0)
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfJobs", new Object[] { duplicatedJobsCount, duplicatedJobsIfAny }));
	}
    }

    private void validateDuplicateJob(Long jobId) throws BusinessException {
	for (JobData current : modifiedJobs) {
	    if (jobId.equals(current.getId())) {
		throw new BusinessException("error_repeatedJob");
	    }
	}

	for (JobData current : minorSpecsModifiedJobs) {
	    if (jobId.equals(current.getId())) {
		throw new BusinessException("error_repeatedJob");
	    }
	}

	for (JobData current : freezedJobs) {
	    if (jobId.equals(current.getId())) {
		throw new BusinessException("error_repeatedJob");
	    }
	}

	for (JobData current : unFreezedJobs) {
	    if (jobId.equals(current.getId())) {
		throw new BusinessException("error_repeatedJob");
	    }
	}

	for (JobData current : movedJobs) {
	    if (jobId.equals(current.getId())) {
		throw new BusinessException("error_repeatedJob");
	    }
	}

	for (JobData current : cancelledJobs) {
	    if (jobId.equals(current.getId())) {
		throw new BusinessException("error_repeatedJob");
	    }
	}
    }

    public void addNewJob() {
	JobData j = new JobData();
	j.setStatus(JobStatusEnum.VACANT.getCode());
	j.setApprovedFlag(FlagsEnum.ON.getCode());
	newJobs.add(j);
	selectedNewJob = j;
    }

    public void deleteNewJob(JobData j) {
	newJobs.remove(j);
	selectedNewJob = null;
    }

    public void deleteModifiedJob(JobData j) {
	modifiedJobs.remove(j);
    }

    public void deleteMinorSpecsModifiedJob(JobData j) {
	minorSpecsModifiedJobs.remove(j);
    }

    public void deleteFreezedJob(JobData j) {
	freezedJobs.remove(j);
    }

    public void deleteDeletedJob(JobData j) {
	cancelledJobs.remove(j);
    }

    public void deleteUnFreezedJob(JobData j) {
	unFreezedJobs.remove(j);
    }

    public void deleteMovedJob(JobData j) {
	movedJobs.remove(j);
    }

    public void jobApprovedFlagListener(JobData job) {
	if (job.getApprovedFlag().intValue() == FlagsEnum.ON.getCode()) {
	    job.setStatus(JobStatusEnum.VACANT.getCode());
	} else {
	    job.setStatus(JobStatusEnum.FROZEN.getCode());
	}
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public int getMode() {
	return mode;
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

    public List<JobData> getNewJobs() {
	return newJobs;
    }

    public void setNewJobs(List<JobData> newJobs) {
	this.newJobs = newJobs;
    }

    public List<JobData> getModifiedJobs() {
	return modifiedJobs;
    }

    public void setModifiedJobs(List<JobData> modifiedJobs) {
	this.modifiedJobs = modifiedJobs;
    }

    public List<JobData> getMinorSpecsModifiedJobs() {
	return minorSpecsModifiedJobs;
    }

    public void setMinorSpecsModifiedJobs(List<JobData> minorSpecsModifiedJobs) {
	this.minorSpecsModifiedJobs = minorSpecsModifiedJobs;
    }

    public List<JobData> getCancelledJobs() {
	return cancelledJobs;
    }

    public void setCancelledJobs(List<JobData> cancelledJobs) {
	this.cancelledJobs = cancelledJobs;
    }

    public List<JobData> getFreezedJobs() {
	return freezedJobs;
    }

    public void setFreezedJobs(List<JobData> freezedJobs) {
	this.freezedJobs = freezedJobs;
    }

    public List<JobData> getUnFreezedJobs() {
	return unFreezedJobs;
    }

    public void setUnFreezedJobs(List<JobData> unFreezedJobs) {
	this.unFreezedJobs = unFreezedJobs;
    }

    public List<JobData> getMovedJobs() {
	return movedJobs;
    }

    public void setMovedJobs(List<JobData> movedJobs) {
	this.movedJobs = movedJobs;
    }

    public int getPageSize() {
	return pageSize;
    }

    public JobData getSelectedNewJob() {
	return selectedNewJob;
    }

    public void setSelectedNewJob(JobData selectedNewJob) {
	this.selectedNewJob = selectedNewJob;
    }

    public List<Rank> getRanks() {
	return ranks;

    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public boolean isDone() {
	return done;
    }

    public String getSelectedJobsIds() {
	return selectedJobsIds;
    }

    public void setSelectedJobsIds(String selectedJobsIds) {
	this.selectedJobsIds = selectedJobsIds;
    }

}
