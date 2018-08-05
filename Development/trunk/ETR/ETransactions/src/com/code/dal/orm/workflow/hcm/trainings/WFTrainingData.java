package com.code.dal.orm.workflow.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "wf_trainingData_searchWFTrainingData",
		query = "select t from WFTrainingData t , WFInstance i " +
			"where (t.instanceId = i.id)" +
			" and (:P_INSTANCE_ID =-1 or t.instanceId =:P_INSTANCE_ID )" +
			" and (:P_STATUS = -1 or i.status = :P_STATUS)" +
			" and (:P_COURSE_ID =-1 or t.courseId = :P_COURSE_ID)" +
			" and (:P_COURSE_EVENT_ID = -1 or t.courseEventId = :P_COURSE_EVENT_ID)" +
			" and (:P_EXTERNAL_PARTY_ID =-1 or t.externalPartyId = :P_EXTERNAL_PARTY_ID)" +
			" and (:P_GRADUATION_PLACE_ID = -1 or t.graduationPlaceId = :P_GRADUATION_PLACE_ID)" +
			" and (:P_GRADUATION_PLACE_DETAIL_ID = -1 or t.graduationPlaceDetailId = :P_GRADUATION_PLACE_DETAIL_ID)" +
			" and (:P_QUAL_MINOR_SPEC_ID = -1 or t.qualificationMinorSpecId = :P_QUAL_MINOR_SPEC_ID) " +
			" and (:P_QUAL_MAJOR_SPEC_ID = -1 or t.qualificationMajorSpecId = :P_QUAL_MAJOR_SPEC_ID)" +
			" and (:P_EMPS_IDS_FLAG =-1 or (t.employeeId in(:P_EMPS_IDS ) or t.replacementEmployeeId in (:P_EMPS_IDS)))" +
			" and (:P_EXCLUDED_INSTANCE_ID = -1 or t.instanceId <> :P_EXCLUDED_INSTANCE_ID)"),

	@NamedQuery(name = "wf_trainingData_searchWFTrainingTasks",
		query = " select trn, t, i, p.processName, requester, " +
			" case when t.originalId <> t.assigneeId then " +
			" (delegator.firstName || ' ' || delegator.secondName || ' ' || delegator.thirdName || case when delegator.thirdName is null then '' else ' ' end || delegator.lastName) " +
			" else null end " +
			" from WFTrainingData trn , WFTask t, WFInstance i, WFProcess p, EmployeeData requester, Employee delegator " +
			" where trn.instanceId = t.instanceId " +
			"   and trn.instanceId = i.id " +
			"   and i.processId = p.id " +
			"   and i.requesterId = requester.id " +
			"   and t.originalId = delegator.id " +
			"   and t.action is null " +
			"   and t.assigneeId = :P_ASSIGNEE_ID " +
			"   and t.assigneeWfRole in (:P_ASSIGNEE_WF_ROLES) " +
			"   order by t.taskId ")
})
@Entity
@Table(name = "ETR_VW_WF_TRAININGS")
public class WFTrainingData extends BaseEntity {
    private Long instanceId;
    private Long employeeId;
    private String employeeName;
    private String employeeRankDesc;
    private String employeeNumber;
    private String employeeJobName;
    private String employeePhysicalUnitFullname;
    private Long categoryId;
    private Long trainingTypeId;
    private Long replacementEmployeeId;
    private Long courseEventId;
    private String eventCourseName;
    private String eventTrainingUnitName;
    private String eventExternalPartyDesc;
    private String eventExternalPartyAddress;
    private Date eventActualStartDate;
    private String eventActualStartDateString;
    private Date eventActualEndDate;
    private String eventActualEndDateString;
    private String eventTrainingYearName;
    private Long courseId;
    private String courseName;
    private Long trainingUnitId;
    private String trainingUnitName;
    private Long externalPartyId;
    private String externalPartyDesc;
    private String externalPartyAddress;
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
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Integer monthsCount;
    private Integer daysCount;
    private Long graduationPlaceDetailId;
    private String graduationPlaceDetailDesc;
    private Long graduationPlaceId;
    private String graduationPlaceDesc;
    private String graduationPlaceCountryName;
    private String graduationPlaceDetailAddress;
    private Long qualificationLevelId;
    private String qualificationLevelDesc;
    private Long qualificationMinorSpecId;
    private String qualificationMinorSpecDesc;
    private Long qualificationMajorSpecId;
    private String studySubject;
    private String ministryDecisionNumber;
    private Date ministryDecisionDate;
    private String ministryDecisionDateString;
    private String ministryReportNumber;
    private Date ministryReportDate;
    private String ministryReportDateString;
    private Date studyGraduationDate;
    private String studyGraduationDateString;
    private String attachments;
    private Long basedOnTrainingTransactionId;
    private String internalCopies;
    private String externalCopies;
    private WFTraining wfTraining;

    public WFTrainingData() {
	wfTraining = new WFTraining();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	this.wfTraining.setInstanceId(instanceId);
    }

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	this.wfTraining.setEmployeeId(employeeId);
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
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "TRAINING_TYPE_ID")
    public Long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(Long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
	this.wfTraining.setTrainingTypeId(trainingTypeId);
    }

    @Basic
    @Column(name = "REPLACEMENT_EMPLOYEE_ID")
    @XmlElement(nillable = true)
    @XmlTransient
    public Long getReplacementEmployeeId() {
	return replacementEmployeeId;
    }

    public void setReplacementEmployeeId(Long replacementEmployeeId) {
	this.replacementEmployeeId = replacementEmployeeId;
	this.wfTraining.setReplacementEmployeeId(replacementEmployeeId);
    }

    @Basic
    @Column(name = "COURSE_EVENT_ID")
    @XmlElement(nillable = true)
    public Long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
	this.wfTraining.setCourseEventId(courseEventId);
    }

    @Basic
    @Column(name = "EVENT_COURSE_NAME")
    @XmlElement(nillable = true)
    public String getEventCourseName() {
	return eventCourseName;
    }

    public void setEventCourseName(String eventCourseName) {
	this.eventCourseName = eventCourseName;
    }

    @Basic
    @Column(name = "EVENT_TRAINING_UNIT_NAME")
    @XmlElement(nillable = true)
    public String getEventTrainingUnitName() {
	return eventTrainingUnitName;
    }

    public void setEventTrainingUnitName(String eventTrainingUnitName) {
	this.eventTrainingUnitName = eventTrainingUnitName;
    }

    @Basic
    @Column(name = "EVENT_EXTERNAL_PARTY_DESC")
    @XmlElement(nillable = true)
    public String getEventExternalPartyDesc() {
	return eventExternalPartyDesc;
    }

    public void setEventExternalPartyDesc(String eventExternalPartyDesc) {
	this.eventExternalPartyDesc = eventExternalPartyDesc;
    }

    @Basic
    @Column(name = "COURSE_ID")
    @XmlElement(nillable = true)
    public Long getCourseId() {
	return courseId;
    }

    public void setCourseId(Long courseId) {
	this.courseId = courseId;
	this.wfTraining.setCourseId(courseId);
    }

    @Basic
    @Column(name = "COURSE_NAME")
    @XmlElement(nillable = true)
    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    @XmlElement(nillable = true)
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
	this.wfTraining.setTrainingUnitId(trainingUnitId);
    }

    @Basic
    @Column(name = "TRAINING_UNIT_NAME")
    @XmlElement(nillable = true)
    public String getTrainingUnitName() {
	return trainingUnitName;
    }

    public void setTrainingUnitName(String trainingUnitName) {
	this.trainingUnitName = trainingUnitName;
    }

    @Basic
    @Column(name = "EVENT_EXTERNAL_PARTY_ADDRESS")
    @XmlElement(nillable = true)
    public String getEventExternalPartyAddress() {
	return eventExternalPartyAddress;
    }

    public void setEventExternalPartyAddress(String eventExternalPartyAddress) {
	this.eventExternalPartyAddress = eventExternalPartyAddress;
    }

    @Basic
    @Column(name = "EVENT_ACTUAL_START_DATE")
    @XmlTransient
    public Date getEventActualStartDate() {
	return eventActualStartDate;
    }

    public void setEventActualStartDate(Date eventActualStartDate) {
	this.eventActualStartDate = eventActualStartDate;
	this.eventActualStartDateString = HijriDateService.getHijriDateString(eventActualStartDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getEventActualStartDateString() {
	return eventActualStartDateString;
    }

    public void setEventActualStartDateString(String eventActualStartDateString) {
	this.eventActualStartDateString = eventActualStartDateString;
    }

    @Basic
    @Column(name = "EVENT_ACTUAL_END_DATE")
    @XmlTransient
    public Date getEventActualEndDate() {
	return eventActualEndDate;
    }

    public void setEventActualEndDate(Date eventActualEndDate) {
	this.eventActualEndDate = eventActualEndDate;
	this.eventActualEndDateString = HijriDateService.getHijriDateString(eventActualEndDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getEventActualEndDateString() {
	return eventActualEndDateString;
    }

    public void setEventActualEndDateString(String eventActualEndDateString) {
	this.eventActualEndDateString = eventActualEndDateString;
    }

    @Basic
    @Column(name = "EVENT_TRAINING_YEAR_NAME")
    @XmlElement(nillable = true)
    public String getEventTrainingYearName() {
	return eventTrainingYearName;
    }

    public void setEventTrainingYearName(String eventTrainingYearName) {
	this.eventTrainingYearName = eventTrainingYearName;
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_ID")
    @XmlElement(nillable = true)
    public Long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(Long externalPartyId) {
	this.externalPartyId = externalPartyId;
	this.wfTraining.setExternalPartyId(externalPartyId);
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_DESC")
    @XmlElement(nillable = true)
    public String getExternalPartyDesc() {
	return externalPartyDesc;
    }

    public void setExternalPartyDesc(String externalPartyDesc) {
	this.externalPartyDesc = externalPartyDesc;
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_ADDRESS")
    @XmlElement(nillable = true)
    public String getExternalPartyAddress() {
	return externalPartyAddress;
    }

    public void setExternalPartyAddress(String externalPartyAddress) {
	this.externalPartyAddress = externalPartyAddress;
    }

    @Basic
    @Column(name = "TRAINING_PURPOSE")
    @XmlElement(nillable = true)
    @XmlTransient
    public String getTrainingPurpose() {
	return trainingPurpose;
    }

    public void setTrainingPurpose(String trainingPurpose) {
	this.trainingPurpose = trainingPurpose;
	this.wfTraining.setTrainingPurpose(trainingPurpose);
    }

    @Basic
    @Column(name = "OTHER_PURPOSE")
    @XmlElement(nillable = true)
    @XmlTransient
    public String getOtherPurpose() {
	return otherPurpose;
    }

    public void setOtherPurpose(String otherPurpose) {
	this.otherPurpose = otherPurpose;
	this.wfTraining.setOtherPurpose(otherPurpose);
    }

    @Basic
    @Column(name = "REASON_TYPE")
    @XmlElement(nillable = true)
    @XmlTransient
    public Integer getReasonType() {
	return reasonType;
    }

    public void setReasonType(Integer reasonType) {
	this.reasonType = reasonType;
	this.wfTraining.setReasonType(reasonType);
    }

    @Basic
    @Column(name = "REASONS")
    @XmlElement(nillable = true)
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	this.wfTraining.setReasons(reasons);
    }

    @Basic
    @Column(name = "SUCCESS_FLAG")
    @XmlElement(nillable = true)
    public Integer getSuccessFlag() {
	return successFlag;
    }

    public void setSuccessFlag(Integer successFlag) {
	this.successFlag = successFlag;
	this.wfTraining.setSuccessFlag(successFlag);
    }

    @Basic
    @Column(name = "SUCCESS_RANKING")
    @XmlElement(nillable = true)
    public Integer getSuccessRanking() {
	return successRanking;
    }

    public void setSuccessRanking(Integer successRanking) {
	this.successRanking = successRanking;
	this.wfTraining.setSuccessRanking(successRanking);
    }

    @Basic
    @Column(name = "SUCCESS_RANKING_DESC")
    @XmlElement(nillable = true)
    public String getSuccessRankingDesc() {
	return successRankingDesc;
    }

    public void setSuccessRankingDesc(String successRankingDesc) {
	this.successRankingDesc = successRankingDesc;
	this.wfTraining.setSuccessRankingDesc(successRankingDesc);
    }

    @Basic
    @Column(name = "QUAL_GRADE_PERCENTAGE")
    @XmlElement(nillable = true)
    public Double getQualificationGradePercentage() {
	return qualificationGradePercentage;
    }

    public void setQualificationGradePercentage(Double qualificationGradePercentage) {
	this.qualificationGradePercentage = qualificationGradePercentage;
	this.wfTraining.setQualificationGradePercentage(qualificationGradePercentage);
    }

    @Basic
    @Column(name = "QUAL_GRADE")
    @XmlElement(nillable = true)
    public Integer getQualificationGrade() {
	return qualificationGrade;
    }

    public void setQualificationGrade(Integer qualificationGrade) {
	this.qualificationGrade = qualificationGrade;
	this.wfTraining.setQualificationGrade(qualificationGrade);
    }

    @Basic
    @Column(name = "ATTENDANCE_GRADE_PERCENTAGE")
    @XmlElement(nillable = true)
    public Double getAttendanceGradePercentage() {
	return attendanceGradePercentage;
    }

    public void setAttendanceGradePercentage(Double attendanceGradePercentage) {
	this.attendanceGradePercentage = attendanceGradePercentage;
	this.wfTraining.setAttendanceGradePercentage(attendanceGradePercentage);
    }

    @Basic
    @Column(name = "ATTENDANCE_GRADE")
    @XmlElement(nillable = true)
    public Integer getAttendanceGrade() {
	return attendanceGrade;
    }

    public void setAttendanceGrade(Integer attendanceGrade) {
	this.attendanceGrade = attendanceGrade;
	this.wfTraining.setAttendanceGrade(attendanceGrade);
    }

    @Basic
    @Column(name = "BEHAVIOR_GRADE_PERCENTAGE")
    @XmlElement(nillable = true)
    public Double getBehaviorGradePercentage() {
	return behaviorGradePercentage;
    }

    public void setBehaviorGradePercentage(Double behaviorGradePercentage) {
	this.behaviorGradePercentage = behaviorGradePercentage;
	this.wfTraining.setBehaviorGradePercentage(behaviorGradePercentage);
    }

    @Basic
    @Column(name = "BEHAVIOR_GRADE")
    @XmlElement(nillable = true)
    public Integer getBehaviorGrade() {
	return behaviorGrade;
    }

    public void setBehaviorGrade(Integer behaviorGrade) {
	this.behaviorGrade = behaviorGrade;
	this.wfTraining.setBehaviorGrade(behaviorGrade);
    }

    @Basic
    @Column(name = "FUND_SOURCE")
    @XmlElement(nillable = true)
    public Integer getFundSource() {
	return fundSource;
    }

    public void setFundSource(Integer fundSource) {
	this.fundSource = fundSource;
	this.wfTraining.setFundSource(fundSource);
    }

    @Basic
    @Column(name = "SERIAL")
    @XmlElement(nillable = true)
    public Integer getSerial() {
	return serial;
    }

    public void setSerial(Integer serial) {
	this.serial = serial;
	this.wfTraining.setSerial(serial);
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
	this.startDateString = HijriDateService.getHijriDateString(startDate);
	this.wfTraining.setStartDate(startDate);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	this.startDate = HijriDateService.getHijriDate(startDateString);
	this.wfTraining.setStartDate(startDate);
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
	this.wfTraining.setEndDate(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
	this.wfTraining.setEndDate(endDate);
    }

    @Basic
    @Column(name = "MONTHS_COUNT")
    @XmlElement(nillable = true)
    public Integer getMonthsCount() {
	return monthsCount;
    }

    public void setMonthsCount(Integer monthsCount) {
	this.monthsCount = monthsCount;
	this.wfTraining.setMonthsCount(monthsCount);
    }

    @Basic
    @Column(name = "DAYS_COUNT")
    @XmlElement(nillable = true)
    public Integer getDaysCount() {
	return daysCount;
    }

    public void setDaysCount(Integer daysCount) {
	this.daysCount = daysCount;
	this.wfTraining.setDaysCount(daysCount);
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DETAIL_ID")
    @XmlElement(nillable = true)
    public Long getGraduationPlaceDetailId() {
	return graduationPlaceDetailId;
    }

    public void setGraduationPlaceDetailId(Long graduationPlaceDetailId) {
	this.graduationPlaceDetailId = graduationPlaceDetailId;
	this.wfTraining.setGraduationPlaceDetailId(graduationPlaceDetailId);
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DETAIL_DESC")
    @XmlElement(nillable = true)
    public String getGraduationPlaceDetailDesc() {
	return graduationPlaceDetailDesc;
    }

    public void setGraduationPlaceDetailDesc(String graduationPlaceDetailDesc) {
	this.graduationPlaceDetailDesc = graduationPlaceDetailDesc;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_ID")
    @XmlElement(nillable = true)
    public Long getGraduationPlaceId() {
	return graduationPlaceId;
    }

    public void setGraduationPlaceId(Long graduationPlaceId) {
	this.graduationPlaceId = graduationPlaceId;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DESC")
    @XmlElement(nillable = true)
    public String getGraduationPlaceDesc() {
	return graduationPlaceDesc;
    }

    public void setGraduationPlaceDesc(String graduationPlaceDesc) {
	this.graduationPlaceDesc = graduationPlaceDesc;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_COUNTRY_NAME")
    @XmlElement(nillable = true)
    public String getGraduationPlaceCountryName() {
	return graduationPlaceCountryName;
    }

    public void setGraduationPlaceCountryName(String graduationPlaceCountryName) {
	this.graduationPlaceCountryName = graduationPlaceCountryName;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DTL_ADDRESS")
    @XmlElement(nillable = true)
    public String getGraduationPlaceDetailAddress() {
	return graduationPlaceDetailAddress;
    }

    public void setGraduationPlaceDetailAddress(String graduationPlaceDetailAddress) {
	this.graduationPlaceDetailAddress = graduationPlaceDetailAddress;
    }

    @Basic
    @Column(name = "QUALIFICATION_LEVEL_ID")
    @XmlElement(nillable = true)
    public Long getQualificationLevelId() {
	return qualificationLevelId;
    }

    public void setQualificationLevelId(Long qualificationLevelId) {
	this.qualificationLevelId = qualificationLevelId;
	this.wfTraining.setQualificationLevelId(qualificationLevelId);
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_ID")
    @XmlElement(nillable = true)
    public Long getQualificationMinorSpecId() {
	return qualificationMinorSpecId;
    }

    public void setQualificationMinorSpecId(Long qualificationMinorSpecId) {
	this.qualificationMinorSpecId = qualificationMinorSpecId;
	this.wfTraining.setQualificationMinorSpecId(qualificationMinorSpecId);
    }

    @Basic
    @Column(name = "QUALIFICATION_MINOR_SPEC_DESC")
    @XmlElement(nillable = true)
    public String getQualificationMinorSpecDesc() {
	return qualificationMinorSpecDesc;
    }

    public void setQualificationMinorSpecDesc(String qualificationMinorSpecDesc) {
	this.qualificationMinorSpecDesc = qualificationMinorSpecDesc;
    }

    @Basic
    @Column(name = "QUALIFICATION_MAJOR_SPEC_ID")
    @XmlElement(nillable = true)
    public Long getQualificationMajorSpecId() {
	return qualificationMajorSpecId;
    }

    public void setQualificationMajorSpecId(Long qualificationMajorSpecId) {
	this.qualificationMajorSpecId = qualificationMajorSpecId;
    }

    @Basic
    @Column(name = "STUDY_SUBJECT")
    @XmlElement(nillable = true)
    public String getStudySubject() {
	return studySubject;
    }

    public void setStudySubject(String studySubject) {
	this.studySubject = studySubject;
	this.wfTraining.setStudySubject(studySubject);
    }

    @Basic
    @Column(name = "MINISTRY_DECISION_NUMBER")
    @XmlElement(nillable = true)
    @XmlTransient
    public String getMinistryDecisionNumber() {
	return ministryDecisionNumber;
    }

    public void setMinistryDecisionNumber(String ministryDecisionNumber) {
	this.ministryDecisionNumber = ministryDecisionNumber;
	this.wfTraining.setMinistryDecisionNumber(ministryDecisionNumber);
    }

    @Basic
    @Column(name = "MINISTRY_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(nillable = true)
    @XmlTransient
    public Date getMinistryDecisionDate() {
	return ministryDecisionDate;
    }

    public void setMinistryDecisionDate(Date ministryDecisionDate) {
	this.ministryDecisionDate = ministryDecisionDate;
	this.ministryDecisionDateString = HijriDateService.getHijriDateString(ministryDecisionDate);
	this.wfTraining.setMinistryDecisionDate(ministryDecisionDate);
    }

    @Transient
    @XmlTransient
    public String getMinistryDecisionDateString() {
	return ministryDecisionDateString;
    }

    public void setMinistryDecisionDateString(String ministryDecisionDateString) {
	this.ministryDecisionDateString = ministryDecisionDateString;
	this.ministryDecisionDate = HijriDateService.getHijriDate(ministryDecisionDateString);
	this.wfTraining.setMinistryDecisionDate(ministryDecisionDate);
    }

    @Basic
    @Column(name = "MINISTRY_REPORT_NUMBER")
    @XmlElement(nillable = true)
    @XmlTransient
    public String getMinistryReportNumber() {
	return ministryReportNumber;
    }

    public void setMinistryReportNumber(String ministryReportNumber) {
	this.ministryReportNumber = ministryReportNumber;
	this.wfTraining.setMinistryReportNumber(ministryReportNumber);
    }

    @Basic
    @Column(name = "MINISTRY_REPORT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(nillable = true)
    @XmlTransient
    public Date getMinistryReportDate() {
	return ministryReportDate;
    }

    public void setMinistryReportDate(Date ministryReportDate) {
	this.ministryReportDate = ministryReportDate;
	this.ministryReportDateString = HijriDateService.getHijriDateString(ministryReportDate);
	this.wfTraining.setMinistryReportDate(ministryReportDate);
    }

    @Transient
    @XmlTransient
    public String getMinistryReportDateString() {
	return ministryReportDateString;
    }

    public void setMinistryReportDateString(String ministryReportDateString) {
	this.ministryReportDateString = ministryReportDateString;
	this.ministryReportDate = HijriDateService.getHijriDate(ministryReportDateString);
	this.wfTraining.setMinistryReportDate(ministryReportDate);
    }

    @Basic
    @Column(name = "STUDY_GRADUATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getStudyGraduationDate() {
	return studyGraduationDate;
    }

    public void setStudyGraduationDate(Date studyGraduationDate) {
	this.studyGraduationDate = studyGraduationDate;
	this.studyGraduationDateString = HijriDateService.getHijriDateString(studyGraduationDate);
	this.wfTraining.setStudyGraduationDate(studyGraduationDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getStudyGraduationDateString() {
	return studyGraduationDateString;
    }

    public void setStudyGraduationDateString(String studyGraduationDateString) {
	this.studyGraduationDateString = studyGraduationDateString;
	this.studyGraduationDate = HijriDateService.getHijriDate(studyGraduationDateString);
	this.wfTraining.setStudyGraduationDate(studyGraduationDate);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    @XmlElement(nillable = true)
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.wfTraining.setAttachments(attachments);
    }

    @Basic
    @Column(name = "BASED_ON_TRN_TRANSACTION_ID")
    @XmlElement(nillable = true)
    public Long getBasedOnTrainingTransactionId() {
	return basedOnTrainingTransactionId;
    }

    public void setBasedOnTrainingTransactionId(Long basedOnTrainingTransactionId) {
	this.basedOnTrainingTransactionId = basedOnTrainingTransactionId;
	this.wfTraining.setBasedOnTrainingTransactionId(basedOnTrainingTransactionId);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    @XmlElement(nillable = true)
    @XmlTransient
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	this.wfTraining.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    @XmlElement(nillable = true)
    @XmlTransient
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	this.wfTraining.setExternalCopies(externalCopies);
    }

    @Transient
    @XmlTransient
    public WFTraining getWfTraining() {
	return wfTraining;
    }

    public void setWfTraining(WFTraining wfTraining) {
	this.wfTraining = wfTraining;
    }

    @Basic
    @Column(name = "QUALIFICATION_LEVEL_DESC")
    @XmlElement(nillable = true)
    public String getQualificationLevelDesc() {
	return qualificationLevelDesc;
    }

    public void setQualificationLevelDesc(String qualificationLevelDesc) {
	this.qualificationLevelDesc = qualificationLevelDesc;
    }
}
