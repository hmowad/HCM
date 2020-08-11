package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@ManagedBean(name = "moveDecisionRequest")
@ViewScoped
public class MoveDecisionRequest extends MovementsBase implements Serializable {

    private Long selectedEmpId;
    private long regionId;
    private List<JobData> jobsData;

    public MoveDecisionRequest() {
	if (getRequest().getParameter("mode") != null) {
	    try {
		init();
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    this.processId = WFProcessesEnum.OFFICERS_MOVE.getCode();
		    setScreenTitle(getMessage("title_officersMoveDecisionRequest"));
		    extraParams.put("skipMonthsRuleValidation", SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MVT_MOVE_OFFICERS_DECISION_REQUEST.getCode(), MenuActionsEnum.MVT_BYPASS_MIN_MONTHS_RULE.getCode()));
		    break;
		case 2:
		    this.processId = WFProcessesEnum.SOLDIERS_MOVE.getCode();
		    setScreenTitle(getMessage("title_soldiersMoveDecisionRequest"));
		    extraParams.put("skipMonthsRuleValidation", SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MVT_MOVE_SOLDIERS_DECISION_REQUEST.getCode(), MenuActionsEnum.MVT_BYPASS_MIN_MONTHS_RULE.getCode()));
		    break;
		case 3:
		    this.processId = WFProcessesEnum.PERSONS_MOVE.getCode();
		    setScreenTitle(getMessage("title_personsMoveDecisionRequest"));
		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
		    break;

		}

		regionId = getLoginEmpPhysicalRegionFlag(true);

		jobsData = new ArrayList<JobData>();
		if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		    wfMovementsList = new ArrayList<WFMovementData>();
		    internalCopies = new ArrayList<EmployeeData>();
		    decisionData = new WFMovementData();
		    decisionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		    decisionData.setReasonType(MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode());
		    decisionData.setExecutionDateFlag(FlagsEnum.ON.getCode());
		    decisionData.setExecutionDate(HijriDateService.getHijriSysDate());
		    decisionData.setBasedOnOrderDate(HijriDateService.getHijriSysDate());
		    decisionData.setVacationGrantFlagBoolean(false);
		    decisionData.setVerbalOrderFlag(FlagsEnum.OFF.getCode());
		} else {
		    wfMovementsList = MovementsWorkFlow.getWFMovementDataByInstanceId(this.instance.getInstanceId());
		    decisionData = wfMovementsList.get(0);

		    for (int i = 0; i < wfMovementsList.size(); i++) {
			WFMovementData wfMovement = wfMovementsList.get(i);

			if (role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()))
			    jobsData.add(JobsService.getJobById(wfMovement.getEmployeeJobId()));

			if (!role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()))
			    MovementsWorkFlow.calculateWarnings(wfMovement, processId);
			if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
			    warningsCount++;
		    }
		    internalCopies = EmployeesService.getEmployeesByIdsString(decisionData.getInternalCopies());
		    if (internalCopies == null)
			internalCopies = new ArrayList<EmployeeData>();
		    externalCopies = decisionData.getExternalCopies();
		}
	    } catch (BusinessException e1) {
		setServerSideErrorMessages(getMessage(e1.getMessage()));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_URLError"));
	}
    }

    public void init() {
	super.init();
    }

    public void addWfMovement() {
	try {
	    WFMovementData wfMovement = MovementsWorkFlow.constructWFMovement(processId, selectedEmpId.longValue(), null, decisionData.getExecutionDateFlag(), decisionData.getExecutionDate(), null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, decisionData.getReasonType(), null, null, null, MovementTypesEnum.MOVE.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    jobsData.add(JobsService.getJobById(EmployeesService.getEmployeeData(wfMovement.getEmployeeId()).getJobId()));
	    wfMovementsList.add(wfMovement);
	    if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
		warningsCount++;
	} catch (Exception e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), ((BusinessException) e1).getParams()));
	}
    }

    public void deleteWFMovement(WFMovementData selectedWFMovement) {
	try {
	    if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {

		if (wfMovementsList.size() == 1)
		    throw new BusinessException("error_moveListEmpty");

		boolean isAllNewRecords = true;
		for (WFMovementData wfMovementData : wfMovementsList) {
		    if (wfMovementData.getInstanceId() != null && !wfMovementData.getEmployeeId().equals(selectedWFMovement.getEmployeeId())) {
			isAllNewRecords = false;
			break;
		    }
		}

		if (isAllNewRecords)
		    throw new BusinessException("error_saveRecordsBeforeDeleting");
	    }

	    if (selectedWFMovement.getInstanceId() != null) {
		MovementsWorkFlow.deleteWFMovement(selectedWFMovement);
	    }
	    if (selectedWFMovement.getWarningMessages() != null && !selectedWFMovement.getWarningMessages().isEmpty())
		warningsCount--;

	    for (WFMovementData wfMovementData : wfMovementsList) {
		if (selectedWFMovement.getEmployeeJobId().equals(wfMovementData.getJobId())) {
		    wfMovementData.setJobId(null);
		    resetWFMovementDataJobDetails(wfMovementData);
		}
	    }

	    jobsData.remove(getJobByIdFromJobsList(selectedWFMovement.getEmployeeJobId()));
	    wfMovementsList.remove(selectedWFMovement);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    private JobData getJobByIdFromJobsList(long jobId) {
	for (JobData jobData : jobsData)
	    if (jobData.getId() == jobId)
		return jobData;
	return null;
    }

    public void verbalOrderFlagListener() {
	try {
	    if (decisionData.getVerbalOrderFlag().equals(FlagsEnum.ON.getCode())) {
		decisionData.setBasedOn(null);
		decisionData.setBasedOnOrderNumber(null);
		decisionData.setBasedOnOrderDate(null);
	    } else {
		decisionData.setBasedOn(null);
		decisionData.setBasedOnOrderNumber(null);
		decisionData.setBasedOnOrderDate(HijriDateService.getHijriSysDate());
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void locationFlagListener() {
	// 0 internal 1 external
	try {
	    if (decisionData.getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()) {
		decisionData.setExecutionDateFlag(FlagsEnum.ON.getCode());
		for (WFMovementData wfMovement : wfMovementsList) {
		    wfMovement.setLocation(null);
		}
		decisionData.setMinistryApprovalNumber(null);
		decisionData.setMinistryApprovalDate(null);
	    } else {
		decisionData.setExecutionDateFlag(FlagsEnum.OFF.getCode());
		for (WFMovementData wfMovement : wfMovementsList) {
		    wfMovement.setJobId(null);
		    wfMovement.setJobName(null);
		    wfMovement.setUnitId(null);
		    wfMovement.setUnitFullName(null);
		}

		decisionData.setMinistryApprovalDate(HijriDateService.getHijriSysDate());
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void executionDateFlagListener() {
	try {
	    if (decisionData.getExecutionDateFlagBoolean()) {
		decisionData.setExecutionDate(null);
	    } else {
		decisionData.setExecutionDate(HijriDateService.getHijriSysDate());
	    }
	    warningsCount = 0;
	    for (int i = 0; i < wfMovementsList.size(); i++) {
		WFMovementData wfMovement = wfMovementsList.get(i);
		wfMovement.setExecutionDateFlag(decisionData.getExecutionDateFlag());
		wfMovement.setExecutionDate(decisionData.getExecutionDate());
		MovementsWorkFlow.calculateWarnings(wfMovement, processId);
		if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
		    warningsCount++;
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void executionDateListener() {
	for (int i = 0; i < wfMovementsList.size(); i++) {
	    WFMovementData wfMovement = wfMovementsList.get(i);
	    wfMovement.setExecutionDate(decisionData.getExecutionDate());
	    calculateWarnings(wfMovement);
	}
    }

    public void calculateWarnings(WFMovementData wfMovement) {
	try {
	    boolean hadWarning = false;
	    boolean hasWarning = false;
	    if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
		hadWarning = true;
	    MovementsWorkFlow.calculateWarnings(wfMovement, processId);
	    if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
		hasWarning = true;
	    if (hasWarning && !hadWarning)
		warningsCount++;
	    if (!hasWarning && hadWarning)
		warningsCount--;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void movedToJobChanged(WFMovementData wfMovement) {
	try {
	    if (wfMovement.getJobId() != null) {

		if (wfMovement.getEmployeeJobId().equals(wfMovement.getJobId())) {
		    wfMovement.setJobId(null);
		    resetWFMovementDataJobDetails(wfMovement);
		    throw new BusinessException("error_cannotMoveEmployeeOnHisSameJob");
		}

		JobData job = JobsService.getJobById(wfMovement.getJobId());
		if (job != null) {
		    wfMovement.setJobCode(job.getCode());
		    wfMovement.setJobName(job.getName());
		    wfMovement.setUnitId(job.getUnitId());
		    wfMovement.setUnitFullName(job.getUnitFullName());
		    calculateWarnings(wfMovement);
		}
	    } else {
		resetWFMovementDataJobDetails(wfMovement);
	    }
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getMessage(e1.getMessage()));
	}
    }

    public void sequentialMvtFlagListener(WFMovementData selectedWFMovementData) {
	selectedWFMovementData.setJobId(null);
	resetWFMovementDataJobDetails(selectedWFMovementData);
    }

    private void resetWFMovementDataJobDetails(WFMovementData selectedWFMovementData) {
	selectedWFMovementData.setJobCode(null);
	selectedWFMovementData.setJobName(null);
	selectedWFMovementData.setUnitId(null);
	selectedWFMovementData.setUnitFullName(null);
	calculateWarnings(selectedWFMovementData);
    }

    public void reasonTypeListener() {
	if (decisionData.getReasonType().equals(MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode())) {
	    decisionData.setVacationGrantFlagBoolean(false);
	}

	if (decisionData.getReasonType() == MovementsReasonTypesEnum.FOR_PUBLIC_INTEREST.getCode() && decisionData.getLocationFlag().longValue() != LocationFlagsEnum.INTERNAL.getCode()) {
	    decisionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	    locationFlagListener();
	}
    }

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public long getRegionId() {
	return regionId;
    }

    public List<JobData> getJobsData() {
	return jobsData;
    }
}
