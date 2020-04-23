package com.code.ui.backings.hcm.vacations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.hcm.VacationsService;
import com.code.services.workflow.hcm.VacationsWorkFlow;

@ManagedBean(name = "vacationJoining")
@ViewScoped
public class VacationJoining extends VacationBase {

    private int exceededFlag = 0;

    public VacationJoining() {
	try {
	    super.init();
	    this.setScreenTitle(this.getMessage("title_vacationJoining"));

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.vacRequest = new WFVacation();
		this.vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
		getBeneficiaryInfo();

	    } else {
		if (this.vacRequest.getExceededDays() != null && this.vacRequest.getExceededDays() > 0)
		    exceededFlag = 1;

		adjustProcess();
	    }

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    protected void getBeneficiaryInfo() throws BusinessException {
	this.lastVacation = VacationsService.getLastVacationWithoutJoiningDate(this.beneficiary.getEmpId());

	if (this.lastVacation == null)
	    throw new BusinessException("error_noVacationWithoutJoiningDate");

	this.vacRequest.setOldVacationId(this.lastVacation.getId());
	this.vacRequest.setVacationTypeId(this.lastVacation.getVacationTypeId());
	this.vacRequest.setStartDateString(this.lastVacation.getStartDateString());
	this.vacRequest.setEndDateString(this.lastVacation.getEndDateString());
	this.vacRequest.setPeriod(this.lastVacation.getPeriod());
	this.vacRequest.setLocationFlag(this.lastVacation.getLocationFlag());
	this.vacRequest.setLocation(this.lastVacation.getLocation());

	exceededFlag = 0;
	this.vacRequest.setExceededDays(0);
	this.vacRequest.setNotes(null);

	adjustProcess();
    }

    public void adjustProcess() {
	int categoryId = this.beneficiary.getCategoryId().intValue();
	if (categoryId == CategoriesEnum.OFFICERS.getCode())
	    this.processId = WFProcessesEnum.OFFICERS_VACATION_JOINING.getCode();
	else if (categoryId == CategoriesEnum.SOLDIERS.getCode())
	    this.processId = WFProcessesEnum.SOLDIERS_VACATION_JOINING.getCode();
	else
	    this.processId = WFProcessesEnum.CIVILIANS_VACATION_JOINING.getCode();
    }

    public void exceededFlagListener() {
	if (exceededFlag == 0)
	    this.vacRequest.setExceededDays(0);
    }

    public void searchBeneficiary() {
	try {
	    if (this.beneficiarySearchId != null && !this.beneficiarySearchId.equals("")) {
		EmployeeData tempBeneficiary = EmployeesService.getEmployeeData(Long.parseLong(this.beneficiarySearchId));
		if (tempBeneficiary != null) {
		    if (tempBeneficiary.getEmpId().equals(this.requester.getEmpId())
			    || (FlagsEnum.ON.getCode() != this.requester.getIsManager()
				    || !tempBeneficiary.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.requester.getPhysicalUnitHKey())))) {

			this.setServerSideErrorMessages(this.getMessage("error_notAuthorized"));

		    } else {
			this.beneficiary = tempBeneficiary;
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

    public void print() {
	try {
	    if (this.lastVacation == null)
		throw new BusinessException("error_noVacationWithoutJoiningDate");

	    byte[] bytes = VacationsService.getVacationDecisionBytes(lastVacation.getId(), lastVacation.getVacationTypeId(), lastVacation.getTransactionEmpCategoryId());
	    super.print(bytes);

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /**************************************************************************/

    public String initProcess() {
	try {
	    VacationsWorkFlow.initVacationJoining(this.requester, this.beneficiary, vacRequest, this.processId, this.attachments, this.taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public String approveDM() {
	try {
	    VacationsWorkFlow.doVacationJoiningDM(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectDM() {
	try {
	    VacationsWorkFlow.doVacationJoiningDM(this.requester, this.beneficiary, this.instance, vacRequest, this.currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    /**************************************************************************/
    public int getExceededFlag() {
	return exceededFlag;
    }

    public void setExceededFlag(int exceededFlag) {
	this.exceededFlag = exceededFlag;
    }

}
