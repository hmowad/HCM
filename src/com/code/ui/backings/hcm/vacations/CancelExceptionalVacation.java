package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;

@ManagedBean(name = "cancelExceptionalVacation")
@ViewScoped
public class CancelExceptionalVacation extends VacationBase {

    public CancelExceptionalVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.CANCEL.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.EXCEPTIONAL.getCode());

		if (this.vacationMode == 31)
		    this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_ONE.getCode());
		else if (this.vacationMode == 32)
		    this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_TWO.getCode());
		if (this.beneficiaryType != null) {
		    this.skipLastTwoVacations = true;
		    resetForm();
		}
		getBeneficiaryInfo();
	    }
	    this.getBeneficiaryPanelTitle();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }

    protected void getBeneficiaryInfo() throws BusinessException {
	adjustProcess();

	if (!skipLastTwoVacations) {
	    reset();
	    this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), this.vacRequest.getVacationTypeId(), this.vacRequest.getSubVacationType());
	    if (this.lastVacation != null) {
		this.vacRequest.setOldVacationId(this.lastVacation.getId());
		this.vacRequest.setStartDateString(this.lastVacation.getStartDateString());
		this.vacRequest.setEndDateString(this.lastVacation.getEndDateString());
		this.vacRequest.setPeriod(this.lastVacation.getPeriod());
		this.vacRequest.setLocationFlag(this.lastVacation.getLocationFlag());
		this.vacRequest.setLocation(this.lastVacation.getLocation());
		this.attachments = this.lastVacation.getAttachments();
	    } else {
		this.lastVacation = new VacationData();
	    }
	}
    }

    public void adjustProcess() {
	try {
	    if (this.beneficiaryType == null) {
		switch (this.vacationMode) {
		case 1:
		    this.processId = WFProcessesEnum.CANCEL_OFFICERS_EXCEPTIONAL_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelOfficersExceptionalVacation"));
		    break;
		case 2:
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_EXCEPTIONAL_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelSoldiersExceptionalVacation"));
		    break;
		case 31:
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelEmployeesExceptionalForCircumstancesVacation"));
		    break;
		case 32:
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelEmployeesExceptionalForAccompanyVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (this.vacationMode) {
		case 1:
		    this.processId = WFProcessesEnum.CANCEL_OFFICERS_EXCEPTIONAL_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBenefciaryOfficersExceptionalVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_OFFICERS_CANCEL_EXCEPTIONAL_VACATION.getCode(), MenuActionsEnum.VAC_EXCEPTIONAL_CANCEL_REQUEST_BENF_OFFICERS.getCode()));
		    this.employeeIds = VacationsService.getPresidencyManagers();

		    break;
		case 2:
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_EXCEPTIONAL_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBenefciarySoldiersExceptionalVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_CANCEL_EXCEPTIONAL_VACATION.getCode(), MenuActionsEnum.VAC_EXCEPTIONAL_CANCEL_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";

		    break;
		case 31:
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiaryEmployeesExceptionalForCircumstancesVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode(), MenuActionsEnum.VAC_EXCEPTIONAL_FOR_CIRCUMSTANCES_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 32:
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiaryEmployeesExceptionalForAccompanyVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode(), MenuActionsEnum.VAC_EXCEPTIONAL_FOR_ACCOMPANY_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}

	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

}