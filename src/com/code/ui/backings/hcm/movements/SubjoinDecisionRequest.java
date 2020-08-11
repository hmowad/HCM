package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.CategoryModesEnum;
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
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@ManagedBean(name = "subjoinDecisionRequest")
@ViewScoped
public class SubjoinDecisionRequest extends MovementsBase implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private Long selectedEmpId;
    private long regionId;

    public SubjoinDecisionRequest() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    try {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		this.init();
		regionId = getLoginEmpPhysicalRegionFlag(true);
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersSubjoinDecisionRequest"));
		    this.processId = WFProcessesEnum.OFFICERS_SUBJOIN.getCode();
		    extraParams.put("skipMonthsRuleValidation", SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MVT_SUBJOIN_OFFICERS_DECISION_REQUEST.getCode(), MenuActionsEnum.MVT_BYPASS_MIN_MONTHS_RULE.getCode()));
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersSubjoinDecisionRequest"));
		    this.processId = WFProcessesEnum.SOLDIERS_SUBJOIN.getCode();
		    extraParams.put("skipMonthsRuleValidation", SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MVT_SUBJOIN_SOLDIERS_DECISION_REQUEST.getCode(), MenuActionsEnum.MVT_BYPASS_MIN_MONTHS_RULE.getCode()));
		    break;
		case 3:
		    setScreenTitle(getMessage("title_personsInternalAssignmentDecisionRequest"));
		    this.processId = WFProcessesEnum.PERSONS_ASSIGNMENT.getCode();
		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
		}
	    } catch (BusinessException e) {
		setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_URLError"));
    }

    protected void init() {
	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfMovementsList = MovementsWorkFlow.getWFMovementDataByInstanceId(instance.getInstanceId());
		decisionData = wfMovementsList.get(0);
		for (int i = 0; i < wfMovementsList.size(); i++) {
		    WFMovementData wfMovement = wfMovementsList.get(i);
		    if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
			warningsCount++;
		}
		internalCopies = EmployeesService.getEmployeesByIdsString(decisionData.getInternalCopies());
		if (internalCopies == null)
		    internalCopies = new ArrayList<EmployeeData>();
		externalCopies = decisionData.getExternalCopies();
	    } else {
		decisionData = new WFMovementData();
		wfMovementsList = new ArrayList<WFMovementData>();
		// Internal is default
		decisionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		decisionData.setReasonType(MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode());
		decisionData.setExecutionDateFlag(FlagsEnum.OFF.getCode());
		decisionData.setBasedOnOrderDate(HijriDateService.getHijriSysDate());
		decisionData.setVerbalOrderFlag(FlagsEnum.OFF.getCode());
		internalCopies = new ArrayList<EmployeeData>();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewSubjoinDecisionRequest() {
	try {
	    WFMovementData temp = MovementsWorkFlow.constructWFMovement(processId, selectedEmpId.longValue(), null, FlagsEnum.OFF.getCode(), HijriDateService.getHijriSysDate(), null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, decisionData.getReasonType(), null, null, null, MovementTypesEnum.SUBJOIN.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    wfMovementsList.add(temp);
	    if (temp.getWarningMessages() != null && !temp.getWarningMessages().isEmpty())
		warningsCount++;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void deleteSubjoin(WFMovementData subjoin) {
	try {
	    if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {

		if (wfMovementsList.size() == 1)
		    if (this.mode == CategoryModesEnum.CIVILIANS.getCode())
			throw new BusinessException("error_assignmentListEmpty");
		    else
			throw new BusinessException("error_subjoinListEmpty");

		boolean isAllNewRecords = true;
		for (WFMovementData wfMovementData : wfMovementsList) {
		    if (wfMovementData.getInstanceId() != null && !wfMovementData.getEmployeeId().equals(subjoin.getEmployeeId())) {
			isAllNewRecords = false;
			break;
		    }
		}

		if (isAllNewRecords)
		    throw new BusinessException("error_saveRecordsBeforeDeleting");
	    }

	    if (subjoin.getInstanceId() != null)
		MovementsWorkFlow.deleteWFMovement(subjoin);

	    if (subjoin.getWarningMessages() != null && !subjoin.getWarningMessages().isEmpty())
		warningsCount--;

	    wfMovementsList.remove(subjoin);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void manipulateEndDate(WFMovementData subjoin) {
	try {
	    // handle warning
	    boolean hadWarning = false;
	    boolean hasWarning = false;
	    if (subjoin.getWarningMessages() != null && !subjoin.getWarningMessages().isEmpty())
		hadWarning = true;
	    MovementsWorkFlow.calculateWarnings(subjoin, processId);
	    if (subjoin.getWarningMessages() != null && !subjoin.getWarningMessages().isEmpty())
		hasWarning = true;
	    if (hasWarning && !hadWarning)
		warningsCount++;
	    if (!hasWarning && hadWarning)
		warningsCount--;

	    if (subjoin.getExecutionDateString() != null && ((subjoin.getPeriodMonths() != null && subjoin.getPeriodMonths() > 0) || (subjoin.getPeriodDays() != null && subjoin.getPeriodDays() > 0))) {
		subjoin.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(subjoin.getExecutionDateString(), subjoin.getPeriodMonths() == null ? 0 : subjoin.getPeriodMonths(), subjoin.getPeriodDays() == null ? -1 : subjoin.getPeriodDays() - 1));
	    } else
		subjoin.setEndDateString(null);
	} catch (Exception e) {
	    subjoin.setEndDateString(null);
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
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
		for (WFMovementData wfMovement : wfMovementsList) {
		    wfMovement.setLocation(null);
		}
		decisionData.setMinistryApprovalNumber(null);
		decisionData.setMinistryApprovalDate(null);
	    } else {
		for (WFMovementData wfMovement : wfMovementsList) {
		    wfMovement.setUnitId(null);
		    wfMovement.setUnitFullName(null);
		}
		decisionData.setMinistryApprovalDate(HijriDateService.getHijriSysDate());
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reasonTypeListener() {
	if (processId == WFProcessesEnum.PERSONS_ASSIGNMENT.getCode()) {
	    decisionData.setTransferAllowanceFlagBoolean(false);
	} else {
	    if (decisionData.getReasonType() == MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode()) {
		decisionData.setVacationGrantFlagBoolean(false);
	    }

	    if (decisionData.getReasonType() == MovementsReasonTypesEnum.FOR_PUBLIC_INTEREST.getCode() && decisionData.getLocationFlag().longValue() != LocationFlagsEnum.INTERNAL.getCode()) {
		decisionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		locationFlagListener();
	    }
	}
    }

    public void changeUnitListener(WFMovementData subjoin) {
	subjoin.setJobId(null);
	subjoin.setJobName(null);
	subjoin.setJobCode(null);
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
}
