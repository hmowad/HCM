
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ticketData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ticketData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservice.services.code.com/}baseEntity">
 *       &lt;sequence>
 *         &lt;element name="airportFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="airportFromDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="airportFromId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="airportTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="airportToDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="airportToId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ariplaneId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="arrivalTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactPhone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="departureTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="employeeStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flightCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flightDuration" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="flightStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="instanceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="instanceDateString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legInstanceId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="meal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ownerUserId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="reservationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="reservationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reservationStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reservationStatusArabicDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seatNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="selected" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="socialId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="specialLeg" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ticketNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="waitingOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ticketData", propOrder = {
    "airportFrom",
    "airportFromDesc",
    "airportFromId",
    "airportTo",
    "airportToDesc",
    "airportToId",
    "ariplaneId",
    "arrivalTime",
    "contactPhone",
    "departureTime",
    "employeeStatus",
    "flightCode",
    "flightDuration",
    "flightStatus",
    "fullName",
    "id",
    "instanceDate",
    "instanceDateString",
    "legInstanceId",
    "meal",
    "ownerUserId",
    "reservationDate",
    "reservationNumber",
    "reservationStatus",
    "reservationStatusArabicDesc",
    "seatNo",
    "selected",
    "socialId",
    "specialLeg",
    "ticketNumber",
    "waitingOrder"
})
public class TicketData
    extends BaseEntity
{

    protected String airportFrom;
    protected String airportFromDesc;
    protected Long airportFromId;
    protected String airportTo;
    protected String airportToDesc;
    protected Long airportToId;
    protected Long ariplaneId;
    protected String arrivalTime;
    protected String contactPhone;
    protected String departureTime;
    protected String employeeStatus;
    protected String flightCode;
    protected Long flightDuration;
    protected String flightStatus;
    protected String fullName;
    protected Long id;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar instanceDate;
    protected String instanceDateString;
    protected Long legInstanceId;
    protected String meal;
    protected Long ownerUserId;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar reservationDate;
    protected String reservationNumber;
    protected String reservationStatus;
    protected String reservationStatusArabicDesc;
    protected String seatNo;
    protected boolean selected;
    protected String socialId;
    protected Boolean specialLeg;
    protected String ticketNumber;
    protected String waitingOrder;

    /**
     * Gets the value of the airportFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAirportFrom() {
        return airportFrom;
    }

    /**
     * Sets the value of the airportFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAirportFrom(String value) {
        this.airportFrom = value;
    }

    /**
     * Gets the value of the airportFromDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAirportFromDesc() {
        return airportFromDesc;
    }

    /**
     * Sets the value of the airportFromDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAirportFromDesc(String value) {
        this.airportFromDesc = value;
    }

    /**
     * Gets the value of the airportFromId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAirportFromId() {
        return airportFromId;
    }

    /**
     * Sets the value of the airportFromId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAirportFromId(Long value) {
        this.airportFromId = value;
    }

    /**
     * Gets the value of the airportTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAirportTo() {
        return airportTo;
    }

    /**
     * Sets the value of the airportTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAirportTo(String value) {
        this.airportTo = value;
    }

    /**
     * Gets the value of the airportToDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAirportToDesc() {
        return airportToDesc;
    }

    /**
     * Sets the value of the airportToDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAirportToDesc(String value) {
        this.airportToDesc = value;
    }

    /**
     * Gets the value of the airportToId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAirportToId() {
        return airportToId;
    }

    /**
     * Sets the value of the airportToId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAirportToId(Long value) {
        this.airportToId = value;
    }

    /**
     * Gets the value of the ariplaneId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAriplaneId() {
        return ariplaneId;
    }

    /**
     * Sets the value of the ariplaneId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAriplaneId(Long value) {
        this.ariplaneId = value;
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
     * Gets the value of the employeeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeStatus() {
        return employeeStatus;
    }

    /**
     * Sets the value of the employeeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeStatus(String value) {
        this.employeeStatus = value;
    }

    /**
     * Gets the value of the flightCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlightCode() {
        return flightCode;
    }

    /**
     * Sets the value of the flightCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlightCode(String value) {
        this.flightCode = value;
    }

    /**
     * Gets the value of the flightDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFlightDuration() {
        return flightDuration;
    }

    /**
     * Sets the value of the flightDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFlightDuration(Long value) {
        this.flightDuration = value;
    }

    /**
     * Gets the value of the flightStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlightStatus() {
        return flightStatus;
    }

    /**
     * Sets the value of the flightStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlightStatus(String value) {
        this.flightStatus = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
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
     * Gets the value of the legInstanceId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLegInstanceId() {
        return legInstanceId;
    }

    /**
     * Sets the value of the legInstanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLegInstanceId(Long value) {
        this.legInstanceId = value;
    }

    /**
     * Gets the value of the meal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeal() {
        return meal;
    }

    /**
     * Sets the value of the meal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeal(String value) {
        this.meal = value;
    }

    /**
     * Gets the value of the ownerUserId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOwnerUserId() {
        return ownerUserId;
    }

    /**
     * Sets the value of the ownerUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOwnerUserId(Long value) {
        this.ownerUserId = value;
    }

    /**
     * Gets the value of the reservationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReservationDate() {
        return reservationDate;
    }

    /**
     * Sets the value of the reservationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReservationDate(XMLGregorianCalendar value) {
        this.reservationDate = value;
    }

    /**
     * Gets the value of the reservationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReservationNumber() {
        return reservationNumber;
    }

    /**
     * Sets the value of the reservationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReservationNumber(String value) {
        this.reservationNumber = value;
    }

    /**
     * Gets the value of the reservationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReservationStatus() {
        return reservationStatus;
    }

    /**
     * Sets the value of the reservationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReservationStatus(String value) {
        this.reservationStatus = value;
    }

    /**
     * Gets the value of the reservationStatusArabicDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReservationStatusArabicDesc() {
        return reservationStatusArabicDesc;
    }

    /**
     * Sets the value of the reservationStatusArabicDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReservationStatusArabicDesc(String value) {
        this.reservationStatusArabicDesc = value;
    }

    /**
     * Gets the value of the seatNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeatNo() {
        return seatNo;
    }

    /**
     * Sets the value of the seatNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeatNo(String value) {
        this.seatNo = value;
    }

    /**
     * Gets the value of the selected property.
     * 
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the value of the selected property.
     * 
     */
    public void setSelected(boolean value) {
        this.selected = value;
    }

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
     * Gets the value of the ticketNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTicketNumber() {
        return ticketNumber;
    }

    /**
     * Sets the value of the ticketNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTicketNumber(String value) {
        this.ticketNumber = value;
    }

    /**
     * Gets the value of the waitingOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaitingOrder() {
        return waitingOrder;
    }

    /**
     * Sets the value of the waitingOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaitingOrder(String value) {
        this.waitingOrder = value;
    }

}
