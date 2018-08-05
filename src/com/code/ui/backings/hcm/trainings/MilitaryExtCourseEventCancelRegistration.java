package com.code.ui.backings.hcm.trainings;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.enums.TrainingTransactionCategoryEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "militaryExtCourseEventCancelRegistration")
@ViewScoped
public class MilitaryExtCourseEventCancelRegistration extends BaseBacking implements Serializable {

    private TrainingCourseEventData courseEventData;

    public MilitaryExtCourseEventCancelRegistration() {
	setScreenTitle(getMessage("title_militaryExtCourseEventCancelRegistration"));
	courseEventData = new TrainingCourseEventData();
    }

    public void save() {
	try {
	    courseEventData.getTrainingCourseEvent().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    TrainingCoursesEventsService.handleExternalCoursesOperations(courseEventData, TrainingTransactionCategoryEnum.EXTERNAL_COURSE_EVENT_CANCELLATION.getCode());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void getTrainingCourseEventData() {
	try {
	    courseEventData = TrainingCoursesEventsService.getCourseEventById(courseEventData.getId());
	} catch (BusinessException e) {
	    e.printStackTrace();
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
