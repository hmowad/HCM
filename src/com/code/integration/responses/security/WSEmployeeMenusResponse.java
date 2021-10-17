package com.code.integration.responses.security;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.security.Menu;
import com.code.integration.responses.WSResponseBase;

public class WSEmployeeMenusResponse extends WSResponseBase {

    private List<Menu> menusList;

    @XmlElementWrapper(name = "menus")
    @XmlElement(name = "menu")
    public List<Menu> getMenusList() {
	return menusList;
    }

    public void setMenusList(List<Menu> menusList) {
	this.menusList = menusList;
    }

}
