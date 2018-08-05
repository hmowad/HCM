package com.code.dal.orm.hcm.promotions;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class PromotionEmployeeDegreeDataId implements Serializable {

    private Long empId;
    private String fromRankId;

    public PromotionEmployeeDegreeDataId() {
    }

    public PromotionEmployeeDegreeDataId(Long empId, String fromRankId) {
	this.empId = empId;
	this.fromRankId = fromRankId;
    }

    @Id
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Id
    @Column(name = "FROM_RANK_ID")
    public String getFromRankId() {
	return fromRankId;
    }

    public void setFromRankId(String fromRankId) {
	this.fromRankId = fromRankId;
    }

    public boolean equals(Object object) {
	if (this == object)
	    return true;

	if (!(object instanceof PromotionEmployeeDegreeDataId))
	    return false;

	final PromotionEmployeeDegreeDataId other = (PromotionEmployeeDegreeDataId) object;

	if (!(empId == null ? other.empId == null : empId.equals(other.empId)))
	    return false;

	if (!(fromRankId == null ? other.fromRankId == null : fromRankId.equals(other.fromRankId)))
	    return false;

	return true;
    }

    public int hashCode() {
	return empId.hashCode() + fromRankId.hashCode();
    }

}
