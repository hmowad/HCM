package com.code.integration.responses.ereservations;

import java.io.Serializable;

public class BookingData implements Serializable {

    private String flightCode;
    private String flightDateString;
    private String departureTime;
    private String fromAirportName;
    private String toAirportName;
    private String bookingStatusDescription;
    private String bookingReference;

    public String getFlightCode() {
	return flightCode;
    }

    public void setFlightCode(String flightCode) {
	this.flightCode = flightCode;
    }

    public String getFlightDateString() {
	return flightDateString;
    }

    public void setFlightDateString(String flightDateString) {
	this.flightDateString = flightDateString;
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

    public String getBookingStatusDescription() {
	return bookingStatusDescription;
    }

    public void setBookingStatusDescription(String bookingStatusDescription) {
	this.bookingStatusDescription = bookingStatusDescription;
    }

    public String getBookingReference() {
	return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
	this.bookingReference = bookingReference;
    }
}
