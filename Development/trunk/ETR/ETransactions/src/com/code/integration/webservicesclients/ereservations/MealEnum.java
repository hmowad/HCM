
package com.code.integration.webservicesclients.ereservations;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mealEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="mealEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ORDINARY_MEAL"/>
 *     &lt;enumeration value="SUGAR_MEAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "mealEnum")
@XmlEnum
public enum MealEnum {

    ORDINARY_MEAL,
    SUGAR_MEAL;

    public String value() {
        return name();
    }

    public static MealEnum fromValue(String v) {
        return valueOf(v);
    }

}
