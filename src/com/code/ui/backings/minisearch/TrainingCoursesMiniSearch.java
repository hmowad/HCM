package com.code.ui.backings.minisearch;

import java.io.Serializable;
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
@ManagedBean(name = "trainingCoursesMiniSearch")
@ViewScoped
public class TrainingCoursesMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private String searchMinorSpec;
    private String searchMajorSpec;
    private String searchCourseName;
    private int courseType; // 1 for civilian courses, 2 for military
    private List<TrainingCourseData> coursesList;

    public TrainingCoursesMiniSearch() {

	if (getRequest().getParameter("courseType") != null) {
	    courseType = Integer.parseInt(getRequest().getParameter("courseType"));
	    if (courseType == MilitaryCivillianEnum.Civillian.getCode())
		setScreenTitle(getMessage("title_civilianTrainingCoursesMiniSearch"));
	    else
		setScreenTitle(getMessage("title_militaryTrainingCoursesMiniSearch"));
	}
	searchTrainingCourses();
    }

    public void searchTrainingCourses() {
	try {
	    coursesList = TrainingCoursesService.getTrainingCoursesByNameAndQualification(courseType, searchCourseName, searchMinorSpec, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), searchMajorSpec);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getSearchMinorSpec() {
	return searchMinorSpec;
    }

    public void setSearchMinorSpec(String searchMinorSpec) {
	this.searchMinorSpec = searchMinorSpec;
    }

    public String getSearchCourseName() {
	return searchCourseName;
    }

    public void setSearchCourseName(String searchCourseName) {
	this.searchCourseName = searchCourseName;
    }

    public List<TrainingCourseData> getCoursesList() {
	return coursesList;
    }

    public void setCoursesList(List<TrainingCourseData> coursesList) {
	this.coursesList = coursesList;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public int getCourseType() {
	return courseType;
    }

    public void setCourseType(int courseType) {
	this.courseType = courseType;
    }

    public String getSearchMajorSpec() {
	return searchMajorSpec;
    }

    public void setSearchMajorSpec(String searchMajorSpec) {
	this.searchMajorSpec = searchMajorSpec;
    }

}