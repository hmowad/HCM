package com.code.ui.backings.base;

import java.io.Serializable;
import java.util.Enumeration;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportOutputFormatsEnum;
import com.code.enums.SessionAttributesEnum;
import com.code.services.BaseService;
import com.code.services.util.CommonService;

public abstract class BaseBacking implements Serializable {
    private String screenTitle;
    protected EmployeeData loginEmpData;

    public BaseBacking() {
	HttpSession session = getSession();
	if (session.getAttribute(SessionAttributesEnum.EMP_DATA.getCode()) != null) {
	    loginEmpData = (EmployeeData) session.getAttribute(SessionAttributesEnum.EMP_DATA.getCode());
	}

	// String curLang =
	// (String)session.getAttribute(SessionAttributesEnum.CURRENT_LANG.getCode());
	// if(curLang != null){
	// this.messages =
	// ResourceBundle.getBundle("com.code.resources.messages", new
	// Locale(curLang));
	// }
	// else{
	// Locale locale =
	// FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
	// this.messages =
	// ResourceBundle.getBundle("com.code.resources.messages", locale);
	// }
    }

    // public void changeLocale(String lang) {
    // Locale locale = new Locale(lang);
    // this.messages = ResourceBundle.getBundle("com.code.resources.messages",
    // locale);
    //
    // getSession().setAttribute(SessionAttributesEnum.CURRENT_LANG.getCode(),
    // lang);
    // }

    protected void init() {
    }

    // -------------------------------------------------------------------------

    protected HttpServletRequest getRequest() {
	return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    protected HttpSession getSession() {
	return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession();
    }

    protected Application getApplication() {
	return FacesContext.getCurrentInstance().getApplication();
    }

    protected FacesContext getFacesContext() {
	return FacesContext.getCurrentInstance();
    }

    // -------------------------------------------------------------------------

    protected void print(byte[] bytes) {
	print(bytes, ReportOutputFormatsEnum.PDF);
    }

    protected void printRTF(byte[] bytes) {
	print(bytes, ReportOutputFormatsEnum.RTF);
    }

    private void print(byte[] bytes, ReportOutputFormatsEnum reportOutputFormat) {
	try {
	    FacesContext context = FacesContext.getCurrentInstance();
	    HttpServletResponse resp = (HttpServletResponse) context.getExternalContext().getResponse();
	    ServletOutputStream outputStream = resp.getOutputStream();
	    String fileName = "";
	    if (ReportOutputFormatsEnum.PDF.equals(reportOutputFormat)) {
		fileName = "\"fileName.pdf\"";
		resp.setContentType("application/PDF");
	    } else if (ReportOutputFormatsEnum.DOCX.equals(reportOutputFormat)) {
		fileName = "\"fileName.docx\"";
		resp.setContentType("application/msword;charset=UTF-8");
	    } else if (ReportOutputFormatsEnum.RTF.equals(reportOutputFormat)) {
		fileName = "\"fileName.rtf\"";
		resp.setContentType("text/rtf;charset=UTF-8");
	    }

	    resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
	    resp.setContentLength(bytes.length);

	    outputStream.write(bytes);
	    outputStream.flush();
	    outputStream.close();
	    context.responseComplete();
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    // -------------------------------------------------------------------------

    public String getMessage(String key) {
	return BaseService.getMessage(key);
    }

    public String getParameterizedMessage(String key, Object... params) {
	return BaseService.getParameterizedMessage(key, params);
    }

    public void setServerSideSuccessMessages(String serverSideSuccessMessages) {
	setNotifyMessage(FacesMessage.SEVERITY_INFO, "", serverSideSuccessMessages);
    }

    public void setServerSideErrorMessages(String serverSideErrorMessages) {
	setNotifyMessage(FacesMessage.SEVERITY_ERROR, "", serverSideErrorMessages);
    }

    private void setNotifyMessage(Severity severity, String messageHeader, String messageDetail) {
	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, messageHeader, messageDetail));
    }

    // -------------------------------------------------------------------------

    @SuppressWarnings("rawtypes")
    protected String getCompleteURL(HttpServletRequest req, String... extraParams) {
	String url = req.getRequestURI();
	Enumeration paramNames = req.getParameterNames();
	if (paramNames != null && paramNames.hasMoreElements()) {
	    String paramSeparator = "?";

	    do {
		String paramName = (String) paramNames.nextElement();
		if (paramName.equals("rootOpened") || paramName.equals("subParentOpened") || paramName.equals("menuType"))
		    continue;
		url += paramSeparator + paramName + "=" + req.getParameter(paramName);
		paramSeparator = "&";
	    } while (paramNames.hasMoreElements());
	}

	url = url.substring(url.indexOf("/", 1), url.length());

	if (extraParams != null && extraParams.length > 0)
	    url += (url.contains("?") ? "&" : "?") + extraParams[0];

	return url;
    }

    public Long[] getCategoriesIdsArrayByMode(int mode) {
	switch (mode) {
	case 1:
	    return CommonService.getOfficerCategoriesIdsArray();
	case 2:
	    return CommonService.getSoldiersCategoriesIdsArray();
	case 3:
	    return CommonService.getCivilCategoriesIdsArray();
	case -1:
	    return CommonService.getAllCategoriesIdsArray();
	default:
	    return null;
	}
    }

    // -------------------------------------------------------------------------

    public long getLoginEmpPhysicalRegionFlag(boolean isAdmin) {
	// If the physical region id of the login employee is null (exempted or external employee),
	// then this method will return 0 as there is no region with ID = 0, so he don't get access on any other employee at any region.
	if (this.loginEmpData.getPhysicalRegionId() == null)
	    return FlagsEnum.OFF.getCode();

	if (isAdmin && RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == this.loginEmpData.getPhysicalRegionId().longValue())
	    return FlagsEnum.ALL.getCode();
	else
	    return this.loginEmpData.getPhysicalRegionId().longValue();
    }

    // -------------------------------------------------------------------------

    public String getScreenTitle() {
	return screenTitle;
    }

    public void setScreenTitle(String screenTitle) {
	this.screenTitle = screenTitle;
    }

    public EmployeeData getLoginEmpData() {
	return loginEmpData;
    }

    public void setLoginEmpData(EmployeeData loginEmpData) {
	this.loginEmpData = loginEmpData;
    }

    public String getLoginUsername() {
	return loginEmpData.getName();
    }
}