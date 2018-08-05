package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.SocialIdIssuePlace;
import com.code.exceptions.BusinessException;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "socialIdIssuePlacesMiniSearch")
@ViewScoped
public class SocialIdIssuePlacesMiniSearch extends BaseBacking implements Serializable {

    private int rowsCount = 10;
    private String desc;
    private List<SocialIdIssuePlace> searchList;

    public SocialIdIssuePlacesMiniSearch() {
	desc="";
	searchSocialIdIssuePlaces();
    }

    public void searchSocialIdIssuePlaces() {
	try {
	    searchList = CommonService.getSocialIdIssuePlacesByDescription(desc);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }
    
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<SocialIdIssuePlace> getSearchList() {
	return searchList;
    }

    public void setSearchList(List<SocialIdIssuePlace> searchList) {
	this.searchList = searchList;
    }
}
