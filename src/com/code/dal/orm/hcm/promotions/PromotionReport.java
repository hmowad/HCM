package com.code.dal.orm.hcm.promotions;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>PromotionReport</code> class represents the Promotion report data as a master report for all employees.
 * 
 */

@NamedQueries({
	@NamedQuery(name = "hcm_promotionReport_searchPromotionReports",
		query = " select pr from PromotionReport pr " +
			" where pr.id in (:P_REPORTS_IDS) " +
			" and pr.status = 15 " +
			" order by pr.id "),

	@NamedQuery(name = "hcm_promotionReport_countRunningPromotionReports",
		query = " select count(pr.id) from PromotionReport pr, PromotionReportDetail pd " +
			" where pr.id  = pd.reportId " +
			" and pd.empId = :P_EMP_ID " +
			" and pr.status <> 20 ")
})

@Entity
@Table(name = "HCM_PRM_REPORTS")
public class PromotionReport extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private String reportNumber;
    private Date reportDate;
    private Long categoryId;
    private Long promotionTypeId;
    private Date dueDate;
    private Date promotionDate;
    private Long rankId;
    private Long status;
    private Long regionId;
    private Integer scaleUpFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String internalCopies;
    private String externalCopies;
    private String attachments;

    @SequenceGenerator(name = "HCMPromotionsReportSeq",
	    sequenceName = "HCM_PRM_REP_SEQ")
    @Id
    @GeneratedValue(generator = "HCMPromotionsReportSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "REPORT_NUMBER")
    public String getReportNumber() {
	return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
	this.reportNumber = reportNumber;
    }

    @Basic
    @Column(name = "REPORT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getReportDate() {
	return reportDate;
    }

    public void setReportDate(Date reportDate) {
	this.reportDate = reportDate;
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
    @Column(name = "PROMOTION_TYPE_ID")
    public Long getPromotionTypeId() {
	return promotionTypeId;
    }

    public void setPromotionTypeId(Long promotionTypeId) {
	this.promotionTypeId = promotionTypeId;
    }

    @Basic
    @Column(name = "DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDueDate() {
	return dueDate;
    }

    public void setDueDate(Date dueDate) {
	this.dueDate = dueDate;
    }

    @Basic
    @Column(name = "PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPromotionDate() {
	return promotionDate;
    }

    public void setPromotionDate(Date promotionDate) {
	this.promotionDate = promotionDate;
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
    @Column(name = "STATUS")
    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
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
    @Column(name = "SCALE_UP_FLAG")
    public Integer getScaleUpFlag() {
	return scaleUpFlag;
    }

    public void setScaleUpFlag(Integer scaleUpFlag) {
	this.scaleUpFlag = scaleUpFlag;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "reportNumber:" + reportNumber + AUDIT_SEPARATOR +
		"reportDate:" + reportDate + AUDIT_SEPARATOR +
		"dueDate:" + dueDate + AUDIT_SEPARATOR +
		"promotionDate:" + promotionDate + AUDIT_SEPARATOR +
		"rankId:" + rankId + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"regionId:" + regionId + AUDIT_SEPARATOR +
		"scaleUpFlag:" + scaleUpFlag + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"decisionDate:" + decisionDate + AUDIT_SEPARATOR +
		"internalCopies:" + internalCopies + AUDIT_SEPARATOR +
		"externalCopies:" + externalCopies + AUDIT_SEPARATOR +
		"attachments:" + attachments;

    }
}
