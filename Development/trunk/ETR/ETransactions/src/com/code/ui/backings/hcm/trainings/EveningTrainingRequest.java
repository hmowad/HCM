package com.code.ui.backings.hcm.trainings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.services.hcm.EmployeesService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "eveningTrainingRequest")
@ViewScoped
public class EveningTrainingRequest extends TrainingAndScholarshipBase {

    public EveningTrainingRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_eveningCourseJoiningRequest"));

	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.processId = WFProcessesEnum.EVENING_TRAINING_REQUEST.getCode();
		wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), (long) TrainingTypesEnum.EVENING_COURSE.getCode(), processId);
	    } else {
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		this.processId = this.instance.getProcessId();
	    }

	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
