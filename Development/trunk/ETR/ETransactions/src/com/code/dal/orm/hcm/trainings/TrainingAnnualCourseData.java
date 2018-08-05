package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingAnnualCourseData_searchTrainingAnnualCourseData",
		query = "select c from TrainingAnnualCourseData c " +
			" where c.trainingUnitId = :P_TRAINING_UNIT_ID " +
			" order by c.id")

})
@Entity
@Table(name = "HCM_VW_TRN_ANNUAL_COURSES")
public class TrainingAnnualCourseData extends BaseEntity {
    private Long id;
    private Long trainingUnitId;
    private Long courseId;
    private String courseName;
    private TrainingAnnualCourse trainingAnnualCourse;

    public TrainingAnnualCourseData() {
	trainingAnnualCourse = new TrainingAnnualCourse();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingAnnualCourse.setId(id);
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
	this.trainingAnnualCourse.setTrainingUnitId(trainingUnitId);
    }

    @Basic
    @Column(name = "COURSE_ID")
    public Long getCourseId() {
	return courseId;
    }

    public void setCourseId(Long courseId) {
	this.courseId = courseId;
	this.trainingAnnualCourse.setCourseId(courseId);
    }

    @Basic
    @Column(name = "COURSE_NAME")
    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    @Transient
    public TrainingAnnualCourse getTrainingAnnualCourse() {
	return trainingAnnualCourse;
    }

    public void setTrainingAnnualCourse(TrainingAnnualCourse trainingAnnualCourse) {
	this.trainingAnnualCourse = trainingAnnualCourse;
    }
}
