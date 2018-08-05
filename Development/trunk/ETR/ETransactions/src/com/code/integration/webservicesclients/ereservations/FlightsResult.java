
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for flightsResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="flightsResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="flightSearchResult" type="{http://webservice.services.code.com/}flightSearchResult" minOccurs="0"/>
 *         &lt;element name="flightSearchResultReturn" type="{http://webservice.services.code.com/}flightSearchResult" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://webservice.services.code.com/}eReservationWSStatusEnum" minOccurs="0"/>
 *         &lt;element name="waitingFlight" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "flightsResult", propOrder = {
    "flightSearchResult",
    "flightSearchResultReturn",
    "message",
    "status",
    "waitingFlight"
})
public class FlightsResult {

    protected FlightSearchResult flightSearchResult;
    protected FlightSearchResult flightSearchResultReturn;
    protected String message;
    @XmlSchemaType(name = "string")
    protected EReservationWSStatusEnum status;
    protected boolean waitingFlight;

    /**
     * Gets the value of the flightSearchResult property.
     * 
     * @return
     *     possible object is
     *     {@link FlightSearchResult }
     *     
     */
    public FlightSearchResult getFlightSearchResult() {
        return flightSearchResult;
    }

    /**
     * Sets the value of the flightSearchResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlightSearchResult }
     *     
     */
    public void setFlightSearchResult(FlightSearchResult value) {
        this.flightSearchResult = value;
    }

    /**
     * Gets the value of the flightSearchResultReturn property.
     * 
     * @return
     *     possible object is
     *     {@link FlightSearchResult }
     *     
     */
    public FlightSearchResult getFlightSearchResultReturn() {
        return flightSearchResultReturn;
    }

    /**
     * Sets the value of the flightSearchResultReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlightSearchResult }
     *     
     */
    public void setFlightSearchResultReturn(FlightSearchResult value) {
        this.flightSearchResultReturn = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link EReservationWSStatusEnum }
     *     
     */
    public EReservationWSStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link EReservationWSStatusEnum }
     *     
     */
    public void setStatus(EReservationWSStatusEnum value) {
        this.status = value;
    }

    /**
     * Gets the value of the waitingFlight property.
     * 
     */
    public boolean isWaitingFlight() {
        return waitingFlight;
    }

    /**
     * Sets the value of the waitingFlight property.
     * 
     */
    public void setWaitingFlight(boolean value) {
        this.waitingFlight = value;
    }

}
