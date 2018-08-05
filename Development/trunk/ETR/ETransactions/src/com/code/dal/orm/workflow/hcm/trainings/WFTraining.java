package com.code.dal.orm.workflow.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "WF_TRAININGS")
public class WFTraining extends BaseEntity {
    private Long instanceId;
    private Long employeeId;
    private Long trainingTypeId;
    private Long replacementEmployeeId;
    private Long courseEventId;
    private Long courseId;
    private Long trainingUnitId;
    private Long externalPartyId;
    private String trainingPurpose;
    private String otherPurpose;
    private Integer reasonType;
    private String reasons;
    private Integer successFlag;
    private Integer successRanking;
    private String successRankingDesc;
    private Double qualificationGradePercentage;
    private Integer qualificationGrade;
    private Double attendanceGradePercentage;
    private Integer attendanceGrade;
    private Double behaviorGradePercentage;
    private Integer behaviorGrade;
    private Integer fundSource;
    private Integer serial;
    private Date startDate;
    private Date endDate;
    private Integer monthsCount;
    private Integer daysCount;
    private Long graduationPlaceDetailId;
    private Long qualificationLevelId;
    private Long qualificationMinorSpecId;
    private String studySubject;
    private String ministryDecisionNumber;
    private Date ministryDecisionDate;
    private String ministryReportNumber;
    private Date ministryReportDate;
    private Date studyGraduationDate;
    private String attachments;
    private Long basedOnTrainingTransactionId;
    private String internalCopies;
    private String externalCopies;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "TRAINING_TYPE_ID")
    public Long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(Long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
    }

    @Basic
    @Column(name = "REPLACEMENT_EMPLOYEE_ID")
    public Long getReplacementEmployeeId() {
	return replacementEmployeeId;
    }

    public void setReplacementEmployeeId(Long replacementEmployeeId) {
	this.replacementEmployeeId = replacementEmployeeId;
    }

    @Basic
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
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
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_ID")
    public Long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(Long externalPartyId) {
	this.externalPartyId = externalPartyId;
    }

    @Basic
    @Column(name = "TRAINING_PURPOSE")
    public String getTrainingPurpose() {
	return trainingPurpose;
    }

    public void setTrainingPurpose(String trainingPurpose) {
	this.trainingPurpose = trainingPurpose;
    }

    @Basic
    @Column(name = "OTHER_PURPOSE")
    public String getOtherPurpose() {
	return otherPurpose;
    }

    public void setOtherPurpose(String otherPurpose) {
	this.otherPurpose = otherPurpose;
    }

    @Basic
    @Column(name = "REASON_TYPE")
    public Integer getReasonType() {
	return reasonType;
    }

    public void setReasonType(Integer reasonType) {
	this.reasonType = reasonType;
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    @Basic
    @Column(name = "SUCCESS_FLAG")
    public Integer getSuccessFlag() {
	return successFlag;
    }

    public void setSuccessFlag(Integer successFlag) {
	this.successFlag = successFlag;
    }

    @Basic
    @Column(name = "SUCCESS_RANKING")
    public Integer getSuccessRanking() {
	return successRanking;
    }

    public void setSuccessRanking(Integer successRanking) {
	this.successRanking = successRanking;
    }

    @Basic
    @Column(name = "SUCCESS_RANKING_DESC")
    public String getSuccessRankingDesc() {
	return successRankingDesc;
    }

    public void setSuccessRankingDesc(String successRankingDesc) {
	this.successRankingDesc = successRankingDesc;
    }

    @Basic
    @Column(name = "QUAL_GRADE_PERCENTAGE")
    public Double getQualificationGradePercentage() {
	return qualificationGradePercentage;
    }

    public void setQualificationGradePercentage(Double qualificationGradePercentage) {
	this.qualificationGradePercentage = qualificationGradePercentage;
    }

    @Basic
    @Column(name = "QUAL_GRADE")
    public Integer getQualificationGrade() {
	return qualificationGrade;
    }

    public void setQualificationGrade(Integer qualificationGrade) {
	this.qualificationGrade = qualificationGrade;
    }

    @Basic
    @Column(name = "ATTENDANCE_GRADE_PERCENTAGE")
    public Double getAttendanceGradePercentage() {
	return attendanceGradePercentage;
    }

    public void setAttendanceGradePercentage(Double attendanceGradePercentage) {
	this.attendanceGradePercentage = attendanceGradePercentage;
    }

    @Basic
    @Column(name = "ATTENDANCE_GRADE")
    public Integer getAttendanceGrade() {
	return attendanceGrade;
    }

    public void setAttendanceGrade(Integer attendanceGrade) {
	this.attendanceGrade = attendanceGrade;
    }

    @Basic
    @Column(name = "BEHAVIOR_GRADE_PERCENTAGE")
    public Double getBehaviorGradePercentage() {
	return behaviorGradePercentage;
    }

    public void setBehaviorGradePercentage(Double behaviorGradePercentage) {
	this.behaviorGradePercentage = behaviorGradePercentage;
    }

    @Basic
    @Column(name = "BEHAVIOR_GRADE")
    public Integer getBehaviorGrade() {
	return behaviorGrade;
    }

    public void setBehaviorGrade(Integer behaviorGrade) {
	this.behaviorGrade = behaviorGrade;
    }

    @Basic
    @Column(name = "FUND_SOURCE")
    public Integer getFundSource() {
	return fundSource;
    }

    public void setFundSource(Integer fundSource) {
	this.fundSource = fundSource;
    }

    @Basic
    @Column(name = "SERIAL")
    public Integer getSerial() {
	return serial;
    }

    public void setSerial(Integer serial) {
	this.serial = serial;
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    @Basic
    @Column(name = "MONTHS_COUNT")
    public Integer getMonthsCount() {
	return monthsCount;
    }

    public void setMonthsCount(Integer monthsCount) {
	this.monthsCount = monthsCount;
    }

    @Basic
    @Column(name = "DAYS_COUNT")
    public Integer getDaysCount() {
	return daysCount;
    }

    public void setDaysCount(Integer daysCount) {
	this.daysCount = daysCount;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DETAIL_ID")
    public Long getGraduationPlaceDetailId() {
	return graduationPlaceDetailId;
    }

    public void setGraduationPlaceDetailId(Long graduationPlaceDetailId) {
	this.graduationPlaceDetailId = graduationPlaceDetailId;
    }

    @Basic
    @Column(name = "QUALIFICATION_LEVEL_ID")
    public Long getQualificationLevelId() {
	return qualificationLevelId;
    }

    public void setQualificationLevelId(Long qualificationLevelId) {
	this.qualificationLevelId = qualificationLevelId;
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
    @Column(name = "STUDY_SUBJECT")
    public String getStudySubject() {
	return studySubject;
    }

    public void setStudySubject(String studySubject) {
	this.studySubject = studySubject;
    }

    @Basic
    @Column(name = "MINISTRY_DECISION_NUMBER")
    public String getMinistryDecisionNumber() {
	return ministryDecisionNumber;
    }

    public void setMinistryDecisionNumber(String ministryDecisionNumber) {
	this.ministryDecisionNumber = ministryDecisionNumber;
    }

    @Basic
    @Column(name = "MINISTRY_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryDecisionDate() {
	return ministryDecisionDate;
    }

    public void setMinistryDecisionDate(Date ministryDecisionDate) {
	this.ministryDecisionDate = ministryDecisionDate;
    }

    @Basic
    @Column(name = "MINISTRY_REPORT_NUMBER")
    public String getMinistryReportNumber() {
	return ministryReportNumber;
    }

    public void setMinistryReportNumber(String ministryReportNumber) {
	this.ministryReportNumber = ministryReportNumber;
    }

    @Basic
    @Column(name = "MINISTRY_REPORT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryReportDate() {
	return ministryReportDate;
    }

    public void setMinistryReportDate(Date ministryReportDate) {
	this.ministryReportDate = ministryReportDate;
    }

    @Basic
    @Column(name = "STUDY_GRADUATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyGraduationDate() {
	return studyGraduationDate;
    }

    public void setStudyGraduationDate(Date studyGraduationDate) {
	this.studyGraduationDate = studyGraduationDate;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
    }

    @Basic
    @Column(name = "BASED_ON_TRN_TRANSACTION_ID")
    public Long getBasedOnTrainingTransactionId() {
	return basedOnTrainingTransactionId;
    }

    public void setBasedOnTrainingTransactionId(Long basedOnTrainingTransactionId) {
	this.basedOnTrainingTransactionId = basedOnTrainingTransactionId;
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }
}
