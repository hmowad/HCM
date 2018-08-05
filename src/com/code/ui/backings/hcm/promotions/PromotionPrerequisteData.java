package com.code.ui.backings.hcm.promotions;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PromotionsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "promotionPrerequisiteData")
@ViewScoped
public class PromotionPrerequisteData extends BaseBacking {

    public int mode;
    public Long empId;
    public EmployeeData employeeData;
    public Date promotionDueDate;
    public Date lastPromotionDate;

    public PromotionPrerequisteData() {

	if (getRequest().getParameter("mode") != null)
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	resetForm();
    }

    public void resetForm() {
	empId = null;
	employeeData = null;
	promotionDueDate = null;
	lastPromotionDate = null;
    }

    public void searchEmpData() {
	try {
	    if (empId != null) {
		employeeData = EmployeesService.getEmployeeData(empId);
		PromotionsService.validatePromotionPrerequisiteEligibility(empId, employeeData.getCategoryId(), mode);
		promotionDueDate = employeeData.getPromotionDueDate();
		lastPromotionDate = employeeData.getLastPromotionDate();
	    }
	} catch (BusinessException e) {
	    resetForm();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void savePromotionEmployeeData() {
	try {
	    PromotionsService.updatePromotionEmployeesDates(employeeData, promotionDueDate, lastPromotionDate, loginEmpData.getEmpId());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public EmployeeData getEmployeeData() {
	return employeeData;
    }

    public void setEmployeeData(EmployeeData employeeData) {
	this.employeeData = employeeData;
    }

    public long getEmployeesSearchRegionId() {
	return getLoginEmpPhysicalRegionFlag(true);
    }

    public Date getPromotionDueDate() {
	return promotionDueDate;
    }

    public void setPromotionDueDate(Date promotionDueDate) {
	this.promotionDueDate = promotionDueDate;
    }

    public Date getLastPromotionDate() {
	return lastPromotionDate;
    }

    public void setLastPromotionDate(Date lastPromotionDate) {
	this.lastPromotionDate = lastPromotionDate;
    }

}
