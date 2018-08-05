package com.code.ui.backings.hcm.retirements;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RetirementsService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "retirementsReports")
@ViewScoped
public class RetirementsReports extends BaseBacking {

    private long empId;
    private int reportType;
    private long categoryId;

    public RetirementsReports() {
	reportType = 10;
	categoryId = FlagsEnum.ALL.getCode();
	reset();
    }

    public void reset() {
	empId = FlagsEnum.ALL.getCode();
    }

    public void reportTypeChangedListener() {
	if (reportType == 20 || reportType == 30)
	    categoryId = CategoriesEnum.OFFICERS.getCode();
	else if (reportType == 40)
	    categoryId = CategoriesEnum.SOLDIERS.getCode();
	else
	    categoryId = FlagsEnum.ALL.getCode();
	reset();
    }

    public void print() {
	try {
	    byte[] bytes = RetirementsService.getRetirementReportsBytes(reportType, empId);
	    if (reportType == 10 || reportType == 50)
		super.print(bytes);
	    else
		super.printRTF(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(long categoryId) {
	this.categoryId = categoryId;
    }
}
