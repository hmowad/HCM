package com.code.dal.orm.audit;

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

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "etr_auditLog_getContentEntities",
		query = " select distinct a.contentEntity, substr(a.contentEntity, instr(a.contentEntity, '.', -1) + 1) from AuditLog a" +
			" order by substr(a.contentEntity, instr(a.contentEntity, '.', -1) + 1) "),

	@NamedQuery(name = "etr_auditLog_getAuditLogs",
		query = " select a from AuditLog a" +
			" where (:P_CONTENT_ENTITY = '-1' or a.contentEntity = :P_CONTENT_ENTITY) " +
			" and (:P_CONTENT_ID = -1 or a.contentId = :P_CONTENT_ID)" +
			" and (:P_OPERATION = '-1' or a.operation = :P_OPERATION)" +
			" and (:P_SYSTEM_USER= -1 or a.systemUser = :P_SYSTEM_USER)" +
			" and (:P_FROM_DATE_FLAG = -1 or TO_DATE(a.operationDate , 'DD/MM/YYYY') >= TO_DATE(:P_FROM_DATE, 'DD/MM/YYYY'))" +
			" and (:P_TO_DATE_FLAG = -1 or TO_DATE(a.operationDate , 'DD/MM/YYYY') <= TO_DATE(:P_TO_DATE, 'DD/MM/YYYY'))" +
			" and (:P_CONTENT = '-1' or a.content like :P_CONTENT)" +
			" order by a.contentEntity, a.operationDate, a.contentId ")
})
@Entity
@Table(name = "ETR_AUDITS")
public class AuditLog extends BaseEntity {

    private Long id;
    private Long systemUser;
    private String operation;
    private Date operationDate;
    private String contentEntity;
    private Long contentId;
    private String content;

    @SequenceGenerator(name = "EtrAuditsSeq",
	    sequenceName = "ETR_AUDITS_SEQ")
    @GeneratedValue(generator = "EtrAuditsSeq")
    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long auditLogId) {
	this.id = auditLogId;
    }

    @Basic
    @Column(name = "SYSTEM_USER")
    public Long getSystemUser() {
	return systemUser;
    }

    public void setSystemUser(Long systemUser) {
	this.systemUser = systemUser;
    }

    @Basic
    @Column(name = "OPERATION")
    public String getOperation() {
	return operation;
    }

    public void setOperation(String operation) {
	this.operation = operation;
    }

    @Basic
    @Column(name = "OPERATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOperationDate() {
	return operationDate;
    }

    public void setOperationDate(Date operationDate) {
	this.operationDate = operationDate;
    }

    @Basic
    @Column(name = "CONTENT_ENTITY")
    public String getContentEntity() {
	return contentEntity;
    }

    public void setContentEntity(String contentEntity) {
	this.contentEntity = contentEntity;
    }

    @Basic
    @Column(name = "CONTENT_ID")
    public Long getContentId() {
	return contentId;
    }

    public void setContentId(Long contentId) {
	this.contentId = contentId;
    }

    @Basic
    @Column(name = "CONTENT")
    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

}
