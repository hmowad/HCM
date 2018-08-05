package com.code.dal.orm.hcm.promotions;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * 
 * The <code>RankPowerData</code> class represents the ranks power details for officers.
 * 
 */
@Entity
@Table(name = "HCM_PRM_RANKS_POWER")
public class RankPower extends AuditEntity implements UpdatableAuditEntity {

    private Long id;
    private Long rankId;
    private Integer power;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "POWER")
    public Integer getPower() {
	return power;
    }

    public void setPower(Integer power) {
	this.power = power;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "rankId:" + rankId + AUDIT_SEPARATOR +
		"power:" + power;

    }

}
