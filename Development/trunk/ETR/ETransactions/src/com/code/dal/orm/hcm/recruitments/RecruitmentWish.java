package com.code.dal.orm.hcm.recruitments;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "HCM_REC_WISHES")
public class RecruitmentWish extends AuditEntity implements Serializable, InsertableAuditEntity, UpdatableAuditEntity {
    private Long id;
    private Long empId;
    private Integer evaluationDegree;
    private Long regionsFirstWishId;
    private Long regionsSecondWishId;
    private Long regionsThirdWishId;
    private Long regionsFourthWishId;
    private Long regionsFifthWishId;
    private Long regionsSixthWishId;
    private Long regionsSeventhWishId;
    private Long regionsEighthWishId;
    private Long regionsNinthWishId;
    private Long regionsTenthWishId;
    private Long regionsEleventhWishId;

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
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "EVALUATION_DEGREE")
    public Integer getEvaluationDegree() {
	return evaluationDegree;
    }

    public void setEvaluationDegree(Integer evaluationDegree) {
	this.evaluationDegree = evaluationDegree;
    }

    @Basic
    @Column(name = "REGIONS_FIRST_WISH_ID")
    public Long getRegionsFirstWishId() {
	return regionsFirstWishId;
    }

    public void setRegionsFirstWishId(Long regionsFirstWishId) {
	this.regionsFirstWishId = regionsFirstWishId;
    }

    @Basic
    @Column(name = "REGIONS_SECOND_WISH_ID")
    public Long getRegionsSecondWishId() {
	return regionsSecondWishId;
    }

    public void setRegionsSecondWishId(Long regionsSecondWishId) {
	this.regionsSecondWishId = regionsSecondWishId;
    }

    @Basic
    @Column(name = "REGIONS_THIRD_WISH_ID")
    public Long getRegionsThirdWishId() {
	return regionsThirdWishId;
    }

    public void setRegionsThirdWishId(Long regionsThirdWishId) {
	this.regionsThirdWishId = regionsThirdWishId;
    }

    @Basic
    @Column(name = "REGIONS_FOURTH_WISH_ID")
    public Long getRegionsFourthWishId() {
	return regionsFourthWishId;
    }

    public void setRegionsFourthWishId(Long regionsFourthWishId) {
	this.regionsFourthWishId = regionsFourthWishId;
    }

    @Basic
    @Column(name = "REGIONS_FIFTH_WISH_ID")
    public Long getRegionsFifthWishId() {
	return regionsFifthWishId;
    }

    public void setRegionsFifthWishId(Long regionsFifthWishId) {
	this.regionsFifthWishId = regionsFifthWishId;
    }

    @Basic
    @Column(name = "REGIONS_SIXTH_WISH_ID")
    public Long getRegionsSixthWishId() {
	return regionsSixthWishId;
    }

    public void setRegionsSixthWishId(Long regionsSixthWishId) {
	this.regionsSixthWishId = regionsSixthWishId;
    }

    @Basic
    @Column(name = "REGIONS_SEVENTH_WISH_ID")
    public Long getRegionsSeventhWishId() {
	return regionsSeventhWishId;
    }

    public void setRegionsSeventhWishId(Long regionsSeventhWishId) {
	this.regionsSeventhWishId = regionsSeventhWishId;
    }

    @Basic
    @Column(name = "REGIONS_EIGHTH_WISH_ID")
    public Long getRegionsEighthWishId() {
	return regionsEighthWishId;
    }

    public void setRegionsEighthWishId(Long regionsEighthWishId) {
	this.regionsEighthWishId = regionsEighthWishId;
    }

    @Basic
    @Column(name = "REGIONS_NINTH_WISH_ID")
    public Long getRegionsNinthWishId() {
	return regionsNinthWishId;
    }

    public void setRegionsNinthWishId(Long regionsNinthWishId) {
	this.regionsNinthWishId = regionsNinthWishId;
    }

    @Basic
    @Column(name = "REGIONS_TENTH_WISH_ID")
    public Long getRegionsTenthWishId() {
	return regionsTenthWishId;
    }

    public void setRegionsTenthWishId(Long regionsTenthWishId) {
	this.regionsTenthWishId = regionsTenthWishId;
    }

    @Basic
    @Column(name = "REGIONS_ELEVENTH_WISH_ID")
    public Long getRegionsEleventhWishId() {
	return regionsEleventhWishId;
    }

    public void setRegionsEleventhWishId(Long regionsEleventhWishId) {
	this.regionsEleventhWishId = regionsEleventhWishId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"evaluationDegree:" + evaluationDegree + AUDIT_SEPARATOR +
		"regionsFirstWishId:" + regionsFirstWishId + AUDIT_SEPARATOR +
		"regionsSecondWishId:" + regionsSecondWishId + AUDIT_SEPARATOR +
		"regionsThirdWishId:" + regionsThirdWishId + AUDIT_SEPARATOR +
		"regionsFourthWishId:" + regionsFourthWishId + AUDIT_SEPARATOR +
		"regionsFifthWishId:" + regionsFifthWishId + AUDIT_SEPARATOR +
		"regionsSixthWishId:" + regionsSixthWishId + AUDIT_SEPARATOR +
		"regionsSeventhWishId:" + regionsSeventhWishId + AUDIT_SEPARATOR +
		"regionsEighthWishId:" + regionsEighthWishId + AUDIT_SEPARATOR +
		"regionsNinthWishId:" + regionsNinthWishId + AUDIT_SEPARATOR +
		"regionsTenthWishId:" + regionsTenthWishId + AUDIT_SEPARATOR +
		"regionsEleventhWishId:" + regionsEleventhWishId + AUDIT_SEPARATOR;
    }
}
