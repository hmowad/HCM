
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for yaqeenGccInfoByPassportRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="yaqeenGccInfoByPassportRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://com.ejada.yaqeen/}requesterInfo">
 *       &lt;sequence>
 *         &lt;element name="gccNationality" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="gccPassportNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "yaqeenGccInfoByPassportRequest",
	propOrder = {
		"gccNationality",
		"gccPassportNumber",
		"referenceNumber"
	})
public class YaqeenGccInfoByPassportRequest
	extends RequesterInfo {

    protected short gccNationality;
    protected String gccPassportNumber;
    protected String referenceNumber;

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
     * Gets the value of the gccPassportNumber property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getGccPassportNumber() {
	return gccPassportNumber;
    }

    /**
     * Sets the value of the gccPassportNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setGccPassportNumber(String value) {
	this.gccPassportNumber = value;
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
