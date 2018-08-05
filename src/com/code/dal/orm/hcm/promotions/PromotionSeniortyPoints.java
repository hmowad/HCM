package com.code.dal.orm.hcm.promotions;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

/**
 * 
 * The <code>PromotionSeniortyPoints</code> class represents the promotion seniority points.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_promotionSeniortyPoints_getPromotionSeniortyPoints",
		query = " select ps from PromotionSeniortyPoints ps " +
			" where ps.year = :P_YEAR " +
			" and ps.month = :P_MONTH ")
})

@Entity
@Table(name = "HCM_PRM_SENIORTY_POINTS")
@IdClass(PromotionSeniortyPointsId.class)
public class PromotionSeniortyPoints extends BaseEntity {

    private Double year;
    private Double month;
    private Double points;

    @Id
    @Column(name = "YEAR")
    public Double getYear() {
	return year;
    }

    public void setYear(Double year) {
	this.year = year;
    }

    @Id
    @Column(name = "MONTH")
    public Double getMonth() {
	return month;
    }

    public void setMonth(Double month) {
	this.month = month;
    }

    @Basic
    @Column(name = "POINTS")
    public Double getPoints() {
	return points;
    }

    public void setPoints(Double points) {
	this.points = points;
    }

}
