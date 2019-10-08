package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;

@ManagedBean(name = "cancelManyVacations")
@ViewScoped
public class CancelManyVacations extends VacationBase {

    public CancelManyVacations() {

	try {
	    super.init();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.CANCEL.getCode());
		if (this.beneficiaryType != null) {
		    this.skipLastTwoVacations = true;
		    resetForm();
		}
		getBeneficiaryInfo();
	    }
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
	    this.lastVacation = VacationsService.getLastVacationData(this.beneficiary.getEmpId(), this.vacRequest.getVacationTypeId());

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
    }

    public void adjustProcess() {
	try {
	    if (this.beneficiaryType == null) {

		switch (this.vacationMode) {
		case 319:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.STUDY.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_STUDY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelEmployeesStudyVacation"));
		    break;
		case 220:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.EXAM.getCode());
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_EXAM_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelSoldiersExamVacation"));
		    break;
		case 320:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.EXAM.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXAM_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesExamVacation"));
		    break;
		case 214:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MATERNITY.getCode());
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_MATERNITY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelSoldiersMaternityVacation"));
		    break;
		case 314:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MATERNITY.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_MATERNITY_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesMaternityVacation"));
		    break;
		case 215:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MOTHERHOOD.getCode());
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_MOTHERHOOD_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelSoldiersMotherhoodVacation"));
		    break;
		case 315:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MOTHERHOOD.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_MOTHERHOOD_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesMotherhoodVacation"));
		    break;
		case 213:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_WAITING_PERIOD.getCode());
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelSoldiersDeathWaitingPeriodVacation"));
		    break;
		case 313:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_WAITING_PERIOD.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesDeathWaitingPeriodVacation"));
		    break;
		case 323:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.RELIEF.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_RELIEF_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesReliefVacation"));
		    break;
		case 312:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.SPORTIVE.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_SPORTIVE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesSportiveVacation"));
		    break;
		case 321:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.CULTURAL.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_CULTURAL_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesCulturalVacation"));
		    break;
		case 322:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.SOCIAL.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_SOCIAL_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesSocialVacation"));
		    break;
		case 324:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.COMPENSATION.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_COMPENSATION_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesCompensationVacation"));
		    break;
		case 316:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.ORPHAN_CARE.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_ORPHAN_CARE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesOrphanCareVacation"));
		    break;
		case 317:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_OF_RELATIVE.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesDeathOfRelativeVacation"));
		    break;
		case 318:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.NEW_BABY.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_NEW_BABY_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelEmployeesNewBabyVacation"));
		    break;
		default:
		    this.setServerSideErrorMessages(this.getMessage("error_general"));
		}
	    } else {
		switch (this.vacationMode) {
		case 319:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.STUDY.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_STUDY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiaryEmployeesStudyVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_STUDY_VACATION.getCode(), MenuActionsEnum.VAC_STUDY_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 220:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.EXAM.getCode());
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_EXAM_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiarySoldiersExamVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_CANCEL_EXAM_VACATION.getCode(), MenuActionsEnum.VAC_EXAM_CANCEL_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 320:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.EXAM.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXAM_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesExamVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_EXAM_VACATION.getCode(), MenuActionsEnum.VAC_EXAM_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 214:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MATERNITY.getCode());
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_MATERNITY_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiarySoldiersMaternityVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_CANCEL_MATERNITY_VACATION.getCode(), MenuActionsEnum.VAC_MATERNITY_CANCEL_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 314:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MATERNITY.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_MATERNITY_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesMaternityVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_MATERNITY_VACATION.getCode(), MenuActionsEnum.VAC_MATERNITY_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 215:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MOTHERHOOD.getCode());
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_MOTHERHOOD_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiarySoldiersMaternityVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_CANCEL_MOTHERHOOD_VACATION.getCode(), MenuActionsEnum.VAC_MOTHERHOOD_CANCEL_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 315:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.MOTHERHOOD.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_MOTHERHOOD_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesMotherhoodVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_MOTHERHOOD_VACATION.getCode(), MenuActionsEnum.VAC_MOTHERHOOD_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 213:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_WAITING_PERIOD.getCode());
		    this.processId = WFProcessesEnum.CANCEL_SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode();
		    this.setScreenTitle(this.getMessage("title_cancelBeneficiarySoldiersDeathWaitingPeriodVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_CANCEL_DEATH_WAITING_PERIOD_VACATION.getCode(), MenuActionsEnum.VAC_DEATH_WAITING_PERIOD_CANCEL_REQUEST_BENF_SOLDIERS.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 313:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_WAITING_PERIOD.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesDeathWaitingPeriodVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_DEATH_WAITING_PERIOD_VACATION.getCode(), MenuActionsEnum.VAC_DEATH_WAITING_PERIOD_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 323:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.RELIEF.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_RELIEF_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesReliefVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_RELIEF_VACATION.getCode(), MenuActionsEnum.VAC_RELIEF_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 312:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.SPORTIVE.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_SPORTIVE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesSportiveVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_SPORTIVE_VACATION.getCode(), MenuActionsEnum.VAC_SPORTIVE_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 321:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.CULTURAL.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_CULTURAL_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesCulturalVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_CULTURAL_VACATION.getCode(), MenuActionsEnum.VAC_CULTURAL_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 322:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.SOCIAL.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_SOCIAL_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesSocialVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_SOCIAL_VACATION.getCode(), MenuActionsEnum.VAC_SOCIAL_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 324:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.COMPENSATION.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_COMPENSATION_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesCompensationVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_COMPENSATION_VACATION.getCode(), MenuActionsEnum.VAC_COMPENSATION_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 316:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.ORPHAN_CARE.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_ORPHAN_CARE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesOrphanCareVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_ORPHAN_CARE_VACATION.getCode(), MenuActionsEnum.VAC_ORPHAN_CARE_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 317:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.DEATH_OF_RELATIVE.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesDeathOfRelativeVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_DEATH_OF_RELATIVE_VACATION.getCode(), MenuActionsEnum.VAC_DEATH_OF_RELATIVE_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
		    this.employeeIds = FlagsEnum.ALL.getCode() + "";
		    break;
		case 318:
		    this.vacRequest.setVacationTypeId(VacationTypesEnum.NEW_BABY.getCode());
		    this.processId = WFProcessesEnum.CANCEL_EMPLOYEES_NEW_BABY_VACATION.getCode();
		    super.setScreenTitle(getMessage("title_cancelBeneficiaryEmployeesNewBabyVacation"));
		    this.setAdmin(SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_NEW_BABY_VACATION.getCode(), MenuActionsEnum.VAC_NEW_BABY_CANCEL_REQUEST_BENF_EMPLOYEES.getCode()));
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