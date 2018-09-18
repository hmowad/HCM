package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.exceptions.BusinessException;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "ranksMiniSearch")
@ViewScoped
public class RanksMiniSearch extends BaseBacking implements Serializable {

    private int rowsCount = 10;
    private String searchRankFullName;
    private Long category;
    private List<Rank> searchRankList;
    private String selectedRanksIds;
    private String selectedRanksDescriptions;
    private List<Rank> selectedRankList;
    private boolean multipleSelectFlag;
    private String comma;

    public RanksMiniSearch() {
	try {
	    category = Long.valueOf(getRequest().getParameter("category"));
	    if (getRequest().getParameter("multipleSelectFlag") != null) {
		multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
		selectedRankList = new ArrayList<Rank>();
		selectedRanksIds = "";
		selectedRanksDescriptions = "";
		comma = "";
		searchRankList = CommonService.getRanks(null, new Long[] { category });
		if (multipleSelectFlag) {
		    rowsCount = 5;
		}
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchRank() {
	try {
	    searchRankList = null;
	    searchRankList = CommonService.getRanks(searchRankFullName, new Long[] { category });
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addSelectedRank(Rank rank) {

	searchRankList.remove(rank);
	selectedRankList.add(rank);
	selectedRanksIds += comma + rank.getId();
	selectedRanksDescriptions += comma + rank.getDescription();
	comma = ",";
    }

    public void removeSelectedRank(Rank rank) {
	selectedRankList.remove(rank);
	searchRankList.add(rank);

	if (selectedRanksIds.equals(rank.getId() + "")) {
	    selectedRanksIds = "";
	    selectedRanksDescriptions = "";
	    comma = "";
	} else {
	    selectedRanksIds = ("," + selectedRanksIds + ",").replace("," + rank.getId() + ",", ",");
	    selectedRanksIds = selectedRanksIds.substring(1, selectedRanksIds.length() - 1);
	    selectedRanksDescriptions = ("," + selectedRanksDescriptions + ",").replace("," + rank.getDescription() + ",", ",");
	    selectedRanksDescriptions = selectedRanksDescriptions.substring(1, selectedRanksDescriptions.length() - 1);
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public String getSearchRankFullName() {
	return searchRankFullName;
    }

    public void setSearchRankFullName(String searchRankFullName) {
	this.searchRankFullName = searchRankFullName;
    }

    public List<Rank> getSearchRankList() {
	return searchRankList;
    }

    public void setSearchRankList(List<Rank> searchRankList) {
	this.searchRankList = searchRankList;
    }

    public String getSelectedRanksIds() {
	return selectedRanksIds;
    }

    public void setSelectedRanksIds(String selectedRanksIds) {
	this.selectedRanksIds = selectedRanksIds;
    }

    public List<Rank> getSelectedRankList() {
	return selectedRankList;
    }

    public void setSelectedRankList(List<Rank> selectedRankList) {
	this.selectedRankList = selectedRankList;
    }

    public boolean isMultipleSelectFlag() {
	return multipleSelectFlag;
    }

    public void setMultipleSelectFlag(boolean multipleSelectFlag) {
	this.multipleSelectFlag = multipleSelectFlag;
    }

    public String getSelectedRanksDescriptions() {
	return selectedRanksDescriptions;
    }

    public void setSelectedRanksDescriptions(String selectedRanksDescriptions) {
	this.selectedRanksDescriptions = selectedRanksDescriptions;
    }

}
