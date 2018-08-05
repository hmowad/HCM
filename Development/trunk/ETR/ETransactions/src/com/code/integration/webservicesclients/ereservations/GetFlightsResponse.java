
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getFlightsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getFlightsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="availableFlightsResult" type="{http://webservice.services.code.com/}flightsResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFlightsResponse", propOrder = {
    "availableFlightsResult"
})
public class GetFlightsResponse {

    protected FlightsResult availableFlightsResult;

    /**
     * Gets the value of the availableFlightsResult property.
     * 
     * @return
     *     possible object is
     *     {@link FlightsResult }
     *     
     */
    public FlightsResult getAvailableFlightsResult() {
        return availableFlightsResult;
    }

    /**
     * Sets the value of the availableFlightsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlightsResult }
     *     
     */
    public void setAvailableFlightsResult(FlightsResult value) {
        this.availableFlightsResult = value;
    }

}
