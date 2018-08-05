package com.code.integration.responses.workflow.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSWFVacationsTasksResponse extends WSResponseBase {

    private List<WSWFVacationTaskInfo> wfVacationsTasksList;

    @XmlElementWrapper(name = "wfVacationsTasks")
    @XmlElement(name = "wfVacationTask")
    public List<WSWFVacationTaskInfo> getWfVacationsTasksList() {
	return wfVacationsTasksList;
    }

    public void setWfVacationsTasksList(List<WSWFVacationTaskInfo> wfVacationsTasksList) {
	this.wfVacationsTasksList = wfVacationsTasksList;
    }
}
