package com.code.integration.responses.workflow;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.workflow.WFTaskData;
import com.code.integration.responses.WSResponseBase;

public class WSWFTasksResponse extends WSResponseBase {

    private List<WFTaskData> wfTasks;

    @XmlElementWrapper(name = "wfTasks")
    @XmlElement(name = "wfTask")
    public List<WFTaskData> getWFTasks() {
	return wfTasks;
    }

    public void setWFTasks(List<WFTaskData> wfTasks) {
	this.wfTasks = wfTasks;
    }

}
