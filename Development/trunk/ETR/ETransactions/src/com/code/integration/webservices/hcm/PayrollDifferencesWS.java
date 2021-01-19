package com.code.integration.webservices.hcm;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.payroll.EmployeePayrollDifferenceData;
import com.code.dal.orm.integration.finance.PaidIssueOrderData;
import com.code.enums.FlagsEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSPaidIssueOrderResponse;
import com.code.integration.responses.hcm.WSPayrollDifferencesResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.integration.FinanceService;
import com.code.services.integration.WSSessionsManagementService;

@WebService(targetNamespace = "http://integration.code.com/payrollDifferences",
	portName = "PayrollDifferencesWSHttpPort")
public class PayrollDifferencesWS {

    @WebMethod(operationName = "getCurrentPayrollDifferences")
    @WebResult(name = "currentPayrollDifferencesResponse")
    public WSPayrollDifferencesResponse getCurrentPayrollDifferences(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	return searchPayrollDifferences(sessionId, employeeId, FlagsEnum.OFF.getCode(), null);
    }

    @WebMethod(operationName = "getPaidPayrollDifferences")
    @WebResult(name = "paidPayrollDifferencesResponse")
    public WSPayrollDifferencesResponse getPaidPayrollDifferences(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "elementDescription") String elementDescription) {

	return searchPayrollDifferences(sessionId, employeeId, FlagsEnum.ON.getCode(), elementDescription);
    }

    private WSPayrollDifferencesResponse searchPayrollDifferences(String sessionId, long employeeId, int mode, String elementDescription) {

	WSPayrollDifferencesResponse response = new WSPayrollDifferencesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeeData employeeData = EmployeesService.getEmployeeData(employeeId);
	    if (employeeData == null)
		throw new BusinessException("error_employeeDataError");

	    List<EmployeePayrollDifferenceData> payrollDifferences = PayrollsService.getEmployeePayrollDifferences(employeeData.getEmpId(), mode, elementDescription);
	    response.setPayrollDifferences(payrollDifferences);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getPaidIssueOrder")
    @WebResult(name = "paidIssueOrderResponse")
    public WSPaidIssueOrderResponse getPaidIssueOrder(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "summarySerial") String summarySerial) {

	WSPaidIssueOrderResponse response = new WSPaidIssueOrderResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    PaidIssueOrderData paidIssueOrder = new PaidIssueOrderData();
	    if (summarySerial != null && !summarySerial.isEmpty())
		paidIssueOrder = FinanceService.getPaidIssueOrder(summarySerial);

	    response.setPaidIssueOrder(paidIssueOrder);
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