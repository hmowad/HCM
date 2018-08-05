package com.code.integration.responses.ereservations;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSBookingsResponse extends WSResponseBase {

    private List<BookingData> bookings;

    @XmlElementWrapper(name = "bookings")
    @XmlElement(name = "booking")
    public List<BookingData> getBookings() {
	return bookings;
    }

    public void setBookings(List<BookingData> bookings) {
	this.bookings = bookings;
    }
}
