package com.code.integration.webservices.workflow.hcm;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.dal.orm.workflow.hcm.movements.WFMovementWish;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.integration.responses.workflow.hcm.WSWFMovementResponse;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.hcm.MovementsWishesWorkFlow;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/movements",
	portName = "MovementsWorkFlowWSHttpPort")
public class MovementsWorkFlowWS {

    @WebMethod(operationName = "initMoveRequest")
    @WebResult(name = "initMoveRequestResponse")
    public WSResponseBase initMoveRequest(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "unitId") long unitId, @WebParam(name = "reasons") String reasons) {

	return initMovementsWorkFlow(MovementTypesEnum.MOVE.getCode(), sessionId, employeeId, unitId, reasons, null, FlagsEnum.OFF.getCode(), FlagsEnum.OFF.getCode());

    }

    @WebMethod(operationName = "initSubjoinRequest")
    @WebResult(name = "initSubjoinRequestResponse")
    public WSResponseBase initSubjoinRequest(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "unitId") long unitId, @WebParam(name = "startDateString") String startDateString, @WebParam(name = "periodMonths") Integer periodMonths, @WebParam(name = "periodDays") Integer periodDays, @WebParam(name = "reasons") String reasons) {

	return initMovementsWorkFlow(MovementTypesEnum.SUBJOIN.getCode(), sessionId, employeeId, unitId, reasons, startDateString, periodMonths, periodDays);
    }

    private WSResponseBase initMovementsWorkFlow(long movementTypeId, String sessionId, long requesterId, long unitId, String reasons, String startDateString, Integer periodMonths, Integer periodDays) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, requesterId, response))
	    return response;

	try {
	    String[] proceeeIdAndTaskURL = null;
	    WFMovementData movementRequest = null;
	    EmployeeData requester = EmployeesService.getEmployeeData(requesterId);

	    if (movementTypeId == MovementTypesEnum.MOVE.getCode()) {
		proceeeIdAndTaskURL = getProcessIdAndTaskURL(requester.getCategoryId(), MovementTypesEnum.MOVE.getCode()).split(",");
		movementRequest = MovementsWorkFlow.constructWFMovement(Long.parseLong(proceeeIdAndTaskURL[0]), requesterId, null, FlagsEnum.OFF.getCode(), null, null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode(), null, null, null, MovementTypesEnum.MOVE.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
		movementRequest.setUnitId(unitId);
		movementRequest.setReasons(reasons);

	    } else if (movementTypeId == MovementTypesEnum.SUBJOIN.getCode()) {
		proceeeIdAndTaskURL = getProcessIdAndTaskURL(requester.getCategoryId(), MovementTypesEnum.SUBJOIN.getCode()).split(",");
		movementRequest = MovementsWorkFlow.constructWFMovement(Long.parseLong(proceeeIdAndTaskURL[0]), requesterId, null, FlagsEnum.OFF.getCode(), HijriDateService.getHijriDate(startDateString), null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode(), null, null, null, MovementTypesEnum.SUBJOIN.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
		movementRequest.setPeriodMonths(periodMonths);
		movementRequest.setPeriodDays(periodDays);
		if (startDateString != null && ((periodMonths != null && periodMonths > 0) || (periodDays != null && periodDays > 0))) {
		    movementRequest.setEndDate(HijriDateService.getHijriDate(HijriDateService.addSubStringHijriMonthsDays(startDateString, periodMonths == null ? 0 : periodMonths, periodDays == null ? -1 : periodDays - 1)));
		}
		movementRequest.setUnitId(unitId);
		movementRequest.setReasons(reasons);
	    }
	    List<WFMovementData> movementRequests = new ArrayList<WFMovementData>();
	    movementRequests.add(movementRequest);

	    MovementsWorkFlow.initMovement(requester, movementRequests, Long.parseLong(proceeeIdAndTaskURL[0]), null, proceeeIdAndTaskURL[1], null, null);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());

	    if (e instanceof BusinessException)
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    else {
		response.setMessage(BaseService.getMessage("error_integrationError"));
		e.printStackTrace();
	    }
	}
	return response;

    }

    private static String getProcessIdAndTaskURL(long categoryId, long movementTypeId) {
	long processId = 0;
	int mode = 0;
	String screenName = "";

	if (movementTypeId == MovementTypesEnum.MOVE.getCode()) {
	    screenName = "MoveRequest";
	    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
		mode = 1;
		processId = WFProcessesEnum.OFFICERS_MOVE_REQUEST.getCode();
	    } else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
		mode = 2;
		processId = WFProcessesEnum.SOLDIERS_MOVE_REQUEST.getCode();
	    }
	} else if (movementTypeId == MovementTypesEnum.SUBJOIN.getCode()) {
	    screenName = "SubjoinRequest";
	    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
		mode = 1;
		processId = WFProcessesEnum.OFFICERS_SUBJOIN_REQUEST.getCode();
	    } else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
		mode = 2;
		processId = WFProcessesEnum.SOLDIERS_SUBJOIN_REQUEST.getCode();
	    }
	}

	String taskUrl = "/Movements/" + screenName + ".jsf?mode=" + mode + "&rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();
	return processId + "," + taskUrl;
    }

    @WebMethod(operationName = "doMovementSM")
    @WebResult(name = "wfMovementSMResponse")
    public WSResponseBase doMovementSM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId, @WebParam(name = "taskId") long taskId, @WebParam(name = "approvalFlag") int approvalFlag, @WebParam(name = "notes") String notes, @WebParam(name = "refuseReasons") String refuseReasons) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    BaseWorkFlow.validateWFTaskRefuseReasonsAndNotes(approvalFlag, refuseReasons, notes);

	    WFTask smTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != smTask.getAssigneeId() || smTask.getAction() != null || !smTask.getAssigneeWfRole().equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()))
		// TODO: Change error message
		throw new BusinessException("error_integrationError");

	    if (notes != null && !notes.trim().isEmpty())
		smTask.setNotes(notes);

	    if (approvalFlag == WFActionFlagsEnum.REJECT.getCode())
		smTask.setRefuseReasons(refuseReasons);

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(smTask.getInstanceId());
	    EmployeeData requester = EmployeesService.getEmployeeData(instance.getRequesterId());
	    List<WFMovementData> movementRequests = MovementsWorkFlow.getWFMovementDataByInstanceId(instance.getInstanceId());

	    MovementsWorkFlow.doMovementSM(requester, instance, movementRequests, smTask, approvalFlag);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getWFMovement")
    @WebResult(name = "wfMovementResponse")
    public WSWFMovementResponse getWFMovement(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId, @WebParam(name = "taskId") long taskId) {

	WSWFMovementResponse response = new WSWFMovementResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    WFTask wfTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != wfTask.getAssigneeId())
		// TODO: Change error message
		throw new BusinessException("error_integrationError");

	    List<WFMovementData> wfMovementDataList = MovementsWorkFlow.getWFMovementDataByInstanceId(wfTask.getInstanceId());

	    if (wfMovementDataList.size() == 0 || !wfMovementDataList.get(0).getMovementTypeId().equals(MovementTypesEnum.ASSIGNMENT.getCode()))
		// TODO: Change error message
		throw new BusinessException("error_integrationError");

	    response.setWfMovementDataList(wfMovementDataList);

	    response.setVerbalOrderFlag(wfMovementDataList.get(0).getVerbalOrderFlag());
	    response.setVerbalOrderMessage(BaseService.getMessage(response.getVerbalOrderFlag().equals(FlagsEnum.ON.getCode()) ? "label_verbalOrder" : "label_letterDispatchEtc"));
	    response.setBasedOn(wfMovementDataList.get(0).getBasedOn());
	    response.setBasedOnOrderDateString(wfMovementDataList.get(0).getBasedOnOrderDateString());
	    response.setBasedOnOrderNumber(wfMovementDataList.get(0).getBasedOnOrderNumber());
	    response.setRemarks(wfMovementDataList.get(0).getRemarks());

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    /********************************************** MoveWishRequestWS **************************************/

    @WebMethod(operationName = "initWFMovementWish")
    @WebResult(name = "initWFMovementWishResponse")
    public WSResponseBase initWFMovementWish(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId, @WebParam(name = "reasons") String reasons, @WebParam(name = "toRegionId") long toRegionId) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    long processId = WFProcessesEnum.SOLDIERS_MOVE_WISH_REQUEST.getCode();
	    EmployeeData requester = EmployeesService.getEmployeeData(employeeId);
	    Object[] wfMovementWishAndAttachment = MovementsWishesWorkFlow.constructWfMovementWish(employeeId, processId);
	    WFMovementWish wfMovementWish = (WFMovementWish) wfMovementWishAndAttachment[0];
	    wfMovementWish.setToRegionId(toRegionId);
	    wfMovementWish.setReasons(reasons);
	    String taskUrl = "/Movements/MoveWishRequest.jsf?rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();
	    MovementsWishesWorkFlow.initWFMovementWish(requester, wfMovementWish, null, processId, taskUrl);
	    response.setMessage(BaseService.getMessage("notify_requestSent"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());

	    if (e instanceof BusinessException)
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    else {
		response.setMessage(BaseService.getMessage("error_integrationError"));
		e.printStackTrace();
	    }
	}
	return response;
    }

}
