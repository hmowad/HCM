package com.code.integration.webservices.hcm;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.enums.FlagsEnum;
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
	Date travelDate = null;
	try {
	    if((FlagsEnum.OFF.getCode() + "").equals(VacationsService.getConfig("internalFlag")))
		throw new Exception();
	    
	    if (socialId == null || socialId.trim().isEmpty())
		throw new BusinessException("error_socialIDMandatory");
	    
	    if (socialId.length() != 10 || socialId.matches("[0-9]+"))
		throw new BusinessException("error_invalidSocialID");

	    if(travelDateString != null && travelDateString.length() != 10)
		throw new BusinessException("error_dateFormat");
	    
	    travelDate = HijriDateService.getHijriDate(travelDateString);
	    if (travelDate == null && travelDateString != null && !travelDateString.isEmpty()) {
		throw new BusinessException("error_dateFormat");
	    }
	    
	    EmployeeData employee = EmployeesService.getEmployeeBySocialID(socialId.trim());
	    if (employee == null)
		throw new BusinessException("error_socialIdNotFound");

	    VacationData externalVacationData = VacationsService.getExternalVacationData(employee.getEmpId(), travelDate);

	    response.setEmployeeName(employee.getName());
	    response.setSocialId(employee.getSocialID());
	    response.setRankDescription(employee.getRankDesc());
	    response.setMilitaryNumber(employee.getMilitaryNumber());
	    if (externalVacationData != null) {
		response.setDecisionNumber(externalVacationData.getDecisionNumber());
		response.setDecisionDateString(externalVacationData.getDecisionDateString());
		response.setPeriod(externalVacationData.getPeriod());
		response.setStartDateString(externalVacationData.getStartDateString());
		response.setEndDateString(externalVacationData.getEndDateString());
		response.setLocation(externalVacationData.getLocation());
		response.setMessage(BaseService.getMessage("notify_successOperation"));
	    } else
		response.setMessage(BaseService.getMessage("error_noMatchingVacationInThisDate"));

	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }
}
