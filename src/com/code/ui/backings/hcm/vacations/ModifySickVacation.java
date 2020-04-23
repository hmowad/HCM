package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.vacations.VacationData;
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

@ManagedBean(name = "modifySickVacation")
@ViewScoped
public class ModifySickVacation extends VacationBase {

    public ModifySickVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.MODIFY.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.SICK.getCode());

		if (this.vacationMode == 22)
		    this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_TWO.getCode());
		else
		    this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_ONE.getCode());

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
	    this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), this.vacRequest.getVacationTypeId(), this.vacRequest.getSubVacationType());
	    if (this.lastVacation != null) {
		this.vacRequest.setOldVacationId(this.lastVacation.getId());
		this.vacRequest.setStartDateString(this.lastVacation.getStartDateString());
		this.vacRequest.setEndDateString(this.lastVacation.getEndDateString());
		this.vacRequest.setPeriod(this.lastVacation.getPeriod());
		this.vacRequest.setLocationFlag(this.lastVacation.getLocationFlag());
		this.vacRequest.setLocation(this.lastVacation.getLocation());

		this.vacRequest.setExtPeriod(this.lastVacation.getExtPeriod());
		this.vacRequest.setExtStartDateString(this.lastVacation.getExtStartDateString());
		this.vacRequest.setExtEndDateString(this.lastVacation.getExtEndDateString());
		this.vacRequest.setExtLocation(this.lastVacation.getExtLocation());

		this.attachments = this.lastVacation.getAttachments();
	    } else {
		this.lastVacation = new VacationData();
	    }
	}
    }

    public void adjustInternalExternalVacationData(AjaxBehaviorEvent event) {
	this.locationFlagChangeListner(event);
	if (vacRequest.getLocationFlag() == LocationFlagsEnum.INTERNAL_EXTERNAL.getCode())
	    vacRequest.setExtStartDate(vacRequest.getStartDate());
	else {
	    vacRequest.setExtStartDate(null);
	    vacRequest.setExtPeriod(null);
	    vacRequest.setExtLocation(null);
	}
    }

    public void adjustProcess() throws BusinessException {
	try {
	    if (this.beneficiaryType == null) {
		switch (this.vacationMode) {
		case 1:
		    this.processId = WFProcessesEnum.MODIFY_OFFICERS_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyOfficersSickVacation"));
		    break;
		case 2:
		    this.processId = WFProcessesEnum.MODIFY_SOLDIERS_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifySoldiersSickVacation"));
		    break;
		case 22:
		    this.processId = WFProcessesEnum.MODIFY_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifySoldiersWorkInjurySickVacation"));
		    break;
		case 3:
		    this.processId = WFProcessesEnum.MODIFY_EMPLOYEES_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyEmployeesSickVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_URLError"));
		}
	    } else {
		switch (this.vacationMode) {
		case 1:
		    this.processId = WFProcessesEnum.MODIFY_OFFICERS_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyBeneficiaryOfficerSickVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_OFFICERS_MODIFY_SICK_VACATION.getCode(), MenuActionsEnum.VAC_SICK_MODIFY_REQUEST_BENF_OFFICERS.getCode()));
		    this.employeeIds = VacationsService.getPresidencyManagers();
		    break;
		case 2:
		    this.processId = WFProcessesEnum.MODIFY_SOLDIERS_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyBeneficiarySoldiersSickVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_MODIFY_SICK_VACATION.getCode(), MenuActionsEnum.VAC_SICK_MODIFY_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 22:
		    this.processId = WFProcessesEnum.MODIFY_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyBeneficiarySoldiersWorkInjurySickVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_MODIFY_WORK_INJURY_SICK_VACATION.getCode(), MenuActionsEnum.VAC_WORK_INJURY_SICK_MODIFY_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 3:
		    this.processId = WFProcessesEnum.MODIFY_EMPLOYEES_SICK_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyBeneficiaryEmployeesSickVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_MODIFY_SICK_VACATION.getCode(), MenuActionsEnum.VAC_SICK_MODIFY_REQUEST_BENF_EMPLOYEES.getCode()));
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

    public boolean getLocFlagWrapper() {
	return (vacRequest.getLocationFlag() == LocationFlagsEnum.INTERNAL_EXTERNAL.getCode()) ? true : false;
    }

    public void setLocFlagWrapper(boolean locFlagWrapper) {
	if (locFlagWrapper)
	    vacRequest.setLocationFlag(LocationFlagsEnum.INTERNAL_EXTERNAL.getCode());
	else
	    vacRequest.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
    }
}