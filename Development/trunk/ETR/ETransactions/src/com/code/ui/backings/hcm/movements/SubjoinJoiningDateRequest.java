package com.code.ui.backings.hcm.movements;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@ManagedBean(name = "subjoinJoiningDateRequest")
@ViewScoped
public class SubjoinJoiningDateRequest extends MovementsBase {
    /**
     * 1 for Officers subjoin joining, 2 for Soldiers subjoin joining and 3 for Officers subjoin return joining
     **/
    private MovementTransactionData lastValidTran;
    private Boolean returnJoining;

    public SubjoinJoiningDateRequest() {
	try {
	    super.init();
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		if (getRequest().getParameter("returnJoining") == null)
		    returnJoining = false;
		else
		    returnJoining = getRequest().getParameter("returnJoining").toString().equals(FlagsEnum.ON.getCode() + "") ? true : false;
		switch (mode) {
		case 1:
		    if (returnJoining == null || returnJoining.equals(FlagsEnum.OFF.getCode())) {
			setScreenTitle(getMessage("title_officersSubjoinJoiningDateRequest"));
			this.processId = WFProcessesEnum.OFFICERS_SUBJOIN_JOINING_DATE_REQUEST.getCode();
		    } else {
			setScreenTitle(getMessage("title_officersSubjoinTerminationJoiningDateRequest"));
			this.processId = WFProcessesEnum.OFFICERS_SUBJOIN_RETURN_JOINING_DATE_REQUEST.getCode();
		    }
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersSubjoinJoiningDateRequest"));
		    this.processId = WFProcessesEnum.SOLDIERS_SUBJOIN_JOINING_DATE_REQUEST.getCode();
		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
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
		setServerSideErrorMessages(getMessage("error_URLError"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchEmployees() {
	try {
	    beneficiary = EmployeesService.getEmployeeData(wfMovement.getEmployeeId());

	    if (mode == 2 && beneficiary.getCategoryId() == CategoriesEnum.OFFICERS.getCode())
		return;

	    if (returnJoining && beneficiary.getCategoryId() != CategoriesEnum.OFFICERS.getCode())
		return;

	    if (returnJoining)
		lastValidTran = MovementsService.getLastValidMovementTransactionForReturnJoiningDate(wfMovement.getEmployeeId(), MovementTypesEnum.SUBJOIN.getCode(), null);
	    else
		lastValidTran = MovementsService.getLastValidMovementTransactionForJoiningDate(wfMovement.getEmployeeId(), MovementTypesEnum.SUBJOIN.getCode());

	    if (lastValidTran == null) {
		if (returnJoining)
		    throw new BusinessException("error_thereIsNoLastValidTrans");
		else
		    throw new BusinessException("error_noValidMovementTransactionForEmployee");
	    }

	    wfMovement = MovementsWorkFlow.constructWFMovementForJoiningDate(wfMovement.getEmployeeId(), MovementTypesEnum.SUBJOIN.getCode(), lastValidTran.getId());
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

    public Boolean getReturnJoining() {
	return returnJoining;
    }

    public void setReturnJoining(Boolean returnJoining) {
	this.returnJoining = returnJoining;
    }

}
