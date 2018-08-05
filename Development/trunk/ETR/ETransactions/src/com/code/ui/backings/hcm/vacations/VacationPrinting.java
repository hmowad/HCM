package com.code.ui.backings.hcm.vacations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.FlagsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "vacationPrinting")
@ViewScoped
public class VacationPrinting extends BaseBacking implements Serializable {

    private boolean adminUser;
    private List<EmployeeDecisionPrivilege> decisionsPrivileges;

    private String searchByEmpId;
    private String searchByRequesterName;
    private String searchByCategoryId;
    private String searchByDecisionNumber;
    private long selectedVacType;
    private int selectedDecisionType;
    private List<VacationType> vacTypes;
    private List<Vacation> vacations;

    private int pageSize = 10;

    public VacationPrinting() {
	super();
	this.setScreenTitle(getMessage("title_vacationsPrinting"));

	try {
	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.VACATIONS.getCode(), FlagsEnum.ALL.getCode());
	    adminUser = decisionsPrivileges.size() == 0 ? false : true;

	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	searchByEmpId = this.loginEmpData.getEmpId() + "";
	searchByRequesterName = this.loginEmpData.getName();
	searchByCategoryId = this.loginEmpData.getCategoryId() + "";
	searchByDecisionNumber = null;
	selectedVacType = VacationTypesEnum.REGULAR.getCode();
	selectedDecisionType = RequestTypesEnum.NEW.getCode();

	setVacationTypes();

	searchVacations();
    }

    public void searchVacations() {
	try {
	    if (searchByEmpId == null || searchByEmpId.equals("")) {
		vacations = new ArrayList<Vacation>();
		return;
	    }

	    long empId = Long.parseLong(searchByEmpId);

	    // Employee can print his reports
	    // Employee can print his employee's reports
	    // Employee can print other employee's reports if authorized
	    if (!SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, empId, decisionsPrivileges))
		throw new BusinessException("error_notAuthorized");

	    int decisionNumber = 0;
	    if (searchByDecisionNumber != null && !searchByDecisionNumber.equals("")) {
		decisionNumber = Integer.parseInt(searchByDecisionNumber);
	    }
	    vacations = VacationsService.searchVacations(empId, selectedDecisionType, selectedVacType, decisionNumber);

	} catch (BusinessException e) {
	    vacations = new ArrayList<Vacation>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    vacations = new ArrayList<Vacation>();
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void print(int index) {
	try {
	    Vacation vac = vacations.get(index);

	    byte[] bytes = VacationsService.getVacationDecisionBytes(vac.getVacationId(), vac.getVacationTypeId(), vac.getTransactionEmpCategoryId());

	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void setVacationTypes() {
	try {
	    this.vacTypes = VacationsService.getVacationTypes(Long.parseLong(searchByCategoryId));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void setSearchByEmpId(String searchByEmpId) {
	this.searchByEmpId = searchByEmpId;
    }

    public String getSearchByEmpId() {
	return searchByEmpId;
    }

    public void setSearchByDecisionNumber(String searchByDecisionNumber) {
	this.searchByDecisionNumber = searchByDecisionNumber;
    }

    public String getSearchByDecisionNumber() {
	return searchByDecisionNumber;
    }

    public void setSelectedVacType(long selectedVacType) {
	this.selectedVacType = selectedVacType;
    }

    public long getSelectedVacType() {
	return selectedVacType;
    }

    public void setSelectedDecisionType(int selectedDecisionType) {
	this.selectedDecisionType = selectedDecisionType;
    }

    public int getSelectedDecisionType() {
	return selectedDecisionType;
    }

    public void setVacTypes(List<VacationType> vacTypes) {
	this.vacTypes = vacTypes;
    }

    public List<VacationType> getVacTypes() {
	return vacTypes;
    }

    public void setVacations(List<Vacation> vacations) {
	this.vacations = vacations;
    }

    public List<Vacation> getVacations() {
	return vacations;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getPageSize() {
	return pageSize;
    }

    public String getSearchByRequesterName() {
	return searchByRequesterName;
    }

    public void setSearchByRequesterName(String searchByRequesterName) {
	this.searchByRequesterName = searchByRequesterName;
    }

    public String getSearchByCategoryId() {
	return searchByCategoryId;
    }

    public void setSearchByCategoryId(String searchByCategoryId) {
	this.searchByCategoryId = searchByCategoryId;
    }

    public boolean getAdminUser() {
	return adminUser;
    }

    public void setAdminUser(boolean adminUser) {
	this.adminUser = adminUser;
    }
}