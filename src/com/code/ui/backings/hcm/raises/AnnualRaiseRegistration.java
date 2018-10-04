package com.code.ui.backings.hcm.raises;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RaiseEmployeesTypesEnum;
import com.code.enums.RaiseStatusEnum;
import com.code.enums.RaiseTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RaisesService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "annualRaiseRegistration")
@ViewScoped
public class AnnualRaiseRegistration extends BaseBacking implements Serializable {

    // private long category;
    private Raise annualRaise;
    private Raise loadedAnnualRaise;
    private List<Category> categoriesList;
    private List<RaiseEmployeeData> raiseEmployees;
    private RaiseEmployeeData raiseEmployee;
    private List<RaiseEmployeeData> updateRaiseEmployees;
    private int empPrintType;
    private boolean approveAdminFlag;
    private boolean modifyAdminFlag;
    private boolean viewAdminFlag;
    private boolean switchPanels;
    private Long raiseIdParam;
    private final int pageSize = 10;
    private Long selectedEmpId;

    public AnnualRaiseRegistration() {
	setScreenTitle(getMessage("title_annualRaiseAddition"));

	if (getRequest().getParameter("raiseId") != null)
	    raiseIdParam = Long.parseLong(getRequest().getParameter("raiseId"));

	if (raiseIdParam == null) {
	    annualRaise = RaisesService.constructRaise(RaiseTypesEnum.ANNUAL.getCode());
	    modifyAdminFlag = true;
	    try {
		if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.RAISES_ANNUAL_RAISES_REGISTRATION.getCode(), MenuActionsEnum.RAISES_APPROVE_ANNUAL_RAISE_REGISTRATION.getCode()))
		    approveAdminFlag = true;
	    } catch (BusinessException e) {
		e.printStackTrace();
	    }
	} else {
	    try {
		annualRaise = RaisesService.getRaiseById(raiseIdParam);
		if (annualRaise.getStatus() == RaiseStatusEnum.INITIAL.getCode()) {
		    modifyAdminFlag = true;
		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.RAISES_ANNUAL_RAISES_REGISTRATION.getCode(), MenuActionsEnum.RAISES_APPROVE_ANNUAL_RAISE_REGISTRATION.getCode()))
			approveAdminFlag = true;
		} else
		    viewAdminFlag = true;
	    } catch (BusinessException e) {
		e.printStackTrace();
	    }
	}

	categoriesList = CommonService.getAllCategories();
	categoriesList.remove(5);

	raiseEmployees = new ArrayList<>();
	updateRaiseEmployees = new ArrayList<>();
	raiseEmployee = new RaiseEmployeeData();
	switchPanels = false;
	empPrintType = RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode();
    }

    public void saveRaise() {

	try {

	    // a new raise is created for the first time
	    if (annualRaise.getId() == null) {
		RaisesService.addRaise(annualRaise);
		raiseEmployees = RaisesService.generateRaiseEmployees(annualRaise, annualRaise.getExecutionDate());
	    }
	    // the raise is updated
	    else if (modifyAdminFlag) {
		// check if there are any changes in execution date or category
		loadedAnnualRaise = RaisesService.getRaiseById(annualRaise.getId());
		RaisesService.updateRaise(annualRaise);
		if ((loadedAnnualRaise.getCategoryId() == annualRaise.getCategoryId()) && (loadedAnnualRaise.getExecutionDateString().equals(annualRaise.getExecutionDateString())))
		    raiseEmployees = RaisesService.getEndOfLadderAndExcludedForAnotherReasonEmployees(annualRaise.getId());
		else
		    raiseEmployees = RaisesService.regenerateRaiseEmployees(annualRaise, annualRaise.getExecutionDate());
	    }
	    // view mode is on
	    else {
		raiseEmployees = RaisesService.getEndOfLadderAndExcludedForAnotherReasonEmployees(annualRaise.getId());
	    }
	    togglePanels();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void addExcludedForAnotherReason() {

	try {
	    raiseEmployee = RaisesService.getRaiseEmployeeByRaiseIdAndEmpId(annualRaise.getId(), selectedEmpId).get(0);
	    if (!raiseEmployees.contains(raiseEmployee)) {
		raiseEmployee.setEmpDeservedFlag(RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode());
		raiseEmployees.add(raiseEmployee);
		updateRaiseEmployees.add(raiseEmployee);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void deleteExcludedForAnotherReason(RaiseEmployeeData raiseEmp) {
	raiseEmployees.remove(raiseEmp);
	raiseEmp.setEmpDeservedFlag(RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode());
	updateRaiseEmployees.add(raiseEmp);
	super.setServerSideSuccessMessages(getMessage("notify_employeeExclusionCancellationSuccessfully"));
    }

    // press save button
    public void saveRaiseEmployees() {
	try {
	    RaisesService.updateRaiseEmployeesList(updateRaiseEmployees);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    modifyAdminFlag = false;
	    approveAdminFlag = false;
	    viewAdminFlag = true;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void approveRaise() {
	try {
	    saveRaiseEmployees();
	    RaisesService.approveAnnualRaise(annualRaise, loginEmpData.getEmpId());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    modifyAdminFlag = false;
	    approveAdminFlag = false;
	    viewAdminFlag = true;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void printRaiseEmployeesReport() {
	try {
	    byte[] bytes = RaisesService.getRaiseEmployeesReportBytes(annualRaise.getDecisionNumber(), annualRaise.getDecisionDate(), empPrintType, annualRaise.getType());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void togglePanels() {
	switchPanels = !switchPanels;
    }

    public List<Category> getCategoriesList() {
	return categoriesList;
    }

    public void setCategoriesList(List<Category> categoriesList) {
	this.categoriesList = categoriesList;
    }

    public List<RaiseEmployeeData> getRaiseEmployees() {
	return raiseEmployees;
    }

    public void setRaiseEmployees(List<RaiseEmployeeData> raiseEmployees) {
	this.raiseEmployees = raiseEmployees;
    }

    public RaiseEmployeeData getRaiseEmployee() {
	return raiseEmployee;
    }

    public void setRaiseEmployee(RaiseEmployeeData raiseEmployee) {
	this.raiseEmployee = raiseEmployee;
    }

    public Raise getAnnualRaise() {
	return annualRaise;
    }

    public void setAnnualRaise(Raise annualRaise) {
	this.annualRaise = annualRaise;
    }

    public int getEmpPrintType() {
	return empPrintType;
    }

    public void setEmpPrintType(int empPrintType) {
	this.empPrintType = empPrintType;
    }

    public boolean isSwitchPanels() {
	return switchPanels;
    }

    public void setSwitchPanels(boolean switchPanels) {
	this.switchPanels = switchPanels;
    }

    public boolean isApproveAdminFlag() {
	return approveAdminFlag;
    }

    public void setApproveAdminFlag(boolean approveAdminFlag) {
	this.approveAdminFlag = approveAdminFlag;
    }

    public boolean isModifyAdminFlag() {
	return modifyAdminFlag;
    }

    public void setModifyAdminFlag(boolean modifyAdminFlag) {
	this.modifyAdminFlag = modifyAdminFlag;
    }

    public boolean isViewAdminFlag() {
	return viewAdminFlag;
    }

    public void setViewAdminFlag(boolean viewAdminFlag) {
	this.viewAdminFlag = viewAdminFlag;
    }

    public int getPageSize() {
	return pageSize;
    }

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

}
