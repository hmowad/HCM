package com.code.integration.responses.ereservations;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSFlightsResponse extends WSResponseBase {

    private List<LegData> goingLegs;
    private List<LegData> returnLegs;
    private boolean waitingFlightFlag;

    @XmlElementWrapper(name = "goingLegs")
    @XmlElement(name = "leg")
    public List<LegData> getGoingLegs() {
	return goingLegs;
    }

    public void setGoingLegs(List<LegData> goingLegs) {
	this.goingLegs = goingLegs;
    }

    @XmlElementWrapper(name = "returnLegs")
    @XmlElement(name = "leg")
    public List<LegData> getReturnLegs() {
	return returnLegs;
    }

    public void setReturnLegs(List<LegData> returnLegs) {
	this.returnLegs = returnLegs;
    }

    public boolean getWaitingFlightFlag() {
	return waitingFlightFlag;
    }

    public void setWaitingFlightFlag(boolean waitingFlightFlag) {
	this.waitingFlightFlag = waitingFlightFlag;
    }

}
