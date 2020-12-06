package com.code.integration.requests.eservices;

import java.util.List;

import com.code.dal.orm.hcm.attachments.ExternalAttachment;
import com.code.dal.orm.hcm.incentives.IncentiveTransaction;

public class IncentiveRequest {

    private IncentiveTransaction incentiveTransaction;
    private List<ExternalAttachment> attachmentList;

    public IncentiveTransaction getIncentiveTransaction() {
	return incentiveTransaction;
    }

    public void setIncentiveTransaction(IncentiveTransaction incentiveTransaction) {
	this.incentiveTransaction = incentiveTransaction;
    }

    public List<ExternalAttachment> getAttachmentList() {
	return attachmentList;
    }

    public void setAttachmentList(List<ExternalAttachment> attachmentList) {
	this.attachmentList = attachmentList;
    }

}
