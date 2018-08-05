package com.code.integration.responses.ereservations;

import java.io.Serializable;

public class LegData implements Serializable {

    private Long id;
    private String departureDateString;
    private String departureTime;
    private String fromAirportName;
    private String toAirportName;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getDepartureDateString() {
	return departureDateString;
    }

    public void setDepartureDateString(String departureDateString) {
	this.departureDateString = departureDateString;
    }

    public String getDepartureTime() {
	return departureTime;
    }

    public void setDepartureTime(String departureTime) {
	this.departureTime = departureTime;
    }

    public String getFromAirportName() {
	return fromAirportName;
    }

    public void setFromAirportName(String fromAirportName) {
	this.fromAirportName = fromAirportName;
    }

    public String getToAirportName() {
	return toAirportName;
    }

    public void setToAirportName(String toAirportName) {
	this.toAirportName = toAirportName;
    }

}
