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
	@NamedQuery(name = "hcm_terminationRecordDetailData_countTerminationRecordDetailStatus",
		query = " select count(td.id) from TerminationRecordDetailData td" +
			" where (:P_RECORD_ID = -1 or td.recordId = :P_RECORD_ID) " +
			" and (:P_STATUSES_FLAG = -1 or td.status not in :P_STATUSES)"),

	@NamedQuery(name = "hcm_terminationRecordDetailData_getTerminationRecordDetails",
		query = " select td from TerminationRecordDetailData td" +
			" where (:P_RECORD_ID = -1 or td.recordId = :P_RECORD_ID)" +
			" and (:P_STATUS= -1 or td.status =:P_STATUS)" +
			" and (:P_EMP_NAME = '-1' or td.empName like :P_EMP_NAME)" +
			" order by td.empMilitaryNumber, td.empRankId, td.empRecruitmentDate , td.empName")
})
@Entity
@Table(name = "HCM_VW_STE_RECORDS_DETAILS")
public class TerminationRecordDetailData extends BaseEntity {

    private Long id;
    private Long recordId;
    private Long empId;
    private String empName;
    private Date empTerminationDueDate;
    private String empTerminationDueDateString;

    private Long empRankId;
    private String empRankDescription;
    private Integer empMilitaryNumber;
    private String empJobCode;
    private String empJobName;
    private Date empBirthDate;
    private String empBirthDateString;
    private Long empPhysicalUnitId;
    private Long empOfficialUnitId;
    private String empOfficialUnitFullName;
    private String empPhysicalUnitFullName;
    private Date empLastExtensionEndDate;
    private String empLastExtensionEndDateString;
    private Date empPromotionDueDate;
    private String empPromotionDueDateString;
    private Date empLastPromotionDate;
    private String empLastPromotionDateString;
    private Date empRecruitmentDate;
    private String empRecruitmentDateString;

    private Date serviceTerminationDate;
    private String serviceTerminationDateString;

    private Long status;

    private String remarks;
    private String referring;
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

    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;

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
    private Integer injuryInDutyFlag;
    private Integer injuryType;

    private TerminationRecordDetail terminationRecordDetail;

    public TerminationRecordDetailData() {
	terminationRecordDetail = new TerminationRecordDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.terminationRecordDetail.setId(id);
    }

    @Basic
    @Column(name = "RECORD_ID")
    public Long getRecordId() {

	return recordId;
    }

    public void setRecordId(Long recordId) {
	this.recordId = recordId;
	this.terminationRecordDetail.setRecordId(recordId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	this.terminationRecordDetail.setEmpId(empId);
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
    @Column(name = "EMP_TERMINATION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEmpTerminationDueDate() {
	return empTerminationDueDate;
    }

    public void setEmpTerminationDueDate(Date empTerminationDueDate) {
	this.empTerminationDueDate = empTerminationDueDate;
	this.empTerminationDueDateString = HijriDateService.getHijriDateString(empTerminationDueDate);
	this.terminationRecordDetail.setEmpTerminationDueDate(empTerminationDueDate);
    }

    @Transient
    public String getEmpTerminationDueDateString() {
	return empTerminationDueDateString;
    }

    public void setEmpTerminationDueDateString(String empTerminationDueDateString) {
	this.empTerminationDueDateString = empTerminationDueDateString;
	this.empTerminationDueDate = HijriDateService.getHijriDate(empTerminationDueDateString);
	this.terminationRecordDetail.setEmpTerminationDueDate(empTerminationDueDate);
    }

    @Basic
    @Column(name = "EMP_RANK_ID")
    public Long getEmpRankId() {
	return empRankId;
    }

    public void setEmpRankId(Long empRankId) {
	this.empRankId = empRankId;
	this.terminationRecordDetail.setEmpRankId(empRankId);

    }

    @Basic
    @Column(name = "EMP_RANK_DESC")
    public String getEmpRankDescription() {
	return empRankDescription;
    }

    public void setEmpRankDescription(String empRankDescription) {
	this.empRankDescription = empRankDescription;
    }

    @Basic
    @Column(name = "EMP_MILITARY_NUMBER")
    public Integer getEmpMilitaryNumber() {
	return empMilitaryNumber;
    }

    public void setEmpMilitaryNumber(Integer empMilitaryNumber) {
	this.empMilitaryNumber = empMilitaryNumber;
    }

    @Basic
    @Column(name = "EMP_JOB_CODE")
    public String getEmpJobCode() {
	return empJobCode;
    }

    public void setEmpJobCode(String empJobCode) {
	this.empJobCode = empJobCode;
    }

    @Basic
    @Column(name = "EMP_JOB_NAME")
    public String getEmpJobName() {
	return empJobName;
    }

    public void setEmpJobName(String empJobName) {
	this.empJobName = empJobName;
    }

    @Basic
    @Column(name = "EMP_BIRTH_DATE")
    public Date getEmpBirthDate() {
	return empBirthDate;
    }

    public void setEmpBirthDate(Date empBirthDate) {
	this.empBirthDate = empBirthDate;
	this.empBirthDateString = HijriDateService.getHijriDateString(empBirthDate);
    }

    @Transient
    public String getEmpBirthDateString() {
	return empBirthDateString;
    }

    public void setEmpBirthDateString(String empBirthDateString) {
	this.empBirthDateString = empBirthDateString;
	this.empBirthDate = HijriDateService.getHijriDate(empBirthDateString);
    }

    @Basic
    @Column(name = "EMP_PHYSICAL_UNIT_ID")
    public Long getEmpPhysicalUnitId() {
	return empPhysicalUnitId;
    }

    public void setEmpPhysicalUnitId(Long empPhysicalUnitId) {
	this.empPhysicalUnitId = empPhysicalUnitId;
    }

    @Basic
    @Column(name = "EMP_OFFICIAL_UNIT_ID")
    public Long getEmpOfficialUnitId() {
	return empOfficialUnitId;
    }

    public void setEmpOfficialUnitId(Long empOfficialUnitId) {
	this.empOfficialUnitId = empOfficialUnitId;
    }

    @Basic
    @Column(name = "EMP_OFFICIAL_UNIT_FULL_NAME")
    public String getEmpOfficialUnitFullName() {
	return empOfficialUnitFullName;
    }

    public void setEmpOfficialUnitFullName(String empOfficialUnitFullName) {
	this.empOfficialUnitFullName = empOfficialUnitFullName;
    }

    @Basic
    @Column(name = "EMP_PHYSICAL_UNIT_FULL_NAME")
    public String getEmpPhysicalUnitFullName() {
	return empPhysicalUnitFullName;
    }

    public void setEmpPhysicalUnitFullName(String empPhysicalUnitFullName) {
	this.empPhysicalUnitFullName = empPhysicalUnitFullName;
    }

    @Basic
    @Column(name = "EMP_LAST_EXTENSION_END_DATE")
    public Date getEmpLastExtensionEndDate() {
	return empLastExtensionEndDate;
    }

    public void setEmpLastExtensionEndDate(Date empLastExtensionEndDate) {
	this.empLastExtensionEndDate = empLastExtensionEndDate;
	this.empLastExtensionEndDateString = HijriDateService.getHijriDateString(empLastExtensionEndDate);
    }

    @Transient
    public String getEmpLastExtensionEndDateString() {
	return empLastExtensionEndDateString;
    }

    public void setEmpLastExtensionEndDateString(String empLastExtensionEndDateString) {
	this.empLastExtensionEndDateString = empLastExtensionEndDateString;
	this.empLastExtensionEndDate = HijriDateService.getHijriDate(empLastExtensionEndDateString);
    }

    @Basic
    @Column(name = "EMP_PROMOTION_DUE_DATE")
    public Date getEmpPromotionDueDate() {
	return empPromotionDueDate;
    }

    public void setEmpPromotionDueDate(Date empPromotionDueDate) {
	this.empPromotionDueDate = empPromotionDueDate;
	this.empPromotionDueDateString = HijriDateService.getHijriDateString(empPromotionDueDate);
    }

    @Transient
    public String getEmpPromotionDueDateString() {
	return empPromotionDueDateString;
    }

    public void setEmpPromotionDueDateString(String empPromotionDueDateString) {
	this.empPromotionDueDateString = empPromotionDueDateString;
	this.empPromotionDueDate = HijriDateService.getHijriDate(empPromotionDueDateString);
    }

    @Basic
    @Column(name = "EMP_LAST_PROMOTION_DATE")
    public Date getEmpLastPromotionDate() {
	return empLastPromotionDate;
    }

    public void setEmpLastPromotionDate(Date empLastPromotionDate) {
	this.empLastPromotionDate = empLastPromotionDate;
	this.empLastPromotionDateString = HijriDateService.getHijriDateString(empLastPromotionDate);
    }

    @Transient
    public String getEmpLastPromotionDateString() {
	return empLastPromotionDateString;
    }

    public void setEmpLastPromotionDateString(String empLastPromotionDateString) {
	this.empLastPromotionDateString = empLastPromotionDateString;
	this.empLastPromotionDate = HijriDateService.getHijriDate(empLastPromotionDateString);
    }

    @Basic
    @Column(name = "EMP_RECRUITMENT_DATE")
    public Date getEmpRecruitmentDate() {
	return empRecruitmentDate;
    }

    public void setEmpRecruitmentDate(Date empRecruitmentDate) {
	this.empRecruitmentDate = empRecruitmentDate;
	this.empRecruitmentDateString = HijriDateService.getHijriDateString(empRecruitmentDate);
    }

    @Transient
    public String getEmpRecruitmentDateString() {
	return empRecruitmentDateString;
    }

    public void setEmpRecruitmentDateString(String empRecruitmentDateString) {
	this.empRecruitmentDateString = empRecruitmentDateString;
	this.empRecruitmentDate = HijriDateService.getHijriDate(empRecruitmentDateString);
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
	this.terminationRecordDetail.setServiceTerminationDate(serviceTerminationDate);
    }

    @Transient
    public String getServiceTerminationDateString() {
	return serviceTerminationDateString;
    }

    public void setServiceTerminationDateString(String serviceTerminationDateString) {
	this.serviceTerminationDateString = serviceTerminationDateString;
	this.serviceTerminationDate = HijriDateService.getHijriDate(serviceTerminationDateString);
	this.terminationRecordDetail.setServiceTerminationDate(serviceTerminationDate);
    }

    @Basic
    @Column(name = "STATUS")
    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
	this.terminationRecordDetail.setStatus(status);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	this.terminationRecordDetail.setRemarks(remarks);
    }

    @Basic
    @Column(name = "REFERRING")
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
	this.terminationRecordDetail.setReferring(referring);
    }

    @Basic
    @Column(name = "TERMINATION_REQUEST_REASON")
    public String getTerminationRequestReason() {
	return terminationRequestReason;
    }

    public void setTerminationRequestReason(String terminationRequestReason) {
	this.terminationRequestReason = terminationRequestReason;
	this.terminationRecordDetail.setTerminationRequestReason(terminationRequestReason);
    }

    @Basic
    @Column(name = "TERMINATION_REQUEST_TYPE")
    public Long getTerminationRequestType() {
	return terminationRequestType;
    }

    public void setTerminationRequestType(Long terminationRequestType) {
	this.terminationRequestType = terminationRequestType;
	this.terminationRecordDetail.setTerminationRequestType(terminationRequestType);
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
	this.terminationRecordDetail.setDesiredTerminationDate(desiredTerminationDate);
    }

    @Transient
    public String getDesiredTerminationDateString() {
	return desiredTerminationDateString;
    }

    public void setDesiredTerminationDateString(String desiredTerminationDateString) {
	this.desiredTerminationDateString = desiredTerminationDateString;
	this.desiredTerminationDate = HijriDateService.getHijriDate(desiredTerminationDateString);
	this.terminationRecordDetail.setDesiredTerminationDate(desiredTerminationDate);
    }

    @Basic
    @Column(name = "MINISTRY_REQUEST_NUMBER")
    public String getMinistryRequestNumber() {
	return ministryRequestNumber;
    }

    public void setMinistryRequestNumber(String ministryRequestNumber) {
	this.ministryRequestNumber = ministryRequestNumber;
	this.terminationRecordDetail.setMinistryRequestNumber(ministryRequestNumber);
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
	this.terminationRecordDetail.setMinistryRequestDate(ministryRequestDate);
    }

    @Transient
    public String getMinistryRequestDateString() {
	return ministryRequestDateString;
    }

    public void setMinistryRequestDateString(String ministryRequestDateString) {
	this.ministryRequestDateString = ministryRequestDateString;
	this.ministryRequestDate = HijriDateService.getHijriDate(ministryRequestDateString);
	this.terminationRecordDetail.setMinistryRequestDate(ministryRequestDate);
    }

    @Basic
    @Column(name = "MINISTRY_REJECTION_NUMBER")
    public String getMinistryRejectionNumber() {
	return ministryRejectionNumber;
    }

    public void setMinistryRejectionNumber(String ministryRejectionNumber) {
	this.ministryRejectionNumber = ministryRejectionNumber;
	this.terminationRecordDetail.setMinistryRejectionNumber(ministryRejectionNumber);
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
	this.terminationRecordDetail.setMinistryRejectionDate(ministryRejectionDate);
    }

    @Transient
    public String getMinistryRejectionDateString() {
	return ministryRejectionDateString;
    }

    public void setMinistryRejectionDateString(String ministryRejectionDateString) {
	this.ministryRejectionDateString = ministryRejectionDateString;
	this.ministryRejectionDate = HijriDateService.getHijriDate(ministryRejectionDateString);
	this.terminationRecordDetail.setMinistryRejectionDate(ministryRejectionDate);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.terminationRecordDetail.setDecisionNumber(decisionNumber);
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
	this.terminationRecordDetail.setDecisionDate(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
	this.terminationRecordDetail.setDecisionDate(decisionDate);
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
	this.terminationRecordDetail.setExtensionStartDate(extensionStartDate);
    }

    @Transient
    public String getExtensionStartDateString() {
	return extensionStartDateString;
    }

    public void setExtensionStartDateString(String extensionStartDateString) {
	this.extensionStartDateString = extensionStartDateString;
	this.extensionStartDate = HijriDateService.getHijriDate(extensionStartDateString);
	this.terminationRecordDetail.setExtensionStartDate(extensionStartDate);
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
	this.terminationRecordDetail.setExtensionEndDate(extensionEndDate);
    }

    @Transient
    public String getExtensionEndDateString() {
	return extensionEndDateString;
    }

    public void setExtensionEndDateString(String extensionEndDateString) {
	this.extensionEndDateString = extensionEndDateString;
	this.extensionEndDate = HijriDateService.getHijriDate(extensionEndDateString);
	this.terminationRecordDetail.setExtensionEndDate(extensionEndDate);
    }

    @Basic
    @Column(name = "EXTENSION_PERIOD_DAYS")
    public Integer getExtensionPeriodDays() {
	return extensionPeriodDays;
    }

    public void setExtensionPeriodDays(Integer extensionPeriodDays) {
	this.extensionPeriodDays = extensionPeriodDays;
	this.terminationRecordDetail.setExtensionPeriodDays(extensionPeriodDays);
    }

    @Basic
    @Column(name = "EXTENSION_PERIOD_MONTHS")
    public Integer getExtensionPeriodMonths() {
	return extensionPeriodMonths;
    }

    public void setExtensionPeriodMonths(Integer extensionPeriodMonths) {
	this.extensionPeriodMonths = extensionPeriodMonths;
	this.terminationRecordDetail.setExtensionPeriodMonths(extensionPeriodMonths);
    }

    @Basic
    @Column(name = "EXTENSION_FLAG")
    public Integer getExtensionFlag() {
	return extensionFlag;
    }

    public void setExtensionFlag(Integer extensionFlag) {
	this.extensionFlag = extensionFlag;
	this.terminationRecordDetail.setExtensionFlag(extensionFlag);
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
	this.terminationRecordDetail.setJudgmentFlag(judgmentFlag);
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
	this.terminationRecordDetail.setSpecializationPeriodFlag(specializationPeriodFlag);
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
	this.terminationRecordDetail.setForeignerMarriageFlag(foreignerMarriageFlag);
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
	this.terminationRecordDetail.setDismissType(dismissType);
    }

    @Basic
    @Column(name = "DISMISS_DECISION_NUMBER")
    public String getDismissDecisionNumber() {
	return dismissDecisionNumber;
    }

    public void setDismissDecisionNumber(String dismissDecisionNumber) {
	this.dismissDecisionNumber = dismissDecisionNumber;
	this.terminationRecordDetail.setDismissDecisionNumber(dismissDecisionNumber);
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
	this.terminationRecordDetail.setDismissDecisionDate(dismissDecisionDate);
    }

    @Transient
    public String getDismissDecisionDateString() {
	return dismissDecisionDateString;
    }

    public void setDismissDecisionDateString(String dismissDecisionDateString) {
	this.dismissDecisionDateString = dismissDecisionDateString;
	this.dismissDecisionDate = HijriDateService.getHijriDate(dismissDecisionDateString);
	this.terminationRecordDetail.setDismissDecisionDate(dismissDecisionDate);
    }

    @Basic
    @Column(name = "DISMISS_STATUS")
    public Integer getDismissStatus() {
	return dismissStatus;
    }

    public void setDismissStatus(Integer dismissStatus) {
	this.dismissStatus = dismissStatus;
	this.terminationRecordDetail.setDismissStatus(dismissStatus);
    }

    @Basic
    @Column(name = "DISMISS_RESUMPTION_NUMBER")
    public String getDismissResumptionNumber() {
	return dismissResumptionNumber;
    }

    public void setDismissResumptionNumber(String dismissResumptionNumber) {
	this.dismissResumptionNumber = dismissResumptionNumber;
	this.terminationRecordDetail.setDismissResumptionNumber(dismissResumptionNumber);
    }

    @Basic
    @Column(name = "DISMISS_RESUMPTION_YEAR")
    public Integer getDismissResumptionYear() {
	return dismissResumptionYear;
    }

    public void setDismissResumptionYear(Integer dismissResumptionYear) {
	this.dismissResumptionYear = dismissResumptionYear;
	this.terminationRecordDetail.setDismissResumptionYear(dismissResumptionYear);
    }

    @Basic
    @Column(name = "DISMISS_APPROVAL_NUMBER")
    public String getDismissApprovalNumber() {
	return dismissApprovalNumber;
    }

    public void setDismissApprovalNumber(String dismissApprovalNumber) {
	this.dismissApprovalNumber = dismissApprovalNumber;
	this.terminationRecordDetail.setDismissApprovalNumber(dismissApprovalNumber);
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
	this.terminationRecordDetail.setDismissApprovalDate(dismissApprovalDate);
    }

    @Transient
    public String getDismissApprovalDateString() {
	return dismissApprovalDateString;
    }

    public void setDismissApprovalDateString(String dismissApprovalDateString) {
	this.dismissApprovalDateString = dismissApprovalDateString;
	this.dismissApprovalDate = HijriDateService.getHijriDate(dismissApprovalDateString);
	this.terminationRecordDetail.setDismissApprovalDate(dismissApprovalDate);
    }

    @Basic
    @Column(name = "DISMISS_BASED_ON")
    public Integer getDismissBasedOn() {
	return dismissBasedOn;
    }

    public void setDismissBasedOn(Integer dismissBasedOn) {
	this.dismissBasedOn = dismissBasedOn;
	this.terminationRecordDetail.setDismissBasedOn(dismissBasedOn);
    }

    @Basic
    @Column(name = "DISMISS_BASED_ON_DESC")
    public String getDismissBasedOnDesc() {
	return dismissBasedOnDesc;
    }

    public void setDismissBasedOnDesc(String dismissBasedOnDesc) {
	this.dismissBasedOnDesc = dismissBasedOnDesc;
	this.terminationRecordDetail.setDismissBasedOnDesc(dismissBasedOnDesc);
    }

    @Basic
    @Column(name = "DISMISS_JUDGMENT")
    public String getDismissJudgment() {
	return dismissJudgment;
    }

    public void setDismissJudgment(String dismissJudgment) {
	this.dismissJudgment = dismissJudgment;
	this.terminationRecordDetail.setDismissJudgment(dismissJudgment);
    }

    @Basic
    @Column(name = "DEATH_CERT_NUMBER")
    public String getDeathCertNumber() {
	return deathCertNumber;
    }

    public void setDeathCertNumber(String deathCertNumber) {
	this.deathCertNumber = deathCertNumber;
	this.terminationRecordDetail.setDeathCertNumber(deathCertNumber);
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
	this.terminationRecordDetail.setDeathCertDate(deathCertDate);
    }

    @Transient
    public String getDeathCertDateString() {
	return deathCertDateString;
    }

    public void setDeathCertDateString(String deathCertDateString) {
	this.deathCertDateString = deathCertDateString;
	this.deathCertDate = HijriDateService.getHijriDate(deathCertDateString);
	this.terminationRecordDetail.setDeathCertDate(deathCertDate);
    }

    @Basic
    @Column(name = "DEATH_CERT_ISSUE_PLACE")
    public String getDeathCertIssuePlace() {
	return deathCertIssuePlace;
    }

    public void setDeathCertIssuePlace(String deathCertIssuePlace) {
	this.deathCertIssuePlace = deathCertIssuePlace;
	this.terminationRecordDetail.setDeathCertIssuePlace(deathCertIssuePlace);
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
	this.terminationRecordDetail.setDeathDate(deathDate);
    }

    @Transient
    public String getDeathDateString() {
	return deathDateString;
    }

    public void setDeathDateString(String deathDateString) {
	this.deathDateString = deathDateString;
	this.deathDate = HijriDateService.getHijriDate(deathDateString);
	this.terminationRecordDetail.setDeathDate(deathDate);
    }

    @Basic
    @Column(name = "DEATH_IN_DUTY_FLAG")
    public Integer getDeathInDutyFlag() {
	return deathInDutyFlag;
    }

    public void setDeathInDutyFlag(Integer deathInDutyFlag) {
	this.deathInDutyFlag = deathInDutyFlag;
	this.terminationRecordDetail.setDeathInDutyFlag(deathInDutyFlag);
    }

    @Basic
    @Column(name = "DEATH_TYPE")
    public Integer getDeathType() {
	return deathType;
    }

    public void setDeathType(Integer deathType) {
	this.deathType = deathType;
	this.terminationRecordDetail.setDeathType(deathType);
    }

    @Basic
    @Column(name = "DEATH_IN_OPERATIONS")
    public String getDeathInOperations() {
	return deathInOperations;
    }

    public void setDeathInOperations(String deathInOperations) {
	this.deathInOperations = deathInOperations;
	this.terminationRecordDetail.setDeathInOperations(deathInOperations);
    }

    @Basic
    @Column(name = "ABSENCE_TYPE")
    public Integer getAbsenceType() {
	return absenceType;
    }

    public void setAbsenceType(Integer absenceType) {
	this.absenceType = absenceType;
	this.terminationRecordDetail.setAbsenceType(absenceType);
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
	this.terminationRecordDetail.setAbsenceStartDate(absenceStartDate);
    }

    @Transient
    public String getAbsenceStartDateString() {
	return absenceStartDateString;
    }

    public void setAbsenceStartDateString(String absenceStartDateString) {
	this.absenceStartDateString = absenceStartDateString;
	this.absenceStartDate = HijriDateService.getHijriDate(absenceStartDateString);
	this.terminationRecordDetail.setAbsenceStartDate(absenceStartDate);
    }

    @Basic
    @Column(name = "ABSENCE_PERIOD_DAYS")
    public Integer getAbsencePeriodDays() {
	return absencePeriodDays;
    }

    public void setAbsencePeriodDays(Integer absencePeriodDays) {
	this.absencePeriodDays = absencePeriodDays;
	this.terminationRecordDetail.setAbsencePeriodDays(absencePeriodDays);
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
	this.terminationRecordDetail.setAbsenceReturnDate(absenceReturnDate);
    }

    @Transient
    public String getAbsenceReturnDateString() {
	return absenceReturnDateString;
    }

    public void setAbsenceReturnDateString(String absenceReturnDateString) {
	this.absenceReturnDateString = absenceReturnDateString;
	this.absenceReturnDate = HijriDateService.getHijriDate(absenceReturnDateString);
	this.terminationRecordDetail.setAbsenceReturnDate(absenceReturnDate);
    }

    @Basic
    @Column(name = "DISQUALIFICATION_DRUGS_FLAG")
    public Integer getDisqualificationDrugsFlag() {
	return disqualificationDrugsFlag;
    }

    public void setDisqualificationDrugsFlag(Integer disqualificationDrugsFlag) {
	this.disqualificationDrugsFlag = disqualificationDrugsFlag;
	this.terminationRecordDetail.setDisqualificationDrugsFlag(disqualificationDrugsFlag);
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
	this.terminationRecordDetail.setDisqualificationReason(disqualificationReason);
    }

    @Basic
    @Column(name = "DISQUALIFICATION_DRUGS_TYPE")
    public String getDisqualificationDrugsType() {
	return disqualificationDrugsType;
    }

    public void setDisqualificationDrugsType(String disqualificationDrugsType) {
	this.disqualificationDrugsType = disqualificationDrugsType;
	this.terminationRecordDetail.setDisqualificationDrugsType(disqualificationDrugsType);
    }

    @Basic
    @Column(name = "MEDICAL_CASE_DESC")
    public String getMedicalCaseDesc() {
	return medicalCaseDesc;
    }

    public void setMedicalCaseDesc(String medicalCaseDesc) {
	this.medicalCaseDesc = medicalCaseDesc;
	this.terminationRecordDetail.setMedicalCaseDesc(medicalCaseDesc);
    }

    @Basic
    @Column(name = "MEDICAL_WORK_DISABILITY")
    public String getMedicalWorkDisability() {
	return medicalWorkDisability;
    }

    public void setMedicalWorkDisability(String medicalWorkDisability) {
	this.medicalWorkDisability = medicalWorkDisability;
	this.terminationRecordDetail.setMedicalWorkDisability(medicalWorkDisability);
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
	this.terminationRecordDetail.setJobCancelationDate(jobCancelationDate);
    }

    @Transient
    public String getJobCancelationDateString() {
	return jobCancelationDateString;
    }

    public void setJobCancelationDateString(String jobCancelationDateString) {
	this.jobCancelationDateString = jobCancelationDateString;
	this.jobCancelationDate = HijriDateService.getHijriDate(jobCancelationDateString);
	this.terminationRecordDetail.setJobCancelationDate(jobCancelationDate);
    }

    @Basic
    @Column(name = "NATIONALITY_LOSS_TYPE")
    public Integer getNationalityLossType() {
	return nationalityLossType;
    }

    public void setNationalityLossType(Integer nationalityLossType) {
	this.nationalityLossType = nationalityLossType;
	this.terminationRecordDetail.setNationalityLossType(nationalityLossType);
    }

    @Basic
    @Column(name = "NATIONALITY_LOSS_REASON")
    public String getNationalityLossReason() {
	return nationalityLossReason;
    }

    public void setNationalityLossReason(String nationalityLossReason) {
	this.nationalityLossReason = nationalityLossReason;
	this.terminationRecordDetail.setNationalityLossReason(nationalityLossReason);
    }

    @Basic
    @Column(name = "CONTRACTOR_BASED_ON_TYPE")
    public Integer getContractorBasedOnType() {
	return contractorBasedOnType;
    }

    public void setContractorBasedOnType(Integer contractorBasedOnType) {
	this.contractorBasedOnType = contractorBasedOnType;
	this.terminationRecordDetail.setContractorBasedOnType(contractorBasedOnType);
    }

    @Basic
    @Column(name = "CONTRACTOR_TERMINATION_REASON")
    public String getContractorTerminationReason() {
	return contractorTerminationReason;
    }

    public void setContractorTerminationReason(String contractorTerminationReason) {
	this.contractorTerminationReason = contractorTerminationReason;
	this.terminationRecordDetail.setContractorTerminationReason(contractorTerminationReason);
    }

    @Basic
    @Column(name = "INJURY_IN_DUTY_FLAG")
    public Integer getInjuryInDutyFlag() {
	return injuryInDutyFlag;
    }

    public void setInjuryInDutyFlag(Integer injuryInDutyFlag) {
	this.injuryInDutyFlag = injuryInDutyFlag;
	this.terminationRecordDetail.setInjuryInDutyFlag(injuryInDutyFlag);
    }

    @Basic
    @Column(name = "INJURY_TYPE")
    public Integer getInjuryType() {
	return injuryType;
    }

    public void setInjuryType(Integer injuryType) {
	this.injuryType = injuryType;
	this.terminationRecordDetail.setInjuryType(injuryType);
    }

    @Transient
    public TerminationRecordDetail getTerminationRecordDetail() {
	return terminationRecordDetail;
    }

    public void setTerminationRecordDetail(TerminationRecordDetail terminationRecordDetail) {
	this.terminationRecordDetail = terminationRecordDetail;
    }
}
