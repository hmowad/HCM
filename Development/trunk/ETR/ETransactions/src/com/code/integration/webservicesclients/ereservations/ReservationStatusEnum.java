
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reservationStatusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="reservationStatusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OPEN"/>
 *     &lt;enumeration value="CONFIRMED"/>
 *     &lt;enumeration value="RESERVED"/>
 *     &lt;enumeration value="WAITING"/>
 *     &lt;enumeration value="CANCELED"/>
 *     &lt;enumeration value="CHECK_IN"/>
 *     &lt;enumeration value="BACKWARD"/>
 *     &lt;enumeration value="DELETED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "reservationStatusEnum")
@XmlEnum
public enum ReservationStatusEnum {

    OPEN,
    CONFIRMED,
    RESERVED,
    WAITING,
    CANCELED,
    CHECK_IN,
    BACKWARD,
    DELETED;

    public String value() {
        return name();
    }

    public static ReservationStatusEnum fromValue(String v) {
        return valueOf(v);
    }

}
