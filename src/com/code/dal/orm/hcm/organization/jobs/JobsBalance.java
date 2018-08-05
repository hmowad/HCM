package com.code.dal.orm.hcm.organization.jobs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_ORG_JOBS_BALANCES")
public class JobsBalance extends AuditEntity implements UpdatableAuditEntity {

    private Long id;
    private Long regionId;
    private Long rankId;
    private Long approvedJobsBalance;
    private Long version;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    @Basic
    @Column(name = "APPROVED_JOBS_BALANCE")
    public Long getApprovedJobsBalance() {
	return approvedJobsBalance;
    }

    public void setApprovedJobsBalance(Long approvedJobsBalance) {
	this.approvedJobsBalance = approvedJobsBalance;
    }

    @Version
    @Column(name = "VERSION")
    /*
     * This attribute handles the optimistic transaction management for the jobs balance entity.
     */
    public Long getVersion() {
	return version;
    }

    public void setVersion(Long version) {
	this.version = version;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "regionId:" + regionId + AUDIT_SEPARATOR +
		"rankId:" + rankId + AUDIT_SEPARATOR +
		"approvedJobsBalance:" + approvedJobsBalance;
    }

}
