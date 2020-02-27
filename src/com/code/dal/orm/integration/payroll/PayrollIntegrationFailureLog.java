package com.code.dal.orm.integration.payroll;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "HCM_PAYROLL_INTEG_FAIL_LOG")
public class PayrollIntegrationFailureLog {

    private Long id;
    private String requestURL;
    private String requestBody;
    private String response;
    private Long transactionId;
    private String tableName;
    private Date requestDate;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "REQUEST_URL")
    public String getRequestURL() {
	return requestURL;
    }

    public void setRequestURL(String requestURL) {
	this.requestURL = requestURL;
    }

    @Basic
    @Column(name = "REQUEST_BODY")
    @Lob
    public String getRequestBody() {
	return requestBody;
    }

    public void setRequestBody(String requestBody) {
	this.requestBody = requestBody;
    }

    @Basic
    @Column(name = "RESPONSE")
    @Lob
    public String getResponse() {
	return response;
    }

    public void setResponse(String response) {
	this.response = response;
    }

    @Basic
    @Column(name = "TRANSACTION_ID")
    public Long getTransactionId() {
	return transactionId;
    }

    public void setTransactionId(Long transactionId) {
	this.transactionId = transactionId;
    }

    @Basic
    @Column(name = "TABLE_NAME")
    public String getTableName() {
	return tableName;
    }

    public void setTableName(String tableName) {
	this.tableName = tableName;
    }

    @Basic
    @Column(name = "REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRequestDate() {
	return requestDate;
    }

    public void setRequestDate(Date requestDate) {
	this.requestDate = requestDate;
    }

}
