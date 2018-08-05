package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@ManagedBean(name = "moveByFreezeDescisionRequest")
@ViewScoped
public class MoveByFreezeDescisionRequest extends MovementsBase implements Serializable {
    private boolean vacationGrantFlagDisabled;
    private long empId;

    public MoveByFreezeDescisionRequest() {
	super.init();
	try {
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.processId = WFProcessesEnum.SOLDIERS_MOVE.getCode();
		wfMovementsList = new ArrayList<WFMovementData>();
		decisionData = new WFMovementData();
		decisionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode()); // internal
		decisionData.setMovementTypeId(MovementTypesEnum.MOVE.getCode()); // move
		decisionData.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());
		decisionData.setCategoryId(CategoriesEnum.SOLDIERS.getCode());
		decisionData.setVacationGrantFlagBoolean(false);
		decisionData.setExecutionDateFlag(FlagsEnum.OFF.getCode());
		decisionData.setExecutionDate(HijriDateService.getHijriSysDate());
		decisionData.setBasedOnOrderDate(HijriDateService.getHijriSysDate());
		decisionData.setVerbalOrderFlag(FlagsEnum.OFF.getCode());
		internalCopies = new ArrayList<EmployeeData>();
		externalCopies = MovementsWorkFlow.getMoveByFreezeDescisionExternalCopies();

		vacationGrantFlagDisabled = true;
	    } else {
		wfMovementsList = MovementsWorkFlow.getWFMovementDataByInstanceId(this.instance.getInstanceId());
		decisionData = wfMovementsList.get(0);
		vacationGrantFlagDisabled = (decisionData.getReasonType().intValue() == MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode());
		for (int i = 0; i < wfMovementsList.size(); i++) {
		    WFMovementData wfMovement = wfMovementsList.get(i);
		    if (wfMovement.getWarningMessages() != null && !wfMovement.getWarningMessages().isEmpty())
			warningsCount++;
		}
		internalCopies = EmployeesService.getEmployeesByIdsString(decisionData.getInternalCopies());
		if (internalCopies == null)
		    internalCopies = new ArrayList<EmployeeData>();
		externalCopies = decisionData.getExternalCopies();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addToList() {
	try {
	    WFMovementData empWF = new WFMovementData();
	    empWF = MovementsWorkFlow.constructWFMovement(empId, null, decisionData.getExecutionDateFlag(), decisionData.getExecutionDate(), null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, decisionData.getReasonType(), null, null, null, MovementTypesEnum.MOVE.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    empWF.setFreezeJobId(empWF.getEmployeeJobId());
	    empWF.setFreezeJobCode(empWF.getEmployeeJobCode());
	    empWF.setFreezeJobName(empWF.getEmployeeJobName());
	    wfMovementsList.add(empWF);
	    if (empWF.getWarningMessages() != null && !empWF.getWarningMessages().isEmpty())
		warningsCount++;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String closeProcess() {
	try {
	    MovementsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getMessage(e1.getMessage()));
	    return null;
	}

    }

    public void removeFromList(WFMovementData movement) {
	try {
	    if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {

		if (wfMovementsList.size() == 1)
		    throw new BusinessException("error_moveListEmpty");

		boolean isAllNewRecords = true;
		for (WFMovementData wfMovementData : wfMovementsList) {
		    if (wfMovementData.getInstanceId() != null && !wfMovementData.getEmployeeId().equals(movement.getEmployeeId())) {
			isAllNewRecords = false;
			break;
		    }
		}

		if (isAllNewRecords)
		    throw new BusinessException("error_saveRecordsBeforeDeleting");
	    }

	    if (movement.getInstanceId() != null) {
		MovementsWorkFlow.deleteWFMovement(movement);
	    }
	    if (movement.getWarningMessages() != null && !movement.getWarningMessages().isEmpty())
		warningsCount--;

	    wfMovementsList.remove(movement);
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
	    MovementsWorkFlow.calculateWarnings(wfMovement);
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

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public void reasonTypeListener() {
	if (decisionData.getReasonType() == MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode()) {
	    decisionData.setVacationGrantFlagBoolean(false);
	    vacationGrantFlagDisabled = true;
	} else {
	    vacationGrantFlagDisabled = false;
	}
    }

    public boolean isVacationGrantFlagDisabled() {
	return vacationGrantFlagDisabled;
    }
}