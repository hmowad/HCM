
package com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for securityRecordList complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="securityRecordList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actionCode" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="actionType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "securityRecordList",
	propOrder = {
		"actionCode",
		"actionType"
	})
public class SecurityRecordList {

    protected short actionCode;
    protected String actionType;

    /**
     * Gets the value of the actionCode property.
     * 
     */
    public short getActionCode() {
	return actionCode;
    }

    /**
     * Sets the value of the actionCode property.
     * 
     */
    public void setActionCode(short value) {
	this.actionCode = value;
    }

    /**
     * Gets the value of the actionType property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getActionType() {
	return actionType;
    }

    /**
     * Sets the value of the actionType property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setActionType(String value) {
	this.actionType = value;
    }

}
