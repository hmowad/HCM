package com.code.dal.orm.hcm.attachments;

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
	@NamedQuery(name = "hcm_externalAttachment_getExternalAttachmentByTransactionIdAndTableName",
		query = " select t from ExternalAttachment t " +
			" where (:P_TRANSACTION_ID = -1 or t.transactionId = :P_TRANSACTION_ID) " +
			" and (:P_TABLE_NAME = '-1' or t.transactionTableName = :P_TABLE_NAME) ")
})

@Entity
@Table(name = "ETR_EXTERNAL_ATTACHMENT")
public class ExternalAttachment extends BaseEntity {

    private Long id;
    private Long transactionId;
    private String transactionTableName;
    private String attachmentId;
    private String documentClass;
    private String filename;

    @SequenceGenerator(name = "ETRExternalAttachmentsSeq",
	    sequenceName = "ETR_EXTERNAL_ATTACHMENTS_SEQ")
    @Id
    @GeneratedValue(generator = "ETRExternalAttachmentsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "TRANSACTION_ID")
    public Long getTransactionId() {
	return transactionId;
    }

    public void setTransactionId(Long transactionId) {
	this.transactionId = transactionId;
    }

    @Basic
    @Column(name = "TRANSACTION_TABLE_NAME")
    public String getTransactionTableName() {
	return transactionTableName;
    }

    public void setTransactionTableName(String transactionTableName) {
	this.transactionTableName = transactionTableName;
    }

    @Basic
    @Column(name = "ATTACHMENT_ID")
    public String getAttachmentId() {
	return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
	this.attachmentId = attachmentId;
    }

    @Basic
    @Column(name = "DOCUMENT_CLASS")
    public String getDocumentClass() {
	return documentClass;
    }

    public void setDocumentClass(String documentClass) {
	this.documentClass = documentClass;
    }

    @Basic
    @Column(name = "FILE_NAME")
    public String getFilename() {
	return filename;
    }

    public void setFilename(String filename) {
	this.filename = filename;
    }

}
