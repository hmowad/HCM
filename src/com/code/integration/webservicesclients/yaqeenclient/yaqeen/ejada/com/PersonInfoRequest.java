
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for personInfoRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="personInfoRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://com.ejada.yaqeen/}requesterInfo">
 *       &lt;sequence>
 *         &lt;element name="idType" type="{http://com.ejada.yaqeen/}idType" minOccurs="0"/>
 *         &lt;element name="idNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="clientIpAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "personInfoRequest",
	propOrder = {
		"idType",
		"idNumber",
		"dateOfBirth",
		"clientIpAddress",
		"referenceNumber"
	})
public class PersonInfoRequest
	extends RequesterInfo {

    @XmlSchemaType(name = "string")
    protected IdType idType;
    protected String idNumber;
    protected String dateOfBirth;
    protected String clientIpAddress;
    protected String referenceNumber;

    /**
     * Gets the value of the idType property.
     * 
     * @return possible object is {@link IdType }
     * 
     */
    public IdType getIdType() {
	return idType;
    }

    /**
     * Sets the value of the idType property.
     * 
     * @param value
     *            allowed object is {@link IdType }
     * 
     */
    public void setIdType(IdType value) {
	this.idType = value;
    }

    /**
     * Gets the value of the idNumber property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getIdNumber() {
	return idNumber;
    }

    /**
     * Sets the value of the idNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setIdNumber(String value) {
	this.idNumber = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDateOfBirth() {
	return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDateOfBirth(String value) {
	this.dateOfBirth = value;
    }

    /**
     * Gets the value of the clientIpAddress property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getClientIpAddress() {
	return clientIpAddress;
    }

    /**
     * Sets the value of the clientIpAddress property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setClientIpAddress(String value) {
	this.clientIpAddress = value;
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
