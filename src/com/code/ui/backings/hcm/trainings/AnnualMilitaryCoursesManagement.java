package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingAnnualCourseData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "annualMilitaryCoursesManagement")
@ViewScoped
public class AnnualMilitaryCoursesManagement extends BaseBacking {

    private final int pageSize = 10;

    private List<TrainingAnnualCourseData> annualMilitaryCourses;

    private Long trainingUnitId;
    private long selectedCourseId;
    private String selectedCourseName;
    List<TrainingUnitData> trainingUnits;

    public AnnualMilitaryCoursesManagement() {
	try {
	    setScreenTitle(getMessage("title_trainingAnnualMilitaryCoursesManagement"));
	    trainingUnits = TrainingSetupService.getAllTrainingUnitsData();
	    trainingUnitId = trainingUnits.get(0).getUnitId();
	    selectTrainingUnit();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewAnnualMilitaryCourse() {
	try {
	    TrainingAnnualCourseData annualMilitaryCourseData = new TrainingAnnualCourseData();

	    annualMilitaryCourseData.setTrainingUnitId(trainingUnitId);
	    annualMilitaryCourseData.setCourseId(selectedCourseId);
	    annualMilitaryCourseData.setCourseName(selectedCourseName);

	    annualMilitaryCourseData.getTrainingAnnualCourse().setSystemUser(loginEmpData.getEmpId() + "");
	    TrainingSetupService.addAnnualMilitaryCourseData(annualMilitaryCourseData);

	    annualMilitaryCourses.add(annualMilitaryCourseData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void deleteAnnualMilitaryCourse(TrainingAnnualCourseData annualCourseData) {
	try {
	    annualCourseData.getTrainingAnnualCourse().setSystemUser(loginEmpData.getEmpId() + "");
	    TrainingSetupService.deleteAnnualMilitaryCourseData(annualCourseData);
	    annualMilitaryCourses.remove(annualCourseData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectTrainingUnit() {
	try {
	    annualMilitaryCourses = TrainingSetupService.getAllAnnualMilitaryCourseDataByTrainingUnitId(trainingUnitId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<TrainingAnnualCourseData> getAnnualMilitaryCourses() {
	return annualMilitaryCourses;
    }

    public void setAnnualMilitaryCourses(List<TrainingAnnualCourseData> annualMilitaryCourses) {
	this.annualMilitaryCourses = annualMilitaryCourses;
    }

    public int getPageSize() {
	return pageSize;
    }

    public long getSelectedCourseId() {
	return selectedCourseId;
    }

    public void setSelectedCourseId(long selectedCourseId) {
	this.selectedCourseId = selectedCourseId;
    }

    public String getSelectedCourseName() {
	return selectedCourseName;
    }

    public void setSelectedCourseName(String selectedCoursName) {
	this.selectedCourseName = selectedCoursName;
    }

    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    public List<TrainingUnitData> getTrainingUnits() {
	return trainingUnits;
    }

    public void setTrainingUnits(List<TrainingUnitData> trainingUnits) {
	this.trainingUnits = trainingUnits;
    }

}