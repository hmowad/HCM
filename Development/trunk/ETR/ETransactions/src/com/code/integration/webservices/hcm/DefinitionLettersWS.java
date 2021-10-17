package com.code.integration.webservices.hcm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.util.WSPrintResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.workflow.hcm.DefinitionLettersWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/definitionLettersWS",
	portName = "DefinitionLettersWSHttpPort")
public class DefinitionLettersWS {

    @WebMethod(operationName = "printDefinitionLetter")
    @WebResult(name = "printDefinitionLetterResponse")
    public WSPrintResponse printDefinitionLetter(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "letterType") String letterType, @WebParam(name = "embassyId") Long embassyId, @WebParam(name = "employeeNameEn") String employeeNameEn, @WebParam(name = "onOfficialPaper") Integer onOfficialPaper) {

	WSPrintResponse response = new WSPrintResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    if (("30").equals(letterType)) {

		EmployeeData empData = EmployeesService.getEmployeeData(employeeId);
		boolean employeeHasNameEn = (empData.getFirstNameEn() == null || empData.getFirstNameEn().isEmpty()) ? false : true;

		if (!employeeHasNameEn) {
		    // validate employeeNameEn
		    if (employeeNameEn == null || employeeNameEn.isEmpty())
			throw new BusinessException("error_nameEnMandatory");
		}
	    }

	    byte[] bytes = DefinitionLettersWorkFlow.getDefinitionLetterBytes(letterType, employeeId, embassyId, employeeNameEn, onOfficialPaper);

	    response.setPrintBytes(bytes);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_printDefinitionLetter"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

}
