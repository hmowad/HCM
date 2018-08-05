package com.code.integration.webservices.ereservations;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.exceptions.ClientIntegrationException;
import com.code.integration.responses.WSResponseBase;
import com.code.integration.responses.ereservations.LegData;
import com.code.integration.responses.ereservations.WSAirportsResponse;
import com.code.integration.responses.ereservations.WSBookingsResponse;
import com.code.integration.responses.ereservations.WSFlightsResponse;
import com.code.integration.responses.ereservations.WSMealsResponse;
import com.code.integration.webservicesclients.ereservations.EReservationsWSClient;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.integration.WSSessionsManagementService;

@WebService(targetNamespace = "http://integration.code.com/airlinesReservation",
	portName = "AirlinesReservationsWSHttpPort")
public class AirlinesReservationsWS {

    @WebMethod(operationName = "getAirports")
    @WebResult(name = "airportsResponse")
    public WSAirportsResponse getAirports(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSAirportsResponse response = new WSAirportsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
	    if (empData == null)
		throw new BusinessException("error_general");

	    response.setAirports(EReservationsWSClient.getAirports(empData.getSocialID()));
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(e instanceof ClientIntegrationException ? e.getMessage() : BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException || e instanceof ClientIntegrationException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getMeals")
    @WebResult(name = "mealsResponse")
    public WSMealsResponse getMeals(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSMealsResponse response = new WSMealsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setMeals(EReservationsWSClient.getMeals());
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getPreviousBookings")
    @WebResult(name = "previousBookingsResponse")
    public WSBookingsResponse getPreviousBookings(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {
	return getBookings(sessionId, employeeId, true);
    }

    @WebMethod(operationName = "getUpcomingBookings")
    @WebResult(name = "upcomingBookingsResponse")
    public WSBookingsResponse getUpcomingBookings(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {
	return getBookings(sessionId, employeeId, false);
    }

    private WSBookingsResponse getBookings(String sessionId, long employeeId, boolean previousBookingsFlag) {

	WSBookingsResponse response = new WSBookingsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
	    if (empData == null)
		throw new BusinessException("error_general");

	    response.setBookings(EReservationsWSClient.getBookings(empData.getSocialID(), previousBookingsFlag));
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(e instanceof ClientIntegrationException ? e.getMessage() : BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException || e instanceof ClientIntegrationException))
		e.printStackTrace();
	}
	return response;
    }

    @SuppressWarnings("unchecked")
    @WebMethod(operationName = "getFlights")
    @WebResult(name = "flightsResponse")
    public WSFlightsResponse getFlights(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "fromAirportId") long fromAirportId, @WebParam(name = "toAirportId") long toAirportId,
	    @WebParam(name = "departureDateString") String departureDateString, @WebParam(name = "returnDateString") String returnDateString) {

	WSFlightsResponse response = new WSFlightsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
	    if (empData == null)
		throw new BusinessException("error_general");

	    Object[] getFlightsResult = EReservationsWSClient.getFlights(empData.getSocialID(), fromAirportId, toAirportId, departureDateString, returnDateString);

	    response.setGoingLegs((List<LegData>) getFlightsResult[0]);
	    response.setReturnLegs((List<LegData>) getFlightsResult[1]);
	    response.setWaitingFlightFlag((boolean) getFlightsResult[2]);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(e instanceof ClientIntegrationException ? e.getMessage() : BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException || e instanceof ClientIntegrationException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "bookFlight")
    @WebResult(name = "bookFlightResponse")
    public WSResponseBase bookFlight(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "goingLegsIds") String goingLegsIds, @WebParam(name = "returnLegsIds") String returnLegsIds,
	    @WebParam(name = "waitingFlightFlag") boolean waitingFlightFlag, @WebParam(name = "contactPhone") String contactPhone,
	    @WebParam(name = "contactEmail") String contactEmail, @WebParam(name = "mealCode") String mealCode,
	    @WebParam(name = "needTransportationFlag") boolean needTransportationFlag) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
	    if (empData == null)
		throw new BusinessException("error_general");

	    EReservationsWSClient.bookFlight(empData.getSocialID(), goingLegsIds, returnLegsIds, waitingFlightFlag, contactPhone, contactEmail, mealCode, needTransportationFlag);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(e instanceof ClientIntegrationException ? e.getMessage() : BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException || e instanceof ClientIntegrationException))
		e.printStackTrace();
	}
	return response;
    }

}
