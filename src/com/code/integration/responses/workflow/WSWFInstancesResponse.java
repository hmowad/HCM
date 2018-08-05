package com.code.integration.responses.workflow;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.workflow.WFInstanceData;
import com.code.integration.responses.WSResponseBase;

public class WSWFInstancesResponse extends WSResponseBase {

    private List<WFInstanceData> wfInstances;

    @XmlElementWrapper(name = "wfInstances")
    @XmlElement(name = "wfInstance")
    public List<WFInstanceData> getWFInstances() {
	return wfInstances;
    }

    public void setWFInstances(List<WFInstanceData> wfInstances) {
	this.wfInstances = wfInstances;
    }

}
