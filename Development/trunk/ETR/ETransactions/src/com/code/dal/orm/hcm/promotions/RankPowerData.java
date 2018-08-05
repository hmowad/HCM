package com.code.dal.orm.hcm.promotions;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

/**
 * 
 * The <code>RankPowerData</code> class represents the ranks power details for officers in detailed information.
 * 
 */

@NamedQuery(name = "hcm_rankPowerData_getAllRanksPowerData",
	query = " select rp from RankPowerData rp " +
		" order by rp.rankId ")

@Entity
@Table(name = "HCM_VW_PRM_RANKS_POWER")
public class RankPowerData extends BaseEntity {

    private Long id;
    private Long categoryId;
    private Long rankId;
    private String rankDescription;
    private Integer power;
    private Integer occupied;
    private Integer vacant;
    private Integer loadedBalanceWithdrawnFromPromotion;
    private Integer netVacant;
    private Integer allowedPromotionCount;

    private RankPower rankPower;

    public RankPowerData() {
	rankPower = new RankPower();
	vacant = 0;
	netVacant = 0;
	allowedPromotionCount = 0;
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	rankPower.setId(id);
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
	rankPower.setRankId(rankId);
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
    @Column(name = "POWER")
    public Integer getPower() {
	return power;
    }

    public void setPower(Integer power) {
	rankPower.setPower(power);
	this.power = power;
    }

    @Basic
    @Column(name = "OCCUPIED")
    public Integer getOccupied() {
	return occupied;
    }

    public void setOccupied(Integer occupied) {
	this.occupied = occupied;
    }

    @Transient
    public Integer getVacant() {
	return vacant;
    }

    public void setVacant(Integer vacant) {
	this.vacant = vacant;
    }

    @Transient
    public Integer getLoadedBalanceWithdrawnFromPromotion() {
	return loadedBalanceWithdrawnFromPromotion;
    }

    public void setLoadedBalanceWithdrawnFromPromotion(Integer loadedBalanceWithdrawnFromPromotion) {
	this.loadedBalanceWithdrawnFromPromotion = loadedBalanceWithdrawnFromPromotion;
    }

    @Transient
    public Integer getNetVacant() {
	return netVacant;
    }

    public void setNetVacant(Integer netVacant) {
	this.netVacant = netVacant;
    }

    @Transient
    public Integer getAllowedPromotionCount() {
	return allowedPromotionCount;
    }

    public void setAllowedPromotionCount(Integer allowedPromotionCount) {
	this.allowedPromotionCount = allowedPromotionCount;
    }

    @Transient
    public RankPower getRankPower() {
	return rankPower;
    }

    public void setRankPower(RankPower rankPower) {
	this.rankPower = rankPower;
    }

}
