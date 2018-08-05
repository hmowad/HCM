package com.code.integration.responses.workflow.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSWFTrainingsTasksResponse extends WSResponseBase {

    private List<WSWFTrainingTaskInfo> wfTrainingsTasksList;

    @XmlElementWrapper(name = "wfTrainingsTasks")
    @XmlElement(name = "wfTrainingTask")
    public List<WSWFTrainingTaskInfo> getWfTrainingsTasksList() {
	return wfTrainingsTasksList;
    }

    public void setWfTrainingsTasksList(List<WSWFTrainingTaskInfo> wfTrainingsTasksList) {
	this.wfTrainingsTasksList = wfTrainingsTasksList;
    }
}
