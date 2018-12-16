package com.code.integration.webservices.hcm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.services.BaseService;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.Log4jService;

@WebService(targetNamespace = "http://integration.code.com/promotionsDrugsTest",
	portName = "PromotionsDrugsTestWSHttpPort")
public class PromotionsDrugsTestWS {

    @WebMethod(operationName = "adjustPromotionsDrugsTestResults")
    @WebResult(name = "promotionsDrugsTestResultsResponse")
    public WSResponseBase adjustPromotionsDrugsTestResults(@WebParam(name = "drugstestresults") String drugsTestResults) {

	Log4jService.traceInfo(PromotionsDrugsTestWS.class, "Start of PromotionsDrugsTestWS");
	WSResponseBase response = new WSResponseBase();
	try {
	    if (drugsTestResults == null || drugsTestResults.trim().isEmpty()) {
		Log4jService.traceError(PromotionsDrugsTestWS.class, "Empty or null message from infoSys");
		throw new BusinessException("error_general");
	    }

	    Log4jService.traceInfo(PromotionsDrugsTestWS.class, "drugTestResults: " + drugsTestResults);
	    PromotionsService.updatePromotionReportDetailsDrugsTestStatus(drugsTestResults);
	    Log4jService.traceInfo(PromotionsDrugsTestWS.class, "Promotion report details updated");
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
