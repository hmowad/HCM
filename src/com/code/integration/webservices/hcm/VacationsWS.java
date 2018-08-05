package com.code.integration.webservices.hcm;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.enums.FlagsEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSVacationBalanceInquiryResponse;
import com.code.integration.responses.hcm.WSVacationResponse;
import com.code.integration.responses.hcm.WSVacationsResponse;
import com.code.integration.responses.util.WSPrintResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.VacationsService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.util.HijriDateService;

@WebService(targetNamespace = "http://integration.code.com/vacations",
	portName = "VacationsWSHttpPort")
public class VacationsWS {

    @WebMethod(operationName = "getVacations")
    @WebResult(name = "vacationsResponse")
    public WSVacationsResponse getVacations(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "vacationTypeId") long vacationTypeId) {

	WSVacationsResponse response = new WSVacationsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    if (vacationTypeId != VacationTypesEnum.REGULAR.getCode())
		throw new BusinessException("error_general");

	    List<Vacation> vacations = VacationsService.searchVacations(employeeId, FlagsEnum.ALL.getCode(), vacationTypeId, FlagsEnum.OFF.getCode());
	    response.setVacations(vacations);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getLastVacation")
    @WebResult(name = "lastVacationResponse")
    public WSVacationResponse getLastVacation(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "vacationTypeId") long vacationTypeId, @WebParam(name = "subVacationType") Integer subVacationType) {

	WSVacationResponse response = new WSVacationResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    VacationData lastVacationData = VacationsService.getLastVacationData(employeeId, vacationTypeId, subVacationType);
	    if (lastVacationData == null)
		throw new BusinessException("error_noPrevVacation");

	    Vacation lastVacation = new Vacation();
	    lastVacation.setStartDateString(lastVacationData.getStartDateString());
	    lastVacation.setEndDateString(lastVacationData.getEndDateString());
	    lastVacation.setPeriod(lastVacationData.getPeriod());

	    response.setVacation(lastVacation);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "inquireVacationBalance")
    @WebResult(name = "inquireVacationBalanceResponse")
    public WSVacationBalanceInquiryResponse inquireVacationBalance(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "vacationTypeId") long vacationTypeId, @WebParam(name = "balanceInquiryDateString") String balanceInquiryDateString) {

	WSVacationBalanceInquiryResponse response = new WSVacationBalanceInquiryResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	    String vacationBalance = VacationsService.inquireVacationBalance(employee, vacationTypeId, HijriDateService.getHijriDate(balanceInquiryDateString));

	    response.setVacationBalance(vacationBalance);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "printVacationDecision")
    @WebResult(name = "printVacationDecisionResponse")
    public WSPrintResponse printVacationDecision(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "vacationId") long vacationId) {

	WSPrintResponse response = new WSPrintResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    Vacation vacation = VacationsService.getVacationById(vacationId);
	    if (vacation == null || vacation.getEmpId() != employeeId)
		throw new BusinessException("error_general");

	    byte[] bytes = VacationsService.getVacationDecisionBytes(vacation.getVacationId(), vacation.getVacationTypeId(), vacation.getTransactionEmpCategoryId());
	    response.setPrintBytes(bytes);
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
