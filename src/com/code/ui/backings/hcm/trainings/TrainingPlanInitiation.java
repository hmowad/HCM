package com.code.ui.backings.hcm.trainings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingPlanData;
import com.code.enums.TrainingYearStatusEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;

@ManagedBean(name = "trainingPlanInitiation")
@ViewScoped
public class TrainingPlanInitiation extends TrainingPlanBase {

    public TrainingPlanInitiation() {
	this.processId = WFProcessesEnum.TRAINING_PLAN_INITIATION.getCode();
	super.init();
	try {
	    if (this.role == WFTaskRolesEnum.REQUESTER.getCode()) {
		trainingPlan = new WFTrainingPlanData();
		// check that Requester is under training unit
		TrainingUnitData trainingUnit;
		TrainingYear trainingYear;
		trainingUnit = TrainingSetupService.getTrainingUnitByPhysicalUnitHKey(this.loginEmpData.getPhysicalUnitHKey());
		if (trainingUnit == null) {
		    errorMessage = getMessage("label_trnTrainingUnitSpecialistPrivilege");
		} else {
		    trainingPlan.setTrainingUnitId(trainingUnit.getUnitId());
		    trainingPlan.setTrainingUnitName(trainingUnit.getName());
		    // check Exist of unApproved Training Year
		    trainingYear = TrainingSetupService.getUnApprovedTrainingYear();
		    if (trainingYear == null) {
			errorMessage = getMessage("error_noNotApprovedTrainingYear");
		    } else {
			trainingPlan.setTrainingYearId(trainingYear.getId());
			trainingPlan.setTrainingYearName(trainingYear.getName());
			hasPrivilege = true;
			// check if the requster can initiate workflow
			if (trainingYear.getStatus() == TrainingYearStatusEnum.INITIAL_DRAFT.getCode() &&
				isStartWFTraniningPlanAllowable(trainingYear.getId(), trainingUnit.getUnitId(), null, processId)) {
			    modifyAdmin = true;
			}
		    }
		}
	    } else {
		hasPrivilege = true;
		trainingPlan = TrainingCoursesEventsWorkFlow.getWFTrainingPlanDataByInstanceId(instance.getInstanceId());
		if (this.loginEmpData.getEmpId().equals(this.requester.getEmpId()) && !this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()) && !this.role.equals(WFTaskRolesEnum.HISTORY.getCode()))
		    modifyAdmin = true;
	    }
	    if (hasPrivilege) {
		searchTrainingCourseEvents();
	    }
	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
	    }

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void importAnnualCourses() {
	try {
	    coursesEvents = TrainingCoursesEventsService.generateNewCoursesEventsByAnnualCourses(trainingPlan.getTrainingYearId(), trainingPlan.getTrainingUnitId(), this.loginEmpData.getEmpId());

	    if (coursesEvents.isEmpty())
		throw new BusinessException("error_noAnnualCoursesFound");
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void printTrainingPlanInitialDraft() {
	try {
	    byte[] bytes = TrainingCoursesEventsService.getTrainingPlanInitialDraftBytes(trainingPlan.getTrainingYearId(), trainingPlan.getTrainingUnitId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }
}
