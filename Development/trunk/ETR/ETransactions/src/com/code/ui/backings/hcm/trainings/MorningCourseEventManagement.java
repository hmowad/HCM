package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.FlagsEnum;
import com.code.enums.TrainingTransactionCategoryEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "morningCourseEventManagement")
@ViewScoped
public class MorningCourseEventManagement extends BaseBacking {

    private int pageSize = 10;
    private String searchCourseName;
    private String searchExternalPartyDesc;
    private Date courseStartDate;
    private Date courseStartDateTo;
    private boolean modify = true;
    private long selectedCourseId;
    private long selectedCourseEventId;
    private long selectedExternalPartyId;

    private String externalPartyAddress;

    private TrainingCourseEventData selectedCourseEvent;
    private List<TrainingCourseEventData> courseEvents;
    private List<TrainingTransactionData> trainingTransactionList;
    private List<WFTrainingData> wfTrainingList;

    public MorningCourseEventManagement() {

	try {
	    super.init();
	    setScreenTitle(getMessage("title_morningCourseEventManagement"));
	    reset();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void reset() {
	selectedCourseId = selectedCourseEventId = selectedExternalPartyId = FlagsEnum.ALL.getCode();

	searchCourseName = searchExternalPartyDesc = "";
	courseStartDate = courseStartDateTo = null;
	courseEvents = new ArrayList<TrainingCourseEventData>();
	wfTrainingList = new ArrayList<WFTrainingData>();
	trainingTransactionList = new ArrayList<TrainingTransactionData>();
	selectedCourseEvent = null;
    }

    public void searchCourseEvents() {
	try {
	    courseEvents = TrainingCoursesEventsService.getTrainingCoursesEventsWithStartDateRange(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), selectedExternalPartyId, TrainingTypesEnum.MORNING_COURSE.getCode(), searchCourseName, null, HijriDateService.getHijriDateString(courseStartDate), HijriDateService.getHijriDateString(courseStartDateTo), searchExternalPartyDesc);
	    selectedCourseEvent = null;
	    selectedCourseEventId = FlagsEnum.ALL.getCode();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void addCourseEvent() {
	try {
	    selectedCourseEvent = TrainingCoursesEventsService.constructCivilianCourseEvent(TrainingTypesEnum.MORNING_COURSE.getCode(), selectedCourseId);
	    wfTrainingList = new ArrayList<WFTrainingData>();
	    trainingTransactionList = new ArrayList<TrainingTransactionData>();
	    modify = true;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void saveCourseEvent() {
	try {
	    selectedCourseEvent.setPlannedStartDate(selectedCourseEvent.getActualStartDate());
	    selectedCourseEvent.setPlannedEndDate(selectedCourseEvent.getActualEndDate());
	    if (selectedCourseEvent.getId() == null) {
		TrainingCoursesEventsService.addTrainingCourseEvent(selectedCourseEvent, TrainingTransactionCategoryEnum.NOMINATION.getCode());
		courseEvents.add(selectedCourseEvent);
	    } else
		TrainingCoursesEventsWorkFlow.updateTrainingCourseEvent(selectedCourseEvent, TrainingTransactionCategoryEnum.NOMINATION.getCode());

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void deleteCourseEvent(TrainingCourseEventData courseEvent) {
	try {
	    TrainingCoursesEventsWorkFlow.deleteTrainingCourseEvent(courseEvent, this.loginEmpData.getEmpId());
	    courseEvents.remove(courseEvent);
	    if (selectedCourseEvent != null && courseEvent.getId().equals(selectedCourseEvent.getId())) {
		selectedCourseEvent = null;
	    }
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void selectCourseEvent(TrainingCourseEventData courseEvent) {
	try {
	    selectedCourseEvent = courseEvent;
	    wfTrainingList = TrainingEmployeesWorkFlow.getRunningWFTrainingDataForTrainingCourseEvent(selectedCourseEvent.getId());
	    trainingTransactionList = TrainingEmployeesService.getTrainingTransactionsDataForCourseEventAndEmployeePhysicalUnit(null, null, selectedCourseEvent.getId(), null);
	    List<WFTrainingData> allWfTrainingList = TrainingEmployeesWorkFlow.getWFTrainingDataByCourseEventId(courseEvent.getId());
	    if (allWfTrainingList.size() == 0 && trainingTransactionList.size() == 0)
		modify = true;
	    else
		modify = false;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public long getSelectedCourseId() {
	return selectedCourseId;
    }

    public void setSelectedCourseId(long selectedCourseId) {
	this.selectedCourseId = selectedCourseId;
    }

    public long getSelectedExternalPartyId() {
	return selectedExternalPartyId;
    }

    public void setSelectedExternalPartyId(long selectedExternalPartyId) {
	this.selectedExternalPartyId = selectedExternalPartyId;
    }

    public Date getCourseStartDate() {
	return courseStartDate;
    }

    public void setCourseStartDate(Date courseStartDate) {
	this.courseStartDate = courseStartDate;
    }

    public Date getCourseStartDateTo() {
	return courseStartDateTo;
    }

    public void setCourseStartDateTo(Date courseStartDateTo) {
	this.courseStartDateTo = courseStartDateTo;
    }

    public String getExternalPartyAddress() {
	return externalPartyAddress;
    }

    public void setExternalPartyAddress(String externalPartyAddress) {
	this.externalPartyAddress = externalPartyAddress;
    }

    public int getPageSize() {
	return pageSize;
    }

    public String getSearchCourseName() {
	return searchCourseName;
    }

    public void setSearchCourseName(String searchCourseName) {
	this.searchCourseName = searchCourseName;
    }

    public long getSelectedCourseEventId() {
	return selectedCourseEventId;
    }

    public void setSelectedCourseEventId(long selectedCourseEventId) {
	this.selectedCourseEventId = selectedCourseEventId;
    }

    public TrainingCourseEventData getSelectedCourseEvent() {
	return selectedCourseEvent;
    }

    public void setSelectedCourseEvent(TrainingCourseEventData selectedCourseEvent) {
	this.selectedCourseEvent = selectedCourseEvent;
    }

    public List<TrainingTransactionData> getTrainingTransactionList() {
	return trainingTransactionList;
    }

    public void setTrainingTransactionList(List<TrainingTransactionData> trainingTransactionList) {
	this.trainingTransactionList = trainingTransactionList;
    }

    public List<WFTrainingData> getWfTrainingList() {
	return wfTrainingList;
    }

    public void setWfTrainingList(List<WFTrainingData> wfTrainingList) {
	this.wfTrainingList = wfTrainingList;
    }

    public List<TrainingCourseEventData> getCourseEvents() {
	return courseEvents;
    }

    public void setCoursesEvents(List<TrainingCourseEventData> courseEvents) {
	this.courseEvents = courseEvents;
    }

    public String getSearchExternalPartyDesc() {
	return searchExternalPartyDesc;
    }

    public void setSearchExternalPartyDesc(String searchExternalPartyDesc) {
	this.searchExternalPartyDesc = searchExternalPartyDesc;
    }

    public void setCourseEvents(List<TrainingCourseEventData> courseEvents) {
	this.courseEvents = courseEvents;
    }

    public boolean isModify() {
	return modify;
    }

}
