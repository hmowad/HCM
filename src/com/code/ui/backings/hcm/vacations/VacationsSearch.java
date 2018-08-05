package com.code.ui.backings.hcm.vacations;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.dal.orm.workflow.hcm.vacations.WFVacationData;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.VacationsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "vacationsSearch")
@ViewScoped
public class VacationsSearch extends BaseBacking implements Serializable {
    private List<UnitData> units;

    private String selectedUnitHKey;
    private String selectedUnitName;
    private long selectedUnitRegionId;

    private List<VacationData> vacations;
    private List<WFVacationData> wfVacations;

    private String vacationRatio;
    private String vacationRequestRatio;

    private boolean viewAllLevelsVacationsFlag = false;

    private List<EmployeeMenuAction> employeeMenuActions;
    private boolean manager = false;
    private boolean admin = false;
    private int mode;
    private Date selectedDate;

    private int pageSize = 10;
    private DecimalFormat decimalFormater = new DecimalFormat("#.##");

    public VacationsSearch() {
	super();
	mode = Integer.parseInt(getRequest().getParameter("mode"));

	try {
	    if (mode == 1) {
		super.setScreenTitle(getMessage("title_vacationsSearchForOfficers"));
		employeeMenuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_VACATIONS_SEARCH_FOR_OFFICERS.getCode());
	    } else if (mode == 2) {
		super.setScreenTitle(getMessage("title_vacationsSearchForSoldiers"));
		employeeMenuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_VACATIONS_SEARCH_FOR_SOLDIERS.getCode());
	    } else if (mode == 3) {
		super.setScreenTitle(getMessage("title_vacationsSearchForEmployees"));
		employeeMenuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_VACATIONS_SEARCH_FOR_EMPLOYEES.getCode());
	    }

	    if (employeeMenuActions != null && employeeMenuActions.size() > 0)
		admin = true;

	    if (FlagsEnum.ON.getCode() == this.loginEmpData.getIsManager())
		manager = true;

	    if (manager || admin) {
		if (!admin)
		    units = UnitsService.getUnitChildren(this.loginEmpData.getPhysicalUnitId(), true, false, FlagsEnum.ON.getCode());

		selectedDate = HijriDateService.getHijriSysDate();
		selectedUnitName = this.loginEmpData.getPhysicalUnitFullName();
		selectedUnitHKey = this.loginEmpData.getPhysicalUnitHKey();
		selectedUnitRegionId = this.loginEmpData.getPhysicalRegionId().longValue();
		getVacations(null);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void getVacations(AjaxBehaviorEvent event) {
	try {
	    if (!(selectedUnitHKey.startsWith(UnitsService.getHKeyPrefix(this.loginEmpData.getPhysicalUnitHKey())) || isEmployeeAuthorizedViewCategory())) {
		// reset the selected unit
		selectedUnitHKey = this.loginEmpData.getPhysicalUnitHKey();
		selectedUnitRegionId = this.loginEmpData.getPhysicalRegionId().longValue();
		selectedUnitName = this.loginEmpData.getPhysicalUnitFullName();

		this.setServerSideErrorMessages(getMessage("error_notAuthorized"));
	    }

	    searchVacations(event);
	    updateVacationRatios(event);

	} catch (BusinessException e) {
	    vacations = new ArrayList<VacationData>();
	    wfVacations = new ArrayList<WFVacationData>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchVacations(AjaxBehaviorEvent event) {
	try {

	    vacations = VacationsService.getUnitCurrentVacationsData(selectedUnitHKey, viewAllLevelsVacationsFlag, getCategoriesIdsArrayByMode(mode));
	    wfVacations = VacationsWorkFlow.getUnitRunningWFVacationsData(selectedUnitHKey, viewAllLevelsVacationsFlag, getCategoriesIdsArrayByMode(mode));

	} catch (BusinessException e) {
	    vacations = new ArrayList<VacationData>();
	    wfVacations = new ArrayList<WFVacationData>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateVacationRatios(AjaxBehaviorEvent event) {
	try {
	    long allVacations = VacationsService.countUnitCurrentVacationsData(selectedUnitHKey, getCategoriesIdsArrayByMode(mode), HijriDateService.getHijriDateString(selectedDate));
	    long allRequestes = VacationsWorkFlow.countUnitRunningWFVacationsData(selectedUnitHKey, getCategoriesIdsArrayByMode(mode), HijriDateService.getHijriDateString(selectedDate));
	    long allEmpsCount = Math.max(EmployeesService.countEmployeesByCategoryAndUnitHkey(selectedUnitHKey, getCategoriesIdsArrayByMode(mode)), 1);
	    vacationRatio = decimalFormater.format((allVacations / (allEmpsCount * 1.0) * 100)) + "%";
	    vacationRequestRatio = decimalFormater.format(((allVacations + allRequestes) / (allEmpsCount * 1.0) * 100)) + "%";
	} catch (BusinessException e) {
	    vacationRatio = "0%";
	    vacationRequestRatio = "0%";
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private boolean isEmployeeAuthorizedViewCategory() throws BusinessException {

	if (mode == 1 && SecurityService.isEmployeeMenuActionGranted(selectedUnitRegionId, selectedUnitHKey, MenuActionsEnum.VAC_VACATIONS_SEARCH_SHOW_ALL_UNITS_VACATION_PERCENTAGE_ALL_OFFICERS.getCode(), employeeMenuActions))
	    return true;

	else if (mode == 2 && SecurityService.isEmployeeMenuActionGranted(selectedUnitRegionId, selectedUnitHKey, MenuActionsEnum.VAC_VACATIONS_SEARCH_SHOW_ALL_UNITS_VACATION_PERCENTAGE_ALL_SOLDIERS.getCode(), employeeMenuActions))
	    return true;

	else if (mode == 3 && SecurityService.isEmployeeMenuActionGranted(selectedUnitRegionId, selectedUnitHKey, MenuActionsEnum.VAC_VACATIONS_SEARCH_SHOW_ALL_UNITS_VACATION_PERCENTAGE_ALL_EMPLOYEES.getCode(), employeeMenuActions))
	    return true;

	return false;
    }

    public List<UnitData> getUnits() {
	return units;
    }

    public void setUnits(List<UnitData> units) {
	this.units = units;
    }

    public long getSelectedUnitRegionId() {
	return selectedUnitRegionId;
    }

    public void setSelectedUnitRegionId(long selectedUnitRegionId) {
	this.selectedUnitRegionId = selectedUnitRegionId;
    }

    public void setSelectedUnitHKey(String selectedUnitHKey) {
	this.selectedUnitHKey = selectedUnitHKey;
    }

    public String getSelectedUnitHKey() {
	return selectedUnitHKey;
    }

    public void setVacations(List<VacationData> vacations) {
	this.vacations = vacations;
    }

    public List<VacationData> getVacations() {
	return vacations;
    }

    public void setWfVacations(List<WFVacationData> wfVacations) {
	this.wfVacations = wfVacations;
    }

    public List<WFVacationData> getWfVacations() {
	return wfVacations;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getPageSize() {
	return pageSize;
    }

    public boolean isViewAllLevelsVacationsFlag() {
	return viewAllLevelsVacationsFlag;
    }

    public void setViewAllLevelsVacationsFlag(boolean viewAllLevelsVacationsFlag) {
	this.viewAllLevelsVacationsFlag = viewAllLevelsVacationsFlag;
    }

    public boolean isManager() {
	return manager;
    }

    public void setManager(boolean manager) {
	this.manager = manager;
    }

    public boolean isAdmin() {
	return admin;
    }

    public void setAdmin(boolean admin) {
	this.admin = admin;
    }

    public String getSelectedUnitName() {
	return selectedUnitName;
    }

    public void setSelectedUnitName(String selectedUnitName) {
	this.selectedUnitName = selectedUnitName;
    }

    public String getVacationRatio() {
	return vacationRatio;
    }

    public void setVacationRatio(String vacationRatio) {
	this.vacationRatio = vacationRatio;
    }

    public String getVacationRequestRatio() {
	return vacationRequestRatio;
    }

    public void setVacationRequestRatio(String vacationRequestRatio) {
	this.vacationRequestRatio = vacationRequestRatio;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Date getSelectedDate() {
	return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
	this.selectedDate = selectedDate;
    }

}