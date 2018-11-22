
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

import com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard.SecurityRecordList;

/**
 * <p>
 * Java class for PersonInfoResultWithDetailedSecurityStatus complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonInfoResultWithDetailedSecurityStatus">
 *   &lt;complexContent>
 *     &lt;extension base="{http://com.ejada.yaqeen/}perosnInfoResult">
 *       &lt;sequence>
 *         &lt;element name="securityStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="securityRecordListList" type="{http://yakeen4borderguard.yakeen.elm.com/}securityRecordList" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonInfoResultWithDetailedSecurityStatus",
	propOrder = {
		"securityStatus",
		"securityRecordListList"
	})
public class PersonInfoResultWithDetailedSecurityStatus
	extends PerosnInfoResult {

    @XmlElementRef(name = "securityStatus",
	    type = JAXBElement.class,
	    required = false)
    protected JAXBElement<String> securityStatus;
    @XmlElement(nillable = true)
    protected List<SecurityRecordList> securityRecordListList;

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

    /**
     * Gets the value of the securityRecordListList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the securityRecordListList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getSecurityRecordListList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link SecurityRecordList }
     * 
     * 
     */
    public List<SecurityRecordList> getSecurityRecordListList() {
	if (securityRecordListList == null) {
	    securityRecordListList = new ArrayList<SecurityRecordList>();
	}
	return this.securityRecordListList;
    }

}
