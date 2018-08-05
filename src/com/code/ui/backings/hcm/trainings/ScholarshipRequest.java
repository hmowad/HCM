package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.enums.FlagsEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "scholarshipRequest")
@ViewScoped
public class ScholarshipRequest extends TrainingAndScholarshipBase {
    private List<QualificationLevel> qualificationLevels;

    public ScholarshipRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_scholarshipRequest"));
	    qualificationLevels = TrainingSetupService.getAllQualificationLevels();
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.processId = WFProcessesEnum.SCHOLARSHIP_REQUEST.getCode();
		wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), TrainingTypesEnum.SCHOLARSHIP.getCode(), this.processId);
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

    public void constructWFTraining() {
	try {
	    wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(wfTraining.getEmployeeId(), wfTraining.getCategoryId(), wfTraining.getTrainingTypeId(), processId);
	    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void manipulateScholarShipEndDate() {
	try {
	    if (wfTraining.getStartDateString() != null && (wfTraining.getMonthsCount() != null && wfTraining.getMonthsCount() > 0)) {
		wfTraining.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(wfTraining.getStartDateString(), wfTraining.getMonthsCount(), FlagsEnum.ALL.getCode()));
	    } else
		wfTraining.setEndDateString(null);

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