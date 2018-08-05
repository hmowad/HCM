
package com.code.integration.webservicesclients.ereservations;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reservationTickets complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reservationTickets">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reservationTickets" type="{http://webservice.services.code.com/}ticketData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reservationTickets", propOrder = {
    "reservationTickets"
})
public class ReservationTickets {

    @XmlElement(nillable = true)
    protected List<TicketData> reservationTickets;

    /**
     * Gets the value of the reservationTickets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reservationTickets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReservationTickets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TicketData }
     * 
     * 
     */
    public List<TicketData> getReservationTickets() {
        if (reservationTickets == null) {
            reservationTickets = new ArrayList<TicketData>();
        }
        return this.reservationTickets;
    }

}
