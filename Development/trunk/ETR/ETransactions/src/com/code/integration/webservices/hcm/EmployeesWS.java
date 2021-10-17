package com.code.integration.webservices.hcm;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeePhoto;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSEmployeePhotoResponse;
import com.code.integration.responses.hcm.WSEmployeeResponse;
import com.code.integration.responses.hcm.WSEmployeesResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.integration.WSSessionsManagementService;

@WebService(targetNamespace = "http://integration.code.com/employees",
	portName = "EmployeesWSHttpPort")
public class EmployeesWS {

    @WebMethod(operationName = "getEmployees")
    @WebResult(name = "employeesResponse")
    public WSEmployeesResponse getEmployees(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "employeeName") String employeeName, @WebParam(name = "socialId") String socialId,
	    @WebParam(name = "jobDescription") String jobDescription, @WebParam(name = "physicalUnitFullName") String physicalUnitFullName) {

	WSEmployeesResponse response = new WSEmployeesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    // validate the input.
	    if ((employeeName == null || employeeName.length() == 0) && (socialId == null || socialId.length() == 0) && (jobDescription == null || jobDescription.length() == 0) && (physicalUnitFullName == null || physicalUnitFullName.length() == 0))
		throw new BusinessException("error_oneSearchFieldNeededAtLeast");
	    if (socialId != null && socialId.length() != 0 && socialId.length() != 10)
		throw new BusinessException("error_socialIdNotValid");
	    if ((employeeName == null || employeeName.length() < 3) && (socialId == null || socialId.length() < 3) && (jobDescription == null || jobDescription.length() < 3) && (physicalUnitFullName == null || physicalUnitFullName.length() < 3))
		throw new BusinessException("error_cannotSearchByLessThanThreeCharacters");

	    EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
	    if (empData == null)
		throw new BusinessException("error_employeeDataError");

	    response.setEmployees(EmployeesService.getEmpByEmpName(socialId, employeeName, jobDescription, physicalUnitFullName, null, empData.getPhysicalRegionId() == null ? FlagsEnum.OFF.getCode() : empData.getPhysicalRegionId(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null));

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getEmployeeData")
    @WebResult(name = "employeeResponse")
    public WSEmployeeResponse getEmployeeData(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSEmployeeResponse response = new WSEmployeeResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
	    if (empData == null)
		throw new BusinessException("error_employeeDataError");

	    response.setEmployee(empData);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getEmployeePhoto")
    @WebResult(name = "employeePhotoResponse")
    public WSEmployeePhotoResponse getEmployeePhoto(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSEmployeePhotoResponse response = new WSEmployeePhotoResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeePhoto employeePhoto = EmployeesService.getEmployeePhotoByEmpId(employeeId);
	    response.setPhoto(employeePhoto != null ? employeePhoto.getPhoto() : null);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getEmployeesByPhysicalUnitHkey")
    @WebResult(name = "employeesByPhysicalUnitHkeyResponse")
    public WSEmployeesResponse getEmployeesByPhysicalUnitHkey(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "employeeName") String employeeName, @WebParam(name = "socialId") String socialId,
	    @WebParam(name = "jobDescription") String jobDescription, @WebParam(name = "physicalUnitFullName") String physicalUnitFullName) {

	WSEmployeesResponse response = new WSEmployeesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    // validate the input.
	    if ((employeeName == null || employeeName.length() == 0) && (socialId == null || socialId.length() == 0) && (jobDescription == null || jobDescription.length() == 0) && (physicalUnitFullName == null || physicalUnitFullName.length() == 0))
		throw new BusinessException("error_oneSearchFieldNeededAtLeast");
	    if (socialId != null && socialId.length() != 0 && socialId.length() != 10)
		throw new BusinessException("error_socialIdNotValid");
	    if ((employeeName == null || employeeName.length() < 3) && (socialId == null || socialId.length() < 3) && (jobDescription == null || jobDescription.length() < 3) && (physicalUnitFullName == null || physicalUnitFullName.length() < 3))
		throw new BusinessException("error_cannotSearchByLessThanThreeCharacters");

	    EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
	    if (empData == null)
		throw new BusinessException("error_employeeDataError");

	    Long[] statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(),
		    EmployeeStatusEnum.SUBJOINED.getCode(),
		    EmployeeStatusEnum.PERSONS_SUBJOINED.getCode(),
		    EmployeeStatusEnum.ASSIGNED.getCode(),
		    EmployeeStatusEnum.MANDATED.getCode(),
		    EmployeeStatusEnum.SECONDMENTED.getCode(),
		    EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(),
		    EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(),
		    EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()
	    };

	    String ancestorUnitHKey = calculateBeneficiaryAncestorUnitHKey(empData.getPhysicalUnitHKey());

	    response.setEmployees(EmployeesService.getEmployeesByPhysicalUnitHkeyNameAndStatusesID(socialId, employeeName, jobDescription, physicalUnitFullName, ancestorUnitHKey, statusIds, new Long[] { empData.getCategoryId() }, empData.getPhysicalRegionId() == null ? FlagsEnum.OFF.getCode() : empData.getPhysicalRegionId(), FlagsEnum.ALL.getCode(), Long.parseLong(FlagsEnum.ALL.getCode() + ""), Long.parseLong(FlagsEnum.ALL.getCode() + ""), Long.parseLong(FlagsEnum.ALL.getCode() + "")));

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    private String calculateBeneficiaryAncestorUnitHKey(String beneficiaryEmpPhysicalUnitHKey) {
	List<String> ancestorUnitsHKeys = UnitsService.getAncestorsHkeys(beneficiaryEmpPhysicalUnitHKey, 2);
	return ancestorUnitsHKeys.get(ancestorUnitsHKeys.size() - 1);
    }

}