
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAirportsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAirportsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="airportsResult" type="{http://webservice.services.code.com/}airportsResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAirportsResponse", propOrder = {
    "airportsResult"
})
public class GetAirportsResponse {

    protected AirportsResult airportsResult;

    /**
     * Gets the value of the airportsResult property.
     * 
     * @return
     *     possible object is
     *     {@link AirportsResult }
     *     
     */
    public AirportsResult getAirportsResult() {
        return airportsResult;
    }

    /**
     * Sets the value of the airportsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link AirportsResult }
     *     
     */
    public void setAirportsResult(AirportsResult value) {
        this.airportsResult = value;
    }

}
