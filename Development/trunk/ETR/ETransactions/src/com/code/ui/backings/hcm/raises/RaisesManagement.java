package com.code.ui.backings.hcm.raises;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RaiseEmployeesTypesEnum;
import com.code.enums.RaiseTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RaisesService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "raisesManagement")
@ViewScoped
public class RaisesManagement extends BaseBacking {
    private int pageSize = 10;
    private int raisesPageNum = 1;
    private int mode = 0;
    private String pageName;
    private String decisionNumber;
    private Long jobCategory;
    private Date decisionDateFrom;
    private Date decisionDateTo;
    private Date executionDateFrom;
    private Date executionDateTo;

    private List<Raise> raises;
    private List<Category> employeesCategories;

    public RaisesManagement() {
	init();
    }

    public void init() {
	employeesCategories = CommonService.getAllCategories();
	if (getRequest().getParameter("mode") != null) {
	    if (Integer.parseInt(getRequest().getParameter("mode")) == RaiseTypesEnum.ANNUAL.getCode()) {
		mode = RaiseTypesEnum.ANNUAL.getCode();
		setScreenTitle(getMessage("title_annualRaisesManagement"));
		pageName = "Annual";

	    } else {
		mode = RaiseTypesEnum.ADDITIONAL.getCode();
		setScreenTitle(getMessage("title_additionalRaisesManagement"));
		pageName = "Additional";
		for (Iterator<Category> i = employeesCategories.iterator(); i.hasNext();) {
		    Category category = i.next();
		    if ((category.getId() == CategoriesEnum.CONTRACTORS.getCode()) || (category.getId() == CategoriesEnum.OFFICERS.getCode()) || (category.getId() == CategoriesEnum.SOLDIERS.getCode()))
			i.remove();
		}

	    }

	    reset();
	}

    }

    public void search() {
	try {
	    raises = RaisesService.getRaises(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), decisionDateFrom, decisionDateTo, decisionNumber, executionDateFrom, executionDateTo, jobCategory, mode, FlagsEnum.ALL.getCode());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void reset() {
	try {
	    decisionNumber = "";
	    jobCategory = null;
	    decisionDateFrom = decisionDateTo = executionDateFrom = executionDateTo = null;
	    raises = RaisesService.getRaises(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, null, null, null, FlagsEnum.ALL.getCode(), mode, FlagsEnum.ALL.getCode());
	} catch (BusinessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void printEligible(Raise raise) {
	try {
	    byte[] bytes = RaisesService.getRaiseEmployeesReportBytes(raise.getDecisionNumber(), raise.getDecisionDate(), RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode(), raise.getType());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void printExcluded(Raise raise) {
	try {
	    byte[] bytes = RaisesService.getRaiseEmployeesReportBytes(raise.getDecisionNumber(), raise.getDecisionDate(), RaiseEmployeesTypesEnum.ALL_EXCLUDED_EMPLOYEES.getCode(), raise.getType());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public String getcategoryName(int categoryId) {
	for (Category category : employeesCategories) {
	    if (category.getId() == categoryId)
		return category.getDescription();
	}
	return employeesCategories.get(0).getDescription();
    }

    public void deleteRaise(Raise raise) {
	try {
	    raise.setSystemUser(loginEmpData.getEmpId() + "");
	    RaisesService.deleteRaise(raise);
	    raises.remove(raise);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Long getJobCategory() {
	return jobCategory;
    }

    public void setJobCategory(Long jobCategory) {
	this.jobCategory = jobCategory;
    }

    public Date getDecisionDateFrom() {
	return decisionDateFrom;
    }

    public void setDecisionDateFrom(Date decisionDateFrom) {
	this.decisionDateFrom = decisionDateFrom;
    }

    public Date getDecisionDateTo() {
	return decisionDateTo;
    }

    public void setDecisionDateTo(Date decisionDateTo) {
	this.decisionDateTo = decisionDateTo;
    }

    public Date getExecutionDateFrom() {
	return executionDateFrom;
    }

    public void setExecutionDateFrom(Date executionDateFrom) {
	this.executionDateFrom = executionDateFrom;
    }

    public Date getExecutionDateTo() {
	return executionDateTo;
    }

    public void setExecutionDateTo(Date executionDateTo) {
	this.executionDateTo = executionDateTo;
    }

    public List<Raise> getRaises() {
	return raises;
    }

    public void setRaises(List<Raise> raises) {
	this.raises = raises;
    }

    public int getRaisesPageNum() {
	return raisesPageNum;
    }

    public void setRaisesPageNum(int raisesPageNum) {
	this.raisesPageNum = raisesPageNum;
    }

    public List<Category> getEmployeesCategories() {
	return employeesCategories;
    }

    public void setEmployeesCategories(List<Category> employeesCategories) {
	this.employeesCategories = employeesCategories;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public String getPageName() {
	return pageName;
    }

    public void setPageName(String pageName) {
	this.pageName = pageName;
    }
}
