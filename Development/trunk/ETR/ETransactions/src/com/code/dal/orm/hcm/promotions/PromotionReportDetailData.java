package com.code.dal.orm.hcm.promotions;

import java.text.DecimalFormat;
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
import com.code.enums.RanksEnum;
import com.code.services.util.HijriDateService;

/**
 * 
 * The <code>PromotionReportDetailData</code> class represents the Promotion report detail data as details of the main report for all employees in detailed information.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_promotionReportDetailData_searchPromotionReportDetailData",
		query = " select pd from PromotionReportDetailData pd " +
			" where (:P_REPORT_ID = -1 or pd.reportId = :P_REPORT_ID) " +
			" and (:P_EMP_ID = -1 or pd.empId = :P_EMP_ID) " +
			" and (:P_EMP_NAME = '-1' or pd.empName like :P_EMP_NAME ) " +
			" and (:P_STATUS_FLAG = -1 or pd.status in (:P_STATUS)) " +
			" and (:P_MEDICAL_TEST_STATUSES_FLAG = -1 or (:P_MEDICAL_TEST_STATUSES_FLAG = 1 and pd.medicalTest in (:P_MEDICAL_TEST_STATUSES)) or (:P_MEDICAL_TEST_STATUSES_FLAG = 0 and pd.medicalTest in (:P_MEDICAL_TEST_STATUSES) or  pd.medicalTest IS NULL)) " +
			" order by pd.status desc, pd.promotionDueDate, pd.militaryNumber, pd.oldRankId, pd.recruitmentDate ,pd.oldDegreeId ,pd.oldJobClassCode,pd.empName "),

	@NamedQuery(name = "hcm_promotionReportDetailData_searchPromotionReportDetailDataBySocialIds",
		query = " select pd from PromotionReportDetailData pd, PromotionReport r " +
			" where pd.reportId = r.id " +
			" and r.status <> 20 " +
			" and r.promotionTypeId = 1 " +
			" and pd.empSocialID in (:P_EMPS_SOCIAL_IDS) " +
			" order by pd.id"),

	@NamedQuery(name = "hcm_PromotionReportDetailData_getPromotionReportDetailDataOfficersByRoyalDateAndRoyalNumber",
		query = " select d from PromotionReportDetailData d, PromotionReport r " +
			" where (r.categoryId = 1) " +
			" and (d.reportId = r.id) " +
			" and (:P_ROYAL_NUMBER = '-1' or d.externalDecisionNumber = :P_ROYAL_NUMBER) " +
			" and (:P_ROYAL_DATE_FLAG = '-1' or to_date(:P_ROYAL_DATE, 'MI/MM/YYYY') = d.externalDecisionDate) " +
			" order by d.externalDecisionDate")
})

@Entity
@Table(name = "HCM_VW_PRM_REPORTS_DETAILS")
public class PromotionReportDetailData extends BaseEntity {

    private Long id;
    private Long reportId;
    private Long empId;
    private String empName;
    private String empSocialID;
    private Long oldRankId;
    private String oldRankDesc;
    private Long newRankId;
    private String newRankDesc;
    private Long oldDegreeId;
    private String oldDegreeDesc;
    private Long newDegreeId;
    private String newDegreeDesc;
    private Date promotionDueDate;
    private String promotionDueDateString;
    private Integer militaryNumber;

    private String externalDecisionNumber;
    private Date externalDecisionDate;
    private String externalDecisionDateString;

    private String referring;
    private String exceptionalPromotionReason;
    private Long basedOnTransactionId;

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
    private Integer managerFlag;
    private Boolean managerFlagBoolean;
    private Long status;
    private Integer requirementsFlag;
    private Boolean requirementsFlagBoolean;
    private Integer judgmentFlag;
    private Boolean judgmentFlagBoolean;
    private Integer retirementFlag;
    private Boolean retirementFlagBoolean;
    private Integer medicalTest;
    private String medicalTestExemptionReason;
    private Integer evaluationResult;
    private Long rankTitleId;
    private String rankTitleName;
    private Double qualificationDegree;
    private String qualificationPersonDegree;
    private Double seniorityDegree;
    private Double fieldServiceDegree;
    private Integer fileDegree;
    private Double trainingDegree;
    private Integer promotionExamDegree;
    private Integer leaderDegree;
    private Long qualificationApprovedId;
    private String qualificationApprovedName;
    private Long fileApprovedId;
    private String fileApprovedName;
    private Long trainingApprovedId;
    private String trainingApprovedName;
    private Integer serviceDegreeDay;
    private Integer serviceDegreeMonth;
    private Integer serviceDegreeYear;
    private Double educationDegree;
    private Double evaluationDegree;
    private String gender;
    private Date recruitmentDate;
    private Date birthDate;
    private String attachments;
    private String remarks;
    private String nonCandidateReasons;
    private Integer bonusFlag;
    private Boolean bonusFlagBoolean;
    private Integer experincePresonsYear;
    private Integer experincePresonsMonth;
    private Integer experincePresonsDay;
    private Integer noVacantFlag;
    private Boolean noVacantFlagBoolean;
    private String promotionCommitteeOpinion;

    private Integer graduationEvaluation;
    private Integer graduationOrder;
    private Double graduationDegree;
    private Long drugsRequestId;

    private PromotionReportDetail promotionReportDetail;

    public PromotionReportDetailData() {
	promotionReportDetail = new PromotionReportDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	promotionReportDetail.setId(id);
    }

    @Basic
    @Column(name = "REPORT_ID")
    public Long getReportId() {
	return reportId;
    }

    public void setReportId(Long reportId) {
	this.reportId = reportId;
	promotionReportDetail.setReportId(reportId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	promotionReportDetail.setEmpId(empId);
    }

    @Basic
    @Column(name = "EMP_NAME")
    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "EMP_SOCIAL_ID")
    public String getEmpSocialID() {
	return empSocialID;
    }

    public void setEmpSocialID(String empSocialID) {
	this.empSocialID = empSocialID;
    }

    @Basic
    @Column(name = "OLD_RANK_ID")
    public Long getOldRankId() {
	return oldRankId;
    }

    public void setOldRankId(Long oldRankId) {
	this.oldRankId = oldRankId;
	promotionReportDetail.setOldRankId(oldRankId);
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
	promotionReportDetail.setNewRankId(newRankId);
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
    @Column(name = "OLD_DEGREE_ID")
    public Long getOldDegreeId() {
	return oldDegreeId;
    }

    public void setOldDegreeId(Long oldDegreeId) {
	this.oldDegreeId = oldDegreeId;
	promotionReportDetail.setOldDegreeId(oldDegreeId);
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
	promotionReportDetail.setNewDegreeId(newDegreeId);
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
    @Column(name = "PROMOTION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPromotionDueDate() {
	return promotionDueDate;
    }

    public void setPromotionDueDate(Date promotionDueDate) {
	this.promotionDueDate = promotionDueDate;
	this.promotionDueDateString = HijriDateService.getHijriDateString(promotionDueDate);
	promotionReportDetail.setPromotionDueDate(promotionDueDate);
    }

    @Transient
    public String getPromotionDueDateString() {
	return promotionDueDateString;
    }

    public void setPromotionDueDateString(String promotionDueDateString) {
	this.promotionDueDateString = promotionDueDateString;
	this.promotionDueDate = HijriDateService.getHijriDate(promotionDueDateString);
    }

    @Basic
    @Column(name = "MILITARY_NUMBER")
    public Integer getMilitaryNumber() {
	return militaryNumber;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
	this.militaryNumber = militaryNumber;
	promotionReportDetail.setMilitaryNumber(militaryNumber);
    }

    @Basic
    @Column(name = "EXTERNAL_DECISION_NUMBER")
    public String getExternalDecisionNumber() {
	return externalDecisionNumber;
    }

    public void setExternalDecisionNumber(String externalDecisionNumber) {
	this.externalDecisionNumber = externalDecisionNumber;
	promotionReportDetail.setExternalDecisionNumber(externalDecisionNumber);
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
	promotionReportDetail.setExternalDecisionDate(externalDecisionDate);

    }

    @Transient
    public String getExternalDecisionDateString() {
	return externalDecisionDateString;
    }

    public void setExternalDecisionDateString(String externalDecisionDateString) {
	this.externalDecisionDateString = externalDecisionDateString;
	this.externalDecisionDate = HijriDateService.getHijriDate(externalDecisionDateString);
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	promotionReportDetail.setReferring(referring);
    }

    @Basic
    @Column(name = "EXCEPTIONAL_PROMOTION_REASON")
    public String getExceptionalPromotionReason() {
	return exceptionalPromotionReason;
    }

    public void setExceptionalPromotionReason(String exceptionalPromotionReason) {
	this.exceptionalPromotionReason = exceptionalPromotionReason;
	promotionReportDetail.setExceptionalPromotionReason(exceptionalPromotionReason);
    }

    @Basic
    @Column(name = "BASED_ON_TRANSACTION_ID")
    public Long getBasedOnTransactionId() {
	return basedOnTransactionId;
    }

    public void setBasedOnTransactionId(Long basedOnTransactionId) {
	this.basedOnTransactionId = basedOnTransactionId;
	promotionReportDetail.setBasedOnTransactionId(basedOnTransactionId);
    }

    @Basic
    @Column(name = "OLD_JOB_ID")
    public Long getOldJobId() {
	return oldJobId;
    }

    public void setOldJobId(Long oldJobId) {
	this.oldJobId = oldJobId;
	promotionReportDetail.setOldJobId(oldJobId);
    }

    @Basic
    @Column(name = "OLD_JOB_CLASS_CODE")
    public String getOldJobClassCode() {
	return oldJobClassCode;
    }

    public void setOldJobClassCode(String oldJobClassCode) {
	this.oldJobClassCode = oldJobClassCode;
	promotionReportDetail.setOldJobClassCode(oldJobClassCode);
    }

    @Basic
    @Column(name = "OLD_JOB_CLASS_DESC")
    public String getOldJobClassDesc() {
	return oldJobClassDesc;
    }

    public void setOldJobClassDesc(String oldJobClassDesc) {
	this.oldJobClassDesc = oldJobClassDesc;
	promotionReportDetail.setOldJobClassDesc(oldJobClassDesc);
    }

    @Basic
    @Column(name = "OLD_JOB_CODE")
    public String getOldJobCode() {
	return oldJobCode;
    }

    public void setOldJobCode(String oldJobCode) {
	this.oldJobCode = oldJobCode;
	promotionReportDetail.setOldJobCode(oldJobCode);
    }

    @Basic
    @Column(name = "OLD_JOB_DESC")
    public String getOldJobDesc() {
	return oldJobDesc;
    }

    public void setOldJobDesc(String oldJobDesc) {
	this.oldJobDesc = oldJobDesc;
	promotionReportDetail.setOldJobDesc(oldJobDesc);
    }

    @Basic
    @Column(name = "NEW_JOB_ID")
    public Long getNewJobId() {
	return newJobId;
    }

    public void setNewJobId(Long newJobId) {
	this.newJobId = newJobId;
	promotionReportDetail.setNewJobId(newJobId);
    }

    @Basic
    @Column(name = "NEW_JOB_CLASS_CODE")
    public String getNewJobClassCode() {
	return newJobClassCode;
    }

    public void setNewJobClassCode(String newJobClassCode) {
	this.newJobClassCode = newJobClassCode;
	promotionReportDetail.setNewJobClassCode(newJobClassCode);
    }

    @Basic
    @Column(name = "NEW_JOB_CLASS_DESC")
    public String getNewJobClassDesc() {
	return newJobClassDesc;
    }

    public void setNewJobClassDesc(String newJobClassDesc) {
	this.newJobClassDesc = newJobClassDesc;
	promotionReportDetail.setNewJobClassDesc(newJobClassDesc);
    }

    @Basic
    @Column(name = "NEW_JOB_CODE")
    public String getNewJobCode() {
	return newJobCode;
    }

    public void setNewJobCode(String newJobCode) {
	this.newJobCode = newJobCode;
	promotionReportDetail.setNewJobCode(newJobCode);
    }

    @Basic
    @Column(name = "NEW_JOB_DESC")
    public String getNewJobDesc() {
	return newJobDesc;
    }

    public void setNewJobDesc(String newJobDesc) {
	this.newJobDesc = newJobDesc;
	promotionReportDetail.setNewJobDesc(newJobDesc);
    }

    @Basic
    @Column(name = "FREEZED_JOB_ID")
    public Long getFreezedJobId() {
	return freezedJobId;
    }

    public void setFreezedJobId(Long freezedJobId) {
	this.freezedJobId = freezedJobId;
	this.promotionReportDetail.setFreezedJobId(freezedJobId);
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
    @Column(name = "MANAGER_FLAG")
    public Integer getManagerFlag() {
	return managerFlag;
    }

    public void setManagerFlag(Integer managerFlag) {
	this.managerFlag = managerFlag;
	promotionReportDetail.setManagerFlag(managerFlag);
	if (this.managerFlag == null || this.managerFlag == FlagsEnum.OFF.getCode()) {
	    this.managerFlagBoolean = false;
	} else {
	    this.managerFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getManagerFlagBoolean() {
	return managerFlagBoolean;
    }

    public void setManagerFlagBoolean(Boolean managerFlagBoolean) {
	this.managerFlagBoolean = managerFlagBoolean;
	if (this.managerFlagBoolean == false) {
	    setManagerFlag(FlagsEnum.OFF.getCode());
	} else {
	    setManagerFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "STATUS")
    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
	promotionReportDetail.setStatus(status);
    }

    @Basic
    @Column(name = "REQUIREMENTS_FLAG")
    public Integer getRequirementsFlag() {
	return requirementsFlag;
    }

    public void setRequirementsFlag(Integer requirementsFlag) {
	this.requirementsFlag = requirementsFlag;
	promotionReportDetail.setRequirementsFlag(requirementsFlag);
	if (this.requirementsFlag == null || this.requirementsFlag == FlagsEnum.OFF.getCode()) {
	    this.requirementsFlagBoolean = false;
	} else {
	    this.requirementsFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getRequirementsFlagBoolean() {
	return requirementsFlagBoolean;
    }

    public void setRequirementsFlagBoolean(Boolean requirementsFlagBoolean) {
	this.requirementsFlagBoolean = requirementsFlagBoolean;
	if (this.requirementsFlagBoolean == false) {
	    setRequirementsFlag(FlagsEnum.OFF.getCode());
	} else {
	    setRequirementsFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "JUDGMENT_FLAG")
    public Integer getJudgmentFlag() {
	return judgmentFlag;
    }

    public void setJudgmentFlag(Integer judgmentFlag) {
	this.judgmentFlag = judgmentFlag;
	promotionReportDetail.setJudgmentFlag(judgmentFlag);
	if (this.judgmentFlag == null || this.judgmentFlag == FlagsEnum.OFF.getCode()) {
	    this.judgmentFlagBoolean = false;
	} else {
	    this.judgmentFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getJudgmentFlagBoolean() {
	return judgmentFlagBoolean;
    }

    public void setJudgmentFlagBoolean(Boolean judgmentFlagBoolean) {
	this.judgmentFlagBoolean = judgmentFlagBoolean;
	if (this.judgmentFlagBoolean == false) {
	    setJudgmentFlag(FlagsEnum.OFF.getCode());
	} else {
	    setJudgmentFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "RETIREMENT_FLAG")
    public Integer getRetirementFlag() {
	return retirementFlag;
    }

    public void setRetirementFlag(Integer retirementFlag) {
	this.retirementFlag = retirementFlag;
	promotionReportDetail.setRetirementFlag(retirementFlag);
	if (this.retirementFlag == null || this.retirementFlag == FlagsEnum.OFF.getCode()) {
	    this.retirementFlagBoolean = false;
	} else {
	    this.retirementFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getRetirementFlagBoolean() {
	return retirementFlagBoolean;
    }

    public void setRetirementFlagBoolean(Boolean retirementFlagBoolean) {
	this.retirementFlagBoolean = retirementFlagBoolean;
	if (this.retirementFlagBoolean == false) {
	    setRetirementFlag(FlagsEnum.OFF.getCode());
	} else {
	    setRetirementFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "MEDICAL_TEST")
    public Integer getMedicalTest() {
	return medicalTest;
    }

    public void setMedicalTest(Integer medicalTest) {
	this.medicalTest = medicalTest;
	promotionReportDetail.setMedicalTest(medicalTest);
    }

    @Basic
    @Column(name = "MEDICAL_TEST_EXEMPTION_REASON")
    public String getMedicalTestExemptionReason() {
	return medicalTestExemptionReason;
    }

    public void setMedicalTestExemptionReason(String medicalTestExemptionReason) {
	this.medicalTestExemptionReason = medicalTestExemptionReason;
	promotionReportDetail.setMedicalTestExemptionReason(medicalTestExemptionReason);
    }

    @Basic
    @Column(name = "EVALUATION_RESULT")
    public Integer getEvaluationResult() {
	return evaluationResult;
    }

    public void setEvaluationResult(Integer evaluationResult) {
	this.evaluationResult = evaluationResult;
	promotionReportDetail.setEvaluationResult(evaluationResult);
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
	promotionReportDetail.setRankTitleId(rankTitleId);
    }

    @Basic
    @Column(name = "RANK_TITLE_NAME")
    public String getRankTitleName() {
	return rankTitleName;
    }

    public void setRankTitleName(String rankTitleName) {
	this.rankTitleName = rankTitleName;
    }

    @Basic
    @Column(name = "QUALIFICATION_DEGREE")
    public Double getQualificationDegree() {
	return qualificationDegree;
    }

    public void setQualificationDegree(Double qualificationDegree) {
	this.qualificationDegree = qualificationDegree;
	promotionReportDetail.setQualificationDegree(qualificationDegree);
    }

    @Basic
    @Column(name = "SENIORITY_DEGREE")
    public Double getSeniorityDegree() {
	return seniorityDegree;
    }

    public void setSeniorityDegree(Double seniorityDegree) {
	this.seniorityDegree = seniorityDegree;
	promotionReportDetail.setSeniorityDegree(seniorityDegree);
    }

    @Basic
    @Column(name = "FIELD_SERVICE_DEGREE")
    public Double getFieldServiceDegree() {
	return fieldServiceDegree;
    }

    public void setFieldServiceDegree(Double fieldServiceDegree) {
	this.fieldServiceDegree = fieldServiceDegree;
	promotionReportDetail.setFieldServiceDegree(fieldServiceDegree);
    }

    @Basic
    @Column(name = "FILE_DEGREE")
    public Integer getFileDegree() {
	return fileDegree;
    }

    public void setFileDegree(Integer fileDegree) {
	this.fileDegree = fileDegree;
	promotionReportDetail.setFileDegree(fileDegree);
    }

    @Basic
    @Column(name = "TRAINING_DEGREE")
    public Double getTrainingDegree() {
	return trainingDegree;
    }

    public void setTrainingDegree(Double trainingDegree) {
	this.trainingDegree = trainingDegree;
	promotionReportDetail.setTrainingDegree(trainingDegree);
    }

    @Basic
    @Column(name = "PROMOTION_EXAM_DEGREE")
    public Integer getPromotionExamDegree() {
	return promotionExamDegree;
    }

    public void setPromotionExamDegree(Integer promotionExamDegree) {
	this.promotionExamDegree = promotionExamDegree;
	promotionReportDetail.setPromotionExamDegree(promotionExamDegree);
    }

    @Basic
    @Column(name = "LEADER_DEGREE")
    public Integer getLeaderDegree() {
	return leaderDegree;
    }

    public void setLeaderDegree(Integer leaderDegree) {
	this.leaderDegree = leaderDegree;
	this.promotionReportDetail.setLeaderDegree(leaderDegree);
    }

    @Basic
    @Column(name = "QUALIFICATION_APPROVED_ID")
    public Long getQualificationApprovedId() {
	return qualificationApprovedId;
    }

    public void setQualificationApprovedId(Long qualificationApprovedId) {
	this.qualificationApprovedId = qualificationApprovedId;
	promotionReportDetail.setQualificationApprovedId(qualificationApprovedId);
    }

    @Basic
    @Column(name = "QUALIFICATION_APPROVED_NAME")
    public String getQualificationApprovedName() {
	return qualificationApprovedName;
    }

    public void setQualificationApprovedName(String qualificationApprovedName) {
	this.qualificationApprovedName = qualificationApprovedName;
    }

    @Basic
    @Column(name = "FILE_APPROVED_ID")
    public Long getFileApprovedId() {
	return fileApprovedId;
    }

    public void setFileApprovedId(Long fileApprovedId) {
	this.fileApprovedId = fileApprovedId;
	promotionReportDetail.setFileApprovedId(fileApprovedId);
    }

    @Basic
    @Column(name = "FILE_APPROVED_NAME")
    public String getFileApprovedName() {
	return fileApprovedName;
    }

    public void setFileApprovedName(String fileApprovedName) {
	this.fileApprovedName = fileApprovedName;
    }

    @Basic
    @Column(name = "TRAINING_APPROVED_ID")
    public Long getTrainingApprovedId() {
	return trainingApprovedId;
    }

    public void setTrainingApprovedId(Long trainingApprovedId) {
	this.trainingApprovedId = trainingApprovedId;
	promotionReportDetail.setTrainingApprovedId(trainingApprovedId);
    }

    @Basic
    @Column(name = "TRAINING_APPROVED_NAME")
    public String getTrainingApprovedName() {
	return trainingApprovedName;
    }

    public void setTrainingApprovedName(String trainingApprovedName) {
	this.trainingApprovedName = trainingApprovedName;
    }

    @Basic
    @Column(name = "EDUCATION_DEGREE")
    public Double getEducationDegree() {
	return educationDegree;
    }

    public void setEducationDegree(Double educationDegree) {
	this.educationDegree = educationDegree;
	promotionReportDetail.setEducationDegree(educationDegree);
    }

    @Basic
    @Column(name = "EVALUATION_DEGREE")
    public Double getEvaluationDegree() {
	return evaluationDegree;
    }

    public void setEvaluationDegree(Double evaluationDegree) {
	this.evaluationDegree = evaluationDegree;
	promotionReportDetail.setEvaluationDegree(evaluationDegree);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	promotionReportDetail.setAttachments(attachments);
    }

    @Basic
    @Column(name = "GENDER")
    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	promotionReportDetail.setRemarks(remarks);
    }

    @Basic
    @Column(name = "BONUS_FLAG")
    public Integer getBonusFlag() {
	return bonusFlag;
    }

    public void setBonusFlag(Integer bonusFlag) {
	this.bonusFlag = bonusFlag;
	promotionReportDetail.setBonusFlag(bonusFlag);
	if (this.bonusFlag == null || this.bonusFlag == FlagsEnum.OFF.getCode()) {
	    this.bonusFlagBoolean = false;
	} else {
	    this.bonusFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getBonusFlagBoolean() {
	return bonusFlagBoolean;
    }

    public void setBonusFlagBoolean(Boolean bonusFlagBoolean) {
	this.bonusFlagBoolean = bonusFlagBoolean;
	if (this.bonusFlagBoolean == false) {
	    setBonusFlag(FlagsEnum.OFF.getCode());
	} else {
	    setBonusFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "NONCANDIDATE_REASONS")
    public String getNonCandidateReasons() {
	return nonCandidateReasons;
    }

    public void setNonCandidateReasons(String nonCandidateReasons) {
	this.nonCandidateReasons = nonCandidateReasons;
	this.promotionReportDetail.setNonCandidateReasons(nonCandidateReasons);
    }

    @Basic
    @Column(name = "QUALIFICATION_PRS_DEGREE")
    public String getQualificationPersonDegree() {
	return qualificationPersonDegree;
    }

    public void setQualificationPersonDegree(String qualificationPersonDegree) {
	this.qualificationPersonDegree = qualificationPersonDegree;
	this.promotionReportDetail.setQualificationPersonDegree(qualificationPersonDegree);
    }

    @Basic
    @Column(name = "SERVICE_DEGREE_DAY")
    public Integer getServiceDegreeDay() {
	return serviceDegreeDay;
    }

    public void setServiceDegreeDay(Integer serviceDegreeDay) {
	this.serviceDegreeDay = serviceDegreeDay;
	this.promotionReportDetail.setServiceDegreeDay(serviceDegreeDay);
    }

    @Basic
    @Column(name = "SERVICE_DEGREE_MONTH")
    public Integer getServiceDegreeMonth() {
	return serviceDegreeMonth;
    }

    public void setServiceDegreeMonth(Integer serviceDegreeMonth) {
	this.serviceDegreeMonth = serviceDegreeMonth;
	this.promotionReportDetail.setServiceDegreeMonth(serviceDegreeMonth);
    }

    @Basic
    @Column(name = "SERVICE_DEGREE_YEAR")
    public Integer getServiceDegreeYear() {
	return serviceDegreeYear;
    }

    public void setServiceDegreeYear(Integer serviceDegreeYear) {
	this.serviceDegreeYear = serviceDegreeYear;
	this.promotionReportDetail.setServiceDegreeYear(serviceDegreeYear);
    }

    @Basic
    @Column(name = "EXPERIENCE_YEAR")
    public Integer getExperincePresonsYear() {
	return experincePresonsYear;
    }

    public void setExperincePresonsYear(Integer experincePresonsYear) {
	this.experincePresonsYear = experincePresonsYear;
	this.promotionReportDetail.setExperincePresonsYear(experincePresonsYear);
    }

    @Basic
    @Column(name = "EXPERIENCE_MONTH")
    public Integer getExperincePresonsMonth() {
	return experincePresonsMonth;
    }

    public void setExperincePresonsMonth(Integer experincePresonsMonth) {
	this.experincePresonsMonth = experincePresonsMonth;
	this.promotionReportDetail.setExperincePresonsMonth(experincePresonsMonth);
    }

    @Basic
    @Column(name = "EXPERIENCE_DAY")
    public Integer getExperincePresonsDay() {
	return experincePresonsDay;
    }

    public void setExperincePresonsDay(Integer experincePresonsDay) {
	this.experincePresonsDay = experincePresonsDay;
	this.promotionReportDetail.setExperincePresonsDay(experincePresonsDay);
    }

    @Basic
    @Column(name = "NO_VACANT_FLAG")
    public Integer getNoVacantFlag() {
	return noVacantFlag;
    }

    public void setNoVacantFlag(Integer noVacantFlag) {
	this.noVacantFlag = noVacantFlag;
	this.promotionReportDetail.setNoVacantFlag(noVacantFlag);
	if (this.noVacantFlag == null || this.noVacantFlag == FlagsEnum.OFF.getCode()) {
	    this.noVacantFlagBoolean = false;
	} else {
	    this.noVacantFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getNoVacantFlagBoolean() {
	return noVacantFlagBoolean;
    }

    public void setNoVacantFlagBoolean(Boolean noVacantFlagBoolean) {
	this.noVacantFlagBoolean = noVacantFlagBoolean;
	if (this.noVacantFlagBoolean == false) {
	    setNoVacantFlag(FlagsEnum.OFF.getCode());
	} else {
	    setNoVacantFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "PROMOTION_COMMITTEE_OPINION")
    public String getPromotionCommitteeOpinion() {
	return promotionCommitteeOpinion;
    }

    public void setPromotionCommitteeOpinion(String promotionCommitteeOpinion) {
	this.promotionCommitteeOpinion = promotionCommitteeOpinion;
	this.promotionReportDetail.setPromotionCommitteeOpinion(promotionCommitteeOpinion);
    }

    @Basic
    @Column(name = "RECRUITMENT_DATE")
    public Date getRecruitmentDate() {
	return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
	this.recruitmentDate = recruitmentDate;
    }

    @Basic
    @Column(name = "BIRTH_DATE")
    public Date getBirthDate() {
	return birthDate;
    }

    public void setBirthDate(Date birthDate) {
	this.birthDate = birthDate;
    }

    @Basic
    @Column(name = "GRADUATION_EVALUATION")
    public Integer getGraduationEvaluation() {
	return graduationEvaluation;
    }

    public void setGraduationEvaluation(Integer graduationEvaluation) {
	this.graduationEvaluation = graduationEvaluation;
	this.promotionReportDetail.setGraduationEvaluation(graduationEvaluation);
    }

    @Basic
    @Column(name = "GRADUATION_ORDER")
    public Integer getGraduationOrder() {
	return graduationOrder;
    }

    public void setGraduationOrder(Integer graduationOrder) {
	this.graduationOrder = graduationOrder;
	this.promotionReportDetail.setGraduationOrder(graduationOrder);
    }

    @Basic
    @Column(name = "GRADUATION_DEGREE")
    public Double getGraduationDegree() {
	return graduationDegree;
    }

    public void setGraduationDegree(Double graduationDegree) {
	this.graduationDegree = graduationDegree;
	this.promotionReportDetail.setGraduationDegree(graduationDegree);
    }

    @Basic
    @Column(name = "DRUGS_REQUEST_ID")
    public Long getDrugsRequestId() {
	return drugsRequestId;
    }

    public void setDrugsRequestId(Long drugsRequestId) {
	this.drugsRequestId = drugsRequestId;
	this.promotionReportDetail.setDrugsRequestId(drugsRequestId);
    }

    @Transient
    public PromotionReportDetail getPromotionReportDetail() {
	return promotionReportDetail;
    }

    public void setPromotionReportDetail(PromotionReportDetail promotionReportDetail) {
	this.promotionReportDetail = promotionReportDetail;
    }

    @Transient
    public Double getTotalDegree() {
	Double totalDegree = 0D;

	if (oldRankId != null && oldRankId >= RanksEnum.STAFF_SERGEANT.getCode() && oldRankId <= RanksEnum.SOLDIER.getCode()) {
	    totalDegree = (fieldServiceDegree == null ? 0 : fieldServiceDegree)
		    + (fileDegree == null ? 0 : fileDegree)
		    + (seniorityDegree == null ? 0 : seniorityDegree)
		    + (trainingDegree == null ? 0 : trainingDegree)
		    + (qualificationDegree == null ? 0 : qualificationDegree)
		    + (promotionExamDegree == null ? 0 : promotionExamDegree)
		    + (leaderDegree == null ? 0 : leaderDegree);
	} else if (oldRankId != null && oldRankId >= RanksEnum.FOURTEENTH.getCode() && oldRankId <= RanksEnum.FIRST.getCode()) {
	    totalDegree = (educationDegree == null ? 0 : educationDegree)
		    + (evaluationDegree == null ? 0 : evaluationDegree)
		    + (seniorityDegree == null ? 0 : seniorityDegree)
		    + (trainingDegree == null ? 0 : trainingDegree);
	}

	return Double.valueOf(new DecimalFormat("#.##").format(totalDegree));
    }

    @Override
    public String toString() {
	return "PromotionReportDetailData [id=" + id + ", reportId=" + reportId + ", empId=" + empId + ", empName=" + empName + ", empSocialID=" + empSocialID + ", oldRankId=" + oldRankId + ", oldRankDesc=" + oldRankDesc + ", newRankId=" + newRankId + ", newRankDesc=" + newRankDesc + ", oldDegreeId=" + oldDegreeId + ", oldDegreeDesc=" + oldDegreeDesc + ", newDegreeId=" + newDegreeId + ", newDegreeDesc=" + newDegreeDesc + ", promotionDueDate=" + promotionDueDate + ", promotionDueDateString="
		+ promotionDueDateString + ", militaryNumber=" + militaryNumber + ", externalDecisionNumber=" + externalDecisionNumber + ", externalDecisionDate=" + externalDecisionDate + ", externalDecisionDateString=" + externalDecisionDateString + ", referring=" + referring + ", exceptionalPromotionReason=" + exceptionalPromotionReason + ", basedOnTransactionId=" + basedOnTransactionId + ", oldJobId=" + oldJobId + ", oldJobClassCode=" + oldJobClassCode + ", oldJobClassDesc=" + oldJobClassDesc
		+ ", oldJobCode=" + oldJobCode + ", oldJobDesc=" + oldJobDesc + ", newJobId=" + newJobId + ", newJobClassCode=" + newJobClassCode + ", newJobClassDesc=" + newJobClassDesc + ", newJobCode=" + newJobCode + ", newJobDesc=" + newJobDesc + ", freezedJobId=" + freezedJobId + ", freezedJobCode=" + freezedJobCode + ", freezedJobName=" + freezedJobName + ", managerFlag=" + managerFlag + ", managerFlagBoolean=" + managerFlagBoolean + ", status=" + status + ", requirementsFlag="
		+ requirementsFlag + ", requirementsFlagBoolean=" + requirementsFlagBoolean + ", judgmentFlag=" + judgmentFlag + ", judgmentFlagBoolean=" + judgmentFlagBoolean + ", retirementFlag=" + retirementFlag + ", retirementFlagBoolean=" + retirementFlagBoolean + ", medicalTest=" + medicalTest + ", medicalTestExemptionReason=" + medicalTestExemptionReason + ", evaluationResult=" + evaluationResult + ", rankTitleId=" + rankTitleId + ", rankTitleName=" + rankTitleName
		+ ", qualificationDegree=" + qualificationDegree + ", qualificationPersonDegree=" + qualificationPersonDegree + ", seniorityDegree=" + seniorityDegree + ", fieldServiceDegree=" + fieldServiceDegree + ", fileDegree=" + fileDegree + ", trainingDegree=" + trainingDegree + ", promotionExamDegree=" + promotionExamDegree + ", leaderDegree=" + leaderDegree + ", qualificationApprovedId=" + qualificationApprovedId + ", qualificationApprovedName=" + qualificationApprovedName
		+ ", fileApprovedId=" + fileApprovedId + ", fileApprovedName=" + fileApprovedName + ", trainingApprovedId=" + trainingApprovedId + ", trainingApprovedName=" + trainingApprovedName + ", serviceDegreeDay=" + serviceDegreeDay + ", serviceDegreeMonth=" + serviceDegreeMonth + ", serviceDegreeYear=" + serviceDegreeYear + ", educationDegree=" + educationDegree + ", evaluationDegree=" + evaluationDegree + ", gender=" + gender + ", recruitmentDate=" + recruitmentDate + ", birthDate="
		+ birthDate + ", attachments=" + attachments + ", remarks=" + remarks + ", nonCandidateReasons=" + nonCandidateReasons + ", bonusFlag=" + bonusFlag + ", bonusFlagBoolean=" + bonusFlagBoolean + ", experincePresonsYear=" + experincePresonsYear + ", experincePresonsMonth=" + experincePresonsMonth + ", experincePresonsDay=" + experincePresonsDay + ", noVacantFlag=" + noVacantFlag + ", noVacantFlagBoolean=" + noVacantFlagBoolean + ", promotionCommitteeOpinion="
		+ promotionCommitteeOpinion + ", graduationEvaluation=" + graduationEvaluation + ", graduationOrder=" + graduationOrder + ", graduationDegree=" + graduationDegree + ", drugsRequestId=" + drugsRequestId + ", promotionReportDetail=" + promotionReportDetail + "]";
    }

}
