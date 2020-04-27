package com.code.integration.webservices.workflow.generalnotifications;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.enums.WFTaskRolesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.util.WSAttachmentsResponse;
import com.code.integration.responses.workflow.generalnotifications.WSGeneralMessageResponse;
import com.code.integration.responses.workflow.generalnotifications.WSNotificationMessageResponse;
import com.code.services.BaseService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.util.AttachmentsService;
import com.code.services.workflow.BaseWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/generalNotifications",
	portName = "GeneralNotificationsWorkFlowWSHttpPort")
public class GeneralNotificationsWorkFlowWS {

    @WebMethod(operationName = "getNotificationMessage")
    @WebResult(name = "notificationMessageResponse")
    public WSNotificationMessageResponse getNotificationMessage(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId) {

	return getGeneralNotificationMessage(sessionId, employeeId, taskId);
    }

    @WebMethod(operationName = "getGeneralMessage")
    @WebResult(name = "generalMessageResponse")
    public WSGeneralMessageResponse getGeneralMessage(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId) {

	WSNotificationMessageResponse notificationResponse = getGeneralNotificationMessage(sessionId, employeeId, taskId);
	WSGeneralMessageResponse response = new WSGeneralMessageResponse();
	response.setGeneralMessage(notificationResponse.getNotificationMessage());
	response.setMessage(notificationResponse.getMessage());
	return response;
    }

    private WSNotificationMessageResponse getGeneralNotificationMessage(String sessionId, long employeeId, long taskId) {
	WSNotificationMessageResponse response = new WSNotificationMessageResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    WFTask task = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != task.getAssigneeId() || task.getAction() != null || !task.getAssigneeWfRole().equals(WFTaskRolesEnum.NOTIFICATION.getCode()))
		// TODO:To Be Discussed
		throw new BusinessException("error_integrationError");

	    response.setNotificationMessage(task.getFlexField1());
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getAttachmentsURL")
    @WebResult(name = "attachmentsURLResponse")
    public WSAttachmentsResponse getAttachmentsURL(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId) {

	WSAttachmentsResponse response = new WSAttachmentsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    WFTask task = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != task.getAssigneeId())
		// To Be Discussed
		throw new BusinessException("error_integrationError");

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(task.getInstanceId());

	    response.setUrl(AttachmentsService.getAttachmentsViewURL(instance.getAttachments(), employeeId));
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }
}