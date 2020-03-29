package com.code.dal.orm.hcm.dutyextension;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_dutyExtensionTransaction_searchDutyExtensionTransactions",
		query = " select t from DutyExtensionTransaction t " +
			" where (:P_ID = -1 or t.id = :P_ID) " +
			" and (:P_EMP_ID = -1 or t.empId = :P_EMP_ID) " +
			" and (:P_SERVICE_TERMINATION_REASON = -1 or t.serviceTerminationReasonId = :P_SERVICE_TERMINATION_REASON) " +
			" and (:P_TRANSACTION_TYPE = '-1' or t.transactionType = :P_TRANSACTION_TYPE) " +
			" order by t.id ")
})

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

}
