package com.code.dal.orm.hcm.organization.jobs;

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
import com.code.services.util.HijriDateService;

/**
 * The <code>ManpowerNeedData</code> class represents the manpower needs data in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_manpowerNeedData_getManpowerNeedsRequestsAndStudies",
		query = " select m from ManpowerNeedData m " +
			" where m.categoryId = :P_CATEGORY_ID " +
			" and (:P_TRANSACTION_DATE_FROM_FLAG = -1 or m.transactionDate >= to_date(:P_TRANSACTION_DATE_FROM, 'MI/MM/YYYY')) " +
			" and (:P_TRANSACTION_DATE_TO_FLAG   = -1 or m.transactionDate <= to_date(:P_TRANSACTION_DATE_TO,   'MI/MM/YYYY')) " +
			" and (:P_MANPOWER_NEEDS_EXCLUDED_IDS_FLAG = -1 or m.id not in ( :P_MANPOWER_NEEDS_EXCLUDED_IDS )) " +
			" and ( (m.needType = 1 and m.status = 3 and m.studyId is null and m.requestingRegionId = :P_REGION_ID) " +
			"    or (m.needType = 2 and m.status = 3 and m.studyId is null and :P_REGION_ID = 1) ) " +
			" order by m.requestingRegionId, m.requestingUnitId, m.transactionDate "),

	@NamedQuery(name = "hcm_manpowerNeedData_getManpowerNeedsStudies",
		query = " select m from ManpowerNeedData m " +
			" where m.requestingRegionId = :P_REGION_ID " +
			" and m.categoryId = :P_CATEGORY_ID " +
			" and (:P_STUDY_DATE_FROM_FLAG = -1 or m.transactionDate >= to_date(:P_STUDY_DATE_FROM, 'MI/MM/YYYY')) " +
			" and (:P_STUDY_DATE_TO_FLAG   = -1 or m.transactionDate <= to_date(:P_STUDY_DATE_TO,   'MI/MM/YYYY')) " +
			" and m.needType <> 1 " +
			" order by m.requestingRegionId, m.transactionDate, m.status "),

	@NamedQuery(name = "hcm_manpowerNeedData_getManpowerNeedsByStudiesIds",
		query = " select m from ManpowerNeedData m " +
			" where m.studyId in ( :P_STUDIES_IDS ) " +
			" order by m.studyId, m.requestingRegionId, m.requestingUnitId, m.transactionDate "),

	@NamedQuery(name = "hcm_manpowerNeedData_getManpowerNeedsByIds",
		query = " select m from ManpowerNeedData m " +
			" where m.id in ( :P_MANPOWER_NEEDS_IDS ) "),

	@NamedQuery(name = "hcm_manpowerNeedData_countUnderProcessingRequestsFromUnit",
		query = " select count(m.id) from ManpowerNeedData m " +
			" where m.requestingUnitHKey in ( :P_UNITS_HKEYS ) " +
			" and m.categoryId = :P_CATEGORY_ID " +
			" and m.needType = 1 " +
			" and m.status = 2 "),

	@NamedQuery(name = "hcm_manpowerNeedData_countUnderProcessingStudies",
		query = " select count(m.id) from ManpowerNeedData m " +
			" where m.requestingRegionId = :P_REQUESTING_REGION_ID " +
			" and m.categoryId = :P_CATEGORY_ID " +
			" and m.needType <> 1 " +
			" and m.status in (1, 2) ")
})
@Entity
@Table(name = "HCM_VW_ORG_MANPOWER_NEEDS")
public class ManpowerNeedData extends BaseEntity {

    private Long id;
    private Long requestingUnitId;
    private String requestingUnitFullName;
    private String requestingUnitHKey;
    private Long requestingRegionId;
    private String requestingRegionDesc;
    private Date transactionDate;
    private String transactionDateString;
    private Long categoryId;
    private String categoryDescription;
    private Integer needType;
    private Integer status;
    private Long studyId;
    private Long approvedId;
    private Long decisionApprovedId;
    private String attachments;

    private ManpowerNeed manpowerNeed;

    public ManpowerNeedData() {
	setManpowerNeed(new ManpowerNeed());
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.manpowerNeed.setId(id);
    }

    @Basic
    @Column(name = "REQUESTING_UNIT_ID")
    public Long getRequestingUnitId() {
	return requestingUnitId;
    }

    public void setRequestingUnitId(Long requestingUnitId) {
	this.requestingUnitId = requestingUnitId;
	this.manpowerNeed.setRequestingUnitId(requestingUnitId);
    }

    @Basic
    @Column(name = "REQUESTING_UNIT_FULL_NAME")
    public String getRequestingUnitFullName() {
	return requestingUnitFullName;
    }

    public void setRequestingUnitFullName(String requestingUnitFullName) {
	this.requestingUnitFullName = requestingUnitFullName;
    }

    @Basic
    @Column(name = "REQUESTING_UNIT_HKEY")
    public String getRequestingUnitHKey() {
	return requestingUnitHKey;
    }

    public void setRequestingUnitHKey(String requestingUnitHKey) {
	this.requestingUnitHKey = requestingUnitHKey;
    }

    @Basic
    @Column(name = "REQUESTING_REGION_ID")
    public Long getRequestingRegionId() {
	return requestingRegionId;
    }

    public void setRequestingRegionId(Long requestingRegionId) {
	this.requestingRegionId = requestingRegionId;
    }

    @Basic
    @Column(name = "REQUESTING_REGION_DESC")
    public String getRequestingRegionDesc() {
	return requestingRegionDesc;
    }

    public void setRequestingRegionDesc(String requestingRegionDesc) {
	this.requestingRegionDesc = requestingRegionDesc;
    }

    @Basic
    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransactionDate() {
	return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
	this.transactionDateString = HijriDateService.getHijriDateString(transactionDate);
	this.manpowerNeed.setTransactionDate(transactionDate);
    }

    @Transient
    public String getTransactionDateString() {
	return transactionDateString;
    }

    public void setTransactionDateString(String transactionDateString) {
	this.transactionDateString = transactionDateString;
	this.transactionDate = HijriDateService.getHijriDate(transactionDateString);
	this.manpowerNeed.setTransactionDateString(transactionDateString);
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	this.manpowerNeed.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "CATEGORY_DESC")
    public String getCategoryDescription() {
	return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
	this.categoryDescription = categoryDescription;
    }

    @Basic
    @Column(name = "NEED_TYPE")
    public Integer getNeedType() {
	return needType;
    }

    public void setNeedType(Integer needType) {
	this.needType = needType;
	this.manpowerNeed.setNeedType(needType);
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
	this.manpowerNeed.setStatus(status);
    }

    @Basic
    @Column(name = "STUDY_ID")
    public Long getStudyId() {
	return studyId;
    }

    public void setStudyId(Long studyId) {
	this.studyId = studyId;
	this.manpowerNeed.setStudyId(studyId);
    }

    @Basic
    @Column(name = "APPROVED_ID")
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
	this.manpowerNeed.setApprovedId(approvedId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	this.manpowerNeed.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.manpowerNeed.setAttachments(attachments);
    }

    @Transient
    public ManpowerNeed getManpowerNeed() {
	return manpowerNeed;
    }

    public void setManpowerNeed(ManpowerNeed manpowerNeed) {
	this.manpowerNeed = manpowerNeed;
    }

}
