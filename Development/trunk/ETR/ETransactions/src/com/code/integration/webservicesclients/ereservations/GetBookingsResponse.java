
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getBookingsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBookingsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bookingsListResult" type="{http://webservice.services.code.com/}bookingsResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBookingsResponse", propOrder = {
    "bookingsListResult"
})
public class GetBookingsResponse {

    protected BookingsResult bookingsListResult;

    /**
     * Gets the value of the bookingsListResult property.
     * 
     * @return
     *     possible object is
     *     {@link BookingsResult }
     *     
     */
    public BookingsResult getBookingsListResult() {
        return bookingsListResult;
    }

    /**
     * Sets the value of the bookingsListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingsResult }
     *     
     */
    public void setBookingsListResult(BookingsResult value) {
        this.bookingsListResult = value;
    }

}
