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

@ManagedBean(name = "cancelAccompanyVacation")
@ViewScoped
public class CancelAccompanyVacation extends VacationBase {

    public CancelAccompanyVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.CANCEL.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.ACCOMPANY.getCode());
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
	    this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), this.vacRequest.getVacationTypeId());
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
		case 3:
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_ACCOMPANY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelEmployeesAccompanyVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (this.vacationMode) {
		case 3:
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_ACCOMPANY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiaryEmployeesAccompanyVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_ACCOMPANY_VACATION.getCode(), MenuActionsEnum.VAC_ACCOMPANY_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
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