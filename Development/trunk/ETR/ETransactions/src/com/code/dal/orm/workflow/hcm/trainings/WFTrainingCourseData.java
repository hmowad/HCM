package com.code.dal.orm.workflow.hcm.trainings;

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
	@NamedQuery(name = "wf_wfTrainingCourseData_searchWFTrainingCoursesData",
		query = "select c from WFTrainingCourseData c "
			+ "where (:P_INSTANCE_ID = -1 or c.instanceId = :P_INSTANCE_ID) ")

})
@SuppressWarnings("serial")
@Entity
@Table(name = "ETR_VW_WF_TRAINING_COURSES")
public class WFTrainingCourseData extends BaseEntity {
    private Long instanceId;
    private Long qualificationMinorSpecId;
    private String qualificationMinorSpecDescription;
    private String courseName;
    private Integer courseType;
    private Long courseId;
    private Integer contentType;
    private String syllabusAttachments;
    private WFTrainingCourse wfTrainingCourse;

    public WFTrainingCourseData() {
	wfTrainingCourse = new WFTrainingCourse();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	this.wfTrainingCourse.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_ID")
    public Long getQualificationMinorSpecId() {
	return qualificationMinorSpecId;
    }

    public void setQualificationMinorSpecId(Long qualificationMinorSpecId) {
	this.qualificationMinorSpecId = qualificationMinorSpecId;
	this.wfTrainingCourse.setQualificationMinorSpecId(qualificationMinorSpecId);
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_DESC")
    public String getQualificationMinorSpecDescription() {
	return qualificationMinorSpecDescription;
    }

    public void setQualificationMinorSpecDescription(String qualificationMinorSpecDescription) {
	this.qualificationMinorSpecDescription = qualificationMinorSpecDescription;
    }

    @Basic
    @Column(name = "COURSE_NAME")
    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
	this.wfTrainingCourse.setCourseName(courseName);
    }

    @Basic
    @Column(name = "COURSE_TYPE")
    public Integer getCourseType() {
	return courseType;
    }

    public void setCourseType(Integer courseType) {
	this.courseType = courseType;
	this.wfTrainingCourse.setCourseType(courseType);
    }

    @Basic
    @Column(name = "COURSE_ID")
    public Long getCourseId() {
	return courseId;
    }

    public void setCourseId(Long courseId) {
	this.courseId = courseId;
	this.wfTrainingCourse.setCourseId(courseId);
    }

    @Basic
    @Column(name = "CONTENT_TYPE")
    public Integer getContentType() {
	return contentType;
    }

    public void setContentType(Integer contentType) {
	this.contentType = contentType;
	this.wfTrainingCourse.setContentType(contentType);
    }

    @Basic
    @Column(name = "SYLLABUS_ATTACHMENTS")
    public String getSyllabusAttachments() {
	return syllabusAttachments;
    }

    public void setSyllabusAttachments(String syllabusAttachments) {
	this.syllabusAttachments = syllabusAttachments;
	this.wfTrainingCourse.setSyllabusAttachments(syllabusAttachments);
    }

    @Transient
    public WFTrainingCourse getWfTrainingCourse() {
	return wfTrainingCourse;
    }

    public void setWfTrainingCourse(WFTrainingCourse wfTrainingCourse) {
	this.wfTrainingCourse = wfTrainingCourse;
    }

}
