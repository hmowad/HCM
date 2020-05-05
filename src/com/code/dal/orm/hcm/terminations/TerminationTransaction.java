package com.code.dal.orm.hcm.terminations;

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
import com.code.dal.orm.AuditEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_terminationTransaction_searchTerminationTransactionsByCancelTransactionId",
		query = " select tt from TerminationTransaction tt" +
			" where tt.cancelTransactionId = :P_CANCEL_TRANSACTION_ID "),

	@NamedQuery(name = "hcm_terminationTransaction_searchLastTerminationMovementTransaction",
		query = " select tt from TerminationTransaction tt, TransactionType tr" +
			" where tt.transactionTypeId = tr.id " +
			" and tt.empId = :P_EMP_ID " +
			" and tr.code = :P_TRANSACTION_TYPE_CODE " +
			" and tt.decisionDate = (select max(tt2.decisionDate) from TerminationTransaction tt2, TransactionType tr2 where tt2.transactionTypeId = tr2.id and tt2.empId = tt.empId and tr2.code = :P_TRANSACTION_TYPE_CODE ) "),

	@NamedQuery(name = "hcm_terminationTransaction_searchEmployeeTerminationTransactions",
		query = " select ste from TerminationTransaction ste, TransactionType tr " +
			" where ste.transactionTypeId = tr.id " +
			" and ste.empId = :P_EMP_ID " +
			" and tr.code = :P_TERMINATION_TYPE_CODE " +
			" and to_date(:P_FROM_DATE, 'MI/MM/YYYY') <= ste.serviceTerminationDate " +
			" and to_date(:P_TO_DATE, 'MI/MM/YYYY') >= ste.serviceTerminationDate " +
			" and (select count(tc.id) from TerminationTransaction tc, TransactionType tr2 where tc.transactionTypeId = tr2.id and tr2.code = :P_CANCELATION_TYPE_CODE and tc.cancelTransactionId = ste.id) = 0 ")
})

@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_STE_TRANSACTIONS")
public class TerminationTransaction extends AuditEntity implements InsertableAuditEntity {
    private Long id;
    private Long recordDetailId;
    private Long empId;
    private Long categoryId;
    private Double basicSalary;
    private Long degreeId;
    private Date serviceTerminationDate;
    private Long status;

    private String remarks;
    private String referring;
    private String attachments;
    private String terminationRequestReason;
    private Long terminationRequestType;
    private Date desiredTerminationDate;
    private String ministryRequestNumber;
    private Date ministryRequestDate;
    private String ministryRejectionNumber;
    private Date ministryRejectionDate;

    private Date extensionStartDate;
    private Date extensionEndDate;
    private Integer extensionPeriodDays;
    private Integer extensionPeriodMonths;
    private Integer extensionFlag;
    private Integer judgmentFlag;
    private Integer specializationPeriodFlag;
    private Integer foreignerMarriageFlag;

    private Integer dismissType;
    private String dismissDecisionNumber;
    private Date dismissDecisionDate;
    private Integer dismissStatus;
    private String dismissResumptionNumber;
    private Integer dismissResumptionYear;
    private String dismissApprovalNumber;
    private Date dismissApprovalDate;
    private Integer dismissBasedOn;
    private String dismissBasedOnDesc;
    private String dismissJudgment;

    private String deathCertNumber;
    private Date deathCertDate;
    private String deathCertIssuePlace;
    private Date deathDate;
    private Integer deathInDutyFlag;
    private Integer deathType;
    private String deathInOperations;

    private Integer absenceType;
    private Date absenceStartDate;
    private Integer absencePeriodDays;
    private Date absenceReturnDate;

    private Integer disqualificationDrugsFlag;
    private String disqualificationReason;
    private String disqualificationDrugsType;
    private String medicalCaseDesc;
    private String medicalWorkDisability;

    private Date jobCancelationDate;
    private Integer nationalityLossType;
    private String nationalityLossReason;
    private Integer contractorBasedOnType;
    private String contractorTerminationReason;

    private String movedTo;
    private Date movementJoiningDate;
    private String movementJoiningDesc;
    private Date disclaimerDate;
    private Long jobId;
    private String jobCode;
    private String jobName;
    private Long jobClassificationId;

    private String transEmpRankDesc;
    private String transEmpJobClassJobCode;
    private String transEmpJobClassJobDesc;
    private String transEmpUnitFullName;
    private Long transEmpUnitId;
    private Long transactionTypeId;

    private Long reasonId;
    private Long cancelTransactionId;
    private String cancelTransactionReason;
    private Integer eflag;
    private Integer migFlag;

    private String decisionNumber;
    private Date decisionDate;
    private Long decisionRegionId;
    private String internalCopies;
    private String externalCopies;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Integer injuryInDutyFlag;
    private Integer injuryType;

    @SequenceGenerator(name = "HCMServiceTerminationTransactionsSeq",
	    sequenceName = "HCM_STE_TRANS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMServiceTerminationTransactionsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "RECORD_DETAIL_ID")
    public Long getRecordDetailId() {
	return recordDetailId;
    }

    public void setRecordDetailId(Long recordDetailId) {
	this.recordDetailId = recordDetailId;
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
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
    }

    @Basic
    @Column(name = "BASIC_SALARY")
    public Double getBasicSalary() {
	return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
	this.basicSalary = basicSalary;
    }

    @Basic
    @Column(name = "SERVICE_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getServiceTerminationDate() {
	return serviceTerminationDate;
    }

    public void setServiceTerminationDate(Date serviceTerminationDate) {
	this.serviceTerminationDate = serviceTerminationDate;
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
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
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
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "TERMINATION_REQUEST_REASON")
    public String getTerminationRequestReason() {
	return terminationRequestReason;
    }

    public void setTerminationRequestReason(String terminationRequestReason) {
	this.terminationRequestReason = terminationRequestReason;
    }

    @Basic
    @Column(name = "TERMINATION_REQUEST_TYPE")
    public Long getTerminationRequestType() {
	return terminationRequestType;
    }

    public void setTerminationRequestType(Long terminationRequestType) {
	this.terminationRequestType = terminationRequestType;
    }

    @Basic
    @Column(name = "DESIRED_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDesiredTerminationDate() {
	return desiredTerminationDate;
    }

    public void setDesiredTerminationDate(Date desiredTerminationDate) {
	this.desiredTerminationDate = desiredTerminationDate;
    }

    @Basic
    @Column(name = "MINISTRY_REQUEST_NUMBER")
    public String getMinistryRequestNumber() {
	return ministryRequestNumber;
    }

    public void setMinistryRequestNumber(String ministryRequestNumber) {
	this.ministryRequestNumber = ministryRequestNumber;
    }

    @Basic
    @Column(name = "MINISTRY_REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryRequestDate() {
	return ministryRequestDate;
    }

    public void setMinistryRequestDate(Date ministryRequestDate) {
	this.ministryRequestDate = ministryRequestDate;
    }

    @Basic
    @Column(name = "MINISTRY_REJECTION_NUMBER")
    public String getMinistryRejectionNumber() {
	return ministryRejectionNumber;
    }

    public void setMinistryRejectionNumber(String ministryRejectionNumber) {
	this.ministryRejectionNumber = ministryRejectionNumber;
    }

    @Basic
    @Column(name = "MINISTRY_REJECTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryRejectionDate() {
	return ministryRejectionDate;
    }

    public void setMinistryRejectionDate(Date ministryRejectionDate) {
	this.ministryRejectionDate = ministryRejectionDate;
    }

    @Basic
    @Column(name = "EXTENSION_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExtensionStartDate() {
	return extensionStartDate;
    }

    public void setExtensionStartDate(Date extensionStartDate) {
	this.extensionStartDate = extensionStartDate;
    }

    @Basic
    @Column(name = "EXTENSION_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExtensionEndDate() {
	return extensionEndDate;
    }

    public void setExtensionEndDate(Date extensionEndDate) {
	this.extensionEndDate = extensionEndDate;
    }

    @Basic
    @Column(name = "EXTENSION_PERIOD_DAYS")
    public Integer getExtensionPeriodDays() {
	return extensionPeriodDays;
    }

    public void setExtensionPeriodDays(Integer extensionPeriodDays) {
	this.extensionPeriodDays = extensionPeriodDays;
    }

    @Basic
    @Column(name = "EXTENSION_PERIOD_MONTHS")
    public Integer getExtensionPeriodMonths() {
	return extensionPeriodMonths;
    }

    public void setExtensionPeriodMonths(Integer extensionPeriodMonths) {
	this.extensionPeriodMonths = extensionPeriodMonths;
    }

    @Basic
    @Column(name = "EXTENSION_FLAG")
    public Integer getExtensionFlag() {
	return extensionFlag;
    }

    public void setExtensionFlag(Integer extensionFlag) {
	this.extensionFlag = extensionFlag;
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
    @Column(name = "SPECIALIZATION_PERIOD_FLAG")
    public Integer getSpecializationPeriodFlag() {
	return specializationPeriodFlag;
    }

    public void setSpecializationPeriodFlag(Integer specializationPeriodFlag) {
	this.specializationPeriodFlag = specializationPeriodFlag;
    }

    @Basic
    @Column(name = "FOREIGNER_MARRIAGE_FLAG")
    public Integer getForeignerMarriageFlag() {
	return foreignerMarriageFlag;
    }

    public void setForeignerMarriageFlag(Integer foreignerMarriageFlag) {
	this.foreignerMarriageFlag = foreignerMarriageFlag;
    }

    @Basic
    @Column(name = "DISMISS_TYPE")
    public Integer getDismissType() {
	return dismissType;
    }

    public void setDismissType(Integer dismissType) {
	this.dismissType = dismissType;
    }

    @Basic
    @Column(name = "DISMISS_DECISION_NUMBER")
    public String getDismissDecisionNumber() {
	return dismissDecisionNumber;
    }

    public void setDismissDecisionNumber(String dismissDecisionNumber) {
	this.dismissDecisionNumber = dismissDecisionNumber;
    }

    @Basic
    @Column(name = "DISMISS_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDismissDecisionDate() {
	return dismissDecisionDate;
    }

    public void setDismissDecisionDate(Date dismissDecisionDate) {
	this.dismissDecisionDate = dismissDecisionDate;
    }

    @Basic
    @Column(name = "DISMISS_STATUS")
    public Integer getDismissStatus() {
	return dismissStatus;
    }

    public void setDismissStatus(Integer dismissStatus) {
	this.dismissStatus = dismissStatus;
    }

    @Basic
    @Column(name = "DISMISS_RESUMPTION_NUMBER")
    public String getDismissResumptionNumber() {
	return dismissResumptionNumber;
    }

    public void setDismissResumptionNumber(String dismissResumptionNumber) {
	this.dismissResumptionNumber = dismissResumptionNumber;
    }

    @Basic
    @Column(name = "DISMISS_RESUMPTION_YEAR")
    public Integer getDismissResumptionYear() {
	return dismissResumptionYear;
    }

    public void setDismissResumptionYear(Integer dismissResumptionYear) {
	this.dismissResumptionYear = dismissResumptionYear;
    }

    @Basic
    @Column(name = "DISMISS_APPROVAL_NUMBER")
    public String getDismissApprovalNumber() {
	return dismissApprovalNumber;
    }

    public void setDismissApprovalNumber(String dismissApprovalNumber) {
	this.dismissApprovalNumber = dismissApprovalNumber;
    }

    @Basic
    @Column(name = "DISMISS_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDismissApprovalDate() {
	return dismissApprovalDate;
    }

    public void setDismissApprovalDate(Date dismissApprovalDate) {
	this.dismissApprovalDate = dismissApprovalDate;
    }

    @Basic
    @Column(name = "DISMISS_BASED_ON")
    public Integer getDismissBasedOn() {
	return dismissBasedOn;
    }

    public void setDismissBasedOn(Integer dismissBasedOn) {
	this.dismissBasedOn = dismissBasedOn;
    }

    @Basic
    @Column(name = "DISMISS_BASED_ON_DESC")
    public String getDismissBasedOnDesc() {
	return dismissBasedOnDesc;
    }

    public void setDismissBasedOnDesc(String dismissBasedOnDesc) {
	this.dismissBasedOnDesc = dismissBasedOnDesc;
    }

    @Basic
    @Column(name = "DISMISS_JUDGMENT")
    public String getDismissJudgment() {
	return dismissJudgment;
    }

    public void setDismissJudgment(String dismissJudgment) {
	this.dismissJudgment = dismissJudgment;
    }

    @Basic
    @Column(name = "DEATH_CERT_NUMBER")
    public String getDeathCertNumber() {
	return deathCertNumber;
    }

    public void setDeathCertNumber(String deathCertNumber) {
	this.deathCertNumber = deathCertNumber;
    }

    @Basic
    @Column(name = "DEATH_CERT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeathCertDate() {
	return deathCertDate;
    }

    public void setDeathCertDate(Date deathCertDate) {
	this.deathCertDate = deathCertDate;
    }

    @Basic
    @Column(name = "DEATH_CERT_ISSUE_PLACE")
    public String getDeathCertIssuePlace() {
	return deathCertIssuePlace;
    }

    public void setDeathCertIssuePlace(String deathCertIssuePlace) {
	this.deathCertIssuePlace = deathCertIssuePlace;
    }

    @Basic
    @Column(name = "DEATH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeathDate() {
	return deathDate;
    }

    public void setDeathDate(Date deathDate) {
	this.deathDate = deathDate;
    }

    @Basic
    @Column(name = "DEATH_IN_DUTY_FLAG")
    public Integer getDeathInDutyFlag() {
	return deathInDutyFlag;
    }

    public void setDeathInDutyFlag(Integer deathInDutyFlag) {
	this.deathInDutyFlag = deathInDutyFlag;
    }

    @Basic
    @Column(name = "DEATH_TYPE")
    public Integer getDeathType() {
	return deathType;
    }

    public void setDeathType(Integer deathType) {
	this.deathType = deathType;
    }

    @Basic
    @Column(name = "DEATH_IN_OPERATIONS")
    public String getDeathInOperations() {
	return deathInOperations;
    }

    public void setDeathInOperations(String deathInOperations) {
	this.deathInOperations = deathInOperations;
    }

    @Basic
    @Column(name = "ABSENCE_TYPE")
    public Integer getAbsenceType() {
	return absenceType;
    }

    public void setAbsenceType(Integer absenceType) {
	this.absenceType = absenceType;
    }

    @Basic
    @Column(name = "ABSENCE_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getAbsenceStartDate() {
	return absenceStartDate;
    }

    public void setAbsenceStartDate(Date absenceStartDate) {
	this.absenceStartDate = absenceStartDate;
    }

    @Basic
    @Column(name = "ABSENCE_PERIOD_DAYS")
    public Integer getAbsencePeriodDays() {
	return absencePeriodDays;
    }

    public void setAbsencePeriodDays(Integer absencePeriodDays) {
	this.absencePeriodDays = absencePeriodDays;
    }

    @Basic
    @Column(name = "ABSENCE_RETURN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getAbsenceReturnDate() {
	return absenceReturnDate;
    }

    public void setAbsenceReturnDate(Date absenceReturnDate) {
	this.absenceReturnDate = absenceReturnDate;
    }

    @Basic
    @Column(name = "DISQUALIFICATION_DRUGS_FLAG")
    public Integer getDisqualificationDrugsFlag() {
	return disqualificationDrugsFlag;
    }

    public void setDisqualificationDrugsFlag(Integer disqualificationDrugsFlag) {
	this.disqualificationDrugsFlag = disqualificationDrugsFlag;
    }

    @Basic
    @Column(name = "DISQUALIFICATION_REASON")
    public String getDisqualificationReason() {
	return disqualificationReason;
    }

    public void setDisqualificationReason(String disqualificationReason) {
	this.disqualificationReason = disqualificationReason;
    }

    @Basic
    @Column(name = "DISQUALIFICATION_DRUGS_TYPE")
    public String getDisqualificationDrugsType() {
	return disqualificationDrugsType;
    }

    public void setDisqualificationDrugsType(String disqualificationDrugsType) {
	this.disqualificationDrugsType = disqualificationDrugsType;
    }

    @Basic
    @Column(name = "MEDICAL_CASE_DESC")
    public String getMedicalCaseDesc() {
	return medicalCaseDesc;
    }

    public void setMedicalCaseDesc(String medicalCaseDesc) {
	this.medicalCaseDesc = medicalCaseDesc;
    }

    @Basic
    @Column(name = "MEDICAL_WORK_DISABILITY")
    public String getMedicalWorkDisability() {
	return medicalWorkDisability;
    }

    public void setMedicalWorkDisability(String medicalWorkDisability) {
	this.medicalWorkDisability = medicalWorkDisability;
    }

    @Basic
    @Column(name = "JOB_CANCELATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJobCancelationDate() {
	return jobCancelationDate;
    }

    public void setJobCancelationDate(Date jobCancelationDate) {
	this.jobCancelationDate = jobCancelationDate;
    }

    @Basic
    @Column(name = "NATIONALITY_LOSS_TYPE")
    public Integer getNationalityLossType() {
	return nationalityLossType;
    }

    public void setNationalityLossType(Integer nationalityLossType) {
	this.nationalityLossType = nationalityLossType;
    }

    @Basic
    @Column(name = "NATIONALITY_LOSS_REASON")
    public String getNationalityLossReason() {
	return nationalityLossReason;
    }

    public void setNationalityLossReason(String nationalityLossReason) {
	this.nationalityLossReason = nationalityLossReason;
    }

    @Basic
    @Column(name = "CONTRACTOR_BASED_ON_TYPE")
    public Integer getContractorBasedOnType() {
	return contractorBasedOnType;
    }

    public void setContractorBasedOnType(Integer contractorBasedOnType) {
	this.contractorBasedOnType = contractorBasedOnType;
    }

    @Basic
    @Column(name = "CONTRACTOR_TERMINATION_REASON")
    public String getContractorTerminationReason() {
	return contractorTerminationReason;
    }

    public void setContractorTerminationReason(String contractorTerminationReason) {
	this.contractorTerminationReason = contractorTerminationReason;
    }

    @Basic
    @Column(name = "MOVED_TO")
    public String getMovedTo() {
	return movedTo;
    }

    public void setMovedTo(String movedTo) {
	this.movedTo = movedTo;
    }

    @Basic
    @Column(name = "MOVEMENT_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMovementJoiningDate() {
	return movementJoiningDate;
    }

    public void setMovementJoiningDate(Date movementJoiningDate) {
	this.movementJoiningDate = movementJoiningDate;
    }

    @Basic
    @Column(name = "MOVEMENT_JOINING_DESC")
    public String getMovementJoiningDesc() {
	return movementJoiningDesc;
    }

    public void setMovementJoiningDesc(String movementJoiningDesc) {
	this.movementJoiningDesc = movementJoiningDesc;
    }

    @Basic
    @Column(name = "DISCLAIMER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDisclaimerDate() {
	return disclaimerDate;
    }

    public void setDisclaimerDate(Date disclaimerDate) {
	this.disclaimerDate = disclaimerDate;
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
    @Column(name = "JOB_CLASSIFICATION_ID")
    public Long getJobClassificationId() {
	return jobClassificationId;
    }

    public void setJobClassificationId(Long jobClassificationId) {
	this.jobClassificationId = jobClassificationId;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CLASS_JOB_CODE")
    public String getTransEmpJobClassJobCode() {
	return transEmpJobClassJobCode;
    }

    public void setTransEmpJobClassJobCode(String transEmpJobClassJobCode) {
	this.transEmpJobClassJobCode = transEmpJobClassJobCode;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CLASS_JOB_DESC")
    public String getTransEmpJobClassJobDesc() {
	return transEmpJobClassJobDesc;
    }

    public void setTransEmpJobClassJobDesc(String transEmpJobClassJobDesc) {
	this.transEmpJobClassJobDesc = transEmpJobClassJobDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_FULL_NAME")
    public String getTransEmpUnitFullName() {
	return transEmpUnitFullName;
    }

    public void setTransEmpUnitFullName(String transEmpUnitFullName) {
	this.transEmpUnitFullName = transEmpUnitFullName;
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_ID")
    public Long getTransEmpUnitId() {
	return transEmpUnitId;
    }

    public void setTransEmpUnitId(Long transEmpUnitId) {
	this.transEmpUnitId = transEmpUnitId;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
    }

    @Basic
    @Column(name = "REASON_ID")
    public Long getReasonId() {
	return reasonId;
    }

    public void setReasonId(Long reasonId) {
	this.reasonId = reasonId;
    }

    @Basic
    @Column(name = "CANCEL_TRANSACTION_ID")
    public Long getCancelTransactionId() {
	return cancelTransactionId;
    }

    public void setCancelTransactionId(Long cancelTransactionId) {
	this.cancelTransactionId = cancelTransactionId;
    }

    @Basic
    @Column(name = "CANCEL_TRANSACTION_REASON")
    public String getCancelTransactionReason() {
	return cancelTransactionReason;
    }

    public void setCancelTransactionReason(String cancelTransactionReason) {
	this.cancelTransactionReason = cancelTransactionReason;
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
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
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
    @Column(name = "APPROVED_ID")
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
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
    @Column(name = "INJURY_IN_DUTY_FLAG")
    public Integer getInjuryInDutyFlag() {
	return injuryInDutyFlag;
    }

    public void setInjuryInDutyFlag(Integer injuryInDutyFlag) {
	this.injuryInDutyFlag = injuryInDutyFlag;
    }

    @Basic
    @Column(name = "INJURY_TYPE")
    public Integer getInjuryType() {
	return injuryType;
    }

    public void setInjuryType(Integer injuryType) {
	this.injuryType = injuryType;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {

	return "empId:" + empId + AUDIT_SEPARATOR +
		"serviceTerminationDate:" + serviceTerminationDate + AUDIT_SEPARATOR +
		"status:" + status + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR +
		"referring:" + referring + AUDIT_SEPARATOR +
		"transEmpUnitId" + transEmpUnitId + AUDIT_SEPARATOR +

		"terminationRequestType:" + terminationRequestType + AUDIT_SEPARATOR +
		"desiredTerminationDate:" + desiredTerminationDate + AUDIT_SEPARATOR +
		"ministryRequestNumber:" + ministryRequestNumber + AUDIT_SEPARATOR +
		"ministryRequestDate:" + ministryRequestDate + AUDIT_SEPARATOR +

		"injuryInDutyFlag:" + injuryInDutyFlag + AUDIT_SEPARATOR +
		"injuryType:" + injuryType + AUDIT_SEPARATOR +

		"ministryRejectionNumber:" + ministryRejectionNumber + AUDIT_SEPARATOR +
		"ministryRejectionDate:" + ministryRejectionDate + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"decisionDate:" + decisionDate + AUDIT_SEPARATOR +

		"dismissDecisionNumber:" + dismissDecisionNumber + AUDIT_SEPARATOR +
		"dismissDecisionDate:" + dismissDecisionDate + AUDIT_SEPARATOR +
		"dismissStatus:" + dismissStatus + AUDIT_SEPARATOR +
		"dismissResumptionNumber:" + dismissResumptionNumber + AUDIT_SEPARATOR +
		"dismissResumptionYear:" + dismissResumptionYear + AUDIT_SEPARATOR +
		"dismissApprovalNumber:" + dismissApprovalNumber + AUDIT_SEPARATOR +
		"dismissApprovalDate:" + dismissApprovalDate + AUDIT_SEPARATOR +
		"dismissBasedOn:" + dismissBasedOn + AUDIT_SEPARATOR +

		"dismissJudgment:" + dismissJudgment + AUDIT_SEPARATOR +
		"jobCancelationDate:" + jobCancelationDate + AUDIT_SEPARATOR;
    }
}
