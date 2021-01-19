package com.code.dal.orm.hcm.attachments;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "etr_employeeFileAttachment_getEmployeeFileAttachmentById",
		query = " select a from EmployeeFileAttachment a " +
			" where a.id = :P_ATTACHMENT_ID"),

	@NamedQuery(name = "etr_employeeFileAttachment_countEmployeeFileAttachmentsByDecisionNumber",
		query = " select count(a.id) from EmployeeFileAttachment a " +
			" where a.decisionNumber = :P_DECISION_NUMBER " +
			" and a.transactionClass = :P_TRANSACTION_CLASS"),
})

@Entity
@Table(name = "ETR_EMP_FILE_ATTACHMENT")
public class EmployeeFileAttachment extends AuditEntity implements InsertableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long empId;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Integer transactionClass;
    private String description;
    private Date creationDate;
    private String creationDateString;
    private String creationTime;

    @SequenceGenerator(name = "EtrEmpFileAttachmentSeq",
	    sequenceName = "ETR_EMP_FILE_ATTACHMENT_SEQ")
    @Id
    @GeneratedValue(generator = "EtrEmpFileAttachmentSeq")
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
    @Column(name = "TRANSACTION_CLASS")
    public Integer getTransactionClass() {
	return transactionClass;
    }

    public void setTransactionClass(Integer transactionClass) {
	this.transactionClass = transactionClass;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Basic
    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(Date creationDate) {
	this.creationDate = creationDate;
	this.creationDateString = HijriDateService.getHijriDateString(creationDate);
    }

    @Transient
    public String getCreationDateString() {
	return creationDateString;
    }

    public void setCreationDateString(String creationDateString) {
	this.creationDateString = creationDateString;
	this.creationDate = HijriDateService.getHijriDate(creationDateString);
    }

    @Basic
    @Column(name = "CREATION_TIME")
    public String getCreationTime() {
	return creationTime;
    }

    public void setCreationTime(String creationTime) {
	this.creationTime = creationTime;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "id:" + id + AUDIT_SEPARATOR +
		"empId:" + empId + AUDIT_SEPARATOR +
		"decisionNumber:" + decisionNumber + AUDIT_SEPARATOR +
		"decisionDate:" + decisionDate;
    }

}
