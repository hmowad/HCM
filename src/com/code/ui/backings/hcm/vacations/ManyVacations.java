package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

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

@ManagedBean(name = "manyVacations")
@ViewScoped
public class ManyVacations extends VacationBase {

    public ManyVacations() {
	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
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
	this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), this.vacRequest.getVacationTypeId());
	if (this.lastVacation != null) {
	    this.vacRequest.setOldVacationId(this.lastVacation.getId());
	} else {
	    this.lastVacation = new VacationData();
	    this.vacRequest.setOldVacationId(null);
	}
    }

    public void adjustProcess() {
	try {
	    switch (this.vacationMode) {
	    case 319:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.STUDY.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_STUDY_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_employeesStudyVacation"));
		break;
	    case 220:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.EXAM.getCode());
		this.processId = WFProcessesEnum.SOLDIERS_EXAM_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_soldiersExamVacation"));
		break;
	    case 320:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.EXAM.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_EXAM_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesExamVacation"));
		break;
	    case 214:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.MATERNITY.getCode());
		this.processId = WFProcessesEnum.SOLDIERS_MATERNITY_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_soldiersMaternityVacation"));
		break;
	    case 314:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.MATERNITY.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_MATERNITY_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesMaternityVacation"));
		break;
	    case 215:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.MOTHERHOOD.getCode());
		this.processId = WFProcessesEnum.SOLDIERS_MOTHERHOOD_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_soldiersMotherhoodVacation"));
		break;
	    case 315:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.MOTHERHOOD.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_MOTHERHOOD_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesMotherhoodVacation"));
		break;
	    case 213:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_WAITING_PERIOD.getCode());
		this.processId = WFProcessesEnum.SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode();
		this.setScreenTitle(this.getMessage("title_soldiersDeathWaitingPeriodVacation"));
		break;
	    case 313:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_WAITING_PERIOD.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesDeathWaitingPeriodVacation"));
		break;
	    case 323:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.RELIEF.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_RELIEF_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesReliefVacation"));
		break;
	    case 312:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.SPORTIVE.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_SPORTIVE_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesSportiveVacation"));
		break;
	    case 321:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.CULTURAL.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_CULTURAL_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesCulturalVacation"));
		break;
	    case 322:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.SOCIAL.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_SOCIAL_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesSocialVacation"));
		break;
	    case 324:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.COMPENSATION.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_COMPENSATION_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesCompensationVacation"));
		break;
	    case 316:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.ORPHAN_CARE.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_ORPHAN_CARE_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesOrphanCareVacation"));
		break;
	    case 317:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_OF_RELATIVE.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesDeathOfRelativeVacation"));
		break;
	    case 318:
		this.vacRequest.setVacationTypeId(VacationTypesEnum.NEW_BABY.getCode());
		this.processId = WFProcessesEnum.EMPLOYEES_NEW_BABY_VACATION.getCode();
		super.setScreenTitle(getMessage("title_employeesNewBabyVacation"));
		break;
	    default:
		this.setServerSideErrorMessages(this.getMessage("error_general"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }

    public void locationFlagChangeListner(AjaxBehaviorEvent event) {
	super.locationFlagChangeListner(event);

	if (vacRequest.getVacationTypeId() == VacationTypesEnum.SPORTIVE.getCode()
		|| vacRequest.getVacationTypeId() == VacationTypesEnum.CULTURAL.getCode()
		|| vacRequest.getVacationTypeId() == VacationTypesEnum.SOCIAL.getCode()) {
	    this.inquiryBalance();
	}
    }
}