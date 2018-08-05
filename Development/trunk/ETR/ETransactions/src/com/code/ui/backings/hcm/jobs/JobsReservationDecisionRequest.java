package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.workflow.hcm.organization.jobs.WFJobData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.JobReservationStatusEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.JobsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "jobsReservationDecisionRequest")
@ViewScoped
public class JobsReservationDecisionRequest extends WFBaseBacking {

    /**
     * 1 for Reservation and 2 for UnReserve
     **/
    private int mode;
    private Long regionId;

    private int reservationStatus;
    private String referring;
    private String remarks;
    private List<EmployeeData> internalCopies;
    private String externalCopies;
    private String decisionReference;

    private String decisionNumber;
    private Date decisionDate;

    private List<WFJobData> wfJobs;
    private String selectedJobsIds;

    private List<EmployeeData> reviewerEmps;
    private Long selectedReviewerEmpId;

    private final int pageSize = 10;

    public JobsReservationDecisionRequest() {
	super.init();
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    this.setScreenTitle(getMessage("title_jobsReserveDecisionRequest"));
		    this.processId = WFProcessesEnum.SOLDIERS_JOBS_RESERVATION.getCode();
		    break;
		case 2:
		    this.setScreenTitle(getMessage("title_jobsUnreserveDecisionRequest"));
		    this.processId = WFProcessesEnum.SOLDIERS_JOBS_UNRESERVATION.getCode();
		    break;
		default:
		    this.setServerSideErrorMessages(getMessage("error_general"));
		}

		if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		    wfJobs = new ArrayList<WFJobData>();
		    reservationStatus = (this.processId == WFProcessesEnum.SOLDIERS_JOBS_RESERVATION.getCode()) ? JobReservationStatusEnum.RESERVED.getCode() : JobReservationStatusEnum.UN_RESERVED.getCode();
		    referring = "";
		    remarks = "";
		    regionId = getLoginEmpPhysicalRegionFlag(true);

		    internalCopies = new ArrayList<EmployeeData>();
		    externalCopies = "";
		    decisionReference = "";
		} else {
		    wfJobs = JobsWorkFlow.getWFJobsByInstanceId(this.instance.getInstanceId());

		    reservationStatus = wfJobs.get(0).getNewReservationStatus();
		    referring = wfJobs.get(0).getReferring();
		    remarks = wfJobs.get(0).getRemarks();
		    regionId = (this.requester.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) ? FlagsEnum.ALL.getCode() : this.requester.getPhysicalRegionId();

		    internalCopies = EmployeesService.getEmployeesByIdsString(wfJobs.get(0).getInternalCopies());
		    externalCopies = wfJobs.get(0).getExternalCopies();
		    decisionReference = wfJobs.get(0).getDecisionReference();

		    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
			selectedReviewerEmpId = 0L;
			reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		    }
		}
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    // Add/Enroll Jobs
    public void addJobs(boolean isEnroll) {
	List<JobData> jobs = new ArrayList<>();
	try {
	    if (isEnroll) {
		if (decisionNumber != null && !decisionNumber.isEmpty() && decisionDate != null) {
		    if (this.processId == WFProcessesEnum.SOLDIERS_JOBS_RESERVATION.getCode())
			jobs = JobsService.getJobsByDecisionInfoForReservation(decisionNumber, decisionDate, regionId, CategoriesEnum.SOLDIERS.getCode());
		    else
			jobs = JobsService.getJobsByDecisionInfoForUnReserve(decisionNumber, decisionDate, regionId, CategoriesEnum.SOLDIERS.getCode());
		}
	    } else {
		if (selectedJobsIds != null && !selectedJobsIds.isEmpty())
		    jobs = JobsService.getJobsByIdsString(selectedJobsIds);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return;
	}

	int transactionTypeCode = (this.processId == WFProcessesEnum.SOLDIERS_JOBS_RESERVATION.getCode()) ? TransactionTypesEnum.JOB_RESERVE.getCode() : TransactionTypesEnum.JOB_UNRESERVE.getCode();
	String duplicatedJobsIfAny = "";
	int duplicatedJobsCount = 0;
	String comma = "";
	for (JobData jobData : jobs) {
	    try {
		validateDuplicateJob(jobData.getId());
		wfJobs.add(JobsWorkFlow.constructWFJob(jobData, transactionTypeCode));
	    } catch (BusinessException e) {
		duplicatedJobsIfAny += comma + " - " + jobData.getCode();
		duplicatedJobsCount++;
		comma = ", \n ";
	    }
	}
	if (duplicatedJobsCount > 0)
	    this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfJobs", new Object[] { duplicatedJobsCount, duplicatedJobsIfAny }));
    }

    private void validateDuplicateJob(Long jobId) throws BusinessException {
	for (WFJobData current : wfJobs) {
	    if (jobId.equals(current.getJobId()))
		throw new BusinessException("error_repeatedJob");
	}
    }

    // Delete WFJob
    public void deleteWFJob(WFJobData wfJobData) {
	try {
	    if (wfJobData.getId() != null)
		JobsWorkFlow.deleteWFJob(wfJobData, this.loginEmpData.getEmpId());
	    wfJobs.remove(wfJobData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /*****************************************************************************************************************/
    // WorkFlow Steps
    // TODO no need to send the map if the action is REJECT or RETURN TO REVIEWER ???? and also employees (to save the string manipulation)
    // initialize the process by requester
    public String initProcess() {
	try {
	    Map<Long, List<WFJobData>> wfJobsMap = new HashMap<Long, List<WFJobData>>();
	    if (wfJobs != null && !wfJobs.isEmpty())
		wfJobsMap.put(getTransactionTypeId(this.processId), wfJobs);

	    JobsWorkFlow.initJob(this.requester, CategoriesEnum.SOLDIERS.getCode(), referring, null, remarks, wfJobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, reservationStatus, this.processId, this.attachments, this.taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve by secondary reviewer employee (the requester)
    public String approveSRE() {
	try {
	    Map<Long, List<WFJobData>> wfJobsMap = new HashMap<Long, List<WFJobData>>();
	    if (wfJobs != null && !wfJobs.isEmpty())
		wfJobsMap.put(getTransactionTypeId(this.processId), wfJobs);

	    JobsWorkFlow.doJobSRE(this.requester, this.instance, CategoriesEnum.SOLDIERS.getCode(), referring, null, remarks, wfJobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, reservationStatus, this.attachments, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by secondary reviewer employee (the requester)
    public String rejectSRE() {
	try {
	    Map<Long, List<WFJobData>> wfJobsMap = new HashMap<Long, List<WFJobData>>();
	    wfJobsMap.put(getTransactionTypeId(this.processId), wfJobs);
	    JobsWorkFlow.doJobSRE(this.requester, this.instance, CategoriesEnum.SOLDIERS.getCode(), referring, null, remarks, wfJobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, reservationStatus, this.attachments, this.currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve by secondary sign manager
    public String approveSSM() {
	try {
	    JobsWorkFlow.doJobSSM(this.requester, this.instance, this.currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // return to secondary reviewer employee by secondary sign manager
    public String modifySSM() {
	try {
	    JobsWorkFlow.doJobSSM(this.requester, this.instance, this.currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by secondary sign manager
    public String rejectSSM() {
	try {
	    JobsWorkFlow.doJobSSM(this.requester, this.instance, this.currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // redirect to reviewer employee by manager redirect
    public String redirectMR() {
	try {
	    JobsWorkFlow.doJobMR(this.requester, this.instance, this.currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve by reviewer employee
    public String approveRE() {
	try {
	    Map<Long, List<WFJobData>> wfJobsMap = new HashMap<Long, List<WFJobData>>();
	    if (wfJobs != null && !wfJobs.isEmpty())
		wfJobsMap.put(getTransactionTypeId(this.processId), wfJobs);

	    JobsWorkFlow.doJobRE(this.requester, this.instance, CategoriesEnum.SOLDIERS.getCode(), referring, null, remarks, wfJobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, reservationStatus, this.attachments, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by reviewer employee
    public String rejectRE() {
	try {
	    Map<Long, List<WFJobData>> wfJobsMap = new HashMap<Long, List<WFJobData>>();
	    wfJobsMap.put(getTransactionTypeId(this.processId), wfJobs);
	    JobsWorkFlow.doJobRE(this.requester, this.instance, CategoriesEnum.SOLDIERS.getCode(), referring, null, remarks, wfJobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, reservationStatus, this.attachments, this.currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve by sign manager
    public String approveSM() {
	try {
	    Map<Long, List<WFJobData>> wfJobsMap = new HashMap<Long, List<WFJobData>>();
	    wfJobsMap.put(getTransactionTypeId(this.processId), wfJobs);
	    JobsWorkFlow.doJobSM(this.requester, this.instance, CategoriesEnum.SOLDIERS.getCode(), referring, null, remarks, wfJobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, reservationStatus, this.currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // return to the reviewer employee by sign manager
    public String modifySM() {
	try {
	    Map<Long, List<WFJobData>> wfJobsMap = new HashMap<Long, List<WFJobData>>();
	    wfJobsMap.put(getTransactionTypeId(this.processId), wfJobs);
	    JobsWorkFlow.doJobSM(this.requester, this.instance, CategoriesEnum.SOLDIERS.getCode(), referring, null, remarks, wfJobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, reservationStatus, this.currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by sign manager
    public String rejectSM() {
	try {
	    Map<Long, List<WFJobData>> wfJobsMap = new HashMap<Long, List<WFJobData>>();
	    wfJobsMap.put(getTransactionTypeId(this.processId), wfJobs);
	    JobsWorkFlow.doJobSM(this.requester, this.instance, CategoriesEnum.SOLDIERS.getCode(), referring, null, remarks, wfJobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, reservationStatus, this.currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // close the process
    public String closeProcess() {
	try {
	    JobsWorkFlow.closeWFInstanceByNotification(this.instance, this.currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    /*****************************************************************************************************************/

    public void print() {
	try {
	    String[] decisionInfo = decisionReference.split(",");
	    byte[] bytes = JobsService.getJobDecisionBytes(30,
		    decisionInfo[0], HijriDateService.getHijriDate(decisionInfo[1]));
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /*****************************************************************************************************************/

    private Long getTransactionTypeId(long processId) {
	return JobsWorkFlow.getTransactionTypeId(processId == WFProcessesEnum.SOLDIERS_JOBS_RESERVATION.getCode() ? TransactionTypesEnum.JOB_RESERVE.getCode() : TransactionTypesEnum.JOB_UNRESERVE.getCode());
    }

    public int getMode() {
	return mode;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public int getReservationStatus() {
	return reservationStatus;
    }

    public void setReservationStatus(int reservationStatus) {
	this.reservationStatus = reservationStatus;
    }

    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
    }

    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }

    public String getDecisionReference() {
	return decisionReference;
    }

    public void setDecisionReference(String decisionReference) {
	this.decisionReference = decisionReference;
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

    public List<WFJobData> getWfJobs() {
	return wfJobs;
    }

    public void setWfJobs(List<WFJobData> wfJobs) {
	this.wfJobs = wfJobs;
    }

    public String getSelectedJobsIds() {
	return selectedJobsIds;
    }

    public void setSelectedJobsIds(String selectedJobsIds) {
	this.selectedJobsIds = selectedJobsIds;
    }

    public List<EmployeeData> getReviewerEmps() {
	return reviewerEmps;
    }

    public void setReviewerEmps(List<EmployeeData> reviewerEmps) {
	this.reviewerEmps = reviewerEmps;
    }

    public Long getSelectedReviewerEmpId() {
	return selectedReviewerEmpId;
    }

    public void setSelectedReviewerEmpId(Long selectedReviewerEmpId) {
	this.selectedReviewerEmpId = selectedReviewerEmpId;
    }

    public int getPageSize() {
	return pageSize;
    }

}
