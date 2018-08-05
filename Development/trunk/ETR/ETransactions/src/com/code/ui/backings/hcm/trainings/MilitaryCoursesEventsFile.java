package com.code.ui.backings.hcm.trainings;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventDecisionData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "militaryCoursesEventsFile")
@ViewScoped
public class MilitaryCoursesEventsFile extends BaseBacking {

    private final int pageSize = 10;
    private boolean admin = false;
    private long trainingUnitId = FlagsEnum.ALL.getCode();
    private long trainingYearId = FlagsEnum.ALL.getCode();

    private long courseEventId;
    private int courseStatus;
    private int mode;
    private List<TrainingUnitData> trainingUnits;
    private List<TrainingYear> trainingYears;
    private List<TrainingCourseEventData> courseEvents;
    private List<TrainingTransactionData> empsTransactions;
    private List<TrainingCourseEventDecisionData> courseEventDecisions;
    private Date startDateFrom;
    private Date startDateTo;

    private int reportType;
    private long trainingTypeId;
    private int empStatusId = FlagsEnum.ALL.getCode();
    private String searchPhysicalUnit;
    private TrainingCourseEventData selectedCourse;
    private long externalPartyId;

    public MilitaryCoursesEventsFile() {
	try {

	    if (getRequest().getParameter("mode") != null)
		mode = Integer.parseInt(getRequest().getParameter("mode"));
	    if (mode == 0) {
		setScreenTitle(getMessage("title_militaryIntCoursesEventsFile"));
		trainingYears = TrainingSetupService.getApprovedTrainingYears();
		trainingUnits = TrainingSetupService.getAllTrainingUnitsData();
		trainingTypeId = TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode();
	    } else {
		setScreenTitle(getMessage("title_militaryExtCoursesEventsFile"));
		trainingTypeId = TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode();
	    }
	    reportType = 1;
	    reset();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void reset() {
	try {
	    if (trainingTypeId == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
		if (this.loginEmpData.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() && TrainingSetupService.getTrainingUnitByPhysicalUnitHKey(this.loginEmpData.getPhysicalUnitHKey()) == null) {
		    trainingUnitId = trainingUnits.get(0).getUnitId();
		    admin = true;
		} else {
		    for (TrainingUnitData trainingUnit : trainingUnits)
			if (trainingUnit.getRegionId().equals(this.loginEmpData.getPhysicalRegionId())) {
			    trainingUnitId = trainingUnit.getUnitId();
			    break;
			}
		    admin = false;
		}
		if (trainingYears.size() != 0)
		    trainingYearId = trainingYears.get(trainingYears.size() - 1).getId();
	    }
	    courseStatus = FlagsEnum.ALL.getCode();
	    externalPartyId = FlagsEnum.ALL.getCode();
	    courseEventId = FlagsEnum.ALL.getCode();

	    startDateFrom = null;
	    startDateTo = null;
	    selectedCourse = null;
	    courseEvents = null;
	} catch (BusinessException e) {

	}
    }

    public void searchDataChangeListener() {
	courseEventId = FlagsEnum.ALL.getCode();
    }

    public void searchCourses() {
	try {
	    selectedCourse = null;
	    courseEvents = TrainingCoursesEventsService.getTrainingCoursesEventsWithStartDateRange(courseEventId, trainingYearId, trainingUnitId, externalPartyId, trainingTypeId, null, courseStatus == FlagsEnum.ALL.getCode() ? null : new Integer[] { courseStatus }, startDateFrom == null ? null : HijriDateService.getHijriDateString(startDateFrom), startDateTo == null ? null : HijriDateService.getHijriDateString(startDateTo), null);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectCourse(TrainingCourseEventData courseEvent) {
	try {
	    selectedCourse = courseEvent;
	    if (trainingTypeId == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
		courseEventDecisions = TrainingCoursesEventsService.getTrainingCourseEventDecisionsByCourseEventId(courseEvent.getId());
	    }
	    searchEmpsTransactions();

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchEmpsTransactions() {
	try {
	    empsTransactions = TrainingEmployeesService.getTrainingTransactionsDataForCourseEventAndEmployeePhysicalUnit(null, empStatusId == FlagsEnum.ALL.getCode() ? null : new Integer[] { empStatusId }, selectedCourse.getId(), searchPhysicalUnit);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printCertificate(long trainingTransactionId) {

	try {
	    long trainingTransactionDetailId = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionId).get(0).getId();
	    byte[] bytes = TrainingEmployeesService.getTraineeCertificateBytes(trainingTransactionDetailId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	byte[] bytes = null;
	try {
	    if (reportType == 1)
		bytes = TrainingCoursesEventsService.getTrainingCourseEventPlanBasedBytes(selectedCourse.getId());
	    else if (reportType == 2)
		bytes = TrainingCoursesEventsService.getTrainingCourseEventJoiningBytes(selectedCourse.getId());
	    else
		bytes = TrainingCoursesEventsService.getTrainingCourseEventResultsBytes(selectedCourse.getId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printTrainingCourseEventDecision(TrainingCourseEventDecisionData decision) {
	try {
	    byte[] bytes = TrainingCoursesEventsService.getTrainingCourseEventDecisionBytes(decision.getId(), decision.getTransactionTypeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    public Long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(Long trainingYearId) {
	this.trainingYearId = trainingYearId;
    }

    public long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(long courseEventId) {
	this.courseEventId = courseEventId;
    }

    public void setTrainingUnitId(long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    public void setTrainingYearId(long trainingYearId) {
	this.trainingYearId = trainingYearId;
    }

    public int getPageSize() {
	return pageSize;
    }

    public List<TrainingUnitData> getTrainingUnits() {
	return trainingUnits;
    }

    public void setTrainingUnits(List<TrainingUnitData> trainingUnits) {
	this.trainingUnits = trainingUnits;
    }

    public List<TrainingYear> getTrainingYears() {
	return trainingYears;
    }

    public void setTrainingYears(List<TrainingYear> trainingYears) {
	this.trainingYears = trainingYears;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public int getCourseStatus() {
	return courseStatus;
    }

    public void setCourseStatus(int courseStatus) {
	this.courseStatus = courseStatus;
    }

    public Date getStartDateFrom() {
	return startDateFrom;
    }

    public void setStartDateFrom(Date startDateFrom) {
	this.startDateFrom = startDateFrom;
    }

    public Date getStartDateTo() {
	return startDateTo;
    }

    public void setStartDateTo(Date startDateTo) {
	this.startDateTo = startDateTo;
    }

    public List<TrainingCourseEventData> getCourseEvents() {
	return courseEvents;
    }

    public void setCourseEvents(List<TrainingCourseEventData> courseEvents) {
	this.courseEvents = courseEvents;
    }

    public List<TrainingTransactionData> getEmpsTransactions() {
	return empsTransactions;
    }

    public void setEmpsTransactions(List<TrainingTransactionData> empsTransactions) {
	this.empsTransactions = empsTransactions;
    }

    public int getEmpStatusId() {
	return empStatusId;
    }

    public void setEmpStatusId(int empStatusId) {
	this.empStatusId = empStatusId;
    }

    public String getSearchPhysicalUnit() {
	return searchPhysicalUnit;
    }

    public void setSearchPhysicalUnit(String searchPhysicalUnit) {
	this.searchPhysicalUnit = searchPhysicalUnit;
    }

    public TrainingCourseEventData getSelectedCourse() {
	return selectedCourse;
    }

    public void setSelectedCourse(TrainingCourseEventData selectedCourse) {
	this.selectedCourse = selectedCourse;
    }

    public long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
    }

    public long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(long externalPartyId) {
	this.externalPartyId = externalPartyId;
    }

    public List<TrainingCourseEventDecisionData> getCourseEventDecisions() {
	return courseEventDecisions;
    }

    public void setCourseEventDecisions(List<TrainingCourseEventDecisionData> courseEventDecisions) {
	this.courseEventDecisions = courseEventDecisions;
    }

    public boolean isAdmin() {
	return admin;
    }

    public void setAdmin(boolean admin) {
	this.admin = admin;
    }

}