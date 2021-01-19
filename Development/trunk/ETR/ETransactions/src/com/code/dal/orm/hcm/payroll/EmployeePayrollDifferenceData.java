package com.code.dal.orm.hcm.payroll;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@NamedQueries({
	@NamedQuery(name = "hcm_EmployeePayrollDifferenceData_getPayrollDiffByTrnsStatus",
		query = " select d from EmployeePayrollDifferenceData d" +
			" where d.employeeId = :P_EMP_ID " +
			" and (:P_TRANSACTION_STATUS = -1 or d.transactionStatus = :P_TRANSACTION_STATUS) " +
			" and (:P_ELEMENT_DESC = '-1' or d.elementDescription like :P_ELEMENT_DESC) " +
			" order by d.decesionDate desc")
})

@Entity
@Table(name = "BG_VW_EMPLOYEES_DIFF")
public class EmployeePayrollDifferenceData implements Serializable {
    private Long id;
    private Long employeeId;
    private Long elementId;
    private String elementDescription;
    private String elementStartDate;
    private String elementEndDate;
    private String decesionNumber;
    private String decesionDate;
    private Double totalAmount;
    private Integer transactionStatus;
    private String summarySerial;

    @Id
    @Column(name = "ID")
    @XmlTransient
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    } 
    
    @Basic
    @Column(name = "EMP_ID")
    @XmlTransient
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "ELEMENT_ID")
    @XmlTransient
    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
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
    @Column(name = "ELEMENT_START_DATE")
    public String getElementStartDate() {
        return elementStartDate;
    }

    public void setElementStartDate(String elementStartDate) {
        this.elementStartDate = elementStartDate;
    }

    @Basic
    @Column(name = "ELEMENT_END_DATE")
    @XmlElement(nillable = true)
    public String getElementEndDate() {
        return elementEndDate;
    }

    public void setElementEndDate(String elementEndDate) {
        this.elementEndDate = elementEndDate;
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
    @XmlElement(nillable = true)
    public String getDecesionDate() {
	return decesionDate;
    }

    public void setDecesionDate(String decesionDate) {
	this.decesionDate = decesionDate;
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

    @Basic
    @Column(name = "SUMMARY_SERIAL")
    @XmlElement(nillable = true)
    public String getSummarySerial() {
	return summarySerial;
    }

    public void setSummarySerial(String summarySerial) {
	this.summarySerial = summarySerial;
    }
}
