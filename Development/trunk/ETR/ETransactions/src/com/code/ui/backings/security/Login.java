package com.code.ui.backings.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.NavigationEnum;
import com.code.enums.SessionAttributesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesPrefrencesService;
import com.code.services.security.SecurityService;

@ManagedBean(name = "login")
@ViewScoped
public class Login extends SecurityBase implements Serializable {
    private String username;
    private String password;
    private String serverSideErrorMessages;

    public void init() {
	super.init();
	username = null;
	password = null;

	loginFromExternalChannel();
    }

    public String login() {
	try {
	    EmployeeData empData = SecurityService.authenticateUser(username, password);
	    HttpSession session = getSession();
	    session.setAttribute(SessionAttributesEnum.EMP_DATA.getCode(), empData);
	    session.setAttribute(SessionAttributesEnum.TIME_LINE_MINI_SEARCH_SHOW_FLAG.getCode(), true);
	    EmployeesPrefrencesService.addEmployeePrefrencesIfNotExist(empData.getEmpId());

	    if (SecurityService.isDualSecurityEnabled()) {
		if (empData.getOfficialMobileNumber() != null) {
		    getRequest().setAttribute(USER_NAME_ATTRIBUTE, username);
		    return NavigationEnum.DUAL_SECURITY.toString();
		} else {
		    serverSideErrorMessages = getMessage("error_missingMobileNumber");
		}
	    } else {
		initializeSessionAttributes();
		return NavigationEnum.SUCCESS.toString();
	    }
	} catch (BusinessException e) {
	    serverSideErrorMessages = getMessage(e.getMessage());
	}
	return NavigationEnum.FAILURE.toString();
    }

    public void loginFromExternalChannel() {
	String wsSessionId;
	String employeeId;
	String navigationOption;
	try {
	    if (getRequest().getParameter("wsSessionId") != null) {
		wsSessionId = getRequest().getParameter("wsSessionId");
		employeeId = getRequest().getParameter("employeeId");
		navigationOption = getRequest().getParameter("navigationOption");

		EmployeeData empData = SecurityService.authenticateUserFromExternalChannel(wsSessionId, Long.parseLong(employeeId));

		if (navigationOption.equals("INBOX")) {
		    getSession().setAttribute(SessionAttributesEnum.EMP_DATA.getCode(), empData);
		    initializeSessionAttributes();
		    redirect("/WorkList/WFInbox.jsf?init=1");
		} else if (navigationOption.equals("HOME")) {
		    getSession().setAttribute(SessionAttributesEnum.EMP_DATA.getCode(), empData);
		    initializeSessionAttributes();
		    redirect("/Main/Welcome.jsf");
		} else
		    throw new Exception();
	    }
	} catch (BusinessException e) {
	    serverSideErrorMessages = getMessage(e.getMessage());
	} catch (Exception e) {
	    serverSideErrorMessages = getMessage("error_general");
	}
    }

    private void redirect(String viewURI) throws IOException {
	getFacesContext().getExternalContext().redirect(getFacesContext().getExternalContext().getRequestContextPath() + viewURI);
    }

    public String getVersionNumber() {
	return BaseService.getConfig("versionNumber");
    }

    public String getVersionYear() {
	return getParameterizedMessage("label_copyright", Calendar.getInstance().get(Calendar.YEAR) + "");
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getUsername() {
	return username;
    }

    public String getServerSideErrorMessages() {
	return serverSideErrorMessages;
    }

}