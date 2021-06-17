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
	@NamedQuery(name = "hcm_SummaryPayrollDifferenceData_getSummaryPayrollDiffBySummaryCodeAndEmployeeIdAndOrderStatus",
		query = " select d from SummaryDifferenceData d"
			+ " where d.employeeId = :P_EMP_ID"
			+ " and (:P_CODE = '-1' or d.code like :P_CODE) "
			+ " and d.status = :P_STATUS ")
})
@Entity
@Table(name = "BG_VW_DIFF_SUMMARIES")
public class SummaryDifferenceData implements Serializable {

    private String code;
    private Long employeeId;
    private String summaryDate;
    private Integer orderNumber;
    private String orderDate;
    private String paymentOrderNumber;
    private String paymentOrderDate;
    private String chequeNumber;
    private String chequeDate;
    private String orderStatus;
    private Double NetAmount;
    private int status;

    @Id
    @Column(name = "CODE")
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    @Id
    @Column(name = "EMP_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "SUMMARY_DATE")
    public String getSummaryDate() {
	return summaryDate;
    }

    public void setSummaryDate(String summaryDate) {
	this.summaryDate = summaryDate;
    }

    @Basic
    @Column(name = "ORDER_NUMBER")
    public Integer getOrderNumber() {
	return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
	this.orderNumber = orderNumber;
    }

    @Basic
    @Column(name = "ORDER_DATE")
    public String getOrderDate() {
	return orderDate;
    }

    public void setOrderDate(String orderDate) {
	this.orderDate = orderDate;
    }

    @Basic
    @Column(name = "PAYMENT_ORDER_NUMBER")
    public String getPaymentOrderNumber() {
	return paymentOrderNumber;
    }

    public void setPaymentOrderNumber(String paymentOrderNumber) {
	this.paymentOrderNumber = paymentOrderNumber;
    }

    @Basic
    @Column(name = "PAYMENT_ORDER_DATE")
    public String getPaymentOrderDate() {
	return paymentOrderDate;
    }

    public void setPaymentOrderDate(String paymentOrderDate) {
	this.paymentOrderDate = paymentOrderDate;
    }

    @Basic
    @Column(name = "CHEQUE_NUMBER")
    public String getChequeNumber() {
	return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
	this.chequeNumber = chequeNumber;
    }

    @Basic
    @Column(name = "CHEQUE_DATE")
    public String getChequeDate() {
	return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
	this.chequeDate = chequeDate;
    }

    @Basic
    @Column(name = "ORDER_STATUS")
    public String getOrderStatus() {
	return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
	this.orderStatus = orderStatus;
    }

    @Basic
    @Column(name = "NET")
    public Double getNetAmount() {
	return NetAmount;
    }

    public void setNetAmount(Double netAmount) {
	NetAmount = netAmount;
    }

    @Basic
    @Column(name = "STATUS")
    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

}
