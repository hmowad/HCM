package com.code.dal.orm.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_trainingCourseDecisionData_searchTrainingCourseDecision",
		query = "select c from TrainingCourseDecision c "
			+ "where (:P_COURSE_ID = -1 or c.courseId = :P_COURSE_ID)"
			+ "and (:P_TRANSACTION_TYPE_ID = -1 or c.transactionTypeId = :P_TRANSACTION_TYPE_ID)"
			+ "and (:P_START_DATE_FLAG = -1 or c.decisionDate >= to_date(:P_START_DATE, 'MI/MM/YYYY') )"
			+ "and (:P_END_DATE_FLAG = -1 or c.decisionDate <= to_date(:P_END_DATE, 'MI/MM/YYYY'))"
			+ "and (:P_DECISION_DATE_FLAG = -1 or c.decisionDate = to_date(:P_DECISION_DATE, 'MI/MM/YYYY'))")
})
@Entity
@Table(name = "HCM_TRN_COURSES_DECISIONS")
public class TrainingCourseDecision extends BaseEntity {
    private Long id;
    private Long transactionTypeId;
    private Long courseId;
    private String transactionCourseName;
    private Integer contentType;
    private Date decisionDate;
    private String decisionNumber;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String syllabusAttachments;

    @SequenceGenerator(name = "HCMTrainingQualSeq",
	    sequenceName = "HCM_TRN_QUAL_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingQualSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
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
    @Column(name = "TRANS_COURSE_NAME")
    public String getTransactionCourseName() {
	return transactionCourseName;
    }

    public void setTransactionCourseName(String transactionCourseName) {
	this.transactionCourseName = transactionCourseName;
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
    @Column(name = "DECISION_DATE")
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DESCISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
    }

    @Basic
    @Column(name = "ORIGINAL_DESCISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
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
