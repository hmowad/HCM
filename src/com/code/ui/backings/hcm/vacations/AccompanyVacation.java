package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "accompanyVacation")
@ViewScoped
public class AccompanyVacation extends VacationBase {

    public AccompanyVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.ACCOMPANY.getCode());
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
	    this.updateBeneficiaryPanelTitle();
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
		case 3:
		    this.processId = WFProcessesEnum.EMPLOYEES_ACCOMPANY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_employeesAccompanyVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (this.vacationMode) {
		case 3:
		    this.processId = WFProcessesEnum.EMPLOYEES_ACCOMPANY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiaryEmployeesAccompanyVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_ACCOMPANY_VACATION.getCode(), MenuActionsEnum.VAC_ACCOMPANY_REQUEST_BENF_EMPLOYEES.getCode()));
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