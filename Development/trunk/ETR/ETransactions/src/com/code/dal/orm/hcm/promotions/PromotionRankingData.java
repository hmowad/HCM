package com.code.dal.orm.hcm.promotions;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_promotionRankingData_getRankedPromotionReportDetailsDataByReportId",
		query = " select pd from PromotionRankingData pr, PromotionReportDetailData pd " +
			" where pr.reportId = :P_REPORT_ID " +
			" and pr.reportDetailId = pd.id " +
			" and pd.status in (:P_STATUS_IDS) " +
			" and (:P_MEDICAL_TEST = -1 or pd.medicalTest = :P_MEDICAL_TEST) " +
			" and (:P_EMP_NAME = '-1' or pd.empName like :P_EMP_NAME ) " +
			" order by pr.totalDegree desc, pr.lastPromotionDate asc, pr.qualificationDegree desc, pr.staffSergeantPromotionDate asc, pr.sergeantPromotionDate asc, pr.underSergeantPromotionDate asc, pr.corporalPromotionDate asc, pr.firstSoldierPromotionDate asc, pr.soldierPromotionDate asc, pr.recruitmentDate asc, pr.graduationEvaluation asc, pr.graduationOrder asc, pr.graduationDegree desc, pd.empName asc ")
})

@Entity
@IdClass(PromotionRankingDataId.class)
@Table(name = "HCM_VW_PRM_RANKINGS")
public class PromotionRankingData extends BaseEntity {
    private Long reportId;
    private Long reportDetailId;
    private Double totalDegree;
    private Date lastPromotionDate;
    private Double qualificationDegree;
    private Date staffSergeantPromotionDate;
    private Date sergeantPromotionDate;
    private Date underSergeantPromotionDate;
    private Date corporalPromotionDate;
    private Date firstSoldierPromotionDate;
    private Date soldierPromotionDate;
    private Date recruitmentDate;
    private Integer graduationEvaluation;
    private Integer graduationOrder;
    private Double graduationDegree;

    @Id
    @Column(name = "REPORT_ID")
    public Long getReportId() {
	return reportId;
    }

    public void setReportId(Long reportId) {
	this.reportId = reportId;
    }

    @Id
    @Column(name = "REPORT_DETAIL_ID")
    public Long getReportDetailId() {
	return reportDetailId;
    }

    public void setReportDetailId(Long reportDetailId) {
	this.reportDetailId = reportDetailId;
    }

    @Basic
    @Column(name = "TOTAL_DEGREE")
    public Double getTotalDegree() {
	return totalDegree;
    }

    public void setTotalDegree(Double totalDegree) {
	this.totalDegree = totalDegree;
    }

    @Basic
    @Column(name = "LAST_PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastPromotionDate() {
	return lastPromotionDate;
    }

    public void setLastPromotionDate(Date lastPromotionDate) {
	this.lastPromotionDate = lastPromotionDate;
    }

    @Basic
    @Column(name = "QUALIFICATION_DEGREE")
    public Double getQualificationDegree() {
	return qualificationDegree;
    }

    public void setQualificationDegree(Double qualificationDegree) {
	this.qualificationDegree = qualificationDegree;
    }

    @Basic
    @Column(name = "STAFF_SERGEANT_PRM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStaffSergeantPromotionDate() {
	return staffSergeantPromotionDate;
    }

    public void setStaffSergeantPromotionDate(Date staffSergeantPromotionDate) {
	this.staffSergeantPromotionDate = staffSergeantPromotionDate;
    }

    @Basic
    @Column(name = "SERGEANT_PRM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSergeantPromotionDate() {
	return sergeantPromotionDate;
    }

    public void setSergeantPromotionDate(Date sergeantPromotionDate) {
	this.sergeantPromotionDate = sergeantPromotionDate;
    }

    @Basic
    @Column(name = "UNDER_SERGEANT_PRM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUnderSergeantPromotionDate() {
	return underSergeantPromotionDate;
    }

    public void setUnderSergeantPromotionDate(Date underSergeantPromotionDate) {
	this.underSergeantPromotionDate = underSergeantPromotionDate;
    }

    @Basic
    @Column(name = "CORPORAL_PRM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCorporalPromotionDate() {
	return corporalPromotionDate;
    }

    public void setCorporalPromotionDate(Date corporalPromotionDate) {
	this.corporalPromotionDate = corporalPromotionDate;
    }

    @Basic
    @Column(name = "FIRST_SOLDIER_PRM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFirstSoldierPromotionDate() {
	return firstSoldierPromotionDate;
    }

    public void setFirstSoldierPromotionDate(Date firstSoldierPromotionDate) {
	this.firstSoldierPromotionDate = firstSoldierPromotionDate;
    }

    @Basic
    @Column(name = "SOLDIER_PRM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSoldierPromotionDate() {
	return soldierPromotionDate;
    }

    public void setSoldierPromotionDate(Date soldierPromotionDate) {
	this.soldierPromotionDate = soldierPromotionDate;
    }

    @Basic
    @Column(name = "RECRUITMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecruitmentDate() {
	return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
	this.recruitmentDate = recruitmentDate;
    }

    @Basic
    @Column(name = "GRADUATION_EVALUATION")
    public Integer getGraduationEvaluation() {
	return graduationEvaluation;
    }

    public void setGraduationEvaluation(Integer graduationEvaluation) {
	this.graduationEvaluation = graduationEvaluation;
    }

    @Basic
    @Column(name = "GRADUATION_ORDER")
    public Integer getGraduationOrder() {
	return graduationOrder;
    }

    public void setGraduationOrder(Integer graduationOrder) {
	this.graduationOrder = graduationOrder;
    }

    @Basic
    @Column(name = "GRADUATION_DEGREE")
    public Double getGraduationDegree() {
	return graduationDegree;
    }

    public void setGraduationDegree(Double graduationDegree) {
	this.graduationDegree = graduationDegree;
    }
}
