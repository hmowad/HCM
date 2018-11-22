
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for requesterInfo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="requesterInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operatorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="systemCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requesterInfo",
	propOrder = {
		"operatorId",
		"systemCode"
	})
@XmlSeeAlso({
	YaqeenCarInfoByPlateRequest.class,
	PersonInfoRequest.class,
	YaqeenGccInfoByPassportRequest.class,
	YaqeenGccInfoByNINRequest.class
})
public class RequesterInfo {

    protected String operatorId;
    protected String systemCode;

    /**
     * Gets the value of the operatorId property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getOperatorId() {
	return operatorId;
    }

    /**
     * Sets the value of the operatorId property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setOperatorId(String value) {
	this.operatorId = value;
    }

    /**
     * Gets the value of the systemCode property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getSystemCode() {
	return systemCode;
    }

    /**
     * Sets the value of the systemCode property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setSystemCode(String value) {
	this.systemCode = value;
    }

}
