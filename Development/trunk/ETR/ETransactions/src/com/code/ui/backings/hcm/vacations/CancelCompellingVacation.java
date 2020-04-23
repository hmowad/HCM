package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;

@ManagedBean(name = "cancelCompellingVacation")
@ViewScoped
public class CancelCompellingVacation extends VacationBase {

    public CancelCompellingVacation() {
	try {
	    super.init();
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.CANCEL.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.COMPELLING.getCode());

		if (this.beneficiaryType != null) {
		    this.skipLastTwoVacations = true;
		    resetForm();
		}
		getBeneficiaryInfo();
	    }
	    this.getBeneficiaryPanelTitle();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    protected void getBeneficiaryInfo() throws BusinessException {
	adjustProcess();
	if (!skipLastTwoVacations) {
	    reset();
	    this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), VacationTypesEnum.COMPELLING.getCode());
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
		    this.processId = WFProcessesEnum.CANCEL_OFFICERS_COMPELLING_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelOfficersCompellingVacation"));
		    break;
		case 2:
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_COMPELLING_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelSoldiersCompellingVacation"));
		    break;
		case 3:
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_COMPELLING_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelEmployeesCompellingVacation"));
		    break;
		case 6:
		    this.processId = WFProcessesEnum.CANCEL_CONTRACTORS_WITHOUT_SALARY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelContractorsWithoutSalaryVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_URLError"));
		}
	    } else {
		switch (this.vacationMode) {
		case 1:
		    this.processId = WFProcessesEnum.CANCEL_OFFICERS_COMPELLING_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiaryOfficersCompellingVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_OFFICERS_CANCEL_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_COMPELLING_CANCEL_REQUEST_BENF_OFFICERS.getCode()));
		    this.employeeIds = VacationsService.getPresidencyManagers();
		    break;
		case 2:
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_COMPELLING_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiarySoldiersCompellingVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_CANCEL_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_COMPELLING_CANCEL_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 3:
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_COMPELLING_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBenefciaryEmployeesCompellingVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_COMPELLING_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 6:
		    this.processId = WFProcessesEnum.CANCEL_CONTRACTORS_WITHOUT_SALARY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBenefciaryContractorsWithoutSalaryVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_CONTRACTORS_WITHOUT_SALARY_CANCEL_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_COMPELLING_CANCEL_REQUEST_BENF_CONTRACTORS_WITHOUT_SALARY.getCode()));
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