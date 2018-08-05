package com.code.dal.orm.hcm.recruitments;

import java.io.Serializable;
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
import javax.persistence.Transient;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_recruitmentTransaction_getRecruitmentTransactionByEmployeeIdAndRecDateAndRecTypes",
		query = " select r from RecruitmentTransaction r where " +
			" :P_EMPLOYEE_ID = r.employeeId" +
			" and (:P_RECRUITMENT_DATE_FROM_FLAG = -1 or to_date(:P_RECRUITMENT_DATE_FROM, 'MI/MM/YYYY') <= r.recruitmentDate) " +
			" and (:P_RECRUITMENT_DATE_TO_FLAG = -1 or to_date(:P_RECRUITMENT_DATE_TO, 'MI/MM/YYYY') >= r.recruitmentDate) " +
			" and (:P_RECRUITMENT_TYPE_FLAG = -1 or r.recruitmentType in (:P_RECRUITMENT_TYPES)) " +
			" order by r.id ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_REC_TRANSACTIONS")
public class RecruitmentTransaction extends AuditEntity implements Serializable, UpdatableAuditEntity {
    private Long id;
    private String basedOnOrderNumber;
    private Date basedOnOrderDate;
    private String basedOnOrderDateString;
    private String basedOn;
    private Long categoryId;
    private Long employeeId;
    private Long jobId;
    private String transJobCode;
    private String transJobName;
    private String transUnitFullName;
    private Long rankId;
    private Long rankTitleId;
    private Long degreeId;
    private Date firstRecruitmentDate;
    private String firstRecruitmentDateString;
    private Date recruitmentDate;
    private String recruitmentDateString;
    private Date lastPromotionDate;
    private String lastPromotionDateString;
    private Integer seniorityMonths;
    private Integer seniorityDays;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String transMinorSpecializationDesc;
    private Integer graduationGroupNumber;
    private Integer graduationGroupPlaceId;
    private Long recruitmentRegionId;
    private Double transSalary;
    private Double transTransportationAllowance;
    private Integer etrFlag;
    private Integer migrationFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private String internalCopies;
    private String externalCopies;
    private Date joiningDate;
    private String joiningDateString;
    private String directedToJobName;
    private String qualificationLevelReward;
    private String referring;
    private Integer recruitmentType;
    private Long recruitmentClass;
    private String remarks;
    private String attachments;
    private String externalPartyMovedFrom;

    @SequenceGenerator(name = "HCMRecSeq",
	    sequenceName = "HCM_REC_SEQ")
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
    @Column(name = "BASED_ON_ORDER_NUMBER")
    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
    }

    @Basic
    @Column(name = "BASED_ON_ORDER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getBasedOnOrderDate() {
	return basedOnOrderDate;
    }

    public void setBasedOnOrderDate(Date basedOnOrderDate) {
	this.basedOnOrderDate = basedOnOrderDate;
	this.basedOnOrderDateString = HijriDateService.getHijriDateString(basedOnOrderDate);
    }

    @Transient
    public String getBasedOnOrderDateString() {
	return basedOnOrderDateString;
    }

    public void setBasedOnOrderDateString(String basedOnOrderDateString) {
	this.basedOnOrderDateString = basedOnOrderDateString;
	this.basedOnOrderDate = HijriDateService.getHijriDate(basedOnOrderDateString);
    }

    @Basic
    @Column(name = "BASED_ON")
    public String getBasedOn() {
	return basedOn;
    }

    public void setBasedOn(String basedOn) {
	this.basedOn = basedOn;
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
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    @Basic
    @Column(name = "TRANS_JOB_CODE")
    public String getTransJobCode() {
	return transJobCode;
    }

    public void setTransJobCode(String transJobCode) {
	this.transJobCode = transJobCode;
    }

    @Basic
    @Column(name = "TRANS_JOB_NAME")
    public String getTransJobName() {
	return transJobName;
    }

    public void setTransJobName(String transJobName) {
	this.transJobName = transJobName;
    }

    @Basic
    @Column(name = "TRANS_UNIT_FULL_NAME")
    public String getTransUnitFullName() {
	return transUnitFullName;
    }

    public void setTransUnitFullName(String transUnitFullName) {
	this.transUnitFullName = transUnitFullName;
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
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
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
    @Column(name = "FIRST_RECRUITMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFirstRecruitmentDate() {
	return firstRecruitmentDate;
    }

    public void setFirstRecruitmentDate(Date firstRecruitmentDate) {
	this.firstRecruitmentDate = firstRecruitmentDate;
	this.firstRecruitmentDateString = HijriDateService.getHijriDateString(firstRecruitmentDate);
    }

    @Transient
    public String getFirstRecruitmentDateString() {
	return firstRecruitmentDateString;
    }

    public void setFirstRecruitmentDateString(String firstRecruitmentDateString) {
	this.firstRecruitmentDateString = firstRecruitmentDateString;
	this.firstRecruitmentDate = HijriDateService.getHijriDate(firstRecruitmentDateString);
    }

    @Basic
    @Column(name = "RECRUITMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecruitmentDate() {
	return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
	this.recruitmentDate = recruitmentDate;
	this.recruitmentDateString = HijriDateService.getHijriDateString(recruitmentDate);
    }

    @Transient
    public String getRecruitmentDateString() {
	return recruitmentDateString;
    }

    public void setRecruitmentDateString(String recruitmentDateString) {
	this.recruitmentDateString = recruitmentDateString;
	this.recruitmentDate = HijriDateService.getHijriDate(recruitmentDateString);
    }

    @Basic
    @Column(name = "LAST_PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastPromotionDate() {
	return lastPromotionDate;
    }

    public void setLastPromotionDate(Date lastPromotionDate) {
	this.lastPromotionDate = lastPromotionDate;
	this.lastPromotionDateString = HijriDateService.getHijriDateString(lastPromotionDate);
    }

    @Transient
    public String getLastPromotionDateString() {
	return lastPromotionDateString;
    }

    public void setLastPromotionDateString(String lastPromotionDateString) {
	this.lastPromotionDateString = lastPromotionDateString;
	this.lastPromotionDate = HijriDateService.getHijriDate(lastPromotionDateString);
    }

    @Basic
    @Column(name = "SENIORITY_MONTHS")
    public Integer getSeniorityMonths() {
	return seniorityMonths;
    }

    public void setSeniorityMonths(Integer seniorityMonths) {
	this.seniorityMonths = seniorityMonths;
    }

    @Basic
    @Column(name = "SENIORITY_DAYS")
    public Integer getSeniorityDays() {
	return seniorityDays;
    }

    public void setSeniorityDays(Integer seniorityDays) {
	this.seniorityDays = seniorityDays;
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
    @Column(name = "TRANS_MINOR_SPEC_DESC")
    public String getTransMinorSpecializationDesc() {
	return transMinorSpecializationDesc;
    }

    public void setTransMinorSpecializationDesc(String transMinorSpecializationDesc) {
	this.transMinorSpecializationDesc = transMinorSpecializationDesc;
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_NUMBER")
    public Integer getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupNumber(Integer graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_PLACE_ID")
    public Integer getGraduationGroupPlaceId() {
	return graduationGroupPlaceId;
    }

    public void setGraduationGroupPlaceId(Integer graduationGroupPlaceId) {
	this.graduationGroupPlaceId = graduationGroupPlaceId;
    }

    @Basic
    @Column(name = "REC_REGION_ID")
    public Long getRecruitmentRegionId() {
	return recruitmentRegionId;
    }

    public void setRecruitmentRegionId(Long recruitmentRegionId) {
	this.recruitmentRegionId = recruitmentRegionId;
    }

    @Basic
    @Column(name = "TRANS_SALARY")
    public Double getTransSalary() {
	return transSalary;
    }

    public void setTransSalary(Double transSalary) {
	this.transSalary = transSalary;
    }

    @Basic
    @Column(name = "TRANS_TRANSPORTATION_ALLOWANCE")
    public Double getTransTransportationAllowance() {
	return transTransportationAllowance;
    }

    public void setTransTransportationAllowance(Double transTransportationAllowance) {
	this.transTransportationAllowance = transTransportationAllowance;
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEtrFlag() {
	return etrFlag;
    }

    public void setEtrFlag(Integer etrFlag) {
	this.etrFlag = etrFlag;
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigrationFlag() {
	return migrationFlag;
    }

    public void setMigrationFlag(Integer migrationFlag) {
	this.migrationFlag = migrationFlag;
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
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
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
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
	this.joiningDateString = HijriDateService.getHijriDateString(joiningDate);
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
    }

    @Basic
    @Column(name = "QUALIFICATION_LEVEL_REWARD")
    public String getQualificationLevelReward() {
	return qualificationLevelReward;
    }

    public void setQualificationLevelReward(String qualificationLevelReward) {
	this.qualificationLevelReward = qualificationLevelReward;
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
    @Column(name = "RECRUITMENT_TYPE")
    public Integer getRecruitmentType() {
	return recruitmentType;
    }

    public void setRecruitmentType(Integer recruitmentType) {
	this.recruitmentType = recruitmentType;
    }

    @Basic
    @Column(name = "RECRUITMENT_CLASS")
    public Long getRecruitmentClass() {
	return recruitmentClass;
    }

    public void setRecruitmentClass(Long recruitmentClass) {
	this.recruitmentClass = recruitmentClass;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {

	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;

    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_MOVED_FROM")
    public String getExternalPartyMovedFrom() {
	return externalPartyMovedFrom;

    }

    public void setExternalPartyMovedFrom(String externalPartyMovedFrom) {
	this.externalPartyMovedFrom = externalPartyMovedFrom;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "employeeId:" + employeeId + AUDIT_SEPARATOR +
		"joiningDate:" + joiningDate;
    }
}