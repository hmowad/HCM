package com.code.ui.backings.hcm.navyformations;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.navyformations.NavyFormation;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.NavyFormationsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "navyFormationManagement")
@ViewScoped
public class NavyFormationManagement extends BaseBacking {
    private int mode;
    private List<Region> regions;
    private List<NavyFormation> navyFormationList;
    private int pageSize = 10;
    private int pageNum = 1;

    public NavyFormationManagement() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_navyVessels"));
		    navyFormationList = NavyFormationsService.getNavyVessels(null, new Long(FlagsEnum.ALL.getCode()));
		    break;
		case 2:
		    setScreenTitle(getMessage("title_navyCommittees"));
		    navyFormationList = NavyFormationsService.getNavyCommittees(null);
		    break;
		default:
		    throw new BusinessException("error_URLError");
		}
		regions = CommonService.getAllRegions();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void delete(NavyFormation navyFormation) {
	try {
	    if (navyFormation.getId() != null) {
		NavyFormationsService.deleteNavyFormation(navyFormation);
	    }
	    navyFormationList.remove(navyFormation);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save(NavyFormation navyFormation) {
	try {
	    if (navyFormation.getId() == null)
		NavyFormationsService.addNavyFormation(navyFormation, (mode == 1) ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode());
	    else
		NavyFormationsService.updateNavyFormation(navyFormation, (mode == 1) ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void add() {
	navyFormationList.add(0, new NavyFormation());
	setPageNum(1);
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public List<NavyFormation> getNavyFormationList() {
	return navyFormationList;
    }

    public void setNavyFormationList(List<NavyFormation> navyFormationList) {
	this.navyFormationList = navyFormationList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getPageNum() {
	return pageNum;
    }

    public void setPageNum(int pageNum) {
	this.pageNum = pageNum;
    }

}
