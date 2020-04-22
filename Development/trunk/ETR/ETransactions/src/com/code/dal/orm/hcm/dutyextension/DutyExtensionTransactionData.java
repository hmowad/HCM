package com.code.dal.orm.hcm.dutyextension;

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
import javax.xml.bind.annotation.XmlTransient;

@NamedQueries({
	@NamedQuery(name = "hcm_dutyExtensionTransactionData_searchDutyExtensionDataTransactions",
		query = " select t from DutyExtensionTransactionData t " +
			" where (:P_ID = -1 or t.id = :P_ID) " +
			" and (:P_EMP_ID = -1 or t.empId = :P_EMP_ID) " +
			" and (:P_SERVICE_TERMINATION_REASON = -1 or t.serviceTerminationReasonId = :P_SERVICE_TERMINATION_REASON) " +
			" and (:P_TRANSACTION_TYPE = '-1' or t.transactionType = :P_TRANSACTION_TYPE) " +
			" order by t.transactionDate desc ")
})

@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_DUTY_EXTN_TRANSACTIONS")
public class DutyExtensionTransactionData {

    private Long id;
    private Long empId;
    private Integer serviceTerminationReasonId;
    private String serviceTerminationReasonDesc;
    private Integer extensionPeriodMonths;
    private String extensionReason;
    private Integer transactionType;
    private Long basedOnExtensionId;
    private Date transactionDate;
    private transient DutyExtensionTransaction dutyExtensionTransaction;

    public DutyExtensionTransactionData() {
	dutyExtensionTransaction = new DutyExtensionTransaction();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.dutyExtensionTransaction.setId(id);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	this.dutyExtensionTransaction.setEmpId(empId);
    }

    @Basic
    @Column(name = "STE_REASON_ID")
    public Integer getServiceTerminationReasonId() {
	return serviceTerminationReasonId;
    }

    public void setServiceTerminationReasonId(Integer serviceTerminationReasonId) {
	this.serviceTerminationReasonId = serviceTerminationReasonId;
	this.dutyExtensionTransaction.setServiceTerminationReasonId(serviceTerminationReasonId);
    }

    @Basic
    @Column(name = "STE_REASON_DESC")
    public String getServiceTerminationReasonDesc() {
	return serviceTerminationReasonDesc;
    }

    public void setServiceTerminationReasonDesc(String serviceTerminationReasonDesc) {
	this.serviceTerminationReasonDesc = serviceTerminationReasonDesc;
    }

    @Basic
    @Column(name = "EXTENSION_PERIOD_MONTHS")
    public Integer getExtensionPeriodMonths() {
	return extensionPeriodMonths;
    }

    public void setExtensionPeriodMonths(Integer extensionPeriodMonths) {
	this.extensionPeriodMonths = extensionPeriodMonths;
	this.dutyExtensionTransaction.setExtensionPeriodMonths(extensionPeriodMonths);
    }

    @Basic
    @Column(name = "EXTENSION_REASON")
    public String getExtensionReason() {
	return extensionReason;
    }

    public void setExtensionReason(String extensionReason) {
	this.extensionReason = extensionReason;
	this.dutyExtensionTransaction.setExtensionReason(extensionReason);
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE")
    public Integer getTransactionType() {
	return transactionType;
    }

    public void setTransactionType(Integer transactionType) {
	this.transactionType = transactionType;
	this.dutyExtensionTransaction.setTransactionType(transactionType);
    }

    @Basic
    @Column(name = "BASED_ON_EXTENSION_ID")
    public Long getBasedOnExtensionId() {
	return basedOnExtensionId;
    }

    public void setBasedOnExtensionId(Long basedOnExtensionId) {
	this.basedOnExtensionId = basedOnExtensionId;
	this.dutyExtensionTransaction.setBasedOnExtensionId(basedOnExtensionId);
    }

    @Basic
    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransactionDate() {
	return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
	this.dutyExtensionTransaction.setTransactionDate(transactionDate);
    }

    @Transient
    @XmlTransient
    public DutyExtensionTransaction getDutyExtensionTransaction() {
	return dutyExtensionTransaction;
    }

    public void setDutyExtensionTransaction(DutyExtensionTransaction dutyExtensionTransaction) {
	this.dutyExtensionTransaction = dutyExtensionTransaction;
    }

}
