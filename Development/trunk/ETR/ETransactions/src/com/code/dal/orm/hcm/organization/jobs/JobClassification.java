package com.code.dal.orm.hcm.organization.jobs;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>JobClassification</code> class represents the classification data of the job that will be occupied by a civilian employee.
 * 
 */
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_jobClassification_searchJobClassification",
		query = " from JobClassification jc where " +
			" (:P_JOB_DESC = '-1' or jc.jobDesc like :P_JOB_DESC) " +
			" and (:P_MAJOR_GROUP_DESC = '-1' or jc.majorGroupDesc = :P_MAJOR_GROUP_DESC) " +
			" and (:P_MINOR_GROUP_DESC = '-1' or jc.minorGroupDesc = :P_MINOR_GROUP_DESC) " +
			" and (:P_SERIES_DESC = '-1' or jc.seriesDesc = :P_SERIES_DESC) " +
			" and (:P_RANK_CODE = '-1' or jc.jobCode like :P_RANK_CODE) " +
			" order by jc.id "
	),
	@NamedQuery(name = "hcm_jobClassification_getJobClassificationMajorGroupDesc",
		query = " select distinct(jc.majorGroupDesc) from JobClassification jc" +
			" where (:P_RANK_CODE = '-1' or jc.jobCode like :P_RANK_CODE)"
	),
	@NamedQuery(name = "hcm_jobClassification_getJobClassificationMinorGroupDesc",
		query = " select distinct(jc.minorGroupDesc) from JobClassification jc " +
			" where (:P_MAJOR_GROUP_DESC = '-1' or jc.majorGroupDesc = :P_MAJOR_GROUP_DESC) " +
			" and (:P_RANK_CODE = '-1' or jc.jobCode like :P_RANK_CODE) "
	),
	@NamedQuery(name = "hcm_jobClassification_getJobClassificationSeriesDesc",
		query = " select distinct(jc.seriesDesc) from JobClassification jc " +
			" where (:P_MINOR_GROUP_DESC = '-1' or jc.minorGroupDesc = :P_MINOR_GROUP_DESC) " +
			" and (:P_MAJOR_GROUP_DESC = '-1' or jc.majorGroupDesc = :P_MAJOR_GROUP_DESC) " +
			" and (:P_RANK_CODE = '-1' or jc.jobCode like :P_RANK_CODE) "
	)
})
@Entity
@Table(name = "HCM_ORG_JOBS_CLASSIFICATIONS")
public class JobClassification extends BaseEntity implements Serializable {
    private Long id;
    private String majorGroupCode;
    private String majorGroupDesc;
    private String minorGroupCode;
    private String minorGroupDesc;
    private String seriesCode;
    private String seriesDesc;
    private String jobCode;
    private String jobDesc;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "MAJOR_GROUP_CODE")
    public String getMajorGroupCode() {
	return majorGroupCode;
    }

    public void setMajorGroupCode(String majorGroupCode) {
	this.majorGroupCode = majorGroupCode;
    }

    @Basic
    @Column(name = "MAJOR_GROUP_DESC")
    public String getMajorGroupDesc() {
	return majorGroupDesc;
    }

    public void setMajorGroupDesc(String majorGroupDesc) {
	this.majorGroupDesc = majorGroupDesc;
    }

    @Basic
    @Column(name = "MINOR_GROUP_CODE")
    public String getMinorGroupCode() {
	return minorGroupCode;
    }

    public void setMinorGroupCode(String minorGroupCode) {
	this.minorGroupCode = minorGroupCode;
    }

    @Basic
    @Column(name = "MINOR_GROUP_DESC")
    public String getMinorGroupDesc() {
	return minorGroupDesc;
    }

    public void setMinorGroupDesc(String minorGroupDesc) {
	this.minorGroupDesc = minorGroupDesc;
    }

    @Basic
    @Column(name = "SERIES_CODE")
    public String getSeriesCode() {
	return seriesCode;
    }

    public void setSeriesCode(String seriesCode) {
	this.seriesCode = seriesCode;
    }

    @Basic
    @Column(name = "SERIES_DESC")
    public String getSeriesDesc() {
	return seriesDesc;
    }

    public void setSeriesDesc(String seriesDesc) {
	this.seriesDesc = seriesDesc;
    }

    @Basic
    @Column(name = "JOB_CODE")
    public String getJobCode() {
	return jobCode;
    }

    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
    }

    @Basic
    @Column(name = "JOB_DESC")
    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }
}
