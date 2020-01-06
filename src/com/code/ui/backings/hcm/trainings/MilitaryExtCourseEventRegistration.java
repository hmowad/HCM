package com.code.ui.backings.hcm.trainings;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.enums.TrainingTransactionCategoryEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "militaryExtCourseEventRegistration")
@ViewScoped
public class MilitaryExtCourseEventRegistration extends BaseBacking implements Serializable {

    private TrainingCourseEventData courseEventData;

    public MilitaryExtCourseEventRegistration() {
	setScreenTitle(getMessage("title_militaryExtCourseEventRegistration"));
	courseEventData = new TrainingCourseEventData();
    }

    public void save() {
	try {
	    courseEventData.getTrainingCourseEvent().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    TrainingCoursesEventsService.handleExternalCoursesOperations(courseEventData, TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_ADDITION.getCode());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void reset() {
	courseEventData = new TrainingCourseEventData();
    }

    public void getTrainingCourseData() {
	try {
	    TrainingCourseData courseData = TrainingCoursesService.getTrainingCoursesById(courseEventData.getCourseId());

	    courseEventData.setCandidatesMax(courseData.getCandidatesMax());
	    courseEventData.setPrerquisites(courseData.getPrerquisites());
	    courseEventData.setWeeksCount(courseData.getWeeksCount());
	    courseEventData.setRankingFlag(courseData.getRankingFlag());
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    public void manipulateEndDate() {
	try {
	    if (courseEventData.getPlannedStartDateString() != null && (courseEventData.getWeeksCount() != null && courseEventData.getWeeksCount() > 0))
		courseEventData.setPlannedEndDateString(HijriDateService.addSubStringHijriDays(courseEventData.getPlannedStartDateString(), (courseEventData.getWeeksCount() * 7) - 1));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public TrainingCourseEventData getCourseEventData() {
	return courseEventData;
    }

    public void setCourseEventData(TrainingCourseEventData courseEventData) {
	this.courseEventData = courseEventData;
    }
}
