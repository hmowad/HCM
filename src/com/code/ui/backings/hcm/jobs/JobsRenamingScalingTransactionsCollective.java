package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsCollectiveService;
import com.code.services.hcm.JobsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "jobsRenamingScalingTransactionsCollective")
@ViewScoped
public class JobsRenamingScalingTransactionsCollective extends BaseBacking {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;

    private String decisionNumber;
    private Date decisionDate;
    private Date executionDate;

    private String selectedJobsIds;
    private List<JobData> selectedJobs;

    private Long newBasicJobNameId;
    private String newJobName;
    private Long newRankId;
    private Long newClassificationId;
    private String newClassificationJobCode;
    private String reasons;

    private int approvedJobsCount;

    private List<Rank> ranks;

    private boolean done;
    private final int pageSize = 10;

    public JobsRenamingScalingTransactionsCollective() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersJobsRenamingScalingTransactionsCollective"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersJobsRenamingScalingTransactionsCollective"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsJobsRenamingTransactionsCollective"));
		break;
	    default:
		this.setServerSideErrorMessages(getMessage("error_general"));
	    }
	    if (mode == 2) {
		ranks = getJobsRanks(CategoriesEnum.SOLDIERS.getCode());
		if (ranks.size() >= 1)
		    ranks.remove(ranks.size() - 1);
	    }

	    resetForm();

	} else {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void resetForm() {
	try {
	    decisionNumber = null;
	    executionDate = decisionDate = HijriDateService.getHijriSysDate();

	    selectedJobsIds = null;
	    selectedJobs = new ArrayList<JobData>();

	    newBasicJobNameId = null;
	    newJobName = null;
	    newRankId = -1L;
	    newClassificationId = null;
	    newClassificationJobCode = null;
	    reasons = null;

	    approvedJobsCount = 0;

	    done = false;
	} catch (BusinessException e) {
	    this.setServerSideSuccessMessages(getMessage(e.getMessage()));
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

	    List<JobData> currentJobsToAdd = new ArrayList<JobData>();

	    Long currentBasicJobNameId;
	    Long currentRankId;

	    if (selectedJobs.isEmpty()) {
		currentBasicJobNameId = jobs.get(0).getBasicJobNameId();
		currentRankId = jobs.get(0).getRankId();
	    } else {
		currentBasicJobNameId = selectedJobs.get(0).getBasicJobNameId();
		currentRankId = selectedJobs.get(0).getRankId();
	    }

	    // Validate Job Names
	    for (JobData job : jobs) {
		if (!job.getBasicJobNameId().equals(currentBasicJobNameId)) {
		    this.setServerSideErrorMessages(getMessage("error_differentBasicJobName"));
		    return;
		}
		if (mode == 2 && !job.getRankId().equals(currentRankId)) {
		    this.setServerSideErrorMessages(getMessage("error_differentRank"));
		    return;
		}
		try {
		    validateDuplicateJob(job.getId());
		    currentJobsToAdd.add(job);

		    if (FlagsEnum.ON.getCode() == job.getApprovedFlag().intValue())
			approvedJobsCount++;

		} catch (BusinessException e) {
		    duplicatedJobsIfAny += comma + " - " + job.getCode();
		    duplicatedJobsCount++;
		    comma = ", \n ";
		}
	    }

	    if (duplicatedJobsCount > 0) {
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfJobs", new Object[] { duplicatedJobsCount,
			duplicatedJobsIfAny }));
	    }

	    selectedJobs.addAll(currentJobsToAdd);
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
	if (FlagsEnum.ON.getCode() == job.getApprovedFlag().intValue())
	    approvedJobsCount--;
	if (selectedJobs.size() == 0) {
	    resetForm();
	}
    }

    public void save() {
	try {
	    if (selectedJobs.isEmpty())
		throw new BusinessException("error_emptyJobsTransactionsCollective");

	    JobsCollectiveService.renameScaleJobs(decisionNumber, decisionDate, executionDate,
		    selectedJobs, newBasicJobNameId, newJobName, newRankId, newClassificationId, reasons, this.loginEmpData.getEmpId());

	    done = true;

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    private List<Rank> getJobsRanks(Long categoryId) {
	try {
	    return CommonService.getRanks(null, new Long[] { categoryId });
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return new ArrayList<Rank>();
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

    public Long getCurrentCategoryId() {

	if (selectedJobs.isEmpty())
	    return null;
	else
	    return selectedJobs.get(0).getCategoryId();
    }

    public Long getCurrentRankId() {
	if (selectedJobs.isEmpty())
	    return null;
	else
	    return selectedJobs.get(0).getRankId();
    }

    public Long getNewBasicJobNameId() {
	return newBasicJobNameId;
    }

    public void setNewBasicJobNameId(Long newBasicJobNameId) {
	this.newBasicJobNameId = newBasicJobNameId;
    }

    public String getNewJobName() {
	return newJobName;
    }

    public void setNewJobName(String newJobName) {
	this.newJobName = newJobName;
    }

    public Long getNewRankId() {
	return newRankId;
    }

    public void setNewRankId(Long newRankId) {
	this.newRankId = newRankId;
    }

    public Long getNewClassificationId() {
	return newClassificationId;
    }

    public void setNewClassificationId(Long newClassificationId) {
	this.newClassificationId = newClassificationId;
    }

    public String getNewClassificationJobCode() {
	return newClassificationJobCode;
    }

    public void setNewClassificationJobCode(String newClassificationJobCode) {
	this.newClassificationJobCode = newClassificationJobCode;
    }

    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    public int getApprovedJobsCount() {
	return approvedJobsCount;
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

    public int getPageSize() {
	return pageSize;
    }

}
