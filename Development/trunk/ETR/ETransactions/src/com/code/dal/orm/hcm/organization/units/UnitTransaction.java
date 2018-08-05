package com.code.dal.orm.hcm.organization.units;

import java.io.Serializable;
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
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>UnitTransaction</code> class represents the transactions that have been occurred on the units (like creation, renaming, cancellation and etc...).
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_ORG_UNITS_TRANSACTIONS")
public class UnitTransaction extends BaseEntity implements Serializable {
    private Long id;
    private Long unitId;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Long transactionTypeId;
    private Long moveToUnitId;
    private String moveToUnitFullName;
    private Long mergedWithUnitId;
    private String mergedWithUnitFullName;
    // private String code;
    private Long unitTypeId;
    private String name;
    private String fullName;
    private Long parentUnitId;
    private String hKey;
    private Long regionId;
    private String Remarks;
    private Long officialManagerId;
    private Long physicalManagerId;
    private Integer orderUnitParent;
    private Long transactionUserId;
    private Date transactionDate;

    @SequenceGenerator(name = "HCMUnitSeq", sequenceName = "HCM_ORG_UNITS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMUnitSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "MOVED_TO_UNIT_ID")
    public Long getMoveToUnitId() {
	return moveToUnitId;
    }

    public void setMoveToUnitId(Long moveToUnitId) {
	this.moveToUnitId = moveToUnitId;
    }

    @Basic
    @Column(name = "MOVED_TO_UNIT_FULL_NAME")
    public String getMoveToUnitFullName() {
	return moveToUnitFullName;
    }

    public void setMoveToUnitFullName(String moveToUnitFullName) {
	this.moveToUnitFullName = moveToUnitFullName;
    }

    @Basic
    @Column(name = "MERGED_WITH_UNIT_ID")
    public Long getMergedWithUnitId() {
	return mergedWithUnitId;
    }

    public void setMergedWithUnitId(Long mergedWithUnitId) {
	this.mergedWithUnitId = mergedWithUnitId;
    }

    @Basic
    @Column(name = "MERGED_WITH_UNIT_FULL_NAME")
    public String getMergedWithUnitFullName() {
	return mergedWithUnitFullName;
    }

    public void setMergedWithUnitFullName(String mergedWithUnitFullName) {
	this.mergedWithUnitFullName = mergedWithUnitFullName;
    }

    // @Basic
    // @Column(name = "CODE")
    // public String getCode() {
    // return code;
    // }
    //
    // public void setCode(String code) {
    // this.code = code;
    // }

    @Basic
    @Column(name = "UNIT_TYPE_ID")
    public Long getUnitTypeId() {
	return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
	this.unitTypeId = unitTypeId;
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
    @Column(name = "PARENT_UNIT_ID")
    public Long getParentUnitId() {
	return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
	this.parentUnitId = parentUnitId;
    }

    @Basic
    @Column(name = "HKEY")
    public String gethKey() {
	return hKey;
    }

    public void sethKey(String hKey) {
	this.hKey = hKey;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return Remarks;
    }

    public void setRemarks(String remarks) {
	Remarks = remarks;
    }

    @Basic
    @Column(name = "OFFICIAL_MANAGER_ID")
    public Long getOfficialManagerId() {
	return officialManagerId;
    }

    public void setOfficialManagerId(Long officialManagerId) {
	this.officialManagerId = officialManagerId;
    }

    @Basic
    @Column(name = "PHYSICAL_MANAGER_ID")
    public Long getPhysicalManagerId() {
	return physicalManagerId;
    }

    public void setPhysicalManagerId(Long physicalManagerId) {
	this.physicalManagerId = physicalManagerId;
    }

    @Basic
    @Column(name = "ORDER_UNDER_PARENT")
    public Integer getOrderUnitParent() {
	return orderUnitParent;
    }

    public void setOrderUnitParent(Integer orderUnitParent) {
	this.orderUnitParent = orderUnitParent;
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
    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransactionDate() {
	return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
    }
}