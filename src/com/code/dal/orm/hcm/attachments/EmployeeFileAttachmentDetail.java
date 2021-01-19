package com.code.dal.orm.hcm.attachments;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;

@Entity
@Table(name = "ETR_EMP_FILE_ATTACH_DETAILS")
public class EmployeeFileAttachmentDetail extends AuditEntity implements InsertableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long attachmentId;
    private String contentId;
    private String fileName;

    @SequenceGenerator(name = "EtrEmpFileAttachDetailSeq",
	    sequenceName = "ETR_EMP_FILE_ATTACH_DETAIL_SEQ")
    @Id
    @GeneratedValue(generator = "EtrEmpFileAttachDetailSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "ATTACHMENT_ID")
    public Long getAttachmentId() {
	return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
	this.attachmentId = attachmentId;
    }

    @Basic
    @Column(name = "CONTENT_ID")
    public String getContentId() {
	return contentId;
    }

    public void setContentId(String contentId) {
	this.contentId = contentId;
    }

    @Basic
    @Column(name = "FILE_NAME")
    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "id:" + id + AUDIT_SEPARATOR +
		"attachmentId" + attachmentId + AUDIT_SEPARATOR +
		"contentId" + contentId + AUDIT_SEPARATOR +
		"fileName:" + fileName;
    }
}
