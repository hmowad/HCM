package com.code.integration.responses.hcm;

import javax.xml.bind.annotation.XmlElement;

import com.code.dal.orm.integration.finance.PaidIssueOrderData;
import com.code.integration.responses.WSResponseBase;

public class WSPaidIssueOrderResponse extends WSResponseBase {

    private PaidIssueOrderData paidIssueOrder;

    @XmlElement(name = "paidIssueOrder",
	    nillable = true)
    public PaidIssueOrderData getPaidIssueOrder() {
	return paidIssueOrder;
    }

    public void setPaidIssueOrder(PaidIssueOrderData paidIssueOrder) {
	this.paidIssueOrder = paidIssueOrder;
    }

}
