
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for PersonInfoResultWithSecurityStatus complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonInfoResultWithSecurityStatus">
 *   &lt;complexContent>
 *     &lt;extension base="{http://com.ejada.yaqeen/}perosnInfoResult">
 *       &lt;sequence>
 *         &lt;element name="securityStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonInfoResultWithSecurityStatus",
	propOrder = {
		"securityStatus"
	})
public class PersonInfoResultWithSecurityStatus
	extends PerosnInfoResult {

    @XmlElementRef(name = "securityStatus",
	    type = JAXBElement.class,
	    required = false)
    protected JAXBElement<String> securityStatus;

    /**
     * Gets the value of the securityStatus property.
     * 
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public JAXBElement<String> getSecurityStatus() {
	return securityStatus;
    }

    /**
     * Sets the value of the securityStatus property.
     * 
     * @param value
     *            allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public void setSecurityStatus(JAXBElement<String> value) {
	this.securityStatus = value;
    }

}
