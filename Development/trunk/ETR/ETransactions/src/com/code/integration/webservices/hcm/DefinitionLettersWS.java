package com.code.integration.webservices.hcm;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSWFDefinitionLetterTypeInfo;
import com.code.integration.responses.hcm.WSWFDefinitionLettersTypesResponse;
import com.code.integration.responses.util.WSPrintResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.workflow.hcm.DefinitionLettersWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/definitionLettersWS",
	portName = "DefinitionLettersWSHttpPort")
public class DefinitionLettersWS {

    @WebMethod(operationName = "getDefinitionLettersTypes")
    @WebResult(name = "definitionLettersTypesResponse")
    public WSWFDefinitionLettersTypesResponse getDefinitionLettersTypes(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSWFDefinitionLettersTypesResponse response = new WSWFDefinitionLettersTypesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    List<WSWFDefinitionLetterTypeInfo> definitionLetterList = new ArrayList<WSWFDefinitionLetterTypeInfo>();
	    definitionLetterList.add(new WSWFDefinitionLetterTypeInfo("10", BaseService.getMessage("label_definitionLettersDetailsOption1")));
	    definitionLetterList.add(new WSWFDefinitionLetterTypeInfo("20", BaseService.getMessage("label_definitionLettersDetailsOption2")));
	    definitionLetterList.add(new WSWFDefinitionLetterTypeInfo("30", BaseService.getMessage("label_definitionLettersDetailsOption3")));
	    definitionLetterList.add(new WSWFDefinitionLetterTypeInfo("40", BaseService.getMessage("label_definitionLettersDetailsOption4")));
	    definitionLetterList.add(new WSWFDefinitionLetterTypeInfo("50", BaseService.getMessage("label_definitionLettersDetailsOption5")));
	    definitionLetterList.add(new WSWFDefinitionLetterTypeInfo("60", BaseService.getMessage("label_definitionLettersDetailsOption6")));

	    response.setLettersTypes(definitionLetterList);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_getDefinitionLettersTypes"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

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
