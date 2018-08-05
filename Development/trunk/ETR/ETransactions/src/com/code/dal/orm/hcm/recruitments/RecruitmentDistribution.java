package com.code.dal.orm.hcm.recruitments;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_REC_DISTRIBUTIONS")
public class RecruitmentDistribution extends AuditEntity implements Serializable, InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long regionId;
    private Long minorSpecializationId;
    private Long rankId;
    private Long count;

    @SequenceGenerator(name = "HCMRecSeq", sequenceName = "HCM_REC_SEQ")
    @GeneratedValue(generator = "HCMRecSeq")
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
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
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
    @Column(name = "COUNT")
    public Long getCount() {
	return count;
    }

    public void setCount(Long count) {
	this.count = count;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "regionId:" + regionId + AUDIT_SEPARATOR +
		"minorSpecializationId:" + minorSpecializationId + AUDIT_SEPARATOR +
		"rankId:" + rankId + AUDIT_SEPARATOR +
		"count:" + count + AUDIT_SEPARATOR;
    }
}