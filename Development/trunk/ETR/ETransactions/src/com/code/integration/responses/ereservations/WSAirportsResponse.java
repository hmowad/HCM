package com.code.integration.responses.ereservations;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSAirportsResponse extends WSResponseBase {

    private List<AirportData> airports;

    @XmlElementWrapper(name = "airports")
    @XmlElement(name = "airport")
    public List<AirportData> getAirports() {
	return airports;
    }

    public void setAirports(List<AirportData> airports) {
	this.airports = airports;
    }
}
