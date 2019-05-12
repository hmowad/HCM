package com.code.ui.backings.hcm.vacations;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "compellingVacation")
@ViewScoped
public class CompellingVacation extends VacationBase {

    public CompellingVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.COMPELLING.getCode());
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

	adjustProcess();
	this.inquiryBalance();

	List<VacationData> lastVacations = VacationsService.getVacationsData(this.beneficiary.getEmpId(), this.vacRequest.getVacationTypeId());
	if (lastVacations != null && !lastVacations.isEmpty()) {
	    this.lastVacation = lastVacations.get(0);
	    if (lastVacations.size() > 1)
		this.secondLastVacation = lastVacations.get(1);
	}

	if (this.lastVacation != null) {
	    this.vacRequest.setOldVacationId(this.lastVacation.getId());
	} else {
	    this.lastVacation = new VacationData();
	    this.vacRequest.setOldVacationId(null);
	}

	if (this.secondLastVacation != null) {
	    this.vacRequest.setSecondOldVacationId(this.secondLastVacation.getId());
	} else {
	    this.secondLastVacation = new VacationData();
	    this.vacRequest.setSecondOldVacationId(null);
	}
    }

    public void adjustProcess() {
	switch (this.vacationMode) {
	case 1:
	    this.processId = WFProcessesEnum.OFFICERS_COMPELLING_VACATION.getCode();
	    this.setScreenTitle(this.getMessage("title_officersCompellingVacation"));
	    break;
	case 2:
	    this.processId = WFProcessesEnum.SOLDIERS_COMPELLING_VACATION.getCode();
	    this.setScreenTitle(this.getMessage("title_soldiersCompellingVacation"));
	    break;
	case 3:
	    this.processId = WFProcessesEnum.EMPLOYEES_COMPELLING_VACATION.getCode();
	    this.setScreenTitle(this.getMessage("title_employeesCompellingVacation"));
	    break;
	case 6:
	    this.processId = WFProcessesEnum.CONTRACTORS_WITHOUT_SALARY_VACATION.getCode();
	    this.setScreenTitle(this.getMessage("title_contractorsWithoutSalaryVacation"));
	    break;
	default:
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }

}