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

@WebService(targetNamespace = "http://integration.code.com/promotionsDrugsTest",
	portName = "PromotionsDrugsTestWSHttpPort")
public class PromotionsDrugsTestWS {

    @WebMethod(operationName = "adjustPromotionsDrugsTestResults")
    @WebResult(name = "promotionsDrugsTestResultsResponse")
    public WSResponseBase adjustPromotionsDrugsTestResults(@WebParam(name = "drugstestresults") String drugsTestResults) {

	WSResponseBase response = new WSResponseBase();

	try {
	    if (drugsTestResults == null || drugsTestResults.trim().isEmpty()) {
		System.out.println("error_general: empty or null message");
		throw new BusinessException("error_general");
	    }

	    System.out.println("msg:" + drugsTestResults);
	    PromotionsService.updatePromotionReportDetailsDrugsTestStatus(drugsTestResults);
	    System.out.println("updated");
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
