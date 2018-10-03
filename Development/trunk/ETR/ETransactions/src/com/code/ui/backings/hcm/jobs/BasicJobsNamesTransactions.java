package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.organization.jobs.BasicJobNameData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "basicJobsNamesTransactions")
@ViewScoped
public class BasicJobsNamesTransactions extends BaseBacking {

    /**
     * 1 for Militaries and 2 for Civilians
     **/
    private int mode;
    private final int pageSize = 10;

    private String basicJobName;
    private Long selectedCategoryId;
    private List<Category> categorieslist;

    private List<BasicJobNameData> basicJobsNames;

    public BasicJobsNamesTransactions() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		this.setScreenTitle(getMessage("title_basicJobsNamesTransactionsForMilitaries"));
		categorieslist = CommonService.getMilitariesCategories();
		break;
	    case 2:
		this.setScreenTitle(getMessage("title_basicJobsNamesTransactionsForCivilians"));
		categorieslist = CommonService.getPersonsCategories();
		break;
	    default:
		this.setServerSideErrorMessages(getMessage("error_general"));
	    }
	}
	reset();
    }

    public void reset() {
	try {
	    basicJobName = "";
	    selectedCategoryId = categorieslist.get(0).getId();
	    searchBasicJobsNames();
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void searchBasicJobsNames() {
	try {
	    basicJobsNames = JobsService.getBasicJobsNames(basicJobName, new Long[] { selectedCategoryId }, null);
	} catch (BusinessException e) {
	    basicJobsNames = new ArrayList<BasicJobNameData>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewBasicJobName() {
	BasicJobNameData j = new BasicJobNameData();
	j.setCategoryId(selectedCategoryId);
	j.setCategoryDescription(CommonService.getCategoryById(selectedCategoryId).getDescription());
	basicJobsNames.add(0, j);
    }

    public void saveJobName(BasicJobNameData j) {
	try {
	    if (j.getId() != null)
		JobsService.modifyBasicJobName(j, this.loginEmpData.getEmpId());
	    else
		JobsService.addBasicJobName(j, this.loginEmpData.getEmpId());

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteJobName(BasicJobNameData j) {
	try {
	    if (j.getId() != null)
		JobsService.deleteBasicJobName(j, this.loginEmpData.getEmpId());
	    basicJobsNames.remove(j);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getPageSize() {
	return pageSize;
    }

    public List<Category> getCategorieslist() {
	return categorieslist;
    }

    public void setCategorieslist(List<Category> categorieslist) {
	this.categorieslist = categorieslist;
    }

    public List<BasicJobNameData> getBasicJobsNames() {
	return basicJobsNames;
    }

    public void setBasicJobsNames(List<BasicJobNameData> basicJobsNames) {
	this.basicJobsNames = basicJobsNames;
    }

    public String getBasicJobName() {
	return basicJobName;
    }

    public void setBasicJobName(String basicJobName) {
	this.basicJobName = basicJobName;
    }

    public Long getSelectedCategoryId() {
	return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
	this.selectedCategoryId = selectedCategoryId;
    }
}