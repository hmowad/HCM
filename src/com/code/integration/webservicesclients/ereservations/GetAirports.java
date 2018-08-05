
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAirports complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAirports">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="socialId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAirports", propOrder = {
    "socialId"
})
public class GetAirports {

    @XmlElement(required = true)
    protected String socialId;

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

}
