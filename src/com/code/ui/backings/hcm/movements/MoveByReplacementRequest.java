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

@ManagedBean(name = "moveByReplacementRequest")
@ViewScoped
public class MoveByReplacementRequest extends MovementsBase implements Serializable {

    private EmployeeData replacementEmployee;

    public MoveByReplacementRequest() {

	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {

	    case 1:
		this.processId = WFProcessesEnum.OFFICERS_MOVE_REQUEST.getCode();
		setScreenTitle(getMessage("title_officersReplacementMoveRequest"));
		break;

	    case 2:
		this.processId = WFProcessesEnum.SOLDIERS_MOVE_REQUEST.getCode();
		setScreenTitle(getMessage("title_soldiersReplacementMoveRequest"));
		break;

	    case 3:
		this.processId = WFProcessesEnum.PERSONS_MOVE_REQUEST.getCode();
		setScreenTitle(getMessage("title_personsReplacementMoveRequest"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));

	    }
	    init();
	    try {

		if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		    wfMovement = MovementsWorkFlow.constructWFMovement(processId, this.loginEmpData.getEmpId(), null, FlagsEnum.ON.getCode(), null, null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode(), null, null, null, MovementTypesEnum.MOVE.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
		    replacementEmployee = new EmployeeData();
		} else {
		    wfMovement = MovementsWorkFlow.getWFMovementDataByInstanceId(this.instance.getInstanceId()).get(0);

		    replacementEmployee = EmployeesService.getEmployeeData(wfMovement.getReplacementEmployeeId());

		    if (!this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()))
			MovementsWorkFlow.calculateWarnings(wfMovement, processId);

		    if (!(this.role.equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()) || this.role.equals(WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode()) || this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()))) {
			internalCopies = EmployeesService.getEmployeesByIdsString(wfMovement.getInternalCopies());
			if (internalCopies == null)
			    internalCopies = new ArrayList<EmployeeData>();
			externalCopies = wfMovement.getExternalCopies();
		    }
		}

		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.REPLACEMENT_SECONDARY_MANAGER_REDIRECT.getCode())) {
		    getSecurityPassScanValue();
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		    if (mode == 1)
			reviewerEmps.add(loginEmpData);
		}

		if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()) && wfMovement.getExecutionDate() == null) {
		    wfMovement.setExecutionDate(HijriDateService.getHijriSysDate());
		}
	    } catch (BusinessException e) {
		setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_URLError"));
    }

    public void searchReplacementEmployee() {
	try {
	    replacementEmployee = EmployeesService.getEmployeeData(wfMovement.getReplacementEmployeeId());
	    if (replacementEmployee == null)
		throw new BusinessException("error_UIError");

	    wfMovement.setReplacementEmployeeId(replacementEmployee.getEmpId());
	    wfMovement.setUnitId(replacementEmployee.getOfficialUnitId());
	    wfMovement.setJobId(replacementEmployee.getJobId());

	    MovementsWorkFlow.calculateWarnings(wfMovement, processId);
	} catch (BusinessException e) {
	    replacementEmployee = new EmployeeData();
	    wfMovement.setReplacementEmployeeId(null);
	    wfMovement.setUnitId(null);
	    wfMovement.setJobId(null);
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void executionDateFlagChanged() {
	// if executionDateFlagBoolean is true then move is from decision Date
	// else from specified Date
	try {
	    if (wfMovement.getExecutionDateFlagBoolean()) {
		wfMovement.setExecutionDate(null);
		wfMovement.setExecutionDateString(null);

	    } else {
		wfMovement.setExecutionDate(HijriDateService.getHijriSysDate());
	    }

	    MovementsWorkFlow.calculateWarnings(wfMovement, processId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void calculateWarnings() {
	try {
	    MovementsWorkFlow.calculateWarnings(wfMovement, processId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public EmployeeData getReplacementEmployee() {
	return replacementEmployee;
    }

}
