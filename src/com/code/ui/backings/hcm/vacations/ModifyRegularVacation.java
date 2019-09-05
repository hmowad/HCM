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

@ManagedBean(name = "modifyRegularVacation")
@ViewScoped
public class ModifyRegularVacation extends VacationBase {

    public ModifyRegularVacation() {
	try {
	    super.init();
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.MODIFY.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());
		if (this.beneficiaryType != null) {
		    this.skipLastTwoVacations = true;
		    resetForm();
		}
		getBeneficiaryInfo();
	    }

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
	    this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), this.vacRequest.getVacationTypeId());
	    if (this.lastVacation != null) {
		this.vacRequest.setOldVacationId(this.lastVacation.getId());
		this.vacRequest.setStartDateString(this.lastVacation.getStartDateString());
		this.vacRequest.setEndDateString(this.lastVacation.getEndDateString());
		this.vacRequest.setPeriod(this.lastVacation.getPeriod());
		this.vacRequest.setLocationFlag(this.lastVacation.getLocationFlag());
		this.vacRequest.setLocation(this.lastVacation.getLocation());
		this.vacRequest.setContractualYearStartDateString(this.lastVacation.getContractualYearStartDateString());
		this.vacRequest.setContractualYearEndDateString(this.lastVacation.getContractualYearEndDateString());
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
		    this.processId = WFProcessesEnum.MODIFY_OFFICERS_REGULAR_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyOfficersReqularVacation"));
		    break;
		case 2:
		    this.processId = WFProcessesEnum.MODIFY_SOLDIERS_REGULAR_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifySoldiersReqularVacation"));
		    break;
		case 3:
		    this.processId = WFProcessesEnum.MODIFY_EMPLOYEES_REGULAR_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyEmployeesReqularVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (this.vacationMode) {
		case 1:
		    this.processId = WFProcessesEnum.MODIFY_OFFICERS_REGULAR_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyBeneficiaryOfficersRegularVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_OFFICERS_MODIFY_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_MODIFY_REQUEST_BENF_OFFICERS.getCode()));
		    this.employeeIds = VacationsService.getPresidencyManagers();
		    break;
		case 2:
		    this.processId = WFProcessesEnum.MODIFY_SOLDIERS_REGULAR_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyBeneficiarySoldiersReqularVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_MODIFY_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_MODIFY_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 3:
		    this.processId = WFProcessesEnum.MODIFY_EMPLOYEES_REGULAR_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_modifyBeneficiaryEmployeesReqularVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_MODIFY_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_MODIFY_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }

}