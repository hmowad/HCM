package com.code.integration.webservices.hcm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.enums.FlagsEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSExternalPartiesResponse;
import com.code.integration.responses.hcm.WSTrainingCourseResponse;
import com.code.services.BaseService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.integration.WSSessionsManagementService;

@WebService(targetNamespace = "http://integration.code.com/trainings",
	portName = "TrainingsWSHttpPort")
public class TrainingsWS {

    @WebMethod(operationName = "getTrainingCourses")
    @WebResult(name = "trainingCoursesResponse")
    public WSTrainingCourseResponse getTrainingCourses(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "courseName") String courseName, @WebParam(name = "courseType") int courseType) {

	WSTrainingCourseResponse response = new WSTrainingCourseResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setTrainingCoursesData(TrainingCoursesService.getTrainingCoursesByNameAndQualification(courseType, courseName, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null));
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getExternalParties")
    @WebResult(name = "externalPartiesResponse")
    public WSExternalPartiesResponse getExternalParties(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId, @WebParam(name = "partyDesc") String partyDesc,
	    @WebParam(name = "mode") int type, @WebParam(name = "partyAddress") String partyAddress) {
	WSExternalPartiesResponse response = new WSExternalPartiesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setTrainingExternalPartiesData(TrainingSetupService.getTrainingExternalParties(type, partyDesc, partyAddress));
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
