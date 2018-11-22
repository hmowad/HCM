
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for getGCCInfoByNIN complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getGCCInfoByNIN">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GccInfoByNINRequest" type="{http://com.ejada.yaqeen/}yaqeenGccInfoByNINRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getGCCInfoByNIN",
	propOrder = {
		"gccInfoByNINRequest"
	})
public class GetGCCInfoByNIN {

    @XmlElement(name = "GccInfoByNINRequest")
    protected YaqeenGccInfoByNINRequest gccInfoByNINRequest;

    /**
     * Gets the value of the gccInfoByNINRequest property.
     * 
     * @return possible object is {@link YaqeenGccInfoByNINRequest }
     * 
     */
    public YaqeenGccInfoByNINRequest getGccInfoByNINRequest() {
	return gccInfoByNINRequest;
    }

    /**
     * Sets the value of the gccInfoByNINRequest property.
     * 
     * @param value
     *            allowed object is {@link YaqeenGccInfoByNINRequest }
     * 
     */
    public void setGccInfoByNINRequest(YaqeenGccInfoByNINRequest value) {
	this.gccInfoByNINRequest = value;
    }

}
