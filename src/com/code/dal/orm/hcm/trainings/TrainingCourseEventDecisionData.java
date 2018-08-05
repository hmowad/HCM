package com.code.dal.orm.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingCourseEventDecisionData_searchTrainingCourseEventDecisionData",
		query = " select d from TrainingCourseEventDecisionData d" +
			" where (:P_COURSE_EVENT_ID = -1 OR  d.courseEventId = :P_COURSE_EVENT_ID) " +
			" and (:P_TRANSACTION_TYPE_ID = -1 OR  d.transactionTypeId = :P_TRANSACTION_TYPE_ID) " +
			" and (:P_NEW_START_DATE_FLAG = -1 or to_date(:P_NEW_START_DATE, 'MI/MM/YYYY') = d.newStartDate) " +
			" and (:P_TRANS_START_DATE_FLAG = -1 or to_date(:P_TRANS_START_DATE, 'MI/MM/YYYY') = d.transStartDate) " +
			" and (:P_DECISION_DATE_FLAG = -1 or to_date(:P_DECISION_DATE, 'MI/MM/YYYY') = d.decisionDate) " +
			" order by d.id"),
	@NamedQuery(name = "hcm_trainingCourseEventDecisionData_searchTrainingCourseEventDecisionDataForInquiry",
		query = " select d from TrainingCourseEventDecisionData d" +
			" where (:P_COURSE_EVENT_ID = -1 OR  d.courseEventId = :P_COURSE_EVENT_ID) " +
			" and (:P_TRANSACTION_TYPE_ID = -1 OR  d.transactionTypeId = :P_TRANSACTION_TYPE_ID) " +
			" and (:P_TRANINING_YEAR_ID = -1 OR  d.trainingYearId = :P_TRANINING_YEAR_ID) " +
			" and (:P_TRANINING_UNIT_ID = -1 OR  d.trainingUnitId = :P_TRANINING_UNIT_ID) " +
			" and (:P_START_DATE_FROM_FLAG = -1 or d.transStartDate >= to_date(:P_START_DATE_FROM, 'MI/MM/YYYY') ) " +
			" and (:P_START_DATE_TO_FLAG = -1 or d.transStartDate <= to_date(:P_START_DATE_TO, 'MI/MM/YYYY'))" +
			" order by d.id")

})
@Entity
@Table(name = "HCM_VW_TRN_COURSES_EVENTS_DECS")
public class TrainingCourseEventDecisionData extends BaseEntity {
    private Long id;
    private Long courseEventId;
    private Long trainingYearId;
    private String trainingYearName;
    private Long trainingUnitId;
    private String trainingUnitName;
    private Long transactionTypeId;
    private Date transStartDate;
    private String transStartDateString;
    private Date transEndDate;
    private String transEndDateString;
    private Date newStartDate;
    private String newStartDateString;
    private Date newEndDate;
    private String newEndDateString;
    private String reasons;
    private String notes;
    private String attachments;
    private Integer eflag;
    private Integer migFlag;
    private Date decisionDate;
    private String decisionDateString;
    private String decisionNumber;
    private Long decisionRegionId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String internalCopies;
    private String externalCopies;
    private String allowance;
    private TrainingCourseEventDecision trainingCourseEventDecision;

    public TrainingCourseEventDecisionData() {
	trainingCourseEventDecision = new TrainingCourseEventDecision();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingCourseEventDecision.setId(id);
    }

    @Basic
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
	this.trainingCourseEventDecision.setCourseEventId(courseEventId);
    }

    @Basic
    @Column(name = "TRAINING_YEAR_NAME")
    public String getTrainingYearName() {
	return trainingYearName;
    }

    public void setTrainingYearName(String trainingYearName) {
	this.trainingYearName = trainingYearName;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_NAME")
    public String getTrainingUnitName() {
	return trainingUnitName;
    }

    public void setTrainingUnitName(String trainingUnitName) {
	this.trainingUnitName = trainingUnitName;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
	this.trainingCourseEventDecision.setTransactionTypeId(transactionTypeId);
    }

    @Basic
    @Column(name = "TRANS_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransStartDate() {
	return transStartDate;
    }

    public void setTransStartDate(Date transStartDate) {
	this.transStartDate = transStartDate;
	this.transStartDateString = HijriDateService.getHijriDateString(transStartDate);
	this.trainingCourseEventDecision.setTransStartDate(transStartDate);
    }

    @Transient
    public String getTransStartDateString() {
	return transStartDateString;
    }

    public void setTransStartDateString(String transStartDateString) {
	this.transStartDateString = transStartDateString;
	this.transStartDate = HijriDateService.getHijriDate(transStartDateString);
    }

    @Basic
    @Column(name = "TRANS_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransEndDate() {
	return transEndDate;
    }

    public void setTransEndDate(Date transEndDate) {
	this.transEndDate = transEndDate;
	this.transEndDateString = HijriDateService.getHijriDateString(transEndDate);
	this.trainingCourseEventDecision.setTransEndDate(transEndDate);
    }

    @Transient
    public String getTransEndDateString() {
	return transEndDateString;
    }

    public void setTransEndDateString(String transEndDateString) {
	this.transEndDateString = transEndDateString;
	this.transEndDate = HijriDateService.getHijriDate(transEndDateString);
    }

    @Basic
    @Column(name = "NEW_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewStartDate() {
	return newStartDate;
    }

    public void setNewStartDate(Date newStartDate) {
	this.newStartDate = newStartDate;
	this.newStartDateString = HijriDateService.getHijriDateString(newStartDate);
	this.trainingCourseEventDecision.setNewStartDate(newStartDate);
    }

    @Transient
    public String getNewStartDateString() {
	return newStartDateString;
    }

    public void setNewStartDateString(String newStartDateString) {
	this.newStartDateString = newStartDateString;
	this.newStartDate = HijriDateService.getHijriDate(newStartDateString);
    }

    @Basic
    @Column(name = "NEW_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewEndDate() {
	return newEndDate;
    }

    public void setNewEndDate(Date newEndDate) {
	this.newEndDate = newEndDate;
	this.newEndDateString = HijriDateService.getHijriDateString(newEndDate);
	this.trainingCourseEventDecision.setNewEndDate(newEndDate);
    }

    @Transient
    public String getNewEndDateString() {
	return newEndDateString;
    }

    public void setNewEndDateString(String newEndDateString) {
	this.newEndDateString = newEndDateString;
	this.newEndDate = HijriDateService.getHijriDate(newEndDateString);
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	this.trainingCourseEventDecision.setReasons(reasons);
    }

    @Basic
    @Column(name = "NOTES")
    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
	this.trainingCourseEventDecision.setNotes(notes);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.trainingCourseEventDecision.setAttachments(attachments);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
	this.trainingCourseEventDecision.setEflag(eflag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
	this.trainingCourseEventDecision.setMigFlag(migFlag);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
	this.trainingCourseEventDecision.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	this.trainingCourseEventDecision.setDecisionDate(decisionDate);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.trainingCourseEventDecision.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	this.trainingCourseEventDecision.setDecisionRegionId(decisionRegionId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	this.trainingCourseEventDecision.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	this.trainingCourseEventDecision.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	this.trainingCourseEventDecision.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	this.trainingCourseEventDecision.setExternalCopies(externalCopies);
    }

    @Transient
    public TrainingCourseEventDecision getTrainingCourseEventDecision() {
	return trainingCourseEventDecision;
    }

    public void setTrainingCourseEventDecision(TrainingCourseEventDecision trainingCourseEventDecision) {
	this.trainingCourseEventDecision = trainingCourseEventDecision;
    }

    @Basic
    @Column(name = "TRAINING_YEAR_ID")
    public Long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(Long trainingYearId) {
	this.trainingYearId = trainingYearId;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    @Basic
    @Column(name = "ALLOWANCE")
    public String getAllowance() {
	return allowance;
    }

    public void setAllowance(String allowance) {
	this.allowance = allowance;
	this.trainingCourseEventDecision.setAllowance(allowance);
    }
}
