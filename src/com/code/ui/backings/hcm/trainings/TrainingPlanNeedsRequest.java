package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingPlanNeedData;
import com.code.enums.NavigationEnum;
import com.code.enums.TrainingYearStatusEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;

@ManagedBean(name = "trainingPlanNeedsRequest")
@ViewScoped
public class TrainingPlanNeedsRequest extends TrainingPlanBase {

    private List<WFTrainingPlanNeedData> trainingCoursesEventsNeedsList;
    private TrainingYear trainingYear;
    private long requestingUnitId;
    private long selectedCourseEventId;
    private String remarks;

    public TrainingPlanNeedsRequest() throws BusinessException {
	super.init();
	setScreenTitle(getMessage("title_trainingPlanNeedsRequest"));
	setNotificationMessage(getMessage("notify_requestSubmittedToStudy"));
	this.processId = WFProcessesEnum.TRAINING_PLAN_NEEDS_REQUEST.getCode();
	setTrainingYear(TrainingSetupService.getUnApprovedTrainingYear());
	requestingUnitId = loginEmpData.getPhysicalUnitId();
	try {
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		trainingCoursesEventsNeedsList = new ArrayList<>();
		if (trainingYear == null)
		    errorMessage = getMessage("error_noNotApprovedTrainingYear");
		else if (trainingYear.getStatus() == TrainingYearStatusEnum.INITIAL_DRAFT.getCode())
		    errorMessage = getMessage("error_workingOnInitialDraft");
		else if (trainingYear.getNeedsStartDateString() == null || trainingYear.getNeedsStartDateString().isEmpty() || trainingYear.getNeedsEndDateString() == null || trainingYear.getNeedsEndDateString().isEmpty())
		    errorMessage = getMessage("error_notAssignedNeedsStartAndEndDates");
		else if (!HijriDateService.isDateBetween(trainingYear.getNeedsStartDate(), trainingYear.getNeedsEndDate(), HijriDateService.getHijriSysDate()))
		    errorMessage = getParameterizedMessage("error_requestDayNotBetweenTrainingPlanNeedsDates", new Object[] { trainingYear.getNeedsStartDateString(), trainingYear.getNeedsEndDateString() });
		else {
		    hasPrivilege = true;
		}
	    } else {
		trainingCoursesEventsNeedsList = TrainingCoursesEventsWorkFlow.getWFTrainingPlanNeedDataByInstanceId(instance.getInstanceId());
		remarks = trainingCoursesEventsNeedsList.get(0).getRemarks();
		hasPrivilege = true;
		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}
	    }

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void addCourseNeed() throws BusinessException {
	try {
	    WFTrainingPlanNeedData data = new WFTrainingPlanNeedData();
	    TrainingCourseEventData courseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
	    data.setCourseEventId(courseEvent.getId());
	    data.setRequestingUnitId(requestingUnitId);
	    data.setCourseId(courseEvent.getCourseId());
	    data.setCourseName(courseEvent.getCourseName());
	    data.setTrainingYearId(courseEvent.getTrainingYearId());
	    data.setTrainingUnitId(courseEvent.getTrainingUnitId());
	    data.setTrainingUnitName(courseEvent.getTrainingUnitName());
	    data.setPlannedStartDate(courseEvent.getPlannedStartDate());
	    data.setPlannedEndDate(courseEvent.getPlannedEndDate());
	    data.setPrerquisites(courseEvent.getPrerquisites());
	    data.setRemarks(remarks);
	    if (checkExistingCourseEventNeed(data))
		throw new BusinessException("error_cannotAddCourseEventNeed");
	    trainingCoursesEventsNeedsList.add(data);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void deleteCourseNeed(WFTrainingPlanNeedData need) {
	trainingCoursesEventsNeedsList.remove(need);
	setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    public boolean checkExistingCourseEventNeed(WFTrainingPlanNeedData needData) {
	for (WFTrainingPlanNeedData planNeedData : trainingCoursesEventsNeedsList)
	    if (planNeedData.getCourseEventId().equals(needData.getCourseEventId()))
		return true;
	return false;
    }

    public String initWFTrainingPlanNeeds() {
	try {
	    for (WFTrainingPlanNeedData wfTrainingPlanNeedData : trainingCoursesEventsNeedsList)
		wfTrainingPlanNeedData.setRemarks(remarks);
	    TrainingCoursesEventsWorkFlow.initWFTrainingPlanNeeds(requester, trainingCoursesEventsNeedsList, trainingYear, requestingUnitId, processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingPlanNeedsDM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanNeedsDM(requester, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingPlanNeedsDM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanNeedsDM(requester, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectWFTrainingPlanNeedsMR() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanNeedsMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingPlanNeedsRE() {
	try {

	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanNeedsRE(requester, instance, trainingCoursesEventsNeedsList, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingPlanNeedsRE() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanNeedsRE(requester, instance, trainingCoursesEventsNeedsList, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingPlanNeedsSM() {
	try {
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanNeedsSM(requester, instance, trainingCoursesEventsNeedsList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingPlanNeedsSM() {
	try {

	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanNeedsSM(requester, instance, trainingCoursesEventsNeedsList, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingPlanNeedsSM() {
	try {
	    // wftrainingList is not used in case of rejection
	    TrainingCoursesEventsWorkFlow.doWFTrainingPlanNeedsSM(requester, instance, trainingCoursesEventsNeedsList, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public List<WFTrainingPlanNeedData> getTrainingCoursesEventsNeedsList() {
	return trainingCoursesEventsNeedsList;
    }

    public void setTrainingCoursesEventsNeedsList(List<WFTrainingPlanNeedData> trainingCoursesEventsNeedsList) {
	this.trainingCoursesEventsNeedsList = trainingCoursesEventsNeedsList;
    }

    public TrainingYear getTrainingYear() {
	return trainingYear;
    }

    public void setTrainingYear(TrainingYear trainingYear) {
	this.trainingYear = trainingYear;
    }

    public long getSelectedCourseEventId() {
	return selectedCourseEventId;
    }

    public void setSelectedCourseEventId(long selectedCourseEventId) {
	this.selectedCourseEventId = selectedCourseEventId;
    }

    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

}
