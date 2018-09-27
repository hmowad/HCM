
package com.code.integration.webservicesclients.infosys;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.code.integration.webservicesclients.infosys package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetLabCheckResultsResponse_QNAME = new QName("http://labcheck.infosys.services.code.com/", "getLabCheckResultsResponse");
    private final static QName _GetLabCheckResults_QNAME = new QName("http://labcheck.infosys.services.code.com/", "getLabCheckResults");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.code.integration.webservicesclients.infosys
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetLabCheckResultsResponse }
     * 
     */
    public GetLabCheckResultsResponse createGetLabCheckResultsResponse() {
        return new GetLabCheckResultsResponse();
    }

    /**
     * Create an instance of {@link GetLabCheckResults }
     * 
     */
    public GetLabCheckResults createGetLabCheckResults() {
        return new GetLabCheckResults();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLabCheckResultsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://labcheck.infosys.services.code.com/", name = "getLabCheckResultsResponse")
    public JAXBElement<GetLabCheckResultsResponse> createGetLabCheckResultsResponse(GetLabCheckResultsResponse value) {
        return new JAXBElement<GetLabCheckResultsResponse>(_GetLabCheckResultsResponse_QNAME, GetLabCheckResultsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLabCheckResults }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://labcheck.infosys.services.code.com/", name = "getLabCheckResults")
    public JAXBElement<GetLabCheckResults> createGetLabCheckResults(GetLabCheckResults value) {
        return new JAXBElement<GetLabCheckResults>(_GetLabCheckResults_QNAME, GetLabCheckResults.class, null, value);
    }

}
