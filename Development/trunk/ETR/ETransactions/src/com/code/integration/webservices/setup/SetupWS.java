package com.code.integration.webservices.setup;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.setup.Country;
import com.code.enums.FlagsEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.setup.WSCountriesResponse;
import com.code.services.BaseService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.util.CountryService;

@WebService(targetNamespace = "http://integration.code.com/setup",
	portName = "SetupWSHttpPort")
public class SetupWS {

    @WebMethod(operationName = "getCountries")
    @WebResult(name = "countriesResponse")
    public WSCountriesResponse getCountries(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "countryName") String countryName) {

	WSCountriesResponse response = new WSCountriesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    // validate the input.
	    if (countryName == null || countryName.length() < 3)
		throw new BusinessException("error_cannotSearchByLessThanThreeCharacters");

	    List<Country> countries = CountryService.getCountryByCountryDesc(countryName, FlagsEnum.OFF.getCode());
	    response.setCountries(countries);
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