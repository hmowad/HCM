
package com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for lifeStatus.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="lifeStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="L"/>
 *     &lt;enumeration value="D"/>
 *     &lt;enumeration value="U"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "lifeStatus")
@XmlEnum
public enum LifeStatus {

    L,
    D,
    U;

    public String value() {
	return name();
    }

    public static LifeStatus fromValue(String v) {
	return valueOf(v);
    }

}
