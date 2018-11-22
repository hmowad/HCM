
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for getPersonInfoWithSecuirtyStatusResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getPersonInfoWithSecuirtyStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://com.ejada.yaqeen/}PersonInfoResultWithSecurityStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPersonInfoWithSecuirtyStatusResponse",
	propOrder = {
		"_return"
	})
public class GetPersonInfoWithSecuirtyStatusResponse {

    @XmlElement(name = "return")
    protected PersonInfoResultWithSecurityStatus _return;

    /**
     * Gets the value of the return property.
     * 
     * @return possible object is {@link PersonInfoResultWithSecurityStatus }
     * 
     */
    public PersonInfoResultWithSecurityStatus getReturn() {
	return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *            allowed object is {@link PersonInfoResultWithSecurityStatus }
     * 
     */
    public void setReturn(PersonInfoResultWithSecurityStatus value) {
	this._return = value;
    }

}
