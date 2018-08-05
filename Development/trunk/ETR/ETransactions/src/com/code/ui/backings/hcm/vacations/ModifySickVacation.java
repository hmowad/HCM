package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;

@ManagedBean(name = "modifySickVacation")
@ViewScoped
public class ModifySickVacation extends VacationBase {

    public ModifySickVacation() {
	try {
	    super.init();

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
		this.setServerSideErrorMessages(this.getMessage("error_general"));
	    }

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.MODIFY.getCode());
		this.vacRequest.setVacationTypeId(VacationTypesEnum.SICK.getCode());

		if (this.vacationMode == 22)
		    this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_TWO.getCode());
		else
		    this.vacRequest.setSubVacationType(SubVacationTypesEnum.SUB_TYPE_ONE.getCode());

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
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
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