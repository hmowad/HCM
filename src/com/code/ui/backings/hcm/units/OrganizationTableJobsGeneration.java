package com.code.ui.backings.hcm.units;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.organization.units.OrganizationTable;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.CategoriesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTablesService;
import com.code.services.util.CommonService;
import com.code.ui.util.UnitTreeNode;

@ManagedBean(name = "organizationTableJobsGeneration")
@SessionScoped
public class OrganizationTableJobsGeneration extends UnitTreeBase {

    private List<Category> categoryClasses;
    private Integer selectedCategoryClass;

    private List<UnitData> selectedUnits;

    private String currentDecisionNumber;
    private String currentDecisionDateString;

    private boolean done;
    private final int pageSize = 10;

    public void init() {
	super.init();
	categoryClasses = CommonService.getCategoryClasses();
	selectedCategoryClass = (int) CategoriesEnum.OFFICERS.getCode();

	reset();
    }

    public void reset() {
	selectedUnits = new ArrayList<UnitData>();
	done = false;
    }

    public void selectUnit(UnitTreeNode unitItem) {
	super.click(unitItem);
	try {
	    // Check if this unit is already in selected units
	    if (checkSelectUnitWithinUnits(selectedUnits, selectedUnitData.getId())) {
		this.setServerSideErrorMessages(getMessage("error_repeatedUnit"));
	    } else {
		List<OrganizationTable> activeOrganizationTables = OrganizationTablesService.getActiveOrganizationTables(new Long[] { selectedUnitData.getId() }, selectedCategoryClass);
		// Check if this unit doesn't have active organization table for the selected category class
		if (activeOrganizationTables.isEmpty()) {
		    this.setServerSideErrorMessages(getMessage("error_unitHasNoOrganizationTable"));
		} else {
		    if (selectedUnits.isEmpty()) {
			currentDecisionNumber = activeOrganizationTables.get(0).getDecisionNumber();
			currentDecisionDateString = activeOrganizationTables.get(0).getDecisionDateString();
			selectedUnits.add(selectedUnitData);
		    } else {
			// Check if the selected unit doesn't have an organization table with the same decision number and decision date.
			if (!currentDecisionNumber.equals(activeOrganizationTables.get(0).getDecisionNumber()) || !currentDecisionDateString.equals(activeOrganizationTables.get(0).getDecisionDateString())) {
			    this.setServerSideErrorMessages(getMessage("error_unitHasOrganizationTableWithDifferentDecisionInfo"));
			} else {
			    selectedUnits.add(selectedUnitData);
			}
		    }
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private boolean checkSelectUnitWithinUnits(List<UnitData> units, Long unitId) {
	for (UnitData unitData : units) {
	    if (unitData.getId().equals(unitId))
		return true;
	}
	return false;
    }

    public void deleteUnit(UnitData detail) {
	selectedUnits.remove(detail);
	if (selectedUnits.size() == 0)
	    reset();
    }

    public void save() {
	try {
	    OrganizationTablesService.generateOrganizationTableJobs(selectedCategoryClass.intValue(), selectedUnits, loginEmpData.getEmpId());
	    done = true;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    // Getters and setters
    public List<Category> getCategoryClasses() {
	return categoryClasses;
    }

    public void setCategoryClasses(List<Category> categoryClasses) {
	this.categoryClasses = categoryClasses;
    }

    public Integer getSelectedCategoryClass() {
	return selectedCategoryClass;
    }

    public void setSelectedCategoryClass(Integer selectedCategoryClass) {
	this.selectedCategoryClass = selectedCategoryClass;
    }

    public List<UnitData> getSelectedUnits() {
	return selectedUnits;
    }

    public void setSelectedUnits(List<UnitData> selectedUnits) {
	this.selectedUnits = selectedUnits;
    }

    public boolean isDone() {
	return done;
    }

    public int getPageSize() {
	return pageSize;
    }

}
