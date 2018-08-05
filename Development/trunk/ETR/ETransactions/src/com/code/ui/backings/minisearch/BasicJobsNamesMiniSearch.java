package com.code.ui.backings.minisearch;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.jobs.BasicJobNameData;
import com.code.enums.CategoriesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "basicJobsNamesMiniSearch")
@ViewScoped
public class BasicJobsNamesMiniSearch extends BaseBacking {

    private int rowsCount = 10;
    private boolean multipleSelectFlag;

    private int mode;
    private Integer category;

    private String basicJobName;
    private List<BasicJobNameData> basicJobNameList;
    private List<BasicJobNameData> selectedBasicJobNameList;

    private String selectedBasicJobsNamesIds;
    private String selectedBasicJobsNamesNames;
    private String selectedBasicJobsNamesCategoriesIds;

    public BasicJobsNamesMiniSearch() {
	if (getRequest().getParameter("mode") != null)
	    mode = Integer.parseInt(getRequest().getParameter("mode"));

	if (getRequest().getParameter("category") != null)
	    category = Integer.parseInt(getRequest().getParameter("category"));

	if (getRequest().getParameter("multipleSelectFlag") != null) {
	    multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
	    if (multipleSelectFlag) {
		selectedBasicJobNameList = new ArrayList<BasicJobNameData>();
		rowsCount = 5;
	    }
	}

	basicJobName = "";
	searchBasicJobName();
    }

    public void searchBasicJobName() {
	try {
	    Long[] categoriesIds = null;
	    Long[] selectedBasicJobsNamesIds = null;

	    if (selectedBasicJobNameList != null && selectedBasicJobNameList.size() > 0) {
		selectedBasicJobsNamesIds = new Long[selectedBasicJobNameList.size()];
		for (int i = 0; i < selectedBasicJobNameList.size(); i++) {
		    selectedBasicJobsNamesIds[i] = selectedBasicJobNameList.get(i).getId();
		}
	    }

	    if (mode == 1) {
		// 1: Category Mode (Officers - Soldiers - All Civilians).
		categoriesIds = getCategoriesIdsArrayByMode(category);
	    } else if (mode == 2) {
		// 2: Category Class (Military - Persons - Users - Wages - contractors - Medical).
		if (category == CategoriesEnum.OFFICERS.getCode() || category == CategoriesEnum.SOLDIERS.getCode())
		    categoriesIds = new Long[] { CategoriesEnum.OFFICERS.getCode(), CategoriesEnum.SOLDIERS.getCode() };
		else
		    categoriesIds = new Long[] { category.longValue() };
	    } else if (mode == 3) {
		// 3: Category Id (Officers - Soldiers - Persons - Users - Wages - contractors - Medical).
		categoriesIds = new Long[] { category.longValue() };
	    }

	    basicJobNameList = JobsService.getBasicJobsNames(basicJobName, categoriesIds, selectedBasicJobsNamesIds);

	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	    basicJobNameList = new ArrayList<BasicJobNameData>();
	}
    }

    public void addSelectedBasicJobName(BasicJobNameData basicJobName) {
	selectedBasicJobNameList.add(basicJobName);
	basicJobNameList.remove(basicJobName);
    }

    public void removeSelectedBasicJobName(BasicJobNameData basicJobName) {
	selectedBasicJobNameList.remove(basicJobName);
	basicJobNameList.add(basicJobName);
    }

    public void generateBasicJobsNamesInfo() {
	selectedBasicJobsNamesIds = "";
	selectedBasicJobsNamesNames = "";
	selectedBasicJobsNamesCategoriesIds = "";
	String comma = "";
	if (selectedBasicJobNameList.size() > 0) {
	    for (BasicJobNameData basicJobName : selectedBasicJobNameList) {
		selectedBasicJobsNamesIds += comma + basicJobName.getId();
		selectedBasicJobsNamesNames += comma + basicJobName.getBasicJobName();
		selectedBasicJobsNamesCategoriesIds += comma + basicJobName.getCategoryId();
		comma = ",";
	    }
	}
	if (selectedBasicJobsNamesIds.length() == 0) {
	    selectedBasicJobsNamesIds = "";
	    selectedBasicJobsNamesNames = "";
	    selectedBasicJobsNamesCategoriesIds = "";
	}
    }

    public boolean getMultipleSelectFlag() {
	return multipleSelectFlag;
    }

    public void setMultipleSelectFlag(boolean multipleSelectFlag) {
	this.multipleSelectFlag = multipleSelectFlag;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public String getBasicJobName() {
	return basicJobName;
    }

    public void setBasicJobName(String basicJobName) {
	this.basicJobName = basicJobName;
    }

    public List<BasicJobNameData> getBasicJobNameList() {
	return basicJobNameList;
    }

    public void setBasicJobNameList(List<BasicJobNameData> basicJobNameList) {
	this.basicJobNameList = basicJobNameList;
    }

    public List<BasicJobNameData> getSelectedBasicJobNameList() {
	return selectedBasicJobNameList;
    }

    public void setSelectedBasicJobNameList(List<BasicJobNameData> selectedBasicJobNameList) {
	this.selectedBasicJobNameList = selectedBasicJobNameList;
    }

    public String getSelectedBasicJobsNamesIds() {
	return selectedBasicJobsNamesIds;
    }

    public void setSelectedBasicJobsNamesIds(String selectedBasicJobsNamesIds) {
	this.selectedBasicJobsNamesIds = selectedBasicJobsNamesIds;
    }

    public String getSelectedBasicJobsNamesNames() {
	return selectedBasicJobsNamesNames;
    }

    public void setSelectedBasicJobsNamesNames(String selectedBasicJobsNamesNames) {
	this.selectedBasicJobsNamesNames = selectedBasicJobsNamesNames;
    }

    public String getSelectedBasicJobsNamesCategoriesIds() {
	return selectedBasicJobsNamesCategoriesIds;
    }

    public void setSelectedBasicJobsNamesCategoriesIds(String selectedBasicJobsNamesCategoriesIds) {
	this.selectedBasicJobsNamesCategoriesIds = selectedBasicJobsNamesCategoriesIds;
    }

}