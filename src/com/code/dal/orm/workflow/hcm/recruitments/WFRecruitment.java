package com.code.dal.orm.workflow.hcm.recruitments;

import java.io.Serializable;
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
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "wf_recruitment_checkExistingRecruitmentProcesses",
		query = " select r from WFRecruitment r, WFInstance i where " +
			" r.instanceId = i.instanceId " +
			" and i.status = 1 " +
			" and (r.employeeId in ( :P_EMPS_IDS ) or r.jobId in ( :P_JOBS_IDS )) " +
			" and (:P_EXCLUDED_INSTANCE_ID = -1 or r.instanceId <> :P_EXCLUDED_INSTANCE_ID) ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "WF_RECRUITMENTS")
@IdClass(WFRecruitmentId.class)
public class WFRecruitment extends BaseEntity implements Serializable {
    private Long instanceId;
    private Long employeeId;
    private String basedOnOrderNumber;
    private Date basedOnOrderDate;
    private String basedOnOrderDateString;
    private String basedOn;
    private Long categoryId;
    private Long jobId;
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
    private Integer recruitmentDocsFlag;
    private Long decisionApprovedId;
    private Integer graduationGroupNumber;
    private Integer graduationGroupPlace;
    private Long recruitmentRegionId;
    private Integer qualificationLevelReward;
    private String referring;
    private String internalCopies;
    private String externalCopies;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
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
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
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
    @Column(name = "RECRUITMENT_DOCS_FLAG")
    public Integer getRecruitmentDocsFlag() {
	return recruitmentDocsFlag;
    }

    public void setRecruitmentDocsFlag(Integer recruitmentDocsFlag) {
	this.recruitmentDocsFlag = recruitmentDocsFlag;
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
    @Column(name = "GRADUATION_GROUP_NUMBER")
    public Integer getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupNumber(Integer graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_PLACE")
    public Integer getGraduationGroupPlace() {
	return graduationGroupPlace;
    }

    public void setGraduationGroupPlace(Integer graduationGroupPlace) {
	this.graduationGroupPlace = graduationGroupPlace;
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
    @Column(name = "QUALIFICATION_LEVEL_REWARD")
    public Integer getQualificationLevelReward() {
	return qualificationLevelReward;
    }

    public void setQualificationLevelReward(Integer qualificationLevelReward) {
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
}