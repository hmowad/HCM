package com.code.integration.responses.workflow;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.workflow.WFProcess;
import com.code.integration.responses.WSResponseBase;

public class WSProcessesResponse extends WSResponseBase {

    private List<WFProcess> processes;

    @XmlElementWrapper(name = "processes")
    @XmlElement(name = "process")
    public List<WFProcess> getProcesses() {
	return processes;
    }

    public void setProcesses(List<WFProcess> processes) {
	this.processes = processes;
    }

}
