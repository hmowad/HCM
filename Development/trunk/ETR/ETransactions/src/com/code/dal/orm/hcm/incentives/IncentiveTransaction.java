package com.code.dal.orm.hcm.incentives;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "HCM_INC_TRANSCATIONS")
public class IncentiveTransaction extends BaseEntity {
    private Long id;
    private Long incentiveTypeId;
    private Long employeeId;
    private Date startDate;
    private Date endDate;
    private Integer Period;
    private Date transactionDate;
    private Double compensationAmount;
    private String financialLossReason;
    private Long portId;
    private Integer sessionsCount;
    private Long committieeCategoryId;
    private Integer destructionsCount;
    private Long missionTransactionId;

    @SequenceGenerator(name = "HCMIncTransactionsSeq",
	    sequenceName = "HCM_INC_TRANSACTIONS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMIncTransactionsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "INCENTIVE_TYPE_ID")
    public Long getIncentiveTypeId() {
	return incentiveTypeId;
    }

    public void setIncentiveTypeId(Long incentiveTypeId) {
	this.incentiveTypeId = incentiveTypeId;
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "START_DATE")
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    @Basic
    @Column(name = "END_DATE")
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return Period;
    }

    public void setPeriod(Integer period) {
	Period = period;
    }

    @Basic
    @Column(name = "TRANSACTION_DATE")
    public Date getTransactionDate() {
	return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
    }

    @Basic
    @Column(name = "COMPENSATION_AMOUNT")
    public Double getCompensationAmount() {
	return compensationAmount;
    }

    public void setCompensationAmount(Double compensationAmount) {
	this.compensationAmount = compensationAmount;
    }

    @Basic
    @Column(name = "FINANCIAL_LOSS_REASON")
    public String getFinancialLossReason() {
	return financialLossReason;
    }

    public void setFinancialLossReason(String financialLossReason) {
	this.financialLossReason = financialLossReason;
    }

    @Basic
    @Column(name = "PORT_ID")
    public Long getPortId() {
	return portId;
    }

    public void setPortId(Long portId) {
	this.portId = portId;
    }

    @Basic
    @Column(name = "SESSIONS_COUNT")
    public Integer getSessionsCount() {
	return sessionsCount;
    }

    public void setSessionsCount(Integer sessionsCount) {
	this.sessionsCount = sessionsCount;
    }

    @Basic
    @Column(name = "COMMITTEE_CATEGORY_ID")
    public Long getCommittieeCategoryId() {
	return committieeCategoryId;
    }

    public void setCommittieeCategoryId(Long committieeCategoryId) {
	this.committieeCategoryId = committieeCategoryId;
    }

    @Basic
    @Column(name = "DESTRUCTIONS_COUNT")
    public Integer getDestructionsCount() {
	return destructionsCount;
    }

    public void setDestructionsCount(Integer destructionsCount) {
	this.destructionsCount = destructionsCount;
    }

    @Basic
    @Column(name = "MSN_TRANSACTION_ID")
    public Long getMissionTransactionId() {
	return missionTransactionId;
    }

    public void setMissionTransactionId(Long missionTransactionId) {
	this.missionTransactionId = missionTransactionId;
    }

}
