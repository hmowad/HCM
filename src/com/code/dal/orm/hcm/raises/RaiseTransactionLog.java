package com.code.dal.orm.hcm.raises;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

@Entity
@Table(name = "HCM_RAISE_TRANSACTIONS_LOG")
public class RaiseTransactionLog extends AuditEntity implements InsertableAuditEntity {
    private Long id;
    private Long raiseTransactionId;
    private Long newDegreeId;
    private Date basedOnDecisionDate;
    private String basedOnDecisionDateString;
    private String basedOnDecisionNumber;
    private String transEmpRankDesc;
    private String transEmpDegreeDesc;
    private Long transEmpDegreeId;

    @GenericGenerator(name = "HCMRaiseSeq",
	    strategy = "enhanced-sequence",
	    parameters = {
		    @org.hibernate.annotations.Parameter(
			    name = "sequence_name",
			    value = "HCM_RAISE_SEQ"),
		    @org.hibernate.annotations.Parameter(
			    name = "optimizer",
			    value = "pooled-lo"),
		    @org.hibernate.annotations.Parameter(
			    name = "increment_size",
			    value = "30") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "HCMRaiseSeq")
    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "NEW_DEGREE_ID")
    public Long getNewDegreeId() {
	return newDegreeId;
    }

    public void setNewDegreeId(Long newDegreeId) {
	this.newDegreeId = newDegreeId;
    }

    @Basic
    @Column(name = "BASED_ON_DECISION_DATE")
    public Date getBasedOnDecisionDate() {
	return basedOnDecisionDate;
    }

    public void setBasedOnDecisionDate(Date basedOnDecisionDate) {
	this.basedOnDecisionDate = basedOnDecisionDate;
	this.basedOnDecisionDateString = HijriDateService.getHijriDateString(basedOnDecisionDate);
    }

    @Basic
    @Column(name = "BASED_ON_DECISION_NUMBER")
    public String getBasedOnDecisionNumber() {
	return basedOnDecisionNumber;
    }

    public void setBasedOnDecisionNumber(String basedOnDecisionNumber) {
	this.basedOnDecisionNumber = basedOnDecisionNumber;
    }

    @Basic
    @Column(name = "RAISE_TRANSACTION_ID")
    public Long getRaiseTransactionId() {
	return raiseTransactionId;
    }

    public void setRaiseTransactionId(Long raiseTransactionId) {
	this.raiseTransactionId = raiseTransactionId;
    }

    @Basic
    @Column(name = "TRANS_EMP_RANK_DESC")
    public String getTransEmpRankDesc() {
	return transEmpRankDesc;
    }

    public void setTransEmpRankDesc(String transEmpRankDesc) {
	this.transEmpRankDesc = transEmpRankDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_DEGREE_DESC")
    public String getTransEmpDegreeDesc() {
	return transEmpDegreeDesc;
    }

    public void setTransEmpDegreeDesc(String transEmpDegreeDesc) {
	this.transEmpDegreeDesc = transEmpDegreeDesc;
    }

    @Basic
    @Column(name = "TRANS_EMP_DEGREE_ID")
    public Long getTransEmpDegreeId() {
	return transEmpDegreeId;
    }

    public void setTransEmpDegreeId(Long transEmpDegreeId) {
	this.transEmpDegreeId = transEmpDegreeId;
    }

    @Transient
    public String getDecisionDateString() {
	return basedOnDecisionDateString;
    }

    public void setDecisionDateString(String basedOnDecisionDateString) {
	this.basedOnDecisionDateString = basedOnDecisionDateString;
	this.basedOnDecisionDate = HijriDateService.getHijriDate(basedOnDecisionDateString);
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "basedOnDecisionDate:" + basedOnDecisionDate + AUDIT_SEPARATOR +
		"basedOnDecisionNumber:" + basedOnDecisionNumber + AUDIT_SEPARATOR +
		"newDegreeId:" + newDegreeId + AUDIT_SEPARATOR +
		"transEmpRankDesc:" + transEmpRankDesc + AUDIT_SEPARATOR +
		"transEmpDegreeDesc:" + transEmpDegreeDesc + AUDIT_SEPARATOR;
    }

}
