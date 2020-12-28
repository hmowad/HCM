package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "empsMiniSearch")
@ViewScoped
public class EmpsMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private int page = 1;
    private int mode;
    private String searchEmpName;
    private String searchSocialId;
    private String searchJobDesc;
    private String searchUnitFullName;
    private Long searchUnitId;
    private String searchMilitaryNumber;
    private int categoryMode;
    private Long[] categoryIds;
    private long recruitmentRegionId = -1;
    private int graduationGroupNumber = -1;
    private int graduationGroupPlace = -1;
    private long recruitmentRankId = -1;
    private long rankId = -1;
    private long recruitmentTrainingUnitId = -1;
    private long physicalRegionId = -1;
    private long officialRegionId = -1;
    private String promotionDueDate;
    private String unitHKey;
    private Long[] statusIds;
    private String gender;
    private long exceptionalRecruitmentFlag = -1;
    private Long sequenceNumber;

    private boolean multipleSelectFlag;

    private Long[] employeeIds;
    private final int SELECTED_EMPS_MAX = 100;

    private List<EmployeeData> searchEmployeeList;
    private List<EmployeeData> selectedEmployeeList;

    private String selectedEmployeesIds;
    private String comma;

    public EmpsMiniSearch() {
	if (getRequest().getParameter("mode") != null)
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	if (getRequest().getParameter("categoryMode") != null)
	    categoryMode = Integer.parseInt(getRequest().getParameter("categoryMode"));
	if (getRequest().getParameter("categoryIds") != null && !getRequest().getParameter("categoryIds").equals("-1")) {
	    String categoryIdsString = getRequest().getParameter("categoryIds");
	    String[] categoryIdsStringArray = categoryIdsString.split(",");
	    categoryIds = new Long[categoryIdsStringArray.length];
	    for (int i = 0; i < categoryIdsStringArray.length; i++)
		categoryIds[i] = Long.parseLong(categoryIdsStringArray[i]);
	}
	if (getRequest().getParameter("recruitmentRegionId") != null)
	    recruitmentRegionId = Long.parseLong(getRequest().getParameter("recruitmentRegionId"));
	if (getRequest().getParameter("graduationGroupNumber") != null)
	    graduationGroupNumber = Integer.parseInt(getRequest().getParameter("graduationGroupNumber"));
	if (getRequest().getParameter("graduationGroupPlace") != null)
	    graduationGroupPlace = Integer.parseInt(getRequest().getParameter("graduationGroupPlace"));
	if (getRequest().getParameter("recruitmentRankId") != null)
	    recruitmentRankId = Long.parseLong(getRequest().getParameter("recruitmentRankId"));
	if (getRequest().getParameter("managerPhysicalUnitHKey") != null)
	    unitHKey = getRequest().getParameter("managerPhysicalUnitHKey");
	if (getRequest().getParameter("unitId") != null)
	    searchUnitId = Long.parseLong(getRequest().getParameter("unitId"));
	if (getRequest().getParameter("rankId") != null)
	    rankId = Long.parseLong(getRequest().getParameter("rankId"));
	if (getRequest().getParameter("promotionDueDate") != null)
	    promotionDueDate = getRequest().getParameter("promotionDueDate");
	if (getRequest().getParameter("recruitmentTrainingUnitId") != null)
	    recruitmentTrainingUnitId = Long.parseLong(getRequest().getParameter("recruitmentTrainingUnitId"));
	if (getRequest().getParameter("physicalRegionId") != null)
	    physicalRegionId = Long.parseLong(getRequest().getParameter("physicalRegionId"));
	if (getRequest().getParameter("officialRegionId") != null)
	    officialRegionId = Long.parseLong(getRequest().getParameter("officialRegionId"));
	if (getRequest().getParameter("gender") != null)
	    gender = getRequest().getParameter("gender");
	if (getRequest().getParameter("exceptionalRecruitmentFlag") != null)
	    exceptionalRecruitmentFlag = Long.parseLong(getRequest().getParameter("exceptionalRecruitmentFlag"));
	if (getRequest().getParameter("statusIds") != null && !getRequest().getParameter("statusIds").equals("-1")) {
	    String statusIdsString = getRequest().getParameter("statusIds");
	    String[] statusIdsStringArray = statusIdsString.split(",");
	    statusIds = new Long[statusIdsStringArray.length];
	    for (int i = 0; i < statusIdsStringArray.length; i++)
		statusIds[i] = Long.parseLong(statusIdsStringArray[i]);
	}
	if (getRequest().getParameter("employeeIds") != null && !getRequest().getParameter("employeeIds").equals("-1")) {
	    String employeeIdsString = getRequest().getParameter("employeeIds");
	    String[] employeeIdsStringArray = employeeIdsString.split(",");
	    employeeIds = new Long[employeeIdsStringArray.length];
	    for (int i = 0; i < employeeIdsStringArray.length; i++)
		employeeIds[i] = Long.parseLong(employeeIdsStringArray[i]);
	}
	if (getRequest().getParameter("multipleSelectFlag") != null) {
	    multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
	    selectedEmployeeList = new ArrayList<EmployeeData>();
	    selectedEmployeesIds = "";
	    comma = "";
	    if (multipleSelectFlag) {
		rowsCount = 5;
	    }
	}
    }

    public void searchEmployee() {
	try {
	    searchEmployeeList = null;
	    int militaryNumber = (searchMilitaryNumber == null || searchMilitaryNumber.isEmpty()) ? FlagsEnum.ALL.getCode() : Integer.parseInt(searchMilitaryNumber);
	    switch (mode) {
	    case 1: // Get employees by category and name
		searchEmployeeList = EmployeesService.getEmpByEmpName(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, categoryMode == -1 ? null : getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, militaryNumber, sequenceNumber);
		break;
	    case 2: // Get employees by category and name used
		    // in [Recruitment work flow screens O/S/P/U/W/C/M]
		searchEmployeeList = EmployeesService.getEmployeesForRecruitment(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, categoryMode, recruitmentRegionId, graduationGroupNumber, graduationGroupPlace, recruitmentRankId, recruitmentTrainingUnitId, gender, exceptionalRecruitmentFlag, statusIds, militaryNumber, sequenceNumber);
		break;
	    case 4: // Get employees with status ON_DUTY by category and name
		    // used in Mandate/Secondment requests for [S], and Subjoin
		    // for [P]
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 5: // Get employees with status ON_DUTY or ASSIGNED by category
		    // and name
		    // used in Move/Subjoin/Assignment for [O,S,P],
		    // Assignment Ext/Term/Cancel [O]
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.ASSIGNED.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 6: // Get employees with status ON_DUTY, ASSIGNED, SUBJOIND,or
		    // PERSONS_SUBJOIND by category and name
		    // Used in Decision Copies
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.ASSIGNED.getCode(), EmployeeStatusEnum.SUBJOINED.getCode(), EmployeeStatusEnum.PERSONS_SUBJOINED.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 7: // Get employees with status ON_DUTY, or MANDATED by
		    // category and name
		    // used in Mandate Ext/Term/Cancel for [S]
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.MANDATED.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 8: // Get employees with status ON_DUTY, or SECONDMENTED by
		    // category and name
		    // used in Secondment Ext/Term/Cancel for [S]
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.SECONDMENTED.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 9: // Get employees with status ON_DUTY, Subjoined or
		    // Assigned[O] by category and name
		    // used in Internal Assignment Registration for [O,S,P]
		    // Used in Missions.
		if (unitHKey == null || unitHKey.equals("-1") || unitHKey.isEmpty())
		    unitHKey = null;
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.SUBJOINED.getCode(), EmployeeStatusEnum.PERSONS_SUBJOINED.getCode(), EmployeeStatusEnum.ASSIGNED.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByPhysicalUnitHkeyNameAndStatusesID(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, unitHKey, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, militaryNumber, FlagsEnum.ALL.getCode(), sequenceNumber, FlagsEnum.ALL.getCode());
		break;
	    case 10: // Get employees by category, name, rank id and promotion
		     // due date
		if (promotionDueDate == null || promotionDueDate.equals("-1") || promotionDueDate.isEmpty())
		    promotionDueDate = null;
		searchEmployeeList = EmployeesService.getEmployeesByCategoryIdAndRankIdAndPromotionDueDate(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, categoryMode == -1 ? null : getCategoriesIdsArrayByMode(categoryMode), rankId, promotionDueDate, officialRegionId, militaryNumber, sequenceNumber);
		break;
	    case 11: // Get employees with NEW_STUDENT_RANKED_SOLDIER and
		     // ON_DUTY_UNDER_RECRUITMENT
		     // Used in recruitment wishes.
		statusIds = new Long[] { EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode(), EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, "M", FlagsEnum.OFF.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 12: // Get employees that still works in BG by category and name
		     // used in employees service termination
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode(), EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.SUBJOINED.getCode(), EmployeeStatusEnum.PERSONS_SUBJOINED.getCode(),
			EmployeeStatusEnum.ASSIGNED.getCode(), EmployeeStatusEnum.MANDATED.getCode(), EmployeeStatusEnum.SECONDMENTED.getCode(), EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(), EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(), EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 13: // Get employees that moved externally category and name
		     // used in employees service termination movement
		statusIds = new Long[] { EmployeeStatusEnum.MOVED_EXTERNALLY.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, officialRegionId, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 14: // Get employees that still works in BG by category Id (which means all different categories of civilians) and name
		     // used in employees service termination
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode(), EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.SUBJOINED.getCode(), EmployeeStatusEnum.PERSONS_SUBJOINED.getCode(),
			EmployeeStatusEnum.ASSIGNED.getCode(), EmployeeStatusEnum.MANDATED.getCode(), EmployeeStatusEnum.SECONDMENTED.getCode(), EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(), EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(), EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, new Long[] { (long) categoryMode }, physicalRegionId, officialRegionId, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 15: // Get employees For Internal Assignment Registration for [O,S,P]
		if (unitHKey == null || unitHKey.equals("-1") || unitHKey.isEmpty())
		    unitHKey = null;
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(),
			EmployeeStatusEnum.SUBJOINED.getCode(),
			EmployeeStatusEnum.PERSONS_SUBJOINED.getCode(),
			EmployeeStatusEnum.ASSIGNED.getCode(),
			EmployeeStatusEnum.MANDATED.getCode(),
			EmployeeStatusEnum.SECONDMENTED.getCode(),
			EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()
		};
		searchEmployeeList = EmployeesService.getEmployeesByPhysicalUnitHkeyNameAndStatusesID(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, unitHKey, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, militaryNumber, officialRegionId, sequenceNumber, FlagsEnum.ALL.getCode());
		break;
	    case 16:
		if (unitHKey == null || unitHKey.equals("-1") || unitHKey.isEmpty())
		    unitHKey = null;
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode(),
			EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.SUBJOINED.getCode(),
			EmployeeStatusEnum.PERSONS_SUBJOINED.getCode(),
			EmployeeStatusEnum.ASSIGNED.getCode(),
			EmployeeStatusEnum.MANDATED.getCode(),
			EmployeeStatusEnum.SECONDMENTED.getCode(),
			EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.SERVICE_TERMINATED.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByPhysicalUnitHkeyNameAndStatusesID(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, unitHKey, statusIds, getCategoriesIdsArrayByMode(categoryMode), physicalRegionId, militaryNumber, officialRegionId, sequenceNumber, FlagsEnum.ALL.getCode());
		break;
	    case 17:
		searchEmployeeList = EmployeesService.getEmpByPhysicalOrOfficialUnit(searchEmpName, categoryMode == -1 ? null : getCategoriesIdsArrayByMode(categoryMode), militaryNumber,
			searchSocialId, searchJobDesc, searchUnitFullName, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), physicalRegionId, sequenceNumber, null);
		break;
	    case 18:
		searchEmployeeList = EmployeesService.getEmployeesByEmpStatusesId(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, statusIds, new Long[] { (long) categoryMode }, physicalRegionId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), militaryNumber, sequenceNumber);
		break;
	    case 19: // Get employees with statusIds and get President and vicePresident
		// used to make vac Request to President , vicePresident for officiers category and External Mission Employees
		if (employeeIds == null) {
		    statusIds = new Long[] { EmployeeStatusEnum.MANDATED.getCode(),
			    EmployeeStatusEnum.SECONDMENTED.getCode(),
			    EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(),
			    EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(),
			    EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode() };
		}

		searchEmployeeList = EmployeesService.searchEmployeesForFutureVacation(searchEmpName, getCategoriesIdsArrayByBeneficiaryMode(categoryMode), employeeIds, militaryNumber, searchSocialId, statusIds, searchJobDesc, searchUnitFullName, sequenceNumber);
		break;
	    case 20: // Get terminated employees for eservices
		searchEmployeeList = EmployeesService.getTerminatedEmployeesByUnitId(searchUnitId, searchEmpName, searchSocialId, searchJobDesc, searchUnitFullName, militaryNumber, sequenceNumber);
		break;
	    case 21: // Get employees with statusIds and get President and vicePresident For Future Vacations
		     // used to make vac Request to President , vicePresident for officiers category and External Mission Employees
		if (employeeIds == null) {
		    statusIds = new Long[] { EmployeeStatusEnum.MANDATED.getCode(),
			    EmployeeStatusEnum.SECONDMENTED.getCode(),
			    EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(),
			    EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(),
			    EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode() };
		}

		searchEmployeeList = EmployeesService.searchEmployeesForFutureVacation(searchEmpName, categoryIds, employeeIds, militaryNumber, searchSocialId, statusIds, searchJobDesc, searchUnitFullName, sequenceNumber);
		break;

	    case 22: // Get employees For HistroicalVacation
		searchEmployeeList = EmployeesService.searchEmployeesForFutureVacation(searchEmpName, categoryIds, employeeIds, militaryNumber, searchSocialId, statusIds, searchJobDesc, searchUnitFullName, sequenceNumber);
		break;

	    case 23:
		if (unitHKey == null || unitHKey.equals("-1") || unitHKey.isEmpty())
		    unitHKey = null;
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode(),
			EmployeeStatusEnum.ON_DUTY.getCode(), EmployeeStatusEnum.SUBJOINED.getCode(),
			EmployeeStatusEnum.PERSONS_SUBJOINED.getCode(),
			EmployeeStatusEnum.ASSIGNED.getCode(),
			EmployeeStatusEnum.MANDATED.getCode(),
			EmployeeStatusEnum.SECONDMENTED.getCode(),
			EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.SERVICE_TERMINATED.getCode() };
		searchEmployeeList = EmployeesService.getEmployeesByPhysicalUnitHkeyNameAndStatusesID(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, unitHKey, statusIds, categoryIds, physicalRegionId, militaryNumber, officialRegionId, sequenceNumber, FlagsEnum.ALL.getCode());
		break;
	    }
	    if (searchEmployeeList == null || searchEmployeeList.isEmpty()) {
		super.setServerSideErrorMessages(getMessage("error_noEmpFound"));
	    }

	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addSelectedEmp(EmployeeData emp) {
	// Check MAX list size
	if (selectedEmployeeList.size() == SELECTED_EMPS_MAX) {
	    super.setServerSideErrorMessages(getMessage("error_employeesListMax"));
	    return;
	}

	// Check duplicate records
	if (("," + selectedEmployeesIds + ",").contains("," + emp.getEmpId() + ",")) {
	    super.setServerSideErrorMessages(getMessage("error_repeatedEmp"));
	    return;
	}

	// Add to list
	searchEmployeeList.remove(emp);
	selectedEmployeeList.add(emp);
	selectedEmployeesIds += comma + emp.getEmpId();
	comma = ",";
    }

    public void removeSelectedEmp(EmployeeData emp) {
	selectedEmployeeList.remove(emp);

	if (selectedEmployeesIds.equals(emp.getEmpId() + "")) {
	    selectedEmployeesIds = "";
	    comma = "";
	} else {
	    selectedEmployeesIds = ("," + selectedEmployeesIds + ",").replace("," + emp.getEmpId() + ",", ",");
	    selectedEmployeesIds = selectedEmployeesIds.substring(1, selectedEmployeesIds.length() - 1);
	}
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setSearchEmpName(String searchEmpName) {
	this.searchEmpName = searchEmpName;
    }

    public String getSelectedEmployeesIds() {
	return selectedEmployeesIds;
    }

    public void setSelectedEmployeesIds(String selectedEmployeesIds) {
	this.selectedEmployeesIds = selectedEmployeesIds;
    }

    public String getSearchEmpName() {
	return searchEmpName;
    }

    public void setSearchEmployeeList(List<EmployeeData> searchEmployeeList) {
	this.searchEmployeeList = searchEmployeeList;
    }

    public List<EmployeeData> getSearchEmployeeList() {
	return searchEmployeeList;
    }

    public List<EmployeeData> getSelectedEmployeeList() {
	return selectedEmployeeList;
    }

    public void setSelectedEmployeeList(List<EmployeeData> selectedEmployeeList) {
	this.selectedEmployeeList = selectedEmployeeList;
    }

    public int getCategoryMode() {
	return categoryMode;
    }

    public void setCategoryMode(int categoryMode) {
	this.categoryMode = categoryMode;
    }

    public void setPage(int page) {
	this.page = page;
    }

    public int getPage() {
	return page;
    }

    public String getSearchSocialId() {
	return searchSocialId;
    }

    public void setSearchSocialId(String searchSocialId) {
	this.searchSocialId = searchSocialId;
    }

    public String getSearchJobDesc() {
	return searchJobDesc;
    }

    public void setSearchJobDesc(String searchJobDesc) {
	this.searchJobDesc = searchJobDesc;
    }

    public boolean isMultipleSelectFlag() {
	return multipleSelectFlag;
    }

    public String getSearchMilitaryNumber() {
	return searchMilitaryNumber;
    }

    public void setSearchMilitaryNumber(String searchMilitaryNumber) {
	this.searchMilitaryNumber = searchMilitaryNumber;
    }

    public String getSearchUnitFullName() {
	return searchUnitFullName;
    }

    public void setSearchUnitFullName(String searchUnitFullName) {
	this.searchUnitFullName = searchUnitFullName;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }

}
