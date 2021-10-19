package com.code.ui.backings.hcm.raises;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.enums.CategoriesEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.NavigationEnum;
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
    private List<Category> categoriesList;
    private List<Region> regions;
    private List<RaiseEmployeeData> raiseEmployees;
    private RaiseEmployeeData raiseEmployee;
    private List<RaiseEmployeeData> updateRaiseEmployees;
    private int empPrintType;
    private boolean approveAdminFlag;
    private boolean viewAdminFlag;
    private boolean switchPanels;
    private boolean regenerateFlag = false;
    private Long raiseIdParam;
    private final int pageSize = 10;
    private Long selectedEmpId;
    private String reasons;

    public AnnualRaiseRegistration() {
	setScreenTitle(getMessage("title_annualRaiseAddition"));

	if (getRequest().getParameter("raiseId") != null)
	    raiseIdParam = Long.parseLong(getRequest().getParameter("raiseId"));

	if (raiseIdParam == null) {
	    annualRaise = RaisesService.constructRaise(RaiseTypesEnum.ANNUAL.getCode());
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
		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.RAISES_ANNUAL_RAISES_REGISTRATION.getCode(), MenuActionsEnum.RAISES_APPROVE_ANNUAL_RAISE_REGISTRATION.getCode()))
			approveAdminFlag = true;

		} else if (annualRaise.getStatus().equals(RaiseStatusEnum.CONFIRMED.getCode())) {
		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.RAISES_ANNUAL_RAISES_REGISTRATION.getCode(), MenuActionsEnum.RAISES_APPROVE_ANNUAL_RAISE_REGISTRATION.getCode())) {
			approveAdminFlag = true;
		    } else
			viewAdminFlag = true;
		} else
		    viewAdminFlag = true;
	    } catch (BusinessException e) {
		e.printStackTrace();
	    }
	}

	categoriesList = CommonService.getAllCategories();
	categoriesList.remove(5);
	regions = CommonService.getAllRegions();
	for (Iterator<Category> i = categoriesList.iterator(); i.hasNext();) {
	    Category category = i.next();
	    if ((category.getId() == CategoriesEnum.CONTRACTORS.getCode())) {
		i.remove();
		break;
	    }
	}

	raiseEmployees = new ArrayList<>();
	updateRaiseEmployees = new ArrayList<>();
	raiseEmployee = new RaiseEmployeeData();
	switchPanels = false;
	empPrintType = RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode();
    }

    public void saveRaise() {

	try {
	    annualRaise.setSystemUser(loginEmpData.getEmpId() + "");
	    // a new raise is created for the first time
	    if (annualRaise.getId() == null) {
		raiseEmployees = RaisesService.addAnnualRaise(annualRaise);
	    }
	    // the raise is updated
	    else if (!viewAdminFlag) {
		// check if there are any changes in execution date or category
		raiseEmployees = RaisesService.saveAnnualRaise(annualRaise);
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
	    RaisesService.validateAddedEmployees(raiseEmployee, raiseEmployees);
	    raiseEmployee.setEmpDeservedFlag(RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode());
	    raiseEmployees.add(raiseEmployee);
	    updateRaiseEmployees.add(raiseEmployee);

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
    public String saveRaiseEmployees() throws BusinessException {
	try {
	    RaisesService.saveRaiseEmployeesList(annualRaise, updateRaiseEmployees);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    approveAdminFlag = false;
	    viewAdminFlag = true;
	    return NavigationEnum.SUCCESS.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}

    }

    public String confirm() {
	try {
	    RaisesService.confirmAnnualRaise(annualRaise, updateRaiseEmployees);
	    approveAdminFlag = false;
	    viewAdminFlag = true;
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    return NavigationEnum.SUCCESS.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String sendBack() {
	try {
	    RaisesService.sendBack(this.reasons, annualRaise);
	    return NavigationEnum.SUCCESS.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRaise() {
	try {
	    RaisesService.approveAnnualRaise(annualRaise, updateRaiseEmployees, loginEmpData.getEmpId(), loginEmpData.getEmpId() + "");
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    approveAdminFlag = false;
	    viewAdminFlag = true;
	    return NavigationEnum.SUCCESS.toString();
	} catch (BusinessException e) {
	    if (annualRaise.isDirtyFlag())
		regenerateFlag = true;
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;

	}

    }

    public void regenerateRaiseEmployees() {
	try {
	    raiseEmployees = RaisesService.regenerateRaiseEmployeesForAnnualRaise(annualRaise);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    regenerateFlag = false;
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

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
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

    public boolean isRegenerateFlag() {
	return regenerateFlag;
    }

    public void setRegenerateFlag(boolean regenerateFlag) {
	this.regenerateFlag = regenerateFlag;
    }

    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

}
