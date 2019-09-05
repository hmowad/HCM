package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
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
import com.code.services.util.HijriDateService;

@ManagedBean(name = "exceptionalVacation")
@ViewScoped
public class ExceptionalVacation extends VacationBase {

    public ExceptionalVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.EXCEPTIONAL.getCode());
		this.vacRequest.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		this.vacRequest.setLocation(getMessage("label_ksa"));
		this.vacRequest.setStartDate(HijriDateService.getHijriSysDate());
		if (this.beneficiaryType != null) {
		    this.skipLastTwoVacations = true;
		    resetForm();
		}
		getBeneficiaryInfo();
	    } else
		adjustProcess();

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
	    this.inquiryBalance();
	    this.getLastTwoVacations();
	}
    }

    public void adjustProcess() {
	try {
	    if (this.beneficiaryType == null) {
		switch (this.vacationMode) {
		case 1:
		    this.processId = WFProcessesEnum.OFFICERS_EXCEPTIONAL_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_officersExceptionalVacation"));
		    break;
		case 2:
		    this.processId = WFProcessesEnum.SOLDIERS_EXCEPTIONAL_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_soldiersExceptionalVacation"));
		    break;
		case 31:
		    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode()))
			this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_ONE.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_employeesExceptionalForCircumstancesVacation"));
		    break;
		case 32:
		    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode()))
			this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_TWO.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_employeesExceptionalForAccompanyVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (this.vacationMode) {

		case 1:
		    this.processId = WFProcessesEnum.OFFICERS_EXCEPTIONAL_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiaryOfficersExceptionalVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_OFFICERS_EXCEPTIONAL_VACATION.getCode(), MenuActionsEnum.VAC_EXCEPTIONAL_REQUEST_BENF_OFFICERS.getCode()));
		    this.employeeIds = VacationsService.getPresidencyManagers();
		    break;
		case 2:
		    this.processId = WFProcessesEnum.SOLDIERS_EXCEPTIONAL_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiarySoldiersExceptionalVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_EXCEPTIONAL_VACATION.getCode(), MenuActionsEnum.VAC_EXCEPTIONAL_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";

		    break;
		case 31:
		    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode()))
			this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_ONE.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiaryEmployeesExceptionalForCircumstancesVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode(), MenuActionsEnum.VAC_EXCEPTIONAL_FOR_CIRCUMSTANCES_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 32:
		    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode()))
			this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_TWO.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiaryEmployeesExceptionalForAccompanyVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode(), MenuActionsEnum.VAC_EXCEPTIONAL_FOR_ACCOMPANY_REQUEST_BENF_EMPLOYEES.getCode()));
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