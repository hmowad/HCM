package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.enums.FlagsEnum;
import com.code.enums.MilitaryCivillianEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "civilCoursesManagement")
@ViewScoped
public class CivilCoursesManagement extends BaseBacking {

    private final int pageSize = 10;

    private String civilCourseName;
    private String searchMinorSpec;
    private String searchMajorSpec;
    private List<TrainingCourseData> civilCourses;
    private TrainingCourseData trainingCourseData;

    private String originalCourseName;
    private int pageNum = 1;

    public CivilCoursesManagement() {
	setScreenTitle(getMessage("title_trainingCivilCourses"));
	reset();
    }

    public void searchCivilCourses() {
	try {
	    civilCourses = TrainingCoursesService.getTrainingCoursesByNameAndQualification(MilitaryCivillianEnum.Civillian.getCode(), civilCourseName, searchMinorSpec, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), searchMajorSpec);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewCivilCourse() {
	trainingCourseData.setType(MilitaryCivillianEnum.Civillian.getCode());
	trainingCourseData.setRankingFlagBoolean(false);
	trainingCourseData.setElectronicCertificateBoolean(false);
	civilCourses.add(0, trainingCourseData);
	trainingCourseData = new TrainingCourseData();
	pageNum = 1;
    }

    public void saveCivilCourse(TrainingCourseData civilCourse) {
	try {
	    if (civilCourse.getId() != null)
		originalCourseName = TrainingCoursesService.getTrainingCoursesById(civilCourse.getId()).getName();

	    TrainingEmployeesWorkFlow.validateTrainingCourseWFBusinessRules(civilCourse, originalCourseName, false);

	    civilCourse.getTrainingCourse().setSystemUser(loginEmpData.getEmpId() + "");

	    if (civilCourse.getId() == null) {
		TrainingCoursesService.addTrainingCourse(civilCourse);
	    } else {
		TrainingCoursesService.updateTrainingCourse(civilCourse, originalCourseName);
	    }

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void deleteCivilCourse(TrainingCourseData civilCourse) {
	try {
	    if (civilCourse.getId() != null) {
		civilCourse.getTrainingCourse().setSystemUser(loginEmpData.getEmpId() + "");
		TrainingEmployeesWorkFlow.validateTrainingCourseWFBusinessRules(civilCourse, null, true);
		TrainingCoursesService.deleteTrainingCourse(civilCourse);
	    }
	    civilCourses.remove(civilCourse);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void reset() {
	searchMinorSpec = "";
	searchMajorSpec = "";
	civilCourseName = "";
	trainingCourseData = new TrainingCourseData();
	searchCivilCourses();
    }

    public String getCivilCourseName() {
	return civilCourseName;
    }

    public void setCivilCourseName(String civilCourseName) {
	this.civilCourseName = civilCourseName;
    }

    public List<TrainingCourseData> getCivilCourses() {
	return civilCourses;
    }

    public void setCivilCourses(List<TrainingCourseData> civilCourses) {
	this.civilCourses = civilCourses;
    }

    public int getPageSize() {
	return pageSize;
    }

    public TrainingCourseData getTrainingCourseData() {
	return trainingCourseData;
    }

    public void setTrainingCourseData(TrainingCourseData trainingCourseData) {
	this.trainingCourseData = trainingCourseData;
    }

    public int getPageNum() {
	return pageNum;
    }

    public void setPageNum(int pageNum) {
	this.pageNum = pageNum;
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