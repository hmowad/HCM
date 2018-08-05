package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsCollectiveService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "jobsConstructionTransactionsCollective")
@ViewScoped
public class JobsConstructionTransactionsCollective extends BaseBacking {

    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;

    private String decisionNumber;
    private Date decisionDate;
    private Date executionDate;

    private List<UnitData> selectedUnits;
    private List<JobData> newJobs;

    private String selectedUnitsIds;

    private JobData selectedJob;
    private String selectedBasicJobsNamesNames;
    private String selectedBasicJobNamesCategoriesIds;

    private List<Rank> ranks;

    private boolean done;
    private final int pageSize = 10;

    public JobsConstructionTransactionsCollective() {

	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersJobsConstructionTransactionsCollective"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersJobsConstructionTransactionsCollective"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsJobsConstructionTransactionsCollective"));
		break;
	    default:
		this.setServerSideErrorMessages(getMessage("error_general"));
	    }

	    if (mode != 3) {
		ranks = getJobsRanks(getCategoriesIdsArrayByMode(mode)[0]);
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
	    selectedUnits = new ArrayList<UnitData>();
	    newJobs = new ArrayList<JobData>();

	    selectedUnitsIds = null;
	    selectedJob = null;

	    selectedBasicJobsNamesNames = null;
	    selectedBasicJobNamesCategoriesIds = null;

	    done = false;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addUnits() {

	if (selectedUnitsIds != null && !selectedUnitsIds.isEmpty()) {
	    String duplicatedUnitsIfAny = "";
	    int duplicatedUnitsCount = 0;
	    String comma = "";

	    List<UnitData> units = new ArrayList<UnitData>();
	    try {
		units = UnitsService.getUnitsByIdsString(selectedUnitsIds);
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
		return;
	    }

	    for (int i = 0; i < units.size(); i++) {
		try {
		    validateDuplicateUnit(units.get(i).getId());
		    selectedUnits.add(units.get(i));
		} catch (BusinessException e) {
		    duplicatedUnitsIfAny += comma + " - " + units.get(i).getFullName();
		    duplicatedUnitsCount++;
		    comma = ", \n ";
		}
	    }
	    if (duplicatedUnitsCount > 0)
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfUnits", new Object[] { duplicatedUnitsCount, duplicatedUnitsIfAny }));
	}
    }

    private void validateDuplicateUnit(Long unitId) throws BusinessException {
	for (UnitData unit : selectedUnits) {
	    if (unitId.equals(unit.getId()))
		throw new BusinessException("error_repeatedUnit");
	}
    }

    public void deleteSelectedUnit(UnitData unit) {
	selectedUnits.remove(unit);
	// if (selectedUnits.size() == 0)
	// resetForm();
    }

    public void addNewJobs() {

	String[] basicJobsNamesNames = selectedBasicJobsNamesNames.split(",");
	String[] basicJobNamesCategoriesIds = selectedBasicJobNamesCategoriesIds.split(",");

	for (int i = 0; i < basicJobsNamesNames.length; i++) {
	    // initialize data
	    JobData job = new JobData();
	    job.setName(basicJobsNamesNames[i]);
	    job.setCategoryId(Long.parseLong(basicJobNamesCategoriesIds[i]));

	    job.setStatus(JobStatusEnum.VACANT.getCode());
	    job.setApprovedFlag(FlagsEnum.ON.getCode());
	    job.setJobsCount(1);

	    newJobs.add(job);
	}

	if (basicJobsNamesNames.length > 0) {
	    selectJob(newJobs.get(0));
	}
    }

    public void deleteNewJob(JobData job) {
	newJobs.remove(job);
	if (newJobs.size() == 0)
	    selectedJob = null;
    }

    public void selectJob(JobData job) {
	selectedJob = job;
    }

    public List<Rank> getJobsRanks(Long categoryId) {
	try {
	    return CommonService.getRanks(null, new Long[] { categoryId });
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return new ArrayList<Rank>();
	}
    }

    public void jobRankChanged(JobData job) {
	// Handles the change of the job rank.
	// Reset classification.
	job.setClassificationId(null);
	job.setClassificationJobCode(null);
	job.setClassificationJobDescription(null);
    }

    public void jobApprovedFlagListener(JobData job) {
	if (job.getApprovedFlag().intValue() == FlagsEnum.ON.getCode())
	    job.setStatus(JobStatusEnum.VACANT.getCode());
	else
	    job.setStatus(JobStatusEnum.FROZEN.getCode());
    }

    public void save() {
	try {
	    if (newJobs.isEmpty())
		throw new BusinessException("error_emptyJobsTransactionsCollective");

	    JobsCollectiveService.constructJobs(decisionNumber, decisionDate, executionDate, selectedUnits, newJobs, this.loginEmpData.getEmpId());
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

    public List<UnitData> getSelectedUnits() {
	return selectedUnits;
    }

    public void setSelectedUnits(List<UnitData> selectedUnits) {
	this.selectedUnits = selectedUnits;
    }

    public List<JobData> getNewJobs() {
	return newJobs;
    }

    public void setNewJobs(List<JobData> newJobs) {
	this.newJobs = newJobs;
    }

    public String getSelectedUnitsIds() {
	return selectedUnitsIds;
    }

    public void setSelectedUnitsIds(String selectedUnitsIds) {
	this.selectedUnitsIds = selectedUnitsIds;
    }

    public JobData getSelectedJob() {
	return selectedJob;
    }

    public void setSelectedJob(JobData selectedJob) {
	this.selectedJob = selectedJob;
    }

    public String getSelectedBasicJobsNamesNames() {
	return selectedBasicJobsNamesNames;
    }

    public void setSelectedBasicJobsNamesNames(String selectedBasicJobsNamesNames) {
	this.selectedBasicJobsNamesNames = selectedBasicJobsNamesNames;
    }

    public String getSelectedBasicJobNamesCategoriesIds() {
	return selectedBasicJobNamesCategoriesIds;
    }

    public void setSelectedBasicJobNamesCategoriesIds(String selectedBasicJobNamesCategoriesIds) {
	this.selectedBasicJobNamesCategoriesIds = selectedBasicJobNamesCategoriesIds;
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
