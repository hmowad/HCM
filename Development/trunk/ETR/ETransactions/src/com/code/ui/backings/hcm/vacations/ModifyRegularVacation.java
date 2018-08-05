package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;

@ManagedBean(name = "modifyRegularVacation")
@ViewScoped
public class ModifyRegularVacation extends VacationBase {

    public ModifyRegularVacation() {
	try {
	    super.init();

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

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.MODIFY.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.REGULAR.getCode());
		this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode());
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
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }
}