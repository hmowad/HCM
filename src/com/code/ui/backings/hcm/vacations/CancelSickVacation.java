package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;

@ManagedBean(name = "cancelSickVacation")
@ViewScoped
public class CancelSickVacation extends VacationBase {

    public CancelSickVacation() {
	try {
	    super.init();

	    switch (this.vacationMode) {
	    case 1:
		this.processId = WFProcessesEnum.CANCEL_OFFICERS_SICK_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelOfficersSickVacation"));
		break;
	    case 2:
		this.processId = WFProcessesEnum.CANCEL_SOLDIERS_SICK_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelSoldiersSickVacation"));
		break;
	    case 22:
		this.processId = WFProcessesEnum.CANCEL_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelSoldiersWorkInjurySickVacation"));
		break;
	    case 3:
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_SICK_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelEmployeesSickVacation"));
		break;
	    default:
		this.setServerSideErrorMessages(this.getMessage("error_general"));
	    }

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.CANCEL.getCode());
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