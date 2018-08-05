
package com.code.integration.webservicesclients.dualsecurity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inOTP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inOrganization" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inIPAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="outChallenge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="outChallengeData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InAndOutState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "inUserName",
    "inOTP",
    "inOrganization",
    "inIPAddress",
    "outChallenge",
    "outChallengeData",
    "inAndOutState"
})
@XmlRootElement(name = "Authenticate")
public class Authenticate {

    protected String inUserName;
    protected String inOTP;
    protected String inOrganization;
    protected String inIPAddress;
    protected String outChallenge;
    protected String outChallengeData;
    @XmlElement(name = "InAndOutState")
    protected String inAndOutState;

    /**
     * Gets the value of the inUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInUserName() {
        return inUserName;
    }

    /**
     * Sets the value of the inUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInUserName(String value) {
        this.inUserName = value;
    }

    /**
     * Gets the value of the inOTP property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInOTP() {
        return inOTP;
    }

    /**
     * Sets the value of the inOTP property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInOTP(String value) {
        this.inOTP = value;
    }

    /**
     * Gets the value of the inOrganization property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInOrganization() {
        return inOrganization;
    }

    /**
     * Sets the value of the inOrganization property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInOrganization(String value) {
        this.inOrganization = value;
    }

    /**
     * Gets the value of the inIPAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInIPAddress() {
        return inIPAddress;
    }

    /**
     * Sets the value of the inIPAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInIPAddress(String value) {
        this.inIPAddress = value;
    }

    /**
     * Gets the value of the outChallenge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutChallenge() {
        return outChallenge;
    }

    /**
     * Sets the value of the outChallenge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutChallenge(String value) {
        this.outChallenge = value;
    }

    /**
     * Gets the value of the outChallengeData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutChallengeData() {
        return outChallengeData;
    }

    /**
     * Sets the value of the outChallengeData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutChallengeData(String value) {
        this.outChallengeData = value;
    }

    /**
     * Gets the value of the inAndOutState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInAndOutState() {
        return inAndOutState;
    }

    /**
     * Sets the value of the inAndOutState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInAndOutState(String value) {
        this.inAndOutState = value;
    }

}
