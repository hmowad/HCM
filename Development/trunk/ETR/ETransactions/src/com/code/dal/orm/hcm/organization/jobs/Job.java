package com.code.dal.orm.hcm.organization.jobs;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>Job</code> class represents the job data which can be occupied by the employees.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_job_countJobsByBasicJobNameId",
		query = " select count(j.id) from Job j " +
			"where j.basicJobNameId = :P_BASIC_JOB_NAME_ID "),

	@NamedQuery(name = "hcm_job_countJobsByUnitsIdsAndCategoriesIds",
		query = " select count(j.id) from Job j where j.unitId in ( :P_UNITS_IDS ) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1 or j.categoryId in ( :P_CATEGORIES_IDS )) " +
			" and j.status <> 4 "),

	@NamedQuery(name = "hcm_job_getJobsSerialsWhereGapsAfterThem",
		query = " select j.serial from Job j " +
			" where j.regionId in ( :P_JOB_REGIONS_IDS ) " +
			" and j.categoryId in ( :P_CATEGORIES_IDS ) " +
			" and j.status <> 4 " +
			" and substr(j.serial,4,5) <> '99999' " +
			" and not exists (select jj from Job jj where jj.regionId = j.regionId and jj.categoryId in ( :P_CATEGORIES_IDS ) and jj.status <> 4  and to_number(jj.serial) = (to_number(j.serial) + 1) ) " +
			" order by j.regionId, j.serial "),

	@NamedQuery(name = "hcm_job_getJobsSerialsWhereGapsBeforeThem",
		query = " select j.serial from Job j " +
			" where j.regionId in ( :P_JOB_REGIONS_IDS ) " +
			" and j.categoryId in ( :P_CATEGORIES_IDS ) " +
			" and j.status <> 4 " +
			" and substr(j.serial,4,5) <> '00001' " +
			" and not exists (select jj from Job jj where jj.regionId = j.regionId and jj.categoryId in ( :P_CATEGORIES_IDS ) and jj.status <> 4 and to_number(jj.serial) = (to_number(j.serial) - 1) ) " +
			" order by j.regionId, j.serial "),

	@NamedQuery(name = "hcm_job_countJobsBySerials",
		query = " select count(j.id) from Job j " +
			" where j.serial in ( :P_SERIALS ) " +
			" and j.status <> 4 ")
})
@Entity
@Table(name = "HCM_ORG_JOBS")
public class Job extends BaseEntity {
    private Long id;
    private String code;
    private String serial;
    private Long basicJobNameId;
    private Long categoryId;
    private Long rankId;
    private Long regionId;
    private Long unitId;
    private Long classificationId;
    private String dutiesDesc;
    private String jobDesc;
    private Integer generalSpecialization;
    private Long minorSpecializationId;
    private Integer generalType;
    private Integer status;
    private Date executionDate;
    private String executionDateString;
    private String remarks;
    private Integer approvedFlag;
    private Integer reservationStatus;
    private String reservationRemarks;

    @SequenceGenerator(name = "HCMOrgJobsSeq",
	    sequenceName = "HCM_ORG_JOBS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMOrgJobsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    @Basic
    @Column(name = "SERIAL")
    public String getSerial() {
	return serial;
    }

    public void setSerial(String serial) {
	this.serial = serial;
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME_ID")
    public Long getBasicJobNameId() {
	return basicJobNameId;
    }

    public void setBasicJobNameId(Long basicJobNameId) {
	this.basicJobNameId = basicJobNameId;
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
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "CLASSIFICATION_ID")
    public Long getClassificationId() {
	return classificationId;
    }

    public void setClassificationId(Long classificationId) {
	this.classificationId = classificationId;
    }

    @Basic
    @Column(name = "DUTIES_DESC")
    public String getDutiesDesc() {
	return dutiesDesc;
    }

    public void setDutiesDesc(String dutiesDesc) {
	this.dutiesDesc = dutiesDesc;
    }

    @Basic
    @Column(name = "JOB_DESC")
    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
    }

    @Basic
    @Column(name = "GENERAL_TYPE")
    public Integer getGeneralType() {
	return generalType;
    }

    public void setGeneralType(Integer generalType) {
	this.generalType = generalType;
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
    @Column(name = "EXECUTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExecutionDate() {
	return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
	this.executionDate = executionDate;
	this.executionDateString = HijriDateService.getHijriDateString(executionDate);
    }

    @Transient
    public String getExecutionDateString() {
	return executionDateString;
    }

    public void setExecutionDateString(String executionDateString) {
	this.executionDateString = executionDateString;
	this.executionDate = HijriDateService.getHijriDate(executionDateString);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Basic
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
    }

    @Basic
    @Column(name = "RESERVATION_STATUS")
    public Integer getReservationStatus() {
	return reservationStatus;
    }

    public void setReservationStatus(Integer reservationStatus) {
	this.reservationStatus = reservationStatus;
    }

    @Basic
    @Column(name = "RESERVATION_REMARKS")
    public String getReservationRemarks() {
	return reservationRemarks;
    }

    public void setReservationRemarks(String reservationRemarks) {
	this.reservationRemarks = reservationRemarks;
    }
}
