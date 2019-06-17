package com.code.dal.orm.hcm.vacations;

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
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>Vacation</code> class represents the vacation data for all vacation types for all employees.
 * 
 */
@NamedQueries({

	@NamedQuery(name = "hcm_vacation_getVacationById",
		query = " select v from Vacation v " +
			" where v.vacationId = :P_VACATION_ID "),

	@NamedQuery(name = "hcm_vacation_searchVacations",
		query = " select v from Vacation v " +
			" where v.empId = :P_EMP_ID " +
			"   and (:P_STATUS = -1 or v.status = :P_STATUS) " +
			"   and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			"   and (:P_DEC_NO = 0 OR v.decisionNumber = :P_DEC_NO) " +
			"   and v.etrFlag = 1 " +
			" order by v.endDate desc "),

	@NamedQuery(name = "hcm_vacation_checkDatesConflict",
		query = " select count(v.vacationId) from Vacation v " +
			" where v.empId = :P_EMP_ID " +
			"   and v.status <> 4 " +
			"   and v.vacationId <> :P_EXCLUDED_VACATION_ID " +
			"   and (   (to_date(:P_START_DATE, 'MI/MM/YYYY') between v.startDate and v.endDate) " +
			"        OR (to_date(:P_END_DATE, 'MI/MM/YYYY') between v.startDate and v.endDate) " +
			"        OR (to_date(:P_START_DATE, 'MI/MM/YYYY') <= v.startDate and to_date(:P_END_DATE, 'MI/MM/YYYY') >= v.endDate) " +
			"   ) "),

	@NamedQuery(name = "hcm_vacation_sumVacDays",
		query = " select nvl(sum(v.period),0) from Vacation v " +
			" where v.empId = :P_EMP_ID " +
			"   and v.status <> 4 " +
			"   and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			"   and (:P_SUB_VACATION_TYPE = -1 or v.subVacationType = :P_SUB_VACATION_TYPE) " +
			"   and (:P_LOCATION_FLAG = -1 or v.locationFlag = :P_LOCATION_FLAG) " +
			"   and (:P_ALLOWED_THRESHOLD = -1 OR v.period < :P_ALLOWED_THRESHOLD) " +
			"   and (   (v.startDate between to_date(:P_FROM_DATE, 'MI/MM/YYYY') and to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"        OR (v.endDate between to_date(:P_FROM_DATE, 'MI/MM/YYYY') and to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"        OR (v.startDate < to_date(:P_FROM_DATE, 'MI/MM/YYYY') and v.endDate > to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"   ) "),

	@NamedQuery(name = "hcm_vacation_sumVacDaysWithinContractualYear",
		query = " select nvl(sum(v.period),0) from Vacation v " +
			" where v.empId = :P_EMP_ID " +
			"   and v.status <> 4 " +
			"   and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			"   and v.contractualYearStartDate = to_date(:CONTRACTUAL_YEAR_START_DATE, 'MI/MM/YYYY') " +
			"   and v.contractualYearEndDate = to_date(:CONTRACTUAL_YEAR_END_DATE, 'MI/MM/YYYY') "),

	@NamedQuery(name = "hcm_vacation_includeDate",
		query = " select v from Vacation v" +
			" where to_date(:P_DATE_BETWEEN, 'MI/MM/YYYY') between v.startDate and v.endDate" +
			" and v.vacationTypeId = :P_VACATION_TYPE_ID" +
			" and (:P_SUB_VACATION_TYPE = -1 or v.subVacationType = :P_SUB_VACATION_TYPE) " +
			" and (:P_LOCATION_FLAG = -1 or v.locationFlag = :P_LOCATION_FLAG) " +
			" and (:P_ALLOWED_THRESHOLD = -1 OR v.period < :P_ALLOWED_THRESHOLD) " +
			" and v.empId = :P_EMP_ID" +
			" and v.status <> 4 "),

	@NamedQuery(name = "hcm_vacation_getVacationByBeneficiaryAndStartDate",
		query = " select v from Vacation v " +
			" where v.empId = :P_EMP_ID " +
			" and v.startDate = to_date(:P_START_DATE, 'MI/MM/YYYY') " +
			" and (:P_VACATION_TYPE_ID = -1 or v.vacationTypeId = :P_VACATION_TYPE_ID) " +
			" and v.status in (:P_STATUS) "),

	@NamedQuery(name = "hcm_vacation_getFirstVacation",
		query = " select v from Vacation v " +
			" where v.empId = :P_EMP_ID " +
			" and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			" and (:P_SUB_VACATION_TYPE = -1 or v.subVacationType = :P_SUB_VACATION_TYPE) " +
			" and v.status <> 4 " +
			" and v.startDate = (select min(vac.startDate) from Vacation vac where vac.empId = :P_EMP_ID and vac.vacationTypeId = :P_VACATION_TYPE_ID and (:P_SUB_VACATION_TYPE = -1 or vac.subVacationType = :P_SUB_VACATION_TYPE) and vac.status <> 4 ) "),

	@NamedQuery(name = "hcm_vacation_sumRelatedDeductedBalance",
		query = " select nvl(sum(v.relatedDeductedBalance),0) from Vacation v " +
			" where v.empId = :P_EMP_ID " +
			"   and v.status <> 4 " +
			"   and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			"   and (   (v.startDate between to_date(:P_FROM_DATE, 'MI/MM/YYYY') and to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"        OR (v.endDate between to_date(:P_FROM_DATE, 'MI/MM/YYYY') and to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"        OR (v.startDate < to_date(:P_FROM_DATE, 'MI/MM/YYYY') and v.endDate > to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			"   ) "),

	@NamedQuery(name = "hcm_vacation_countLaterVacations",
		query = " select count(v.vacationId) from Vacation v " +
			" where v.empId = :P_EMP_ID " +
			" and v.vacationTypeId in ( :P_VACATION_TYPES_IDS ) " +
			" and (:P_SUB_VACATION_TYPE = -1 or v.subVacationType = :P_SUB_VACATION_TYPE) " +
			" and v.status <> 4 " +
			" and v.startDate > to_date(:P_START_DATE, 'MI/MM/YYYY')"),

	@NamedQuery(name = "hcm_vacation_countLaterVacationsHasJoiningDate",
		query = " select count(v.vacationId) from Vacation v " +
			" where v.joiningDate is not null " +
			" and v.empId = :P_EMP_ID " +
			" and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			" and (:P_SUB_VACATION_TYPE = -1 or v.subVacationType = :P_SUB_VACATION_TYPE) " +
			" and v.status <> 4 " +
			" and v.startDate > to_date(:P_END_DATE, 'MI/MM/YYYY') ")

})
@Entity
@Table(name = "HCM_VAC_TRANSACTIONS")
public class Vacation extends AuditEntity implements UpdatableAuditEntity {
    private Long vacationId;
    private Long empId;
    private Long vacationTypeId;
    private Integer subVacationType;
    private Integer paidVacationType;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Integer period;
    private Integer locationFlag;
    private String location;
    private Date extStartDate;
    private String extStartDateString;
    private Date extEndDate;
    private String extEndDateString;
    private Integer extPeriod;
    private String extLocation;
    private String contactAddress;
    private String contactNumber;
    private Integer status;
    private Integer etrFlag;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long approvedId;
    private Long decisionApprovedId;
    private Long originalDecisionApprovedId;
    private String transactionEmpRankDesc;
    private String transactionEmpJobCode;
    private String transactionEmpJobDesc;
    private String transactionEmpUnitDesc;
    private Long transactionEmpCategoryId;
    private String transactionApprovedRankDesc;
    private String transactionApprovedJobDesc;
    private Integer migrationFlag;
    private String decisionData;
    private Date joiningDate;
    private String attachments;
    private Long decisionRegionId;
    private String externalCopies;
    private Date contractualYearStartDate;
    private String contractualYearStartDateString;
    private Date contractualYearEndDate;
    private String contractualYearEndDateString;
    private String referring;
    private Integer relatedDeductedBalance;
    private Integer exceededDays;
    private Date frameStartDate;
    private Date frameEndDate;

    public void setVacationId(Long vacationId) {
	this.vacationId = vacationId;
    }

    @SequenceGenerator(name = "HCMVacationsSeq",
	    sequenceName = "HCM_VACATIONS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMVacationsSeq")
    @Column(name = "ID")
    public Long getVacationId() {
	return vacationId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "EMP_ID")
    @XmlTransient
    public Long getEmpId() {
	return empId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    @Basic
    @Column(name = "VACATION_TYPE_ID")
    @XmlTransient
    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    @Basic
    @Column(name = "SUB_VACATION_TYPE")
    @XmlTransient
    public Integer getSubVacationType() {
	return subVacationType;
    }

    public void setSubVacationType(Integer subVacationType) {
	this.subVacationType = subVacationType;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
	this.startDateString = HijriDateService.getHijriDateString(startDate);
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	this.startDate = HijriDateService.getHijriDate(startDateString);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    @XmlTransient
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    @Basic
    @Column(name = "LOCATION")
    @XmlTransient
    public String getLocation() {
	return location;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    @XmlTransient
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Transient
    @XmlTransient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setApprovedId(Long approvedId) {
	this.approvedId = approvedId;
    }

    @Basic
    @Column(name = "APPROVED_ID")
    @XmlTransient
    public Long getApprovedId() {
	return approvedId;
    }

    public void setDecisionApprovedId(Long decisionApprovedId) {
	this.decisionApprovedId = decisionApprovedId;
    }

    @Basic
    @Column(name = "DECISION_APPROVED_ID")
    @XmlTransient
    public Long getDecisionApprovedId() {
	return decisionApprovedId;
    }

    @Basic
    @Column(name = "ORIGINAL_DECISION_APPROVED_ID")
    @XmlTransient
    public Long getOriginalDecisionApprovedId() {
	return originalDecisionApprovedId;
    }

    public void setOriginalDecisionApprovedId(Long originalDecisionApprovedId) {
	this.originalDecisionApprovedId = originalDecisionApprovedId;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setEtrFlag(Integer etrFlag) {
	this.etrFlag = etrFlag;
    }

    @Basic
    @Column(name = "EFLAG")
    @XmlTransient
    public Integer getEtrFlag() {
	return etrFlag;
    }

    public void setContactNumber(String contactNumber) {
	this.contactNumber = contactNumber;
    }

    @Basic
    @Column(name = "CONTACT_NUMBER")
    @XmlTransient
    public String getContactNumber() {
	return contactNumber;
    }

    public void setContactAddress(String contactAddress) {
	this.contactAddress = contactAddress;
    }

    @Basic
    @Column(name = "CONTACT_ADDRESS")
    @XmlTransient
    public String getContactAddress() {
	return contactAddress;
    }

    public void setMigrationFlag(Integer migrationFlag) {
	this.migrationFlag = migrationFlag;
    }

    @Basic
    @Column(name = "MIG_FLAG")
    @XmlTransient
    public Integer getMigrationFlag() {
	return migrationFlag;
    }

    @Basic
    @Column(name = "PAID_VACATION_TYPE")
    @XmlTransient
    public Integer getPaidVacationType() {
	return paidVacationType;
    }

    public void setPaidVacationType(Integer paidVacationType) {
	this.paidVacationType = paidVacationType;
    }

    @Basic
    @Column(name = "EXT_START_DATE")
    @XmlTransient
    public Date getExtStartDate() {
	return extStartDate;
    }

    public void setExtStartDate(Date extStartDate) {
	this.extStartDate = extStartDate;
	this.extStartDateString = HijriDateService.getHijriDateString(extStartDate);
    }

    @Transient
    @XmlTransient
    public String getExtStartDateString() {
	return extStartDateString;
    }

    public void setExtStartDateString(String extStartDateString) {
	this.extStartDateString = extStartDateString;
	this.extStartDate = HijriDateService.getHijriDate(extStartDateString);
    }

    @Basic
    @Column(name = "EXT_END_DATE")
    @XmlTransient
    public Date getExtEndDate() {
	return extEndDate;
    }

    public void setExtEndDate(Date extEndDate) {
	this.extEndDate = extEndDate;
	this.extEndDateString = HijriDateService.getHijriDateString(extEndDate);

    }

    @Transient
    @XmlTransient
    public String getExtEndDateString() {
	return extEndDateString;
    }

    public void setExtEndDateString(String extEndDateString) {
	this.extEndDateString = extEndDateString;
	this.extEndDate = HijriDateService.getHijriDate(extEndDateString);
    }

    @Basic
    @Column(name = "EXT_PERIOD")
    @XmlTransient
    public Integer getExtPeriod() {
	return extPeriod;
    }

    public void setExtPeriod(Integer extPeriod) {
	this.extPeriod = extPeriod;
    }

    @Basic
    @Column(name = "EXT_LOCATION")
    @XmlTransient
    public String getExtLocation() {
	return extLocation;
    }

    public void setExtLocation(String extLocation) {
	this.extLocation = extLocation;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    @XmlTransient
    public String getTransactionEmpRankDesc() {
	return transactionEmpRankDesc;
    }

    public void setTransactionEmpRankDesc(String transactionEmpRankDesc) {
	this.transactionEmpRankDesc = transactionEmpRankDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_CODE")
    @XmlTransient
    public String getTransactionEmpJobCode() {
	return transactionEmpJobCode;
    }

    public void setTransactionEmpJobCode(String transactionEmpJobCode) {
	this.transactionEmpJobCode = transactionEmpJobCode;
    }

    @Basic
    @Column(name = "TRANS_EMP_JOB_DESC")
    @XmlTransient
    public String getTransactionEmpJobDesc() {
	return transactionEmpJobDesc;
    }

    public void setTransactionEmpJobDesc(String transactionEmpJobDesc) {
	this.transactionEmpJobDesc = transactionEmpJobDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_UNIT_DESC")
    @XmlTransient
    public String getTransactionEmpUnitDesc() {
	return transactionEmpUnitDesc;
    }

    public void setTransactionEmpUnitDesc(String transactionEmpUnitDesc) {
	this.transactionEmpUnitDesc = transactionEmpUnitDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_CATEGORY_ID")
    @XmlTransient
    public Long getTransactionEmpCategoryId() {
	return transactionEmpCategoryId;
    }

    public void setTransactionEmpCategoryId(Long transactionEmpCategoryId) {
	this.transactionEmpCategoryId = transactionEmpCategoryId;
    }

    @Basic
    @Column(name = "TRANS_APPROVED_RANK_DESC")
    @XmlTransient
    public String getTransactionApprovedRankDesc() {
	return transactionApprovedRankDesc;
    }

    public void setTransactionApprovedRankDesc(String transactionApprovedRankDesc) {
	this.transactionApprovedRankDesc = transactionApprovedRankDesc;
    }

    @Basic
    @Column(name = "TRANS_APPROVED_JOB_DESC")
    @XmlTransient
    public String getTransactionApprovedJobDesc() {
	return transactionApprovedJobDesc;
    }

    public void setTransactionApprovedJobDesc(String transactionApprovedJobDesc) {
	this.transactionApprovedJobDesc = transactionApprovedJobDesc;
    }

    @Basic
    @Column(name = "DECISION_DATA")
    @XmlTransient
    public String getDecisionData() {
	return decisionData;

    }

    public void setDecisionData(String decisionData) {
	this.decisionData = decisionData;
    }

    @Basic
    @Column(name = "JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getJoiningDate() {
	return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
	this.joiningDate = joiningDate;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    @XmlTransient
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "DECISION_REGION_ID")
    @XmlTransient
    public Long getDecisionRegionId() {
	return decisionRegionId;
    }

    public void setDecisionRegionId(Long decisionRegionId) {
	this.decisionRegionId = decisionRegionId;
    }

    @Basic
    @Column(name = "EXTERNAL_COPIES")
    @XmlTransient
    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }

    @Basic
    @Column(name = "CONTRACTUAL_YEAR_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getContractualYearStartDate() {
	return contractualYearStartDate;
    }

    public void setContractualYearStartDate(Date contractualYearStartDate) {
	this.contractualYearStartDate = contractualYearStartDate;
	this.contractualYearStartDateString = HijriDateService.getHijriDateString(contractualYearStartDate);
    }

    @Transient
    @XmlTransient
    public String getContractualYearStartDateString() {
	return contractualYearStartDateString;
    }

    public void setContractualYearStartDateString(String contractualYearStartDateString) {
	this.contractualYearStartDateString = contractualYearStartDateString;
	this.contractualYearStartDate = HijriDateService.getHijriDate(contractualYearStartDateString);
    }

    @Basic
    @Column(name = "CONTRACTUAL_YEAR_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getContractualYearEndDate() {
	return contractualYearEndDate;
    }

    public void setContractualYearEndDate(Date contractualYearEndDate) {
	this.contractualYearEndDate = contractualYearEndDate;
	this.contractualYearEndDateString = HijriDateService.getHijriDateString(contractualYearEndDate);
    }

    @Transient
    @XmlTransient
    public String getContractualYearEndDateString() {
	return contractualYearEndDateString;
    }

    public void setContractualYearEndDateString(String contractualYearEndDateString) {
	this.contractualYearEndDateString = contractualYearEndDateString;
	this.contractualYearEndDate = HijriDateService.getHijriDate(contractualYearEndDateString);
    }

    @Basic
    @Column(name = "REFERRING")
    @XmlTransient
    public String getReferring() {
	return referring;
    }

    public void setReferring(String referring) {
	this.referring = referring;
    }

    @Basic
    @Column(name = "RELATED_DEDUCTED_BALANCE")
    @XmlTransient
    public Integer getRelatedDeductedBalance() {
	return relatedDeductedBalance;
    }

    public void setRelatedDeductedBalance(Integer relatedDeductedBalance) {
	this.relatedDeductedBalance = relatedDeductedBalance;
    }

    @Basic
    @Column(name = "EXCEEDED_DAYS")
    @XmlTransient
    public Integer getExceededDays() {
	return exceededDays;
    }

    public void setExceededDays(Integer exceededDays) {
	this.exceededDays = exceededDays;
    }

    @Basic
    @Column(name = "FRAME_START_DATE")
    public Date getFrameStartDate() {
	return frameStartDate;
    }

    public void setFrameStartDate(Date frameStartDate) {
	this.frameStartDate = frameStartDate;
    }

    @Basic
    @Column(name = "FRAME_END_DATE")
    public Date getFrameEndDate() {
	return frameEndDate;
    }

    public void setFrameEndDate(Date frameEndDate) {
	this.frameEndDate = frameEndDate;
    }

    @Override
    public Long calculateContentId() {
	return vacationId;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"joiningDate:" + joiningDate;
    }
}
