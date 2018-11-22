
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.2.9-b130926.1035 Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "Yakeen4BorderGuardService",
	targetNamespace = "http://com.ejada.yaqeen/",
	wsdlLocation = "http://192.168.170.53:8043/YaqeenWrapper/Yakeen4BorderGuardService?WSDL")
public class Yakeen4BorderGuardService
	extends Service {

    private final static URL YAKEEN4BORDERGUARDSERVICE_WSDL_LOCATION;
    private final static WebServiceException YAKEEN4BORDERGUARDSERVICE_EXCEPTION;
    private final static QName YAKEEN4BORDERGUARDSERVICE_QNAME = new QName("http://com.ejada.yaqeen/", "Yakeen4BorderGuardService");

    static {
	URL url = null;
	WebServiceException e = null;
	try {
	    url = new URL("http://192.168.170.53:8043/YaqeenWrapper/Yakeen4BorderGuardService?WSDL");
	} catch (MalformedURLException ex) {
	    e = new WebServiceException(ex);
	}
	YAKEEN4BORDERGUARDSERVICE_WSDL_LOCATION = url;
	YAKEEN4BORDERGUARDSERVICE_EXCEPTION = e;
    }

    public Yakeen4BorderGuardService() {
	super(__getWsdlLocation(), YAKEEN4BORDERGUARDSERVICE_QNAME);
    }

    public Yakeen4BorderGuardService(WebServiceFeature... features) {
	super(__getWsdlLocation(), YAKEEN4BORDERGUARDSERVICE_QNAME, features);
    }

    public Yakeen4BorderGuardService(URL wsdlLocation) {
	super(wsdlLocation, YAKEEN4BORDERGUARDSERVICE_QNAME);
    }

    public Yakeen4BorderGuardService(URL wsdlLocation, WebServiceFeature... features) {
	super(wsdlLocation, YAKEEN4BORDERGUARDSERVICE_QNAME, features);
    }

    public Yakeen4BorderGuardService(URL wsdlLocation, QName serviceName) {
	super(wsdlLocation, serviceName);
    }

    public Yakeen4BorderGuardService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
	super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return returns YaqeenServices
     */
    @WebEndpoint(name = "Yakeen4BorderGuardPort")
    public YaqeenServices getYakeen4BorderGuardPort() {
	return super.getPort(new QName("http://com.ejada.yaqeen/", "Yakeen4BorderGuardPort"), YaqeenServices.class);
    }

    /**
     * 
     * @param features
     *            A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy. Supported features not in the <code>features</code> parameter will have their default values.
     * @return returns YaqeenServices
     */
    @WebEndpoint(name = "Yakeen4BorderGuardPort")
    public YaqeenServices getYakeen4BorderGuardPort(WebServiceFeature... features) {
	return super.getPort(new QName("http://com.ejada.yaqeen/", "Yakeen4BorderGuardPort"), YaqeenServices.class, features);
    }

    private static URL __getWsdlLocation() {
	if (YAKEEN4BORDERGUARDSERVICE_EXCEPTION != null) {
	    throw YAKEEN4BORDERGUARDSERVICE_EXCEPTION;
	}
	return YAKEEN4BORDERGUARDSERVICE_WSDL_LOCATION;
    }

}