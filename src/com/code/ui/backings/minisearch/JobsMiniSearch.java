package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.enums.JobStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "jobsMiniSearch")
@ViewScoped
public class JobsMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private String searchJobCode;
    private String searchJobName;
    private String searchUnitFullName;
    private Long[] categoriesIds;
    private int categoryMode;
    private List<JobData> searchJobList;
    private int mode;

    private String unitHKey;
    private long rankId = -1;
    private long regionId = -1;
    private long minorSpecId = -1;

    private boolean multipleSelectFlag;
    private final int SELECTED_JOBS_MAX = 100;
    private List<JobData> selectedJobList;
    private String selectedJobsIds;
    private String comma;

    public JobsMiniSearch() {
	searchJobList = new ArrayList<JobData>();
	searchJobCode = "";
	searchJobName = "";
	searchUnitFullName = "";
	if (getRequest().getParameter("categoryMode") != null) {
	    categoryMode = Integer.parseInt(getRequest().getParameter("categoryMode"));
	    if (getRequest().getParameter("mode") != null)
		mode = Integer.parseInt(getRequest().getParameter("mode"));
	    unitHKey = getRequest().getParameter("unitHKey");
	    if (getRequest().getParameter("rankId") != null)
		rankId = Long.parseLong(getRequest().getParameter("rankId"));
	    if (getRequest().getParameter("regionId") != null)
		regionId = Long.parseLong(getRequest().getParameter("regionId"));
	    if (getRequest().getParameter("minorSpecId") != null)
		minorSpecId = Long.parseLong(getRequest().getParameter("minorSpecId"));
	    if (getRequest().getParameter("multipleSelectFlag") != null) {
		multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
		selectedJobList = new ArrayList<JobData>();
		selectedJobsIds = "";
		comma = "";
		if (multipleSelectFlag) {
		    rowsCount = 5;
		}
	    }

	    switch (categoryMode) {
	    case 1: {
		setScreenTitle(getMessage("title_officerJobs"));
		categoriesIds = getCategoriesIdsArrayByMode(categoryMode);
		break;
	    }
	    case 2: {
		setScreenTitle(getMessage("title_soldierJobs"));
		categoriesIds = getCategoriesIdsArrayByMode(categoryMode);
		break;
	    }
	    case 3: {
		setScreenTitle(getMessage("title_employeeJobs"));
		categoriesIds = getCategoriesIdsArrayByMode(categoryMode);
		break;
	    }
	    case -1:
		setScreenTitle(getMessage("title_jobs"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} else {
	    categoriesIds = null;
	    setScreenTitle(getMessage("title_jobs"));
	}
    }

    public void searchJobs() {
	try {
	    Integer[] status;
	    switch (mode) {
	    case 1: /*
		     * Get not cancelled Jobs (used in Rename[O,S] /Move[P] /Scale[S] on jobs)
		     */
		searchJobList = JobsService.getNotCancelledJobs(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, regionId);
		break;
	    case 2: /*
		     * Get non-reserve (non frozen and vacant) jobs (used in Rename[P] /Scale[P] /Freeze[S])
		     */
		searchJobList = JobsService.getVacantJobs(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, rankId, regionId, false, false);
		break;
	    case 3: /*
		     * Get non-reserved (vacant and frozen) jobs (used in Reserve[S] /Cancel[O,S,P])
		     */
		searchJobList = JobsService.getVacantJobs(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, rankId, regionId, true, false);
		break;
	    case 4: /* Get frozen jobs (used in UnFreeze[S]) */
		searchJobList = JobsService.getFrozenJobs(searchUnitFullName, searchJobCode, searchJobName, regionId);
		break;
	    case 5: /*
		     * Get occupied jobs (used in Search[O,S,P]) for Emps Inquiry
		     */
		searchJobList = JobsService.getOccupiedJobs(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, rankId);
		break;
	    case 6: /*
		     * Get vacant jobs in unit and it's children (used in movements)
		     */
		status = new Integer[] { JobStatusEnum.VACANT.getCode() };
		if (unitHKey == null || unitHKey.equals("-1") || unitHKey.isEmpty())
		    unitHKey = null;
		searchJobList = JobsService.getJobsByUnitHKey(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, unitHKey, status, regionId);
		break;
	    case 7: /*
		     * Get vacant jobs by recruitment info (used in recruitments)
		     */
		if (unitHKey == null || unitHKey.equals("-1") || unitHKey.isEmpty())
		    unitHKey = null;
		searchJobList = JobsService.getVacantJobsByRecruitmentInfo(searchUnitFullName, searchJobCode, searchJobName, (long) categoryMode, rankId, regionId, minorSpecId, unitHKey);
		break;
	    case 8: /*
		     * Get vacant and occupied jobs in unit and it's children (used in movements)
		     */
		status = new Integer[] { JobStatusEnum.VACANT.getCode(), JobStatusEnum.OCCUPIED.getCode() };
		if (unitHKey == null || unitHKey.equals("-1") || unitHKey.isEmpty())
		    unitHKey = null;
		searchJobList = JobsService.getJobsByUnitHKey(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, unitHKey, status, regionId);
		break;
	    case 9: /*
		     * Get reserved (non frozen and vacant) jobs (used in Reserve[P])
		     */
		searchJobList = JobsService.getVacantJobs(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, rankId, regionId, false, true);
		break;
	    case 10: /*
		      * Get reserved (frozen and vacant) jobs (used in Reserve[P])
		      */
		searchJobList = JobsService.getVacantJobs(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, rankId, regionId, true, true);
		break;
	    case 11: /*
		      * Get vacant and occupied jobs.
		      */
		status = new Integer[] { JobStatusEnum.VACANT.getCode(), JobStatusEnum.OCCUPIED.getCode() };
		searchJobList = JobsService.getVacantAndOccupiedJobs(searchUnitFullName, searchJobCode, searchJobName, categoriesIds, rankId, status, regionId);
		break;
	    case 12:
		/*
		 * Get vacant and reserved for promotion jobs.
		 */
		if (unitHKey == null || unitHKey.equals("-1") || unitHKey.isEmpty())
		    unitHKey = null;
		searchJobList = JobsService.getVacantJobsByPromotionInfo(searchUnitFullName, searchJobCode, searchJobName, (long) categoryMode, rankId, regionId, minorSpecId, unitHKey);
		break;
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addSelectedJob(JobData job) {
	// Check MAX list size
	if (selectedJobList.size() == SELECTED_JOBS_MAX) {
	    super.setServerSideErrorMessages(getMessage("error_jobsListMax"));
	    return;
	}

	// Check duplicate records
	if (("," + selectedJobsIds + ",").contains("," + job.getId() + ",")) {
	    super.setServerSideErrorMessages(getMessage("error_repeatedJob"));
	    return;
	}

	// Add to list
	searchJobList.remove(job);
	selectedJobList.add(job);
	selectedJobsIds += comma + job.getId();
	comma = ",";
    }

    public void removeSelectedJob(JobData job) {
	selectedJobList.remove(job);
	searchJobList.add(job);

	if (selectedJobsIds.equals(job.getId() + "")) {
	    selectedJobsIds = "";
	    comma = "";
	} else {
	    selectedJobsIds = ("," + selectedJobsIds + ",").replace("," + job.getId() + ",", ",");
	    selectedJobsIds = selectedJobsIds.substring(1, selectedJobsIds.length() - 1);
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public List<JobData> getsearchJobList() {
	return searchJobList;
    }

    public void setsearchJobList(List<JobData> searchJobList) {
	this.searchJobList = searchJobList;
    }

    public String getSearchJobCode() {
	return searchJobCode;
    }

    public void setSearchJobCode(String searchJobCode) {
	this.searchJobCode = searchJobCode;
    }

    public String getSearchJobName() {
	return searchJobName;
    }

    public void setSearchJobName(String searchJobName) {
	this.searchJobName = searchJobName;
    }

    public List<JobData> getSearchJobList() {
	return searchJobList;
    }

    public void setSearchJobList(List<JobData> searchJobList) {
	this.searchJobList = searchJobList;
    }

    public String getSearchUnitFullName() {
	return searchUnitFullName;
    }

    public void setSearchUnitFullName(String searchUnitFullName) {
	this.searchUnitFullName = searchUnitFullName;
    }

    public String getSelectedJobsIds() {
	return selectedJobsIds;
    }

    public void setSelectedJobsIds(String selectedJobsIds) {
	this.selectedJobsIds = selectedJobsIds;
    }

    public List<JobData> getSelectedJobList() {
	return selectedJobList;
    }

    public void setSelectedJobList(List<JobData> selectedJobList) {
	this.selectedJobList = selectedJobList;
    }

    public boolean isMultipleSelectFlag() {
	return multipleSelectFlag;
    }

}