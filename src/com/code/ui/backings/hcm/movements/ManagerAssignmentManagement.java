package com.code.ui.backings.hcm.movements;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitsAncestorsLevelsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.UnitsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "managerAssignmentManagement")
@ViewScoped
public class ManagerAssignmentManagement extends BaseBacking {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;
    private int pageSize = 10;
    private long employeeId;
    private long unitId;
    private EmployeeData employee;
    private UnitData selectedUnit;
    private UnitData equivalentUnit;
    private List<UnitData> employeePhysicalUnits;

    public ManagerAssignmentManagement() {

	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    employee = new EmployeeData();
	    selectedUnit = new UnitData();
	    equivalentUnit = new UnitData();
	    switch (mode) {

	    case 1:
		setScreenTitle(getMessage("title_officersManagerAssignmentManagement"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_solidersManagerAssignmentManagement"));

		break;
	    case 3:
		setScreenTitle(getMessage("title_personsManagerAssignmentManagement"));

		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_URLError"));
	}
    }

    public void getEmployeeData() {
	try {
	    this.employee = EmployeesService.getEmployeeData(employeeId);
	    if (employee.getIsManager().intValue() == 0)
		throw new BusinessException("error_managersOnly");
	    equivalentUnit = UnitsService.getAncestorUnderPresidencyByLevel(employee.getPhysicalUnitId(), employee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) ? UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode() : UnitsAncestorsLevelsEnum.LEVEL_THREE.getCode());
	    getEmployeePhysicalUnitsData();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void getEmployeePhysicalUnitsData() {
	try {
	    this.employeePhysicalUnits = UnitsService.getUnitsByPhysicalManager(employeeId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // get physical unit by unit id
    public void getEmployeeSelectedUnit() {
	try {
	    this.selectedUnit = UnitsService.getUnitById(unitId);
	    addEmployeeAsPhysicalManagerOnUnit(selectedUnit);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // add a physical unit
    private void addEmployeeAsPhysicalManagerOnUnit(UnitData unit) {
	try {
	    for (UnitData iterUnit : employeePhysicalUnits) {
		if (iterUnit.getId().longValue() == unit.getId()) {
		    throw new BusinessException("error_duplicatedUnit");
		}
	    }
	    unit.getUnit().setSystemUser(this.loginEmpData.getEmpId() + "");
	    MovementsService.setEmployeeAsPhysicalManagerOnUnit(unit, employeePhysicalUnits, employee.getPhysicalUnitId(), employeeId);
	    employeePhysicalUnits.add(unit);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    // delete all units except
    public void removePhysicalManagerFromlUnit(UnitData unit) {
	try {
	    unit.getUnit().setSystemUser(this.loginEmpData.getEmpId() + "");
	    MovementsService.removePhysicalManagerFromUnit(unit, employeePhysicalUnits, employee.getPhysicalUnitId());
	    employeePhysicalUnits.remove(unit);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(long employeeId) {
	this.employeeId = employeeId;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public long getUnitId() {
	return unitId;
    }

    public void setUnitId(long unitId) {
	this.unitId = unitId;
    }

    public void setSelectedUnit(UnitData selectedUnit) {
	this.selectedUnit = selectedUnit;
    }

    public UnitData getSelectedUnit() {
	return selectedUnit;
    }

    public List<UnitData> getEmployeePhysicalUnits() {
	return employeePhysicalUnits;
    }

    public void setEmployeePhysicalUnits(List<UnitData> employeePhysicalUnits) {
	this.employeePhysicalUnits = employeePhysicalUnits;
    }

    public UnitData getEquivalentUnit() {
	return equivalentUnit;
    }

    public void setEquivalentUnit(UnitData equivalentUnit) {
	this.equivalentUnit = equivalentUnit;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
}
