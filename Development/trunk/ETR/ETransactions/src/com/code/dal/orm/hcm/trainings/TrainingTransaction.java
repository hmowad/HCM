package com.code.dal.orm.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingTransaction_countConflictTrainingTransactionsDates",
		query = "select count(t.id) from TrainingTransaction t " +
			" where (:P_TRAINING_TYPE_IDS_FLAG = -1 or t.trainingTypeId in (:P_TRAINING_TYPE_IDS ))" +
			" and (:P_STATUS_IDS_FLAG = -1 or t.status in (:P_STATUS_IDS ))" +
			" and (t.employeeId = :P_EMPLOYEE_ID)" +
			" and (to_date(:P_STUDY_END_DATE, 'MI/MM/YYYY')>=t.studyStartDate" +
			" and to_date(:P_STUDY_START_DATE, 'MI/MM/YYYY')<=t.studyEndDate)" +
			" and (:P_EXCLUDED_ID = -1 or t.id <> :P_EXCLUDED_ID)"),

	@NamedQuery(name = "hcm_trainingTransaction_countConflictCoursesEventsDates",
		query = "select count(courseEvent.id) " +
			" from TrainingTransaction trainingTransaction , TrainingCourseEvent courseEvent" +
			" where courseEvent.id = trainingTransaction.courseEventId " +
			" and (:P_TRAINING_TYPE_IDS_FLAG = -1 or courseEvent.trainingTypeId in (:P_TRAINING_TYPE_IDS )) " +
			" and (:P_STATUS_IDS_FLAG = -1 or trainingTransaction.status in (:P_STATUS_IDS )) " +
			" and (trainingTransaction.employeeId = :P_EMPLOYEE_ID) " +
			" and (to_date(:P_ACTUAL_END_DATE, 'MI/MM/YYYY')>= courseEvent.actualStartDate" +
			" and to_date(:P_ACTUAL_START_DATE, 'MI/MM/YYYY')<=courseEvent.actualEndDate)" +
			" and (:P_EXCLUDED_IDS_FLAG = -1 or trainingTransaction.id not in (:P_EXCLUDED_IDS) )")
})
@Entity
@Table(name = "HCM_TRN_TRANSACTIONS")
public class TrainingTransaction extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long trainingTypeId;
    private Integer status;
    private Long employeeId;
    private Long replacementEmployeeId;
    private Long courseEventId;
    private String trainingPurpose;
    private String otherPurpose;
    private Integer reasonType;
    private String reasons;
    private Date trainingJoiningDate;
    private Date joiningDate;
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
    private Long graduationPlaceDetailId;
    private Long qualificationLevelId;
    private Long qualificationMinorSpecId;
    private String studySubject;
    private Date studyStartDate;
    private Date studyEndDate;
    private Integer studyMonthsCount;
    private Integer studyDaysCount;
    private Date studyGraduationDate;
    private Integer eflag;
    private Integer migFlag;
    private String attachments;

    @SequenceGenerator(name = "HCMTrainingSeq",
	    sequenceName = "HCM_TRN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
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
    @Column(name = "TRAINING_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTrainingJoiningDate() {
	return trainingJoiningDate;
    }

    public void setTrainingJoiningDate(Date trainingJoiningDate) {
	this.trainingJoiningDate = trainingJoiningDate;
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
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
    @Column(name = "STUDY_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyStartDate() {
	return studyStartDate;
    }

    public void setStudyStartDate(Date studyStartDate) {
	this.studyStartDate = studyStartDate;
    }

    @Basic
    @Column(name = "STUDY_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyEndDate() {
	return studyEndDate;
    }

    public void setStudyEndDate(Date studyEndDate) {
	this.studyEndDate = studyEndDate;
    }

    @Basic
    @Column(name = "STUDY_MONTHS_COUNT")
    public Integer getStudyMonthsCount() {
	return studyMonthsCount;
    }

    public void setStudyMonthsCount(Integer studyMonthsCount) {
	this.studyMonthsCount = studyMonthsCount;
    }

    @Basic
    @Column(name = "STUDY_DAYS_COUNT")
    public Integer getStudyDaysCount() {
	return studyDaysCount;
    }

    public void setStudyDaysCount(Integer studyDaysCount) {
	this.studyDaysCount = studyDaysCount;
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
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "trainingTypeId:" + trainingTypeId + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"employeeId:" + employeeId + AUDIT_SEPARATOR +
		"replacementEmployeeId:" + replacementEmployeeId + AUDIT_SEPARATOR +
		"courseEventId:" + courseEventId + AUDIT_SEPARATOR +
		"trainingPurpose:" + trainingPurpose + AUDIT_SEPARATOR +
		"otherPurpose:" + otherPurpose + AUDIT_SEPARATOR +
		"reasonType:" + reasonType + AUDIT_SEPARATOR +
		"reasons:" + reasons + AUDIT_SEPARATOR +
		"trainingJoiningDate:" + trainingJoiningDate + AUDIT_SEPARATOR +
		"joiningDate:" + joiningDate + AUDIT_SEPARATOR +
		"successFlag:" + successFlag + AUDIT_SEPARATOR +
		"successRanking:" + successRanking + AUDIT_SEPARATOR +
		"successRankingDesc:" + successRankingDesc + AUDIT_SEPARATOR +
		"qualificationGradePercentage:" + qualificationGradePercentage + AUDIT_SEPARATOR +
		"qualificationGrade:" + qualificationGrade + AUDIT_SEPARATOR +
		"attendanceGradePercentage:" + attendanceGradePercentage + AUDIT_SEPARATOR +
		"attendanceGrade:" + attendanceGrade + AUDIT_SEPARATOR +
		"behaviorGradePercentage:" + behaviorGradePercentage + AUDIT_SEPARATOR +
		"behaviorGrade:" + behaviorGrade + AUDIT_SEPARATOR +
		"fundSource:" + fundSource + AUDIT_SEPARATOR +
		"graduationPlaceDetailId:" + graduationPlaceDetailId + AUDIT_SEPARATOR +
		"qualificationLevelId:" + qualificationLevelId + AUDIT_SEPARATOR +
		"qualificationMinorSpecId:" + qualificationMinorSpecId + AUDIT_SEPARATOR +
		"studySubject:" + studySubject + AUDIT_SEPARATOR +
		"studyStartDate:" + studyStartDate + AUDIT_SEPARATOR +
		"studyEndDate:" + studyEndDate + AUDIT_SEPARATOR +
		"studyMonthsCount:" + studyMonthsCount + AUDIT_SEPARATOR +
		"studyDaysCount:" + studyDaysCount + AUDIT_SEPARATOR +
		"studyGraduationDate:" + studyGraduationDate + AUDIT_SEPARATOR +
		"eflag:" + eflag + AUDIT_SEPARATOR +
		"migFlag:" + migFlag + AUDIT_SEPARATOR +
		"attachments:" + attachments + AUDIT_SEPARATOR;
    }
}
