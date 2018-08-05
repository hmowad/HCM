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

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>Promotion</code> class represents the Promotion transaction data for all promotion transaction types for all employees.
 * 
 */

@NamedQueries({
	@NamedQuery(name = "hcm_promotionTransaction_countPromotionTransactions",
		query = " select count(p.id) from PromotionTransaction p " +
			" where (:P_DECISION_NUMBER = '-1' or p.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_DECISION_DATE_FLAG = -1 or to_date(:P_DECISION_DATE, 'MI/MM/YYYY') = p.decisionDate) " +
			" and (:P_EMP_ID = -1 or p.empId = :P_EMP_ID) " +
			" and (:P_E_FLAG = -1 or p.eflag = :P_E_FLAG ) " +
			" and p.migFlag = 0 ")
})

@Entity
@Table(name = "HCM_PRM_TRANSACTIONS")
public class PromotionTransaction extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {

    private Long id;
    private Long promotionTypeId;
    private Long empId;
    private Long categoryId;
    private Long oldRankId;
    private Long newRankId;
    private Long rankTitleId;
    private Long oldDegreeId;
    private Long newDegreeId;
    private Integer oldMilitaryNumber;
    private Integer newMilitaryNumber;

    private String externalDecisionNumber;
    private Date externalDecisionDate;
    private String referring;
    private String exceptionalPromotionReason;

    private Date oldLastPromotionDate;
    private Date newLastPromotionDate;
    private Date oldDueDate;
    private Date newDueDate;

    private Long oldJobId;
    private String oldJobClassCode;
    private String oldJobClassDesc;
    private String oldJobCode;
    private String oldJobDesc;
    private Long newJobId;
    private String newJobClassCode;
    private String newJobClassDesc;
    private String newJobCode;
    private String newJobDesc;
    private Long freezedJobId;
    private String freezedJobCode;
    private String freezedJobName;
    private String freezedJobUnitFullName;

    private Double oldSalary;
    private Double newSalary;
    private Long oldUnitId;
    private String oldUnitFullName;
    private Long newUnitId;
    private String newUnitFullName;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String decisionNumber;
    private Date decisionDate;
    private Integer eflag;
    private Integer migFlag;
    private Long reportDetailId;
    private Date joiningDate;
    private Integer regionChangedFlag;
    private String internalCopies;
    private String externalCopies;
    private String attachments;

    @SequenceGenerator(name = "HCMPromotionsSeq",
	    sequenceName = "HCM_PRM_SEQ")
    @Id
    @GeneratedValue(generator = "HCMPromotionsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
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
    @Column(name = "OLD_RANK_ID")
    public Long getOldRankId() {
	return oldRankId;
    }

    public void setOldRankId(Long oldRankId) {
	this.oldRankId = oldRankId;
    }

    @Basic
    @Column(name = "NEW_RANK_ID")
    public Long getNewRankId() {
	return newRankId;
    }

    public void setNewRankId(Long newRankId) {
	this.newRankId = newRankId;
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
    }

    @Basic
    @Column(name = "OLD_DEGREE_ID")
    public Long getOldDegreeId() {
	return oldDegreeId;
    }

    public void setOldDegreeId(Long oldDegreeId) {
	this.oldDegreeId = oldDegreeId;
    }

    @Basic
    @Column(name = "NEW_DEGREE_ID")
    public Long getNewDegreeId() {
	return newDegreeId;
    }

    public void setNewDegreeId(Long newDegreeId) {
	this.newDegreeId = newDegreeId;
    }

    @Basic
    @Column(name = "OLD_MILITARY_NUMBER")
    public Integer getOldMilitaryNumber() {
	return oldMilitaryNumber;
    }

    public void setOldMilitaryNumber(Integer oldMilitaryNumber) {
	this.oldMilitaryNumber = oldMilitaryNumber;
    }

    @Basic
    @Column(name = "NEW_MILITARY_NUMBER")
    public Integer getNewMilitaryNumber() {
	return newMilitaryNumber;
    }

    public void setNewMilitaryNumber(Integer newMilitaryNumber) {
	this.newMilitaryNumber = newMilitaryNumber;
    }

    @Basic
    @Column(name = "EXTERNAL_DECISION_NUMBER")
    public String getExternalDecisionNumber() {
	return externalDecisionNumber;
    }

    public void setExternalDecisionNumber(String externalDecisionNumber) {
	this.externalDecisionNumber = externalDecisionNumber;
    }

    @Basic
    @Column(name = "EXTERNAL_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExternalDecisionDate() {
	return externalDecisionDate;
    }

    public void setExternalDecisionDate(Date externalDecisionDate) {
	this.externalDecisionDate = externalDecisionDate;
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
    }

    @Basic
    @Column(name = "EXCEPTIONAL_PROMOTION_REASON")
    public String getExceptionalPromotionReason() {
	return exceptionalPromotionReason;
    }

    public void setExceptionalPromotionReason(String exceptionalPromotionReason) {
	this.exceptionalPromotionReason = exceptionalPromotionReason;
    }

    @Basic
    @Column(name = "OLD_LAST_PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOldLastPromotionDate() {
	return oldLastPromotionDate;
    }

    public void setOldLastPromotionDate(Date oldLastPromotionDate) {
	this.oldLastPromotionDate = oldLastPromotionDate;
    }

    @Basic
    @Column(name = "NEW_LAST_PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewLastPromotionDate() {
	return newLastPromotionDate;
    }

    public void setNewLastPromotionDate(Date newLastPromotionDate) {
	this.newLastPromotionDate = newLastPromotionDate;
    }

    @Basic
    @Column(name = "OLD_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOldDueDate() {
	return oldDueDate;
    }

    public void setOldDueDate(Date oldDueDate) {
	this.oldDueDate = oldDueDate;
    }

    @Basic
    @Column(name = "NEW_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewDueDate() {
	return newDueDate;
    }

    public void setNewDueDate(Date newDueDate) {
	this.newDueDate = newDueDate;
    }

    @Basic
    @Column(name = "OLD_JOB_ID")
    public Long getOldJobId() {
	return oldJobId;
    }

    public void setOldJobId(Long oldJobId) {
	this.oldJobId = oldJobId;
    }

    @Basic
    @Column(name = "OLD_JOB_CLASS_CODE")
    public String getOldJobClassCode() {
	return oldJobClassCode;
    }

    public void setOldJobClassCode(String oldJobClassCode) {
	this.oldJobClassCode = oldJobClassCode;
    }

    @Basic
    @Column(name = "OLD_JOB_CLASS_DESC")
    public String getOldJobClassDesc() {
	return oldJobClassDesc;
    }

    public void setOldJobClassDesc(String oldJobClassDesc) {
	this.oldJobClassDesc = oldJobClassDesc;
    }

    @Basic
    @Column(name = "OLD_JOB_CODE")
    public String getOldJobCode() {
	return oldJobCode;
    }

    public void setOldJobCode(String oldJobCode) {
	this.oldJobCode = oldJobCode;
    }

    @Basic
    @Column(name = "OLD_JOB_DESC")
    public String getOldJobDesc() {
	return oldJobDesc;
    }

    public void setOldJobDesc(String oldJobDesc) {
	this.oldJobDesc = oldJobDesc;
    }

    @Basic
    @Column(name = "NEW_JOB_ID")
    public Long getNewJobId() {
	return newJobId;
    }

    public void setNewJobId(Long newJobId) {
	this.newJobId = newJobId;
    }

    @Basic
    @Column(name = "NEW_JOB_CLASS_CODE")
    public String getNewJobClassCode() {
	return newJobClassCode;
    }

    public void setNewJobClassCode(String newJobClassCode) {
	this.newJobClassCode = newJobClassCode;
    }

    @Basic
    @Column(name = "NEW_JOB_CLASS_DESC")
    public String getNewJobClassDesc() {
	return newJobClassDesc;
    }

    public void setNewJobClassDesc(String newJobClassDesc) {
	this.newJobClassDesc = newJobClassDesc;
    }

    @Basic
    @Column(name = "NEW_JOB_CODE")
    public String getNewJobCode() {
	return newJobCode;
    }

    public void setNewJobCode(String newJobCode) {
	this.newJobCode = newJobCode;
    }

    @Basic
    @Column(name = "NEW_JOB_DESC")
    public String getNewJobDesc() {
	return newJobDesc;
    }

    public void setNewJobDesc(String newJobDesc) {
	this.newJobDesc = newJobDesc;
    }

    @Basic
    @Column(name = "FREEZED_JOB_ID")
    public Long getFreezedJobId() {
	return freezedJobId;
    }

    public void setFreezedJobId(Long freezedJobId) {
	this.freezedJobId = freezedJobId;
    }

    @Basic
    @Column(name = "FREEZED_JOB_CODE")
    public String getFreezedJobCode() {
	return freezedJobCode;
    }

    public void setFreezedJobCode(String freezedJobCode) {
	this.freezedJobCode = freezedJobCode;
    }

    @Basic
    @Column(name = "FREEZED_JOB_NAME")
    public String getFreezedJobName() {
	return freezedJobName;
    }

    public void setFreezedJobName(String freezedJobName) {
	this.freezedJobName = freezedJobName;
    }

    @Basic
    @Column(name = "FREEZED_JOB_UNIT_FULL_NAME")
    public String getFreezedJobUnitFullName() {
	return freezedJobUnitFullName;
    }

    public void setFreezedJobUnitFullName(String freezedJobUnitFullName) {
	this.freezedJobUnitFullName = freezedJobUnitFullName;
    }

    @Basic
    @Column(name = "OLD_UNIT_ID")
    public Long getOldUnitId() {
	return oldUnitId;
    }

    public void setOldUnitId(Long oldUnitId) {
	this.oldUnitId = oldUnitId;
    }

    @Basic
    @Column(name = "OLD_UNIT_FULL_NAME")
    public String getOldUnitFullName() {
	return oldUnitFullName;
    }

    public void setOldUnitFullName(String oldUnitFullName) {
	this.oldUnitFullName = oldUnitFullName;
    }

    @Basic
    @Column(name = "NEW_UNIT_ID")
    public Long getNewUnitId() {
	return newUnitId;
    }

    public void setNewUnitId(Long newUnitId) {
	this.newUnitId = newUnitId;
    }

    @Basic
    @Column(name = "NEW_UNIT_FULL_NAME")
    public String getNewUnitFullName() {
	return newUnitFullName;
    }

    public void setNewUnitFullName(String newUnitFullName) {
	this.newUnitFullName = newUnitFullName;
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
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
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
    }

    @Basic
    @Column(name = "REPORT_DETAIL_ID")
    public Long getReportDetailId() {
	return reportDetailId;
    }

    public void setReportDetailId(Long reportDetailId) {
	this.reportDetailId = reportDetailId;
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
    }

    @Basic
    @Column(name = "OLD_SALARY")
    public Double getOldSalary() {
	return oldSalary;
    }

    public void setOldSalary(Double oldSalary) {
	this.oldSalary = oldSalary;
    }

    @Basic
    @Column(name = "NEW_SALARY")
    public Double getNewSalary() {
	return newSalary;
    }

    public void setNewSalary(Double newSalary) {
	this.newSalary = newSalary;
    }

    @Basic
    @Column(name = "REGION_CHANGED_FLAG")
    public Integer getRegionChangedFlag() {
	return regionChangedFlag;
    }

    public void setRegionChangedFlag(Integer regionChangedFlag) {
	this.regionChangedFlag = regionChangedFlag;
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
	return "empId:" + empId + AUDIT_SEPARATOR +
		"newMilitaryNumber:" + newMilitaryNumber + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"promotionTypeId:" + promotionTypeId + AUDIT_SEPARATOR +
		"joiningDate:" + joiningDate + AUDIT_SEPARATOR +
		"newLastPromotionDate:" + newLastPromotionDate + AUDIT_SEPARATOR +
		"newJobId:" + newJobId + AUDIT_SEPARATOR +
		"decisionDate:" + decisionDate + AUDIT_SEPARATOR;
    }
}