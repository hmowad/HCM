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
import com.code.enums.MovementTypesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

/**
 * @author Sherif-Ejada
 * 
 */
@ManagedBean(name = "subjoinCancellationDecisionRequest")
@ViewScoped
public class SubjoinCancellationDecisionRequest extends MovementsBase implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/

    public SubjoinCancellationDecisionRequest() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    this.init();
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersSubjoinCancellationDecisionRequest"));
		this.processId = WFProcessesEnum.OFFICERS_SUBJOIN_CANCELLATION.getCode();
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersSubjoinCancellationDecisionRequest"));
		this.processId = WFProcessesEnum.SOLDIERS_SUBJOIN_CANCELLATION.getCode();
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsInternalAssignmentCancellationDecisionRequest"));
		this.processId = WFProcessesEnum.PERSONS_ASSIGNMENT_CANCELLATION.getCode();
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
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

	    wfMovementsList.remove(subjoin);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getSubjoinByDecisionInfo() {
	try {
	    wfMovementsList = MovementsWorkFlow.constructWFMovementByDecisionInfo(processId, decisionData.getBasedOnDecisionNumber(), decisionData.getBasedOnDecisionDate(), getCategoriesIdsArrayByMode(mode), MovementTypesEnum.SUBJOIN.getCode(), TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), getLoginEmpPhysicalRegionFlag(true));
	    if (wfMovementsList != null && wfMovementsList.size() > 0) {
		decisionData = wfMovementsList.get(0);
		decisionData.setBasedOnOrderDate(HijriDateService.getHijriSysDate());
		if (decisionData.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()))
		    decisionData.setMinistryApprovalDate(HijriDateService.getHijriSysDate());
		internalCopies = EmployeesService.getEmployeesByIdsString(wfMovementsList.get(0).getInternalCopies());
		externalCopies = wfMovementsList.get(0).getExternalCopies();
	    }
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

    public void manipulateEndDate(WFMovementData subjoin) {
	try {
	    if (subjoin.getExecutionDateString() != null && ((subjoin.getPeriodMonths() != null && subjoin.getPeriodMonths() > 0) || (subjoin.getPeriodDays() != null && subjoin.getPeriodDays() > 0))) {
		subjoin.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(subjoin.getExecutionDateString(), subjoin.getPeriodMonths() == null ? 0 : subjoin.getPeriodMonths(), subjoin.getPeriodDays() == null ? -1 : subjoin.getPeriodDays() - 1));
	    } else
		subjoin.setEndDateString(null);
	} catch (Exception e) {
	    subjoin.setEndDateString(null);
	}
    }
}
