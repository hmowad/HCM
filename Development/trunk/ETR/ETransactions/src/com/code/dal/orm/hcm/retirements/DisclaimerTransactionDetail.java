package com.code.dal.orm.hcm.retirements;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;

@Entity
@Table(name = "HCM_RET_DISCLAIMER_TRN_DTLS")
public class DisclaimerTransactionDetail extends BaseEntity {

    private Long id;
    private Long disclaimerTransactionId;
    private Long managerId;
    private String transManagerUnitFullName;
    private String transManagerJobDesc;
    private String transManagerName;
    private String transManagerRankDesc;
    private Integer claimedFlag;

    @SequenceGenerator(name = "HCMRetirementsSeq",
	    sequenceName = "HCM_RET_SEQ")

    @Id
    @GeneratedValue(generator = "HCMRetirementsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "DISCLAIMER_TRANSACTION_ID")
    public Long getDisclaimerTransactionId() {
	return disclaimerTransactionId;
    }

    public void setDisclaimerTransactionId(Long disclaimerTransactionId) {
	this.disclaimerTransactionId = disclaimerTransactionId;
    }

    @Basic
    @Column(name = "MANAGER_ID")
    public Long getManagerId() {
	return managerId;
    }

    public void setManagerId(Long managerId) {
	this.managerId = managerId;
    }

    @Basic
    @Column(name = "TRANS_MANAGER_UNIT_FULL_NAME")
    public String getTransManagerUnitFullName() {
	return transManagerUnitFullName;
    }

    public void setTransManagerUnitFullName(String transManagerUnitFullName) {
	this.transManagerUnitFullName = transManagerUnitFullName;
    }

    @Basic
    @Column(name = "TRANS_MANAGER_JOB_DESC")
    public String getTransManagerJobDesc() {
	return transManagerJobDesc;
    }

    public void setTransManagerJobDesc(String transManagerJobDesc) {
	this.transManagerJobDesc = transManagerJobDesc;
    }

    @Basic
    @Column(name = "TRANS_MANAGER_NAME")
    public String getTransManagerName() {
	return transManagerName;
    }

    public void setTransManagerName(String transManagerName) {
	this.transManagerName = transManagerName;
    }

    @Basic
    @Column(name = "TRANS_MANAGER_RANK_DESC")
    public String getTransManagerRankDesc() {
	return transManagerRankDesc;
    }

    public void setTransManagerRankDesc(String transManagerRankDesc) {
	this.transManagerRankDesc = transManagerRankDesc;
    }

    @Basic
    @Column(name = "CLAIMED_FLAG")
    public Integer getClaimedFlag() {
	return claimedFlag;
    }

    public void setClaimedFlag(Integer claimedFlag) {
	if (claimedFlag == null)
	    claimedFlag = FlagsEnum.OFF.getCode();
	this.claimedFlag = claimedFlag;
    }

}
