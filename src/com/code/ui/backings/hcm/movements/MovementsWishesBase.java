package com.code.ui.backings.hcm.movements;

import java.util.List;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.workflow.hcm.movements.WFMovementWish;
import com.code.enums.NavigationEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.MovementsWishesWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

public abstract class MovementsWishesBase extends WFBaseBacking {

    protected WFMovementWish wfMovementWish;
    protected Long selectedReviewerEmpId;
    protected List<EmployeeData> reviewerEmps;
    protected Long selectedEmployeeId;
    protected List<Region> regions;
    protected String regionFromDesc;
    protected String regionToDesc;
    protected boolean adminRequest;
    protected boolean movementWishOpenFlag;

    public boolean canChangeRequestData() {
	if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode()))
	    return true;

	if ((this.getRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()))
		&& this.currentEmployee.getEmpId().equals(this.requester.getEmpId()))
	    return true;

	return false;
    }

    public void selectEmployee() {
	try {
	    beneficiary = EmployeesService.getEmployeeData(selectedEmployeeId);

	    Object[] wfMovementWishAndAttachment = MovementsWishesWorkFlow.constructWfMovementWish(selectedEmployeeId, processId);
	    wfMovementWish = (WFMovementWish) wfMovementWishAndAttachment[0];
	    attachments = wfMovementWishAndAttachment[1] == null ? null : (String) wfMovementWishAndAttachment[1];
	    regionFromDesc = CommonService.getRegionById(wfMovementWish.getFromRegionId()).getDescription();

	    if (processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL.getCode()
		    || processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL_REQUEST.getCode()) {
		regionToDesc = CommonService.getRegionById(wfMovementWish.getToRegionId()).getDescription();
	    }
	} catch (BusinessException e) {
	    wfMovementWish = null;
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /*********************************************** Work Flow Steps ************************************************/
    public String initWFMovementWish() {
	try {
	    MovementsWishesWorkFlow.initWFMovementWish(requester, wfMovementWish, attachments, processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFMovementWishDM() {
	try {
	    MovementsWishesWorkFlow.doWFMovementWishDM(requester, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFMovementWishDM() {
	try {
	    MovementsWishesWorkFlow.doWFMovementWishDM(requester, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectWFMovementWishMR() {
	try {
	    MovementsWishesWorkFlow.doWFMovementWishMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFMovementWishRE() {
	try {

	    MovementsWishesWorkFlow.doWFMovementWishRE(requester, wfMovementWish, instance, attachments, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFMovementWishRE() {
	try {

	    MovementsWishesWorkFlow.doWFMovementWishRE(requester, wfMovementWish, instance, attachments, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFMovementWishSM() {

	try {
	    MovementsWishesWorkFlow.doWFMovementWishSM(requester, instance, wfMovementWish, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFMovementWishSM() {
	try {
	    MovementsWishesWorkFlow.doWFMovementWishSM(requester, instance, wfMovementWish, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFMovementWishSM() {
	try {
	    MovementsWishesWorkFlow.doWFMovementWishSM(requester, instance, wfMovementWish, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    MovementsWishesWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    /****************************************************************************************************/
    public WFMovementWish getWfMovementWish() {
	return wfMovementWish;
    }

    public void setWfMovementWish(WFMovementWish wfMovementWish) {
	this.wfMovementWish = wfMovementWish;
    }

    public Long getSelectedReviewerEmpId() {
	return selectedReviewerEmpId;
    }

    public void setSelectedReviewerEmpId(Long selectedReviewerEmpId) {
	this.selectedReviewerEmpId = selectedReviewerEmpId;
    }

    public List<EmployeeData> getReviewerEmps() {
	return reviewerEmps;
    }

    public void setReviewerEmps(List<EmployeeData> reviewerEmps) {
	this.reviewerEmps = reviewerEmps;
    }

    public boolean isAdminRequest() {
	return adminRequest;
    }

    public void setAdminRequest(boolean adminRequest) {
	this.adminRequest = adminRequest;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public String getRegionFromDesc() {
	return regionFromDesc;
    }

    public void setRegionFromDesc(String regionFromDesc) {
	this.regionFromDesc = regionFromDesc;
    }

    public String getRegionToDesc() {
	return regionToDesc;
    }

    public void setRegionToDesc(String regionToDesc) {
	this.regionToDesc = regionToDesc;
    }

    public Long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(Long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }

    public boolean isMovementWishOpenFlag() {
	return movementWishOpenFlag;
    }

    public void setMovementWishOpenFlag(boolean movementWishOpenFlag) {
	this.movementWishOpenFlag = movementWishOpenFlag;
    }
}
