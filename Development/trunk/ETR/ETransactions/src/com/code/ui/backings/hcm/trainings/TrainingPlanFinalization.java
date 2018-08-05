package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventAllocationData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingPlanData;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TrainingYearStatusEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;

@ManagedBean(name = "trainingPlanFinalization")
@ViewScoped
public class TrainingPlanFinalization extends TrainingPlanBase {
    private boolean courseEventHasAllocations;

    public TrainingPlanFinalization() {
	this.processId = WFProcessesEnum.TRAINING_PLAN_FINALIZATION.getCode();
	super.init();
	setScreenTitle(getMessage("title_trainingPlanFinalization"));
	try {
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		trainingPlan = new WFTrainingPlanData();
		TrainingYear trainingYear = TrainingSetupService.getUnApprovedTrainingYear();
		if (trainingYear == null) {
		    errorMessage = getMessage("error_noNotApprovedTrainingYear");
		} else if (trainingYear.getStatus() == TrainingYearStatusEnum.INITIAL_DRAFT.getCode())
		    errorMessage = getMessage("error_workingOnInitialDraft");
		else {
		    trainingPlan.setTrainingYearId(trainingYear.getId());
		    trainingPlan.setTrainingYearName(trainingYear.getName());
		    Region region = CommonService.getRegionById(loginEmpData.getPhysicalRegionId());
		    trainingPlan.setRegionId(region.getId());
		    trainingPlan.setRegionName(region.getDescription());
		    hasPrivilege = true;
		}

	    } else {
		hasPrivilege = true;
		trainingPlan = TrainingCoursesEventsWorkFlow.getWFTrainingPlanDataByInstanceId(instance.getInstanceId());

		modifyAdmin = (!this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()) && !this.role.equals(WFTaskRolesEnum.HISTORY.getCode()))
			&& this.loginEmpData.getEmpId().equals(this.requester.getEmpId());

		regionOwner = (!trainingPlan.getRegionId().equals(RegionsEnum.ACADEMY.getCode()) && trainingPlan.getTrainingUnitRegionId().equals(trainingPlan.getRegionId()))
			|| (trainingPlan.getRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) && trainingPlan.getTrainingUnitRegionId().equals(RegionsEnum.ACADEMY.getCode()));

		// (loginEmpData.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) && trainingPlan.getTrainingUnitRegionId().equals(trainingPlan.getRegionId()))
		// || trainingPlan.getTrainingUnitRegionId().equals(loginEmpData.getPhysicalRegionId()) ? true : false;
		searchTrainingCourseEvents();
	    }
	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
	    }
	    if (hasPrivilege) {
		trainingUnits = TrainingSetupService.getAllTrainingUnitsData();
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void delete(TrainingCourseEventData trainingCourseEventData) throws BusinessException {
	courseEventHasAllocations = false;
	List<TrainingCourseEventAllocationData> allocations = TrainingCoursesEventsService.getTrainingCourseEventAllocations(trainingCourseEventData.getId());
	for (TrainingCourseEventAllocationData allocation : allocations)
	    if (allocation.getAllocationCount() != null || (allocation.getAllocationDesc() != null && !allocation.getAllocationDesc().isEmpty())) {
		courseEventHasAllocations = true;
		break;
	    }
    }

    public void selectTrainingUnit() {
	try {
	    if (trainingPlan.getTrainingUnitId() == null || trainingPlan.getTrainingUnitId() == -1) {
		modifyAdmin = false;
		coursesEvents = null;
		selectedTrainingCourseEvent = null;
		return;
	    }

	    List<WFTrainingPlanData> oldTrainingPlans = TrainingCoursesEventsWorkFlow.getWFTrainingPlanData(FlagsEnum.ALL.getCode(), trainingPlan.getTrainingYearId(), trainingPlan.getTrainingUnitId(), trainingPlan.getRegionId(), processId, FlagsEnum.ALL.getCode());
	    if (oldTrainingPlans.size() != 0)
		trainingPlan = oldTrainingPlans.get(oldTrainingPlans.size() - 1);

	    modifyAdmin = (!this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()) && !this.role.equals(WFTaskRolesEnum.HISTORY.getCode()))
		    && isStartWFTraniningPlanAllowable(trainingPlan.getTrainingYearId(), trainingPlan.getTrainingUnitId(), trainingPlan.getRegionId(), processId);

	    if (modifyAdmin) {
		WFTrainingPlanData temp = trainingPlan;
		trainingPlan = new WFTrainingPlanData();
		trainingPlan.setTrainingYearId(temp.getTrainingYearId());
		trainingPlan.setTrainingYearName(temp.getTrainingYearName());
		trainingPlan.setTrainingUnitId(temp.getTrainingUnitId());
		trainingPlan.setTrainingUnitName(temp.getTrainingUnitName());
		trainingPlan.setRegionId(temp.getRegionId());
		trainingPlan.setRegionName(temp.getRegionName());
		if (oldTrainingPlans.size() != 0) // old Workflow
		    trainingPlan.setRemarks(temp.getRemarks());
	    }

	    trainingPlan.setTrainingUnitRegionId(getTrainingUnit(trainingPlan.getTrainingUnitId()).getRegionId());
	    regionOwner = (!trainingPlan.getRegionId().equals(RegionsEnum.ACADEMY.getCode()) && trainingPlan.getTrainingUnitRegionId().equals(trainingPlan.getRegionId()))
		    || (trainingPlan.getRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) && trainingPlan.getTrainingUnitRegionId().equals(RegionsEnum.ACADEMY.getCode()));

	    // (loginEmpData.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
	    // && trainingPlan.getTrainingUnitRegionId().equals(trainingPlan.getRegionId()))
	    // || trainingPlan.getTrainingUnitRegionId().equals(loginEmpData.getPhysicalRegionId());

	    super.searchTrainingCourseEvents();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private TrainingUnitData getTrainingUnit(long id) {
	for (TrainingUnitData unit : trainingUnits)
	    if (unit.getUnitId().equals(id))
		return unit;
	return null;
    }

    public void printTrainingPlanFinalDraft() {
	try {
	    byte[] bytes = TrainingCoursesEventsService.getTrainingPlanFinalDraftBytes(trainingPlan.getTrainingYearId(), trainingPlan.getTrainingUnitId(), trainingPlan.getRegionId());
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
