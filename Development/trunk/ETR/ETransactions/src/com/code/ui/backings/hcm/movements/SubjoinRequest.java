package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@ManagedBean(name = "subjoinRequest")
@ViewScoped
public class SubjoinRequest extends MovementsBase implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/

    public SubjoinRequest() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    this.init();
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersSubjoinRequest"));
		this.processId = WFProcessesEnum.OFFICERS_SUBJOIN_REQUEST.getCode();
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersSubjoinRequest"));
		this.processId = WFProcessesEnum.SOLDIERS_SUBJOIN_REQUEST.getCode();
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsInternalAssingnmentRequest"));
		this.processId = WFProcessesEnum.PERSONS_ASSIGNMENT_REQUEST.getCode();
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_general"));
    }

    protected void init() {
	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfMovement = MovementsWorkFlow.getWFMovementDataByInstanceId(instance.getInstanceId()).get(0);

		if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()) && wfMovement.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()) && wfMovement.getMinistryApprovalDate() == null)
		    wfMovement.setMinistryApprovalDate(HijriDateService.getHijriSysDate());

		if (!(this.role.equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()) || this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()))) {
		    internalCopies = EmployeesService.getEmployeesByIdsString(wfMovement.getInternalCopies());
		    if (internalCopies == null)
			internalCopies = new ArrayList<EmployeeData>();
		    externalCopies = wfMovement.getExternalCopies();
		}

		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		    if (mode == 1)
			reviewerEmps.add(loginEmpData);
		}
	    } else {
		wfMovement = MovementsWorkFlow.constructWFMovement(processId, requester.getEmpId(), null, FlagsEnum.OFF.getCode(), HijriDateService.getHijriSysDate(), null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode(), null, null, null, MovementTypesEnum.SUBJOIN.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void changeUnitListener() {
	wfMovement.setJobId(null);
	wfMovement.setJobName(null);
	wfMovement.setJobCode(null);
    }

    public void changeLocationFlagListener() {
	wfMovement.setUnitId(null);
	wfMovement.setUnitFullName(null);
	wfMovement.setUnitHKey(null);
	wfMovement.setLocation(null);
    }

    public void manipulateEndDate() {

	try {
	    MovementsWorkFlow.calculateWarnings(wfMovement, processId);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

	try {
	    if (wfMovement.getExecutionDateString() != null && ((wfMovement.getPeriodMonths() != null && wfMovement.getPeriodMonths() > 0) || (wfMovement.getPeriodDays() != null && wfMovement.getPeriodDays() > 0))) {
		wfMovement.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(wfMovement.getExecutionDateString(), wfMovement.getPeriodMonths() == null ? 0 : wfMovement.getPeriodMonths(), wfMovement.getPeriodDays() == null ? -1 : wfMovement.getPeriodDays() - 1));
	    } else
		wfMovement.setEndDateString(null);
	} catch (Exception e) {
	    wfMovement.setEndDateString(null);
	}
    }

    public Long getUnitAncestorId() {
	try {
	    return wfMovement.getUnitId() == null ? null : MovementsWorkFlow.getUnitAncestorId(wfMovement.getUnitId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }
}
