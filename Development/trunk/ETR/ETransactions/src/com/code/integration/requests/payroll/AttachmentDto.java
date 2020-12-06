package com.code.integration.requests.payroll;

public class AttachmentDto {

    private String attachmentId;
    private String documentClass;
    private String filename;

    public String getAttachmentId() {
	return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
	this.attachmentId = attachmentId;
    }

    public String getDocumentClass() {
	return documentClass;
    }

    public void setDocumentClass(String documentClass) {
	this.documentClass = documentClass;
    }

    public String getFilename() {
	return filename;
    }

    public void setFilename(String filename) {
	this.filename = filename;
    }

}
