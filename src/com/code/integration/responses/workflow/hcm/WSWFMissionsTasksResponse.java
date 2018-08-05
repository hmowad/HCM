package com.code.integration.responses.workflow.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSWFMissionsTasksResponse extends WSResponseBase {
    private List<WSWFMissionTaskInfo> missions;

    @XmlElementWrapper(name = "wfMissionsTasks")
    @XmlElement(name = "wfMissionTask")
    public List<WSWFMissionTaskInfo> getMissions() {
	return missions;
    }

    public void setMissions(List<WSWFMissionTaskInfo> missions) {
	this.missions = missions;
    }

}
