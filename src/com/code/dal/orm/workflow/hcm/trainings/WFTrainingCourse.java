package com.code.dal.orm.workflow.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_wfTrainingCourse_getRunningWFTrainingCourses",
		query = "select c from WFTrainingCourse c , WFInstance i " +
			"where (c.instanceId = i.id)" +
			" and ( c.courseId = :P_COURSE_ID)" +
			" and ( i.status = 1)")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "WF_TRAINING_COURSES")
public class WFTrainingCourse extends BaseEntity {
    private Long instanceId;
    private Long qualificationMinorSpecId;
    private String courseName;
    private Integer courseType;
    private Long courseId;
    private Integer contentType;
    private String syllabusAttachments;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_ID")
    public Long getQualificationMinorSpecId() {
	return qualificationMinorSpecId;
    }

    public void setQualificationMinorSpecId(Long qualificationMinorSpecId) {
	this.qualificationMinorSpecId = qualificationMinorSpecId;
    }

    @Basic
    @Column(name = "COURSE_NAME")
    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    @Basic
    @Column(name = "COURSE_TYPE")
    public Integer getCourseType() {
	return courseType;
    }

    public void setCourseType(Integer courseType) {
	this.courseType = courseType;
    }

    @Basic
    @Column(name = "COURSE_ID")
    public Long getCourseId() {
	return courseId;
    }

    public void setCourseId(Long courseId) {
	this.courseId = courseId;
    }

    @Basic
    @Column(name = "CONTENT_TYPE")
    public Integer getContentType() {
	return contentType;
    }

    public void setContentType(Integer contentType) {
	this.contentType = contentType;
    }

    @Basic
    @Column(name = "SYLLABUS_ATTACHMENTS")
    public String getSyllabusAttachments() {
	return syllabusAttachments;
    }

    public void setSyllabusAttachments(String syllabusAttachments) {
	this.syllabusAttachments = syllabusAttachments;
    }

}
