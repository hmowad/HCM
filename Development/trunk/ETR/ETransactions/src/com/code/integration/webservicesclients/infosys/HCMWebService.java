
package com.code.integration.webservicesclients.infosys;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "HCMWebService", targetNamespace = "http://labcheck.infosys.services.code.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface HCMWebService {


    /**
     * 
     * @param socialIds
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "results", targetNamespace = "")
    @RequestWrapper(localName = "getLabCheckResults", targetNamespace = "http://labcheck.infosys.services.code.com/", className = "com.code.integration.webservicesclients.infosys.GetLabCheckResults")
    @ResponseWrapper(localName = "getLabCheckResultsResponse", targetNamespace = "http://labcheck.infosys.services.code.com/", className = "com.code.integration.webservicesclients.infosys.GetLabCheckResultsResponse")
    @Action(input = "http://labcheck.infosys.services.code.com/HCMWebService/getLabCheckResultsRequest", output = "http://labcheck.infosys.services.code.com/HCMWebService/getLabCheckResultsResponse")
    public String getLabCheckResults(
        @WebParam(name = "socialIds", targetNamespace = "")
        String socialIds);

}
