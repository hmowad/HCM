package com.code.dal.orm.hcm.attachments;

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

@NamedQueries({
	@NamedQuery(name = "etr_employeeFileAttachmentDetailData_searchEmployeeFileAttachmentDetails",
		query = " select a from EmployeeFileAttachmentDetailData a " +
			" where a.empId = :P_EMP_ID" +
			" and a.transactionClass = :P_TRANSACTION_CLASS" +
			" and (:P_DECISION_NUMBER = '-1' or a.decisionNumber = :P_DECISION_NUMBER)" +
			" and (:P_DESCRIPTION = '-1' or a.description like :P_DESCRIPTION )" +
			" and (:P_SKIP_FROM_DATE = -1 or a.decisionDate > to_date(:P_FROM_DATE, 'MI/MM/YYYY')) " +
			" and (:P_SKIP_TO_DATE = -1 or a.decisionDate < to_date(:P_TO_DATE, 'MI/MM/YYYY')) " +
			" order by a.decisionDate"),

	@NamedQuery(name = "etr_employeeFileAttachmentDetailData_countEmployeeFileAttachmentDetailsByAttachmentId",
		query = " select count(a.id) from EmployeeFileAttachmentDetailData a " +
			" where a.attachmentId = :P_ATTACHMENT_ID "),
})

@Entity
@Table(name = "ETR_VW_EMP_FILE_ATTACH_DTLS")
public class EmployeeFileAttachmentDetailData extends BaseEntity {

    private Long id;
    private Long empId;
    private Long attachmentId;
    private String contentId;
    private String fileName;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;
    private Integer transactionClass;
    private String description;
    private Date creationDate;
    private String creationDateString;
    private String creationTime;

    private EmployeeFileAttachmentDetail employeeFileAttachmentDetail;

    public EmployeeFileAttachmentDetailData() {
	employeeFileAttachmentDetail = new EmployeeFileAttachmentDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.employeeFileAttachmentDetail.setId(id);
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
    @Column(name = "ATTACHMENT_ID")
    public Long getAttachmentId() {
	return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
	this.attachmentId = attachmentId;
	this.employeeFileAttachmentDetail.setAttachmentId(attachmentId);
    }

    @Basic
    @Column(name = "CONTENT_ID")
    public String getContentId() {
	return contentId;
    }

    public void setContentId(String contentId) {
	this.contentId = contentId;
	this.employeeFileAttachmentDetail.setContentId(contentId);
    }

    @Basic
    @Column(name = "FILE_NAME")
    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
	this.employeeFileAttachmentDetail.setFileName(fileName);
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

    @Transient
    public EmployeeFileAttachmentDetail getEmployeeFileAttachmentDetail() {
	return employeeFileAttachmentDetail;
    }

    public void setEmployeeFileAttachmentDetail(EmployeeFileAttachmentDetail employeeFileAttachmentDetail) {
	this.employeeFileAttachmentDetail = employeeFileAttachmentDetail;
    }

}
