package com.code.dal.orm.hcm.vacations;

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

/**
 * The <code>VacationData</code> class represents the vacation data for all vacation types for all employees in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_vacationData_getVacationDataById",
		query = " select v from VacationData v " +
			" where v.id = :P_VACATION_ID "),

	@NamedQuery(name = "hcm_vacationData_getExternalVacation",
		query = " select v from VacationData v " +
			" where v.empId = :P_EMP_ID " +
			" and v.status <> 4 " +
			" and (to_date(:P_TRAVEL_DATE, 'MI/MM/YYYY') >= v.startDate and to_date(:P_TRAVEL_DATE, 'MI/MM/YYYY') <= v.endDate) "),

	@NamedQuery(name = "hcm_vacationData_getLastVacation",
		query = " select v from VacationData v " +
			" where v.empId = :P_EMP_ID " +
			" and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			" and (:P_SUB_VACATION_TYPE = -1 or v.subVacationType = :P_SUB_VACATION_TYPE) " +
			" and v.status <> 4 " +
			" order by v.endDate desc"),

	@NamedQuery(name = "hcm_vacationData_getLastVacationBeforeSpecificDate",
		query = " select v from VacationData v " +
			" where v.empId = :P_EMP_ID " +
			" and v.status <> 4 " +
			" and (:P_WITHOUT_JOINING_FLAG = -1 or (:P_WITHOUT_JOINING_FLAG = 1 and v.joiningDate is null))" +
			" and v.endDate = (select max(vac.endDate) from Vacation vac where vac.empId = :P_EMP_ID and vac.status <> 4 and (:P_WITHOUT_JOINING_FLAG = -1 or (:P_WITHOUT_JOINING_FLAG = 1 and vac.joiningDate is null)) and vac.endDate < to_date(:P_DATE, 'MI/MM/YYYY') ) "),

	@NamedQuery(name = "hcm_vacationData_getLastVacationAfterSpecificDate",
		query = " select v from VacationData v " +
			" where v.empId = :P_EMP_ID " +
			" and v.status <> 4 " +
			" and v.endDate = (select max(vac.endDate) from Vacation vac where vac.empId = :P_EMP_ID and vac.status <> 4 and vac.endDate >= to_date(:P_DATE, 'MI/MM/YYYY') ) "),

	@NamedQuery(name = "hcm_vacationData_getUnitCurrentVacationsData",
		query = "select v from VacationData v " +
			"where v.currentEmployeePhysicalUnitId in (select id from UnitData where hKey like :P_PREFIX_HKEY) and v.status <> 4 " +
			"  and (v.startDate > to_date(:P_TODAY_DATE, 'MI/MM/YYYY') or to_date(:P_TODAY_DATE, 'MI/MM/YYYY') between v.startDate and v.endDate)" +
			" and (:P_CATEGORIES_IDS_FLAG = -1  or v.transactionEmpCategoryId in (:P_CATEGORIES_IDS) ) " +
			"order by v.empName"),

	@NamedQuery(name = "hcm_vacationData_countUnitCurrentVacationsData",
		query = " select count(v.id) from VacationData v " +
			" where v.currentEmployeePhysicalUnitId in (select id from UnitData where hKey like :P_PREFIX_HKEY) and v.status <> 4 " +
			" and (to_date(:P_SELECTED_DATE, 'MI/MM/YYYY') between v.startDate and v.endDate) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1  or v.transactionEmpCategoryId in (:P_CATEGORIES_IDS) ) "),

	@NamedQuery(name = "hcm_vacationData_transactions_history",
		query = " select v from VacationData v " +
			" where v.empId = :P_EMP_ID " +
			" order by startDate desc")
})
@Entity
@Table(name = "HCM_VW_VAC_TRANSACTIONS")
public class VacationData extends BaseEntity {
    private Long id;
    private Long empId;
    private String empName;
    private Long currentEmployeePhysicalUnitId;
    private Long vacationTypeId;
    private String vacationTypeDescription;
    private Integer subVacationType;
    private String subVacationTypeDescription;
    private String vacationTransactionTypeDescription;
    private Integer paidVacationType;
    private Integer locationFlag;
    private String location;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Integer period;
    private Date extStartDate;
    private String extStartDateString;
    private Date extEndDate;
    private String extEndDateString;
    private Integer extPeriod;
    private String extLocation;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Integer status;
    private Long transactionEmpCategoryId;
    private Date joiningDate;
    private String joiningDateString;
    private Integer exceededDays;
    private Date contractualYearStartDate;
    private String contractualYearStartDateString;
    private Date contractualYearEndDate;
    private String contractualYearEndDateString;
    private Integer etrFlag;
    private String attachments;

    public void setId(Long id) {
	this.id = id;
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setVacationTypeDescription(String vacationTypeDescription) {
	this.vacationTypeDescription = vacationTypeDescription;
    }

    @Basic
    @Column(name = "VACATION_TYPE_DESCRIPTION")
    public String getVacationTypeDescription() {
	return vacationTypeDescription;
    }

    public void setSubVacationType(Integer subVacationType) {
	this.subVacationType = subVacationType;
    }

    @Basic
    @Column(name = "SUB_VACATION_TYPE")
    public Integer getSubVacationType() {
	return subVacationType;
    }

    public void setSubVacationTypeDescription(String subVacationTypeDescription) {
	this.subVacationTypeDescription = subVacationTypeDescription;
    }

    @Basic
    @Column(name = "SUB_VACATION_TYPE_DESCRIPTION")
    public String getSubVacationTypeDescription() {
	return subVacationTypeDescription;
    }

    @Basic
    @Column(name = "VACATION_TRANSACTION_TYPE_DESC")
    public String getVacationTransactionTypeDescription() {
	return vacationTransactionTypeDescription;
    }

    public void setVacationTransactionTypeDescription(String vacationTransactionTypeDescription) {
	this.vacationTransactionTypeDescription = vacationTransactionTypeDescription;
    }

    @Basic
    @Column(name = "PAID_VACATION_TYPE")
    public Integer getPaidVacationType() {
	return paidVacationType;
    }

    public void setPaidVacationType(Integer paidVacationType) {
	this.paidVacationType = paidVacationType;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
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
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "EMP_NAME")
    public String getEmpName() {
	return empName;
    }

    public void setCurrentEmployeePhysicalUnitId(Long currentEmployeePhysicalUnitId) {
	this.currentEmployeePhysicalUnitId = currentEmployeePhysicalUnitId;
    }

    @Basic
    @Column(name = "CURRENT_EMP_PHYSICAL_UNIT_ID")
    public Long getCurrentEmployeePhysicalUnitId() {
	return currentEmployeePhysicalUnitId;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    @Basic
    @Column(name = "LOCATION")
    public String getLocation() {
	return location;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
	this.startDateString = HijriDateService.getHijriDateString(startDate);
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
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

    @Basic
    @Column(name = "EXT_START_DATE")
    public Date getExtStartDate() {
	return extStartDate;
    }

    public void setExtStartDate(Date extStartDate) {
	this.extStartDate = extStartDate;
	this.extStartDateString = HijriDateService.getHijriDateString(extStartDate);
    }

    @Transient
    public String getExtStartDateString() {
	return extStartDateString;
    }

    public void setExtStartDateString(String extStartDateString) {
	this.extStartDateString = extStartDateString;
	this.extStartDate = HijriDateService.getHijriDate(extStartDateString);
    }

    @Basic
    @Column(name = "EXT_END_DATE")
    public Date getExtEndDate() {
	return extEndDate;
    }

    public void setExtEndDate(Date extEndDate) {
	this.extEndDate = extEndDate;
	this.extEndDateString = HijriDateService.getHijriDateString(extEndDate);

    }

    @Transient
    public String getExtEndDateString() {
	return extEndDateString;
    }

    public void setExtEndDateString(String extEndDateString) {
	this.extEndDateString = extEndDateString;
	this.extEndDate = HijriDateService.getHijriDate(extEndDateString);
    }

    @Basic
    @Column(name = "EXT_PERIOD")
    public Integer getExtPeriod() {
	return extPeriod;
    }

    public void setExtPeriod(Integer extPeriod) {
	this.extPeriod = extPeriod;
    }

    @Basic
    @Column(name = "EXT_LOCATION")
    public String getExtLocation() {
	return extLocation;
    }

    public void setExtLocation(String extLocation) {
	this.extLocation = extLocation;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    @Basic
    @Column(name = "VACATION_TYPE_ID")
    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    @Basic
    @Column(name = "TRANS_EMP_CATEGORY_ID")
    public Long getTransactionEmpCategoryId() {
	return transactionEmpCategoryId;
    }

    public void setTransactionEmpCategoryId(Long transactionEmpCategoryId) {
	this.transactionEmpCategoryId = transactionEmpCategoryId;
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
    }

    @Transient
    public String getJoiningDateString() {
	return joiningDateString;
    }

    public void setJoiningDateString(String joiningDateString) {
	this.joiningDateString = joiningDateString;
	this.joiningDate = HijriDateService.getHijriDate(joiningDateString);
    }

    @Basic
    @Column(name = "EXCEEDED_DAYS")
    public Integer getExceededDays() {
	return exceededDays;
    }

    public void setExceededDays(Integer exceededDays) {
	this.exceededDays = exceededDays;
    }

    @Basic
    @Column(name = "CONTRACTUAL_YEAR_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getContractualYearStartDate() {
	return contractualYearStartDate;
    }

    public void setContractualYearStartDate(Date contractualYearStartDate) {
	this.contractualYearStartDate = contractualYearStartDate;
	this.contractualYearStartDateString = HijriDateService.getHijriDateString(contractualYearStartDate);
    }

    @Transient
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
    public Date getContractualYearEndDate() {
	return contractualYearEndDate;
    }

    public void setContractualYearEndDate(Date contractualYearEndDate) {
	this.contractualYearEndDate = contractualYearEndDate;
	this.contractualYearEndDateString = HijriDateService.getHijriDateString(contractualYearEndDate);
    }

    @Transient
    public String getContractualYearEndDateString() {
	return contractualYearEndDateString;
    }

    public void setContractualYearEndDateString(String contractualYearEndDateString) {
	this.contractualYearEndDateString = contractualYearEndDateString;
	this.contractualYearEndDate = HijriDateService.getHijriDate(contractualYearEndDateString);
    }

    @Basic
    @Column(name = "EFLAG")
    public Integer getEtrFlag() {
	return etrFlag;
    }

    public void setEtrFlag(Integer etrFlag) {
	this.etrFlag = etrFlag;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }
}