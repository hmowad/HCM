package com.code.ui.backings.hcm.terminations;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "extensionDecisionRegistration")
@ViewScoped
public class ExtensionDecisionRegistration extends BaseBacking {
    private Long categoryId;
    private int mode;
    private TerminationTransactionData terminationTransactionData;
    private boolean saveHidden = false;
    private EmployeeData employee = null;

    public ExtensionDecisionRegistration() throws BusinessException {
	this.init();
    }

    public void init() {
	if (getRequest().getParameter("mode") != null || getRequest().getAttribute("mode") != null) {
	    mode = getRequest().getParameter("mode") != null ? Integer.parseInt(getRequest().getParameter("mode")) : Integer.parseInt(getRequest().getAttribute("mode").toString());
	    if (mode == CategoryModesEnum.SOLDIERS.getCode())
		setScreenTitle(getMessage("label_extenstionDecisionSoldiers"));
	    else if (mode == CategoryModesEnum.CIVILIANS.getCode())
		setScreenTitle(getMessage("label_extenstionDecisionCivilians"));
	    else
		setServerSideErrorMessages(getMessage("error_URLError"));

	    try {
		resetForm();
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_URLError"));
	}

    }

    public void resetForm() throws BusinessException {
	terminationTransactionData = new TerminationTransactionData();
	terminationTransactionData.setDecisionDate(HijriDateService.getHijriSysDate());
	terminationTransactionData.setCategoryId(new Long(mode));
	employee = null;
	saveHidden = false;
    }

    public void saveExtensionTransaction() {
	try {
	    TerminationsService.saveExtensionTransactionRecord(terminationTransactionData, employee, loginEmpData.getEmpId());
	    saveHidden = true;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void selectEmployee() {
	try {
	    employee = EmployeesService.getEmployeeData(terminationTransactionData.getEmpId());
	    terminationTransactionData.setEmpName(employee.getName());
	    if (employee.getCategoryId().equals(CategoriesEnum.CONTRACTORS.getCode())) {
		resetForm();
		this.setServerSideErrorMessages(getMessage("error_contractorNotValid"));
		return;
	    }
	    if (employee.getStatusId().equals(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode())) {
		resetForm();
		this.setServerSideErrorMessages(getMessage("error_empOnDutyUnderRecrutment"));
		return;
	    }
	} catch (BusinessException e) {
	    employee = null;
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public boolean isSaveHidden() {
	return saveHidden;
    }

    public void setSaveHidden(boolean saveHidden) {
	this.saveHidden = saveHidden;
    }

    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public TerminationTransactionData getTerminationTransactionData() {
	return terminationTransactionData;
    }

    public void setTerminationTransactionData(TerminationTransactionData terminationTransactionData) {
	this.terminationTransactionData = terminationTransactionData;
    }

}
