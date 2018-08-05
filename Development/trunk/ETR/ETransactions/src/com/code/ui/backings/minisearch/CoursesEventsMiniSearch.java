package com.code.ui.backings.minisearch;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.enums.FlagsEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "trainingCoursesEventsMiniSearch")
@ViewScoped
public class CoursesEventsMiniSearch extends BaseBacking {

    private int rowsCount = 10;

    // parameters
    private long externalPartyId = FlagsEnum.ALL.getCode();
    private long requestingRegionId = FlagsEnum.ALL.getCode();
    private long trainingTypeId = FlagsEnum.ALL.getCode();
    private Integer[] statusesIds = null;
    private int nominationFlag;

    // search fields
    private long selectedTrainingYearId = FlagsEnum.ALL.getCode();
    private long selectedTrainingUnitId = FlagsEnum.ALL.getCode();
    private String searchCourseSerial;
    private String searchCourseName;
    private String searchExternalPartyName;

    private List<TrainingYear> trainingYears;
    private List<TrainingUnitData> trainingUnits;
    private List<TrainingCourseEventData> coursesEventsList;

    private boolean noApprovedTrnYears;

    public CoursesEventsMiniSearch() {
	try {
	    setScreenTitle(getMessage("title_coursesEventsMiniSearch"));

	    // get request Parameters
	    if (getRequest().getParameter("trainingYearId") != null) {
		selectedTrainingYearId = Long.parseLong(getRequest().getParameter("trainingYearId"));
	    }

	    if (getRequest().getParameter("requestingRegionId") != null)
		requestingRegionId = Long.parseLong(getRequest().getParameter("requestingRegionId"));

	    if (getRequest().getParameter("trainingTypeId") != null)
		trainingTypeId = Long.parseLong(getRequest().getParameter("trainingTypeId"));

	    if (getRequest().getParameter("trainingUnitId") != null) {
		selectedTrainingUnitId = Long.parseLong(getRequest().getParameter("trainingUnitId"));
	    }

	    if (getRequest().getParameter("externalPartyId") != null) {
		externalPartyId = Long.parseLong(getRequest().getParameter("externalPartyId"));
	    }

	    if (getRequest().getParameter("nominationFlag") != null) {
		nominationFlag = Integer.parseInt(getRequest().getParameter("nominationFlag"));
	    }

	    if (getRequest().getParameter("statuses") != null) {
		String statuses = getRequest().getParameter("statuses");

		if (!statuses.equals("-1")) {
		    String[] arrayOfStatuses = statuses.split(",");
		    statusesIds = new Integer[arrayOfStatuses.length];
		    for (int i = 0; i < arrayOfStatuses.length; i++) {
			statusesIds[i] = Integer.parseInt(arrayOfStatuses[i]);
		    }
		}
	    }

	    // initialize lists and variables
	    if (trainingTypeId == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
		if (selectedTrainingYearId != FlagsEnum.ALL.getCode()) {
		    trainingYears = new ArrayList<>(1);
		    trainingYears.add(TrainingSetupService.getTrainingYearById(selectedTrainingYearId));
		} else {
		    trainingYears = TrainingSetupService.getApprovedTrainingYears();
		    if (trainingYears.isEmpty()) {
			noApprovedTrnYears = true;
			throw new BusinessException("error_noApprovedTrainingYears");
		    }
		}

		if (selectedTrainingUnitId != FlagsEnum.ALL.getCode()) {
		    trainingUnits = new ArrayList<>(1);
		    trainingUnits.add(TrainingSetupService.getTrainingUnitById(selectedTrainingUnitId));
		} else {
		    if (nominationFlag == FlagsEnum.ON.getCode()) // Used in training requests (Nomination)
			trainingUnits = TrainingSetupService.getAllowedForNominationTrainingUnits(requestingRegionId);
		    else
			trainingUnits = TrainingSetupService.filterTrainingUnitsForCourseEventMiniSearch(requestingRegionId);
		}
	    } else {
		if (externalPartyId != FlagsEnum.ALL.getCode())
		    searchExternalPartyName = TrainingSetupService.getTrainingExternalPartyById(externalPartyId).getDescription();
	    }

	    reset();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void reset() throws BusinessException {
	searchCourseName = "";
	searchCourseSerial = "";

	if (externalPartyId == FlagsEnum.ALL.getCode())
	    searchExternalPartyName = "";

	if (trainingTypeId == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
	    selectedTrainingYearId = trainingYears.get(trainingYears.size() - 1).getId();
	    selectedTrainingUnitId = trainingUnits.get(0).getUnitId();
	}

	searchTrainingCoursesEvents();
    }

    public void searchTrainingCoursesEvents() {
	try {
	    coursesEventsList = TrainingCoursesEventsService.getTrainingCoursesEvents(FlagsEnum.ALL.getCode(), selectedTrainingYearId, selectedTrainingUnitId, externalPartyId, externalPartyId == FlagsEnum.ALL.getCode() ? searchExternalPartyName : null, searchCourseName, trainingTypeId, statusesIds, null, null, searchCourseSerial.isEmpty() ? FlagsEnum.ALL.getCode() : Integer.parseInt(searchCourseSerial));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public long getSelectedTrainingYearId() {
	return selectedTrainingYearId;
    }

    public void setSelectedTrainingYearId(long selectedTrainingYearId) {
	this.selectedTrainingYearId = selectedTrainingYearId;
    }

    public long getSelectedTrainingUnitId() {
	return selectedTrainingUnitId;
    }

    public void setSelectedTrainingUnitId(long selectedTrainingUnitId) {
	this.selectedTrainingUnitId = selectedTrainingUnitId;
    }

    public long getRequestingRegionId() {
	return requestingRegionId;
    }

    public void setRequestingRegionId(long requestingRegionId) {
	this.requestingRegionId = requestingRegionId;
    }

    public long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
    }

    public List<TrainingYear> getTrainingYears() {
	return trainingYears;
    }

    public void setTrainingYears(List<TrainingYear> trainingYears) {
	this.trainingYears = trainingYears;
    }

    public List<TrainingUnitData> getTrainingUnits() {
	return trainingUnits;
    }

    public void setTrainingUnits(List<TrainingUnitData> trainingUnits) {
	this.trainingUnits = trainingUnits;
    }

    public List<TrainingCourseEventData> getCoursesEventsList() {
	return coursesEventsList;
    }

    public void setCoursesEventsList(List<TrainingCourseEventData> coursesEventsList) {
	this.coursesEventsList = coursesEventsList;
    }

    public Integer[] getStatusesIds() {
	return statusesIds;
    }

    public void setStatusesIds(Integer[] statusesIds) {
	this.statusesIds = statusesIds;
    }

    public String getSearchCourseName() {
	return searchCourseName;
    }

    public void setSearchCourseName(String searchCourseName) {
	this.searchCourseName = searchCourseName;
    }

    public String getSearchExternalPartyName() {
	return searchExternalPartyName;
    }

    public void setSearchExternalPartyName(String searchExternalPartyName) {
	this.searchExternalPartyName = searchExternalPartyName;
    }

    public String getSearchCourseSerial() {
	return searchCourseSerial;
    }

    public void setSearchCourseSerial(String searchCourseSerial) {
	this.searchCourseSerial = searchCourseSerial;
    }

    public long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(long externalPartyId) {
	this.externalPartyId = externalPartyId;
    }

    public boolean isNoApprovedTrnYears() {
	return noApprovedTrnYears;
    }
}