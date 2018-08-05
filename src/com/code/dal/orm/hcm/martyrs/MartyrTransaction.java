package com.code.dal.orm.hcm.martyrs;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * Provides information regarding martyr transaction such as martyrdom type, date, and martyrdom different compensations number and date .. etc <br/>
 * 
 */

@Entity
@Table(name = "HCM_PRS_MARTYRS_TRANSACTIONS")
public class MartyrTransaction extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long employeeId;
    private Long martyrdomReasonId;
    private Long martyrdomType;
    private Date martyrdomDate;
    private Long martyrdomRegionId;
    private String firstContactNumber;
    private String secondContactNumber;
    private Integer injuryCompensation;
    private String attachments;
    private String remarks;
    private Integer medicalDecisionFlag;
    private String retirementCompensationNumber;
    private Date retirementCompensationDate;
    private String vacationsCompensationNumber;
    private Date vacationsCompensationDate;
    private String deathCompensationNumber;
    private Date deathCompensationDate;
    private String terminationCompensationNumber;
    private Date terminationCompensationDate;
    private String injuryCompensationNumber;
    private Date injuryCompensationDate;
    private String housingCompensationNumber;
    private Date housingCompensationDate;

    @SequenceGenerator(name = "HCMMartyrsSeq",
	    sequenceName = "HCM_PRS_MARTYRS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMMartyrsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "MARTYRDOM_REASON_ID")
    public Long getMartyrdomReasonId() {
	return martyrdomReasonId;
    }

    public void setMartyrdomReasonId(Long martyrdomReasonId) {
	this.martyrdomReasonId = martyrdomReasonId;
    }

    @Basic
    @Column(name = "MARTYRDOM_TYPE")
    public Long getMartyrdomType() {
	return martyrdomType;
    }

    public void setMartyrdomType(Long martyrdomType) {
	this.martyrdomType = martyrdomType;
    }

    @Basic
    @Column(name = "MARTYRDOM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMartyrdomDate() {
	return martyrdomDate;
    }

    public void setMartyrdomDate(Date martyrdomDate) {
	this.martyrdomDate = martyrdomDate;
    }

    @Basic
    @Column(name = "MARTYRDOM_REGION_ID")
    public Long getMartyrdomRegionId() {
	return martyrdomRegionId;
    }

    public void setMartyrdomRegionId(Long martyrdomRegionId) {
	this.martyrdomRegionId = martyrdomRegionId;
    }

    @Basic
    @Column(name = "FIRST_CONTACT_NUMBER")
    public String getFirstContactNumber() {
	return firstContactNumber;
    }

    public void setFirstContactNumber(String firstContactNumber) {
	this.firstContactNumber = firstContactNumber;
    }

    @Basic
    @Column(name = "SECOND_CONTACT_NUMBER")
    public String getSecondContactNumber() {
	return secondContactNumber;
    }

    public void setSecondContactNumber(String secondContactNumber) {
	this.secondContactNumber = secondContactNumber;
    }

    @Basic
    @Column(name = "INJURY_COMPENSATION")
    public Integer getInjuryCompensation() {
	return injuryCompensation;
    }

    public void setInjuryCompensation(Integer injuryCompensation) {
	this.injuryCompensation = injuryCompensation;
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
    @Column(name = "MEDICAL_DECISION_FLAG")
    public Integer getMedicalDecisionFlag() {
	return medicalDecisionFlag;
    }

    public void setMedicalDecisionFlag(Integer medicalDecisionFlag) {
	this.medicalDecisionFlag = medicalDecisionFlag;
    }

    @Basic
    @Column(name = "RETIREMENT_COMPENSATION_NUM")
    public String getRetirementCompensationNumber() {
	return retirementCompensationNumber;
    }

    public void setRetirementCompensationNumber(String retirementCompensationNumber) {
	this.retirementCompensationNumber = retirementCompensationNumber;
    }

    @Basic
    @Column(name = "RETIREMENT_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRetirementCompensationDate() {
	return retirementCompensationDate;
    }

    public void setRetirementCompensationDate(Date retirementCompensationDate) {
	this.retirementCompensationDate = retirementCompensationDate;
    }

    @Basic
    @Column(name = "VACATIONS_COMPENSATION_NUM")
    public String getVacationsCompensationNumber() {
	return vacationsCompensationNumber;
    }

    public void setVacationsCompensationNumber(String vacationsCompensationNumber) {
	this.vacationsCompensationNumber = vacationsCompensationNumber;
    }

    @Basic
    @Column(name = "VACATIONS_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getVacationsCompensationDate() {
	return vacationsCompensationDate;
    }

    public void setVacationsCompensationDate(Date vacationsCompensationDate) {
	this.vacationsCompensationDate = vacationsCompensationDate;
    }

    @Basic
    @Column(name = "DEATH_COMPENSATION_NUM")
    public String getDeathCompensationNumber() {
	return deathCompensationNumber;
    }

    public void setDeathCompensationNumber(String deathCompensationNumber) {
	this.deathCompensationNumber = deathCompensationNumber;
    }

    @Basic
    @Column(name = "DEATH_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDeathCompensationDate() {
	return deathCompensationDate;
    }

    public void setDeathCompensationDate(Date deathCompensationDate) {
	this.deathCompensationDate = deathCompensationDate;
    }

    @Basic
    @Column(name = "TERMINATION_COMPENSATION_NUM")
    public String getTerminationCompensationNumber() {
	return terminationCompensationNumber;
    }

    public void setTerminationCompensationNumber(String terminationCompensationNumber) {
	this.terminationCompensationNumber = terminationCompensationNumber;
    }

    @Basic
    @Column(name = "TERMINATION_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTerminationCompensationDate() {
	return terminationCompensationDate;
    }

    public void setTerminationCompensationDate(Date terminationCompensationDate) {
	this.terminationCompensationDate = terminationCompensationDate;
    }

    @Basic
    @Column(name = "INJURY_COMPENSATION_NUM")
    public String getInjuryCompensationNumber() {
	return injuryCompensationNumber;
    }

    public void setInjuryCompensationNumber(String injuryCompensationNumber) {
	this.injuryCompensationNumber = injuryCompensationNumber;
    }

    @Basic
    @Column(name = "INJURY_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getInjuryCompensationDate() {
	return injuryCompensationDate;
    }

    public void setInjuryCompensationDate(Date injuryCompensationDate) {
	this.injuryCompensationDate = injuryCompensationDate;
    }

    @Basic
    @Column(name = "HOUSING_COMPENSATION_NUM")
    public String getHousingCompensationNumber() {
	return housingCompensationNumber;
    }

    public void setHousingCompensationNumber(String housingCompensationNumber) {
	this.housingCompensationNumber = housingCompensationNumber;
    }

    @Basic
    @Column(name = "HOUSING_COMPENSATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHousingCompensationDate() {
	return housingCompensationDate;
    }

    public void setHousingCompensationDate(Date housingCompensationDate) {
	this.housingCompensationDate = housingCompensationDate;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "id:" + id + AUDIT_SEPARATOR +
		"employeeId:" + employeeId + AUDIT_SEPARATOR +
		"martyrdomReasonId:" + martyrdomReasonId + AUDIT_SEPARATOR +
		"martyrdomType:" + martyrdomType + AUDIT_SEPARATOR +
		"martyrdomDate:" + martyrdomDate + AUDIT_SEPARATOR +
		"martyrdomRegionId:" + martyrdomRegionId + AUDIT_SEPARATOR +
		"firstContactNumber:" + firstContactNumber + AUDIT_SEPARATOR +
		"secondContactNumber:" + secondContactNumber + AUDIT_SEPARATOR +
		"injuryCompensation:" + injuryCompensation + AUDIT_SEPARATOR +
		"attachments:" + attachments + AUDIT_SEPARATOR +
		"remarks:" + remarks + AUDIT_SEPARATOR +
		"medicalDecisionFlag:" + medicalDecisionFlag + AUDIT_SEPARATOR +
		"retirementCompensationNumber:" + retirementCompensationNumber + AUDIT_SEPARATOR +
		"retirementCompensationDate:" + retirementCompensationDate + AUDIT_SEPARATOR +
		"vacationsCompensationNumber:" + vacationsCompensationNumber + AUDIT_SEPARATOR +
		"vacationsCompensationDate:" + vacationsCompensationDate + AUDIT_SEPARATOR +
		"deathCompensationNumber:" + deathCompensationNumber + AUDIT_SEPARATOR +
		"deathCompensationDate:" + deathCompensationDate + AUDIT_SEPARATOR +
		"injuryCompensationNumber:" + injuryCompensationNumber + AUDIT_SEPARATOR +
		"injuryCompensationDate:" + injuryCompensationDate + AUDIT_SEPARATOR +
		"terminationCompensationNumber:" + terminationCompensationNumber + AUDIT_SEPARATOR +
		"terminationCompensationDate:" + terminationCompensationDate + AUDIT_SEPARATOR +
		"housingCompensationNumber:" + housingCompensationNumber + AUDIT_SEPARATOR +
		"housingCompensationDate:" + housingCompensationDate + AUDIT_SEPARATOR;

    }

}
