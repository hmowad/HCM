
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bookFlight complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bookFlight">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="socialId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contactPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contactEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mealEnumCode" type="{http://webservice.services.code.com/}mealEnum"/>
 *         &lt;element name="TransportationEnum" type="{http://webservice.services.code.com/}transportationEnum"/>
 *         &lt;element name="goingLegInstancesIds" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="returnLegInstancesIds" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="waitingFlight" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bookFlight", propOrder = {
    "socialId",
    "contactPhone",
    "contactEmail",
    "mealEnumCode",
    "transportationEnum",
    "goingLegInstancesIds",
    "returnLegInstancesIds",
    "waitingFlight"
})
public class BookFlight {

    @XmlElement(required = true)
    protected String socialId;
    @XmlElement(required = true)
    protected String contactPhone;
    protected String contactEmail;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected MealEnum mealEnumCode;
    @XmlElement(name = "TransportationEnum", required = true)
    @XmlSchemaType(name = "string")
    protected TransportationEnum transportationEnum;
    @XmlElement(required = true)
    protected String goingLegInstancesIds;
    protected String returnLegInstancesIds;
    protected boolean waitingFlight;

    /**
     * Gets the value of the socialId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocialId() {
        return socialId;
    }

    /**
     * Sets the value of the socialId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocialId(String value) {
        this.socialId = value;
    }

    /**
     * Gets the value of the contactPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * Sets the value of the contactPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPhone(String value) {
        this.contactPhone = value;
    }

    /**
     * Gets the value of the contactEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Sets the value of the contactEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactEmail(String value) {
        this.contactEmail = value;
    }

    /**
     * Gets the value of the mealEnumCode property.
     * 
     * @return
     *     possible object is
     *     {@link MealEnum }
     *     
     */
    public MealEnum getMealEnumCode() {
        return mealEnumCode;
    }

    /**
     * Sets the value of the mealEnumCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link MealEnum }
     *     
     */
    public void setMealEnumCode(MealEnum value) {
        this.mealEnumCode = value;
    }

    /**
     * Gets the value of the transportationEnum property.
     * 
     * @return
     *     possible object is
     *     {@link TransportationEnum }
     *     
     */
    public TransportationEnum getTransportationEnum() {
        return transportationEnum;
    }

    /**
     * Sets the value of the transportationEnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportationEnum }
     *     
     */
    public void setTransportationEnum(TransportationEnum value) {
        this.transportationEnum = value;
    }

    /**
     * Gets the value of the goingLegInstancesIds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGoingLegInstancesIds() {
        return goingLegInstancesIds;
    }

    /**
     * Sets the value of the goingLegInstancesIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGoingLegInstancesIds(String value) {
        this.goingLegInstancesIds = value;
    }

    /**
     * Gets the value of the returnLegInstancesIds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnLegInstancesIds() {
        return returnLegInstancesIds;
    }

    /**
     * Sets the value of the returnLegInstancesIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnLegInstancesIds(String value) {
        this.returnLegInstancesIds = value;
    }

    /**
     * Gets the value of the waitingFlight property.
     * 
     */
    public boolean isWaitingFlight() {
        return waitingFlight;
    }

    /**
     * Sets the value of the waitingFlight property.
     * 
     */
    public void setWaitingFlight(boolean value) {
        this.waitingFlight = value;
    }

}
