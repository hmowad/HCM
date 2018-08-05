package com.code.dal.orm.hcm.recruitments;

import java.io.Serializable;
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

@NamedQueries({
	@NamedQuery(name = "hcm_recruitmentTransactionData_searchRecruitmentTransactionData",
		query = " select r from RecruitmentTransactionData r where " +
			" (:P_CATEGORIES_IDS_FLAG = -1 or r.categoryId in ( :P_CATEGORIES_IDS )) " +
			" and (:P_DECISION_NUMBER = '-1' or r.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_BASED_ON_ORDER_NUMBER = '-1' or r.basedOnOrderNumber = :P_BASED_ON_ORDER_NUMBER) " +
			" and (:P_DECISION_DATE_FROM_FLAG = -1 or to_date(:P_DECISION_DATE_FROM, 'MI/MM/YYYY') <= r.decisionDate) " +
			" and (:P_DECISION_DATE_TO_FLAG = -1 or to_date(:P_DECISION_DATE_TO, 'MI/MM/YYYY') >= r.decisionDate) " +
			" and (:P_RECRUITMENT_DATE_FROM_FLAG = -1 or to_date(:P_RECRUITMENT_DATE_FROM, 'MI/MM/YYYY') <= r.recruitmentDate) " +
			" and (:P_RECRUITMENT_DATE_TO_FLAG = -1 or to_date(:P_RECRUITMENT_DATE_TO, 'MI/MM/YYYY') >= r.recruitmentDate) " +
			" and (:P_GRADUATION_GROUP_NUMBER = -1 or r.graduationGroupNumber = :P_GRADUATION_GROUP_NUMBER) " +
			" and (:P_GRADUATION_GROUP_PLACE_ID = -1 or r.graduationGroupPlaceId = :P_GRADUATION_GROUP_PLACE_ID ) " +
			" and (r.id = (select min(rt.id) from RecruitmentTransactionData rt where (:P_EMPLOYEE_ID = -1 or rt.employeeId = :P_EMPLOYEE_ID) " +
			"               and rt.decisionNumber = r.decisionNumber and rt.decisionDate = r.decisionDate )) " +
			" and (:P_REGION_ID = -1 or r.empPhysicalRegionId = :P_REGION_ID) " +
			" and (:P_ETR_FLAG = -1 or r.etrFlag = :P_ETR_FLAG) " +
			" and (:P_RECRUITMENT_TYPE_FLAG = -1 or r.recruitmentType in (:P_RECRUITMENT_TYPES)) " +
			" order by r.id "),
	@NamedQuery(name = "hcm_recruitmentTransactionData_getRecruitmentTransactionDataByDecisionNumberAndDecisionDate",
		query = " select r from RecruitmentTransactionData r where " +
			" (:P_DECISION_NUMBER = '-1' or r.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_DECISION_DATE_FROM_FLAG = -1 or to_date(:P_DECISION_DATE_FROM, 'MI/MM/YYYY') <= r.decisionDate) " +
			" and (:P_DECISION_DATE_TO_FLAG = -1 or to_date(:P_DECISION_DATE_TO, 'MI/MM/YYYY') >= r.decisionDate) " +
			" and (r.etrFlag = 1) " +
			" order by r.employeeMilitaryNumber, r.rankId, r.recruitmentDate, r.employeeName "),
	@NamedQuery(name = "hcm_recruitmentTransactionData_getRecruitmentTransactionDataById",
		query = " select r from RecruitmentTransactionData r where " +
			" r.id = :P_TRANS_ID ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_REC_TRANSACTIONS")
public class RecruitmentTransactionData extends BaseEntity implements Serializable {
    private Long id;
    private String basedOnOrderNumber;
    private Date basedOnOrderDate;
    private String basedOnOrderDateString;
    private String basedOn;
    private Long categoryId;
    private String categoryDesc;
    private Long employeeId;
    private String employeeName;
    private Integer employeeMilitaryNumber;
    private Long jobId;
    private String transJobCode;
    private String transJobName;
    private String transUnitFullName;
    private Long rankId;
    private String rankDescription;
    private Long rankTitleId;
    private String rankTitleDesc;
    private Long degreeId;
    private String degreeDescription;
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
    private String graduationGroupPlaceName;
    private Long recruitmentRegionId;
    private String recruitmentRegionDesc;
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
    private Long empPhysicalRegionId;
    private String qualificationLevelReward;
    private String referring;
    private Integer recruitmentType;
    private Long recruitmentClass;
    private String remarks;
    private String attachments;
    private String externalPartyMovedFrom;

    private RecruitmentTransaction recruitmentTransaction;

    public RecruitmentTransactionData() {
	recruitmentTransaction = new RecruitmentTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	recruitmentTransaction.setId(id);
    }

    @Basic
    @Column(name = "BASED_ON_ORDER_NUMBER")
    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
	recruitmentTransaction.setBasedOnOrderNumber(basedOnOrderNumber);
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
	recruitmentTransaction.setBasedOnOrderDate(basedOnOrderDate);
    }

    @Transient
    public String getBasedOnOrderDateString() {
	return basedOnOrderDateString;
    }

    public void setBasedOnOrderDateString(String basedOnOrderDateString) {
	this.basedOnOrderDateString = basedOnOrderDateString;
	this.basedOnOrderDate = HijriDateService.getHijriDate(basedOnOrderDateString);
	recruitmentTransaction.setBasedOnOrderDateString(basedOnOrderDateString);
    }

    @Basic
    @Column(name = "BASED_ON")
    public String getBasedOn() {
	return basedOn;
    }

    public void setBasedOn(String basedOn) {
	this.basedOn = basedOn;
	recruitmentTransaction.setBasedOn(basedOn);
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	recruitmentTransaction.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "CATEGORY_DESC")
    public String getCategoryDesc() {
	return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
	this.categoryDesc = categoryDesc;
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	recruitmentTransaction.setEmployeeId(employeeId);
    }

    @Basic
    @Column(name = "EMPLOYEE_NAME")
    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    @Basic
    @Column(name = "EMPLOYEE_MILITARY_NUMBER")
    public Integer getEmployeeMilitaryNumber() {
	return employeeMilitaryNumber;
    }

    public void setEmployeeMilitaryNumber(Integer employeeMilitaryNumber) {
	this.employeeMilitaryNumber = employeeMilitaryNumber;
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	recruitmentTransaction.setJobId(jobId);
    }

    @Basic
    @Column(name = "TRANS_JOB_CODE")
    public String getTransJobCode() {
	return transJobCode;
    }

    public void setTransJobCode(String transJobCode) {
	this.transJobCode = transJobCode;
	recruitmentTransaction.setTransJobCode(transJobCode);
    }

    @Basic
    @Column(name = "TRANS_JOB_NAME")
    public String getTransJobName() {
	return transJobName;
    }

    public void setTransJobName(String transJobName) {
	this.transJobName = transJobName;
	recruitmentTransaction.setTransJobName(transJobName);
    }

    @Basic
    @Column(name = "TRANS_UNIT_FULL_NAME")
    public String getTransUnitFullName() {
	return transUnitFullName;
    }

    public void setTransUnitFullName(String transUnitFullName) {
	this.transUnitFullName = transUnitFullName;
	recruitmentTransaction.setTransUnitFullName(transUnitFullName);
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	recruitmentTransaction.setRankId(rankId);
    }

    @Basic
    @Column(name = "RANK_DESC")
    public String getRankDescription() {
	return rankDescription;
    }

    public void setRankDescription(String rankDescription) {
	this.rankDescription = rankDescription;
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
	recruitmentTransaction.setRankTitleId(rankTitleId);
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
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
	recruitmentTransaction.setDegreeId(degreeId);
    }

    @Basic
    @Column(name = "DEGREE_DESC")
    public String getDegreeDescription() {
	return degreeDescription;
    }

    public void setDegreeDescription(String degreeDescription) {
	this.degreeDescription = degreeDescription;
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
	recruitmentTransaction.setFirstRecruitmentDate(firstRecruitmentDate);
    }

    @Transient
    public String getFirstRecruitmentDateString() {
	return firstRecruitmentDateString;
    }

    public void setFirstRecruitmentDateString(String firstRecruitmentDateString) {
	this.firstRecruitmentDateString = firstRecruitmentDateString;
	this.firstRecruitmentDate = HijriDateService.getHijriDate(firstRecruitmentDateString);
	recruitmentTransaction.setFirstRecruitmentDateString(firstRecruitmentDateString);
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
	recruitmentTransaction.setRecruitmentDate(recruitmentDate);
    }

    @Transient
    public String getRecruitmentDateString() {
	return recruitmentDateString;
    }

    public void setRecruitmentDateString(String recruitmentDateString) {
	this.recruitmentDateString = recruitmentDateString;
	this.recruitmentDate = HijriDateService.getHijriDate(recruitmentDateString);
	recruitmentTransaction.setRecruitmentDateString(recruitmentDateString);
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
	recruitmentTransaction.setLastPromotionDate(lastPromotionDate);
    }

    @Transient
    public String getLastPromotionDateString() {
	return lastPromotionDateString;
    }

    public void setLastPromotionDateString(String lastPromotionDateString) {
	this.lastPromotionDateString = lastPromotionDateString;
	this.lastPromotionDate = HijriDateService.getHijriDate(lastPromotionDateString);
	recruitmentTransaction.setLastPromotionDateString(lastPromotionDateString);
    }

    @Basic
    @Column(name = "SENIORITY_MONTHS")
    public Integer getSeniorityMonths() {
	return seniorityMonths;
    }

    public void setSeniorityMonths(Integer seniorityMonths) {
	this.seniorityMonths = seniorityMonths;
	recruitmentTransaction.setSeniorityMonths(seniorityMonths);
    }

    @Basic
    @Column(name = "SENIORITY_DAYS")
    public Integer getSeniorityDays() {
	return seniorityDays;
    }

    public void setSeniorityDays(Integer seniorityDays) {
	this.seniorityDays = seniorityDays;
	recruitmentTransaction.setSeniorityDays(seniorityDays);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	recruitmentTransaction.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	recruitmentTransaction.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "TRANS_MINOR_SPEC_DESC")
    public String getTransMinorSpecializationDesc() {
	return transMinorSpecializationDesc;
    }

    public void setTransMinorSpecializationDesc(String transMinorSpecializationDesc) {
	this.transMinorSpecializationDesc = transMinorSpecializationDesc;
	recruitmentTransaction.setTransMinorSpecializationDesc(transMinorSpecializationDesc);
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_NUMBER")
    public Integer getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupNumber(Integer graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
	recruitmentTransaction.setGraduationGroupNumber(graduationGroupNumber);
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_PLACE_ID")
    public Integer getGraduationGroupPlaceId() {
	return graduationGroupPlaceId;
    }

    public void setGraduationGroupPlaceId(Integer graduationGroupPlaceId) {
	this.graduationGroupPlaceId = graduationGroupPlaceId;
	recruitmentTransaction.setGraduationGroupPlaceId(graduationGroupPlaceId);
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_PLACE_NAME")
    public String getGraduationGroupPlaceName() {
	return graduationGroupPlaceName;
    }

    public void setGraduationGroupPlaceName(String graduationGroupPlaceName) {
	this.graduationGroupPlaceName = graduationGroupPlaceName;
    }

    @Basic
    @Column(name = "REC_REGION_ID")
    public Long getRecruitmentRegionId() {
	return recruitmentRegionId;
    }

    public void setRecruitmentRegionId(Long recruitmentRegionId) {
	this.recruitmentRegionId = recruitmentRegionId;
	recruitmentTransaction.setRecruitmentRegionId(recruitmentRegionId);
    }

    @Basic
    @Column(name = "REGION_DESC")
    public String getRecruitmentRegionDesc() {
	return recruitmentRegionDesc;
    }

    public void setRecruitmentRegionDesc(String recruitmentRegionDesc) {
	this.recruitmentRegionDesc = recruitmentRegionDesc;
    }

    @Basic
    @Column(name = "TRANS_SALARY")
    public Double getTransSalary() {
	return transSalary;
    }

    public void setTransSalary(Double transSalary) {
	this.transSalary = transSalary;
	recruitmentTransaction.setTransSalary(transSalary);
    }

    @Basic
    @Column(name = "TRANS_TRANSPORTATION_ALLOWANCE")
    public Double getTransTransportationAllowance() {
	return transTransportationAllowance;
    }

    public void setTransTransportationAllowance(Double transTransportationAllowance) {
	this.transTransportationAllowance = transTransportationAllowance;
	recruitmentTransaction.setTransTransportationAllowance(transTransportationAllowance);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEtrFlag() {
	return etrFlag;
    }

    public void setEtrFlag(Integer etrFlag) {
	this.etrFlag = etrFlag;
	recruitmentTransaction.setEtrFlag(etrFlag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigrationFlag() {
	return migrationFlag;
    }

    public void setMigrationFlag(Integer migrationFlag) {
	this.migrationFlag = migrationFlag;
	recruitmentTransaction.setMigrationFlag(migrationFlag);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	recruitmentTransaction.setDecisionNumber(decisionNumber);
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
	recruitmentTransaction.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	recruitmentTransaction.setDecisionDateString(decisionDateString);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	recruitmentTransaction.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	recruitmentTransaction.setExternalCopies(externalCopies);
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
	recruitmentTransaction.setJoiningDate(joiningDate);
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
	recruitmentTransaction.setJoiningDateString(joiningDateString);
    }

    @Basic
    @Column(name = "DIRECTED_TO_JOB_NAME")
    public String getDirectedToJobName() {
	return directedToJobName;
    }

    public void setDirectedToJobName(String directedToJobName) {
	this.directedToJobName = directedToJobName;
	recruitmentTransaction.setDirectedToJobName(directedToJobName);
    }

    @Basic
    @Column(name = "EMP_PHYSICAL_REGION_ID")
    public Long getEmpPhysicalRegionId() {
	return empPhysicalRegionId;
    }

    public void setEmpPhysicalRegionId(Long empPhysicalRegionId) {
	this.empPhysicalRegionId = empPhysicalRegionId;
    }

    @Basic
    @Column(name = "QUALIFICATION_LEVEL_REWARD")
    public String getQualificationLevelReward() {
	return qualificationLevelReward;
    }

    public void setQualificationLevelReward(String qualificationLevelReward) {
	this.qualificationLevelReward = qualificationLevelReward;
	recruitmentTransaction.setQualificationLevelReward(qualificationLevelReward);
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	recruitmentTransaction.setReferring(referring);
    }

    @Basic
    @Column(name = "RECRUITMENT_TYPE")
    public Integer getRecruitmentType() {
	return recruitmentType;
    }

    public void setRecruitmentType(Integer recruitmentType) {
	this.recruitmentType = recruitmentType;
	recruitmentTransaction.setRecruitmentType(recruitmentType);
    }

    @Column(name = "RECRUITMENT_CLASS")
    public Long getRecruitmentClass() {
	return recruitmentClass;
    }

    public void setRecruitmentClass(Long recruitmentClass) {
	this.recruitmentClass = recruitmentClass;
	recruitmentTransaction.setRecruitmentClass(recruitmentClass);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	recruitmentTransaction.setAttachments(attachments);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	recruitmentTransaction.setRemarks(remarks);
    }

    @Basic
    @Column(name = "EXTERNAL_PARTY_MOVED_FROM")
    public String getExternalPartyMovedFrom() {
	return externalPartyMovedFrom;
    }

    public void setExternalPartyMovedFrom(String externalPartyMovedFrom) {
	this.externalPartyMovedFrom = externalPartyMovedFrom;
	recruitmentTransaction.setExternalPartyMovedFrom(externalPartyMovedFrom);
    }

    @Transient
    public RecruitmentTransaction getRecruitmentTransaction() {
	return recruitmentTransaction;
    }

    public void setRecruitmentTransaction(RecruitmentTransaction recruitmentTransaction) {
	this.recruitmentTransaction = recruitmentTransaction;
    }

}