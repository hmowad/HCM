package com.code.ui.backings.hcm.movements;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsWishesService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.MovementsWishesWorkFlow;

@ManagedBean(name = "moveWishCancellationRequest")
@ViewScoped
public class MoveWishCancellationRequest extends MovementsWishesBase {

    public MoveWishCancellationRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_moveWishCancellationRequest"));
	    regions = CommonService.getAllRegions();
	    movementWishOpenFlag = true;
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		if (!MovementsWishesService.getMovementWishesOpenFlag()) {
		    movementWishOpenFlag = false;
		    return;
		}
		if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.MVT_MOVE_WISH_CANCELLATION_REQUEST.getCode(), MenuActionsEnum.MVT_MOVE_WISHES_ADMIN.getCode())) {
		    this.processId = WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL.getCode();
		    adminRequest = true;
		} else
		    this.processId = WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL_REQUEST.getCode();

		selectedEmployeeId = loginEmpData.getEmpId();
		selectEmployee();
	    } else {
		wfMovementWish = MovementsWishesWorkFlow.getWFMovementWishByInstanceId(this.instance.getInstanceId());
		regionFromDesc = CommonService.getRegionById(wfMovementWish.getFromRegionId()).getDescription();
		regionToDesc = CommonService.getRegionById(wfMovementWish.getToRegionId()).getDescription();
		attachments = instance.getAttachments();
		beneficiary = EmployeesService.getEmployeeData(wfMovementWish.getEmployeeId());
		this.processId = this.instance.getProcessId();

		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

}