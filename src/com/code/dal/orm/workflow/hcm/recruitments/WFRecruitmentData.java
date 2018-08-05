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
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "wf_recruitmentData_getEmployeesIdsInRunningRecruitmentProcesses",
		query = " select r.employeeId from WFRecruitmentData r, WFInstance i where " +
			" r.instanceId = i.instanceId " +
			" and i.status = 1 "),
	@NamedQuery(name = "wf_recruitmentData_getJobsIdsInRunningRecruitmentProcesses",
		query = " select r.jobId from WFRecruitmentData r, WFInstance i where " +
			" r.instanceId = i.instanceId " +
			" and i.status = 1 "),
	@NamedQuery(name = "wf_recruitmentData_getWFRecruitmentDataByInstanceId",
		query = " from WFRecruitmentData r " +
			" where r.instanceId in (:P_INSTANCE_ID) " +
			" and (:P_REC_REGION_ID = -1 or r.recruitmentRegionId = :P_REC_REGION_ID) " +
			" order by r.militaryNumber, r.recruitmentDate, r.rankId, r.employeeName "),
	@NamedQuery(name = "wf_recruitmentData_getRecruitmentsTasks",
		query = " select  t, i, p.processName, requester, " +
			" case when t.originalId <> t.assigneeId then " +
			" (delegator.firstName || ' ' || delegator.secondName || ' ' || delegator.thirdName || case when delegator.thirdName is null then '' else ' ' end || delegator.lastName) " +
			" else null end " +
			" from  WFTask t, WFInstance i, WFProcess p, EmployeeData requester, Employee delegator " +
			" where t.instanceId	= i.id " +
			"   and i.processId  	= p.id " +
			"   and i.requesterId 	= requester.id " +
			"   and t.originalId 	= delegator.id " +
			"   and t.action is null " +
			"   and p.processGroupId   = :P_PROCESS_GROUP_ID " +
			"   and t.assigneeId       = :P_ASSIGNEE_ID " +
			"   and t.assigneeWfRole   = :P_ASSIGNEE_WF_ROLE " +
			"   order by t.taskId ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "ETR_VW_WF_RECRUITMENTS")
@IdClass(WFRecruitmentId.class)
public class WFRecruitmentData extends BaseEntity implements Serializable {
    private Long instanceId;
    private String basedOnOrderNumber;
    private Date basedOnOrderDate;
    private String basedOnOrderDateString;
    private String basedOn;
    private Long categoryId;
    private Long employeeId;
    private String employeeName;
    private Long empRecruitmentRankId;
    private Long empRecruitmentMinorSpecId;
    private Integer militaryNumber;
    private Long recTrainingUnitId;
    private String recTrainingUnitFullName;
    private String recTrainingUnitHKey;
    private Long jobId;
    private String jobCode;
    private String jobName;
    private Long minorSpecId;
    private String minorSpecDescription;
    private String unitFullName;
    private String regionDescription;
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
    private Boolean recruitmentDocsFlagBoolean;
    private Integer recruitmentDocsFlag;
    private Long decisionApprovedId;
    private Integer graduationGroupNumber;
    private Integer graduationGroupPlace;
    private Long recruitmentRegionId;
    private String recruitmentRegionDesc;
    private Integer qualificationLevelReward;
    private String referring;
    private String internalCopies;
    private String externalCopies;

    private WFRecruitment wfRecruitment;

    public WFRecruitmentData() {
	wfRecruitment = new WFRecruitment();
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
	wfRecruitment.setInstanceId(instanceId);
    }

    @Basic
    @Column(name = "BASED_ON_ORDER_NUMBER")
    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
	wfRecruitment.setBasedOnOrderNumber(basedOnOrderNumber);
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
	wfRecruitment.setBasedOnOrderDate(basedOnOrderDate);
    }

    @Transient
    public String getBasedOnOrderDateString() {
	return basedOnOrderDateString;
    }

    public void setBasedOnOrderDateString(String basedOnOrderDateString) {
	this.basedOnOrderDateString = basedOnOrderDateString;
	this.basedOnOrderDate = HijriDateService.getHijriDate(basedOnOrderDateString);
	wfRecruitment.setBasedOnOrderDateString(basedOnOrderDateString);
    }

    @Basic
    @Column(name = "BASED_ON")
    public String getBasedOn() {
	return basedOn;
    }

    public void setBasedOn(String basedOn) {
	this.basedOn = basedOn;
	wfRecruitment.setBasedOn(basedOn);
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	wfRecruitment.setCategoryId(categoryId);
    }

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	wfRecruitment.setEmployeeId(employeeId);
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
    @Column(name = "EMP_RECRUITMENT_RANK_ID")
    public Long getEmpRecruitmentRankId() {
	return empRecruitmentRankId;
    }

    public void setEmpRecruitmentRankId(Long empRecruitmentRankId) {
	this.empRecruitmentRankId = empRecruitmentRankId;
    }

    @Basic
    @Column(name = "EMP_RECRUITMENT_MINOR_SPEC_ID")
    public Long getEmpRecruitmentMinorSpecId() {
	return empRecruitmentMinorSpecId;
    }

    public void setEmpRecruitmentMinorSpecId(Long empRecruitmentMinorSpecId) {
	this.empRecruitmentMinorSpecId = empRecruitmentMinorSpecId;
    }

    @Basic
    @Column(name = "MILITARY_NUMBER")
    public Integer getMilitaryNumber() {
	return militaryNumber;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
	this.militaryNumber = militaryNumber;
    }

    @Basic
    @Column(name = "REC_TRAINING_UNIT_ID")
    public Long getRecTrainingUnitId() {
	return recTrainingUnitId;
    }

    public void setRecTrainingUnitId(Long recTrainingUnitId) {
	this.recTrainingUnitId = recTrainingUnitId;
    }

    @Basic
    @Column(name = "REC_TRAINING_UNIT_FULL_NAME")
    public String getRecTrainingUnitFullName() {
	return recTrainingUnitFullName;
    }

    public void setRecTrainingUnitFullName(String recTrainingUnitFullName) {
	this.recTrainingUnitFullName = recTrainingUnitFullName;
    }

    @Basic
    @Column(name = "REC_TRAINING_UNIT_HKEY")
    public String getRecTrainingUnitHKey() {
	return recTrainingUnitHKey;
    }

    public void setRecTrainingUnitHKey(String recTrainingUnitHKey) {
	this.recTrainingUnitHKey = recTrainingUnitHKey;
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	wfRecruitment.setJobId(jobId);
    }

    @Basic
    @Column(name = "JOB_CODE")
    public String getJobCode() {
	return jobCode;
    }

    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
    }

    @Basic
    @Column(name = "JOB_NAME")
    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecId() {
	return minorSpecId;
    }

    public void setMinorSpecId(Long minorSpecId) {
	this.minorSpecId = minorSpecId;
    }

    @Basic
    @Column(name = "MINOR_SPEC_DESCRIPTION")
    public String getMinorSpecDescription() {
	return minorSpecDescription;
    }

    public void setMinorSpecDescription(String minorSpecDescription) {
	this.minorSpecDescription = minorSpecDescription;
    }

    @Basic
    @Column(name = "UNIT_FULL_NAME")
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    @Basic
    @Column(name = "REGION_DESCRIPTION")
    public String getRegionDescription() {
	return regionDescription;
    }

    public void setRegionDescription(String regionDescription) {
	this.regionDescription = regionDescription;
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	wfRecruitment.setRankId(rankId);
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
	wfRecruitment.setRankTitleId(rankTitleId);
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
	wfRecruitment.setDegreeId(degreeId);
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
	wfRecruitment.setFirstRecruitmentDate(firstRecruitmentDate);
    }

    @Transient
    public String getFirstRecruitmentDateString() {
	return firstRecruitmentDateString;
    }

    public void setFirstRecruitmentDateString(String firstRecruitmentDateString) {
	this.firstRecruitmentDateString = firstRecruitmentDateString;
	this.firstRecruitmentDate = HijriDateService.getHijriDate(firstRecruitmentDateString);
	wfRecruitment.setFirstRecruitmentDateString(firstRecruitmentDateString);
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
	wfRecruitment.setRecruitmentDate(recruitmentDate);
    }

    @Transient
    public String getRecruitmentDateString() {
	return recruitmentDateString;
    }

    public void setRecruitmentDateString(String recruitmentDateString) {
	this.recruitmentDateString = recruitmentDateString;
	this.recruitmentDate = HijriDateService.getHijriDate(recruitmentDateString);
	wfRecruitment.setRecruitmentDateString(recruitmentDateString);
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
	wfRecruitment.setLastPromotionDate(lastPromotionDate);
    }

    @Transient
    public String getLastPromotionDateString() {
	return lastPromotionDateString;
    }

    public void setLastPromotionDateString(String lastPromotionDateString) {
	this.lastPromotionDateString = lastPromotionDateString;
	this.lastPromotionDate = HijriDateService.getHijriDate(lastPromotionDateString);
	wfRecruitment.setLastPromotionDateString(lastPromotionDateString);
    }

    @Basic
    @Column(name = "SENIORITY_MONTHS")
    public Integer getSeniorityMonths() {
	return seniorityMonths;
    }

    public void setSeniorityMonths(Integer seniorityMonths) {
	this.seniorityMonths = seniorityMonths;
	wfRecruitment.setSeniorityMonths(seniorityMonths);
    }

    @Basic
    @Column(name = "SENIORITY_DAYS")
    public Integer getSeniorityDays() {
	return seniorityDays;
    }

    public void setSeniorityDays(Integer seniorityDays) {
	this.seniorityDays = seniorityDays;
	wfRecruitment.setSeniorityDays(seniorityDays);
    }

    @Transient
    public Boolean getRecruitmentDocsFlagBoolean() {
	return recruitmentDocsFlagBoolean;
    }

    public void setRecruitmentDocsFlagBoolean(Boolean recruitmentDocsFlagBoolean) {
	this.recruitmentDocsFlagBoolean = recruitmentDocsFlagBoolean;

	if (this.recruitmentDocsFlagBoolean == null || !this.recruitmentDocsFlagBoolean) {
	    this.recruitmentDocsFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.recruitmentDocsFlag = FlagsEnum.ON.getCode();
	}

	wfRecruitment.setRecruitmentDocsFlag(recruitmentDocsFlag);
    }

    @Basic
    @Column(name = "RECRUITMENT_DOCS_FLAG")
    public Integer getRecruitmentDocsFlag() {
	return recruitmentDocsFlag;
    }

    public void setRecruitmentDocsFlag(Integer recruitmentDocsFlag) {
	this.recruitmentDocsFlag = recruitmentDocsFlag;

	if (this.recruitmentDocsFlag == null || this.recruitmentDocsFlag == FlagsEnum.OFF.getCode())
	    this.recruitmentDocsFlagBoolean = false;
	else
	    this.recruitmentDocsFlagBoolean = true;

	wfRecruitment.setRecruitmentDocsFlag(recruitmentDocsFlag);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	wfRecruitment.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_NUMBER")
    public Integer getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupNumber(Integer graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
	wfRecruitment.setGraduationGroupNumber(graduationGroupNumber);
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_PLACE")
    public Integer getGraduationGroupPlace() {
	return graduationGroupPlace;
    }

    public void setGraduationGroupPlace(Integer graduationGroupPlace) {
	this.graduationGroupPlace = graduationGroupPlace;
	wfRecruitment.setGraduationGroupPlace(graduationGroupPlace);
    }

    @Basic
    @Column(name = "REC_REGION_ID")
    public Long getRecruitmentRegionId() {
	return recruitmentRegionId;
    }

    public void setRecruitmentRegionId(Long recruitmentRegionId) {
	this.recruitmentRegionId = recruitmentRegionId;
	wfRecruitment.setRecruitmentRegionId(recruitmentRegionId);
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
    @Column(name = "QUALIFICATION_LEVEL_REWARD")
    public Integer getQualificationLevelReward() {
	return qualificationLevelReward;
    }

    public void setQualificationLevelReward(Integer qualificationLevelReward) {
	this.qualificationLevelReward = qualificationLevelReward;
	wfRecruitment.setQualificationLevelReward(qualificationLevelReward);
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	wfRecruitment.setReferring(referring);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	wfRecruitment.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	wfRecruitment.setExternalCopies(externalCopies);
    }

    @Transient
    public WFRecruitment getWfRecruitment() {
	return wfRecruitment;
    }

    public void setWfRecruitment(WFRecruitment wfRecruitment) {
	this.wfRecruitment = wfRecruitment;
    }

}