
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for getPersonInfoDetailedResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getPersonInfoDetailedResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://com.ejada.yaqeen/}PersonInfoDetailedResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPersonInfoDetailedResponse",
	propOrder = {
		"_return"
	})
public class GetPersonInfoDetailedResponse {

    @XmlElement(name = "return")
    protected PersonInfoDetailedResult _return;

    /**
     * Gets the value of the return property.
     * 
     * @return possible object is {@link PersonInfoDetailedResult }
     * 
     */
    public PersonInfoDetailedResult getReturn() {
	return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *            allowed object is {@link PersonInfoDetailedResult }
     * 
     */
    public void setReturn(PersonInfoDetailedResult value) {
	this._return = value;
    }

}
