package com.code.integration.requests.eservices;

import java.util.List;

import com.code.dal.orm.hcm.attachments.ExternalAttachment;
import com.code.dal.orm.hcm.dutyextension.DutyExtensionTransaction;

public class ExtentionRequest {

    private DutyExtensionTransaction dutyExtensionTransaction;
    private List<ExternalAttachment> attachmentList;

    public DutyExtensionTransaction getDutyExtensionTransaction() {
	return dutyExtensionTransaction;
    }

    public void setDutyExtensionTransaction(DutyExtensionTransaction dutyExtensionTransaction) {
	this.dutyExtensionTransaction = dutyExtensionTransaction;
    }

    public List<ExternalAttachment> getAttachmentList() {
	return attachmentList;
    }

    public void setAttachmentList(List<ExternalAttachment> attachmentList) {
	this.attachmentList = attachmentList;
    }

}
