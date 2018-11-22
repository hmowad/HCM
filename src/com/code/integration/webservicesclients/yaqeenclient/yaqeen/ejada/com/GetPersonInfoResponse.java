
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for getPersonInfoResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getPersonInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://com.ejada.yaqeen/}perosnInfoResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPersonInfoResponse",
	propOrder = {
		"_return"
	})
public class GetPersonInfoResponse {

    @XmlElement(name = "return")
    protected PerosnInfoResult _return;

    /**
     * Gets the value of the return property.
     * 
     * @return possible object is {@link PerosnInfoResult }
     * 
     */
    public PerosnInfoResult getReturn() {
	return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *            allowed object is {@link PerosnInfoResult }
     * 
     */
    public void setReturn(PerosnInfoResult value) {
	this._return = value;
    }

}
