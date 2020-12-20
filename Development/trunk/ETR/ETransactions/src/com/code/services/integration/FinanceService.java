package com.code.services.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.integration.finance.PaidIssueOrderData;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;

public class FinanceService extends BaseService {

    private final static String PAID_ISSUE_ORDER_REQUEST_NUMBER_PREFIX = "107-";

    private FinanceService() {
    }

    /*---------------------------------- PaidIssueOrderData -----------------------------------*/
    public static PaidIssueOrderData getPaidIssueOrder(String summarySerial) throws BusinessException {
	String requestNumber = PAID_ISSUE_ORDER_REQUEST_NUMBER_PREFIX + summarySerial.toString();

	List<PaidIssueOrderData> issueOrderDataList = searchPaidIssueOrders(requestNumber);
	if (issueOrderDataList.isEmpty())
	    return null;
	return issueOrderDataList.get(0);
    }

    private static List<PaidIssueOrderData> searchPaidIssueOrders(String requestNumber) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REQUEST_NUMBER", requestNumber);
	    return DataAccess.executeNamedQuery(PaidIssueOrderData.class, QueryNamesEnum.FIN_GET_PAID_ISSUE_ORDERS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
