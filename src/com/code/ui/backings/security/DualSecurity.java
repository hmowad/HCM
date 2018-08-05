package com.code.ui.backings.security;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.NavigationEnum;
import com.code.exceptions.BusinessException;
import com.code.services.security.SecurityService;

@ManagedBean(name = "dualSecurity")
@ViewScoped
public class DualSecurity extends SecurityBase implements Serializable {
    private String userName;
    private String securityKey;
    private String serverSideErrorMessages;
    private String serverSideSuccessMessages;

    public DualSecurity() {
	userName = (String) getRequest().getAttribute(USER_NAME_ATTRIBUTE);
	sendNewSecurityKey();
    }

    public void sendNewSecurityKey() {
	serverSideErrorMessages = "";
	SecurityService.sendSecurityKey(userName);
	constructDualSecuritySuccessMessage();
    }

    public String checkSecurityKey() {
	try {
	    if (SecurityService.checkSecurityKey(userName, securityKey)) {
		initializeSessionAttributes();
		return NavigationEnum.SUCCESS.toString();
	    } else {
		serverSideErrorMessages = getMessage("error_wrongSecurityKey");
		serverSideSuccessMessages = "";
		return NavigationEnum.FAILURE.toString();
	    }
	} catch (BusinessException e) {
	    serverSideErrorMessages = getMessage(e.getMessage());
	    serverSideSuccessMessages = "";
	    return NavigationEnum.FAILURE.toString();
	}
    }
    
    public void constructDualSecuritySuccessMessage() {
	String mobilrNumber = this.loginEmpData.getOfficialMobileNumber();
	int mobileNumberLength = mobilrNumber.length();
	String mobileNumberPostfix = this.loginEmpData.getOfficialMobileNumber().substring(Math.min(mobileNumberLength, Math.abs(mobileNumberLength - 4)));
	serverSideSuccessMessages = getParameterizedMessage("label_dualSecurityHeader", mobileNumberPostfix);
    }

    public String getSecurityKey() {
	return securityKey;
    }

    public void setSecurityKey(String securityKey) {
	this.securityKey = securityKey;
    }

    public String getServerSideErrorMessages() {
	return serverSideErrorMessages;
    }
    
    public String getServerSideSuccessMessages() {
        return serverSideSuccessMessages;
    }
}
