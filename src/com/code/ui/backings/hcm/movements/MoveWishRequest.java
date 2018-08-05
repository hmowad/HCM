package com.code.ui.backings.hcm.movements;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
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

@ManagedBean(name = "moveWishRequest")
@ViewScoped
public class MoveWishRequest extends MovementsWishesBase {

    public MoveWishRequest() {
	super.init();
	setScreenTitle(getMessage("title_moveWishRequest"));
	setRegions(CommonService.getAllRegions());
	try {
	    movementWishOpenFlag = true;

	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {

		if (!MovementsWishesService.getMovementWishesOpenFlag()) {
		    movementWishOpenFlag = false;
		    return;
		}

		if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.MVT_MOVE_WISH_REQUEST.getCode(), MenuActionsEnum.MVT_MOVE_WISHES_ADMIN.getCode())) {
		    this.processId = WFProcessesEnum.SOLDIERS_MOVE_WISH.getCode();
		    adminRequest = true;
		} else
		    this.processId = WFProcessesEnum.SOLDIERS_MOVE_WISH_REQUEST.getCode();

		if (!adminRequest && loginEmpData.getIsManager().equals(FlagsEnum.OFF.getCode()) && !loginEmpData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
		    throw new BusinessException("error_employeeMustBeSoldier");

		selectedEmployeeId = loginEmpData.getEmpId();
		selectEmployee();
	    } else {
		wfMovementWish = MovementsWishesWorkFlow.getWFMovementWishByInstanceId(this.instance.getInstanceId());
		beneficiary = EmployeesService.getEmployeeData(wfMovementWish.getEmployeeId());
		this.processId = this.instance.getProcessId();
		regionFromDesc = CommonService.getRegionById(wfMovementWish.getFromRegionId()).getDescription();
		regionToDesc = CommonService.getRegionById(wfMovementWish.getToRegionId()).getDescription();
		attachments = instance.getAttachments();

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
