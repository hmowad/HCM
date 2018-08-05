
package com.code.integration.webservicesclients.ereservations;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bookingsResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bookingsResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bookingsList" type="{http://webservice.services.code.com/}reservationTickets" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://webservice.services.code.com/}eReservationWSStatusEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bookingsResult", propOrder = {
    "bookingsList",
    "message",
    "status"
})
public class BookingsResult {

    @XmlElement(nillable = true)
    protected List<ReservationTickets> bookingsList;
    protected String message;
    @XmlSchemaType(name = "string")
    protected EReservationWSStatusEnum status;

    /**
     * Gets the value of the bookingsList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bookingsList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBookingsList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReservationTickets }
     * 
     * 
     */
    public List<ReservationTickets> getBookingsList() {
        if (bookingsList == null) {
            bookingsList = new ArrayList<ReservationTickets>();
        }
        return this.bookingsList;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link EReservationWSStatusEnum }
     *     
     */
    public EReservationWSStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link EReservationWSStatusEnum }
     *     
     */
    public void setStatus(EReservationWSStatusEnum value) {
        this.status = value;
    }

}
