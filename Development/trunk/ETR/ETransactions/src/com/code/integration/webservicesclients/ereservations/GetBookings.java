
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getBookings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBookings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="socialId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="previousFlightsFlag" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBookings", propOrder = {
    "socialId",
    "previousFlightsFlag"
})
public class GetBookings {

    @XmlElement(required = true)
    protected String socialId;
    protected boolean previousFlightsFlag;

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
     * Gets the value of the previousFlightsFlag property.
     * 
     */
    public boolean isPreviousFlightsFlag() {
        return previousFlightsFlag;
    }

    /**
     * Sets the value of the previousFlightsFlag property.
     * 
     */
    public void setPreviousFlightsFlag(boolean value) {
        this.previousFlightsFlag = value;
    }

}
