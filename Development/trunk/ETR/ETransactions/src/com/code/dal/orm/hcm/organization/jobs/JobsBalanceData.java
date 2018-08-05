package com.code.dal.orm.hcm.organization.jobs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_jobsBalanceData_getJobsBalanceData",
		query = " select j from JobsBalanceData j " +
			" where j.regionId = :P_REGION_ID " +
			" and j.categoryId = :P_CATEGORY_ID " +
			" order by j.rankId"),

	@NamedQuery(name = "hcm_jobsBalanceData_getTotalJobsBalanceData",
		query = " select new JobsBalanceData(j.rankId, j.rankDescription, sum(j.approvedJobsBalance), sum(j.vacantJobsCount), sum(j.occupiedJobsCount), sum(j.availableJobsBalance) )" +
			" from JobsBalanceData j " +
			" where j.categoryId = :P_CATEGORY_ID " +
			" group by j.rankId, j.rankDescription " +
			" order by j.rankId "),

	@NamedQuery(name = "hcm_jobsBalanceData_getJobsBalanceDataByRegionsAndRanks",
		query = " select j from JobsBalanceData j " +
			" where j.regionId || ':' || j.rankId in ( :P_REGIONS_IDS_AND_RANKS_IDS ) ")
})

@Entity
@Table(name = "HCM_VW_ORG_JOBS_BALANCES")
public class JobsBalanceData extends BaseEntity {

    private Long id;
    private Long regionId;
    private Long rankId;
    private String rankDescription;
    private Long categoryId;
    private Long approvedJobsBalance;
    private Long vacantJobsCount;
    private Long occupiedJobsCount;
    private Long availableJobsBalance;
    private Long version;

    private JobsBalance jobsBalance;

    public JobsBalanceData() {
	jobsBalance = new JobsBalance();
    }

    public JobsBalanceData(Long rankId, String rankDescription, Long approvedJobsBalance, Long vacantJobsCount, Long occupiedJobsCount, Long availableJobsBalance) {
	this.rankId = rankId;
	this.rankDescription = rankDescription;
	this.approvedJobsBalance = approvedJobsBalance;
	this.vacantJobsCount = vacantJobsCount;
	this.occupiedJobsCount = occupiedJobsCount;
	this.availableJobsBalance = availableJobsBalance;
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.jobsBalance.setId(id);
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	this.jobsBalance.setRegionId(regionId);
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	this.jobsBalance.setRankId(rankId);
    }

    @Basic
    @Column(name = "RANK_DESC")
    public String getRankDescription() {
	return rankDescription;
    }

    public void setRankDescription(String rankDescription) {
	this.rankDescription = rankDescription;
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
    @Column(name = "APPROVED_JOBS_BALANCE")
    public Long getApprovedJobsBalance() {
	return approvedJobsBalance;
    }

    public void setApprovedJobsBalance(Long approvedJobsBalance) {
	this.approvedJobsBalance = approvedJobsBalance;
	this.jobsBalance.setApprovedJobsBalance(approvedJobsBalance);
    }

    @Basic
    @Column(name = "VACANT_JOBS_COUNT")
    public Long getVacantJobsCount() {
	return vacantJobsCount;
    }

    public void setVacantJobsCount(Long vacantJobsCount) {
	this.vacantJobsCount = vacantJobsCount;
    }

    @Basic
    @Column(name = "OCCUPIED_JOBS_COUNT")
    public Long getOccupiedJobsCount() {
	return occupiedJobsCount;
    }

    public void setOccupiedJobsCount(Long occupiedJobsCount) {
	this.occupiedJobsCount = occupiedJobsCount;
    }

    @Basic
    @Column(name = "AVAILABLE_JOBS_BALANCE")
    public Long getAvailableJobsBalance() {
	return availableJobsBalance;
    }

    public void setAvailableJobsBalance(Long availableJobsBalance) {
	this.availableJobsBalance = availableJobsBalance;
    }

    @Basic
    @Column(name = "VERSION")
    public Long getVersion() {
	return version;
    }

    public void setVersion(Long version) {
	this.version = version;
	this.jobsBalance.setVersion(version);
    }

    @Transient
    public JobsBalance getJobsBalance() {
	return jobsBalance;
    }

    public void setJobsBalance(JobsBalance jobsBalance) {
	this.jobsBalance = jobsBalance;
    }

}
