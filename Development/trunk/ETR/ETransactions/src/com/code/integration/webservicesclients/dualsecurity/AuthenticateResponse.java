
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
 *         &lt;element name="AuthenticateResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "authenticateResult",
    "outChallenge",
    "outChallengeData",
    "inAndOutState"
})
@XmlRootElement(name = "AuthenticateResponse")
public class AuthenticateResponse {

    @XmlElement(name = "AuthenticateResult")
    protected int authenticateResult;
    protected String outChallenge;
    protected String outChallengeData;
    @XmlElement(name = "InAndOutState")
    protected String inAndOutState;

    /**
     * Gets the value of the authenticateResult property.
     * 
     */
    public int getAuthenticateResult() {
        return authenticateResult;
    }

    /**
     * Sets the value of the authenticateResult property.
     * 
     */
    public void setAuthenticateResult(int value) {
        this.authenticateResult = value;
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
