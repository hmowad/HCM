package com.code.integration.webservices.hcm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.log4j.Logger;

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
	Logger log = Logger.getLogger(PromotionsDrugsTestWS.class.getName());
	try {
	    if (drugsTestResults == null || drugsTestResults.trim().isEmpty()) {
		log.info("empty or null message");
		throw new BusinessException("error_general");
	    }

	    log.info("msg:" + drugsTestResults);
	    PromotionsService.updatePromotionReportDetailsDrugsTestStatus(drugsTestResults);
	    log.info("updated");
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
