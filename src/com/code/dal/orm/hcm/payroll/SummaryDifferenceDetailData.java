package com.code.dal.orm.hcm.payroll;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery(name = "hcm_SummaryDifferenceDetailData_getSummaryDifferenceDetailDataBySummaryCodeAndEmployeeId",
		query = " select d from SummaryDifferenceDetailData d" +
			" where d.summaryNumber = :P_CODE " +
			" and d.employeeId = :P_EMP_ID " +
			" order by d.elementStartDate desc")
})

@Entity
@Table(name = "BG_VW_DIFF_EMPLOYEE_DETLS")
public class SummaryDifferenceDetailData implements Serializable {

    private String summaryNumber;
    private String elementStartDate;
    private String elementEndDate;
    private Long employeeId;
    private String elementDescription;
    private Long financialElementId;
    private Double totalAmount;

    @Id
    @Column(name = "SUMMARY_NUMBER")
    public String getSummaryNumber() {
	return summaryNumber;
    }

    public void setSummaryNumber(String summaryNumber) {
	this.summaryNumber = summaryNumber;
    }

    @Id
    @Basic
    @Column(name = "ELEMENT_START_DATE")
    public String getElementStartDate() {
	return elementStartDate;
    }

    public void setElementStartDate(String elementStartDate) {
	this.elementStartDate = elementStartDate;
    }

    @Id
    @Basic
    @Column(name = "ELEMENT_END_DATE")
    public String getElementEndDate() {
	return elementEndDate;
    }

    public void setElementEndDate(String elementEndDate) {
	this.elementEndDate = elementEndDate;
    }

    @Id
    @Column(name = "EMP_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Column(name = "ELEMENT_DESCRIPTION")
    public String getElementDescription() {
	return elementDescription;
    }

    public void setElementDescription(String elementDescription) {
	this.elementDescription = elementDescription;
    }

    @Id
    @Column(name = "FINANCIAL_ELEMENT_ID")
    public Long getFinancialElementId() {
	return financialElementId;
    }

    public void setFinancialElementId(Long financialElementId) {
	this.financialElementId = financialElementId;
    }

    @Basic
    @Column(name = "TOTAL_AMOUNT")
    public Double getTotalAmount() {
	return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
	this.totalAmount = totalAmount;
    }

}
