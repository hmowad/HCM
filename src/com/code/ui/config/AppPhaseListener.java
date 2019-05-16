package com.code.ui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.security.Menu;
import com.code.enums.SessionAttributesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.security.SecurityService;

public class AppPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    public void afterPhase(PhaseEvent phaseEvent) {
    }

    public PhaseId getPhaseId() {
	return PhaseId.ANY_PHASE;
    }

    @SuppressWarnings("unchecked")
    public void beforePhase(PhaseEvent phaseEvent) {
	if (PhaseId.RESTORE_VIEW.compareTo(phaseEvent.getPhaseId()) == 0) {
	    HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	    if (req.getParameterMap().containsKey("user") && req.getParameterMap().get("user") != null && req.getParameterMap().get("user")[0].length() != 0) {
		try {
		    String userAccountDecrypted = req.getParameterMap().get("user")[0];
		    String userAccount = SecurityService.decryptAES(userAccountDecrypted);
		    EmployeeData empData = EmployeesService.getEmployeeByUserAccount(userAccount);

		    HttpSession session = req.getSession();
		    session.setAttribute(SessionAttributesEnum.EMP_DATA.getCode(), empData);
		    if (empData.getManagerId() != null) {
			session.setAttribute(SessionAttributesEnum.USER_TRANSACTIONS_MENU.getCode(), SecurityService.getEmployeeMenus(empData.getEmpId(), 1));
			session.setAttribute(SessionAttributesEnum.USER_WORKFLOWS_MENU.getCode(), SecurityService.getEmployeeMenus(empData.getEmpId(), 2));
			session.setAttribute(SessionAttributesEnum.USER_REPORTS_MENU.getCode(), SecurityService.getEmployeeMenus(empData.getEmpId(), 3));
		    } else {
			session.setAttribute(SessionAttributesEnum.USER_TRANSACTIONS_MENU.getCode(), SecurityService.getExternalMenus());
			session.setAttribute(SessionAttributesEnum.USER_WORKFLOWS_MENU.getCode(), new ArrayList<Menu>());
			session.setAttribute(SessionAttributesEnum.USER_REPORTS_MENU.getCode(), new ArrayList<Menu>());
		    }

		} catch (BusinessException e) {
		    e.printStackTrace();
		}
	    }

	    String requestURI = getRequestURI(req.getRequestURI());
	    EmployeeData sessionUser = (EmployeeData) req.getSession().getAttribute(SessionAttributesEnum.EMP_DATA.getCode());

	    if (sessionUser == null && !requestURI.endsWith("/Main/Login.jsf")) {
		redirect("/Main/Login.jsf", false);
	    } else if (sessionUser != null && requestURI.endsWith("/Main/Login.jsf")) {
		// Navigation Option is for by-passing the normal navigation rules for external channels.
		String navigationOption = req.getParameter("navigationOption");
		if (navigationOption != null && navigationOption.equals("INBOX"))
		    redirect("/WorkList/WFInbox.jsf?init=1", false);
		else
		    redirect("/Main/Welcome.jsf", false);
	    }

	    if (!requestURI.endsWith("/Main/Login.jsf") && !requestURI.endsWith("/Security/DualSecurity.jsf") && !requestURI.endsWith("Welcome.jsf")) {
		String taskId = req.getParameter("taskId");

		// If request contains parameter "taskId" then make sure that the session user is this task owner.
		if (taskId != null) {
		    if (!isRequestParametersValid(SecurityService.getUserWFTaskUrl(sessionUser.getEmpId(), requestURI, Long.parseLong(taskId)), req, true))
			redirect("/Main/Welcome.jsf", true);
		} else {
		    // If request doesn't contain parameter "taskId" then check the privilege against the session user menus.
		    boolean authorized = false;
		    List<Menu> allMenus = new ArrayList<Menu>();
		    allMenus.addAll((List<Menu>) req.getSession().getAttribute(SessionAttributesEnum.USER_TRANSACTIONS_MENU.getCode()));
		    allMenus.addAll((List<Menu>) req.getSession().getAttribute(SessionAttributesEnum.USER_WORKFLOWS_MENU.getCode()));
		    allMenus.addAll((List<Menu>) req.getSession().getAttribute(SessionAttributesEnum.USER_REPORTS_MENU.getCode()));

		    for (Menu menuEntry : allMenus) {
			if (menuEntry.getUrl() != null && menuEntry.getUrl().contains(requestURI)) {
			    if (isRequestParametersValid(menuEntry.getUrl(), req, true)) {
				authorized = true;
				break;
			    }
			}
		    }

		    // If session user still not authorized, the only case that authorizes him is that he is working on a task via AJAX or non-AJAX call.
		    if (!authorized) {
			List<String> grantedUrls = SecurityService.getUserWFTaskMatchedUrls(sessionUser.getEmpId(), requestURI, !FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest());
			for (String grantedUrl : grantedUrls) {
			    if (isRequestParametersValid(grantedUrl, req, false)) {
				authorized = true;
				break;
			    }
			}
		    }

		    if (!authorized)
			redirect("/Main/Welcome.jsf", true);
		}
	    }
	} else if (PhaseId.RENDER_RESPONSE.compareTo(phaseEvent.getPhaseId()) == 0) {
	    // To avoid the security issue of pressing the back button after sign out, apply no caching for all web pages except web resources.
	    HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	    HttpServletResponse res = (HttpServletResponse) phaseEvent.getFacesContext().getExternalContext().getResponse();

	    // Skip JSF resources (CSS/JS/Images/etc)
	    if (!req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);
	    }

	    // String curLang = (String) req.getSession().getAttribute(SessionAttributesEnum.CURRENT_LANG.getCode());
	    // if (curLang != null) {
	    // FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(curLang));
	    // }
	}
    }

    // isDirectAccess should be true whenever an access to a new page is requested (Link - Navigation).
    // isDirectAccess should be false whenever an access is requested within the page (AJAX request or non-AJAX request returned to the same page).
    private boolean isRequestParametersValid(String grantedUrl, HttpServletRequest req, boolean isDirectAccess) {
	if (grantedUrl == null || req == null)
	    return false;

	if (grantedUrl.indexOf("?") == -1)
	    return true;

	String[] grantedUrlParams = grantedUrl.substring(grantedUrl.indexOf("?") + 1).split("&");
	for (String grantedUrlParam : grantedUrlParams) {
	    String[] grantedUrlParamArray = grantedUrlParam.split("=");
	    if (grantedUrlParamArray.length != 2)
		return false;

	    if (isDirectAccess) {
		if (req.getParameter(grantedUrlParamArray[0]) != null && !req.getParameter(grantedUrlParamArray[0]).equals(grantedUrlParamArray[1]))
		    return false;
	    } else {
		if (req.getParameter(grantedUrlParamArray[0]) != null)
		    return false;
	    }

	}

	return true;
    }

    private void redirect(String viewURI, boolean appendErrorMessage) {
	try {
	    FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + viewURI + (appendErrorMessage ? "?privilegeError=error" : ""));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private String getRequestURI(String requestURI) {
	requestURI = requestURI.substring(requestURI.indexOf("/", 1), requestURI.length());
	int index = requestURI.indexOf("?");
	if (index != -1) {
	    requestURI = requestURI.substring(0, index);
	}
	return requestURI;
    }
}