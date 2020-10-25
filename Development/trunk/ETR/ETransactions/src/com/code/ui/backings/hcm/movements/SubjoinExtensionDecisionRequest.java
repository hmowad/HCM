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
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

/**
 * @author Sherif-Ejada
 * 
 */
@ManagedBean(name = "subjoinExtensionDecisionRequest")
@ViewScoped
public class SubjoinExtensionDecisionRequest extends MovementsBase implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    public SubjoinExtensionDecisionRequest() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    try {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		this.init();
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersSubjoinExtensionDecisionRequest"));
		    this.processId = WFProcessesEnum.OFFICERS_SUBJOIN_EXTENSION.getCode();
		    decisionData.setExtraParams("skipMonthsRuleValidation=" + (SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MVT_SUBJOIN_EXTENSION_OFFICERS_DECISION_REQUEST.getCode(), MenuActionsEnum.MVT_BYPASS_MIN_MONTHS_RULE.getCode()) ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode()));
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersSubjoinExtensionDecisionRequest"));
		    this.processId = WFProcessesEnum.SOLDIERS_SUBJOIN_EXTENSION.getCode();
		    decisionData.setExtraParams("skipMonthsRuleValidation=" + (SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MVT_SUBJOIN_EXTENSION_SOLDIERS_DECISION_REQUEST.getCode(), MenuActionsEnum.MVT_BYPASS_MIN_MONTHS_RULE.getCode()) ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode()));
		    break;
		case 3:
		    setScreenTitle(getMessage("title_personsInternalAssignmentExtensionDecisionRequest"));
		    this.processId = WFProcessesEnum.PERSONS_ASSIGNMENT_EXTENSION.getCode();
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
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		decisionData = new WFMovementData();
		wfMovementsList = new ArrayList<WFMovementData>();
		decisionData.setBasedOnDecisionDate(HijriDateService.getHijriSysDate());
		decisionData.setVerbalOrderFlag(FlagsEnum.OFF.getCode());
		internalCopies = new ArrayList<EmployeeData>();
	    } else {
		wfMovementsList = MovementsWorkFlow.getWFMovementDataByInstanceId(instance.getInstanceId());
		decisionData = wfMovementsList.get(0);
		internalCopies = EmployeesService.getEmployeesByIdsString(decisionData.getInternalCopies());
		if (internalCopies == null)
		    internalCopies = new ArrayList<EmployeeData>();
		externalCopies = decisionData.getExternalCopies();
		warningsCount = 0;
		if (processId == WFProcessesEnum.SOLDIERS_SUBJOIN_EXTENSION.getCode()) {
		    for (WFMovementData wfMovement : wfMovementsList) {
			if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
			    warningsCount++;
		    }
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
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

	    if (processId == WFProcessesEnum.SOLDIERS_SUBJOIN_EXTENSION.getCode() && (subjoin.getWarningMessages() != null || subjoin.getWarningMessages().isEmpty()))
		warningsCount--;

	    wfMovementsList.remove(subjoin);
	} catch (BusinessException e) {
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

    public void getSubjoinByDecisionInfo() {
	try {
	    wfMovementsList = MovementsWorkFlow.constructWFMovementByDecisionInfo(processId, decisionData.getBasedOnDecisionNumber(), decisionData.getBasedOnDecisionDate(), getCategoriesIdsArrayByMode(mode), MovementTypesEnum.SUBJOIN.getCode(), TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode(), getLoginEmpPhysicalRegionFlag(true), decisionData.getExtraParams());
	    if (wfMovementsList != null && wfMovementsList.size() > 0) {
		decisionData = wfMovementsList.get(0);
		decisionData.setBasedOnOrderDate(HijriDateService.getHijriSysDate());
		if (decisionData.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()))
		    decisionData.setMinistryApprovalDate(HijriDateService.getHijriSysDate());
		internalCopies = EmployeesService.getEmployeesByIdsString(wfMovementsList.get(0).getInternalCopies());
		externalCopies = wfMovementsList.get(0).getExternalCopies();
		warningsCount = 0;
		if (processId == WFProcessesEnum.SOLDIERS_SUBJOIN_EXTENSION.getCode()) {
		    for (WFMovementData wfMovement : wfMovementsList) {
			if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
			    warningsCount++;
		    }
		}
	    }

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void manipulateEndDate(WFMovementData subjoin) {
	try {
	    if (processId == WFProcessesEnum.SOLDIERS_SUBJOIN_EXTENSION.getCode())
		calculateWarnings(subjoin);
	    if (subjoin.getExecutionDateString() != null && ((subjoin.getPeriodMonths() != null && subjoin.getPeriodMonths() > 0) || (subjoin.getPeriodDays() != null && subjoin.getPeriodDays() > 0))) {
		subjoin.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(subjoin.getExecutionDateString(), subjoin.getPeriodMonths() == null ? 0 : subjoin.getPeriodMonths(), subjoin.getPeriodDays() == null ? -1 : subjoin.getPeriodDays() - 1));
	    } else
		subjoin.setEndDateString(null);
	} catch (Exception e) {
	    subjoin.setEndDateString(null);
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
}
