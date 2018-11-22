
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for getPersonInfo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getPersonInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PersonInfoRequest" type="{http://com.ejada.yaqeen/}personInfoRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPersonInfo",
	propOrder = {
		"personInfoRequest"
	})
public class GetPersonInfo {

    @XmlElement(name = "PersonInfoRequest")
    protected PersonInfoRequest personInfoRequest;

    /**
     * Gets the value of the personInfoRequest property.
     * 
     * @return possible object is {@link PersonInfoRequest }
     * 
     */
    public PersonInfoRequest getPersonInfoRequest() {
	return personInfoRequest;
    }

    /**
     * Sets the value of the personInfoRequest property.
     * 
     * @param value
     *            allowed object is {@link PersonInfoRequest }
     * 
     */
    public void setPersonInfoRequest(PersonInfoRequest value) {
	this.personInfoRequest = value;
    }

}
