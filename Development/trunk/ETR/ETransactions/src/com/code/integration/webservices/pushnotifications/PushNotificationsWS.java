package com.code.integration.webservices.pushnotifications;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.integration.webservicesclients.pushclient.PushNotificationRestClient;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.generalnotifications.GeneralNotificationsWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/pushnotifications",
	portName = "PushNotificationsWSHttpPort")
public class PushNotificationsWS {

    @WebMethod(operationName = "pushNotificationsByNotifiersIds")
    @WebResult(name = "pushNotificationsResponse")
    public WSResponseBase pushNotificationsByNotifiersIds(@WebParam(name = "pushNotificationsServiceAuthCode") String pushNotificationsServiceAuthCode, @WebParam(name = "pushNotificationsServiceAuthValue") String pushNotificationsServiceAuthValue, @WebParam(name = "notifiersIds") String notifiersIds, @WebParam(name = "message") String message) {

	WSResponseBase response = new WSResponseBase();
	try {
	    if (message == null || message.isEmpty() || notifiersIds == null || notifiersIds.isEmpty())
		throw new Exception();

	    pushNotifications(pushNotificationsServiceAuthCode, pushNotificationsServiceAuthValue, notifiersIds, message);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "pushNotificationsByNotifiersSocialIds")
    @WebResult(name = "pushNotificationsResponse")
    public WSResponseBase pushNotificationsByNotifiersSocialIds(@WebParam(name = "pushNotificationsServiceAuthCode") String pushNotificationsServiceAuthCode, @WebParam(name = "pushNotificationsServiceAuthValue") String pushNotificationsServiceAuthValue, @WebParam(name = "notifiersSocialIds") String notifiersSocialIds, @WebParam(name = "message") String message) {

	WSResponseBase response = new WSResponseBase();
	try {
	    if (message == null || message.isEmpty() || notifiersSocialIds == null || notifiersSocialIds.isEmpty())
		throw new Exception();

	    StringBuilder notifiersIds = new StringBuilder();
	    String[] notifiersSocialIdsArray = notifiersSocialIds.split(",");
	    for (String notifierSocialId : notifiersSocialIdsArray)
		notifiersIds.append(EmployeesService.getEmployeeBySocialID(notifierSocialId).getEmpId()).append(",");

	    pushNotifications(pushNotificationsServiceAuthCode, pushNotificationsServiceAuthValue, notifiersIds.substring(0, notifiersIds.length()), message);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    private void pushNotifications(String pushNotificationsServiceAuthCode, String pushNotificationsServiceAuthValue, String notifiersIds, String message) throws BusinessException {
	SecurityService.authenticateExternalService(pushNotificationsServiceAuthCode, pushNotificationsServiceAuthValue);

	GeneralNotificationsWorkFlow.sendGeneralMessages(notifiersIds, message);

	String[] notifiersIdsArray = notifiersIds.split(",");
	for (String notifierId : notifiersIdsArray)
	    PushNotificationRestClient.pushNotificationMessage(Long.parseLong(notifierId), message);
    }
}