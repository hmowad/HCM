
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for getGCCInfoByPassport complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getGCCInfoByPassport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GccInfoByPassportRequest" type="{http://com.ejada.yaqeen/}yaqeenGccInfoByPassportRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getGCCInfoByPassport",
	propOrder = {
		"gccInfoByPassportRequest"
	})
public class GetGCCInfoByPassport {

    @XmlElement(name = "GccInfoByPassportRequest")
    protected YaqeenGccInfoByPassportRequest gccInfoByPassportRequest;

    /**
     * Gets the value of the gccInfoByPassportRequest property.
     * 
     * @return possible object is {@link YaqeenGccInfoByPassportRequest }
     * 
     */
    public YaqeenGccInfoByPassportRequest getGccInfoByPassportRequest() {
	return gccInfoByPassportRequest;
    }

    /**
     * Sets the value of the gccInfoByPassportRequest property.
     * 
     * @param value
     *            allowed object is {@link YaqeenGccInfoByPassportRequest }
     * 
     */
    public void setGccInfoByPassportRequest(YaqeenGccInfoByPassportRequest value) {
	this.gccInfoByPassportRequest = value;
    }

}
