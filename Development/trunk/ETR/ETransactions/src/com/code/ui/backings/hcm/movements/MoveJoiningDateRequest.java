package com.code.ui.backings.hcm.movements;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.CategoriesEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@ManagedBean(name = "moveJoiningDateRequest")
@ViewScoped
public class MoveJoiningDateRequest extends MovementsBase {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private MovementTransactionData lastValidTran;

    public MoveJoiningDateRequest() {
	try {
	    super.init();
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersMoveJoiningDateRequest"));
		    this.processId = WFProcessesEnum.OFFICERS_MOVE_JOINING_DATE_REQUEST.getCode();
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersMoveJoiningDateRequest"));
		    this.processId = WFProcessesEnum.SOLDIERS_MOVE_JOINING_DATE_REQUEST.getCode();
		    break;

		default:
		    setServerSideErrorMessages(getMessage("error_general"));
		}

		wfMovement = new WFMovementData();
		if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		    wfMovement.setEmployeeId(this.loginEmpData.getEmpId());
		    searchEmployees();
		} else {
		    wfMovement = MovementsWorkFlow.getWFMovementDataByInstanceId(instance.getInstanceId()).get(0);
		    lastValidTran = MovementsService.getMovementTransactionById(wfMovement.getTransactionId());
		    beneficiary = EmployeesService.getEmployeeData(wfMovement.getEmployeeId());
		}
	    } else
		setServerSideErrorMessages(getMessage("error_general"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchEmployees() {
	try {
	    beneficiary = EmployeesService.getEmployeeData(wfMovement.getEmployeeId());

	    if (mode == 2 && beneficiary.getCategoryId() == CategoriesEnum.OFFICERS.getCode())
		return;

	    lastValidTran = MovementsService.getLastValidMovementTransactionForJoiningDate(wfMovement.getEmployeeId(), MovementTypesEnum.MOVE.getCode());
	    if (lastValidTran == null)
		throw new BusinessException("error_noValidMovementTransactionForEmployee");

	    wfMovement = MovementsWorkFlow.constructWFMovementForJoiningDate(wfMovement.getEmployeeId(), MovementTypesEnum.MOVE.getCode(), lastValidTran.getId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public MovementTransactionData getLastValidTran() {
	return lastValidTran;
    }

    public void setLastValidTran(MovementTransactionData lastValidTran) {
	this.lastValidTran = lastValidTran;
    }

    public String initMovement() {
	try {
	    MovementsWorkFlow.initMovementJoiningDate(requester, wfMovement, processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveMovementDM() {
	try {
	    MovementsWorkFlow.doMovementJoiningDateDM(requester, wfMovement, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementDM() {
	try {
	    MovementsWorkFlow.doMovementJoiningDateDM(requester, wfMovement, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }
}
