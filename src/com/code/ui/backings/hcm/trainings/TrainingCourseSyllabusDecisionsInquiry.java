package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseDecision;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "trainingCourseSyllabusDecisionsInquiry")
@ViewScoped
public class TrainingCourseSyllabusDecisionsInquiry extends BaseBacking {
    private int pageSize = 10;
    private Date fromDate;
    private Date toDate;

    private String trainingCourseName;
    private long trainingCourseId;
    private List<TrainingCourseDecision> coursesDecisions;

    public TrainingCourseSyllabusDecisionsInquiry() {
	reset();
    }

    public void reset() {
	fromDate = toDate = null;
	trainingCourseName = "";
	trainingCourseId = FlagsEnum.ALL.getCode();
	coursesDecisions = new ArrayList<TrainingCourseDecision>();
    }

    public void searchDecisions() {
	try {
	    coursesDecisions = TrainingCoursesService.getTrainingCoursesDecisions(trainingCourseId, null, fromDate, toDate, null);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), new Object[] {}));
	}
    }

    public void print(TrainingCourseDecision decision) {
	try {
	    byte[] bytes = null;
	    bytes = TrainingCoursesService.getTrainingCourseDecisionSyllabusBytes(decision.getId(), decision.getTransactionTypeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), new Object[] {}));
	}
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public String getTrainingCourseName() {
	return trainingCourseName;
    }

    public void setTrainingCourseName(String trainingCourseName) {
	this.trainingCourseName = trainingCourseName;
    }

    public long getTrainingCourseId() {
	return trainingCourseId;
    }

    public void setTrainingCourseId(long trainingCourseId) {
	this.trainingCourseId = trainingCourseId;
    }

    public List<TrainingCourseDecision> getCoursesDecisions() {
	return coursesDecisions;
    }

    public void setCoursesDecisions(List<TrainingCourseDecision> coursesDecisions) {
	this.coursesDecisions = coursesDecisions;
    }

    public int getPageSize() {
	return pageSize;
    }

}
