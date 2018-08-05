package com.code.integration.webservices.hcm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSRegionsResponse;
import com.code.integration.responses.hcm.WSUnitsResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.util.CommonService;

@WebService(targetNamespace = "http://integration.code.com/units",
	portName = "UnitsWSHttpPort")
public class UnitsWS {

    @WebMethod(operationName = "getRegions")
    @WebResult(name = "regionsResponse")
    public WSRegionsResponse getRegions(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSRegionsResponse response = new WSRegionsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    response.setRegions(CommonService.getAllRegions());
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getRegionsAndSectors")
    @WebResult(name = "regionsAndSectorsResponse")
    public WSUnitsResponse getRegionsAndSectors(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "unitName") String unitName) {

	WSUnitsResponse response = new WSUnitsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    // validate the input.
	    if (unitName == null || unitName.length() < 3)
		throw new BusinessException("error_cannotSearchByLessThanThreeCharacters");

	    response.setUnits(UnitsService.getRegionsAndSectorsByName(unitName));
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getUnits")
    @WebResult(name = "unitsResponse")
    public WSUnitsResponse getUnits(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "unitName") String unitName) {

	WSUnitsResponse response = new WSUnitsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    // validate the input.
	    if (unitName == null || unitName.length() < 3)
		throw new BusinessException("error_cannotSearchByLessThanThreeCharacters");

	    EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
	    if (empData == null)
		throw new BusinessException("error_general");

	    response.setUnits(UnitsService.getUnitsByRegion(empData.getPhysicalRegionId() == null ? FlagsEnum.OFF.getCode() : (empData.getPhysicalRegionId().longValue() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : empData.getPhysicalRegionId()), unitName));

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