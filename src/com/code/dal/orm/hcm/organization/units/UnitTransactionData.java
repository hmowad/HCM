package com.code.dal.orm.hcm.organization.units;

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

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>UnitTransactionData</code> class represents the transactions that have been occurred on the units (like creation, renaming, cancellation and etc...) in detailed format.
 * 
 */
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_unitTransactionData_getUnitTransactions",
		query = " select t from UnitTransactionData t " +
			" where (:P_UNIT_ID = -1 or t.unitId = :P_UNIT_ID)" +
			" order by t.id desc "
	)
})
@Entity
@Table(name = "HCM_VW_ORG_UNITS_TRANSACTIONS")
public class UnitTransactionData extends BaseEntity implements Serializable {
    private Long id;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long transactionTypeId;
    private String transactionTypeDesc;
    private Long unitId;
    private Long unitTypeId;
    private String unitTypeDesc;
    private String name;
    private String fullName;
    private Long movedToUnitId;
    private String movedToUnitFullName;
    private Long mergedWithUnitID;
    private String mergedWithUnitFullName;
    private Long transactionUserId;
    private String transactionUserFullName;
    private Date transactionDate;
    private String transactionDateString;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_ID")
    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
    }

    @Basic
    @Column(name = "TRANSACTION_TYPE_DESC")
    public String getTransactionTypeDesc() {
	return transactionTypeDesc;
    }

    public void setTransactionTypeDesc(String transactionTypeDesc) {
	this.transactionTypeDesc = transactionTypeDesc;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "UNIT_TYPE_ID")
    public Long getUnitTypeId() {
	return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
	this.unitTypeId = unitTypeId;
    }

    @Basic
    @Column(name = "UNIT_TYPE_DESC")
    public String getUnitTypeDesc() {
	return unitTypeDesc;
    }

    public void setUnitTypeDesc(String unitTypeDesc) {
	this.unitTypeDesc = unitTypeDesc;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "FULL_NAME")
    public String getFullName() {
	return fullName;
    }

    public void setFullName(String fullName) {
	this.fullName = fullName;
    }

    @Basic
    @Column(name = "MOVED_TO_UNIT_ID")
    public Long getMovedToUnitId() {
	return movedToUnitId;
    }

    public void setMovedToUnitId(Long movedToUnitId) {
	this.movedToUnitId = movedToUnitId;
    }

    @Basic
    @Column(name = "MOVED_TO_UNIT_FULL_NAME")
    public String getMovedToUnitFullName() {
	return movedToUnitFullName;
    }

    public void setMovedToUnitFullName(String movedToUnitFullName) {
	this.movedToUnitFullName = movedToUnitFullName;
    }

    @Basic
    @Column(name = "MERGED_WITH_UNIT_ID")
    public Long getMergedWithUnitID() {
	return mergedWithUnitID;
    }

    public void setMergedWithUnitID(Long mergedWithUnitID) {
	this.mergedWithUnitID = mergedWithUnitID;
    }

    @Basic
    @Column(name = "MERGED_WITH_UNIT_FULL_NAME")
    public String getMergedWithUnitFullName() {
	return mergedWithUnitFullName;
    }

    public void setMergedWithUnitFullName(String mergedWithUnitFullName) {
	this.mergedWithUnitFullName = mergedWithUnitFullName;
    }

    @Basic
    @Column(name = "TRANSACTION_USER_ID")
    public Long getTransactionUserId() {
	return transactionUserId;
    }

    public void setTransactionUserId(Long transactionUserId) {
	this.transactionUserId = transactionUserId;
    }

    @Basic
    @Column(name = "TRANSACTION_USER_FULL_NAME")
    public String getTransactionUserFullName() {
	return transactionUserFullName;
    }

    public void setTransactionUserFullName(String transactionUserFullName) {
	this.transactionUserFullName = transactionUserFullName;
    }

    @Basic
    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransactionDate() {
	return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
	this.transactionDateString = HijriDateService.getHijriDateString(transactionDate);
    }

    @Transient
    public String getTransactionDateString() {
	return transactionDateString;
    }

    public void setTransactionDateString(String transactionDateString) {
	this.transactionDateString = transactionDateString;
	this.transactionDate = HijriDateService.getHijriDate(transactionDateString);
    }
}
