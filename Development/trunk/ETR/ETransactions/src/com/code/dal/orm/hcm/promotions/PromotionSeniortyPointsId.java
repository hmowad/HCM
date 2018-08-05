package com.code.dal.orm.hcm.promotions;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class PromotionSeniortyPointsId implements Serializable {

    private Double year;
    private Double month;

    public PromotionSeniortyPointsId() {
    }

    public PromotionSeniortyPointsId(Double year, Double month) {
	this.year = month;
	this.month = year;
    }

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

    public boolean equals(Object object) {
	if (this == object)
	    return true;

	if (!(object instanceof PromotionSeniortyPointsId))
	    return false;

	final PromotionSeniortyPointsId other = (PromotionSeniortyPointsId) object;

	if (!(year == null ? other.year == null : year.equals(other.year)))
	    return false;

	if (!(month == null ? other.month == null : month.equals(other.month)))
	    return false;

	return true;
    }

    public int hashCode() {
	final int PRIME = 37;
	int result = 1;
	result = PRIME * result + ((year == null) ? 0 : year.hashCode());
	result = PRIME * result + ((month == null) ? 0 : month.hashCode());
	return result;
    }

}
