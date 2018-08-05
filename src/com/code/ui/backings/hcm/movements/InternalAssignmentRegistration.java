package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.MovementTransactionViewsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "internalAssignmentRegistration")
@ViewScoped
public class InternalAssignmentRegistration extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;
    private MovementTransactionData movementTrans;
    private Long originalDecisionApprovedId;
    private Long physicalUnitId;
    private String physicalUnitHKey;
    private boolean hasPrivilege;

    private int pageSize = 10;

    public InternalAssignmentRegistration() {
	try {
	    if (getRequest().getParameter("mode") != null) {

		mode = Integer.parseInt(getRequest().getParameter("mode"));
		hasPrivilege = loginEmpData.getIsManager().equals(FlagsEnum.ON.getCode());
		originalDecisionApprovedId = loginEmpData.getEmpId();
		physicalUnitId = loginEmpData.getPhysicalUnitId();
		physicalUnitHKey = loginEmpData.getPhysicalUnitHKey();

		EmployeeData equivalentPrivilegeManager = loginEmpData;

		switch (mode) {

		case 1:
		    setScreenTitle(getMessage("title_officersInternalAssignmentRegistration"));

		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.MVT_INTERNAL_ASSIGNMENT_REGISTRATION_OFFICERS.getCode(), MenuActionsEnum.MVT_INTERNAL_ASSIGNMENT_MANAGER_OFFICERS.getCode())) {
			initInternalAssignmentParameters(equivalentPrivilegeManager);
		    }

		    break;
		case 2:
		    setScreenTitle(getMessage("title_solidersInternalAssignmentRegistration"));

		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.MVT_INTERNAL_ASSIGNMENT_REGISTRATION_SOLDIERS.getCode(), MenuActionsEnum.MVT_INTERNAL_ASSIGNMENT_MANAGER_SOLDIERS.getCode())) {
			initInternalAssignmentParameters(equivalentPrivilegeManager);
		    }

		    break;
		case 3:
		    setScreenTitle(getMessage("title_personsInternalAssignmentRegistration"));

		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.MVT_INTERNAL_ASSIGNMENT_REGISTRATION_CIVILIANS.getCode(), MenuActionsEnum.MVT_INTERNAL_ASSIGNMENT_MANAGER_CIVILIANS.getCode())) {
			initInternalAssignmentParameters(equivalentPrivilegeManager);
		    }

		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_general"));
		}

		if (hasPrivilege)
		    resetForm();

	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    private void initInternalAssignmentParameters(EmployeeData equivalentPrivilegeManager) throws BusinessException {

	UnitData equivalentPrivilegeManagerUnit = UnitsService.getUnitById(equivalentPrivilegeManager.getPhysicalUnitId());

	while (equivalentPrivilegeManagerUnit.getUnitTypeCode() != UnitTypesEnum.SECTOR_COMMANDER.getCode() &&
		equivalentPrivilegeManagerUnit.getUnitTypeCode() != UnitTypesEnum.REGION_COMMANDER.getCode() &&
		equivalentPrivilegeManagerUnit.getUnitTypeCode() != UnitTypesEnum.PRESIDENCY.getCode()) {

	    equivalentPrivilegeManagerUnit = UnitsService.getUnitById(equivalentPrivilegeManagerUnit.getParentUnitId());
	}

	if (equivalentPrivilegeManagerUnit.getPhysicalManagerId() != null) {
	    hasPrivilege = true;
	    originalDecisionApprovedId = equivalentPrivilegeManagerUnit.getPhysicalManagerId();
	    physicalUnitId = equivalentPrivilegeManagerUnit.getId();
	    physicalUnitHKey = equivalentPrivilegeManagerUnit.gethKey();
	}
    }

    public void constructTransaction() {
	try {
	    movementTrans = MovementsService.constructMovementTransaction(movementTrans.getEmployeeId(), null, null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, MovementsReasonTypesEnum.FOR_WORK_INTEREST.getCode(), MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    movementTrans.setExecutionDateFlag(FlagsEnum.ON.getCode());
	    movementTrans.setEffectFlag(FlagsEnum.ON.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    List<MovementTransactionData> movementList = new ArrayList<MovementTransactionData>();
	    movementTrans.setApprovedId(loginEmpData.getEmpId());
	    movementTrans.setDecisionApprovedId(loginEmpData.getEmpId());
	    movementTrans.setOriginalDecisionApprovedId(originalDecisionApprovedId);
	    movementList.add(movementTrans);
	    MovementsService.handleMovementsTransactions(null, movementList, false, null, MovementTransactionViewsEnum.REGISTRATION.getCode());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void resetForm() {
	movementTrans = new MovementTransactionData();
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public MovementTransactionData getMovementTrans() {
	return movementTrans;
    }

    public void setMovementTrans(MovementTransactionData movementTrans) {
	this.movementTrans = movementTrans;
    }

    public Long getPhysicalUnitId() {
	return physicalUnitId;
    }

    public String getPhysicalUnitHKey() {
	return physicalUnitHKey;
    }

    public boolean isHasPrivilege() {
	return hasPrivilege;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
}
