package com.code.ui.backings.hcm.vacations;

import java.util.List;

import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.VacationsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

public abstract class VacationBase extends WFBaseBacking {

    protected int vacationMode;
    protected WFVacation vacRequest;
    protected VacationData lastVacation;

    protected String vacationBalance;

    protected List<EmployeeData> reviewerEmps;
    protected Long selectedReviewerEmpId;

    public void init() {
	try {
	    super.init();

	    if (this.getRequest().getParameter("mode") != null)
		vacationMode = Integer.parseInt(this.getRequest().getParameter("mode"));

	    this.beneficiary = this.requester;

	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		vacRequest = VacationsWorkFlow.getWFVacationByInstanceId(this.instance.getInstanceId());

		if (!vacRequest.getBeneficiaryId().equals(this.requester.getEmpId())) {
		    this.beneficiary = EmployeesService.getEmployeeData(vacRequest.getBeneficiaryId());
		    if (this.beneficiary == null)
			throw new BusinessException("error_general");
		}

		if (vacRequest.getOldVacationId() != null)
		    lastVacation = VacationsService.getVacationDataById(vacRequest.getOldVacationId());
		else
		    lastVacation = new VacationData();

		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(this.getMessage("error_general"));
	}
    }

    public void searchBeneficiary() {
	try {
	    if (this.beneficiarySearchId != null && !this.beneficiarySearchId.equals("")) {
		EmployeeData tempBeneficiary = EmployeesService.getEmployeeData(Long.parseLong(this.beneficiarySearchId));
		if (tempBeneficiary != null) {
		    if (tempBeneficiary.getEmpId().equals(this.requester.getEmpId())
			    || ((FlagsEnum.ON.getCode() != this.requester.getIsManager() || !tempBeneficiary.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.requester.getPhysicalUnitHKey())))
				    && !isEmployeeAuthorizedRequestVacation())) {

			this.setServerSideErrorMessages(this.getMessage("error_notAuthorized"));

		    } else {
			this.beneficiary = tempBeneficiary;
			// adjust the vacation mode and the task url.
			int categoryId = this.beneficiary.getCategoryId().intValue();
			long vacationTypeId = vacRequest.getVacationTypeId();
			int newVacationMode = -1;
			switch (categoryId) {
			case 1:
			    newVacationMode = 1;
			    break;
			case 2:
			    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode() || vacationTypeId == VacationTypesEnum.COMPELLING.getCode()
				    || (vacationTypeId == VacationTypesEnum.SICK.getCode() && vacRequest.getSubVacationType() == SubVacationTypesEnum.SUB_TYPE_ONE.getCode())
				    || vacationTypeId == VacationTypesEnum.FIELD.getCode() || vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode())
				newVacationMode = 2;
			    else if (vacationTypeId == VacationTypesEnum.SICK.getCode() && vacRequest.getSubVacationType() == SubVacationTypesEnum.SUB_TYPE_TWO.getCode())
				newVacationMode = 22;
			    else if (vacationTypeId == VacationTypesEnum.EXAM.getCode())
				newVacationMode = 220;
			    else if (vacationTypeId == VacationTypesEnum.MATERNITY.getCode())
				newVacationMode = 214;
			    else if (vacationTypeId == VacationTypesEnum.MOTHERHOOD.getCode())
				newVacationMode = 215;
			    else if (vacationTypeId == VacationTypesEnum.DEATH_WAITING_PERIOD.getCode())
				newVacationMode = 213;
			    break;
			case 6:
			    if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode())
				newVacationMode = 6;
			    else
				newVacationMode = 3;
			    break;
			case 3:
			case 4:
			case 5:
			case 9:
			    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode() || vacationTypeId == VacationTypesEnum.COMPELLING.getCode()
				    || vacationTypeId == VacationTypesEnum.SICK.getCode() || vacationTypeId == VacationTypesEnum.ACCOMPANY.getCode())
				newVacationMode = 3;
			    else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {
				if (vacRequest.getSubVacationType() == SubVacationTypesEnum.SUB_TYPE_ONE.getCode())
				    newVacationMode = 31;
				else if (vacRequest.getSubVacationType() == SubVacationTypesEnum.SUB_TYPE_TWO.getCode())
				    newVacationMode = 32;
			    } else if (vacationTypeId == VacationTypesEnum.STUDY.getCode())
				newVacationMode = 319;
			    else if (vacationTypeId == VacationTypesEnum.EXAM.getCode())
				newVacationMode = 320;
			    else if (vacationTypeId == VacationTypesEnum.MATERNITY.getCode())
				newVacationMode = 314;
			    else if (vacationTypeId == VacationTypesEnum.MOTHERHOOD.getCode())
				newVacationMode = 315;
			    else if (vacationTypeId == VacationTypesEnum.DEATH_WAITING_PERIOD.getCode())
				newVacationMode = 313;
			    else if (vacationTypeId == VacationTypesEnum.RELIEF.getCode())
				newVacationMode = 323;
			    else if (vacationTypeId == VacationTypesEnum.SPORTIVE.getCode())
				newVacationMode = 312;
			    else if (vacationTypeId == VacationTypesEnum.CULTURAL.getCode())
				newVacationMode = 321;
			    else if (vacationTypeId == VacationTypesEnum.SOCIAL.getCode())
				newVacationMode = 322;
			    else if (vacationTypeId == VacationTypesEnum.COMPENSATION.getCode())
				newVacationMode = 324;
			    else if (vacationTypeId == VacationTypesEnum.ORPHAN_CARE.getCode())
				newVacationMode = 316;
			    else if (vacationTypeId == VacationTypesEnum.DEATH_OF_RELATIVE.getCode())
				newVacationMode = 317;
			    else if (vacationTypeId == VacationTypesEnum.NEW_BABY.getCode())
				newVacationMode = 318;
			    break;
			}
			if ((newVacationMode != -1) && (vacationMode != newVacationMode)) {
			    this.taskUrl = this.taskUrl.replace("mode=" + vacationMode, "mode=" + newVacationMode);
			    vacationMode = newVacationMode;
			}
			getBeneficiaryInfo();
		    }
		} else {
		    this.setServerSideErrorMessages(this.getMessage("error_noEmpFound"));
		}
	    }
	} catch (Exception e) {
	    this.setServerSideErrorMessages(this.getMessage(e.getMessage()));
	}
    }

    private boolean isEmployeeAuthorizedRequestVacation() {
	boolean isAuthorized = false;
	try {
	    if (this.beneficiary.getPhysicalRegionId().longValue() == this.requester.getPhysicalRegionId().longValue()) {

		long beneficiaryCategoryId = this.beneficiary.getCategoryId();
		long vacationTypeId = vacRequest.getVacationTypeId();

		if (CategoriesEnum.OFFICERS.getCode() == beneficiaryCategoryId) {
		    if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		    } else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		    } else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		    } else if (VacationTypesEnum.FIELD.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_FIELD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		    } else if (VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_EXCEPTIONAL_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		    }
		} else if (CategoriesEnum.SOLDIERS.getCode() == beneficiaryCategoryId) {
		    if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    } else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    } else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
			if (SubVacationTypesEnum.SUB_TYPE_ONE.getCode() == vacRequest.getSubVacationType())
			    isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
			else if (SubVacationTypesEnum.SUB_TYPE_TWO.getCode() == vacRequest.getSubVacationType())
			    isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    } else if (VacationTypesEnum.FIELD.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_FIELD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    } else if (VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_EXCEPTIONAL_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    } else if (VacationTypesEnum.EXAM.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_EXAM_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    } else if (VacationTypesEnum.MATERNITY.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_MATERNITY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    } else if (VacationTypesEnum.MOTHERHOOD.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_MOTHERHOOD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    } else if (VacationTypesEnum.DEATH_WAITING_PERIOD.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    }
		} else if (CategoriesEnum.CONTRACTORS.getCode() == beneficiaryCategoryId) {
		    if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_CONTRACTORS_WITHOUT_SALARY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    }
		} else if (CategoriesEnum.PERSONS.getCode() == beneficiaryCategoryId || CategoriesEnum.USERS.getCode() == beneficiaryCategoryId
			|| CategoriesEnum.WAGES.getCode() == beneficiaryCategoryId || CategoriesEnum.MEDICAL_STAFF.getCode() == beneficiaryCategoryId) {
		    if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {
			if (SubVacationTypesEnum.SUB_TYPE_ONE.getCode() == vacRequest.getSubVacationType())
			    isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
			else if (SubVacationTypesEnum.SUB_TYPE_TWO.getCode() == vacRequest.getSubVacationType())
			    isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.STUDY.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_STUDY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.EXAM.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_EXAM_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.MATERNITY.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_MATERNITY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.MOTHERHOOD.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_MOTHERHOOD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.DEATH_WAITING_PERIOD.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.RELIEF.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_RELIEF_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.SPORTIVE.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_SPORTIVE_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.CULTURAL.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_CULTURAL_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.SOCIAL.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_SOCIAL_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.COMPENSATION.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_COMPENSATION_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.ORPHAN_CARE.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_ORPHAN_CARE_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.DEATH_OF_RELATIVE.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    } else if (VacationTypesEnum.NEW_BABY.getCode() == vacationTypeId) {
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_NEW_BABY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    }
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
	return isAuthorized;
    }

    protected void getBeneficiaryInfo() throws BusinessException {
    }

    public void startDateChangeListener(AjaxBehaviorEvent event) {
	inquiryBalance();
    }

    protected void inquiryBalance() {
	try {
	    vacationBalance = VacationsService.calculateVacationBalance(vacRequest.getVacationTypeId(), vacRequest.getSubVacationType(), vacRequest.getLocationFlag(), this.beneficiary, vacRequest.getStartDate());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void locationFlagChangeListner(AjaxBehaviorEvent event) {
	if (vacRequest.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()))
	    vacRequest.setLocation(null);
	else
	    vacRequest.setLocation(getMessage("label_ksa"));
    }

    public boolean hasEarlierStartDateWarning() {
	try {
	    if (!this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode())
		    && !this.role.equals(WFTaskRolesEnum.HISTORY.getCode())
		    && vacRequest != null && vacRequest.getStartDate() != null
		    && vacRequest.getStartDate().before(HijriDateService.getHijriSysDate())) {

		return true;
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
	return false;
    }

    public boolean hasContinousPreviousVacationWarning() {
	try {
	    if (!this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode())
		    && !this.role.equals(WFTaskRolesEnum.HISTORY.getCode())) {

		return VacationsService.checkPreviousVacationContinuity(beneficiary.getEmpId(), beneficiary.getCategoryId(), vacRequest.getStartDate());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
	return false;
    }

    public void print() {
	try {
	    Vacation vac = null;
	    if (vacRequest.getRequestType().intValue() == RequestTypesEnum.NEW.getCode())
		vac = VacationsService.getVacationByBeneficiaryAndStartDate(vacRequest.getBeneficiaryId(), vacRequest.getStartDateString(), FlagsEnum.ALL.getCode(), new Integer[] { RequestTypesEnum.NEW.getCode() });
	    else
		vac = VacationsService.getVacationById(vacRequest.getOldVacationId());

	    if (vac == null)
		throw new BusinessException("error_vacationHasBeenmModifiedOrCanceled");

	    byte[] bytes = VacationsService.getVacationDecisionBytes(vac.getVacationId(), vac.getVacationTypeId(), vac.getTransactionEmpCategoryId());
	    super.print(bytes);

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /**************************************************************************/

    public String initProcess() {
	try {
	    VacationsWorkFlow.initVacation(this.requester, this.beneficiary, vacRequest, this.processId, this.attachments, this.taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String approveDM() {
	try {
	    VacationsWorkFlow.doVacationDM(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectDM() {
	try {
	    VacationsWorkFlow.doVacationDM(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String approveMAE() {
	try {
	    VacationsWorkFlow.doVacationMAE(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectMAE() {
	try {
	    VacationsWorkFlow.doVacationMAE(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String redirectMR() {
	try {
	    VacationsWorkFlow.doVacationMR(this.requester, this.beneficiary, this.instance, this.currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String redirectRER() {
	try {
	    VacationsWorkFlow.doVacationRER(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectRER() {
	try {
	    VacationsWorkFlow.doVacationRER(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String approveSE() {
	try {
	    VacationsWorkFlow.doVacationSE(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectSE() {
	try {
	    VacationsWorkFlow.doVacationSE(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String approveRE() {
	try {
	    VacationsWorkFlow.doVacationRE(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectRE() {
	try {
	    VacationsWorkFlow.doVacationRE(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String signSM() {
	try {
	    VacationsWorkFlow.doVacationSM(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, 1);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifySM() {
	try {
	    VacationsWorkFlow.doVacationSM(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, 2);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectSM() {
	try {
	    VacationsWorkFlow.doVacationSM(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, 0);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String closeProcess() {
	try {
	    VacationsWorkFlow.closeWFInstanceByNotification(this.instance, this.currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    /**************************************************************************/

    public int getVacationMode() {
	return vacationMode;
    }

    public void setVacationMode(int vacationMode) {
	this.vacationMode = vacationMode;
    }

    public String getNormalizedModeFromVacationMode() {
	String normalizedMode = new Integer(this.vacationMode).toString().substring(0, 1);
	if (normalizedMode.equals("6"))
	    normalizedMode = "3";
	return normalizedMode;
    }

    public WFVacation getVacRequest() {
	return vacRequest;
    }

    public void setVacRequest(WFVacation vacRequest) {
	this.vacRequest = vacRequest;
    }

    public VacationData getLastVacation() {
	return lastVacation;
    }

    public void setLastVacation(VacationData lastVacation) {
	this.lastVacation = lastVacation;
    }

    public String getVacationBalance() {
	return vacationBalance;
    }

    public void setVacationBalance(String vacationBalance) {
	this.vacationBalance = vacationBalance;
    }

    public List<EmployeeData> getReviewerEmps() {
	return reviewerEmps;
    }

    public void setReviewerEmps(List<EmployeeData> reviewerEmps) {
	this.reviewerEmps = reviewerEmps;
    }

    public Long getSelectedReviewerEmpId() {
	return selectedReviewerEmpId;
    }

    public void setSelectedReviewerEmpId(Long selectedReviewerEmpId) {
	this.selectedReviewerEmpId = selectedReviewerEmpId;
    }

}
