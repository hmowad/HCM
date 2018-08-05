package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "studyEnrollmentClaimRequest")
@ViewScoped
public class StudyEnrollmentClaimRequest extends TrainingAndScholarshipBase {
    private List<QualificationLevel> qualificationLevels;

    public StudyEnrollmentClaimRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_studyEnrollmentClaimRequest"));
	    qualificationLevels = TrainingSetupService.getAllQualificationLevels();
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.TRN_STUDY_CLAIM_REQUEST.getCode(), MenuActionsEnum.TRN_CLAIMS_REQUEST.getCode()))
		    this.processId = WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode();
		else
		    this.processId = WFProcessesEnum.STUDY_ENROLLMENT_CLAIM_REQUEST.getCode();

		wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), TrainingTypesEnum.STUDY_ENROLLMENT.getCode(), this.processId);
	    } else {
		// getwfTraining by instanceId
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		this.processId = this.instance.getProcessId();
	    }

	    if (requester.getEmpId().equals(wfTraining.getEmployeeId()))
		beneficiary = requester;
	    else
		beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());

	    if (this.processId == WFProcessesEnum.STUDY_ENROLLMENT_CLAIM.getCode())
		isDecisionRequest = true;

	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void constructWFTraining() {
	try {
	    wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(wfTraining.getEmployeeId(), wfTraining.getCategoryId(), wfTraining.getTrainingTypeId(), processId);
	    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
    }
}