
package com.code.integration.webservicesclients.ereservations;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for flightSearchResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="flightSearchResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="legInstancesDataList" type="{http://webservice.services.code.com/}legInstancesData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="statusEnum" type="{http://webservice.services.code.com/}reservationStatusEnum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "flightSearchResult", propOrder = {
    "legInstancesDataList",
    "statusEnum"
})
public class FlightSearchResult {

    @XmlElement(nillable = true)
    protected List<LegInstancesData> legInstancesDataList;
    @XmlSchemaType(name = "string")
    protected ReservationStatusEnum statusEnum;

    /**
     * Gets the value of the legInstancesDataList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the legInstancesDataList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLegInstancesDataList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LegInstancesData }
     * 
     * 
     */
    public List<LegInstancesData> getLegInstancesDataList() {
        if (legInstancesDataList == null) {
            legInstancesDataList = new ArrayList<LegInstancesData>();
        }
        return this.legInstancesDataList;
    }

    /**
     * Gets the value of the statusEnum property.
     * 
     * @return
     *     possible object is
     *     {@link ReservationStatusEnum }
     *     
     */
    public ReservationStatusEnum getStatusEnum() {
        return statusEnum;
    }

    /**
     * Sets the value of the statusEnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReservationStatusEnum }
     *     
     */
    public void setStatusEnum(ReservationStatusEnum value) {
        this.statusEnum = value;
    }

}
