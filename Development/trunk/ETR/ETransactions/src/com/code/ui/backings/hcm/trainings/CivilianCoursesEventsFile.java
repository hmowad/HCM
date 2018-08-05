package com.code.ui.backings.hcm.trainings;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.FundSourceEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "civilianCoursesEventsFile")
@ViewScoped
public class CivilianCoursesEventsFile extends BaseBacking {
    private final int pageSize = 10;
    private boolean admin = false;

    private long courseEventId;
    private List<TrainingCourseEventData> courseEvents;
    private List<TrainingTransactionData> empsTransactions;
    private Long[] trainingTypeIds;
    private Date startDateFrom;
    private Date startDateTo;

    private long trainingTypeId = FlagsEnum.ALL.getCode();
    private long trainingUnitId = FlagsEnum.ALL.getCode();
    private int fundSource = FundSourceEnum.COST_ON_EMPLOYEE.getCode();
    private int empStatusId = FlagsEnum.ALL.getCode();
    private String searchPhysicalUnit;
    private TrainingCourseEventData selectedCourse;
    private long externalPartyId;

    public CivilianCoursesEventsFile() {
	setScreenTitle(getMessage("title_civilianCoursesEventsFile"));
	reset();
    }

    public void reset() {

	externalPartyId = FlagsEnum.ALL.getCode();
	courseEventId = FlagsEnum.ALL.getCode();
	// trainingTypeId = FlagsEnum.ALL.getCode();
	trainingTypeId = TrainingTypesEnum.MORNING_COURSE.getCode();
	externalPartyId = FlagsEnum.ALL.getCode();
	startDateFrom = null;
	startDateTo = null;
	selectedCourse = null;
	courseEvents = null;

    }

    public void searchCourses() {
	try {
	    selectedCourse = null;
	    trainingTypeIds = (trainingTypeId == FlagsEnum.ALL.getCode()) ? new Long[] { TrainingTypesEnum.MORNING_COURSE.getCode(), TrainingTypesEnum.EVENING_COURSE.getCode() } : new Long[] { trainingTypeId };
	    courseEvents = TrainingCoursesEventsService.getTrainingCoursesEventsWithStartDateRange(courseEventId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), externalPartyId, trainingTypeId, null, null, startDateFrom == null ? null : HijriDateService.getHijriDateString(startDateFrom), startDateTo == null ? null : HijriDateService.getHijriDateString(startDateTo), null);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectCourse(TrainingCourseEventData courseEvent) {
	selectedCourse = courseEvent;
	searchPhysicalUnit = null;
	searchEmpsTransactions();
    }

    public void searchEmpsTransactions() {
	searchPhysicalUnit = (searchPhysicalUnit == null) ? null : searchPhysicalUnit.trim();
	try {
	    empsTransactions = TrainingEmployeesService.getTrainingTransactionsDataForCourseEventAndEmployeePhysicalUnit(trainingTypeIds, empStatusId == FlagsEnum.ALL.getCode() ? null : new Integer[] { empStatusId }, selectedCourse.getId(), searchPhysicalUnit);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
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

    public int getPageSize() {
	return pageSize;
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

    public Long[] getTrainingTypeIds() {
	return trainingTypeIds;
    }

    public void setTrainingTypeIds(Long[] trainingTypeIds) {
	this.trainingTypeIds = trainingTypeIds;
    }

    public int getFundSource() {
	return fundSource;
    }

    public void setFundSource(int fundSource) {
	this.fundSource = fundSource;
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

    public boolean isAdmin() {
	return admin;
    }

    public void setAdmin(boolean admin) {
	this.admin = admin;
    }

}
