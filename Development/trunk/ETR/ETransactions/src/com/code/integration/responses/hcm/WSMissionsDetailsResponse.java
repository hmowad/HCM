package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.integration.responses.WSResponseBase;

public class WSMissionsDetailsResponse extends WSResponseBase {

    private List<MissionDetailData> missionsDetails;

    @XmlElementWrapper(name = "missionDetails")
    @XmlElement(name = "missionDetail")
    public List<MissionDetailData> getMissionsDetails() {
	return missionsDetails;
    }

    public void setMissionsDetails(List<MissionDetailData> missionsDetails) {
	this.missionsDetails = missionsDetails;
    }

}
