
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bookFlightResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bookFlightResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ticketsListResult" type="{http://webservice.services.code.com/}bookingsResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bookFlightResponse", propOrder = {
    "ticketsListResult"
})
public class BookFlightResponse {

    protected BookingsResult ticketsListResult;

    /**
     * Gets the value of the ticketsListResult property.
     * 
     * @return
     *     possible object is
     *     {@link BookingsResult }
     *     
     */
    public BookingsResult getTicketsListResult() {
        return ticketsListResult;
    }

    /**
     * Sets the value of the ticketsListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingsResult }
     *     
     */
    public void setTicketsListResult(BookingsResult value) {
        this.ticketsListResult = value;
    }

}
