package com.code.ui.backings.hcm.trainings;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.enums.TrainingTransactionCategoryEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "militaryExtCourseEventPostponeRegistration")
@ViewScoped
public class MilitaryExtCourseEventPostponeRegistration extends BaseBacking implements Serializable {

    private TrainingCourseEventData courseEventData;
    private String courseEventStartDateString;
    private String courseEventEndDateString;

    public MilitaryExtCourseEventPostponeRegistration() {
	setScreenTitle(getMessage("title_militaryExtCourseEventPostponeRegistration"));
	courseEventData = new TrainingCourseEventData();
    }

    public void save() {
	try {
	    courseEventData.getTrainingCourseEvent().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    TrainingCoursesEventsService.handleExternalCoursesOperations(courseEventData, TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_POSTPONEMENT.getCode());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void getTrainingCourseEventData() {
	try {
	    courseEventData = TrainingCoursesEventsService.getCourseEventById(courseEventData.getId());
	    courseEventStartDateString = courseEventData.getActualStartDateString();
	    courseEventEndDateString = courseEventData.getActualEndDateString();
	    courseEventData.setActualStartDateString("");
	    courseEventData.setActualEndDateString("");
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    public void calculateNewEndDate() throws BusinessException {
	this.courseEventData.setActualEndDate(HijriDateService.addSubHijriDays(this.courseEventData.getActualStartDate(), HijriDateService.hijriDateDiff(HijriDateService.getHijriDate(courseEventStartDateString), HijriDateService.getHijriDate(courseEventEndDateString))));
    }

    public TrainingCourseEventData getCourseEventData() {
	return courseEventData;
    }

    public void setCourseEventData(TrainingCourseEventData courseEventData) {
	this.courseEventData = courseEventData;
    }

    public String getCourseEventStartDateString() {
	return courseEventStartDateString;
    }

    public void setCourseEventStartDateString(String courseEventStartDateString) {
	this.courseEventStartDateString = courseEventStartDateString;
    }

    public String getCourseEventEndDateString() {
	return courseEventEndDateString;
    }

    public void setCourseEventEndDateString(String courseEventEndDateString) {
	this.courseEventEndDateString = courseEventEndDateString;
    }
}
