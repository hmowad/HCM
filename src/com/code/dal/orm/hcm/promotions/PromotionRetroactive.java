package com.code.dal.orm.hcm.promotions;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "HCM_PRM_RETROACTIVE_TEST")
public class PromotionRetroactive extends BaseEntity {

    private Long empId;
    private Long rankId;
    private Long degreeId;
    private Date effectiveDate;

    @Id
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
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
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
    }

    @Basic
    @Column(name = "EFFECTIVE_DATE")
    public Date getEffectiveDate() {
	return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
	this.effectiveDate = effectiveDate;
    }

}