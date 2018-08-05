package com.code.ui.backings.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;

import com.code.dal.orm.security.Menu;
import com.code.enums.SessionAttributesEnum;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "menuTree")
@SessionScoped
public class MenuTree extends BaseBacking {
    private List<Menu> currentTree;
    private List<Menu> transactions, workFlow, reports;

    private Integer rootOpened, subParentOpened;

    private Integer menuType;

    @SuppressWarnings("unchecked")
    public MenuTree() {
	super();

	transactions = (List<Menu>) getSession().getAttribute(SessionAttributesEnum.USER_TRANSACTIONS_MENU.getCode());
	transactions = adjustMenuTree(transactions);

	currentTree = transactions;
	menuType = 1;
    }

    @SuppressWarnings("unchecked")
    public void init() {
	super.init();

	HttpServletRequest req = getRequest();
	if (req.getParameter("menuType") != null) {
	    menuType = Integer.parseInt(req.getParameter("menuType"));
	    if (menuType == 1) {
		currentTree = transactions;
	    } else if (menuType == 2) {
		if (workFlow == null) {
		    workFlow = (List<Menu>) req.getSession().getAttribute(SessionAttributesEnum.USER_WORKFLOWS_MENU.getCode());
		    workFlow = adjustMenuTree(workFlow);
		}
		currentTree = workFlow;
	    } else if (menuType == 3) {
		if (reports == null) {
		    reports = (List<Menu>) req.getSession().getAttribute(SessionAttributesEnum.USER_REPORTS_MENU.getCode());
		    reports = adjustMenuTree(reports);
		}
		currentTree = reports;
	    }
	}

	if (req.getParameter("rootOpened") != null)
	    rootOpened = Integer.parseInt(req.getParameter("rootOpened"));
	else
	    rootOpened = 0;

	if (req.getParameter("subParentOpened") != null)
	    subParentOpened = Integer.parseInt(req.getParameter("subParentOpened"));
	else
	    subParentOpened = 0;
    }

    private List<Menu> adjustMenuTree(List<Menu> menus) {
	List<Menu> menuTree = new ArrayList<Menu>();

	Map<Long, Menu> parents = new HashMap<Long, Menu>();

	for (Menu m : menus) {
	    if (m.getMenuFlag().equals(0))
		continue;

	    if (!parents.containsKey(m.getParentId())) { // Root Node
		m.setSubMenus(new ArrayList<Menu>());
		menuTree.add(m);
		parents.put(m.getMenuId(), m);
	    } else if (parents.containsKey(m.getParentId()) && m.getUrl() == null) { // Parent Node under the Root Node
		m.setSubMenus(new ArrayList<Menu>());
		Menu p = parents.get(m.getParentId());
		p.getSubMenus().add(m);
		parents.put(m.getMenuId(), m);
	    } else if (parents.containsKey(m.getParentId()) && m.getUrl() != null) { // Link Node
		Menu p = parents.get(m.getParentId());
		String url = m.getUrl() + (m.getUrl().contains("?") ? "&" : "?");

		if (!parents.containsKey(p.getParentId())) { // Link under Root Node
		    url += "rootOpened=" + p.getMenuId();
		} else { // Link under sub parent
		    Menu r = parents.get(p.getParentId());
		    url += "rootOpened=" + r.getMenuId() + "&subParentOpened=" + p.getMenuId();
		}

		url += "&menuType=" + m.getMenuType();
		m.setUrl(url);
		p.getSubMenus().add(m);
	    }
	}

	return menuTree;
    }

    public boolean checkCurrentURL(String url) {
	HttpServletRequest req = getRequest();
	String testUrl = req.getContextPath() + url;
	if (testUrl.contains(req.getRequestURI())) {
	    if (testUrl.contains("?")) {
		String[] testParams = testUrl.substring(testUrl.indexOf("?") + 1).split("&");
		for (int i = 0; i < testParams.length; i++) {
		    String[] testParamNameValue = testParams[i].split("=");
		    if (!(req.getParameter(testParamNameValue[0]) != null && req.getParameter(testParamNameValue[0]).equals(testParamNameValue[1])))
			return false;
		}
	    }
	    return true;
	}

	return false;
    }

    public void setCurrentTree(List<Menu> currentTree) {
	this.currentTree = currentTree;
    }

    public List<Menu> getCurrentTree() {
	return currentTree;
    }

    public Integer getRootOpened() {
	return rootOpened;
    }

    public void setRootOpened(Integer rootOpened) {
	this.rootOpened = rootOpened;
    }

    public Integer getSubParentOpened() {
	return subParentOpened;
    }

    public void setSubParentOpened(Integer subParentOpened) {
	this.subParentOpened = subParentOpened;
    }

    public Integer getMenuType() {
	return menuType;
    }

    public void setMenuType(Integer menuType) {
	this.menuType = menuType;
    }

}