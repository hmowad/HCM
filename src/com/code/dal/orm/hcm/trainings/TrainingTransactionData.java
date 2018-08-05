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
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingTransactionData_searchTrainingTransactionsData",
		query = "select t from TrainingTransactionData t " +
			"where (:P_IDS_FLAG = -1 OR t.id in (:P_IDS))" +
			"AND (:P_TRAINING_TYPE_IDS_FLAG = -1 OR t.trainingTypeId in (:P_TRAINING_TYPE_IDS ))" +
			"AND (:P_STATUS_IDS_FLAG = -1 OR t.status in (:P_STATUS_IDS ))" +
			"AND (:P_EMPLOYEE_ID =-1 OR t.employeeId = :P_EMPLOYEE_ID)" +
			"AND (:P_EMPLOYEE_PHYSICAL_REGION_ID =-1 OR t.employeePhysicalRegionId = :P_EMPLOYEE_PHYSICAL_REGION_ID)" +
			"AND (:P_EMP_PHYSC_UNIT_NAME ='-1' OR t.employeePhysicalUnitFullname LIKE :P_EMP_PHYSC_UNIT_NAME)" +
			"AND (:P_GRADUATION_PLACE_DETAIL_ID =-1 OR t.graduationPlaceDetailId=:P_GRADUATION_PLACE_DETAIL_ID)" +
			"AND (:P_GRADUATION_PLACE_ID =-1 OR t.graduationPlaceId =:P_GRADUATION_PLACE_ID)" +
			"AND (:P_QUAL_MINOR_SPEC_ID = -1 OR t.qualificationMinorSpecId = :P_QUAL_MINOR_SPEC_ID) " +
			"AND (:P_QUAL_MAJOR_SPEC_ID = -1 OR t.qualificationMajorSpecId = :P_QUAL_MAJOR_SPEC_ID)" +
			"AND (:P_SUCCESS_FLAG = -1 OR t.successFlag = :P_SUCCESS_FLAG)" +
			"AND (:P_COURSE_EVENT_ID = -1 OR t.courseEventId = :P_COURSE_EVENT_ID)" +
			"AND (:P_DATE_FROM_FLAG = -1 OR (NVL(t.actualStartDate, t.studyStartDate) >= to_date(:P_DATE_FROM, 'MI/MM/YYYY'))) " +
			"AND (:P_DATE_TO_FLAG = -1 OR (NVL(t.actualEndDate, t.studyEndDate) <= to_date(:P_DATE_TO, 'MI/MM/YYYY')))" +
			"AND (:P_CATEGORY_IDS_FLAG = -1 OR t.employeeCategoryId in (:P_CATEGORY_IDS ))" +
			"AND (:P_TRAINING_COURSE_NAME ='-1' OR t.courseName LIKE :P_TRAINING_COURSE_NAME)" +
			" ORDER BY t.actualStartDate desc, t.studyStartDate desc") // order by actual start date for EmployeeTrainingFile data view, order by study start date for scholarship ext, term, cancel decisions
})

@Entity
@Table(name = "HCM_VW_TRN_TRANSACTIONS")
public class TrainingTransactionData extends BaseEntity {
    private Long id;
    private Long trainingTypeId;
    private Integer status;
    private Long employeeId;
    private String employeeName;
    private String employeeRankDesc;
    private String employeeNumber;
    private String employeeJobName;
    private Long employeePhysicalRegionId;
    private String employeePhysicalUnitFullname;
    private String employeePhysicalUnitHKey;
    private String employeeSocialId;
    private Long employeeCategoryId;
    private Long employeeSequenceNumber;
    private Long replacementEmployeeId;
    private Long courseEventId;
    private String courseName;
    private Long trainingUnitId;
    private String trainingUnitName;
    private String trainingYearName;
    private String externalPartyDesc;
    private String externalPartyAddress;
    private Date actualStartDate;
    private String actualStartDateString;
    private Date actualEndDate;
    private String actualEndDateString;
    private String trainingPurpose;
    private String otherPurpose;
    private Integer reasonType;
    private String reasons;
    private Date trainingJoiningDate;
    private String trainingJoiningDateString;
    private Date joiningDate;
    private String joiningDateString;
    private Integer successFlag;
    private Boolean successFlagBoolean;
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
    private Long graduationPlaceId;
    private String graduationPlaceDetailDesc;
    private String graduationPlaceDetailAddress;
    private String graduationPlaceDesc;
    private String graduationPlaceCountryName;
    private Long qualificationLevelId;
    private String qualificationLevelDesc;
    private Long qualificationMinorSpecId;
    private String qualificationMinorSpecDesc;
    private Long qualificationMajorSpecId;
    private String studySubject;
    private Date studyStartDate;
    private String studyStartDateString;
    private Date studyEndDate;
    private String studyEndDateString;
    private Integer studyMonthsCount;
    private Integer studyDaysCount;
    private Date studyGraduationDate;
    private String studyGraduationDateString;
    private Integer eflag;
    private Integer migFlag;
    private String attachments;
    private TrainingTransaction trainingTransaction;

    public TrainingTransactionData() {
	trainingTransaction = new TrainingTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingTransaction.setId(id);
    }

    @Basic
    @Column(name = "TRAINING_TYPE_ID")
    public Long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(Long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
	this.trainingTransaction.setTrainingTypeId(trainingTypeId);
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
	this.trainingTransaction.setStatus(status);
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	this.trainingTransaction.setEmployeeId(employeeId);
    }

    @Basic
    @Column(name = "EMPLOYEE_NAME")
    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    @Basic
    @Column(name = "EMPLOYEE_RANK_DESC")
    public String getEmployeeRankDesc() {
	return employeeRankDesc;
    }

    public void setEmployeeRankDesc(String employeeRankDesc) {
	this.employeeRankDesc = employeeRankDesc;
    }

    @Basic
    @Column(name = "EMPLOYEE_NUMBER")
    public String getEmployeeNumber() {
	return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
	this.employeeNumber = employeeNumber;
    }

    @Basic
    @Column(name = "EMPLOYEE_JOB_NAME")
    public String getEmployeeJobName() {
	return employeeJobName;
    }

    public void setEmployeeJobName(String employeeJobName) {
	this.employeeJobName = employeeJobName;
    }

    @Basic
    @Column(name = "EMPLOYEE_PHYCL_UNIT_FULL_NAME")
    public String getEmployeePhysicalUnitFullname() {
	return employeePhysicalUnitFullname;
    }

    public void setEmployeePhysicalUnitFullname(String employeePhysicalUnitFullname) {
	this.employeePhysicalUnitFullname = employeePhysicalUnitFullname;
    }

    @Basic
    @Column(name = "EMPLOYEE_PHYSICAL_REGION_ID")

    public Long getEmployeePhysicalRegionId() {
	return employeePhysicalRegionId;
    }

    public void setEmployeePhysicalRegionId(Long employeePhysicalRegionId) {
	this.employeePhysicalRegionId = employeePhysicalRegionId;
    }

    @Basic
    @Column(name = "EMPLOYEE_PHYCL_UNIT_HKEY")
    public String getEmployeePhysicalUnitHKey() {
	return employeePhysicalUnitHKey;
    }

    public void setEmployeePhysicalUnitHKey(String employeePhysicalUnitHKey) {
	this.employeePhysicalUnitHKey = employeePhysicalUnitHKey;
    }

    @Basic
    @Column(name = "EMPLOYEE_SOCIAL_ID")
    public String getEmployeeSocialId() {
	return employeeSocialId;
    }

    public void setEmployeeSocialId(String employeeSocialId) {
	this.employeeSocialId = employeeSocialId;
    }

    @Basic
    @Column(name = "EMPLOYEE_CATEGORY_ID")
    public Long getEmployeeCategoryId() {
	return employeeCategoryId;
    }

    public void setEmployeeCategoryId(Long categoryId) {
	this.employeeCategoryId = categoryId;
    }

    @Basic
    @Column(name = "SEQUENCE_NUMBER")
    public Long getEmployeeSequenceNumber() {
	return employeeSequenceNumber;
    }

    public void setEmployeeSequenceNumber(Long employeeSequenceNumber) {
	this.employeeSequenceNumber = employeeSequenceNumber;
    }

    @Basic
    @Column(name = "REPLACEMENT_EMPLOYEE_ID")
    public Long getReplacementEmployeeId() {
	return replacementEmployeeId;
    }

    public void setReplacementEmployeeId(Long replacementEmployeeId) {
	this.replacementEmployeeId = replacementEmployeeId;
	this.trainingTransaction.setReplacementEmployeeId(replacementEmployeeId);
    }

    @Basic
    @Column(name = "COURSE_EVENT_ID")
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
	this.trainingTransaction.setCourseEventId(courseEventId);
    }

    @Basic
    @Column(name = "TRAINING_PURPOSE")
    public String getTrainingPurpose() {
	return trainingPurpose;
    }

    public void setTrainingPurpose(String trainingPurpose) {
	this.trainingPurpose = trainingPurpose;
	this.trainingTransaction.setTrainingPurpose(trainingPurpose);
    }

    @Basic
    @Column(name = "OTHER_PURPOSE")
    public String getOtherPurpose() {
	return otherPurpose;
    }

    public void setOtherPurpose(String otherPurpose) {
	this.otherPurpose = otherPurpose;
	this.trainingTransaction.setOtherPurpose(otherPurpose);
    }

    @Basic
    @Column(name = "REASON_TYPE")
    public Integer getReasonType() {
	return reasonType;
    }

    public void setReasonType(Integer reasonType) {
	this.reasonType = reasonType;
	this.trainingTransaction.setReasonType(reasonType);
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	this.trainingTransaction.setReasons(reasons);
    }

    @Basic
    @Column(name = "TRAINING_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTrainingJoiningDate() {
	return trainingJoiningDate;
    }

    public void setTrainingJoiningDate(Date trainingJoiningDate) {
	this.trainingJoiningDate = trainingJoiningDate;
	this.trainingJoiningDateString = HijriDateService.getHijriDateString(trainingJoiningDate);
	this.trainingTransaction.setTrainingJoiningDate(trainingJoiningDate);
    }

    @Transient
    public String getTrainingJoiningDateString() {
	return trainingJoiningDateString;
    }

    public void setTrainingJoiningDateString(String trainingJoiningDateString) {
	this.trainingJoiningDateString = trainingJoiningDateString;
	this.trainingJoiningDate = HijriDateService.getHijriDate(trainingJoiningDateString);
	this.trainingTransaction.setTrainingJoiningDate(trainingJoiningDate);
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
	this.joiningDateString = HijriDateService.getHijriDateString(joiningDate);
	this.trainingTransaction.setJoiningDate(joiningDate);
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
	this.trainingTransaction.setJoiningDate(joiningDate);
    }

    @Basic
    @Column(name = "SUCCESS_FLAG")
    public Integer getSuccessFlag() {
	return successFlag;
    }

    public void setSuccessFlag(Integer successFlag) {
	this.successFlag = successFlag;
	if (this.successFlag == null || this.successFlag == FlagsEnum.OFF.getCode()) {
	    this.successFlagBoolean = false;
	} else {
	    this.successFlagBoolean = true;
	}
	this.trainingTransaction.setSuccessFlag(successFlag);
    }

    @Transient
    public Boolean getSuccessFlagBoolean() {
	return successFlagBoolean;
    }

    public void setSuccessFlagBoolean(Boolean successFlagBoolean) {
	this.successFlagBoolean = successFlagBoolean;
	if (this.successFlagBoolean == false) {
	    setSuccessFlag(FlagsEnum.OFF.getCode());
	} else {
	    setSuccessFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "SUCCESS_RANKING")
    public Integer getSuccessRanking() {
	return successRanking;
    }

    public void setSuccessRanking(Integer successRanking) {
	this.successRanking = successRanking;
	this.trainingTransaction.setSuccessRanking(successRanking);
    }

    @Basic
    @Column(name = "SUCCESS_RANKING_DESC")
    public String getSuccessRankingDesc() {
	return successRankingDesc;
    }

    public void setSuccessRankingDesc(String successRankingDesc) {
	this.successRankingDesc = successRankingDesc;
	this.trainingTransaction.setSuccessRankingDesc(successRankingDesc);
    }

    @Basic
    @Column(name = "QUAL_GRADE_PERCENTAGE")
    public Double getQualificationGradePercentage() {
	return qualificationGradePercentage;
    }

    public void setQualificationGradePercentage(Double qualificationGradePercentage) {
	this.qualificationGradePercentage = qualificationGradePercentage;
	this.trainingTransaction.setQualificationGradePercentage(qualificationGradePercentage);
    }

    @Basic
    @Column(name = "QUAL_GRADE")
    public Integer getQualificationGrade() {
	return qualificationGrade;
    }

    public void setQualificationGrade(Integer qualificationGrade) {
	this.qualificationGrade = qualificationGrade;
	this.trainingTransaction.setQualificationGrade(qualificationGrade);
    }

    @Basic
    @Column(name = "ATTENDANCE_GRADE_PERCENTAGE")
    public Double getAttendanceGradePercentage() {
	return attendanceGradePercentage;
    }

    public void setAttendanceGradePercentage(Double attendanceGradePercentage) {
	this.attendanceGradePercentage = attendanceGradePercentage;
	this.trainingTransaction.setAttendanceGradePercentage(attendanceGradePercentage);
    }

    @Basic
    @Column(name = "ATTENDANCE_GRADE")
    public Integer getAttendanceGrade() {
	return attendanceGrade;
    }

    public void setAttendanceGrade(Integer attendanceGrade) {
	this.attendanceGrade = attendanceGrade;
	this.trainingTransaction.setAttendanceGrade(attendanceGrade);
    }

    @Basic
    @Column(name = "BEHAVIOR_GRADE_PERCENTAGE")
    public Double getBehaviorGradePercentage() {
	return behaviorGradePercentage;
    }

    public void setBehaviorGradePercentage(Double behaviorGradePercentage) {
	this.behaviorGradePercentage = behaviorGradePercentage;
	this.trainingTransaction.setBehaviorGradePercentage(behaviorGradePercentage);
    }

    @Basic
    @Column(name = "BEHAVIOR_GRADE")
    public Integer getBehaviorGrade() {
	return behaviorGrade;
    }

    public void setBehaviorGrade(Integer behaviorGrade) {
	this.behaviorGrade = behaviorGrade;
	this.trainingTransaction.setBehaviorGrade(behaviorGrade);
    }

    @Basic
    @Column(name = "FUND_SOURCE")
    public Integer getFundSource() {
	return fundSource;
    }

    public void setFundSource(Integer fundSource) {
	this.fundSource = fundSource;
	this.trainingTransaction.setFundSource(fundSource);
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DETAIL_ID")
    public Long getGraduationPlaceDetailId() {
	return graduationPlaceDetailId;
    }

    public void setGraduationPlaceDetailId(Long graduationPlaceDetailId) {
	this.graduationPlaceDetailId = graduationPlaceDetailId;
	this.trainingTransaction.setGraduationPlaceDetailId(graduationPlaceDetailId);
    }

    @Basic
    @Column(name = "GRADUATION_DETAIL_ADDRESS")
    public String getGraduationPlaceDetailAddress() {
	return graduationPlaceDetailAddress;
    }

    public void setGraduationPlaceDetailAddress(String graduationPlaceDetailAddress) {
	this.graduationPlaceDetailAddress = graduationPlaceDetailAddress;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_ID")
    public Long getGraduationPlaceId() {
	return graduationPlaceId;
    }

    public void setGraduationPlaceId(Long graduationPlaceId) {
	this.graduationPlaceId = graduationPlaceId;
    }

    @Basic
    @Column(name = "QUALIFICATION_LEVEL_ID")
    public Long getQualificationLevelId() {
	return qualificationLevelId;
    }

    public void setQualificationLevelId(Long qualificationLevelId) {
	this.qualificationLevelId = qualificationLevelId;
	this.trainingTransaction.setQualificationLevelId(qualificationLevelId);
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_ID")
    public Long getQualificationMinorSpecId() {
	return qualificationMinorSpecId;
    }

    public void setQualificationMinorSpecId(Long qualificationMinorSpecId) {
	this.qualificationMinorSpecId = qualificationMinorSpecId;
	this.trainingTransaction.setQualificationMinorSpecId(qualificationMinorSpecId);
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_DESC")
    public String getQualificationMinorSpecDesc() {
	return qualificationMinorSpecDesc;
    }

    public void setQualificationMinorSpecDesc(String qualificationMinorSpecDesc) {
	this.qualificationMinorSpecDesc = qualificationMinorSpecDesc;
    }

    @Basic
    @Column(name = "QUALIFICATION_MAJOR_SPEC_ID")
    public Long getQualificationMajorSpecId() {
	return qualificationMajorSpecId;
    }

    public void setQualificationMajorSpecId(Long qualificationMajorSpecId) {
	this.qualificationMajorSpecId = qualificationMajorSpecId;
    }

    @Basic
    @Column(name = "STUDY_SUBJECT")
    public String getStudySubject() {
	return studySubject;
    }

    public void setStudySubject(String studySubject) {
	this.studySubject = studySubject;
	this.trainingTransaction.setStudySubject(studySubject);
    }

    @Basic
    @Column(name = "STUDY_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyStartDate() {
	return studyStartDate;
    }

    public void setStudyStartDate(Date studyStartDate) {
	this.studyStartDate = studyStartDate;
	this.studyStartDateString = HijriDateService.getHijriDateString(studyStartDate);
	this.trainingTransaction.setStudyStartDate(studyStartDate);
    }

    @Transient
    public String getStudyStartDateString() {
	return studyStartDateString;
    }

    public void setStudyStartDateString(String studyStartDateString) {
	this.studyStartDateString = studyStartDateString;
	this.studyStartDate = HijriDateService.getHijriDate(studyStartDateString);
	this.trainingTransaction.setStudyStartDate(studyStartDate);
    }

    @Basic
    @Column(name = "STUDY_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyEndDate() {
	return studyEndDate;
    }

    public void setStudyEndDate(Date studyEndDate) {
	this.studyEndDate = studyEndDate;
	this.studyEndDateString = HijriDateService.getHijriDateString(studyEndDate);
	this.trainingTransaction.setStudyEndDate(studyEndDate);
    }

    @Transient
    public String getStudyEndDateString() {
	return studyEndDateString;
    }

    public void setStudyEndDateString(String studyEndDateString) {
	this.studyEndDateString = studyEndDateString;
	this.studyEndDate = HijriDateService.getHijriDate(studyEndDateString);
	this.trainingTransaction.setStudyEndDate(studyEndDate);
    }

    @Basic
    @Column(name = "STUDY_MONTHS_COUNT")
    public Integer getStudyMonthsCount() {
	return studyMonthsCount;
    }

    public void setStudyMonthsCount(Integer studyMonthsCount) {
	this.studyMonthsCount = studyMonthsCount;
	this.trainingTransaction.setStudyMonthsCount(studyMonthsCount);
    }

    @Basic
    @Column(name = "STUDY_DAYS_COUNT")
    public Integer getStudyDaysCount() {
	return studyDaysCount;
    }

    public void setStudyDaysCount(Integer studyDaysCount) {
	this.studyDaysCount = studyDaysCount;
	this.trainingTransaction.setStudyDaysCount(studyDaysCount);
    }

    @Basic
    @Column(name = "STUDY_GRADUATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStudyGraduationDate() {
	return studyGraduationDate;
    }

    public void setStudyGraduationDate(Date studyGraduationDate) {
	this.studyGraduationDate = studyGraduationDate;
	this.studyGraduationDateString = HijriDateService.getHijriDateString(studyGraduationDate);
	this.trainingTransaction.setStudyGraduationDate(studyGraduationDate);
    }

    @Transient
    public String getStudyGraduationDateString() {
	return studyGraduationDateString;
    }

    public void setStudyGraduationDateString(String studyGraduationDateString) {
	this.studyGraduationDateString = studyGraduationDateString;
	this.studyGraduationDate = HijriDateService.getHijriDate(studyGraduationDateString);
	this.trainingTransaction.setStudyGraduationDate(studyGraduationDate);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
	this.trainingTransaction.setEflag(eflag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
	this.trainingTransaction.setMigFlag(migFlag);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.trainingTransaction.setAttachments(attachments);
    }

    @Transient
    public TrainingTransaction getTrainingTransaction() {
	return trainingTransaction;
    }

    public void setTrainingTransaction(TrainingTransaction trainingTransaction) {
	this.trainingTransaction = trainingTransaction;
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
    @Column(name = "TRAINING_UNIT_NAME")
    public String getTrainingUnitName() {
	return trainingUnitName;
    }

    public void setTrainingUnitName(String trainingUnitName) {
	this.trainingUnitName = trainingUnitName;
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
    @Column(name = "TRAINING_YEAR_NAME")
    public String getTrainingYearName() {
	return trainingYearName;
    }

    public void setTrainingYearName(String trainingYearName) {
	this.trainingYearName = trainingYearName;
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_DESC")
    public String getExternalPartyDesc() {
	return externalPartyDesc;
    }

    public void setExternalPartyDesc(String externalPartyDesc) {
	this.externalPartyDesc = externalPartyDesc;
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_ADDRESS")
    public String getExternalPartyAddress() {
	return externalPartyAddress;
    }

    public void setExternalPartyAddress(String externalPartyAddress) {
	this.externalPartyAddress = externalPartyAddress;
    }

    @Basic
    @Column(name = "ACTUAL_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getActualStartDate() {
	return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
	this.actualStartDate = actualStartDate;
	this.actualStartDateString = HijriDateService.getHijriDateString(actualStartDate);
    }

    @Transient
    public String getActualStartDateString() {
	return actualStartDateString;
    }

    public void setActualStartDateString(String actualStartDateString) {
	this.actualStartDateString = actualStartDateString;
	this.actualEndDate = HijriDateService.getHijriDate(actualStartDateString);
    }

    @Basic
    @Column(name = "ACTUAL_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getActualEndDate() {
	return actualEndDate;

    }

    public void setActualEndDate(Date actualEndDate) {
	this.actualEndDate = actualEndDate;
	this.actualEndDateString = HijriDateService.getHijriDateString(actualEndDate);
    }

    @Transient
    public String getActualEndDateString() {
	return actualEndDateString;
    }

    public void setActualEndDateString(String actualEndDateString) {
	this.actualEndDateString = actualEndDateString;
	this.actualEndDate = HijriDateService.getHijriDate(actualEndDateString);
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DETAIL_DESC")
    public String getGraduationPlaceDetailDesc() {
	return graduationPlaceDetailDesc;
    }

    public void setGraduationPlaceDetailDesc(String graduationPlaceDetailDesc) {
	this.graduationPlaceDetailDesc = graduationPlaceDetailDesc;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DESC")
    public String getGraduationPlaceDesc() {
	return graduationPlaceDesc;
    }

    public void setGraduationPlaceDesc(String graduationPlaceDesc) {
	this.graduationPlaceDesc = graduationPlaceDesc;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_COUNTRY_NAME")
    public String getGraduationPlaceCountryName() {
	return graduationPlaceCountryName;
    }

    public void setGraduationPlaceCountryName(String graduationPlaceCountryName) {
	this.graduationPlaceCountryName = graduationPlaceCountryName;
    }

    @Basic
    @Column(name = "QUALIFICATION_LEVEL_DESC")
    public String getQualificationLevelDesc() {
	return qualificationLevelDesc;
    }

    public void setQualificationLevelDesc(String qualificationLevelDesc) {
	this.qualificationLevelDesc = qualificationLevelDesc;
    }
}
