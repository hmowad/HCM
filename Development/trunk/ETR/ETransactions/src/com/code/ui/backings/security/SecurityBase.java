package com.code.ui.backings.security;

import java.io.Serializable;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.security.Menu;
import com.code.enums.SessionAttributesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

public abstract class SecurityBase extends BaseBacking implements Serializable {

    protected static final String USER_NAME_ATTRIBUTE = "username";
    protected final void initializeSessionAttributes() throws BusinessException {
	try {
	    HttpSession session = getSession();
	    EmployeeData empData = (EmployeeData) session.getAttribute(SessionAttributesEnum.EMP_DATA.getCode());
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
	    throw e;
	}
    }

}
