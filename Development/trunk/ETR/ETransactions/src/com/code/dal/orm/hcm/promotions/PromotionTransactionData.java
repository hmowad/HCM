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
import com.code.services.util.HijriDateService;

/**
 * The <code>PromotionTransactionData</code> class represents the Promotion transaction data for all promotion transaction types for all employees in detailed information.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_promotionTransactionData_searchPromotionTransactions",
		query = " select p from PromotionTransactionData p " +
			" where (:P_ID = -1 or p.id = :P_ID) " +
			" and (:P_PROMOTION_TYPE_ID = -1 or p.promotionTypeId = :P_PROMOTION_TYPE_ID) " +
			" and (:P_EMP_ID = -1 or p.empId = :P_EMP_ID) " +
			" and (:P_NEW_RANK_ID = -1 or p.newRankId = :P_NEW_RANK_ID) " +
			" order by p.decisionDate desc "),

	@NamedQuery(name = "hcm_promotionTransactionData_searchPromotionTransactionsDecisions",
		query = " select p from PromotionTransactionData p " +
			" where (:P_PROMOTION_TYPE_ID = -1 or p.promotionTypeId = :P_PROMOTION_TYPE_ID) " +
			" and (:P_EMP_ID = -1 or p.empId = :P_EMP_ID) " +
			" and (:P_DECISION_NUMBER = '-1' or p.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_DECISION_DATE_FLAG = -1 or to_date(:P_DECISION_DATE, 'MI/MM/YYYY') = p.decisionDate) " +
			" and (:P_CATEGORY_ID = -1 or p.categoryId = :P_CATEGORY_ID) " +
			" and p.eflag != 0  " +
			" and (p.id = (select min(pt.id) from PromotionTransaction pt where pt.decisionNumber = p.decisionNumber and pt.decisionDate = p.decisionDate and (:P_EMP_ID = -1 or pt.empId = :P_EMP_ID))) " +
			" order by p.decisionDate desc "),
	@NamedQuery(name = "hcm_promotionTransactionData_getPromotionTransactionDataByDecisionDateAndDecisionNumber",
		query = " select d from PromotionTransactionData d " +
			" where (:P_DECISION_NUMBER = '-1' or d.decisionNumber = :P_DECISION_NUMBER) "
			+ "and (:P_DECISION_DATE_FLAG = '-1' or to_date(:P_DECISION_DATE, 'MI/MM/YYYY') = d.decisionDate)" +
			" order by d.decisionDate"),

	@NamedQuery(name = "hcm_promotionTransactionData_getPromotionTransactionDataOfficersUnnotifiedByRoyalDateAndRoyalNumber",
		query = " select d from PromotionTransactionData d " +
			" where (d.categoryId = 1) " +
			" and (:P_ROYAL_NUMBER = '-1' or d.externalDecisionNumber = :P_ROYAL_NUMBER) " +
			" and (:P_ROYAL_DATE_FLAG = '-1' or to_date(:P_ROYAL_DATE, 'MI/MM/YYYY') = d.externalDecisionDate) " +
			" and (d.id not in (select n.transactionId from PromotionNotification n )) " +
			" order by d.externalDecisionDate"),

	@NamedQuery(name = "hcm_promotionTransactionData_getPromotionTransactionDataByIdsAndUnitHkeyAndRegionIdAndEmpId",
		query = " select d from PromotionTransactionData d " +
			" where d.id in (:P_IDS) " +
			" and (:P_EMP_ID = -1 or d.empId = :P_EMP_ID) " +
			" and (:P_UNIT_HKEY = '-1' or d.physicalUnitHkey like :P_UNIT_HKEY) " +
			" and (:P_REGION_ID = -1 or d.physicalRegionId = :P_REGION_ID) " +
			" order by d.externalDecisionDate "),
})

@Entity
@Table(name = "HCM_VW_PRM_TRANSACTIONS")
public class PromotionTransactionData extends BaseEntity {

    private Long id;
    private Long promotionTypeId;
    private String promotionDesc;
    private Long empId;
    private String name;
    private String socialId;
    private Long categoryId;
    private Long oldRankId;
    private String oldRankDesc;
    private Long newRankId;
    private String newRankDesc;
    private Long rankTitleId;
    private String rankTitleDesc;
    private Long oldDegreeId;
    private String oldDegreeDesc;
    private Long newDegreeId;
    private String newDegreeDesc;
    private Integer oldMilitaryNumber;
    private Integer newMilitaryNumber;

    private String externalDecisionNumber;
    private Date externalDecisionDate;
    private String externalDecisionDateString;

    private String referring;
    private String exceptionalPromotionReason;

    private Date oldLastPromotionDate;
    private String oldLastPromotionDateString;
    private Date newLastPromotionDate;
    private String newLastPromotionDateString;
    private Date oldDueDate;
    private String oldDueDateString;
    private Date newDueDate;
    private String newDueDateString;

    private Long oldJobId;
    private String oldJobClassCode;
    private String oldJobClassDesc;
    private String oldJobCode;
    private String oldJobDesc;
    private Double oldSalary;
    private Double newSalary;
    private Long newJobId;
    private String newJobClassCode;
    private String newJobClassDesc;
    private String newJobCode;
    private String newJobDesc;
    private Long freezedJobId;
    private String freezedJobCode;
    private String freezedJobName;
    private String freezedJobUnitFullName;

    private Long oldUnitId;
    private String oldUnitFullName;
    private Long newUnitId;
    private String newUnitFullName;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Integer eflag;
    private Integer migFlag;
    private Long reportDetailId;
    private Date joiningDate;
    private String joiningDateString;
    private Integer regionChangedFlag;
    private String internalCopies;
    private String externalCopies;
    private String attachments;
    private Long basedOnTransactionId;
    private Long physicalUnitId;
    private String physicalUnitHkey;
    private String physicalUnitFullName;
    private Long officialUnitId;
    private String officialUnitFullName;
    private Long physicalRegionId;

    private PromotionTransaction promotionTransaction;

    public PromotionTransactionData() {
	promotionTransaction = new PromotionTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	promotionTransaction.setId(id);
    }

    @Basic
    @Column(name = "PROMOTION_TYPE_ID")
    public Long getPromotionTypeId() {
	return promotionTypeId;
    }

    public void setPromotionTypeId(Long promotionTypeId) {
	this.promotionTypeId = promotionTypeId;
	promotionTransaction.setPromotionTypeId(promotionTypeId);
    }

    @Basic
    @Column(name = "PROMOTION_DESC")
    public String getPromotionDesc() {
	return promotionDesc;
    }

    public void setPromotionDesc(String promotionDesc) {
	this.promotionDesc = promotionDesc;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	promotionTransaction.setEmpId(empId);
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "SOCIAL_ID")
    public String getSocialId() {
	return socialId;
    }

    public void setSocialId(String socialId) {
	this.socialId = socialId;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	promotionTransaction.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "OLD_RANK_ID")
    public Long getOldRankId() {
	return oldRankId;
    }

    public void setOldRankId(Long oldRankId) {
	this.oldRankId = oldRankId;
	promotionTransaction.setOldRankId(oldRankId);
    }

    @Basic
    @Column(name = "OLD_RANK_DESC")
    public String getOldRankDesc() {
	return oldRankDesc;
    }

    public void setOldRankDesc(String oldRankDesc) {
	this.oldRankDesc = oldRankDesc;
    }

    @Basic
    @Column(name = "NEW_RANK_ID")
    public Long getNewRankId() {
	return newRankId;
    }

    public void setNewRankId(Long newRankId) {
	this.newRankId = newRankId;
	promotionTransaction.setNewRankId(newRankId);
    }

    @Basic
    @Column(name = "NEW_RANK_DESC")
    public String getNewRankDesc() {
	return newRankDesc;
    }

    public void setNewRankDesc(String newRankDesc) {
	this.newRankDesc = newRankDesc;
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
	promotionTransaction.setRankTitleId(rankTitleId);
    }

    @Basic
    @Column(name = "RANK_TITLE_DESC")
    public String getRankTitleDesc() {
	return rankTitleDesc;
    }

    public void setRankTitleDesc(String rankTitleDesc) {
	this.rankTitleDesc = rankTitleDesc;
    }

    @Basic
    @Column(name = "OLD_DEGREE_ID")
    public Long getOldDegreeId() {
	return oldDegreeId;
    }

    public void setOldDegreeId(Long oldDegreeId) {
	this.oldDegreeId = oldDegreeId;
	promotionTransaction.setOldDegreeId(oldDegreeId);
    }

    @Basic
    @Column(name = "OLD_DEGREE_DESC")
    public String getOldDegreeDesc() {
	return oldDegreeDesc;
    }

    public void setOldDegreeDesc(String oldDegreeDesc) {
	this.oldDegreeDesc = oldDegreeDesc;

    }

    @Basic
    @Column(name = "NEW_DEGREE_ID")
    public Long getNewDegreeId() {
	return newDegreeId;
    }

    public void setNewDegreeId(Long newDegreeId) {
	this.newDegreeId = newDegreeId;
	promotionTransaction.setNewDegreeId(newDegreeId);
    }

    @Basic
    @Column(name = "NEW_DEGREE_DESC")
    public String getNewDegreeDesc() {
	return newDegreeDesc;
    }

    public void setNewDegreeDesc(String newDegreeDesc) {
	this.newDegreeDesc = newDegreeDesc;

    }

    @Basic
    @Column(name = "OLD_MILITARY_NUMBER")
    public Integer getOldMilitaryNumber() {
	return oldMilitaryNumber;
    }

    public void setOldMilitaryNumber(Integer oldMilitaryNumber) {
	this.oldMilitaryNumber = oldMilitaryNumber;
	promotionTransaction.setOldMilitaryNumber(oldMilitaryNumber);
    }

    @Basic
    @Column(name = "NEW_MILITARY_NUMBER")
    public Integer getNewMilitaryNumber() {
	return newMilitaryNumber;
    }

    public void setNewMilitaryNumber(Integer newMilitaryNumber) {
	this.newMilitaryNumber = newMilitaryNumber;
	promotionTransaction.setNewMilitaryNumber(newMilitaryNumber);
    }

    @Basic
    @Column(name = "EXTERNAL_DECISION_NUMBER")
    public String getExternalDecisionNumber() {
	return externalDecisionNumber;
    }

    public void setExternalDecisionNumber(String externalDecisionNumber) {
	this.externalDecisionNumber = externalDecisionNumber;
	promotionTransaction.setExternalDecisionNumber(externalDecisionNumber);
    }

    @Basic
    @Column(name = "EXTERNAL_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExternalDecisionDate() {
	return externalDecisionDate;
    }

    public void setExternalDecisionDate(Date externalDecisionDate) {
	this.externalDecisionDate = externalDecisionDate;
	this.externalDecisionDateString = HijriDateService.getHijriDateString(externalDecisionDate);
	promotionTransaction.setExternalDecisionDate(externalDecisionDate);
    }

    @Transient
    public String getExternalDecisionDateString() {
	return externalDecisionDateString;
    }

    public void setExternalDecisionDateString(String externalDecisionDateString) {
	this.externalDecisionDateString = externalDecisionDateString;
	this.setExternalDecisionDate(HijriDateService.getHijriDate(externalDecisionDateString));
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	promotionTransaction.setReferring(referring);
    }

    @Basic
    @Column(name = "EXCEPTIONAL_PROMOTION_REASON")
    public String getExceptionalPromotionReason() {
	return exceptionalPromotionReason;
    }

    public void setExceptionalPromotionReason(String exceptionalPromotionReason) {
	this.exceptionalPromotionReason = exceptionalPromotionReason;
	promotionTransaction.setExceptionalPromotionReason(exceptionalPromotionReason);
    }

    @Basic
    @Column(name = "OLD_LAST_PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOldLastPromotionDate() {
	return oldLastPromotionDate;
    }

    public void setOldLastPromotionDate(Date oldLastPromotionDate) {
	this.oldLastPromotionDate = oldLastPromotionDate;
	this.oldLastPromotionDateString = HijriDateService.getHijriDateString(oldLastPromotionDate);
	promotionTransaction.setOldLastPromotionDate(oldLastPromotionDate);
    }

    @Transient
    public String getOldLastPromotionDateString() {
	return oldLastPromotionDateString;
    }

    public void setOldLastPromotionDateString(String oldLastPromotionDateString) {
	this.oldLastPromotionDateString = oldLastPromotionDateString;
	this.setOldLastPromotionDate(HijriDateService.getHijriDate(oldLastPromotionDateString));
    }

    @Basic
    @Column(name = "NEW_LAST_PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewLastPromotionDate() {
	return newLastPromotionDate;
    }

    public void setNewLastPromotionDate(Date newLastPromotionDate) {
	this.newLastPromotionDate = newLastPromotionDate;
	this.newLastPromotionDateString = HijriDateService.getHijriDateString(newLastPromotionDate);
	promotionTransaction.setNewLastPromotionDate(newLastPromotionDate);
    }

    @Transient
    public String getNewLastPromotionDateString() {
	return newLastPromotionDateString;
    }

    public void setNewLastPromotionDateString(String newLastPromotionDateString) {
	this.newLastPromotionDateString = newLastPromotionDateString;
	this.setNewLastPromotionDate(HijriDateService.getHijriDate(newLastPromotionDateString));
    }

    @Basic
    @Column(name = "OLD_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOldDueDate() {
	return oldDueDate;
    }

    public void setOldDueDate(Date oldDueDate) {
	this.oldDueDate = oldDueDate;
	this.oldDueDateString = HijriDateService.getHijriDateString(oldDueDate);
	promotionTransaction.setOldDueDate(oldDueDate);
    }

    @Transient
    public String getOldDueDateString() {
	return oldDueDateString;
    }

    public void setOldDueDateString(String oldDueDateString) {
	this.oldDueDateString = oldDueDateString;
	this.setOldDueDate(HijriDateService.getHijriDate(oldDueDateString));
    }

    @Basic
    @Column(name = "NEW_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getNewDueDate() {
	return newDueDate;
    }

    public void setNewDueDate(Date newDueDate) {
	this.newDueDate = newDueDate;
	this.newDueDateString = HijriDateService.getHijriDateString(newDueDate);
	promotionTransaction.setNewDueDate(newDueDate);
    }

    @Transient
    public String getNewDueDateString() {
	return newDueDateString;
    }

    public void setNewDueDateString(String newDueDateString) {
	this.newDueDateString = newDueDateString;
	this.setNewDueDate(HijriDateService.getHijriDate(newDueDateString));
    }

    @Basic
    @Column(name = "OLD_JOB_ID")
    public Long getOldJobId() {
	return oldJobId;
    }

    public void setOldJobId(Long oldJobId) {
	this.oldJobId = oldJobId;
	promotionTransaction.setOldJobId(oldJobId);
    }

    @Basic
    @Column(name = "OLD_JOB_CLASS_CODE")
    public String getOldJobClassCode() {
	return oldJobClassCode;
    }

    public void setOldJobClassCode(String oldJobClassCode) {
	this.oldJobClassCode = oldJobClassCode;
	promotionTransaction.setOldJobClassCode(oldJobClassCode);
    }

    @Basic
    @Column(name = "OLD_JOB_CLASS_DESC")
    public String getOldJobClassDesc() {
	return oldJobClassDesc;
    }

    public void setOldJobClassDesc(String oldJobClassDesc) {
	this.oldJobClassDesc = oldJobClassDesc;
	promotionTransaction.setOldJobClassDesc(oldJobClassDesc);
    }

    @Basic
    @Column(name = "OLD_JOB_CODE")
    public String getOldJobCode() {
	return oldJobCode;
    }

    public void setOldJobCode(String oldJobCode) {
	this.oldJobCode = oldJobCode;
	promotionTransaction.setOldJobCode(oldJobCode);
    }

    @Basic
    @Column(name = "OLD_JOB_DESC")
    public String getOldJobDesc() {
	return oldJobDesc;
    }

    public void setOldJobDesc(String oldJobDesc) {
	this.oldJobDesc = oldJobDesc;
	promotionTransaction.setOldJobDesc(oldJobDesc);
    }

    @Basic
    @Column(name = "NEW_JOB_ID")
    public Long getNewJobId() {
	return newJobId;
    }

    public void setNewJobId(Long newJobId) {
	this.newJobId = newJobId;
	promotionTransaction.setNewJobId(newJobId);
    }

    @Basic
    @Column(name = "NEW_JOB_CLASS_CODE")
    public String getNewJobClassCode() {
	return newJobClassCode;
    }

    public void setNewJobClassCode(String newJobClassCode) {
	this.newJobClassCode = newJobClassCode;
	promotionTransaction.setNewJobClassCode(newJobClassCode);
    }

    @Basic
    @Column(name = "NEW_JOB_CLASS_DESC")
    public String getNewJobClassDesc() {
	return newJobClassDesc;
    }

    public void setNewJobClassDesc(String newJobClassDesc) {
	this.newJobClassDesc = newJobClassDesc;
	promotionTransaction.setNewJobClassDesc(newJobClassDesc);
    }

    @Basic
    @Column(name = "NEW_JOB_CODE")
    public String getNewJobCode() {
	return newJobCode;
    }

    public void setNewJobCode(String newJobCode) {
	this.newJobCode = newJobCode;
	promotionTransaction.setNewJobCode(newJobCode);
    }

    @Basic
    @Column(name = "NEW_JOB_DESC")
    public String getNewJobDesc() {
	return newJobDesc;
    }

    public void setNewJobDesc(String newJobDesc) {
	this.newJobDesc = newJobDesc;
	promotionTransaction.setNewJobDesc(newJobDesc);
    }

    @Basic
    @Column(name = "FREEZED_JOB_ID")
    public Long getFreezedJobId() {
	return freezedJobId;
    }

    public void setFreezedJobId(Long freezedJobId) {
	this.freezedJobId = freezedJobId;
	this.promotionTransaction.setFreezedJobId(freezedJobId);
    }

    @Basic
    @Column(name = "FREEZED_JOB_CODE")
    public String getFreezedJobCode() {
	return freezedJobCode;
    }

    public void setFreezedJobCode(String freezedJobCode) {
	this.freezedJobCode = freezedJobCode;
	this.promotionTransaction.setFreezedJobCode(freezedJobCode);
    }

    @Basic
    @Column(name = "FREEZED_JOB_NAME")
    public String getFreezedJobName() {
	return freezedJobName;
    }

    public void setFreezedJobName(String freezedJobName) {
	this.freezedJobName = freezedJobName;
	this.promotionTransaction.setFreezedJobName(freezedJobName);
    }

    @Basic
    @Column(name = "FREEZED_JOB_UNIT_FULL_NAME")
    public String getFreezedJobUnitFullName() {
	return freezedJobUnitFullName;
    }

    public void setFreezedJobUnitFullName(String freezedJobUnitFullName) {
	this.freezedJobUnitFullName = freezedJobUnitFullName;
	this.promotionTransaction.setFreezedJobUnitFullName(freezedJobUnitFullName);
    }

    @Basic
    @Column(name = "OLD_UNIT_ID")
    public Long getOldUnitId() {
	return oldUnitId;
    }

    public void setOldUnitId(Long oldUnitId) {
	this.oldUnitId = oldUnitId;
	promotionTransaction.setOldUnitId(oldUnitId);
    }

    @Basic
    @Column(name = "OLD_UNIT_FULL_NAME")
    public String getOldUnitFullName() {
	return oldUnitFullName;
    }

    public void setOldUnitFullName(String oldUnitFullName) {
	this.oldUnitFullName = oldUnitFullName;
	promotionTransaction.setOldUnitFullName(oldUnitFullName);
    }

    @Basic
    @Column(name = "NEW_UNIT_ID")
    public Long getNewUnitId() {
	return newUnitId;
    }

    public void setNewUnitId(Long newUnitId) {
	this.newUnitId = newUnitId;
	promotionTransaction.setNewUnitId(newUnitId);
    }

    @Basic
    @Column(name = "NEW_UNIT_FULL_NAME")
    public String getNewUnitFullName() {
	return newUnitFullName;
    }

    public void setNewUnitFullName(String newUnitFullName) {
	this.newUnitFullName = newUnitFullName;
	promotionTransaction.setNewUnitFullName(newUnitFullName);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	promotionTransaction.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	promotionTransaction.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	promotionTransaction.setDecisionNumber(decisionNumber);
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
	promotionTransaction.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.setDecisionDate(HijriDateService.getHijriDate(decisionDateString));
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
	promotionTransaction.setEflag(eflag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
	promotionTransaction.setMigFlag(migFlag);
    }

    @Basic
    @Column(name = "REPORT_DETAIL_ID")
    public Long getReportDetailId() {
	return reportDetailId;
    }

    public void setReportDetailId(Long reportDetailId) {
	this.reportDetailId = reportDetailId;
	promotionTransaction.setReportDetailId(reportDetailId);
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
	this.joiningDateString = HijriDateService.getHijriDateString(joiningDate);
	promotionTransaction.setJoiningDate(joiningDate);
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.setJoiningDate(HijriDateService.getHijriDate(joiningDateString));
    }

    @Basic
    @Column(name = "OLD_SALARY")
    public Double getOldSalary() {
	return oldSalary;
    }

    public void setOldSalary(Double oldSalary) {
	this.oldSalary = oldSalary;
	promotionTransaction.setOldSalary(oldSalary);
    }

    @Basic
    @Column(name = "NEW_SALARY")
    public Double getNewSalary() {
	return newSalary;
    }

    public void setNewSalary(Double newSalary) {
	this.newSalary = newSalary;
	promotionTransaction.setNewSalary(newSalary);
    }

    @Basic
    @Column(name = "REGION_CHANGED_FLAG")
    public Integer getRegionChangedFlag() {
	return regionChangedFlag;
    }

    public void setRegionChangedFlag(Integer regionChangedFlag) {
	this.regionChangedFlag = regionChangedFlag;
	promotionTransaction.setRegionChangedFlag(regionChangedFlag);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	this.promotionTransaction.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	this.promotionTransaction.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.promotionTransaction.setAttachments(attachments);
    }

    @Basic
    @Column(name = "BASED_ON_TRANSACTION_ID")
    public Long getBasedOnTransactionId() {
	return basedOnTransactionId;
    }

    public void setBasedOnTransactionId(Long basedOnTransactionId) {
	this.basedOnTransactionId = basedOnTransactionId;
	this.promotionTransaction.setBasedOnTransactionId(basedOnTransactionId);
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_FULL_NAME")
    public String getPhysicalUnitFullName() {
	return physicalUnitFullName;
    }

    public void setPhysicalUnitFullName(String physicalUnitFullName) {
	this.physicalUnitFullName = physicalUnitFullName;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_FULL_NAME")
    public String getOfficialUnitFullName() {
	return officialUnitFullName;
    }

    public void setOfficialUnitFullName(String officialUnitFullName) {
	this.officialUnitFullName = officialUnitFullName;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_ID")
    public Long getPhysicalUnitId() {
	return physicalUnitId;
    }

    public void setPhysicalUnitId(Long physicalUnitId) {
	this.physicalUnitId = physicalUnitId;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_HKEY")
    public String getPhysicalUnitHkey() {
	return physicalUnitHkey;
    }

    public void setPhysicalUnitHkey(String physicalUnitHkey) {
	this.physicalUnitHkey = physicalUnitHkey;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_ID")
    public Long getOfficialUnitId() {
	return officialUnitId;
    }

    public void setOfficialUnitId(Long officialUnitId) {
	this.officialUnitId = officialUnitId;
    }

    @Basic
    @Column(name = "PHYSICAL_REGION_ID")
    public Long getPhysicalRegionId() {
	return physicalRegionId;
    }

    public void setPhysicalRegionId(Long physicalRegionId) {
	this.physicalRegionId = physicalRegionId;
    }

    @Transient
    public PromotionTransaction getPromotionTransaction() {
	return promotionTransaction;
    }

    public void setPromotionTransaction(PromotionTransaction promotionTransaction) {
	this.promotionTransaction = promotionTransaction;
    }

}