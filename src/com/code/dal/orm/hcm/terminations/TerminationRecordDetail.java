package com.code.dal.orm.hcm.terminations;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQuery(name = "hcm_terminationRecordDetail_getRunningTerminationRecords",
	query = " select d from TerminationRecordDetail d, TerminationRecord r " +
		" where d.recordId = r.id " +
		" and r.status in (5, 10) " +
		" and ( (r.categoryId <> 1)  or (r.categoryId = 1 and d.status not in (15, 25) ) )" +
		" and d.empId in ( :P_EMPS_IDS )" +
		" and (:P_EXCLUDED_RECORD_ID = -1 or r.id <> :P_EXCLUDED_RECORD_ID)")
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_STE_RECORDS_DETAILS")
public class TerminationRecordDetail extends AuditEntity implements UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long recordId;
    private Long empId;
    private Date serviceTerminationDate;
    private Long status;

    private String remarks;
    private String referring;
    private String terminationRequestReason;
    private Long terminationRequestType;
    private Date desiredTerminationDate;
    private String ministryRequestNumber;
    private Date ministryRequestDate;
    private String ministryRejectionNumber;
    private Date ministryRejectionDate;
    private String decisionNumber;
    private Date decisionDate;

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
    private Long empRankId;
    private Date empTerminationDueDate;
    private Integer injuryInDutyFlag;
    private Integer injuryType;

    @SequenceGenerator(name = "HCMServiceTerminationRecordSeq",
	    sequenceName = "HCM_STE_RECORD_SEQ")
    @Id
    @GeneratedValue(generator = "HCMServiceTerminationRecordSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "RECORD_ID")
    public Long getRecordId() {
	return recordId;
    }

    public void setRecordId(Long recordId) {
	this.recordId = recordId;
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
    @Column(name = "EMP_RANK_ID")
    public Long getEmpRankId() {
	return empRankId;
    }

    public void setEmpRankId(Long empRankId) {
	this.empRankId = empRankId;
    }

    @Basic
    @Column(name = "EMP_TERMINATION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEmpTerminationDueDate() {
	return empTerminationDueDate;
    }

    public void setEmpTerminationDueDate(Date empTerminationDueDate) {
	this.empTerminationDueDate = empTerminationDueDate;

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
		"terminationRequestReason:" + terminationRequestReason + AUDIT_SEPARATOR +
		"terminationRequestType:" + terminationRequestType + AUDIT_SEPARATOR +
		"desiredTerminationDate:" + desiredTerminationDate + AUDIT_SEPARATOR +
		"ministryRequestNumber:" + ministryRequestNumber + AUDIT_SEPARATOR +
		"ministryRequestDate:" + ministryRequestDate + AUDIT_SEPARATOR +
		"ministryRejectionNumber:" + ministryRejectionNumber + AUDIT_SEPARATOR +
		"ministryRejectionDate:" + ministryRejectionDate + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"decisionDate:" + decisionDate + AUDIT_SEPARATOR +

		"extensionStartDate:" + extensionStartDate + AUDIT_SEPARATOR +
		"extensionEndDate:" + extensionEndDate + AUDIT_SEPARATOR +
		"extensionPeriodDays:" + extensionPeriodDays + AUDIT_SEPARATOR +
		"extensionPeriodMonths:" + extensionPeriodMonths + AUDIT_SEPARATOR +
		"extensionFlag:" + extensionFlag + AUDIT_SEPARATOR +
		"judgmentFlag:" + judgmentFlag + AUDIT_SEPARATOR +
		"specializationPeriodFlag:" + specializationPeriodFlag + AUDIT_SEPARATOR +
		"foreignerMarriageFlag:" + foreignerMarriageFlag + AUDIT_SEPARATOR +

		"injuryInDutyFlag:" + injuryInDutyFlag + AUDIT_SEPARATOR +
		"injuryType:" + injuryType + AUDIT_SEPARATOR +

		"dismissType:" + dismissType + AUDIT_SEPARATOR +
		"dismissDecisionNumber:" + dismissDecisionNumber + AUDIT_SEPARATOR +
		"dismissDecisionDate:" + dismissDecisionDate + AUDIT_SEPARATOR +
		"dismissStatus:" + dismissStatus + AUDIT_SEPARATOR +
		"dismissResumptionNumber:" + dismissResumptionNumber + AUDIT_SEPARATOR +
		"dismissResumptionYear:" + dismissResumptionYear + AUDIT_SEPARATOR +
		"dismissApprovalNumber:" + dismissApprovalNumber + AUDIT_SEPARATOR +
		"dismissApprovalDate:" + dismissApprovalDate + AUDIT_SEPARATOR +
		"dismissBasedOn:" + dismissBasedOn + AUDIT_SEPARATOR +
		"dismissJudgment:" + dismissJudgment + AUDIT_SEPARATOR +

		"deathCertNumber:" + deathCertNumber + AUDIT_SEPARATOR +
		"deathCertDate:" + deathCertDate + AUDIT_SEPARATOR +
		"deathCertIssuePlace:" + deathCertIssuePlace + AUDIT_SEPARATOR +
		"deathDate:" + deathDate + AUDIT_SEPARATOR +
		"deathInDutyFlag:" + deathInDutyFlag + AUDIT_SEPARATOR +
		"deathType:" + deathType + AUDIT_SEPARATOR +
		"deathInOperations:" + deathInOperations + AUDIT_SEPARATOR +

		"absenceType:" + absenceType + AUDIT_SEPARATOR +
		"absenceStartDate:" + absenceStartDate + AUDIT_SEPARATOR +
		"absencePeriodDays:" + absencePeriodDays + AUDIT_SEPARATOR +
		"absenceReturnDate:" + absenceReturnDate + AUDIT_SEPARATOR +

		"disqualificationDrugsFlag:" + disqualificationDrugsFlag + AUDIT_SEPARATOR +
		"disqualificationReason:" + disqualificationReason + AUDIT_SEPARATOR +
		"disqualificationDrugsType:" + disqualificationDrugsType + AUDIT_SEPARATOR +
		"medicalCaseDesc:" + medicalCaseDesc + AUDIT_SEPARATOR +
		"medicalWorkDisability:" + medicalWorkDisability + AUDIT_SEPARATOR +

		"jobCancelationDate:" + jobCancelationDate + AUDIT_SEPARATOR +
		"nationalityLossType:" + nationalityLossType + AUDIT_SEPARATOR +
		"nationalityLossReason:" + nationalityLossReason + AUDIT_SEPARATOR +
		"contractorBasedOnType:" + contractorBasedOnType + AUDIT_SEPARATOR +
		"contractorTerminationReason:" + contractorTerminationReason + AUDIT_SEPARATOR +
		"empTerminationDueDate:" + empTerminationDueDate + AUDIT_SEPARATOR;

    }
}
