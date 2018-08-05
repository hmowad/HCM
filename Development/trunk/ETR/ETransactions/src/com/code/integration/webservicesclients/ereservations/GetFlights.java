
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getFlights complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getFlights">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="socialId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fromAirportId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="toAirportId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="departureDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="returnDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFlights", propOrder = {
    "socialId",
    "fromAirportId",
    "toAirportId",
    "departureDate",
    "returnDate"
})
public class GetFlights {

    @XmlElement(required = true)
    protected String socialId;
    protected long fromAirportId;
    protected long toAirportId;
    @XmlElement(required = true)
    protected String departureDate;
    @XmlElementRef(name = "returnDate", type = JAXBElement.class, required = false)
    protected JAXBElement<String> returnDate;

    /**
     * Gets the value of the socialId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocialId() {
        return socialId;
    }

    /**
     * Sets the value of the socialId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocialId(String value) {
        this.socialId = value;
    }

    /**
     * Gets the value of the fromAirportId property.
     * 
     */
    public long getFromAirportId() {
        return fromAirportId;
    }

    /**
     * Sets the value of the fromAirportId property.
     * 
     */
    public void setFromAirportId(long value) {
        this.fromAirportId = value;
    }

    /**
     * Gets the value of the toAirportId property.
     * 
     */
    public long getToAirportId() {
        return toAirportId;
    }

    /**
     * Sets the value of the toAirportId property.
     * 
     */
    public void setToAirportId(long value) {
        this.toAirportId = value;
    }

    /**
     * Gets the value of the departureDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureDate() {
        return departureDate;
    }

    /**
     * Sets the value of the departureDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureDate(String value) {
        this.departureDate = value;
    }

    /**
     * Gets the value of the returnDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the value of the returnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReturnDate(JAXBElement<String> value) {
        this.returnDate = value;
    }

}
