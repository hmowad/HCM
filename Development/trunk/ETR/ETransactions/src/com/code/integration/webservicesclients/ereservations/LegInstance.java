
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for legInstance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="legInstance">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservice.services.code.com/}baseEntity">
 *       &lt;sequence>
 *         &lt;element name="actualArrivalTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="actualDepartureTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="airplanId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="arrivalTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="closeBoardingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="closeDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="departureTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flightLegId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="fromAirportId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="instanceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="instanceDateString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legNo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="openBoardingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="openWaitingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="specialLeg" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="toAirportId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "legInstance", propOrder = {
    "actualArrivalTime",
    "actualDepartureTime",
    "airplanId",
    "arrivalTime",
    "closeBoardingDate",
    "closeDate",
    "departureTime",
    "flightLegId",
    "fromAirportId",
    "id",
    "instanceDate",
    "instanceDateString",
    "legNo",
    "openBoardingDate",
    "openWaitingDate",
    "specialLeg",
    "status",
    "toAirportId"
})
public class LegInstance
    extends BaseEntity
{

    protected String actualArrivalTime;
    protected String actualDepartureTime;
    protected Long airplanId;
    protected String arrivalTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar closeBoardingDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar closeDate;
    protected String departureTime;
    protected Long flightLegId;
    protected Long fromAirportId;
    protected Long id;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar instanceDate;
    protected String instanceDateString;
    protected Integer legNo;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar openBoardingDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar openWaitingDate;
    protected Boolean specialLeg;
    protected String status;
    protected Long toAirportId;

    /**
     * Gets the value of the actualArrivalTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActualArrivalTime() {
        return actualArrivalTime;
    }

    /**
     * Sets the value of the actualArrivalTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActualArrivalTime(String value) {
        this.actualArrivalTime = value;
    }

    /**
     * Gets the value of the actualDepartureTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActualDepartureTime() {
        return actualDepartureTime;
    }

    /**
     * Sets the value of the actualDepartureTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActualDepartureTime(String value) {
        this.actualDepartureTime = value;
    }

    /**
     * Gets the value of the airplanId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAirplanId() {
        return airplanId;
    }

    /**
     * Sets the value of the airplanId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAirplanId(Long value) {
        this.airplanId = value;
    }

    /**
     * Gets the value of the arrivalTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the value of the arrivalTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalTime(String value) {
        this.arrivalTime = value;
    }

    /**
     * Gets the value of the closeBoardingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCloseBoardingDate() {
        return closeBoardingDate;
    }

    /**
     * Sets the value of the closeBoardingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCloseBoardingDate(XMLGregorianCalendar value) {
        this.closeBoardingDate = value;
    }

    /**
     * Gets the value of the closeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCloseDate() {
        return closeDate;
    }

    /**
     * Sets the value of the closeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCloseDate(XMLGregorianCalendar value) {
        this.closeDate = value;
    }

    /**
     * Gets the value of the departureTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     * Sets the value of the departureTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureTime(String value) {
        this.departureTime = value;
    }

    /**
     * Gets the value of the flightLegId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFlightLegId() {
        return flightLegId;
    }

    /**
     * Sets the value of the flightLegId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFlightLegId(Long value) {
        this.flightLegId = value;
    }

    /**
     * Gets the value of the fromAirportId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFromAirportId() {
        return fromAirportId;
    }

    /**
     * Sets the value of the fromAirportId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFromAirportId(Long value) {
        this.fromAirportId = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the instanceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInstanceDate() {
        return instanceDate;
    }

    /**
     * Sets the value of the instanceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInstanceDate(XMLGregorianCalendar value) {
        this.instanceDate = value;
    }

    /**
     * Gets the value of the instanceDateString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceDateString() {
        return instanceDateString;
    }

    /**
     * Sets the value of the instanceDateString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceDateString(String value) {
        this.instanceDateString = value;
    }

    /**
     * Gets the value of the legNo property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLegNo() {
        return legNo;
    }

    /**
     * Sets the value of the legNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLegNo(Integer value) {
        this.legNo = value;
    }

    /**
     * Gets the value of the openBoardingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOpenBoardingDate() {
        return openBoardingDate;
    }

    /**
     * Sets the value of the openBoardingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOpenBoardingDate(XMLGregorianCalendar value) {
        this.openBoardingDate = value;
    }

    /**
     * Gets the value of the openWaitingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOpenWaitingDate() {
        return openWaitingDate;
    }

    /**
     * Sets the value of the openWaitingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOpenWaitingDate(XMLGregorianCalendar value) {
        this.openWaitingDate = value;
    }

    /**
     * Gets the value of the specialLeg property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSpecialLeg() {
        return specialLeg;
    }

    /**
     * Sets the value of the specialLeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSpecialLeg(Boolean value) {
        this.specialLeg = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the toAirportId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getToAirportId() {
        return toAirportId;
    }

    /**
     * Sets the value of the toAirportId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setToAirportId(Long value) {
        this.toAirportId = value;
    }

}
