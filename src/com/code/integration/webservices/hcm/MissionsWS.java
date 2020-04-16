package com.code.integration.webservices.hcm;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSMissionsDetailsResponse;
import com.code.integration.responses.util.WSPrintResponse;
import com.code.services.BaseService;
import com.code.services.hcm.MissionsService;
import com.code.services.integration.WSSessionsManagementService;

@WebService(targetNamespace = "http://integration.code.com/missions",
	portName = "MissionsWSHttpPort")
public class MissionsWS {

    @WebMethod(operationName = "getMissionsDetails")
    @WebResult(name = "missionsDetailsResponse")
    public WSMissionsDetailsResponse getMissionsDetails(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSMissionsDetailsResponse response = new WSMissionsDetailsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    List<MissionDetailData> missionDetailList = MissionsService.getMissionsDetailsByEmpId(employeeId);

	    response.setMissionsDetails(missionDetailList);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "printMissionDecision")
    @WebResult(name = "printMissionDecisionResponse")
    public WSPrintResponse printMissionDecision(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "missionDetailId") long missionDetailId) {

	WSPrintResponse response = new WSPrintResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {

	    MissionDetailData missionDetail = MissionsService.getMissionsDetailById(missionDetailId);

	    if (missionDetail == null || missionDetail.getEmpId() != employeeId)
		throw new BusinessException("error_integrationError"); // TODO: to be discussed

	    MissionData mission = MissionsService.getMissionsById(missionDetail.getMissionId());
	    byte[] bytes = MissionsService.getMissionDecisionBytes(mission.getId(), mission.getCategoryMode());
	    response.setPrintBytes(bytes);
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
