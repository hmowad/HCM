package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.trainings.TrainingExternalPartyData;
import com.code.integration.responses.WSResponseBase;

public class WSExternalPartiesResponse extends WSResponseBase {

    private List<TrainingExternalPartyData> trainingExternalPartiesData;

    @XmlElementWrapper(name = "trainingExternalParties")
    @XmlElement(name = "trainingExternalParty")
    public List<TrainingExternalPartyData> getTrainingExternalPartiesData() {
	return trainingExternalPartiesData;
    }

    public void setTrainingExternalPartiesData(List<TrainingExternalPartyData> trainingExternalPartiesData) {
	this.trainingExternalPartiesData = trainingExternalPartiesData;
    }
}
