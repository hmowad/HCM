package com.code.integration.responses.workflow;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.integration.responses.WSResponseBase;

public class WSProcessesGroupsResponse extends WSResponseBase {

    private List<WFProcessGroup> processesGroups;

    @XmlElementWrapper(name = "processesGroups")
    @XmlElement(name = "processesGroup")
    public List<WFProcessGroup> getProcessesGroups() {
	return processesGroups;
    }

    public void setProcessesGroups(List<WFProcessGroup> processesGroups) {
	this.processesGroups = processesGroups;
    }

}
