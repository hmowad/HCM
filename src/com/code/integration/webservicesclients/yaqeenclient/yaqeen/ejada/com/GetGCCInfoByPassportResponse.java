
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard.GccInfoByPassportResult;

/**
 * <p>
 * Java class for getGCCInfoByPassportResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getGCCInfoByPassportResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://yakeen4borderguard.yakeen.elm.com/}gccInfoByPassportResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getGCCInfoByPassportResponse",
	propOrder = {
		"_return"
	})
public class GetGCCInfoByPassportResponse {

    @XmlElement(name = "return")
    protected GccInfoByPassportResult _return;

    /**
     * Gets the value of the return property.
     * 
     * @return possible object is {@link GccInfoByPassportResult }
     * 
     */
    public GccInfoByPassportResult getReturn() {
	return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *            allowed object is {@link GccInfoByPassportResult }
     * 
     */
    public void setReturn(GccInfoByPassportResult value) {
	this._return = value;
    }

}
