
package com.code.integration.webservicesclients.infosys;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getLabCheckResults complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getLabCheckResults">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="socialIds" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getLabCheckResults", propOrder = {
    "socialIds"
})
public class GetLabCheckResults {

    @XmlElement(required = true)
    protected String socialIds;

    /**
     * Gets the value of the socialIds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocialIds() {
        return socialIds;
    }

    /**
     * Sets the value of the socialIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocialIds(String value) {
        this.socialIds = value;
    }

}
