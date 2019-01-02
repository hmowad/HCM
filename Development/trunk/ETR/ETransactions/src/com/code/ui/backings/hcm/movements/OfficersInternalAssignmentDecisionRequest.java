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

@ManagedBean(name = "officersInternalAssignmentDecisionRequest")
@ViewScoped
public class OfficersInternalAssignmentDecisionRequest extends MovementsBase implements Serializable {

    private Long selectedEmpId;
    private long regionId;

    public OfficersInternalAssignmentDecisionRequest() {
	this.processId = WFProcessesEnum.OFFICERS_ASSIGNMENT.getCode();
	init();
	regionId = getLoginEmpPhysicalRegionFlag(true);
	try {
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		decisionData = new WFMovementData();
		wfMovementsList = new ArrayList<WFMovementData>();
		internalCopies = new ArrayList<EmployeeData>();
		decisionData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		decisionData.setExecutionDateFlag(FlagsEnum.OFF.getCode());
		decisionData.setBasedOnOrderDate(HijriDateService.getHijriSysDate());
		decisionData.setReasonType(MovementsReasonTypesEnum.FOR_WORK_INTEREST.getCode());
		decisionData.setVerbalOrderFlag(FlagsEnum.OFF.getCode());
	    } else {
		wfMovementsList = MovementsWorkFlow.getWFMovementDataByInstanceId(this.instance.getInstanceId());
		decisionData = wfMovementsList.get(0);
		internalCopies = EmployeesService.getEmployeesByIdsString(decisionData.getInternalCopies());
		if (internalCopies == null)
		    internalCopies = new ArrayList<EmployeeData>();
		externalCopies = decisionData.getExternalCopies();
	    }
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getMessage(e1.getMessage()));
	}
    }

    public void init() {
	super.init();
    }

    public void addWFMovement() {
	try {
	    WFMovementData wfMovement = MovementsWorkFlow.constructWFMovement(processId, selectedEmpId.longValue(), null, FlagsEnum.OFF.getCode(), HijriDateService.getHijriSysDate(), null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, MovementsReasonTypesEnum.FOR_WORK_INTEREST.getCode(), null, null, null, MovementTypesEnum.ASSIGNMENT.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    wfMovementsList.add(wfMovement);
	} catch (Exception e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), ((BusinessException) e1).getParams()));
	}
    }

    public void assignmentUnitListener(WFMovementData wfMovement) {
	wfMovement.setJobId(null);
	wfMovement.setJobName(null);
    }

    public void deleteWFMovement(WFMovementData wfMovement) {
	try {
	    if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {

		if (wfMovementsList.size() == 1)
		    throw new BusinessException("error_officersAssignmentListEmpty");

		boolean isAllNewRecords = true;
		for (WFMovementData wfMovementData : wfMovementsList) {
		    if (wfMovementData.getInstanceId() != null && !wfMovementData.getEmployeeId().equals(wfMovement.getEmployeeId())) {
			isAllNewRecords = false;
			break;
		    }
		}

		if (isAllNewRecords)
		    throw new BusinessException("error_saveRecordsBeforeDeleting");
	    }

	    if (wfMovement.getInstanceId() != null) {
		MovementsWorkFlow.deleteWFMovement(wfMovement);
	    }
	    wfMovementsList.remove(wfMovement);
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
