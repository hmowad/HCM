package com.code.integration.webservices.hcm;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.pushServer.PushServerLog;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.services.BaseService;
import com.code.services.hcm.PushServerService;
import com.code.services.integration.WSSessionsManagementService;

@WebService(targetNamespace = "http://integration.code.com/pushServer",
	portName = "PushServerWSHttpPort")
public class PushServerWS {

    @WebMethod(operationName = "logPushServerToken")
    @WebResult(name = "logPushServerTokenResponse")
    public WSResponseBase logPushServerToken(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "operation") int operation, @WebParam(name = "deviceToken") String deviceToken, @WebParam(name = "deviceType") String deviceType,
	    @WebParam(name = "osName") String osName, @WebParam(name = "osVersion") String osVersion) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    PushServerLog pushServerLog = new PushServerLog();
	    pushServerLog.setEmployeeId(employeeId);
	    pushServerLog.setOperation(operation); // 1:Login, 0:Logout
	    pushServerLog.setOperationDate(new Date());
	    pushServerLog.setDeviceToken(deviceToken);
	    pushServerLog.setDeviceType(deviceType);
	    pushServerLog.setOsName(osName);
	    pushServerLog.setOsVersion(osVersion);

	    PushServerService.logPushServerToken(pushServerLog);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

}