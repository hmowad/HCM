package com.code.dal.orm.hcm.organization.jobs;

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

/**
 * The <code>JobData</code> class represents the job data which can be occupied by the employees in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_jobData_searchJobData",
		query = " from JobData j where " +
			" (:P_JOB_ID = -1 or j.id = :P_JOB_ID) " +
			" and (:P_JOB_CODE = '-1' or j.code = :P_JOB_CODE) " +
			" and (:P_JOB_NAME = '-1' or j.name like :P_JOB_NAME) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1 or j.categoryId in ( :P_CATEGORIES_IDS )) " +
			" and (:P_JOB_UNIT_ID = -1 or j.unitId = :P_JOB_UNIT_ID) " +
			" and (:P_JOB_UNIT_HKEY = '-1' or j.unitHKey like :P_JOB_UNIT_HKEY) " +
			" and (:P_JOB_UNIT_FULL_NAME = '-1' or j.unitFullName like :P_JOB_UNIT_FULL_NAME) " +
			" and (:P_JOB_REGION_ID = -1 or j.regionId = :P_JOB_REGION_ID) " +
			" and (:P_JOB_RANK_ID = -1 or j.rankId = :P_JOB_RANK_ID) " +
			" and (:P_JOB_MINOR_SPEC_ID = -1 or j.minorSpecializationId = :P_JOB_MINOR_SPEC_ID) " +
			" and (:P_JOB_STATUSES_FLAG = -1 or j.status in ( :P_JOB_STATUSES )) " +
			" and (:P_APPROVED_FLAG = -1 or j.approvedFlag = :P_APPROVED_FLAG) " +
			" and (:P_JOB_RESERVATION_STATUSES_FLAG = -1 or j.reservationStatus in ( :P_JOB_RESERVATION_STATUSES )) " +
			" and (:P_JOB_EXECUTION_DATE_FROM_FLAG = -1 or j.executionDate >= to_date(:P_JOB_EXECUTION_DATE_FROM, 'MI/MM/YYYY')) " +
			" and (:P_JOB_EXECUTION_DATE_TO_FLAG = -1 or j.executionDate <= to_date(:P_JOB_EXECUTION_DATE_TO, 'MI/MM/YYYY')) " +
			" order by j.code "),

	@NamedQuery(name = "hcm_jobData_getJobsForRecruitment",
		query = " select j from JobData j where " +
			" j.categoryId = :P_CATEGORY_ID " +
			" and (:P_JOB_UNIT_HKEY = '-1' or j.unitHKey like :P_JOB_UNIT_HKEY) " +
			" and (:P_JOB_REGION_ID = -1 or j.regionId = :P_JOB_REGION_ID) " +
			" and (:P_JOB_RANK_ID = -1 or j.rankId = :P_JOB_RANK_ID) " +
			" and (:P_JOB_MINOR_SPECS_IDS_FLAG = -1 or j.minorSpecializationId in (:P_JOB_MINOR_SPECS_IDS)) " +
			" and j.status = 1 and j.approvedFlag = 1 and j.reservationStatus = 2 " +
			" order by j.code "),

	@NamedQuery(name = "hcm_jobData_searchJobByUnitsIds",
		query = " select j from JobData j where j.unitId in ( :P_UNITS_IDS ) and j.status <> 4 "),

	@NamedQuery(name = "hcm_jobData_searchJobsByJobsIds",
		query = " select j from JobData j " +
			" where j.id in (:P_JOBS_IDS) " +
			" order by j.code "),

	@NamedQuery(name = "hcm_jobData_countJobsByUnitHKeyAndMinorSpecsIds",
		query = " select count(j.id) from JobData j " +
			" where j.unitHKey like :P_UNIT_HKEY_PREFIX " +
			" and j.minorSpecializationId in (:P_MINOR_SPECS_IDS) " +
			" and j.status <> 4 " +
			" group by j.minorSpecializationId"),

	@NamedQuery(name = "hcm_jobData_getJobsByDecisionInfo",
		query = " select j from JobData j,JobTransaction jt " +
			" where j.id = jt.jobId " +
			" and j.categoryId = :P_CATEGORY_ID " +
			" and (:P_REGION_ID = -1 or j.regionId = :P_REGION_ID) " +
			" and j.status in (:P_JOB_STATUSES) " +
			" and j.reservationStatus in (:P_JOB_RESERVATION_STATUSES) " +
			" and jt.eFlag = 1 " +
			" and jt.decisionNumber = :P_DECISION_NUMBER " +
			" and jt.decisionDate = to_date(:P_DECISION_DATE, 'MI/MM/YYYY') " +
			" order by j.code ")
})
@Entity
@Table(name = "HCM_VW_ORG_JOBS")
public class JobData extends BaseEntity {
    private Long id;

    private String code;
    private String serial;
    private String sequence; // Transient

    private Long basicJobNameId;
    private String name;
    private Long employeeId;
    private String employeeFullName;
    private Long categoryId;
    private String categoryDescription;
    private Long rankId;
    private String rankDescription;
    private Long regionId;
    private String regionCode;
    private String regionDescription;
    private Long unitId;
    private String unitFullName;
    private String unitHKey;
    private Long classificationId;
    private String classificationJobCode;
    private String classificationJobDescription;
    private String dutiesDesc;
    private String jobDesc;
    private Integer generalSpecialization;
    private Long minorSpecializationId;
    private Integer minorSpecializationCode;
    private String minorSpecializationDescription;
    private Long majorSpecializationId;
    private Integer majorSpecializationCode;
    private String majorSpecializationDescription;
    private Integer generalType;
    private Integer status;
    private Date executionDate;
    private String executionDateString;
    private String remarks;
    private Integer approvedFlag;
    private Integer reservationStatus;
    private String reservationRemarks;

    private Job job;

    /**
     * Transient : Used to hold the reasons of the transaction applied to the Job so that we can save it to the JobTransaction records after that.
     */
    private String reasons;

    /**
     * Transient : Used to hold the new job name for the rename transaction
     */
    private Long newBasicJobNameId;
    private String newName;

    /**
     * Transient : Used to hold the new job rank for the scale transaction
     */
    private Long newRankId;
    private String newRankDescription;

    /**
     * Transient : Used to hold the new job unit and region for the move transaction
     */
    private Long newUnitId;
    private String newUnitFullName;
    private Long newRegionId;

    /**
     * Transient : Used to hold the new job classification for the scale and rename transaction
     */
    private Long newClassificationId;
    private String newClassificationJobCode;
    private String newClassificationJobDescription;

    /**
     * Transient : Used to hold the new job minor specialization for the modify minor specialization transaction
     */
    private Long newMinorSpecializationId;
    private String newMinorSpecializationDescription;

    /**
     * Transient : Used to hold the number of jobs that will be constructed at the collective transactions.
     */
    private Integer jobsCount;

    public JobData() {
	job = new Job();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	job.setId(id);
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
	job.setCode(code);
    }

    @Basic
    @Column(name = "SERIAL")
    public String getSerial() {
	return serial;
    }

    public void setSerial(String serial) {
	this.serial = serial;
	job.setSerial(serial);
    }

    @Transient
    public String getSequence() {
	return sequence;
    }

    public void setSequence(String sequence) {
	this.sequence = sequence;
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME_ID")
    public Long getBasicJobNameId() {
	return basicJobNameId;
    }

    public void setBasicJobNameId(Long basicJobNameId) {
	this.basicJobNameId = basicJobNameId;
	job.setBasicJobNameId(basicJobNameId);
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
	job.setName(name);
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
    @Column(name = "EMPLOYEE_FULL_NAME")
    public String getEmployeeFullName() {
	return employeeFullName;
    }

    public void setEmployeeFullName(String employeeFullName) {
	this.employeeFullName = employeeFullName;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	job.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "CATEGORY_DESCRIPTION")
    public String getCategoryDescription() {
	return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
	this.categoryDescription = categoryDescription;
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	job.setRankId(rankId);
    }

    @Basic
    @Column(name = "RANK_DESCRIPTION")
    public String getRankDescription() {
	return rankDescription;
    }

    public void setRankDescription(String rankDescription) {
	this.rankDescription = rankDescription;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	job.setRegionId(regionId);
    }

    @Basic
    @Column(name = "REGION_CODE")
    public String getRegionCode() {
	return regionCode;
    }

    public void setRegionCode(String regionCode) {
	this.regionCode = regionCode;
    }

    @Basic
    @Column(name = "REGION_DESCRIPTION")
    public String getRegionDescription() {
	return regionDescription;
    }

    public void setRegionDescription(String regionDescription) {
	this.regionDescription = regionDescription;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
	job.setUnitId(unitId);
    }

    @Basic
    @Column(name = "UNIT_FULL_NAME")
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    @Basic
    @Column(name = "UNIT_HKEY")
    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHKey) {
	this.unitHKey = unitHKey;
    }

    @Basic
    @Column(name = "CLASSIFICATION_ID")
    public Long getClassificationId() {
	return classificationId;
    }

    public void setClassificationId(Long classificationId) {
	this.classificationId = classificationId;
	job.setClassificationId(classificationId);
    }

    @Basic
    @Column(name = "CLASSIFICATION_JOB_CODE")
    public String getClassificationJobCode() {
	return classificationJobCode;
    }

    public void setClassificationJobCode(String classificationJobCode) {
	this.classificationJobCode = classificationJobCode;
    }

    @Basic
    @Column(name = "CLASSIFICATION_JOB_DESCRIPTION")
    public String getClassificationJobDescription() {
	return classificationJobDescription;
    }

    public void setClassificationJobDescription(String classificationJobDescription) {
	this.classificationJobDescription = classificationJobDescription;
    }

    @Basic
    @Column(name = "DUTIES_DESC")
    public String getDutiesDesc() {
	return dutiesDesc;
    }

    public void setDutiesDesc(String dutiesDesc) {
	this.dutiesDesc = dutiesDesc;
	job.setDutiesDesc(dutiesDesc);
    }

    @Basic
    @Column(name = "JOB_DESC")
    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
	job.setJobDesc(jobDesc);
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
	job.setGeneralSpecialization(generalSpecialization);
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
	job.setMinorSpecializationId(minorSpecializationId);
    }

    @Basic
    @Column(name = "MINOR_SPEC_CODE")
    public Integer getMinorSpecializationCode() {
	return minorSpecializationCode;
    }

    public void setMinorSpecializationCode(Integer minorSpecializationCode) {
	this.minorSpecializationCode = minorSpecializationCode;
    }

    @Basic
    @Column(name = "MINOR_SPEC_DESCRIPTION")
    public String getMinorSpecializationDescription() {
	return minorSpecializationDescription;
    }

    public void setMinorSpecializationDescription(String minorSpecializationDescription) {
	this.minorSpecializationDescription = minorSpecializationDescription;
    }

    @Basic
    @Column(name = "MAJOR_SPECIALIZATION_ID")
    public Long getMajorSpecializationId() {
	return majorSpecializationId;
    }

    public void setMajorSpecializationId(Long majorSpecializationId) {
	this.majorSpecializationId = majorSpecializationId;
    }

    @Basic
    @Column(name = "MAJOR_SPEC_CODE")
    public Integer getMajorSpecializationCode() {
	return majorSpecializationCode;
    }

    public void setMajorSpecializationCode(Integer majorSpecializationCode) {
	this.majorSpecializationCode = majorSpecializationCode;
    }

    @Basic
    @Column(name = "MAJOR_SPEC_DESCRIPTION")
    public String getMajorSpecializationDescription() {
	return majorSpecializationDescription;
    }

    public void setMajorSpecializationDescription(String majorSpecializationDescription) {
	this.majorSpecializationDescription = majorSpecializationDescription;
    }

    @Basic
    @Column(name = "GENERAL_TYPE")
    public Integer getGeneralType() {
	return generalType;
    }

    public void setGeneralType(Integer generalType) {
	this.generalType = generalType;
	job.setGeneralType(generalType);
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
	job.setStatus(status);
    }

    @Basic
    @Column(name = "EXECUTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
	this.executionDateString = HijriDateService.getHijriDateString(executionDate);
	job.setExecutionDate(executionDate);
    }

    @Transient
    public String getExecutionDateString() {
	return executionDateString;
    }

    public void setExecutionDateString(String executionDateString) {
	this.executionDateString = executionDateString;
	this.executionDate = HijriDateService.getHijriDate(executionDateString);
	job.setExecutionDateString(executionDateString);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	job.setRemarks(remarks);
    }

    @Basic
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
	job.setApprovedFlag(approvedFlag);
    }

    @Basic
    @Column(name = "RESERVATION_STATUS")
    public Integer getReservationStatus() {
	return reservationStatus;
    }

    public void setReservationStatus(Integer reservationStatus) {
	this.reservationStatus = reservationStatus;
	job.setReservationStatus(reservationStatus);
    }

    @Basic
    @Column(name = "RESERVATION_REMARKS")
    public String getReservationRemarks() {
	return reservationRemarks;
    }

    public void setReservationRemarks(String reservationRemarks) {
	this.reservationRemarks = reservationRemarks;
	job.setReservationRemarks(reservationRemarks);
    }

    @Transient
    public Job getJob() {
	return job;
    }

    public void setJob(Job job) {
	this.job = job;
    }

    @Transient
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    @Transient
    public Long getNewBasicJobNameId() {
	return newBasicJobNameId;
    }

    public void setNewBasicJobNameId(Long newBasicJobNameId) {
	this.newBasicJobNameId = newBasicJobNameId;
    }

    @Transient
    public String getNewName() {
	return newName;
    }

    public void setNewName(String newName) {
	this.newName = newName;
    }

    @Transient
    public Long getNewRankId() {
	return newRankId;
    }

    public void setNewRankId(Long newRankId) {
	this.newRankId = newRankId;
    }

    @Transient
    public String getNewRankDescription() {
	return newRankDescription;
    }

    public void setNewRankDescription(String newRankDescription) {
	this.newRankDescription = newRankDescription;
    }

    @Transient
    public Long getNewUnitId() {
	return newUnitId;
    }

    public void setNewUnitId(Long newUnitId) {
	this.newUnitId = newUnitId;
    }

    @Transient
    public String getNewUnitFullName() {
	return newUnitFullName;
    }

    public void setNewUnitFullName(String newUnitFullName) {
	this.newUnitFullName = newUnitFullName;
    }

    @Transient
    public Long getNewRegionId() {
	return newRegionId;
    }

    public void setNewRegionId(Long newRegionId) {
	this.newRegionId = newRegionId;
    }

    @Transient
    public Long getNewClassificationId() {
	return newClassificationId;
    }

    public void setNewClassificationId(Long newClassificationId) {
	this.newClassificationId = newClassificationId;
    }

    @Transient
    public String getNewClassificationJobCode() {
	return newClassificationJobCode;
    }

    public void setNewClassificationJobCode(String newClassificationJobCode) {
	this.newClassificationJobCode = newClassificationJobCode;
    }

    @Transient
    public String getNewClassificationJobDescription() {
	return newClassificationJobDescription;
    }

    public void setNewClassificationJobDescription(String newClassificationJobDescription) {
	this.newClassificationJobDescription = newClassificationJobDescription;
    }

    @Transient
    public Long getNewMinorSpecializationId() {
	return newMinorSpecializationId;
    }

    public void setNewMinorSpecializationId(Long newMinorSpecializationId) {
	this.newMinorSpecializationId = newMinorSpecializationId;
    }

    @Transient
    public String getNewMinorSpecializationDescription() {
	return newMinorSpecializationDescription;
    }

    public void setNewMinorSpecializationDescription(String newMinorSpecializationDescription) {
	this.newMinorSpecializationDescription = newMinorSpecializationDescription;
    }

    @Transient
    public Integer getJobsCount() {
	return jobsCount;
    }

    public void setJobsCount(Integer jobsCount) {
	this.jobsCount = jobsCount;
    }

}