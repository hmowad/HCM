package com.code.dal.orm.hcm.trainings;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingCourseEventData_searchTrainingCourseEventData",
		query = " select e from TrainingCourseEventData e " +
			" where (:P_ID = -1 or e.id = :P_ID )" +
			" and (:P_COURSE_ID = -1 or e.courseId = :P_COURSE_ID )" +
			" and (:P_TRAINING_TYPE_ID = -1 or e.trainingTypeId = :P_TRAINING_TYPE_ID )" +
			" and (:P_SERIAL = -1 or e.serial = :P_SERIAL )" +
			" and (:P_TRAINING_UNIT_ID = -1 or e.trainingUnitId = :P_TRAINING_UNIT_ID )" +
			" and (:P_TRAINING_YEAR_ID = -1 or e.trainingYearId = :P_TRAINING_YEAR_ID ) " +
			" and (:P_COURSE_NAME ='-1' or e.courseName like :P_COURSE_NAME) " +
			" and (:P_STATUSES_IDS_FLAG = -1 or e.status in (:P_STATUSES_IDS)) " +
			" and (:P_TRAINING_YEAR_STATUS_IDS_FLAG = -1 or e.trainingYearStatus in (:P_TRAINING_YEAR_STATUS_IDS))" +
			" and (:P_TRAINING_EXTERNAL_PARTY_ID = -1 or e.externalPartyId = :P_TRAINING_EXTERNAL_PARTY_ID )" +
			" and (:P_TRAINING_EXTERNAL_PARTY_DESC = '-1' or e.externalPartyDesc like :P_TRAINING_EXTERNAL_PARTY_DESC )" +
			" and (:P_START_DATE_FLAG = -1 or to_date(:P_START_DATE, 'MI/MM/YYYY') = e.actualStartDate) " +
			" and (:P_END_DATE_FLAG = -1 or to_date(:P_END_DATE, 'MI/MM/YYYY') = e.actualEndDate)" +
			" and (:P_START_DATE_FROM_FLAG = -1 or e.actualStartDate >= to_date(:P_START_DATE_FROM, 'MI/MM/YYYY') ) " +
			" and (:P_START_DATE_TO_FLAG = -1 or e.actualStartDate <= to_date(:P_START_DATE_TO, 'MI/MM/YYYY'))" +
			" and (:P_END_DATE_FROM_FLAG= -1 or e.actualEndDate >= to_date(:P_END_DATE_FROM,'MI/MM/YYYY'))" +
			" and (:P_E_FLAG = -1 or e.eflag = :P_E_FLAG )" +
			" order by e.actualStartDate,e.plannedStartDate,e.courseId,e.serial"),
	@NamedQuery(name = "hcm_trainingCourseEventData_searchTrainingCourseEventDataForSerialGeneration",
		query = " select e from TrainingCourseEventData e " +
			" where (e.trainingYearId = :P_TRAINING_YEAR_ID )" +
			" and (:P_TRAINING_UNIT_ID = -1 or e.trainingUnitId = :P_TRAINING_UNIT_ID)" +
			" order by e.trainingUnitId,e.courseId,e.plannedStartDate,e.serial"),
	@NamedQuery(name = "hcm_trainingCourseEventData_searchTrainingCourseEventDataForNomination",
		query = " select e from TrainingCourseEventData e " +
			" where e.id in (select t.courseEventId from TrainingTransaction t where "
			+ " (:P_EMPLOYEE_ID = -1 or t.employeeId = :P_EMPLOYEE_ID) "
			+ " and (:P_STATUSES_IDS_FLAG = -1 or t.status in (:P_STATUSES_IDS)) "
			+ " and (:P_TRAINING_TYPE_ID = -1 or t.trainingTypeId = :P_TRAINING_TYPE_ID))"
			+ " and (:P_TRAINING_UNIT_REGION_ID = -1 or e.trainingUnitRegionId = :P_TRAINING_UNIT_REGION_ID )"
			+ " order by e.actualStartDate,e.plannedStartDate desc"),
	@NamedQuery(name = "hcm_trainingCourseEventData_getCourseEventsDataNotHavingAllocation",
		query = " select e1.courseName ,e1.trainingUnitName from TrainingCourseEventData e1 " +
			" where e1.trainingYearId = :P_TRAINING_YEAR_ID " +
			" and e1.id not in " +
			"(select distinct e2.id from TrainingCourseEventAllocation a, TrainingCourseEvent e2 " +
			"where e2.id = a.courseEventId " +
			"and e2.trainingYearId = e1.trainingYearId " +
			"and a.allocationCount is not null or TRIM(a.allocationDesc) is not null )"),
	@NamedQuery(name = "hcm_trainingCourseEventData_searchMilitaryTrainingCourseEventDataForBrowsing",
		query = " select e from TrainingCourseEventData e " +
			" where ((:P_TRAINING_TYPE_ID = -1 and e.trainingTypeId in (1,2)) or e.trainingTypeId = :P_TRAINING_TYPE_ID )" +
			" and (:P_TRAINING_UNIT_PARTY_DESC = '-1' or " +
			"(:P_TRAINING_TYPE_ID = -1 and (e.trainingUnitName like :P_TRAINING_UNIT_PARTY_DESC or e.externalPartyDesc like :P_TRAINING_UNIT_PARTY_DESC )) or" +
			"(:P_TRAINING_TYPE_ID = 1 and e.trainingUnitName like :P_TRAINING_UNIT_PARTY_DESC ) or" +
			"(:P_TRAINING_TYPE_ID = 2 and e.externalPartyDesc like :P_TRAINING_UNIT_PARTY_DESC )" +
			") " +
			" and (:P_COUNTRY_NAME ='-1' or e.countryName like :P_COUNTRY_NAME) " +
			" and (:P_COURSE_NAME ='-1' or e.courseName like :P_COURSE_NAME) " +
			" and (:P_START_DATE_FROM_FLAG = -1 or e.actualStartDate >= to_date(:P_START_DATE_FROM, 'MI/MM/YYYY') ) " +
			" and (:P_START_DATE_TO_FLAG = -1 or e.actualStartDate <= to_date(:P_START_DATE_TO, 'MI/MM/YYYY'))" +
			" order by e.actualStartDate,e.plannedStartDate,e.courseId,e.serial")
})

@Entity
@Table(name = "HCM_VW_TRN_COURSES_EVENTS")
public class TrainingCourseEventData extends BaseEntity {
    private Long id;
    private Long courseId;
    private String courseName;
    private Long trainingTypeId;
    private String trainingYearName;
    private Long trainingYearId;
    private Integer trainingYearStatus;
    private Long trainingUnitId;
    private String trainingUnitName;
    private Long trainingUnitRegionId;
    private Long externalPartyId;
    private String externalPartyDesc;
    private String countryName;
    private String externalPartyAddress;
    private Integer serial;
    private Integer weeksCount;
    private Integer candidatesMin;
    private Integer candidatesMax;
    private String prerquisites;
    private Date plannedStartDate;
    private String plannedStartDateString;
    private Date plannedEndDate;
    private String plannedEndDateString;
    private Date actualStartDate;
    private String actualStartDateString;
    private Date actualEndDate;
    private String actualEndDateString;
    private Integer status;
    private Integer migFlag;
    private Integer eflag;
    private String attachments;
    private String reasons;
    private Integer autoGeneratedSerialFlag;
    private TrainingCourseEvent trainingCourseEvent;

    public TrainingCourseEventData() {
	trainingCourseEvent = new TrainingCourseEvent();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingCourseEvent.setId(id);
    }

    @Basic
    @Column(name = "COURSE_ID")
    public Long getCourseId() {
	return courseId;
    }

    public void setCourseId(Long courseId) {
	this.courseId = courseId;
	this.trainingCourseEvent.setCourseId(courseId);
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
    @Column(name = "TRAINING_TYPE_ID")
    public Long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(Long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
	this.trainingCourseEvent.setTrainingTypeId(trainingTypeId);
    }

    @Basic
    @Column(name = "TRAINING_YEAR_ID")
    public Long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(Long trainingYearId) {
	this.trainingYearId = trainingYearId;
	this.trainingCourseEvent.setTrainingYearId(trainingYearId);
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
    @Column(name = "TRAINING_YEAR_STATUS")
    public Integer getTrainingYearStatus() {
	return trainingYearStatus;
    }

    public void setTrainingYearStatus(Integer trainingYearStatus) {
	this.trainingYearStatus = trainingYearStatus;
    }

    @Basic
    @Column(name = "TRAINING_UNIT_ID")
    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
	this.trainingCourseEvent.setTrainingUnitId(trainingUnitId);
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
    @Column(name = "TRAINING_UNIT_REGION_ID")
    public Long getTrainingUnitRegionId() {
	return trainingUnitRegionId;
    }

    public void setTrainingUnitRegionId(Long trainingUnitRegionId) {
	this.trainingUnitRegionId = trainingUnitRegionId;
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_ID")
    public Long getExternalPartyId() {
	return externalPartyId;
    }

    public void setExternalPartyId(Long externalPartyId) {
	this.externalPartyId = externalPartyId;
	this.trainingCourseEvent.setExternalPartyId(externalPartyId);
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
    @Column(name = "COUNTRY_NAME")
    public String getCountryName() {
	return countryName;
    }

    public void setCountryName(String countryName) {
	this.countryName = countryName;
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
    @Column(name = "SERIAL")
    public Integer getSerial() {
	return serial;
    }

    public void setSerial(Integer serial) {
	this.serial = serial;
	this.trainingCourseEvent.setSerial(serial);
    }

    @Basic
    @Column(name = "WEEKS_COUNT")
    public Integer getWeeksCount() {
	return weeksCount;
    }

    public void setWeeksCount(Integer weeksCount) {
	this.weeksCount = weeksCount;
	this.trainingCourseEvent.setWeeksCount(weeksCount);
    }

    @Basic
    @Column(name = "CANDIDATES_MIN")
    public Integer getCandidatesMin() {
	return candidatesMin;
    }

    public void setCandidatesMin(Integer candidatesMin) {
	this.candidatesMin = candidatesMin;
	this.trainingCourseEvent.setCandidatesMin(candidatesMin);
    }

    @Basic
    @Column(name = "CANDIDATES_MAX")
    public Integer getCandidatesMax() {
	return candidatesMax;
    }

    public void setCandidatesMax(Integer candidatesMax) {
	this.candidatesMax = candidatesMax;
	this.trainingCourseEvent.setCandidatesMax(candidatesMax);
    }

    @Basic
    @Column(name = "PRERQUISITES")
    public String getPrerquisites() {
	return prerquisites;
    }

    public void setPrerquisites(String prerquisites) {
	this.prerquisites = prerquisites;
	this.trainingCourseEvent.setPrerquisites(prerquisites);
    }

    @Basic
    @Column(name = "PLANNED_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPlannedStartDate() {
	return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
	this.plannedStartDate = plannedStartDate;
	this.plannedStartDateString = HijriDateService.getHijriDateString(plannedStartDate);
	this.trainingCourseEvent.setPlannedStartDate(plannedStartDate);
    }

    @Transient
    public String getPlannedStartDateString() {
	return plannedStartDateString;
    }

    public void setPlannedStartDateString(String plannedStartDateString) {
	this.plannedStartDateString = plannedStartDateString;
	this.plannedStartDate = HijriDateService.getHijriDate(plannedStartDateString);
	this.trainingCourseEvent.setPlannedStartDate(plannedStartDate);
    }

    @Basic
    @Column(name = "PLANNED_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPlannedEndDate() {
	return plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
	this.plannedEndDate = plannedEndDate;
	this.plannedEndDateString = HijriDateService.getHijriDateString(plannedEndDate);
	this.trainingCourseEvent.setPlannedEndDate(plannedEndDate);
    }

    @Transient
    public String getPlannedEndDateString() {
	return plannedEndDateString;
    }

    public void setPlannedEndDateString(String plannedEndDateString) {
	this.plannedEndDateString = plannedEndDateString;
	this.plannedEndDate = HijriDateService.getHijriDate(plannedEndDateString);
	this.trainingCourseEvent.setPlannedEndDate(plannedEndDate);
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
	this.trainingCourseEvent.setActualStartDate(actualStartDate);
    }

    @Transient
    public String getActualStartDateString() {
	return actualStartDateString;
    }

    public void setActualStartDateString(String actualStartDateString) {
	this.actualStartDateString = actualStartDateString;
	this.actualStartDate = HijriDateService.getHijriDate(actualStartDateString);
	this.trainingCourseEvent.setActualStartDate(actualStartDate);
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
	this.trainingCourseEvent.setActualEndDate(actualEndDate);
    }

    @Transient
    public String getActualEndDateString() {
	return actualEndDateString;
    }

    public void setActualEndDateString(String actualEndDateString) {
	this.actualEndDateString = actualEndDateString;
	this.actualEndDate = HijriDateService.getHijriDate(actualEndDateString);
	this.trainingCourseEvent.setActualEndDate(actualEndDate);
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
	this.trainingCourseEvent.setStatus(status);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
	this.trainingCourseEvent.setEflag(eflag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
	this.trainingCourseEvent.setMigFlag(migFlag);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.trainingCourseEvent.setAttachments(attachments);
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
	this.trainingCourseEvent.setReasons(reasons);
    }

    @Basic
    @Column(name = "AUTO_GENERATED_SERIAL_FLAG")
    public Integer getAutoGeneratedSerialFlag() {
	return autoGeneratedSerialFlag;
    }

    public void setAutoGeneratedSerialFlag(Integer autoGeneratedSerialFlag) {
	this.autoGeneratedSerialFlag = autoGeneratedSerialFlag;
	this.trainingCourseEvent.setAutoGeneratedSerialFlag(autoGeneratedSerialFlag);
    }

    @Transient
    public TrainingCourseEvent getTrainingCourseEvent() {
	return trainingCourseEvent;
    }

    public void setTrainingCourseEvent(TrainingCourseEvent trainingCourseEvent) {
	this.trainingCourseEvent = trainingCourseEvent;
    }

}
