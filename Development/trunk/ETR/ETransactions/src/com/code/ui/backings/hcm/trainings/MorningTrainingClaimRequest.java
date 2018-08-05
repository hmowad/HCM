package com.code.ui.backings.hcm.trainings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "morningTrainingClaimRequest")
@ViewScoped
public class MorningTrainingClaimRequest extends TrainingAndScholarshipBase {

    public MorningTrainingClaimRequest() {
	super.init();
	setScreenTitle(getMessage("title_morningTrainingClaimRequest"));
	try {
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_MORNING_CLAIM_REQUEST.getCode(), MenuActionsEnum.TRN_CLAIMS_REQUEST.getCode())) {
		    this.processId = WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode();
		} else {
		    this.processId = WFProcessesEnum.MORNING_TRAINING_CLAIM_REQUEST.getCode();
		}

		wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), (long) TrainingTypesEnum.MORNING_COURSE.getCode(), processId);
	    } else {
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		this.processId = this.instance.getProcessId();
	    }

	    if (this.processId == WFProcessesEnum.MORNING_TRAINING_CLAIM.getCode())
		isDecisionRequest = true;

	    if (requester.getEmpId().equals(wfTraining.getEmployeeId()))
		beneficiary = requester;
	    else
		beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());

	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public void constructWFTraining() {
	try {
	    wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(wfTraining.getEmployeeId(), wfTraining.getCategoryId(), wfTraining.getTrainingTypeId(), processId);
	    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

}
