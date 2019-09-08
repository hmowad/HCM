package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeeFileReport")
@ViewScoped
public class EmployeeFileReport extends BaseBacking implements Serializable {

    private EmployeeData selectedEmployee;
    private Long employeeId;
    private List<EmployeeMenuAction> employeeMenuActions;

    private boolean printRecruitments;
    private boolean printSeniortiy;
    private boolean printPromotions;
    private boolean printVacations;
    private boolean printMovements;
    private boolean printPenalities;
    private boolean printBonuses;
    private boolean printServiceTermination;
    private boolean printTraining;
    private boolean printEducations;
    private boolean printAllowances;
    private boolean printServiceExtension;
    private boolean printExercises;
    private boolean printRaises;

    public EmployeeFileReport() {
	try {
	    setScreenTitle(getMessage("title_employeeFileReport"));
	    reset();
	    employeeMenuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.EMPS_FILE_REPORT.getCode());

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchEmployee() {
	try {
	    EmployeeData searchEmployee = EmployeesService.getEmployeeData(employeeId);

	    if (searchEmployee == null) {
		throw new BusinessException("error_general");
	    }

	    if (!(this.loginEmpData.getEmpId().equals(searchEmployee.getEmpId())
		    || (this.loginEmpData.getIsManager().equals(FlagsEnum.ON.getCode()) && searchEmployee.getPhysicalUnitHKey() != null && searchEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.loginEmpData.getPhysicalUnitHKey())))
		    || isRequesterAuthorized(searchEmployee))) {
		throw new BusinessException("error_notAuthorized");
	    }

	    selectedEmployee = searchEmployee;
	    employeeId = searchEmployee.getEmpId();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private boolean isRequesterAuthorized(EmployeeData searchEmployee) throws BusinessException {
	if (employeeMenuActions.size() == 0)
	    return false;

	long searchEmployeePhysicalRegionId = searchEmployee.getPhysicalRegionId() != null ? searchEmployee.getPhysicalRegionId() : FlagsEnum.ALL.getCode();
	long searchEmployeeOfficialRegionId = searchEmployee.getOfficialRegionId() != null ? searchEmployee.getOfficialRegionId() : FlagsEnum.ALL.getCode();
	long searchEmployeeCategoryId = searchEmployee.getCategoryId().longValue();

	if (loginEmpData.getPhysicalRegionId() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() && searchEmployeePhysicalRegionId != FlagsEnum.ALL.getCode()
		&& searchEmployeePhysicalRegionId != loginEmpData.getPhysicalRegionId() && searchEmployeeOfficialRegionId != loginEmpData.getPhysicalRegionId())
	    return false;

	// check needed for service terminated employees
	if (searchEmployee.getPhysicalRegionId() == null) {
	    for (EmployeeMenuAction employeeMenuAction : employeeMenuActions) {
		if (employeeMenuAction.getBeneficiaryRegionId() == null && employeeMenuAction.getBeneficiaryUnitId() == null) {
		    if (searchEmployeeCategoryId == CategoriesEnum.OFFICERS.getCode() && employeeMenuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_OFFICERS.getCode()))
			return true;
		    else if (searchEmployeeCategoryId == CategoriesEnum.SOLDIERS.getCode() && employeeMenuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_SOLDIERS.getCode()))
			return true;
		    else if ((CategoriesEnum.PERSONS.getCode() == searchEmployeeCategoryId || CategoriesEnum.USERS.getCode() == searchEmployeeCategoryId
			    || CategoriesEnum.WAGES.getCode() == searchEmployeeCategoryId || CategoriesEnum.CONTRACTORS.getCode() == searchEmployeeCategoryId
			    || CategoriesEnum.MEDICAL_STAFF.getCode() == searchEmployeeCategoryId) && employeeMenuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_CIVILIANS.getCode()))
			return true;
		}
	    }
	}

	if (searchEmployee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
	    return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_OFFICERS.getCode(), employeeMenuActions)
		    ||
		    SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_OFFICERS.getCode(), employeeMenuActions);
	} else if (searchEmployee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
	    if (searchEmployee.getGender().equals(GendersEnum.MALE.getCode())) {
		return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_SOLDIERS.getCode(), employeeMenuActions)
			||
			SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_SOLDIERS.getCode(), employeeMenuActions);
	    } else {
		return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_FEMALE_SOLDIERS.getCode(), employeeMenuActions)
			||
			SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_FEMALE_SOLDIERS.getCode(), employeeMenuActions);
	    }
	} else {
	    if (searchEmployee.getGender().equals(GendersEnum.MALE.getCode())) {
		return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_CIVILIANS.getCode(), employeeMenuActions)
			||
			SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_CIVILIANS.getCode(), employeeMenuActions);
	    } else {
		return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_FEMALE_CIVILIANS.getCode(), employeeMenuActions)
			||
			SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_REPORT_SHOW_FEMALE_CIVILIANS.getCode(), employeeMenuActions);
	    }
	}
    }

    public void reset() {
	try {
	    selectedEmployee = EmployeesService.getEmployeeData(this.loginEmpData.getEmpId());

	    printRecruitments = true;
	    printSeniortiy = true;
	    printPromotions = true;
	    printVacations = true;
	    printMovements = true;
	    printPenalities = true;
	    printBonuses = true;
	    printServiceTermination = true;
	    printTraining = true;
	    printEducations = true;
	    printAllowances = true;
	    printServiceExtension = true;
	    printExercises = true;
	    printRaises = true;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printEmployeeFileReport() {
	try {
	    byte[] bytes = EmployeesService.getEmployeesFileReportDataBytes(selectedEmployee.getEmpId(), selectedEmployee.getCategoryId(), printRecruitments, printSeniortiy, printPromotions, printVacations, printMovements, printPenalities, printBonuses, printServiceTermination, printTraining, printEducations, printAllowances, printServiceExtension, printExercises, printRaises);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printRecruitmentsListener() {
	printRecruitments = !printRecruitments;
    }

    public void printSeniortiyListener() {
	printSeniortiy = !printSeniortiy;
    }

    public void printPromotionsListener() {
	printPromotions = !printPromotions;
    }

    public void printVacationsListener() {
	printVacations = !printVacations;
    }

    public void printMovementsListener() {
	printMovements = !printMovements;
    }

    public void printPenalitiesListener() {
	printPenalities = !printPenalities;
    }

    public void printBonusesListener() {
	printBonuses = !printBonuses;
    }

    public void printServiceTerminationListener() {
	printServiceTermination = !printServiceTermination;
    }

    public void printTrainingListener() {
	printTraining = !printTraining;
    }

    public void printEducationsListener() {
	printEducations = !printEducations;
    }

    public void printAllowancesListener() {
	printAllowances = !printAllowances;
    }

    public void printServiceExtensionListener() {
	printServiceExtension = !printServiceExtension;
    }

    public void printExercisesListener() {
	printExercises = !printExercises;
    }

    public void printRaisesListener() {
	printRaises = !printRaises;
    }

    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    public EmployeeData getSelectedEmployee() {
	return selectedEmployee;
    }

    public void setSelectedEmployee(EmployeeData selectedEmployee) {
	this.selectedEmployee = selectedEmployee;
    }

    public boolean isPrintRecruitments() {
	return printRecruitments;
    }

    public void setPrintRecruitments(boolean printRecruitments) {
	this.printRecruitments = printRecruitments;
    }

    public boolean isPrintSeniortiy() {
	return printSeniortiy;
    }

    public void setPrintSeniortiy(boolean printSeniortiy) {
	this.printSeniortiy = printSeniortiy;
    }

    public boolean isPrintPromotions() {
	return printPromotions;
    }

    public void setPrintPromotions(boolean printPromotions) {
	this.printPromotions = printPromotions;
    }

    public boolean isPrintVacations() {
	return printVacations;
    }

    public void setPrintVacations(boolean printVacations) {
	this.printVacations = printVacations;
    }

    public boolean isPrintMovements() {
	return printMovements;
    }

    public void setPrintMovements(boolean printMovements) {
	this.printMovements = printMovements;
    }

    public boolean isPrintPenalities() {
	return printPenalities;
    }

    public void setPrintPenalities(boolean printPenalities) {
	this.printPenalities = printPenalities;
    }

    public boolean isPrintBonuses() {
	return printBonuses;
    }

    public void setPrintBonuses(boolean printBonuses) {
	this.printBonuses = printBonuses;
    }

    public boolean isPrintServiceTermination() {
	return printServiceTermination;
    }

    public void setPrintServiceTermination(boolean printServiceTermination) {
	this.printServiceTermination = printServiceTermination;
    }

    public boolean isPrintTraining() {
	return printTraining;
    }

    public void setPrintTraining(boolean printTraining) {
	this.printTraining = printTraining;
    }

    public boolean isPrintEducations() {
	return printEducations;
    }

    public void setPrintEducations(boolean printEducations) {
	this.printEducations = printEducations;
    }

    public boolean isPrintAllowances() {
	return printAllowances;
    }

    public void setPrintAllowances(boolean printAllowances) {
	this.printAllowances = printAllowances;
    }

    public boolean isPrintServiceExtension() {
	return printServiceExtension;
    }

    public void setPrintServiceExtension(boolean printServiceExtension) {
	this.printServiceExtension = printServiceExtension;
    }

    public boolean isPrintExercises() {
	return printExercises;
    }

    public void setPrintExercises(boolean printExercises) {
	this.printExercises = printExercises;
    }

    public boolean isPrintRaises() {
	return printRaises;
    }

    public void setPrintRaises(boolean printRaises) {
	this.printRaises = printRaises;
    }

}
