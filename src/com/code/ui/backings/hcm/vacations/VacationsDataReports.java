package com.code.ui.backings.hcm.vacations;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.hcm.VacationsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "vacationsDataReports")
@ViewScoped
public class VacationsDataReports extends BaseBacking implements Serializable {

    private int mode;
    private int reportType;

    private List<Region> regionList;
    private long selectedRegionId;
    private long selectedUnitId;
    private String selectedUnitFullName;
    private String selectedUnitHKey;
    private List<VacationType> vacationTypes;
    private long selectedVacationTypeId;
    private Date fromDate;
    private Date toDate;
    private Date employeeVactionFromDate;
    private Date employeeVactionToDate;
    private int vacationStatusFlag; // 1:running, 2:done

    private Long selectedEmployeeId;
    private String selectedEmployeeName;
    private Long selectedEmployeeCategoryId;

    private List<Rank> ranksList;
    private Long fromRankId;
    private Long toRankId;

    private String decisionNumber;
    private Date decisionDate;

    public VacationsDataReports() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    this.setScreenTitle(getMessage("title_vacationsDataReportForOfficers"));
		    break;
		case 2:
		    this.setScreenTitle(getMessage("title_vacationsDataReportForSoldiers"));
		    break;
		case 3:
		    this.setScreenTitle(getMessage("title_vacationsDataReportForEmployees"));
		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_general"));
		}
	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }

	    regionList = CommonService.getAllRegions();
	    if (CategoriesEnum.OFFICERS.getCode() == mode) {
		ranksList = CommonService.getRanks(null, new Long[] { (CategoriesEnum.OFFICERS.getCode()) });
		ranksList.remove(ranksList.size() - 1);
	    }
	    reportType = 20;
	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	try {
	    selectedRegionId = getLoginEmpPhysicalRegionFlag(true);
	    resetUnits(null);

	    fromDate = toDate = decisionDate = HijriDateService.getHijriSysDate();
	    employeeVactionFromDate = employeeVactionToDate = null;
	    decisionNumber = null;
	    selectedVacationTypeId = FlagsEnum.ALL.getCode();
	    vacationStatusFlag = WFInstanceStatusEnum.DONE.getCode();

	    if (CategoriesEnum.OFFICERS.getCode() == mode) {
		fromRankId = ranksList.get(ranksList.size() - 1).getId(); // from lowest rank
		toRankId = ranksList.get(0).getId(); // to highest rank
	    }

	    setSelectedEmployee();
	    setVacationTypes();

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reportTypeChangeListener() {
	setSelectedEmployee();
    }

    private void setSelectedEmployee() {
	try {
	    if (reportType == 50) {
		selectedEmployeeId = null;
		selectedEmployeeName = "";
		selectedEmployeeCategoryId = (long) mode;
	    } else {
		// We load the employee to don't reference the loginEmpData.
		if (mode == this.loginEmpData.getCategoryId()
			|| (mode == CategoriesEnum.PERSONS.getCode() // 3
				&& (CategoriesEnum.USERS.getCode() == this.loginEmpData.getCategoryId()
					|| CategoriesEnum.WAGES.getCode() == this.loginEmpData.getCategoryId()
					|| CategoriesEnum.CONTRACTORS.getCode() == this.loginEmpData.getCategoryId()
					|| CategoriesEnum.MEDICAL_STAFF.getCode() == this.loginEmpData.getCategoryId()))) {
		    EmployeeData employee = EmployeesService.getEmployeeData(this.loginEmpData.getEmpId());
		    if (employee != null) {
			selectedEmployeeId = employee.getEmpId();
			selectedEmployeeName = employee.getName();
			selectedEmployeeCategoryId = employee.getCategoryId();
		    }
		} else {
		    selectedEmployeeId = null;
		    selectedEmployeeName = "";
		    selectedEmployeeCategoryId = (long) mode;
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetUnits(AjaxBehaviorEvent event) {
	try {
	    UnitData unit = null;
	    if (selectedRegionId == FlagsEnum.ALL.getCode() || selectedRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
		unit = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0);
	    } else {
		unit = UnitsService.getUnitsByTypeAndRegion(UnitTypesEnum.REGION_COMMANDER.getCode(), selectedRegionId).get(0);
	    }
	    selectedUnitId = unit.getId();
	    selectedUnitFullName = unit.getFullName();
	    selectedUnitHKey = unit.gethKey();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    String reportTitle = "";
	    String employeeRankDesc = "";
	    String fromRankDesc = "";
	    String toRankDesc = "";

	    if (reportType == 20) {
		if (vacationStatusFlag == WFInstanceStatusEnum.DONE.getCode())
		    reportTitle = getMessage("title_decisionIssuedVacationsReport") + " " + (mode == 1 ? getMessage("label_forOfficers") : (mode == 2 ? getMessage("label_forSoldiers") : getMessage("label_forPersons")));
		else if (vacationStatusFlag == WFInstanceStatusEnum.RUNNING.getCode())
		    reportTitle = getMessage("title_underProcessingVacationsReport") + " " + (mode == 1 ? getMessage("label_forOfficers") : (mode == 2 ? getMessage("label_forSoldiers") : getMessage("label_forPersons")));

		if (CategoriesEnum.OFFICERS.getCode() == mode) {
		    fromRankDesc = CommonService.getRankById(fromRankId).getDescription();
		    toRankDesc = CommonService.getRankById(toRankId).getDescription();
		}
	    } else if (reportType == 30) {
		reportTitle = getMessage("title_employeeVacationsReport");

		if (selectedEmployeeId == null) {
		    this.setServerSideErrorMessages(getMessage("error_employeeMandatory"));
		    return;
		}

		EmployeeData employee = EmployeesService.getEmployeeData(selectedEmployeeId);
		if (employee != null) {
		    employeeRankDesc = employee.getRankDesc();
		}
	    } else if (reportType == 40) {
		reportTitle = getMessage("title_joiningDateReport") + " " + (mode == 1 ? getMessage("label_forOfficers") : (mode == 2 ? getMessage("label_forSoldiers") : getMessage("label_forPersons")));
	    }

	    String regionDesc = getMessage("label_all");
	    if (selectedRegionId != FlagsEnum.ALL.getCode())
		regionDesc = CommonService.getRegionById(selectedRegionId).getDescription();

	    byte[] bytes = VacationsService.getVacationsReportsBytes(reportType, selectedRegionId, regionDesc, selectedUnitHKey, selectedUnitFullName, mode,
		    reportType == 20 ? fromDate : employeeVactionFromDate, reportType == 20 ? toDate : employeeVactionToDate, selectedVacationTypeId, vacationStatusFlag,
		    selectedEmployeeId, selectedEmployeeName, employeeRankDesc, fromRankId, fromRankDesc, toRankId, toRankDesc, decisionNumber, decisionDate, null, reportTitle, false);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void setVacationTypes() {
	try {
	    this.vacationTypes = VacationsService.getVacationTypes(selectedEmployeeCategoryId);
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

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public List<Region> getRegionList() {
	return regionList;
    }

    public void setRegionList(List<Region> regionList) {
	this.regionList = regionList;
    }

    public long getSelectedRegionId() {
	return selectedRegionId;
    }

    public void setSelectedRegionId(long selectedRegionId) {
	this.selectedRegionId = selectedRegionId;
    }

    public long getSelectedUnitId() {
	return selectedUnitId;
    }

    public void setSelectedUnitId(long selectedUnitId) {
	this.selectedUnitId = selectedUnitId;
    }

    public String getSelectedUnitFullName() {
	return selectedUnitFullName;
    }

    public void setSelectedUnitFullName(String selectedUnitFullName) {
	this.selectedUnitFullName = selectedUnitFullName;
    }

    public String getSelectedUnitHKey() {
	return selectedUnitHKey;
    }

    public void setSelectedUnitHKey(String selectedUnitHKey) {
	this.selectedUnitHKey = selectedUnitHKey;
    }

    public List<VacationType> getVacationTypes() {
	return vacationTypes;
    }

    public void setVacationTypes(List<VacationType> vacationTypes) {
	this.vacationTypes = vacationTypes;
    }

    public long getSelectedVacationTypeId() {
	return selectedVacationTypeId;
    }

    public void setSelectedVacationTypeId(long selectedVacationTypeId) {
	this.selectedVacationTypeId = selectedVacationTypeId;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public int getVacationStatusFlag() {
	return vacationStatusFlag;
    }

    public void setVacationStatusFlag(int vacationStatusFlag) {
	this.vacationStatusFlag = vacationStatusFlag;
    }

    public Long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(Long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }

    public String getSelectedEmployeeName() {
	return selectedEmployeeName;
    }

    public void setSelectedEmployeeName(String selectedEmployeeName) {
	this.selectedEmployeeName = selectedEmployeeName;
    }

    public Long getSelectedEmployeeCategoryId() {
	return selectedEmployeeCategoryId;
    }

    public void setSelectedEmployeeCategoryId(Long selectedEmployeeCategoryId) {
	this.selectedEmployeeCategoryId = selectedEmployeeCategoryId;
    }

    public Date getEmployeeVactionFromDate() {
	return employeeVactionFromDate;
    }

    public void setEmployeeVactionFromDate(Date employeeVactionFromDate) {
	this.employeeVactionFromDate = employeeVactionFromDate;
    }

    public Date getEmployeeVactionToDate() {
	return employeeVactionToDate;
    }

    public void setEmployeeVactionToDate(Date employeeVactionToDate) {
	this.employeeVactionToDate = employeeVactionToDate;
    }

    public List<Rank> getRanksList() {
	return ranksList;
    }

    public void setRanksList(List<Rank> ranksList) {
	this.ranksList = ranksList;
    }

    public Long getFromRankId() {
	return fromRankId;
    }

    public void setFromRankId(Long fromRankId) {
	this.fromRankId = fromRankId;
    }

    public Long getToRankId() {
	return toRankId;
    }

    public void setToRankId(Long toRankId) {
	this.toRankId = toRankId;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }
}
