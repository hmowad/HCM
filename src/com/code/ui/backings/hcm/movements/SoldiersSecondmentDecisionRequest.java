package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
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

@ManagedBean(name = "soldiersSecondmentDecisionRequest")
@ViewScoped
public class SoldiersSecondmentDecisionRequest extends MovementsBase implements Serializable {
    private int movementType;

    private EmployeeData soldier;

    private long movementTypeId;

    public SoldiersSecondmentDecisionRequest() {
	if (getRequest().getParameter("movementType") != null) {
	    movementType = Integer.parseInt(getRequest().getParameter("movementType"));
	    if (movementType == 4) {
		this.processId = WFProcessesEnum.SOLDIERS_MANDATE.getCode();
		movementTypeId = MovementTypesEnum.MANDATE.getCode();
		setScreenTitle(getMessage("title_soldiersMandateDecisionRequest"));
	    } else {
		movementTypeId = MovementTypesEnum.SECONDMENT.getCode();
		this.processId = WFProcessesEnum.SOLDIERS_SECONDMENT.getCode();
		setScreenTitle(getMessage("title_soldiersSecondmentDecisionRequest"));
	    }

	    super.init();
	    try {
		if (role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		    wfMovement = new WFMovementData();
		    wfMovement.setExecutionDate(HijriDateService.getHijriSysDate());
		    wfMovement.setMinistryApprovalDate(HijriDateService.getHijriSysDate());
		    internalCopies = new ArrayList<EmployeeData>();
		} else {
		    wfMovement = MovementsWorkFlow.getWFMovementDataByInstanceId(this.instance.getInstanceId()).get(0);
		    soldier = EmployeesService.getEmployeeData(wfMovement.getEmployeeId());
		    internalCopies = EmployeesService.getEmployeesByIdsString(wfMovement.getInternalCopies());
		    if (internalCopies == null)
			internalCopies = new ArrayList<EmployeeData>();
		    externalCopies = wfMovement.getExternalCopies();
		}
	    } catch (BusinessException e) {
		setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_general"));
    }

    public void searchSoldiers() {
	try {
	    soldier = EmployeesService.getEmployeeData(wfMovement.getEmployeeId());
	    if (soldier == null)
		throw new BusinessException("error_general");

	    wfMovement = MovementsWorkFlow.constructWFMovement(processId, soldier.getEmpId(), null, FlagsEnum.OFF.getCode(), HijriDateService.getHijriSysDate(), null, null, null, null, null, LocationFlagsEnum.EXTERNAL.getCode(), null, null, null, MovementsReasonTypesEnum.FOR_WORK_INTEREST.getCode(), null,
		    null, null, movementTypeId, TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    wfMovement.setMinistryApprovalDate(HijriDateService.getHijriSysDate());
	    wfMovementsList = null;

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void manipulateEndDate() {
	try {
	    MovementsWorkFlow.calculateWarnings(wfMovement, processId);
	    if (wfMovement.getExecutionDateString() != null && ((wfMovement.getPeriodMonths() != null && wfMovement.getPeriodMonths() > 0) || (wfMovement.getPeriodDays() != null && wfMovement.getPeriodDays() > 0))) {
		wfMovement.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(wfMovement.getExecutionDateString(), wfMovement.getPeriodMonths() == null ? 0 : wfMovement.getPeriodMonths(), wfMovement.getPeriodDays() == null ? -1 : wfMovement.getPeriodDays() - 1));
	    } else
		wfMovement.setEndDateString(null);
	} catch (Exception e) {
	    wfMovement.setEndDateString(null);
	}
    }

    public int getMovementType() {
	return movementType;
    }

    public void setMovementType(int movementType) {
	this.movementType = movementType;
    }

    public EmployeeData getSoldier() {
	return soldier;
    }

    public void setSoldier(EmployeeData soldier) {
	this.soldier = soldier;
    }

}
