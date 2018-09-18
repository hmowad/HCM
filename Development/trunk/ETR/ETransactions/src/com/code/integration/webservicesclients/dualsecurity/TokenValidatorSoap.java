
package com.code.integration.webservicesclients.dualsecurity;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "TokenValidatorSoap", targetNamespace = "http://www.cryptocard.com/blackshield/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TokenValidatorSoap {


    /**
     * Proxy - This method checks the status of the Authentication Server
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "CheckServerStatus", action = "http://www.cryptocard.com/blackshield/CheckServerStatus")
    @WebResult(name = "CheckServerStatusResult", targetNamespace = "http://www.cryptocard.com/blackshield/")
    @RequestWrapper(localName = "CheckServerStatus", targetNamespace = "http://www.cryptocard.com/blackshield/", className = "com.cryptocard.blackshield.CheckServerStatus")
    @ResponseWrapper(localName = "CheckServerStatusResponse", targetNamespace = "http://www.cryptocard.com/blackshield/", className = "com.cryptocard.blackshield.CheckServerStatusResponse")
    public String checkServerStatus();

    /**
     * Proxy - This method authenticates a user against their passcode in the Authentication Server
     * 
     * @param outChallenge
     * @param inOrganization
     * @param inOTP
     * @param inAndOutState
     * @param inUserName
     * @param authenticateResult
     * @param inIPAddress
     * @param outChallengeData
     */
    @WebMethod(operationName = "Authenticate", action = "http://www.cryptocard.com/blackshield/Authenticate")
    @RequestWrapper(localName = "Authenticate", targetNamespace = "http://www.cryptocard.com/blackshield/", className = "com.cryptocard.blackshield.Authenticate")
    @ResponseWrapper(localName = "AuthenticateResponse", targetNamespace = "http://www.cryptocard.com/blackshield/", className = "com.cryptocard.blackshield.AuthenticateResponse")
    public void authenticate(
        @WebParam(name = "inUserName", targetNamespace = "http://www.cryptocard.com/blackshield/")
        String inUserName,
        @WebParam(name = "inOTP", targetNamespace = "http://www.cryptocard.com/blackshield/")
        String inOTP,
        @WebParam(name = "inOrganization", targetNamespace = "http://www.cryptocard.com/blackshield/")
        String inOrganization,
        @WebParam(name = "inIPAddress", targetNamespace = "http://www.cryptocard.com/blackshield/")
        String inIPAddress,
        @WebParam(name = "outChallenge", targetNamespace = "http://www.cryptocard.com/blackshield/", mode = WebParam.Mode.INOUT)
        Holder<String> outChallenge,
        @WebParam(name = "outChallengeData", targetNamespace = "http://www.cryptocard.com/blackshield/", mode = WebParam.Mode.INOUT)
        Holder<String> outChallengeData,
        @WebParam(name = "InAndOutState", targetNamespace = "http://www.cryptocard.com/blackshield/", mode = WebParam.Mode.INOUT)
        Holder<String> inAndOutState,
        @WebParam(name = "AuthenticateResult", targetNamespace = "http://www.cryptocard.com/blackshield/", mode = WebParam.Mode.OUT)
        Holder<Integer> authenticateResult);

}