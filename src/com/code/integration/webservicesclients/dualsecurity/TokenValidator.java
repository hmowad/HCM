
package com.code.integration.webservicesclients.dualsecurity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "TokenValidator", targetNamespace = "http://www.cryptocard.com/blackshield/", wsdlLocation = "http://172.16.4.185/TokenValidator/TokenValidator.asmx?WSDL")
public class TokenValidator
    extends Service
{

    private final static URL TOKENVALIDATOR_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.code.integration.webservicesclients.dualsecurity.TokenValidator.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.code.integration.webservicesclients.dualsecurity.TokenValidator.class.getResource(".");
            url = new URL(baseUrl, "http://172.16.4.185/TokenValidator/TokenValidator.asmx?WSDL");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://172.16.4.185/TokenValidator/TokenValidator.asmx?WSDL', retrying as a local file");
            logger.warning(e.getMessage());
        }
        TOKENVALIDATOR_WSDL_LOCATION = url;
    }

    public TokenValidator(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TokenValidator() {
        super(TOKENVALIDATOR_WSDL_LOCATION, new QName("http://www.cryptocard.com/blackshield/", "TokenValidator"));
    }

    /**
     * 
     * @return
     *     returns TokenValidatorSoap
     */
    @WebEndpoint(name = "TokenValidatorSoap")
    public TokenValidatorSoap getTokenValidatorSoap() {
        return super.getPort(new QName("http://www.cryptocard.com/blackshield/", "TokenValidatorSoap"), TokenValidatorSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TokenValidatorSoap
     */
    @WebEndpoint(name = "TokenValidatorSoap")
    public TokenValidatorSoap getTokenValidatorSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.cryptocard.com/blackshield/", "TokenValidatorSoap"), TokenValidatorSoap.class, features);
    }

}