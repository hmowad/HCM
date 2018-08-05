package com.code.integration.responses.workflow.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.integration.responses.WSResponseBase;

public class WSWFTrainingResponse extends WSResponseBase {

    private List<WFTrainingData> wfTrainingsData;

    @XmlElementWrapper(name = "wfTrainings")
    @XmlElement(name = "wfTraining")
    public List<WFTrainingData> getWfTrainingsData() {
	return wfTrainingsData;
    }

    public void setWfTrainingsData(List<WFTrainingData> wfTrainingsData) {
	this.wfTrainingsData = wfTrainingsData;
    }
}
