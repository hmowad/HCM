package com.code.dal.orm.hcm.terminations;

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
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_terminationTransactionData_searchTerminationTransactions",
		query = " select tt from TerminationTransactionData tt " +
			" where (:P_RECORD_DETAIL_ID = '-1' or tt.recordDetailId = :P_RECORD_DETAIL_ID) " +
			" and (:P_EMP_ID = '-1' or tt.empId = :P_EMP_ID) " +
			" and (:P_TRANSACTION_TYPE_CODE = '-1' or tt.transactionTypeCode = :P_TRANSACTION_TYPE_CODE) " +
			" and (:P_DECISION_NUMBER = '-1' or tt.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_FROM_DATE_FLAG = -1 or to_date(:P_FROM_DATE, 'MI/MM/YYYY') <= tt.decisionDate) " +
			" and (:P_TO_DATE_FLAG = -1  or to_date(:P_TO_DATE, 'MI/MM/YYYY') >= tt.decisionDate) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1 or tt.categoryId in ( :P_CATEGORIES_IDS )) " +
			" order by tt.id "),

	@NamedQuery(name = "hcm_terminationTransactionData_searchTerminationTransactionsById",
		query = " select tt from TerminationTransactionData tt" +
			" where tt.id = :P_ID "),

	@NamedQuery(name = "hcm_terminationTransactionData_getLastTerminationTransaction",
		query = " select tt from TerminationTransactionData tt, Employee e " +
			" where tt.empId = e.id " +
			" and tt.empId = :P_EMP_ID" +
			" and tt.transactionTypeCode = :P_TERMINATION_TRANS_TYPE_CODE " +
			" and e.serviceTerminationDate is not null " +
			" and tt.decisionDate = ( select max(ste.decisionDate) from TerminationTransaction ste where ste.empId = tt.empId ) "),

	@NamedQuery(name = "hcm_terminationTransactionData_searchTerminationCivilianMovementTransaction",
		query = " select tt from TerminationTransactionData tt" +
			" where :P_EMP_ID = -1 or tt.empId =  :P_EMP_ID " +
			" and (to_date(:P_MOVEMENT_JOINING_DATE, 'MI/MM/YYYY') = tt.movementJoiningDate)" +
			" and (to_date(:P_DISCLAIMER_DATE, 'MI/MM/YYYY') = tt.disclaimerDate)" +
			" and (to_date(:P_SERVICE_TERMINATION_DATE, 'MI/MM/YYYY') = tt.serviceTerminationDate)" +
			" and  tt.movementJoiningDesc like  :P_MVT_JOINING_DESC "),

	@NamedQuery(name = "hcm_terminationTransactionData_getTerminationTransactionAfterDecisionDate",
		query = (" select tt from TerminationTransactionData tt "
			+ " where (:P_EMP_ID = -1 or tt.empId = :P_EMP_ID) "
			+ " and (:P_DECISION_DATE_FLAG = -1 or to_date(:P_DECISION_DATE, 'MI/MM/YYYY') <= tt.decisionDate) "
			+ " order by tt.decisionDate,tt.decisionNumber ")),

	@NamedQuery(name = "hcm_terminationTransactionData_getTerminationTransactionsByunitHkey",
		query = (" select tt, e from TerminationTransactionData tt ,EmployeeData e, UnitData u "
			+ " where (tt.transEmpUnitId = u.id) "
			+ " and ( u.hKey like :P_UNIT_HKEY ) "
			+ " and ( tt.categoryId <> 6 ) "
			+ " and ( tt.empId = e.id ) "
			+ " and ( tt.transactionTypeCode = 10 ) "
			+ " and ( e.statusId = 70 ) "
			+ " order by tt.decisionDate,tt.decisionNumber "))
})
@Entity
@Table(name = "HCM_VW_STE_TRANSACTIONS")
public class TerminationTransactionData extends BaseEntity {
    private Long id;
    private Long recordDetailId;
    private Long empId;
    private String empName;
    private Long categoryId;
    private Double basicSalary;
    private Long degreeId;
    private String degreeDesc;
    private Date serviceTerminationDate;
    private String serviceTerminationDateString;
    private Long status;

    private String remarks;
    private String referring;
    private String attachments;
    private String terminationRequestReason;
    private Long terminationRequestType;
    private Date desiredTerminationDate;
    private String desiredTerminationDateString;
    private String ministryRequestNumber;
    private Date ministryRequestDate;
    private String ministryRequestDateString;
    private String ministryRejectionNumber;
    private Date ministryRejectionDate;
    private String ministryRejectionDateString;

    private Date extensionStartDate;
    private String extensionStartDateString;
    private Date extensionEndDate;
    private String extensionEndDateString;
    private Integer extensionPeriodDays;
    private Integer extensionPeriodMonths;
    private Integer extensionFlag;
    private Boolean extensionFlagBoolean;
    private Integer judgmentFlag;
    private Boolean judgmentFlagBoolean;
    private Integer specializationPeriodFlag;
    private Boolean specializationPeriodFlagBoolean;
    private Integer foreignerMarriageFlag;
    private Boolean foreignerMarriageFlagBoolean;

    private Integer dismissType;
    private String dismissDecisionNumber;
    private Date dismissDecisionDate;
    private String dismissDecisionDateString;
    private Integer dismissStatus;
    private String dismissResumptionNumber;
    private Integer dismissResumptionYear;
    private String dismissApprovalNumber;
    private Date dismissApprovalDate;
    private String dismissApprovalDateString;
    private Integer dismissBasedOn;
    private String dismissBasedOnDesc;
    private String dismissJudgment;

    private String deathCertNumber;
    private Date deathCertDate;
    private String deathCertDateString;
    private String deathCertIssuePlace;
    private Date deathDate;
    private String deathDateString;
    private Integer deathInDutyFlag;
    private Boolean deathInDutyFlagBoolean;
    private Integer deathType;
    private String deathInOperations;

    private Integer absenceType;
    private Date absenceStartDate;
    private String absenceStartDateString;
    private Integer absencePeriodDays;
    private Date absenceReturnDate;
    private String absenceReturnDateString;

    private Integer disqualificationDrugsFlag;
    private Boolean disqualificationDrugsFlagBoolean;
    private String disqualificationReason;
    private String disqualificationDrugsType;
    private String medicalCaseDesc;
    private String medicalWorkDisability;

    private Date jobCancelationDate;
    private String jobCancelationDateString;
    private Integer nationalityLossType;
    private String nationalityLossReason;
    private Integer contractorBasedOnType;
    private String contractorTerminationReason;

    private String movedTo;
    private Date movementJoiningDate;
    private String movementJoiningDateString;
    private String movementJoiningDesc;
    private Date disclaimerDate;
    private String disclaimerDateString;
    private Long jobId;
    private String jobCode;
    private String jobName;
    private Long jobClassificationId;

    private String transEmpRankDesc;
    private String transEmpJobClassJobCode;
    private String transEmpJobClassJobDesc;
    private String transEmpUnitFullName;
    private Long transEmpUnitId;
    private Long cancelTransactionId;
    private Long transactionTypeId;
    private String transactionTypeDesc;
    private Integer transactionTypeCode;

    private Long reasonId;
    private String reasonDesc;
    private String cancelTransactionReason;
    private Integer eflag;
    private Integer migFlag;

    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long decisionRegionId;
    private String internalCopies;
    private String externalCopies;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private Integer injuryInDutyFlag;
    private Integer injuryType;

    private TerminationTransaction terminationTransaction;

    public TerminationTransactionData() {
	this.terminationTransaction = new TerminationTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.terminationTransaction.setId(id);
    }

    @Basic
    @Column(name = "RECORD_DETAIL_ID")
    public Long getRecordDetailId() {
	return recordDetailId;
    }

    public void setRecordDetailId(Long recordDetailId) {
	this.recordDetailId = recordDetailId;
	this.terminationTransaction.setRecordDetailId(recordDetailId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	this.terminationTransaction.setEmpId(empId);
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
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	this.terminationTransaction.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "BASIC_SALARY")
    public Double getBasicSalary() {
	return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
	this.basicSalary = basicSalary;
	this.terminationTransaction.setBasicSalary(basicSalary);
    }

    @Basic
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
	this.terminationTransaction.setDegreeId(degreeId);
    }

    @Basic
    @Column(name = "DEGREE_DESC")
    public String getDegreeDesc() {
	return degreeDesc;
    }

    public void setDegreeDesc(String degreeDesc) {
	this.degreeDesc = degreeDesc;
    }

    @Basic
    @Column(name = "SERVICE_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getServiceTerminationDate() {
	return serviceTerminationDate;
    }

    public void setServiceTerminationDate(Date serviceTerminationDate) {
	this.serviceTerminationDate = serviceTerminationDate;
	this.serviceTerminationDateString = HijriDateService.getHijriDateString(serviceTerminationDate);
	this.terminationTransaction.setServiceTerminationDate(serviceTerminationDate);
    }

    @Transient
    public String getServiceTerminationDateString() {
	return serviceTerminationDateString;
    }

    public void setServiceTerminationDateString(String serviceTerminationDateString) {
	this.serviceTerminationDateString = serviceTerminationDateString;
	this.serviceTerminationDate = HijriDateService.getHijriDate(serviceTerminationDateString);
	this.terminationTransaction.setServiceTerminationDate(serviceTerminationDate);
    }

    @Basic
    @Column(name = "STATUS")
    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
	this.terminationTransaction.setStatus(status);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	this.terminationTransaction.setRemarks(remarks);
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	this.terminationTransaction.setReferring(referring);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	this.terminationTransaction.setAttachments(attachments);
    }

    @Basic
    @Column(name = "TERMINATION_REQUEST_REASON")
    public String getTerminationRequestReason() {
	return terminationRequestReason;
    }

    public void setTerminationRequestReason(String terminationRequestReason) {
	this.terminationRequestReason = terminationRequestReason;
	this.terminationTransaction.setTerminationRequestReason(terminationRequestReason);
    }

    @Basic
    @Column(name = "TERMINATION_REQUEST_TYPE")
    public Long getTerminationRequestType() {
	return terminationRequestType;
    }

    public void setTerminationRequestType(Long terminationRequestType) {
	this.terminationRequestType = terminationRequestType;
	this.terminationTransaction.setTerminationRequestType(terminationRequestType);
    }

    @Basic
    @Column(name = "DESIRED_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDesiredTerminationDate() {
	return desiredTerminationDate;
    }

    public void setDesiredTerminationDate(Date desiredTerminationDate) {
	this.desiredTerminationDate = desiredTerminationDate;
	this.desiredTerminationDateString = HijriDateService.getHijriDateString(desiredTerminationDate);
	this.terminationTransaction.setDesiredTerminationDate(desiredTerminationDate);
    }

    @Transient
    public String getDesiredTerminationDateString() {
	return desiredTerminationDateString;
    }

    public void setDesiredTerminationDateString(String desiredTerminationDateString) {
	this.desiredTerminationDateString = desiredTerminationDateString;
	this.desiredTerminationDate = HijriDateService.getHijriDate(desiredTerminationDateString);
	this.terminationTransaction.setDesiredTerminationDate(desiredTerminationDate);
    }

    @Basic
    @Column(name = "MINISTRY_REQUEST_NUMBER")
    public String getMinistryRequestNumber() {
	return ministryRequestNumber;
    }

    public void setMinistryRequestNumber(String ministryRequestNumber) {
	this.ministryRequestNumber = ministryRequestNumber;
	this.terminationTransaction.setMinistryRequestNumber(ministryRequestNumber);
    }

    @Basic
    @Column(name = "MINISTRY_REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryRequestDate() {
	return ministryRequestDate;
    }

    public void setMinistryRequestDate(Date ministryRequestDate) {
	this.ministryRequestDate = ministryRequestDate;
	this.ministryRequestDateString = HijriDateService.getHijriDateString(ministryRequestDate);
	this.terminationTransaction.setMinistryRequestDate(ministryRequestDate);
    }

    @Transient
    public String getMinistryRequestDateString() {
	return ministryRequestDateString;
    }

    public void setMinistryRequestDateString(String ministryRequestDateString) {
	this.ministryRequestDateString = ministryRequestDateString;
	this.ministryRequestDate = HijriDateService.getHijriDate(ministryRequestDateString);
	this.terminationTransaction.setMinistryRequestDate(ministryRequestDate);
    }

    @Basic
    @Column(name = "MINISTRY_REJECTION_NUMBER")
    public String getMinistryRejectionNumber() {
	return ministryRejectionNumber;
    }

    public void setMinistryRejectionNumber(String ministryRejectionNumber) {
	this.ministryRejectionNumber = ministryRejectionNumber;
	this.terminationTransaction.setMinistryRejectionNumber(ministryRejectionNumber);
    }

    @Basic
    @Column(name = "MINISTRY_REJECTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMinistryRejectionDate() {
	return ministryRejectionDate;
    }

    public void setMinistryRejectionDate(Date ministryRejectionDate) {
	this.ministryRejectionDate = ministryRejectionDate;
	this.ministryRejectionDateString = HijriDateService.getHijriDateString(ministryRejectionDate);
	this.terminationTransaction.setMinistryRejectionDate(ministryRejectionDate);
    }

    @Transient
    public String getMinistryRejectionDateString() {
	return ministryRejectionDateString;
    }

    public void setMinistryRejectionDateString(String ministryRejectionDateString) {
	this.ministryRejectionDateString = ministryRejectionDateString;
	this.ministryRejectionDate = HijriDateService.getHijriDate(ministryRejectionDateString);
	this.terminationTransaction.setMinistryRejectionDate(ministryRejectionDate);
    }

    @Basic
    @Column(name = "EXTENSION_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExtensionStartDate() {
	return extensionStartDate;
    }

    public void setExtensionStartDate(Date extensionStartDate) {
	this.extensionStartDate = extensionStartDate;
	this.extensionStartDateString = HijriDateService.getHijriDateString(extensionStartDate);
	this.terminationTransaction.setExtensionStartDate(extensionStartDate);
    }

    @Transient
    public String getExtensionStartDateString() {
	return extensionStartDateString;
    }

    public void setExtensionStartDateString(String extensionStartDateString) {
	this.extensionStartDateString = extensionStartDateString;
	this.extensionStartDate = HijriDateService.getHijriDate(extensionStartDateString);
	this.terminationTransaction.setExtensionStartDate(extensionStartDate);
    }

    @Basic
    @Column(name = "EXTENSION_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExtensionEndDate() {
	return extensionEndDate;
    }

    public void setExtensionEndDate(Date extensionEndDate) {
	this.extensionEndDate = extensionEndDate;
	this.extensionEndDateString = HijriDateService.getHijriDateString(extensionEndDate);
	this.terminationTransaction.setExtensionEndDate(extensionEndDate);
    }

    @Transient
    public String getExtensionEndDateString() {
	return extensionEndDateString;
    }

    public void setExtensionEndDateString(String extensionEndDateString) {
	this.extensionEndDateString = extensionEndDateString;
	this.extensionEndDate = HijriDateService.getHijriDate(extensionEndDateString);
	this.terminationTransaction.setExtensionEndDate(extensionEndDate);
    }

    @Basic
    @Column(name = "EXTENSION_PERIOD_DAYS")
    public Integer getExtensionPeriodDays() {
	return extensionPeriodDays;
    }

    public void setExtensionPeriodDays(Integer extensionPeriodDays) {
	this.extensionPeriodDays = extensionPeriodDays;
	this.terminationTransaction.setExtensionPeriodDays(extensionPeriodDays);
    }

    @Basic
    @Column(name = "EXTENSION_PERIOD_MONTHS")
    public Integer getExtensionPeriodMonths() {
	return extensionPeriodMonths;
    }

    public void setExtensionPeriodMonths(Integer extensionPeriodMonths) {
	this.extensionPeriodMonths = extensionPeriodMonths;
	this.terminationTransaction.setExtensionPeriodMonths(extensionPeriodMonths);
    }

    @Basic
    @Column(name = "EXTENSION_FLAG")
    public Integer getExtensionFlag() {
	return extensionFlag;
    }

    public void setExtensionFlag(Integer extensionFlag) {
	this.extensionFlag = extensionFlag;
	this.terminationTransaction.setExtensionFlag(extensionFlag);
	if (this.extensionFlag == null || this.extensionFlag == FlagsEnum.OFF.getCode()) {
	    this.extensionFlagBoolean = false;
	} else {
	    this.extensionFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getExtensionFlagBoolean() {
	return extensionFlagBoolean;
    }

    public void setExtensionFlagBoolean(Boolean extensionFlagBoolean) {
	this.extensionFlagBoolean = extensionFlagBoolean;
	if (this.extensionFlagBoolean == false) {
	    setExtensionFlag(FlagsEnum.OFF.getCode());
	} else {
	    setExtensionFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "JUDGMENT_FLAG")
    public Integer getJudgmentFlag() {
	return judgmentFlag;
    }

    public void setJudgmentFlag(Integer judgmentFlag) {
	this.judgmentFlag = judgmentFlag;
	this.terminationTransaction.setJudgmentFlag(judgmentFlag);
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
    @Column(name = "SPECIALIZATION_PERIOD_FLAG")
    public Integer getSpecializationPeriodFlag() {
	return specializationPeriodFlag;
    }

    public void setSpecializationPeriodFlag(Integer specializationPeriodFlag) {
	this.specializationPeriodFlag = specializationPeriodFlag;
	this.terminationTransaction.setSpecializationPeriodFlag(specializationPeriodFlag);
	if (this.specializationPeriodFlag == null || this.specializationPeriodFlag == FlagsEnum.OFF.getCode()) {
	    this.specializationPeriodFlagBoolean = false;
	} else {
	    this.specializationPeriodFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getSpecializationPeriodFlagBoolean() {
	return specializationPeriodFlagBoolean;
    }

    public void setSpecializationPeriodFlagBoolean(Boolean specializationPeriodFlagBoolean) {
	this.specializationPeriodFlagBoolean = specializationPeriodFlagBoolean;
	if (this.specializationPeriodFlagBoolean == false) {
	    setSpecializationPeriodFlag(FlagsEnum.OFF.getCode());
	} else {
	    setSpecializationPeriodFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "FOREIGNER_MARRIAGE_FLAG")
    public Integer getForeignerMarriageFlag() {
	return foreignerMarriageFlag;
    }

    public void setForeignerMarriageFlag(Integer foreignerMarriageFlag) {
	this.foreignerMarriageFlag = foreignerMarriageFlag;
	this.terminationTransaction.setForeignerMarriageFlag(foreignerMarriageFlag);
	if (this.foreignerMarriageFlag == null || this.foreignerMarriageFlag == FlagsEnum.OFF.getCode()) {
	    this.foreignerMarriageFlagBoolean = false;
	} else {
	    this.foreignerMarriageFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getForeignerMarriageFlagBoolean() {
	return foreignerMarriageFlagBoolean;
    }

    public void setForeignerMarriageFlagBoolean(Boolean foreignerMarriageFlagBoolean) {
	this.foreignerMarriageFlagBoolean = foreignerMarriageFlagBoolean;
	if (this.foreignerMarriageFlagBoolean == false) {
	    setForeignerMarriageFlag(FlagsEnum.OFF.getCode());
	} else {
	    setForeignerMarriageFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "DISMISS_TYPE")
    public Integer getDismissType() {
	return dismissType;
    }

    public void setDismissType(Integer dismissType) {
	this.dismissType = dismissType;
	this.terminationTransaction.setDismissType(dismissType);
    }

    @Basic
    @Column(name = "DISMISS_DECISION_NUMBER")
    public String getDismissDecisionNumber() {
	return dismissDecisionNumber;
    }

    public void setDismissDecisionNumber(String dismissDecisionNumber) {
	this.dismissDecisionNumber = dismissDecisionNumber;
	this.terminationTransaction.setDismissDecisionNumber(dismissDecisionNumber);
    }

    @Basic
    @Column(name = "DISMISS_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDismissDecisionDate() {
	return dismissDecisionDate;
    }

    public void setDismissDecisionDate(Date dismissDecisionDate) {
	this.dismissDecisionDate = dismissDecisionDate;
	this.dismissDecisionDateString = HijriDateService.getHijriDateString(dismissDecisionDate);
	this.terminationTransaction.setDismissDecisionDate(dismissDecisionDate);
    }

    @Transient
    public String getDismissDecisionDateString() {
	return dismissDecisionDateString;
    }

    public void setDismissDecisionDateString(String dismissDecisionDateString) {
	this.dismissDecisionDateString = dismissDecisionDateString;
	this.dismissDecisionDate = HijriDateService.getHijriDate(dismissDecisionDateString);
	this.terminationTransaction.setDismissDecisionDate(dismissDecisionDate);
    }

    @Basic
    @Column(name = "DISMISS_STATUS")
    public Integer getDismissStatus() {
	return dismissStatus;
    }

    public void setDismissStatus(Integer dismissStatus) {
	this.dismissStatus = dismissStatus;
	this.terminationTransaction.setDismissStatus(dismissStatus);
    }

    @Basic
    @Column(name = "DISMISS_RESUMPTION_NUMBER")
    public String getDismissResumptionNumber() {
	return dismissResumptionNumber;
    }

    public void setDismissResumptionNumber(String dismissResumptionNumber) {
	this.dismissResumptionNumber = dismissResumptionNumber;
	this.terminationTransaction.setDismissResumptionNumber(dismissResumptionNumber);
    }

    @Basic
    @Column(name = "DISMISS_RESUMPTION_YEAR")
    public Integer getDismissResumptionYear() {
	return dismissResumptionYear;
    }

    public void setDismissResumptionYear(Integer dismissResumptionYear) {
	this.dismissResumptionYear = dismissResumptionYear;
	this.terminationTransaction.setDismissResumptionYear(dismissResumptionYear);
    }

    @Basic
    @Column(name = "DISMISS_APPROVAL_NUMBER")
    public String getDismissApprovalNumber() {
	return dismissApprovalNumber;
    }

    public void setDismissApprovalNumber(String dismissApprovalNumber) {
	this.dismissApprovalNumber = dismissApprovalNumber;
	this.terminationTransaction.setDismissApprovalNumber(dismissApprovalNumber);
    }

    @Basic
    @Column(name = "DISMISS_APPROVAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDismissApprovalDate() {
	return dismissApprovalDate;
    }

    public void setDismissApprovalDate(Date dismissApprovalDate) {
	this.dismissApprovalDate = dismissApprovalDate;
	this.dismissApprovalDateString = HijriDateService.getHijriDateString(dismissApprovalDate);
	this.terminationTransaction.setDismissApprovalDate(dismissApprovalDate);
    }

    @Transient
    public String getDismissApprovalDateString() {
	return dismissApprovalDateString;
    }

    public void setDismissApprovalDateString(String dismissApprovalDateString) {
	this.dismissApprovalDateString = dismissApprovalDateString;
	this.dismissApprovalDate = HijriDateService.getHijriDate(dismissApprovalDateString);
	this.terminationTransaction.setDismissApprovalDate(dismissApprovalDate);
    }

    @Basic
    @Column(name = "DISMISS_BASED_ON")
    public Integer getDismissBasedOn() {
	return dismissBasedOn;
    }

    public void setDismissBasedOn(Integer dismissBasedOn) {
	this.dismissBasedOn = dismissBasedOn;
	this.terminationTransaction.setDismissBasedOn(dismissBasedOn);
    }

    @Basic
    @Column(name = "DISMISS_BASED_ON_DESC")
    public String getDismissBasedOnDesc() {
	return dismissBasedOnDesc;
    }

    public void setDismissBasedOnDesc(String dismissBasedOnDesc) {
	this.dismissBasedOnDesc = dismissBasedOnDesc;
	this.terminationTransaction.setDismissBasedOnDesc(dismissBasedOnDesc);
    }

    @Basic
    @Column(name = "DISMISS_JUDGMENT")
    public String getDismissJudgment() {
	return dismissJudgment;
    }

    public void setDismissJudgment(String dismissJudgment) {
	this.dismissJudgment = dismissJudgment;
	this.terminationTransaction.setDismissJudgment(dismissJudgment);
    }

    @Basic
    @Column(name = "DEATH_CERT_NUMBER")
    public String getDeathCertNumber() {
	return deathCertNumber;
    }

    public void setDeathCertNumber(String deathCertNumber) {
	this.deathCertNumber = deathCertNumber;
	this.terminationTransaction.setDeathCertNumber(deathCertNumber);
    }

    @Basic
    @Column(name = "DEATH_CERT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeathCertDate() {
	return deathCertDate;
    }

    public void setDeathCertDate(Date deathCertDate) {
	this.deathCertDate = deathCertDate;
	this.deathCertDateString = HijriDateService.getHijriDateString(deathCertDate);
	this.terminationTransaction.setDeathCertDate(deathCertDate);
    }

    @Transient
    public String getDeathCertDateString() {
	return deathCertDateString;
    }

    public void setDeathCertDateString(String deathCertDateString) {
	this.deathCertDateString = deathCertDateString;
	this.deathCertDate = HijriDateService.getHijriDate(deathCertDateString);
	this.terminationTransaction.setDeathCertDate(deathCertDate);
    }

    @Basic
    @Column(name = "DEATH_CERT_ISSUE_PLACE")
    public String getDeathCertIssuePlace() {
	return deathCertIssuePlace;
    }

    public void setDeathCertIssuePlace(String deathCertIssuePlace) {
	this.deathCertIssuePlace = deathCertIssuePlace;
	this.terminationTransaction.setDeathCertIssuePlace(deathCertIssuePlace);
    }

    @Basic
    @Column(name = "DEATH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeathDate() {
	return deathDate;
    }

    public void setDeathDate(Date deathDate) {
	this.deathDate = deathDate;
	this.deathDateString = HijriDateService.getHijriDateString(deathDate);
	this.terminationTransaction.setDeathDate(deathDate);
    }

    @Transient
    public String getDeathDateString() {
	return deathDateString;
    }

    public void setDeathDateString(String deathDateString) {
	this.deathDateString = deathDateString;
	this.deathDate = HijriDateService.getHijriDate(deathDateString);
	this.terminationTransaction.setDeathDate(deathDate);
    }

    @Basic
    @Column(name = "DEATH_IN_DUTY_FLAG")
    public Integer getDeathInDutyFlag() {
	return deathInDutyFlag;
    }

    public void setDeathInDutyFlag(Integer deathInDutyFlag) {
	this.deathInDutyFlag = deathInDutyFlag;
	this.terminationTransaction.setDeathInDutyFlag(deathInDutyFlag);
	if (this.deathInDutyFlag == null || this.deathInDutyFlag == FlagsEnum.OFF.getCode()) {
	    this.deathInDutyFlagBoolean = false;
	} else {
	    this.deathInDutyFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getDeathInDutyFlagBoolean() {
	return deathInDutyFlagBoolean;
    }

    public void setDeathInDutyFlagBoolean(Boolean deathInDutyFlagBoolean) {
	this.deathInDutyFlagBoolean = deathInDutyFlagBoolean;
	if (this.deathInDutyFlagBoolean == false) {
	    setDeathInDutyFlag(FlagsEnum.OFF.getCode());
	} else {
	    setDeathInDutyFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "DEATH_TYPE")
    public Integer getDeathType() {
	return deathType;
    }

    public void setDeathType(Integer deathType) {
	this.deathType = deathType;
	this.terminationTransaction.setDeathType(deathType);
    }

    @Basic
    @Column(name = "DEATH_IN_OPERATIONS")
    public String getDeathInOperations() {
	return deathInOperations;
    }

    public void setDeathInOperations(String deathInOperations) {
	this.deathInOperations = deathInOperations;
	this.terminationTransaction.setDeathInOperations(deathInOperations);
    }

    @Basic
    @Column(name = "ABSENCE_TYPE")
    public Integer getAbsenceType() {
	return absenceType;
    }

    public void setAbsenceType(Integer absenceType) {
	this.absenceType = absenceType;
	this.terminationTransaction.setAbsenceType(absenceType);
    }

    @Basic
    @Column(name = "ABSENCE_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getAbsenceStartDate() {
	return absenceStartDate;
    }

    public void setAbsenceStartDate(Date absenceStartDate) {
	this.absenceStartDate = absenceStartDate;
	this.absenceStartDateString = HijriDateService.getHijriDateString(absenceStartDate);
	this.terminationTransaction.setAbsenceStartDate(absenceStartDate);
    }

    @Transient
    public String getAbsenceStartDateString() {
	return absenceStartDateString;
    }

    public void setAbsenceStartDateString(String absenceStartDateString) {
	this.absenceStartDateString = absenceStartDateString;
	this.absenceStartDate = HijriDateService.getHijriDate(absenceStartDateString);
	this.terminationTransaction.setAbsenceStartDate(absenceStartDate);
    }

    @Basic
    @Column(name = "ABSENCE_PERIOD_DAYS")
    public Integer getAbsencePeriodDays() {
	return absencePeriodDays;
    }

    public void setAbsencePeriodDays(Integer absencePeriodDays) {
	this.absencePeriodDays = absencePeriodDays;
	this.terminationTransaction.setAbsencePeriodDays(absencePeriodDays);
    }

    @Basic
    @Column(name = "ABSENCE_RETURN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getAbsenceReturnDate() {
	return absenceReturnDate;
    }

    public void setAbsenceReturnDate(Date absenceReturnDate) {
	this.absenceReturnDate = absenceReturnDate;
	this.absenceReturnDateString = HijriDateService.getHijriDateString(absenceReturnDate);
	this.terminationTransaction.setAbsenceReturnDate(absenceReturnDate);
    }

    @Transient
    public String getAbsenceReturnDateString() {
	return absenceReturnDateString;
    }

    public void setAbsenceReturnDateString(String absenceReturnDateString) {
	this.absenceReturnDateString = absenceReturnDateString;
	this.absenceReturnDate = HijriDateService.getHijriDate(absenceReturnDateString);
	this.terminationTransaction.setAbsenceReturnDate(absenceReturnDate);
    }

    @Basic
    @Column(name = "DISQUALIFICATION_DRUGS_FLAG")
    public Integer getDisqualificationDrugsFlag() {
	return disqualificationDrugsFlag;
    }

    public void setDisqualificationDrugsFlag(Integer disqualificationDrugsFlag) {
	this.disqualificationDrugsFlag = disqualificationDrugsFlag;
	this.terminationTransaction.setDisqualificationDrugsFlag(disqualificationDrugsFlag);
	if (this.disqualificationDrugsFlag == null || this.disqualificationDrugsFlag == FlagsEnum.OFF.getCode()) {
	    this.disqualificationDrugsFlagBoolean = false;
	} else {
	    this.disqualificationDrugsFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getDisqualificationDrugsFlagBoolean() {
	return disqualificationDrugsFlagBoolean;
    }

    public void setDisqualificationDrugsFlagBoolean(Boolean disqualificationDrugsFlagBoolean) {
	this.disqualificationDrugsFlagBoolean = disqualificationDrugsFlagBoolean;
	if (this.disqualificationDrugsFlagBoolean == false) {
	    setDisqualificationDrugsFlag(FlagsEnum.OFF.getCode());
	} else {
	    setDisqualificationDrugsFlag(FlagsEnum.ON.getCode());
	}
    }

    @Basic
    @Column(name = "DISQUALIFICATION_REASON")
    public String getDisqualificationReason() {
	return disqualificationReason;
    }

    public void setDisqualificationReason(String disqualificationReason) {
	this.disqualificationReason = disqualificationReason;
	this.terminationTransaction.setDisqualificationReason(disqualificationReason);
    }

    @Basic
    @Column(name = "DISQUALIFICATION_DRUGS_TYPE")
    public String getDisqualificationDrugsType() {
	return disqualificationDrugsType;
    }

    public void setDisqualificationDrugsType(String disqualificationDrugsType) {
	this.disqualificationDrugsType = disqualificationDrugsType;
	this.terminationTransaction.setDisqualificationDrugsType(disqualificationDrugsType);
    }

    @Basic
    @Column(name = "MEDICAL_CASE_DESC")
    public String getMedicalCaseDesc() {
	return medicalCaseDesc;
    }

    public void setMedicalCaseDesc(String medicalCaseDesc) {
	this.medicalCaseDesc = medicalCaseDesc;
	this.terminationTransaction.setMedicalCaseDesc(medicalCaseDesc);
    }

    @Basic
    @Column(name = "MEDICAL_WORK_DISABILITY")
    public String getMedicalWorkDisability() {
	return medicalWorkDisability;
    }

    public void setMedicalWorkDisability(String medicalWorkDisability) {
	this.medicalWorkDisability = medicalWorkDisability;
	this.terminationTransaction.setMedicalWorkDisability(medicalWorkDisability);
    }

    @Basic
    @Column(name = "JOB_CANCELATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getJobCancelationDate() {
	return jobCancelationDate;
    }

    public void setJobCancelationDate(Date jobCancelationDate) {
	this.jobCancelationDate = jobCancelationDate;
	this.jobCancelationDateString = HijriDateService.getHijriDateString(jobCancelationDate);
	this.terminationTransaction.setJobCancelationDate(jobCancelationDate);
    }

    @Transient
    public String getJobCancelationDateString() {
	return jobCancelationDateString;
    }

    public void setJobCancelationDateString(String jobCancelationDateString) {
	this.jobCancelationDateString = jobCancelationDateString;
	this.jobCancelationDate = HijriDateService.getHijriDate(jobCancelationDateString);
	this.terminationTransaction.setJobCancelationDate(jobCancelationDate);
    }

    @Basic
    @Column(name = "NATIONALITY_LOSS_TYPE")
    public Integer getNationalityLossType() {
	return nationalityLossType;
    }

    public void setNationalityLossType(Integer nationalityLossType) {
	this.nationalityLossType = nationalityLossType;
	this.terminationTransaction.setNationalityLossType(nationalityLossType);
    }

    @Basic
    @Column(name = "NATIONALITY_LOSS_REASON")
    public String getNationalityLossReason() {
	return nationalityLossReason;
    }

    public void setNationalityLossReason(String nationalityLossReason) {
	this.nationalityLossReason = nationalityLossReason;
	this.terminationTransaction.setNationalityLossReason(nationalityLossReason);
    }

    @Basic
    @Column(name = "CONTRACTOR_BASED_ON_TYPE")
    public Integer getContractorBasedOnType() {
	return contractorBasedOnType;
    }

    public void setContractorBasedOnType(Integer contractorBasedOnType) {
	this.contractorBasedOnType = contractorBasedOnType;
	this.terminationTransaction.setContractorBasedOnType(contractorBasedOnType);
    }

    @Basic
    @Column(name = "CONTRACTOR_TERMINATION_REASON")
    public String getContractorTerminationReason() {
	return contractorTerminationReason;
    }

    public void setContractorTerminationReason(String contractorTerminationReason) {
	this.contractorTerminationReason = contractorTerminationReason;
	this.terminationTransaction.setContractorTerminationReason(contractorTerminationReason);
    }

    @Basic
    @Column(name = "MOVED_TO")
    public String getMovedTo() {
	return movedTo;
    }

    public void setMovedTo(String movedTo) {
	this.movedTo = movedTo;
	this.terminationTransaction.setMovedTo(movedTo);
    }

    @Basic
    @Column(name = "MOVEMENT_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMovementJoiningDate() {
	return movementJoiningDate;
    }

    public void setMovementJoiningDate(Date movementJoiningDate) {
	this.movementJoiningDate = movementJoiningDate;
	this.movementJoiningDateString = HijriDateService.getHijriDateString(movementJoiningDate);
	this.terminationTransaction.setMovementJoiningDate(movementJoiningDate);
    }

    @Transient
    public String getMovementJoiningDateString() {
	return movementJoiningDateString;
    }

    public void setMovementJoiningDateString(String movementJoiningDateString) {
	this.movementJoiningDateString = movementJoiningDateString;
	this.movementJoiningDate = HijriDateService.getHijriDate(movementJoiningDateString);
	this.terminationTransaction.setMovementJoiningDate(movementJoiningDate);
    }

    @Basic
    @Column(name = "MOVEMENT_JOINING_DESC")
    public String getMovementJoiningDesc() {
	return movementJoiningDesc;
    }

    public void setMovementJoiningDesc(String movementJoiningDesc) {
	this.movementJoiningDesc = movementJoiningDesc;
	this.terminationTransaction.setMovementJoiningDesc(movementJoiningDesc);
    }

    @Basic
    @Column(name = "DISCLAIMER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDisclaimerDate() {
	return disclaimerDate;
    }

    public void setDisclaimerDate(Date disclaimerDate) {
	this.disclaimerDate = disclaimerDate;
	this.disclaimerDateString = HijriDateService.getHijriDateString(disclaimerDate);
	this.terminationTransaction.setDisclaimerDate(disclaimerDate);
    }

    @Transient
    public String getDisclaimerDateString() {
	return disclaimerDateString;
    }

    public void setDisclaimerDateString(String disclaimerDateString) {
	this.disclaimerDateString = disclaimerDateString;
	this.disclaimerDate = HijriDateService.getHijriDate(disclaimerDateString);
	this.terminationTransaction.setDisclaimerDate(disclaimerDate);
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	this.terminationTransaction.setJobId(jobId);
    }

    @Basic
    @Column(name = "JOB_CODE")
    public String getJobCode() {
	return jobCode;
    }

    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
	this.terminationTransaction.setJobCode(jobCode);
    }

    @Basic
    @Column(name = "JOB_NAME")
    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
	this.terminationTransaction.setJobName(jobName);
    }

    @Basic
    @Column(name = "JOB_CLASSIFICATION_ID")
    public Long getJobClassificationId() {
	return jobClassificationId;
    }

    public void setJobClassificationId(Long jobClassificationId) {
	this.jobClassificationId = jobClassificationId;
	this.terminationTransaction.setJobClassificationId(jobClassificationId);
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
	this.terminationTransaction.setTransEmpRankDesc(transEmpRankDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CLASS_JOB_CODE")
    public String getTransEmpJobClassJobCode() {
	return transEmpJobClassJobCode;
    }

    public void setTransEmpJobClassJobCode(String transEmpJobClassJobCode) {
	this.transEmpJobClassJobCode = transEmpJobClassJobCode;
	this.terminationTransaction.setTransEmpJobClassJobCode(transEmpJobClassJobCode);
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CLASS_JOB_DESC")
    public String getTransEmpJobClassJobDesc() {
	return transEmpJobClassJobDesc;
    }

    public void setTransEmpJobClassJobDesc(String transEmpJobClassJobDesc) {
	this.transEmpJobClassJobDesc = transEmpJobClassJobDesc;
	this.terminationTransaction.setTransEmpJobClassJobDesc(transEmpJobClassJobDesc);
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_FULL_NAME")
    public String getTransEmpUnitFullName() {
	return transEmpUnitFullName;
    }

    public void setTransEmpUnitFullName(String transEmpUnitFullName) {
	this.transEmpUnitFullName = transEmpUnitFullName;
	this.terminationTransaction.setTransEmpUnitFullName(transEmpUnitFullName);
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_ID")
    public Long getTransEmpUnitId() {
	return transEmpUnitId;
    }

    public void setTransEmpUnitId(Long transEmpUnitId) {
	this.transEmpUnitId = transEmpUnitId;
	terminationTransaction.setTransEmpUnitId(transEmpUnitId);
    }

    @Basic
    @Column(name = "CANCEL_TRANSACTION_ID")
    public Long getCancelTransactionId() {
	return cancelTransactionId;
    }

    public void setCancelTransactionId(Long cancelTransactionId) {
	this.cancelTransactionId = cancelTransactionId;
	this.terminationTransaction.setCancelTransactionId(cancelTransactionId);
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
	this.terminationTransaction.setTransactionTypeId(transactionTypeId);
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_DESC")
    public String getTransactionTypeDesc() {
	return transactionTypeDesc;
    }

    public void setTransactionTypeDesc(String transactionTypeDesc) {
	this.transactionTypeDesc = transactionTypeDesc;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_CODE")
    public Integer getTransactionTypeCode() {
	return transactionTypeCode;
    }

    public void setTransactionTypeCode(Integer transactionTypeCode) {
	this.transactionTypeCode = transactionTypeCode;
    }

    @Basic
    @Column(name = "REASON_ID")
    public Long getReasonId() {
	return reasonId;
    }

    public void setReasonId(Long reasonId) {
	this.reasonId = reasonId;
	this.terminationTransaction.setReasonId(reasonId);
    }

    @Basic
    @Column(name = "REASON_DESC")
    public String getReasonDesc() {
	return reasonDesc;
    }

    public void setReasonDesc(String reasonDesc) {
	this.reasonDesc = reasonDesc;
    }

    @Basic
    @Column(name = "CANCEL_TRANSACTION_REASON")
    public String getCancelTransactionReason() {
	return cancelTransactionReason;
    }

    public void setCancelTransactionReason(String cancelTransactionReason) {
	this.cancelTransactionReason = cancelTransactionReason;
	this.terminationTransaction.setCancelTransactionReason(cancelTransactionReason);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEflag() {
	return eflag;
    }

    public void setEflag(Integer eflag) {
	this.eflag = eflag;
	this.terminationTransaction.setEflag(eflag);
    }

    @Basic
    @Column(name = "MIG_FLAG")
    public Integer getMigFlag() {
	return migFlag;
    }

    public void setMigFlag(Integer migFlag) {
	this.migFlag = migFlag;
	this.terminationTransaction.setMigFlag(migFlag);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.terminationTransaction.setDecisionNumber(decisionNumber);
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
	this.terminationTransaction.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	this.terminationTransaction.setDecisionDate(decisionDate);
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
	this.terminationTransaction.setDecisionRegionId(decisionRegionId);
    }

    @Basic
    @Column(name = "INTERNAL_COPIES")
    public String getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(String internalCopies) {
	this.internalCopies = internalCopies;
	this.terminationTransaction.setInternalCopies(internalCopies);
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
	this.terminationTransaction.setExternalCopies(externalCopies);
    }

    @Basic
    @Column(name = "APPROVED_ID")
    public Long getApprovedId() {
	return approvedId;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
	this.terminationTransaction.setApprovedId(approvedId);
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
	this.terminationTransaction.setDecisionApprovedId(decisionApprovedId);
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
	this.terminationTransaction.setOriginalDecisionApprovedId(originalDecisionApprovedId);
    }

    @Basic
    @Column(name = "INJURY_IN_DUTY_FLAG")
    public Integer getInjuryInDutyFlag() {
	return injuryInDutyFlag;
    }

    public void setInjuryInDutyFlag(Integer injuryInDutyFlag) {
	this.injuryInDutyFlag = injuryInDutyFlag;
	this.terminationTransaction.setInjuryInDutyFlag(injuryInDutyFlag);
    }

    @Basic
    @Column(name = "INJURY_TYPE")
    public Integer getInjuryType() {
	return injuryType;
    }

    public void setInjuryType(Integer injuryType) {
	this.injuryType = injuryType;
	this.terminationTransaction.setInjuryType(injuryType);
    }

    @Transient
    public TerminationTransaction getTerminationTransaction() {
	return terminationTransaction;
    }

    public void setTerminationTransaction(TerminationTransaction terminationTransaction) {
	this.terminationTransaction = terminationTransaction;
    }

}
