package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rankings;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
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

@ManagedBean(name = "militaryTrainingClaimRequest")
@ViewScoped
public class MilitaryTrainingClaimRequest extends TrainingAndScholarshipBase {

    private long selectedEmployeeId;
    private long selectedEmpCategoryId;
    private List<Rankings> rankings;

    public MilitaryTrainingClaimRequest() {
	try {
	    super.init();
	    wfTraining = new WFTrainingData();
	    rankings = TrainingSetupService.getRankings(FlagsEnum.ALL.getCode());

	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 0:
		    setScreenTitle(getMessage("title_internalMilitaryTrainingClaimRequest"));

		    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {

			this.processId = WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode();

			if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_INTERNAL_MILITARY_CLAIM_REQUEST.getCode(), MenuActionsEnum.TRN_CLAIMS_REQUEST.getCode()))
			    this.processId = WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode();

			wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), processId);
		    } else {
			wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
			this.processId = this.instance.getProcessId();
		    }
		    break;
		case 1:
		    setScreenTitle(getMessage("title_externalMilitaryTrainingClaimRequest"));

		    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {

			this.processId = WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM_REQUEST.getCode();

			if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_EXTERNAL_MILITARY_CLAIM_REQUEST.getCode(), MenuActionsEnum.TRN_CLAIMS_REQUEST.getCode()))
			    this.processId = WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode();

			wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode(), processId);
		    } else {
			wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
			this.processId = this.instance.getProcessId();
		    }
		    break;

		default:

		}

		if (this.processId == WFProcessesEnum.INTERNAL_MILITARY_TRAINING_CLAIM.getCode() || this.processId == WFProcessesEnum.EXTERNAL_MILITARY_TRAINING_CLAIM.getCode())
		    isDecisionRequest = true;

		if (requester.getEmpId().equals(wfTraining.getEmployeeId()))
		    beneficiary = requester;
		else
		    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());

		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}

		trainingUnitsList = TrainingSetupService.getAllTrainingUnitsData();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void constructWFTraining() {
	try {
	    if (selectedEmpCategoryId != CategoriesEnum.OFFICERS.getCode() && selectedEmpCategoryId != CategoriesEnum.SOLDIERS.getCode()) {
		throw new BusinessException("error_employeeQualificationLevelNotValidForTrnCourse", new Object[] { "" });
	    }

	    wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(selectedEmployeeId, selectedEmpCategoryId, wfTraining.getTrainingTypeId(), processId);
	    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public String calculateRankingDesc() {
	if (wfTraining.getSuccessRanking() == null || wfTraining.getSuccessRanking().equals(0) || wfTraining.getSuccessRanking() > rankings.size())
	    return "";
	String successRankDesc = rankings.get(wfTraining.getSuccessRanking() - 1).getDescription();
	wfTraining.setSuccessRankingDesc(successRankDesc);
	return successRankDesc;
    }

    public long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }

    public long getSelectedEmpCategoryId() {
	return selectedEmpCategoryId;
    }

    public void setSelectedEmpCategoryId(long selectedEmpCategoryId) {
	this.selectedEmpCategoryId = selectedEmpCategoryId;
    }
}