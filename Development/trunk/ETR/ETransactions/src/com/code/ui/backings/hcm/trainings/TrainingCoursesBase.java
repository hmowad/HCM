package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseEventData;
import com.code.enums.NavigationEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

public abstract class TrainingCoursesBase extends WFBaseBacking {

    protected int pageSize = 10;
    protected WFTrainingCourseEventData wfTrainingCourseEvent;
    protected List<WFTrainingCourseEventData> wfTrainingCourseEventsList;
    protected List<WFTrainingCourseEventData> wfTrainingCourseEventsForExternalEmpsList;
    protected Long selectedReviewerEmpId;
    protected List<EmployeeData> reviewerEmps;
    protected TrainingCourseEventData selectedCourseEvent;
    protected int selectedTrainingUnitId;
    protected int selectedTrainingYearId;
    protected Long selectedCourseEventId;
    protected List<EmployeeData> internalCopies;
    protected String externalCopies;
    protected boolean hasAccessPrivillege;

    public void init() {
	try {
	    super.init();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    // initiate work flow
    public String initWFTrainingCourseEvent() {
	try {
	    if (wfTrainingCourseEvent != null) {
		wfTrainingCourseEventsList = new ArrayList<WFTrainingCourseEventData>();
		wfTrainingCourseEventsList.add(wfTrainingCourseEvent);
	    }

	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.addAll(wfTrainingCourseEventsForExternalEmpsList);

	    TrainingCoursesEventsWorkFlow.initWFTrainingCourseEvents(requester, wfTrainingCourseEventsList, selectedCourseEvent, processId, taskUrl, internalCopies, externalCopies, attachments);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.removeAll(wfTrainingCourseEventsForExternalEmpsList);
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve reviewer
    public String approveWFTrainingCourseEventRE() {
	try {
	    if (wfTrainingCourseEvent != null) {
		wfTrainingCourseEventsList = new ArrayList<WFTrainingCourseEventData>();
		wfTrainingCourseEventsList.add(wfTrainingCourseEvent);
	    }

	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.addAll(wfTrainingCourseEventsForExternalEmpsList);

	    TrainingCoursesEventsWorkFlow.doWFTrainingCourseEventsRE(requester, instance, wfTrainingCourseEventsList, selectedCourseEvent, internalCopies, externalCopies, attachments, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.removeAll(wfTrainingCourseEventsForExternalEmpsList);
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject reviewer
    public String rejectWFTrainingCourseEventRE() {
	try {
	    if (wfTrainingCourseEvent != null) {
		wfTrainingCourseEventsList = new ArrayList<WFTrainingCourseEventData>();
		wfTrainingCourseEventsList.add(wfTrainingCourseEvent);
	    }

	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.addAll(wfTrainingCourseEventsForExternalEmpsList);

	    TrainingCoursesEventsWorkFlow.doWFTrainingCourseEventsRE(requester, instance, wfTrainingCourseEventsList, selectedCourseEvent, internalCopies, externalCopies, attachments, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.removeAll(wfTrainingCourseEventsForExternalEmpsList);
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // approve sign manager
    public String approveWFTrainingCourseEventSM() {
	try {
	    if (wfTrainingCourseEvent != null) {
		wfTrainingCourseEventsList = new ArrayList<WFTrainingCourseEventData>();
		wfTrainingCourseEventsList.add(wfTrainingCourseEvent);
	    }

	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.addAll(wfTrainingCourseEventsForExternalEmpsList);

	    TrainingCoursesEventsWorkFlow.doWFTrainingCourseEventsSM(requester, instance, wfTrainingCourseEventsList, selectedCourseEvent, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.removeAll(wfTrainingCourseEventsForExternalEmpsList);
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // return sign manager
    public String returnWFTrainingCourseEventSM() {
	try {
	    if (wfTrainingCourseEvent != null) {
		wfTrainingCourseEventsList = new ArrayList<WFTrainingCourseEventData>();
		wfTrainingCourseEventsList.add(wfTrainingCourseEvent);
	    }

	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.addAll(wfTrainingCourseEventsForExternalEmpsList);

	    TrainingCoursesEventsWorkFlow.doWFTrainingCourseEventsSM(requester, instance, wfTrainingCourseEventsList, selectedCourseEvent, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.removeAll(wfTrainingCourseEventsForExternalEmpsList);
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject sign manager
    public String rejectWFTrainingCourseEventSM() {
	try {
	    if (wfTrainingCourseEvent != null) {
		wfTrainingCourseEventsList = new ArrayList<WFTrainingCourseEventData>();
		wfTrainingCourseEventsList.add(wfTrainingCourseEvent);
	    }

	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.addAll(wfTrainingCourseEventsForExternalEmpsList);

	    TrainingCoursesEventsWorkFlow.doWFTrainingCourseEventsSM(requester, instance, wfTrainingCourseEventsList, selectedCourseEvent, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    if (processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode() || processId == WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode())
		wfTrainingCourseEventsList.removeAll(wfTrainingCourseEventsForExternalEmpsList);
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

    public TrainingCourseEventData getSelectedCourseEvent() {
	return selectedCourseEvent;
    }

    public void setSelectedCourseEvent(TrainingCourseEventData selectedCourseEvent) {
	this.selectedCourseEvent = selectedCourseEvent;
    }

    public WFTrainingCourseEventData getWfTrainingCourseEvent() {
	return wfTrainingCourseEvent;
    }

    public void setWfTrainingCourseEvent(WFTrainingCourseEventData wfTrainingCourseEvent) {
	this.wfTrainingCourseEvent = wfTrainingCourseEvent;
    }

    public List<WFTrainingCourseEventData> getWfTrainingCourseEventsList() {
	return wfTrainingCourseEventsList;
    }

    public void setWfTrainingCourseEventsList(List<WFTrainingCourseEventData> wfTrainingCourseEventsList) {
	this.wfTrainingCourseEventsList = wfTrainingCourseEventsList;
    }

    public List<WFTrainingCourseEventData> getWfTrainingCourseEventsForExternalEmpsList() {
	return wfTrainingCourseEventsForExternalEmpsList;
    }

    public void setWfTrainingCourseEventsForExternalEmpsList(List<WFTrainingCourseEventData> wfTrainingCourseEventsForExternalEmpsList) {
	this.wfTrainingCourseEventsForExternalEmpsList = wfTrainingCourseEventsForExternalEmpsList;
    }


    public int getSelectedTrainingUnitId() {
	return selectedTrainingUnitId;
    }

    public void setSelectedTrainingUnitId(int selectedTrainingUnitId) {
	this.selectedTrainingUnitId = selectedTrainingUnitId;
    }

    public int getSelectedTrainingYearId() {
	return selectedTrainingYearId;
    }

    public void setSelectedTrainingYearId(int selectedTrainingYearId) {
	this.selectedTrainingYearId = selectedTrainingYearId;
    }

    public Long getSelectedCourseEventId() {
	return selectedCourseEventId;
    }

    public void setSelectedCourseEventId(Long selectedCourseEventId) {
	this.selectedCourseEventId = selectedCourseEventId;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }

    public boolean isHasAccessPrivillege() {
	return hasAccessPrivillege;
    }

    public void setHasAccessPrivillege(boolean hasAccessPrivillege) {
	this.hasAccessPrivillege = hasAccessPrivillege;
    }

}
