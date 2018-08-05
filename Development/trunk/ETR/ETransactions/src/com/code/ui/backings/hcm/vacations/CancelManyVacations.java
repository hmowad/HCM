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

@ManagedBean(name = "cancelManyVacations")
@ViewScoped
public class CancelManyVacations extends VacationBase {

    public CancelManyVacations() {
	try {
	    super.init();

	    long vacationTypeId = 0;

	    switch (this.vacationMode) {
	    case 319:
		vacationTypeId = VacationTypesEnum.STUDY.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_STUDY_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelEmployeesStudyVacation"));
		break;
	    case 220:
		vacationTypeId = VacationTypesEnum.EXAM.getCode();
		this.processId = WFProcessesEnum.CANCEL_SOLDIERS_EXAM_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelSoldiersExamVacation"));
		break;
	    case 320:
		vacationTypeId = VacationTypesEnum.EXAM.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXAM_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesExamVacation"));
		break;
	    case 214:
		vacationTypeId = VacationTypesEnum.MATERNITY.getCode();
		this.processId = WFProcessesEnum.CANCEL_SOLDIERS_MATERNITY_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelSoldiersMaternityVacation"));
		break;
	    case 314:
		vacationTypeId = VacationTypesEnum.MATERNITY.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_MATERNITY_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesMaternityVacation"));
		break;
	    case 215:
		vacationTypeId = VacationTypesEnum.MOTHERHOOD.getCode();
		this.processId = WFProcessesEnum.CANCEL_SOLDIERS_MOTHERHOOD_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelSoldiersMotherhoodVacation"));
		break;
	    case 315:
		vacationTypeId = VacationTypesEnum.MOTHERHOOD.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_MOTHERHOOD_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesMotherhoodVacation"));
		break;
	    case 213:
		vacationTypeId = VacationTypesEnum.DEATH_WAITING_PERIOD.getCode();
		this.processId = WFProcessesEnum.CANCEL_SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_cancelSoldiersDeathWaitingPeriodVacation"));
		break;
	    case 313:
		vacationTypeId = VacationTypesEnum.DEATH_WAITING_PERIOD.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesDeathWaitingPeriodVacation"));
		break;
	    case 323:
		vacationTypeId = VacationTypesEnum.RELIEF.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_RELIEF_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesReliefVacation"));
		break;
	    case 312:
		vacationTypeId = VacationTypesEnum.SPORTIVE.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_SPORTIVE_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesSportiveVacation"));
		break;
	    case 321:
		vacationTypeId = VacationTypesEnum.CULTURAL.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_CULTURAL_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesCulturalVacation"));
		break;
	    case 322:
		vacationTypeId = VacationTypesEnum.SOCIAL.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_SOCIAL_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesSocialVacation"));
		break;
	    case 324:
		vacationTypeId = VacationTypesEnum.COMPENSATION.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_COMPENSATION_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesCompensationVacation"));
		break;
	    case 316:
		vacationTypeId = VacationTypesEnum.ORPHAN_CARE.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_ORPHAN_CARE_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesOrphanCareVacation"));
		break;
	    case 317:
		vacationTypeId = VacationTypesEnum.DEATH_OF_RELATIVE.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesDeathOfRelativeVacation"));
		break;
	    case 318:
		vacationTypeId = VacationTypesEnum.NEW_BABY.getCode();
		this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_NEW_BABY_VACATION.getCode();
		super.setScreenTitle(getMessage("title_cancelEmployeesNewBabyVacation"));
		break;
	    default:
		this.setServerSideErrorMessages(this.getMessage("error_general"));
	    }

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.CANCEL.getCode());

		this.vacRequest.setVacationTypeId(vacationTypeId);
		this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), vacationTypeId);

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