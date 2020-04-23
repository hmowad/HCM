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

@ManagedBean(name = "sickVacation")
@ViewScoped
public class SickVacation extends VacationBase {

    public SickVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.SICK.getCode());

		if (this.vacationMode == 22)
		    this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_TWO.getCode());
		else
		    this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_ONE.getCode());

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

	    this.getBeneficiaryPanelTitle();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
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
		    this.processId = WFProcessesEnum.OFFICERS_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_officersSickVacation"));
		    break;
		case 2:
		    this.processId = WFProcessesEnum.SOLDIERS_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_soldiersSickVacation"));
		    break;
		case 22:
		    this.processId = WFProcessesEnum.SOLDIERS_WORK_INJURY_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_soldiersWorkInjurySickVacation"));
		    break;
		case 3:
		    this.processId = WFProcessesEnum.EMPLOYEES_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_employeesSickVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_URLError"));
		}
	    } else {
		switch (this.vacationMode) {
		case 1:
		    this.processId = WFProcessesEnum.OFFICERS_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiaryOfficersSickVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_OFFICERS_SICK_VACATION.getCode(), MenuActionsEnum.VAC_SICK_REQUEST_BENF_OFFICERS.getCode()));
		    this.employeeIds = VacationsService.getPresidencyManagers();

		    break;
		case 2:
		    this.processId = WFProcessesEnum.SOLDIERS_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiarySoldiersSickVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_SICK_VACATION.getCode(), MenuActionsEnum.VAC_SICK_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 22:
		    this.processId = WFProcessesEnum.SOLDIERS_WORK_INJURY_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiarySoldiersWorkInjurySickVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode(), MenuActionsEnum.VAC_WORK_INJURY_SICK_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 3:
		    this.processId = WFProcessesEnum.EMPLOYEES_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiaryEmployeesSickVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_SICK_VACATION.getCode(), MenuActionsEnum.VAC_SICK_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_URLError"));
		}
	    }

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }
}