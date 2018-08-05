package com.code.dal.orm.hcm.recruitments;

import java.io.Serializable;

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
	@NamedQuery(name = "hcm_recruitmentDistributionData_searchRecruitmentDistributionData",
		query = " from RecruitmentDistributionData recDist where " +
			" (:P_REGION_ID = -1 or recDist.regionId = :P_REGION_ID) " +
			" and (:P_MINOR_SPEC_ID = -1 or recDist.minorSpecializationId = :P_MINOR_SPEC_ID) " +
			" and (:P_RANK_ID = -1 or recDist.rankId = :P_RANK_ID) " +
			" order by recDist.id "),

	@NamedQuery(name = "hcm_recruitmentDistributionData_countRegionsNeeds",
		query = " select nvl(sum(recDist.count),0) from RecruitmentDistributionData recDist "),

	@NamedQuery(name = "hcm_recruitmentDistributionData_checkSoldiersAndRegionsNeedsCount",
		query = " select sum(r.count) from RecruitmentDistributionData r " +
			" group by r.rankId, r.minorSpecializationId " +
			" having sum(r.count) != (select count(e.empId) from EmployeeData e, RecruitmentWishData w " +
			" where e.recruitmentRegionId is null " +
			" and (e.statusId in (10,12)) " +
			" and (w.empId = e.empId) " +
			" and (e.exceptionalRecruitmentFlag = 0) " +
			" and (e.gender = 'M') " +
			" and (e.recruitmentRankId = r.rankId and e.recruitmentMinorSpecId = r.minorSpecializationId)) ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_REC_DISTRIBUTIONS")
public class RecruitmentDistributionData extends BaseEntity implements Serializable {

    private Long id;
    private Long regionId;
    private Long minorSpecializationId;
    private String minorSpecializationDesc;
    private Long rankId;
    private Long count;

    private RecruitmentDistribution recruitmentDistribution;

    public RecruitmentDistributionData() {
	recruitmentDistribution = new RecruitmentDistribution();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	recruitmentDistribution.setId(id);
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	recruitmentDistribution.setRegionId(regionId);
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
	recruitmentDistribution.setMinorSpecializationId(minorSpecializationId);
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_DESC")
    public String getMinorSpecializationDesc() {
	return minorSpecializationDesc;
    }

    public void setMinorSpecializationDesc(String minorSpecializationDesc) {
	this.minorSpecializationDesc = minorSpecializationDesc;
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	recruitmentDistribution.setRankId(rankId);
    }

    @Basic
    @Column(name = "COUNT")
    public Long getCount() {
	return count;
    }

    public void setCount(Long count) {
	this.count = count;
	recruitmentDistribution.setCount(count);
    }

    @Transient
    public RecruitmentDistribution getRecruitmentDistribution() {
	return recruitmentDistribution;
    }

    public void setRecruitmentDistribution(RecruitmentDistribution recruitmentDistribution) {
	this.recruitmentDistribution = recruitmentDistribution;
    }
}