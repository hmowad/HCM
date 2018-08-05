package com.code.ui.backings.hcm.trainings;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.TrainingCourseEventStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.TrainingYearStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "militaryCoursesEventsInquiry")
@ViewScoped
public class MilitaryCoursesEventsInquiry extends BaseBacking {

    private final int pageSize = 10;

    private long trainingTypeId;
    private String courseName;
    private String countryName;
    private String trainingUnitPartyDesc;
    private Date startDateFrom;
    private Date startDateTo;

    private List<TrainingCourseEventData> courseEvents;

    private Date sysDate, oneYearSysDate;

    public MilitaryCoursesEventsInquiry() {
	try {
	    sysDate = HijriDateService.getHijriSysDate();
	    oneYearSysDate = HijriDateService.addSubHijriMonthsDays(sysDate, 12, 0);
	    reset();
	    searchCourses();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	courseName = countryName = trainingUnitPartyDesc = "";
	trainingTypeId = FlagsEnum.ALL.getCode();
	startDateFrom = sysDate;
	startDateTo = oneYearSysDate;
    }

    public void searchCourses() {
	try {
	    courseEvents = TrainingCoursesEventsService.getMilitaryTrainingCoursesEventsForInquiry(trainingTypeId, trainingUnitPartyDesc, countryName, courseName, startDateFrom == null ? null : HijriDateService.getHijriDateString(startDateFrom), startDateTo == null ? null : HijriDateService.getHijriDateString(startDateTo));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public boolean isNominationAllowed(TrainingCourseEventData courseEvent) {
	if (loginEmpData.getCategoryId() != CategoriesEnum.OFFICERS.getCode())
	    return false;
	if (courseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && courseEvent.getTrainingYearStatus() != TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode())
	    return false;
	if (courseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() &&
		courseEvent.getStatus() != TrainingCourseEventStatusEnum.COURSE_EVENT_CANCEL_DECISION_ISSUED.getCode() &&
		courseEvent.getStatus() != TrainingCourseEventStatusEnum.COURSE_EVENT_HELD.getCode())
	    return true;
	if (courseEvent.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() &&
		courseEvent.getStatus() == TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode())
	    return true;
	return false;
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

    public long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
    }

    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    public String getCountryName() {
	return countryName;
    }

    public void setCountryName(String countryName) {
	this.countryName = countryName;
    }

    public String getTrainingUnitPartyDesc() {
	return trainingUnitPartyDesc;
    }

    public void setTrainingUnitPartyDesc(String trainingUnitPartyDesc) {
	this.trainingUnitPartyDesc = trainingUnitPartyDesc;
    }

    public int getPageSize() {
	return pageSize;
    }

}
