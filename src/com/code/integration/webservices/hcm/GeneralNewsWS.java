package com.code.integration.webservices.hcm;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSGeneralNewsResponse;
import com.code.integration.responses.util.WSCountResponse;
import com.code.services.BaseService;
import com.code.services.hcm.GeneralNewsService;

@WebService(targetNamespace = "http://integration.code.com/generalNews",
	portName = "GeneralNewsWSHttpPort")
public class GeneralNewsWS {

    @WebMethod(operationName = "getGeneralNews")
    @WebResult(name = "generalNewsResponse")
    public WSGeneralNewsResponse getGeneralNews() {

	WSGeneralNewsResponse response = new WSGeneralNewsResponse();
	try {
	    // display the mandatory image only? (if yes, then the others should be XmlTransient)
	    response.setGeneralNews(GeneralNewsService.getGeneralNews());
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getGeneralNewsWithPagination")
    @WebResult(name = "generalNewsResponse")
    public WSGeneralNewsResponse getGeneralNewsWithPagination(@WebParam(name = "offset") Integer offset, @WebParam(name = "pageSize") Integer pageSize) {

	WSGeneralNewsResponse response = new WSGeneralNewsResponse();
	try {
	    response.setGeneralNews(GeneralNewsService.getGeneralNewsWithPagination(offset, pageSize));
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "getGeneralNewsCount")
    @WebResult(name = "generalNewsResponse")
    public WSCountResponse getGeneralNewsCount() {

	WSCountResponse response = new WSCountResponse();
	try {
	    response.setCount(GeneralNewsService.getGeneralNewsCount());
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

}