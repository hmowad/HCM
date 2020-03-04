package com.code.dal.orm.integration.payroll;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "HCM_PAYROLL_INTEG_FAIL_LOG")
public class PayrollIntegrationFailureLog extends BaseEntity {

    private Long id;
    private String requestURL;
    private String requestBody;
    private String response;
    private String tableName;
    private Date requestDate;
    private String decisionNumber;
    private Date decisionDate;

    @SequenceGenerator(name = "HCMPayrollIntgFailSeq",
	    sequenceName = "HCM_PAYROLL_INTG_FAIL_SEQ")
    @Id
    @GeneratedValue(generator = "HCMPayrollIntgFailSeq")
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

    @Column(name = "REQUEST_BODY")
    @Lob
    public String getRequestBody() {
	return requestBody;
    }

    public void setRequestBody(String requestBody) {
	this.requestBody = requestBody;
    }

    @Column(name = "RESPONSE")
    @Lob
    public String getResponse() {
	return response;
    }

    public void setResponse(String response) {
	this.response = response;
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

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

}
