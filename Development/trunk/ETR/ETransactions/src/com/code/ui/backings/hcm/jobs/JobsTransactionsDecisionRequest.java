package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
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
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.JobsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "jobsTransactionsDecisionRequest")
@ViewScoped
public class JobsTransactionsDecisionRequest extends WFBaseBacking {

    /**
     * 1 for Officers and 2 for Soldiers
     **/
    private int mode;
    private long regionId;
    private List<Rank> ranks;

    private String referring;
    private String reasons;
    private String remarks;
    private List<EmployeeData> internalCopies;
    private String externalCopies;
    private String decisionReference;

    private String decisionNumber;
    private Date decisionDate;

    private List<WFJobData> newJobs;
    private List<WFJobData> modifiedJobs;
    private List<WFJobData> minorSpecsModifiedJobs;
    private List<WFJobData> freezedJobs;
    private List<WFJobData> unFreezedJobs;
    private List<WFJobData> movedJobs;
    private List<WFJobData> cancelledJobs;

    // optional data used for jobs construction
    private String selectedUnitsIds;
    private String selectedUnitsFullNames;
    private List<UnitData> selectedUnits;
    private Long selectedRankId;
    private Long selectedMinorSpecId;
    private String selectedMinorSpecDesc;
    private Integer selectedGeneralSpec;
    private Integer selectedGeneralType;
    private String selectedBasicJobsNamesIds;
    private String selectedBasicJobsNames;
    private final int initialJobsCount = 1;

    // optional data used for jobs renaming/scaling
    private Long selectedNewBasicJobNameId;
    private String selectedNewName;
    private Long selectedNewRankId;

    // optional data used for jobs minor specialization modification
    private Long selectedNewMinorSpecId;
    private String selectedNewMinorSpecDesc;
    private Integer selectedNewGeneralSpecId;
    private Integer selectedNewGeneralTypeId;
    private Long selectedNewBasicJobNameIdForModifyMinorSpec;
    private String selectedNewNameForModifyMinorSpec;

    // optional data used for jobs moving
    private Long selectedNewUnitId;
    private String selectedNewUnitFullName;
    private Long selectedNewRegionId;

    private String selectedJobsIds;

    private List<EmployeeData> reviewerEmps;
    private Long selectedReviewerEmpId;

    private final int pageSize = 10;

    public JobsTransactionsDecisionRequest() {
	super.init();
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    this.setScreenTitle(getMessage("title_officersJobsTransactionsDescisionRequest"));
		    this.processId = WFProcessesEnum.OFFICERS_JOBS_TRANSACTIONS.getCode();
		    break;
		case 2:
		    this.setScreenTitle(getMessage("title_soldiersJobsTransactionsDecisionRequest"));
		    this.processId = WFProcessesEnum.SOLDIERS_JOBS_TRANSACTIONS.getCode();
		    break;
		default:
		    this.setServerSideErrorMessages(getMessage("error_URLError"));
		}

		ranks = CommonService.getRanks(null, new Long[] { (long) mode });
		ranks.remove(ranks.size() - 1);

		newJobs = new ArrayList<WFJobData>();
		modifiedJobs = new ArrayList<WFJobData>();
		minorSpecsModifiedJobs = new ArrayList<WFJobData>();
		freezedJobs = new ArrayList<WFJobData>();
		unFreezedJobs = new ArrayList<WFJobData>();
		movedJobs = new ArrayList<WFJobData>();
		cancelledJobs = new ArrayList<WFJobData>();

		if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		    referring = "";
		    reasons = "";
		    remarks = "";
		    regionId = getLoginEmpPhysicalRegionFlag(true);

		    internalCopies = new ArrayList<EmployeeData>();
		    externalCopies = "";
		    decisionReference = "";

		    decisionNumber = "";
		    decisionDate = null;

		    selectedUnits = new ArrayList<UnitData>();
		    selectedBasicJobsNames = "";
		    selectedJobsIds = "";
		} else {
		    List<WFJobData> jobs = JobsWorkFlow.getWFJobsByInstanceId(this.instance.getInstanceId());

		    populateWFJobsLists(jobs);

		    referring = jobs.get(0).getReferring();
		    reasons = jobs.get(0).getReasons();
		    remarks = jobs.get(0).getRemarks();
		    regionId = this.requester.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : this.requester.getPhysicalRegionId();

		    internalCopies = EmployeesService.getEmployeesByIdsString(jobs.get(0).getInternalCopies());
		    externalCopies = jobs.get(0).getExternalCopies();
		    decisionReference = jobs.get(0).getDecisionReference();

		    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
			selectedReviewerEmpId = 0L;
			reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		    }
		}
	    } else {
		this.setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    private void populateWFJobsLists(List<WFJobData> wfJobs) {
	for (WFJobData wfJob : wfJobs) {
	    if (wfJob.getTransactionTypeId().equals(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode())))
		newJobs.add(wfJob);
	    else if (wfJob.getTransactionTypeId().equals(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode())))
		minorSpecsModifiedJobs.add(wfJob);
	    else if (wfJob.getTransactionTypeId().equals(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_FREEZE.getCode())))
		freezedJobs.add(wfJob);
	    else if (wfJob.getTransactionTypeId().equals(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_UNFREEZE.getCode())))
		unFreezedJobs.add(wfJob);
	    else if (wfJob.getTransactionTypeId().equals(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_MOVE.getCode())))
		movedJobs.add(wfJob);
	    else if (wfJob.getTransactionTypeId().equals(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_CANCELLATION.getCode())))
		cancelledJobs.add(wfJob);
	    else // rename, scale up, and scale down
		modifiedJobs.add(wfJob);
	}
    }

    public void addUnits() {
	if (selectedUnitsIds != null && !selectedUnitsIds.isEmpty()) {
	    try {
		selectedUnits = UnitsService.getUnitsByIdsString(selectedUnitsIds);
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
		return;
	    }
	    selectedUnitsFullNames = "";
	    String comma = "";
	    for (UnitData unit : selectedUnits) {
		selectedUnitsFullNames += comma + unit.getFullName();
		comma = ",";
	    }
	}
    }

    // we can remove this listener and check on the selectedUnitsIds not only the selectedUnits list when adding new jobs
    public void resetUnits() {
	selectedUnitsIds = "";
	selectedUnitsFullNames = "";
	selectedUnits = new ArrayList<UnitData>();
    }

    /*****************************************************************************************************************/
    // Add jobs
    public void addNewJobs() {
	if (selectedBasicJobsNamesIds != null && !selectedBasicJobsNamesIds.isEmpty()) {

	    String[] basicJobsNames = selectedBasicJobsNames.split(",");
	    String[] basicJobsNamesIds = selectedBasicJobsNamesIds.split(",");

	    if (selectedUnits != null && selectedUnits.size() > 0) {
		for (UnitData unit : selectedUnits) {
		    for (int i = 0; i < basicJobsNames.length; i++) {
			WFJobData wfJobData = new WFJobData();
			wfJobData.setNewName(basicJobsNames[i]);
			wfJobData.setNewBasicJobNameId(Long.parseLong(basicJobsNamesIds[i]));
			wfJobData.setNewUnitId(unit.getId());
			wfJobData.setNewUnitFullName(unit.getFullName());
			wfJobData.setNewRegionId(unit.getRegionId());
			wfJobData.setNewRankId(selectedRankId);
			wfJobData.setNewMinorSpecializationId(selectedMinorSpecId);
			wfJobData.setNewMinorSpecializationDesc(selectedMinorSpecDesc);
			wfJobData.setNewGeneralType(selectedGeneralType);
			wfJobData.setNewGeneralSpecialization(selectedGeneralSpec);
			wfJobData.setNewApprovedFlag(FlagsEnum.ON.getCode());
			wfJobData.setNewReservationStatus(JobReservationStatusEnum.UN_RESERVED.getCode());
			wfJobData.setJobsCount(initialJobsCount);
			wfJobData.setTransactionTypeId(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode()));
			newJobs.add(wfJobData);
		    }
		}
	    } else {
		for (int i = 0; i < basicJobsNames.length; i++) {
		    WFJobData wfJobData = new WFJobData();
		    wfJobData.setNewName(basicJobsNames[i]);
		    wfJobData.setNewBasicJobNameId(Long.parseLong(basicJobsNamesIds[i]));
		    wfJobData.setNewRankId(selectedRankId);
		    wfJobData.setNewMinorSpecializationId(selectedMinorSpecId);
		    wfJobData.setNewMinorSpecializationDesc(selectedMinorSpecDesc);
		    wfJobData.setNewGeneralType(selectedGeneralType);
		    wfJobData.setNewGeneralSpecialization(selectedGeneralSpec);
		    wfJobData.setNewApprovedFlag(FlagsEnum.ON.getCode());
		    wfJobData.setNewReservationStatus(JobReservationStatusEnum.UN_RESERVED.getCode());
		    wfJobData.setJobsCount(initialJobsCount);
		    wfJobData.setTransactionTypeId(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode()));
		    newJobs.add(wfJobData);
		}
	    }
	}
    }

    public void addModifiedJobs() {
	addJobs(modifiedJobs, TransactionTypesEnum.JOB_RENAME.getCode());
    }

    public void addMinorSpecsModifiedJobs() {
	addJobs(minorSpecsModifiedJobs, TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode());
    }

    public void addFreezedJobs() {
	addJobs(freezedJobs, TransactionTypesEnum.JOB_FREEZE.getCode());
    }

    public void addUnFreezedJobs() {
	addJobs(unFreezedJobs, TransactionTypesEnum.JOB_UNFREEZE.getCode());
    }

    public void addMovedJobs() {
	addJobs(movedJobs, TransactionTypesEnum.JOB_MOVE.getCode());
    }

    public void addCancelledJobs() {
	addJobs(cancelledJobs, TransactionTypesEnum.JOB_CANCELLATION.getCode());
    }

    public void enrollJobs() {
	List<JobData> jobs = new ArrayList<JobData>();
	try {
	    jobs = JobsService.getJobsByDecisionInfoForUnFreeze(decisionNumber, decisionDate, this.getLoginEmpPhysicalRegionFlag(true), mode);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return;
	}

	String duplicatedJobsIfAny = "";
	int duplicatedJobsCount = 0;
	String comma = "";
	for (JobData job : jobs) {
	    try {
		validateDuplicateJob(job.getId());
		unFreezedJobs.add(JobsWorkFlow.constructWFJob(job, TransactionTypesEnum.JOB_UNFREEZE.getCode()));
	    } catch (BusinessException e) {
		duplicatedJobsIfAny += comma + " - " + job.getCode();
		duplicatedJobsCount++;
		comma = ", \n ";
	    }
	}
	if (duplicatedJobsCount > 0)
	    this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfJobs", new Object[] { duplicatedJobsCount, duplicatedJobsIfAny }));
    }

    private void addJobs(List<WFJobData> wfJobs, int transactionTypeCode) {
	if (selectedJobsIds != null && !selectedJobsIds.isEmpty()) {

	    List<JobData> jobs = new ArrayList<JobData>();
	    try {
		jobs = JobsService.getJobsByIdsString(selectedJobsIds);
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }

	    String duplicatedJobsIfAny = "";
	    int duplicatedJobsCount = 0;
	    String comma = "";
	    for (JobData job : jobs) {
		try {
		    validateDuplicateJob(job.getId());
		    WFJobData wfJobData = JobsWorkFlow.constructWFJob(job, transactionTypeCode);

		    if (transactionTypeCode == TransactionTypesEnum.JOB_RENAME.getCode()) {
			wfJobData.setNewBasicJobNameId(selectedNewBasicJobNameId);
			wfJobData.setNewName(selectedNewName);
			if (selectedNewRankId != null && !selectedNewRankId.equals((long) FlagsEnum.ALL.getCode())
				&& !wfJobData.getRankId().equals(selectedNewRankId) && job.getApprovedFlag() == FlagsEnum.OFF.getCode()) {
			    wfJobData.setNewRankId(selectedNewRankId);
			}
		    } else if (transactionTypeCode == TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode()) {
			wfJobData.setNewMinorSpecializationId(selectedNewMinorSpecId);
			wfJobData.setNewMinorSpecializationDesc(selectedNewMinorSpecDesc);
			wfJobData.setNewGeneralSpecialization(selectedNewGeneralSpecId);
			wfJobData.setNewGeneralType(selectedNewGeneralTypeId);
			wfJobData.setNewBasicJobNameId(selectedNewBasicJobNameIdForModifyMinorSpec);
			wfJobData.setNewName(selectedNewNameForModifyMinorSpec);

		    } else if (transactionTypeCode == TransactionTypesEnum.JOB_MOVE.getCode()) {
			wfJobData.setNewUnitId(selectedNewUnitId);
			wfJobData.setNewUnitFullName(selectedNewUnitFullName);
			wfJobData.setNewRegionId(selectedNewRegionId);
		    }
		    wfJobs.add(wfJobData);
		} catch (BusinessException e) {
		    duplicatedJobsIfAny += comma + " - " + job.getCode();
		    duplicatedJobsCount++;
		    comma = ", \n ";
		}
	    }
	    if (duplicatedJobsCount > 0)
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfJobs", new Object[] { duplicatedJobsCount, duplicatedJobsIfAny }));
	}
    }

    private void validateDuplicateJob(Long jobId) throws BusinessException {
	for (WFJobData current : modifiedJobs) {
	    if (jobId.equals(current.getJobId()))
		throw new BusinessException("error_repeatedJob");
	}
	for (WFJobData current : minorSpecsModifiedJobs) {
	    if (jobId.equals(current.getJobId()))
		throw new BusinessException("error_repeatedJob");
	}
	for (WFJobData current : freezedJobs) {
	    if (jobId.equals(current.getJobId()))
		throw new BusinessException("error_repeatedJob");
	}
	for (WFJobData current : unFreezedJobs) {
	    if (jobId.equals(current.getJobId()))
		throw new BusinessException("error_repeatedJob");
	}
	for (WFJobData current : movedJobs) {
	    if (jobId.equals(current.getJobId()))
		throw new BusinessException("error_repeatedJob");
	}
	for (WFJobData current : cancelledJobs) {
	    if (jobId.equals(current.getJobId()))
		throw new BusinessException("error_repeatedJob");
	}
    }

    /*****************************************************************************************************************/
    // Delete Jobs
    public void deleteNewJob(WFJobData wfJobData) {
	deleteJob(wfJobData, newJobs);
    }

    public void deleteModifiedJob(WFJobData wfJobData) {
	deleteJob(wfJobData, modifiedJobs);
    }

    public void deleteMinorSpecsModifiedJob(WFJobData wfJobData) {
	deleteJob(wfJobData, minorSpecsModifiedJobs);
    }

    public void deleteFreezedJob(WFJobData wfJobData) {
	deleteJob(wfJobData, freezedJobs);
    }

    public void deleteUnFreezedJob(WFJobData wfJobData) {
	deleteJob(wfJobData, unFreezedJobs);
    }

    public void deleteMovedJob(WFJobData wfJobData) {
	deleteJob(wfJobData, movedJobs);
    }

    public void deleteCancelledJob(WFJobData wfJobData) {
	deleteJob(wfJobData, cancelledJobs);
    }

    private void deleteJob(WFJobData wfJobData, List<WFJobData> wfJobs) {
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
    // TODO don't construct the map if we won't use it (like rejection or returning to RE)
    // initialize the process by requester
    public String initProcess() {
	try {
	    Map<Long, List<WFJobData>> jobsMap = constructWFJobsMap();
	    JobsWorkFlow.initJob(this.requester, mode, referring, reasons, remarks, jobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, null, this.processId, this.attachments, this.taskUrl); //
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve by secondary reviewer employee (the requester)
    public String approveSRE() {
	try {
	    Map<Long, List<WFJobData>> jobsMap = constructWFJobsMap();
	    JobsWorkFlow.doJobSRE(this.requester, this.instance, mode, referring, reasons, remarks, jobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, null, this.attachments, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by secondary reviewer employee (the requester)
    public String rejectSRE() {
	try {
	    Map<Long, List<WFJobData>> jobsMap = constructWFJobsMap();
	    JobsWorkFlow.doJobSRE(this.requester, this.instance, mode, referring, reasons, remarks, jobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, null, this.attachments, this.currentTask, true);
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
	    Map<Long, List<WFJobData>> jobsMap = constructWFJobsMap();
	    JobsWorkFlow.doJobRE(this.requester, this.instance, mode, referring, reasons, remarks, jobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, null, this.attachments, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by reviewer employee
    public String rejectRE() {
	try {
	    Map<Long, List<WFJobData>> jobsMap = constructWFJobsMap();
	    JobsWorkFlow.doJobRE(this.requester, this.instance, mode, referring, reasons, remarks, jobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, null, this.attachments, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve by sign manager
    public String approveSM() {
	try {
	    Map<Long, List<WFJobData>> jobsMap = constructWFJobsMap();
	    JobsWorkFlow.doJobSM(this.requester, this.instance, mode, referring, reasons, remarks, jobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, null, this.currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // return to the reviewer employee by sign manager
    public String rejectSM() {
	try {
	    Map<Long, List<WFJobData>> jobsMap = constructWFJobsMap();
	    JobsWorkFlow.doJobSM(this.requester, this.instance, mode, referring, reasons, remarks, jobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, null, this.currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by sign manager
    public String modifySM() {
	try {
	    Map<Long, List<WFJobData>> jobsMap = constructWFJobsMap();
	    JobsWorkFlow.doJobSM(this.requester, this.instance, mode, referring, reasons, remarks, jobsMap, EmployeesService.getEmployeesIdsString(internalCopies), externalCopies, null, this.currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
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

    private Map<Long, List<WFJobData>> constructWFJobsMap() {
	Map<Long, List<WFJobData>> wfJobs = new HashMap<Long, List<WFJobData>>();

	if (newJobs != null && !newJobs.isEmpty())
	    wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_CONSTRUCTION.getCode()), newJobs);

	if (modifiedJobs != null && !modifiedJobs.isEmpty()) {
	    List<WFJobData> renamedJobs = new ArrayList<WFJobData>();
	    List<WFJobData> scaledJobsUp = new ArrayList<WFJobData>();
	    List<WFJobData> scaledJobsDown = new ArrayList<WFJobData>();

	    for (WFJobData wfJobData : modifiedJobs) {
		if (wfJobData.getNewRankId() != null && !wfJobData.getNewRankId().equals((long) FlagsEnum.ALL.getCode())
			&& !wfJobData.getRankId().equals(wfJobData.getNewRankId())) {
		    if (wfJobData.getRankId().longValue() < wfJobData.getNewRankId().longValue()) {
			wfJobData.setTransactionTypeId(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_DOWN.getCode()));
			scaledJobsDown.add(wfJobData);
		    } else {
			wfJobData.setTransactionTypeId(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_UP.getCode()));
			scaledJobsUp.add(wfJobData);
		    }
		} else
		    renamedJobs.add(wfJobData);
	    }
	    if (renamedJobs != null && !renamedJobs.isEmpty())
		wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_RENAME.getCode()), renamedJobs);
	    if (scaledJobsUp != null && !scaledJobsUp.isEmpty())
		wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_UP.getCode()), scaledJobsUp);
	    if (scaledJobsDown != null && !scaledJobsDown.isEmpty())
		wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_SCALE_DOWN.getCode()), scaledJobsDown);
	}

	if (minorSpecsModifiedJobs != null && !minorSpecsModifiedJobs.isEmpty())
	    wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode()), minorSpecsModifiedJobs);

	if (freezedJobs != null && !freezedJobs.isEmpty())
	    wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_FREEZE.getCode()), freezedJobs);

	if (unFreezedJobs != null && !unFreezedJobs.isEmpty())
	    wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_UNFREEZE.getCode()), unFreezedJobs);

	if (movedJobs != null && !movedJobs.isEmpty())
	    wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_MOVE.getCode()), movedJobs);

	if (cancelledJobs != null && !cancelledJobs.isEmpty())
	    wfJobs.put(JobsWorkFlow.getTransactionTypeId(TransactionTypesEnum.JOB_CANCELLATION.getCode()), cancelledJobs);

	return wfJobs;
    }

    /*****************************************************************************************************************/

    public void print() {
	try {
	    String[] decisionInfo = decisionReference.split(",");
	    byte[] bytes = JobsService.getJobDecisionBytes(mode == CategoriesEnum.OFFICERS.getCode() ? 10 : 20,
		    decisionInfo[0], HijriDateService.getHijriDate(decisionInfo[1]));
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /*****************************************************************************************************************/

    public int getMode() {
	return mode;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
    }

    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
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

    public List<WFJobData> getNewJobs() {
	return newJobs;
    }

    public void setNewJobs(List<WFJobData> newJobs) {
	this.newJobs = newJobs;
    }

    public List<WFJobData> getModifiedJobs() {
	return modifiedJobs;
    }

    public void setModifiedJobs(List<WFJobData> modifiedJobs) {
	this.modifiedJobs = modifiedJobs;
    }

    public List<WFJobData> getMinorSpecsModifiedJobs() {
	return minorSpecsModifiedJobs;
    }

    public void setMinorSpecsModifiedJobs(List<WFJobData> minorSpecsModifiedJobs) {
	this.minorSpecsModifiedJobs = minorSpecsModifiedJobs;
    }

    public List<WFJobData> getFreezedJobs() {
	return freezedJobs;
    }

    public void setFreezedJobs(List<WFJobData> freezedJobs) {
	this.freezedJobs = freezedJobs;
    }

    public List<WFJobData> getUnFreezedJobs() {
	return unFreezedJobs;
    }

    public void setUnFreezedJobs(List<WFJobData> unFreezedJobs) {
	this.unFreezedJobs = unFreezedJobs;
    }

    public List<WFJobData> getMovedJobs() {
	return movedJobs;
    }

    public void setMovedJobs(List<WFJobData> movedJobs) {
	this.movedJobs = movedJobs;
    }

    public List<WFJobData> getCancelledJobs() {
	return cancelledJobs;
    }

    public void setCancelledJobs(List<WFJobData> cancelledJobs) {
	this.cancelledJobs = cancelledJobs;
    }

    public String getSelectedUnitsIds() {
	return selectedUnitsIds;
    }

    public void setSelectedUnitsIds(String selectedUnitsIds) {
	this.selectedUnitsIds = selectedUnitsIds;
    }

    public String getSelectedUnitsFullNames() {
	return selectedUnitsFullNames;
    }

    public void setSelectedUnitsFullNames(String selectedUnitsFullNames) {
	this.selectedUnitsFullNames = selectedUnitsFullNames;
    }

    public List<UnitData> getSelectedUnits() {
	return selectedUnits;
    }

    public void setSelectedUnits(List<UnitData> selectedUnits) {
	this.selectedUnits = selectedUnits;
    }

    public Long getSelectedRankId() {
	return selectedRankId;
    }

    public void setSelectedRankId(Long selectedRankId) {
	this.selectedRankId = selectedRankId;
    }

    public Long getSelectedMinorSpecId() {
	return selectedMinorSpecId;
    }

    public void setSelectedMinorSpecId(Long selectedMinorSpecId) {
	this.selectedMinorSpecId = selectedMinorSpecId;
    }

    public String getSelectedMinorSpecDesc() {
	return selectedMinorSpecDesc;
    }

    public void setSelectedMinorSpecDesc(String selectedMinorSpecDesc) {
	this.selectedMinorSpecDesc = selectedMinorSpecDesc;
    }

    public Integer getSelectedGeneralSpec() {
	return selectedGeneralSpec;
    }

    public void setSelectedGeneralSpec(Integer selectedGeneralSpec) {
	this.selectedGeneralSpec = selectedGeneralSpec;
    }

    public Integer getSelectedGeneralType() {
	return selectedGeneralType;
    }

    public void setSelectedGeneralType(Integer selectedGeneralType) {
	this.selectedGeneralType = selectedGeneralType;
    }

    public String getSelectedBasicJobsNamesIds() {
	return selectedBasicJobsNamesIds;
    }

    public void setSelectedBasicJobsNamesIds(String selectedBasicJobsNamesIds) {
	this.selectedBasicJobsNamesIds = selectedBasicJobsNamesIds;
    }

    public String getSelectedBasicJobsNames() {
	return selectedBasicJobsNames;
    }

    public void setSelectedBasicJobsNames(String selectedBasicJobsNames) {
	this.selectedBasicJobsNames = selectedBasicJobsNames;
    }

    public Long getSelectedNewBasicJobNameId() {
	return selectedNewBasicJobNameId;
    }

    public void setSelectedNewBasicJobNameId(Long selectedNewBasicJobNameId) {
	this.selectedNewBasicJobNameId = selectedNewBasicJobNameId;
    }

    public String getSelectedNewName() {
	return selectedNewName;
    }

    public void setSelectedNewName(String selectedNewName) {
	this.selectedNewName = selectedNewName;
    }

    public Long getSelectedNewRankId() {
	return selectedNewRankId;
    }

    public void setSelectedNewRankId(Long selectedNewRankId) {
	this.selectedNewRankId = selectedNewRankId;
    }

    public Long getSelectedNewMinorSpecId() {
	return selectedNewMinorSpecId;
    }

    public void setSelectedNewMinorSpecId(Long selectedNewMinorSpecId) {
	this.selectedNewMinorSpecId = selectedNewMinorSpecId;
    }

    public String getSelectedNewMinorSpecDesc() {
	return selectedNewMinorSpecDesc;
    }

    public void setSelectedNewMinorSpecDesc(String selectedNewMinorSpecDesc) {
	this.selectedNewMinorSpecDesc = selectedNewMinorSpecDesc;
    }

    public Integer getSelectedNewGeneralSpecId() {
	return selectedNewGeneralSpecId;
    }

    public void setSelectedNewGeneralSpecId(Integer selectedNewGeneralSpecId) {
	this.selectedNewGeneralSpecId = selectedNewGeneralSpecId;
    }

    public Integer getSelectedNewGeneralTypeId() {
	return selectedNewGeneralTypeId;
    }

    public void setSelectedNewGeneralTypeId(Integer selectedNewGeneralTypeId) {
	this.selectedNewGeneralTypeId = selectedNewGeneralTypeId;
    }

    public Long getSelectedNewBasicJobNameIdForModifyMinorSpec() {
	return selectedNewBasicJobNameIdForModifyMinorSpec;
    }

    public void setSelectedNewBasicJobNameIdForModifyMinorSpec(Long selectedNewBasicJobNameIdForModifyMinorSpec) {
	this.selectedNewBasicJobNameIdForModifyMinorSpec = selectedNewBasicJobNameIdForModifyMinorSpec;
    }

    public String getSelectedNewNameForModifyMinorSpec() {
	return selectedNewNameForModifyMinorSpec;
    }

    public void setSelectedNewNameForModifyMinorSpec(String selectedNewNameForModifyMinorSpec) {
	this.selectedNewNameForModifyMinorSpec = selectedNewNameForModifyMinorSpec;
    }

    public Long getSelectedNewUnitId() {
	return selectedNewUnitId;
    }

    public void setSelectedNewUnitId(Long selectedNewUnitId) {
	this.selectedNewUnitId = selectedNewUnitId;
    }

    public String getSelectedNewUnitFullName() {
	return selectedNewUnitFullName;
    }

    public void setSelectedNewUnitFullName(String selectedNewUnitFullName) {
	this.selectedNewUnitFullName = selectedNewUnitFullName;
    }

    public Long getSelectedNewRegionId() {
	return selectedNewRegionId;
    }

    public void setSelectedNewRegionId(Long selectedNewRegionId) {
	this.selectedNewRegionId = selectedNewRegionId;
    }

    public int getInitialJobsCount() {
	return initialJobsCount;
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
