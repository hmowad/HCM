package com.code.integration.responses.workflow;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.workflow.WFDelegationData;
import com.code.integration.responses.WSResponseBase;

public class WSWFDelegationsResponse extends WSResponseBase {

    private List<WFDelegationData> totalDelegations;
    private List<WFDelegationData> partialDelegations;

    @XmlElementWrapper(name = "totalDelegations")
    @XmlElement(name = "totalDelegation")
    public List<WFDelegationData> getTotalDelegations() {
	return totalDelegations;
    }

    public void setTotalDelegations(List<WFDelegationData> totalDelegations) {
	this.totalDelegations = totalDelegations;
    }

    @XmlElementWrapper(name = "partialDelegations")
    @XmlElement(name = "partialDelegation")
    public List<WFDelegationData> getPartialDelegations() {
	return partialDelegations;
    }

    public void setPartialDelegations(List<WFDelegationData> partialDelegations) {
	this.partialDelegations = partialDelegations;
    }

}
