package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "studyEnrollmentRequest")
@ViewScoped
public class StudyEnrollmentRequest extends TrainingAndScholarshipBase {
    private List<QualificationLevel> qualificationLevels;

    public StudyEnrollmentRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_studyEnrollmentRequest"));
	    qualificationLevels = TrainingSetupService.getAllQualificationLevels();
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.processId = WFProcessesEnum.STUDY_ENROLLMENT_REQUEST.getCode();
		wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), TrainingTypesEnum.STUDY_ENROLLMENT.getCode(), this.processId);
	    } else {
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		this.processId = this.instance.getProcessId();
		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}
	    }

	} catch (BusinessException e) {
	    e.printStackTrace();
	}

    }

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
    }

}
