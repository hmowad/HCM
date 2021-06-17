package com.code.integration.webservices.hcm;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.payroll.SummaryDifferenceData;
import com.code.dal.orm.hcm.payroll.SummaryDifferenceDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSPayrollDifferenceDetailResponse;
import com.code.integration.responses.hcm.WSPayrollDifferencesResponse;
import com.code.services.BaseService;
import com.code.services.hcm.PayrollsService;
import com.code.services.integration.WSSessionsManagementService;

@WebService(targetNamespace = "http://integration.code.com/payrollDifferences",
	portName = "PayrollDifferencesWSHttpPort")
public class PayrollDifferencesWS {

    @WebMethod(operationName = "getCurrentPayrollDifferences")
    @WebResult(name = "currentPayrollDifferencesResponse")
    public WSPayrollDifferencesResponse getCurrentPayrollDifferences(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "summaryCode") String summaryCode) {

	return searchPayrollDifferences(sessionId, employeeId, FlagsEnum.OFF.getCode(), summaryCode);
    }

    @WebMethod(operationName = "getPaidPayrollDifferences")
    @WebResult(name = "paidPayrollDifferencesResponse")
    public WSPayrollDifferencesResponse getPaidPayrollDifferences(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "summaryCode") String summaryCode) {

	return searchPayrollDifferences(sessionId, employeeId, FlagsEnum.ON.getCode(), summaryCode);
    }

    private WSPayrollDifferencesResponse searchPayrollDifferences(String sessionId, long employeeId, int mode, String summaryCode) {

	WSPayrollDifferencesResponse response = new WSPayrollDifferencesResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    List<SummaryDifferenceData> payrollDifferences = PayrollsService.getSummaryPayrollDifferencesBySummaryCodeAndEmployeeIdAndOrderStatus(summaryCode, employeeId, mode);
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

    @WebMethod(operationName = "getPayrollDifferenceDetails")
    @WebResult(name = "PayrollDifferenceDetailResponse")
    public WSPayrollDifferenceDetailResponse getPayrollDifferenceDetails(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "summaryCode") String summaryCode) {

	WSPayrollDifferenceDetailResponse response = new WSPayrollDifferenceDetailResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    List<SummaryDifferenceDetailData> summaryDifferenceDetailDataList = new ArrayList<SummaryDifferenceDetailData>();
	    if (summaryCode != null && !summaryCode.isEmpty())
		summaryDifferenceDetailDataList = PayrollsService.getSummaryDifferenceDetailDataBySummaryCodeAndEmployeeId(summaryCode, employeeId);

	    response.setSummaryDifferenceDetailDataList(summaryDifferenceDetailDataList);
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