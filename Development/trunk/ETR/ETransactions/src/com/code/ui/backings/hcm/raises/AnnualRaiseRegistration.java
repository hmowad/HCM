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
    private List<Category> categoriesList;
    private List<RaiseEmployeeData> raiseEmployees;
    private RaiseEmployeeData raiseEmployee;
    private List<RaiseEmployeeData> addedRaiseEmployees; // excluded for another reason
    private List<RaiseEmployeeData> deletedRaiseEmployees; // excluded for another reason
    private int empPrintType;
    private boolean approveAdminFlag;
    private boolean modifyAdminFlag;
    private boolean viewAdminFlag;
    private boolean switchPanels;
    private Long raiseIdParam;
    private final int pageSize = 10;

    public AnnualRaiseRegistration() {
	// raiseIdParam = Long.parseLong(getRequest().getParameter("raiseId"));
	// raiseIdParam = (long) 33;
	if (raiseIdParam == null) {
	    annualRaise = new Raise();
	    modifyAdminFlag = true;
	} else {
	    try {
		annualRaise = RaisesService.getRaiseById(raiseIdParam);
		if (annualRaise.getType() == RaiseStatusEnum.INITIAL.getCode()) {
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
	raiseEmployees = new ArrayList<>();
	addedRaiseEmployees = new ArrayList<>();
	deletedRaiseEmployees = new ArrayList<>();
	raiseEmployee = new RaiseEmployeeData();
	switchPanels = false;
	empPrintType = RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode();
    }

    public void saveRaise() {
	togglePanels();

	try {
	    // a new raise is created for the first time
	    if (raiseIdParam == null) {
		annualRaise.setType(RaiseTypesEnum.ANNUAL.getCode());
		RaisesService.addAnnualRaise(annualRaise);
		raiseEmployees = RaisesService.saveAllEmployeesgeAndGetEmployeesEndOfLadder(annualRaise, annualRaise.getExecutionDate());
	    }
	    // the raise is updated
	    else if (modifyAdminFlag) {
		RaisesService.updateRaise(annualRaise);
		raiseEmployees = RaisesService.manipulationRaiseEmployees(annualRaise, annualRaise.getExecutionDate());
	    } else // view only
	    {
		// raiseEmployees=
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	}

    }

    public void addExcludedForAnotherReason() {
	if (!raiseEmployees.contains(raiseEmployee)) {
	    raiseEmployee.setEmpDeservedFlag(RaiseEmployeesTypesEnum.EXCLUDED_EMPLOYEES_FOR_ANOTHER_REASON.getCode());
	    raiseEmployees.add(raiseEmployee);
	    addedRaiseEmployees.add(raiseEmployee);
	}
    }

    public void deleteExcludedForAnotherReason() {
	raiseEmployees.remove(raiseEmployee);
	deletedRaiseEmployees.add(raiseEmployee);
    }

    // press save button
    public void saveRaiseEmployees() {
	try {
	    RaisesService.updateRaiseAndEmployees(annualRaise, addedRaiseEmployees, deletedRaiseEmployees);
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    public void search() {

	try {
	    raiseEmployees = RaisesService.getRaiseEmployeeByRaiseId(annualRaise.getId());
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    public void approveRaise() {

	saveRaiseEmployees();
	try {
	    RaisesService.approveAnnualRaise(annualRaise, loginEmpData.getEmpId());
	} catch (BusinessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void printRaiseEmployeesReport() {

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

}
