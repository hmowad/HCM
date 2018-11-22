
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for yaqeenGccInfoByNINRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="yaqeenGccInfoByNINRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://com.ejada.yaqeen/}requesterInfo">
 *       &lt;sequence>
 *         &lt;element name="gccNIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gccNationality" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="referenceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "yaqeenGccInfoByNINRequest",
	propOrder = {
		"gccNIN",
		"gccNationality",
		"referenceNumber"
	})
public class YaqeenGccInfoByNINRequest
	extends RequesterInfo {

    protected String gccNIN;
    protected short gccNationality;
    protected String referenceNumber;

    /**
     * Gets the value of the gccNIN property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getGccNIN() {
	return gccNIN;
    }

    /**
     * Sets the value of the gccNIN property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setGccNIN(String value) {
	this.gccNIN = value;
    }

    /**
     * Gets the value of the gccNationality property.
     * 
     */
    public short getGccNationality() {
	return gccNationality;
    }

    /**
     * Sets the value of the gccNationality property.
     * 
     */
    public void setGccNationality(short value) {
	this.gccNationality = value;
    }

    /**
     * Gets the value of the referenceNumber property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getReferenceNumber() {
	return referenceNumber;
    }

    /**
     * Sets the value of the referenceNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setReferenceNumber(String value) {
	this.referenceNumber = value;
    }

}
