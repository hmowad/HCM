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
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * 
 * The <code>PromotionReportDetailData</code> class represents the Promotion report detail data as details of the main report for all employees.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_promotionReportDetail_getOpenedPromotionReportDetails",
		query = " select prd from PromotionReportDetail prd, PromotionReport pr " +
			" where pr.id = prd.reportId " +
			" and (prd.empId in ( :P_EMPS_IDS ) or prd.newJobId in ( :P_JOBS_IDS ) or prd.freezedJobId in ( :P_JOBS_IDS )) " +
			" and (:P_EXCLUDED_REPORT_ID = -1 or prd.reportId <> :P_EXCLUDED_REPORT_ID) " +
			" and (prd.status in (10, 15, 25)) " +
			" and (:P_PROMOTION_TYPES_IDS_FLAG = -1 or pr.promotionTypeId in (:P_PROMOTION_TYPES_IDS)) " +
			" and (pr.status <> 20) "),

	@NamedQuery(name = "hcm_promotionReportDetail_getFreezedJobsIdsInPromotionReportDetails",
		query = " select prd.freezedJobId from PromotionReportDetail prd, PromotionReport pr " +
			" where pr.id = prd.reportId " +
			" and prd.freezedJobId is not null" +
			" and prd.newRankId = :P_NEW_RANK_ID" +
			" and pr.status <> 20 "),

	@NamedQuery(name = "hcm_promotionReportDetail_countPromotionReportDetailsStatuses",
		query = " select count(prd.id) from PromotionReportDetail prd " +
			" where prd.reportId = :P_REPORT_ID " +
			" and (prd.status in (:P_REPORT_DETAILS_STATUSES)) " +
			" and (:P_EXCLUDED_IDS_FLAG = -1 or prd.id not in (:P_EXCLUDED_IDS)) ")

})

@Entity
@Table(name = "HCM_PRM_REPORTS_DETAILS")
public class PromotionReportDetail extends AuditEntity implements UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private Long reportId;
    private Long empId;
    private Long oldRankId;
    private Long newRankId;
    private Long oldDegreeId;
    private Long newDegreeId;
    private Date promotionDueDate;
    private Integer militaryNumber;
    private String externalDecisionNumber;
    private Date externalDecisionDate;
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
    private Integer managerFlag;
    private Long status;
    private Integer requirementsFlag;
    private Integer judgmentFlag;
    private Integer retirementFlag;
    private Integer medicalTest;
    private String medicalTestExemptionReason;
    private Integer evaluationResult;
    private Long rankTitleId;
    private Double qualificationDegree;
    private String qualificationPersonDegree;
    private Double seniorityDegree;
    private Double fieldServiceDegree;
    private Integer fileDegree;
    private Double trainingDegree;
    private Integer promotionExamDegree;
    private Integer leaderDegree;
    private Long qualificationApprovedId;
    private Long fileApprovedId;
    private Long trainingApprovedId;
    private Double educationDegree;
    private Double evaluationDegree;
    private String attachments;
    private String remarks;
    private String nonCandidateReasons;
    private Integer bonusFlag;
    private Integer serviceDegreeDay;
    private Integer serviceDegreeMonth;
    private Integer serviceDegreeYear;
    private Integer experincePresonsYear;
    private Integer experincePresonsMonth;
    private Integer experincePresonsDay;
    private Integer noVacantFlag;
    private String promotionCommitteeOpinion;

    private Integer graduationEvaluation;
    private Integer graduationOrder;
    private Double graduationDegree;
    private Long drugsRequestId;

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
    @Column(name = "REPORT_ID")
    public Long getReportId() {
	return reportId;
    }

    public void setReportId(Long reportId) {
	this.reportId = reportId;
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
    @Column(name = "PROMOTION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPromotionDueDate() {
	return promotionDueDate;
    }

    public void setPromotionDueDate(Date promotionDueDate) {
	this.promotionDueDate = promotionDueDate;
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
    @Column(name = "BASED_ON_TRANSACTION_ID")
    public Long getBasedOnTransactionId() {
	return basedOnTransactionId;
    }

    public void setBasedOnTransactionId(Long basedOnTransactionId) {
	this.basedOnTransactionId = basedOnTransactionId;
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
    @Column(name = "MANAGER_FLAG")
    public Integer getManagerFlag() {
	return managerFlag;
    }

    public void setManagerFlag(Integer managerFlag) {
	this.managerFlag = managerFlag;
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
    @Column(name = "REQUIREMENTS_FLAG")
    public Integer getRequirementsFlag() {
	return requirementsFlag;
    }

    public void setRequirementsFlag(Integer requirementsFlag) {
	this.requirementsFlag = requirementsFlag;
    }

    @Basic
    @Column(name = "JUDGMENT_FLAG")
    public Integer getJudgmentFlag() {
	return judgmentFlag;
    }

    public void setJudgmentFlag(Integer judgmentFlag) {
	this.judgmentFlag = judgmentFlag;
    }

    @Basic
    @Column(name = "RETIREMENT_FLAG")
    public Integer getRetirementFlag() {
	return retirementFlag;
    }

    public void setRetirementFlag(Integer retirementFlag) {
	this.retirementFlag = retirementFlag;
    }

    @Basic
    @Column(name = "MEDICAL_TEST")
    public Integer getMedicalTest() {
	return medicalTest;
    }

    public void setMedicalTest(Integer medicalTest) {
	this.medicalTest = medicalTest;
    }

    @Basic
    @Column(name = "MEDICAL_TEST_EXEMPTION_REASON")
    public String getMedicalTestExemptionReason() {
	return medicalTestExemptionReason;
    }

    public void setMedicalTestExemptionReason(String medicalTestExemptionReason) {
	this.medicalTestExemptionReason = medicalTestExemptionReason;
    }

    @Basic
    @Column(name = "EVALUATION_RESULT")
    public Integer getEvaluationResult() {
	return evaluationResult;
    }

    public void setEvaluationResult(Integer evaluationResult) {
	this.evaluationResult = evaluationResult;
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
    @Column(name = "QUALIFICATION_DEGREE")
    public Double getQualificationDegree() {
	return qualificationDegree;
    }

    public void setQualificationDegree(Double qualificationDegree) {
	this.qualificationDegree = qualificationDegree;
    }

    @Basic
    @Column(name = "SENIORITY_DEGREE")
    public Double getSeniorityDegree() {
	return seniorityDegree;
    }

    public void setSeniorityDegree(Double seniorityDegree) {
	this.seniorityDegree = seniorityDegree;
    }

    @Basic
    @Column(name = "FIELD_SERVICE_DEGREE")
    public Double getFieldServiceDegree() {
	return fieldServiceDegree;
    }

    public void setFieldServiceDegree(Double fieldServiceDegree) {
	this.fieldServiceDegree = fieldServiceDegree;
    }

    @Basic
    @Column(name = "FILE_DEGREE")
    public Integer getFileDegree() {
	return fileDegree;
    }

    public void setFileDegree(Integer fileDegree) {
	this.fileDegree = fileDegree;
    }

    @Basic
    @Column(name = "TRAINING_DEGREE")
    public Double getTrainingDegree() {
	return trainingDegree;
    }

    public void setTrainingDegree(Double trainingDegree) {
	this.trainingDegree = trainingDegree;
    }

    @Basic
    @Column(name = "PROMOTION_EXAM_DEGREE")
    public Integer getPromotionExamDegree() {
	return promotionExamDegree;
    }

    public void setPromotionExamDegree(Integer promotionExamDegree) {
	this.promotionExamDegree = promotionExamDegree;
    }

    @Basic
    @Column(name = "LEADER_DEGREE")
    public Integer getLeaderDegree() {
	return leaderDegree;
    }

    public void setLeaderDegree(Integer leaderDegree) {
	this.leaderDegree = leaderDegree;
    }

    @Basic
    @Column(name = "QUALIFICATION_APPROVED_ID")
    public Long getQualificationApprovedId() {
	return qualificationApprovedId;
    }

    public void setQualificationApprovedId(Long qualificationApprovedId) {
	this.qualificationApprovedId = qualificationApprovedId;
    }

    @Basic
    @Column(name = "FILE_APPROVED_ID")
    public Long getFileApprovedId() {
	return fileApprovedId;
    }

    public void setFileApprovedId(Long fileApprovedId) {
	this.fileApprovedId = fileApprovedId;
    }

    @Basic
    @Column(name = "TRAINING_APPROVED_ID")
    public Long getTrainingApprovedId() {
	return trainingApprovedId;
    }

    public void setTrainingApprovedId(Long trainingApprovedId) {
	this.trainingApprovedId = trainingApprovedId;
    }

    @Basic
    @Column(name = "EDUCATION_DEGREE")
    public Double getEducationDegree() {
	return educationDegree;
    }

    public void setEducationDegree(Double educationDegree) {
	this.educationDegree = educationDegree;
    }

    @Basic
    @Column(name = "EVALUATION_DEGREE")
    public Double getEvaluationDegree() {
	return evaluationDegree;
    }

    public void setEvaluationDegree(Double evaluationDegree) {
	this.evaluationDegree = evaluationDegree;
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
    @Column(name = "NONCANDIDATE_REASONS")
    public String getNonCandidateReasons() {
	return nonCandidateReasons;
    }

    public void setNonCandidateReasons(String nonCandidateReasons) {
	this.nonCandidateReasons = nonCandidateReasons;
    }

    @Basic
    @Column(name = "BONUS_FLAG")
    public Integer getBonusFlag() {
	return bonusFlag;
    }

    public void setBonusFlag(Integer bonusFlag) {
	this.bonusFlag = bonusFlag;
    }

    @Basic
    @Column(name = "QUALIFICATION_PRS_DEGREE")
    public String getQualificationPersonDegree() {
	return qualificationPersonDegree;
    }

    public void setQualificationPersonDegree(String qualificationPersonDegree) {
	this.qualificationPersonDegree = qualificationPersonDegree;
    }

    @Basic
    @Column(name = "SERVICE_DEGREE_DAY")
    public Integer getServiceDegreeDay() {
	return serviceDegreeDay;
    }

    public void setServiceDegreeDay(Integer serviceDegreeDay) {
	this.serviceDegreeDay = serviceDegreeDay;
    }

    @Basic
    @Column(name = "SERVICE_DEGREE_MONTH")
    public Integer getServiceDegreeMonth() {
	return serviceDegreeMonth;
    }

    public void setServiceDegreeMonth(Integer serviceDegreeMonth) {
	this.serviceDegreeMonth = serviceDegreeMonth;
    }

    @Basic
    @Column(name = "SERVICE_DEGREE_YEAR")
    public Integer getServiceDegreeYear() {
	return serviceDegreeYear;
    }

    public void setServiceDegreeYear(Integer serviceDegreeYear) {
	this.serviceDegreeYear = serviceDegreeYear;
    }

    @Basic
    @Column(name = "EXPERIENCE_YEAR")
    public Integer getExperincePresonsYear() {
	return experincePresonsYear;
    }

    public void setExperincePresonsYear(Integer experincePresonsYear) {
	this.experincePresonsYear = experincePresonsYear;
    }

    @Basic
    @Column(name = "EXPERIENCE_MONTH")
    public Integer getExperincePresonsMonth() {
	return experincePresonsMonth;
    }

    public void setExperincePresonsMonth(Integer experincePresonsMonth) {
	this.experincePresonsMonth = experincePresonsMonth;
    }

    @Basic
    @Column(name = "EXPERIENCE_DAY")
    public Integer getExperincePresonsDay() {
	return experincePresonsDay;
    }

    public void setExperincePresonsDay(Integer experincePresonsDay) {
	this.experincePresonsDay = experincePresonsDay;
    }

    @Basic
    @Column(name = "NO_VACANT_FLAG")
    public Integer getNoVacantFlag() {
	return noVacantFlag;
    }

    public void setNoVacantFlag(Integer noVacantFlag) {
	this.noVacantFlag = noVacantFlag;
    }

    @Basic
    @Column(name = "PROMOTION_COMMITTEE_OPINION")
    public String getPromotionCommitteeOpinion() {
	return promotionCommitteeOpinion;
    }

    public void setPromotionCommitteeOpinion(String promotionCommitteeOpinion) {
	this.promotionCommitteeOpinion = promotionCommitteeOpinion;
    }

    @Basic
    @Column(name = "GRADUATION_EVALUATION")
    public Integer getGraduationEvaluation() {
	return graduationEvaluation;
    }

    public void setGraduationEvaluation(Integer graduationEvaluation) {
	this.graduationEvaluation = graduationEvaluation;
    }

    @Basic
    @Column(name = "GRADUATION_ORDER")
    public Integer getGraduationOrder() {
	return graduationOrder;
    }

    public void setGraduationOrder(Integer graduationOrder) {
	this.graduationOrder = graduationOrder;
    }

    @Basic
    @Column(name = "GRADUATION_DEGREE")
    public Double getGraduationDegree() {
	return graduationDegree;
    }

    public void setGraduationDegree(Double graduationDegree) {
	this.graduationDegree = graduationDegree;
    }

    @Basic
    @Column(name = "DRUGS_REQUEST_ID")
    public Long getDrugsRequestId() {
	return drugsRequestId;
    }

    public void setDrugsRequestId(Long drugsRequestId) {
	this.drugsRequestId = drugsRequestId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "reportId:" + reportId + AUDIT_SEPARATOR +
		"empId:" + empId + AUDIT_SEPARATOR +

		"externalDecisionNumber:" + externalDecisionNumber + AUDIT_SEPARATOR +
		"externalDecisionDate:" + externalDecisionDate + AUDIT_SEPARATOR +
		"militaryNumber:" + militaryNumber + AUDIT_SEPARATOR +
		"referring:" + referring + AUDIT_SEPARATOR +
		"exceptionalPromotionReason:" + exceptionalPromotionReason + AUDIT_SEPARATOR +
		"basedOnTransactionId:" + basedOnTransactionId + AUDIT_SEPARATOR +

		"newJobId:" + newJobId + AUDIT_SEPARATOR +
		"freezedJobId:" + freezedJobId + AUDIT_SEPARATOR +
		"managerFlag:" + managerFlag + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"rankTitleId:" + rankTitleId + AUDIT_SEPARATOR +

		"qualificationDegree:" + qualificationDegree + AUDIT_SEPARATOR +
		"qualificationPersonDegree:" + qualificationPersonDegree + AUDIT_SEPARATOR +
		"seniorityDegree:" + seniorityDegree + AUDIT_SEPARATOR +
		"fieldServiceDegree:" + fieldServiceDegree + AUDIT_SEPARATOR +
		"fileDegree:" + fileDegree + AUDIT_SEPARATOR +
		"trainingDegree:" + trainingDegree + AUDIT_SEPARATOR +
		"promotionExamDegree:" + promotionExamDegree + AUDIT_SEPARATOR +
		"leaderDegree:" + leaderDegree + AUDIT_SEPARATOR +
		"educationDegree:" + educationDegree + AUDIT_SEPARATOR +
		"evaluationDegree:" + evaluationDegree + AUDIT_SEPARATOR +

		"qualificationApprovedId:" + qualificationApprovedId + AUDIT_SEPARATOR +
		"fileApprovedId:" + fileApprovedId + AUDIT_SEPARATOR +
		"trainingApprovedId:" + trainingApprovedId + AUDIT_SEPARATOR +

		"attachments:" + attachments + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR +
		"nonCandidateReasons:" + nonCandidateReasons + AUDIT_SEPARATOR +
		"bonusFlag:" + bonusFlag + AUDIT_SEPARATOR +
		"serviceDegreeDay:" + serviceDegreeDay + AUDIT_SEPARATOR +
		"serviceDegreeMonth:" + serviceDegreeMonth + AUDIT_SEPARATOR +
		"serviceDegreeYear:" + serviceDegreeYear + AUDIT_SEPARATOR +
		"experincePresonsYear:" + experincePresonsYear + AUDIT_SEPARATOR +
		"experincePresonsMonth:" + experincePresonsMonth + AUDIT_SEPARATOR +
		"experincePresonsDay:" + experincePresonsDay + AUDIT_SEPARATOR +
		"promotionCommitteeOpinion:" + promotionCommitteeOpinion + AUDIT_SEPARATOR +
		"noVacantFlag:" + noVacantFlag + AUDIT_SEPARATOR +

		"medicalTest:" + medicalTest + AUDIT_SEPARATOR +
		"medicalTestExemptionReason:" + medicalTestExemptionReason + AUDIT_SEPARATOR +
		"drugsRequestId:" + drugsRequestId;

    }
}