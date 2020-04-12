package com.code.dal.orm.hcm.dutyextension;

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

import com.code.dal.orm.BaseEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_DUTY_EXTNSION_TRANSACTIONS")
public class DutyExtensionTransaction extends BaseEntity {

    private Long id;
    private Long empId;
    private Integer serviceTerminationReasonId;
    private Integer extensionPeriodMonths;
    private String extensionReason;
    private Integer transactionType;
    private Long basedOnExtensionId;
    private Date transactionDate;

    @SequenceGenerator(name = "HCMDutyExtensionTransactionsSeq",
	    sequenceName = "HCM_DUTY_EXTENSION_SEQ")
    @Id
    @GeneratedValue(generator = "HCMDutyExtensionTransactionsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "STE_REASON_ID")
    public Integer getServiceTerminationReasonId() {
	return serviceTerminationReasonId;
    }

    public void setServiceTerminationReasonId(Integer serviceTerminationReasonId) {
	this.serviceTerminationReasonId = serviceTerminationReasonId;
    }

    @Basic
    @Column(name = "EXTENSION_PERIOD_MONTHS")
    public Integer getExtensionPeriodMonths() {
	return extensionPeriodMonths;
    }

    public void setExtensionPeriodMonths(Integer extensionPeriodMonths) {
	this.extensionPeriodMonths = extensionPeriodMonths;
    }

    @Basic
    @Column(name = "EXTENSION_REASON")
    public String getExtensionReason() {
	return extensionReason;
    }

    public void setExtensionReason(String extensionReason) {
	this.extensionReason = extensionReason;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE")
    public Integer getTransactionType() {
	return transactionType;
    }

    public void setTransactionType(Integer transactionType) {
	this.transactionType = transactionType;
    }

    @Basic
    @Column(name = "BASED_ON_EXTENSION_ID")
    public Long getBasedOnExtensionId() {
	return basedOnExtensionId;
    }

    public void setBasedOnExtensionId(Long basedOnExtensionId) {
	this.basedOnExtensionId = basedOnExtensionId;
    }

    @Basic
    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransactionDate() {
	return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
    }

}
