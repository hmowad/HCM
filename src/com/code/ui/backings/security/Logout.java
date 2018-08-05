package com.code.ui.backings.security;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.code.enums.NavigationEnum;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "logout")
@RequestScoped
public class Logout extends BaseBacking implements Serializable {
    public String logout() {
	getRequest().getSession().invalidate();
	return NavigationEnum.LOGIN.toString();
    }
}
