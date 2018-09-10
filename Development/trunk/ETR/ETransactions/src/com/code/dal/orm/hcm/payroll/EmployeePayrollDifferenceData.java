package com.code.dal.orm.hcm.payroll;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_EmployeePayrollDifferenceData_getPayrollDiffByTrnsStatus",
		query = " select d from EmployeePayrollDifferenceData d" +
			" where d.personId = :P_PERSON_ID " +
			" and (:P_TRANSACTION_STATUS = -1 or d.transactionStatus = :P_TRANSACTION_STATUS) " +
			" and (:P_ELEMENT_DESC = '-1' or d.elementDescription like :P_ELEMENT_DESC) " +
			" order by d.transactionDate desc")
})

@Entity
@Table(name = "BG_VW_EMPLOYEES_DIFF")
@IdClass(EmployeePayrollDifferenceId.class)
public class EmployeePayrollDifferenceData implements Serializable {
    private String personId;
    private Date transactionDate;
    private String transactionDateString;
    private String transactionNumber;
    private String elementDescription;
    private String decesionNumber;
    private Date decesionDate;
    private String decesionDateString;
    private Double totalAmount;
    private Integer transactionStatus;
    private Long transactionType;
    private String regionCode;
    private Long transactionYear;
    private Long TransactionNo;
    private Long summarySerial;

    @Basic
    @Column(name = "PER_ID")
    @XmlTransient
    public String getPersonId() {
	return personId;
    }

    public void setPersonId(String personId) {
	this.personId = personId;
    }

    @Basic
    @Column(name = "TRANS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getTransactionDate() {
	return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
	this.transactionDateString = HijriDateService.getHijriDateString(transactionDate);
    }

    @Transient
    public String getTransactionDateString() {
	return transactionDateString;
    }

    public void setTransactionDateString(String transactionDateString) {
	this.transactionDateString = transactionDateString;
	this.transactionDate = HijriDateService.getHijriDate(transactionDateString);
    }

    @Basic
    @Column(name = "TRANSACTION_NUMBER")
    public String getTransactionNumber() {
	return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
	this.transactionNumber = transactionNumber;
    }

    @Basic
    @Column(name = "ELEMENT_DESCRIPTION")
    public String getElementDescription() {
	return elementDescription;
    }

    public void setElementDescription(String elementDescription) {
	this.elementDescription = elementDescription;
    }

    @Basic
    @Column(name = "DECESION_NO")
    @XmlElement(nillable = true)
    public String getDecesionNumber() {
	return decesionNumber;
    }

    public void setDecesionNumber(String decesionNumber) {
	this.decesionNumber = decesionNumber;
    }

    @Basic
    @Column(name = "DECESION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getDecesionDate() {
	return decesionDate;
    }

    public void setDecesionDate(Date decesionDate) {
	this.decesionDate = decesionDate;
	this.decesionDateString = HijriDateService.getHijriDateString(decesionDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getDecesionDateString() {
	return decesionDateString;
    }

    public void setDecesionDateString(String decesionDateString) {
	this.decesionDateString = decesionDateString;
	this.decesionDate = HijriDateService.getHijriDate(decesionDateString);
    }

    @Basic
    @Column(name = "TOTAL_AMOUNT")
    public Double getTotalAmount() {
	return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
	this.totalAmount = totalAmount;
    }

    @Basic
    @Column(name = "TRANSACTION_STATUS")
    @XmlTransient
    public Integer getTransactionStatus() {
	return transactionStatus;
    }

    public void setTransactionStatus(Integer transactionStatus) {
	this.transactionStatus = transactionStatus;
    }

    @Id
    @Column(name = "TRANSTYPE")
    @XmlTransient
    public Long getTransactionType() {
	return transactionType;
    }

    public void setTransactionType(Long transactionType) {
	this.transactionType = transactionType;
    }

    @Id
    @Column(name = "REGIONCODE")
    @XmlTransient
    public String getRegionCode() {
	return regionCode;
    }

    public void setRegionCode(String regionCode) {
	this.regionCode = regionCode;
    }

    @Id
    @Column(name = "TRANSYEAR")
    @XmlTransient
    public Long getTransactionYear() {
	return transactionYear;
    }

    public void setTransactionYear(Long transactionYear) {
	this.transactionYear = transactionYear;
    }

    @Id
    @Column(name = "TRANSNO")
    @XmlTransient
    public Long getTransactionNo() {
	return TransactionNo;
    }

    public void setTransactionNo(Long transactionNo) {
	TransactionNo = transactionNo;
    }

    @Basic
    @Column(name = "SUMMARY_SERIAL")
    @XmlElement(nillable = true)
    public Long getSummarySerial() {
	return summarySerial;
    }

    public void setSummarySerial(Long summarySerial) {
	this.summarySerial = summarySerial;
    }
}