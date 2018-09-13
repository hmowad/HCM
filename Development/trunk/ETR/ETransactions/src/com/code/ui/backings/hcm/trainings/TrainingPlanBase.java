package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventAllocationData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventAllocationEmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingPlanData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

public abstract class TrainingPlanBase extends WFBaseBacking {

    protected int pageSize = 10;

    protected boolean hasPrivilege; // only view screen data
    protected boolean modifyAdmin; // Final : adds his needs on course; Approval : Full edit privilge
    protected List<TrainingCourseEventData> coursesEvents;
    protected TrainingCourseEventData selectedTrainingCourseEvent;
    protected Integer newCourseId;
    protected String searchCourseName;
    protected List<TrainingCourseEventAllocationData> externalPartiesAllocations;
    protected List<TrainingCourseEventAllocationData> regionsAllocations;
    protected TrainingCourseEventAllocationData currentRegionsAllocation;
    protected String errorMessage;
    protected Long selectedReviewerEmpId;
    protected List<EmployeeData> reviewerEmps;
    protected WFTrainingPlanData trainingPlan;
    protected Long selectedTrainingUnitId;// Used only for Approval
    protected boolean regionOwner; // Only in Final : add and delete courses, add and delete external parties
    protected List<TrainingUnitData> trainingUnits;

    protected long newExternalPartyId;
    protected String newEmpAllocationId;
    protected String allocationsErrors;

    public void calculateErrors() {
	try {
	    allocationsErrors = TrainingCoursesEventsService.calculateTrainingPlanApprovalErrors(trainingPlan.getTrainingYearId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void addCourseEvent() {
	try {
	    if (this.processId == WFProcessesEnum.TRAINING_PLAN_INITIATION.getCode())
		selectedTrainingCourseEvent = TrainingCoursesEventsService.generateNewCourseEvent(newCourseId, trainingPlan.getTrainingYearId(), trainingPlan.getTrainingUnitId(), null, TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), false, this.loginEmpData.getEmpId());
	    else {
		selectedTrainingCourseEvent = TrainingCoursesEventsService.generateNewCourseEvent(newCourseId, trainingPlan.getTrainingYearId(), trainingPlan.getTrainingUnitId() == null ? selectedTrainingUnitId : trainingPlan.getTrainingUnitId(), null, TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), true, this.loginEmpData.getEmpId());
		selectCourse(selectedTrainingCourseEvent);
	    }
	    coursesEvents.add(selectedTrainingCourseEvent);
	    if (processId == WFProcessesEnum.TRAINING_PLAN_APPROVAL.getCode())
		calculateErrors();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void manipulateEndDate() {
	try {
	    if (selectedTrainingCourseEvent.getPlannedStartDateString() != null && (selectedTrainingCourseEvent.getWeeksCount() != null && selectedTrainingCourseEvent.getWeeksCount() > 0))
		selectedTrainingCourseEvent.setPlannedEndDateString(TrainingCoursesEventsService.manipulateDateNeglectWeekend(HijriDateService.addSubStringHijriDays(selectedTrainingCourseEvent.getPlannedStartDateString(), (selectedTrainingCourseEvent.getWeeksCount() * 7) - 1)));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    protected boolean isWFInstanceFinishedWithReject(long instanceId) throws BusinessException {
	List<WFTaskData> tasks = BaseWorkFlow.getWFInstanceTasksData(instanceId);
	for (int i = tasks.size() - 1; i >= 0; i--) {
	    if (tasks.get(i).getAction() != null && !tasks.get(i).getAction().equals(WFTaskActionsEnum.NOTIFIED.getCode())) {
		return tasks.get(i).getAction().equals(WFTaskActionsEnum.REJECT.getCode());
	    }
	}
	return false;
    }

    public void deleteCourseEvent(TrainingCourseEventData courseEventData) {
	try {
	    TrainingCoursesEventsService.deleteTrainingCourseEvent(courseEventData, this.loginEmpData.getEmpId());
	    coursesEvents.remove(courseEventData);
	    if (selectedTrainingCourseEvent == courseEventData)
		selectedTrainingCourseEvent = null;
	    if (processId == WFProcessesEnum.TRAINING_PLAN_APPROVAL.getCode())
		calculateErrors();
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void selectCourse(TrainingCourseEventData trainingCourseEventData) {
	try {
	    if (!TrainingCoursesEventsService.checkExistingCourseEventById(trainingCourseEventData.getId())) {
		setServerSideErrorMessages(getMessage("error_trainingPlanModifiedMustReload"));
		return;
	    }

	    selectedTrainingCourseEvent = trainingCourseEventData;
	    if (this.processId != WFProcessesEnum.TRAINING_PLAN_INITIATION.getCode())
		getCourseEventsAllocations();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    private void getCourseEventsAllocations() throws BusinessException {
	List<TrainingCourseEventAllocationData> mixed = TrainingCoursesEventsService.getTrainingCourseEventAllocations(selectedTrainingCourseEvent.getId());
	regionsAllocations = new ArrayList<TrainingCourseEventAllocationData>();
	externalPartiesAllocations = new ArrayList<TrainingCourseEventAllocationData>();
	currentRegionsAllocation = new TrainingCourseEventAllocationData();
	for (TrainingCourseEventAllocationData allocation : mixed) {
	    if (allocation.getRegionId() != null) {
		if (processId == WFProcessesEnum.TRAINING_PLAN_FINALIZATION.getCode() && trainingPlan.getRegionId().equals(allocation.getRegionId())) {
		    allocation.setAllocationEmployees(TrainingCoursesEventsService.getTrainingCoursesEventsAllocationEmps(allocation.getId()));
		    regionsAllocations.add(0, allocation);
		    currentRegionsAllocation = allocation;
		} else if (processId == WFProcessesEnum.TRAINING_PLAN_APPROVAL.getCode()) {
		    allocation.setAllocationEmployees(TrainingCoursesEventsService.getTrainingCoursesEventsAllocationEmps(allocation.getId()));
		    regionsAllocations.add(allocation);
		}
	    } else {
		externalPartiesAllocations.add(allocation);
	    }
	}
    }

    public void saveCourseEvent() {
	try {
	    selectedTrainingCourseEvent.setActualStartDate(selectedTrainingCourseEvent.getPlannedStartDate());
	    selectedTrainingCourseEvent.setActualEndDate(selectedTrainingCourseEvent.getPlannedEndDate());

	    selectedTrainingCourseEvent.getTrainingCourseEvent().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    if (regionsAllocations != null)
		for (TrainingCourseEventAllocationData trainingCourseEventAllocation : regionsAllocations)
		    trainingCourseEventAllocation.getTrainingCourseEventAllocation().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.

	    if (externalPartiesAllocations != null)
		for (TrainingCourseEventAllocationData trainingCourseEventAllocation : externalPartiesAllocations)
		    trainingCourseEventAllocation.getTrainingCourseEventAllocation().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.

	    if (selectedTrainingCourseEvent.getId() == null)
		TrainingCoursesEventsService.addTrainingCourseEventForTrainingPlan(selectedTrainingCourseEvent, coursesEvents, regionsAllocations, externalPartiesAllocations, loginEmpData.getEmpId());
	    else
		TrainingCoursesEventsService.updateTrainingCourseEventForTrainingPlan(selectedTrainingCourseEvent, coursesEvents, regionsAllocations, externalPartiesAllocations, loginEmpData.getEmpId());
	    if (processId == WFProcessesEnum.TRAINING_PLAN_APPROVAL.getCode())
		calculateErrors();
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    // for plan approval
    public void selectTrainingCourseAllocation(TrainingCourseEventAllocationData region) {
	currentRegionsAllocation = region;
    }

    public void searchTrainingCourseEvents() {
	try {
	    coursesEvents = TrainingCoursesEventsService.getTrainingCoursesEvents(FlagsEnum.ALL.getCode(), trainingPlan.getTrainingYearId(), trainingPlan.getTrainingUnitId() == null ? selectedTrainingUnitId : trainingPlan.getTrainingUnitId(), FlagsEnum.ALL.getCode(), null, searchCourseName, FlagsEnum.ALL.getCode(), null, null, null, FlagsEnum.ALL.getCode(), null);
	    selectedTrainingCourseEvent = null;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    protected boolean isStartWFTraniningPlanAllowable(long trainingYearId, Long trainingUnitId, Long regionId, long processId) throws BusinessException {
	if (TrainingCoursesEventsWorkFlow.checkExistingTrainingPlanUnderApproval(trainingYearId, trainingUnitId != null ? trainingUnitId : FlagsEnum.ALL.getCode(), regionId != null ? regionId : FlagsEnum.ALL.getCode(), processId))
	    return false;
	if (TrainingCoursesEventsWorkFlow.checkExistingApprovedTrainingPlan(trainingYearId, trainingUnitId != null ? trainingUnitId : FlagsEnum.ALL.getCode(), regionId != null ? regionId : FlagsEnum.ALL.getCode(), processId))
	    return false;

	return true;
    }

    public void addExternalPartyAllocation() {
	try {
	    externalPartiesAllocations.add(TrainingCoursesEventsService.constructExternalPartyCourseAllocation(newExternalPartyId, selectedTrainingCourseEvent.getId()));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteExternalPartyAllocation(TrainingCourseEventAllocationData externalPartyAllocation) {
	externalPartiesAllocations.remove(externalPartyAllocation);
    }

    public void addEmpAllocation() {
	try {

	    String[] empAllocationIds = newEmpAllocationId.split(",");
	    for (String empAllocationId : empAllocationIds) {
		boolean check = false;
		EmployeeData emp = EmployeesService.getEmployeeData(Long.parseLong(empAllocationId));
		// only officers and soldiers are chosen
		if (!(emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode())) {
		    this.setServerSideErrorMessages(getParameterizedMessage("error_militaryTrainingNotAllowedForCivilians", new Object[] { emp.getName() }));
		    continue;
		}
		TrainingCourseEventAllocationEmployeeData allocationEmp = TrainingCoursesEventsService.constructTrainingCourseEventAllocationsEmp(currentRegionsAllocation.getId(), emp);
		// preventing adding the same employee two times
		for (TrainingCourseEventAllocationEmployeeData allocationEmployee : currentRegionsAllocation.getAllocationEmployees()) {
		    if (allocationEmp.getEmpId().equals(allocationEmployee.getEmpId())) {
			check = true;
			setServerSideErrorMessages(getMessage("label_existingEmployee"));
			break;
		    }
		}
		if (!check) {
		    currentRegionsAllocation.getAllocationEmployees().add(allocationEmp);
		}
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteEmpAllocation(TrainingCourseEventAllocationEmployeeData allocationEmp) {
	currentRegionsAllocation.getAllocationEmployees().remove(allocationEmp);
    }

    public String initWFTrainingPlan() {
	try {
	    TrainingCoursesEventsWorkFlow.initWFTrainingPlan(requester, trainingPlan, processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectWFTrainingPlanMR() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingPlanRE() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanRE(requester, instance, trainingPlan, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingPlanRE() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanRE(requester, instance, trainingPlan, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingPlanSRE() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanSRE(requester, trainingPlan, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingPlanSRE() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanSRE(requester, trainingPlan, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingPlanSM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanSM(requester, instance, currentTask, trainingPlan, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingPlanSM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanSM(requester, instance, currentTask, trainingPlan, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingPlanSM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanSM(requester, instance, currentTask, trainingPlan, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingPlanSSM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanSSM(requester, instance, trainingPlan, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingPlanSSM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanSSM(requester, instance, trainingPlan, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingPlanSSM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanSSM(requester, instance, trainingPlan, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    TrainingCoursesEventsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}

    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public TrainingCourseEventData getSelectedTrainingCourseEvent() {
	return selectedTrainingCourseEvent;
    }

    public void setSelectedTrainingCourseEvent(TrainingCourseEventData selectedTrainingCourseEvent) {
	this.selectedTrainingCourseEvent = selectedTrainingCourseEvent;
    }

    public Long getSelectedReviewerEmpId() {
	return selectedReviewerEmpId;
    }

    public void setSelectedReviewerEmpId(Long selectedReviewerEmpId) {
	this.selectedReviewerEmpId = selectedReviewerEmpId;
    }

    public List<EmployeeData> getReviewerEmps() {
	return reviewerEmps;
    }

    public void setReviewerEmps(List<EmployeeData> reviewerEmps) {
	this.reviewerEmps = reviewerEmps;
    }

    public List<TrainingCourseEventData> getCoursesEvents() {
	return coursesEvents;
    }

    public void setCoursesEvents(List<TrainingCourseEventData> coursesEvents) {
	this.coursesEvents = coursesEvents;
    }

    public Integer getNewCourseId() {
	return newCourseId;
    }

    public void setNewCourseId(Integer newCourseId) {
	this.newCourseId = newCourseId;
    }

    public String getSearchCourseName() {
	return searchCourseName;
    }

    public void setSearchCourseName(String searchCourseName) {
	this.searchCourseName = searchCourseName;
    }

    public List<TrainingCourseEventAllocationData> getExternalPartiesAllocations() {
	return externalPartiesAllocations;
    }

    public void setExternalPartiesAllocations(List<TrainingCourseEventAllocationData> externalPartiesAllocations) {
	this.externalPartiesAllocations = externalPartiesAllocations;
    }

    public List<TrainingCourseEventAllocationData> getRegionsAllocations() {
	return regionsAllocations;
    }

    public void setRegionsAllocations(List<TrainingCourseEventAllocationData> regionsAllocations) {
	this.regionsAllocations = regionsAllocations;
    }

    public TrainingCourseEventAllocationData getCurrentRegionsAllocation() {
	return currentRegionsAllocation;
    }

    public void setCurrentRegionsAllocation(TrainingCourseEventAllocationData currentRegionsAllocation) {
	this.currentRegionsAllocation = currentRegionsAllocation;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    public WFTrainingPlanData getTrainingPlan() {
	return trainingPlan;
    }

    public void setTrainingPlan(WFTrainingPlanData trainingPlan) {
	this.trainingPlan = trainingPlan;
    }

    public boolean isHasPrivilege() {
	return hasPrivilege;
    }

    public void setHasPrivilege(boolean hasPrivilege) {
	this.hasPrivilege = hasPrivilege;
    }

    public boolean isModifyAdmin() {
	return modifyAdmin;
    }

    public void setModifyAdmin(boolean modifyAdmin) {
	this.modifyAdmin = modifyAdmin;
    }

    public boolean isRegionOwner() {
	return regionOwner;
    }

    public void setRegionOwner(boolean regionOwner) {
	this.regionOwner = regionOwner;
    }

    public List<TrainingUnitData> getTrainingUnits() {
	return trainingUnits;
    }

    public void setTrainingUnits(List<TrainingUnitData> trainingUnits) {
	this.trainingUnits = trainingUnits;
    }

    public Long getSelectedTrainingUnitId() {
	return selectedTrainingUnitId;
    }

    public void setSelectedTrainingUnitId(Long selectedTrainingUnitId) {
	this.selectedTrainingUnitId = selectedTrainingUnitId;
    }

    public long getNewExternalPartyId() {
	return newExternalPartyId;
    }

    public void setNewExternalPartyId(long newExternalPartyId) {
	this.newExternalPartyId = newExternalPartyId;
    }

    public String getNewEmpAllocationId() {
	return newEmpAllocationId;
    }

    public void setNewEmpAllocationId(String newEmpAllocationId) {
	this.newEmpAllocationId = newEmpAllocationId;
    }

    public String getAllocationsErrors() {
	return allocationsErrors;
    }

    public void setAllocationsErrors(String allocationsErrors) {
	this.allocationsErrors = allocationsErrors;
    }

}
