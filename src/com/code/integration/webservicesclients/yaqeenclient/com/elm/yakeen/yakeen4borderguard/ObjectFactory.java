
package com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the com.elm.yakeen.yakeen4borderguard package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. The Java representation of XML content can consist of schema derived interfaces and classes representing the binding of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Yakeen4BorderGuardFault_QNAME = new QName("http://yakeen4borderguard.yakeen.elm.com/", "Yakeen4BorderGuardFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.elm.yakeen.yakeen4borderguard
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Yakeen4BorderGuardFault }
     * 
     */
    public Yakeen4BorderGuardFault createYakeen4BorderGuardFault() {
	return new Yakeen4BorderGuardFault();
    }

    /**
     * Create an instance of {@link GccInfoByPassportResult }
     * 
     */
    public GccInfoByPassportResult createGccInfoByPassportResult() {
	return new GccInfoByPassportResult();
    }

    /**
     * Create an instance of {@link CommonErrorObject }
     * 
     */
    public CommonErrorObject createCommonErrorObject() {
	return new CommonErrorObject();
    }

    /**
     * Create an instance of {@link GccInfoByNINResult }
     * 
     */
    public GccInfoByNINResult createGccInfoByNINResult() {
	return new GccInfoByNINResult();
    }

    /**
     * Create an instance of {@link SecurityRecordList }
     * 
     */
    public SecurityRecordList createSecurityRecordList() {
	return new SecurityRecordList();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Yakeen4BorderGuardFault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://yakeen4borderguard.yakeen.elm.com/",
	    name = "Yakeen4BorderGuardFault")
    public JAXBElement<Yakeen4BorderGuardFault> createYakeen4BorderGuardFault(Yakeen4BorderGuardFault value) {
	return new JAXBElement<Yakeen4BorderGuardFault>(_Yakeen4BorderGuardFault_QNAME, Yakeen4BorderGuardFault.class, null, value);
    }

}
