package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.CategoriesEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "regularVacation")
@ViewScoped
public class RegularVacation extends VacationBase {

    public RegularVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());
		this.vacRequest.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		this.vacRequest.setLocation(getMessage("label_ksa"));
		this.vacRequest.setStartDate(HijriDateService.getHijriSysDate());

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
	this.inquiryBalance();
	this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode());
	if (this.lastVacation != null) {
	    this.vacRequest.setOldVacationId(this.lastVacation.getId());
	} else {
	    this.lastVacation = new VacationData();
	    this.vacRequest.setOldVacationId(null);
	}
	adjustProcess();
    }

    public void adjustProcess() {
	try {
	    switch (this.vacationMode) {
	    case 1:
		this.processId = WFProcessesEnum.OFFICERS_REGULAR_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_officersReqularVacation"));
		break;
	    case 2:
		this.processId = WFProcessesEnum.SOLDIERS_REGULAR_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_soldiersReqularVacation"));
		break;
	    case 3:
		this.processId = WFProcessesEnum.EMPLOYEES_REGULAR_VACATION.getCode();
		this.setScreenTitle(getMessage("title_employeesReqularVacation"));
		if (this.beneficiary.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode() && this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {

		    String[] contractualYear = VacationsService.calculateContractualYearStartAndEndDates(this.beneficiary, this.vacRequest.getStartDateString());
		    this.vacRequest.setContractualYearStartDateString(contractualYear[0]);
		    this.vacRequest.setContractualYearEndDateString(contractualYear[1]);
		}
		break;
	    default:
		this.setServerSideErrorMessages(this.getMessage("error_general"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }
}