
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eReservationWSStatusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="eReservationWSStatusEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="SUCCEEDED"/>
 *     &lt;enumeration value="MISSING_PARAMS"/>
 *     &lt;enumeration value="WRONG_DATA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "eReservationWSStatusEnum")
@XmlEnum
public enum EReservationWSStatusEnum {

    FAILED,
    SUCCEEDED,
    MISSING_PARAMS,
    WRONG_DATA;

    public String value() {
        return name();
    }

    public static EReservationWSStatusEnum fromValue(String v) {
        return valueOf(v);
    }

}
