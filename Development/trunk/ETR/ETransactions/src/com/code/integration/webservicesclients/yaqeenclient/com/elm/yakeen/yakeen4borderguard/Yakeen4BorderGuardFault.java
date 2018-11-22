
package com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Yakeen4BorderGuardFault complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Yakeen4BorderGuardFault">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="commonErrorObject" type="{http://yakeen4borderguard.yakeen.elm.com/}CommonErrorObject"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Yakeen4BorderGuardFault",
	propOrder = {
		"commonErrorObject"
	})
public class Yakeen4BorderGuardFault {

    @XmlElement(required = true,
	    nillable = true)
    protected CommonErrorObject commonErrorObject;

    /**
     * Gets the value of the commonErrorObject property.
     * 
     * @return possible object is {@link CommonErrorObject }
     * 
     */
    public CommonErrorObject getCommonErrorObject() {
	return commonErrorObject;
    }

    /**
     * Sets the value of the commonErrorObject property.
     * 
     * @param value
     *            allowed object is {@link CommonErrorObject }
     * 
     */
    public void setCommonErrorObject(CommonErrorObject value) {
	this.commonErrorObject = value;
    }

}
