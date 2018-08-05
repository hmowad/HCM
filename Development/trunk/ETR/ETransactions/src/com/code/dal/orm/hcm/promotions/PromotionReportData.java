package com.code.dal.orm.hcm.promotions;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

/**
 * 
 * The <code>PromotionReport</code> class represents the Promotion report data as a master report for all employees in detailed information.
 * 
 */
@NamedQueries({ @NamedQuery(name = "hcm_promotionReportData_searchPromotionReportsData",
	query = " select pr from PromotionReportData pr " +
		" where (:P_REPORT_ID = -1 or pr.id = :P_REPORT_ID) " +
		" and (:P_CATEGORY_ID = -1 or pr.categoryId = :P_CATEGORY_ID) " +
		" and (:P_REPORT_NUMBER = '-1' or pr.reportNumber = :P_REPORT_NUMBER) " +
		" and (:P_EMP_ID = -1 or pr.id in (select pd.reportId from PromotionReportDetail pd where pd.empId = :P_EMP_ID)) " +
		" and (:P_REPORT_DATE_FLAG = -1 or to_date(:P_REPORT_DATE, 'MI/MM/YYYY') = pr.reportDate) " +
		" and (:P_DUE_DATE_FLAG = -1 or to_date(:P_DUE_DATE, 'MI/MM/YYYY') = pr.dueDate) " +
		" and (:P_RANK_ID = -1 or pr.rankId = :P_RANK_ID) " +
		" and (:P_STATUS = -1 or pr.status = :P_STATUS) " +
		" and (:P_REGION_ID = -1 or pr.regionId = :P_REGION_ID) " +
		" and (:P_DECISION_NUMBER = '-1' or pr.decisionNumber = :P_DECISION_NUMBER) " +
		" and (:P_DECISION_DATE_FLAG = -1 or to_date(:P_DECISION_DATE, 'MI/MM/YYYY') = pr.decisionDate) " +
		" and (:P_ALL_SUBMITTED_OFFICERS = -1 or ((select count(pd.id) from PromotionReportDetail pd where pd.status = 10 and pd.reportId = pr.id) != 0)) " +
		" order by pr.reportDate desc ")
})

@Entity
@Table(name = "HCM_VW_PRM_REPORTS")
public class PromotionReportData extends BaseEntity {

    private Long id;
    private String reportNumber;
    private Date reportDate;
    private String reportDateString;
    private Long categoryId;
    private Long promotionTypeId;
    private String promotionTypeDesc;
    private Date dueDate;
    private String dueDateString;
    private Date promotionDate;
    private String promotionDateString;
    private Long rankId;
    private String rankDesc;
    private Long status;
    private Long regionId;
    private String regionDesc;
    private Integer scaleUpFlag;
    private Boolean scaleUpFlagBoolean;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private String internalCopies;
    private String externalCopies;
    private String attachments;

    private PromotionReport promotionReport;

    public PromotionReportData() {
	promotionReport = new PromotionReport();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	promotionReport.setId(id);
    }

    @Basic
    @Column(name = "REPORT_NUMBER")
    public String getReportNumber() {
	return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
	this.reportNumber = reportNumber;
	promotionReport.setReportNumber(reportNumber);
    }

    @Basic
    @Column(name = "REPORT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getReportDate() {
	return reportDate;
    }

    public void setReportDate(Date reportDate) {
	this.reportDate = reportDate;
	this.reportDateString = HijriDateService.getHijriDateString(reportDate);
	promotionReport.setReportDate(reportDate);
    }

    @Transient
    public String getReportDateString() {
	return reportDateString;
    }

    public void setReportDateString(String reportDateString) {
	this.reportDateString = reportDateString;
	this.reportDate = HijriDateService.getHijriDate(reportDateString);
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	promotionReport.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "PROMOTION_TYPE_ID")
    public Long getPromotionTypeId() {
	return promotionTypeId;
    }

    public void setPromotionTypeId(Long promotionTypeId) {
	this.promotionTypeId = promotionTypeId;
	promotionReport.setPromotionTypeId(promotionTypeId);
    }

    @Basic
    @Column(name = "PROMOTION_TYPE_DESC")
    public String getPromotionTypeDesc() {
	return promotionTypeDesc;
    }

    public void setPromotionTypeDesc(String promotionTypeDesc) {
	this.promotionTypeDesc = promotionTypeDesc;
    }

    @Basic
    @Column(name = "DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDueDate() {
	return dueDate;
    }

    public void setDueDate(Date dueDate) {
	this.dueDate = dueDate;
	this.dueDateString = HijriDateService.getHijriDateString(dueDate);
	promotionReport.setDueDate(dueDate);
    }

    @Transient
    public String getDueDateString() {
	return dueDateString;
    }

    public void setDueDateString(String dueDateString) {
	this.dueDateString = dueDateString;
	this.dueDate = HijriDateService.getHijriDate(dueDateString);
    }

    @Basic
    @Column(name = "PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPromotionDate() {
	return promotionDate;
    }

    public void setPromotionDate(Date promotionDate) {
	this.promotionDate = promotionDate;
	this.promotionDateString = HijriDateService.getHijriDateString(promotionDate);
	promotionReport.setPromotionDate(promotionDate);

    }

    @Transient
    public String getPromotionDateString() {
	return promotionDateString;
    }

    public void setPromotionDateString(String promotionDateString) {
	this.promotionDateString = promotionDateString;
	this.setPromotionDate(HijriDateService.getHijriDate(promotionDateString));
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	promotionReport.setRankId(rankId);
    }

    @Basic
    @Column(name = "RANK_DESC")
    public String getRankDesc() {
	return rankDesc;
    }

    public void setRankDesc(String rankDesc) {
	this.rankDesc = rankDesc;
    }

    @Basic
    @Column(name = "STATUS")
    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
	promotionReport.setStatus(status);
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
	promotionReport.setRegionId(regionId);
    }

    @Basic
    @Column(name = "REGION_DESC")
    public String getRegionDesc() {
	return regionDesc;
    }

    public void setRegionDesc(String regionDesc) {
	this.regionDesc = regionDesc;
    }

    @Basic
    @Column(name = "SCALE_UP_FLAG")
    public Integer getScaleUpFlag() {
	return scaleUpFlag;
    }

    public void setScaleUpFlag(Integer scaleUpFlag) {
	this.scaleUpFlag = scaleUpFlag;
	promotionReport.setScaleUpFlag(scaleUpFlag);
	if (this.scaleUpFlag == null || this.scaleUpFlag == FlagsEnum.OFF.getCode()) {
	    this.scaleUpFlagBoolean = false;
	} else {
	    this.scaleUpFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getScaleUpFlagBoolean() {
	return scaleUpFlagBoolean;
    }

    public void setScaleUpFlagBoolean(Boolean scaleUpFlagBoolean) {
	this.scaleUpFlagBoolean = scaleUpFlagBoolean;
	if (this.scaleUpFlagBoolean == true) {
	    setScaleUpFlag(FlagsEnum.ON.getCode());
	} else {
	    setScaleUpFlag(FlagsEnum.OFF.getCode());
	}
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.promotionReport.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
	promotionReport.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.setDecisionDate(HijriDateService.getHijriDate(decisionDateString));
    }

    @Transient
    public PromotionReport getPromotionReport() {
	return promotionReport;
    }

    public void setPromotionReport(PromotionReport promotionReport) {
	this.promotionReport = promotionReport;
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	this.promotionReport.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	this.promotionReport.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.promotionReport.setAttachments(attachments);
    }

}
