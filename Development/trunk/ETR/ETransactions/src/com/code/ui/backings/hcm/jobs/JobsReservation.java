package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.jobs.JobTransaction;
import com.code.enums.FlagsEnum;
import com.code.enums.JobReservationStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "jobsReservation")
@ViewScoped
public class JobsReservation extends BaseBacking {
    /**
     * 2 for Soldiers and 3 for Persons
     **/
    private final int pageSize = 10;
    private int mode;
    private int reservationStatus;
    private List<JobData> jobs;
    private String selectedJobsIds;
    private String reservationRemarks;

    public JobsReservation() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 2:
		setScreenTitle(getMessage("title_soldiersJobReservation"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsJobReservation"));
		break;

	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_URLError"));

	jobs = new ArrayList<JobData>();
	reservationStatus = JobReservationStatusEnum.RESERVED.getCode();
    }

    public void reservationStatusChangeListener(AjaxBehaviorEvent event) {
	reset();
    }

    public void addJobs() {

	if (selectedJobsIds != null && !selectedJobsIds.isEmpty()) {
	    String duplicatedJobsIfAny = "";
	    int duplicatedJobsCount = 0;
	    String comma = "";

	    List<JobData> jobsList = new ArrayList<JobData>();
	    try {
		jobsList = JobsService.getJobsByIdsString(selectedJobsIds);
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }

	    for (int i = 0; i < jobsList.size(); i++) {
		try {
		    validateDuplicateJob(jobsList.get(i).getId());
		    jobs.add(jobsList.get(i));
		} catch (BusinessException e) {
		    duplicatedJobsIfAny += comma + " - " + jobsList.get(i).getCode();
		    duplicatedJobsCount++;
		    comma = ", \n ";
		}
	    }
	    if (duplicatedJobsCount > 0)
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfJobs", new Object[] { duplicatedJobsCount, duplicatedJobsIfAny }));
	}
    }

    private void validateDuplicateJob(Long jobId) throws BusinessException {
	for (JobData current : jobs) {
	    if (jobId.equals(current.getId())) {
		throw new BusinessException("error_repeatedJob");
	    }
	}
    }

    public void deleteJob(JobData j) {
	jobs.remove(j);
    }

    public void reserveJobs() {
	try {
	    if (jobs.size() > 0) {
		JobTransaction decisionData = new JobTransaction();
		decisionData.setDecisionNumber("-");
		decisionData.setDecisionDate(HijriDateService.getHijriSysDate());
		decisionData.setTransactionUserId(this.loginEmpData.getEmpId());
		decisionData.seteFlag(FlagsEnum.OFF.getCode());

		JobsService.reserveJobs(jobs, reservationStatus, reservationRemarks, decisionData, true);
		reset();
		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    } else {
		this.setServerSideSuccessMessages(getMessage("error_atLeastOneJobShouldExist"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void unReserveJobs() {
	try {
	    if (jobs.size() > 0) {
		JobTransaction decisionData = new JobTransaction();
		decisionData.setDecisionNumber("-");
		decisionData.setDecisionDate(HijriDateService.getHijriSysDate());
		decisionData.setTransactionUserId(this.loginEmpData.getEmpId());
		decisionData.seteFlag(FlagsEnum.OFF.getCode());

		JobsService.unreserveJobs(jobs, decisionData, true);

		reset();
		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    } else {
		this.setServerSideSuccessMessages(getMessage("error_atLeastOneJobShouldExist"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    private void reset() {
	jobs = new ArrayList<JobData>();
	reservationRemarks = "";
    }

    public int getMode() {
	return mode;
    }

    public int getReservationStatus() {
	return reservationStatus;
    }

    public void setReservationStatus(int reservationStatus) {
	this.reservationStatus = reservationStatus;
    }

    public List<JobData> getJobs() {
	return jobs;
    }

    public void setJobs(List<JobData> jobs) {
	this.jobs = jobs;
    }

    public String getSelectedJobsIds() {
	return selectedJobsIds;
    }

    public void setSelectedJobsIds(String selectedJobsIds) {
	this.selectedJobsIds = selectedJobsIds;
    }

    public String getReservationRemarks() {
	return reservationRemarks;
    }

    public void setReservationRemarks(String reservationRemarks) {
	this.reservationRemarks = reservationRemarks;
    }

    public int getPageSize() {
	return pageSize;
    }

}
