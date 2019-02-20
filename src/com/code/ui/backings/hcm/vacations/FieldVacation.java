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

@ManagedBean(name = "fieldVacation")
@ViewScoped
public class FieldVacation extends VacationBase {

    public FieldVacation() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.FIELD.getCode());
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
	List<VacationData> lastVacations = VacationsService.getVacationsData(this.beneficiary.getEmpId(), this.vacRequest.getVacationTypeId());
	this.lastVacation = (lastVacations != null && !lastVacations.isEmpty()) ? lastVacations.get(0) : new VacationData();
	this.secondLastVacation = (lastVacations != null && lastVacations.size() > 1) ? lastVacations.get(1) : new VacationData();
	if (lastVacations != null) {
	    this.vacRequest.setOldVacationId(this.lastVacation.getId());
	} else {
	    this.vacRequest.setOldVacationId(null);
	}
	adjustProcess();
    }

    public void adjustProcess() {
	switch (this.vacationMode) {
	case 1:
	    this.processId = WFProcessesEnum.OFFICERS_FIELD_VACATION.getCode();
	    this.setScreenTitle(this.getMessage("title_officersFieldVacation"));
	    break;
	case 2:
	    this.processId = WFProcessesEnum.SOLDIERS_FIELD_VACATION.getCode();
	    this.setScreenTitle(this.getMessage("title_soldiersFieldVacation"));
	    break;
	default:
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }
}