package com.code.integration.webservices.config;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.config.WSConfigurationURLItem;
import com.code.integration.responses.config.WSConfigurationURLsResponse;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;

@WebService(targetNamespace = "http://integration.code.com/config",
	portName = "ConfigWSHttpPort")
public class ETRConfigurationWS {

    private enum ConfigCodesEnum {

	MANAGERS("HCM_MANAGERS_URL"), // https://fg.gov.sa/Arabic/Managers.aspx
	TASKS("HCM_TASKS_URL"), // https://fg.gov.sa/Arabic/Tasks.aspx
	ABOUT_US("HCM_ABOUT_US_URL"), // https://fg.gov.sa/Arabic/AboutUs.aspx
	BORDER_CROSSINGS("HCM_BORDER_CROSSINGS_URL"), // https://fg.gov.sa/Arabic/BorderCrossings.aspx

	PORT_SECURITY_SERVICES("HCM_PORT_SECURITY_SERVICES_URL"), // https://www.zawil.com.sa/bg2/#/login
	FISHING_AND_PICNIC_SERVICES("HCM_FISHING_AND_PICNIC_SERVICES_URL"), // https://www.zawil.com.sa/fne/#/ar/web/login
	// ZAWIL("HCM_ZAWIL_URL"), // https://www.zawil.com.sa/
	EXPERIENCES("HCM_EXPERIENCES_URL"), // https://eservices.fg.gov.sa/WCV
	JOBS("HCM_JOBS_URL"), // https://jobs.fg.gov.sa/
	BORDER_PERMISSION_REQUEST("HCM_BORDER_PERMISSION_REQUEST_URL"), // https://fg.gov.sa/Arabic/BorderPermissionRequestCompany.aspx
	SEARCH_EMPLOYEE("HCM_SEARCH_EMPLOYEE_URL"), // https://fg.gov.sa/Arabic/SearchEmployee.aspx

	MARITIME_SAFETY_INFORMATION("HCM_MARITIME_SAFETY_INFORMATION_URL"), // https://fg.gov.sa/Arabic/MaritimeSafety_Informations.aspx
	MARITIME_SAFETY_GALLERY_DETAILS("HCM_MARITIME_SAFETY_GALLERY_DETAILS_URL"), // https://www.fg.gov.sa/Arabic/MaritimeSafety.aspx
	HCM_DUTY_MARTYRS_URL("HCM_DUTY_MARTYRS_URL"), // https://www.fg.gov.sa/Arabic/DutyMartyrs.aspx

	TWITTER("HCM_TWITTER_URL"), // http://www.twitter.com/bg994/
	YOUTUBE("HCM_YOUTUBE_URL"), // https://www.youtube.com/bg994/
	CONTACT_US("HCM_CONTACT_US_URL"), // https://fg.gov.sa/Arabic/ContactUs.aspx

	FG_EMAIL("HCM_EMAIL_URL"), // https://mail.fg.gov.sa

	FG_ERESERVATION("HCM_ERESERVATION_URL"); // https://eservices.fg.gov.sa/EReservation/Main/Login.jsf

	private String code;

	private ConfigCodesEnum(String code) {
	    this.code = code;
	}

	public String getCode() {
	    return code;
	}
    }

    /**************************************************************************/

    @WebMethod(operationName = "getEServicesURLs")
    @WebResult(name = "eServicesURLsResponse")
    public WSConfigurationURLsResponse getEServicesURLs() {

	WSConfigurationURLsResponse response = new WSConfigurationURLsResponse();
	try {
	    List<WSConfigurationURLItem> configurationURLs = new ArrayList<WSConfigurationURLItem>();

	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.MANAGERS.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.MANAGERS.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.TASKS.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.TASKS.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.ABOUT_US.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.ABOUT_US.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.BORDER_CROSSINGS.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.BORDER_CROSSINGS.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.PORT_SECURITY_SERVICES.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.PORT_SECURITY_SERVICES.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.FISHING_AND_PICNIC_SERVICES.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.FISHING_AND_PICNIC_SERVICES.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.EXPERIENCES.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.EXPERIENCES.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.JOBS.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.JOBS.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.BORDER_PERMISSION_REQUEST.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.BORDER_PERMISSION_REQUEST.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.SEARCH_EMPLOYEE.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.SEARCH_EMPLOYEE.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.MARITIME_SAFETY_INFORMATION.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.MARITIME_SAFETY_INFORMATION.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.MARITIME_SAFETY_GALLERY_DETAILS.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.MARITIME_SAFETY_GALLERY_DETAILS.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.HCM_DUTY_MARTYRS_URL.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.HCM_DUTY_MARTYRS_URL.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.TWITTER.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.TWITTER.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.YOUTUBE.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.YOUTUBE.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.CONTACT_US.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.CONTACT_US.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.FG_EMAIL.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.FG_EMAIL.getCode())));
	    configurationURLs.add(new WSConfigurationURLItem(ConfigCodesEnum.FG_ERESERVATION.getCode(),
		    ETRConfigurationService.getETRConfigValueByCode(ConfigCodesEnum.FG_ERESERVATION.getCode())));

	    response.setConfigurationURLs(configurationURLs);
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