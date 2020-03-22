package com.code.dal.orm.integration.payroll;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_payrollIntegrationFailureLog_getNonExecutedPayrollIntegrationFailureLog",
		query = " select f from PayrollIntegrationFailureLogData f " +
			" where (:P_CATEGORY_ID = -1 OR f.categoryId = :P_CATEGORY_ID) " +
			" and (:P_ADMIN_DECISION_ID = -1 OR f.adminDecisionId = :P_ADMIN_DECISION_ID) " +
			" and (:P_DECISION_NUMBER = '-1' OR f.decisionNumber = :P_DECISION_NUMBER) " +
			" and (:P_DECISION_DATE_FLAG = '-1' OR  f.decisionDate = to_date(:P_DECISION_DATE, 'MI/MM/YYYY')) " +
			" and (f.executedFlag = 0) " +
			" order by f.requestDate")
})
@Entity
@Table(name = "HCM_VW_PAYROLL_INTEG_FAIL_LOG")
public class PayrollIntegrationFailureLogData extends BaseEntity {

    private Long id;
    private String requestURL;
    private String requestBody;
    private String response;
    private String tableName;
    private Date requestDate;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long adminDecisionId;
    private String adminDecisionName;
    private Long categoryId;
    private String categoryDesc;
    private Integer executedFlag;
    private Long rowId;

    private PayrollIntegrationFailureLog payrollIntegrationFailureLog;

    public PayrollIntegrationFailureLogData() {
	payrollIntegrationFailureLog = new PayrollIntegrationFailureLog();
    }

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
	this.payrollIntegrationFailureLog.setId(id);
    }

    @Basic
    @Column(name = "REQUEST_URL")
    public String getRequestURL() {
	return requestURL;
    }

    public void setRequestURL(String requestURL) {
	this.requestURL = requestURL;
	this.payrollIntegrationFailureLog.setRequestURL(requestURL);
    }

    @Column(name = "REQUEST_BODY")
    @Lob
    public String getRequestBody() {
	return requestBody;
    }

    public void setRequestBody(String requestBody) {
	this.requestBody = requestBody;
	this.payrollIntegrationFailureLog.setRequestBody(requestBody);
    }

    @Column(name = "RESPONSE")
    @Lob
    public String getResponse() {
	return response;
    }

    public void setResponse(String response) {
	this.response = response;
	this.payrollIntegrationFailureLog.setResponse(response);
    }

    @Basic
    @Column(name = "TABLE_NAME")
    public String getTableName() {
	return tableName;
    }

    public void setTableName(String tableName) {
	this.tableName = tableName;
	this.payrollIntegrationFailureLog.setTableName(tableName);
    }

    @Basic
    @Column(name = "REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRequestDate() {
	return requestDate;
    }

    public void setRequestDate(Date requestDate) {
	this.requestDate = requestDate;
	this.payrollIntegrationFailureLog.setRequestDate(requestDate);
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
	this.payrollIntegrationFailureLog.setDecisionNumber(decisionNumber);
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
	this.payrollIntegrationFailureLog.setDecisionDate(decisionDate);
    }

    @Transient
    @XmlTransient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Basic
    @Column(name = "ADMIN_DECISION_ID")
    public Long getAdminDecisionId() {
	return adminDecisionId;
    }

    public void setAdminDecisionId(Long adminDecisionId) {
	this.adminDecisionId = adminDecisionId;
	this.payrollIntegrationFailureLog.setAdminDecisionId(adminDecisionId);
    }

    @Basic
    @Column(name = "ADMIN_DECISION_NAME")
    public String getAdminDecisionName() {
	return adminDecisionName;
    }

    public void setAdminDecisionName(String adminDecisionName) {
	this.adminDecisionName = adminDecisionName;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	this.payrollIntegrationFailureLog.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "CATEGORY_DESC")
    public String getCategoryDesc() {
	return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
	this.categoryDesc = categoryDesc;
    }

    @Basic
    @Column(name = "EXECUTED_FLAG")
    public Integer getExecutedFlag() {
	return executedFlag;
    }

    public void setExecutedFlag(Integer executedFlag) {
	this.executedFlag = executedFlag;
	this.payrollIntegrationFailureLog.setExecutedFlag(executedFlag);
    }

    @Basic
    @Column(name = "ROW_ID")
    public Long getRowId() {
	return rowId;
    }

    public void setRowId(Long rowId) {
	this.rowId = rowId;
	this.payrollIntegrationFailureLog.setRowId(rowId);
    }

    @Transient
    @XmlTransient
    public PayrollIntegrationFailureLog getPayrollIntegrationFailureLog() {
	return payrollIntegrationFailureLog;
    }

    public void setPayrollIntegrationFailureLog(PayrollIntegrationFailureLog payrollIntegrationFailureLog) {
	this.payrollIntegrationFailureLog = payrollIntegrationFailureLog;
    }

}
