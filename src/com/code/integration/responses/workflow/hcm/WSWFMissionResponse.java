package com.code.integration.responses.workflow.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.workflow.hcm.missions.WFMissionData;
import com.code.dal.orm.workflow.hcm.missions.WFMissionDetailData;
import com.code.integration.responses.WSResponseBase;

public class WSWFMissionResponse extends WSResponseBase {

    private WFMissionData wfMission;
    private List<WFMissionDetailData> wfMissionDetails;

    @XmlElement(name = "wfMission")
    public WFMissionData getWfMission() {
	return wfMission;
    }

    public void setWfMission(WFMissionData wfMission) {
	this.wfMission = wfMission;
    }

    @XmlElementWrapper(name = "wfMissionDetails")
    @XmlElement(name = "wfMissionDetail")
    public List<WFMissionDetailData> getWfMissionDetails() {
	return wfMissionDetails;
    }

    public void setWfMissionDetails(List<WFMissionDetailData> wfMissionDetails) {
	this.wfMissionDetails = wfMissionDetails;
    }

}
