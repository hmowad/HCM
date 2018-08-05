package com.code.dal.orm.integration.finance;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "fin_PaidIssueOrderData_getPaidIssueOrder",
		query = " select d from PaidIssueOrderData d" +
			" where d.requestNumber = :P_REQUEST_NUMBER ")
})

@Entity
@Table(name = "BG_VW_FIN_PAID_ISSUE_ORDERS")
public class PaidIssueOrderData implements Serializable {

    private String requestNumber;

    private Long orderNumber;
    private Date orderDate;
    private String orderDateString;

    private String paymentOrderNumber;
    private Date paymentOrderDate;
    private String paymentOrderDateString;

    private String chequeNumber;
    private Date chequeDate;
    private String chequeDateString;

    @Id
    @Column(name = "REQUEST_NUMBER")
    public String getRequestNumber() {
	return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
	this.requestNumber = requestNumber;
    }

    @Basic
    @Column(name = "ORDER_NO")
    @XmlElement(nillable = true)
    public Long getOrderNumber() {
	return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
	this.orderNumber = orderNumber;
    }

    @Basic
    @Column(name = "ORDER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getOrderDate() {
	return orderDate;
    }

    public void setOrderDate(Date orderDate) {
	this.orderDate = orderDate;
	this.orderDateString = HijriDateService.getHijriDateString(orderDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getOrderDateString() {
	return orderDateString;
    }

    public void setOrderDateString(String orderDateString) {
	this.orderDateString = orderDateString;
	this.orderDate = HijriDateService.getHijriDate(orderDateString);
    }

    @Basic
    @Column(name = "PAYMENT_ORDER_NUMBER")
    @XmlElement(nillable = true)
    public String getPaymentOrderNumber() {
	return paymentOrderNumber;
    }

    public void setPaymentOrderNumber(String paymentOrderNumber) {
	this.paymentOrderNumber = paymentOrderNumber;
    }

    @Basic
    @Column(name = "PAYMENT_ORDER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getPaymentOrderDate() {
	return paymentOrderDate;
    }

    public void setPaymentOrderDate(Date paymentOrderDate) {
	this.paymentOrderDate = paymentOrderDate;
	this.paymentOrderDateString = HijriDateService.getHijriDateString(paymentOrderDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getPaymentOrderDateString() {
	return paymentOrderDateString;
    }

    public void setPaymentOrderDateString(String paymentOrderDateString) {
	this.paymentOrderDateString = paymentOrderDateString;
	this.paymentOrderDate = HijriDateService.getHijriDate(paymentOrderDateString);
    }

    @Basic
    @Column(name = "CHEQUE_NUMBER")
    @XmlElement(nillable = true)
    public String getChequeNumber() {
	return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
	this.chequeNumber = chequeNumber;
    }

    @Basic
    @Column(name = "CHEQUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getChequeDate() {
	return chequeDate;
    }

    public void setChequeDate(Date chequeDate) {
	this.chequeDate = chequeDate;
	this.chequeDateString = HijriDateService.getHijriDateString(chequeDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getChequeDateString() {
	return chequeDateString;
    }

    public void setChequeDateString(String chequeDateString) {
	this.chequeDateString = chequeDateString;
	this.chequeDate = HijriDateService.getHijriDate(chequeDateString);
    }

}
