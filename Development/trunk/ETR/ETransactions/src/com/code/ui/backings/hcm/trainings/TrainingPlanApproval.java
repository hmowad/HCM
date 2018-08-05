package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventAllocationData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingPlanData;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportOutputFormatsEnum;
import com.code.enums.TrainingYearStatusEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;

@ManagedBean(name = "trainingPlanApproval")
@ViewScoped
public class TrainingPlanApproval extends TrainingPlanBase {

    private boolean courseEventHasAllocations;

    public TrainingPlanApproval() throws BusinessException {
	setScreenTitle(getMessage("title_trainingPlanApproval"));
	this.processId = WFProcessesEnum.TRAINING_PLAN_APPROVAL.getCode();
	trainingUnits = TrainingSetupService.getAllTrainingUnitsData();
	TrainingYear trainingYear = TrainingSetupService.getUnApprovedTrainingYear();
	super.init();

	try {
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		trainingPlan = new WFTrainingPlanData();
		trainingPlan.setTrainingUnitId(null);
		// check Exist of unApproved Training Year
		trainingYear = TrainingSetupService.getUnApprovedTrainingYear();
		if (trainingYear == null)
		    errorMessage = getMessage("error_noNotApprovedTrainingYear");
		else if (trainingYear.getStatus() == TrainingYearStatusEnum.INITIAL_DRAFT.getCode())
		    errorMessage = getMessage("error_workingOnInitialDraft");
		else if (trainingYear.getStatus() == TrainingYearStatusEnum.FINAL_DRAFT.getCode())
		    errorMessage = getMessage("error_workingOnFinalDraft");
		else {
		    trainingPlan.setTrainingYearId(trainingYear.getId());
		    trainingPlan.setTrainingYearName(trainingYear.getName());
		    hasPrivilege = true;
		    selectDefaultTrainingUnit();
		    if (trainingYear.getStatus() == TrainingYearStatusEnum.TRAINING_PLAN_PENDING_APPROVE.getCode() &&
			    isStartWFTraniningPlanAllowable(trainingYear.getId(), null, null, processId)) {
			modifyAdmin = true;
		    }
		}

	    } else {
		trainingPlan = TrainingCoursesEventsWorkFlow.getWFTrainingPlanDataByInstanceId(instance.getInstanceId());
		hasPrivilege = true;
		selectDefaultTrainingUnit();
		if (this.loginEmpData.getEmpId().equals(this.requester.getEmpId()) && !this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()) && !this.role.equals(WFTaskRolesEnum.HISTORY.getCode())) {
		    modifyAdmin = true;
		}
	    }
	    if (hasPrivilege && !this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()) && !this.role.equals(WFTaskRolesEnum.HISTORY.getCode()))
		calculateErrors();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void selectDefaultTrainingUnit() throws BusinessException {
	// We are assuming here that academy region has only one training unit, if a training unit is added please review
	selectedTrainingUnitId = TrainingSetupService.getTrainingUnitsByRegionId(RegionsEnum.ACADEMY.getCode()).get(0).getUnitId();
	selectTrainingUnit();
    }

    public void selectTrainingUnit() {
	searchCourseName = null;
	searchTrainingCourseEvents();
    }

    public void delete(TrainingCourseEventData trainingCourseEventData) throws BusinessException {
	courseEventHasAllocations = false;
	List<TrainingCourseEventAllocationData> allocations = TrainingCoursesEventsService.getTrainingCourseEventAllocations(trainingCourseEventData.getId());
	for (TrainingCourseEventAllocationData allocation : allocations)
	    if (allocation.getAllocationCount() != null || (allocation.getAllocationDesc() != null && !allocation.getAllocationDesc().isEmpty()))
		courseEventHasAllocations = true;
    }

    public void printTrainingPlanDecisionApprovalBytes() {
	try {
	    byte[] bytes = TrainingCoursesEventsService.getTrainingPlanDecisionApprovalBytes(trainingPlan.getTrainingYearId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printTrainingPlanDecisionDetailsBytes() {
	try {
	    byte[] bytes = TrainingCoursesEventsService.getTrainingPlanDecisionDetailsBytes(trainingPlan.getTrainingYearId(), ReportOutputFormatsEnum.PDF.getCode());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public boolean isCourseEventHasAllocations() {
	return courseEventHasAllocations;
    }

    public void setCourseEventHasAllocations(boolean courseEventHasAllocations) {
	this.courseEventHasAllocations = courseEventHasAllocations;
    }

}
