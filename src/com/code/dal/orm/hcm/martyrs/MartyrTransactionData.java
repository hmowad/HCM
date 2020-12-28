package com.code.dal.orm.hcm.martyrs;

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

/**
 * Provides information regarding martyr transaction such as martyrdom type, date, and martyrdom different compensations number and date .. etc <br/>
 * and as the view of {@link MartyrTransaction} which is mainly used by UI layer through service
 * 
 */

@NamedQueries({
	@NamedQuery(name = "hcm_martyrTransactionData_searchMartyrTransactionsData",
		query = " select m" +
			" from MartyrTransactionData m where"
			+ " (:P_EMP_ID = -1 or m.employeeId = :P_EMP_ID) "
			+ " and (:P_REASON_ID = -1 or m.martyrdomReasonId = :P_REASON_ID) "
			+ " order by m.martyrdomDate DESC")

})

@Entity
@Table(name = "HCM_VW_PRS_MARTYRS_TRANS")
public class MartyrTransactionData extends BaseEntity {
    private Long id;
    private Long employeeId;
    private Long martyrdomReasonId;
    private String martyrdomReasonName;
    private Long martyrdomType;
    private Date martyrdomDate;
    private String martyrdomDateString;
    private Long martyrdomRegionId;
    private String martyrdomRegionName;
    private String firstContactNumber;
    private String secondContactNumber;
    private Integer injuryCompensation;
    private String attachments;
    private String remarks;
    private Integer medicalDecisionFlag;
    private Boolean medicalDecisionFlagBoolean;
    private String retirementCompensationNumber;
    private Date retirementCompensationDate;
    private String retirementCompensationDateString;
    private String vacationsCompensationNumber;
    private Date vacationsCompensationDate;
    private String vacationsCompensationDateString;
    private String deathCompensationNumber;
    private Date deathCompensationDate;
    private String deathCompensationDateString;
    private String terminationCompensationNumber;
    private Date terminationCompensationDate;
    private String terminationCompensationDateString;
    private String injuryCompensationNumber;
    private Date injuryCompensationDate;
    private String injuryCompensationDateString;
    private String housingCompensationNumber;
    private Date housingCompensationDate;
    private String housingCompensationDateString;

    private MartyrTransaction martyrTransaction;

    public MartyrTransactionData() {
	martyrTransaction = new MartyrTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	martyrTransaction.setId(id);
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	martyrTransaction.setEmployeeId(employeeId);
    }

    @Basic
    @Column(name = "MARTYRDOM_REASON_ID")
    public Long getMartyrdomReasonId() {
	return martyrdomReasonId;
    }

    public void setMartyrdomReasonId(Long martyrdomReasonId) {
	this.martyrdomReasonId = martyrdomReasonId;
	martyrTransaction.setMartyrdomReasonId(martyrdomReasonId);
    }

    @Basic
    @Column(name = "MARTYRDOM_REASON_NAME")
    public String getMartyrdomReasonName() {
	return martyrdomReasonName;
    }

    public void setMartyrdomReasonName(String martyrdomReasonName) {
	this.martyrdomReasonName = martyrdomReasonName;
    }

    @Basic
    @Column(name = "MARTYRDOM_TYPE")
    public Long getMartyrdomType() {
	return martyrdomType;
    }

    public void setMartyrdomType(Long martyrdomType) {
	this.martyrdomType = martyrdomType;
	martyrTransaction.setMartyrdomType(martyrdomType);
    }

    @Basic
    @Column(name = "MARTYRDOM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMartyrdomDate() {
	return martyrdomDate;
    }

    public void setMartyrdomDate(Date martyrdomDate) {
	this.martyrdomDate = martyrdomDate;
	martyrTransaction.setMartyrdomDate(martyrdomDate);
	martyrdomDateString = HijriDateService.getHijriDateString(martyrdomDate);
    }

    @Transient
    public String getMartyrdomDateString() {
	return martyrdomDateString;
    }

    public void setMartyrdomDateString(String martyrdomDateString) {
	this.martyrdomDateString = martyrdomDateString;
	martyrdomDate = HijriDateService.getHijriDate(martyrdomDateString);
    }

    @Basic
    @Column(name = "MARTYRDOM_REGION_ID")
    public Long getMartyrdomRegionId() {
	return martyrdomRegionId;
    }

    public void setMartyrdomRegionId(Long martyrdomRegionId) {
	this.martyrdomRegionId = martyrdomRegionId;
	martyrTransaction.setMartyrdomRegionId(martyrdomRegionId);
    }

    @Basic
    @Column(name = "MARTYRDOM_REGION_NAME")
    public String getMartyrdomRegionName() {
	return martyrdomRegionName;
    }

    public void setMartyrdomRegionName(String martyrdomRegionName) {
	this.martyrdomRegionName = martyrdomRegionName;
    }

    @Basic
    @Column(name = "FIRST_CONTACT_NUMBER")
    public String getFirstContactNumber() {
	return firstContactNumber;
    }

    public void setFirstContactNumber(String firstContactNumber) {
	this.firstContactNumber = firstContactNumber;
	martyrTransaction.setFirstContactNumber(firstContactNumber);
    }

    @Basic
    @Column(name = "SECOND_CONTACT_NUMBER")
    public String getSecondContactNumber() {
	return secondContactNumber;
    }

    public void setSecondContactNumber(String secondContactNumber) {
	this.secondContactNumber = secondContactNumber;
	martyrTransaction.setSecondContactNumber(secondContactNumber);
    }

    @Basic
    @Column(name = "INJURY_COMPENSATION")
    public Integer getInjuryCompensation() {
	return injuryCompensation;
    }

    public void setInjuryCompensation(Integer injuryCompensation) {
	this.injuryCompensation = injuryCompensation;
	martyrTransaction.setInjuryCompensation(injuryCompensation);
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
	martyrTransaction.setAttachments(attachments);
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
	martyrTransaction.setRemarks(remarks);
    }

    @Basic
    @Column(name = "MEDICAL_DECISION_FLAG")
    public Integer getMedicalDecisionFlag() {
	return medicalDecisionFlag;
    }

    public void setMedicalDecisionFlag(Integer medicalDecisionFlag) {
	this.medicalDecisionFlag = medicalDecisionFlag;
	if (this.medicalDecisionFlag == null || this.medicalDecisionFlag == FlagsEnum.OFF.getCode())
	    this.medicalDecisionFlagBoolean = false;
	else
	    this.medicalDecisionFlagBoolean = true;
	martyrTransaction.setMedicalDecisionFlag(medicalDecisionFlag);
    }

    @Transient
    public Boolean getMedicalDecisionFlagBoolean() {
	return medicalDecisionFlagBoolean;
    }

    public void setMedicalDecisionFlagBoolean(Boolean medicalDecisionFlagBoolean) {
	this.medicalDecisionFlagBoolean = medicalDecisionFlagBoolean;
	if (this.medicalDecisionFlagBoolean == null || !this.medicalDecisionFlagBoolean) {
	    this.medicalDecisionFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.medicalDecisionFlag = FlagsEnum.ON.getCode();
	}
	martyrTransaction.setMedicalDecisionFlag(medicalDecisionFlag);
    }

    @Basic
    @Column(name = "RETIREMENT_COMPENSATION_NUM")
    public String getRetirementCompensationNumber() {
	return retirementCompensationNumber;
    }

    public void setRetirementCompensationNumber(String retirementCompensationNumber) {
	this.retirementCompensationNumber = retirementCompensationNumber;
	martyrTransaction.setRetirementCompensationNumber(retirementCompensationNumber);
    }

    @Basic
    @Column(name = "RETIREMENT_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRetirementCompensationDate() {
	return retirementCompensationDate;
    }

    public void setRetirementCompensationDate(Date retirementCompensationDate) {
	this.retirementCompensationDate = retirementCompensationDate;
	martyrTransaction.setRetirementCompensationDate(retirementCompensationDate);
	retirementCompensationDateString = HijriDateService.getHijriDateString(retirementCompensationDate);
    }

    @Transient
    public String getRetirementCompensationDateString() {
	return retirementCompensationDateString;
    }

    public void setRetirementCompensationDateString(String retirementCompensationDateString) {
	this.retirementCompensationDateString = retirementCompensationDateString;
	retirementCompensationDate = HijriDateService.getHijriDate(retirementCompensationDateString);
    }

    @Basic
    @Column(name = "VACATIONS_COMPENSATION_NUM")
    public String getVacationsCompensationNumber() {
	return vacationsCompensationNumber;
    }

    public void setVacationsCompensationNumber(String vacationsCompensationNumber) {
	this.vacationsCompensationNumber = vacationsCompensationNumber;
	martyrTransaction.setVacationsCompensationNumber(vacationsCompensationNumber);
    }

    @Basic
    @Column(name = "VACATIONS_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getVacationsCompensationDate() {
	return vacationsCompensationDate;
    }

    public void setVacationsCompensationDate(Date vacationsCompensationDate) {
	this.vacationsCompensationDate = vacationsCompensationDate;
	martyrTransaction.setVacationsCompensationDate(vacationsCompensationDate);
	vacationsCompensationDateString = HijriDateService.getHijriDateString(vacationsCompensationDate);
    }

    @Transient
    public String getVacationsCompensationDateString() {
	return vacationsCompensationDateString;
    }

    public void setVacationsCompensationDateString(String vacationsCompensationDateString) {
	this.vacationsCompensationDateString = vacationsCompensationDateString;
	vacationsCompensationDate = HijriDateService.getHijriDate(vacationsCompensationDateString);
    }

    @Basic
    @Column(name = "DEATH_COMPENSATION_NUM")
    public String getDeathCompensationNumber() {
	return deathCompensationNumber;
    }

    public void setDeathCompensationNumber(String deathCompensationNumber) {
	this.deathCompensationNumber = deathCompensationNumber;
	martyrTransaction.setDeathCompensationNumber(deathCompensationNumber);
    }

    @Basic
    @Column(name = "DEATH_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeathCompensationDate() {
	return deathCompensationDate;
    }

    public void setDeathCompensationDate(Date deathCompensationDate) {
	this.deathCompensationDate = deathCompensationDate;
	martyrTransaction.setDeathCompensationDate(deathCompensationDate);
	deathCompensationDateString = HijriDateService.getHijriDateString(deathCompensationDate);
    }

    @Transient
    public String getDeathCompensationDateString() {
	return deathCompensationDateString;
    }

    public void setDeathCompensationDateString(String deathCompensationDateString) {
	this.deathCompensationDateString = deathCompensationDateString;
	deathCompensationDate = HijriDateService.getHijriDate(deathCompensationDateString);
    }

    @Basic
    @Column(name = "TERMINATION_COMPENSATION_NUM")
    public String getTerminationCompensationNumber() {
	return terminationCompensationNumber;
    }

    public void setTerminationCompensationNumber(String terminationCompensationNumber) {
	this.terminationCompensationNumber = terminationCompensationNumber;
	martyrTransaction.setTerminationCompensationNumber(terminationCompensationNumber);
    }

    @Basic
    @Column(name = "TERMINATION_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTerminationCompensationDate() {
	return terminationCompensationDate;
    }

    public void setTerminationCompensationDate(Date terminationCompensationDate) {
	this.terminationCompensationDate = terminationCompensationDate;
	martyrTransaction.setTerminationCompensationDate(terminationCompensationDate);
	terminationCompensationDateString = HijriDateService.getHijriDateString(terminationCompensationDate);
    }

    @Transient
    public String getTerminationCompensationDateString() {
	return terminationCompensationDateString;
    }

    public void setTerminationCompensationDateString(String terminationCompensationDateString) {
	this.terminationCompensationDateString = terminationCompensationDateString;
	terminationCompensationDate = HijriDateService.getHijriDate(terminationCompensationDateString);
    }

    @Basic
    @Column(name = "INJURY_COMPENSATION_NUM")
    public String getInjuryCompensationNumber() {
	return injuryCompensationNumber;
    }

    public void setInjuryCompensationNumber(String injuryCompensationNumber) {
	this.injuryCompensationNumber = injuryCompensationNumber;
	martyrTransaction.setInjuryCompensationNumber(injuryCompensationNumber);
    }

    @Basic
    @Column(name = "INJURY_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getInjuryCompensationDate() {
	return injuryCompensationDate;
    }

    public void setInjuryCompensationDate(Date injuryCompensationDate) {
	this.injuryCompensationDate = injuryCompensationDate;
	martyrTransaction.setInjuryCompensationDate(injuryCompensationDate);
	injuryCompensationDateString = HijriDateService.getHijriDateString(injuryCompensationDate);
    }

    @Transient
    public String getInjuryCompensationDateString() {
	return injuryCompensationDateString;
    }

    public void setInjuryCompensationDateString(String injuryCompensationDateString) {
	this.injuryCompensationDateString = injuryCompensationDateString;
	injuryCompensationDate = HijriDateService.getHijriDate(injuryCompensationDateString);
    }

    @Basic
    @Column(name = "HOUSING_COMPENSATION_NUM")
    public String getHousingCompensationNumber() {
	return housingCompensationNumber;
    }

    public void setHousingCompensationNumber(String housingCompensationNumber) {
	this.housingCompensationNumber = housingCompensationNumber;
	martyrTransaction.setHousingCompensationNumber(housingCompensationNumber);
    }

    @Basic
    @Column(name = "HOUSING_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHousingCompensationDate() {
	return housingCompensationDate;
    }

    public void setHousingCompensationDate(Date housingCompensationDate) {
	this.housingCompensationDate = housingCompensationDate;
	martyrTransaction.setHousingCompensationDate(housingCompensationDate);
	housingCompensationDateString = HijriDateService.getHijriDateString(housingCompensationDate);
    }

    @Transient
    public String getHousingCompensationDateString() {
	return housingCompensationDateString;
    }

    public void setHousingCompensationDateString(String housingCompensationDateString) {
	this.housingCompensationDateString = housingCompensationDateString;
	housingCompensationDate = HijriDateService.getHijriDate(housingCompensationDateString);
    }

    @Transient
    public MartyrTransaction getMartyrTransaction() {
	return martyrTransaction;
    }

    public void setMartyrTransaction(MartyrTransaction martyrTransaction) {
	this.martyrTransaction = martyrTransaction;
    }

}
