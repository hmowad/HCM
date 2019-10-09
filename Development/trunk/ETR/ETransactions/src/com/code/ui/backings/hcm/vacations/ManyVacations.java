package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

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
		if (this.beneficiaryType != null) {
		    this.skipLastTwoVacations = true;
		    resetForm();
		}

		getBeneficiaryInfo();
	    } else
		adjustProcess();

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
	    this.inquiryBalance();
	    this.getLastTwoVacations();
	}
    }

    public void adjustProcess() {
	try {
	    if (this.beneficiaryType == null) {
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
	    } else {
		switch (this.vacationMode) {
		case 319:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.STUDY.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_STUDY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiaryEmployeesStudyVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_STUDY_VACATION.getCode(), MenuActionsEnum.VAC_STUDY_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 220:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.EXAM.getCode());
		    this.processId = WFProcessesEnum.SOLDIERS_EXAM_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiarySoldiersExamVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_EXAM_VACATION.getCode(), MenuActionsEnum.VAC_EXAM_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";

		    break;
		case 320:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.EXAM.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_EXAM_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesExamVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_EXAM_VACATION.getCode(), MenuActionsEnum.VAC_EXAM_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 214:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MATERNITY.getCode());
		    this.processId = WFProcessesEnum.SOLDIERS_MATERNITY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiarySoldiersMaternityVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_MATERNITY_VACATION.getCode(), MenuActionsEnum.VAC_MATERNITY_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 314:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MATERNITY.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_MATERNITY_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesMaternityVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_MATERNITY_VACATION.getCode(), MenuActionsEnum.VAC_MATERNITY_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 215:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MOTHERHOOD.getCode());
		    this.processId = WFProcessesEnum.SOLDIERS_MOTHERHOOD_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiarySoldiersMotherhoodVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_MOTHERHOOD_VACATION.getCode(), MenuActionsEnum.VAC_MOTHERHOOD_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 315:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MOTHERHOOD.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_MOTHERHOOD_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesMotherhoodVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_MOTHERHOOD_VACATION.getCode(), MenuActionsEnum.VAC_MOTHERHOOD_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 213:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_WAITING_PERIOD.getCode());
		    this.processId = WFProcessesEnum.SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_beneficiarySoldiersDeathWaitingPeriodVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode(), MenuActionsEnum.VAC_DEATH_WAITING_PERIOD_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 313:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_WAITING_PERIOD.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesDeathWaitingPeriodVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode(), MenuActionsEnum.VAC_DEATH_WAITING_PERIOD_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 323:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.RELIEF.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_RELIEF_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesReliefVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_RELIEF_VACATION.getCode(), MenuActionsEnum.VAC_RELIEF_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 312:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.SPORTIVE.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_SPORTIVE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesSportiveVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_SPORTIVE_VACATION.getCode(), MenuActionsEnum.VAC_SPORTIVE_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 321:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.CULTURAL.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_CULTURAL_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesCulturalVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CULTURAL_VACATION.getCode(), MenuActionsEnum.VAC_CULTURAL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 322:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.SOCIAL.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_SOCIAL_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesSocialVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_SOCIAL_VACATION.getCode(), MenuActionsEnum.VAC_SOCIAL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 324:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.COMPENSATION.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_COMPENSATION_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesCompensationVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_COMPENSATION_VACATION.getCode(), MenuActionsEnum.VAC_COMPENSATION_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 316:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.ORPHAN_CARE.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_ORPHAN_CARE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesOrphanCareVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_ORPHAN_CARE_VACATION.getCode(), MenuActionsEnum.VAC_ORPHAN_CARE_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 317:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_OF_RELATIVE.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesDeathOfRelativeVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode(), MenuActionsEnum.VAC_DEATH_OF_RELATIVE_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 318:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.NEW_BABY.getCode());
		    this.processId = WFProcessesEnum.EMPLOYEES_NEW_BABY_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_beneficiaryEmployeesNewBabyVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_NEW_BABY_VACATION.getCode(), MenuActionsEnum.VAC_NEW_BABY_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
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