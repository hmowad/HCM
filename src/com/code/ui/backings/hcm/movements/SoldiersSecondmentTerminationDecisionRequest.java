package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@ManagedBean(name = "soldiersSecondmentTerminationDecisionRequest")
@ViewScoped
public class SoldiersSecondmentTerminationDecisionRequest extends MovementsBase implements Serializable {
    private int movementType;

    private MovementTransactionData lastSecondment;
    private EmployeeData soldier;

    private long movementTypeId;

    public SoldiersSecondmentTerminationDecisionRequest() {
	if (getRequest().getParameter("movementType") != null) {
	    movementType = Integer.parseInt(getRequest().getParameter("movementType"));
	    if (movementType == 4) {
		this.processId = WFProcessesEnum.SOLDIERS_MANDATE_TERMINATION.getCode();
		movementTypeId = MovementTypesEnum.MANDATE.getCode();
		setScreenTitle(getMessage("title_soldiersMandateTerminationDecisionRequest"));
	    } else {
		this.processId = WFProcessesEnum.SOLDIERS_SECONDMENT_TERMINATION.getCode();
		movementTypeId = MovementTypesEnum.SECONDMENT.getCode();
		setScreenTitle(getMessage("title_soldiersSecondmentTerminationDecisionRequest"));
	    }

	    super.init();
	    try {
		if (role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		    wfMovement = new WFMovementData();
		    wfMovement.setMinistryApprovalDate(HijriDateService.getHijriSysDate());
		    wfMovement.setEndDate(HijriDateService.getHijriSysDate());
		    internalCopies = new ArrayList<EmployeeData>();
		} else {
		    wfMovement = MovementsWorkFlow.getWFMovementDataByInstanceId(this.instance.getInstanceId()).get(0);
		    soldier = EmployeesService.getEmployeeData(wfMovement.getEmployeeId());
		    lastSecondment = MovementsService.getMovementTransactions(soldier.getEmpId(), CategoriesEnum.SOLDIERS.getCode(), wfMovement.getBasedOnDecisionNumber(), wfMovement.getBasedOnDecisionDate(), wfMovement.getBasedOnDecisionDate(), movementTypeId, FlagsEnum.ON.getCode()).get(0);
		    internalCopies = EmployeesService.getEmployeesByIdsString(wfMovement.getInternalCopies());
		    if (internalCopies == null)
			internalCopies = new ArrayList<EmployeeData>();
		    externalCopies = wfMovement.getExternalCopies();
		}
	    } catch (BusinessException e) {
		setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_URLError"));
    }

    public void searchSoldiers() {
	try {
	    lastSecondment = MovementsService.getLastValidMovementTransaction(wfMovement.getEmployeeId(), movementTypeId, FlagsEnum.ON.getCode());
	    soldier = EmployeesService.getEmployeeData(wfMovement.getEmployeeId());
	    if (soldier == null)
		throw new BusinessException("error_UIError");

	    if (lastSecondment != null) {
		wfMovement = MovementsWorkFlow.constructWFMovement(processId, soldier.getEmpId(), null, lastSecondment.getExecutionDateFlag(), lastSecondment.getExecutionDate(), HijriDateService.getHijriSysDate(), null, null, null, null, lastSecondment.getLocationFlag(), lastSecondment.getLocation(), lastSecondment.getDecisionNumber(), lastSecondment.getDecisionDate(), lastSecondment.getReasonType(),
			null, null, null, movementTypeId, TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode());
		wfMovement.setMinistryApprovalDate(HijriDateService.getHijriSysDate());
		internalCopies = EmployeesService.getEmployeesByIdsString(lastSecondment.getInternalCopies());
		externalCopies = lastSecondment.getExternalCopies();
	    } else {
		wfMovement = new WFMovementData();
		wfMovementsList = null;
	    }

	} catch (BusinessException e) {
	    wfMovement = new WFMovementData();
	    wfMovementsList = null;
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage()));
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

    public MovementTransactionData getLastSecondment() {
	return lastSecondment;
    }

    public void setLastSecondment(MovementTransactionData lastSecondment) {
	this.lastSecondment = lastSecondment;
    }

}
