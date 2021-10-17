package com.code.integration.webservices.security;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.commons.codec.binary.Base64;

import com.code.dal.orm.integration.security.WSSession;
import com.code.enums.FlagsEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.security.WSAuthenticationResponse;
import com.code.integration.responses.security.WSEmployeeMenusResponse;
import com.code.services.BaseService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.security.SecurityService;

@WebService(targetNamespace = "http://integration.code.com/security",
	portName = "SecurityWSHttpPort")
public class SecurityWS {

    @WebMethod(operationName = "login")
    @WebResult(name = "authenticationResponse")
    public WSAuthenticationResponse login(@WebParam(name = "userCredential") String userCredential) {

	WSAuthenticationResponse response = new WSAuthenticationResponse();
	try {

	    if (userCredential == null || userCredential.isEmpty())
		throw new BusinessException("error_userNameAndPasswordMandatory");

	    final byte[] decodedBytes = Base64.decodeBase64(userCredential.getBytes("UTF-8"));
	    final String[] userCredentialList = (new String(decodedBytes)).split(":", 2);
	    return login(userCredentialList[0], userCredentialList[1]);

	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getEmployeeMenus")
    @WebResult(name = "employeeMenusResponse")
    public WSEmployeeMenusResponse getEmployeeMenus(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSEmployeeMenusResponse response = new WSEmployeeMenusResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setMenusList(SecurityService.getEmployeeMenusShowInMobile(employeeId, FlagsEnum.ON.getCode()));
	    response.setMessage(BaseService.getMessage("notify_successOperation"));

	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    private WSAuthenticationResponse login(String username, String password) throws BusinessException {

	WSAuthenticationResponse response = new WSAuthenticationResponse();
	if (username == null || username.isEmpty() || password == null || password.isEmpty())
	    throw new BusinessException("error_userNameAndPasswordMandatory");

	WSSession sessionInfo = WSSessionsManagementService.makeNewSession(username, password);
	response.setSessionId(sessionInfo.getSessionId());
	response.setEmployeeId(sessionInfo.getEmpId());

	response.setMessage(BaseService.getMessage("notify_successOperation"));

	return response;
    }

}