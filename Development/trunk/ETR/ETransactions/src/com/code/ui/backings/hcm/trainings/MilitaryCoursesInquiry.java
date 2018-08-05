package com.code.ui.backings.hcm.trainings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.enums.FlagsEnum;
import com.code.enums.MilitaryCivillianEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "militaryCoursesInquiry")
@ViewScoped
public class MilitaryCoursesInquiry extends BaseBacking implements Serializable {

    private String trainingCourseName;
    private String searchMinorSpec;
    private String searchMajorSpec;
    private int pageSize = 10;

    private List<TrainingCourseData> trainingCoursesList;

    public MilitaryCoursesInquiry() {
	setScreenTitle(getMessage("title_militaryCoursesInquiry"));
	resetForm();
    }

    public void searchTrainingCourses() {
	try {
	    trainingCoursesList = TrainingCoursesService.getTrainingCoursesByNameAndQualification(MilitaryCivillianEnum.Military.getCode(), trainingCourseName, searchMinorSpec, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), searchMajorSpec);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void resetForm() {
	trainingCourseName = null;
	searchMinorSpec = "";
	searchMajorSpec = "";
	trainingCoursesList = new ArrayList<>();
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public String getTrainingCourseName() {
	return trainingCourseName;
    }

    public void setTrainingCourseName(String trainingCourseName) {
	this.trainingCourseName = trainingCourseName;
    }

    public List<TrainingCourseData> getTrainingCoursesList() {
	return trainingCoursesList;
    }

    public void setTrainingCoursesList(List<TrainingCourseData> trainingCoursesList) {
	this.trainingCoursesList = trainingCoursesList;
    }

    public String getSearchMinorSpec() {
	return searchMinorSpec;
    }

    public void setSearchMinorSpec(String searchMinorSpec) {
	this.searchMinorSpec = searchMinorSpec;
    }

    public String getSearchMajorSpec() {
	return searchMajorSpec;
    }

    public void setSearchMajorSpec(String searchMajorSpec) {
	this.searchMajorSpec = searchMajorSpec;
    }

}
