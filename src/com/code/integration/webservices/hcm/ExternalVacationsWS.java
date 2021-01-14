package com.code.integration.webservices.hcm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSExternalVacationResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.VacationsService;
import com.code.services.util.HijriDateService;

@WebService(targetNamespace = "http://integration.code.com/externalVacations",
	portName = "ExternalVacationsWSHttpPort")
public class ExternalVacationsWS {

    @WebMethod(operationName = "getExternalVacation")
    @WebResult(name = "externalVacationResponse")
    public WSExternalVacationResponse getExternalVacation(@WebParam(name = "socialId") String socialId, @WebParam(name = "travelDateString") String travelDateString) {
	WSExternalVacationResponse response = new WSExternalVacationResponse();
	try {
	    if (socialId == null || socialId.isEmpty())
		throw new BusinessException("error_socialIdNotFound");
	    EmployeeData employee = EmployeesService.getEmployeeBySocialID(socialId);

	    VacationData externalVacationData = VacationsService.getExternalVacationData(employee.getEmpId(), HijriDateService.getHijriDate(travelDateString));

	    response.setEmployeeName(employee.getName());
	    response.setSocialId(employee.getSocialID());
	    response.setRankDescription(employee.getRankDesc());
	    response.setMilitaryNumber(employee.getMilitaryNumber());
	    response.setDecisionNumber(externalVacationData.getDecisionNumber());
	    response.setDecisionDateString(externalVacationData.getDecisionDateString());
	    response.setPeriod(externalVacationData.getPeriod());
	    response.setStartDateString(externalVacationData.getStartDateString());
	    response.setEndDateString(externalVacationData.getEndDateString());
	    response.setLocation(externalVacationData.getLocation());

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
